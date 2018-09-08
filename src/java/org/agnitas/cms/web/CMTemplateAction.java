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
import org.agnitas.cms.utils.dataaccess.CMTemplateManager;
import org.agnitas.cms.utils.dataaccess.MediaFileManager;
import org.agnitas.cms.utils.preview.PreviewImageGenerator;
import org.agnitas.cms.web.forms.CMTemplateForm;
import org.agnitas.cms.webservices.generated.CMTemplate;
import org.agnitas.cms.webservices.generated.MediaFile;
import org.agnitas.dao.MailingDao;
import org.agnitas.emm.core.commons.util.ConfigService;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.StrutsActionBase;
import org.agnitas.web.forms.StrutsFormBase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Action for managing CM templates
 * 
 * @author Vyacheslav Stepanov
 */
public class CMTemplateAction extends StrutsActionBase {
	
	/** The logger. */
	private static final transient Logger logger = Logger.getLogger(CMTemplateAction.class);

	public static final int ACTION_PURE_PREVIEW = ACTION_LAST + 1;
	public static final int ACTION_UPLOAD = ACTION_LAST + 2;
	public static final int ACTION_STORE_UPLOADED = ACTION_LAST + 3;
	public static final int ACTION_ASSIGN_LIST = ACTION_LAST + 4;
	public static final int ACTION_STORE_ASSIGNMENT = ACTION_LAST + 5;
	public static final int ACTION_EDIT_TEMPLATE = ACTION_LAST + 6;
	public static final int ACTION_DELETE_IMAGE_TEMPLATE = ACTION_LAST + 7;
	public static final int ACTION_SAVE_TEMPLATE = ACTION_LAST + 8;

	// @todo will be moved to some other place
	public static final String MEDIA_FOLDER = "template-media";
	public static final String THUMBS_DB = "thumbs.db";

	public static final int LIST_PREVIEW_WIDTH = 500;
	public static final int LIST_PREVIEW_HEIGHT = 400;
	public static final int PREVIEW_MAX_WIDTH = 150;
	public static final int PREVIEW_MAX_HEIGHT = 150;

	protected MailingDao mailingDao;
	protected ConfigService configService;
	protected CMTemplateManager cmTemplateManager;
	protected ClassicTemplateGenerator classicTemplateGenerator;
	protected CmsMailingDao cmsMailingDao;
	protected MediaFileManager mediaFileManager;
	
	public void setMailingDao(MailingDao mailingDao) {
		this.mailingDao = mailingDao;
	}

	@Required
	public void setConfigService(ConfigService configService) {
		this.configService = configService;
	}

	public void setCmTemplateManager(CMTemplateManager cmTemplateManager) {
		this.cmTemplateManager = cmTemplateManager;
	}

	public void setClassicTemplateGenerator(ClassicTemplateGenerator classicTemplateGenerator) {
		this.classicTemplateGenerator = classicTemplateGenerator;
	}

	public void setCmsMailingDao(CmsMailingDao cmsMailingDao) {
		this.cmsMailingDao = cmsMailingDao;
	}

	public void setMediaFileManager(MediaFileManager mediaFileManager) {
		this.mediaFileManager = mediaFileManager;
	}

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		CMTemplateForm aForm;

		ActionMessages errors = new ActionMessages();
		ActionMessages messages = new ActionMessages();
		ActionForward destination = null;

		if (!AgnUtils.isUserLoggedIn(request)) {
			return mapping.findForward("logon");
		}

		if (form != null) {
			aForm = (CMTemplateForm) form;
		} else {
			aForm = new CMTemplateForm();
		}

		if (logger.isInfoEnabled())
			logger.info("Action: " + aForm.getAction());

		// if preview size is changed - return to view page
		if (AgnUtils.parameterNotEmpty(request, "changePreviewSize")) {
			aForm.setAction(CMTemplateAction.ACTION_VIEW);
		}

		// if assign button is pressed - store mailings assignment
		if (AgnUtils.parameterNotEmpty(request, "assign")) {
			aForm.setAction(CMTemplateAction.ACTION_STORE_ASSIGNMENT);
		}

		try {
			switch (aForm.getAction()) {
			case CMTemplateAction.ACTION_LIST:
				initializeColumnWidthsListIfNeeded(aForm);
				destination = mapping.findForward("list");
				aForm.reset(mapping, request);
				setAvailableCharsets(aForm, request);
				aForm.setAction(CMTemplateAction.ACTION_LIST);
				break;

			case CMTemplateAction.ACTION_ASSIGN_LIST:
				initializeColumnWidthsListIfNeeded(aForm);
				destination = mapping.findForward("assign_list");
				aForm.reset(mapping, request);
				aForm.setAction(CMTemplateAction.ACTION_ASSIGN_LIST);
				break;

			case CMTemplateAction.ACTION_STORE_ASSIGNMENT:
				initializeColumnWidthsListIfNeeded(aForm);
				storeMailingAssignment(request, aForm);
				destination = mapping.findForward("assign_list");
				aForm.reset(mapping, request);
				aForm.setAction(CMTemplateAction.ACTION_ASSIGN_LIST);

				messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
				break;

			case CMTemplateAction.ACTION_VIEW:
				loadCMTemplate(request, aForm);
				aForm.setAction(CMTemplateAction.ACTION_SAVE);
				destination = mapping.findForward("view");
				break;

			case CMTemplateAction.ACTION_UPLOAD:
				aForm.setAction(CMTemplateAction.ACTION_STORE_UPLOADED);
				setAvailableCharsets(aForm, request);
				destination = mapping.findForward("upload");
				break;

			case CMTemplateAction.ACTION_STORE_UPLOADED:
				errors = storeUploadedTemplate(aForm, request);
				// if template is uploaded and stored successfuly - go to
				// template edit page, otherwise - stay on upload page to display
				// errors and allow user to repeat his try to upload template
				if (errors.isEmpty()) {
					this.writeUserActivityLog(AgnUtils.getAdmin(request), "create CM template", aForm.getName());
					loadCMTemplate(request, aForm);
					aForm.setAction(CMTemplateAction.ACTION_SAVE);
					destination = mapping.findForward("view");

					messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
				} else {
					aForm.setAction(CMTemplateAction.ACTION_STORE_UPLOADED);
					destination = mapping.findForward("list");
				}
				break;

			case CMTemplateAction.ACTION_SAVE:
				boolean saveOk = saveCMTemplate(aForm);
				// if save is successful - stay on view page
				// if not - got to list page
				if (saveOk) {
					aForm.setAction(CMTemplateAction.ACTION_SAVE);
					destination = mapping.findForward("view");

					messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
				} else {
					destination = mapping.findForward("list");
					aForm.setAction(CMTemplateAction.ACTION_LIST);
				}
				break;

			case CMTemplateAction.ACTION_PURE_PREVIEW:
				destination = mapping.findForward("pure_preview");
				aForm.reset(mapping, request);
				aForm.setPreview(getCmTemplatePreview(aForm.getCmTemplateId()));
				aForm.setAction(CMTemplateAction.ACTION_PURE_PREVIEW);
				break;

			case CMTemplateAction.ACTION_CONFIRM_DELETE:
				loadCMTemplate(request, aForm);
				aForm.setAction(CMTemplateAction.ACTION_DELETE);
				destination = mapping.findForward("delete");
				break;

			case CMTemplateAction.ACTION_DELETE:
				if (AgnUtils.parameterNotEmpty(request, "kill")) {
					deleteCMTemplate(aForm.getCmTemplateId(), request);
				}
				aForm.setAction(CMTemplateAction.ACTION_LIST);
				destination = mapping.findForward("list");

				messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
				break;

			case CMTemplateAction.ACTION_EDIT_TEMPLATE:
				loadCMTemplate(request, aForm);
				aForm.setLMediaFile(mediaFileManager.getMediaFilesForContentModuleTemplate(aForm.getCmTemplateId()));
				aForm.setContentTemplateNoConvertion(new String(cmTemplateManager.getCMTemplate(aForm.getCmTemplateId()).getContent(), Charset.forName("UTF-8")));
				aForm.setAction(CMTemplateAction.ACTION_EDIT_TEMPLATE);
				destination = mapping.findForward("edit_template");
				break;
			case CMTemplateAction.ACTION_DELETE_IMAGE_TEMPLATE:
				this.deleteImage(request);
				aForm.setLMediaFile(mediaFileManager.getMediaFilesForContentModuleTemplate(aForm.getCmTemplateId()));
				loadCMTemplate(request, aForm);
				aForm.setAction(CMTemplateAction.ACTION_EDIT_TEMPLATE);
				destination = mapping.findForward("edit_template");
				break;
			case CMTemplateAction.ACTION_SAVE_TEMPLATE:
				if (aForm.getErrorFieldMap().isEmpty()) {
					this.saveEditTemplate(request, aForm);
					aForm.setLMediaFile(mediaFileManager.getMediaFilesForContentModuleTemplate(aForm.getCmTemplateId()));
					aForm.setAction(CMTemplateAction.ACTION_EDIT_TEMPLATE);
					destination = mapping.findForward("edit_template");
					if (errors.isEmpty()) {
						messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
						request.removeAttribute("save_ok");
					}
					break;
				} else {
					aForm.setAction(CMTemplateAction.ACTION_EDIT_TEMPLATE);
					destination = mapping.findForward("edit_template");
					for (String key : aForm.getErrorFieldMap().keySet()) {
						errors.add(key, new ActionMessage(aForm.getErrorFieldMap().get(key)));
					}
					request.setAttribute("save_ok", "false");
				}
			}
		} catch (Exception e) {
			logger.error("Error while executing action with CM Template: " + e, e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception", configService.getValue(ConfigService.Value.SupportEmergencyUrl)));
		}

		// collect list of CM Templates for list-page
		if (destination != null && "list".equals(destination.getName())) {
			try {
				if (((StrutsFormBase) form).getNumberOfRows() < 1) {
					setNumberOfRows(request, (StrutsFormBase) form);
				}
				request.setAttribute("cmTemplateList", getCMTemplateList(request));
			} catch (Exception e) {
				logger.error("cmTemplateList: " + e, e);
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

		return destination;
	}

	protected void initializeColumnWidthsListIfNeeded(CMTemplateForm aForm) {
		if (aForm.getColumnwidthsList() == null || aForm.getColumnwidthsList().size() == 0) {
			aForm.setColumnwidthsList(getInitializedColumnWidthList(3));
		}
	}

	private void setAvailableCharsets(CMTemplateForm aForm, HttpServletRequest request) {
		List<String> charsets = new ArrayList<String>();
		for (String charset : CMTemplateForm.CHARTERSET_LIST) {
			if (allowed("charset.use." + charset.replaceAll("-", "_"), request)) {
				charsets.add(charset);
			}
		}
		aForm.setAvailableCharsets(charsets);
	}

	private void storeMailingAssignment(HttpServletRequest request, CMTemplateForm aForm) {
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
		List<Integer> mailingsToAssign = new ArrayList<Integer>();
		List<Integer> mailingsToDeassign = new ArrayList<Integer>();
		Map<Integer, Integer> oldAssignment = aForm.getOldAssignment();
		for (Integer mailingId : oldAssignment.keySet()) {
			if (!assignedMailings.contains(mailingId) && oldAssignment.get(mailingId) == aForm.getCmTemplateId()) {
				mailingsToDeassign.add(mailingId);
			}
		}
		for (Integer assignedMailingId : assignedMailings) {
			if (oldAssignment.get(assignedMailingId) == null) {
				mailingsToAssign.add(assignedMailingId);
			} else if (oldAssignment.get(assignedMailingId) != aForm.getCmTemplateId()) {
				mailingsToDeassign.add(assignedMailingId);
				mailingsToAssign.add(assignedMailingId);
			}
		}

		cmTemplateManager.removeMailingBindings(mailingsToDeassign);
		cmTemplateManager.addMailingBindings(aForm.getCmTemplateId(), mailingsToAssign);

		for (Integer mailingId : mailingsToAssign) {
			classicTemplateGenerator.generate(mailingId, request, false);
		}
		for (Integer mailingId : mailingsToDeassign) {
			classicTemplateGenerator.generate(mailingId, request, false);
		}
	}

	private ActionErrors storeUploadedTemplate(CMTemplateForm aForm, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		FormFile file = aForm.getTemplateFile();
		if (file != null) {
			if (!file.getFileName().toLowerCase().endsWith("zip")) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.cmtemplate.filetype"));
			} else {
				try {
					byte[] fileData = file.getFileData();
					if (fileData.length > 0) {
						int templateId = readArchivedCMTemplate(aForm, file.getInputStream(), request);
						if (templateId == -1) {
							errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.cmtemplate.notemplatefile"));
						} else {
							aForm.setCmTemplateId(templateId);
							final int maxWidth = PREVIEW_MAX_WIDTH;
							final int maxHeight = PREVIEW_MAX_HEIGHT;
							final HttpSession session = request.getSession();
							final PreviewImageGenerator previewImageGenerator = new PreviewImageGenerator(getWebApplicationContext(request), session, maxWidth, maxHeight);
							previewImageGenerator.generatePreview(templateId, 0, 0);
						}
						return errors;
					}
				} catch (IOException e) {
					logger.error("Error while uploading CM Template: " + e, e);
				}
			}
		}
		if (errors.isEmpty()) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.cmtemplate.upload"));
		}
		return errors;
	}

	public int readArchivedCMTemplate(CMTemplateForm aForm, InputStream stream, HttpServletRequest request) {
		ZipInputStream zipInputStream = new ZipInputStream(stream);
		ZipEntry entry;
		String templateBody = null;
		// binds image name in zip to image id in CCR (Central Content Repository)
		Map<String, Integer> imageBindMap = new HashMap<String, Integer>();
		int newTemplateId = createEmptyCMTemplate(request);
		if (newTemplateId == -1) {
			return -1;
		}
		try {
			while ((entry = zipInputStream.getNextEntry()) != null) {
				String entryName = entry.getName();
				// hack for ignoring MACOS archive system folders
				if (entryName.contains("__MACOSX")) {
					continue;
				}
				// skip if directory
				if (entryName.endsWith("/")) {
					continue;
				}
				// if file is in media-folder - store it in CCR
				if (entryName.startsWith(MEDIA_FOLDER)) {
					// thumbs.db is ignored by EMM
					if (!StringUtils.endsWithIgnoreCase(entryName, THUMBS_DB)) {
						byte[] fileData = getEntryData(zipInputStream, entry);
						int mediaFileId = storeMediaFile(fileData, entryName, newTemplateId, request);
						if (mediaFileId != -1) {
							imageBindMap.put(entryName, mediaFileId);
						}
					}
				} else if (entryName.endsWith(".html") && templateBody == null) {
					// first html file that was found in root folder of
					// zip-archive is considered to be a template-file
					byte[] templateData = getEntryData(zipInputStream, entry);
					templateBody = new String(templateData, Charset.forName(aForm.getCharset()).name());
				}
			}
			zipInputStream.close();
		} catch (IOException e) {
			logger.error("Error occured reading CM template from zip: ", e);
		}
		if (templateBody == null) {
			cmTemplateManager.deleteCMTemplate(newTemplateId);
			mediaFileManager.removeMediaFilesForCMTemplateId(newTemplateId);
			return -1;
		} else {
			templateBody = replacePictureLinks(templateBody, imageBindMap);
			try {
				cmTemplateManager.updateContent(newTemplateId, templateBody.getBytes(Charset.forName("UTF-8").name()));
			} catch (UnsupportedEncodingException e) {
				logger.warn("Wrong charset name", e);
			}
			return newTemplateId;
		}
	}

	private byte[] getEntryData(ZipInputStream zipInputStream, ZipEntry entry) throws IOException {
		byte[] fileData = new byte[(int) entry.getSize()];
		byte[] buf = new byte[2048];
		int bytesRead = 0;
		int dataIndex = 0;
		while (bytesRead != -1) {
			bytesRead = zipInputStream.read(buf);
			for (int i = 0; i < bytesRead; i++) {
				if (dataIndex < fileData.length && i < buf.length) {
					fileData[dataIndex] = buf[i];
					dataIndex++;
				}
			}
		}
		return fileData;
	}

	private String replacePictureLinks(String templateBody, Map<String, Integer> imageBindMap) {
		for (String imageName : imageBindMap.keySet()) {
			Integer imageId = imageBindMap.get(imageName);
			String newImageUrl = CmsUtils.generateMediaFileUrl(imageId);
			templateBody = templateBody.replaceAll("./" + imageName, newImageUrl);
			templateBody = templateBody.replaceAll(imageName, newImageUrl);
		}
		return templateBody;
	}

	private int createEmptyCMTemplate(HttpServletRequest request) {
		Locale locale = AgnUtils.getLocale(request);
		ResourceBundle bundle = ResourceBundle.getBundle("cmsmessages", locale);
		CMTemplate template = new CMTemplate();
		template.setCompanyId(AgnUtils.getCompanyID(request));
		template.setName(bundle.getString("NewCMTemplateName"));
		template.setDescription(bundle.getString("NewCMDescription"));
		template.setContent(new byte[] { 0 });
		template = cmTemplateManager.createCMTemplate(template);
		return template.getId();
	}

	private int storeMediaFile(byte[] fileData, String entryName, int cmTemplateId, HttpServletRequest request) {
		// get mime-type for file
		String mimeType = CmsUtils.UNKNOWN_MIME_TYPE;
		@SuppressWarnings("rawtypes")
		Collection mimeTypes = MimeUtil.getMimeTypes(entryName);
		if (!mimeTypes.isEmpty()) {
			MimeType type = (MimeType) mimeTypes.iterator().next();
			mimeType = type.toString();
		}
		// store media file
		MediaFile mediaFile = new MediaFile();
		mediaFile.setCompanyId(AgnUtils.getCompanyID(request));
		mediaFile.setCmTemplateId(cmTemplateId);
		mediaFile.setName(entryName);
		mediaFile.setMimeType(mimeType);
		mediaFile.setContent(fileData);
		mediaFile = mediaFileManager.createMediaFile(mediaFile);
		return mediaFile.getId();
	}

	private boolean saveCMTemplate(CMTemplateForm aForm) {
		return cmTemplateManager.updateCMTemplate(aForm.getCmTemplateId(), aForm.getName(), aForm.getDescription());
	}

	private void loadCMTemplate(HttpServletRequest request, CMTemplateForm aForm) {
		CMTemplate template = cmTemplateManager.getCMTemplate(aForm.getCmTemplateId());
		if (template != null) {
			aForm.setName(template.getName());
			aForm.setDescription(template.getDescription());
		}
		this.writeUserActivityLog(AgnUtils.getAdmin(request), "do load CM template", aForm.getName());
	}

	private void deleteImage(HttpServletRequest request) {
		int idImage = Integer.parseInt(request.getParameter("cmTemplateMediaFileId"));
		mediaFileManager.removeMediaFile(idImage);
	}

	private void saveEditTemplate(HttpServletRequest request, CMTemplateForm aForm) throws Exception {
		for (MediaFile mediaFile : aForm.getLMediaFile()) {
			final String imageUploadOrExternalSelect = request.getParameter(String.format("imageUploadOrExternal.%s.select", mediaFile.getId()));
			final String imageUploadOrExternalUrl = request.getParameter(String.format("imageUploadOrExternal.%s.url", mediaFile.getId()));
			final String changeImageSelect = request.getParameter(String.format("changeImage.%s.select", mediaFile.getId()));
			if (!StringUtils.isEmpty(changeImageSelect) && changeImageSelect.equals("on")) {
				if (!StringUtils.isEmpty(imageUploadOrExternalSelect)) {
					if (imageUploadOrExternalSelect.equals("upload")) {
						saveUploadImage(request, aForm, mediaFile.getId());
					} else if (imageUploadOrExternalSelect.equals("external")) {
						if (!StringUtils.isEmpty(imageUploadOrExternalSelect)) {
							mediaFileManager.updateMediaFile(
									new MediaFile(0, 0, getImageToUrl(imageUploadOrExternalUrl), 0, 0, mediaFile.getId(), 0, null, null));
						}
					}
				}
			}
		}
		MediaFile mediaFile = new MediaFile(aForm.getCmTemplateId(), AgnUtils.getCompanyID(request), null, 0, 0, -1, 0, "image/jpeg", null);
		final String imageUploadOrExternalNew = request.getParameter("imageUploadOrExternal.new.select");
		final String changeImageNewSelect = request.getParameter("changeImage.new.select");
		if (!StringUtils.isEmpty(changeImageNewSelect) && changeImageNewSelect.equals("on")) {
			mediaFile.setName("template-media/" + request.getParameter("name.new.text"));
			if (!StringUtils.isEmpty(imageUploadOrExternalNew)) {
				if (imageUploadOrExternalNew.equals("upload")) {
					if (aForm.getLNewFile().containsKey(-1)) {
						String fileName = aForm.getLNewFile().get(-1).getFileName();
						mediaFile.setName("template-media/" + fileName);
						mediaFile.setContent(aForm.getLNewFile().get(-1).getFileData());
						mediaFileManager.createMediaFile(mediaFile);
					}
				} else if (imageUploadOrExternalNew.equals("external")) {
					final String image = request.getParameter("imageUploadOrExternal.new.url");
					final String type = image.substring(image.lastIndexOf('.') + 1);
					if (type.equals("gif")) {
						mediaFile.setMimeType("image/gif");
					} else if (type.equals("png")) {
						mediaFile.setMimeType("image/png");
					}
					if (!StringUtils.isEmpty(image)) {
						mediaFile.setContent(getImageToUrl(image));
					}
					mediaFileManager.createMediaFile(mediaFile);
				}
			}
		}
		replaceAgnitasTagImage(aForm, request);
	}

	private void replaceAgnitasTagImage(CMTemplateForm aForm, HttpServletRequest request) {
		String content = aForm.getContentTemplate();
		String sPattern = "\\[agnIMAGE\\s*name=\"(.*)\"\\]";
		Pattern pattern = Pattern.compile(sPattern);
		Matcher matcher = pattern.matcher(content);
		MediaFile mediaFile = null;
		while (matcher.find()) {
			mediaFile = mediaFileManager.getMediaFileForContentModelAndMediaName(aForm.getCmTemplateId(), matcher.group(1));
			if (mediaFile == null) {
				continue;
			}
			String imgTag = CmsUtils.generateMediaFileUrl(mediaFile.getId());
			content = content.replaceFirst(sPattern, imgTag);
		}
		aForm.setContentTemplateNoConvertion(content);
		cmTemplateManager.updateContent(aForm.getCmTemplateId(), content.getBytes(Charset.forName("UTF-8")));
		PreviewImageGenerator previewImageGenerator = new PreviewImageGenerator(getWebApplicationContext(request), request.getSession(), PREVIEW_MAX_WIDTH, PREVIEW_MAX_HEIGHT);
		previewImageGenerator.setMediaFileManager(mediaFileManager);
		previewImageGenerator.generatePreview(aForm.getCmTemplateId(), 0, 0);
	}

	private void saveUploadImage(HttpServletRequest request, CMTemplateForm aForm, int id) throws IOException {
		if (aForm.getLNewFile().containsKey(id)) {
			Map<Integer, FormFile> mTemp = aForm.getLNewFile();
			FormFile ffTemp = mTemp.get(id);
			mediaFileManager.updateMediaFile(new MediaFile(0, 0, ffTemp.getFileData(), 0, 0, id, 0, null, null));
		}
	}

	private byte[] getImageToUrl(String address) throws IOException {
		InputStream urlInputStream = null;
		try {
			URL url = new URL(address);
			urlInputStream = url.openStream();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1];
			int readBytes = 0;
			while ((readBytes = urlInputStream.read(buffer, 0, buffer.length)) > 0) {
				outputStream.write(buffer, 0, readBytes);
			}
			return outputStream.toByteArray();
		} finally {
			IOUtils.closeQuietly(urlInputStream);
		}
	}

	protected void deleteCMTemplate(int templateId, HttpServletRequest request) {
		cmTemplateManager.deleteCMTemplate(templateId);
		mediaFileManager.removeMediaFilesForCMTemplateId(templateId);
		this.writeUserActivityLog(AgnUtils.getAdmin(request), "delete CM template", Integer.toString(templateId));
	}

	private String getCmTemplatePreview(int cmTemplateId) {
		CMTemplate template = cmTemplateManager.getCMTemplate(cmTemplateId);
		if (template != null) {
			try {
				String templateContent = new String(template.getContent(), Charset.forName("UTF-8").name());
				templateContent = CmsUtils.appendImageURLsWithSystemUrl(templateContent);
				return templateContent;
			} catch (UnsupportedEncodingException e) {
				logger.warn("Wrong charser name", e);
			}
		}
		return "";
	}

	/**
	 * Gets list of CM Templates for overview-page table
	 */
	public List<CMTemplate> getCMTemplateList(HttpServletRequest request) throws IllegalAccessException, InstantiationException {
		return cmTemplateManager.getCMTemplates(AgnUtils.getCompanyID(request));
	}

	/**
	 * Gets list of mailings for assign-page
	 */
	public PaginatedListImpl<Map<String, Object>> getMailingsList(HttpServletRequest request, CMTemplateForm templateForm) throws IllegalAccessException, InstantiationException {
		PaginatedListImpl<Map<String, Object>> mailingList = getPageMailings(request, templateForm, mailingDao);

		List<Integer> mailingIds = getMailingIds(mailingList);

		List<Integer> mailingWithNoClassicTemplate = cmsMailingDao.getMailingsWithNoClassicTemplate(mailingIds, AgnUtils.getCompanyID(request));

		Map<Integer, Integer> mailBinding = cmTemplateManager.getMailingBinding(mailingIds);
		templateForm.setOldAssignment(mailBinding);

		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

		for (Map<String, Object> mailingMap : mailingList.getList()) {
			int mailingId;
			String shortname;
			String description;

			mailingId = ((Number) mailingMap.get("mailingid")).intValue();
			shortname = String.valueOf(mailingMap.get("shortname"));
			description = AgnUtils.getStringFromNull(String.valueOf(mailingMap.get("description")));

			Integer bindTemplate = mailBinding.get(mailingId);
			boolean assigned = bindTemplate != null && bindTemplate == templateForm.getCmTemplateId();

			Map<String, Object> newBean = new HashMap<String, Object>();
			newBean.put("mailingid", mailingId);
			newBean.put("shortname", shortname);
			newBean.put("description", description);
			newBean.put("hasCMTemplate", bindTemplate != null);
			newBean.put("assigned", assigned);
			newBean.put("hasClassicTemplate", !mailingWithNoClassicTemplate.contains(mailingId));
			resultList.add(newBean);
		}
		mailingList.getList().clear();
		mailingList.getList().addAll(resultList);
		return mailingList;
	}

	public static PaginatedListImpl<Map<String, Object>> getPageMailings(HttpServletRequest request, StrutsFormBase aForm, MailingDao mailingDao) {
		String sort1 = request.getParameter("sort");
		if (sort1 == null) {
			sort1 = aForm.getSort();
		} else {
			aForm.setSort(sort1);
		}
		String sort = sort1;
		String direction = request.getParameter("dir");
		if (direction == null) {
			direction = aForm.getOrder();
		} else {
			aForm.setOrder(direction);
		}
		String pageStr = request.getParameter("page");
		if (pageStr == null || "".equals(pageStr.trim())) {
			if (aForm.getPage() == null || "".equals(aForm.getPage().trim())) {
				aForm.setPage("1");
			}
			pageStr = aForm.getPage();

		} else {
			aForm.setPage(pageStr);
		}
		if (aForm.isNumberOfRowsChanged()) {
			aForm.setPage("1");
			aForm.setNumberOfRowsChanged(false);
			pageStr = "1";
		}
		int page = Integer.parseInt(pageStr);
		int rownums = aForm.getNumberofRows();
		PaginatedListImpl<Map<String, Object>> mailingList = mailingDao.getMailingList(AgnUtils.getCompanyID(request), "0", false, sort, direction, page, rownums);
		return mailingList;
	}

	public static List<Integer> getMailingIds(PaginatedListImpl<Map<String, Object>> mailingList) {
		List<Integer> mailingIds = new ArrayList<Integer>();
		for (Map<String, Object> map : mailingList.getList()) {
			int mailingId = ((Number) map.get("mailingid")).intValue();
			mailingIds.add(mailingId);
		}
		return mailingIds;
	}

	private WebApplicationContext getWebApplicationContext(HttpServletRequest request) {
		return WebApplicationContextUtils.getRequiredWebApplicationContext(request.getSession().getServletContext());
	}
}