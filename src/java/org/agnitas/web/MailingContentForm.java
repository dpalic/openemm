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
import org.apache.struts.action.*;
import org.apache.struts.util.*;
import org.apache.struts.upload.*;
import org.agnitas.util.*;
import java.util.*;
import java.io.*;
import java.net.*;
import org.agnitas.beans.*;

public final class MailingContentForm extends StrutsFormBase {
    
    /** 
     * Holds value of property mailingID. 
     */
    private int mailingID;
    
    /**
     * Holds value of property shortname. 
     */
    private String shortname;
    
    /**
     * Holds value of property action. 
     */
    private int action;
    
    /**
     * Holds value of property NewFile. 
     */
    private FormFile NewFile;
    
    /**
     * Holds value of property isTemplate.
     */
    private boolean isTemplate;
    
    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        
        this.mailingID=0;
        this.shortname=new String("");
        this.contentID=0;
        this.newContent="";
        this.newTargetID=0;
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
     * Holds value of property dynNameID.
     */
    private int dynNameID;

    /**
     * Getter for property dynNameID.
     *
     * @return Value of property dynNameID.
     */
    public int getDynNameID() {

        return this.dynNameID;
    }

    /**
     * Setter for property dynNameID.
     *
     * @param dynNameID New value of property dynNameID.
     */
    public void setDynNameID(int dynNameID) {

        this.dynNameID = dynNameID;
    }

    /**
     * Holds value of property newContent.
     */
    private String newContent;

    /**
     * Getter for property newContent.
     *
     * @return Value of property newContent.
     */
    public String getNewContent() {

        return this.newContent;
    }

    /**
     * Setter for property newContent.
     *
     * @param newContent New value of property newContent.
     */
    public void setNewContent(String newContent) {

        this.newContent = newContent;
    }

    /**
     * Holds value of property newTargetID.
     */
    private int newTargetID;

    /**
     * Getter for property newTargetID.
     *
     * @return Value of property newTargetID.
     */
    public int getNewTargetID() {

        return this.newTargetID;
    }

    /**
     * Setter for property newTargetID.
     *
     * @param newTargetID New value of property newTargetID.
     */
    public void setNewTargetID(int newTargetID) {

        this.newTargetID = newTargetID;
    }

    /**
     * Holds value of property content.
     */
    private java.util.Map content;

    /**
     * Getter for property content.
     *
     * @return Value of property content.
     */
    public java.util.Map getContent() {

        return this.content;
    }

    /**
     * Setter for property content.
     *
     * @param content New value of property content.
     */
    public void setContent(java.util.Map content) {

        this.content = content;
    }

    /**
     * Holds value of property contentID.
     */
    private int contentID;

    /**
     * Getter for property contentID.
     *
     * @return Value of property contentID.
     */
    public int getContentID() {
        return this.contentID;
    }

    /**
     * Setter for property contentID.
     *
     * @param contentID New value of property contentID.
     */
    public void setContentID(int contentID) {
        this.contentID = contentID;
    }

    /**
     * Holds value of property previewFormat.
     */
    private int previewFormat;

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
     * Holds value of property previewSize.
     */
    private int previewSize;

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
     * Holds value of property previewCustomerID.
     */
    private int previewCustomerID;

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

    /**
     * Holds value of property mailFormat.
     */
    private int mailFormat;

    /**
     * Getter for property mailFormat.
     *
     * @return Value of property mailFormat.
     */
    public int getMailFormat() {
        return this.mailFormat;
    }

    /**
     * Setter for property mailFormat.
     *
     * @param mailFormat New value of property mailFormat.
     */
    public void setMailFormat(int mailFormat) {
        this.mailFormat = mailFormat;
    }

    /**
     * Holds value of property dynName.
     */
    private String dynName;

    /**
     * Getter for property dynName.
     *
     * @return Value of property dynName.
     */
    public String getDynName() {
        return this.dynName;
    }

    /**
     * Setter for property dynName.
     *
     * @param dynName New value of property dynName.
     */
    public void setDynName(String dynName) {
        this.dynName = dynName;
    } 
}
