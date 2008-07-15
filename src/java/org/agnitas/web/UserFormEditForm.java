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
import javax.mail.internet.InternetAddress;

/**
 * Implementation of <strong>Form</strong> that holds data for user forms.
 * @author ar, mhe
 */
public final class UserFormEditForm extends ActionForm {
    
    /** Holds value of property action. */
    private int action;
    
    /** Holds value of property formID. */
    private int formID;
    
    /** Holds value of property formName. */
    private String formName;
    
    /** Holds value of property description. */
    private String description;
    
    /** Holds value of property startActionID. */
    private int startActionID;
    
    /** Holds value of property endActionID. */
    private int endActionID;
    
    /** Holds value of property successTemplate. */
    private String successTemplate;
    
    /** Holds value of property errorTemplate. */
    private String errorTemplate;
    
    /**
     * Validate the properties that have been set from this HTTP request,
     * and return an <code>ActionErrors</code> object that encapsulates any
     * validation errors that have been found.  If no errors are found, return
     * <code>null</code> or an <code>ActionErrors</code> object with no
     * recorded error messages.
     * 
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     * @return messages for errors, that occured. 
     */
    public ActionErrors validate(ActionMapping mapping,
    HttpServletRequest request) {
        
        ActionErrors errors = new ActionErrors();
                
        if(!errors.isEmpty()) {
            mapping.setInput(mapping.findForward("view").getPath());
        }
        
        return errors;
    }
    
    /** Getter for property action.
     * @return Value of property action.
     */
    public int getAction() {
        return this.action;
    }
    
    /** Setter for property action.
     * @param action New value of property action.
     */
    public void setAction(int action) {
        this.action = action;
    }
    
    /** Getter for property fontID.
     * @return Value of property fontID.
     *
     */
    public int getFormID() {
        return this.formID;
    }
    
    /**
     * Setter for property fontID.
     * 
     * @param formID 
     */
    public void setFormID(int formID) {
        this.formID = formID;
    }
    
    /** Getter for property fontName.
     * @return Value of property fontName.
     *
     */
    public String getFormName() {
        return this.formName;
    }
    
    /**
     * Setter for property fontName.
     * 
     * @param formName 
     */
    public void setFormName(String formName) {
        this.formName = formName;
    }
    
    /**
     * Getter for property description.
     * @return Value of property description.
     */
    public String getDescription() {
        return this.description;
    }
    
    /**
     * Setter for property description.
     * @param description New value of property description.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Getter for property startActionID.
     * @return Value of property startActionID.
     */
    public int getStartActionID() {
        return this.startActionID;
    }
    
    /**
     * Setter for property startActionID.
     * @param startActionID New value of property startActionID.
     */
    public void setStartActionID(int startActionID) {
        this.startActionID = startActionID;
    }
    
    /**
     * Getter for property endActionID.
     * @return Value of property endActionID.
     */
    public int getEndActionID() {
        return this.endActionID;
    }
    
    /**
     * Setter for property endActionID.
     * @param endActionID New value of property endActionID.
     */
    public void setEndActionID(int endActionID) {
        this.endActionID = endActionID;
    }
    
    /**
     * Getter for property successTemplate.
     * @return Value of property successTemplate.
     */
    public String getSuccessTemplate() {
        return this.successTemplate;
    }
    
    /**
     * Setter for property successTemplate.
     * @param successTemplate New value of property successTemplate.
     */
    public void setSuccessTemplate(String successTemplate) {
        this.successTemplate = successTemplate;
    }
    
    /**
     * Getter for property errorTemplate.
     * @return Value of property errorTemplate.
     */
    public String getErrorTemplate() {
        return this.errorTemplate;
    }
    
    /**
     * Setter for property errorTemplate.
     * @param errorTemplate New value of property errorTemplate.
     */
    public void setErrorTemplate(String errorTemplate) {
        this.errorTemplate = errorTemplate;
    }
    
}
