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

/**
 *
 * @author  mhe
 */
public class SalutationForm extends ActionForm {
    
    /** 
     * Holds value of property mailinglistID. 
     */
    private int salutationID;
    
    /**
     * Holds value of property description. 
     */
    private String description;
    
    /**
     * Holds value of property fromEmail. 
     */
    private String fromEmail;
    
    /**
     * Holds value of property action. 
     */
    private int action;
    
    /**
     * Holds value of property shortname. 
     */
    private String shortname;
    
    /**
     * Holds value of property salMale. 
     */
    private String salMale;
    
    /**
     * Holds value of property salFemale.
     */
    private String salFemale;
    
    /**
     * Holds value of property salUnknown. 
     */
    private String salUnknown;
    
    /**
     * Holds value of property salCompany. 
     */
    private String salCompany;
    
    /**
     * Holds value of property salMiss. 
     */
    private String salMiss;
    
    /**
     * Holds value of property salPractice. 
     */
    private String salPractice;
    
    /**
     * Creates a new instance of MailinglistForm 
     */
    public SalutationForm() {
    }
    
    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        
        this.salutationID = 0;
        Locale aLoc=(Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
        //MessageResources text=this.getServlet().getResources();
        MessageResources text=(MessageResources)this.getServlet().getServletContext().getAttribute(org.apache.struts.Globals.MESSAGES_KEY);
        
        this.shortname=text.getMessage(aLoc, "default.salutation.shortname");
        this.description=text.getMessage(aLoc, "default.salutation.description");
        this.salCompany=new String("");
        this.salMiss=new String("");
        this.salPractice=new String("");
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
        if(action==SalutationAction.ACTION_SAVE || action==SalutationAction.ACTION_NEW) {
            if(this.shortname.length()<3)
                errors.add("shortname", new ActionMessage("error.nameToShort"));
        }
        
        return errors;
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
     * Getter for property salutationID.
     *
     * @return Value of property salutationID.
     */
    public int getSalutationID() {
        return this.salutationID;
    }
    
    /**
     * Setter for property salutationID.
     *
     * @param salutationID New value of property salutationID.
     */
    public void setSalutationID(int salutationID) {
        this.salutationID = salutationID;
    }
    
    /**
     * Getter for property salMale.
     *
     * @return Value of property salMale.
     */
    public String getSalMale() {
        return this.salMale;
    }
    
    /**
     * Setter for property salMale.
     *
     * @param salMale New value of property salMale.
     */
    public void setSalMale(String salMale) {
        this.salMale = salMale;
    }
    
    /**
     * Getter for property salFemale.
     *
     * @return Value of property salFemale.
     */
    public String getSalFemale() {
        return this.salFemale;
    }
    
    /**
     * Setter for property salFemale.
     *
     * @param salFemale New value of property salFemale.
     */
    public void setSalFemale(String salFemale) {
        this.salFemale = salFemale;
    }
    
    /**
     * Getter for property salUnknown.
     *
     * @return Value of property salUnknown.
     */
    public String getSalUnknown() {
        return this.salUnknown;
    }
    
    /**
     * Setter for property salUnknown.
     *
     * @param salUnknown New value of property salUnknown.
     */
    public void setSalUnknown(String salUnknown) {
        this.salUnknown = salUnknown;
    }
    
    /**
     * Getter for property salCompany.
     *
     * @return Value of property salCompany.
     */
    public String getSalCompany() {
        return this.salCompany;
    }
    
    /**
     * Setter for property salCompany.
     *
     * @param salCompany New value of property salCompany.
     */
    public void setSalCompany(String salCompany) {
        this.salCompany = salCompany;
    }
    
    /**
     * Getter for property salMiss.
     *
     * @return Value of property salMiss.
     */
    public String getSalMiss() {
        return this.salMiss;
    }
    
    /**
     * Setter for property salMiss.
     *
     * @param salMiss New value of property salMiss.
     */
    public void setSalMiss(String salMiss) {
        this.salMiss = salMiss;
    }
    
    /**
     * Getter for property salPractice.
     *
     * @return Value of property salPractice.
     */
    public String getSalPractice() {
        return this.salPractice;
    }
    
    /**
     * Setter for property salPractice.
     *
     * @param salPractice New value of property salPractice.
     */
    public void setSalPractice(String salPractice) {
        this.salPractice = salPractice;
    }   
}