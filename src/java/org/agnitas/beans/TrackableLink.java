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
import org.springframework.context.ApplicationContext;
import java.io.Serializable;

/**
 *
 * @author mhe
 */
public interface TrackableLink extends Serializable {
    
    int TRACKABLE_NONE = 0;

    int TRACKABLE_ONLY_HTML = 2;

    int TRACKABLE_ONLY_TEXT = 1;

    int TRACKABLE_TEXT_HTML = 3;

    /**
     * Getter for property actionID.
     * 
     * @return Value of property actionID.
     */
    int getActionID();

    /**
     * Getter for property companyID.
     * 
     * @return Value of property companyID.
     */
    int getCompanyID();

    /**
     * Getter for property fullUrl.
     * 
     * @return Value of property fullUrl.
     */
    String getFullUrl();

    /**
     * Getter for property mailingID.
     * 
     * @return Value of property mailingID.
     */
    int getMailingID();

    /**
     * Getter for property shortname.
     * 
     * @return Value of property shortname.
     */
    String getShortname();

    /**
     * Getter for property urlID.
     * 
     * @return Value of property urlID.
     */
    int getId();

    /**
     * Getter for property usage.
     * 
     * @return Value of property usage.
     */
    int getUsage();

    /**
     * Logs the customerclick in database.
     */
    boolean logClickInDB(int customerID, String remoteAddr, ApplicationContext con);

    /**
     * Performes the action behind the clicked link.
     */
    boolean performLinkAction(HashMap params, int customerID, ApplicationContext con);

    /**
     * Personalizes the person who clicked on the link.
     */
    String personalizeLink(int customerID, String orgUID, ApplicationContext con);

     /**
     * Setter for property actionID.
     * 
     * @param id New value of property actionID.
     */
    void setActionID(int id);

     /**
     * Setter for property companysID.
     * 
     * @param id New value of property companyID.
     */
    void setCompanyID(int id);

     /**
     * Setter for property fullUrl.
     * 
     * @param url New value of property fullUrl.
     */
    void setFullUrl(String url);

     /**
     * Setter for property mailingID.
     * 
     * @param id New value of property mailingID.
     */
    void setMailingID(int id);

    /**
     * Setter for property shortname.
     * 
     * @param shortname New value of property shortname.
     */
    void setShortname(String shortname);

    void setId(int id);

    /**
     * Setter for property usage.
     * 
     * @param usage New value of property usage.
     */
    void setUsage(int usage);

    /**
     * Getter for property relevance.
     *
     * @return Value of property relevance.
     */
    public int getRelevance();

    /**
     * Setter for property relevance.
     *
     * @param relevance New value of property relevance.
     */
    public void setRelevance(int relevance);
    
}
