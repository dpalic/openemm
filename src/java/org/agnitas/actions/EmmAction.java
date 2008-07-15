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

package org.agnitas.actions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.context.ApplicationContext;

/**
 *
 * @author mhe
 */
public interface EmmAction extends Serializable {
    /**
     * Adds a ActionOperation to the end of the list of ActionOperations. (ArrayList actions)
     * 
     * @param aAction ActionOperation to be added to this Action
     */
    void addActionOperation(ActionOperation aAction);

    /**
     * Executes all ActionOperations for this Action in ArrayList actions
     * 
     * 
     * @return true==sucess
     * false=error
     * @param con 
     * @param params HashMap containing all available informations
     */
    boolean executeActions(ApplicationContext con, HashMap params);

    /**
     * Getter for property actionID.
     * 
     * @return Value of property actionID.
     */
    int getId();

    /**
     * Getter for property actions.
     * 
     * @return Value of property actions.
     */
    ArrayList getActions();

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
     * Getter for property shortname.
     * 
     * @return Value of property shortname.
     */
    String getShortname();

    /**
     * Getter for property type.
     * 
     * @return Value of property type.
     */
    int getType();

    /**
     * Removes ActionOperation with the given Index from the list of ActionOperations (ArrayList actions)
     * 
     * @param index Index to be removed from ArrayList actions
     * @return true==sucess
     * false==index does not exist
     */
    boolean removeActionOperation(int index);

    /**
     * Setter for property actionID.
     * 
     * @param actionID New value of property actionID.
     */
    void setId(int actionID);

    /**
     * Setter for property actions.
     * 
     * @param actions New value of property actions.
     */
    void setActions(ArrayList actions);

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
     * Setter for property shortname.
     * 
     * @param shortname New value of property shortname.
     */
    void setShortname(String shortname);

    /**
     * Setter for property type.
     * 
     * @param type New value of property type.
     */
    void setType(int type);

    public static final int TYPE_LINK = 0;

    public static final int TYPE_FORM = 1;

    public static final int TYPE_ALL = 9;
    
}
