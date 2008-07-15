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

import org.agnitas.beans.BindingEntry;
import org.agnitas.target.Target;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * @author ar
 */
public interface BindingEntryDao extends ApplicationContextAware {

	/**
	 * Load a binding from database. Uses recipientID, mailinglistID and
	 * mediaType of the given binding.
	 *
	 * @param entry the binding to load from database.
	 * @param companyID the id of the company for the binding.
	 * @return true on success
	 */
	BindingEntry get(int recipientID, int companyID,
			int mailinglistID, int mediaType);

	void save(int companyID, BindingEntry entry);

	/**
	 * Updates this Binding in the Database
	 *
	 * @return True: Sucess
	 * False: Failure
	 * @param companyID The company ID of the Binding
	 */
	boolean updateBinding(BindingEntry entry, int companyID);

	/**
	 * Inserts a new binding into the database.
	 *
	 * @param entry the entry to create.
	 * @param companyID the company we are working on.
	 * @return true on success.
	 */
	boolean insertNewBinding(BindingEntry entry, int companyID);

	/**
	 * Update the status for the binding. Also updates exit_mailing_id and
	 * user_remark to reflect the status change.
	 *
	 * @param entry the entry on which the status has changed.
	 * @param companyID the company we are working on.
	 * @return true on success.
	 */
	boolean updateStatus(BindingEntry entry, int companyID);

	/**
	 * Set given email to status optout. The given email can be an sql
	 * like pattern.
	 * 
	 * @param email the sql like pattern of the email-address.
	 * @param companyID only update addresses for this company.
	 */
	boolean optOutEmailAdr(String email, int CompanyID);

	/**
	 * Subscribes all customers in the given target group to the given
	 * mailinglist.
	 *
	 * @param companyID the company to work in.
	 * @param mailinglistID the id of the mailinglist to which the targets
	 *			should be subscribed.
	 * @param target the target describing the recipients that shall be
	 *		 added.
	 * @return true on success.	
	 */ 
	boolean addTargetsToMailinglist(int companyID, int mailinglistID, Target target);
}
