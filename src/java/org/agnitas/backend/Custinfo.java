/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/
package org.agnitas.backend;

/**
 * Keeps track of some customer relevant data
 * during mail generation
 */
public class Custinfo {
    /** The user type of this customer */
    protected String    usertype = null;
    /** The email address */
    protected String    email = null;
    /** Numeric gender of the customer 0 male, 1 female, 2 unknown */
    protected int       gender = -1;
    /** Firstname of the customer */
    protected String    firstname = null;
    /** Lastname of the customer */
    protected String    lastname = null;
    /** Title of the customer */
    protected String     title = null;

    /** Number of entries to check against blacklist */
    public int      checkForBlacklist = 1;

    /**
     * Reset all values
     */
    protected void clear () {
        usertype = null;
        email = null;
        gender = -1;
        firstname = null;
        lastname = null;
        title = null;
    }
    
    /**
     * Set usertype
     * 
     * @param nUserType the user type
     */
    protected void setUserType (String nUserType) {
        usertype = nUserType;
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

    /**
     * Set title
     * 
     * @param nTitle the new title
     */
    protected void setTitle (String nTitle) {
        title = nTitle;
    }

    /**
     * Set values directly from database record
     *
     * @param rmap database column mapping
     * @param indices of database entries
     */
    public void setFromDatabase (Column[] rmap, Object indicesp) {
        Indices indices = (Indices) indicesp;
        if (indices.email != -1) {
            setEmail (rmap[indices.email].get ());
        }
        if (indices.gender != -1) {
            setGender (rmap[indices.gender].get ());
        }
        if (indices.firstname != -1) {
            setFirstname (rmap[indices.firstname].get ());
        }
        if (indices.lastname != -1) {
            setLastname (rmap[indices.lastname].get ());
        }
        if (indices.title != -1) {
            setTitle (rmap[indices.title].get ());
        }
    }

    /** Returns the value to check against blacklist for given state
     * @param state the state of blacklist check
     * @return the value for this blacklist
     */
    public String blacklistValue (int state) {
        switch (state) {
        case 0:
            return email;
        }
        return null;
    }

    /** Returns a textual ID for blacklist state
     * @param state the state of blacklist check
     * @return the textual ID
     */
    public String blacklistName (int state) {
        switch (state) {
        case 0:
            return "EMail";
        }
        return null;
    }
}
