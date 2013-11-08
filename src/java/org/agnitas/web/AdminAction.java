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
import java.util.Locale;
import java.util.AbstractMap;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.beans.Admin;
import org.agnitas.beans.AdminGroup;
import org.agnitas.dao.AdminDao;
import org.agnitas.dao.AdminGroupDao;
import org.agnitas.dao.CompanyDao;
import org.agnitas.dao.ProfileFieldDao;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.forms.AdminForm;
import org.agnitas.web.forms.StrutsFormBase;
import org.agnitas.service.ProfilefieldListQueryWorker;
import org.agnitas.service.AdminListQueryWorker;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 * Implementation of <strong>Action</strong> that handles Account Admins
 *
 * @author Andreas Rehak, Martin Helff
 */

public class AdminAction extends StrutsActionBase {

    public static final int ACTION_VIEW_RIGHTS    = ACTION_LAST+1;
    public static final int ACTION_SAVE_RIGHTS    = ACTION_LAST+2;
    private static final String FUTURE_TASK = "GET_ADMIN_LIST";

    // ---------------------------------------- Public Methods


    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form
     * @param req
     * @param res
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     * @return destination
     */
    public ActionForward execute(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest req,
            HttpServletResponse res)
            throws IOException, ServletException {

        ApplicationContext aContext = this.getWebApplicationContext();
        AdminForm aForm = null;
        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();
        ActionForward destination = null;

        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }

        if(form != null) {
            aForm = (AdminForm)form;
        } else {
            aForm = new AdminForm();
        }

        AgnUtils.logger().info("Action: " + aForm.getAction());
        if (req.getParameter("delete") != null && req.getParameter("delete").equals("delete")) {
            aForm.setAction(ACTION_CONFIRM_DELETE);
        }

        try {
            switch(aForm.getAction()) {
                case AdminAction.ACTION_LIST:
                    if(allowed("admin.show", req)) {
                        destination = prepareList(mapping, req, errors, destination, aForm);
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case AdminAction.ACTION_VIEW:
                    if(allowed("admin.show", req)) {
                        if(aForm.getAdminID() != 0) {
                            aForm.setAction(AdminAction.ACTION_SAVE);
                            loadAdmin(aForm, aContext, req);
                        } else {
                            aForm.setAction(AdminAction.ACTION_NEW);
                        }
                        destination = mapping.findForward("view");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case AdminAction.ACTION_SAVE:
                    if(allowed("admin.change", req)) {
                        if(AgnUtils.parameterNotEmpty(req, "save")) {
                            if (!adminUsernameChangedToExisting(aForm)) {
                                saveAdmin(aForm, aContext, req);

                                // Show "changes saved"
                                messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
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
                    loadAdmin(aForm, aContext, req);
                    aForm.setAction(AdminAction.ACTION_SAVE_RIGHTS);
                    destination = mapping.findForward("rights");
                    break;

                case AdminAction.ACTION_SAVE_RIGHTS:
                    saveAdminRights(aForm, aContext, req);
                    loadAdmin(aForm, aContext, req);
                    aForm.setAction(AdminAction.ACTION_SAVE_RIGHTS);
                    destination = mapping.findForward("rights");

                    // Show "changes saved"
                    messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
                    break;

                case AdminAction.ACTION_NEW:
                    if(allowed("admin.new", req)) {
                        if(AgnUtils.parameterNotEmpty(req, "save")) {
                            aForm.setAdminID(0);

                            if(aForm.getPassword().length() > 0) {
                                if (!adminExists(aForm)) {
                                    try {
                                        saveAdmin(aForm, aContext, req);

                                        // Show "changes saved"
                                        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));

                                        destination = prepareList(mapping, req, errors, destination, aForm);
                                        aForm.setAction(AdminAction.ACTION_LIST);
                                    } catch (Exception e) {
                                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.admin.save"));
                                        destination = mapping.findForward("view");
                                        aForm.setAction(AdminAction.ACTION_NEW);
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
                    loadAdmin(aForm, aContext, req);
                    aForm.setAction(AdminAction.ACTION_DELETE);
                    destination = mapping.findForward("delete");
                    break;

                case AdminAction.ACTION_DELETE:
                    if(req.getParameter("kill") != null) {
                        if(allowed("admin.delete", req)) {
                            deleteAdmin(aForm, aContext, req);

                            // Show "changes saved"
                            messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
                        } else {
                            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                        }
                        aForm.setAction(AdminAction.ACTION_LIST);
                        destination = prepareList(mapping, req, errors, destination, aForm);
                    }
                    break;

                default:
                    aForm.setAction(AdminAction.ACTION_LIST);
                    destination = prepareList(mapping, req, errors, destination, aForm);
            }
        } catch (Exception e) {
            AgnUtils.logger().error("execute: " + e + "\n" + AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
            throw new ServletException(e);
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
     * Load an admin account.
     * Loads the data of the admin from the database and stores it in the
     * form.
     *
     * @param aForm the formular passed from the jsp
     * @param aContext the ApplicationContext (unused)
     * @param req the Servlet Request (needed to get the company id)
     */
    protected void loadAdmin(AdminForm aForm, ApplicationContext aContext, HttpServletRequest req) {
        int adminID = aForm.getAdminID();
        int compID = getCompanyID(req);
        AdminDao adminDao=(AdminDao) getBean("AdminDao");
        Admin admin = adminDao.getAdmin(adminID, compID);

        if(admin != null) {
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
            aForm.setNumberofRows(admin.getPreferredListSize());
            AgnUtils.logger().info("loadAdmin: admin " + aForm.getAdminID() + " loaded");
        } else {
            aForm.setAdminID(0);
            aForm.setCompanyID(this.getCompanyID(req));
            AgnUtils.logger().warn("loadAdmin: admin " + aForm.getAdminID() + " could not be loaded");
        }
    }

    /**
     * Save an admin account.
     * Gets the admin data from a form and stores it in the database.
     *
     * @param aForm the formular passed from the jsp
     * @param aContext the ApplicationContext (unused)
     * @param req the Servlet Request (needed to get the company id)
     */
    protected void saveAdmin(AdminForm aForm, ApplicationContext aContext, HttpServletRequest req) {
        HibernateTemplate tmpl = getHibernateTemplate();
        int adminID = aForm.getAdminID();
        int compID = aForm.getCompanyID();
        int groupID = aForm.getGroupID();
        AdminDao adminDao = (AdminDao) getBean("AdminDao");
        Admin admin = adminDao.getAdmin(adminID, compID);
        boolean isNew=false;
        if(admin == null) {
            CompanyDao companyDao = (CompanyDao) getBean("CompanyDao");

            admin = (Admin) getBean("Admin");
            admin.setCompanyID(compID);
            admin.setCompany(companyDao.getCompany(compID));
            admin.setLayoutID(0);
            isNew=true;
        }

        AdminGroupDao groupDao = (AdminGroupDao) getBean("AdminGroupDao");
        AdminGroup group = (AdminGroup) groupDao.getAdminGroup(groupID);

        admin.setAdminID(aForm.getAdminID());
        admin.setUsername(aForm.getUsername());
        if(aForm.getPassword() != null && aForm.getPassword().trim().length() != 0) {
            admin.setPassword(aForm.getPassword());
        }

        if(aForm.getPassword().length() > 0) {
        	AgnUtils.logger().error("Username: " + aForm.getUsername() + " Password: " + aForm.getPassword().substring(0,1) + "...");
        } else {
        	AgnUtils.logger().error("Username: " + aForm.getUsername());
        }

        admin.setFullname(aForm.getFullname());
        admin.setAdminCountry(aForm.getAdminLocale().getCountry());
        admin.setAdminLang(aForm.getAdminLocale().getLanguage());
        admin.setAdminTimezone(aForm.getAdminTimezone());
        admin.setGroup(group);
        admin.setPreferredListSize(aForm.getNumberofRows());

        tmpl.saveOrUpdate("Admin", admin);
        tmpl.flush();
        if (isNew) {
            AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": create user " + admin.getUsername());
        } else {
            AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": edit user " + aForm.getUsername());
        }
        AgnUtils.logger().info("saveAdmin: admin " + aForm.getAdminID());
    }

    /**
     * Save the permission for an admin.
     * Gets the permissions for the admin from the form and stores it in the
     * database.
     *
     * @param aForm the formular passed from the jsp
     * @param aContext the ApplicationContext (unused)
     * @param req the Servlet Request (needed to get the company id)
     */
    protected void saveAdminRights(AdminForm aForm, ApplicationContext aContext, HttpServletRequest req) {
        int adminID = aForm.getAdminID();
        int compID = getCompanyID(req);
        AdminDao adminDao = (AdminDao) getBean("AdminDao");
        Admin admin = adminDao.getAdmin(adminID, compID);

        admin.setAdminPermissions(aForm.getUserRights());
        getHibernateTemplate().saveOrUpdate("Admin", admin);
        AgnUtils.logger().info("saveAdminRights: permissions changed");
    }

    /**
     * Delete an admin from the database.
     *
     * @param aForm the formular passed from the jsp
     * @param aContext the ApplicationContext (unused)
     * @param req the Servlet Request (needed to get the company id)
     */
    protected void deleteAdmin(AdminForm aForm, ApplicationContext aContext, HttpServletRequest req) {
        int adminID = aForm.getAdminID();
        int companyID = getCompanyID(req);
        AdminDao adminDao = (AdminDao) getBean("AdminDao");
        Admin admin = adminDao.getAdmin(adminID, companyID);
        String username = admin != null ? admin.getUsername() : aForm.getUsername();        
        adminDao.delete(adminID, companyID);
//
//
//        if(admin != null) {
//            getHibernateTemplate().delete(admin);
//            getHibernateTemplate().flush();
//        }
        AgnUtils.logger().info("Admin " + adminID + " deleted");
        AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": delete user " + username);
    }

   protected ActionForward prepareList(ActionMapping mapping,
			HttpServletRequest req, ActionMessages errors,
			ActionForward destination,AdminForm adminForm) {
        AdminDao dao = (AdminDao) getBean("AdminDao");
		ActionMessages messages = null;

		try {
			  setNumberOfRows(req, adminForm);
			   destination = mapping.findForward("loading");
			   AbstractMap<String,Future> futureHolder = (AbstractMap<String, Future>)getBean("futureHolder");
			   String key =  FUTURE_TASK+"@"+ req.getSession(false).getId();
			   if( ! futureHolder.containsKey(key) ) {
				   Future adminFuture = getAdminlistFuture(dao, req, getWebApplicationContext() , adminForm);
				   futureHolder.put(key,adminFuture);
			   }
			  if ( futureHolder.containsKey(key)  && futureHolder.get(key).isDone()) {
					req.setAttribute("adminEntries", futureHolder.get(key).get());
					destination = mapping.findForward("list");
					futureHolder.remove(key);
					adminForm.setRefreshMillis(RecipientForm.DEFAULT_REFRESH_MILLIS);
					messages = adminForm.getMessages();

					if(messages != null && !messages.isEmpty()) {
						saveMessages(req, messages);
						adminForm.setMessages(null);
					}
			  }
			  else {
					if( adminForm.getRefreshMillis() < 1000 ) { // raise the refresh time
				 	adminForm.setRefreshMillis( adminForm.getRefreshMillis() + 50 );
					}
					adminForm.setError(false);
				}
		}
		catch(Exception e){
				AgnUtils.logger().error("admin: " + e + "\n" + AgnUtils.getStackTrace(e));
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
				adminForm.setError(true); // do not refresh when an error has been occurred
			}
		return destination;
	}

    protected Future getAdminlistFuture(AdminDao adminDao, HttpServletRequest request, ApplicationContext aContext, StrutsFormBase aForm) throws NumberFormatException, IllegalAccessException, InstantiationException, InterruptedException, ExecutionException {

        String sort = getSort(request, aForm);
        String direction = request.getParameter("dir");

        int rownums = aForm.getNumberofRows();
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

        int companyID = AgnUtils.getCompanyID(request);

        ExecutorService service = (ExecutorService) aContext.getBean("workerExecutorService");
        Future future = service.submit(new AdminListQueryWorker(adminDao, companyID, sort, direction, Integer.parseInt(pageStr), rownums));

        return future;

    }

    /**
	 * Method checks if admin with entered username already exists in system
	 *
	 * @param aForm form
	 * @return true if admin already exists, false otherwise
	 */
	protected boolean adminExists(AdminForm aForm) {
		AdminDao adminDao = (AdminDao) getBean("AdminDao");
		return adminDao.adminExists(aForm.getCompanyID(), aForm.getUsername());
	}

    /**
	 * Method checks if username was changed to existing one
	 *
	 * @param aForm the form
	 * @return true if username was changed to existing one; false - if the username
	 * was changed to none-existing or if the username was not changed at all
	 */
	protected boolean adminUsernameChangedToExisting(AdminForm aForm) {
		AdminDao adminDao = (AdminDao) getBean("AdminDao");
		Admin currentAdmin = adminDao.getAdmin(aForm.getAdminID(), aForm.getCompanyID());
		if(currentAdmin.getUsername().equals(aForm.getUsername())) {
			return false;
		} else {
			return adminDao.adminExists(aForm.getCompanyID(), aForm.getUsername());
		}
	}

}
