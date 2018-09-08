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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.agnitas.beans.BindingEntry;
import org.agnitas.beans.ImportProfile;
import org.agnitas.beans.ProfileRecipientFields;
import org.agnitas.beans.impl.ImportKeyColumnsKey;
import org.agnitas.beans.impl.PaginatedListImpl;
import org.agnitas.dao.ImportRecipientsDao;
import org.agnitas.emm.core.velocity.VelocityCheck;
import org.agnitas.service.NewImportWizardService;
import org.agnitas.service.csv.Toolkit;
import org.agnitas.service.impl.CSVColumnState;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.DbUtilities;
import org.agnitas.util.ImportRecipientsToolongValueException;
import org.agnitas.util.ImportUtils;
import org.agnitas.util.importvalues.ImportMode;
import org.agnitas.util.importvalues.NullValuesAction;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.ValidatorResults;
import org.apache.log4j.Logger;
import org.displaytag.pagination.PaginatedList;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * @author Viktor Gema
 */
public class ImportRecipientsDaoImpl extends AbstractImportDao implements ImportRecipientsDao {

	private static final transient Logger logger = Logger.getLogger( ImportRecipientsDaoImpl.class);
    public static final String MYSQL_COMPARE_DATE_FORMAT = "%Y-%m-%d %H:%i:%s";
    public static final String JAVA_COMPARE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private SingleConnectionDataSource temporaryConnection;
    private static final SimpleDateFormat DB_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    public static final int MAX_WRITE_PROGRESS = 90;
    public static final int MAX_WRITE_PROGRESS_HALF = MAX_WRITE_PROGRESS / 2;

    @Override
	public Map<String, Map<String, Object>> getColumnInfoByColumnName(@VelocityCheck int companyId, String column) {
		DataSource dataSource = (DataSource) applicationContext.getBean("dataSource");
		Connection connection = DataSourceUtils.getConnection(dataSource);
		ResultSet resultSet = null;
		LinkedHashMap<String, Map<String, Object>> list = new LinkedHashMap<String, Map<String, Object>>();
		try {
			DatabaseMetaData metaData = connection.getMetaData();
			String tableName = "customer_" + companyId + "_tbl";
			if (AgnUtils.isOracleDB()) {
				resultSet = metaData.getColumns(null, metaData.getUserName().toUpperCase(), tableName.toUpperCase(), column.toUpperCase());
			} else {
				resultSet = metaData.getColumns(null, null, tableName.toLowerCase(), column);
			}
			if (resultSet != null) {
				while (resultSet.next()) {
					Map<String, Object> mapping = new HashMap<String, Object>();
					mapping.put("column", resultSet.getString(4).toLowerCase());
					mapping.put("shortname", resultSet.getString(4).toLowerCase());
					mapping.put("type", ImportUtils.dbtype2string(resultSet.getInt(5)));
					mapping.put("length", resultSet.getInt(7));
					if (resultSet.getInt(11) == DatabaseMetaData.columnNullable) {
						mapping.put("nullable", 1);
					} else {
						mapping.put("nullable", 0);
					}

					list.put((String) mapping.get("shortname"), mapping);
				}
			}
		} catch (Exception e) {
			logger.error(MessageFormat.format("Failed to get column ({0}) info for companyId ({1})", column, companyId), e);
		} finally {
			DbUtilities.closeQuietly(resultSet);
			DbUtilities.closeQuietly(connection);
		}
		return list;
	}

    @Override
    public void createRecipients(final Map<ProfileRecipientFields, ValidatorResults> recipientBeansMap, final Integer adminID, final ImportProfile profile, final Integer type, int datasource_id, CSVColumnState[] columns) {
        if (recipientBeansMap.isEmpty()) {
            return;
        }
        final JdbcTemplate template = getJdbcTemplateForTemporaryTable();
        final String prefix = "cust_" + adminID + "_tmp_";
        final String tableName = prefix + datasource_id + "_tbl";
        final ProfileRecipientFields[] recipients = recipientBeansMap.keySet().toArray(new ProfileRecipientFields[recipientBeansMap.keySet().size()]);
        String keyColumn = profile.getKeyColumn();
        List<String> keyColumns = profile.getKeyColumns();
        String duplicateSql = "";
        String duplicateSqlParams = "";
        if (keyColumns.isEmpty()) {
            duplicateSql += " column_duplicate_check_0 ";
            duplicateSqlParams = "?";
        } else {
            for (int i = 0; i < keyColumns.size(); i++) {
                duplicateSql += "column_duplicate_check_" + i;
                duplicateSqlParams += "?";
                if (i != keyColumns.size() - 1) {
                    duplicateSql += ",";
                    duplicateSqlParams += ",";
                }
            }
        }
        final List<CSVColumnState> temporaryKeyColumns = new ArrayList<CSVColumnState>();
        for (CSVColumnState column : columns) {
            if (keyColumns.isEmpty()) {
                if (column.getColName().equals(keyColumn) && column.getImportedColumn()) {
                    temporaryKeyColumns.add(column);
                }
            } else {
                for (String columnName : keyColumns) {
                    if (column.getColName().equals(columnName) && column.getImportedColumn()) {
                        temporaryKeyColumns.add(column);
                        break;
                    }
                }
            }
        }
        final String query = "INSERT INTO " + tableName + " (recipient, validator_result, temporary_id, status_type, " + duplicateSql + ") VALUES (?,?,?,?," + duplicateSqlParams + ")";
        final BatchPreparedStatementSetter setter = new BatchPreparedStatementSetter() {

            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setBytes(1, ImportUtils.getObjectAsBytes(recipients[i]));
                ps.setBytes(2, ImportUtils.getObjectAsBytes(recipientBeansMap.get(recipients[i])));
                ps.setString(3, recipients[i].getTemporaryId());
                ps.setInt(4, type);
                for (int j = 0; j < temporaryKeyColumns.size(); j++) {
                    setPreparedStatmentForCurrentColumn(ps, 5 + j, temporaryKeyColumns.get(j), recipients[i], profile, recipientBeansMap.get(recipients[i]));
                }

                if( logger.isInfoEnabled()) {
                	logger.info("Import ID: " + profile.getImportId() + " Adding recipient to temp-table: " + Toolkit.getValueFromBean(recipients[i], profile.getKeyColumn()));
                }
            }

            public int getBatchSize() {
                return recipientBeansMap.size();
            }
        };
        template.batchUpdate(query, setter);
    }

    @Override
    public HashMap<ProfileRecipientFields, ValidatorResults> getRecipientsByType(int adminID, Integer[] types, int datasource_id) {
        final JdbcTemplate aTemplate = getJdbcTemplateForTemporaryTable();
        final String prefix = "cust_" + adminID + "_tmp_";
        final String tableName = prefix + datasource_id + "_tbl";
        final String typesAsString = StringUtils.join(types, ",");
        String sqlStatement = "SELECT recipient, validator_result FROM " + tableName + " " +
                "WHERE status_type IN (" +
                typesAsString
                + ")";
        @SuppressWarnings("unchecked")
		List<Map<String, Object>> resultList = aTemplate.queryForList(sqlStatement);
        HashMap<ProfileRecipientFields, ValidatorResults> recipients = new HashMap<ProfileRecipientFields, ValidatorResults>();
        for (Map<String, Object> row : resultList) {
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

    @Override
    public Map<Integer, Integer> assignToMailingLists(List<Integer> mailingLists, @VelocityCheck int companyID, int datasourceId, int mode, int adminId, NewImportWizardService importWizardHelper) {
        Map<Integer, Integer> mailinglistStat = new HashMap<Integer, Integer>();
        if (mailingLists == null || mailingLists.isEmpty() || mode == ImportMode.TO_BLACKLIST.getIntValue()) {
            return mailinglistStat;
        }

        JdbcTemplate jdbc = createJdbcTemplate();
        String currentTimestamp = AgnUtils.getHibernateDialect().getCurrentTimestampSQLFunctionName();
        String sql = null;
        importWizardHelper.setCompletedPercent(0);
        double count = 0;
        double diffComplete = 0;
        int newRecipientsCount = jdbc.queryForInt("SELECT COUNT(*) FROM customer_" + companyID + "_tbl WHERE datasource_id = " + datasourceId);
        if (mode != ImportMode.ADD.getIntValue()) {
            count = newRecipientsCount != 0 ? (newRecipientsCount / NewImportWizardService.BLOCK_SIZE) * mailingLists.size() : 1;
            Integer[] types = {NewImportWizardService.RECIPIENT_TYPE_DUPLICATE_RECIPIENT};
            final int updatedRecipients = getRecipientsCountByType(types, adminId, datasourceId);
            if (updatedRecipients > 0) {
                diffComplete = MAX_WRITE_PROGRESS_HALF / (count != 0 ? count : 1);
            } else {
                diffComplete = MAX_WRITE_PROGRESS / (count != 0 ? count : 1);
            }
        } else {
            count = newRecipientsCount != 0 ? (newRecipientsCount / NewImportWizardService.BLOCK_SIZE) * mailingLists.size() : 1;
            diffComplete = MAX_WRITE_PROGRESS / (count != 0 ? count : 1);
        }
        double intNumber = 0;
        // assign new recipients to mailing lists
        for (Integer mailingList : mailingLists) {
            mailinglistStat.put(mailingList, 0);
            if (mode == ImportMode.ADD.getIntValue() || mode == ImportMode.ADD_AND_UPDATE.getIntValue()) {
                int position = 1;
                int recipientIterator = newRecipientsCount;
                int added = 0;
                while (recipientIterator > 0 || (position == 1 && newRecipientsCount > 0)) {
                    final ImportProfile importProfile = importWizardHelper.getImportProfile();
                    if (importProfile != null) {
                    	if( logger.isInfoEnabled()) {
                    		logger.info("Import ID: " + importProfile.getImportId() + " Assigning new recipients to mailinglist with ID " + mailingList + ", datasourceID: " + datasourceId);
                    	}
                    }else{
                    	if( logger.isInfoEnabled()) {
                    		logger.info("Import ID is undefined");
                    	}
                    }
                    
                    if (AgnUtils.isOracleDB()) {
                        sql = "INSERT INTO customer_" + companyID + "_binding_tbl (customer_id, user_type, user_status, user_remark, creation_date, exit_mailing_id, mailinglist_id) " +
                                "(SELECT customer_id, 'W', 1, 'CSV File Upload', " + currentTimestamp + ", 0," + mailingList + " FROM (SELECT customer_id, datasource_id, rownum r FROM customer_" + companyID + "_tbl WHERE datasource_id = " + datasourceId + " AND 1=1) " +
                                " WHERE r BETWEEN " + (((position - 1) * NewImportWizardService.BLOCK_SIZE) + 1) + " AND " + (((position - 1) * NewImportWizardService.BLOCK_SIZE) + NewImportWizardService.BLOCK_SIZE) + " )";
                    } else {
                        sql = "INSERT INTO customer_" + companyID + "_binding_tbl (customer_id, user_type, user_status, user_remark, creation_date, exit_mailing_id, mailinglist_id) " +
                                "(SELECT customer_id, 'W', 1, 'CSV File Upload', " + currentTimestamp + ", 0," + mailingList + " FROM customer_" + companyID + "_tbl " +
                                "WHERE datasource_id = " + datasourceId + " LIMIT " + (position - 1) * NewImportWizardService.BLOCK_SIZE + "," + NewImportWizardService.BLOCK_SIZE + " )";
                    }
                    added = added + jdbc.update(sql);
                    mailinglistStat.put(mailingList, added);
                    intNumber = intNumber + diffComplete;
                    if (intNumber >= 1) {
                        importWizardHelper.setCompletedPercent((int) (importWizardHelper.getCompletedPercent() + Math.floor(intNumber)));
                        intNumber = intNumber - Math.floor(intNumber);
                    }
                    position++;
                    recipientIterator = recipientIterator - NewImportWizardService.BLOCK_SIZE;
                }
            }
        }

        if (mode != ImportMode.ADD.getIntValue()) {
            Integer[] types = {NewImportWizardService.RECIPIENT_TYPE_DUPLICATE_RECIPIENT};
            final int updatedRecipients = getRecipientsCountByType(types, adminId, datasourceId);

            count = count + updatedRecipients != 0 ? updatedRecipients / NewImportWizardService.BLOCK_SIZE : 1;
            if (newRecipientsCount > 0) {
                diffComplete = MAX_WRITE_PROGRESS_HALF / (count != 0 ? count : 1);
            } else {
                diffComplete = MAX_WRITE_PROGRESS / (count != 0 ? count : 1);
            }

        }
        // assign updated recipients to mailing lists
        if (mode != ImportMode.ADD.getIntValue()) {
            Integer[] types = {NewImportWizardService.RECIPIENT_TYPE_DUPLICATE_RECIPIENT};
            int page = 0;
            int rowNum = NewImportWizardService.BLOCK_SIZE;
            HashMap<ProfileRecipientFields, ValidatorResults> recipients = null;
            while (recipients == null || recipients.size() >= rowNum) {
                recipients = getRecipientsByTypePaginated(types, page, rowNum, adminId, datasourceId);
                List<Integer> updatedRecipients = new ArrayList<Integer>();
                for (ProfileRecipientFields recipient : recipients.keySet()) {
                    if (recipient.getUpdatedIds() != null && !recipient.getUpdatedIds().isEmpty()) {
                        updatedRecipients.addAll(recipient.getUpdatedIds());
                    }
                }
                updateMailinglists(mailingLists, companyID, datasourceId, mode, mailinglistStat, jdbc, currentTimestamp, updatedRecipients);
                page++;
                importWizardHelper.setCompletedPercent((int) (importWizardHelper.getCompletedPercent() + diffComplete));
            }
        }
        importWizardHelper.setCompletedPercent(MAX_WRITE_PROGRESS);
        return mailinglistStat;
    }

    @Override
    public void removeTemporaryTable(String tableName, String sessionId) {
        if (AgnUtils.isOracleDB()) {
            final JdbcTemplate template = createJdbcTemplate();
            try {
                String query = "select count(*) from user_tables where table_name = '" + tableName.toUpperCase() + "'";
                int totalRows = template.queryForInt(query);
                if (totalRows != 0) {
                    template.execute("DROP TABLE " + tableName);
                    template.execute("DELETE FROM import_temporary_tables WHERE SESSION_ID='" + sessionId + "'");
                }
            } catch (Exception e) {
            	logger.error( "deleteTemporaryTables: " +  e.getMessage() + " (table: " + tableName + ")", e);
            }
        }
    }

	@Override
	public void removeTemporaryTable(int adminId, int datasourceId, String sessionId) {
		final String tableName = "cust_" + adminId + "_tmp_" + datasourceId + "_tbl";
		removeTemporaryTable(tableName, sessionId);
	}

    @Override
    public List<String> getTemporaryTableNamesBySessionId(String sessionId) {
        List<String> result = new ArrayList<String>();
        final JdbcTemplate template = createJdbcTemplate();
        String query = "SELECT TEMPORARY_TABLE_NAME FROM import_temporary_tables WHERE SESSION_ID='" + sessionId + "'";
        @SuppressWarnings("unchecked")
		List<Map<String, Object>> resultList = template.queryForList(query);
        for (Map<String, Object> row : resultList) {
            final String temporaryTableName = (String) row.get("TEMPORARY_TABLE_NAME");
            result.add(temporaryTableName);
        }
        return result;
    }

    private void updateMailinglists(List<Integer> mailingLists, @VelocityCheck int companyID, int datasourceId, int mode, Map<Integer, Integer> mailinglistStat, JdbcTemplate jdbc, String currentTimestamp, List<Integer> updatedRecipients) {
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
            	logger.error( "writeContent: " + e.getMessage(), e);
            }
            finally {
                removeBindTemporaryTable(companyID, datasourceId, jdbc);
            }
        }
    }

    @Override
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
        if (AgnUtils.isOracleDB()) {
            sqlStatement = "SELECT * FROM ( SELECT recipient, validator_result, rownum r FROM ( " + sqlStatement + " )  WHERE 1=1 ) WHERE r BETWEEN " + (offset + 1) + " AND " + (offset + rownums);
        } else {
            sqlStatement = sqlStatement + " LIMIT  " + offset + " , " + rownums;
        }
        @SuppressWarnings("unchecked")
		List<Map<String, Object>> tmpList = aTemplate.queryForList(sqlStatement);
        for (Map<String, Object> row : tmpList) {
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

    @Override
    public int getRecipientsCountByType(Integer[] types, Integer adminID, int datasourceId) {
        final JdbcTemplate aTemplate = getJdbcTemplateForTemporaryTable();
        final String prefix = "cust_" + adminID + "_tmp_";
        final String tableName = prefix + datasourceId + "_tbl";
        String typesStr = "(" + StringUtils.join(types, ",") + ")";
        int totalRows = aTemplate.queryForInt("SELECT count(temporary_id) FROM " + tableName + " WHERE status_type IN " + typesStr);
        return totalRows;
    }


    @Override
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
        if (AgnUtils.isOracleDB()) {
            sqlStatement = "SELECT * from ( select recipient, validator_result, rownum r from ( " + sqlStatement + " )  where 1=1 ) where r between " + (offset + 1) + " and " + (offset + rownums);
        } else {
            sqlStatement = sqlStatement + " LIMIT  " + offset + " , " + rownums;
        }
        @SuppressWarnings("unchecked")
		List<Map<String, Object>> tmpList = aTemplate.queryForList(sqlStatement);
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> row : tmpList) {
            Map<String, Object> newBean = new HashMap<String, Object>();
            final ProfileRecipientFields recipient = (ProfileRecipientFields) ImportUtils.deserialiseBean((byte[]) row.get("recipient"));
            final ValidatorResults validatorResult = (ValidatorResults) ImportUtils.deserialiseBean((byte[]) row.get("validator_result"));
            for (CSVColumnState column : columns) {
                if (column.getImportedColumn()) {
                    newBean.put(column.getColName(), Toolkit.getValueFromBean(recipient, column.getColName()));
                }
            }
            newBean.put(NewImportWizardService.VALIDATOR_RESULT_RESERVED, validatorResult);
            newBean.put(NewImportWizardService.ERROR_EDIT_RECIPIENT_EDIT_RESERVED, recipient);
            result.add(newBean);
        }

        return new PaginatedListImpl<Map<String, Object>>(result, totalRows, rownums, page, sort, direction);
    }

    @Override
    public Set<String> loadBlackList( @VelocityCheck int companyID) throws Exception {
        final JdbcTemplate jdbcTemplate = createJdbcTemplate();
        SqlRowSet rset = null;
        Set<String> blacklist = new HashSet<String>();
        try {
        	if (AgnUtils.isOracleDB()) {
        		// ignore cust_ban_tbl so that global blacklisted addresses can be imported to local blacklist
        		rset = jdbcTemplate.queryForRowSet("SELECT email FROM cust" + companyID + "_ban_tbl");
        	} else {
        		rset = jdbcTemplate.queryForRowSet("SELECT email FROM cust_ban_tbl");
        	}
        	while (rset.next()) {
        		blacklist.add(rset.getString(1).toLowerCase());
        	}
        } catch (Exception e) {
        	logger.error( "loadBlacklist: " + e.getMessage(), e);

        	throw new Exception(e.getMessage());
        }
        return blacklist;
    }

    @Override
    public HashMap<ProfileRecipientFields, ValidatorResults> getDuplicateRecipientsFromNewDataOnly(Map<ProfileRecipientFields, ValidatorResults> listOfValidBeans, ImportProfile profile, CSVColumnState[] columns, Integer adminID, int datasource_id) {
        final HashMap<ProfileRecipientFields, ValidatorResults> result = new HashMap<ProfileRecipientFields, ValidatorResults>();
        if (listOfValidBeans.isEmpty()) {
            return result;
        }

        final String prefix = "cust_" + adminID + "_tmp_";
        final String tableName = prefix + datasource_id + "_tbl";
        final HashMap<ImportKeyColumnsKey, ProfileRecipientFields> columnKeyValueToTemporaryIdMap = new HashMap<ImportKeyColumnsKey, ProfileRecipientFields>();
        final JdbcTemplate template = getJdbcTemplateForTemporaryTable();
        List<Object> parameters = new ArrayList<Object>();
        Map<String, List<Object>> parametersMap = new HashMap<String, List<Object>>();
        String columnKeyBuffer = "(";
        for (ProfileRecipientFields profileRecipientFields : listOfValidBeans.keySet()) {
            ImportKeyColumnsKey keyValue = ImportKeyColumnsKey.createInstance(profile, profileRecipientFields, columns);
            if (columnKeyValueToTemporaryIdMap.containsKey(keyValue)) {
                result.put(profileRecipientFields, null);
                continue;
            }

            columnKeyBuffer += keyValue.getParametersString();
            keyValue.addParameters(parametersMap);

            columnKeyValueToTemporaryIdMap.put(keyValue, profileRecipientFields);
        }
        columnKeyBuffer = columnKeyBuffer.substring(0, columnKeyBuffer.length() - 1);
        columnKeyBuffer = columnKeyBuffer + ")";
        ImportKeyColumnsKey keyColumnsKey = columnKeyValueToTemporaryIdMap.keySet().iterator().next();
        Iterator<String> keyColumnIterator = keyColumnsKey.getKeyColumnsMap().keySet().iterator();
        StringBuffer sqlQuery = new StringBuffer("SELECT ");
        StringBuffer wherePart = new StringBuffer("");
        int index = 0;
        while (keyColumnIterator.hasNext()) {
            String keyColumnName = keyColumnIterator.next();
            CSVColumnState columnState = keyColumnsKey.getKeyColumnsMap().get(keyColumnName);
            String column = "i.column_duplicate_check_" + index;
            String columnAlias = ImportKeyColumnsKey.KEY_COLUMN_PREFIX + keyColumnName;
            sqlQuery.append(column + " AS " + columnAlias + ",");
            int type = columnState.getType();
            if (!AgnUtils.isOracleDB() && type == CSVColumnState.TYPE_DATE) {
                wherePart.append("DATE_FORMAT(" + column + ", '" + MYSQL_COMPARE_DATE_FORMAT + "')");
            } else {
                wherePart.append(column);
            }
            wherePart.append(" IN " + columnKeyBuffer + " AND ");
            // gather parameters
            List<Object> objectList = parametersMap.get(keyColumnName);
            if (objectList != null){
                parameters.addAll(objectList);
            }
            index++;
        }

        sqlQuery.delete(sqlQuery.length() - 1, sqlQuery.length());
        sqlQuery.append(" FROM " + tableName + " i  WHERE (");
        sqlQuery.append(wherePart);
        sqlQuery.append("(i.status_type=" +
                    NewImportWizardService.RECIPIENT_TYPE_VALID + " OR i.status_type=" + NewImportWizardService.RECIPIENT_TYPE_FIXED_BY_HAND + " OR i.status_type=" + NewImportWizardService.RECIPIENT_TYPE_DUPLICATE_RECIPIENT + "))");
        adjustDateParams(parameters);

        @SuppressWarnings("unchecked")
		List<Map<String, Object>> resultList = template.queryForList(sqlQuery.toString(), parameters.toArray());
        for (Map<String, Object> row : resultList) {
            ImportKeyColumnsKey columnsKey = ImportKeyColumnsKey.createInstance(row);
            ProfileRecipientFields recipientFields = columnKeyValueToTemporaryIdMap.get(columnsKey);
            if(recipientFields != null){
                result.put(recipientFields, null);
            }
        }
        return result;
    }

    @Override
    public void updateRecipients(final Map<ProfileRecipientFields, ValidatorResults> recipientBeans, Integer adminID, final int type, final ImportProfile profile, int datasource_id, CSVColumnState[] columns) {
        if (recipientBeans.isEmpty()) {
            return;
        }
        final JdbcTemplate template = getJdbcTemplateForTemporaryTable();
        final String prefix = "cust_" + adminID + "_tmp_";
        final String tableName = prefix + datasource_id + "_tbl";
        final ProfileRecipientFields[] recipients = recipientBeans.keySet().toArray(new ProfileRecipientFields[recipientBeans.keySet().size()]);
        String keyColumn = profile.getKeyColumn();
        List<String> keyColumns = profile.getKeyColumns();
        String duplicateSql = "";
        if (keyColumns.isEmpty()) {
            duplicateSql += " column_duplicate_check_0=? ";
        } else {
            for (int i = 0; i < keyColumns.size(); i++) {
                duplicateSql += " column_duplicate_check_" + i+"=? ";
                if (i != keyColumns.size() - 1) {
                    duplicateSql += ", ";
                }
            }
        }
        final String query = "UPDATE  " + tableName + " SET recipient=?, validator_result=?, status_type=?, " + duplicateSql + " WHERE temporary_id=?";
        final List<CSVColumnState> temporaryKeyColumns = new ArrayList<CSVColumnState>();
        for (CSVColumnState column : columns) {
            if (keyColumns.isEmpty()) {
                if (column.getColName().equals(keyColumn) && column.getImportedColumn()) {
                    temporaryKeyColumns.add(column);
                }
            } else {
                for (String columnName : keyColumns){
                    if (column.getColName().equals(columnName) && column.getImportedColumn()) {
                        temporaryKeyColumns.add(column);
                        break;
                    }
                }
            }
        }
        final BatchPreparedStatementSetter setter = new BatchPreparedStatementSetter() {

            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setBytes(1, ImportUtils.getObjectAsBytes(recipients[i]));
                ps.setBytes(2, ImportUtils.getObjectAsBytes(recipientBeans.get(recipients[i])));
                ps.setInt(3, type);
                for (int j = 0; j < temporaryKeyColumns.size(); j++) {
                    setPreparedStatmentForCurrentColumn(ps, 4 + j, temporaryKeyColumns.get(j), recipients[i], profile, recipientBeans.get(recipients[i]));
                }
                ps.setString(4 + temporaryKeyColumns.size(), recipients[i].getTemporaryId());

                if( logger.isInfoEnabled()) {
                	logger.info("Import ID: " + profile.getImportId() + " Updating recipient in temp-table: " + Toolkit.getValueFromBean(recipients[i], profile.getKeyColumn()));
                }
            }

            public int getBatchSize() {
                return recipientBeans.size();
            }
        };
        template.batchUpdate(query, setter);
    }

    @Override
    public void addNewRecipients(final Map<ProfileRecipientFields, ValidatorResults> validRecipients, Integer adminId, final ImportProfile importProfile, final CSVColumnState[] columns, final int datasourceID) throws Exception {
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
        boolean isGenderMapped = false;
        query = query + "mailtype, datasource_id, ";
        for (CSVColumnState column : columns) {
            if (column.getColName().equals("creation_date")){
                throw new Exception(" creation_date column is not allowed to be imported");
            }
            if (column.getImportedColumn() && !column.getColName().equals("mailtype")) {
                query = query + column.getColName() + ", ";
            }
            if (column.getImportedColumn() && column.getColName().equals("gender")) {
                isGenderMapped = true;
            }
        }
        if (!isGenderMapped) {
            if (AgnUtils.isOracleDB()) {
                query = query + "gender, ";
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
        if (!isGenderMapped) {
            if (AgnUtils.isOracleDB()) {
                query = query + "?, ";
            }
        }
        query = query + "?, ?, ";
        query = query.substring(0, query.length() - 2);
        query = query + ", " + currentTimestamp + ")";
        final Boolean finalIsGenderMapped = isGenderMapped;
        final BatchPreparedStatementSetter setter = new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ProfileRecipientFields profileRecipientFields = recipientsBean[i];
                Integer mailtype = Integer.valueOf(profileRecipientFields.getMailtype());

                int index = 0;
                if (AgnUtils.isOracleDB()) {
                    ps.setInt(1, newcustomerIDs[i]);
                    ps.setInt(2, mailtype);
                    ps.setInt(3, datasourceID);
                    index = 4;
                } else {
                    ps.setInt(1, mailtype);
                    ps.setInt(2, datasourceID);
                    index = 3;
                }
                for (CSVColumnState column : columns) {
                    if (column.getImportedColumn() && !column.getColName().equals("mailtype")) {
                        setPreparedStatmentForCurrentColumn(ps, index, column, profileRecipientFields, importProfile, null);
                        index++;
                    }
                }
                if (!finalIsGenderMapped) {
                	if (AgnUtils.isOracleDB()) {
                		ps.setInt(index, 2);
                	}
                }

                if( logger.isInfoEnabled()) {
                	logger.info("Import ID: " + importProfile.getImportId() + " Adding recipient to recipient-table: " + Toolkit.getValueFromBean(profileRecipientFields, importProfile.getKeyColumn()));
                }
            }

            public int getBatchSize() {
                return validRecipients.size();
            }
        };

        template.batchUpdate(query, setter);
    }

    public boolean isKeyColumnIndexed( @VelocityCheck int companyId, String keyColumn){
        final JdbcTemplate template = createJdbcTemplate();
        boolean keyColumnIndexed = false;
        if(AgnUtils.isOracleDB()) {
            String query = "select count(*) from user_ind_columns where lower(table_name) = 'customer_" + companyId + "_tbl' and lower(COLUMN_NAME) = '" + keyColumn + "'";
            int totalCount = template.queryForInt(query);
            if (totalCount > 0) {
                keyColumnIndexed = true;
            }
        } else {
            String query = "SHOW INDEX FROM customer_" + companyId + "_tbl where column_name='" + keyColumn + "'";
            @SuppressWarnings("unchecked")
			final List<Map<String, Object>> resultList = template.queryForList(query);
            if(resultList.size() > 0){
                keyColumnIndexed = true;
            }
        }
        return keyColumnIndexed;
    }

    private void setPreparedStatmentForCurrentColumn(PreparedStatement ps, int index, CSVColumnState column, ProfileRecipientFields bean, ImportProfile importProfile, ValidatorResults validatorResults) throws SQLException {
        String value = Toolkit.getValueFromBean(bean, column.getColName());
        if (column.getType() == CSVColumnState.TYPE_NUMERIC && column.getColName().equals("gender")) {
            if (StringUtils.isEmpty(value) || value == null) {
                ps.setInt(index, 2);
            } else {
                if (importProfile.getGenderMapping().keySet().contains(value)) {
                    final Integer intValue = importProfile.getGenderMapping().get(value);
                    ps.setInt(index, intValue);
                } else {
                    if (GenericValidator.isInt(value) && Integer.valueOf(value) <= NewImportWizardService.MAX_GENDER_VALUE_EXTENDED && Integer.valueOf(value) >= 0) {
                        ps.setInt(index, Integer.valueOf(value));
                    }
                }
            }

        } else if (column.getType() == CSVColumnState.TYPE_CHAR) {
            if (value == null) {
                ps.setNull(index, Types.VARCHAR);
            } else {
                String columnName = column.getColName();
                if (columnName.equals("email")) {
                    value = value.toLowerCase();
                    if (validatorResults != null && !ImportUtils.checkIsCurrentFieldValid(validatorResults, "email", "checkRange")) {
                        throw new ImportRecipientsToolongValueException(value);
                    }
                } else if (importProfile.getKeyColumns().contains(columnName) || (importProfile.getKeyColumns().isEmpty() && columnName.equals(importProfile.getKeyColumn()))) {
                    // range validation for keyColumn
                    if (validatorResults != null && !ImportUtils.checkIsCurrentFieldValid(validatorResults, columnName, "checkRange")) {
                        throw new ImportRecipientsToolongValueException(value);
                    }
                }
                if (AgnUtils.isOracleDB()){
                	ps.setString(index, value);
                } else {
					if (column.isNullable() && value.isEmpty()) {
					    ps.setNull(index, Types.VARCHAR);
					}
					else {
                    	ps.setString(index, value);
					}
                }

            }
        } else if (column.getType() == CSVColumnState.TYPE_NUMERIC) {
            if (StringUtils.isEmpty(value) || value == null) {
                ps.setNull(index, Types.NUMERIC);
            } else {
                ps.setDouble(index, Double.valueOf(value));
            }
        } else if (column.getType() == CSVColumnState.TYPE_DATE) {
            if (StringUtils.isEmpty(value) || value == null) {
                ps.setNull(index, Types.DATE);
            } else {
                Date date = ImportUtils.getDateAsString(value, importProfile.getDateFormat());

                ps.setTimestamp(index, new Timestamp(date.getTime()));
            }
        }
    }

    @Override
    public HashMap<ProfileRecipientFields, ValidatorResults> getDuplicateRecipientsFromExistData(Map<ProfileRecipientFields, ValidatorResults> listOfValidBeans, ImportProfile profile, CSVColumnState[] columns) {
        final HashMap<ProfileRecipientFields, ValidatorResults> result = new HashMap<ProfileRecipientFields, ValidatorResults>();
        if (listOfValidBeans.isEmpty()) {
            return result;
        }
        final HashMap<ImportKeyColumnsKey, ProfileRecipientFields> columnKeyValueToTemporaryIdMap = new HashMap<ImportKeyColumnsKey, ProfileRecipientFields>();
        final JdbcTemplate template = createJdbcTemplate();
        List<Object> parameters = new ArrayList<Object>();
        Map<String, List<Object>> parametersMap = new HashMap<String, List<Object>>();
        String columnKeyBuffer = "(";
        for (ProfileRecipientFields profileRecipientFields : listOfValidBeans.keySet()) {
            ImportKeyColumnsKey keyValue = ImportKeyColumnsKey.createInstance(profile, profileRecipientFields, columns);
            if (columnKeyValueToTemporaryIdMap.containsKey(keyValue)) {
                result.put(profileRecipientFields, null);
                continue;
            }

            columnKeyBuffer += keyValue.getParametersString();
            keyValue.addParameters(parametersMap);

            columnKeyValueToTemporaryIdMap.put(keyValue, profileRecipientFields);
        }
        columnKeyBuffer = columnKeyBuffer.substring(0, columnKeyBuffer.length() - 1);
        columnKeyBuffer = columnKeyBuffer + ")";

        ImportKeyColumnsKey keyColumnsKey = columnKeyValueToTemporaryIdMap.keySet().iterator().next();
        Iterator<String> keyColumnIterator = keyColumnsKey.getKeyColumnsMap().keySet().iterator();
        StringBuffer sqlQuery = new StringBuffer("SELECT customer_id, ");
        StringBuffer wherePart = new StringBuffer("");
        Map<String, Integer> columnTypes = new HashMap<String, Integer>();
        while (keyColumnIterator.hasNext()) {
            String keyColumnName = keyColumnIterator.next();
            CSVColumnState columnState = keyColumnsKey.getKeyColumnsMap().get(keyColumnName);
            String column = keyColumnName;
            String columnAlias = ImportKeyColumnsKey.KEY_COLUMN_PREFIX + keyColumnName;
            sqlQuery.append(column + " AS " + columnAlias + ",");
            int type = columnState.getType();
            if (!AgnUtils.isOracleDB() && type == CSVColumnState.TYPE_DATE) {
                wherePart.append("DATE_FORMAT(" + column + ", '" + MYSQL_COMPARE_DATE_FORMAT + "')");
            } else {
                wherePart.append(column);
            }
            wherePart.append(" IN " + columnKeyBuffer + " AND ");
            // gather parameters
            List<Object> objectList = parametersMap.get(keyColumnName);
            if (objectList != null){
                parameters.addAll(objectList);
            }
            columnTypes.put(columnAlias, type);
        }

        sqlQuery.delete(sqlQuery.length() - 1, sqlQuery.length());
        wherePart.delete(wherePart.length() - 4, wherePart.length());
        sqlQuery.append(" FROM customer_" + profile.getCompanyId() + "_tbl c WHERE (");
        sqlQuery.append(wherePart);
        sqlQuery.append(")");
        adjustDateParams(parameters);

        @SuppressWarnings("unchecked")
		final List<Map<String, Object>> resultList = template.queryForList(sqlQuery.toString(), parameters.toArray());
        for (Map<String, Object> row : resultList) {
            ImportKeyColumnsKey columnsKey = ImportKeyColumnsKey.createInstance(row);
            ProfileRecipientFields recipientFields = columnKeyValueToTemporaryIdMap.get(columnsKey);
            if(recipientFields != null){
                result.put(recipientFields, null);
                if (profile.getUpdateAllDuplicates() || (recipientFields.getUpdatedIds() == null || recipientFields.getUpdatedIds().size() == 0)) {
                    recipientFields.addUpdatedIds(((Number) row.get("customer_id")).intValue());
                }
            }
        }
        return result;
    }

    private void adjustDateParams(List<Object> parameters) {
        if (!AgnUtils.isOracleDB()) {
            for (int i = 0; i < parameters.size(); i++) {
                Object parameter = parameters.get(i);
                if (parameter instanceof Date) {
                    final SimpleDateFormat dateFormat = new SimpleDateFormat(JAVA_COMPARE_DATE_FORMAT);
                    String formattedDate = dateFormat.format((Date) parameter);
                    parameters.set(i, formattedDate);
                }
            }
        }
    }

    @Override
    public int updateExistRecipients(final Collection<ProfileRecipientFields> recipientsForUpdate, final ImportProfile importProfile, final CSVColumnState[] columns) {
        if (recipientsForUpdate.isEmpty()) {
            return 0;
        }

        final JdbcTemplate template = createJdbcTemplate();
        List<String> querys = new ArrayList<String>();
        for (ProfileRecipientFields recipientForUpdate : recipientsForUpdate) {
			String query = "UPDATE customer_" + importProfile.getCompanyId() + "_tbl SET " + AgnUtils.changeDateName() + " = CURRENT_TIMESTAMP, ";

			if (recipientForUpdate.getMailtypeDefined().equals(ImportUtils.MAIL_TYPE_DEFINED)) {
				query = query + "mailtype = " + recipientForUpdate.getMailtype() + ", ";
			}
            
            for (CSVColumnState column : columns) {
                if (column.getImportedColumn() && !column.getColName().equals("mailtype")) {
                     String value = Toolkit.getValueFromBean(recipientForUpdate, column.getColName());

                    // @todo: agn: value == null
                    if (StringUtils.isEmpty(value) && importProfile.getNullValuesAction() == NullValuesAction.OVERWRITE.getIntValue() && !column.getColName().equals("gender")) {
                        query = query + column.getColName() + " = NULL, ";
                    } else if (!StringUtils.isEmpty(value)) {
                        if (column.getColName().equals("gender")) {
                            if (StringUtils.isEmpty(value)) {
                                query = query + column.getColName() + " = 2, ";
                            } else {
                                if (importProfile.getGenderMapping().keySet().contains(value)) {
                                    final Integer intValue = importProfile.getGenderMapping().get(value);
                                    query = query + column.getColName() + " = " + intValue + ", ";
                                } else {
                                    if (GenericValidator.isInt(value)) {
                                        query = query + column.getColName() + " = " + value + ", ";
                                    }
                                }
                            }
                        } else {
                            switch (column.getType()) {
                                case CSVColumnState.TYPE_CHAR:
                                    if (column.getColName().equals("email")) {
                                        value = value.toLowerCase();
                                    }
                                    if (AgnUtils.isOracleDB()){
                                        query = query + column.getColName() + " = '" + value.replace("'","''") + "', ";
                                    } else {
                                        query = query + column.getColName() + " = '" + value.replace("\\","\\\\").replace("'","\\'") + "', ";
                                    }
                                    break;
                                case CSVColumnState.TYPE_NUMERIC:
                                    query = query + column.getColName() + " = " + value + ", ";
                                    break;
                                case CSVColumnState.TYPE_DATE:
                                    if (StringUtils.isEmpty(value) || value == null) {
                                        query = query + column.getColName() + " = null, ";
                                    } else {
	                                    final int format = importProfile.getDateFormat();
	                                    Date date = ImportUtils.getDateAsString(value, format);
	                                    if (AgnUtils.isOracleDB()) {
	                                        final String dateAsFormatedString = DB_DATE_FORMAT.format(date);
	                                        query = query + column.getColName() + " = to_date('"
	                                                + dateAsFormatedString + "', 'dd.MM.YYYY HH24:MI:SS'), ";
	                                    } else {
	                                        String temTimestamp = new Timestamp(date.getTime()).toString();
	                                        query = query + column.getColName() + " = '" + temTimestamp.substring(0, temTimestamp.indexOf(" ")) + "', ";
	                                    }
                                    }
                                    break;
                            }
                        }
                    }
                }
            }

            query = query.substring(0, query.length() - 2);
            String value = Toolkit.getValueFromBean(recipientForUpdate, importProfile.getKeyColumn());
            value = value.toLowerCase();
            if (!importProfile.getUpdateAllDuplicates()) {
                query += " WHERE customer_id = " + recipientForUpdate.getUpdatedIds().get(0);
                querys.add(query);
            } else {
                query += " WHERE customer_id IN (";
                
                List<Integer> recipientIdsForUpdate = recipientForUpdate.getUpdatedIds();
                // OracleSQL only allows 1000 list items in one single list statement like "WHERE x IN (item1, ..., item1000)", so chop the full list in smaller pieces
                List<List<Integer>> recipientIdLists = AgnUtils.chopToChunks(recipientIdsForUpdate, 1000);
                for (List<Integer> recipientIdList : recipientIdLists) {
                    String nextUpdateSql = query + StringUtils.join(recipientIdList, ',') + ")";
                    querys.add(nextUpdateSql);
                }
            }

            if( logger.isInfoEnabled()) {
            	logger.info("Import ID: " + importProfile.getImportId() + " Updating recipient in recipient-table: " + Toolkit.getValueFromBean(recipientForUpdate, importProfile.getKeyColumn()));
            }
        }
        int[] touchedRows = template.batchUpdate(querys.toArray(new String[0]));
        int touchedRowsSum = 0;
        for (int i : touchedRows) {
        	touchedRowsSum += i;
        }
        return touchedRowsSum;
    }

	@Override
	public void removeImportedRecipients(int companyId, int dataSourceId) {
		String query = "delete from customer_" + companyId + "_tbl where datasource_id = ?";
		createJdbcTemplate().update(query, new Object[] {dataSourceId});
	}

    @Override
    public void importInToBlackList(final Collection<ProfileRecipientFields> recipients, @VelocityCheck final int companyId) {
        if (recipients.isEmpty()) {
            return;
        }
        final JdbcTemplate template = createJdbcTemplate();
        final ProfileRecipientFields[] recipientsArray = recipients.toArray(new ProfileRecipientFields[recipients.size()]);
        String query;
        if (AgnUtils.isOracleDB()) {
        	query = "INSERT INTO cust" + companyId + "_ban_tbl (email) VALUES (?)";
        } else {
        query = "INSERT INTO cust_ban_tbl (company_id, email) VALUES (?,?)";
        }
        final BatchPreparedStatementSetter setter = new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
            	if (AgnUtils.isOracleDB()) {
            		ps.setString(1, recipientsArray[i].getEmail().toLowerCase());
            	} else {
            		ps.setInt(1, companyId);
            		ps.setString(2, recipientsArray[i].getEmail().toLowerCase());
            	}
            }

            public int getBatchSize() {
                return recipients.size();
            }
        };
        template.batchUpdate(query, setter);
    }

    @Override
    public void createTemporaryTable(int adminID, int datasource_id, String keyColumn, @VelocityCheck int companyId, String sessionId){
        createTemporaryTable(adminID, datasource_id, keyColumn, new ArrayList<String>(), companyId, sessionId);
    }

    @Override
    public void createTemporaryTable(int adminID, int datasource_id, String keyColumn, List<String> keyColumns, @VelocityCheck int companyId, String sessionId) {
        final DataSource dataSource = (DataSource) applicationContext.getBean("dataSource");
        try {
            if (temporaryConnection != null) {
                temporaryConnection.destroy();
                temporaryConnection = null;
            }
            SingleConnectionDataSource scds = null;
            scds = new SingleConnectionDataSource(dataSource.getConnection(), true);
            setTemporaryConnection(scds);
        } catch (SQLException e) {
            throw new DataAccessResourceFailureException("Unable to create single connection data source", e);
        }

        final JdbcTemplate template = getJdbcTemplateForTemporaryTable();
        final String prefix = "cust_" + adminID + "_tmp_";
        final String tableName = prefix + datasource_id + "_tbl";


        String indexSql = "";
        String duplicateSql = "";
        if (keyColumns.isEmpty()) {
            duplicateSql += keyColumn + " as column_duplicate_check_0 ";
            indexSql = "column_duplicate_check_0";
        } else {
            for (int i = 0; i < keyColumns.size(); i++) {
                duplicateSql += keyColumns.get(i) + " as column_duplicate_check_" + i;
                indexSql += "column_duplicate_check_" + i;
                if (i != keyColumns.size() - 1) {
                    duplicateSql += ", ";
                    indexSql += ", ";
                }
            }
        }
        duplicateSql += " from customer_" + companyId + "_tbl where 1=0)";

        if (AgnUtils.isOracleDB()) {
            // @todo: we need to decide when all those tables will be removed
            String query = "CREATE TABLE " + tableName + " as (select ";
            query += duplicateSql;
            template.execute(query);
            query = "ALTER TABLE " + tableName + " ADD (recipient blob NOT NULL, " +
                    "validator_result blob NOT NULL, " +
                    "temporary_id varchar2(128) NOT NULL, " +
                    "status_type number(3) NOT NULL)";
            template.execute(query);
            String indexquery = "create index " + tableName + "_cdc on " + tableName + " (" + indexSql + ") nologging";
            template.execute(indexquery);
            query = " INSERT INTO import_temporary_tables (SESSION_ID, TEMPORARY_TABLE_NAME) VALUES('" + sessionId + "', '" + tableName + "')";
            template.execute(query);
        } else {
            String query = "CREATE TEMPORARY TABLE IF NOT EXISTS " + tableName + " as (select ";
            query += duplicateSql;
            template.execute(query);
            query = "ALTER TABLE " + tableName + " ADD (recipient mediumblob NOT NULL, " +
                    "validator_result mediumblob NOT NULL, " +
                    "temporary_id varchar(128) NOT NULL, " +
                    "INDEX ("+indexSql+"), " +
                    "status_type int(3) NOT NULL)";
            template.execute(query);
            query = "alter table " + tableName + " collate utf8_unicode_ci";
            template.execute(query);
        }
    }

    private int changeStatusInMailingList( @VelocityCheck int companyID, List<Integer> recipientIdsForUpdate, JdbcTemplate jdbc,
                                          int mailinglistId, int newStatus, String remark, String currentTimestamp) {
        if (recipientIdsForUpdate == null || recipientIdsForUpdate.size() == 0) {
            return 0;
        }
        
        int updatedCustomers = 0;
        
        // OracleSQL only allows 1000 list items in one single list statement like "WHERE x IN (item1, ..., item1000)", so chop the full list in smaller pieces
        List<List<Integer>> recipientIdLists = AgnUtils.chopToChunks(recipientIdsForUpdate, 1000);
        for (List<Integer> recipientIdList : recipientIdLists) {
            String sql = "UPDATE customer_" + companyID + "_binding_tbl SET user_status=" + newStatus +
                    ", exit_mailing_id=0, user_remark='" + remark + "', " + AgnUtils.changeDateName() + "=" + currentTimestamp +
                    " WHERE mailinglist_id=" + mailinglistId + " AND customer_id IN (" + StringUtils.join(recipientIdList, ',') +
                    ") AND user_status=" + BindingEntry.USER_STATUS_ACTIVE;
            updatedCustomers += jdbc.update(sql);
        }
        
        return updatedCustomers;
    }

    private void createRecipientBindTemporaryTable( @VelocityCheck int companyID, int datasourceId, final List<Integer> updatedRecipients, JdbcTemplate jdbc) {
        String sql = removeBindTemporaryTable(companyID, datasourceId, jdbc);
        if (AgnUtils.isOracleDB()) {
            sql = "CREATE TABLE cust_" + companyID + "_exist1_tmp" + datasourceId + "_tbl (customer_id NUMBER(10) NOT NULL)";
        } else {
            sql = "CREATE TEMPORARY TABLE cust_" + companyID + "_exist1_tmp" + datasourceId + "_tbl (`customer_id` int(10) unsigned NOT NULL)";
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

    private String removeBindTemporaryTable( @VelocityCheck int companyID, int datasourceId, JdbcTemplate jdbc) {
        final String tablename = "cust_" + companyID + "_exist1_tmp" + datasourceId + "_tbl";
        if (AgnUtils.isOracleDB()) {
            String query = "select count(*) from user_tables where table_name = '" + tablename.toUpperCase() + "'";
            int totalRows = jdbc.queryForInt(query);

            if (totalRows != 0) {
                String sql = "DROP TABLE " + tablename;

                jdbc.execute(sql);
            }
        } else {
            String sql = "DROP TABLE IF EXISTS " + tablename;
            try {
                jdbc.execute(sql);
            } catch (Exception e) {
            	if( logger.isInfoEnabled()) {
            		logger.info("Tried to remove table that doesn't exist", e);
            	}
            }
        }
        return "";
    }

    private int getNextCustomerSequence( @VelocityCheck int companyID, JdbcTemplate template) {
        String query = " SELECT customer_" + companyID + "_tbl_seq.nextval FROM DUAL ";
        return template.queryForInt(query);
    }

    private int[] getNextCustomerSequences( @VelocityCheck int companyID, int amount) {
        int[] customerids = new int[amount];
        if (AgnUtils.isOracleDB()) {
            JdbcTemplate template = createJdbcTemplate();
            for (int i = 0; i < amount; i++) {
                customerids[i] = getNextCustomerSequence(companyID, template);
            }
        }
        return customerids;
    }

    public JdbcTemplate getJdbcTemplateForTemporaryTable() {
        return new JdbcTemplate(temporaryConnection);
    }

    @Override
    public SingleConnectionDataSource getTemporaryConnection() {
        return temporaryConnection;
    }

    @Override
    public void setTemporaryConnection(SingleConnectionDataSource temporaryConnection) {
        this.temporaryConnection = temporaryConnection;
    }
}
