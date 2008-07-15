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

import java.io.*;
import java.util.*;

public interface DatasourceDescription {
    /**
     * Setter for property id.
     *
     * @param id New value of property id.
     */
    public void setId(int id);

    /**
     * Setter for property companyID.
     *
     * @param id New value of property companyID.
     */
    public void setCompanyID(int id);
    
    /**
     * Setter for property sourcegroupID.
     *
     * @param title New value of property sourcegroupID.
     */
    public void setSourcegroupID(int title);
    
    /**
     * Setter for property description.
     *
     * @param desc New value of property description.
     */
    public void setDescription(String desc);

    /**
     * Setter for property changeDate.
     *
     * @param changeDate New value of property changeDate.
     */
    public void setChangeDate(Date changeDate);

    /**
     * Setter for property creationDate.
     *
     * @param creationDate New value of property creationDate.
     */
    public void setCreationDate(Date creationDate);

    /**
     * Getter for property id.
     * 
     * @return Value of property id.
     */
    public int getId();

    /**
     * Getter for property companyID.
     * 
     * @return Value of property companyID.
     */
    public int getCompanyID();
    
    /**
     * Getter for property sourcegroupID.
     * 
     * @return Value of property sourcegroupID.
     */
    public int getSourcegroupID();
    
    /**
     * Getter for property description.
     * 
     * @return Value of property description.
     */
    public String getDescription();

    /**
     * Getter for property changeDate.
     * 
     * @return Value of property changeDate.
     */
    public Date getChangeDate();

    /**
     * Getter for property creationDate.
     * 
     * @return Value of property creationDate.
     */
    public Date getCreationDate();
}
