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
 * the code written by AGNITAS AG are Copyright (c) 2014 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/

package org.agnitas.web;

import org.agnitas.beans.Admin;
import org.agnitas.beans.Mailing;
import org.agnitas.beans.TrackableLink;
import org.agnitas.dao.EmmActionDao;
import org.agnitas.dao.MailingDao;
import org.agnitas.dao.TrackableLinkDao;
import org.agnitas.emm.core.commons.util.ConfigService;
import org.agnitas.util.AgnUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Implementation of <strong>Action</strong> that validates a user logon.
 * 
 * @author Martin Helff
 */

public class TrackableLinkAction extends StrutsActionBase {
	private static final transient Logger logger = Logger.getLogger(TrackableLinkAction.class);

	public static final int ACTION_SET_STANDARD_ACTION = ACTION_LAST + 1;

	public static final int ACTION_GLOBAL_USAGE = ACTION_LAST + 2;

	public static final int ACTION_SAVE_ALL = ACTION_LAST + 3;

	public static final int ACTION_ORG_LAST = ACTION_SAVE_ALL;

    public static final int KEEP_UNCHANGED = -1;

	protected MailingDao mailingDao;
	protected EmmActionDao actionDao;
	protected TrackableLinkDao linkDao;
	protected ConfigService configService;

	@Required
	public void setConfigService(ConfigService configService) {
		this.configService = configService;
	}

	// --------------------------------------------------------- Public Methods

	/**
	 * Process the specified HTTP request, and create the corresponding HTTP
	 * response (or forward to another web component that will create it).
	 * Return an <code>ActionForward</code> instance describing where and how
	 * control should be forwarded, or <code>null</code> if the response has
	 * already been completed.
	 *
     * ACTION_LIST: loads list of trackable links into form; loads name, description, click-action and open-action of
     *     current mailing into form. Forwards to trackable links list page
     * <br><br>
     * ACTION_VIEW: loads data of chosen trackable link into form, forwards to trackable link view page
     * <br><br>
     * ACTION_SAVE: saves trackable link into database, loads list of trackable links into form, resets click action id,
     *     forwards to trackable links list page.
     * <br><br>
     * ACTION_SET_STANDARD_ACTION: checks defaultActionType property of form. If it is "link" - owerwrites actions of
     *     current mailing links with the one chosen by user. If it is "click" - changes the default click-action of
     *     mailing with the one chosen by user. If it is "open" - changes the default open-action of mailing with the
     *     one selected by user.<br>
     *     Saves current mailing in DB, loads list of trackable links into form, resets click action id, forwards to
     *     trackable links list page.
     * <br><br>
     * ACTION_GLOBAL_USAGE: updates "usage" property for all links of mailing with a values selected by user, saves
     *     mailing in DB, loads list of trackable links into form, forwards to trackable links list page.
     * <br><br>
     * Any other ACTION_* would cause a forward to "list"
     * <br><br>
     * If destination is "list" - loads all actions to request, loads all none-form actions to request
     *
	 * @param form ActionForm object
     * @param req request
     * @param res response
	 * @param mapping
	 *            The ActionMapping used to select this instance
	 * @exception IOException
	 *                if an input/output error occurs
	 * @exception ServletException
	 *                if a servlet exception occurs
	 * @return destination
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {

		// Validate the request parameters specified by the user
		TrackableLinkForm aForm = null;
		ActionMessages errors = new ActionMessages();
    	ActionMessages messages = new ActionMessages();
		ActionForward destination = null;

		if (!AgnUtils.isUserLoggedIn(req)) {
			return mapping.findForward("logon");
		}

		aForm = (TrackableLinkForm) form;

		if (logger.isInfoEnabled()) logger.info("Action: " + aForm.getAction());

		if (!allowed("mailing.content.show", req)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"error.permissionDenied"));
			saveErrors(req, errors);
			return null;
		}

		try {
			switch (aForm.getAction()) {
                case ACTION_LIST:
                    this.loadLinks(aForm, req);
                    destination = mapping.findForward("list");
                    break;

                case ACTION_VIEW:
                    aForm.setAction(ACTION_SAVE);
                    loadLink(aForm, req);
                    destination = mapping.findForward("view");
                    break;

                case ACTION_SAVE:
                    saveLink(aForm, req);
                    this.loadLinks(aForm, req);
                    destination = mapping.findForward("list");
                    aForm.setLinkAction(0);
                    break;

                case ACTION_SAVE_ALL:
                    saveAll(aForm, req);
                    this.loadLinks(aForm, req);
                    destination = mapping.findForward("list");
                    aForm.setLinkAction(0);
                    messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
                    break;

                default:
                    aForm.setAction(ACTION_LIST);
                    this.loadLinks(aForm, req);
                    destination = mapping.findForward("list");
                    break;
			}
		} catch (Exception e) {
			logger.error(
					"execute: " + e, e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"error.exception", configService.getValue(ConfigService.Value.SupportEmergencyUrl)));
		}

		// Report any errors we have discovered back to the original form
		if (!errors.isEmpty()) {
			saveErrors(req, errors);
			logger.error("saving errors: " + destination);
		}

		if (destination != null && ("list".equals(destination.getName()) || "view".equals(destination.getName()))) {
			loadActions(aForm, req);
            loadNotFormActions(req);
		}
		
		// Report any message (non-errors) we have discovered
		if (!messages.isEmpty()) {
			saveMessages(req, messages);
		}

        // Report any message (non-errors) we have discovered
        if (!messages.isEmpty()) {
        	saveMessages(req, messages);
        }

		return destination;
	}

    protected void saveAll(TrackableLinkForm aForm, HttpServletRequest req) throws Exception {
        Mailing aMailing = mailingDao.getMailing(aForm.getMailingID(), AgnUtils.getCompanyID(req));
        saveAllProceed(aMailing, aForm, req);
        mailingDao.saveMailing(aMailing);
     }

    protected void saveAllProceed(Mailing aMailing, TrackableLinkForm aForm, HttpServletRequest req) throws Exception {

        // ACTION_SET_STANDARD_ACTION
        // setStandardActions
        TrackableLink aLink;
        try {
            // set link actions
            int linkAction = aForm.getLinkAction();
            if (linkAction != KEEP_UNCHANGED) {
                Iterator<TrackableLink> it = aMailing.getTrackableLinks().values().iterator();
                while (it.hasNext()) {
                    aLink = it.next();
                    writeTrackableLinkActionChange(aForm, aLink, req);
                    aLink.setActionID(linkAction);
                }
            }
            writeCommonActionChanges(aMailing, aForm, req);
            aMailing.setOpenActionID(aForm.getOpenActionID());
            aMailing.setClickActionID(aForm.getClickActionID());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        // ACTION_GLOBAL_USAGE
        // saveGlobalUsage(aMailing, aForm, req);
        int globalUsage = aForm.getGlobalUsage();
        if (globalUsage != KEEP_UNCHANGED) {
            try {
                Map<String,TrackableLink> trackableLinks = aMailing.getTrackableLinks();
                Iterator<TrackableLink> it = trackableLinks.values().iterator();
                while (it.hasNext()) {
                    aLink = it.next();
                    writeTrackableLinkTrackableChange(aForm, aLink, req);
                    aLink.setUsage(globalUsage);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

    }

	/**
	 * Loads links.
	 */
	protected void loadLinks(TrackableLinkForm aForm, HttpServletRequest req) throws Exception {
		Mailing aMailing = mailingDao.getMailing(aForm.getMailingID(), AgnUtils.getCompanyID(req));

		aForm.setLinks(aMailing.getTrackableLinks().values());
		aForm.setShortname(aMailing.getShortname());
        aForm.setDescription(aMailing.getDescription());
		aForm.setIsTemplate(aMailing.isIsTemplate());
        aForm.setOpenActionID(aMailing.getOpenActionID());
        aForm.setClickActionID(aMailing.getClickActionID());

		if (logger.isInfoEnabled()) logger.info("loadMailing: mailing loaded");
	}

    protected void loadNotFormActions(HttpServletRequest req) {
        List emmNotFormActions = actionDao.getEmmNotFormActions(AgnUtils.getCompanyID(req));
        req.setAttribute("notFormActions", emmNotFormActions);
    }

	protected void loadActions(TrackableLinkForm aForm, HttpServletRequest request) {
		List actions = actionDao.getEmmActions(getCompanyID(request));
		request.setAttribute("actions", actions);
	}

	/**
	 * Loads link.
	 */
	protected void loadLink(TrackableLinkForm aForm, HttpServletRequest req) {
		TrackableLink aLink;
		aLink = linkDao.getTrackableLink(aForm.getLinkID(), AgnUtils.getCompanyID(req));
		if (aLink != null) {
			aForm.setLinkName(aLink.getShortname());
			aForm.setTrackable(aLink.getUsage());
			aForm.setLinkUrl(aLink.getFullUrl());
			aForm.setLinkAction(aLink.getActionID());
			aForm.setRelevance(aLink.getRelevance());
			aForm.setDeepTracking(aLink.getDeepTracking());
			aForm.setRelevance(aLink.getRelevance());
			if (req.getParameter("deepTracking") != null) { // only if parameter is provided in form
				aForm.setDeepTracking(aLink.getDeepTracking());
			}
		} else {
			logger.error("could not load link: " + aForm.getLinkID());
		}
	}

	/**
	 * Saves link.
	 */
	protected void saveLink(TrackableLinkForm aForm, HttpServletRequest req) {
		TrackableLink aLink;
		aLink = linkDao.getTrackableLink(aForm.getLinkID(), AgnUtils.getCompanyID(req));
        writeTrackableLinkChanges(aForm, aLink, req);
		if (aLink != null) {
			aLink.setShortname(aForm.getLinkName());
			aLink.setUsage(aForm.getTrackable());
			aLink.setActionID(aForm.getLinkAction());
			aLink.setRelevance(aForm.getRelevance());
			if (req.getParameter("deepTracking") != null) { // only if parameter is provided in form
				aLink.setDeepTracking(aForm.getDeepTracking());
			}
			linkDao.saveTrackableLink(aLink);
		}
	}

    protected void writeTrackableLinkChanges(TrackableLinkForm form, TrackableLink link, HttpServletRequest request){
        try {
            Admin admin = AgnUtils.getAdmin(request);
            int mailingId = form.getMailingID();
            int companyId = form.getCompanyID(request);
            String linkUrl= link.getFullUrl();
            //log description changes
            String oldDescription = link.getShortname();
            String newDescription = form.getLinkName();

            if(!oldDescription.equals(newDescription)){
                if( oldDescription.isEmpty() && !newDescription.isEmpty()){
                    writeUserActivityLog(admin, "edit mailing, ID = " + mailingId,
                            "Trackable link " + linkUrl + " description added");
                }
                if( !oldDescription.isEmpty() && newDescription.isEmpty() ){
                    writeUserActivityLog(admin, "edit mailing, ID = " + mailingId,
                            "Trackable link " + linkUrl + " description removed");
                }
                if((oldDescription.length()>0)&&(newDescription.length()>0)){
                    writeUserActivityLog(admin, "edit mailing, ID = " + mailingId,
                            "Trackable link " + linkUrl + " description changed");
                }
            }

            //log Trackable changes
            int newTrackable = form.getTrackable();
            int oldTrackable = link.getUsage();
            if(oldTrackable != newTrackable){
                writeUserActivityLog(admin, "edit mailing, ID = " + mailingId,
                        "Trackable link " + linkUrl + " Trackable changed from " + getTrackableName(oldTrackable) +
                                " to " + getTrackableName(newTrackable));
            }

            //log Action changes
            int newAction = form.getLinkAction();
            int oldAction = link.getActionID();
            if(oldAction != newAction){
                writeUserActivityLog(admin, "edit mailing, ID = " + mailingId,
                        "Trackable link " + linkUrl + " Action changed from " + actionDao.getEmmAction(oldAction, companyId) +
                                " to " + actionDao.getEmmAction(newAction, companyId));
            }

            if (logger.isInfoEnabled()){
                logger.info("save Trackable link of mailing with ID =  " + mailingId);
            }
        } catch (Exception e) {
            logger.error("Log OpenEMM Trackable link changes error" + e);
        }
    }

    protected void writeTrackableLinkActionChange(TrackableLinkForm form, TrackableLink link, HttpServletRequest request){
        try {
            Admin admin = AgnUtils.getAdmin(request);
            int mailingId = form.getMailingID();
            int companyId = form.getCompanyID(request);
            String linkUrl= link.getFullUrl();

            //log Action changes
            int newAction = form.getLinkAction();
            int oldAction = link.getActionID();
            if(oldAction != newAction){
                writeUserActivityLog(admin, "edit mailing, ID = " + mailingId,
                        "Trackable link " + linkUrl + " Action changed from " + getActionName(oldAction, companyId) +
                                " to " + getActionName(newAction, companyId));
            }

            if (logger.isInfoEnabled()){
                logger.info("save Trackable link Action, mailing ID =  " + mailingId);
            }
        } catch (Exception e) {
            logger.error("Log OpenEMM Trackable link Action changes error" + e);
        }
    }

    protected void writeTrackableLinkTrackableChange(TrackableLinkForm form, TrackableLink link, HttpServletRequest request){
        try {
            Admin admin = AgnUtils.getAdmin(request);
            int mailingId = form.getMailingID();
            String linkUrl= link.getFullUrl();

            //log Trackable changes
            int newTrackable = form.getGlobalUsage();
            int oldTrackable = link.getUsage();
            if(oldTrackable != newTrackable){
                writeUserActivityLog(admin, "edit mailing, ID = " + mailingId,
                        "Trackable link " + linkUrl + " Trackable changed from " + getTrackableName(oldTrackable) +
                                " to " + getTrackableName(newTrackable));
            }

            if (logger.isInfoEnabled()){
                logger.info("save Trackable link Trackable, mailing ID =  " + mailingId );
            }
        } catch (Exception e) {
            logger.error("Log OpenEMM Trackable link Trackable changes error" + e);
        }
    }

    protected void  writeCommonActionChanges(Mailing mailing, TrackableLinkForm form, HttpServletRequest request){
        try {
            Admin admin = AgnUtils.getAdmin(request);
            int mailingId = form.getMailingID();
            int companyId = form.getCompanyID(request);

            //log Open Action changes
            int newOpenAction = form.getOpenActionID();
            int oldOpenAction = mailing.getOpenActionID();
            if(oldOpenAction != newOpenAction){
                writeUserActivityLog(admin, "edit mailing, ID = " + mailingId,
                        "Trackable links Open Action changed from " + getActionName(oldOpenAction, companyId) +
                                " to " + getActionName(newOpenAction, companyId));
            }

            //log Open Action changes
            int newClickAction = form.getClickActionID();
            int oldClickAction = mailing.getClickActionID();
            if(oldClickAction != newClickAction){
                writeUserActivityLog(admin, "edit mailing, ID = " + mailingId,
                        "Trackable links Click Action changed from " + getActionName(oldClickAction, companyId) +
                                " to " + getActionName(newClickAction, companyId));
            }

            if (logger.isInfoEnabled()){
                logger.info("save Trackable links Open/Click Actions, mailing ID =  " + mailingId );
            }
        } catch (Exception e) {
            logger.error("Log OpenEMM Trackable links Open/Click Action changes error" + e);
        }
    }

    /**
     * Return mailing trackable link setting "Trackable" text representation by id
     *
     * @param type Trackable type id
     * @return     "Trackable" setting text representation
     */
    private String getTrackableName(int type){

        switch (type){
            case 0:
                return "not trackable";
            case 1:
                return "only text version";
            case 2:
                return "only HTML version";
            case 3:
                return "text and HTML version";
            default:
                return "unknown type";
        }
    }

    private String getActionName(int actionId, int companyId ){
        return actionDao.getEmmAction(actionId, companyId).getShortname();
    }

	public void setMailingDao(MailingDao mailingDao) {
		this.mailingDao = mailingDao;
	}

	public MailingDao getMailingDao() {
		return mailingDao;
	}

	public void setActionDao(EmmActionDao actionDao) {
		this.actionDao = actionDao;
	}

	public EmmActionDao getActionDao() {
		return actionDao;
	}

	public void setLinkDao(TrackableLinkDao linkDao) {
		this.linkDao = linkDao;
	}

	public TrackableLinkDao getLinkDao() {
		return linkDao;
	}
}
