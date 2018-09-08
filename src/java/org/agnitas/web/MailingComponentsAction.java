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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.beans.Mailing;
import org.agnitas.beans.MailingComponent;
import org.agnitas.beans.TrackableLink;
import org.agnitas.beans.impl.MailingComponentImpl;
import org.agnitas.dao.MailingComponentDao;
import org.agnitas.dao.MailingDao;
import org.agnitas.dao.TrackableLinkDao;
import org.agnitas.emm.core.commons.util.ConfigService;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.FileUtils;
import org.agnitas.util.ZipUtilities;
import org.agnitas.web.forms.MailingComponentsForm;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.TransientDataAccessResourceException;

/**
 * Implementation of <strong>Action</strong> that validates a user logon.
 *
 * @author Martin Helff, Nicole Serek
 */

public class MailingComponentsAction extends StrutsActionBase {
	private static final transient Logger logger = Logger.getLogger(MailingComponentsAction.class);

    public static final int ACTION_SAVE_COMPONENTS = ACTION_LAST+1;

    public static final int ACTION_SAVE_COMPONENT_EDIT = ACTION_LAST+2;

    public static final int ACTION_BULK_DOWNLOAD_COMPONENT = ACTION_LAST+3;
    
    public static final int ACTION_MAILINGCOMPONENT_LAST = ACTION_LAST+3;

    protected static final List imageTypes = Arrays.asList(new String[]{".jpg", ".gif", ".png"});

	private MailingDao mailingDao;
	private MailingComponentDao componentDao;
	private TrackableLinkDao linkDao;
	
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
     * ACTION_LIST:  loads mailing data to form, forwards to "list"
     * <br><br>
     * ACTION_SAVE_COMPONENTS: checks if file to upload was selected and that the file has correct file
     *     type (.png, .jsp, .gif). If it is ok - adds the component to database. Also handles updating and deletion
     *     of components: checks the request parameters "delete" + componentID to delete certain components, checks
     *     request parameters "update" + componentID to update certain components. Also loads mailing data to form
     *     and forwards to "list". If no component was updated or removed and there's no file to upload - shows the
     *     error message that no file was selected.
     * <br><br>
     * ACTION_SAVE_COMPONENT_EDIT: saves component to database. Also handles updating and deletion
     *     of components: checks the request parameters "delete" + componentID to delete certain components, checks
     *     request parameters "update" + componentID to update certain components. Forwards to "component_edit".<br>
     *     Doesn't seem to be currently used (it seems that it was used for fck-editor).
     * <br><br>
     * ACTION_BULK_DOWNLOAD_COMPONENT: get picture components from database, compress their in zip-archive,
     *     provides downloading zip file.
     * <br><br>
     * Any other ACTION_* would cause a forward to "list"
     * <br><br>
     * If the forward is "list" - loads components and components links to request.
     *
     * @param form ActionForm object
     * @param req HTTP request
     * @param res HTTP response
     * @param mapping The ActionMapping used to select this instance
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     * @return destination
     */
	@Override
    public ActionForward execute(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest req,
            HttpServletResponse res)
            throws IOException, ServletException {

        // Validate the request parameters specified by the user
        MailingComponentsForm aForm=null;
        ActionMessages errors = new ActionMessages();
    	ActionMessages messages = new ActionMessages();
    	ActionForward destination=null;

        if(!AgnUtils.isUserLoggedIn(req)) {
            return mapping.findForward("logon");
        }

        aForm=(MailingComponentsForm)form;
        if (logger.isInfoEnabled()) logger.info("Action: "+aForm.getAction());

        try {
            switch(aForm.getAction()) {
                case MailingComponentsAction.ACTION_LIST:
                    if(allowed("mailing.components.show", req)) {
                        loadMailing(aForm, req);
                        aForm.setAction(MailingComponentsAction.ACTION_SAVE_COMPONENTS);
                        destination=mapping.findForward("list");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case MailingComponentsAction.ACTION_SAVE_COMPONENTS:
                    if(allowed("mailing.components.change", req)) {
                        destination=mapping.findForward("list");
                        if(!"".equals(aForm.getNewFile().getFileName()) && !imageTypes.contains(getFileType(aForm.getNewFile().getFileName()))) {
                            loadMailing(aForm, req);
                            aForm.setAction(MailingComponentsAction.ACTION_SAVE_COMPONENTS);
                            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("mailing.errors.format.error"));
                            break;
                        }
                        loadMailing(aForm, req);
                        try {
                            saveComponent(aForm, req, messages, errors);
                        } catch (TransientDataAccessResourceException e) {
                            logger.error("execute: " + e, e);
                            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.hibernate.attachmentTooLarge"));
                        }
                        aForm.setAction(MailingComponentsAction.ACTION_SAVE_COMPONENTS);
                        Enumeration parameterNames = req.getParameterNames();
                        boolean aComponentWasJustDeleted = false;
                        boolean aComponentWasJustUpdated = false;
                        while (parameterNames.hasMoreElements()) {
                            Object parameter = parameterNames.nextElement();
                            if (parameter instanceof String){
                                String parameterString = (String) parameter;
                                if (parameterString.startsWith("delete") && AgnUtils.parameterNotEmpty(req, parameterString)){
                                    aComponentWasJustDeleted = true;
                                    break;
                                }
                                if (parameterString.startsWith("update") && AgnUtils.parameterNotEmpty(req, parameterString)){
                                    aComponentWasJustUpdated = true;
                                    break;
                                }
                            }
                        }

                        // Show "changes saved"
                        if (errors.isEmpty()) {
                            if (!aComponentWasJustDeleted && !aComponentWasJustUpdated && (aForm.getNewFile() == null || aForm.getNewFile().getFileName() == null || "".equals(aForm.getNewFile().getFileName()))) {
                                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("mailing.errors.no_component_file"));
                            } else {
                                messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
                            }
                        }
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                        destination=mapping.findForward("list");
                    }
                    break;

                case MailingComponentsAction.ACTION_SAVE_COMPONENT_EDIT:
                    if(allowed("mailing.components.change", req)) {
                        destination=mapping.findForward("component_edit");
                        saveComponent(aForm, req, messages, errors);
                        aForm.setAction(MailingComponentsAction.ACTION_SAVE_COMPONENTS);

                        // Show "changes saved"
                    	messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case MailingComponentsAction.ACTION_BULK_DOWNLOAD_COMPONENT:
                    byte bytes[] = new byte[16384];
                    int len = 0;
                    String filename = getExportFilename(aForm) + ".zip";

                    File zipFile = createComponentsZipFile(aForm, req, filename);


                    if(zipFile != null) {

                        FileInputStream instream = null;
                        try {
                            instream = new FileInputStream(zipFile);
                            res.setContentType("application/zip");
                            res.setHeader("Content-Disposition", "attachment; filename=\"" + filename +"\";");
                            res.setContentLength((int)zipFile.length());
                            @SuppressWarnings("resource")
                            ServletOutputStream ostream = res.getOutputStream();
                            while((len = instream.read(bytes))!=-1) {
                                ostream.write(bytes, 0, len);
                            }
                        }catch(Exception e){
                            logger.error("execute: "+e, e);
                        }

                        finally {
                            if (instream != null) {
                                instream.close();
                            }

                        }
                        destination=null;
                    } else {
                        errors.add("global", new ActionMessage("mailing.Graphics_Component.NoImage"));
                    }
                    aForm.setAction(MailingComponentsAction.ACTION_SAVE_COMPONENTS);
                    break;


                default:
                    aForm.setAction(MailingComponentsAction.ACTION_LIST);
                    destination=mapping.findForward("list");
            }
            if (destination != null && "list".equals(destination.getName())) {
                List<MailingComponent> components = loadComponents(aForm, req);
                req.setAttribute("components", components);
                List<TrackableLink> links = loadComponentsLinks(aForm, req, components);
                req.setAttribute("componentLinks", links);
            }

        } catch (Exception e) {
            logger.error("execute: "+e, e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception", configService.getValue(ConfigService.Value.SupportEmergencyUrl)));
        }

        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(req, errors);
        }

        // Report any message (non-errors) we have discovered
        if (!messages.isEmpty()) {
        	saveMessages(req, messages);
        }

        return destination;
    }

    /**
     * Loads mailing.
     */
    protected void loadMailing(MailingComponentsForm aForm, HttpServletRequest req) throws Exception {
        Mailing aMailing=mailingDao.getMailing(aForm.getMailingID(), AgnUtils.getCompanyID(req));

        aForm.setShortname(aMailing.getShortname());
        aForm.setDescription(aMailing.getDescription());
        aForm.setIsTemplate(aMailing.isIsTemplate());
        aForm.setLink("");
        aForm.setWorldMailingSend(aMailing.isWorldMailingSend());

        if (logger.isInfoEnabled()) logger.info("loadMailing: mailing loaded");
    }

    /**
     * Saves components.
     */
    protected boolean saveComponent(MailingComponentsForm aForm, HttpServletRequest req, ActionMessages messages, ActionMessages errors) {
        MailingComponent aComp = null;
        Vector<MailingComponent> deleteEm = new Vector<MailingComponent>();

        Mailing aMailing=mailingDao.getMailing(aForm.getMailingID(), AgnUtils.getCompanyID(req));

        addUploadedImage(aForm, aMailing, req, null, messages, errors);

        Iterator<MailingComponent> it=aMailing.getComponents().values().iterator();
        while (it.hasNext()) {
            aComp = it.next();
            switch(aComp.getType()) {
                case MailingComponent.TYPE_IMAGE:
                    if(AgnUtils.parameterNotEmpty(req, "update" + aComp.getId())) {
                        aComp.loadContentFromURL();
                    }
                    break;

                case MailingComponent.TYPE_HOSTED_IMAGE:
                    if(AgnUtils.parameterNotEmpty(req, "delete" + aComp.getId())) {
                        deleteEm.add(aComp);
                        if (AgnUtils.isProjectEMM()){
	                        MailingComponent amComponent=componentDao.getMailingComponentByName(aComp.getMailingID(), aComp.getCompanyID(), aComp.getComponentName());
    	                    componentDao.deleteMailingComponent(amComponent);
                        }
                    }
                    break;
            }
        }

        Enumeration<MailingComponent> en = deleteEm.elements();
        while(en.hasMoreElements()) {
        	aMailing.getComponents().remove(en.nextElement().getComponentName());
        }

        mailingDao.saveMailing(aMailing);

        if(deleteEm.size() == 0){
          return true;
        } else {
          return false;
        }
    }

	protected List<MailingComponent> loadComponents(MailingComponentsForm aForm, HttpServletRequest request) {
		List<MailingComponent> components = componentDao.getMailingComponents(aForm.getMailingID(), AgnUtils.getCompanyID(request));
		request.setAttribute("components", components);
		return components;
	}

	protected List<TrackableLink> loadComponentsLinks(MailingComponentsForm aForm, HttpServletRequest request, List<MailingComponent> components) {
		List<TrackableLink> links = new ArrayList<TrackableLink>();
		for(MailingComponent component : components) {
			int urlID = component.getUrlID();
			if (urlID > 0) {
				TrackableLink trackableLink = linkDao.getTrackableLink(urlID, AgnUtils.getCompanyID(request));
				links.add(trackableLink);
			}
		}
		return links;

	}

	protected void addUploadedImage(MailingComponentsForm componentForm, Mailing mailing, HttpServletRequest req, String componentName, ActionMessages messages, ActionMessages errors) {
		MailingComponent component = null;

		FormFile newImage = componentForm.getNewFile();
		String newComponentName = componentName;
		if (StringUtils.isBlank(newComponentName)) {
			newComponentName = newImage.getFileName();
		}

		try {
			if (newImage.getFileSize() != 0) {
				component = mailing.getComponents().get(newComponentName);
				if (component != null && component.getType() == MailingComponent.TYPE_HOSTED_IMAGE) {
					component.setBinaryBlock(newImage.getFileData());
					component.setEmmBlock(component.makeEMMBlock());
					component.setMimeType(newImage.getContentType());
					component.setLink(componentForm.getLink());
					component.setDescription(componentForm.getDescription());
				} else {
					component = new MailingComponentImpl();
					component.setCompanyID(mailing.getCompanyID());
					component.setMailingID(componentForm.getMailingID());
					component.setType(MailingComponent.TYPE_HOSTED_IMAGE);
					component.setDescription(componentForm.getDescription());
					component.setComponentName(newComponentName);
					component.setBinaryBlock(newImage.getFileData());
					component.setEmmBlock(component.makeEMMBlock());
					component.setMimeType(newImage.getContentType());
					component.setLink(componentForm.getLink());
					mailing.addComponent(component);
				}
			}
		} catch (Exception e) {
			logger.error("saveComponent: " + e);
		}

		if (componentForm.getAction() == MailingComponentsAction.ACTION_SAVE_COMPONENT_EDIT) {
			req.setAttribute("file_path",
					AgnUtils.getAdmin(req).getCompany().getRdirDomain() + "/image?ci=" + mailing.getCompanyID() + "&mi=" + componentForm.getMailingID() + "&name=" + newImage.getFileName());
		}
	}

    protected File createComponentsZipFile(MailingComponentsForm form, HttpServletRequest req, String fileName) throws IOException {
        int mailingId = form.getMailingID();
        int companyId = AgnUtils.getCompanyID(req);
        List<File> allImageComponents = new ArrayList<File>();

        Mailing mailing = mailingDao.getMailing(mailingId, companyId);
        Map<String, MailingComponent> components =  mailing.getComponents();

        if(components.isEmpty()){ //no pictures connected with mailing
            return null;
        }

        File systemUploadDirectory = AgnUtils.createDirectory(AgnUtils.getDefaultValue("system.upload"));
        for (MailingComponent component : components.values()){
            if (component != null && component.getType() == MailingComponent.TYPE_HOSTED_IMAGE){
                String componentFileType =  getFileType(component.getComponentName());
                if(imageTypes.contains(componentFileType)){ //create file from bin block and add created file to list
                    String componentFileName = getFileName(component.getComponentName());
                    byte[] bytes = component.getBinaryBlock();
                    File outFile = File.createTempFile(componentFileName, componentFileType, systemUploadDirectory);
                    InputStream inputStream = new ByteArrayInputStream(bytes);

                    FileUtils.streamToFile(inputStream, bytes.length, outFile);
                    allImageComponents.add(outFile);
                }
            }
        }
        File zipFile = File.createTempFile(fileName, ".zip", systemUploadDirectory);
        try {
            ZipUtilities.addFileToEmptyZipFile(allImageComponents, zipFile);
            return zipFile;
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        return null;
    }

    protected String getExportFilename(MailingComponentsForm aForm) {
        Date currentTimestamp = AgnUtils.getSysdate("now()");
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(currentTimestamp);
        int month = currentDate.get(Calendar.MONTH) + 1;
        return "Uploaded_images_mailingId_"+ aForm.getMailingID()+ "_" + currentDate.get(Calendar.YEAR) + "_" + month + "_" + currentDate.get(Calendar.DAY_OF_MONTH);
    }

	public void setMailingDao(MailingDao mailingDao) {
		this.mailingDao = mailingDao;
	}

	public MailingDao getMailingDao() {
		return mailingDao;
	}

	public void setComponentDao(MailingComponentDao componentDao) {
		this.componentDao = componentDao;
	}

	public MailingComponentDao getComponentDao() {
		return componentDao;
	}

	public void setLinkDao(TrackableLinkDao linkDao) {
		this.linkDao = linkDao;
	}

	public TrackableLinkDao getLinkDao() {
		return linkDao;
	}

    public String getFileType(String fileName) {
    	int index = fileName.lastIndexOf( '.');
    	
    	// No "."? Take whole name
    	if( index == -1)
    		index = 0;
    	
    	String ext = fileName.substring( index).toLowerCase().trim();
    	
        return ext;
    }

    public String getFileName(String fileName) {
        int index = fileName.lastIndexOf( '.');

        // Return whole name if input string do not consist on "."
        if( index == -1)
            return fileName;

        String name = fileName.substring(0, index).toLowerCase().trim();

        return name;
    }

}
