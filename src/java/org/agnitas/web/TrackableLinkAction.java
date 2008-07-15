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

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.beans.Mailing;
import org.agnitas.beans.TrackableLink;
import org.agnitas.dao.MailingDao;
import org.agnitas.dao.TrackableLinkDao;
import org.agnitas.util.AgnUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;


/**
 * Implementation of <strong>Action</strong> that validates a user logon.
 *
 * @author Martin Helff
 */

public final class TrackableLinkAction extends StrutsActionBase {

    public static final int ACTION_SET_STANDARD_ACTION = ACTION_LAST+1;

    public static final int ACTION_SET_STANDARD_DEEPTRACKING = ACTION_LAST+2;


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
        TrackableLinkForm aForm=null;
        ActionMessages errors = new ActionMessages();
        ActionForward destination=null;

        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }

        aForm=(TrackableLinkForm)form;

        AgnUtils.logger().info("Action: "+aForm.getAction());

       if(!allowed("mailing.content.show", req)) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
            saveErrors(req, errors);
            return null;
        }

        try {
            switch(aForm.getAction()) {
                case TrackableLinkAction.ACTION_LIST:
                    this.loadLinks(aForm, req);
                    destination=mapping.findForward("list");
                    break;

                case TrackableLinkAction.ACTION_VIEW:
                    aForm.setAction(TrackableLinkAction.ACTION_SAVE);
                    loadLink(aForm, req);
                    destination=mapping.findForward("view");
                    break;

                case TrackableLinkAction.ACTION_SAVE:
                    destination=mapping.findForward("list");
                    saveLink(aForm, req);
                    this.loadLinks(aForm, req);
                    break;

                case TrackableLinkAction.ACTION_SET_STANDARD_ACTION:
                    destination=mapping.findForward("list");
                    setStandardAction(aForm, req);
                    this.loadLinks(aForm, req);
                    break;

                case TrackableLinkAction.ACTION_SET_STANDARD_DEEPTRACKING:
                    destination=mapping.findForward("list");
                    setStandardDeeptracking(aForm, req);
                    this.loadLinks(aForm, req);
                    break;

                default:
                    aForm.setAction(TrackableLinkAction.ACTION_LIST);
                    this.loadLinks(aForm, req);
                    destination=mapping.findForward("list");
            }
        } catch (Exception e) {
            AgnUtils.logger().error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        }

        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(req, errors);
            AgnUtils.logger().error("saving errors: "+destination);
            // return (new ActionForward(mapping.getInput()));
        }

        return destination;

    }

    /**
     * Loads links.
     */
    protected void loadLinks(TrackableLinkForm aForm, HttpServletRequest req) throws Exception {

        MailingDao mDao=(MailingDao) getBean("MailingDao");
        Mailing aMailing=mDao.getMailing(aForm.getMailingID(), getCompanyID(req));

        aForm.setLinks(aMailing.getTrackableLinks().values());
        aForm.setShortname(aMailing.getShortname());
        aForm.setIsTemplate(aMailing.isIsTemplate());

        AgnUtils.logger().info("loadMailing: mailing loaded");
        return;
    }

    /**
     * Loads link.
     */
    protected void loadLink(TrackableLinkForm aForm, HttpServletRequest req) {

        TrackableLink aLink=null;

        TrackableLinkDao tDao=(TrackableLinkDao) getBean("TrackableLinkDao");
        aLink=tDao.getTrackableLink(aForm.getLinkID(), getCompanyID(req));

        if(aLink!=null) {
            aForm.setLinkName(aLink.getShortname());
            aForm.setTrackable(aLink.getUsage());
            aForm.setLinkUrl(aLink.getFullUrl());
            aForm.setLinkAction(aLink.getActionID());
            aForm.setRelevance(aLink.getRelevance());
            aForm.setDeepTracking(aLink.getDeepTracking());
            aLink.setRelevance(aForm.getRelevance());
            if(req.getParameter("deepTracking")!=null) {  // only if parameter is provided in form
                aLink.setDeepTracking(aForm.getDeepTracking());
            }
        } else {
            AgnUtils.logger().error("could not load link: "+aForm.getLinkID());
        }
        return;
    }

    /**
     * Saves link.
     */
    protected void saveLink(TrackableLinkForm aForm, HttpServletRequest req) {
        TrackableLink aLink=null;

        TrackableLinkDao tDao=(TrackableLinkDao) getBean("TrackableLinkDao");
        aLink=tDao.getTrackableLink(aForm.getLinkID(), getCompanyID(req));

        if(aLink!=null) {
            aLink.setShortname(aForm.getLinkName());
            aLink.setUsage(aForm.getTrackable());
            aLink.setActionID(aForm.getLinkAction());
            aLink.setRelevance(aForm.getRelevance());
            if(req.getParameter("deepTracking")!=null) {  // only if parameter is provided in form
                aLink.setDeepTracking(aForm.getDeepTracking());
            }
            tDao.saveTrackableLink(aLink);
        }

        return;
    }

    /**
     * Gets the link action.
     * Saves mailing.
     */
    protected void setStandardAction(TrackableLinkForm aForm, HttpServletRequest req) {
        TrackableLink aLink=null;

        MailingDao mDao=(MailingDao) getBean("MailingDao");
        Mailing aMailing=mDao.getMailing(aForm.getMailingID(), getCompanyID(req));
try {
        Iterator it=aMailing.getTrackableLinks().values().iterator();
        while(it.hasNext()) {
            aLink=(TrackableLink)it.next();
            aLink.setActionID(aForm.getLinkAction());
        }
} catch (Exception e) {
	System.err.println(e.getMessage());
	System.err.println(AgnUtils.getStackTrace(e));
}
        mDao.saveMailing(aMailing);

        return;
    }

    protected void setStandardDeeptracking(TrackableLinkForm aForm, HttpServletRequest req) {
        // set Default Deeptracking;
        TrackableLinkDao tDao=(TrackableLinkDao) getBean("TrackableLinkDao");
    	tDao.setDeeptracking(aForm.getDeepTracking(), this.getCompanyID(req), aForm.getMailingID());

        return;
    }
}
