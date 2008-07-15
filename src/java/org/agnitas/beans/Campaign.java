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

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Locale;

import org.springframework.context.ApplicationContext;

public interface Campaign {
	
	public interface Stats {
    	public int getBounces();
        public int getClicks();
        public int getOpened();
        public int getOptouts();
        public int getSubscribers();
    	public Hashtable getMailingData();
        public int getMaxBounces();
        public int getMaxClicks();
        public int getMaxOpened();
        public int getMaxOptouts();
        public int getMaxSubscribers();

        public void setMaxClickRate(double maxClickRate);
        public void setMaxOpenRate(double maxOpenRate);
    };

    public Stats getStats(boolean useMailtracking, Locale aLocale, LinkedList mailingIDs, ApplicationContext con);

    /**
     * Getter for property id.
     * 
     * @return Value of property id.
     */
    int getId();

    /**
     * Getter for property companyID.
     * 
     * @return Value of property companyID.
     */
    int getCompanyID();

    /**
     * Getter for property targetID.
     * 
     * @return Value of property targetID.
     */
    int getTargetID();
    
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
     * Setter for property campaignID.
     *
     * @param id New value of property campaignID.
     */
    void setId(int id);

    /**
     * Setter for property companyID.
     * 
     * @param companyID New value of property companyID.
     */
    void setCompanyID(int companyID);

    /**
     * Setter for property targetID.
     * 
     * @param targetID New value of property targetID.
     */
    void setTargetID(int targetID);
    
    /**
     * Setter for property shortname.
     *
     * @param shortname New value of property shortname.
     */
    void setShortname(String shortname);

    /**
     * Setter for property description.
     *
     * @param description New value of property description.
     */
    void setDescription(String description);
    
    /** Getter for property netto.
     * @return Value of property netto.
     *
     */
    public boolean isNetto();
    
    
    /** Setter for property netto.
     * @param netto New value of property netto.
     *
     */
    public void setNetto(boolean netto);
        
}
