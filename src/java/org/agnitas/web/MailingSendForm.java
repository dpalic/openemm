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
import org.agnitas.stat.DeliveryStat;
import org.apache.struts.action.*;
import org.apache.struts.util.*;
import org.agnitas.util.*;
import org.agnitas.beans.*;
import java.util.*;

public final class MailingSendForm extends StrutsFormBase {
    
    /** 
     * Holds value of property mailingID. 
     */
    protected int mailingID;
    
    /**
     * Holds value of property shortname. 
     */
    protected String shortname;
    
    /**
     * Holds value of property description. 
     */
    protected String description;
    
    /**
     * Holds value of property emailFormat. 
     */
    protected int emailFormat;
    
    /**
     * Holds value of property action. 
     */
    protected int action;
    
    /**
     * Holds value of property previewCustomerID. 
     */
    protected int previewCustomerID;
    
    /**
     * Holds value of property textPreview. 
     */
    protected String textPreview;
    
    /**
     * Holds value of property previewFormat. 
     */
    protected int previewFormat;
    
    /**
     * Holds value of property previewSize. 
     */
    protected int previewSize=1;
    
    /**
     * Holds value of property subjectPreview. 
     */
    protected String subjectPreview;
    
    /**
     * Holds value of property senderPreview. 
     */
    protected String senderPreview;
    
    /**
     * Holds value of property sendStatText. 
     */
    protected int sendStatText;
    
    /**
     * Holds value of property sendStatHtml. 
     */
    protected int sendStatHtml;
    
    /**
     * Holds value of property sendStatOffline. 
     */
    protected int sendStatOffline;
    
    /**
     * Holds value of property sendStatAll. 
     */
    protected int sendStatAll;
    
    /**
     * Holds value of property isTemplate.
     */
    protected boolean isTemplate;
    
    /**
     * Holds value of property deliveryStat.
     */
    protected DeliveryStat deliveryStat;
    
    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        try {
            TimeZone aZone=TimeZone.getTimeZone(AgnUtils.getAdmin(request).getAdminTimezone());
            GregorianCalendar aDate=new GregorianCalendar(aZone);
            this.sendHour=aDate.get(GregorianCalendar.HOUR_OF_DAY);
            this.sendMinute=aDate.get(GregorianCalendar.MINUTE);
            this.previewFormat=1;
        } catch (Exception e) {
            // do nothing
        }
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
        
        return errors;
    }
    
    /** 
     * Getter for property mailingID.
     *
     * @return Value of property mailingID.
     */
    public int getMailingID() {
        return this.mailingID;
    }
    
    /**
     * Setter for property mailingID.
     *
     * @param mailingID New value of property mailingID.
     */
    public void setMailingID(int mailingID) {
        this.mailingID = mailingID;
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
     * Getter for property previewCustomerID.
     *
     * @return Value of property previewCustomerID.
     */
    public int getPreviewCustomerID() {
        return this.previewCustomerID;
    }
    
    /**
     * Setter for property previewCustomerID.
     *
     * @param previewCustomerID New value of property previewCustomerID.
     */
    public void setPreviewCustomerID(int previewCustomerID) {
        this.previewCustomerID = previewCustomerID;
    }
    
    /**
     * Getter for property textPreview.
     *
     * @return Value of property textPreview.
     */
    public String getTextPreview() {
        return this.textPreview;
    }
    
    /**
     * Setter for property textPreview.
     *
     * @param textPreview New value of property textPreview.
     */
    public void setTextPreview(String textPreview) {
        this.textPreview = textPreview;
    }
    
    /**
     * Getter for property previewFormat.
     *
     * @return Value of property previewFormat.
     */
    public int getPreviewFormat() {
        return this.previewFormat;
    }
    
    /**
     * Setter for property previewFormat.
     *
     * @param previewFormat New value of property previewFormat.
     */
    public void setPreviewFormat(int previewFormat) {
        this.previewFormat = previewFormat;
    }
    
    /**
     * Getter for property previewSize.
     *
     * @return Value of property previewSize.
     */
    public int getPreviewSize() {
        return this.previewSize;
    }
    
    /**
     * Setter for property previewSize.
     *
     * @param previewSize New value of property previewSize.
     */
    public void setPreviewSize(int previewSize) {
        this.previewSize = previewSize;
    }
    
    /**
     * Getter for property subjectPreview.
     *
     * @return Value of property subjectPreview.
     */
    public String getSubjectPreview() {
        return this.subjectPreview;
    }
    
    /** 
     * Setter for property subjectPreview.
     *
     * @param subjectPreview New value of property subjectPreview.
     */
    public void setSubjectPreview(String subjectPreview) {
        this.subjectPreview = subjectPreview;
    }
    
    /**
     * Getter for property senderPreview.
     *
     * @return Value of property senderPreview.
     */
    public String getSenderPreview() {
        return this.senderPreview;
    }
    
    /**
     * Setter for property senderPreview.
     *
     * @param senderPreview New value of property senderPreview.
     */
    public void setSenderPreview(String senderPreview) {
        this.senderPreview = senderPreview;
    }
    
    /** 
     * Getter for property sendStatText.
     *
     * @return Value of property sendStatText.
     */
    public int getSendStatText() {
        return this.sendStatText;
    }
    
    /**
     * Setter for property sendStatText.
     *
     * @param sendStatText New value of property sendStatText.
     */
    public void setSendStatText(int sendStatText) {
        this.sendStatText = sendStatText;
    }
    
    /** 
     * Getter for property sendStatHtml.
     *
     * @return Value of property sendStatHtml.
     */
    public int getSendStatHtml() {
        return this.sendStatHtml;
    }
    
    /**
     * Setter for property sendStatHtml.
     *
     * @param sendStatHtml New value of property sendStatHtml.
     */
    public void setSendStatHtml(int sendStatHtml) {
        this.sendStatHtml = sendStatHtml;
    }
    
    /**
     * Getter for property sendStatOffline.
     *
     * @return Value of property sendStatOffline.
     */
    public int getSendStatOffline() {
        return this.sendStatOffline;
    }
    
    /**
     * Setter for property sendStatOffline.
     *
     * @param sendStatOffline New value of property sendStatOffline.
     */
    public void setSendStatOffline(int sendStatOffline) {
        this.sendStatOffline = sendStatOffline;
    }
    
    /**
     * Getter for property sendStatAll.
     *
     * @return Value of property sendStatAll.
     */
    public int getSendStatAll() {
        return this.sendStatAll;
    }
    
    /**
     * Setter for property sendStatAll.
     *
     * @param sendStatAll New value of property sendStatAll.
     */
    public void setSendStatAll(int sendStatAll) {
        this.sendStatAll = sendStatAll;
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
     * Getter for property deliveryStat.
     *
     * @return Value of property deliveryStat.
     */
    public DeliveryStat getDeliveryStat() {
        
        return this.deliveryStat;
    }
    
    /**
     * Setter for property deliveryStat.
     *
     * @param deliveryStat New value of property deliveryStat.
     */
    public void setDeliveryStat(DeliveryStat deliveryStat) {
        
        this.deliveryStat = deliveryStat;
    }
    
    /**
     * Holds value of property mailing.
     */
    private Mailing mailing;
    
    /**
     * Getter for property mailing.
     *
     * @return Value of property mailing.
     */
    public Mailing getMailing() {
        return this.mailing;
    }
    
    /**
     * Setter for property mailing.
     *
     * @param mailing New value of property mailing.
     */
    public void setMailing(Mailing mailing) {
        this.mailing = mailing;
    }
    
    /**
     * Holds value of property worldMailingSend.
     */
    private boolean worldMailingSend;
    
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
     * Holds value of property mailingtype.
     */
    private int mailingtype;
    
    /**
     * Getter for property mailingtype.
     *
     * @return Value of property mailingtype.
     */
    public int getMailingtype() {
        return this.mailingtype;
    }
    
    /**
     * Setter for property mailingtype.
     *
     * @param mailingtype New value of property mailingtype.
     */
    public void setMailingtype(int mailingtype) {
        this.mailingtype = mailingtype;
    }
    
    /**
     * Holds value of property sendDate.
     */
    private String sendDate;
    
    /**
     * Getter for property sendDate.
     *
     * @return Value of property sendDate.
     */
    public String getSendDate() {
        return this.sendDate;
    }
    
    /**
     * Setter for property sendDate.
     *
     * @param sendDate New value of property sendDate.
     */
    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }
    
    /**
     * Holds value of property sendHour.
     */
    private int sendHour;
    
    /**
     * Getter for property sendHour.
     *
     * @return Value of property sendHour.
     */
    public int getSendHour() {
        return this.sendHour;
    }
    
    /**
     * Setter for property sendHour.
     *
     * @param sendHour New value of property sendHour.
     */
    public void setSendHour(int sendHour) {
        this.sendHour = sendHour;
    }
    
    /**
     * Holds value of property sendMinute.
     */
    private int sendMinute;
    
    /**
     * Getter for property sendMinute.
     *
     * @return Value of property sendMinute.
     */
    public int getSendMinute() {
        return this.sendMinute;
    }
    
    /**
     * Setter for property sendMinute.
     *
     * @param sendMinute New value of property sendMinute.
     */
    public void setSendMinute(int sendMinute) {
        this.sendMinute = sendMinute;
    }
    
    /**
     * Holds value of property targetGroups.
     */
    private Collection targetGroups;
    
    /**
     * Getter for property targetGroups.
     *
     * @return Value of property targetGroups.
     */
    public Collection getTargetGroups() {
        return this.targetGroups;
    }
    
    /**
     * Setter for property targetGroups.
     *
     * @param targetGroups New value of property targetGroups.
     */
    public void setTargetGroups(Collection targetGroups) {
        this.targetGroups = targetGroups;
    }
    
    /**
     * Holds value of property htmlPreview.
     */
    private String htmlPreview;
    
    /**
     * Getter for property htmlPreview.
     *
     * @return Value of property htmlPreview.
     */
    public String getHtmlPreview() {
        return this.htmlPreview;
    }
    
    /**
     * Setter for property htmlPreview.
     *
     * @param htmlPreview New value of property htmlPreview.
     */
    public void setHtmlPreview(String htmlPreview) {
        this.htmlPreview = htmlPreview;
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
     * Holds value of property mailinglistID.
     */
    private int mailinglistID;
    
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
}
