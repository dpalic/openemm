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
 * the code written by AGNITAS AG are Copyright (c) 2009 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/

package org.agnitas.cms.web;

import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil;
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
import org.agnitas.util.AgnUtils;
import org.agnitas.web.StrutsActionBase;
import org.agnitas.web.forms.StrutsFormBase;
import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.struts.action.*;
import org.apache.struts.upload.FormFile;
import org.displaytag.pagination.PaginatedList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Action for managing CM templates
 *
 * @author Vyacheslav Stepanov
 */

public class CMTemplateAction extends StrutsActionBase {

	public static final int ACTION_PURE_PREVIEW = ACTION_LAST + 1;
	public static final int ACTION_UPLOAD = ACTION_LAST + 2;
	public static final int ACTION_STORE_UPLOADED = ACTION_LAST + 3;
	public static final int ACTION_ASSIGN_LIST = ACTION_LAST + 4;
	public static final int ACTION_STORE_ASSIGNMENT = ACTION_LAST + 5;

	// @todo will be moved to some other place
	public static final String MEDIA_FOLDER = "template-media";

	public static final int LIST_PREVIEW_WIDTH = 500;
	public static final int LIST_PREVIEW_HEIGHT = 400;
	public static final int PREVIEW_MAX_WIDTH = 150;
	public static final int PREVIEW_MAX_HEIGHT = 150;


	public ActionForward execute(ActionMapping mapping, ActionForm form,
								 HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {

		CMTemplateForm aForm;

		ActionMessages errors = new ActionMessages();
		ActionMessages messages = new ActionMessages();
		ActionForward destination = null;

		if(!this.checkLogon(req)) {
			return mapping.findForward("logon");
		}

		if(form != null) {
			aForm = (CMTemplateForm) form;
		} else {
			aForm = new CMTemplateForm();
		}

		AgnUtils.logger().info("Action: " + aForm.getAction());

		// if preview size is changed - return to view page
		if(req.getParameter("changePreviewSize.x") != null) {
			aForm.setAction(CMTemplateAction.ACTION_VIEW);
		}

		// if assign button is pressed - store mailings assignment
		if(req.getParameter("assign.x") != null) {
			aForm.setAction(CMTemplateAction.ACTION_STORE_ASSIGNMENT);
		}

		try {
			switch(aForm.getAction()) {
				case CMTemplateAction.ACTION_LIST:
					destination = mapping.findForward("list");
					aForm.reset(mapping, req);
					aForm.setAction(CMTemplateAction.ACTION_LIST);
					break;

				case CMTemplateAction.ACTION_ASSIGN_LIST:
					destination = mapping.findForward("assign_list");
					aForm.reset(mapping, req);
					aForm.setAction(CMTemplateAction.ACTION_ASSIGN_LIST);
					break;

				case CMTemplateAction.ACTION_STORE_ASSIGNMENT:
					storeMailingAssignment(req, aForm);
					destination = mapping.findForward("assign_list");
					aForm.reset(mapping, req);
					aForm.setAction(CMTemplateAction.ACTION_ASSIGN_LIST);

					messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("changes_saved"));
					break;

				case CMTemplateAction.ACTION_VIEW:
					loadCMTemplate(aForm);
					aForm.setAction(CMTemplateAction.ACTION_SAVE);
					destination = mapping.findForward("view");
					break;

				case CMTemplateAction.ACTION_UPLOAD:
					aForm.setAction(CMTemplateAction.ACTION_STORE_UPLOADED);
					destination = mapping.findForward("upload");
					break;

				case CMTemplateAction.ACTION_STORE_UPLOADED:
					errors = storeUploadedTemplate(aForm, req);
					// if template is uploaded and stored successfuly - go to
					// template edit page, otherwise - stay on upload page to display
					// errors and allow user to repeat his try to upload template
					if(errors.isEmpty()) {
						loadCMTemplate(aForm);
						aForm.setAction(CMTemplateAction.ACTION_SAVE);
						destination = mapping.findForward("view");

						messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("changes_saved"));
					} else {
						aForm.setAction(CMTemplateAction.ACTION_STORE_UPLOADED);
						destination = mapping.findForward("upload");
					}
					break;

				case CMTemplateAction.ACTION_SAVE:
					boolean saveOk = saveCMTemplate(aForm);
					// if save is successful - stay on view page
					// if not - got to list page
					if(saveOk) {
						aForm.setAction(CMTemplateAction.ACTION_SAVE);
						destination = mapping.findForward("view");

						messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("changes_saved"));
					} else {
						destination = mapping.findForward("list");
						aForm.setAction(CMTemplateAction.ACTION_LIST);
					}
					break;

				case CMTemplateAction.ACTION_PURE_PREVIEW:
					destination = mapping.findForward("pure_preview");
					aForm.reset(mapping, req);
					aForm.setPreview(getCmTemplatePreview(aForm.getCmTemplateId()));
					aForm.setAction(CMTemplateAction.ACTION_PURE_PREVIEW);
					break;

				case CMTemplateAction.ACTION_CONFIRM_DELETE:
					loadCMTemplate(aForm);
					aForm.setAction(CMTemplateAction.ACTION_DELETE);
					destination = mapping.findForward("delete");
					break;

				case CMTemplateAction.ACTION_DELETE:
					if(req.getParameter("kill.x") != null) {
						deleteCMTemplate(aForm.getCmTemplateId());
					}
					aForm.setAction(CMTemplateAction.ACTION_LIST);
					destination = mapping.findForward("list");

					messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("changes_saved"));
					break;
			}
		}
		catch(Exception e) {
			AgnUtils.logger()
					.error("Error while executing action with CM Template: " + e + "\n" +
							AgnUtils.getStackTrace(e));
			errors.add(ActionMessages.GLOBAL_MESSAGE,
					new ActionMessage("error.exception"));
		}

		// collect list of CM Templates for list-page
		if(destination != null && "list".equals(destination.getName())) {
			try {
				setNumberOfRows(req, (StrutsFormBase) form);
				req.setAttribute("cmTemplateList", getCMTemplateList(req));
			} catch(Exception e) {
				AgnUtils.logger()
						.error("cmTemplateList: " + e + "\n" + AgnUtils.getStackTrace(e));
				errors.add(ActionMessages.GLOBAL_MESSAGE,
						new ActionMessage("error.exception"));
			}
		}

		// collect list of Mailings for assign-page
		if(destination != null && "assign_list".equals(destination.getName())) {
			try {
				setNumberOfRows(req, (StrutsFormBase) form);
				req.setAttribute("mailingsList", getMailingsList(req, aForm));
			} catch(Exception e) {
				AgnUtils.logger().error("getMailingsList: " + e + "\n" +
						AgnUtils.getStackTrace(e));
				errors.add(ActionMessages.GLOBAL_MESSAGE,
						new ActionMessage("error.exception"));
			}
		}

		// Report any errors we have discovered back to the original form
		if(!errors.isEmpty()) {
			saveErrors(req, errors);
		}

		if(!messages.isEmpty()) {
			saveMessages(req, messages);
		}

		return destination;
	}

	private void storeMailingAssignment(HttpServletRequest req, CMTemplateForm aForm) {
		List<Integer> assignedMailings = new ArrayList<Integer>();
		Enumeration parameterNames = req.getParameterNames();
		while(parameterNames.hasMoreElements()) {
			String paramName = (String) parameterNames.nextElement();
			if(paramName.startsWith("assign_mailing_")) {
				String value = req.getParameter(paramName);
				if(value != null) {
					if(value.startsWith("mailing_")) {
						value = value.substring("mailing_".length());
						assignedMailings.add(Integer.parseInt(value));
					}
				}
			}
		}
		List<Integer> mailingsToAssign = new ArrayList<Integer>();
		List<Integer> mailingsToDeassign = new ArrayList<Integer>();
		Map<Integer, Integer> oldAssignment = aForm.getOldAssignment();
		for(Integer mailingId : oldAssignment.keySet()) {
			if(!assignedMailings.contains(mailingId) &&
					oldAssignment.get(mailingId) == aForm.getCmTemplateId()) {
				mailingsToDeassign.add(mailingId);
			}
		}
		for(Integer assignedMailingId : assignedMailings) {
			if(oldAssignment.get(assignedMailingId) == null) {
				mailingsToAssign.add(assignedMailingId);
			} else if(oldAssignment.get(assignedMailingId) != aForm.getCmTemplateId()) {
				mailingsToDeassign.add(assignedMailingId);
				mailingsToAssign.add(assignedMailingId);
			}
		}

		getTemplateManager().removeMailingBindings(mailingsToDeassign);
		getTemplateManager()
				.addMailingBindings(aForm.getCmTemplateId(), mailingsToAssign);
		final ClassicTemplateGenerator classicTemplateGenerator =
				(ClassicTemplateGenerator) getWebApplicationContext()
						.getBean("ClassicTemplateGenerator");
		for(Integer mailingId : mailingsToAssign) {
			classicTemplateGenerator.generate(mailingId, req, false);
		}
		for(Integer mailingId : mailingsToDeassign) {
			classicTemplateGenerator.generate(mailingId, req, false);
		}
	}

	private ActionErrors storeUploadedTemplate(CMTemplateForm aForm,
											   HttpServletRequest req) {
		ActionErrors errors = new ActionErrors();
		FormFile file = aForm.getTemplateFile();
		if(file != null) {
			if(!file.getContentType().contains("zip")) {
				errors.add(ActionMessages.GLOBAL_MESSAGE,
						new ActionMessage("error.cmtemplate.filetype"));
			} else {
				try {
					byte[] fileData = file.getFileData();
					if(fileData.length > 0) {
						int templateId = readArchivedCMTemplate(aForm,
								file.getInputStream(), req);
						if(templateId == -1) {
							errors.add(ActionMessages.GLOBAL_MESSAGE,
									new ActionMessage("error.cmtemplate.notemplatefile"));
						} else {
							aForm.setCmTemplateId(templateId);
							final int maxWidth = PREVIEW_MAX_WIDTH;
							final int maxHeight = PREVIEW_MAX_HEIGHT;
							final HttpSession session = req.getSession();
							final PreviewImageGenerator previewImageGenerator =
									new PreviewImageGenerator(getWebApplicationContext(),
											session,
											maxWidth,
											maxHeight);
							previewImageGenerator.generatePreview(templateId, 0, 0);
						}
						return errors;
					}
				} catch(IOException e) {
					AgnUtils.logger()
							.error("Error while uploading CM Template: " + e + "\n" +
									AgnUtils.getStackTrace(e));
				}
			}
		}
		if(errors.isEmpty()) {
			errors.add(ActionMessages.GLOBAL_MESSAGE,
					new ActionMessage("error.cmtemplate.upload"));
		}
		return errors;
	}

	public int readArchivedCMTemplate(CMTemplateForm aForm, InputStream stream,
									  HttpServletRequest request) {
		ZipInputStream zipInputStream = new ZipInputStream(stream);
		ZipEntry entry;
		String templateBody = null;
		// binds image name in zip to image id in CCR (Central Content Repository)
		Map<String, Integer> imageBindMap = new HashMap<String, Integer>();
		int newTemplateId = createEmptyCMTemplate(request);
		try {
			while((entry = zipInputStream.getNextEntry()) != null) {
				String entryName = entry.getName();
				// hack for ignoring MACOS archive system folders
				if(entryName.contains("__MACOSX")) {
					continue;
				}
				// skip if directory
				if(entryName.endsWith("/")) {
					continue;
				}
				// if file is in media-folder - store it in CCR
				if(entryName.startsWith(MEDIA_FOLDER)) {
					byte[] fileData = getEntryData(zipInputStream, entry);
					int mediaFileId = storeMediaFile(fileData, entryName, newTemplateId,
							request);
					if(mediaFileId != -1) {
						imageBindMap.put(entryName, mediaFileId);
					}
				} else if(entryName.endsWith(".html") && templateBody == null) {
					// first html file that was found in root folder of
					// zip-archive is considered to be a template-file
					byte[] templateData = getEntryData(zipInputStream, entry);
					templateBody = new String(templateData,
							Charset.forName(aForm.getCharset()).name());
				}
			}
			zipInputStream.close();
		} catch(IOException e) {
			AgnUtils.logger().error("Error occured reading CM template from zip: ", e);
		}
		if(templateBody == null) {
			getTemplateManager().deleteCMTemplate(newTemplateId);
			getMediaManager().removeMediaFilesForCMTemplateId(newTemplateId);
			return -1;
		} else {
			templateBody = replacePictureLinks(templateBody, imageBindMap);
			try {
				getTemplateManager().updateContent(newTemplateId,
						templateBody.getBytes(Charset.forName("UTF-8").name()));
			} catch(UnsupportedEncodingException e) {
				AgnUtils.logger().warn("Wrong charset name", e);
			}
			return newTemplateId;
		}
	}

	private byte[] getEntryData(ZipInputStream zipInputStream, ZipEntry entry) throws
			IOException {
		byte[] fileData = new byte[(int) entry.getSize()];
		byte[] buf = new byte[2048];
		int bytesRead = 0;
		int dataIndex = 0;
		while(bytesRead != -1) {
			bytesRead = zipInputStream.read(buf);
			for(int i = 0; i < bytesRead; i++) {
				if(dataIndex < fileData.length && i < buf.length) {
					fileData[dataIndex] = buf[i];
					dataIndex++;
				}
			}
		}
		return fileData;
	}

	private String replacePictureLinks(String templateBody,
									   Map<String, Integer> imageBindMap) {
		for(String imageName : imageBindMap.keySet()) {
			Integer imageId = imageBindMap.get(imageName);
			String newImageUrl = CmsUtils.generateMediaFileUrl(imageId);
			templateBody = templateBody.replaceAll("./" + imageName, newImageUrl);
			templateBody = templateBody.replaceAll(imageName, newImageUrl);
		}
		return templateBody;
	}

	private int createEmptyCMTemplate(HttpServletRequest request) {
		Locale locale = (Locale) request.getSession()
				.getAttribute(org.apache.struts.Globals.LOCALE_KEY);
		ResourceBundle bundle = ResourceBundle.getBundle("cmsmessages", locale);
		CMTemplate template = new CMTemplate();
		template.setCompanyId(AgnUtils.getCompanyID(request));
		template.setName(bundle.getString("NewCMTemplateName"));
		template.setDescription(bundle.getString("NewCMDescription"));
		template.setContent(new byte[]{0});
		template = getTemplateManager().createCMTemplate(template);
		return template.getId();
	}

	private int storeMediaFile(byte[] fileData, String entryName,
							   int cmTemplateId, HttpServletRequest request) {
		// get mime-type for file
		String mimeType = CmsUtils.UNKNOWN_MIME_TYPE;
		Collection mimeTypes = MimeUtil.getMimeTypes(entryName);
		if(!mimeTypes.isEmpty()) {
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
		mediaFile = getMediaManager().createMediaFile(mediaFile);
		return mediaFile.getId();
	}

	private boolean saveCMTemplate(CMTemplateForm aForm) {
		return getTemplateManager().updateCMTemplate(aForm.getCmTemplateId(),
				aForm.getName(), aForm.getDescription());
	}

	private void loadCMTemplate(CMTemplateForm aForm) {
		CMTemplate template = getTemplateManager().getCMTemplate(aForm.getCmTemplateId());
		if(template != null) {
			aForm.setName(template.getName());
			aForm.setDescription(template.getDescription());
		}
	}

	private void deleteCMTemplate(int cmTemplateId) {
		getTemplateManager().deleteCMTemplate(cmTemplateId);
		getMediaManager().removeMediaFilesForCMTemplateId(cmTemplateId);
	}

	private String getCmTemplatePreview(int cmTemplateId) {
		CMTemplate template = getTemplateManager().getCMTemplate(cmTemplateId);
		if(template != null) {
			try {
                String templateContent = new String(template.getContent(), Charset.forName("UTF-8").name());
                templateContent = CmsUtils.appendImageURLsWithSystemUrl(templateContent);
                return templateContent;
			} catch(UnsupportedEncodingException e) {
				AgnUtils.logger().warn("Wrong charser name", e);
			}
		}
		return "";
	}

	/**
	 * Gets list of CM Templates for overview-page table
	 */
	public List<CMTemplate> getCMTemplateList(HttpServletRequest request) throws
			IllegalAccessException, InstantiationException {
		return getTemplateManager().getCMTemplates(AgnUtils.getCompanyID(request));
	}

	/**
	 * Gets list of mailings for assign-page
	 */
	public PaginatedList getMailingsList(HttpServletRequest req,
										 CMTemplateForm templateForm) throws
			IllegalAccessException, InstantiationException {
		PaginatedList mailingList = getPageMailings(req, templateForm,
				(MailingDao) getBean("MailingDao"));

		List<Integer> mailingIds = getMailingIds(mailingList);

		CmsMailingDao cmsMailingDao = (CmsMailingDao) getWebApplicationContext()
				.getBean("CmsMailingDao");

		List<Integer> mailingWithNoClassicTemplate =
				cmsMailingDao.getMailingsWithNoClassicTemplate(mailingIds,
						AgnUtils.getCompanyID(req));

		Map<Integer, Integer> mailBinding = getTemplateManager()
				.getMailingBinding(mailingIds);
		templateForm.setOldAssignment(mailBinding);

		DynaProperty[] properties = new DynaProperty[]{
				new DynaProperty("mailingid", Long.class),
				new DynaProperty("shortname", String.class),
				new DynaProperty("description", String.class),
				new DynaProperty("assigned", Boolean.class),
				new DynaProperty("hasCMTemplate", Boolean.class),
				new DynaProperty("hasClassicTemplate", Boolean.class),
		};
		BasicDynaClass dynaClass = new BasicDynaClass("mailingExtended", null,
				properties);

		List<DynaBean> resultList = new ArrayList<DynaBean>();

		for(Object object : mailingList.getList()) {
			DynaBean mailingBean = (DynaBean) object;
			Long mailingId = (Long) mailingBean.get("mailingid");
			Integer bindTemplate = mailBinding.get(mailingId.intValue());
			boolean assigned = bindTemplate != null &&
					bindTemplate == templateForm.getCmTemplateId();

			DynaBean newBean = dynaClass.newInstance();
			newBean.set("mailingid", mailingId);
			newBean.set("shortname", mailingBean.get("shortname"));
			newBean.set("description", mailingBean.get("description"));
			newBean.set("hasCMTemplate", bindTemplate != null);
			newBean.set("assigned", assigned);
			newBean.set("hasClassicTemplate",
					!mailingWithNoClassicTemplate.contains(mailingId.intValue()));
			resultList.add(newBean);
		}
		mailingList.getList().clear();
		mailingList.getList().addAll(resultList);
		return mailingList;
	}

	public static PaginatedList getPageMailings(HttpServletRequest req,
												StrutsFormBase aForm,
												MailingDao mailingDao) {
		String sort1 = req.getParameter("sort");
		if(sort1 == null) {
			sort1 = aForm.getSort();
		} else {
			aForm.setSort(sort1);
		}
		String sort = sort1;
		String direction = req.getParameter("dir");
		if(direction == null) {
			direction = aForm.getOrder();
		} else {
			aForm.setOrder(direction);
		}
		String pageStr = req.getParameter("page");
		if(pageStr == null || "".equals(pageStr.trim())) {
			if(aForm.getPage() == null || "".equals(aForm.getPage().trim())) {
				aForm.setPage("1");
			}
			pageStr = aForm.getPage();

		} else {
			aForm.setPage(pageStr);
		}
		if(aForm.isNumberOfRowsChanged()) {
			aForm.setPage("1");
			aForm.setNumberOfRowsChanged(false);
			pageStr = "1";
		}
		int page = Integer.parseInt(pageStr);
		int rownums = aForm.getNumberofRows();
		PaginatedList mailingList = mailingDao.getMailingList(AgnUtils.getCompanyID(req),
				"0", false, sort, direction, page, rownums);
		return mailingList;
	}

	public static List<Integer> getMailingIds(PaginatedList mailingList) {
		List<Integer> mailingIds = new ArrayList<Integer>();
		for(Object object : mailingList.getList()) {
			DynaBean mailingBean = (DynaBean) object;
			Long mailingId = (Long) mailingBean.get("mailingid");
			mailingIds.add(mailingId.intValue());
		}
		return mailingIds;
	}

	private CMTemplateManager getTemplateManager() {
		return CmsUtils.getCMTemplateManager(getWebApplicationContext());
	}

	private MediaFileManager getMediaManager() {
		return CmsUtils.getMediaFileManager(getWebApplicationContext());
	}

}