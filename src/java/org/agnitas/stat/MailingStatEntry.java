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

package org.agnitas.stat;

import java.io.Serializable;
import java.util.Hashtable;

/**
 *
 * @author mhe
 */
public interface MailingStatEntry extends Serializable {

    /**
     * Getter for property bounces.
     * 
     * @return Value of property bounces.
     */
    int getBounces();

    /**
     * Getter for property clickStatsValues.
     * 
     * @return Value of property clickStatsValues.
     */
    Hashtable getClickStatValues();

    /**
     * Getter for property maxNRblue.
     * 
     * @return Value of property maxNRblue.
     */
    int getMaxNRblue();

    /**
     * Getter for property maxblue.
     * 
     * @return Value of property maxblue.
     */
    int getMaxblue();

    /**
     * Getter for property opened.
     * 
     * @return Value of property opened.
     */
    int getOpened();

    /**
     * Getter for property optouts.
     * 
     * @return Value of property optouts.
     */
    int getOptouts();

    /**
     * Getter for property targetName.
     * 
     * @return Value of property targetName.
     */
    String getTargetName();

    /**
     * Getter for property totalClickSubscribers.
     * 
     * @return Value of property totalClickSubscribers.
     */
    int getTotalClickSubscribers();

    /**
     * Getter for property totalClicks.
     * 
     * @return Value of property totalClicks.
     */
    int getTotalClicks();

    /**
     * Getter for property totalClicksNetto.
     * 
     * @return Value of property totalClicksNetto.
     */
    int getTotalClicksNetto();

    /**
     * Getter for property totalMails.
     * 
     * @return Value of property totalMails.
     */
    int getTotalMails();

    /**
     * Setter for property bounces.
     * 
     * @param bounces New value of property bounces.
     */
    void setBounces(int bounces);

    /**
     * Setter for property clickStatValues.
     * 
     * @param clickStatValues New value of property clickStatValues.
     */
    void setClickStatValues(Hashtable clickStatValues);

    /**
     * Setter for property maxNRblue.
     * 
     * @param maxNRblue New value of property maxNRblue.
     */
    void setMaxNRblue(int maxNRblue);

    /**
     * Setter for property maxblue.
     * 
     * @param maxblue New value of property maxblue.
     */
    void setMaxblue(int maxblue);

    /**
     * Setter for property opnened.
     *
     * @param opened New value of property opened.
     */
    void setOpened(int opened);

    /**
     * Setter for property optouts.
     *
     * @param optouts New value of property optouts.
     */
    void setOptouts(int optouts);

    /**
     * Setter for property targetName.
     * 
     * @param targetName New value of property targetName.
     */
    void setTargetName(String targetName);

    /**
     * Setter for property totalClickSubscribers.
     *
     * @param totalClickSubscribers New value of property totalClickSubscribers.
     */
    void setTotalClickSubscribers(int totalClickSubscribers);

    /**
     * Setter for property totalClicks.
     *
     * @param totalClicks New value of property totalClicks.
     */
    void setTotalClicks(int totalClicks);

    /**
     * Setter for property totalClicksNetto.
     *
     * @param totalClicksNetto New value of property totlClicksNetto.
     */
    void setTotalClicksNetto(int totalClicksNetto);

    /**
     * Setter for property totalMails.
     *
     * @param totalMails New value of property totalMails.
     */
    void setTotalMails(int totalMails);
    
}
