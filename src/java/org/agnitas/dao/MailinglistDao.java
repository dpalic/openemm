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
package org.agnitas.dao;

import java.util.List;

import org.agnitas.beans.Mailinglist;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * @author mhe
 */
public interface MailinglistDao extends ApplicationContextAware {
    /**
     * Deletes mailinglist.
     *
     * @return true==success
     *false==errror
     */
    boolean deleteMailinglist(int listID, int companyID);

    /**
     * Getter for property mailinglist by list id and company id.
     *
     * @return Value of mailinglist.
     */
    Mailinglist getMailinglist(int listID, int companyID);

    /**
     * Getter for property mailinglists by company id.
     *
     * @return Value of mailinglists.
     */
    List getMailinglists(int companyID);

    /**
     * Saves mailinglist.
     *
     * @return Saved mailinglist id.
     */
    int saveMailinglist(Mailinglist list);
    
    /**
     * Removes bindings from database.
     */
    boolean deleteBindings(int id, int companyID);
    
    /**
     * Getter for property numberOfActiveSubscribers.
     *
     * @return Value of property numberOfActiveSubscribers.
     */
    int getNumberOfActiveSubscribers(boolean admin, boolean test, boolean world, int targetID, int companyID, int id);
    
}
