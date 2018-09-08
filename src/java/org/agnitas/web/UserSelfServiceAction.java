package org.agnitas.web;

import org.agnitas.beans.Admin;
import org.agnitas.beans.AdminGroup;
import org.agnitas.beans.AdminPreferences;
import org.agnitas.dao.AdminDao;
import org.agnitas.dao.AdminGroupDao;
import org.agnitas.dao.AdminPreferencesDao;
import org.agnitas.emm.core.commons.password.PasswordCheck;
import org.agnitas.emm.core.commons.password.PasswordCheckHandler;
import org.agnitas.emm.core.commons.password.StrutsPasswordCheckHandler;
import org.agnitas.emm.core.commons.util.ConfigService;
import org.agnitas.emm.core.logintracking.LoginTrackServiceRequestHelper;
import org.agnitas.service.UserActivityLogService;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.forms.AdminForm;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Locale;

/**
 * Implementation of <strong>Action</strong> that lets an user change his password and other profiledata.
 *
 * @author Andreas Soderer (aso)
 * @version 14.03.2012
 */
public class UserSelfServiceAction extends DispatchAction {
	
	/** The logger. */
	private static final transient Logger logger = Logger.getLogger(UserSelfServiceAction.class);
	
	// ----------------------------------------------------------------------------------------------------------------
	// Dependency Injection

	/** DAO for accessing user data. */
	protected AdminDao adminDao;

    /** DAO for accessing admin group data. */
    protected AdminGroupDao adminGroupDao;

    /** DAO for accessing admin preferences data. */
    protected AdminPreferencesDao adminPreferencesDao;
	
	/** Helper for login tracking. */
	private LoginTrackServiceRequestHelper loginTrackHelper;
	
	/** Password checker and error reporter. */
	private PasswordCheck passwordCheck;

	/** Services for accessing application configuration in DB. */
	protected ConfigService configService;
	
	/**
	 * Set DAO for accessing user data.
	 * 
	 * @param adminDao AdminDao
	 */
	@Required
	public void setAdminDao(AdminDao adminDao) {
		this.adminDao = adminDao;
	}

    /**
     * Set DAO for accessing user group.
     *
     * @param adminGroupDao AdminGroupDao
     */
    @Required
    public void setAdminGroupDao(AdminGroupDao adminGroupDao) {
        this.adminGroupDao = adminGroupDao;
    }

    /**
     * Set DAO for accessing user group.
     *
     * @param adminPreferencesDao AdminPreferencesDao
     */
    @Required
    public void setAdminPreferencesDao(AdminPreferencesDao adminPreferencesDao) {
        this.adminPreferencesDao = adminPreferencesDao;
    }

    /**
	 * Set helper for login tracking.
	 * 
	 * @param helper helper for login tracking
	 */
	@Required
	public void setLoginTrackServiceRequestHelper( LoginTrackServiceRequestHelper helper) {
		this.loginTrackHelper = helper;
	}

	/**
	 * Set checker for passwords.
	 * 
	 * @param check checker for passwords
	 */
	@Required
	public void setPasswordCheck(PasswordCheck check) {
		this.passwordCheck = check;
	}

	/**
	 * Set service for accessing application configuration in DB. 
	 * 
	 * @param configService service for accessing application configuration in DB
	 */
	@Required
	public void setConfigService(ConfigService configService) {
		this.configService = configService;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Business Logic

    /**
     * Loads admin data from database into form.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form   The optional ActionForm bean for this request (if any)
     * @param request  The HTTP request we are processing
     * @param response  The HTTP response we are creating
     * @return destination specified in struts-config.xml to forward to next jsp
     * @throws Exception if some exception occurs
     */
	public ActionForward showChangeForm(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {		
		if (form == null || !(form instanceof AdminForm)) {
			throw new RuntimeException("Invalid Form for showChangeForm in UserSelfServiceAction");
		}
        Admin admin = AgnUtils.getAdmin(request);

		if (admin == null) {
			return mapping.findForward("logon");
		}

        try {
            if(AgnUtils.allowed("admin.setgroup", admin)){
                request.setAttribute("adminGroups", adminGroupDao.getAdminGroupsByCompanyId(AgnUtils.getCompanyID(request)));
            }
        } catch (Exception e) {
            logger.error("UserSelfServiceAction.showChangeForm: " + e, e);
        }

        this.loginTrackHelper.setLoginTrackingDataToRequest(request, admin, LoginTrackServiceRequestHelper.DEFAULT_LOGIN_MIN_PERIOD_DAYS);
		this.loginTrackHelper.removeFailedLoginWarningFromRequest( request);

        AdminPreferences adminPreferences = adminPreferencesDao.getAdminPreferences(admin.getAdminID());

		fillAdminFormWithOriginalValues((AdminForm) form, adminPreferences, admin);

		return mapping.findForward("show");
	}

    /**
     * Validates admin full name and admin password form data.
     * If admin name is empty, shows error message of wrong admin name.
     * If admin password is empty or differs from the one stored in session, shows error message of password mismatch.
     * If admin name and admin password are ok, updates admin entry data in database and stores the updated data in
     * current session.
     * Forwards to admin view page.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form   The optional ActionForm bean for this request (if any)
     * @param request  The HTTP request we are processing
     * @param response  The HTTP response we are creating
     * @return destination specified in struts-config.xml to forward to next jsp
     * @throws Exception if some exception occurs
     */
	
	public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
	    ActionMessages errors = new ActionMessages();
		
		if (form == null || !(form instanceof AdminForm)) {
			throw new RuntimeException("Invalid Form for showChangeForm in UserSelfServiceAction");
		}
		
		Admin admin = AgnUtils.getAdmin(request);
		if (admin == null) {
			return mapping.findForward("logon");
		}
        AdminPreferences adminPreferences = adminPreferencesDao.getAdminPreferences(admin.getAdminID());
		this.loginTrackHelper.setLoginTrackingDataToRequest(request, admin, LoginTrackServiceRequestHelper.DEFAULT_LOGIN_MIN_PERIOD_DAYS);

		try {
			AdminForm adminForm = (AdminForm) form;

            // Log changes
            writeUserSelfServiceLog(admin, adminForm, request);
            writeUserPreferencesChangesLog(admin, adminPreferences, adminForm);

            if(adminPreferences != null) {
                //save preferences
                adminPreferences.setAdminID(adminForm.getAdminID());
                adminPreferences.setMailingContentView(adminForm.getMailingContentView());
                adminPreferences.setListSize(adminForm.getNumberOfRows());
                adminPreferences.setMailingSettingsView(adminForm.getMailingSettingsView());
            }
			
			// Set new Fullname
			if (StringUtils.isNotBlank(adminForm.getFullname())) {
				admin.setFullname(adminForm.getFullname());
			}
			else {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.invalid.username"));
			}
			
			// Set new Password
			if (StringUtils.isNotEmpty(adminForm.getPassword())) {
				// Only change if user entered a new password
				if (!adminForm.getPassword().equals(adminForm.getPasswordConfirm())) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.password.mismatch"));
				} else {
					if (checkPassword(adminForm.getPassword(), admin, errors)) {
						admin.setPassword(adminForm.getPassword());
					}
				}

				adminForm.setPassword("");
				adminForm.setPasswordConfirm("");
			}
			// Set new Language and Country
			admin.setAdminLang(adminForm.getAdminLocale().getLanguage());
			admin.setAdminCountry(adminForm.getAdminLocale().getCountry());
			
			// Set new Timezone
			admin.setAdminTimezone(adminForm.getAdminTimezone());
			
            // Set new admin group
            AdminGroup group = adminGroupDao.getAdminGroup(adminForm.getGroupID());
            if(group != null){
                admin.setGroup(group);
            }

		} catch (Exception e) {
			logger.error("UserSelfServiceAction.save: " + e, e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception", configService.getValue(ConfigService.Value.SupportEmergencyUrl)));
		}
		
		if (errors.isEmpty()) {
			adminDao.save(admin);
            adminPreferencesDao.save(adminPreferences);
			
			// Set the new values for this session
			HttpSession session = request.getSession();
			session.setAttribute("emm.admin", admin);
            session.setAttribute("emm.adminPreferences", adminPreferences);
			session.setAttribute("emm.locale", admin.getLocale());
			session.setAttribute(org.apache.struts.Globals.LOCALE_KEY, admin.getLocale());
			
			ActionMessages messages = new ActionMessages();
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("changes_saved"));
			saveMessages(request, messages);
			return mapping.findForward("show");
		}
		else {
			// Revert Admin Data Changes
			AgnUtils.setAdmin(request, adminDao.getAdmin(admin.getAdminID(), admin.getCompanyID()));
			
			saveErrors(request, errors);
			
			return mapping.findForward("show");
		}
	}
	
    /**
     * Load an admin account.
     * Loads the data of the admin from the database and stores it in the
     * form.
     *
     * @param adminForm AdminForm object
     * @param adminPreferences AdminPreferences object
     * @param admin  Admin bean object
     */
	protected void fillAdminFormWithOriginalValues(AdminForm adminForm, AdminPreferences adminPreferences, Admin admin) {
        adminForm.setAdminID(admin.getAdminID());
        adminForm.setUsername(admin.getUsername());
        adminForm.setPassword("");
        adminForm.setPasswordConfirm("");
        adminForm.setCompanyID(admin.getCompanyID());
        adminForm.setFullname(admin.getFullname());
        adminForm.setAdminLocale(new Locale(admin.getAdminLang(), admin.getAdminCountry()));
        adminForm.setAdminTimezone(admin.getAdminTimezone());
        adminForm.setLayoutID(admin.getLayoutID());
        adminForm.setUserRights(admin.getAdminPermissions());
        adminForm.setGroupID(admin.getGroup().getGroupID());
        adminForm.setGroupRights(admin.getGroup().getGroupPermissions());
        adminForm.setNumberOfRows(adminPreferences.getListSize());
        adminForm.setMailingContentView(adminPreferences.getMailingContentView());
        adminForm.setMailingSettingsView(adminPreferences.getMailingSettingsView());

		if (logger.isDebugEnabled()) logger.debug("loadAdmin: admin " + adminForm.getAdminID() + " loaded");
    }

	/** Service class for accessing user activity log. */
    private UserActivityLogService userActivityLogService;

    /**
     * Set service class for accessing user activity log. 
     * 
     * @param userActivityLogService service class for accessing user activity log
     */
    @Required
    public void setUserActivityLogService(UserActivityLogService userActivityLogService) {
        this.userActivityLogService = userActivityLogService;
    }

    /**
     * Write user activity log.
     * 
     * @param admin user 
     * @param action performed action
     * @param description description
     */
    protected void writeUserActivityLog(Admin admin, String action, String description)  {
        try {
            if (userActivityLogService != null) {
                userActivityLogService.writeUserActivityLog(admin, action, description);
            } else {
                logger.error("Missing userActivityLogService in " + this.getClass().getSimpleName());
                logger.info("Userlog: " + admin.getUsername() + " " + action + " " +  description);
            }
        } catch (Exception e) {
            logger.error("Error writing ActivityLog: " + e.getMessage(), e);
            logger.info("Userlog: " + admin.getUsername() + " " + action + " " +  description);
        }
    }
    
    /**
     * Check password to match several constraints.
     * 
     * @param password password to check
     * @param admin Admin to the password
     * @param errors ActionMessages to be filled
     * 
     * @return {@code true} if password is valid
     */
    private boolean checkPassword(String password, Admin admin, ActionMessages errors) {
    	PasswordCheckHandler handler = new StrutsPasswordCheckHandler(errors, "password");
    	
    	return this.passwordCheck.checkAdminPassword(password, admin, handler);
    }

    /**
     * Compare existed and new user data and write changes in user log
     *
     * @param adminForm the formular passed from the jsp
     * @param admin existed admin account data
     * @param request processed HTTP request
     */
    private void writeUserSelfServiceLog(Admin admin, AdminForm adminForm, HttpServletRequest request){

        try {
            String userName = admin.getUsername();
            if(!(admin.getFullname().equals(adminForm.getFullname()))){
                writeUserActivityLog(admin, "edit user " + userName,
                        "Name changed from " + admin.getFullname() + " to " + adminForm.getFullname());
            }

            if(passwordCheck.passwordChanged(admin.getUsername(), adminForm.getPassword())){
                writeUserActivityLog(admin, "edit user " + userName, "Password changed");
            }

            if(!(admin.getAdminLang().equals(adminForm.getAdminLocale().getLanguage()))){
                writeUserActivityLog(admin, "edit user " + admin.getUsername(),
                        "Language changed from " + Locale.forLanguageTag(admin.getAdminLang()).getDisplayLanguage() +
                                " to " + Locale.forLanguageTag(adminForm.getAdminLocale().getLanguage()).getDisplayLanguage());
            }
            if(!(admin.getAdminTimezone().equals(adminForm.getAdminTimezone()))){
                writeUserActivityLog(admin, "edit user " + admin.getUsername(),
                        "Timezone changed from " + admin.getAdminTimezone() + " to " + adminForm.getAdminTimezone());
            }
            if(admin.getGroup().getGroupID() != adminForm.getGroupID()){
                String oldGroupName = admin.getGroup().getGroupID() == 0 ? "None" : admin.getGroup().getShortname();
                String newGroupName = adminForm.getGroupID() == 0 ? "None" : adminGroupDao.getAdminGroup(adminForm.getGroupID()).getShortname();

                writeUserActivityLog(admin, "edit user " + userName,
                        "User Group changed from " + oldGroupName +
                                " to " + newGroupName);
            }

            if (logger.isInfoEnabled()){
                logger.info("saveOpenEmmUser: self edit save user " + adminForm.getAdminID());
            }
        } catch (Exception e) {
            logger.error("Log OpenEMM User self changes error" + e);
        }
    }

    /**
     * Compare existed and new user preferences data and write changes in user log
     *
     * @param admin            existed admin account data
     * @param adminPreferences existed admin preferences data
     * @param adminForm        the data passed from the jsp
     */
    private void writeUserPreferencesChangesLog(Admin admin, AdminPreferences adminPreferences, AdminForm adminForm){

        try {
            String userName = admin.getUsername();

            if(adminPreferences.getListSize() != adminForm.getNumberOfRows()){
                writeUserActivityLog(admin, "edit user " + userName,
                        "List length changed from " + adminPreferences.getListSize() + " to " + adminForm.getNumberOfRows());
            }

            int oldMailingContentView = adminPreferences.getMailingContentView();
            int newMailingContentView = adminForm.getMailingContentView();

            if(oldMailingContentView != newMailingContentView){
                writeUserActivityLog(admin, "edit user " + userName,
                        "User mailing content view type changed from " + getMailingContentViewName(oldMailingContentView) +
                                " to " + getMailingContentViewName(newMailingContentView));
            }

            int oldMailingSettingsView = adminPreferences.getMailingSettingsView();
            int newMailingSettingsView = adminForm.getMailingSettingsView();

            if(oldMailingSettingsView != newMailingSettingsView){
                writeUserActivityLog(admin, "edit user " + userName,
                        "User mailing settings view type changed from " + getMailingSettingsViewName(oldMailingSettingsView) +
                                " to " + getMailingSettingsViewName(newMailingSettingsView));
            }

            if (logger.isInfoEnabled()){
                logger.info("saveOpenEmmUser: self edit save user preferences " + adminForm.getAdminID());
            }
        } catch (Exception e) {
            logger.error("Log OpenEMM User self user preferences changes error" + e);
        }
    }

    /**
     * Return mailing content view type text representation by id
     *
     * @param type MailingContentViewType Id
     * @return   mailing content view type text representation
     */
    private String getMailingContentViewName(int type){

        switch (type){
            case 0:
                return "HTML-Code";
            case 1:
                return "HTML-Editor";
            default:
                return "Unknown type";
        }
    }

    /**
     * Return mailing settings view type text representation by id
     *
     * @param type MailingSettingsViewType Id
     * @return   mailing settings view type text representation
     */
    private String getMailingSettingsViewName(int type){

        switch (type){
            case 0:
                return "Expanded";
            case 1:
                return "Collapsed";
            default:
                return "Unknown type";
        }
    }

}
