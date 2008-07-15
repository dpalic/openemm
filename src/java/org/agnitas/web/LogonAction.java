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
import org.agnitas.dao.AdminDao;
import java.io.*;
import java.security.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.struts.action.*;
import org.apache.struts.util.*;
import org.agnitas.beans.*;


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
