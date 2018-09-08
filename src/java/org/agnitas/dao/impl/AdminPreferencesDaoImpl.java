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
package org.agnitas.dao.impl;

import org.agnitas.beans.AdminPreferences;
import org.agnitas.beans.factory.AdminPreferencesFactory;
import org.agnitas.dao.AdminPreferencesDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Zhykhariev Dmytro
 */
public class AdminPreferencesDaoImpl  extends BaseDaoImpl implements AdminPreferencesDao {

    //the logger
    protected static final transient Logger logger = Logger.getLogger(AdminPreferencesDaoImpl.class);

    //table name
    protected static final String TABLE = "admin_pref_tbl";

    //table columns
    protected static final String FIELD_ADMIN_ID = "admin_id";
    protected static final String FIELD_PREFERENCE = "pref";
    protected static final String FIELD_VALUE = "val";

    //preferences names
    protected static final String PREFERENCE_CONTENTBLOCKS = "mailing.contentblocks";      //Contentblocks
    protected static final String PREFERENCE_LISTSIZE = "listsize";                        //List Size
    protected static final String PREFERENCE_MAILING_SETTINGS_VIEW = "mailing.settings";   //Mailing Settings

    //preferences default values
    protected static final int PREFERENCE_CONTENTBLOCKS_DEFAULT = 0;
    protected static final int PREFERENCE_LISTSIZE_DEFAULT = 20;
    protected static final int PREFERENCE_MAILING_SETTINGS_VIEW_DEFAULT = 0;

    //SQL requests
    private static final String DELETE_PREFERENCES_BY_ADMIN_ID = "DELETE FROM " + TABLE + " WHERE " + FIELD_ADMIN_ID + " = ?";
    private static final String INSERT = "INSERT INTO " + TABLE + " ( " + FIELD_ADMIN_ID + " , " + FIELD_PREFERENCE + " , " + FIELD_VALUE +  " ) VALUES (?,?,?)";
    private static final String SELECT_ALL_BY_ADMIN_ID = "SELECT " + FIELD_PREFERENCE + " , " + FIELD_VALUE + " FROM " + TABLE + " WHERE " + FIELD_ADMIN_ID + " = ?";

    //Factory for ComAdminPreferences  objects
    protected AdminPreferencesFactory adminPreferencesFactory;

    @Required
    public void setAdminPreferencesFactory(AdminPreferencesFactory adminPreferencesFactory) {
        this.adminPreferencesFactory = adminPreferencesFactory;
    }


    @Override
    public AdminPreferences getAdminPreferences(int adminId) {
        try {
            AdminPreferences adminPreferences = adminPreferencesFactory.newAdminPreferences();
            adminPreferences.setAdminID(adminId);

            List<Map<String, Object>> resultList = getSimpleJdbcTemplate().queryForList(SELECT_ALL_BY_ADMIN_ID, adminId);

            if(resultList.size()>0) {

                Map<String, String> adminPreferencesMap = new HashMap<>();
                for (Map<String, Object> resultRow : resultList) {
                    adminPreferencesMap.put(resultRow.get(FIELD_PREFERENCE).toString(), resultRow.get(FIELD_VALUE).toString());
                }

                int prefContentBlocks = adminPreferencesMap.get(PREFERENCE_CONTENTBLOCKS) != null ?
                        Integer.parseInt(adminPreferencesMap.get(PREFERENCE_CONTENTBLOCKS)) : PREFERENCE_CONTENTBLOCKS_DEFAULT;
                int prefListSize = adminPreferencesMap.get(PREFERENCE_LISTSIZE) != null ?
                        Integer.parseInt(adminPreferencesMap.get(PREFERENCE_LISTSIZE)) : PREFERENCE_LISTSIZE_DEFAULT;
                int prefMailingSettingsView = adminPreferencesMap.get(PREFERENCE_MAILING_SETTINGS_VIEW) != null ?
                        Integer.parseInt(adminPreferencesMap.get(PREFERENCE_MAILING_SETTINGS_VIEW)) : PREFERENCE_MAILING_SETTINGS_VIEW_DEFAULT;

                adminPreferences.setMailingContentView(prefContentBlocks);
                adminPreferences.setListSize(prefListSize);
                adminPreferences.setMailingSettingsView(prefMailingSettingsView);

                return adminPreferences;
            }else {
                logger.debug("User preferences not found for user id = " + adminId);
                //set and return default preferences
                adminPreferences.setMailingContentView(PREFERENCE_CONTENTBLOCKS_DEFAULT);
                adminPreferences.setListSize(PREFERENCE_LISTSIZE_DEFAULT);
                adminPreferences.setMailingSettingsView(PREFERENCE_MAILING_SETTINGS_VIEW_DEFAULT);

                return adminPreferences;
            }
        } catch (DataAccessException e) {
            logger.error("Error reading admin preferences", e);
            return null;
        } catch (NumberFormatException e) {
            logger.error("Error reading admin preferences", e);
            return null;
        }
    }

    @Override
    public void save(AdminPreferences adminPreferences) {

        if (adminPreferences == null) {
            return;
        }

        int adminId = adminPreferences.getAdminID();

        getSimpleJdbcTemplate().update(DELETE_PREFERENCES_BY_ADMIN_ID, adminId);
        getSimpleJdbcTemplate().update(INSERT, adminId, PREFERENCE_CONTENTBLOCKS, adminPreferences.getMailingContentView());
        getSimpleJdbcTemplate().update(INSERT, adminId, PREFERENCE_LISTSIZE, adminPreferences.getListSize());
        getSimpleJdbcTemplate().update(INSERT, adminId, PREFERENCE_MAILING_SETTINGS_VIEW, adminPreferences.getMailingSettingsView());

    }

    @Override
    public boolean delete(int adminId) {
        int affectedRows = getSimpleJdbcTemplate().update(DELETE_PREFERENCES_BY_ADMIN_ID, adminId);
        return affectedRows > 0;
    }

    @Override
    public void writeDefaultValues(int adminId) {

        getSimpleJdbcTemplate().update(INSERT, adminId, PREFERENCE_CONTENTBLOCKS, PREFERENCE_CONTENTBLOCKS_DEFAULT);
        getSimpleJdbcTemplate().update(INSERT, adminId, PREFERENCE_LISTSIZE, PREFERENCE_LISTSIZE_DEFAULT);
        getSimpleJdbcTemplate().update(INSERT, adminId, PREFERENCE_MAILING_SETTINGS_VIEW, PREFERENCE_MAILING_SETTINGS_VIEW_DEFAULT);

    }
}
