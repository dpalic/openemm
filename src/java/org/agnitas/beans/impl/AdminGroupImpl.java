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

package org.agnitas.beans.impl;

import org.agnitas.beans.AdminGroup;

public class AdminGroupImpl implements AdminGroup {
    
	private static final long serialVersionUID = 4657656754098173278L;
	protected int companyID;
    protected String shortname;
    
    /**
     * Holds value of property groupID.
     */
    protected int groupID=0;
    
    // CONSTRUCTOR:
    public AdminGroupImpl() {
    }
    
    // * * * * *
    //  SETTER:
    // * * * * *
    public void setCompanyID(int id) {
        companyID=id;
    }
    
    public void setShortname(String name) {
        shortname=name;
    }
      
    public int getCompanyID() {
        return companyID;
    }
    
    public String getShortname() {
        return shortname;
    }
        
    /**
     * Getter for property groupID.
     * @return Value of property groupID.
     */
    public int getGroupID() {
        return this.groupID;
    }
    
    /**
     * Setter for property groupID.
     * @param groupID New value of property groupID.
     */
    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }
        
    /**
     * Holds value of property groupPermissions.
     */
    protected java.util.Set groupPermissions;
    
    /**
     * Getter for property groupPermissions.
     * @return Value of property groupPermissions.
     */
    public java.util.Set getGroupPermissions() {
        
        return this.groupPermissions;
    }
    
    /**
     * Setter for property groupPermissions.
     * @param groupPermissions New value of property groupPermissions.
     */
    public void setGroupPermissions(java.util.Set groupPermissions) {
        
        this.groupPermissions = groupPermissions;
    }
        
    public boolean permissionAllowed(String token) {
        boolean result=false;
        
        if(this.groupPermissions.contains(token)) {
            result=true;
        }
                
        return result;
    }

    /**
     * Holds value of property description.
     */
    protected String description;

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
    
}
