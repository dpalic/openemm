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

package org.agnitas.dao.impl;

import org.agnitas.beans.ColumnMapping;
import org.agnitas.beans.ImportProfile;
import org.agnitas.dao.ImportProfileDao;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.ImportUtils;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Vyacheslav Stepanov
 */
public class ImportProfileDaoImpl extends AbstractImportDao implements ImportProfileDao {

    public int createImportProfile(final ImportProfile profile) {
        if (AgnUtils.isOracleDB()) {
            String sql = "INSERT INTO import_profile_tbl " +
                    "(company_id, admin_id, shortname, column_separator, text_delimiter, file_charset, " +
                    "date_format, import_mode, null_values_action, key_column, ext_email_check, " +
                    "report_email, check_for_duplicates, mail_type, id, update_all_duplicates) " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            final String sqlQuery = sql;

            final JdbcTemplate jdbcTemplate = createJdbcTemplate();
            final int nextProfileId = getNextProfileSequence(jdbcTemplate);

            // @todo: add compability for mysql
            jdbcTemplate.update(
                    new PreparedStatementCreator() {
                        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                            PreparedStatement ps =
                                    connection.prepareStatement(sqlQuery/*, new String[]{"id"}*/);
                            ps.setInt(1, profile.getCompanyId());
                            ps.setInt(2, profile.getAdminId());
                            ps.setString(3, profile.getName());
                            ps.setInt(4, profile.getSeparator());
                            ps.setInt(5, profile.getTextRecognitionChar());
                            ps.setInt(6, profile.getCharset());
                            ps.setInt(7, profile.getDateFormat());
                            ps.setInt(8, profile.getImportMode());
                            ps.setInt(9, profile.getNullValuesAction());
                            ps.setString(10, profile.getKeyColumn());
                            ps.setInt(11, ImportUtils.getBooleanAsInt(profile.getExtendedEmailCheck()));
                            ps.setString(12, profile.getMailForReport());
                            ps.setInt(13, profile.getCheckForDuplicates());
                            ps.setInt(14, profile.getDefaultMailType());

                            if (AgnUtils.isOracleDB()) {
                                ps.setInt(15, nextProfileId);
                            }
                            ps.setInt(16, ImportUtils.getBooleanAsInt(profile.getUpdateAllDuplicates()));
                            return ps;
                        }
                    });
            return nextProfileId;
        } else {
            String sql = "INSERT INTO import_profile_tbl " +
                    "(company_id, admin_id, shortname, column_separator, text_delimiter, file_charset, " +
                    "date_format, import_mode, null_values_action, key_column, ext_email_check, " +
                    "report_email, check_for_duplicates, mail_type, update_all_duplicates) " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            SqlUpdate sqlUpdate = new SqlUpdate(getDataSource(), sql,
                    new int[]{Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.INTEGER,
                            Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER,
                            Types.INTEGER, Types.VARCHAR, Types.BOOLEAN, Types.VARCHAR,
                            Types.INTEGER, Types.INTEGER, Types.BOOLEAN});
            sqlUpdate.setReturnGeneratedKeys(true);
            sqlUpdate.setGeneratedKeysColumnNames(new String[]{"id"});
            sqlUpdate.compile();
            GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
            Object[] values = {profile.getCompanyId(), profile.getAdminId(), profile.getName(),
                    profile.getSeparator(), profile.getTextRecognitionChar(), profile.getCharset(),
                    profile.getDateFormat(), profile.getImportMode(), profile.getNullValuesAction(),
                    profile.getKeyColumn(), profile.getExtendedEmailCheck(),
                    profile.getMailForReport(), profile.getCheckForDuplicates(),
                    profile.getDefaultMailType(), profile.getUpdateAllDuplicates() };
            sqlUpdate.update(values, generatedKeyHolder);
            return generatedKeyHolder.getKey().intValue();
        }
    }

    public void updateImportProfile(ImportProfile profile) {
        String sql = "UPDATE import_profile_tbl SET " +
                "company_id=?, admin_id=?, shortname=?, column_separator=?, text_delimiter=?, file_charset=?, " +
                "date_format=?, import_mode=?, null_values_action=?, key_column=?, ext_email_check=?, " +
                "report_email=?, check_for_duplicates=?, mail_type=?, update_all_duplicates=? WHERE id=?";
        SqlUpdate sqlUpdate = new SqlUpdate(getDataSource(), sql,
                new int[]{Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.INTEGER,
                        Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER,
                        Types.INTEGER, Types.VARCHAR, Types.INTEGER, Types.VARCHAR,
                        Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER});
        sqlUpdate.compile();
        Object[] values = {profile.getCompanyId(), profile.getAdminId(), profile.getName(),
                profile.getSeparator(), profile.getTextRecognitionChar(), profile.getCharset(),
                profile.getDateFormat(), profile.getImportMode(), profile.getNullValuesAction(),
                profile.getKeyColumn(), ImportUtils.getBooleanAsInt(profile.getExtendedEmailCheck()),
                profile.getMailForReport(), profile.getCheckForDuplicates(),
                profile.getDefaultMailType(), ImportUtils.getBooleanAsInt(profile.getUpdateAllDuplicates()), profile.getId()};
        sqlUpdate.update(values);
    }

    public ImportProfile getImportProfile(int profileId) {
        String sqlStatement = "SELECT * FROM import_profile_tbl WHERE id=" + profileId;
        try {
            ImportProfile profile = (ImportProfile) createJdbcTemplate().
                    queryForObject(sqlStatement, new ImportProfileRowMapper());
            return profile;
        }
        catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    public void removeImportProfile(int profileId) {
        String sqlStatement = "DELETE FROM import_profile_tbl WHERE id=" + profileId;
        createJdbcTemplate().execute(sqlStatement);
    }

    public ImportProfile getImportProfileFull(int profileId) {
        ImportProfile profile = getImportProfile(profileId);
        if (profile == null) {
            return null;
        }
        List<ColumnMapping> columnMappings = getColumnMappings(profileId);
        profile.setColumnMapping(columnMappings);
        Map<String, Integer> genderMappings = getGenderMappings(profileId);
        profile.setGenderMapping(genderMappings);
        return profile;
    }

    public void updateImportProfileFull(ImportProfile profile) {
        updateImportProfile(profile);
        removeColumnMappings(profile.getId());
        saveColumnMappings(profile.getColumnMapping());
        removeGenderMappings(profile.getId());
        saveGenderMappings(profile.getGenderMapping(), profile.getId());
    }

    public int createImportProfileFull(ImportProfile profile) {
        int profileId = createImportProfile(profile);
        for (ColumnMapping columnMapping : profile.getColumnMapping()) {
            columnMapping.setProfileId(profileId);
        }
        saveColumnMappings(profile.getColumnMapping());
        saveGenderMappings(profile.getGenderMapping(), profileId);
        return profileId;
    }

    public void removeImportProfileFull(int profileId) {
        removeImportProfile(profileId);
        removeColumnMappings(profileId);
        removeGenderMappings(profileId);
    }

    public List<ImportProfile> getImportProfiles(int companyId) {
        String sqlStatement = "SELECT * FROM import_profile_tbl WHERE company_id=" + companyId;
        return createJdbcTemplate().query(sqlStatement, new ImportProfileRowMapper());
    }

    public void saveColumnMappings(final List<ColumnMapping> mappings) {
        if (mappings.isEmpty()) {
            return;
        }
        final JdbcTemplate template = createJdbcTemplate();
        String query = null;
        if (AgnUtils.isOracleDB()) {
            query = "INSERT INTO import_column_mapping_tbl " +
                    "(id, profile_id, file_column, db_column, mandatory, default_value) VALUES (import_column_mapping_tbl_seq.nextval,?,?,?,?,?)";
        }
        if (AgnUtils.isMySQLDB()) {
            query = "INSERT INTO import_column_mapping_tbl " +
                    "( profile_id, file_column, db_column, mandatory, default_value) VALUES (?,?,?,?,?)";
        }
        final BatchPreparedStatementSetter setter = new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ColumnMapping mapping = mappings.get(i);
                ps.setInt(1, mapping.getProfileId());
                ps.setString(2, mapping.getFileColumn());
                ps.setString(3, mapping.getDatabaseColumn());
                ps.setBoolean(4, mapping.getMandatory());
                ps.setString(5, mapping.getDefaultValue());
            }

            public int getBatchSize() {
                return mappings.size();
            }
        };
        template.batchUpdate(query, setter);
    }

    public List<ColumnMapping> getColumnMappings(int profileId) {
        String sqlStatement = "SELECT * FROM import_column_mapping_tbl WHERE profile_id=" + profileId;
        return createJdbcTemplate().query(sqlStatement, new ColumnMappingRowMapper());
    }

    public void removeColumnMappings(int profileId) {
        String sqlStatement = "DELETE FROM import_column_mapping_tbl WHERE " +
                "profile_id=" + profileId;
        createJdbcTemplate().execute(sqlStatement);
    }

    public void saveGenderMappings(final Map<String, Integer> mappings, final int importProfileId) {
        if (mappings.isEmpty()) {
            return;
        }
        final ArrayList<String> mappingKeys = new ArrayList<String>(mappings.keySet());
        final JdbcTemplate template = createJdbcTemplate();
        String query = null;
        if (AgnUtils.isOracleDB()) {
            query = "INSERT INTO import_gender_mapping_tbl " +
                    "(id, profile_id, int_gender, string_gender) VALUES (import_gender_mapping_tbl_seq.nextval,?,?,?)";
        }
        if (AgnUtils.isMySQLDB()) {
            query = "INSERT INTO import_gender_mapping_tbl " +
                    "(profile_id, int_gender, string_gender) VALUES (?,?,?)";
        }
        final BatchPreparedStatementSetter setter = new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, importProfileId);
                ps.setInt(2, mappings.get(mappingKeys.get(i)));
                ps.setString(3, mappingKeys.get(i));
            }

            public int getBatchSize() {
                return mappings.size();
            }
        };
        template.batchUpdate(query, setter);
    }

    public Map<String, Integer> getGenderMappings(int profileId) {
        String sql = "SELECT * FROM import_gender_mapping_tbl " +
                "WHERE profile_id =" + profileId + " ORDER BY id";
        List<Map> queryResult = createJdbcTemplate().queryForList(sql);
        Map<String, Integer> result = new HashMap<String, Integer>();
        for (Map row : queryResult) {
            int intValue = ((Number) row.get("int_gender")).intValue();
            String stringValue = (String) row.get("string_gender");
            result.put(stringValue, intValue);
        }
        return result;
    }

    public void removeGenderMappings(int profileId) {
        String sqlStatement = "DELETE FROM import_gender_mapping_tbl WHERE " +
                "profile_id=" + profileId;
        createJdbcTemplate().execute(sqlStatement);
    }

    public ImportProfile getImportProfileByShortName(String shortname) {
        String sqlStatement = "SELECT * FROM import_profile_tbl WHERE upper(shortname)='" + shortname + "'";
        try {
            ImportProfile profile = (ImportProfile) createJdbcTemplate().
                    queryForObject(sqlStatement, new ImportProfileRowMapper());
            return profile;
        }
        catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    private int getNextProfileSequence(JdbcTemplate template) {
        String query = " SELECT import_profile_tbl_seq.nextval FROM DUAL ";
        return template.queryForInt(query);
    }

}
