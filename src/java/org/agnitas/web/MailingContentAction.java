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

import org.agnitas.beans.DynamicTag;
import org.agnitas.beans.DynamicTagContent;
import org.agnitas.beans.Mailing;
import org.agnitas.dao.MailingDao;
import org.agnitas.dao.TargetDao;
import org.agnitas.util.AgnUtils;
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

public class MailingContentAction extends StrutsActionBase {
    
    public static final int ACTION_VIEW_CONTENT = ACTION_LAST+1;
    
    public static final int ACTION_VIEW_TEXTBLOCK = ACTION_LAST+2;
    
    public static final int ACTION_ADD_TEXTBLOCK = ACTION_LAST+3;
    
    public static final int ACTION_SAVE_TEXTBLOCK = ACTION_LAST+4;
    
    public static final int ACTION_SAVE_COMPONENT_EDIT = ACTION_LAST+5;
    
    public static final int ACTION_DELETE_TEXTBLOCK = ACTION_LAST+6;
    
    public static final int ACTION_CHANGE_ORDER_UP = ACTION_LAST+7;
    
    public static final int ACTION_CHANGE_ORDER_DOWN = ACTION_LAST+8;
    
    public static final int ACTION_CHANGE_ORDER_TOP = ACTION_LAST+9;
    
    public static final int ACTION_CHANGE_ORDER_BOTTOM = ACTION_LAST+10;

    public static final int ACTION_MAILING_CONTENT_LAST = ACTION_LAST+10;
    
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
        MailingContentForm aForm=null;
        ActionMessages errors = new ActionMessages();
        ActionForward destination=null;
        
        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }
        
        aForm=(MailingContentForm)form;
        AgnUtils.logger().info("Action: " + aForm.getAction());

       if(!allowed("mailing.content.show", req)) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
            saveErrors(req, errors);
            return null;
        }
        
        try {
            switch(aForm.getAction()) {
                case MailingContentAction.ACTION_VIEW_CONTENT:
                    loadMailing(aForm, req);
                    destination=mapping.findForward("list");
                    break;
                    
                case MailingContentAction.ACTION_VIEW_TEXTBLOCK:
                    aForm.setAction(MailingContentAction.ACTION_SAVE_TEXTBLOCK);
                    loadMailing(aForm, req);
                    destination=mapping.findForward("view");
                    break;
                    
                case MailingContentAction.ACTION_SAVE_TEXTBLOCK:
                case MailingContentAction.ACTION_ADD_TEXTBLOCK:
                case MailingContentAction.ACTION_DELETE_TEXTBLOCK:
                case MailingContentAction.ACTION_CHANGE_ORDER_UP:
                case MailingContentAction.ACTION_CHANGE_ORDER_DOWN:
                case MailingContentAction.ACTION_CHANGE_ORDER_TOP:
                case MailingContentAction.ACTION_CHANGE_ORDER_BOTTOM:
                    destination=mapping.findForward("list");
                    this.saveContent(aForm, req);
                    aForm.setAction(MailingContentAction.ACTION_VIEW_CONTENT);
                    loadMailing(aForm, req);
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
     * Loads mailing.
     */
    protected void loadMailing(MailingContentForm aForm, HttpServletRequest req) throws Exception {
        MailingDao mDao=(MailingDao) getBean("MailingDao");
        Mailing aMailing=mDao.getMailing(aForm.getMailingID(), this.getCompanyID(req));
        TargetDao tDao=(TargetDao) getBean("TargetDao");
        
        if(aMailing==null) {
            aMailing=(Mailing) getBean("Mailing");
            aMailing.init(getCompanyID(req), getWebApplicationContext());
            aMailing.setId(0);
            aForm.setMailingID(0);
        }
        
        aForm.setShortname(aMailing.getShortname());
        aForm.setIsTemplate(aMailing.isIsTemplate());
        aForm.setMailinglistID(aMailing.getMailinglistID());
        aForm.setMailingID(aMailing.getId());
        aForm.setMailFormat(aMailing.getEmailParam(this.getWebApplicationContext()).getMailFormat());
        aForm.setContent(aMailing.getDynTags());
        if(aForm.getAction()==MailingContentAction.ACTION_VIEW_TEXTBLOCK || aForm.getAction()==MailingContentAction.ACTION_SAVE_TEXTBLOCK) {
            aForm.setContent(aMailing.getDynamicTagById(aForm.getDynNameID()).getDynContent());
            aForm.setDynName(aMailing.getDynamicTagById(aForm.getDynNameID()).getDynName());
        }
        
        req.setAttribute("targetGroups", tDao.getTargets(this.getCompanyID(req)));
        
        return;
    }
    
    /**
     * Saves content.
     */
    protected void saveContent(MailingContentForm aForm, HttpServletRequest req) {
        
        MailingDao mDao=(MailingDao) getBean("MailingDao");
        Mailing aMailing=mDao.getMailing(aForm.getMailingID(), this.getCompanyID(req));
        DynamicTagContent aContent=null;
        
        if(aMailing!=null) {
            DynamicTag aTag=aMailing.getDynamicTagById(aForm.getDynNameID());
            
            if(aTag!=null) {
                aTag.setDynContent(aForm.getContent());
                
                switch(aForm.getAction()) {
                    case MailingContentAction.ACTION_ADD_TEXTBLOCK:
                        aContent=(DynamicTagContent) getBean("DynamicTagContent");
                        aContent.setCompanyID(this.getCompanyID(req));
                        aContent.setDynContent(aForm.getNewContent());
                        aContent.setTargetID(aForm.getNewTargetID());
                        aContent.setDynOrder(aTag.getMaxOrder()+1);
                        aContent.setDynNameID(aTag.getId());
                        aContent.setMailingID(aTag.getMailingID());
                        aTag.addContent(aContent);
                        break;
                        
                    case MailingContentAction.ACTION_DELETE_TEXTBLOCK:
                        aTag.removeContent(aForm.getContentID());
                        break;
                        
                    case MailingContentAction.ACTION_CHANGE_ORDER_UP:
                        // aTag.changeContentOrder(aForm.getContentID(), 1);
                        aTag.moveContentDown(aForm.getContentID(), -1);
                        break;
                        
                    case MailingContentAction.ACTION_CHANGE_ORDER_DOWN:
                        aTag.moveContentDown(aForm.getContentID(), 1);
                        // aTag.changeContentOrder(aForm.getContentID(), 2);
                        break;

                    case MailingContentAction.ACTION_CHANGE_ORDER_TOP:
                        for(int c=0; c < 20; c++) {
                            aTag.moveContentDown(aForm.getContentID(), -1);
                        }
                        break;
                        
                    case MailingContentAction.ACTION_CHANGE_ORDER_BOTTOM:
                        for(int c=0; c < 20; c++) {
                            aTag.moveContentDown(aForm.getContentID(), 1);
                        }
                        break;
                }
            }
            try {
                aMailing.buildDependencies(false, this.getWebApplicationContext());
            } catch (Exception e) {
                AgnUtils.logger().error(e.getMessage());
            }
            mDao.saveMailing(aMailing);
            // save
        }
        AgnUtils.logger().info("change content of mailing: "+aForm.getMailingID());
        return;
    }
}
