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
 * the code written by AGNITAS AG are Copyright (c) 2014 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/
package org.agnitas.dao;


import org.agnitas.beans.AdminPreferences;

/**
 * Dao for AdminPreferences Objects loads, saves, and delete AdminPreferencesObjects to/from database.
 *
 * @author Dmytro Zhykhariev
 */
public interface AdminPreferencesDao {

    /**
     * Return admin preferences by adminId.
     *
     * @param adminId
     *            The id of the admin which preferences should be deleted.
     */
    public AdminPreferences getAdminPreferences(int adminId);

    /**
     * Saves an admin preferences.
     *
     * @param adminPreferences
     *            The admin preferences that should be saved.
     */
    public void save(AdminPreferences adminPreferences);

    /**
     * Deletes an admin preferences.
     *
     * @param adminId
     *            The id of the admin which preferences should be deleted.
     * @return true
     */
    public boolean delete(int adminId);

    /**
     * Write default preferences values Deletes an admin preferences.
     *
     * @param adminId
     *            The id of the admin which preferences should be created.
     */
    public void writeDefaultValues(int adminId);


}
