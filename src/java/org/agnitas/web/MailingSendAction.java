/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/

package org.agnitas.web;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.agnitas.beans.DynamicTag;
import org.agnitas.beans.DynamicTagContent;
import org.agnitas.beans.MaildropEntry;
import org.agnitas.beans.Mailing;
import org.agnitas.beans.MailingComponent;
import org.agnitas.beans.Mailinglist;
import org.agnitas.cms.utils.CmsUtils;
import org.agnitas.dao.MailingComponentDao;
import org.agnitas.dao.MailingDao;
import org.agnitas.dao.MailinglistDao;
import org.agnitas.dao.TargetDao;
import org.agnitas.preview.Preview;
import org.agnitas.stat.DeliveryStat;
import org.agnitas.target.Target;
import org.agnitas.util.AgnUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.springframework.jdbc.datasource.DataSourceUtils;


/**
 * Implementation of <strong>Action</strong> that validates a user logon.
 *
 * @author Martin Helff
 */

public class MailingSendAction extends StrutsActionBase {

    public static final int ACTION_VIEW_SEND = ACTION_LAST+1;

    public static final int ACTION_SEND_ADMIN = ACTION_LAST+2;

    public static final int ACTION_SEND_TEST = ACTION_LAST+3;

    public static final int ACTION_SEND_WORLD = ACTION_LAST+4;

    public static final int ACTION_VIEW_SEND2 = ACTION_LAST+5;

    public static final int ACTION_VIEW_DELSTATBOX = ACTION_LAST+6;

    public static final int ACTION_ACTIVATE_CAMPAIGN = ACTION_LAST+7;

    public static final int ACTION_ACTIVATE_RULEBASED = ACTION_LAST+8;

    public static final int ACTION_PREVIEW_SELECT = ACTION_LAST+9;

    public static final int ACTION_PREVIEW = ACTION_LAST+10;

    public static final int ACTION_PREVIEW_HEADER = ACTION_LAST+13;

    public static final int ACTION_DEACTIVATE_MAILING = ACTION_LAST+14;

    public static final int ACTION_CHANGE_SENDDATE = ACTION_LAST+15;

    public static final int ACTION_CANCEL_MAILING_REQUEST = ACTION_LAST+16;

    public static final int ACTION_CANCEL_MAILING = ACTION_LAST+17;

    public static final int ACTION_CONFIRM_SEND_WORLD = ACTION_LAST+18;

    public static final int ACTION_SEND_LAST = ACTION_LAST+18;

    public static final int PREVIEW_MODE_HEADER = 1;
    public static final int PREVIEW_MODE_TEXT = 2;
    public static final int PREVIEW_MODE_HTML = 3;
    public static final int PREVIEW_MODE_OFFLINE = 4;

	//
	private final Pattern targetIdsFromExpressionPattern = Pattern.compile( "^.*?(\\d+)(.*)$");


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
                        aForm.setAction(MailingSendAction.ACTION_CONFIRM_SEND_WORLD);
                        destination=mapping.findForward("send2");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case MailingSendAction.ACTION_SEND_ADMIN:
                    if(allowed("mailing.send.admin", req)) {
                        loadMailing(aForm, req);
                        sendMailing(aForm, req);
                        loadDeliveryStats(aForm, req);
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
                        loadDeliveryStats(aForm, req);
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

                case ACTION_CONFIRM_SEND_WORLD:
                    loadMailing(aForm, req);
                    aForm.setAction(MailingSendAction.ACTION_SEND_WORLD);
                    destination=mapping.findForward("send_confirm");
                    break;

                case MailingSendAction.ACTION_ACTIVATE_RULEBASED:
                case MailingSendAction.ACTION_ACTIVATE_CAMPAIGN:
                case MailingSendAction.ACTION_SEND_WORLD:
                    if(allowed("mailing.send.world", req)) {
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

                    if(hasPreviewRecipient(aForm, req)) {
                		aForm.setHasPreviewRecipient(true);
                	} else {
                		aForm.setHasPreviewRecipient(false);

                		errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.preview.no_recipient"));
                	}

                    destination=mapping.findForward("preview_select");
                    break;

                case MailingSendAction.ACTION_PREVIEW_HEADER:
                	destination=mapping.findForward("preview_header");
                    this.getHeaderPreview(aForm, req);
                    break;

                case MailingSendAction.ACTION_PREVIEW:
                	if(hasPreviewRecipient(aForm, req)) {
                		aForm.setHasPreviewRecipient(true);

                        getPreview(aForm, req);
                        destination=mapping.findForward("preview."+aForm.getPreviewFormat());
                	} else {
                		aForm.setHasPreviewRecipient(false);
                    	destination=mapping.findForward("preview_errors");

                		errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.preview.no_recipient"));
                	}
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

    protected boolean hasPreviewRecipient(MailingSendForm aForm, HttpServletRequest req) {
    	MailingDao mDao = (MailingDao) getBean("MailingDao");

    	return mDao.hasPreviewRecipients(aForm.getMailingID(), aForm.getCompanyID(req));
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
        aForm.setHasDeletedTargetGroups(hasDeletedTargetGroups(aMailing));

        req.setAttribute("targetGroups", tDao.getTargets(this.getCompanyID(req)));
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
    	int stepping, blocksize;
        boolean admin=false;
        boolean test=false;
        boolean world=false;
        java.util.Date sendDate=new java.util.Date();
        java.util.Date genDate=new java.util.Date();
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
        stepping = 0;
        blocksize = 0;
        try {
			stepping = aForm.getStepping();
			blocksize = aForm.getBlocksize();
		} catch (Exception e) {
			stepping = 0;
			blocksize = 0;
		}

        MailinglistDao listDao=(MailinglistDao) getBean("MailinglistDao");
        Mailinglist aList=listDao.getMailinglist(aMailing.getMailinglistID(), getCompanyID(req));
        String preview=null;

        if(listDao.getNumberOfActiveSubscribers(admin, test, world, aMailing.getTargetID(), aList.getCompanyID(), aList.getId())==0) {
            throw new Exception("error.mailing.no_subscribers");
        }

        // check syntax of mailing by generating dummy preview
        preview=aMailing.getPreview(aMailing.getTextTemplate().getEmmBlock(), Mailing.INPUT_TYPE_TEXT, aForm.getPreviewCustomerID(), this.getWebApplicationContext());
        if(preview.trim().length()==0) {
            throw new Exception("error.mailing.no_text_version");
        }
        preview=aMailing.getPreview(aMailing.getHtmlTemplate().getEmmBlock(), Mailing.INPUT_TYPE_HTML, aForm.getPreviewCustomerID(), this.getWebApplicationContext());
        if(aForm.getEmailFormat()>0 && preview.trim().length()==0) {
            throw new Exception("error.mailing.no_html_version");
        }
        preview=aMailing.getPreview(aMailing.getEmailParam(this.getWebApplicationContext()).getSubject(), Mailing.INPUT_TYPE_HTML, aForm.getPreviewCustomerID(), this.getWebApplicationContext());
        if(preview.trim().length()==0) {
            throw new Exception("error.mailing.subject.too_short");
        }
        preview=aMailing.getPreview(aMailing.getEmailParam(this.getWebApplicationContext()).getFromAdr(), Mailing.INPUT_TYPE_HTML, aForm.getPreviewCustomerID(), this.getWebApplicationContext());
        if(preview.trim().length()==0) {
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
        drop.setGenChangeDate(new java.util.Date());
        drop.setMailingID(aMailing.getId());
        drop.setCompanyID(aMailing.getCompanyID());
        drop.setStepping(stepping);
		drop.setBlocksize(blocksize);

        aMailing.getMaildropStatus().add(drop);

        mDao.saveMailing(aMailing);
        if(startGen==1 && drop.getStatus()!=MaildropEntry.STATUS_ACTIONBASED && drop.getStatus()!=MaildropEntry.STATUS_DATEBASED) {
			CmsUtils.generateClassicTemplate(aForm.getMailingID(), req, getWebApplicationContext());
            aMailing.triggerMailing(drop.getId(), new Hashtable(), this.getWebApplicationContext());
        }
        AgnUtils.logger().info("send mailing id: "+aMailing.getId()+" type: "+drop.getStatus());
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

        aMailing.cleanupMaildrop(getWebApplicationContext());

        mDao.saveMailing(aMailing);
    }

    /**
     * Gets a preview of mailing.
     */
    protected void getPreview(MailingSendForm aForm, HttpServletRequest req) throws Exception {
        MailingDao mDao=(MailingDao) getBean("MailingDao");
        Mailing aMailing=mDao.getMailing(aForm.getMailingID(), this.getCompanyID(req));
        String[] tmplNames={ "Text", "Html", "FAX", "PRINT", "MMS", "SMS"  };

        if(aMailing == null)
            return;
        if( aForm.getPreviewFormat()==  Mailing.INPUT_TYPE_HTML ||  aForm.getPreviewFormat()==  Mailing.INPUT_TYPE_TEXT ) {


			Preview preview = new Preview();
			Hashtable<String, Object>	output = preview.createPreview (aMailing.getId(), aForm.getPreviewCustomerID(), true);
			if( aForm.getPreviewFormat() == Mailing.INPUT_TYPE_HTML ) {
				aForm.setPreview((String) output.get(Preview.ID_HTML));
			}
			if ( aForm.getPreviewFormat() == Mailing.INPUT_TYPE_TEXT ) {
				String previewString = (String) output.get(org.agnitas.preview.Preview.ID_TEXT);
				if( previewString.indexOf("<pre>") > -1  ) {
					previewString = previewString.substring( previewString.indexOf("<pre>") + 5, previewString.length() );
				}
				if(previewString.lastIndexOf("</pre>") > -1) {
					previewString = previewString.substring(0,previewString.lastIndexOf("</pre>"));
				}
				aForm.setPreview( previewString );
			}
			preview.done();
		}
		else {

		aForm.setPreview(aMailing.getPreview(aMailing.getTemplate(
				tmplNames[aForm.getPreviewFormat()]).getEmmBlock(), aForm
				.getPreviewFormat(), aForm.getPreviewCustomerID(), true,
				this.getWebApplicationContext()));
		}
        aForm.setEmailFormat(aMailing.getEmailParam(this.getWebApplicationContext()).getMailFormat());
        aForm.setMailinglistID(aMailing.getMailinglistID());
    }

    /**
     * Gets a preview of mailing.
     */
    protected void getHeaderPreview(MailingSendForm aForm, HttpServletRequest req) throws Exception {
        MailingDao mDao=(MailingDao) getBean("MailingDao");
        Mailing aMailing=mDao.getMailing(aForm.getMailingID(), this.getCompanyID(req));

        if(aMailing!=null) {
            aForm.setSenderPreview(aMailing.getPreview(aMailing.getEmailParam(this.getWebApplicationContext()).getFromAdr(), Mailing.INPUT_TYPE_HTML, aForm.getPreviewCustomerID(), this.getWebApplicationContext()));
            aForm.setSubjectPreview(aMailing.getPreview(aMailing.getEmailParam(this.getWebApplicationContext()).getSubject(), Mailing.INPUT_TYPE_HTML, aForm.getPreviewCustomerID(), this.getWebApplicationContext()));
        }
        aForm.setEmailFormat(aMailing.getEmailParam(this.getWebApplicationContext()).getMailFormat());
        aForm.setMailinglistID(aMailing.getMailinglistID());
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
        DataSource ds=(DataSource) getBean("dataSource");

        MailingDao mDao=(MailingDao) getBean("MailingDao");
        Mailing aMailing=mDao.getMailing(aForm.getMailingID(), getCompanyID(req));
        TargetDao tDao=(TargetDao) getBean("TargetDao");

        if(aMailing.getTargetMode()==Mailing.TARGET_MODE_AND) {
            tmpOp=new String("AND ");
        }

        if(aForm.getTargetGroups()!=null && aForm.getTargetGroups().size() > 0) {
            Iterator<Integer> aIt=aForm.getTargetGroups().iterator();

            while(aIt.hasNext()) {
                aTarget=tDao.getTarget(aIt.next(), this.getCompanyID(req));
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

        Connection con=DataSourceUtils.getConnection(ds);

        try {
            Statement stmt=con.createStatement();
            ResultSet rset=stmt.executeQuery(sqlStatement);

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
                    default:
                        aForm.setSendStat(rset.getInt(2), rset.getInt(1));
                }
            }
            rset.close();
            stmt.close();
        } catch ( Exception e) {
            DataSourceUtils.releaseConnection(con, ds);
            AgnUtils.logger().error("loadSendStats: " + e);
            AgnUtils.logger().error("SQL: " + sqlStatement);
            throw new Exception("SQL-Error: " + e);
        }
        DataSourceUtils.releaseConnection(con, ds);

        anzGesamt+=anzText;
        anzGesamt+=anzHtml;
        anzGesamt+=anzOffline;

        aForm.setSendStatText(anzText);
        aForm.setSendStatHtml(anzHtml);
        aForm.setSendStatOffline(anzOffline);
        aForm.setSendStat(0, anzGesamt);
    }

    protected boolean hasDeletedTargetGroups(Mailing mailing) {
        TargetDao targetDao = (TargetDao) getBean("TargetDao");

    	Set<Integer> targetIds = new HashSet<Integer>();

    	targetIds.addAll(getTargetIdsFromExpression(mailing));
    	targetIds.addAll(getTargetIdsFromContent(mailing));
    	targetIds.addAll(getTargetIdsFromAttachments(mailing));

    	for( int targetId : targetIds) {
    		Target target = targetDao.getTarget(targetId, mailing.getCompanyID());

    		if( target == null)
    			continue;

    		if( target.getDeleted() != 0)
    			return true;
    	}

    	return false;
    }

    protected Set<Integer> getTargetIdsFromExpression(Mailing mailing) {
    	Set<Integer> targetIds = new HashSet<Integer>();

    	String expression = mailing.getTargetExpression();
    	if( expression != null) {
    		Matcher matcher = this.targetIdsFromExpressionPattern.matcher(expression);

    		while( matcher.matches()) {
    			targetIds.add(Integer.parseInt(matcher.group(1)));
    			expression = matcher.group(2);
    			matcher = this.targetIdsFromExpressionPattern.matcher(expression);
    		}
    	}

    	return targetIds;
    }

    protected Set<Integer> getTargetIdsFromContent(Mailing mailing) {
    	Set<Integer> targetIds = new HashSet<Integer>();

    	for( Object tagObject : mailing.getDynTags().values()) {
    		DynamicTag tag = (DynamicTag) tagObject;

    		for( Object contentObject : tag.getDynContent().values()) {
    			DynamicTagContent content = (DynamicTagContent) contentObject;
    			targetIds.add( content.getTargetID());
    		}
    	}

    	return targetIds;
    }

    protected Set<Integer> getTargetIdsFromAttachments(Mailing mailing) {
    	MailingComponentDao mailingComponentDao = (MailingComponentDao) getBean("MailingComponentDao");
    	List result = mailingComponentDao.getMailingComponents(mailing.getId(), mailing.getCompanyID(), MailingComponent.TYPE_ATTACHMENT);

    	Set<Integer> targetIds = new HashSet<Integer>();
    	for( Object attachmentObj : result) {
    		MailingComponent component = (MailingComponent) attachmentObj;

    		targetIds.add( component.getTargetID());
    	}

    	return targetIds;
    }
}
