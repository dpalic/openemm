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

import org.displaytag.pagination.PaginatedList;
import org.springframework.context.ApplicationContextAware;

/** Dao for Blacklist Objects
 *
 * @author ar
 */
public interface BlacklistDao extends ApplicationContextAware {
	/**
	 * Adds the given email to the blacklist.
	 *
	 * @param companyID the company to add it for.
	 * @param email the address to add to the blacklist.
	 * @return true on success.
	 */
	boolean	insert(int companyID, String email);

	/**
	 * Remove the given email from the blacklist.
	 *
	 * @param companyID the company to work on.
	 * @param email the address to remove from to the blacklist.
	 * @return true on success.
	 */
	boolean	delete(int companyID, String email);
	
	
	/**
	 * get a list of blacklisted recipients
	 * 
	 */
	public PaginatedList getBlacklistedRecipients(int companyID, String sort, String direction , int page, int rownums );
	
}
