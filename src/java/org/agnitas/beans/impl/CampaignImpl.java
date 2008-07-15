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
import java.net.*;
import java.util.*;
import java.text.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import sun.net.smtp.*;
import org.agnitas.beans.Campaign;



public class CampaignImpl implements Campaign {
    
    /** Holds value of property id. */
    private int id;
    
    /** Holds value of property companyID. */
    private int companyID;
    
    /** Holds value of property targetID. */
    private int targetID;    
    
    /** Holds value of property netto. */    
    private boolean netto;

    /** Holds value of property shortname. */
    private String shortname;
    
    /** Holds value of property description. */
    private String description;
    
    // CONSTRUCTORS:
    public CampaignImpl() {
        id = 0;
        companyID = 0;
    }
    
    // automatically generated
    // get & set methods:

    public int getId() {
        return id;
    }
    
    public int getCompanyID() {
        return companyID;
    }
    
    public int getTargetID() {
        return targetID;
    }
    
    public String getShortname() {
        return this.shortname;
    }
    
    public String getDescription() {
        return description;
    }

    /** Getter for property netto.
     * @return Value of property netto.
     *
     */
    public boolean isNetto() {
        return this.netto;
    }
    
    /** Setter for property netto.
     * @param netto New value of property netto.
     *
     */
    public void setNetto(boolean netto) {
        this.netto = netto;
    }    
    
    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }
    
    public void setTargetID(int targetID) {
        this.targetID = targetID;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setShortname(String shortname) {
        this.shortname = shortname;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
}
