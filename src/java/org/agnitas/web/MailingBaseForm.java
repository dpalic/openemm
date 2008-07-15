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

import javax.servlet.http.*;
import org.apache.struts.*;
import org.apache.struts.action.*;
import org.apache.struts.util.*;
import org.apache.struts.upload.*;
import org.agnitas.util.*;
import java.util.*;
import java.net.*;
import javax.mail.internet.InternetAddress;
import org.agnitas.beans.*;

/**
 *
 * @author  mhe
 */
public class MailingBaseForm extends StrutsFormBase {
    
    /** 
     * Holds value of property mailinglistID. 
     */
    private int mailingID;
    
    /**
     * Holds value of property campaignID. 
     */
    private int campaignID;
    
    /**
     * Holds value of property shortname. 
     */
    private String shortname="";
    
    /**
     * Holds value of property description. 
     */
    private String description;
    
    /**
     * Holds value of property emailCharset. 
     */
    private String emailCharset;
    
    /**
     * Holds value of property action. 
     */
    private int action;
    
    /**
     * Holds value of property emailSubject. 
     */
    private String emailSubject;
    
    /**
     * Holds value of property emailFormat. 
     */
    private int emailFormat;
    
    /**
     * Holds value of property emailLinefeed.
     */
    private int emailLinefeed;
    
    /**
     * Holds value of property mailingType. 
     */
    private int mailingType;
    
    /**
     * Holds value of property targetID. 
     */
    private int targetID;
    
    /**
     * Holds value of property mailinglistID. 
     */
    private int mailinglistID;
    
    /**
     * Holds value of property templateID. 
     */
    private int templateID;
    
    /**
     * Holds value of property worldMailingSend.
     */
    private boolean worldMailingSend;
     
    /**
     * Holds value of property htmlTemplate.
     */
    private String htmlTemplate;
    
    /**
     * Holds value of property textTemplate.
     */
    private String textTemplate;
    
    /**
     * Holds value of property showTemplate.
     */
    private boolean showTemplate;
  
    /**
     * Holds value of property targetGroups.
     */
    private LinkedList targetGroups;
    
    /**
     * Holds value of property isTemplate.
     */
    private boolean isTemplate;
    
    /**
     * Holds value of property oldMailingID.
     */
    private int oldMailingID;
    
    /**
     * Holds value of property copyFlag.
     */
    private boolean copyFlag;
    
    /**
     * Holds value of property needsTarget.
     */
    private boolean needsTarget;
    
    /**
     * Holds value of property targetMode.
     */
    private int targetMode;
    
    /**
     * Holds value of property senderEmail.
     */
    private String emailSenderEmail;
    
    /**
     * Holds value of property senderFullname.
     */
    private String emailSenderFullname;
    
    /**
     * Holds value of property replyEmail.
     */
    private String emailReplytoEmail;
    
    /**
     * Holds value of property replyFullname.
     */
    private String emailReplytoFullname;
    
    /** 
     * Creates a new instance of TemplateForm 
     */
    public MailingBaseForm() {
    }
    
    /**
     * Reset all properties to their default values.
     * 
     * @param companyID 
     * @param defaultMediaType 
     * @throws java.lang.Exception 
     */
/*    public void reset(ActionMapping mapping, HttpServletRequest request) {
 
        super.reset(mapping, request);
        this.templateID = 0;
        Locale aLoc=(Locale)request.getSession().getAttribute(org.apache.struts.action.Action.LOCALE_KEY);
        MessageResources text=this.getServlet().getResources();
 
        this.shortname=text.getMessage(aLoc, "default.template.shortname");
        this.emailFrom=text.getMessage(aLoc, "default.template.fromemail");
    }
 */
    
    /**
     * Initialization.
     */
    public void clearData(int companyID, int defaultMediaType) throws Exception {
        
        this.targetID=0;
        this.mailinglistID=0;
        this.templateID=0;
        this.mailingType=Mailing.TYPE_NORMAL;
        
        this.shortname=new String("");
        this.description=new String("");
        
        this.htmlTemplate=new String("");
        this.textTemplate=new String("");
        
        this.emailSenderEmail="";
        this.emailSenderFullname="";
        this.emailReplytoEmail="";
        this.emailReplytoFullname="";
        this.emailSubject="";
        this.emailCharset="ISO-8859-1";
        this.emailLinefeed=72;
        this.emailFormat=2;
        
        this.worldMailingSend=false;
        this.targetGroups=null;
        this.isTemplate=false;
        this.showTemplate=false;
        this.copyFlag=false;
        this.needsTarget=false;
        this.targetMode=Mailing.TARGET_MODE_OR;
    }
    
    /**
     * Validate the properties that have been set from this HTTP request,
     * and return an <code>ActionErrors</code> object that encapsulates any
     * validation errors that have been found.  If no errors are found, return
     * <code>null</code> or an <code>ActionErrors</code> object with no
     * recorded error messages.
     * 
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     * @return errors
     */
    public ActionErrors validate(ActionMapping mapping,
            HttpServletRequest request) {
        
        ActionErrors errors = new ActionErrors();
        
        Enumeration allParams=request.getParameterNames();
        Mailing aMailing=null;
        String paramName=null;
        int type;

        if(action==MailingBaseAction.ACTION_SAVE) {
            
            if(request.getParameter("addtarget.x")!=null) {
                //this.action=MailingBaseAction.ACTION_VIEW_WITHOUT_LOAD;
                if(this.targetID!=0) {
                    if(this.targetGroups==null) {
                        this.targetGroups=new LinkedList();
                    }
                    this.targetGroups.add(new Integer(this.targetID));
                }
            }
            
            Enumeration allNames=request.getParameterNames();
            String aName=null;
            int tmpTarget=0;
            while(allNames.hasMoreElements()) {
                aName=(String)allNames.nextElement();
                if(aName.startsWith("removetarget")) {
                    try {
                        tmpTarget=Integer.parseInt(aName.substring(12, aName.indexOf('.')));
                    } catch (Exception e) {
                        AgnUtils.logger().error("validate: "+e.getMessage());
                    }
                }
            }
            if(tmpTarget!=0) {
                this.targetID=tmpTarget;
                this.action=MailingBaseAction.ACTION_REMOVE_TARGET;
            }
        }
        
        if(action==MailingBaseAction.ACTION_SAVE) {
            
            if((this.isIsTemplate()==false) && this.isNeedsTarget() && this.targetGroups==null) {
                errors.add("global", new ActionMessage("error.mailing.rulebased_without_target"));
            }

            if(this.shortname.length()<3) {
                errors.add("shortname", new ActionMessage("error.nameToShort"));
            }
            
            
            // NEW CODE (to be inserted):
            if(this.emailReplytoFullname!=null && this.emailReplytoFullname.length()>255) {
                errors.add("replyFullname", new ActionMessage("error.reply_fullname_too_long"));
            }
            if(this.emailSenderFullname!=null && this.emailSenderFullname.length()>255) {
                errors.add("senderFullname", new ActionMessage("error.sender_fullname_too_long"));
            }
            if(this.emailReplytoFullname!=null && this.emailReplytoFullname.trim().length()==0) {
                this.emailReplytoFullname=this.emailSenderFullname;
            }
            
            
            
            if(this.targetGroups==null && this.mailingType==Mailing.TYPE_DATEBASED) {
                errors.add("global", new ActionMessage("error.mailing.rulebased_without_target"));
            }
            
            
            // NEW CODE (to be inserted):
            if(this.emailSenderEmail.length()<3)
                errors.add("shortname", new ActionMessage("error.invalid.email"));
            
            if(this.emailSubject.length()<2) {
                errors.add("subject", new ActionMessage("error.mailing.subject.too_short"));
            }
           
/* 
            if(this.textTemplate.trim().length()==0) {
                errors.add("template", new ActionMessage("error.mailing.no_text_template"));
            }
            
            if(this.htmlTemplate.trim().length()==0) {
                errors.add("template", new ActionMessage("error.mailing.no_html_template"));
            }
*/
            
            
            try {
                InternetAddress adr=new InternetAddress(this.emailSenderEmail);
                if(adr.getAddress().indexOf("@")==-1) {
                    errors.add("sender", new ActionMessage("error.mailing.sender_adress"));
                }              
            } catch (Exception e) {
                if(this.emailSenderEmail.indexOf("[agn")==-1) {
                    errors.add("sender", new ActionMessage("error.mailing.sender_adress"));
                }
            }
            
            try {
                aMailing=(Mailing) getWebApplicationContext().getBean("Mailing");
                aMailing.setCompanyID(this.getCompanyID(request));
                aMailing.findDynTagsInTemplates(new String(this.getEmailSubject()), this.getWebApplicationContext());
                aMailing.findDynTagsInTemplates(new String(this.getEmailSenderFullname()), this.getWebApplicationContext());
            } catch (Exception e) {
                AgnUtils.logger().error("validate: "+e);
                errors.add("subject", new ActionMessage("error.template.dyntags"));
            }
            
            try {
                aMailing=(Mailing) getWebApplicationContext().getBean("Mailing");
                aMailing.setCompanyID(this.getCompanyID(request));
                aMailing.personalizeText(new String(this.getEmailSubject()), 0, this.getWebApplicationContext());
                aMailing.personalizeText(new String(this.getEmailSenderFullname()), 0, this.getWebApplicationContext());
            } catch (Exception e) {
                errors.add("subject", new ActionMessage("error.personalization_tag"));
            }
            
            if(this.textTemplate.length()!=0) {
                // Just a syntax-check, no MailingID required
                aMailing=(Mailing) getWebApplicationContext().getBean("Mailing");
                aMailing.setCompanyID(this.getCompanyID(request));
                
                try {
                    aMailing.personalizeText(new String(this.getTextTemplate()), 0, this.getWebApplicationContext());
                } catch (Exception e) {
                    errors.add("texttemplate", new ActionMessage("error.personalization_tag"));
                }
                
                try {
                    aMailing.findDynTagsInTemplates(new String(this.textTemplate), this.getWebApplicationContext());
                } catch (Exception e) {
                    errors.add("texttemplate", new ActionMessage("error.template.dyntags"));
                }
                
            }
            
            if(this.htmlTemplate.length()!=0) {
                // Just a syntax-check, no MailingID required
                aMailing=(Mailing) getWebApplicationContext().getBean("Mailing");
                aMailing.setCompanyID(this.getCompanyID(request));
                
                try {
                    aMailing.personalizeText(new String(this.getHtmlTemplate()), 0, this.getWebApplicationContext());
                } catch (Exception e) {
                    errors.add("texttemplate", new ActionMessage("error.personalization_tag"));
                }
                
                try {
                    aMailing.findDynTagsInTemplates(new String(this.htmlTemplate), this.getWebApplicationContext());
                } catch (Exception e) {
                    AgnUtils.logger().error("validate: find "+e);
                    errors.add("texttemplate", new ActionMessage("error.template.dyntags"));
                }
            }
            
        }
        
        return errors;
    }
    
    
    /**
     * Getter for property templateID.
     *
     * @return Value of property templateID.
     */
    public int getMailingID() {
        return this.mailingID;
    }
    
    /**
     * Setter for property templateID.
     * 
     * @param mailingID New value of property mailingID.
     */
    public void setMailingID(int mailingID) {
        this.mailingID = mailingID;
    }
    
    /**
     * Getter for property campaignID.
     *
     * @return Value of property campaignID.
     */
    public int getCampaignID() {
        return this.campaignID;
    }
    
    /**
     * Setter for property campaignID.
     * 
     * @param campaignID New value of property campaignID.
     */
    public void setCampaignID(int campaignID) {
        this.campaignID = campaignID;
    }
    
    /**
     * Getter for property shortname.
     *
     * @return Value of property shortname.
     */
    public String getShortname() {
        return this.shortname;
    }
    
    /** 
     * Setter for property shortname.
     *
     * @param shortname New value of property shortname.
     */
    public void setShortname(String shortname) {
        this.shortname = shortname;
    }
    
    /**
     * Getter for property description.
     *
     * @return Value of property description.
     */
    public String getDescription() {
        return this.description;
    }
    
    /** 
     * Setter for property description.
     *
     * @param description New value of property description.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Getter for property charset.
     *
     * @return Value of property charset.
     */
    public String getEmailCharset() {
        return this.emailCharset;
    }
    
    /**
     * Setter for property charset.
     * 
     * @param emailCharset New value of property emailCharset.
     */
    public void setEmailCharset(String emailCharset) {
        this.emailCharset = emailCharset;
    }
    
    /**
     * Getter for property action.
     *
     * @return Value of property action.
     */
    public int getAction() {
        return this.action;
    }
    
    /** 
     * Setter for property action.
     *
     * @param action New value of property action.
     */
    public void setAction(int action) {
        this.action = action;
    }
    
    /** 
     * Getter for property subject.
     *
     * @return Value of property subject.
     */
    public String getEmailSubject() {
        return this.emailSubject;
    }
    
    /** 
     * Setter for property subject.
     *
     * @param subject New value of property subject.
     */
    public void setEmailSubject(String subject) {
        this.emailSubject = subject;
    }
    
    /** 
     * Getter for property emailFormat.
     *
     * @return Value of property emailFormat.
     */
    public int getEmailFormat() {
        return this.emailFormat;
    }
    
    /** 
     * Setter for property emailFormat.
     *
     * @param emailFormat New value of property emailFormat.
     */
    public void setEmailFormat(int emailFormat) {
        this.emailFormat = emailFormat;
    }
    
    /** 
     * Getter for property emailLinefeed.
     *
     * @return Value of property emailLinefeed.
     */
    public int getEmailLinefeed() {
        return this.emailLinefeed;
    }
    
    /**
     * Setter for property emailLinefeed.
     *
     * @param emailLinefeed New value of property emailLinefeed.
     */
    public void setEmailLinefeed(int emailLinefeed) {
        this.emailLinefeed = emailLinefeed;
    }
    
    /**
     * Getter for property mailingType.
     *
     * @return Value of property mailingType.
     */
    public int getMailingType() {
        return this.mailingType;
    }
    
    /**
     * Setter for property mailingType.
     *
     * @param mailingType New value of property mailingType.
     */
    public void setMailingType(int mailingType) {
        this.mailingType = mailingType;
    }
    
    /** 
     * Getter for property targetID.
     *
     * @return Value of property targetID.
     */
    public int getTargetID() {
        return this.targetID;
    }
    
    /**
     * Setter for property targetID.
     *
     * @param targetID New value of property targetID.
     */
    public void setTargetID(int targetID) {
        this.targetID = targetID;
    }
    
    /** 
     * Getter for property mailinglistID.
     *
     * @return Value of property mailinglistID.
     */
    public int getMailinglistID() {
        return this.mailinglistID;
    }
    
    /**
     * Setter for property mailinglistID.
     *
     * @param mailinglistID New value of property mailinglistID.
     */
    public void setMailinglistID(int mailinglistID) {
        this.mailinglistID = mailinglistID;
    }
    
    /**
     * Getter for property templateID.
     *
     * @return Value of property templateID.
     */
    public int getTemplateID() {
        return this.templateID;
    }
    
    /**
     * Setter for property templateID.
     *
     * @param templateID New value of property templateID.
     */
    public void setTemplateID(int templateID) {
        this.templateID = templateID;
    }
    
    /**
     * Getter for property worldMailingSend.
     *
     * @return Value of property worldMailingSend.
     */
    public boolean isWorldMailingSend() {
        return this.worldMailingSend;
    }
    
    /**
     * Setter for property worldMailingSend.
     *
     * @param worldMailingSend New value of property worldMailingSend.
     */
    public void setWorldMailingSend(boolean worldMailingSend) {
        this.worldMailingSend = worldMailingSend;
    }
    
    
    /**
     * Getter for property htmlTemplate.
     * 
     * @return Value of property htmlTemplate.
     */
    public String getHtmlTemplate() {
        return this.htmlTemplate;
    }
    
    /**
     * Setter for property htmlTemplate.
     *
     * @param htmlTemplate New value of property htmlTemplate.
     */
    public void setHtmlTemplate(String htmlTemplate) {
        this.htmlTemplate = htmlTemplate;
    }
    
    /**
     * Getter for property textTemplate.
     *
     * @return Value of property textTemplate.
     */
    public String getTextTemplate() {
        return this.textTemplate;
    }
    
    /**
     * Setter for property textTemplate.
     *
     * @param textTemplate New value of property textTemplate.
     */
    public void setTextTemplate(String textTemplate) {
        this.textTemplate = textTemplate;
    }
    
    /**
     * Getter for property showTemplate.
     *
     * @return Value of property showTemplate.
     */
    public boolean isShowTemplate() {
        return this.showTemplate;
    }
    
    /**
     * Setter for property showTemplate.
     *
     * @param showTemplate New value of property showTemplate.
     */
    public void setShowTemplate(boolean showTemplate) {
        this.showTemplate = showTemplate;
    }
    
    /**
     * Getter for property worldMailingSend.
     *
     * @return Value of property worldMailingSend.
     */
    
    /**
     * Getter for property targetGroups.
     *
     * @return Value of property targetGroups.
     */
    public LinkedList getTargetGroups() {
        return this.targetGroups;
    }
    
    /**
     * Setter for property targetGroups.
     *
     * @param targetGroups New value of property targetGroups.
     */
    public void setTargetGroups(LinkedList targetGroups) {
        this.targetGroups = targetGroups;
    }
    
    /**
     * Getter for property isTemplate.
     *
     * @return Value of property isTemplate.
     */
    public boolean isIsTemplate() {
        return this.isTemplate;
    }
    
    /**
     * Setter for property isTemplate.
     *
     * @param isTemplate New value of property isTemplate.
     */
    public void setIsTemplate(boolean isTemplate) {
        this.isTemplate = isTemplate;
    }
    
    /**
     * Getter for property oldMailingID.
     *
     * @return Value of property oldMailingID.
     */
    public int getOldMailingID() {
        return this.oldMailingID;
    }
    
    /**
     * Setter for property oldMailingID.
     *
     * @param oldMailingID New value of property oldMailingID.
     */
    public void setOldMailingID(int oldMailingID) {
        this.oldMailingID = oldMailingID;
    }
    
    /**
     * Getter for property copyFlag.
     *
     * @return Value of property copyFlag.
     */
    public boolean isCopyFlag() {
        return this.copyFlag;
    }
    
    /**
     * Setter for property copyFlag.
     *
     * @param copyFlag New value of property copyFlag.
     */
    public void setCopyFlag(boolean copyFlag) {
        this.copyFlag = copyFlag;
    }
    
    /**
     * Getter for property needsTarget.
     *
     * @return Value of property needsTarget.
     */
    public boolean isNeedsTarget() {
        return this.needsTarget;
    }
    
    /**
     * Setter for property needsTarget.
     *
     * @param needsTarget New value of property needsTarget.
     */
    public void setNeedsTarget(boolean needsTarget) {
        this.needsTarget = needsTarget;
    }
    
    /**
     * Getter for property targetMode.
     *
     * @return Value of property targetMode.
     */
    public int getTargetMode() {
        return this.targetMode;
    }
    
    /**
     * Setter for property targetMode.
     *
     * @param targetMode New value of property targetMode.
     */
    public void setTargetMode(int targetMode) {
        this.targetMode = targetMode;
    }
    
    /**
     * Getter for property senderEmail.
     *
     * @return Value of property senderEmil.
     */
    public String getEmailSenderEmail() {
        
        return this.emailSenderEmail;
    }
    
    /**
     * Setter for property senderEmail.
     *
     * @param senderEmail New value of property senderEmail.
     */
    public void setEmailSenderEmail(String senderEmail) {
        
        this.emailSenderEmail = senderEmail;
    }
    
    /**
     * Getter for property senderFullname.
     *
     * @return Value of property senderFullname.
     */
    public String getEmailSenderFullname() {
        
        return this.emailSenderFullname;
    }
    
    /**
     * Setter for property senderFullname.
     *
     * @param senderFullname New value of property senderFullname.
     */
    public void setEmailSenderFullname(String senderFullname) {
        
        this.emailSenderFullname = senderFullname;
    }
    
    /**
     * Getter for property replyEmail.
     *
     * @return Value of property replyEmail.
     */
    public String getEmailReplytoEmail() {
        
        return this.emailReplytoEmail;
    }
    
    /**
     * Setter for property replyEmail.
     *
     * @param replyEmail New value of property replyEmail.
     */
    public void setEmailReplytoEmail(String replyEmail) {
        
        this.emailReplytoEmail = replyEmail;
    }
    
    /**
     * Getter for property replyFullname.
     *
     * @return Value of property replyFullname.
     */
    public String getEmailReplytoFullname() {
        
        return this.emailReplytoFullname;
    }
    
    /**
     * Setter for property replyFullname.
     *
     * @param replyFullname New value of property replyFullname.
     */
    public void setEmailReplytoFullname(String replyFullname) {
        
        this.emailReplytoFullname = replyFullname;
    }
    
    /**
     * Holds value of property emailOnepixel.
     */
    private String emailOnepixel;
    
    /**
     * Getter for property emailOnepixel.
     *
     * @return Value of property emailOnepixel.
     */
    public String getEmailOnepixel() {
        
        return this.emailOnepixel;
    }
    
    /**
     * Setter for property emailOnepixel.
     *
     * @param emailOnepixel New value of property emailOnepixel.
     */
    public void setEmailOnepixel(String emailOnepixel) {
        
        this.emailOnepixel = emailOnepixel;
    }  
}
