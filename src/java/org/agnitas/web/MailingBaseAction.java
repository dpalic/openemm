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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.mail.internet.InternetAddress;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.beans.Mailing;
import org.agnitas.beans.MailingComponent;
import org.agnitas.beans.MediatypeEmail;
import org.agnitas.dao.MailingDao;
import org.agnitas.dao.MailinglistDao;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.SafeString;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;


/**
 * Implementation of <strong>Action</strong> that handles Mailings
 *
 * @author Martin Helff
 */

public class MailingBaseAction extends StrutsActionBase {

    public static final int ACTION_SELECT_TEMPLATE = ACTION_LAST+1;
    
    public static final int ACTION_REMOVE_TARGET = ACTION_LAST+2;
    
    public static final int ACTION_VIEW_WITHOUT_LOAD = ACTION_LAST+3;
    
    public static final int ACTION_CLONE_AS_MAILING = ACTION_LAST+4;

    public static final int ACTION_MAILING_BASE_LAST = ACTION_LAST+4;
    
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
        MailingBaseForm aForm=null;
        ActionMessages errors = new ActionMessages();
        ActionForward destination=null;
        boolean showTemplates=false;
        
        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }
        
        aForm=(MailingBaseForm)form;
        
        AgnUtils.logger().info("execute: action "+aForm.getAction());
 
        if(aForm.isIsTemplate()) {
            if(!allowed("template.show", req)) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                saveErrors(req, errors);
                return null;
            }
        } else {
            if(!allowed("mailing.show", req)) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                saveErrors(req, errors);
                return null;
            }
        }
       
        try {
            switch(aForm.getAction()) {
                case MailingBaseAction.ACTION_LIST:
                    destination=mapping.findForward("list");
                    break;
                    
                case MailingBaseAction.ACTION_NEW:
                    if(allowed("mailing.new", req)) {
                        MailinglistDao mDao=(MailinglistDao) getBean("MailinglistDao");
                        List mlists=mDao.getMailinglists(getCompanyID(req));
                       
                        if(mlists.size() > 0) { 
                            aForm.setAction(MailingBaseAction.ACTION_SAVE);
                            aForm.clearData(getCompanyID(req), getDefaultMediaType(req));
                            aForm.setMailingID(0);
                            destination=mapping.findForward("view");
                        } else {
                            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.mailing.noMailinglist"));
                        }
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
 
                case MailingBaseAction.ACTION_VIEW:
                    aForm.setAction(MailingBaseAction.ACTION_SAVE);
                    loadMailing(aForm, req);
                    destination=mapping.findForward("view");
                    break;
                    
                case MailingBaseAction.ACTION_VIEW_WITHOUT_LOAD:
                    aForm.setAction(MailingBaseAction.ACTION_SAVE);
                    destination=mapping.findForward("view");
                    break;
                    
                case MailingBaseAction.ACTION_REMOVE_TARGET:
                    removeTarget(aForm, req);
                    if(aForm.getMailingID()!=0) {
                        aForm.setAction(MailingBaseAction.ACTION_SAVE);
                        //this.saveMailing(aForm, req);
                    } else {
                        aForm.setAction(MailingBaseAction.ACTION_NEW);
                    }
                    destination=mapping.findForward("view");
                    break;
                    
                case MailingBaseAction.ACTION_SAVE:
                    if(allowed("mailing.change", req)) {
                        saveMailing(aForm, req);
                        showTemplates=aForm.isShowTemplate();
                        loadMailing(aForm, req);
                        aForm.setShowTemplate(showTemplates);
                        destination=mapping.findForward("view");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
                    
                case MailingBaseAction.ACTION_SELECT_TEMPLATE:
                    loadTemplateSettings(aForm, req);
                    aForm.setAction(MailingBaseAction.ACTION_SAVE);
                    destination=mapping.findForward("view");
                    break;
                    
                case MailingBaseAction.ACTION_CLONE_AS_MAILING:
                    if(allowed("mailing.copy", req)) {
                    
                        int tmpTemplateID=aForm.getMailingID();
                        int tmpMlId=aForm.getMailinglistID();
                        String sname = aForm.getShortname();
                        int tmpFormat=aForm.getMediaEmail().getMailFormat();
                        boolean tmpl=aForm.isIsTemplate();
                        aForm.clearData(this.getCompanyID(req), this.getDefaultMediaType(req));
                        aForm.setTemplateID(tmpTemplateID);
                        aForm.setIsTemplate(tmpl);
                        loadTemplateSettings(aForm, req);
                        aForm.setMailinglistID(tmpMlId);
                        aForm.getMediaEmail().setMailFormat(tmpFormat);
                        aForm.setMailingID(0);
                        aForm.setAction(MailingBaseAction.ACTION_SAVE);
                        aForm.setShortname(new String(SafeString.getLocaleString("CopyOf", (Locale)req.getSession().getAttribute(Globals.LOCALE_KEY)) + " " + sname));
                        aForm.setDescription(SafeString.getLocaleString("default.mailing.description", (Locale)req.getSession().getAttribute(Globals.LOCALE_KEY)));
                        aForm.setCopyFlag(true);
                        destination=mapping.findForward("view");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
                    
                case MailingBaseAction.ACTION_CONFIRM_DELETE:
                    if(allowed("mailing.delete", req)) {
                        aForm.setAction(MailingBaseAction.ACTION_DELETE);
                        loadMailing(aForm, req);
                        destination=mapping.findForward("delete");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
                    
                case MailingBaseAction.ACTION_DELETE:
                    if(allowed("mailing.delete", req)) {
                        aForm.setAction(MailingBaseAction.ACTION_LIST);
                        deleteMailing(aForm, req);
                        destination=mapping.findForward("list");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
                    
                default:
                    aForm.setAction(MailingBaseAction.ACTION_LIST);
                    destination=mapping.findForward("list");
            }
            
        } catch (Exception e) {
            AgnUtils.logger().error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            System.err.println("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        }
        
        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(req, errors);
            if(destination == null) {
                destination=mapping.findForward("list");
            }
        }
        
        return destination;
    }
    
    /**
     * Loads mailing. 
     */
    protected void loadMailing(MailingBaseForm aForm, HttpServletRequest req) throws Exception {
        MailingComponent comp=null;
        MailingDao mDao=(MailingDao) getBean("MailingDao");
        Mailing aMailing=mDao.getMailing(aForm.getMailingID(), getCompanyID(req));
        MediatypeEmail type=null;

        if(aMailing==null) {
            aMailing=(Mailing) getBean("Mailing");
            aMailing.init(getCompanyID(req), getWebApplicationContext());
            aMailing.setId(0);
            aForm.setMailingID(0);
        }
        
        aForm.setShortname(aMailing.getShortname());
        aForm.setDescription(aMailing.getDescription());
        aForm.setMailingType(aMailing.getMailingType());
        aForm.setMailinglistID(aMailing.getMailinglistID());
        aForm.setTemplateID(aMailing.getMailTemplateID());
        aForm.setTargetGroups(aMailing.getTargetGroups());
        aForm.setMediatypes(aMailing.getMediatypes());
        aForm.setCampaignID(aMailing.getCampaignID());
        
        type=aMailing.getEmailParam(this.getWebApplicationContext());
        if(type!=null) {
            aForm.setEmailSubject(type.getSubject());
            aForm.setEmailOnepixel(type.getOnepixel());
            try {
                aForm.setEmailReplytoEmail(new InternetAddress(type.getReplyAdr()).getAddress());
            } catch (Exception e) {
                // do nothing
            }
            try {
                aForm.setEmailReplytoFullname(new InternetAddress(type.getReplyAdr()).getPersonal());
            } catch (Exception e) {
                // do nothing
            }
            aForm.setEmailLinefeed(type.getLinefeed());
            aForm.setEmailCharset(type.getCharset());
        }
        
        comp=aMailing.getTextTemplate();
        if(comp!=null) {
            aForm.setTextTemplate(comp.getEmmBlock());
        }
        
        comp=aMailing.getHtmlTemplate();
        if(comp!=null) {
            aForm.setHtmlTemplate(comp.getEmmBlock());
        }
        
        AgnUtils.logger().info("loadMailing: mailing loaded");
        return;
    }
    
    /**
     * Removes target.
     */
    protected void removeTarget(MailingBaseForm aForm, HttpServletRequest req) throws Exception {
        Collection allTargets=aForm.getTargetGroups();
        Integer tmpInt=null;
        
        if(allTargets!=null) {
            Iterator aIt=allTargets.iterator();
            while(aIt.hasNext()) {
                tmpInt=(Integer)aIt.next();
                if(aForm.getTargetID()==tmpInt.intValue()) {
                    allTargets.remove(tmpInt);
                    break;
                }
            }
        }
        
        if(allTargets.isEmpty()) {
            aForm.setTargetGroups(null);
        }
    }
    
    /**
     * Loads template settings.
     */
    protected void loadTemplateSettings(MailingBaseForm aForm, HttpServletRequest req) throws Exception {
        Mailing aTemplate=null;
        MailingComponent tmpComp=null;
        
        if(aForm.getTemplateID()!=0) {
            
            MailingDao dao=(MailingDao) getBean("MailingDao");
            aTemplate=dao.getMailing(aForm.getTemplateID(), this.getCompanyID(req));
            if(aTemplate!=null) {
                aForm.setMailingType(aTemplate.getMailingType());
                
                aForm.setTargetMode(aTemplate.getTargetMode());
                aForm.setTargetGroups(aTemplate.getTargetGroups());
                aForm.setMediatypes(aTemplate.getMediatypes()); 
                
                // load template for this mailing
                if((tmpComp=aTemplate.getHtmlTemplate())!=null) {
                    aForm.setHtmlTemplate(tmpComp.getEmmBlock());
                }
                
                if((tmpComp=aTemplate.getTextTemplate())!=null) {
                    aForm.setTextTemplate(tmpComp.getEmmBlock());
                }
                MediatypeEmail type=aTemplate.getEmailParam(this.getWebApplicationContext());
                if(type!=null) {
                    aForm.setEmailSubject(type.getSubject());
                    aForm.setEmailOnepixel(type.getOnepixel());
                    try {
                        aForm.setEmailReplytoEmail(new InternetAddress(type.getReplyAdr()).getAddress());
                    } catch (Exception e) {
                        // do nothing
                    }
                    try {
                        aForm.setEmailReplytoFullname(new InternetAddress(type.getReplyAdr()).getPersonal());
                    } catch (Exception e) {
                        // do nothing
                    }
                    aForm.setEmailLinefeed(type.getLinefeed());
                    aForm.setEmailCharset(type.getCharset());
                    
                }
            }
        }
    }
    
    /**
     * saves current mailing in DB
     *
     * @param aForm 
     * @param req 
     */
    protected void saveMailing(MailingBaseForm aForm, HttpServletRequest req) throws Exception {
        Mailing aMailing=null;
        Mailing aTemplate=null;
        MediatypeEmail paramEmail=null;
        MailingDao mDao=(MailingDao) getBean("MailingDao");

        if(aForm.getMailingID()!=0) {
            aMailing=mDao.getMailing(aForm.getMailingID(), this.getCompanyID(req));
        } else {
            if(aForm.getTemplateID()!=0) {
                aTemplate=mDao.getMailing(aForm.getTemplateID(), this.getCompanyID(req));
                aMailing=(Mailing)aTemplate.clone(this.getWebApplicationContext());
                aMailing.setId(0);
                aMailing.setMailTemplateID(aForm.getTemplateID());
                aMailing.setCompanyID(this.getCompanyID(req));
            }
        }
        
        if(aMailing==null) {
            aMailing=(Mailing) getBean("Mailing");
            aMailing.init(this.getCompanyID(req), this.getWebApplicationContext());
            aMailing.setId(0);
            aForm.setMailingID(0);
        }
        
        aMailing.setIsTemplate(aForm.isIsTemplate());
        aMailing.setCampaignID(aForm.getCampaignID());
        aMailing.setDescription(aForm.getDescription());
        aMailing.setShortname(aForm.getShortname());
        aMailing.setMailinglistID(aForm.getMailinglistID());
        aMailing.setMailingType(aForm.getMailingType());
        aMailing.setTargetMode(aForm.getTargetMode());
        aMailing.setTargetGroups(aForm.getTargetGroups());
        aMailing.setMediatypes(aForm.getMediatypes());

        try {
            paramEmail=aMailing.getEmailParam(this.getWebApplicationContext());
            
            paramEmail.setSubject(aForm.getEmailSubject());
            paramEmail.setLinefeed(aForm.getEmailLinefeed());
            paramEmail.setCharset(aForm.getEmailCharset());
            paramEmail.setOnepixel(aForm.getEmailOnepixel());
           
            aForm.getMediaEmail().syncTemplate(aMailing, getWebApplicationContext());
            
            aMailing.buildDependencies(true, this.getWebApplicationContext());
        } catch (Exception e) {
            AgnUtils.logger().error("Error in save mailing id: "+aForm.getMailingID()+" msg: "+e.getMessage());
        }
      
        mDao.saveMailing(aMailing);
        aForm.setMailingID(aMailing.getId());
        
        return;
    }
    
    /**
     * Deletes mailing.
     */
    protected void deleteMailing(MailingBaseForm aForm, HttpServletRequest req) throws Exception {
        
        MailingDao mDao=(MailingDao) getBean("MailingDao");
        mDao.deleteMailing(aForm.getMailingID(), this.getCompanyID(req));
        
        return;
    }
}
