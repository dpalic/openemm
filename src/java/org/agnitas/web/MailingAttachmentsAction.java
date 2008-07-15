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
import java.text.*;
import javax.servlet.http.*;
import org.apache.struts.action.*;
import org.apache.struts.util.*;
import org.apache.struts.upload.*;


/**
 * Implementation of <strong>Action</strong> that validates a user logon.
 *
 * @author Martin Helff
 */

public final class MailingAttachmentsAction extends StrutsActionBase {
    
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
        MailingAttachmentsForm aForm=null;
        ActionMessages errors = new ActionMessages();
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
            return (new ActionForward(mapping.getInput()));
        }
        
        return destination;
        
    }
    
    /**
     * Loads mailing
     */
    protected void loadMailing(MailingAttachmentsForm aForm, HttpServletRequest req) throws Exception {
        MailingComponent comp=null;
        
        MailingDao mDao=(MailingDao) getBean("MailingDao");
        Mailing aMailing=mDao.getMailing(aForm.getMailingID(), this.getCompanyID(req));
        
        aForm.setShortname(aMailing.getShortname());
        aForm.setDescription(aMailing.getDescription());
        aForm.setIsTemplate(aMailing.isIsTemplate());
        
        AgnUtils.logger().info("loadMailing: mailing loaded");
        return;
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
            if(newAttachment.getFileSize()!=0) {
                aComp=(MailingComponent) getBean("MailingComponent");
                aComp.setCompanyID(this.getCompanyID(req));
                aComp.setMailingID(aForm.getMailingID());
                aComp.setType(MailingComponent.TYPE_ATTACHMENT);
                aComp.setComponentName(aForm.getNewAttachmentName());
                aComp.setBinaryBlock(newAttachment.getFileData());
                aComp.setEmmBlock(aComp.makeEMMBlock());
                aComp.setMimeType(newAttachment.getContentType());
                aMailing.addComponent(aComp);
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
        
        return;
    }  
}
