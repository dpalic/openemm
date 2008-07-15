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
import org.agnitas.target.*;
import javax.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import javax.mail.internet.*;
import org.apache.struts.action.*;
import org.apache.struts.util.*;
import org.apache.struts.upload.*;


/**
 * Implementation of <strong>Action</strong> that handles Mailings
 *
 * @author Martin Helff
 */

public final class MailingWizardAction extends StrutsDispatchActionBase {
    
    public static final String ACTION_START = "start";
    public static final String ACTION_NAME = "name";
    public static final String ACTION_TEMPLATE = "template";
    public static final String ACTION_TYPE = "type";
    public static final String ACTION_SENDADDRESS = "sendaddress";
    public static final String ACTION_MAILTYPE = "mailtype";
    public static final String ACTION_SUBJECT = "subject";
    public static final String ACTION_TARGET = "target";
    public static final String ACTION_TEXTMODULES = "textmodules";
    public static final String ACTION_TEXTMODULE = "textmodule";
    public static final String ACTION_TEXTMODULE_ADD = "textmodule_add";
    public static final String ACTION_MEASURELINKS = "links";
    public static final String ACTION_MEASURELINK = "link";
    public static final String ACTION_ATTACHMENT = "attachment";
    public static final String ACTION_FINISH = "finish";
    
    
    // --------------------------------------------------------- Public Methods
    
    /**
     * Initialization
     */
    public ActionForward init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
        AgnUtils.logger().debug("init");
        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }
        
        return mapping.findForward("this");
    }
    
    /**
     * Starts mailing.
     */
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
        AgnUtils.logger().debug("start");
        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }
        
        MailingWizardForm aForm=(MailingWizardForm)form;
        
        Mailing tmpMailing=(Mailing) getBean("Mailing");
        tmpMailing.init(getCompanyID(req), getWebApplicationContext());
        aForm.setMailing(tmpMailing);
        
        return mapping.findForward("next");
    }
    
    /**
     * Names mailing.
     */
    public ActionForward name(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
        AgnUtils.logger().debug("name");
        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }
        
        return mapping.findForward("next");
    }
    
    /**
     * 
     */
    public ActionForward template(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }
        
        MailingWizardForm aForm=(MailingWizardForm)form;
        MailingDao mDao=(MailingDao) getBean("MailingDao");
        
        Mailing tmpMailing=mDao.getMailing(aForm.getMailing().getMailTemplateID(), this.getCompanyID(req));
        if(tmpMailing!=null) {
            tmpMailing.clone(getWebApplicationContext());
            tmpMailing.setShortname(aForm.getMailing().getShortname());
            tmpMailing.setDescription(aForm.getMailing().getDescription());
            aForm.setMailing(tmpMailing);
            MediatypeEmail param=tmpMailing.getEmailParam(getWebApplicationContext());
            aForm.setEmailSubject(param.getSubject());
            aForm.setEmailFormat(param.getMailFormat());
            aForm.setEmailOnepixel(param.getOnepixel());
            try {
                aForm.setSenderEmail(new InternetAddress(param.getFromAdr()).getAddress());
            } catch (Exception e) {
                // do nothing
            }
            try {
                aForm.setSenderFullname(new InternetAddress(param.getFromAdr()).getPersonal());
            } catch (Exception e) {
                // do nothing
            }
            try {
                aForm.setReplyFullname(new InternetAddress(param.getReplyAdr()).getPersonal());
            } catch (Exception e) {
                // do nothing
            }
        }
        
        aForm.getMailing().buildDependencies(true, this.getWebApplicationContext());
        
        return mapping.findForward("next");
    }
    
    /**
     *
     */
    public ActionForward type(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
        AgnUtils.logger().debug("type");
        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }
        
        return mapping.findForward("next");
    }
    
    /**
     *
     */
    public ActionForward sendaddress(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
        AgnUtils.logger().debug("sendaddress");
        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }
        
        return mapping.findForward("next");
    }
    
    /**
     *
     */
    public ActionForward subject(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
        AgnUtils.logger().debug("subject");
        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }
        MailingWizardForm aForm=(MailingWizardForm)form;
        MediatypeEmail param=aForm.getMailing().getEmailParam(this.getWebApplicationContext());
        param.setSubject(aForm.getEmailSubject());
        
        return mapping.findForward("next");
    }
    
    /**
     *
     */
    public ActionForward mailtype(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
        AgnUtils.logger().debug("mailtype");
        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }
        MailingWizardForm aForm=(MailingWizardForm)form;
        MediatypeEmail param=aForm.getMailing().getEmailParam(this.getWebApplicationContext());
        param.setMailFormat(aForm.getEmailFormat());
        
        return mapping.findForward("next");
    }
    
    /**
     *
     */
    public ActionForward target(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
        AgnUtils.logger().debug("target");
        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }
        MailingWizardForm aForm=(MailingWizardForm)form;
        MediatypeEmail param=aForm.getMailing().getEmailParam(this.getWebApplicationContext());
        param.setOnepixel(aForm.getEmailOnepixel());
        
        if(aForm.getTargetID()!=0) {
            Collection aList=aForm.getMailing().getTargetGroups();
            if(aList==null) {
                aList=new HashSet();
            }
            if(!aList.contains(new Integer(aForm.getTargetID()))) {
                aList.add(new Integer(aForm.getTargetID()));
            }
            aForm.getMailing().setTargetGroups(aList);
            
            return mapping.getInputForward();
        }
        
        if(aForm.getRemoveTargetID()!=0) {
            Collection aList=aForm.getMailing().getTargetGroups();
            if(aList!=null) {
                aList.remove(new Integer(aForm.getRemoveTargetID()));
            }
            return mapping.getInputForward();
        }
        
        return mapping.findForward("next");
    }
    
    /**
     *
     */
    public ActionForward textmodule(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
        AgnUtils.logger().debug("textmodule");
        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }
        
        MailingWizardForm aForm=(MailingWizardForm)form;
        if(aForm.getDynName()==null || aForm.getDynName().trim().length()==0) {
            aForm.setDynName((String)aForm.getMailing().getDynTags().keySet().iterator().next());
        }
        
        return mapping.findForward("next");
    }

    /**
     *
     */
    public ActionForward textmodule_add(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
        MailingWizardForm aForm=(MailingWizardForm)form;
        Mailing mailing=aForm.getMailing();
        MailingComponent component=null;

        component=mailing.getTextTemplate();
        if(component!=null) {
            component.setEmmBlock(aForm.getNewContent());
            component.setBinaryBlock(aForm.getNewContent().getBytes());
        } 
        component=mailing.getHtmlTemplate();
        if(component!=null) {
            component.setEmmBlock(aForm.getNewContent());
            component.setBinaryBlock(aForm.getNewContent().getBytes());
        } 
        mailing.buildDependencies(true, this.getWebApplicationContext());
/*
        DynamicTag dynTag=(DynamicTag) getBean("DynamicTag");

        dynTag.setMailingID(aForm.getMailing().getId());
        dynTag.setCompanyID(AgnUtils.getCompanyID(req));
*/
//        dynTag.setContent(aForm.getContent());
        
        AgnUtils.logger().debug("textmodule_add");
        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }

        return mapping.findForward("add");
    }
 
    /**
     *
     */
    public ActionForward links(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
        AgnUtils.logger().debug("links");
        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }
        
        MailingWizardForm aForm=(MailingWizardForm)form;
        aForm.clearAktTracklink();
        if(aForm.getDynName()==null || aForm.getDynName().trim().length()==0) {
            aForm.setDynName((String)aForm.getMailing().getDynTags().keySet().iterator().next());
        }
        
        return mapping.findForward("next");
    }

    /**
     *
     */
    public ActionForward link(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
        AgnUtils.logger().debug("link");
        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }
       
        MailingWizardForm aForm=(MailingWizardForm)form;
 
        if(aForm.getDynName()==null || aForm.getDynName().trim().length()==0) {
            aForm.setDynName((String)aForm.getMailing().getDynTags().keySet().iterator().next());
        }

        return mapping.findForward("next");
    }

    /**
     *
     */
    public ActionForward attachment(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
        AgnUtils.logger().debug("attachment");
        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }
        
        MailingWizardForm aForm=(MailingWizardForm)form;
        if(aForm.getDynName()==null || aForm.getDynName().trim().length()==0) {
            aForm.setDynName((String)aForm.getMailing().getDynTags().keySet().iterator().next());
        }
        return mapping.findForward("next");
    }

    /**
     *
     */
    public ActionForward previous(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }
        
        return mapping.findForward("previous");
    }   
}
