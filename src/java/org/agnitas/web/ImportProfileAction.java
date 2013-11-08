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

package org.agnitas.web;

import org.agnitas.beans.Admin;
import org.agnitas.beans.ImportProfile;
import org.agnitas.beans.impl.ImportProfileImpl;
import org.agnitas.dao.AdminDao;
import org.agnitas.dao.ImportProfileDao;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.ImportUtils;
import org.agnitas.web.forms.ImportProfileForm;
import org.agnitas.web.forms.StrutsFormBase;
import org.apache.struts.action.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Action that handles import profile actions: view, edit, remove, list,
 * manage gender mappings.
 *
 * @author Vyacheslav Stepanov
 */
public class ImportProfileAction extends StrutsActionBase {

    public static final int ACTION_NEW_GENDER = ACTION_LAST + 1;

    public static final int ACTION_REMOVE_GENDER = ACTION_LAST + 2;

    public static final int ACTION_SET_DEFAULT = ACTION_LAST + 3;

    protected static final String IMPORT_PROFILE_ERRORS_KEY = "import-profile-errors";
    protected static final String IMPORT_PROFILE_ID_KEY = "import-profile-id";

    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form    The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param res     The HTTP response we are creating
     * @throws java.io.IOException            if an input/output error occurs
     * @throws javax.servlet.ServletException if a servlet exception occurs
     */
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse res)
            throws IOException, ServletException {

        // Validate the request parameters specified by the user
        ImportProfileForm aForm;
        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();
        ActionForward destination = null;

        if (!this.checkLogon(request)) {
            return mapping.findForward("logon");
        }
        if (form != null) {
            aForm = (ImportProfileForm) form;
        } else {
            aForm = new ImportProfileForm();
        }

        AgnUtils.logger().info("Action: " + aForm.getAction());

        if (request.getSession().getAttribute(IMPORT_PROFILE_ID_KEY) != null) {
            errors = (ActionMessages) request.getSession().getAttribute(IMPORT_PROFILE_ERRORS_KEY);
            int profileId = (Integer) request.getSession().getAttribute(IMPORT_PROFILE_ID_KEY);
            aForm.setProfileId(profileId);
            aForm.setAction(ACTION_VIEW);
            request.getSession().removeAttribute(IMPORT_PROFILE_ERRORS_KEY);
            request.getSession().removeAttribute(IMPORT_PROFILE_ID_KEY);
        }

        if (request.getParameter("addGender.x") != null) {
            aForm.setAction(ACTION_NEW_GENDER);
        }

        if (ImportUtils.hasParameterStartsWith(request, "removeGender")) {
            aForm.setAction(ACTION_REMOVE_GENDER);
        }

        if (request.getParameter("delete.x") != null) {
            aForm.setAction(ACTION_CONFIRM_DELETE);
        }

        if (request.getParameter("setDefault.x") != null) {
            aForm.setAction(ACTION_SET_DEFAULT);
        }

        try {
            switch (aForm.getAction()) {
                case ImportProfileAction.ACTION_LIST:
                    destination = mapping.findForward("list");
                    aForm.reset(mapping, request);
                    aForm.setAction(ImportProfileAction.ACTION_LIST);
                    break;

                case ImportProfileAction.ACTION_VIEW:
                    aForm.reset(mapping, request);
                    loadImportProfile(aForm);
                    aForm.setAction(ImportProfileAction.ACTION_SAVE);
                    destination = mapping.findForward("view");
                    break;

                case ImportProfileAction.ACTION_NEW_GENDER:
                    createNewGender(aForm);
                    aForm.setAction(ImportProfileAction.ACTION_SAVE);
                    destination = mapping.findForward("view");
                    break;

                case ImportProfileAction.ACTION_REMOVE_GENDER:
                    removeGender(aForm, request);
                    aForm.setAction(ImportProfileAction.ACTION_SAVE);
                    destination = mapping.findForward("view");
                    break;

                case ImportProfileAction.ACTION_SAVE:
                    if(aForm.getProfileId() == 0 && profileNameIsDuplicate(aForm, request)) {
                        errors.add("shortname", new ActionMessage("error.import.duplicate_profile_name"));
                    } else {
                    saveImportProfile(aForm);
                    messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("changes_saved"));
                    }
                    aForm.setAction(ImportProfileAction.ACTION_SAVE);
                    destination = mapping.findForward("view");
                    
                    
                    break;

                case ImportProfileAction.ACTION_NEW:
                    aForm.reset(mapping, request);
                    aForm.setAction(ImportProfileAction.ACTION_SAVE);
                    createEmptyProfile(aForm, request);
                    destination = mapping.findForward("view");
                    break;

                case ImportProfileAction.ACTION_SET_DEFAULT:
                    setDefaultProfile(aForm, request);
                    aForm.setAction(ImportProfileAction.ACTION_LIST);
                    destination = mapping.findForward("list");
                    
                    messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("changes_saved"));
                    break;

                case ImportProfileAction.ACTION_CONFIRM_DELETE:
                    loadImportProfile(aForm);
                    aForm.setAction(ImportProfileAction.ACTION_DELETE);
                    destination = mapping.findForward("delete");
                    break;

                case ImportProfileAction.ACTION_DELETE:
                    if (request.getParameter("kill.x") != null) {
                        removeProfile(aForm);
                        aForm.setAction(ImportProfileAction.ACTION_LIST);
                        
                        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("changes_saved"));
                    }
                    destination = mapping.findForward("list");
                    break;

                default:
                    aForm.setAction(ImportProfileAction.ACTION_LIST);
                    destination = mapping.findForward("list");
            }

        } catch (Exception e) {
            AgnUtils.logger().error("execute: " + e + "\n" + AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        }

        if (destination != null && "list".equals(destination.getName())) {
            try {
                // if we will go to list page we need to load profiles list
                // and load the default profile for current admin to show
                // that on list-page
                setNumberOfRows(request, (StrutsFormBase) form);
                request.setAttribute("profileList", getProfileList(request));
                aForm.setDefaultProfileId(AgnUtils.getAdmin(request).getDefaultImportProfileID());
            } catch (Exception e) {
                AgnUtils.logger().error("getCampaignList: " + e + "\n" + AgnUtils.getStackTrace(e));
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
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

    /**
     * Method checks if there is already a profile with a name user entered creating new profile
     *
     * @param aForm form
     * @param request request
     * @return true if the profile with such name alreay exists, false if not
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private boolean profileNameIsDuplicate(ImportProfileForm aForm, HttpServletRequest request)
            throws IllegalAccessException, InstantiationException {
        List<ImportProfile> importProfileList = getProfileList(request);
        for(ImportProfile importProfile : importProfileList) {
            if(importProfile.getName().equals(aForm.getProfile().getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Saves the default profile id for admin that is set on overview-page
     *
     * @param aForm   a form
     * @param request request
     */
    private void setDefaultProfile(ImportProfileForm aForm, HttpServletRequest request) {
        int defaultProfileId = aForm.getDefaultProfileId();
        Admin admin = AgnUtils.getAdmin(request);
        admin.setDefaultImportProfileID(defaultProfileId);
        AdminDao adminDao = (AdminDao) getWebApplicationContext().getBean("AdminDao");
        adminDao.save(admin);
    }

    /**
     * Creates empty profile for displaying on view-page when user wants to
     * create new import profile
     *
     * @param aForm   a form
     * @param request request
     */
    private void createEmptyProfile(ImportProfileForm aForm, HttpServletRequest request) {
        ImportProfileImpl newProfile = new ImportProfileImpl();
        newProfile.setAdminId(AgnUtils.getAdmin(request).getAdminID());
        newProfile.setCompanyId(AgnUtils.getCompanyID(request));
        newProfile.setKeyColumn("email");
        aForm.setProfile(newProfile);
        aForm.setProfileId(0);
    }

    /**
     * Removes profile from system with all its data using Dao
     *
     * @param aForm a form
     */
    public void removeProfile(ImportProfileForm aForm) {
        ImportProfileDao profileDao = (ImportProfileDao) getWebApplicationContext().getBean("ImportProfileDao");
        profileDao.removeImportProfileFull(aForm.getProfileId());
    }

    /**
     * @param request request
     * @return list of import profiles for overview page with current company id
     */
    private List<ImportProfile> getProfileList(HttpServletRequest request) throws InstantiationException, IllegalAccessException {
        ImportProfileDao profileDao = (ImportProfileDao) getWebApplicationContext().getBean("ImportProfileDao");
        return profileDao.getImportProfiles(AgnUtils.getCompanyID(request));
    }

    /**
     * Handles user action of removing gender mapping
     *
     * @param aForm   a form
     * @param request request
     */
    private void removeGender(ImportProfileForm aForm, HttpServletRequest request) {
        String gender = ImportUtils.getValueFromParameter(request, "removeGender_", ".x");
        aForm.getProfile().getGenderMapping().remove(gender);
    }

    /**
     * Creates new gender mapping added by user on profile view-page
     *
     * @param aForm a form
     */
    private void createNewGender(ImportProfileForm aForm) {
        aForm.getProfile().getGenderMapping().put(aForm.getAddedGender(), aForm.getAddedGenderInt());
        aForm.setAddedGender("");
        aForm.setAddedGenderInt(0);
    }

    /**
     * Saves import profile (or creats new if its' "New import profile" page)
     *
     * @param aForm a form
     */
    private void saveImportProfile(ImportProfileForm aForm) {
        ImportProfileDao profileDao = (ImportProfileDao) getWebApplicationContext().getBean("ImportProfileDao");
        if (aForm.getProfileId() != 0) {
            profileDao.updateImportProfile(aForm.getProfile());
        } else {
            int profileId = profileDao.createImportProfile(aForm.getProfile());
            aForm.getProfile().setId(profileId);
            aForm.setProfileId(profileId);
        }

        profileDao.removeGenderMappings(aForm.getProfile().getId());
        profileDao.saveGenderMappings(aForm.getProfile().getGenderMapping(), aForm.getProfile().getId());
    }

    /**
     * Loads import profile to show it on profile view-page
     *
     * @param aForm a form
     */
    private void loadImportProfile(ImportProfileForm aForm) {
        ImportProfileDao profileDao = (ImportProfileDao) getWebApplicationContext().getBean("ImportProfileDao");
        ImportProfile profile = profileDao.getImportProfileFull(aForm.getProfileId());
		aForm.setProfile(profile);
	}


}