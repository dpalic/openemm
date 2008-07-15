/*********************************************************************************
 * The contents of this file are subject to the OpenEMM Public License Version 1.1
 * ("License"); You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.agnitas.org/openemm.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is OpenEMM.
 * The Initial Developer of the Original Code is AGNITAS AG. Portions created by
 * AGNITAS AG are Copyright (C) 2006 AGNITAS AG. All Rights Reserved.
 *
 * All copies of the Covered Code must include on each user interface screen,
 * visible to all users at all times
 *    (a) the OpenEMM logo in the upper left corner and
 *    (b) the OpenEMM copyright notice at the very bottom center
 * See full license, exhibit B for requirements.
 ********************************************************************************/

package org.agnitas.web;

import java.util.*;
import java.io.IOException;
import javax.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.struts.action.*;
import org.springframework.context.*;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.rowset.*;
import org.agnitas.util.*;
import org.agnitas.target.*;
import org.agnitas.dao.*;
import org.agnitas.beans.BindingEntry;

/**
 * Implementation of <strong>Action</strong> that validates a user logon.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 3.0 $ $Date: 2006/07/14 09:57:10 $
 */

public final class CompareMailingAction extends StrutsActionBase {
    
    public static final int ACTION_COMPARE = ACTION_LAST+1;
    
    // --------------------------------------------------------- Public Methods
    
    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param req The HTTP request we are processing
     * @param res The HTTP response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */ 
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                HttpServletRequest req, HttpServletResponse res)
                         throws IOException, ServletException {
        CompareMailingForm aForm=null;
        ActionMessages errors = new ActionMessages();
        ActionForward destination=null;
        ApplicationContext aContext=this.getWebApplicationContext();
        
        if(form==null) {
            aForm=new CompareMailingForm();
        } else {
            aForm=(CompareMailingForm) form;
        }

        AgnUtils.logger().info("Action: "+aForm.getAction());        
        // "read" action; if none is set, set default action.
        // senseless in this particular case because we have only one action
        try {
            switch(aForm.getAction()) {
                case ACTION_LIST:
                    if(allowed("stats.mailing", req)) {
                        aForm.setAction(ACTION_COMPARE);
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    destination=mapping.findForward("list");
                    break;
                case ACTION_COMPARE:
                    if(allowed("stats.mailing", req)) {
                        aForm.setAction(ACTION_COMPARE);
                        compareMailings(aForm, aContext, req);
                        destination=mapping.findForward("compare");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                        destination=mapping.findForward("list");
                    }
                    break;
                default:
                    destination=mapping.findForward("list");
            }
            
        } catch (Exception e) {
            AgnUtils.logger().error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        }
        
        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(req, errors);
            return(new ActionForward(mapping.getInput()));
        }
        
        return destination;
    }
    
    /**
     * Checks which Mailings were choosen for comparison (read from form)
     * for those mailings get requested stat information from DB and put it into the form
     * display mailing_compare_struts, in which we display the results from the form
     */
    protected void compareMailings(CompareMailingForm aForm,
                         ApplicationContext aContext, HttpServletRequest req) {
        
        JdbcTemplate jdbc=new JdbcTemplate((DataSource)aContext.getBean("dataSource"));
        SqlRowSet rset=null;
        long timeA = 0;
        StringBuffer sqlBuf=null;
        String csv_file = "";
        String mailingIDList="";
        Target aTarget = null;

        // first reset results that we might have stored in session-form-bean
        aForm.resetResults();
        
        if(aForm.getTargetID() != 0) {
            TargetDao dao=(TargetDao) getBean("TargetDao");

            aTarget = dao.getTarget(aForm.getTargetID(), getCompanyID(req));
        } else {
            aTarget=(Target) getBean("Target");
            aTarget.setCompanyID(this.getCompanyID(req));
        }

        AgnUtils.logger().info("Loading target: "+aForm.getTargetID()+"/"+getCompanyID(req));
        
        // write "header" of the csv file:
        try {
            Locale loc=(Locale)req.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
            csv_file = SafeString.getLocaleString("Mailing", loc);
            csv_file += " " + SafeString.getLocaleString("comparison", loc);
            csv_file += "\r\n\r\n" + SafeString.getLocaleString("Target", loc);
            csv_file += ": ;";
            if(aTarget.getId()!=0) {
                csv_file += aTarget.getTargetName();
            } else {
                csv_file += SafeString.getLocaleString("All_Subscribers", loc);
            }
            
            csv_file += "\r\n\r\n" + SafeString.getLocaleString("Mailing", loc)
            + ";" + SafeString.getLocaleString("Receipients", loc)
            + ";" + SafeString.getLocaleString("Clicks", loc)
            + ";" + SafeString.getLocaleString("opened", loc)
            + ";" + SafeString.getLocaleString("Bounces", loc)
            + ";" + SafeString.getLocaleString("Opt_Outs", loc)
            + "\r\n";
        } catch (Exception e) {
            AgnUtils.logger().error("while creating csv header: "+e);
            csv_file = "";
        }
        aForm.setCvsfile(csv_file);
        
        mailingIDList=AgnUtils.join(aForm.getMailings().toArray(), ", ");

        // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
        // * * S T A R T   G E T T I N G   D A T A   F R O M   D B * *
        // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

        timeA=System.currentTimeMillis();
        Hashtable allNames=aForm.getMailingName();
        Hashtable allDesc=aForm.getMailingDescription();

        // * Names & descriptions  *  //
        String sql = "SELECT shortname, description, mailing_id FROM mailing_tbl A WHERE company_id=" + this.getCompanyID(req) + " AND mailing_id IN (" + mailingIDList + ")";

        try {
            rset=jdbc.queryForRowSet(sql);
            
            while(rset.next()) {
                Integer id=new Integer(rset.getInt(3));

                allNames.put(id, SafeString.getHTMLSafeString(rset.getString(1)));
                allDesc.put(id, SafeString.getHTMLSafeString(rset.getString(2)));
                csv_file += "\r\n" + SafeString.getHTMLSafeString(rset.getString(1)) + " (" + SafeString.getHTMLSafeString(rset.getString(2)) + ")";
            }
            
        } catch (Exception e) {
            AgnUtils.logger().error("while loading mailing info: "+e);
            AgnUtils.logger().error("Query was: "+sql);
        }
        
        //  T O T A L   S E N T   M A I L S
        Hashtable allSent=aForm.getNumRecipients();
        Hashtable allAdmSent=aForm.getNumRecipients();

        sqlBuf=new StringBuffer("SELECT count(distinct mailtrack.customer_id), maildrop.mailing_id FROM mailtrack_tbl mailtrack, maildrop_status_tbl maildrop");

        if(aTarget.getId()!=0) {
            sqlBuf.append(", customer_" + this.getCompanyID(req) + "_tbl cust");
        }
        sqlBuf.append(" WHERE mailtrack.company_id="+ this.getCompanyID(req) +" AND mailtrack.maildrop_status_id=maildrop.status_id and maildrop.mailing_id IN ("+mailingIDList);
        sqlBuf.append(") and maildrop.company_id="+this.getCompanyID(req));
        if(aTarget.getId()!=0) {
            sqlBuf.append(" AND ((" + aTarget.getTargetSQL() + ") AND cust.customer_id=mailtrack.customer_id)");
        }
        sqlBuf.append(" GROUP BY maildrop.mailing_id");
        
        try {
            rset=jdbc.queryForRowSet(sqlBuf.toString());
            
            while(rset.next()) {
                Integer id=new Integer(rset.getInt(2));    // get MailingID
                if(allSent.containsKey(id)) {      // check if there is a value for this mailing
                    int aVal=((Integer)allSent.get(id)).intValue();
                    if(rset.getInt(1)>aVal) {
                        allSent.put(id, new Integer(rset.getInt(1)));
                    }
                } else {
                    allSent.put(id, new Integer(rset.getInt(1)));
                }
                // used for bar length in JSP's graphical diasplay
                if(rset.getInt(1)>aForm.getBiggestRecipients()) {
                    aForm.setBiggestRecipients(rset.getInt(1));
                }
            }
        } catch (Exception e) {
            AgnUtils.logger().error("while getting total mailing info: "+e);
            AgnUtils.logger().error("Query was: "+sqlBuf.toString());
        }
            
        // O P E N E D   M A I L S
        sqlBuf=new StringBuffer("SELECT count(onepixel.customer_id), onepixel.mailing_id FROM onepixellog_"+this.getCompanyID(req)+"_tbl onepixel");
        if(aTarget.getId()!=0) {
            sqlBuf.append(", customer_" + this.getCompanyID(req) + "_tbl cust");
        }
        sqlBuf.append(" WHERE mailing_id IN (" + mailingIDList + ")");
        if(aTarget.getId()!=0) {
            sqlBuf.append(" AND ((" + aTarget.getTargetSQL() + ") AND onepixel.customer_id=cust.customer_id)");
        }
        sqlBuf.append(" GROUP BY mailing_id");
        Hashtable allOpen=aForm.getNumOpen();

        try {
            rset=jdbc.queryForRowSet(sqlBuf.toString());
            while(rset.next()) {
                Integer id=new Integer(rset.getInt(2));

                allOpen.put(id, new Integer(rset.getInt(1)));
                if(rset.getInt(1)>aForm.getBiggestOpened()) {
                    aForm.setBiggestOpened(rset.getInt(1));
                }
            }
        } catch (Exception e) {
            AgnUtils.logger().error("while getting opened mails: "+e);
            AgnUtils.logger().error("Query was: "+sqlBuf.toString());
        }
        
        // * T O T A L   C L I C K S *
        sqlBuf=new StringBuffer("SELECT count(rdir.customer_id), rdir.url_id, rdir.mailing_id FROM rdirlog_"+this.getCompanyID(req)+"_tbl rdir");
        if(aTarget.getId()!=0) {
            sqlBuf.append(", customer_" + this.getCompanyID(req) + "_tbl cust");
        }
        
        sqlBuf.append(" WHERE rdir.mailing_id IN ("+ mailingIDList + ")");
        
        if(aTarget.getId()!=0) {
            sqlBuf.append(" AND ((" + aTarget.getTargetSQL() + ") AND cust.customer_id=rdir.customer_id)");
        }
        sqlBuf.append(" GROUP BY rdir.url_id, rdir.mailing_id");
        
        Hashtable allClicks=aForm.getNumClicks();
        
        try {
            rset=jdbc.queryForRowSet(sqlBuf.toString());
            while(rset.next()) {
                Integer id=new Integer(rset.getInt(3)); // get mailingID
                int aVal=0;

                if(allClicks.containsKey(id)) {
                    aVal=((Integer)allClicks.get(id)).intValue();
                    aVal+=rset.getInt(1);
                } else {
                    aVal=rset.getInt(1);
                }
                allClicks.put(id, new Integer(aVal));
                if(aVal>aForm.getBiggestClicks()) {
                    aForm.setBiggestClicks(aVal);
                }
            }
            
        } catch (Exception e) {
            AgnUtils.logger().error("while getting total clicks: "+e);
            AgnUtils.logger().error("Query was: "+sqlBuf.toString());
        }
        
        // csv_file += ";" + clicks[k] + ";" + opened[k];
        
        // O P T O U T  &  B O U N C E
        sqlBuf=new StringBuffer("SELECT count(bind.customer_id), bind.user_status, bind.exit_mailing_id FROM customer_" + this.getCompanyID(req) + "_binding_tbl bind");
        if(aTarget.getId()!=0) {
            sqlBuf.append(", customer_" + getCompanyID(req) + "_tbl cust");
        }
        sqlBuf.append(" WHERE exit_mailing_id IN (" + mailingIDList + ")");
        if(aTarget.getId()!=0) {
            sqlBuf.append(" AND ((" + aTarget.getTargetSQL() + ") AND cust.customer_id=bind.customer_id)");
        }
        sqlBuf.append(" GROUP BY bind.user_status, bind.exit_mailing_id, bind.mailinglist_id");
        
        Hashtable allOptout=aForm.getNumOptout();
        Hashtable allBounce=aForm.getNumBounce();

        try {
            rset=jdbc.queryForRowSet(sqlBuf.toString());
            while(rset.next()) {
                Integer id=new Integer(rset.getInt(3));
                switch(rset.getInt(2)) {
                    case BindingEntry.USER_STATUS_OPTOUT:
                        allOptout.put(id, new Integer(rset.getInt(1)));
                        if(rset.getInt(1)>aForm.getBiggestOptouts()) {
                            aForm.setBiggestOptouts(rset.getInt(1));
                        }
                        break;
                        
                    case BindingEntry.USER_STATUS_BOUNCED:
                        int tmpVal=0;
                        if(allBounce.containsKey(id)) {
                            tmpVal=((Integer)allBounce.get(id)).intValue();
                        }
                        if(rset.getInt(1)>tmpVal) {
                            tmpVal=rset.getInt(1);
                        }
                        allBounce.put(id, new Integer(tmpVal));
                        if(rset.getInt(1)>aForm.getBiggestBounce()) {
                            aForm.setBiggestBounce(tmpVal);
                        }
                        break;
                }
            }
        } catch (Exception e) {
            AgnUtils.logger().error("while getting optout: "+e);
            AgnUtils.logger().error("Query was: "+sqlBuf.toString());
        }
        
        AgnUtils.logger().info("sendquerytime: " + (System.currentTimeMillis()-timeA));
        
        //  * * * * * * * * * * * * * * * * * * * * * * * * * * * *
        //  * * E N D   G E T T I N G   D A T A   F R O M   D B * *
        //  * * * * * * * * * * * * * * * * * * * * * * * * * * * *
        return;
    }
}
