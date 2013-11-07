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
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.dao.TargetDao;
import org.agnitas.dao.RecipientDao;
import org.agnitas.target.Target;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.SafeString;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

/**
 * Implementation of <strong>Action</strong> that handles Targets
 * 
 * @author Martin Helff, Nicole Serek
 */

public class TargetAction extends StrutsActionBase {

	public static final int ACTION_CREATE_ML = ACTION_LAST + 1;

	public static final int ACTION_CLONE = ACTION_LAST + 2;
	
	public static final int ACTION_DELETE_RECIPIENTS_CONFIRM = ACTION_LAST + 3;
	
	public static final int ACTION_DELETE_RECIPIENTS = ACTION_LAST + 4;
	
	public static final int ACTION_BACK_TO_MAILINGWIZARD = ACTION_LAST + 5;
	
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

		TargetForm aForm = null;
		ActionMessages errors = new ActionMessages();
		ActionMessages messages = new ActionMessages();
		ActionForward destination = null;

		if (!this.checkLogon(req)) {
			return mapping.findForward("logon");
		}

		if (form != null) {
			aForm = (TargetForm) form;
		} else {
			aForm = new TargetForm();
		}

		if (req.getParameter("delete.x") != null) {
			aForm.setAction(TargetAction.ACTION_CONFIRM_DELETE);
		}

		AgnUtils.logger().info("Action: " + aForm.getAction());

		if (!allowed("targets.show", req)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"error.permissionDenied"));
			saveErrors(req, errors);
			return null;
		}

		try {
			switch (aForm.getAction()) {
			case ACTION_LIST:
				if ( aForm.getColumnwidthsList() == null) {
                	aForm.setColumnwidthsList(getInitializedColumnWidthList(3));
                }				
				destination = mapping.findForward("list");
				break;

			case ACTION_VIEW:
				if (aForm.getTargetID() != 0) {
					aForm.setAction(TargetAction.ACTION_SAVE);
					loadTarget(aForm, req);
				} else {
					aForm.setAction(TargetAction.ACTION_NEW);
				}
				destination = mapping.findForward("view");
				break;

			case ACTION_SAVE:
				// if(req.getParameter("save.x")!=null) {
				saveTarget(aForm, req);
				// }
				destination = mapping.findForward("success");
				
				messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("changes_saved"));
				break;

			case ACTION_NEW:
				// if(req.getParameter("save.x")!=null) {
				saveTarget(aForm, req);
				aForm.setAction(TargetAction.ACTION_SAVE);
				// }
				destination = mapping.findForward("view");
				
				messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("changes_saved"));
				break;

			case ACTION_CONFIRM_DELETE:
				loadTarget(aForm, req);
				destination = mapping.findForward("delete");
				aForm.setAction(TargetAction.ACTION_DELETE);
				break;

			case ACTION_DELETE:
				this.deleteTarget(aForm, req);
				aForm.setAction(TargetAction.ACTION_LIST);
				destination = mapping.findForward("list");
				
				messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("changes_saved"));
				break;

			case ACTION_CREATE_ML:
				destination = mapping.findForward("create_ml");
				break;

			case ACTION_CLONE:
				if (aForm.getTargetID() != 0) {
					loadTarget(aForm, req);
					cloneTarget(aForm, req);
					aForm.setAction(TargetAction.ACTION_SAVE);
				}
				destination = mapping.findForward("view");
				break;
				
			case ACTION_DELETE_RECIPIENTS_CONFIRM:
				loadTarget(aForm, req);
				this.getRecipientNumber(aForm, req);
				destination = mapping.findForward("delete_recipients");
				break;
				
			case ACTION_DELETE_RECIPIENTS:
				loadTarget(aForm, req);
				this.deleteRecipients(aForm, req);				
				aForm.setAction(TargetAction.ACTION_LIST);
				destination = mapping.findForward("list");
				break;
				
			case ACTION_BACK_TO_MAILINGWIZARD:
				destination = mapping.findForward("back_mailingwizard");
				break;
				
			default:
				destination = mapping.findForward("list");
				break;
			}

		} catch (Exception e) {
			AgnUtils.logger().error(
					"execute: " + e + "\n" + AgnUtils.getStackTrace(e));
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"error.exception"));
		}
		
		if( "list".equals(destination.getName()) || "success".equals(destination.getName())) {
			req.setAttribute("targetlist", loadTargetList(req) );
			setNumberOfRows(req, aForm);
		}
		

		// Report any errors we have discovered back to the original form
		if (!errors.isEmpty()) {
			saveErrors(req, errors);
			return (new ActionForward(mapping.getInput()));
		}

		// Report any message (non-errors) we have discovered
		if (!messages.isEmpty()) {
			saveMessages(req, messages);
		}

		return destination;

	}

	/**
	 * Loads target.
	 */
	protected void loadTarget(TargetForm aForm, HttpServletRequest req)
			throws Exception {
		TargetDao targetDao = (TargetDao) getBean("TargetDao");
		Target aTarget = targetDao.getTarget(aForm.getTargetID(),
				getCompanyID(req));

		if (aTarget.getId() == 0) {
			AgnUtils.logger().warn(
					"loadTarget: could not load target " + aForm.getTargetID());
			aTarget = (Target) getBean("Target");
			aTarget.setId(aForm.getTargetID());
		}
		aForm.setShortname(aTarget.getTargetName());
		aForm.setDescription(aTarget.getTargetDescription());
		aForm.setTarget(aTarget.getTargetStructure());
		AgnUtils.logger().info(
				"loadTarget: target " + aForm.getTargetID() + " loaded");
	}

	/**
	 * Clone target.
	 */
	protected void cloneTarget(TargetForm aForm, HttpServletRequest req)
			throws Exception {
		aForm.setTargetID(0);
		aForm.setShortname(SafeString.getLocaleString("CopyOf", (Locale) req
				.getSession().getAttribute(Globals.LOCALE_KEY))
				+ " " + aForm.getShortname());
		saveTarget(aForm, req);
	}

	/**
	 * Saves target.
	 */
	protected void saveTarget(TargetForm aForm, HttpServletRequest req)
			throws Exception {
		TargetDao targetDao = (TargetDao) getBean("TargetDao");
		Target aTarget = targetDao.getTarget(aForm.getTargetID(),
				getCompanyID(req));

		if (aTarget == null) {
			// be sure to use id 0 if there is no existing object
			aForm.setTargetID(0);
			aTarget = (Target) getBean("Target");
			aTarget.setCompanyID(this.getCompanyID(req));
		}

		aTarget.setTargetName(aForm.getShortname());
		aTarget.setTargetDescription(aForm.getDescription());
		aTarget.setTargetSQL(aForm.getTarget().generateSQL());
		aTarget.setTargetStructure(aForm.getTarget());

		targetDao.saveTarget(aTarget);

		AgnUtils.logger().info("saveTarget: save target " + aTarget.getId());
		aForm.setTargetID(aTarget.getId());
	}

	/**
	 * Removes target.
	 */
	protected void deleteTarget(TargetForm aForm, HttpServletRequest req) {
		TargetDao targetDao = (TargetDao) getBean("TargetDao");

		targetDao.deleteTarget(aForm.getTargetID(), getCompanyID(req));
	}
	
	/**
	 * Gets number of recipients affected in a target group.
	 */
	protected void getRecipientNumber(TargetForm aForm, HttpServletRequest req) {
		TargetDao targetDao = (TargetDao) getBean("TargetDao");
		Target target = (Target) getBean("Target");
		RecipientDao recipientDao = (RecipientDao) getBean("RecipientDao");
		
		target = targetDao.getTarget(aForm.getTargetID(), aForm.getCompanyID(req));
		int numOfRecipients = recipientDao.sumOfRecipients(aForm.getCompanyID(req), target.getTargetSQL());
		
		aForm.setNumOfRecipients(numOfRecipients);
		
	}
	
	/**
	 * Removes recipients affected in a target group.
	 */
	protected void deleteRecipients(TargetForm aForm, HttpServletRequest req) {
		TargetDao targetDao = (TargetDao) getBean("TargetDao");
		Target target = (Target) getBean("Target");
		RecipientDao recipientDao = (RecipientDao) getBean("RecipientDao");

		target = targetDao.getTarget(aForm.getTargetID(), aForm.getCompanyID(req));
		recipientDao.deleteRecipients(aForm.getCompanyID(req), target.getTargetSQL());
	}
	
	/**
	 * load the list of targets
	 * @param request
	 * @return
	 */
	private List loadTargetList(HttpServletRequest request) {
		TargetDao targetDao = (TargetDao) getBean("TargetDao");
		return targetDao.getTargets(AgnUtils.getCompanyID(request));
		
	}
}
