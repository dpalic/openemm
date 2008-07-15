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

import org.agnitas.beans.MaildropEntry;

/**
 *
 * @author mhe
 */
public class MaildropEntryImpl implements MaildropEntry {
    
    /** Creates a new instance of MaildropEntryImpl */
    public MaildropEntryImpl() {
    }

    /**
     * Holds value of property companyID.
     */
    protected int companyID;

    /**
     * Getter for property companyID.
     * @return Value of property companyID.
     */
    public int getCompanyID() {
        return this.companyID;
    }

    /**
     * Setter for property companyID.
     * @param companyID New value of property companyID.
     */
    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }

    /**
     * Holds value of property id.
     */
    protected int id;

    /**
     * Getter for property id.
     * @return Value of property id.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Setter for property id.
     * @param id New value of property id.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Holds value of property mailingID.
     */
    protected int mailingID;

    /**
     * Getter for property mailingID.
     * @return Value of property mailingID.
     */
    public int getMailingID() {
        return this.mailingID;
    }

    /**
     * Setter for property mailingID.
     * @param mailingID New value of property mailingID.
     */
    public void setMailingID(int mailingID) {
        this.mailingID = mailingID;
    }

    /**
     * Holds value of property status.
     */
    protected char status;

    /**
     * Getter for property status.
     * @return Value of property status.
     */
    public char getStatus() {
        return this.status;
    }

    /**
     * Setter for property status.
     * @param status New value of property status.
     */
    public void setStatus(char status) {
        this.status = status;
    }

    /**
     * Holds value of property sendDate.
     */
    protected java.util.Date sendDate;

    /**
     * Getter for property senddate.
     * @return Value of property senddate.
     */
    public java.util.Date getSendDate() {
        return this.sendDate;
    }

    /**
     * Setter for property senddate.
     * @param sendDate 
     */
    public void setSendDate(java.util.Date sendDate) {
        this.sendDate = sendDate;
    }

    /**
     * Holds value of property genDate.
     */
    protected java.util.Date genDate;

    /**
     * Getter for property genDate.
     * @return Value of property genDate.
     */
    public java.util.Date getGenDate() {
        return this.genDate;
    }

    /**
     * Setter for property genDate.
     * @param genDate New value of property genDate.
     */
    public void setGenDate(java.util.Date genDate) {
        this.genDate = genDate;
    }

    /**
     * Holds value of property genChangeDate.
     */
    protected java.util.Date genChangeDate;

    /**
     * Getter for property genChangeDate.
     * @return Value of property genChangeDate.
     */
    public java.util.Date getGenChangeDate() {
        return this.genChangeDate;
    }

    /**
     * Setter for property genChangeDate.
     * @param genChangeDate New value of property genChangeDate.
     */
    public void setGenChangeDate(java.util.Date genChangeDate) {
        this.genChangeDate = genChangeDate;
    }

    /**
     * Holds value of property genStatus.
     */
    protected int genStatus;

    /**
     * Getter for property genStatus.
     * @return Value of property genStatus.
     */
    public int getGenStatus() {
        return this.genStatus;
    }

    /**
     * Setter for property genStatus.
     * @param genStatus New value of property genStatus.
     */
    public void setGenStatus(int genStatus) {
        this.genStatus = genStatus;
    }

    /**
     * Holds value of property stepping.
     */
    protected int stepping;

    /**
     * Getter for property stepping.
     * @return Value of property stepping.
     */
    public int getStepping() {
        return this.stepping;
    }

    /**
     * Setter for property stepping.
     * @param stepping New value of property stepping.
     */
    public void setStepping(int stepping) {
        this.stepping = stepping;
    }

    /**
     * Holds value of property blocksize.
     */
    protected int blocksize;

    /**
     * Getter for property blocksize.
     * @return Value of property blocksize.
     */
    public int getBlocksize() {
        return this.blocksize;
    }

    /**
     * Setter for property blocksize.
     * @param blocksize New value of property blocksize.
     */
    public void setBlocksize(int blocksize) {
        this.blocksize = blocksize;
    }
    
}
