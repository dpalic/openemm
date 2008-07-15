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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.beans.Admin;
import org.agnitas.beans.AdminGroup;
import org.agnitas.dao.AdminDao;
import org.agnitas.dao.AdminGroupDao;
import org.agnitas.dao.CompanyDao;
import org.agnitas.util.AgnUtils;
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

public final class AdminAction extends StrutsActionBase {

    public static final int ACTION_VIEW_RIGHTS    = ACTION_LAST+1;
    public static final int ACTION_SAVE_RIGHTS    = ACTION_LAST+2;

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

        ApplicationContext aContext=this.getWebApplicationContext();
        AdminForm aForm=null;
        ActionMessages errors = new ActionMessages();
        ActionForward destination=null;

        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }

        if(form!=null) {
            aForm=(AdminForm)form;
        } else {
            aForm=new AdminForm();
        }

        AgnUtils.logger().info("Action: "+aForm.getAction());
        if(req.getParameter("delete.x")!=null) {
            aForm.setAction(ACTION_CONFIRM_DELETE);
        }

        try {
            switch(aForm.getAction()) {
                case AdminAction.ACTION_LIST:
                    if(allowed("admin.show", req)) {
                        destination=mapping.findForward("list");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case AdminAction.ACTION_VIEW:
                    if(allowed("admin.show", req)) {
                        if(aForm.getAdminID()!=0) {

                            aForm.setAction(AdminAction.ACTION_SAVE);
                            loadAdmin(aForm, aContext, req);
                        } else {
                            aForm.setAction(AdminAction.ACTION_NEW);
                        }
                        destination=mapping.findForward("view");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case AdminAction.ACTION_SAVE:
                    if(allowed("admin.change", req)) {
                        if(req.getParameter("save.x")!=null) {
                            saveAdmin(aForm, aContext, req);
                        }
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    destination=mapping.findForward("view");
                    break;

                case AdminAction.ACTION_VIEW_RIGHTS:
                    loadAdmin(aForm, aContext, req);
                    aForm.setAction(AdminAction.ACTION_SAVE_RIGHTS);
                    destination=mapping.findForward("rights");
                    break;

                case AdminAction.ACTION_SAVE_RIGHTS:
                    saveAdminRights(aForm, aContext, req);
                    loadAdmin(aForm, aContext, req);
                    aForm.setAction(AdminAction.ACTION_SAVE_RIGHTS);
                    destination=mapping.findForward("rights");
                    break;

                case AdminAction.ACTION_NEW:
                    if(allowed("admin.new", req)) {
                        if(req.getParameter("save.x")!=null) {
                            aForm.setAdminID(0);
                            try {
                                saveAdmin(aForm, aContext, req);
                            } catch(Exception e) {
                                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.admin.save"));
                            }
                            aForm.setAction(AdminAction.ACTION_LIST);
                            destination=mapping.findForward("list");
                        }
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case AdminAction.ACTION_CONFIRM_DELETE:
                    loadAdmin(aForm, aContext, req);
                    aForm.setAction(AdminAction.ACTION_DELETE);
                    destination=mapping.findForward("delete");
                    break;

                case AdminAction.ACTION_DELETE:
                    if(req.getParameter("kill.x")!=null) {
                        if(allowed("admin.delete", req)) {
                            deleteAdmin(aForm, aContext, req);
                        } else {
                            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                        }
                        aForm.setAction(AdminAction.ACTION_LIST);
                        destination=mapping.findForward("list");
                    }
                    break;

                default:
                    aForm.setAction(AdminAction.ACTION_LIST);
                    destination=mapping.findForward("list");
            }


        } catch (Exception e) {
            AgnUtils.logger().error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
            throw new ServletException(e);
        }

        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(req, errors);
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
        int adminID=aForm.getAdminID();
        int compID = getCompanyID(req);
        AdminDao adminDao=(AdminDao) getBean("AdminDao");
        Admin admin=adminDao.getAdmin(adminID, compID);

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
            AgnUtils.logger().info("loadAdmin: admin "+aForm.getAdminID()+" loaded");
        } else {
            aForm.setAdminID(0);
            aForm.setCompanyID(this.getCompanyID(req));
            AgnUtils.logger().warn("loadAdmin: admin "+aForm.getAdminID()+" could not be loaded");
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
        HibernateTemplate tmpl=getHibernateTemplate();
        int adminID = aForm.getAdminID();
        int compID = aForm.getCompanyID();
        int groupID = aForm.getGroupID();
        AdminDao adminDao=(AdminDao) getBean("AdminDao");
        Admin admin=adminDao.getAdmin(adminID, compID);

System.err.println("Saving to Companyid: "+compID);
        if(admin == null) {
            CompanyDao companyDao=(CompanyDao) getBean("CompanyDao");

            admin=(Admin) getBean("Admin");
            admin.setCompanyID(compID);
            admin.setCompany(companyDao.getCompany(compID));
            admin.setLayoutID(0);
        }

        AdminGroupDao groupDao=(AdminGroupDao) getBean("AdminGroupDao");
        AdminGroup group=(AdminGroup) groupDao.getAdminGroup(groupID);

        admin.setAdminID(aForm.getAdminID());
        admin.setUsername(aForm.getUsername());
        if(aForm.getPassword()!=null && aForm.getPassword().trim().length()!=0) {
            admin.setPassword(aForm.getPassword());
        }
        AgnUtils.logger().error("Username: "+aForm.getUsername()+" Password: "+aForm.getPassword());

        admin.setFullname(aForm.getFullname());
        admin.setAdminCountry(aForm.getAdminLocale().getCountry());
        admin.setAdminLang(aForm.getAdminLocale().getLanguage());
        admin.setAdminTimezone(aForm.getAdminTimezone());
        admin.setGroup(group);

        tmpl.saveOrUpdate("Admin", admin);
        tmpl.flush();
        AgnUtils.logger().info("saveAdmin: admin "+aForm.getAdminID());
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
        AdminDao adminDao=(AdminDao) getBean("AdminDao");
        Admin admin=adminDao.getAdmin(adminID, compID);

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
        int compID = getCompanyID(req);
        AdminDao adminDao=(AdminDao) getBean("AdminDao");
        Admin admin=adminDao.getAdmin(adminID, compID);

        if(admin != null) {
            getHibernateTemplate().delete(admin);
            getHibernateTemplate().flush();
        }
        AgnUtils.logger().info("Admin " + adminID + " deleted");
    }
}
