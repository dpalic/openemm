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

package org.agnitas.cms.web;

import org.agnitas.cms.utils.dataaccess.ContentModuleTypeManager;
import org.agnitas.cms.utils.dataaccess.MediaFileManager;
import org.agnitas.cms.utils.preview.PreviewImageGenerator;
import org.agnitas.cms.web.forms.ContentModuleTypeForm;
import org.agnitas.cms.webservices.generated.ContentModuleType;
import org.agnitas.cms.webservices.generated.MediaFile;
import org.agnitas.emm.core.commons.util.ConfigService;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.StrutsActionBase;
import org.agnitas.web.forms.StrutsFormBase;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Action for managing Content Module Types
 * 
 * @author Vyacheslav Stepanov
 */

public class ContentModuleTypeAction extends StrutsActionBase {
	/** The logger. */
	private static final transient Logger logger = Logger.getLogger(ContentModuleTypeAction.class);

	public static final int ACTION_PURE_PREVIEW = ACTION_LAST + 1;
	public static final int ACTION_COPY = ACTION_LAST + 2;
	public static final int ACTION_PREVIEW = ACTION_LAST + 3;
	public static final int ACTION_NEW = ACTION_LAST + 4;
	public static final int ACTION_NEW_PROCEED = ACTION_LAST + 5;

	public static final int LIST_PREVIEW_WIDTH = 500;
	public static final int LIST_PREVIEW_HEIGHT = 400;
	public static final int PREVIEW_MAX_WIDTH = 150;
	public static final int PREVIEW_MAX_HEIGHT = 150;

	protected ConfigService configService;
	protected MediaFileManager mediaFileManager;
	private ContentModuleTypeManager contentModuleTypeManager;

	@Required
	public void setConfigService(ConfigService configService) {
		this.configService = configService;
	}

	public void setMediaFileManager(MediaFileManager mediaFileManager) {
		this.mediaFileManager = mediaFileManager;
	}

	public void setContentModuleTypeManager(ContentModuleTypeManager contentModuleTypeManager) {
		this.contentModuleTypeManager = contentModuleTypeManager;
	}

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		ContentModuleTypeForm aForm;
		ActionMessages messages = new ActionMessages();
		ActionMessages errors = new ActionMessages();
		ActionForward destination = null;

		if (!AgnUtils.isUserLoggedIn(request)) {
			return mapping.findForward("logon");
		}

		if (form != null) {
			aForm = (ContentModuleTypeForm) form;
		} else {
			aForm = new ContentModuleTypeForm();
		}

		if (logger.isInfoEnabled())
			logger.info("Action: " + aForm.getAction());

		// if preview size is changed - return to preview page
		if (request.getParameter("changePreviewSize.x") != null) {
			aForm.setAction(ContentModuleTypeAction.ACTION_PREVIEW);
		}

		try {
			switch (aForm.getAction()) {
			case ContentModuleTypeAction.ACTION_LIST:
				if (aForm.getColumnwidthsList() == null) {
					aForm.setColumnwidthsList(getInitializedColumnWidthList(3));
				}
				destination = mapping.findForward("list");
				aForm.reset(mapping, request);
				loadCMTList(aForm, request);
				aForm.setAction(ContentModuleTypeAction.ACTION_LIST);
				break;

			case ContentModuleTypeAction.ACTION_VIEW:
				loadCMT(aForm, request);
				aForm.setAction(ContentModuleTypeAction.ACTION_SAVE);
				destination = mapping.findForward("view");
				break;

			case ContentModuleTypeAction.ACTION_NEW:
				loadCMTList(aForm, request);
				aForm.setAction(ContentModuleTypeAction.ACTION_NEW_PROCEED);
				destination = mapping.findForward("new");
				break;

			case ContentModuleTypeAction.ACTION_NEW_PROCEED:
				loadDataNewCMT(aForm, request);
				aForm.setAction(ContentModuleTypeAction.ACTION_SAVE);
				destination = mapping.findForward("view");
				break;

			case ContentModuleTypeAction.ACTION_COPY:
				copyCMT(aForm, request);
				aForm.setAction(ContentModuleTypeAction.ACTION_SAVE);
				destination = mapping.findForward("view");
				break;

			case ContentModuleTypeAction.ACTION_SAVE:
				boolean saveOk = saveCMT(aForm, request);
				// if save is successful - stay on view page
				// if not - got to list page
				if (saveOk) {
					aForm.setAction(ContentModuleTypeAction.ACTION_SAVE);
					destination = mapping.findForward("view");

					messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
				} else {
					destination = mapping.findForward("list");
					aForm.setAction(ContentModuleTypeAction.ACTION_LIST);
				}
				break;

			case ContentModuleTypeAction.ACTION_PURE_PREVIEW:
				destination = mapping.findForward("pure_preview");
				aForm.reset(mapping, request);
				aForm.setPreview(getCMTPreview(request, aForm.getCmtId()));
				aForm.setAction(ContentModuleTypeAction.ACTION_PURE_PREVIEW);
				break;

			case ContentModuleTypeAction.ACTION_PREVIEW:
				destination = mapping.findForward("preview");
				aForm.reset(mapping, request);
				aForm.setAction(ContentModuleTypeAction.ACTION_PREVIEW);
				break;

			case ContentModuleTypeAction.ACTION_CONFIRM_DELETE:
				loadCMT(aForm, request);
				aForm.setAction(ContentModuleTypeAction.ACTION_DELETE);
				destination = mapping.findForward("delete");
				break;

			case ContentModuleTypeAction.ACTION_DELETE:
				if (AgnUtils.parameterNotEmpty(request, "kill")) {
					deleteCMT(request, aForm.getCmtId());

					messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
				}
				aForm.setAction(ContentModuleTypeAction.ACTION_LIST);
				destination = mapping.findForward("list");
				break;
			}
		} catch (Exception e) {
			logger.error("Error while executing action with CM Template: " + e, e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception", configService.getValue(ConfigService.Value.SupportEmergencyUrl)));
		}

		// collect list of CMTs for list-page
		if (destination != null && "list".equals(destination.getName())) {
			try {
                if(!aForm.isNumberOfRowsChanged()) {
                    setNumberOfRows(request, (StrutsFormBase) form);
                }
                aForm.setNumberOfRowsChanged(false);
				request.setAttribute("cmtList", getCMTList(request));
			} catch (Exception e) {
				logger.error("getCMTList: " + e, e);
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception", configService.getValue(ConfigService.Value.SupportEmergencyUrl)));
			}
		}

		// Report any errors we have discovered back to the original form
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
		}

		if (!messages.isEmpty()) {
			saveMessages(request, messages);
		}

		// we need some destination to show error messages
		if (destination == null && !errors.isEmpty()) {
			destination = mapping.findForward("list");
		}

		return destination;
	}

	private void loadDataNewCMT(ContentModuleTypeForm aForm, HttpServletRequest request) {
		ContentModuleType moduleType = contentModuleTypeManager.getContentModuleType(aForm.getCmtId());
		if (moduleType == null) {
			aForm.setContent("");
		} else {
			aForm.setContent(moduleType.getContent());
		}
		aForm.setName("");
		aForm.setDescription("");
		aForm.setCmtId(0);
		aForm.setReadOnly(false);
	}

	private void loadCMTList(ContentModuleTypeForm aForm, HttpServletRequest request) {
		List<ContentModuleType> moduleTypes = contentModuleTypeManager.getContentModuleTypes(AgnUtils.getCompanyID(request), true);
		aForm.setAllCMT(moduleTypes);
	}

	private boolean saveCMT(ContentModuleTypeForm aForm, HttpServletRequest request) {
		boolean success = true;
		ContentModuleType moduleType = new ContentModuleType();
		moduleType.setId(aForm.getCmtId());
		moduleType.setCompanyId(AgnUtils.getCompanyID(request));
		moduleType.setName(aForm.getName());
		moduleType.setDescription(aForm.getDescription());
		moduleType.setContent(aForm.getContent());
		moduleType.setIsPublic(false);
		moduleType.setReadOnly(false);
		// save existing CMT
		if (aForm.getCmtId() > 0) {
			success = contentModuleTypeManager.updateContentModuleType(moduleType);
		} else {
			// create new CMT
			int newId = contentModuleTypeManager.createContentModuleType(moduleType);
			aForm.setCmtId(newId);
			this.writeUserActivityLog(AgnUtils.getAdmin(request), "create content module type", aForm.getName());
		}

		generateThumbnailPreview(aForm, request);
		return success;
	}

	private void generateThumbnailPreview(ContentModuleTypeForm aForm, HttpServletRequest request) {
		final HttpSession session = request.getSession();
		final int maxWidth = PREVIEW_MAX_WIDTH;
		final int maxHeight = PREVIEW_MAX_HEIGHT;
		final PreviewImageGenerator previewImageGenerator = new PreviewImageGenerator(getWebApplicationContext(request), session, maxWidth, maxHeight);
		previewImageGenerator.generatePreview(0, 0, aForm.getCmtId());
	}

	private void loadCMT(ContentModuleTypeForm aForm, HttpServletRequest request) {
		ContentModuleType moduleType = contentModuleTypeManager.getContentModuleType(aForm.getCmtId());
		if (moduleType != null) {
			aForm.setName(moduleType.getName());
			aForm.setDescription(moduleType.getDescription());
			aForm.setContent(moduleType.getContent());
			aForm.setReadOnly(moduleType.isReadOnly());
			// if it's read-only CMT - we need to generate preview thumbnail for
			// it when it's open for the first time, because this CMT has no "Save" button
			// (usually CMT preview thumbnail is generated when "Save" is clicked)
			if (moduleType.isReadOnly()) {
				MediaFile preview = mediaFileManager.getPreviewOfContentModuleType(moduleType.getId());
				if (preview == null) {
					generateThumbnailPreview(aForm, request);
				}
			}
			this.writeUserActivityLog(AgnUtils.getAdmin(request), "do load content module type", aForm.getName());
		}
	}

	private void copyCMT(ContentModuleTypeForm aForm, HttpServletRequest request) {
		ContentModuleType moduleType = contentModuleTypeManager.getContentModuleType(aForm.getCmtId());
		if (moduleType != null) {
			Locale locale = AgnUtils.getLocale(request);
			ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
			aForm.setName(bundle.getString("mailing.CopyOf") + " " + moduleType.getName());
			aForm.setDescription(moduleType.getDescription());
			aForm.setContent(moduleType.getContent());
			aForm.setCmtId(0);
			aForm.setReadOnly(false);
		}
	}

	protected void deleteCMT(HttpServletRequest request, int cmtId) {
		if (cmtId != 0) {
			contentModuleTypeManager.deleteContentModuleType(cmtId);
			this.writeUserActivityLog(AgnUtils.getAdmin(request), "delete content module type", Integer.toString(cmtId));
		}
	}

	private String getCMTPreview(HttpServletRequest request, int cmtId) {
		ContentModuleType moduleType = contentModuleTypeManager.getContentModuleType(cmtId);
		if (moduleType != null) {
			return moduleType.getContent();
		}
		return "";
	}

	/**
	 * Gets list of CM Templates for overview-page table
	 */
	public List<ContentModuleType> getCMTList(HttpServletRequest request) throws IllegalAccessException, InstantiationException {
		return contentModuleTypeManager.getContentModuleTypes(AgnUtils.getCompanyID(request), true);
	}

	private WebApplicationContext getWebApplicationContext(HttpServletRequest request) {
		return WebApplicationContextUtils.getRequiredWebApplicationContext(request.getSession().getServletContext());
	}
}