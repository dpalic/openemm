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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Iterator;
import java.util.Vector;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.beans.Mailing;
import org.agnitas.beans.MailingComponent;
import org.agnitas.beans.Mediatype;
import org.agnitas.beans.MediatypeEmail;
import org.agnitas.beans.DynamicTag;
import org.agnitas.beans.DynamicTagContent;
import org.agnitas.dao.MailingDao;
import org.agnitas.dao.MailinglistDao;
import org.agnitas.util.AgnUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

/**
 * Implementation of <strong>Action</strong> that handles Mailings
 * 
 * @author Martin Helff, Nicole Serek, Andreas Rehak
 */

public class MailingWizardAction extends StrutsDispatchActionBase {

	public static final String ACTION_START = "start";

	public static final String ACTION_NAME = "name";

	public static final String ACTION_TEMPLATE = "template";

	public static final String ACTION_TYPE = "type";

	public static final String ACTION_SENDADDRESS = "sendaddress";

	public static final String ACTION_MAILTYPE = "mailtype";

	public static final String ACTION_SUBJECT = "subject";

	public static final String ACTION_TARGET = "target";

	public static final String ACTION_TEXTMODULES = "textmodules";

	public static final String ACTION_TEXTMODULE = "textmodule";

	public static final String ACTION_TEXTMODULE_ADD = "textmodule_add";

	public static final String ACTION_MEASURELINKS = "links";

	public static final String ACTION_MEASURELINK = "link";

	public static final String ACTION_ATTACHMENT = "attachment";

	public static final String ACTION_FINISH = "finish";

	// --------------------------------------------------------- Public Methods

	/**
	 * Initialization
	 */
	public ActionForward init(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!this.checkLogon(req)) {
			return mapping.findForward("logon");
		}

		return mapping.getInputForward();
	}

	/**
	 * Starts mailing.
	 */
	public ActionForward start(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!this.checkLogon(req)) {
			return mapping.findForward("logon");
		}

		MailinglistDao mDao=(MailinglistDao) getBean("MailinglistDao");
		List mlists=mDao.getMailinglists(getCompanyID(req));

		if(mlists.size() <= 0) {
			ActionMessages	errors = new ActionMessages();

			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.mailing.noMailinglist"));
			saveErrors(req, errors);
			return mapping.getInputForward();
		}

		MailingWizardForm aForm = (MailingWizardForm) form;
		Mailing mailing = (Mailing) getBean("Mailing");

		mailing.init(getCompanyID(req), getWebApplicationContext());
		aForm.setMailing(mailing);

		return mapping.findForward("next");
	}

	/**
	 * Names mailing.
	 */
	public ActionForward name(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!this.checkLogon(req)) {
			return mapping.findForward("logon");
		}

		MailingWizardForm aForm = (MailingWizardForm) form;
		Mailing mailing = aForm.getMailing();

		if (mailing != null) {
			mailing.setShortname(aForm.getMailing().getShortname());
			mailing.setDescription(
					aForm.getMailing().getDescription());
			mailing.setIsTemplate(false);
		}
		return mapping.findForward("next");
	}

	/**
	 * Saves the template for the new mailing
	 */
	public ActionForward template(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!this.checkLogon(req)) {
			return mapping.findForward("logon");
		}

		MailingWizardForm aForm = (MailingWizardForm) form;
		MailingDao mDao = (MailingDao) getBean("MailingDao");
		Mailing mailing = aForm.getMailing();

		if (aForm.getMailing().getMailTemplateID() == 0) {
			mailing.setIsTemplate(false);

			Map mediatypes = mailing.getMediatypes();

			Mediatype type = (Mediatype) mediatypes.get(0);
			if (type != null) {
				type.setStatus(Mediatype.STATUS_ACTIVE);
			} else {
				// should not happen
				MediatypeEmail paramEmail = mailing
						.getEmailParam(getWebApplicationContext());

				paramEmail.setCharset("iso-8859-1");
				paramEmail.setMailFormat(1);
				paramEmail.setLinefeed(0);
				paramEmail.setPriority(1);
				paramEmail.setStatus(Mediatype.STATUS_ACTIVE);
				mediatypes.put(0, paramEmail);
			}
		} else {
			Mailing template = mDao.getMailing(aForm.getMailing().getMailTemplateID(), getCompanyID(req));

			if (template != null) {
				Mailing newMailing = (Mailing) template
						.clone(getWebApplicationContext());
				newMailing.setId(0); // 0 for creating a new mailing and not
				// changing the template
				newMailing.setShortname(
					aForm.getMailing().getShortname());
				newMailing.setDescription(
					aForm.getMailing().getDescription());
				newMailing.setIsTemplate(false);
				newMailing.setMediatypes(template.getMediatypes());
				newMailing.setMailTemplateID(template.getId());
				newMailing.setCompanyID(aForm.getCompanyID(req));
				newMailing.setMailinglistID(template.getMailinglistID());
				newMailing.setArchived(template.getArchived());

				Map mediatypes = newMailing.getMediatypes();

				Mediatype type = (Mediatype) mediatypes.get(0);
				if (type != null) {
					type.setStatus(Mediatype.STATUS_ACTIVE);
				}
				aForm.setMailing(newMailing);

				MediatypeEmail param = newMailing
						.getEmailParam(getWebApplicationContext());
				// param.setStatus(Mediatype.STATUS_ACTIVE);
				aForm.setEmailSubject(param.getSubject());
				aForm.setEmailFormat(param.getMailFormat());
				aForm.setEmailOnepixel(param.getOnepixel());
				aForm.setSenderEmail(param.getFromEmail());
				aForm.setSenderFullname(param.getFromFullname());
				aForm.setReplyEmail(param.getReplyEmail());
				aForm.setReplyFullname(param.getReplyFullname());
				
				aForm.setMailing(newMailing);
			}
		}
		return mapping.findForward("next");
	}

	/**
	 * saves the type for the mailing
	 */
	public ActionForward type(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!this.checkLogon(req)) {
			return mapping.findForward("logon");
		}
		MailingWizardForm aForm = (MailingWizardForm) form;
		Mailing mailing = aForm.getMailing();

		mailing.setMailingType(aForm.getMailing().getMailingType());
		return mapping.findForward("next");
	}

	/**
	 * gets the address information for the new mailing
	 */
	public ActionForward sendaddress(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!this.checkLogon(req)) {
			return mapping.findForward("logon");
		}
		MailingWizardForm aForm = (MailingWizardForm) form;
		Mailing mailing = aForm.getMailing();

		MediatypeEmail param = mailing
				.getEmailParam(getWebApplicationContext());
		param.setFromEmail(aForm.getSenderEmail());
		param.setFromFullname(aForm.getSenderFullname());
		param.setReplyEmail(aForm.getReplyEmail());
		param.setReplyFullname(aForm.getReplyFullname());

		return mapping.findForward("next");
	}

	/**
	 * 
	 */
	public ActionForward subject(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!this.checkLogon(req)) {
			return mapping.findForward("logon");
		}
		MailingWizardForm aForm = (MailingWizardForm) form;
		MediatypeEmail param = aForm.getMailing().getEmailParam(
				this.getWebApplicationContext());

		param.setSubject(aForm.getEmailSubject());
		aForm.getMailing().buildDependencies(true,
				getWebApplicationContext());
		return mapping.findForward("next");
	}

	/**
	 * 
	 */
	public ActionForward mailtype(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!this.checkLogon(req)) {
			return mapping.findForward("logon");
		}
		MailingWizardForm aForm = (MailingWizardForm) form;
		MediatypeEmail param = aForm.getMailing().getEmailParam(
				getWebApplicationContext());

		param.setMailFormat(aForm.getEmailFormat());

		return mapping.findForward("next");
	}

	/**
	 * 
	 */
	public ActionForward target(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!this.checkLogon(req)) {
			return mapping.findForward("logon");
		}
		MailingWizardForm aForm = (MailingWizardForm) form;
		Mailing mailing = aForm.getMailing();

		mailing.setMailinglistID(mailing.getMailinglistID());
		mailing.setCampaignID(mailing.getCampaignID());
		MediatypeEmail param = mailing.getEmailParam(
						getWebApplicationContext());
		param.setOnepixel(aForm.getEmailOnepixel());

		if (aForm.getTargetID() != 0) {
			Collection aList = mailing.getTargetGroups();

			if (aList == null) {
				aList = new HashSet();
			}
			if (!aList.contains(new Integer(aForm.getTargetID()))) {
				aList.add(new Integer(aForm.getTargetID()));
			}
			mailing.setTargetGroups(aList);
			return mapping.getInputForward();
		}

		if (aForm.getRemoveTargetID() != 0) {
			Collection aList = aForm.getMailing().getTargetGroups();

			if (aList != null) {
				aList.remove(new Integer(aForm.getRemoveTargetID()));
			}
			return mapping.getInputForward();
		}
		return mapping.findForward("next");
	}

	/**
	 * gets the first dynName for inserting the content
	 */
	public ActionForward textmodule(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!this.checkLogon(req)) {
			return mapping.findForward("logon");
		}

		MailingWizardForm aForm = (MailingWizardForm) form;
		Mailing mailing = aForm.getMailing();
		DynamicTag dynTag = null;

		dynTag = (DynamicTag) mailing.getDynTags().get(aForm.getDynName());
		if (dynTag != null) {
			dynTag.setMailingID(mailing.getId());
			dynTag.setCompanyID(mailing.getCompanyID());

			mailing.cleanupTrackableLinks(new Vector());
			mailing.buildDependencies(true, getWebApplicationContext());
			if (aForm.getDynName() != null
					&& aForm.getDynName().trim().length() != 0) {
				Iterator it = mailing.getDynTags().keySet().iterator();
				while (it.hasNext()) {
					if (it.next().equals(aForm.getDynName())) {
						break;
					}
				}
				if(!it.hasNext()) {
					return mapping.findForward("skip");
				}
				aForm.setDynName((String) it.next());
			}
			return mapping.findForward("next");
		}

		if (aForm.getDynName() == null
				|| aForm.getDynName().trim().length() == 0) {
			aForm.setDynName((String) mailing.getDynTags().keySet()
					.iterator().next());
		}

		return mapping.findForward("next");
	}

	/**
	 * adds the content from the active textmodule and gets the next dynName
	 */
	public ActionForward textmodule_add(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		if (!this.checkLogon(req)) {
			return mapping.findForward("logon");
		}

		MailingWizardForm aForm = (MailingWizardForm) form;
		Mailing mailing = aForm.getMailing();
		DynamicTag dynTag = (DynamicTag) mailing.getDynTags().get(aForm.getDynName());
		DynamicTagContent content = (DynamicTagContent) getBean("DynamicTagContent");

		dynTag.setMailingID(mailing.getId());
		dynTag.setCompanyID(mailing.getCompanyID());

		content.setCompanyID(mailing.getCompanyID());
		content.setDynContent(aForm.getNewContent());
		content.setTargetID(aForm.getTargetID());
		content.setDynNameID(dynTag.getId());
		content.setMailingID(dynTag.getMailingID());
		content.setDynOrder(dynTag.getMaxOrder()+1);
		dynTag.addContent(content);
		aForm.setTargetID(0);
		aForm.setNewContent("");
		return mapping.findForward("add");

	}

	/**
	 * adds the content from the active textmodule and gets the next dynName
	 */
	public ActionForward textmodule_save(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		if (!this.checkLogon(req)) {
			return mapping.findForward("logon");
		}
		return mapping.findForward("next");
	}

	/**
	 * gets first link for next page
	 */
	public ActionForward links(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!this.checkLogon(req)) {
			return mapping.findForward("logon");
		}

		MailingWizardForm aForm = (MailingWizardForm) form;
		aForm.clearAktTracklink();

		return mapping.findForward("next");
	}

	/**
	 * saves link description and gets next link
	 */
	public ActionForward link(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!this.checkLogon(req)) {
			return mapping.findForward("logon");
		}

		MailingWizardForm aForm = (MailingWizardForm) form;

		if(aForm.nextTracklink()) {
			return mapping.findForward("next");
		}
		return mapping.findForward("skip");
	}

	/**
	 * 
	 */
	public ActionForward attachment(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!this.checkLogon(req)) {
			return mapping.findForward("logon");
		}

		MailingWizardForm aForm = (MailingWizardForm) form;
		FormFile newAttachment=aForm.getNewAttachment();

		try	{
			if(newAttachment.getFileSize()!=0) {
				MailingComponent comp=(MailingComponent) getBean("MailingComponent");

				comp.setCompanyID(this.getCompanyID(req));
				comp.setMailingID(aForm.getMailing().getId());
				if(aForm.getNewAttachmentType() == 0) {
					comp.setType(MailingComponent.TYPE_ATTACHMENT);
				} else {
					comp.setType(MailingComponent.TYPE_PERSONALIZED_ATTACHMENT);
				}
				comp.setComponentName(aForm.getNewAttachmentName());
				comp.setBinaryBlock(newAttachment.getFileData());
				comp.setEmmBlock(comp.makeEMMBlock());
				comp.setMimeType(newAttachment.getContentType());
				comp.setTargetID(aForm.getAttachmentTargetID());
				aForm.getMailing().addComponent(comp);
			}

		} catch (Exception e) {
			AgnUtils.logger().error("saveAttachment: "+e);
		}

		return mapping.findForward("next");
	}

	/**
	 * Finish mailing.
	 */
	public ActionForward finish(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		if (!this.checkLogon(req)) {
			return mapping.findForward("logon");
		}

		MailingWizardForm aForm = (MailingWizardForm) form;
		MailingDao mDao = (MailingDao) getBean("MailingDao");

		mDao.saveMailing(aForm.getMailing());

		return mapping.findForward("finish");
	}

	/**
	 * Forwarding when previous is clicked
	 */
	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!this.checkLogon(req)) {
			return mapping.findForward("logon");
		}

		return mapping.findForward("previous");
	}

	/**
	 * Forwarding when skip is clicked
	 */
	public ActionForward skip(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!this.checkLogon(req)) {
			return mapping.findForward("logon");
		}

		return mapping.findForward("skip");
	}
}
