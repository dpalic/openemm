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
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.WebApplicationContext;

/**
 *
 * @author es
 */
public interface DomainStat extends Serializable {
    
     /**
     * Getter for property companyID.
     * 
     * @return Value of property companyID.
     */
    int getCompanyID();
    
    /**
     * Getter for property csvfile.
     * 
     * @return Value of property csvfile.
     */
    String getCsvfile();

     /**
     * Getter for property domains.
     * 
     * @return Value of property domains.
     */
    LinkedList getDomains();

     /**
     * Getter for property lines.
     * 
     * @return Value of property lines.
     */
    int getLines();

     /**
     * Getter for property listID.
     * 
     * @return Value of property listID.
     */
    int getListID();

     /**
     * Getter for property maxDomains.
     * 
     * @return Value of property maxDomains.
     */
    int getMaxDomains();

     /**
     * Getter for property rest.
     * 
     * @return Value of property rest.
     */
    int getRest();

     /**
     * Getter for property stat from database.
     * 
     * @return Value of property stat from database.
     */
    boolean getStatFromDB(WebApplicationContext myContext, HttpServletRequest request);

     /**
     * Getter for property subscribers.
     * 
     * @return Value of property subscribers.
     */
    LinkedList getSubscribers();

     /**
     * Getter for property targetID.
     * 
     * @return Value of property targetID.
     */
    int getTargetID();

     /**
     * Getter for property total.
     * 
     * @return Value of property total.
     */
    int getTotal();
    
    /**
     * Setter for property companyID.
     * 
     * @param id New value of property companyID.
     */
    void setCompanyID(int id);

    /**
     * Setter for property csvfile.
     * 
     * @param file New value of property csvfile.
     */
    void setCsvfile(String file);

    /**
     * Setter for property domains.
     * 
     * @param domains New value of property domains.
     */
    void setDomains(LinkedList domains);

    /**
     * Setter for property lines.
     * 
     * @param lines New value of property lines.
     */
    void setLines(int lines);

    /**
     * Setter for property listID.
     * 
     * @param id New value of property listID.
     */
    void setListID(int id);

    /**
     * Setter for property maxDomains.
     * 
     * @param maxDomains New value of property maxDomains.
     */
    void setMaxDomains(int maxDomains);

    /**
     * Setter for property rest.
     * 
     * @param rest New value of property rest.
     */
    void setRest(int rest);

    /**
     * Setter for property subscribers.
     * 
     * @param subscribers New value of property subscribers.
     */
    void setSubscribers(LinkedList subscribers);

    /**
     * Setter for property targetID.
     * 
     * @param id New value of property targetID.
     */
    void setTargetID(int id);

    /**
     * Setter for property total.
     * 
     * @param total New value of property total.
     */
    void setTotal(int total);
    
}
