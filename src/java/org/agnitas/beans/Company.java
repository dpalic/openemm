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

import java.io.Serializable;
import java.sql.Connection;

/**
 * 
 * @author Martin Helff
 */
public interface Company extends Serializable {

    /**
     * Getter for property id.
     * 
     * @return Value of property id.
     */
    int getId();

    /**
     * Getter for property creatorID.
     * 
     * @return Value of property creatorID.
     */
    int getCreatorID();

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
     * Getter for property secret.
     * 
     * @return Value of property secret.
     */
    String getSecret();

    /**
     * Getter for property rdirDomain.
     * 
     * @return Value of property rdirDomain.
     */
    String getRdirDomain();

    /**
     * Getter for property mailloopDomain.
     * 
     * @return Value of property mailloopDomain.
     */
    public String getMailloopDomain();

    /**
     * Getter for property status.
     * 
     * @return Value of property ststus.
     */
    String getStatus();

    /**
     * Setter for property id.
     *
     * @param id New value of property id.
     */
    void setId(int id);

    /**
     * Setter for property creatorID.
     *
     * @param creatorID New value of property creatorID.
     */
    void setCreatorID(int creatorID);

    /**
     * Setter for property shortname.
     *
     * @param name New value of property shortname.
     */
    void setShortname(String name);

    /**
     * Setter for property description.
     *
     * @param description New value of property description.
     */
    void setDescription(String description);

    /**
     * Setter for property rdirDomain.
     *
     * @param rdirDomain New value of property rdirDomain.
     */
    void setRdirDomain(String rdirDomain);

    /**
     * Setter for property secret.
     *
     * @param secret New value of property secret.
     */
    void setSecret(String secret);

    /**
     * Setter for property mailloopDomain.
     * 
     * @param mailloopDomain New value of property mailloopDomain.
     */
    public void setMailloopDomain(String mailloopDomain);
    
    /**
     * Setter for property status.
     * 
     * @param status New value of property status.
     */
    void setStatus(String status);

    /**
     * Init for tables.
     *
     * @param dbConn Initialized
     */
    boolean initTables(Connection dbConn);
}
