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

import org.agnitas.dao.TargetDao;
import org.agnitas.target.Target;
import org.agnitas.util.AgnUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;


/**
 * Implementation of <strong>Action</strong> that handles Targets
 *
 * @author Martin Helff
 */

public class TargetAction extends StrutsActionBase {
    
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
        
        TargetForm aForm=null;
        ActionMessages errors = new ActionMessages();
        ActionForward destination=null;
        
        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }
        
        if(form!=null) {
            aForm=(TargetForm)form;
        } else {
            aForm=new TargetForm();
        }
                
        if(req.getParameter("delete.x")!=null) {
            aForm.setAction(TargetAction.ACTION_CONFIRM_DELETE);
        }
         
        AgnUtils.logger().info("Action: " + aForm.getAction());

        if(!allowed("targets.show", req)) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
            saveErrors(req, errors);
            return null;
        }

        try {
            switch(aForm.getAction()) {
                case TargetAction.ACTION_LIST:
                    destination=mapping.findForward("list");
                    break;
                    
                case TargetAction.ACTION_VIEW:
                    if(aForm.getTargetID()!=0) {
                        aForm.setAction(TargetAction.ACTION_SAVE);
                        loadTarget(aForm, req);
                    } else {
                        aForm.setAction(TargetAction.ACTION_NEW);
                    }
                    destination=mapping.findForward("success");
                    break;
                    
                case TargetAction.ACTION_SAVE:
                    //if(req.getParameter("save.x")!=null) {
                        saveTarget(aForm, req);
                    //}
                    destination=mapping.findForward("success");
                    break;
                    
                case TargetAction.ACTION_NEW:
                    //if(req.getParameter("save.x")!=null) {
                        saveTarget(aForm, req);
                        aForm.setAction(TargetAction.ACTION_SAVE);
                    //}
                    destination=mapping.findForward("success");
                    break;
                    
                case TargetAction.ACTION_CONFIRM_DELETE:
                    loadTarget(aForm, req);
                    destination=mapping.findForward("delete");
                    aForm.setAction(TargetAction.ACTION_DELETE);
                    break;
                    
                case TargetAction.ACTION_DELETE:
                    this.deleteTarget(aForm, req);
                    aForm.setAction(TargetAction.ACTION_LIST);
                    destination=mapping.findForward("list");
                    break;
                    
                default:
                    destination=mapping.findForward("list");
                    break;
            }
            
        } catch (Exception e) {
            AgnUtils.logger().error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        }
        
        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(req, errors);
            return (new ActionForward(mapping.getInput()));
        }
        
        return destination;
        
    }
    
    /**
     * Loads target.
     */
    protected void loadTarget(TargetForm aForm, HttpServletRequest req) throws Exception {
        TargetDao targetDao=(TargetDao) getBean("TargetDao"); 
        Target aTarget=targetDao.getTarget(aForm.getTargetID(), getCompanyID(req));
        
        if(aTarget.getId()!=0) {
            aForm.setShortname(aTarget.getTargetName());
            aForm.setDescription(aTarget.getTargetDescription());
            aForm.setTarget(aTarget.getTargetStructure());
            AgnUtils.logger().info("loadTarget: target "+aForm.getTargetID()+" loaded");
        } else {
            AgnUtils.logger().warn("loadTarget: could not load target "+aForm.getTargetID());
            
        }
        return;
    }
    
    /**
     * Saves target.
     */
    protected void saveTarget(TargetForm aForm, HttpServletRequest req) throws Exception {
        TargetDao targetDao=(TargetDao) getBean("TargetDao");
        Target aTarget=targetDao.getTarget(aForm.getTargetID(), getCompanyID(req));
        
        if(aTarget==null) {
            // be sure to use id 0 if there is no existing object
            aForm.setTargetID(0);
            
            aTarget=(Target) getBean("Target");
            aTarget.setCompanyID(this.getCompanyID(req));
        }
        
        aTarget.setTargetName(aForm.getShortname());
        aTarget.setTargetDescription(aForm.getDescription());
        aTarget.setTargetSQL(aForm.getTarget().generateSQL());
        aTarget.setTargetStructure(aForm.getTarget());

        getHibernateTemplate().saveOrUpdate("Target", aTarget);
        
        AgnUtils.logger().info("saveTarget: save target "+aTarget.getId());
        aForm.setTargetID(aTarget.getId());
        
        return;
    }
        
    /**
     * Removes target.
     */
    protected void deleteTarget(TargetForm aForm, HttpServletRequest req) {
        TargetDao targetDao=(TargetDao) getBean("TargetDao");
        Target aTarget=targetDao.getTarget(aForm.getTargetID(), getCompanyID(req));

        if(aTarget != null) {
            getHibernateTemplate().delete(aTarget);
            getHibernateTemplate().flush();
        }
        return;
    }
}
