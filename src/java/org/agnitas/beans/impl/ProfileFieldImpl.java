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

import java.io.*;
import java.util.*;
import org.agnitas.beans.ProfileField;

public class ProfileFieldImpl implements ProfileField, Serializable {
    protected int companyID=-1;
    protected String column;
    protected int adminID=0;
    protected String shortname="";
    protected String description="";
    protected String defaultValue="";
    protected int modeEdit=0;
    protected int modeInsert=0;
   
    // CONSTRUCTOR:
    public ProfileFieldImpl() {
    }
    
    // * * * * *
    //  SETTER:
    // * * * * *
    public void setCompanyID(int company) {
        this.companyID=company;
    }
    
    public void setColumn(String column) {
        this.column = column;
    }
    
    public void setAdminID(int adminID) {
        this.adminID=adminID;
    }
    
    public void setShortname(String shortname) {
        if(shortname == null) {
            shortname="";
        }
        this.shortname = shortname;
    }
    
    public void setDescription(String desc) {
        if(desc == null) {
            desc="";
        }
        this.description = desc;
    }
    
    public void setDefaultValue(String value) {
        if(value == null) {
            value="";
        }
        this.defaultValue = value;
    }
    
    public void setModeEdit(int modeEdit) {
        this.modeEdit=modeEdit;
    }
    
    public void setModeInsert(int modeInsert) {
        this.modeInsert=modeInsert;
    }
    
    // * * * * *
    //  GETTER:
    // * * * * *
    public int getCompanyID() {
        return companyID;
    }
    
    public String getColumn() {
        return column;
    }

    public int getAdminID() {
        return adminID;
    }
    
    public String getShortname() {
        return shortname;
    }

    public String getDescription() {
        return description;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public int getModeEdit() {
        return modeEdit;
    }
    
    public int getModeInsert() {
        return modeInsert;
    }
    
    public boolean equals(Object o) {
        if(!getClass().isInstance(o)) {
            return false;
        }

        ProfileField f=(ProfileField) o;

        if(f.getCompanyID() != companyID)
            return false;

        if(!f.getColumn().equals(column))
            return false;

        return true;
    }

    public int hashCode() {
        Integer i=new Integer(companyID);

        return i.hashCode()*column.hashCode();
    }
}
