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
	
	public void delete(Admin admin);
	
	public void delete(int adminID, int companyID);
	
}
