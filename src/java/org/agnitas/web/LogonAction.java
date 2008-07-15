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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.agnitas.beans.Admin;
import org.agnitas.beans.EmmLayout;
import org.agnitas.dao.AdminDao;
import org.agnitas.util.AgnUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;


/**
 * Implementation of <strong>Action</strong> that validates a user logon.
 *
 * @author Martin Helff
 */

public final class LogonAction extends StrutsActionBase {
    
    public static final int ACTION_LOGON = 1;
    
    public static final int ACTION_LOGOFF = 2;
   
    
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
     * @return destination
     */
    public ActionForward execute(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest req,
            HttpServletResponse res)
            throws IOException, ServletException {
        
        // Validate the request parameters specified by the user
        ActionMessages errors = new ActionMessages();
        LogonForm aForm=(LogonForm)form;
        ActionForward destination=null;
        
        try {
            AgnUtils.logger().info("execute: action " + aForm.getAction());
            switch(aForm.getAction()) {
                
                case LogonAction.ACTION_LOGON:
                    logon(aForm, req, errors);
                    destination=mapping.findForward("success");
                    // end case LogonAction.ACTION_LOGON:
                    break;
                    
                case LogonAction.ACTION_LOGOFF:
                    AgnUtils.logger().info("execute: logoff");
                    logoff(aForm, req);
                    // setLayout(aForm, dbConn, req);
                    destination=mapping.findForward("view_logon");
                    aForm.setAction(LogonAction.ACTION_LOGON);
                    break;
                                        
                default:
                    AgnUtils.logger().debug("execute: default");
                    aForm.setAction(LogonAction.ACTION_LOGON);
                    destination=mapping.findForward("view_logon");
            }
            
        } catch (Exception e) {
            AgnUtils.logger().error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        }
        
        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            
            saveErrors(req, errors);
            return new ActionForward(mapping.getInput());
        }  
        return destination;
    }
    
    /**
     * Tries to logon a user.
     */
    protected void logon(LogonForm aForm, HttpServletRequest req, ActionMessages errors) {
        
        HttpSession session=req.getSession();
        AdminDao adminDao=(AdminDao) getBean("AdminDao");
        Admin aAdmin=adminDao.getAdminByLogin(aForm.getUsername(), aForm.getPassword());
        
        if(aAdmin!=null) {
            session.setAttribute("emm.admin", aAdmin);
            
            session.setAttribute("emm.layout", (EmmLayout)AgnUtils.getFirstResult(this.getHibernateTemplate().find("from EmmLayout where layoutID=?", new Object [] {new Integer(aAdmin.getLayoutID())})));
            session.setAttribute("emm.locale", aAdmin.getLocale());
            session.setAttribute(org.apache.struts.Globals.LOCALE_KEY, aAdmin.getLocale());
        } else {
            AgnUtils.logger().warn("logon: login FAILED User: " + aForm.getUsername() + " Password-Length: " + aForm.getPassword().length());
            errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("error.login"));
        }
        return;
    }
    
    /**
     * Logs off a user
     */
    protected void logoff(LogonForm aForm, HttpServletRequest req) {
        AgnUtils.logger().info("logoff: logout "+aForm.getUsername()+"!");
        req.getSession().removeAttribute("emm.admin");
        req.getSession().invalidate();
        return;
    }    
}
