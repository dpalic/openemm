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

import org.agnitas.beans.BindingEntry;
import org.agnitas.beans.ImportProfile;
import org.agnitas.beans.ProfileRecipientFields;
import org.agnitas.beans.impl.DynaBeanPaginatedListImpl;
import org.agnitas.dao.ImportRecipientsDao;
import org.agnitas.service.NewImportWizardService;
import org.agnitas.service.csv.Toolkit;
import org.agnitas.service.impl.CSVColumnState;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.ImportUtils;
import org.agnitas.util.importvalues.ImportMode;
import org.agnitas.util.importvalues.NullValuesAction;
import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.ValidatorResults;
import org.displaytag.pagination.PaginatedList;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.sql.*;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * @author Viktor Gema
 */
public class ImportRecipientsDaoImpl extends AbstractImportDao implements ImportRecipientsDao {

    private SingleConnectionDataSource temporaryConnection;
    private static final SimpleDateFormat DB_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    public LinkedHashMap<String, Map<String, Object>> getColumnInfoByColumnName(int companyId, String column) {
        DataSource ds = (DataSource) applicationContext.getBean("dataSource");
        LinkedHashMap<String, Map<String, Object>> list = new LinkedHashMap<String, Map<String, Object>>();
        ResultSet resultSet = null;

        Connection con = DataSourceUtils.getConnection(ds);
        try {
            if (AgnUtils.isOracleDB()) {
                resultSet = con.getMetaData().getColumns(null, AgnUtils.getDefaultValue("jdbc.username").toUpperCase(), "CUSTOMER_" + companyId + "_TBL", column.toUpperCase());
            } else {
                resultSet = con.getMetaData().getColumns(null, null, "customer_" + companyId + "_tbl", column);
            }
            if (resultSet != null) {
                while (resultSet.next()) {
                    String type;
                    String col = resultSet.getString(4).toLowerCase();
                    Map<String, Object> mapping = new HashMap<String, Object>();

                    mapping.put("column", col);
                    mapping.put("shortname", col);
                    type = ImportUtils.dbtype2string(resultSet.getInt(5));
                    mapping.put("type", type);
                    mapping.put("length", resultSet.getInt(7));
                    if (resultSet.getInt(11) == DatabaseMetaData.columnNullable) {
                        mapping.put("nullable", 1);
                    } else {
                        mapping.put("nullable", 0);
                    }

                    list.put((String) mapping.get("shortname"), mapping);
                }
            }
            resultSet.close();
        } catch (Exception e) {
            AgnUtils.logger().error(MessageFormat.format("Failed to get colum ({0}) info for admin ({1})", column, companyId), e);
        } finally {
            DataSourceUtils.releaseConnection(con, ds);
        }
        return list;

    }

    public void createRecipients(final Map<ProfileRecipientFields, ValidatorResults> recipientBeansMap, final Integer adminID, final ImportProfile profile, final Integer type, int datasource_id, CSVColumnState[] columns) {
        if (recipientBeansMap.isEmpty()) {
            return;
        }
        final JdbcTemplate template = getJdbcTemplateForTemporaryTable();
        final String prefix = "cust_" + adminID + "_tmp_";
        final String tableName = prefix + datasource_id + "_tbl";
        final ProfileRecipientFields[] recipients = recipientBeansMap.keySet().toArray(new ProfileRecipientFields[recipientBeansMap.keySet().size()]);
        final String query = "INSERT INTO " + tableName + " (recipient, validator_result, temporary_id, status_type, column_duplicate_check) VALUES (?,?,?,?,?)";
        CSVColumnState temporaryKeyColumn = null;
        for (CSVColumnState column : columns) {
            if (column.getColName().equals(profile.getKeyColumn()) && column.getImportedColumn()) {
                temporaryKeyColumn = column;
            }
        }
        final CSVColumnState keyColumn = temporaryKeyColumn;
        final BatchPreparedStatementSetter setter = new BatchPreparedStatementSetter() {

            public void setValues(PreparedStatement ps, int i) throws SQLException {

                ps.setBytes(1, ImportUtils.getObjectAsBytes(recipients[i]));
                ps.setBytes(2, ImportUtils.getObjectAsBytes(recipientBeansMap.get(recipients[i])));
                ps.setString(3, recipients[i].getTemporaryId());
                ps.setInt(4, type);
                setPreparedStatmentForCurrentColumn(ps, 5, keyColumn, recipients[i], profile);
            }

            public int getBatchSize() {
                return recipientBeansMap.size();
            }
        };
        template.batchUpdate(query, setter);
    }

    public HashMap<ProfileRecipientFields, ValidatorResults> getRecipientsByType(int adminID, Integer[] types, int datasource_id) {
        final JdbcTemplate aTemplate = getJdbcTemplateForTemporaryTable();
        final String prefix = "cust_" + adminID + "_tmp_";
        final String tableName = prefix + datasource_id + "_tbl";
        final String typesAsString = StringUtils.join(types, ",");
        String sqlStatement = "SELECT recipient, validator_result FROM " + tableName + " " +
                "WHERE status_type IN (" +
                typesAsString
                + ")";
        List<Map> resultList = aTemplate.queryForList(sqlStatement);
        HashMap<ProfileRecipientFields, ValidatorResults> recipients = new HashMap<ProfileRecipientFields, ValidatorResults>();
        for (Map row : resultList) {
            Object recipientBean = ImportUtils.deserialiseBean((byte[]) row.get("recipient"));
            final ProfileRecipientFields recipient = (ProfileRecipientFields) recipientBean;
            ValidatorResults validatorResults = null;
            if (row.get("validator_result") != null) {
                Object validatorResultsBean = ImportUtils.deserialiseBean((byte[]) row.get("validator_result"));
                validatorResults = (ValidatorResults) validatorResultsBean;
            }
            recipients.put(recipient, validatorResults);
        }

        return recipients;
    }

    public Map<Integer, Integer> assiggnToMailingLists(List<Integer> mailingLists, int companyID, int datasourceId, int mode, int adminId) {
        Map<Integer, Integer> mailinglistStat = new HashMap<Integer, Integer>();
        if (mailingLists == null || mailingLists.isEmpty() || mode == ImportMode.TO_BLACKLIST.getIntValue()) {
            return mailinglistStat;
        }

        JdbcTemplate jdbc = createJdbcTemplate();
        String currentTimestamp = AgnUtils.getHibernateDialect().getCurrentTimestampSQLFunctionName();
        String sql;

        // assign new recipients to mailing lists
        for (Integer mailingList : mailingLists) {
            mailinglistStat.put(mailingList, 0);
            if (mode == ImportMode.ADD.getIntValue() || mode == ImportMode.ADD_AND_UPDATE.getIntValue()) {
                sql = "INSERT INTO customer_" + companyID + "_binding_tbl (customer_id, user_type, user_status, user_remark, creation_date, exit_mailing_id, mailinglist_id) (SELECT customer_id, 'W', 1, 'CSV File Upload', " + currentTimestamp + ", 0," + mailingList + " FROM customer_" + companyID + "_tbl WHERE datasource_id = " + datasourceId + ")";
                int added = jdbc.update(sql);
                mailinglistStat.put(mailingList, added);
            }
        }

        // assign updated recipients to mailing lists
        Integer[] types = {NewImportWizardService.RECIPIENT_TYPE_DUPLICATE_RECIPIENT};
        int page = 0;
        int rowNum = NewImportWizardService.BLOCK_SIZE;
        HashMap<ProfileRecipientFields, ValidatorResults> recipients = null;
        while (recipients == null || recipients.size() >= rowNum) {
            recipients = getRecipientsByTypePaginated(types, page, rowNum, adminId, datasourceId);
            List<Integer> updatedRecipients = new ArrayList<Integer>();
            for (ProfileRecipientFields recipient : recipients.keySet()) {
                if (recipient.getUpdatedId() != null && recipient.getUpdatedId() != 0) {
                    updatedRecipients.add(recipient.getUpdatedId());
                }
            }
            updateMailinglists(mailingLists, companyID, datasourceId, mode, mailinglistStat, jdbc, currentTimestamp, updatedRecipients);
            page++;
        }

        return mailinglistStat;
    }

    public void removeTemporaryTable(String tableName, String sessionId) {
        if (AgnUtils.isOracleDB()) {
            final JdbcTemplate template = createJdbcTemplate();
            try {
                template.execute("DROP TABLE " + tableName);
                template.execute("DELETE FROM IMPORT_TMP_TABLES WHERE SESSION_ID='" + sessionId + "'");
            } catch (Exception e) {
                AgnUtils.logger().error("deleteTemporaryTables: " + e.getMessage());
                AgnUtils.logger().error("Table: " + tableName);
                e.printStackTrace();
            }
        }
    }

    public List<String> getTemporaryTableNamesBySessionId(String sessionId) {
        List<String> result = new ArrayList<String>();
        final JdbcTemplate template = createJdbcTemplate();
        String query = "SELECT TEMPORARY_TABLE_NAME FROM IMPORT_TMP_TABLES WHERE SESSION_ID='" + sessionId + "'";
        List<Map> resultList = template.queryForList(query);
        for (Map row : resultList) {
            final String temporaryTableName = (String) row.get("TEMPORARY_TABLE_NAME");
            result.add(temporaryTableName);
        }
        return result;
    }

    private void updateMailinglists(List<Integer> mailingLists, int companyID, int datasourceId, int mode, Map<Integer, Integer> mailinglistStat, JdbcTemplate jdbc, String currentTimestamp, List<Integer> updatedRecipients) {
        String sql;
        for (Integer mailinglistId : mailingLists) {
            try {
                if (mode == ImportMode.ADD.getIntValue() || mode == ImportMode.ADD_AND_UPDATE.getIntValue() || mode == ImportMode.UPDATE.getIntValue()) {
                    int added = 0;
                    createRecipientBindTemporaryTable(companyID, datasourceId, updatedRecipients, jdbc);
                    sql = "DELETE FROM cust_" + companyID + "_exist1_tmp" + datasourceId + "_tbl WHERE customer_id IN (SELECT customer_id FROM customer_" + companyID + "_binding_tbl WHERE mailinglist_id=" + mailinglistId + ")";
                    jdbc.execute(sql);
                    sql = "INSERT INTO customer_" + companyID + "_binding_tbl (customer_id, user_type, user_status, user_remark, creation_date, exit_mailing_id, mailinglist_id) (SELECT customer_id, 'W', 1, 'CSV File Upload', " + currentTimestamp + ", 0," + mailinglistId + " FROM cust_" + companyID + "_exist1_tmp" + datasourceId + "_tbl)";
                    added += jdbc.update(sql);
                    mailinglistStat.put(mailinglistId, mailinglistStat.get(mailinglistId) + added);
                } else if (mode == ImportMode.MARK_OPT_OUT.getIntValue()) {
                    int changed = changeStatusInMailingList(companyID, updatedRecipients, jdbc, mailinglistId, BindingEntry.USER_STATUS_OPTOUT, "Mass Opt-Out by Admin", currentTimestamp);
                    mailinglistStat.put(mailinglistId, mailinglistStat.get(mailinglistId) + changed);
                } else if (mode == ImportMode.MARK_BOUNCED.getIntValue()) {
                    int changed = changeStatusInMailingList(companyID, updatedRecipients, jdbc, mailinglistId, BindingEntry.USER_STATUS_BOUNCED, "Mass Bounce by Admin", currentTimestamp);
                    mailinglistStat.put(mailinglistId, mailinglistStat.get(mailinglistId) + changed);
                }
            } catch (Exception e) {
                AgnUtils.logger().error("writeContent: " + e);
            }
            finally {
                removeBindTemporaryTable(companyID, datasourceId, jdbc);
            }
        }
    }

    public HashMap<ProfileRecipientFields, ValidatorResults> getRecipientsByTypePaginated(Integer[] types, int page, int rownums, Integer adminID, int datasourceId) {
        HashMap<ProfileRecipientFields, ValidatorResults> recipients = new HashMap<ProfileRecipientFields, ValidatorResults>();
        if (types == null || types.length == 0) {
            return recipients;
        }

        final JdbcTemplate aTemplate = getJdbcTemplateForTemporaryTable();
        final String prefix = "cust_" + adminID + "_tmp_";
        final String tableName = prefix + datasourceId + "_tbl";
        String typesStr = "(" + StringUtils.join(types, ",") + ")";
        int offset = (page) * rownums;
        String sqlStatement = "SELECT * FROM " + tableName + " WHERE status_type IN " + typesStr;
        if (AgnUtils.isMySQLDB()) {
            sqlStatement = sqlStatement + " LIMIT  " + offset + " , " + rownums;
        }
        if (AgnUtils.isOracleDB()) {
            sqlStatement = "SELECT * FROM ( SELECT recipient, validator_result, rownum r FROM ( " + sqlStatement + " )  WHERE 1=1 ) WHERE r BETWEEN " + (offset + 1) + " AND " + (offset + rownums);
        }
        List<Map> tmpList = aTemplate.queryForList(sqlStatement);


        for (Map row : tmpList) {
            Object recipientBean = ImportUtils.deserialiseBean((byte[]) row.get("recipient"));
            final ProfileRecipientFields recipient = (ProfileRecipientFields) recipientBean;
            ValidatorResults validatorResults = null;
            if (row.get("validator_result") != null) {
                Object validatorResultsBean = ImportUtils.deserialiseBean((byte[]) row.get("validator_result"));
                validatorResults = (ValidatorResults) validatorResultsBean;
            }
            recipients.put(recipient, validatorResults);
        }

        return recipients;
    }

    public int getRecipientsCountByType(Integer[] types, Integer adminID, int datasourceId) {
        final JdbcTemplate aTemplate = getJdbcTemplateForTemporaryTable();
        final String prefix = "cust_" + adminID + "_tmp_";
        final String tableName = prefix + datasourceId + "_tbl";
        String typesStr = "(" + StringUtils.join(types, ",") + ")";
        int totalRows = aTemplate.queryForInt("SELECT count(temporary_id) FROM " + tableName + " WHERE status_type IN " + typesStr);
        return totalRows;
    }


    public PaginatedList getInvalidRecipientList(CSVColumnState[] columns, String sort, String direction, int page, int rownums, int previousFullListSize, Integer adminID, int datasource_id) throws Exception {
        final JdbcTemplate aTemplate = getJdbcTemplateForTemporaryTable();
        final String prefix = "cust_" + adminID + "_tmp_";
        final String tableName = prefix + datasource_id + "_tbl";
        int totalRows = aTemplate.queryForInt("SELECT count(temporary_id) FROM " + tableName + " WHERE status_type=" + NewImportWizardService.RECIPIENT_TYPE_FIELD_INVALID);
        if (previousFullListSize == 0 || previousFullListSize != totalRows) {
            page = 1;
        }

        int offset = (page - 1) * rownums;
        String sqlStatement = "SELECT * FROM " + tableName + " where status_type=" + NewImportWizardService.RECIPIENT_TYPE_FIELD_INVALID;
        if (AgnUtils.isMySQLDB()) {
            sqlStatement = sqlStatement + " LIMIT  " + offset + " , " + rownums;
        }
        if (AgnUtils.isOracleDB()) {
            sqlStatement = "SELECT * from ( select recipient, validator_result, rownum r from ( " + sqlStatement + " )  where 1=1 ) where r between " + (offset + 1) + " and " + (offset + rownums);
        }
        List<Map> tmpList = aTemplate.queryForList(sqlStatement);

        final List<DynaProperty> properties = new ArrayList<DynaProperty>();
        for (CSVColumnState column : columns) {
            if (column.getImportedColumn()) {
                properties.add(new DynaProperty(column.getColName(), String.class));
            }
        }
        properties.add(new DynaProperty(NewImportWizardService.VALIDATOR_RESULT_RESERVED, ValidatorResults.class));
        properties.add(new DynaProperty(NewImportWizardService.ERROR_EDIT_RECIPIENT_EDIT_RESERVED, ProfileRecipientFields.class));


        BasicDynaClass dynaClass = new BasicDynaClass("recipient", null, properties.toArray(new DynaProperty[properties.size()]));
        List<DynaBean> result = new ArrayList<DynaBean>();
        for (Map row : tmpList) {
            DynaBean newBean = dynaClass.newInstance();
            final ProfileRecipientFields recipient = (ProfileRecipientFields) ImportUtils.deserialiseBean((byte[]) row.get("recipient"));
            final ValidatorResults validatorResult = (ValidatorResults) ImportUtils.deserialiseBean((byte[]) row.get("validator_result"));
            for (CSVColumnState column : columns) {
                if (column.getImportedColumn()) {
                    newBean.set(column.getColName(), Toolkit.getValueFromBean(recipient, column.getColName()));
                }
            }
            newBean.set(NewImportWizardService.VALIDATOR_RESULT_RESERVED, validatorResult);
            newBean.set(NewImportWizardService.ERROR_EDIT_RECIPIENT_EDIT_RESERVED, recipient);
            result.add(newBean);
        }

        DynaBeanPaginatedListImpl paginatedList = new DynaBeanPaginatedListImpl(result, totalRows, rownums, page, sort, direction);
        return paginatedList;
    }

    public Set<String> loadBlackList(int companyID) throws Exception {
        final JdbcTemplate jdbcTemplate = createJdbcTemplate();
        SqlRowSet rset = null;
        Set<String> blacklist = new HashSet<String>();
        try {
            // @todo: agn
            // removed because no customer id in cust_ban_tbl.
            // rset = jdbcTemplate.queryForRowSet("SELECT email FROM cust_ban_tbl WHERE company_id=? OR company_id=0", params);
            rset = jdbcTemplate.queryForRowSet("SELECT email FROM cust_ban_tbl");
            while (rset.next()) {
                blacklist.add(rset.getString(1).toLowerCase());
            }
        } catch (Exception e) {
            AgnUtils.logger().error("loadBlacklist: " + e);
            throw new Exception(e.getMessage());
        }

        return blacklist;
    }

    public HashMap<ProfileRecipientFields, ValidatorResults> getDuplicateRecipientsFromNewDataOnly(Map<ProfileRecipientFields, ValidatorResults> listOfValidBeans, ImportProfile profile, CSVColumnState[] columns, Integer adminID, int datasource_id) {
        final HashMap<ProfileRecipientFields, ValidatorResults> result = new HashMap<ProfileRecipientFields, ValidatorResults>();
        if (listOfValidBeans.isEmpty()) {
            return result;
        }

        final String prefix = "cust_" + adminID + "_tmp_";
        final String tableName = prefix + datasource_id + "_tbl";
        final HashMap<String, ProfileRecipientFields> columnKeyValueToTemporaryIdMap = new HashMap<String, ProfileRecipientFields>();
        final JdbcTemplate template = getJdbcTemplateForTemporaryTable();
        int type = 0;
        for (CSVColumnState column : columns) {
            if (column.getColName().equals(profile.getKeyColumn()) && column.getImportedColumn()) {
                type = column.getType();
            }
        }
        List parameters = new ArrayList();
        String columnKeyBuffer = "(";
        int i = 0;
        for (ProfileRecipientFields profileRecipientFields : listOfValidBeans.keySet()) {
            String value = Toolkit.getValueFromBean(profileRecipientFields, profile.getKeyColumn());
            //value = value.toLowerCase();
            if (columnKeyValueToTemporaryIdMap.containsKey(value)) {
                result.put(profileRecipientFields, null);
                continue;
            }

            if (type == CSVColumnState.TYPE_DATE) {
                Date date = ImportUtils.getDateAsString(value, profile.getDateFormat());
                final Date sqlDate = createDateValue(date);
                if (columnKeyValueToTemporaryIdMap.containsKey(sqlDate.toString())) {
                    result.put(profileRecipientFields, null);
                    continue;
                }
            }

            if (type == CSVColumnState.TYPE_CHAR) {
                parameters.add(value);
                columnKeyBuffer = columnKeyBuffer + " ?,";
            } else if (type == CSVColumnState.TYPE_NUMERIC) {
                parameters.add(Double.valueOf(value));
                columnKeyBuffer = columnKeyBuffer + " ?,";
            } else {
                Date date = ImportUtils.getDateAsString(value, profile.getDateFormat());
                final Date sqlDate = createDateValue(date);
                parameters.add(sqlDate);
                value = String.valueOf(sqlDate.toString());
                columnKeyBuffer = columnKeyBuffer + " ?,";
            }
            columnKeyValueToTemporaryIdMap.put(value, profileRecipientFields);
            i++;
        }
        columnKeyBuffer = columnKeyBuffer.substring(0, columnKeyBuffer.length() - 1);
        columnKeyBuffer = columnKeyBuffer + ")";

        String query = null;
        if (AgnUtils.isOracleDB()) {
            query = "SELECT i.column_duplicate_check as temporary_recipient_keyColumn FROM " + tableName + " i" +
                    " WHERE " + "(i.column_duplicate_check IN" + columnKeyBuffer + " AND (i.status_type=" +
                    NewImportWizardService.RECIPIENT_TYPE_VALID + " OR i.status_type=" + NewImportWizardService.RECIPIENT_TYPE_FIXED_BY_HAND + " OR i.status_type=" + NewImportWizardService.RECIPIENT_TYPE_DUPLICATE_RECIPIENT + "))";
        }

        if (AgnUtils.isMySQLDB()) {
            //@todo: select doesn't use any indexes, need improve it later
            query = "SELECT i.column_duplicate_check as temporary_recipient_keyColumn FROM " + tableName + " i" +
                    " WHERE  " + "(BINARY i.column_duplicate_check IN" + columnKeyBuffer + " AND (i.status_type=" +
                    NewImportWizardService.RECIPIENT_TYPE_VALID + " OR i.status_type=" + NewImportWizardService.RECIPIENT_TYPE_FIXED_BY_HAND + " OR i.status_type=" + NewImportWizardService.RECIPIENT_TYPE_DUPLICATE_RECIPIENT + "))";
        }
        final List<Map> resultList = template.queryForList(query, parameters.toArray());
        for (Map row : resultList) {
            if (row.get("temporary_recipient_keyColumn") != null) {
                if (type == CSVColumnState.TYPE_CHAR) {
                    String value = (String) row.get("temporary_recipient_keyColumn");
                    //value = value.toLowerCase();
                    final ProfileRecipientFields recipientFields = columnKeyValueToTemporaryIdMap.get(value);
                    result.put(recipientFields, null);
                } else if (type == CSVColumnState.TYPE_NUMERIC) {
                    Double value = (Double) row.get("temporary_recipient_keyColumn");
                    final ProfileRecipientFields recipientFields = columnKeyValueToTemporaryIdMap.get(String.valueOf(value));
                    result.put(recipientFields, null);
                } else if (type == CSVColumnState.TYPE_DATE) {
                    java.util.Date value = (java.util.Date) row.get("temporary_recipient_keyColumn");
                    final ProfileRecipientFields recipientFields = columnKeyValueToTemporaryIdMap.get(value.toString());
                    result.put(recipientFields, null);
                }
            }
        }
        return result;
    }

    private Date createDateValue(Date date) {
        return (AgnUtils.isOracleDB()) ? new Timestamp(date.getTime()) : new java.sql.Date(date.getTime());
    }

    public void updateRecipients(final Map<ProfileRecipientFields, ValidatorResults> recipientBeans, Integer adminID, final int type, final ImportProfile profile, int datasource_id, CSVColumnState[] columns) {
        if (recipientBeans.isEmpty()) {
            return;
        }
        final JdbcTemplate template = getJdbcTemplateForTemporaryTable();
        final String prefix = "cust_" + adminID + "_tmp_";
        final String tableName = prefix + datasource_id + "_tbl";
        final ProfileRecipientFields[] recipients = recipientBeans.keySet().toArray(new ProfileRecipientFields[recipientBeans.keySet().size()]);
        final String query = "UPDATE  " + tableName + " SET recipient=?, validator_result=?, status_type=?, column_duplicate_check=? WHERE temporary_id=?";
        CSVColumnState temporaryKeyColumn = null;
        for (CSVColumnState column : columns) {
            if (column.getColName().equals(profile.getKeyColumn()) && column.getImportedColumn()) {
                temporaryKeyColumn = column;
            }
        }
        final CSVColumnState keyColumn = temporaryKeyColumn;
        final BatchPreparedStatementSetter setter = new BatchPreparedStatementSetter() {

            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setBytes(1, ImportUtils.getObjectAsBytes(recipients[i]));
                ps.setBytes(2, ImportUtils.getObjectAsBytes(recipientBeans.get(recipients[i])));
                ps.setInt(3, type);
                setPreparedStatmentForCurrentColumn(ps, 4, keyColumn, recipients[i], profile);
                ps.setString(5, recipients[i].getTemporaryId());
            }

            public int getBatchSize() {
                return recipientBeans.size();
            }
        };
        template.batchUpdate(query, setter);
    }

    public void addNewRecipients(final Map<ProfileRecipientFields, ValidatorResults> validRecipients, Integer adminId, final ImportProfile importProfile, final CSVColumnState[] columns, final int datasourceID) {
        if (validRecipients.isEmpty()) {
            return;
        }

		String currentTimestamp = AgnUtils.getHibernateDialect().getCurrentTimestampSQLFunctionName();

        final JdbcTemplate template = createJdbcTemplate();
        final ProfileRecipientFields[] recipientsBean = validRecipients.keySet().toArray(new ProfileRecipientFields[validRecipients.size()]);

        final int[] newcustomerIDs = getNextCustomerSequences(importProfile.getCompanyId(), recipientsBean.length);

        final String tableName = "customer_" + importProfile.getCompanyId() + "_tbl";

        String query = "INSERT INTO " + tableName + " (";

        if (AgnUtils.isOracleDB()) {
            query = query + "customer_id,";
        }

        query = query + "mailtype, datasource_id, ";
        for (CSVColumnState column : columns) {
            if (column.getImportedColumn() && !column.getColName().equals("mailtype")) {
                query = query + column.getColName() + ", ";
            }
        }

        query = query.substring(0, query.length() - 2);
		query = query + ", creation_date) VALUES (";

        if (AgnUtils.isOracleDB()) {
            query = query + "?, ";
        }

        for (CSVColumnState column : columns) {
            if (column.getImportedColumn() && !column.getColName().equals("mailtype")) {
                query = query + "?, ";
            }
        }
        query = query + "?, ?, ";
        query = query.substring(0, query.length() - 2);
        query = query + ", " + currentTimestamp + ")";
        final BatchPreparedStatementSetter setter = new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                int index = 0;
                if (AgnUtils.isOracleDB()) {
                    ps.setInt(1, newcustomerIDs[i]);
                    ps.setInt(2, Integer.valueOf(recipientsBean[i].getMailtype()));
                    ps.setInt(3, datasourceID);
                    index = 4;
                }
                if (AgnUtils.isMySQLDB()) {
                    ps.setInt(1, Integer.valueOf(recipientsBean[i].getMailtype()));
                    ps.setInt(2, datasourceID);
                    index = 3;
                }
                for (CSVColumnState column : columns) {
                    if (column.getImportedColumn() && !column.getColName().equals("mailtype")) {
                        setPreparedStatmentForCurrentColumn(ps, index, column, recipientsBean[i], importProfile);
                        index++;
                    }
                }


            }

            public int getBatchSize() {
                return validRecipients.size();
            }
        };

        template.batchUpdate(query, setter);
		updateMySQLSequenceSimulator(importProfile.getCompanyId());
    }

	private void updateMySQLSequenceSimulator(int companyId) {
		if (AgnUtils.isMySQLDB()) {
			JdbcTemplate template = createJdbcTemplate();
			String sql = "INSERT INTO customer_" + companyId + "_tbl_seq (customer_id) SELECT max(customer_id) " +
					"FROM customer_" + companyId + "_tbl";
	        template.execute(sql);
		}
    }

    private void setPreparedStatmentForCurrentColumn(PreparedStatement ps, int index, CSVColumnState column, ProfileRecipientFields bean, ImportProfile importProfile) throws SQLException {
        final String value = Toolkit.getValueFromBean(bean, column.getColName());

        if (column.getType() == CSVColumnState.TYPE_NUMERIC && column.getColName().equals("gender")) {
            if (value == null) {
                ps.setNull(index, Types.NUMERIC);
            } else {
                if (GenericValidator.isInt(value) && Integer.valueOf(value) <= 5 && Integer.valueOf(value) >= 0) {
                    ps.setInt(index, Integer.valueOf(value));
                } else {
                    final Integer intValue = importProfile.getGenderMapping().get(value);
                    ps.setInt(index, intValue);
                }
            }

        } else if (column.getType() == CSVColumnState.TYPE_CHAR) {
            if (value == null) {
                ps.setNull(index, Types.VARCHAR);
            } else {
                ps.setString(index, value);
            }
        } else if (column.getType() == CSVColumnState.TYPE_NUMERIC) {
            if (value == null) {
                ps.setNull(index, Types.NUMERIC);
            } else {
                ps.setDouble(index, Double.valueOf(value));
            }
        } else if (column.getType() == CSVColumnState.TYPE_DATE) {
            if (value == null) {
                ps.setNull(index, Types.DATE);
            } else {
                Date date = ImportUtils.getDateAsString(value, importProfile.getDateFormat());

                ps.setTimestamp(index, new Timestamp(date.getTime()));
            }
        }
    }

    public HashMap<ProfileRecipientFields, ValidatorResults> getDuplicateRecipientsFromExistData(Map<ProfileRecipientFields, ValidatorResults> listOfValidBeans, ImportProfile profile, CSVColumnState[] columns) {
        final HashMap<ProfileRecipientFields, ValidatorResults> result = new HashMap<ProfileRecipientFields, ValidatorResults>();
        if (listOfValidBeans.isEmpty()) {
            return result;
        }
        final HashMap<Object, ProfileRecipientFields> columnKeyValueToTemporaryIdMap = new HashMap<Object, ProfileRecipientFields>();
        final JdbcTemplate template = createJdbcTemplate();
        int type = 0;
        for (CSVColumnState column : columns) {
            if (column.getColName().equals(profile.getKeyColumn()) && column.getImportedColumn()) {
                type = column.getType();
            }
        }
        List parameters = new ArrayList();
        String columnKeyBuffer = "(";
        int i = 0;
        for (ProfileRecipientFields profileRecipientFields : listOfValidBeans.keySet()) {
            String value = Toolkit.getValueFromBean(profileRecipientFields, profile.getKeyColumn());
            //value = value.toLowerCase();
            if (columnKeyValueToTemporaryIdMap.containsKey(value)) {
                result.put(profileRecipientFields, null);
                continue;
            }

            if (type == CSVColumnState.TYPE_DATE) {
                Date date = ImportUtils.getDateAsString(value, profile.getDateFormat());
                final Date sqlDate = createDateValue(date);
                if (columnKeyValueToTemporaryIdMap.containsKey(sqlDate.toString())) {
                    result.put(profileRecipientFields, null);
                    continue;
                }
            }
            if (type == CSVColumnState.TYPE_CHAR) {
                parameters.add(value);
                columnKeyBuffer = columnKeyBuffer + " ?,";
            } else if (type == CSVColumnState.TYPE_NUMERIC) {
                parameters.add(Double.valueOf(value));
                columnKeyBuffer = columnKeyBuffer + " ?,";
            } else {
                Date date = ImportUtils.getDateAsString(value, profile.getDateFormat());
                final Date sqlDate = createDateValue(date);
                parameters.add(sqlDate);
                value = sqlDate.toString();
                columnKeyBuffer = columnKeyBuffer + " ?,";
            }
            columnKeyValueToTemporaryIdMap.put(value, profileRecipientFields);
            i++;
        }
        columnKeyBuffer = columnKeyBuffer.substring(0, columnKeyBuffer.length() - 1);
        columnKeyBuffer = columnKeyBuffer + ")";
        String query = null;
        if (AgnUtils.isOracleDB()) {
            query = "SELECT c." + profile.getKeyColumn() + " as temporary_recipient_keyColumn, customer_id FROM customer_" + profile.getCompanyId() + "_tbl c" +
                    " WHERE  (c." + profile.getKeyColumn() + ") IN" + columnKeyBuffer;
        }

        if (AgnUtils.isMySQLDB()) {
            query = "SELECT c." + profile.getKeyColumn() + " as temporary_recipient_keyColumn, customer_id FROM customer_" + profile.getCompanyId() + "_tbl c" +
                    " WHERE   BINARY c." + profile.getKeyColumn() + " IN" + columnKeyBuffer;
        }
        final List<Map> resultList = template.queryForList(query, parameters.toArray());
        for (Map row : resultList) {
            if (row.get("temporary_recipient_keyColumn") != null) {
                if (type == CSVColumnState.TYPE_CHAR) {
                    String value = (String) row.get("temporary_recipient_keyColumn");
                    //value = value.toLowerCase();
                    final ProfileRecipientFields recipientFields = columnKeyValueToTemporaryIdMap.get(value);
                    recipientFields.setUpdatedId(((Number) row.get("customer_id")).intValue());
                    result.put(recipientFields, null);
                } else if (type == CSVColumnState.TYPE_NUMERIC) {
                    Double value = (Double) row.get("temporary_recipient_keyColumn");
                    final ProfileRecipientFields recipientFields = columnKeyValueToTemporaryIdMap.get(String.valueOf(value));
                    recipientFields.setUpdatedId(((Number) row.get("customer_id")).intValue());
                    result.put(recipientFields, null);
                } else if (type == CSVColumnState.TYPE_DATE) {
                    java.util.Date value = (java.util.Date) row.get("temporary_recipient_keyColumn");
                    final ProfileRecipientFields recipientFields = columnKeyValueToTemporaryIdMap.get(value.toString());
                    recipientFields.setUpdatedId(((Number) row.get("customer_id")).intValue());
                    result.put(recipientFields, null);
                }
            }
        }
        return result;
    }

    public void updateExistRecipients(final Collection<ProfileRecipientFields> recipientsForUpdate, final ImportProfile importProfile, final CSVColumnState[] columns, Integer adminId) {
        if (recipientsForUpdate.isEmpty()) {
            return;
        }

        final JdbcTemplate template = createJdbcTemplate();
        final ProfileRecipientFields[] recipientsBean = recipientsForUpdate.toArray(new ProfileRecipientFields[recipientsForUpdate.size()]);
        final String[] querys = new String[recipientsForUpdate.size()];
        int type = 0;
        for (int i = 0; i < querys.length; i++) {
            String query = "UPDATE  customer_" + importProfile.getCompanyId() + "_tbl SET ";
            query = query + "mailtype=" + recipientsBean[i].getMailtype() + ", ";
            for (CSVColumnState column : columns) {
                if (column.getImportedColumn() && !column.getColName().equals("mailtype")) {
                    if (column.getColName().equals(importProfile.getKeyColumn())) {
                        type = column.getType();
                    }
                    final String value = Toolkit.getValueFromBean(recipientsBean[i], column.getColName());

                    // @todo: agn: value == null
                    if (StringUtils.isEmpty(value) && importProfile.getNullValuesAction() == NullValuesAction.OVERWRITE.getIntValue()) {
                        query = query + column.getColName() + "=NULL, ";
                    } else if (!StringUtils.isEmpty(value)) {
                        if (column.getColName().equals("gender")) {
                            if (GenericValidator.isInt(value)) {
                                query = query + column.getColName() + "=" + value + ", ";
                            } else {
                                final Integer intValue = importProfile.getGenderMapping().get(value);
                                query = query + column.getColName() + "=" + intValue + ", ";
                            }
                        } else {
                            switch (column.getType()) {
                                case CSVColumnState.TYPE_CHAR:
                                    query = query + column.getColName() + "='" + value + "', ";
                                    break;
                                case CSVColumnState.TYPE_NUMERIC:
                                    query = query + column.getColName() + "=" + value + ", ";
                                    break;
                                case CSVColumnState.TYPE_DATE:
                                    final int format = importProfile.getDateFormat();
                                    Date date = ImportUtils.getDateAsString(value, format);
                                    if (AgnUtils.isMySQLDB()) {
                                        query = query + column.getColName() + "='" + new Timestamp(date.getTime()).toString() + "', ";
                                    }
                                    if (AgnUtils.isOracleDB()) {
                                        final String dateAsFormatedString = DB_DATE_FORMAT.format(date);
                                        query = query + column.getColName() + "=to_date('"
                                                + dateAsFormatedString + "', 'dd.MM.YYYY HH24:MI:SS'), ";
                                    }
                                    break;
                            }
                        }
                    }
                }
            }

            query = query.substring(0, query.length() - 2);
            final String value = Toolkit.getValueFromBean(recipientsBean[i], importProfile.getKeyColumn());

            switch (type) {
                case CSVColumnState.TYPE_CHAR:
                    query = query + " WHERE " + importProfile.getKeyColumn() + "='" + value + "'";
                    break;
                case CSVColumnState.TYPE_NUMERIC:
                    query = query + " WHERE " + importProfile.getKeyColumn() + "=?";
                    break;
                case CSVColumnState.TYPE_DATE:
                    Date date = ImportUtils.getDateAsString(value, importProfile.getDateFormat());
                    if (AgnUtils.isMySQLDB()) {
                        query = query + " WHERE " + importProfile.getKeyColumn()
                                + "='" + new java.sql.Date(date.getTime()).toString() + "'";
                    }
                    if (AgnUtils.isOracleDB()) {
                        final String dateAsFormatedString = DB_DATE_FORMAT.format(date);
                        query = query + " WHERE " + importProfile.getKeyColumn() +
                                "=to_date('" + dateAsFormatedString + "', 'dd.MM.YYYY HH24:MI:SS')";
                    }
                    break;
            }
            querys[i] = query;
        }
        template.batchUpdate(querys);
    }

    public void importInToBlackList(final Collection<ProfileRecipientFields> recipients, final int companyId) {
        if (recipients.isEmpty()) {
            return;
        }
        final JdbcTemplate template = createJdbcTemplate();
        final ProfileRecipientFields[] recipientsArray = recipients.toArray(new ProfileRecipientFields[recipients.size()]);
        String query = "INSERT INTO cust_ban_tbl (company_id, email) VALUES (?,?)";
        final BatchPreparedStatementSetter setter = new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, companyId);
                ps.setString(2, recipientsArray[i].getEmail());
            }

            public int getBatchSize() {
                return recipients.size();
            }
        };
        template.batchUpdate(query, setter);
    }

    public void createTemporaryTable(int adminID, int datasource_id, String keyColumn, int companyId, String sessionId) {
        final DataSource dataSource = (DataSource) applicationContext.getBean("dataSource");
        try {
            if (temporaryConnection != null) {
                temporaryConnection.destroy();
                temporaryConnection = null;
            }
            SingleConnectionDataSource scds = null;
            scds = new SingleConnectionDataSource(dataSource.getConnection(), true);
            setJdbcTemplateForTemporaryTable(scds);
        } catch (SQLException e) {
            throw new DataAccessResourceFailureException("Unable to create single connnection ds", e);
        }

        final JdbcTemplate template = getJdbcTemplateForTemporaryTable();
        final String prefix = "cust_" + adminID + "_tmp_";
        final String tableName = prefix + datasource_id + "_tbl";
        if (AgnUtils.isMySQLDB()) {
            String query = "CREATE TEMPORARY TABLE IF NOT EXISTS " + tableName + " as (select " + keyColumn + " as column_duplicate_check from customer_" + companyId + "_tbl where 1=0)";
            template.execute(query);
            query = "ALTER TABLE " + tableName + " ADD (recipient mediumblob NOT NULL, " +
                    "validator_result mediumblob NOT NULL, " +
                    "temporary_id varchar(128) NOT NULL, " +
                    "INDEX (column_duplicate_check), " +
                    "status_type int(3) NOT NULL)";
            template.execute(query);
            query = "alter table " + tableName + " collate utf8_unicode_ci";
            template.execute(query);
        } else if (AgnUtils.isOracleDB()) {
            // @todo: we need to decide when all those tables will be removed
            String query = "CREATE TABLE " + tableName + " as (select " + keyColumn + " as column_duplicate_check from customer_" + companyId + "_tbl where 1=0)";
            template.execute(query);
            query = "ALTER TABLE " + tableName + " ADD (recipient blob NOT NULL, " +
                    "validator_result blob NOT NULL, " +
                    "temporary_id varchar2(128) NOT NULL, " +
                    "status_type number(3) NOT NULL)";
            template.execute(query);
            String indexquery = "create index " + tableName + "_cdc on " + tableName + " (column_duplicate_check) nologging";
            template.execute(indexquery);
            query = " INSERT INTO IMPORT_TMP_TABLES (SESSION_ID, TEMPORARY_TABLE_NAME) VALUES('" + sessionId + "', '" + tableName + "')";
            template.execute(query);
        }
    }

    public void setJdbcTemplateForTemporaryTable(SingleConnectionDataSource temporaryConnection) {
        this.temporaryConnection = temporaryConnection;
    }

    private int changeStatusInMailingList(int companyID, List<Integer> updatedRecipients, JdbcTemplate jdbc,
                                          int mailinglistId, int newStatus, String remark, String currentTimestamp) {
        if (updatedRecipients.size() == 0) {
            return 0;
        }
        String recipientsStr = StringUtils.join(updatedRecipients, ',');
        String sql = "UPDATE customer_" + companyID + "_binding_tbl SET user_status=" + newStatus +
                ", exit_mailing_id=0, user_remark='" + remark + "', change_date=" + currentTimestamp +
                " WHERE mailinglist_id=" + mailinglistId + " AND customer_id IN (" + recipientsStr +
                ") AND user_status=" + BindingEntry.USER_STATUS_ACTIVE;
        return jdbc.update(sql);
    }

    private void createRecipientBindTemporaryTable(int companyID, int datasourceId, final List<Integer> updatedRecipients, JdbcTemplate jdbc) {
        String sql = removeBindTemporaryTable(companyID, datasourceId, jdbc);
        if (AgnUtils.isMySQLDB()) {
            sql = "CREATE TEMPORARY TABLE cust_" + companyID + "_exist1_tmp" + datasourceId + "_tbl (`customer_id` int(10) unsigned NOT NULL)";
        } else if (AgnUtils.isOracleDB()) {
            sql = "CREATE TABLE cust_" + companyID + "_exist1_tmp" + datasourceId + "_tbl (customer_id NUMBER(10) NOT NULL)";
        }
        jdbc.execute(sql);
        if (updatedRecipients.isEmpty()) {
            return;
        }
        sql = "INSERT INTO cust_" + companyID + "_exist1_tmp" + datasourceId + "_tbl (customer_id) VALUES (?)";

        final BatchPreparedStatementSetter setter = new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, updatedRecipients.get(i));
            }

            public int getBatchSize() {
                return updatedRecipients.size();
            }
        };
        jdbc.batchUpdate(sql, setter);
    }

    private String removeBindTemporaryTable(int companyID, int datasourceId, JdbcTemplate jdbc) {
        String sql = "DROP TABLE cust_" + companyID + "_exist1_tmp" + datasourceId + "_tbl";
        try {
            jdbc.execute(sql);
        } catch (Exception e) {
            AgnUtils.logger().info("Tried to remove table that doesn't exist", e);
        }
        return sql;
    }

    private int getNextCustomerSequence(int companyID, JdbcTemplate template) {
        String query = " SELECT customer_" + companyID + "_tbl_seq.nextval FROM DUAL ";
        return template.queryForInt(query);
    }

    private int[] getNextCustomerSequences(int companyID, int amount) {
        int[] customerids = new int[amount];
        if (AgnUtils.isOracleDB()) {
            JdbcTemplate template = createJdbcTemplate();
            for (int i = 0; i < amount; i++) {
                customerids[i] = getNextCustomerSequence(companyID, template);
            }
        }
        return customerids;
    }

    private JdbcTemplate getJdbcTemplateForTemporaryTable() {
        return new JdbcTemplate(temporaryConnection);
    }

    public SingleConnectionDataSource getTemporaryConnection() {
        return temporaryConnection;
    }
}
