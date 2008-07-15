/*********************************************************************************
 * The contents of this file are subject to the OpenEMM Public License Version 1.1
 * ("License"); You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.agnitas.org/openemm.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is OpenEMM.
 * The Initial Developer of the Original Code is AGNITAS AG. Portions created by
 * AGNITAS AG are Copyright (C) 2006 AGNITAS AG. All Rights Reserved.
 *
 * All copies of the Covered Code must include on each user interface screen,
 * visible to all users at all times
 *    (a) the OpenEMM logo in the upper left corner and
 *    (b) the OpenEMM copyright notice at the very bottom center
 * See full license, exhibit B for requirements.
 ********************************************************************************/

package org.agnitas.web;

import org.agnitas.util.*;
import org.agnitas.beans.*;
import org.agnitas.dao.*;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.struts.action.*;
import org.apache.struts.util.*;
import org.apache.struts.upload.*;


/**
 * Implementation of <strong>Action</strong> that handle input of user forms.
 *
 * @author mhe
 */

public final class UserFormEditAction extends StrutsActionBase {
    
    // --------------------------------------------------------- Public Methods
    
    
    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     * 
     * @param form 
     * @param req 
     * @param res 
     * @param mapping The ActionMapping used to select this instance
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     * @return the action to forward to. 
     */
    public ActionForward execute(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest req,
            HttpServletResponse res)
            throws IOException, ServletException {
        
        // Validate the request parameters specified by the user
        UserFormEditForm aForm=null;
        ActionMessages errors = new ActionMessages();
        ActionForward destination=null;
        
        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }
        
        aForm=(UserFormEditForm)form;
        AgnUtils.logger().info("Action: "+aForm.getAction());
        
        try {
            switch(aForm.getAction()) {
                case UserFormEditAction.ACTION_LIST:
                    if(allowed("forms.view", req)) {
                        destination=mapping.findForward("list");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
                    
                case UserFormEditAction.ACTION_VIEW:
                    if(allowed("forms.view", req)) {
                        if(aForm.getFormID()!=0) {
                            loadUserForm(aForm, req);
                        }
                        aForm.setAction(UserFormEditAction.ACTION_SAVE);
                        destination=mapping.findForward("view");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
                    
                case UserFormEditAction.ACTION_SAVE:
                    if(allowed("forms.change", req)) {
                        destination=mapping.findForward("view");
                        saveUserForm(aForm, req);
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
                    
                case UserFormEditAction.ACTION_CONFIRM_DELETE:
                    if(allowed("forms.delete", req)) {
                        loadUserForm(aForm, req);
                        aForm.setAction(UserFormEditAction.ACTION_DELETE);
                        destination=mapping.findForward("delete");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
                    
                case UserFormEditAction.ACTION_DELETE:
                    if(allowed("forms.delete", req)) {
                        if(req.getParameter("delete.x")!=null) {
                            deleteUserForm(aForm, req);
                        }
                        aForm.setAction(UserFormEditAction.ACTION_LIST);
                        destination=mapping.findForward("list");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
                    
                default:
                    aForm.setAction(UserFormEditAction.ACTION_LIST);
                    destination=mapping.findForward("list");
            }
        } catch (Exception e) {
            AgnUtils.logger().error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        }
        
        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(req, errors);
        }
        
        return destination;
        
    }
   
    /**
     * Load a user form.
     * Retrieves the data of a form from the database.
     * @param aForm on input contains the id of the form.
     *              On exit contains the data read from the database. 
     * @param req used to get the ApplicationContext.
     */ 
    protected void loadUserForm(UserFormEditForm aForm, HttpServletRequest req) {
        UserFormDao dao=(UserFormDao) getBean("UserFormDao");
        
        UserForm aUserForm=dao.getUserForm(aForm.getFormID(), this.getCompanyID(req));
        
        if(aUserForm!=null && aUserForm.getId()!=0) {
            aForm.setFormName(aUserForm.getFormName());
            aForm.setDescription(aUserForm.getDescription());
            aForm.setStartActionID(aUserForm.getStartActionID());
            aForm.setEndActionID(aUserForm.getEndActionID());
            aForm.setSuccessTemplate(aUserForm.getSuccessTemplate());
            aForm.setErrorTemplate(aUserForm.getErrorTemplate());
            AgnUtils.logger().info("loadUserForm: form "+aForm.getFormID()+" loaded");
        } else {
            AgnUtils.logger().warn("loadUserForm: could not load userform "+aForm.getFormID());  
        }
        
        return;
    }
    
    /**
     * Save a user form.
     * Writes the data of a form to the database.
     * @param aForm contains the data of the form. 
     * @param req used to get the ApplicationContext.
     */ 
    protected void saveUserForm(UserFormEditForm aForm, HttpServletRequest req) {
        UserFormDao dao=(UserFormDao) getBean("UserFormDao");
        UserForm aUserForm=(UserForm) getBean("UserForm");
        
        aUserForm.setCompanyID(this.getCompanyID(req));
        aUserForm.setId(aForm.getFormID());
        aUserForm.setFormName(aForm.getFormName());
        aUserForm.setDescription(aForm.getDescription());
        aUserForm.setStartActionID(aForm.getStartActionID());
        aUserForm.setEndActionID(aForm.getEndActionID());
        aUserForm.setSuccessTemplate(aForm.getSuccessTemplate());
        aUserForm.setErrorTemplate(aForm.getErrorTemplate());
        
        aForm.setFormID(dao.saveUserForm(aUserForm));
        
        return;
    }
    
    /**
     * Delete a user form.
     * Removes the data of a form from the database.
     * @param aForm contains the id of the form. 
     * @param req used to get the ApplicationContext.
     */ 
    protected void deleteUserForm(UserFormEditForm aForm, HttpServletRequest req) {
        UserFormDao dao=(UserFormDao) getBean("UserFormDao");

        dao.deleteUserForm(aForm.getFormID(), getCompanyID(req));
        return;
    }
}
