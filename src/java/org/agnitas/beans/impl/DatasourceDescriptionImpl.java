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

import java.util.Date;

import org.agnitas.beans.DatasourceDescription;

public class DatasourceDescriptionImpl implements DatasourceDescription {
    protected int id=-1;
    protected int companyID=-1;
    protected int sourcegroupID;
    protected String description;
    protected Date changeDate=null;
    protected Date creationDate=null;
   
    // CONSTRUCTOR:
    public DatasourceDescriptionImpl() {
    }
    
    // * * * * *
    //  SETTER:
    // * * * * *
    public void setId(int id) {
        this.id=id;
    }
    
    public void setCompanyID(int company) {
        this.companyID=company;
    }
    
    public void setSourcegroupID(int sourcegroup) {
        this.sourcegroupID=sourcegroup;
    }
    
    public void setDescription(String desc) {
        this.description = desc;
    }
    
    public void setChangeDate(Date changeDate) {
        this.changeDate=changeDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate=creationDate;
    }

    // * * * * *
    //  GETTER:
    // * * * * *
    public int getId() {
        return id;
    }
    
    public int getCompanyID() {
        return companyID;
    }
    
    public int getSourcegroupID() {
        return sourcegroupID;
    }
    
    public String getDescription() {
        return this.description;
    }

    public Date getChangeDate() {
        return this.changeDate;
    }
    
    public Date getCreationDate() {
        return this.creationDate;
    }
    
}
