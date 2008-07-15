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

package org.agnitas.util;

/**
 *
 * @author mhe
 */
public interface UID {
    
    /**
     * Create a signature when all parameter are set
     * 
     * @return signature as string
     */
    String createSignature(String base) throws Exception;

    /**
     * Getter for property companyID.
     * 
     * @return Value of property companyID.
     */
    long getCompanyID();

    /**
     * Getter for property customerID.
     * 
     * @return Value of property customerID.
     */
    long getCustomerID();

    /**
     * Getter for property mailingID.
     * 
     * @return Value of property mailingID.
     */
    long getMailingID();

    /**
     * Getter for property password.
     * 
     * @return Value of property password.
     */
    String getPassword();

    /**
     * Getter for property prefix.
     * 
     * @return Value of property prefix.
     */
    String getPrefix();

    /**
     * Getter for property URLID.
     * 
     * @return Value of property URLID.
     */
    long getURLID();

    /**
     * Create the base UID string
     * 
     * @return the UID
     */
    String makeBaseUID();

    /**
     * Create the pure signature
     * 
     * @param s the string to sign
     * @return the signature
     */
    String makeSignature(String s) throws Exception;

    /**
     * Make the final UID string
     * 
     * @return UID as string
     */
    String makeUID() throws Exception;

    /**
     * Make the final UID string using given customer id and URL ID
     * 
     * @param customerID the customer ID to use
     * @param URLID the URL ID to use
     * @return UID as string
     */
    String makeUID(long customerID, long URLID) throws Exception;

    /**
     * Parses an uid
     */
    void parseUID(String uid) throws Exception;

    /**
     * Setter for property companyID.
     * 
     * @param companyID New value for property companyID.
     */
    void setCompanyID(long companyID);

    /**
     * Setter for property customerID.
     * 
     * @param customerID New value of property customerID.
     */
    void setCustomerID(long customerID);

    /**
     * Setter for property mailingID.
     * 
     * @param mailingID new value of property mailingID.
     */
    void setMailingID(long mailingID);

    /**
     * Setter for property password.
     * 
     * @param password New value of property password.
     */
    void setPassword(String password);

    /**
     * Setter for property prefix.
     * 
     * @param prefix New value of property prefix.
     */
    void setPrefix(String prefix);

    /**
     * Setter for property URLID.
     * 
     * @param URLID New value of property URLID.
     */
    void setURLID(long URLID);

    /**
     * Validate an UID
     * 
     * @return true, if UID is valid
     */
    boolean validateUID() throws Exception;

    /**
     * Validate an UID
     * 
     * @param password the password
     * @return true, if UID is valid
     */
    boolean validateUID(String password) throws Exception;
    
}
