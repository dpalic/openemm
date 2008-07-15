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
import java.io.IOException;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.struts.action.*;
import org.apache.struts.util.*;

import org.agnitas.beans.Mailinglist;
import org.agnitas.dao.*;

import org.springframework.jdbc.core.*;
import org.springframework.orm.hibernate3.*;




public final class MailinglistAction extends StrutsActionBase {
    
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
        
        MailinglistForm aForm=null;
        ActionMessages errors = new ActionMessages();
        ActionForward destination=null;
        
        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }
        
        if(form!=null) {
            aForm=(MailinglistForm)form;
        } else {
            aForm=new MailinglistForm();
        }
        
        
        AgnUtils.logger().info("Action: "+aForm.getAction());
        if(req.getParameter("delete.x")!=null) {
            aForm.setAction(this.ACTION_CONFIRM_DELETE);
        }
        
        try {
            switch(aForm.getAction()) {
                case MailinglistAction.ACTION_LIST:
                    if(allowed("mailinglist.show", req)) {
                        list(aForm, req);
                        destination=mapping.findForward("list");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
                    
                case MailinglistAction.ACTION_VIEW:
                    if(allowed("mailinglist.show", req)) {
                        loadMailinglist(aForm, req);
                        aForm.setAction(MailinglistAction.ACTION_SAVE);
                        destination=mapping.findForward("view");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
                    
                case MailinglistAction.ACTION_NEW:
                    if(allowed("mailinglist.new", req)) {
                        aForm.setMailinglistID(0);
                        aForm.setAction(MailinglistAction.ACTION_SAVE);
                        destination=mapping.findForward("view");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
                    
                    
                case MailinglistAction.ACTION_SAVE:
                    if(allowed("mailinglist.change", req)) {
                        if(req.getParameter("save.x")!=null) {
                            if(saveMailinglist(aForm, req)) {
                                list(aForm, req);
                                destination=mapping.findForward("list");
                            } else {
                                destination=mapping.findForward("view");
                            }
                        }
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
                    
                case MailinglistAction.ACTION_CONFIRM_DELETE:
                    if(allowed("mailinglist.delete", req)) {
                        loadMailinglist(aForm, req);
                        aForm.setAction(MailinglistAction.ACTION_DELETE);
                        destination=mapping.findForward("delete");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
                    
                case MailinglistAction.ACTION_DELETE:
                    if(allowed("mailinglist.delete", req)) {
                        deleteMailinglist(aForm, req);
                        aForm.setAction(MailinglistAction.ACTION_LIST);
                        list(aForm, req);
                        destination=mapping.findForward("list");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
                    
                default:
                    aForm.setAction(MailinglistAction.ACTION_LIST);
                    destination=mapping.findForward("list");
            }
            
        } catch (Exception e) {
            AgnUtils.logger().error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        }
        
        if (!errors.isEmpty()) {
            saveErrors(req, errors);
        }
        
        return destination;
    }
    
    /**
     * Sets attributes for mailingslists request.
     */
    protected void list(MailinglistForm aForm, HttpServletRequest req) {
        MailinglistDao mDao=(MailinglistDao) getBean("MailinglistDao");
        
        req.setAttribute("mailinglists", mDao.getMailinglists(this.getCompanyID(req)));
        return;
    }
    
    /**
     * Loads mailingslist.
     */
    protected void loadMailinglist(MailinglistForm aForm, HttpServletRequest req) {
        MailinglistDao mDao=(MailinglistDao) getBean("MailinglistDao");
        Mailinglist aMailinglist=mDao.getMailinglist(aForm.getMailinglistID(), this.getCompanyID(req));

        if(aMailinglist!=null) {
            aForm.setShortname(aMailinglist.getShortname());
            aForm.setDescription(aMailinglist.getDescription());
        } else if(aForm.getMailinglistID() != 0) {
            AgnUtils.logger().warn("loadMailinglist: could not load mailinglist: "+aForm.getMailinglistID());
        }
        return;
    }
    
    /**
     * Saves mailinglist.
     */
    protected boolean saveMailinglist(MailinglistForm aForm, HttpServletRequest req) {
        
        MailinglistDao mDao=(MailinglistDao) getBean("MailinglistDao");
        Mailinglist aMailinglist=mDao.getMailinglist(aForm.getMailinglistID(), this.getCompanyID(req));
        boolean is_new=false;
        
        if(aMailinglist==null) {
            aForm.setMailinglistID(0);
            aMailinglist=(Mailinglist) getBean("Mailinglist");
            aMailinglist.setCompanyID(this.getCompanyID(req));
            is_new=true;
        }
        aMailinglist.setShortname(aForm.getShortname());
        aMailinglist.setDescription(aForm.getDescription());
        
        mDao.saveMailinglist(aMailinglist);
        
        aForm.setMailinglistID(aMailinglist.getId());
        AgnUtils.logger().info("saveMailinglist: save mailinglist id: "+aMailinglist.getId());
        return is_new;
    }
    
    /**
     * Removes mailinglist.
     */
    protected void deleteMailinglist(MailinglistForm aForm, HttpServletRequest req) {
        
        if(aForm.getMailinglistID()!=0) {
            MailinglistDao mDao=(MailinglistDao) getBean("MailinglistDao");
            Mailinglist aMailinglist=mDao.getMailinglist(aForm.getMailinglistID(), this.getCompanyID(req));
            if(aMailinglist!=null) {
                aMailinglist.deleteBindings();
                mDao.deleteMailinglist(aForm.getMailinglistID(), this.getCompanyID(req));
            }
        }
        return;
    } 
}

