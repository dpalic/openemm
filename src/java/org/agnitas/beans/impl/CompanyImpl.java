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

import java.sql.*;
import java.io.*;
import org.agnitas.beans.Company;
import org.agnitas.util.AgnUtils;

public class CompanyImpl implements Company {

    protected int companyID;
    protected int creatorID;
    protected String shortname;
    protected String description;
    protected String status;
    protected int mailtracking = 0;


    // CONSTRUCTOR:
    public CompanyImpl() {
        companyID=0;
        creatorID=0;
    }



    // * * * * *
    //  SETTER:
    // * * * * *
    public void setId(int id) {
        companyID=id;
    }

    public void setShortname(String name) {
        shortname=name;
    }

    public void setDescription(String sql) {
        description=sql;
    }

    public void setCreatorID(int creatorID) {
        this.creatorID = creatorID;
    }

    public void setStatus(String status) {
        this.status = status;
    }




    // * * * * *
    //  GETTER:
    // * * * * *
    public int getCreatorID() {
        return this.creatorID;
    }

    public String getStatus() {
        return this.status;
    }

    public int getId() {
        return companyID;
    }

    public String getShortname() {
        return shortname;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Holds value of property rdirDomain.
     */
    protected String rdirDomain;

    /**
     * Getter for property rdirDomain.
     * @return Value of property rdirDomain.
     */
    public String getRdirDomain() {

        return this.rdirDomain;
    }

    /**
     * Setter for property rdirDomain.
     * @param rdirDomain New value of property rdirDomain.
     */
    public void setRdirDomain(String rdirDomain) {

        this.rdirDomain = rdirDomain;
    }

    /**
     * Holds value of property secret.
     */
    protected String secret;

    /**
     * Getter for property secret.
     * @return Value of property secret.
     */
    public String getSecret() {

        return this.secret;
    }

    /**
     * Setter for property secret.
     * @param secret New value of property secret.
     */
    public void setSecret(String secret) {

        this.secret = secret;
    }

    /**
     * Holds value of property mailloopDomain.
     */
    private String mailloopDomain;

    /**
     * Getter for property mailloopDomain.
     * @return Value of property mailloopDomain.
     */
    public String getMailloopDomain() {
        return this.mailloopDomain;
    }

    /**
     * Setter for property mailloopDomain.
     * @param mailloopDomain New value of property mailloopDomain.
     */
    public void setMailloopDomain(String mailloopDomain) {
        this.mailloopDomain = mailloopDomain;
    }

    public int getMailtracking() {
    	return this.mailtracking;
    }

    public void setMailtracking (int tracking) {
    	this.mailtracking = tracking;
    }
}
