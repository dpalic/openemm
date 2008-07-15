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

import org.agnitas.stat.DeliveryStat;
import org.agnitas.util.*;
import org.agnitas.dao.*;
import org.agnitas.beans.*;
import org.agnitas.target.*;
import java.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.struts.action.*;
import org.apache.struts.util.*;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import javax.sql.*;


/**
 * Implementation of <strong>Action</strong> that validates a user logon.
 *
 * @author Martin Helff
 */

public final class MailingSendAction extends StrutsActionBase {
    
    public static final int ACTION_VIEW_SEND = ACTION_LAST+1;
    
    public static final int ACTION_SEND_ADMIN = ACTION_LAST+2;
    
    public static final int ACTION_SEND_TEST = ACTION_LAST+3;
    
    public static final int ACTION_SEND_WORLD = ACTION_LAST+4;
    
    public static final int ACTION_VIEW_SEND2 = ACTION_LAST+5;
    
    public static final int ACTION_VIEW_DELSTATBOX = ACTION_LAST+6;
    
    public static final int ACTION_ACTIVATE_CAMPAIGN = ACTION_LAST+7;
    
    public static final int ACTION_ACTIVATE_RULEBASED = ACTION_LAST+8;
    
    public static final int ACTION_PREVIEW_SELECT = ACTION_LAST+9;
    
    public static final int ACTION_PREVIEW_TEXT = ACTION_LAST+10;
    
    public static final int ACTION_PREVIEW_HTML = ACTION_LAST+11;
    
    public static final int ACTION_PREVIEW_OFFLINE = ACTION_LAST+12;
    
    public static final int ACTION_PREVIEW_HEADER = ACTION_LAST+13;
    
    public static final int ACTION_DEACTIVATE_MAILING = ACTION_LAST+14;
    
    public static final int ACTION_CHANGE_SENDDATE = ACTION_LAST+15;
    
    public static final int ACTION_CANCEL_MAILING_REQUEST = ACTION_LAST+16;
    
    public static final int ACTION_CANCEL_MAILING = ACTION_LAST+17;
    
    
    public static final int PREVIEW_MODE_HEADER = 1;
    public static final int PREVIEW_MODE_TEXT = 2;
    public static final int PREVIEW_MODE_HTML = 3;
    public static final int PREVIEW_MODE_OFFLINE = 4;
    
    // --------------------------------------------------------- Public Methods
    
    
    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     *
     * @param form
     * @param req
     * @param res
     * @param mapping The ActionMapping used to select this instance
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     * @return destination
     */
    public ActionForward execute(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest req,
            HttpServletResponse res)
            throws IOException, ServletException {
        
        // Validate the request parameters specified by the user
        MailingSendForm aForm=null;
        ActionMessages errors = new ActionMessages();
        ActionForward destination=null;
        
        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }
        
        aForm=(MailingSendForm)form;
        AgnUtils.logger().info("Action: " + aForm.getAction()); 

        try {
            switch(aForm.getAction()) {
                case ACTION_VIEW_SEND:
                    if(allowed("mailing.send.show", req)) {
                        loadMailing(aForm, req);
                        loadDeliveryStats(aForm, req);
                        destination=mapping.findForward("send");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
                    
                case ACTION_VIEW_DELSTATBOX:
                    if(allowed("mailing.send.show", req)) {
                        loadDeliveryStats(aForm, req);
                        destination=mapping.findForward("view_delstatbox");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
                    
                case ACTION_CANCEL_MAILING_REQUEST:
                    loadMailing(aForm, req);
                    aForm.setAction(MailingSendAction.ACTION_CANCEL_MAILING);
                    destination=mapping.findForward("cancel_generation_question");
                    break;
                    
                case ACTION_CANCEL_MAILING:
                    loadMailing(aForm, req);
                    if(req.getParameter("kill.x")!=null) {
                        if(cancelMailingDelivery(aForm, req)) {
                            loadDeliveryStats(aForm, req);
                            destination=mapping.findForward("send");
                        } else {
                            destination=mapping.findForward("cancel_generation_deny");
                        }
                    }
                    break;
                    
                    
                case MailingSendAction.ACTION_VIEW_SEND2:
                    if(allowed("mailing.send.show", req)) {
                        loadMailing(aForm, req);
                        loadSendStats(aForm, req);
                        aForm.setAction(MailingSendAction.ACTION_SEND_WORLD);
                        destination=mapping.findForward("send2");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
                    
                case MailingSendAction.ACTION_SEND_ADMIN:
                    if(allowed("mailing.send.admin", req)) {
                        loadMailing(aForm, req);
                        sendMailing(aForm, req);
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    aForm.setAction(MailingSendAction.ACTION_VIEW_SEND);
                    destination=mapping.findForward("send");
                    break;
                    
                case MailingSendAction.ACTION_SEND_TEST:
                    if(allowed("mailing.send.test", req)) {
                        loadMailing(aForm, req);
                        sendMailing(aForm, req);
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    aForm.setAction(MailingSendAction.ACTION_VIEW_SEND);
                    destination=mapping.findForward("send");
                    break;
                    
                case MailingSendAction.ACTION_DEACTIVATE_MAILING:
                    if(allowed("mailing.send.world", req)) {
                        deactivateMailing(aForm, req);
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    loadMailing(aForm, req);
                    aForm.setAction(MailingSendAction.ACTION_VIEW_SEND);
                    destination=mapping.findForward("send");
                    break;
                    
                case MailingSendAction.ACTION_ACTIVATE_RULEBASED:
                case MailingSendAction.ACTION_ACTIVATE_CAMPAIGN:
                case MailingSendAction.ACTION_SEND_WORLD:
                    if(this.allowed("mailing.send.world", req)) {
                        sendMailing(aForm, req);
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    loadMailing(aForm, req);
                    loadDeliveryStats(aForm, req);
                    aForm.setAction(MailingSendAction.ACTION_VIEW_SEND);
                    destination=mapping.findForward("send");
                    break;
                    
                case MailingSendAction.ACTION_PREVIEW_SELECT:
                    loadMailing(aForm, req);
                    destination=mapping.findForward("preview_select");
                    break;
                    
                case MailingSendAction.ACTION_PREVIEW_HEADER:
                    destination=mapping.findForward("preview_header");
                    this.getPreview(aForm, MailingSendAction.PREVIEW_MODE_HEADER, req);
                    break;
                    
                case MailingSendAction.ACTION_PREVIEW_TEXT:
                    destination=mapping.findForward("preview_text");
                    this.getPreview(aForm, MailingSendAction.PREVIEW_MODE_TEXT, req);
                    break;
                    
                case MailingSendAction.ACTION_PREVIEW_OFFLINE:
                case MailingSendAction.ACTION_PREVIEW_HTML:
                    destination=mapping.findForward("preview_html");
                    this.getPreview(aForm, MailingSendAction.PREVIEW_MODE_HTML, req);
                    break;
            }
        } catch (Exception e) {
            AgnUtils.logger().error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage()));
            destination=mapping.findForward("send");
        }
        
        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            this.saveErrors(req, errors);
            if(aForm.getAction()==MailingSendAction.ACTION_SEND_ADMIN || aForm.getAction()==MailingSendAction.ACTION_SEND_TEST || aForm.getAction()==MailingSendAction.ACTION_SEND_WORLD) {
                return (new ActionForward(mapping.getInput()));
            }
        }
        
        return destination;
    }
    
    /**
     * Loads mailing.
     */
    protected void loadMailing(MailingSendForm aForm, HttpServletRequest req) throws Exception {
        MailingDao mDao=(MailingDao) getBean("MailingDao");
        Mailing aMailing=mDao.getMailing(aForm.getMailingID(), this.getCompanyID(req));
        TargetDao tDao=(TargetDao) getBean("TargetDao");
        
        if(aMailing==null) {
            aMailing=(Mailing) getBean("Mailing");
            aMailing.init(getCompanyID(req), getWebApplicationContext());
            aMailing.setId(0);
            aForm.setMailingID(0);
        }
        
        aForm.setShortname(aMailing.getShortname());
        aForm.setIsTemplate(aMailing.isIsTemplate());
        aForm.setMailingtype(aMailing.getMailingType());
        aForm.setWorldMailingSend(aMailing.isWorldMailingSend());
        aForm.setTargetGroups(aMailing.getTargetGroups());
        aForm.setEmailFormat(aMailing.getEmailParam(this.getWebApplicationContext()).getMailFormat());
        aForm.setMailinglistID(aMailing.getMailinglistID());
        aForm.setMailing(aMailing);
        
        req.setAttribute("targetGroups", tDao.getTargets(this.getCompanyID(req)));
        
        return;
    }
    
    /**
     * Loads delivery statistics.
     */
    protected void loadDeliveryStats(MailingSendForm aForm, HttpServletRequest req) throws Exception {
        DeliveryStat aDelStat = (DeliveryStat) getBean("DeliveryStat");

        aDelStat.setCompanyID(this.getCompanyID(req));
        aDelStat.setMailingID(aForm.getMailingID());
        aDelStat.getDeliveryStatsFromDB(aForm.getMailingtype(), getWebApplicationContext());
        aForm.setDeliveryStat(aDelStat);
    }
    
    /**
     * Cancels mailing delivery.
     */
    protected boolean cancelMailingDelivery(MailingSendForm aForm, HttpServletRequest req) {
        DeliveryStat aDelStat = (DeliveryStat) getBean("DeliveryStat");

        aDelStat.setCompanyID(this.getCompanyID(req));
        aDelStat.setMailingID(aForm.getMailingID());
        if(aDelStat.cancelDelivery(getWebApplicationContext())) {
            aForm.setWorldMailingSend(false);
            aForm.setMailingtype(Mailing.TYPE_NORMAL);
            return true;
        }
        return false;
    }
    
    /**
     * Sends mailing.
     */
    protected void sendMailing(MailingSendForm aForm, HttpServletRequest req) throws Exception {
        String fullDate=null;
        int stepping, blocksize;
        boolean admin=false;
        boolean test=false;
        boolean world=false;
        java.util.Date sendDate=new Date();
        java.util.Date genDate=new Date();
        int startGen=1;
        MaildropEntry drop=(MaildropEntry) getBean("MaildropEntry");
        
        switch(aForm.getAction()) {
            case MailingSendAction.ACTION_SEND_ADMIN:
                drop.setStatus(MaildropEntry.STATUS_ADMIN);
                admin=true;
                break;
                
            case MailingSendAction.ACTION_SEND_TEST:
                drop.setStatus(MaildropEntry.STATUS_TEST);
                admin=true;
                test=true;
                break;
                
            case MailingSendAction.ACTION_SEND_WORLD:
                drop.setStatus(MaildropEntry.STATUS_WORLD);
                admin=true;
                test=true;
                world=true;
                break;
                
            case MailingSendAction.ACTION_ACTIVATE_RULEBASED:
                drop.setStatus(MaildropEntry.STATUS_DATEBASED);
                world=true;
                break;
                
            case MailingSendAction.ACTION_ACTIVATE_CAMPAIGN:
                drop.setStatus(MaildropEntry.STATUS_ACTIONBASED);
                world=true;
        }

        if(aForm.getSendDate()!=null) {
            GregorianCalendar aCal=new GregorianCalendar(TimeZone.getTimeZone(AgnUtils.getAdmin(req).getAdminTimezone()));

            aCal.set(Integer.parseInt(aForm.getSendDate().substring(0, 4)), Integer.parseInt(aForm.getSendDate().substring(4, 6))-1, Integer.parseInt(aForm.getSendDate().substring(6, 8)), aForm.getSendHour(), aForm.getSendMinute());
            sendDate=aCal.getTime();
        }
        
        MailingDao mDao=(MailingDao) getBean("MailingDao");
        Mailing aMailing=mDao.getMailing(aForm.getMailingID(), getCompanyID(req));
        
        if(aMailing==null) {
            return;
        }
        
        MailinglistDao listDao=(MailinglistDao) getBean("MailinglistDao");
        Mailinglist aList=listDao.getMailinglist(aMailing.getMailinglistID(), getCompanyID(req));
        
        if(aList.getNumberOfActiveSubscribers(admin, test, world, aMailing.getTargetID())==0) {
            throw new Exception("error.mailing.no_subscribers");
        }
        
        // check syntax of mailing by generating dummy preview
        aForm.setTextPreview(aMailing.getPreview(aMailing.getTextTemplate().getEmmBlock(), Mailing.INPUT_TYPE_TEXT, aForm.getPreviewCustomerID(), this.getWebApplicationContext()));
        if(aForm.getTextPreview().trim().length()==0) {
            throw new Exception("error.mailing.no_text_version");
        }
        aForm.setHtmlPreview(aMailing.getPreview(aMailing.getHtmlTemplate().getEmmBlock(), Mailing.INPUT_TYPE_HTML, aForm.getPreviewCustomerID(), this.getWebApplicationContext()));
        if(aForm.getEmailFormat()>0 && aForm.getHtmlPreview().trim().length()==0) {
            throw new Exception("error.mailing.no_html_version");
        }
        aForm.setSubjectPreview(aMailing.getPreview(aMailing.getEmailParam(this.getWebApplicationContext()).getSubject(), Mailing.INPUT_TYPE_HTML, aForm.getPreviewCustomerID(), this.getWebApplicationContext()));
        if(aForm.getSubjectPreview().trim().length()==0) {
            throw new Exception("error.mailing.subject.too_short");
        }
        aForm.setSenderPreview(aMailing.getPreview(aMailing.getEmailParam(this.getWebApplicationContext()).getFromAdr(), Mailing.INPUT_TYPE_HTML, aForm.getPreviewCustomerID(), this.getWebApplicationContext()));
        if(aForm.getSenderPreview().trim().length()==0) {
            throw new Exception("error.mailing.sender_adress");
        }
        
        drop.setSendDate(sendDate);
        
        if(AgnUtils.isDateInFuture(sendDate)) {
            // sent gendate if senddate is in future
            GregorianCalendar tmpGen=new GregorianCalendar();
            GregorianCalendar now=new GregorianCalendar();

            tmpGen.setTime(sendDate);
            tmpGen.add(GregorianCalendar.HOUR_OF_DAY, -3);
            if(tmpGen.before(now)) {
                tmpGen=now;
            }
            genDate=tmpGen.getTime();
        }
        
        if(AgnUtils.isDateInFuture(genDate)) {
            startGen=0;
        }
        
        if(world && aMailing.isWorldMailingSend()) {
            return;
        }
        
        drop.setGenStatus(startGen);
        drop.setGenDate(genDate);
        drop.setGenChangeDate(new Date());
        drop.setMailingID(aMailing.getId());
        drop.setCompanyID(aMailing.getCompanyID());
        
        aMailing.getMaildropStatus().add(drop);
        
        mDao.saveMailing(aMailing);
        if(startGen==1 && drop.getStatus()!=MaildropEntry.STATUS_ACTIONBASED && drop.getStatus()!=MaildropEntry.STATUS_DATEBASED) {
            aMailing.triggerMailing(drop.getId(), new Hashtable(), this.getWebApplicationContext());
        }
        AgnUtils.logger().info("send mailing id: "+aMailing.getId()+" type: "+drop.getStatus());
        
        return;
    }
    
    /**
     * Disables mailing.
     */
    protected void deactivateMailing(MailingSendForm aForm, HttpServletRequest req) throws Exception {
        MailingDao mDao=(MailingDao) getBean("MailingDao");
        Mailing aMailing=mDao.getMailing(aForm.getMailingID(), getCompanyID(req));
        
        if(aMailing==null) {
            return;
        }
        
        aMailing.cleanupMaildrop();
        
        mDao.saveMailing(aMailing);
        
        return;
    }
    
    /**
     * Gets a preview of mailing.
     */
    protected void getPreview(MailingSendForm aForm, int type, HttpServletRequest req) throws Exception {
        
        MailingDao mDao=(MailingDao) getBean("MailingDao");
        Mailing aMailing=mDao.getMailing(aForm.getMailingID(), this.getCompanyID(req));
        
        if(aMailing!=null) {
            switch(type) {
                case MailingSendAction.PREVIEW_MODE_TEXT:
                    aForm.setTextPreview(aMailing.getPreview(aMailing.getTextTemplate().getEmmBlock(), Mailing.INPUT_TYPE_TEXT, aForm.getPreviewCustomerID(), true, this.getWebApplicationContext()));
                    break;
                    
                case MailingSendAction.PREVIEW_MODE_HTML:
                    aForm.setHtmlPreview(aMailing.getPreview(aMailing.getHtmlTemplate().getEmmBlock(), Mailing.INPUT_TYPE_HTML, aForm.getPreviewCustomerID(), true, this.getWebApplicationContext()));
                    break;
                    
                case MailingSendAction.PREVIEW_MODE_HEADER:
                    aForm.setSenderPreview(aMailing.getPreview(aMailing.getEmailParam(this.getWebApplicationContext()).getFromAdr(), Mailing.INPUT_TYPE_HTML, aForm.getPreviewCustomerID(), this.getWebApplicationContext()));
                    aForm.setSubjectPreview(aMailing.getPreview(aMailing.getEmailParam(this.getWebApplicationContext()).getSubject(), Mailing.INPUT_TYPE_HTML, aForm.getPreviewCustomerID(), this.getWebApplicationContext()));
                    break;
            }
            aForm.setEmailFormat(aMailing.getEmailParam(this.getWebApplicationContext()).getMailFormat());
            aForm.setMailinglistID(aMailing.getMailinglistID());
        }
        
        return;
    }
    
    /**
     * Loads sending statistics.
     */
    protected void loadSendStats(MailingSendForm aForm, HttpServletRequest req) throws Exception {
        int anzText=0;
        int anzHtml=0;
        int anzOffline=0;
        int anzGesamt=0;
        StringBuffer sqlSelection=new StringBuffer(" ");
        Target aTarget=null;
        boolean isFirst=true;
        int numTargets=0;
        String tmpOp=new String("OR ");
        SqlRowSet rset=null;
        
        MailingDao mDao=(MailingDao) getBean("MailingDao");
        Mailing aMailing=mDao.getMailing(aForm.getMailingID(), getCompanyID(req));
        TargetDao tDao=(TargetDao) getBean("TargetDao");
        
        if(aMailing.getTargetMode()==Mailing.TARGET_MODE_AND) {
            tmpOp=new String("AND ");
        }
        
        if(aForm.getTargetGroups()!=null) {
            Iterator aIt=aForm.getTargetGroups().iterator();
            while(aIt.hasNext()) {
                aTarget=tDao.getTarget(((Integer)aIt.next()).intValue(), this.getCompanyID(req));
                if(aTarget!=null) {
                    if(isFirst) {
                        isFirst=false;
                    } else {
                        sqlSelection.append(tmpOp);
                    }
                    sqlSelection.append("("  + aTarget.getTargetSQL() + ") ");
                    numTargets++;
                }
            }
            if(numTargets>1) {
                sqlSelection.insert(0, " AND (");
            } else {
                sqlSelection.insert(0, " AND ");
            }
            if(!isFirst && numTargets>1) {
                sqlSelection.append(") ");
            }
        }
        
        String sqlStatement="SELECT count(*), bind.mediatype, cust.mailtype FROM customer_" + this.getCompanyID(req) + "_tbl cust, customer_" +
                this.getCompanyID(req) + "_binding_tbl bind WHERE bind.mailinglist_id=" + aMailing.getMailinglistID() +
                " AND cust.customer_id=bind.customer_id" + sqlSelection.toString() + " AND bind.user_status=1 GROUP BY bind.mediatype, cust.mailtype";
        
        try {
            JdbcTemplate tmpl=new JdbcTemplate((DataSource) getBean("dataSource"));
            
            rset=tmpl.queryForRowSet(sqlStatement);
            while(rset.next()==true){
                switch(rset.getInt(2)) {
                    case 0:
                        switch(rset.getInt(3)) {
                            case 0: // nur Text
                                anzText+=rset.getInt(1);
                                break;
                                
                            case 1: // Online-HTML
                                if(aMailing.getEmailParam(this.getWebApplicationContext()).getMailFormat()==0) { // nur Text-Mailing
                                    anzText+=rset.getInt(1);
                                } else {
                                    anzHtml+=rset.getInt(1);
                                }
                                break;
                                
                            case 2: // Offline-HTML
                                if(aMailing.getEmailParam(this.getWebApplicationContext()).getMailFormat()==0) { // nur Text-Mailing
                                    anzText+=rset.getInt(1);
                                }
                                if(aMailing.getEmailParam(this.getWebApplicationContext()).getMailFormat()==1) { // nur Text/Online-HTML-Mailing
                                    anzHtml+=rset.getInt(1);
                                }
                                if(aMailing.getEmailParam(this.getWebApplicationContext()).getMailFormat()==2) { // alle Formate
                                    anzOffline+=rset.getInt(1);
                                }
                                break;
                        }
                        break;
                }
            }
        } catch ( Exception e) {
            AgnUtils.logger().error("loadSendStats: " + e);
            AgnUtils.logger().error("SQL: " + sqlStatement);
            throw new Exception("SQL-Error: " + e);
        }
        
        anzGesamt+=anzText;
        anzGesamt+=anzHtml;
        anzGesamt+=anzOffline;
        
        aForm.setSendStatText(anzText);
        aForm.setSendStatHtml(anzHtml);
        aForm.setSendStatOffline(anzOffline);
        aForm.setSendStatAll(anzGesamt);
        
        return;
    }  
}
