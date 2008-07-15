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

import java.io.Serializable;

import org.springframework.context.ApplicationContextAware;

/**
 * 
 * @author Eduard Scherer
 */
public interface Mailinglist extends ApplicationContextAware, Serializable {

    /**
     * Removes bindings from database.
     */
    boolean deleteBindings();
   
   /**
     * Setter for property companyID.
     *
     * @param id New value of propety companyID.
     */
    void setCompanyID(int id);
    
    /**
     * Setter for property id.
     *
     * @param id New value of propety id.
     */
    void setId(int id);
    
    /**
     * Setter for property shortname.
     *
     * @param shortname New value of propety shortname.
     */
    void setShortname(String shortname);
    
    /**
     * Setter for property description.
     *
     * @param description New value of propety description.
     */
    void setDescription(String description);
    
     /**
     * Getter for property companyID.
     *
     * @return Value of property companyID.
     */
    int getCompanyID();
    
     /**
     * Getter for property id.
     *
     * @return Value of property id.
     */
    int getId();
    
     /**
     * Getter for property shortname.
     *
     * @return Value of property shortname.
     */
    String getShortname();
    
     /**
     * Getter for property description.
     *
     * @return Value of property description.
     */
    String getDescription();
    
    /**
     * Getter for property numberOfActiveSubscribers.
     *
     * @return Value of property numberOfActiveSubscribers.
     */
    int getNumberOfActiveSubscribers(boolean admin, boolean test, boolean world, int targetID);
}