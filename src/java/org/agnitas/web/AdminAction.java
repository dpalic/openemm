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
import org.agnitas.beans.AdminEntry;
import org.agnitas.beans.AdminGroup;
import org.agnitas.beans.AdminPreferences;
import org.agnitas.beans.impl.AdminImpl;
import org.agnitas.beans.impl.PaginatedListImpl;
import org.agnitas.dao.AdminDao;
import org.agnitas.dao.AdminGroupDao;
import org.agnitas.dao.AdminPreferencesDao;
import org.agnitas.dao.CompanyDao;
import org.agnitas.emm.core.commons.password.PasswordCheck;
import org.agnitas.emm.core.commons.password.PasswordCheckHandler;
import org.agnitas.emm.core.commons.password.StrutsPasswordCheckHandler;
import org.agnitas.emm.core.commons.util.ConfigService;
import org.agnitas.service.AdminListQueryWorker;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.forms.AdminForm;
import org.agnitas.web.forms.StrutsFormBase;
import org.apache.commons.lang.StringUtils;
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
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Implementation of <strong>Action</strong> that handles Account Admins
 * 
 * @author Andreas Rehak, Martin Helff
 */

public class AdminAction extends StrutsActionBase {

    /** The logger. */
	private static final transient Logger logger = Logger.getLogger(AdminAction.class);

	public static final int ACTION_VIEW_RIGHTS = ACTION_LAST + 1;
	public static final int ACTION_SAVE_RIGHTS = ACTION_LAST + 2;
	public static final int ACTION_VIEW_WITHOUT_LOAD = ACTION_LAST + 3;
	protected static final String FUTURE_TASK = "GET_ADMIN_LIST";

	/** DAO for accessing admin data. */
	protected AdminDao adminDao;
	
	/** DAO for accessing admin group data. */
	protected AdminGroupDao adminGroupDao;

    /** DAO for accessing admin preferences data. */
    protected AdminPreferencesDao adminPreferencesDao;
	
	/** DAO for accessing company data. */
	protected CompanyDao companyDao;
	protected ConcurrentHashMap<String, Future<PaginatedListImpl<AdminEntry>>> futureHolder;
	protected ScheduledThreadPoolExecutor executorService;
	
	/** Service for accessing configuration. */
	protected ConfigService configService;
	
	/** Password checker and error reporter. */
	private PasswordCheck passwordCheck;

	// ---------------------------------------- Public Methods

	/**
	 * Process the specified HTTP request, and create the corresponding HTTP
	 * response (or forward to another web component that will create it).
	 * Return an <code>ActionForward</code> instance describing where and how
	 * control should be forwarded, or <code>null</code> if the response has
	 * already been completed.
	 * <br>
	 * ACTION_LIST: calls a FutureHolder to get the list of entries.<br>
	 * 		While FutureHolder is running, destination is "loading". <br>
	 * 		After FutureHolder is finished destination is "list".
	 * <br><br>
	 * ACTION_SAVE: checks, if admin username was changed to one that is used for some another admin of the same company<br>
 * 		    If the username is ok, saves admin data
	 * <br><br>
     * ACTION_VIEW: loads data of chosen admin into form and forwards to admin view page
     * <br><br>
     * ACTION_VIEW_RIGHTS: loads list of permissions for given admin and forwards to user right list page
     * <br><br>
     * ACTION_SAVE_RIGHTS: saves permissions for certain admin and forwards to user right list page
     * <br><br>
     * ACTION_NEW: creates new admin db entry if the password field is not empty (after trimming the password string)<br>
     *      and there is no another admin with the same username;<br>
     *      saves permissions for the new admin;<br>
     *      forwards to admin list page
     * <br><br>
     * ACTION_VIEW_WITHOUT_LOAD: is used after failing form validation<br>
     *      for loading essential data into request before returning to the view page;<br>
     *      does not reload form data
     * <br><br>
	 * ACTION_CONFIRM_DELETE: only forwards to jsp with question to confirm deletion.
	 * <br><br>
	 * ACITON_DELETE: deletes the entry of certain admin, deletes userrights of given admin, forwards to admin list page.
	 * <br><br>
	 * Any other ACTION_* would cause a forward to "list"
     * <br><br>
	 * @param mapping
	 *            The ActionMapping used to select this instance
	 * @param form  ActionForm object, data for the action filled by the jsp
	 * @param req   HTTP request from jsp
	 * @param res   HTTP response
	 * @exception IOException
	 *                if an input/output error occurs
	 * @exception ServletException
	 *                if a servlet exception occurs
	 * @return destination specified in struts-config.xml to forward to next jsp
	 */
    @Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		AdminForm aForm = null;
		ActionMessages errors = new ActionMessages();
		ActionMessages messages = new ActionMessages();
		ActionForward destination = null;

		if (!AgnUtils.isUserLoggedIn(req)) {
			return mapping.findForward("logon");
		}

		if (form != null) {
			aForm = (AdminForm) form;
		} else {
			aForm = new AdminForm();
		}

		if (logger.isInfoEnabled()) logger.info("Action: " + aForm.getAction());
		if (req.getParameter("delete") != null && req.getParameter("delete").equals("delete")) {
			aForm.setAction(ACTION_CONFIRM_DELETE);
		}

		try {
			switch (aForm.getAction()) {
			case AdminAction.ACTION_LIST:
				if (allowed("admin.show", req)) {
					destination = prepareList(mapping, req, errors, destination, aForm);
				} else {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
				}
				break;

			case AdminAction.ACTION_VIEW:
				if (allowed("admin.show", req)) {
					if (aForm.getAdminID() != 0) {
						aForm.setAction(AdminAction.ACTION_SAVE);
						loadAdmin(aForm, req);
					} else {
						aForm.setAction(AdminAction.ACTION_NEW);
					}
					destination = mapping.findForward("view");
				} else {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
				}
				break;

			case AdminAction.ACTION_SAVE:
				if (allowed("admin.change", req)) {
					if (AgnUtils.parameterNotEmpty(req, "save")) {
						if (!adminUsernameChangedToExisting(aForm)) {
							if(checkPassword(aForm, errors)) {
								saveAdmin(aForm, req);
	
								// Show "changes saved"
								messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
							}
						} else {
							errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.username.duplicate"));
						}
					}
				} else {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
				}
				destination = mapping.findForward("view");
				break;

			case AdminAction.ACTION_VIEW_RIGHTS:
				loadAdmin(aForm, req);
				aForm.setAction(AdminAction.ACTION_SAVE_RIGHTS);
				destination = mapping.findForward("rights");
				break;

			case AdminAction.ACTION_SAVE_RIGHTS:
				saveAdminRights(aForm, req);
				loadAdmin(aForm, req);
				aForm.setAction(AdminAction.ACTION_SAVE_RIGHTS);
				destination = mapping.findForward("rights");

				// Show "changes saved"
				messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
				break;

			case AdminAction.ACTION_NEW:
				if (allowed("admin.new", req)) {
					if (AgnUtils.parameterNotEmpty(req, "save")) {
						aForm.setAdminID(0);

						if (aForm.getPassword().length() > 0) {
							if (!adminExists(aForm)) {
								if(checkPassword(aForm, errors)) {
									try {
										saveAdmin(aForm, req);
	
										// Show "changes saved"
										messages.add(ActionMessages.GLOBAL_MESSAGE,new ActionMessage("default.changes_saved"));

										destination = prepareList(mapping, req, errors, destination, aForm);
										aForm.setAction(AdminAction.ACTION_LIST);
									} catch (Exception e) {
										errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.admin.save"));
										destination = mapping.findForward("view");
										aForm.setAction(AdminAction.ACTION_NEW);
									}
								} else {
									destination = mapping.findForward("view");
								}
							} else {
								errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.username.duplicate"));
								destination = mapping.findForward("view");
								aForm.setAction(ACTION_NEW);
							}
						} else {
							errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.admin.no_password"));
							destination = mapping.findForward("view");
							aForm.setAction(AdminAction.ACTION_NEW);
						}
					}
				} else {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
				}
				break;

			case AdminAction.ACTION_CONFIRM_DELETE:
				loadAdmin(aForm, req);
				aForm.setAction(AdminAction.ACTION_DELETE);
				destination = mapping.findForward("delete");
				break;

			case AdminAction.ACTION_DELETE:
				if (req.getParameter("kill") != null) {
					if (allowed("admin.delete", req)) {
						deleteAdmin(aForm, req);

						// Show "changes saved"
						messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
					} else {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
					}
					aForm.setAction(AdminAction.ACTION_LIST);
					destination = prepareList(mapping, req, errors, destination, aForm);
				}
				break;
			case AdminAction.ACTION_VIEW_WITHOUT_LOAD:
				if (allowed("admin.show", req)) {
					req.setAttribute("adminGroups", adminGroupDao.getAdminGroupsByCompanyId(AgnUtils.getAdmin(req).getCompanyID()));
					if (aForm.getAdminID() != 0) {
						aForm.setAction(AdminAction.ACTION_SAVE);
					} else {
						aForm.setAction(AdminAction.ACTION_NEW);
					}
					destination = mapping.findForward("view");
				} else {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
				}
				break;

			default:
				aForm.setAction(AdminAction.ACTION_LIST);
				destination = prepareList(mapping, req, errors, destination,
						aForm);
			}
		} catch (Exception e) {
			logger.error("execute: " + e, e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception", configService.getValue(ConfigService.Value.SupportEmergencyUrl)));
			throw new ServletException(e);
		}

		if (destination != null && "view".equals(destination.getName())) {
			req.setAttribute("adminGroups", adminGroupDao.getAdminGroupsByCompanyId(AgnUtils.getAdmin(req).getCompanyID()));
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
     * Check password and return result.
     * 
     * @param form FormBean used to retrieve admin and password data
     * @param errors data structure to report errors found during validation of password
     * 
     * @return {@code true} if password is ok, otherwise {@code false}
     */
    protected boolean checkPassword(AdminForm form, ActionMessages errors) {
    	Admin admin = adminDao.getAdmin(form.getAdminID(), form.getCompanyID());
    	
    	/*
    	 *  Flag to control check of password. 
    	 *  Password check cannot be run in any case.
    	 *  
    	 *  When to check password:
    	 *  1. A new admin is created. A password is required, so check it.
    	 *  2. An existing admin is updated. Password check is required, if and only if password is set in form (-> user requested password to be updated)
    	 */
    	boolean checkPassword = true;

    	if(admin != null && admin.getAdminID() != 0 && StringUtils.isEmpty(form.getPassword())) {
   			// There is an existing admin, but no password -> no update of password -> no check
   			checkPassword = false;
    	}

    	if(checkPassword) {
    		PasswordCheckHandler handler = new StrutsPasswordCheckHandler(errors, "password");
    		
    		return this.passwordCheck.checkAdminPassword(form.getPassword(), admin, handler);
    	} else {
    		return true; // No password is always valid.
    	}
    	
    }
    
	/**
	 * Load an admin account. Loads the data of the admin from the database and
	 * stores it in the form.
	 * 
	 * @param aForm
	 *            the formular passed from the jsp
	 * @param req
	 *            the Servlet Request (needed to get the company id)
	 */
	protected void loadAdmin(AdminForm aForm, HttpServletRequest req) {
		int adminID = aForm.getAdminID();
		int compID = AgnUtils.getCompanyID(req);
		Admin admin = adminDao.getAdmin(adminID, compID);

		if (admin != null) {
            AdminPreferences adminPreferences = adminPreferencesDao.getAdminPreferences(adminID);

			aForm.setUsername(admin.getUsername());
			aForm.setPassword("");
			aForm.setPasswordConfirm("");
			aForm.setCompanyID(admin.getCompany().getId());
			aForm.setFullname(admin.getFullname());
			aForm.setAdminLocale(new Locale(admin.getAdminLang(), admin.getAdminCountry()));
			aForm.setAdminTimezone(admin.getAdminTimezone());
			aForm.setLayoutID(admin.getLayoutID());
			aForm.setGroupID(admin.getGroup().getGroupID());
			aForm.setUserRights(admin.getAdminPermissions());
			aForm.setGroupRights(admin.getGroup().getGroupPermissions());
			aForm.setNumberOfRows(adminPreferences.getListSize());
            aForm.setMailingContentView(adminPreferences.getMailingContentView());
            aForm.setMailingSettingsView(adminPreferences.getMailingSettingsView());
			if (logger.isInfoEnabled()) logger.info("loadAdmin: admin " + aForm.getAdminID()+ " loaded");
		} else {
			aForm.setAdminID(0);
			aForm.setCompanyID(AgnUtils.getCompanyID(req));
			logger.warn("loadAdmin: admin " + aForm.getAdminID() + " could not be loaded");
		}
	}

	/**
	 * Save an admin account. Gets the admin data from a form and stores it in
	 * the database.
	 * 
	 * @param aForm
	 *            the formula passed from the jsp
	 * @param req
	 *            the Servlet Request (needed to get the company id)
	 */
	protected void saveAdmin(AdminForm aForm, HttpServletRequest req) {
		int adminID = aForm.getAdminID();
		int compID = aForm.getCompanyID();
		int groupID = aForm.getGroupID();
		Admin admin = adminDao.getAdmin(adminID, compID);
		boolean isNew = false;
		if (admin == null) {
			admin = new AdminImpl();
			admin.setCompanyID(compID);
			admin.setCompany(companyDao.getCompany(compID));
			admin.setLayoutID(0);
			isNew = true;
		}

		AdminGroup group = adminGroupDao.getAdminGroup(groupID);

		admin.setAdminID(aForm.getAdminID());

        if(!isNew){
            writeOpenEmmSpecificUserChangesLog(aForm, admin);
        }

		if (!isNew && passwordChanged(admin.getUsername(), aForm.getPassword())) {
			admin.setLastPasswordChange(new Date());
		}

		admin.setUsername(aForm.getUsername());
		if (aForm.getPassword() != null
				&& aForm.getPassword().trim().length() != 0) {
			admin.setPassword(aForm.getPassword());
		}

		if (aForm.getPassword().length() > 0) {
			logger.error("Username: " + aForm.getUsername() + " PasswordLength: " + aForm.getPassword().length());
		} else {
			logger.error("Username: " + aForm.getUsername());
		}

		admin.setFullname(aForm.getFullname());
		admin.setAdminCountry(aForm.getAdminLocale().getCountry());
		admin.setAdminLang(aForm.getAdminLocale().getLanguage());
		admin.setAdminTimezone(aForm.getAdminTimezone());
		admin.setGroup(group);

		adminDao.save(admin);

        if (isNew) {
            int createdAdminId = adminDao.getAdminByLogin(aForm.getUsername(), aForm.getPassword()).getAdminID();
            writeUserActivityLog(AgnUtils.getAdmin(req), "create user " + admin.getUsername(), "User created, ID = " + createdAdminId);
            adminPreferencesDao.writeDefaultValues(createdAdminId);
        }else {
            AdminPreferences adminPreferences = adminPreferencesDao.getAdminPreferences(adminID);
            writeOpenEmmSpecificUserPreferencesChangesLog(aForm, admin, adminPreferences);
            adminPreferences.setListSize(aForm.getNumberOfRows());
            adminPreferences.setMailingContentView(aForm.getMailingContentView());
            adminPreferences.setMailingSettingsView(aForm.getMailingSettingsView());
            adminPreferencesDao.save(adminPreferences);
            // Set the new values for this session if user edit own profile via Administration -> User -> OwnProfile
            if (admin.getAdminID() == AgnUtils.getAdmin(req).getAdminID()) {
                HttpSession session = req.getSession();
                session.setAttribute("emm.admin", admin);
                session.setAttribute("emm.adminPreferences", adminPreferences);
            }
        }

		if (logger.isInfoEnabled()) logger.info("saveAdmin: admin " + aForm.getAdminID());
	}

	protected boolean passwordChanged(String username, String password) {
		Admin admin = adminDao.getAdminByLogin(username, password);
		if (StringUtils.isEmpty(password) || (admin != null && admin.getAdminID() > 0)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Save the permission for an admin. Gets the permissions for the admin from
	 * the form and stores it in the database.
	 * 
	 * @param aForm
	 *            the formula passed from the jsp
	 * @param req
	 *            the Servlet Request (needed to get the company id)
	 */
	protected void saveAdminRights(AdminForm aForm, HttpServletRequest req) {
        int adminID = aForm.getAdminID();
        int compID = aForm.getCompanyID();
        Admin admin = adminDao.getAdmin(adminID, compID);


        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages");
        Enumeration<String> aEnum = resourceBundle.getKeys();
        LinkedList<String> allAllowedRightsKeyList = new LinkedList<>();

        //get all possible keys
        while (aEnum.hasMoreElements()) {
            String nextElement = aEnum.nextElement();
            if (nextElement.startsWith("UserRight.")) {
                //need remove first 2 elements ex: UserRight.Admin.admin.delete --> admin.delete
                String[] array = nextElement.split("\\.");
                array = java.util.Arrays.copyOfRange(array, 2, array.length);
                String key = StringUtils.join(array, ".");

                if(AgnUtils.allowed(key, AgnUtils.getAdmin(req))){
                    allAllowedRightsKeyList.add(nextElement);
                }
            }
        }

        //get existing data
        Set<String> newRightsKeys = aForm.getUserRights();
        Set<String> oldRightsKeys = admin.getAdminPermissions();
        //get added permissions keys
        Set<String> addedRightsKeys = new HashSet<>(newRightsKeys);
        addedRightsKeys.removeAll(oldRightsKeys);
        //get removed permissions keys
        Set<String> removedRightsKeys = new HashSet<>(oldRightsKeys);
        removedRightsKeys.removeAll(newRightsKeys);

        Map<String, List<String>> allAllowedRightsMap = AgnUtils.createUserRightsMap(allAllowedRightsKeyList);

        //Log Added permissions:

        //Create map for added Rights:
        Map<String, List<String>> addedRightsMap = AgnUtils.createUserRightsMap(allAllowedRightsKeyList);

        //remove added rights from full map:
        for(String rightKey : addedRightsKeys){
            for (List<String> group : addedRightsMap.values()) {
                group.remove(rightKey);
            }
        }

        // log all added rights groups
        for (String group : addedRightsMap.keySet()) {
            if(addedRightsMap.get(group).isEmpty()){
                String groupName = resourceBundle.getString(group);
                writeUserActivityLog(AgnUtils.getAdmin(req), "edit user " + admin.getUsername(), "Added group of permissions: " + groupName);
            }
        }

        //log all added permissions from partially changed groups
        for (String group : addedRightsMap.keySet()) {
            if(!addedRightsMap.get(group).isEmpty()){
                //get full group
                ArrayList<String>addedRightsFromGroup = new ArrayList<String>(allAllowedRightsMap.get(group));
                //get added part of group
                addedRightsFromGroup.removeAll(addedRightsMap.get(group));

                for (String rightKey : addedRightsFromGroup){

                    for (String key: allAllowedRightsKeyList){
                        if (key.contains(rightKey)){
                            String addedRightName = resourceBundle.getString(key);
                            String groupName = resourceBundle.getString(group);
                            writeUserActivityLog(AgnUtils.getAdmin(req), "edit user " + admin.getUsername(),
                                    "Group " + groupName + " : added permission: " + addedRightName);
                            break;
                        }
                    }
                }
            }
        }

        //Log Removed permissions:

        //Create map for removed Rights:
        Map<String, List<String>> removedRightsMap = AgnUtils.createUserRightsMap(allAllowedRightsKeyList);

        //remove removed rights from full map:
        for(String rightKey : removedRightsKeys){
            for (List<String> group : removedRightsMap.values()) {
                group.remove(rightKey);
            }
        }

        // log all removed rights groups
        for (String group : removedRightsMap.keySet()) {
            if(removedRightsMap.get(group).isEmpty()){
                String groupName = resourceBundle.getString(group);
                writeUserActivityLog(AgnUtils.getAdmin(req), "edit user " + admin.getUsername(), "Removed group of permissions: " + groupName);
            }
        }

        //log all removed permissions from partially changed groups
        for (String group : removedRightsMap.keySet()) {
            if(!removedRightsMap.get(group).isEmpty()){
                //get full group
                ArrayList<String>removedRightsFromGroup = new ArrayList<String>(allAllowedRightsMap.get(group));
                //get added part of group
                removedRightsFromGroup.removeAll(removedRightsMap.get(group));

                for (String rightKey : removedRightsFromGroup){

                    for (String key: allAllowedRightsKeyList){
                        if (key.contains(rightKey)){
                            String removedRightName = resourceBundle.getString(key);
                            String groupName = resourceBundle.getString(group);
                            writeUserActivityLog(AgnUtils.getAdmin(req), "edit user " + admin.getUsername(),
                                    "Group " + groupName + " : removed permission: " + removedRightName);
                            break;
                        }
                    }
                }
            }
        }

        //Save Rights:
        adminDao.saveAdminRights(aForm.getAdminID(), aForm.getUserRights());

		if (logger.isInfoEnabled()) logger.info("saveAdminRights: permissions changed");
	}

	/**
	 * Deletes an admin from the database. Also deletes all the admin permissions.
	 * 
	 * @param aForm
	 *            the formular passed from the jsp
	 * @param req
	 *            the Servlet Request (needed to get the company id)
	 */
	protected void deleteAdmin(AdminForm aForm, HttpServletRequest req) {
		int adminID = aForm.getAdminID();
		int companyID = AgnUtils.getCompanyID(req);
		Admin admin = adminDao.getAdmin(adminID, companyID);
		String username = admin != null ? admin.getUsername() : aForm.getUsername();
		adminDao.delete(admin);
        adminPreferencesDao.delete(admin.getAdminID());
        writeUserActivityLog(AgnUtils.getAdmin(req), "delete user " + username, "User deleted");
        if (logger.isInfoEnabled()){
            logger.info("Admin " + adminID + " deleted");
        }
	}

    /**
     * Creates Future object contains list of admins.
     * @param mapping the ActionMapping used to select this instance
     * @param req  HTTP request
     * @param errors ActionMessages object contains error messages, could be changed inside the method
     * @param destination specified in struts-config.xml to forward to next jsp
     * @param adminForm  AdminForm object
     * @return action forward for displaying page with admin list or loading admin page, if the list is not prepared yet
     */
	protected ActionForward prepareList(ActionMapping mapping, HttpServletRequest req, ActionMessages errors, ActionForward destination, AdminForm adminForm) {
		ActionMessages messages = null;

		try {
            if (!adminForm.isNumberOfRowsChanged()) {
                setNumberOfRows(req, adminForm);
            }
			destination = mapping.findForward("loading");
			String key = FUTURE_TASK + "@" + req.getSession(false).getId();
			if (!futureHolder.containsKey(key)) {
				Future<PaginatedListImpl<AdminEntry>> adminFuture = getAdminlistFuture(adminDao, req, adminForm);
				futureHolder.put(key, adminFuture);
			}

			if (futureHolder.containsKey(key) && futureHolder.get(key).isDone()) {
				req.setAttribute("adminEntries", futureHolder.get(key).get());
				destination = mapping.findForward("list");
				futureHolder.remove(key);
				adminForm.setRefreshMillis(RecipientForm.DEFAULT_REFRESH_MILLIS);
				messages = adminForm.getMessages();

                if(futureHolder.isEmpty()) {
                    adminForm.setNumberOfRowsChanged(false);
                }

				if (messages != null && !messages.isEmpty()) {
					saveMessages(req, messages);
					adminForm.setMessages(null);
				}
			} else {
				// raise the refresh time
				if (adminForm.getRefreshMillis() < 1000) { 
					adminForm.setRefreshMillis(adminForm.getRefreshMillis() + 50);
				}
				adminForm.setError(false);
			}
		} catch (Exception e) {
			logger.error("admin: " + e, e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception", configService.getValue(ConfigService.Value.SupportEmergencyUrl)));
			// do not refresh when an error has been occurred
			adminForm.setError(true);
		}
		return destination;
	}

    /**
     * Gets list of admins from database according to given sorting parameters.
     * @param adminDao AdminDao object
     * @param request  HTTP request
     * @param aForm  AdminForm object
     * @return Future object contains admin list
     * @throws NumberFormatException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InterruptedException
     * @throws ExecutionException
     */
	protected Future<PaginatedListImpl<AdminEntry>> getAdminlistFuture(AdminDao adminDao, HttpServletRequest request, StrutsFormBase aForm)
			throws NumberFormatException, IllegalAccessException, InstantiationException, InterruptedException, ExecutionException {
		int rownums = aForm.getNumberOfRows();

		String direction = request.getParameter("dir");
        if(direction == null){
            direction = aForm.getOrder();
        }
        if (direction.isEmpty()) {
            direction = request.getSession().getAttribute("admin_dir") == null ? "" : (String) request.getSession().getAttribute("admin_dir");
        }
        request.getSession().setAttribute("admin_dir",direction);

		String sort = request.getParameter("sort");
		if (sort == null) {
			sort = request.getSession().getAttribute("admin_sort") == null ? "" : (String) request.getSession().getAttribute("admin_sort");
		} else {
			request.getSession().setAttribute("admin_sort", sort);
		}

		String pageStr = request.getParameter("page");
		if (pageStr == null || "".equals(pageStr.trim())) {
			pageStr = request.getSession().getAttribute("admin_page") == null ? "1" : (String) request.getSession().getAttribute("admin_page");
		} else {
			request.getSession().setAttribute("admin_page", pageStr);
		}

		if (aForm.isNumberOfRowsChanged()) {
			aForm.setPage("1");
			request.getSession().setAttribute("admin_page", "1");
			pageStr = "1";
		}

		int companyID = AgnUtils.getCompanyID(request);

		Future<PaginatedListImpl<AdminEntry>> future = executorService.submit(new AdminListQueryWorker(adminDao, companyID, sort, direction, Integer.parseInt(pageStr), rownums));

		return future;
	}

	/**
	 * Method checks if admin with entered username already exists in system.
	 * 
	 * @param aForm
	 *            form
	 * @return true if admin already exists, false otherwise
	 */
	protected boolean adminExists(AdminForm aForm) {
		return adminDao.adminExists(aForm.getCompanyID(), aForm.getUsername());
	}

	/**
	 * Method checks if username was changed to existing one.
	 * 
	 * @param aForm
	 *            the form
	 * @return true if username was changed to existing one; false - if the
	 *         username was changed to none-existing or if the username was not
	 *         changed at all
	 */
	protected boolean adminUsernameChangedToExisting(AdminForm aForm) {
		Admin currentAdmin = adminDao.getAdmin(aForm.getAdminID(), aForm.getCompanyID());
		if (currentAdmin.getUsername().equals(aForm.getUsername())) {
			return false;
		} else {
			return adminDao.adminExists(aForm.getCompanyID(), aForm.getUsername());
		}
	}

    /**
     * Method compare OpenEMM specific fields of user and write log if they changed
     *
     * @param aForm - new data for front-end
     * @param admin - current data
     *
     */
    protected void writeOpenEmmSpecificUserChangesLog(AdminForm aForm, Admin admin) {

        try {
            String userName = admin.getUsername();
            if(!(admin.getFullname().equals(aForm.getFullname()))){
                writeUserActivityLog(admin, "edit user " + userName,
                        "Name changed from " + admin.getFullname() + " to " + aForm.getFullname());
            }
            if(!(userName.equals(aForm.getUsername()))){
                writeUserActivityLog(admin, "edit user " + userName,
                        "Login user name changed from " + userName + " to " + aForm.getUsername());
            }

            if(passwordChanged(admin.getUsername(), aForm.getPassword())){
                writeUserActivityLog(admin, "edit user " + userName, "Password changed");
            }
            if(!(admin.getAdminLang().equals(aForm.getAdminLocale().getLanguage()))){
                writeUserActivityLog(admin, "edit user " + userName,
                        "Language changed from " + Locale.forLanguageTag(admin.getAdminLang()).getDisplayLanguage() +
                                " to " + Locale.forLanguageTag(aForm.getAdminLocale().getLanguage()).getDisplayLanguage());
            }
            if(!(admin.getAdminTimezone().equals(aForm.getAdminTimezone()))){
                writeUserActivityLog(admin, "edit user " + userName,
                        "Timezone changed from " + admin.getAdminTimezone() + " to " + aForm.getAdminTimezone());
            }

            if(admin.getGroup().getGroupID() != aForm.getGroupID()){
                String oldGroupName = admin.getGroup().getGroupID() == 0 ? "None" : admin.getGroup().getShortname();
                String newGroupName = aForm.getGroupID() == 0 ? "None" : adminGroupDao.getAdminGroup(aForm.getGroupID()).getShortname();

                writeUserActivityLog(admin, "edit user " + userName,
                        "User Group changed from " + oldGroupName + " to " + newGroupName);
            }

            if (logger.isInfoEnabled()){
                logger.info("saveOpenEmmUser: save user " + aForm.getAdminID());
            }
        } catch (Exception e) {
            logger.error("Log OpenEMM User changes error" + e);
        }
    }

    /**
     * Method compare OpenEMM specific preferences fields of user and write log if they changed
     *
     * @param aForm - new data for front-end
     * @param admin - current data
     * @param adminPreferences - current admin preferences
     *
     */
    protected void writeOpenEmmSpecificUserPreferencesChangesLog(AdminForm aForm, Admin admin, AdminPreferences adminPreferences) {

        try {
            String userName = admin.getUsername();

            if(adminPreferences.getListSize() != aForm.getNumberOfRows()){
                writeUserActivityLog(admin, "edit user " + userName,
                        "List length changed from " + adminPreferences.getListSize() + " to " + aForm.getNumberOfRows());
            }

            int oldMailingContentView = adminPreferences.getMailingContentView();
            int newMailingContentView = aForm.getMailingContentView();

            if(oldMailingContentView != newMailingContentView){
                writeUserActivityLog(admin, "edit user " + userName,
                        "User mailing content view type changed from " + getMailingContentViewName(oldMailingContentView) +
                                " to " + getMailingContentViewName(newMailingContentView));
            }

            int oldMailingSettingsView = adminPreferences.getMailingSettingsView();
            int newMailingSettingsView = aForm.getMailingSettingsView();

            if(oldMailingSettingsView != newMailingSettingsView){
                writeUserActivityLog(admin, "edit user " + userName,
                        "User mailing settings view type changed from " + getMailingSettingsViewName(oldMailingSettingsView) +
                                " to " + getMailingSettingsViewName(newMailingSettingsView));
            }

            if (logger.isInfoEnabled()){
                logger.info("saveOpenEmmUser: save user preferences " + aForm.getAdminID());
            }
        } catch (Exception e) {
            logger.error("Log OpenEMM User preferences changes error" + e);
        }
    }




	public void setAdminDao(AdminDao adminDao) {
		this.adminDao = adminDao;
	}

	@Required
	public void setAdminGroupDao(AdminGroupDao adminGroupDao) {
		this.adminGroupDao = adminGroupDao;
	}

    @Required
    public void setAdminPreferencesDao(AdminPreferencesDao adminPreferencesDao) {
        this.adminPreferencesDao = adminPreferencesDao;
    }

    public void setCompanyDao(CompanyDao companyDao) {
		this.companyDao = companyDao;
	}

	public void setFutureHolder(ConcurrentHashMap<String, Future<PaginatedListImpl<AdminEntry>>> futureHolder) {
		this.futureHolder = futureHolder;
	}

	@Required
	public void setExecutorService(ScheduledThreadPoolExecutor executorService) {
		this.executorService = executorService;
	}

	@Required
	public void setConfigService(ConfigService configService) {
		this.configService = configService;
	}
	
	@Required
	public void setPasswordCheck(PasswordCheck check) {
		this.passwordCheck = check;
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
