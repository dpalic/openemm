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

package org.agnitas.beans;

import java.util.HashMap;

import org.agnitas.actions.EmmAction;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author mhe
 */
public interface UserForm {
    boolean evaluateEndAction(ApplicationContext con, HashMap params);

    String evaluateForm(ApplicationContext con, HashMap params);

    boolean evaluateStartAction(ApplicationContext con, HashMap params);

    /**
     * Getter for property companyID.
     * 
     * @return Value of property companyID.
     */
    int getCompanyID();

    /**
     * Getter for property description.
     * 
     * @return Value of property description.
     */
    String getDescription();

    /**
     * Getter for property endAction.
     * 
     * @return Value of property endAction.
     */
    EmmAction getEndAction();

    /**
     * Getter for property endActionID.
     * 
     * @return Value of property endActionID.
     */
    int getEndActionID();

    /**
     * Getter for property errorTemplate.
     * 
     * @return Value of property errorTemplate.
     */
    String getErrorTemplate();

    /**
     * Getter for property formID.
     * 
     * @return Value of property formID.
     */
    int getId();

    /**
     * Getter for property formName.
     * 
     * @return Value of property formName.
     */
    String getFormName();

    /**
     * Getter for property startAction.
     * 
     * @return Value of property startAction.
     */
    EmmAction getStartAction();

    /**
     * Getter for property startActionID.
     * 
     * @return Value of property startActionID.
     */
    int getStartActionID();

    /**
     * Getter for property sucessTemplate.
     * 
     * @return Value of property sucessTemplate.
     */
    String getSuccessTemplate();

    /**
     * Setter for property companyID.
     * 
     * @param companyID New value of property companyID.
     */
    void setCompanyID(int companyID);

    /**
     * Setter for property description.
     * 
     * @param description New value of property description.
     */
    void setDescription(String description);

    /**
     * Setter for property endAction.
     * 
     * @param endAction New value of property endAction.
     */
    void setEndAction(EmmAction endAction);

    /**
     * Setter for property endActionID.
     * 
     * @param endActionID New value of property endActionID.
     */
    void setEndActionID(int endActionID);

    /**
     * Setter for property errorTemplate.
     * 
     * @param errorTemplate New value of property errorTemplate.
     */
    void setErrorTemplate(String errorTemplate);

    /**
     * Setter for property formID.
     * 
     * @param formID New value of property formID.
     */
    void setId(int formID);

    /**
     * Setter for property formName.
     * 
     * @param formName New value of property formName.
     */
    void setFormName(String formName);

    /**
     * Setter for property startAction.
     * 
     * @param startAction New value of property startAction.
     */
    void setStartAction(EmmAction startAction);

    /**
     * Setter for property startActionID.
     * 
     * @param startActionID New value of property startActionID.
     */
    void setStartActionID(int startActionID);

    /**
     * Setter for property sucessTemplate.
     * 
     * @param successTemplate 
     */
    void setSuccessTemplate(String successTemplate);
    
}
