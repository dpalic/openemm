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
package org.agnitas.backend;

/** 
 * Keeps track of some customer relevant data
 * during mail generation
 */
class Custinfo {
    /** The email address */
    protected String	email = null;
    /** Numeric gender of the customer 0 male, 1 female, 2 unknown */
    protected int		gender = -1;
    /** Firstname of the customer */
    protected String	firstname = null;
    /** Lastname of the customer */
    protected String	lastname = null;
    
    /**
     * Reset all values
     */
    protected void clear () {
        email = null;
        gender = -1;
        firstname = null;
        lastname = null;
    }
    
    /**
     * Set email
     * 
     * @param nEmail the email address
     */
    protected void setEmail (String nEmail) {
        email = nEmail;
    }
    /**
     * Set gender
     *
     * @param nGender the gender to use
     */
    protected void setGender (int nGender) {
        gender = nGender;
    }
    
    /**
     * Set gender from string
     * 
     * @param nGender the gender in string form
     */
    protected void setGender (String nGender) {
        setGender (Integer.parseInt (nGender));
    }
    
    /**
     * Set firstname
     *
     * @param nFirstname the new firstname
     */
    protected void setFirstname (String nFirstname) {
        firstname = nFirstname;
    }
    
    /** 
     * Set lastname
     * 
     * @param nLastname the new lastname
     */
    protected void setLastname (String nLastname) {
        lastname = nLastname;
    }
}