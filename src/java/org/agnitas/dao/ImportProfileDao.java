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
 * the code written by AGNITAS AG are Copyright (c) 2009 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/

package org.agnitas.dao;

import org.agnitas.beans.ColumnMapping;
import org.agnitas.beans.ImportProfile;

import java.util.List;
import java.util.Map;

/**
 * @author Vyacheslav Stepanov
 */
public interface ImportProfileDao {

    /**
     * Stored import profile to database
     *
     * @param profile import profile to store
     * @return the id of created import profile in database
     */
    int createImportProfile(ImportProfile profile);

    /**
     * Updates data of import profile
     *
     * @param profile import profile to update
     */
    void updateImportProfile(ImportProfile profile);

    /**
     * Gets import profile by id
     *
     * @param profileId id of import profile
     * @return import profile by id
     */
    ImportProfile getImportProfile(int profileId);

    /**
     * Removes import profile
     *
     * @param profileId id of import profile to remove
     */
    void removeImportProfile(int profileId);

    /**
     * Gets import profile with all its data (column mapping, gender mapping)
     * from database
     *
     * @param profileId id of import profile
     * @return import profile with all its data (column mapping, gender mapping)
     */
    ImportProfile getImportProfileFull(int profileId);

    /**
     * Updates import profile with all its data (column mapping, gender mapping)
     *
     * @param profile import profile to update
     */
    void updateImportProfileFull(ImportProfile profile);

    /**
     * Creates import profile with all its data (column mapping, gender mapping)
     *
     * @param profile profile to create
     * @return id of created import profile in database
     */
    int createImportProfileFull(ImportProfile profile);

    /**
     * Removes import profile with all its data (column mapping, gender mapping)
     *
     * @param profileId id of import profile
     */
    void removeImportProfileFull(int profileId);

    /**
     * Gets list of import profiles for company id
     *
     * @param companyId id of company
     * @return list of import profiles attached to the company id
     */
    List<ImportProfile> getImportProfiles(int companyId);

    /**
     * Stores column mapping to database
     *
     * @param mappings column mapping
     */
    void saveColumnMappings(List<ColumnMapping> mappings);

    /**
     * Gets column mapping of import profile
     *
     * @param profileId id of import profile
     * @return list of column mapping for the import profile
     */
    List<ColumnMapping> getColumnMappings(int profileId);

    /**
     * Removes all column mapping of import profile
     *
     * @param profileId id of profile
     */
    void removeColumnMappings(int profileId);

    /**
     * Stores gender mapping of import profile to database
     *
     * @param mappings        mapping to save
     * @param importProfileId id of import profile
     */
    void saveGenderMappings(Map<String, Integer> mappings, int importProfileId);

    /**
     * Gets import profile gender mapping
     *
     * @param profileId id of import profile
     * @return gender mapping
     */
    Map<String, Integer> getGenderMappings(int profileId);

    /**
     * Removes gender mapping of impot profile
     *
     * @param profileId id of import profile
     */
    void removeGenderMappings(int profileId);

    ImportProfile getImportProfileByShortName(String shortname);
}
