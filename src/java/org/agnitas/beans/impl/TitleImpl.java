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
import org.agnitas.beans.Title;

public class TitleImpl implements Title, Serializable {
    protected int companyID=-1;
    protected int Id;
    protected String shortname;
    protected String description;
    protected Map titleGender=new HashMap();
   
    // CONSTRUCTOR:
    public TitleImpl() {
    }
    
    // * * * * *
    //  SETTER:
    // * * * * *
    public void setCompanyID(int company) {
        this.companyID=company;
    }
    
    public void setId(int title) {
        this.Id=title;
    }
    
    public void setShortname(String shortname) {
        this.shortname = shortname;
    }
    
    public void setDescription(String desc) {
        this.description = desc;
    }
    
    public void setTitleGender(Map titleGender) {
        this.titleGender=titleGender;
    }

    // * * * * *
    //  GETTER:
    // * * * * *
    public int getCompanyID() {
        return companyID;
    }
    
    public int getId() {
        return this.Id;
    }
    
    public String getShortname() {
        return this.shortname;
    }

    public String getDescription() {
        return this.description;
    }

    public Map getTitleGender() {
        return this.titleGender;
    }
    
    public boolean equals(Object o) {
        if(!getClass().isInstance(o)) {
            return false;
        }

        Title t=(Title) o;

        if(t.getCompanyID() != companyID)
            return false;

        if(t.getId() != this.Id)
            return false;

        return true;
    }

    public int hashCode() {
	Integer i=new Integer((companyID*100)+this.Id);

        return i.hashCode();
    }
}
