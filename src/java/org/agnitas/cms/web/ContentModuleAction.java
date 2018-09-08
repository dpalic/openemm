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

import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil;
import org.agnitas.beans.impl.PaginatedListImpl;
import org.agnitas.cms.dao.CmsMailingDao;
import org.agnitas.cms.utils.ClassicTemplateGenerator;
import org.agnitas.cms.utils.CmsUtils;
import org.agnitas.cms.utils.TagUtils;
import org.agnitas.cms.utils.dataaccess.CMTemplateManager;
import org.agnitas.cms.utils.dataaccess.ContentModuleManager;
import org.agnitas.cms.utils.dataaccess.ContentModuleTypeManager;
import org.agnitas.cms.utils.dataaccess.MediaFileManager;
import org.agnitas.cms.utils.preview.PreviewImageGenerator;
import org.agnitas.cms.web.forms.ContentModuleForm;
import org.agnitas.cms.webservices.generated.CMTemplate;
import org.agnitas.cms.webservices.generated.CmsTag;
import org.agnitas.cms.webservices.generated.ContentModule;
import org.agnitas.cms.webservices.generated.ContentModuleLocation;
import org.agnitas.cms.webservices.generated.ContentModuleType;
import org.agnitas.cms.webservices.generated.MediaFile;
import org.agnitas.dao.MailingDao;
import org.agnitas.emm.core.commons.util.ConfigService;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.StrutsActionBase;
import org.agnitas.web.forms.StrutsFormBase;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;
import org.displaytag.pagination.PaginatedList;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Action for managing Content Modules
 * 
 * @author Vyacheslav Stepanov
 */

public class ContentModuleAction extends StrutsActionBase {
	/** The logger. */
	private static final transient Logger logger = Logger.getLogger(ContentModuleAction.class);

	public static final int ACTION_PURE_PREVIEW = ACTION_LAST + 1;
	public static final int ACTION_COPY = ACTION_LAST + 2;
	public static final int ACTION_NEW = ACTION_LAST + 3;
	public static final int ACTION_ASSIGN_LIST = ACTION_LAST + 4;
	public static final int ACTION_STORE_ASSIGNMENT = ACTION_LAST + 5;

	public static final int LIST_PREVIEW_WIDTH = 500;
	public static final int LIST_PREVIEW_HEIGHT = 400;
	public static final int PREVIEW_MAX_WIDTH = 150;
	public static final int PREVIEW_MAX_HEIGHT = 150;

	protected ConfigService configService;
	protected MediaFileManager mediaFileManager;
	private CMTemplateManager cmTemplateManager;
	private ContentModuleTypeManager contentModuleTypeManager;
	private ContentModuleManager contentModuleManager;
	private MailingDao mailingDao;
	private CmsMailingDao cmsMailingDao;
	private ClassicTemplateGenerator classicTemplateGenerator;

	@Required
	public void setConfigService(ConfigService configService) {
		this.configService = configService;
	}

	public void setMediaFileManager(MediaFileManager mediaFileManager) {
		this.mediaFileManager = mediaFileManager;
	}

	public void setCmTemplateManager(CMTemplateManager cmTemplateManager) {
		this.cmTemplateManager = cmTemplateManager;
	}

	public void setContentModuleTypeManager(ContentModuleTypeManager contentModuleTypeManager) {
		this.contentModuleTypeManager = contentModuleTypeManager;
	}

	public void setContentModuleManager(ContentModuleManager contentModuleManager) {
		this.contentModuleManager = contentModuleManager;
	}

	public void setMailingDao(MailingDao mailingDao) {
		this.mailingDao = mailingDao;
	}

	public void setCmsMailingDao(CmsMailingDao cmsMailingDao) {
		this.cmsMailingDao = cmsMailingDao;
	}

	public void setClassicTemplateGenerator(ClassicTemplateGenerator classicTemplateGenerator) {
		this.classicTemplateGenerator = classicTemplateGenerator;
	}

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		ActionMessages errors = new ActionMessages();
		ActionMessages messages = new ActionMessages();

		ActionForward destination = null;

		if (!AgnUtils.isUserLoggedIn(request)) {
			return mapping.findForward("logon");
		}

		ContentModuleForm aForm = (ContentModuleForm) form;
		if (form == null) {
			aForm = new ContentModuleForm();
		}

		if (logger.isInfoEnabled())
			logger.info("Action: " + aForm.getAction());

		// if assign button is pressed - store mailings assignment
		if (AgnUtils.parameterNotEmpty(request, "assign")) {
			aForm.setAction(ContentModuleAction.ACTION_STORE_ASSIGNMENT);
		}

		try {
			switch (aForm.getAction()) {
			case ContentModuleAction.ACTION_LIST:
				initializeColumnWidthsListIfNeeded(aForm);
				destination = mapping.findForward("list");
				aForm.reset(mapping, request);
				aForm.setMailingId(0);
				loadCMTList(aForm, request);
				aForm.setAction(ContentModuleAction.ACTION_LIST);
				break;

			case ContentModuleAction.ACTION_VIEW:
				loadContentModule(aForm, request);
				aForm.setAction(ContentModuleAction.ACTION_SAVE);
				destination = mapping.findForward("view");
				break;

			case ContentModuleAction.ACTION_NEW:
				aForm.clearData();
				loadCMTList(aForm, request);
				aForm.setAction(ContentModuleAction.ACTION_VIEW);
				destination = mapping.findForward("new");
				break;

			case ContentModuleAction.ACTION_COPY:
				copyContentModule(aForm, request);
				aForm.setAction(ContentModuleAction.ACTION_SAVE);
				destination = mapping.findForward("view");

				break;

			case ContentModuleAction.ACTION_SAVE:
				boolean saveOk = saveContentModule(aForm, request);
				// if save is successful - stay on view page
				// if not - got to list page
				if (saveOk) {
					aForm.setAction(ContentModuleAction.ACTION_SAVE);
					loadContentModule(aForm, request);
					if (aForm.getMailingId() == 0) {
						destination = mapping.findForward("view");
						messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
					} else {
						destination = new ActionForward(mapping.findForward("mailing_content").getPath() + "&mailingID=" + aForm.getMailingId(), true);
					}
				} else {
					destination = mapping.findForward("list");
					aForm.setAction(ContentModuleAction.ACTION_LIST);
				}
				break;

			case ContentModuleAction.ACTION_ASSIGN_LIST:
				initializeColumnWidthsListIfNeeded(aForm);
				destination = mapping.findForward("assign_list");
				aForm.reset(mapping, request);
				aForm.setAction(ContentModuleAction.ACTION_ASSIGN_LIST);
				break;

			case ContentModuleAction.ACTION_STORE_ASSIGNMENT:
				initializeColumnWidthsListIfNeeded(aForm);
				storeMailingAssignment(request, aForm);
				destination = mapping.findForward("assign_list");
				aForm.reset(mapping, request);
				aForm.setAction(ContentModuleAction.ACTION_ASSIGN_LIST);

				messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
				break;

			case ContentModuleAction.ACTION_PURE_PREVIEW:
				destination = mapping.findForward("pure_preview");
				aForm.reset(mapping, request);
				aForm.setPreview(getContentModulePreview(request, aForm));
				aForm.setAction(ContentModuleAction.ACTION_SAVE);
				break;

			case ContentModuleAction.ACTION_CONFIRM_DELETE:
				loadContentModule(aForm, request);
				aForm.setAction(ContentModuleAction.ACTION_DELETE);
				destination = mapping.findForward("delete");
				break;

			case ContentModuleAction.ACTION_DELETE:
				if (AgnUtils.parameterNotEmpty(request, "kill")) {
					deleteContentModule(request, aForm.getContentModuleId());

					messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
				}
				aForm.setAction(ContentModuleAction.ACTION_LIST);
				destination = mapping.findForward("list");
				break;
			}
		} catch (Exception e) {
			logger.error("Error while executing action with CM Template: " + e, e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception", configService.getValue(ConfigService.Value.SupportEmergencyUrl)));
		}

		// collect list of CMs for list-page
		if (destination != null && "list".equals(destination.getName())) {
			try {
                if(!aForm.isNumberOfRowsChanged()) {
                    setNumberOfRows(request, aForm);
                }
                aForm.setNumberOfRowsChanged(false);
				aForm.setAllCategories(contentModuleManager.getAllCMCategories(AgnUtils.getCompanyID(request)));
				if (aForm.getCategoryToShow() > 0) {
					request.setAttribute("contentModuleList", contentModuleManager.getContentModulesForCategory(AgnUtils.getCompanyID(request), aForm.getCategoryToShow()));
				} else {
					request.setAttribute("contentModuleList", getContentModuleList(request));
				}
			} catch (Exception e) {
				logger.error("getContentModuleList: " + e, e);
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception", configService.getValue(ConfigService.Value.SupportEmergencyUrl)));
			}
		}

		// collect list of Mailings for assign-page
		if (destination != null && "assign_list".equals(destination.getName())) {
			try {
				setNumberOfRows(request, (StrutsFormBase) form);
				request.setAttribute("mailingsList", getMailingsList(request, aForm));
			} catch (Exception e) {
				logger.error("getMailingsList: " + e, e);
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

	protected void initializeColumnWidthsListIfNeeded(ContentModuleForm aForm) {
		if (aForm.getColumnwidthsList() == null) {
			aForm.setColumnwidthsList(getInitializedColumnWidthList(3));
		}
	}

	private void storeMailingAssignment(HttpServletRequest request, ContentModuleForm aForm) {
		List<Integer> assignedMailings = new ArrayList<Integer>();
		@SuppressWarnings("rawtypes")
		Enumeration parameterNames = request.getParameterNames();
		while (parameterNames.hasMoreElements()) {
			String paramName = (String) parameterNames.nextElement();
			if (paramName.startsWith("assign_mailing_")) {
				String value = request.getParameter(paramName);
				if (value != null) {
					if (value.startsWith("mailing_")) {
						value = value.substring("mailing_".length());
						assignedMailings.add(Integer.parseInt(value));
					}
				}
			}
		}
		List<Integer> oldAssignment = aForm.getOldAssignment();
		List<Integer> mailingsToAssign = new ArrayList<Integer>();
		List<Integer> mailingsToDeassign = new ArrayList<Integer>();
		for (Integer mailingId : oldAssignment) {
			if (!assignedMailings.contains(mailingId)) {
				mailingsToDeassign.add(mailingId);
			}
		}
		for (Integer assignedMailingId : assignedMailings) {
			if (!oldAssignment.contains(assignedMailingId)) {
				mailingsToAssign.add(assignedMailingId);
			}
		}

		final int contentModuleId = aForm.getContentModuleId();
		contentModuleManager.addMailingBindings(contentModuleId, mailingsToAssign);
		for (Integer mailingId : mailingsToAssign) {
			classicTemplateGenerator.generate(mailingId, request, false);
		}

		contentModuleManager.removeMailingBindings(contentModuleId, mailingsToDeassign);
		contentModuleManager.removeCMLocationForMailingsByContentModule(contentModuleId, mailingsToDeassign);
		for (Integer mailingId : mailingsToDeassign) {
			classicTemplateGenerator.generate(mailingId, request, false);
		}
	}

	private void copyContentModule(ContentModuleForm aForm, HttpServletRequest request) {
		loadContentModule(aForm, request);
		Locale locale = AgnUtils.getLocale(request);
		ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
		aForm.setNameNoConvertion(bundle.getString("mailing.CopyOf") + " " + aForm.getName());
		aForm.setSourceCMId(aForm.getContentModuleId());
		aForm.setContentModuleId(0);
	}

	private void loadCMTList(ContentModuleForm aForm, HttpServletRequest request) {
		List<ContentModuleType> moduleTypes = contentModuleTypeManager.getContentModuleTypes(AgnUtils.getCompanyID(request), true);
		aForm.setAllCMT(moduleTypes);
	}

	private boolean saveContentModule(ContentModuleForm aForm, HttpServletRequest request) {
		boolean success = true;
		ContentModule contentModule = new ContentModule();
		contentModule.setId(aForm.getContentModuleId());
		contentModule.setCompanyId(AgnUtils.getCompanyID(request));
		contentModule.setName(aForm.getName());
		contentModule.setDescription(aForm.getDescription());
		contentModule.setCategoryId(aForm.getCategory());
		// save existing CM
		if (aForm.getContentModuleId() > 0) {
			success = contentModuleManager.updateContentModule(contentModule.getId(), contentModule.getName(), contentModule.getDescription(), contentModule.getCategoryId());
		} else {
			// create new CM
			contentModule.setContent(aForm.getContent());
			int contentModuleId = contentModuleManager.createContentModule(contentModule);
			contentModule.setId(contentModuleId);
			aForm.setContentModuleId(contentModuleId);
			aForm.setCmtId(0);
			// assign to mailing if we came from mailing-content-page
			if (aForm.isCreateForMailing() && !StringUtils.isEmpty(aForm.getPhName())) {
				assignCmToMailing(aForm);
			}
			this.writeUserActivityLog(AgnUtils.getAdmin(request), "create content module", aForm.getName());
		}
		// save placeholders contents
		saveContentModuleContents(aForm, request, contentModule);
		// if Content module was copied from another CM - we need to copy images
		// that were not specified for new CM from original CM (source CM)
		if (aForm.getSourceCMId() > 0) {
			copyUnspecifiedImages(aForm.getSourceCMId(), contentModule);
			aForm.setSourceCMId(0);
		}

		// try to generate thumbnail preview for CM
		final HttpSession session = request.getSession();
		final int maxWidth = PREVIEW_MAX_WIDTH;
		final int maxHeight = PREVIEW_MAX_HEIGHT;
		PreviewImageGenerator previewGenerator = new PreviewImageGenerator(AgnUtils.getSpringContextFromRequest(request), session, maxWidth, maxHeight);
		previewGenerator.generatePreview(0, aForm.getContentModuleId(), 0);

		// update classictemplate for assigned mailings
		final List<Integer> mailingIds = contentModuleManager.getMailingsByContentModule(contentModule.getId());
		for (Integer mailingId : mailingIds) {
			classicTemplateGenerator.generate(mailingId, request);
		}
		return success;
	}

	private void assignCmToMailing(ContentModuleForm aForm) {
		ArrayList<Integer> mailingIds = new ArrayList<Integer>();
		mailingIds.add(aForm.getMailingId());
		List<ContentModuleLocation> locations = contentModuleManager.getCMLocationsForMailingId(aForm.getMailingId());
		for (ContentModuleLocation location : locations) {
			if (aForm.getPhName().equals(location.getDynName())) {
				contentModuleManager.removeCMLocationForMailingsByContentModule(location.getContentModuleId(), mailingIds);
				contentModuleManager.removeMailingBindings(location.getContentModuleId(), mailingIds);
			}
		}
		CMTemplate template = cmTemplateManager.getCMTemplateForMailing(aForm.getMailingId());
		int templateId = template == null ? 0 : template.getId();
		ArrayList<ContentModuleLocation> newLocations = new ArrayList<ContentModuleLocation>();
		ContentModuleLocation newLocation = new ContentModuleLocation(templateId, aForm.getContentModuleId(), aForm.getPhName(), 0, aForm.getMailingId(), 1, 0);
		newLocations.add(newLocation);
		contentModuleManager.addCMLocations(newLocations);
		contentModuleManager.addMailingBindings(aForm.getContentModuleId(), mailingIds);
	}

	private void saveContentModuleContents(ContentModuleForm aForm, HttpServletRequest request, ContentModule contentModule) {
		final List<CmsTag> cmsTagList = aForm.getTags();
		for (CmsTag cmsTag : cmsTagList) {
			if (cmsTag.getType() == TagUtils.TAG_IMAGE) {
				cmsTag.setValue(CmsUtils.removeSystemUrlFromImageUrls(cmsTag.getValue()));
			}
			if (cmsTag instanceof CmsImageTag) {
				final CmsImageTag cmsImageTag = (CmsImageTag) cmsTag;
				if (cmsImageTag.isUpload()) {
					final FormFile formFile = cmsImageTag.getFormFile();
					if (formFile != null) {
						final String pictureUrl = storeUploadedPicture(contentModule.getId(), cmsImageTag, formFile, request);
						if (pictureUrl != null && pictureUrl.length() != 0) {
							cmsTag.setValue(pictureUrl);
						}
					}
				}
			}
		}
		contentModuleManager.saveContentModuleContentList(contentModule.getId(), cmsTagList);
	}

	private void copyUnspecifiedImages(int sourceCMId, ContentModule contentModule) {
		List<MediaFile> sourceCMImages = mediaFileManager.getMediaFilesForContentModule(sourceCMId);
		List<CmsTag> sourceContents = contentModuleManager.getContentModuleContents(sourceCMId);
		sourceContents = TagUtils.filterTagsByType(sourceContents, TagUtils.TAG_IMAGE);
		List<CmsTag> newContents = contentModuleManager.getContentModuleContents(contentModule.getId());
		newContents = TagUtils.filterTagsByType(newContents, TagUtils.TAG_IMAGE);
		if (sourceContents.size() > newContents.size()) {
			// List<CmsTag> newTagsToAdd = new ArrayList<CmsTag>();
			for (CmsTag sourceTag : sourceContents) {
				CmsTag newTag = TagUtils.findTag(newContents, sourceTag.getName(), sourceTag.getType());
				if (newTag == null) {
					MediaFile mediaFileForTag = TagUtils.getMediaFileForTag(sourceCMImages, sourceTag, sourceCMId);
					// copy uploaded image
					if (mediaFileForTag != null) {
						mediaFileForTag.setContentModuleId(contentModule.getId());
						MediaFile newMediaFile = mediaFileManager.createMediaFile(mediaFileForTag);
						String imageUrl = CmsUtils.generateMediaFileUrl(newMediaFile.getId());
						sourceTag.setValue(imageUrl);
					}
				}
			}
			contentModuleManager.saveContentModuleContentList(contentModule.getId(), sourceContents);
			// newTagsToAdd.add(sourceTag);
		}
	}

	private String storeUploadedPicture(int contentModuleId, CmsTag tag, FormFile file, HttpServletRequest req) {
		try {
			if (file.getFileName() == null || (file.getFileName().length() == 0) || file.getFileData() == null || file.getFileData().length == 0) {
				return null;
			}
			// get mime-type for file
			String mimeType = CmsUtils.UNKNOWN_MIME_TYPE;
			@SuppressWarnings("rawtypes")
			Collection mimeTypes = MimeUtil.getMimeTypes(file.getFileName());
			if (!mimeTypes.isEmpty()) {
				MimeType type = (MimeType) mimeTypes.iterator().next();
				mimeType = type.toString();
			}
			if (!mimeType.startsWith("image")) {
				return null;
			}
			// store media file
			MediaFile mediaFile = new MediaFile();
			mediaFile.setCompanyId(AgnUtils.getCompanyID(req));
			mediaFile.setContentModuleId(contentModuleId);
			mediaFile.setName(tag.getName());
			mediaFile.setMimeType(mimeType);
			mediaFile.setContent(file.getFileData());
			// remove current CM picture for that tag
			mediaFileManager.removeContentModuleImage(contentModuleId, tag.getName());
			// store new CM picture for CM tag
			mediaFile = mediaFileManager.createMediaFile(mediaFile);
			return CmsUtils.generateMediaFileUrl(mediaFile.getId());
		} catch (IOException e) {
			return null;
		}
	}

	private void loadContentModule(ContentModuleForm aForm, HttpServletRequest request) {
		int contentModuleId = aForm.getContentModuleId();
		String content = "";
		if (contentModuleId > 0) {
			ContentModule contentModule = contentModuleManager.getContentModule(contentModuleId);
			if (contentModule != null) {
				aForm.setNameNoConvertion(contentModule.getName());
				aForm.setDescriptionNoConvertion(contentModule.getDescription());
				aForm.setContent(contentModule.getContent());
				aForm.setCategory(contentModule.getCategoryId());
				content = contentModule.getContent();
			}
		} else {
			aForm.setNameNoConvertion("");
			aForm.setDescriptionNoConvertion("");
			aForm.setCategory(0);
			ContentModuleType moduleType = contentModuleTypeManager.getContentModuleType(aForm.getCmtId());
			if (moduleType != null) {
				aForm.setContent(moduleType.getContent());
				content = moduleType.getContent();
			}
		}
		List<CmsTag> tags = TagUtils.getCmsTags(content);
		if (contentModuleId > 0) {
			List<CmsTag> tagContents = contentModuleManager.getContentModuleContents(contentModuleId);
			for (CmsTag tag : tags) {
				String value = TagUtils.getValueForTag(tag, tagContents);
				if (tag.getType() == TagUtils.TAG_IMAGE) {
					value = CmsUtils.appendImageURLsWithSystemUrl(value);
				}
				tag.setValue(value);
			}
		} else {
			for (CmsTag tag : tags) {
				tag.setValue("");
			}
		}
		aForm.setTags(tags);
		aForm.setAllCategories(contentModuleManager.getAllCMCategories(AgnUtils.getCompanyID(request)));
		if (contentModuleId > 0)
			this.writeUserActivityLog(AgnUtils.getAdmin(request), "do load content module", aForm.getName());
	}

	protected void deleteContentModule(HttpServletRequest request, int contentModuleId) {
		if (contentModuleId != 0) {
			contentModuleManager.deleteContentModule(contentModuleId);
			contentModuleManager.removeContentsForContentModule(contentModuleId);
			mediaFileManager.removeMediaFilesForContentModuleId(contentModuleId);
			this.writeUserActivityLog(AgnUtils.getAdmin(request), "delete content module", Integer.toString(contentModuleId));
		}
	}

	private String getContentModulePreview(HttpServletRequest request, ContentModuleForm aForm) {
		if (aForm.getContentModuleId() > 0) {
			String cmContent = TagUtils.generateContentModuleContent(aForm.getContentModuleId(), false, AgnUtils.getSpringContextFromRequest(request));
			cmContent = CmsUtils.appendImageURLsWithSystemUrl(cmContent);
			return cmContent;
		} else if (aForm.getSourceCMId() > 0) {
			String cmContent = TagUtils.generateContentModuleContent(aForm.getSourceCMId(), false, AgnUtils.getSpringContextFromRequest(request));
			cmContent = CmsUtils.appendImageURLsWithSystemUrl(cmContent);
			return cmContent;
		} else {
			ContentModuleType moduleType = contentModuleTypeManager.getContentModuleType(aForm.getCmtId());
			if (moduleType != null) {
				return moduleType.getContent();
			} else {
				return "";
			}
		}
	}

	/**
	 * Gets list of content modules for overview-page table
	 */
	public List<ContentModule> getContentModuleList(HttpServletRequest request) throws IllegalAccessException, InstantiationException {
		return contentModuleManager.getContentModules(AgnUtils.getCompanyID(request));
	}

	/**
	 * Gets list of mailings for assign-page
	 */
	public PaginatedList getMailingsList(HttpServletRequest request, ContentModuleForm aForm) throws IllegalAccessException, InstantiationException {
		PaginatedListImpl<Map<String, Object>> mailingList = CMTemplateAction.getPageMailings(request, aForm, mailingDao);

		List<Integer> mailingIds = CMTemplateAction.getMailingIds(mailingList);

		List<Integer> mailingWithNoClassicTemplate = cmsMailingDao.getMailingsWithNoClassicTemplate(mailingIds, AgnUtils.getCompanyID(request));

		List<Integer> mailBinding = contentModuleManager.getMailingBinding(mailingIds, aForm.getContentModuleId());

		Map<Integer, Integer> mailCMTemplateBinding = cmTemplateManager.getMailingBinding(mailingIds);

		aForm.setOldAssignment(mailBinding);

		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

		for (Map<String, Object> mailingMap : mailingList.getList()) {
			int mailingId = ((Number) mailingMap.get("mailingid")).intValue();
			String shortname = String.valueOf(mailingMap.get("shortname"));

			Integer bindCMTemplate = mailCMTemplateBinding.get(mailingId);
			Map<String, Object> newBean = new HashMap<String, Object>();
			newBean.put("mailingid", mailingId);
			newBean.put("shortname", shortname);
			newBean.put("hasCMTemplate", bindCMTemplate != null);
			newBean.put("assigned", mailBinding.contains(mailingId));
			newBean.put("hasClassicTemplate", !mailingWithNoClassicTemplate.contains(mailingId));
			resultList.add(newBean);
		}
		mailingList.getList().clear();
		mailingList.getList().addAll(resultList);
		return mailingList;
	}
}