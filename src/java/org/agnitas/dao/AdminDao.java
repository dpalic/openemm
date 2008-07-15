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

package org.agnitas.dao;

import org.agnitas.beans.Admin;
import org.springframework.context.ApplicationContextAware;

/** Dao for Admin Objects
 * Loads and saves Adminobjects to/from database.
 *
 * @author ar
 */
public interface AdminDao extends ApplicationContextAware {
    
	/**
	 * Loads an admin identified by admin id an company id.
	 *
	 * @param adminID The id of the admin that should be loaded.
	 * @param companyID The companyID for the admin. 
	 * @return The Admin or null on failure.
	 */
	Admin getAdmin(int adminID, int companyID);

	/**
	 * Loads an admin identified by login data.
	 *
	 * @param name The username of the admin.
	 * @param password The password for the admin. 
	 * @return The Admin or null on failure.
	 */
	Admin getAdminByLogin(String name, String password);

	/**
	 * Saves an admin.
	 *
	 * @param admin The admin that should be saved.
	 */
	void	save(Admin admin); 
}
