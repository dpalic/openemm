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
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.beans.Mailing;
import org.agnitas.beans.MailingComponent;
import org.agnitas.dao.MailingDao;
import org.agnitas.util.AgnUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;


/**
 * Implementation of <strong>Action</strong> that validates a user logon.
 *
 * @author Martin Helff
 */

public final class MailingAttachmentsAction extends StrutsActionBase {
    
    // --------------------------------------------------------- Public Methods
	ActionMessages errors;
    
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
        MailingAttachmentsForm aForm=null;
        errors = new ActionMessages();
        ActionForward destination=null;
        
        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }
        
        aForm=(MailingAttachmentsForm)form;

       if(!allowed("mailing.attachments.show", req)) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
            saveErrors(req, errors);
            return null;
        }
        
        try {
            switch(aForm.getAction()) {
                case MailingAttachmentsAction.ACTION_LIST:
                    loadMailing(aForm, req);
                    aForm.setAction(MailingAttachmentsAction.ACTION_SAVE);
                    destination=mapping.findForward("list");
                    break;
                    
                case MailingAttachmentsAction.ACTION_SAVE:
                    destination=mapping.findForward("list");
                    	saveAttachment(aForm, req);
                    	loadMailing(aForm, req);
                    	aForm.setAction(MailingAttachmentsAction.ACTION_SAVE);
                    break;
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
     * Loads mailing
     */
    protected void loadMailing(MailingAttachmentsForm aForm, HttpServletRequest req) throws Exception {
        MailingDao mDao=(MailingDao) getBean("MailingDao");
        Mailing aMailing=mDao.getMailing(aForm.getMailingID(), this.getCompanyID(req));
        
        aForm.setShortname(aMailing.getShortname());
        aForm.setDescription(aMailing.getDescription());
        aForm.setIsTemplate(aMailing.isIsTemplate());
        
        AgnUtils.logger().info("loadMailing: mailing loaded");
    }
    
    /**
     * Saves attachement
     */
    protected void saveAttachment(MailingAttachmentsForm aForm, HttpServletRequest req) {
        MailingComponent aComp=null;
        String aParam=null;
        Vector deleteEm=new Vector();
        
        MailingDao mDao=(MailingDao) getBean("MailingDao");
        Mailing aMailing=mDao.getMailing(aForm.getMailingID(), this.getCompanyID(req));
        
        FormFile newAttachment=aForm.getNewAttachment();
        try {
            if(newAttachment.getFileSize() != 0  && newAttachment.getFileSize() < 1048576) {
                aComp=(MailingComponent) getBean("MailingComponent");
                aComp.setCompanyID(this.getCompanyID(req));
                aComp.setMailingID(aForm.getMailingID());
                aComp.setType(MailingComponent.TYPE_ATTACHMENT);
                aComp.setComponentName(aForm.getNewAttachmentName());
                aComp.setBinaryBlock(newAttachment.getFileData());
                aComp.setEmmBlock(aComp.makeEMMBlock());
                aComp.setMimeType(newAttachment.getContentType());
                aComp.setTargetID(aForm.getAttachmentTargetID());
                aMailing.addComponent(aComp);
            } else if(newAttachment.getFileSize() >= 1048576) {
            	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.attachment"));
            }
        } catch(Exception e) {
            AgnUtils.logger().error("saveAttachment: "+e);
        }
        
        Iterator it=aMailing.getComponents().values().iterator();
        while (it.hasNext()) {
            aComp=(MailingComponent)it.next();
            switch(aComp.getType()) {
                case MailingComponent.TYPE_ATTACHMENT:
                    aParam=req.getParameter("delete"+aComp.getId()+".x");
                    if(aParam!=null) {
                        deleteEm.add(aComp);
                    }
                    aParam=req.getParameter("target"+aComp.getId());
                    if(aParam!=null) {
                        aComp.setTargetID(Integer.parseInt(aParam));
                    }
                    break;
            }
        }
        
        Enumeration en=deleteEm.elements();
        while(en.hasMoreElements()) {
            aMailing.getComponents().remove(((MailingComponent)en.nextElement()).getComponentName());
        }
        
        mDao.saveMailing(aMailing);
    }  
}
