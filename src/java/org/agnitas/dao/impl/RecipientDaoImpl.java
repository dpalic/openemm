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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.agnitas.beans.BindingEntry;
import org.agnitas.beans.CustomerImportStatus;
import org.agnitas.beans.ProfileField;
import org.agnitas.beans.Recipient;
import org.agnitas.beans.impl.BindingEntryImpl;
import org.agnitas.beans.impl.PaginatedListImpl;
import org.agnitas.beans.impl.RecipientImpl;
import org.agnitas.dao.BindingEntryDao;
import org.agnitas.dao.CompanyDao;
import org.agnitas.dao.RecipientDao;
import org.agnitas.dao.impl.mapper.IntegerRowMapper;
import org.agnitas.emm.core.velocity.VelocityCheck;
import org.agnitas.service.ColumnInfoService;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.CaseInsensitiveMap;
import org.agnitas.util.CaseInsensitiveSet;
import org.agnitas.util.CsvColInfo;
import org.agnitas.util.DbColumnType;
import org.agnitas.util.DbColumnType.SimpleDataType;
import org.agnitas.util.DbUtilities;
import org.agnitas.util.SafeString;
import org.agnitas.util.SqlPreparedInsertStatementManager;
import org.agnitas.util.SqlPreparedUpdateStatementManager;
import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

/**
 * @author Nicole Serek, Andreas Rehak
 */
public class RecipientDaoImpl extends BaseDaoImpl implements RecipientDao {
	
	/** The logger. */
	private static final transient Logger logger = Logger.getLogger(RecipientDaoImpl.class);

	/** DAO for accessing company data. */
	protected CompanyDao companyDao;
	
	protected ColumnInfoService columnInfoService;
	
	private BindingEntryDao bindingEntryDao;
	
	public void setCompanyDao(CompanyDao companyDao) {
		this.companyDao = companyDao;
	}
	
	public void setColumnInfoService(ColumnInfoService columnInfoService) {
		this.columnInfoService = columnInfoService;
	}
	
	@Required
	public void setBindingEntryDao(BindingEntryDao bindingEntryDao) {
		this.bindingEntryDao = bindingEntryDao;
	}

	private static Integer maxRecipientConfigValueCache = null;

	private int getMaxRecipientConfigValue() {
		if (maxRecipientConfigValueCache == null) {
			synchronized (this) {
				if (maxRecipientConfigValueCache == null) {
					maxRecipientConfigValueCache = new Integer(AgnUtils.getDefaultIntValue("recipient.maxRows"));
				}
			}
		}
		if (maxRecipientConfigValueCache == null) {
			return 0;
		}
		return maxRecipientConfigValueCache.intValue();
	}

	@Override
	public boolean mayAdd(@VelocityCheck int companyID, int count) {
		if (getMaxRecipientConfigValue() > 0) {
			String sql = "SELECT COUNT(customer_id) FROM customer_" + companyID + "_tbl";
			int current = selectInt(logger, sql);
			int max = getMaxRecipientConfigValue();
			
			if(max == 0 || current + count <= max) {
				return true;
			} else {
				logger.warn("Cannot add new recipient: Maximum limit of " + max + " recipient records reached (current: " + current + ")");
				
				return false;
			}
		} else {
			return true;
		}
	}

	@Override
	public boolean isNearLimit(@VelocityCheck int companyID, int count) {
		if (getMaxRecipientConfigValue() != 0) {
			String sql = "SELECT COUNT(customer_id) FROM customer_" + companyID + "_tbl";
			int current = selectInt(logger, sql);
			int max = (int) (getMaxRecipientConfigValue() * 0.9);
			return max > 0 && current + count > max;
		} else {
			return true;
		}
	}

	
	private class CustomerQueryProperties {
		private String insertStatementTemplate = null;
		private String updateStatementTemplate = null;
		
		private CaseInsensitiveMap<ProfileField> customerDBProfileStructure = null;
		
		CustomerQueryProperties(int companyID) throws Exception {
			insertStatementTemplate = new String("INSERT INTO customer_" + companyID + "_tbl");
			updateStatementTemplate = new String("UPDATE customer_" + companyID + "_tbl SET ");
			customerDBProfileStructure = loadCustDBProfileStructure(companyID);
		}

		String getUpdateStatementTemplate() {
			return updateStatementTemplate;
		}

		String getInsertStatementTemplate() {
			return insertStatementTemplate;
		}
		
		CaseInsensitiveMap<ProfileField> getCustomerDBProfileStructure() {
			return customerDBProfileStructure;
		}
	}
//	private Map<Integer, CustomerQueryProperties> customerQueryPropertiesByCompany = new HashMap<Integer, CustomerQueryProperties>();
	private CustomerQueryProperties getQueryProperties(int companyID) throws Exception {
//		CustomerQueryProperties queryProperties = customerQueryPropertiesByCompany.get(companyID);
//		if (queryProperties == null) {
//			queryProperties = new CustomerQueryProperties(companyID);
//			customerQueryPropertiesByCompany.put(companyID, queryProperties);
//		}
//		return queryProperties;
		return new CustomerQueryProperties(companyID);
	}
	
	private static Integer tryParseInt(String text, int defaultValue) {
		try {
			return Integer.parseInt(text);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	private static Double tryParseDouble(String text, double defaultValue) {
		try {
			return Double.parseDouble(text);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
	private static String buildCustomerTimestamp(Recipient customer, String fieldName) {
		int day = Integer.parseInt(customer.getCustParameters(fieldName + "_DAY_DATE"));
		int month = Integer.parseInt(customer.getCustParameters(fieldName + "_MONTH_DATE"));
		int year = Integer.parseInt(customer.getCustParameters(fieldName + "_YEAR_DATE"));
		int hour = tryParseInt(customer.getCustParameters(fieldName + "_HOUR_DATE"), 0);
		int minute = tryParseInt(customer.getCustParameters(fieldName + "_MINUTE_DATE"), 0);
		int second = tryParseInt(customer.getCustParameters(fieldName + "_SECOND_DATE"), 0);

		return AgnUtils.isOracleDB() ? DbUtilities.getToDateString_Oracle(day, month, year, hour, minute, second) 
				                     : DbUtilities.getToDateString_MySQL(day, month, year, hour, minute, second);
	}
	
	private static boolean hasTripleDateParameter(Recipient customer, String fieldName) {
		return customer.getCustParameters(fieldName + "_DAY_DATE") != null 
				&& customer.getCustParameters(fieldName + "_MONTH_DATE") != null
				&& customer.getCustParameters(fieldName + "_YEAR_DATE") != null;
	}
	
	private SqlPreparedInsertStatementManager prepareInsertStatement(CustomerQueryProperties queryProperties, 
			Recipient customer, boolean withEmptyParameters) throws Exception {
		
		SqlPreparedInsertStatementManager insertStatementManager 
			= new SqlPreparedInsertStatementManager(queryProperties.getInsertStatementTemplate());
		
		for (Entry<String, ProfileField> entry : queryProperties.getCustomerDBProfileStructure().entrySet()) {
			String fieldName = entry.getKey();
			ProfileField profileField = entry.getValue();
			String columnType = profileField.getDataType();
			
			if (fieldName.equalsIgnoreCase("customer_id")) {
				// customer_id is set in a special way
			} else if (fieldName.equalsIgnoreCase("creation_date") 
					|| fieldName.equalsIgnoreCase("timestamp") || fieldName.equalsIgnoreCase("change_date")) {
				// Field is a system timestamp field
				insertStatementManager.addValue(fieldName, "current_timestamp", true);
			} else if (columnType.equalsIgnoreCase("DATE")) {
				if (hasTripleDateParameter(customer, fieldName)) {
					// Customer table has a timestamp field, which is split into 3 or 6 separate fields (day, month, year) or (day, month, year, hour, minute, second)
					if (StringUtils.isNotBlank(customer.getCustParameters(fieldName + "_DAY_DATE"))) {
						insertStatementManager.addValue(fieldName, buildCustomerTimestamp(customer, fieldName), true);
					} else {
						insertStatementManager.addValue(fieldName, null);
					}
				} else {
					// Simple date field
					// Only default values are filled, other values must be set as split timestamp (fieldName + "_DAY_DATE" ...)
					String defaultValue = profileField.getDefaultValue();
					if (StringUtils.isNotBlank(defaultValue)) {
						insertStatementManager.addValue(fieldName, createDateDefaultValueExpression(defaultValue), true);
					} else {
						insertStatementManager.addValue(fieldName, null);
					}
				}
			} else {
				String value = customer.getCustParameters(fieldName);
				if (fieldName.equalsIgnoreCase("datasource_id")) {
		            logger.trace("Prepare insert. New datasourceID = " + value + " for recipient with email  " + customer.getEmail());
				}
				if (StringUtils.isEmpty(value)) {
					value = profileField.getDefaultValue();
					if (StringUtils.isBlank(value)) {
						if (withEmptyParameters) {
							//don't miss any parameter - for batch processing
							insertStatementManager.addValue(fieldName, null);
							if (fieldName.equalsIgnoreCase("datasource_id")) {
					            logger.trace("Prepare insert. Adding empty datasourceID for recipient with email  " + customer.getEmail());
							}
						}
						continue;
					}
				} 
				if (columnType.equalsIgnoreCase("INTEGER")) {
					insertStatementManager.addValue(fieldName, tryParseInt(value.trim(), 0));
					if (fieldName.equalsIgnoreCase("datasource_id")) {
			            logger.trace("Prepare insert. Adding INTEGER datasourceID for recipient with email  " + customer.getEmail());
					}
				} else if (columnType.equalsIgnoreCase("DOUBLE")) {
					insertStatementManager.addValue(fieldName, tryParseDouble(value, 0));
				} else { // if (columnType.equalsIgnoreCase("VARCHAR") || columnType.equalsIgnoreCase("CHAR")) {
					insertStatementManager.addValue(fieldName, value);
				}
			}
		}
		
		return insertStatementManager;
	}
	
	private SqlPreparedUpdateStatementManager prepareUpdateStatement(CustomerQueryProperties queryProperties, 
			Recipient customer, boolean withEmptyParameters) throws Exception {
		
		SqlPreparedUpdateStatementManager updateStatementManager 
			= new SqlPreparedUpdateStatementManager(queryProperties.getUpdateStatementTemplate());

		updateStatementManager.addValue(AgnUtils.changeDateName(), "CURRENT_TIMESTAMP", true);

		for (Entry<String, ProfileField> entry : queryProperties.getCustomerDBProfileStructure().entrySet()) {
			String fieldName = entry.getKey();
			ProfileField profileField = entry.getValue();
			String columnType = profileField.getDataType();
			
			if (fieldName.equalsIgnoreCase("customer_id") || fieldName.equalsIgnoreCase("change_date") || fieldName.equalsIgnoreCase("timestamp")
					|| fieldName.equalsIgnoreCase("creation_date")) {
				// Do not update these special fields
			} else if (fieldName.equalsIgnoreCase("datasource_id")) {
				// Only update datasource_id if it is not set yet
				if (customer.getCustParameters("DATASOURCE_ID") != null) {
					int datasourceID = selectIntWithDefaultValue(logger, "SELECT datasource_id FROM customer_" + customer.getCompanyID() + "_tbl WHERE customer_id = ?", -1, customer.getCustomerID());
		            logger.trace("Prepare update. Existing datasourceID = " + datasourceID + " for recipient with email  " + customer.getEmail());
					if (datasourceID <= 0) {
						String value = customer.getCustParameters("DATASOURCE_ID");
			            logger.trace("Prepare update. New datasourceID = " + value + " for recipient with email  " + customer.getEmail());
						if (StringUtils.isNotEmpty(value) && AgnUtils.isNumber(value)) {
				            logger.trace("Prepare update. Adding for recipient with email  " + customer.getEmail());
							updateStatementManager.addValue(fieldName, Integer.parseInt(value));
						}
					}
				}
			} else if (columnType.equalsIgnoreCase("DATE")) {
				if (hasTripleDateParameter(customer, fieldName)) {
					// Customer table has a timestamp field, which is split into 3 or 6 separate fields (day, month, year) or (day, month, year, hour, minute, second)
					if (StringUtils.isNotBlank(customer.getCustParameters(fieldName + "_DAY_DATE"))) {
						updateStatementManager.addValue(fieldName, buildCustomerTimestamp(customer, fieldName), true);
					} else {
						updateStatementManager.addValue(fieldName, null);
					}
				} else {
					updateStatementManager.addValue(fieldName, null);
				}
			} else {
				String value = customer.getCustParameters(fieldName);
				if (StringUtils.isEmpty(value) && !withEmptyParameters) {
					continue;
				} 
				if (columnType.equalsIgnoreCase("INTEGER")) {
					updateStatementManager.addValue(fieldName, StringUtils.isEmpty(value) ? null : tryParseInt(value, 0));
				} else if (columnType.equalsIgnoreCase("DOUBLE")) {
					updateStatementManager.addValue(fieldName, StringUtils.isEmpty(value) ? null : tryParseDouble(value, 0));
				} else { // if (columnType.equalsIgnoreCase("VARCHAR") || columnType.equalsIgnoreCase("CHAR")) {
					updateStatementManager.addValue(fieldName, StringUtils.isEmpty(value) ? null : value);
				}
			}
		}

		updateStatementManager.addWhereClause("customer_id = ?", customer.getCustomerID());
		
		return updateStatementManager;
	}
	
	private int getFirstCustomerID(String keyField, String keyValue, int companyID, JdbcTemplate jdbcTmpl) {
		@SuppressWarnings("unchecked")
		List<Object> list = jdbcTmpl.query("SELECT customer_id from customer_" 
			+ companyID + "_tbl where " + keyField + " = '" + keyValue + "'",
			new RowMapper() {
				@Override
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					return rs.getInt(1);
				}});
		return (!list.isEmpty()) ? (Integer) list.get(0) : 0;
	}
	
	private int addInsertStatement(Recipient customer, Statement stm, List<Object> results) throws Exception {
		int toGenerateKeys = 0;
		int companyID = customer.getCompanyID();
		CustomerQueryProperties queryProperties = getQueryProperties(companyID);
		SqlPreparedInsertStatementManager insertStatementManager 
			= prepareInsertStatement(queryProperties, customer, false);
		if (AgnUtils.isOracleDB()) {
			int customerID = selectInt(logger, "SELECT customer_" + companyID + "_tbl_seq.NEXTVAL FROM DUAL");
			insertStatementManager.addValue("customer_id", customerID);
			results.add(customerID);
		} else {
			results.add(0);
			toGenerateKeys = 1;
		}
		String query = insertStatementManager.getReadySqlString();
		stm.addBatch(query);
		return toGenerateKeys;
	}
	
	public List<Object> insertCustomers(List<Recipient> recipients, 
			List<Boolean> doubleCheck, List<Boolean> overwrite, List<String> keyFields) {
//		long startTime = System.nanoTime();
		
		List<Object> results = new ArrayList<Object>();
		
		Connection con = null;
		Statement stm;
		JdbcTemplate jdbcTmpl = new JdbcTemplate(getDataSource());
		try {
			con = DataSourceUtils.getConnection(getDataSource());
			stm = con.createStatement();

			int toGenerateKeys = 0;
			boolean emptyStatement = true;
			for (int i = 0; i < recipients.size(); i++) {
				Recipient customer = recipients.get(i);
				int companyID = customer.getCompanyID();
				
				if (!doubleCheck.get(i)) {
					toGenerateKeys += addInsertStatement(recipients.get(i), stm, results);
					emptyStatement = false;
				} else {
					String keyField = keyFields.get(i);
					String keyValue = (String) customer.getCustParameters().get(keyField);
					if (StringUtils.isEmpty(keyValue)) {
						toGenerateKeys += addInsertStatement(recipients.get(i), stm, results);
						emptyStatement = false;
						continue;
					}
					
					int customerID = getFirstCustomerID(keyField, keyValue, companyID, jdbcTmpl);
					if (customerID == 0) {
						toGenerateKeys += addInsertStatement(recipients.get(i), stm, results);
						emptyStatement = false;
						continue;
					} 
					
					if (overwrite.get(i)) {
						CustomerQueryProperties queryProperties = getQueryProperties(companyID);
						SqlPreparedInsertStatementManager insertStatementManager 
							= prepareInsertStatement(queryProperties, customer, false);
						String query = insertStatementManager.getReadyUpdateString(queryProperties.getUpdateStatementTemplate(), customerID);
						stm.addBatch(query);
						emptyStatement = false;
					}
					results.add(customerID);
				}
			}
			
//			long estimatedTime = System.nanoTime() - startTime;
//			logger.warn("insertCustomers: prepared chunck parameters  " + estimatedTime + "ns");

			logger.debug("insertCustomers: number of Generated Keys " + toGenerateKeys);
			if (!emptyStatement) {
				stm.executeBatch();
				if (toGenerateKeys > 0) {
					ResultSet keyRS = stm.getGeneratedKeys();
					// Fill in generated IDs instead of 0 (really inserted and not Oracle)
					for (int i = 0; i < results.size(); i++) {
						int res = (Integer) results.get(i);
						if (res == 0) {
							if (keyRS.next()) {
								results.set(i, keyRS.getInt(1));
							} else {
								logger.error("insertCustomers: missed Generated Key!");
							}
						}
					}
				}
			}
			
			stm.close();
			
//			estimatedTime = System.nanoTime() - startTime;
//			logger.warn("insertCustomers: inserted chunck bulk  " + estimatedTime + "ns");
		} catch (Exception e) {
			logger.error("insertCustomers: Exception in getGeneratedKeys", e);
			return Collections.emptyList();
		} finally {
			if (con != null) {
				DataSourceUtils.releaseConnection(con, getDataSource());
			}
		}
		
		return results;
	}

	
	public List<Object> insertCustomers(List<Recipient> recipients) {
		return AgnUtils.isOracleDB() ? insertCustomersPreparedStm(recipients) : insertCustomersMultiTuple(recipients);
	}

	private List<Object> insertCustomersMultiTuple(List<Recipient> recipients) {
//		long startTime = System.nanoTime();
		if (recipients.isEmpty()) {
			logger.error("insertCustomers: nothing to do");
			return Collections.emptyList();
		}
		
		List<Object> results = new ArrayList<Object>();
		int companyID = recipients.get(0).getCompanyID();
		CustomerQueryProperties queryProperties = null;
		SqlPreparedInsertStatementManager insertStatementManager = null;
		try {
			queryProperties = getQueryProperties(companyID);
			insertStatementManager = prepareInsertStatement(queryProperties, recipients.get(0), true);
		} catch (Exception e) {
			logger.error("insertCustomers: Exception in getQueryProperties or prepareInsertStatement", e);
			return Collections.emptyList();
		}
		boolean isOracle = AgnUtils.isOracleDB();
		if (isOracle) {
			insertStatementManager.addValue("customer_id", 0);
		}
		StringBuilder queryStringBuilder = new StringBuilder(insertStatementManager.getPreparedInsertHead());
		queryStringBuilder.append(isOracle ? "" : " VALUES ");
		
		boolean firstIter = true;
		for (Recipient customer : recipients) {
			try {
				if (!firstIter) {
					queryStringBuilder.append(isOracle ? " UNION ALL " : ",");
				}
				insertStatementManager = prepareInsertStatement(queryProperties, customer, true);
				if (isOracle) {
					int customerID = selectInt(logger, "SELECT customer_" + companyID + "_tbl_seq.NEXTVAL FROM DUAL");
					insertStatementManager.addValue("customer_id", customerID);
					results.add(customerID);
					queryStringBuilder.append(" select ");
				} else {
					queryStringBuilder.append("(");
				}
				queryStringBuilder.append(insertStatementManager.getPreparedParameters());
				queryStringBuilder.append(isOracle ? " FROM DUAL  " : ")");
				firstIter = false;
			} catch (Exception e) {
				logger.error("insertCustomers: Exception in new CustomerQueryProperties or prepareInsertStatement", e);
				return Collections.emptyList();
			}
		}

//		long estimatedTime = System.nanoTime() - startTime;
//		logger.warn("insertCustomers: prepared parameters  " + estimatedTime + "ns");
        		
		if (AgnUtils.isOracleDB()) {
			JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());
			jdbcTemplate.update(queryStringBuilder.toString());
		} else {
			JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());
			
			KeyHolder keyHolder = new GeneratedKeyHolder();
			final String query = queryStringBuilder.toString();
			jdbcTemplate.update(
			    new PreparedStatementCreator() {
			        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
			            PreparedStatement ps =
			                connection.prepareStatement(query, new String[] {"customer_id"});
			            return ps;
			        }
			    },
			    keyHolder);
			
			List<?> keys = keyHolder.getKeyList();
			for (Object key : keys) {
				@SuppressWarnings("unchecked")
				Map<Object, Object> keyMap = (Map<Object, Object>)key;
				Entry<Object, Object> entry = keyMap.entrySet().iterator().next();
				Long id = (Long) entry.getValue();
				results.add(id.intValue());
			}
		}

//		estimatedTime = System.nanoTime() - startTime;
//		logger.warn("insertCustomers: inserted bulk  " + estimatedTime + "ns");
		
		return results;
	}
	
	private List<Object> insertCustomersPreparedStm(List<Recipient> recipients) {
//		long startTime = System.nanoTime();
		if (recipients.isEmpty()) {
			logger.error("insertCustomers: nothing to do");
			return Collections.emptyList();
		}
		
		List<Object> results = new ArrayList<Object>();
		List<Object[]> parametersValue = new ArrayList<Object[]>();
		String queryString = null;
		int companyID = -1;
		for (Recipient customer : recipients) {
			companyID = customer.getCompanyID();
			try {
				CustomerQueryProperties queryProperties = getQueryProperties(companyID);
				SqlPreparedInsertStatementManager insertStatementManager 
					= prepareInsertStatement(queryProperties, customer, true);
				if (AgnUtils.isOracleDB()) {
					int customerID = selectInt(logger, "SELECT customer_" + companyID + "_tbl_seq.NEXTVAL FROM DUAL");
					insertStatementManager.addValue("customer_id", customerID);
					results.add(customerID);
				} 
				// Take sql statement and field names once
				if (queryString == null) {
					queryString = insertStatementManager.getPreparedSqlString();
				}
				parametersValue.add(insertStatementManager.getPreparedSqlParameters());
			} catch (Exception e) {
				logger.error("insertCustomers: Exception in new CustomerQueryProperties or prepareInsertStatement", e);
				return Collections.emptyList();
			}
		}

//		long estimatedTime = System.nanoTime() - startTime;
//		logger.warn("insertCustomers: prepared parameters  " + estimatedTime + "ns");
        		
		if (AgnUtils.isOracleDB()) {
			SimpleJdbcTemplate simpleJdbcTemplate = new SimpleJdbcTemplate(getDataSource());
	        simpleJdbcTemplate.batchUpdate(queryString, parametersValue);
		} else {
			Connection con = null;
			PreparedStatement ps = null;
	        try {
				con = DataSourceUtils.getConnection(getDataSource());
				ps = con.prepareStatement(queryString, new String[]{"customer_id"});
				for (Object[] objects : parametersValue) {
					for (int i = 0; i < objects.length; i++) {
						ps.setObject(i + 1, objects[i]);
					}
					ps.addBatch();
				}
				ps.executeBatch();
				ResultSet keyRS = ps.getGeneratedKeys();
				while (keyRS.next()) {
					results.add(keyRS.getInt(1));
				}   
			} catch (SQLException e) {
				logger.error("insertCustomers: Exception in PrepareStatement", e);
				return Collections.emptyList();
			} finally {
				if (ps != null) {
					try {
						ps.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if (con != null) {
					DataSourceUtils.releaseConnection(con, getDataSource());
				}
			}
		}

//		estimatedTime = System.nanoTime() - startTime;
//		logger.warn("insertCustomers: inserted bulk  " + estimatedTime + "ns");
		
		return results;
	}
	
	public List<Object> updateCustomers(List<Recipient> recipients) {
//		long startTime = System.nanoTime();
		
		List<Object> results = new ArrayList<Object>();
		
		Connection con = null;
		Statement stm;
		try {
			con = DataSourceUtils.getConnection(getDataSource());
			stm = con.createStatement();

			int toGenerateKeys = 0;
			for (int i = 0; i < recipients.size(); i++) {
				Recipient customer = recipients.get(i);
				int companyID = customer.getCompanyID();
				int customerID = customer.getCustomerID();

				if (customerID == 0) {
					toGenerateKeys += addInsertStatement(customer, stm, results);
				} else {
					CustomerQueryProperties queryProperties = getQueryProperties(companyID);
					SqlPreparedUpdateStatementManager updateStatementManager 
						= prepareUpdateStatement(queryProperties, customer, false);
					String query = updateStatementManager.getReadyUpdateString(customerID);
					stm.addBatch(query);
					results.add(customerID);
				}
			}
			
//			long estimatedTime = System.nanoTime() - startTime;
//			logger.warn("updateCustomers: prepared chunck parameters  " + estimatedTime + "ns");

			logger.debug("updateCustomers: number of Generated Keys " + toGenerateKeys);
			stm.executeBatch();
			if (toGenerateKeys > 0) {
				ResultSet keyRS = stm.getGeneratedKeys();
				// Fill in generated IDs instead of 0 (really inserted and not Oracle)
				for (int i = 0; i < results.size(); i++) {
					int res = (Integer) results.get(i);
					if (res == 0) {
						if (keyRS.next()) {
							results.set(i, keyRS.getInt(1));
						} else {
							logger.error("updateCustomers: missed Generated Key!");
						}
					}
				}
			}
			
			stm.close();
			
//			estimatedTime = System.nanoTime() - startTime;
//			logger.warn("updateCustomers: inserted chunck bulk  " + estimatedTime + "ns");
		} catch (Exception e) {
			logger.error("insertCustomers: Exception in getGeneratedKeys", e);
			return Collections.emptyList();
		} finally {
			if (con != null) {
				DataSourceUtils.releaseConnection(con, getDataSource());
			}
		}
		
		
		List<Object> booleanResults = new ArrayList<Object>();
		for (Object res : results) {
			booleanResults.add( (Integer)res > 0);
		}
		
		return booleanResults;
	}
	
	public void checkParameters(org.apache.commons.collections.map.CaseInsensitiveMap custParameters, int companyID) {
		CaseInsensitiveMap<ProfileField> profile = null;
		try {
			profile = loadCustDBProfileStructure(companyID);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 for (Object param : custParameters.keySet()) {
			 String paramName = new String( (String)param );
			 for(String suffix : Arrays.asList("_second_date", "_minute_date", "_hour_date", "_day_date", "_month_date", "_year_date")) {
				 int idx = paramName.indexOf(suffix);
				 if (idx >= 0) {
					 paramName = paramName.substring(0, idx);
				 }
			 }
			if (!profile.keySet().contains(paramName)) {
				throw new IllegalArgumentException("the field " + paramName + " does not exist");
			}
			ProfileField field = profile.get(paramName);
			if (field.getDataType().equalsIgnoreCase("INTEGER")) {
				String value = (String) custParameters.get(paramName);
				try {
					Integer.parseInt(value);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("parameter " + paramName + " value type is wrong " + value);
				}
			}
		}
	}
	
	@Override
	public int insertNewCust(Recipient customer) {
		int companyID = customer.getCompanyID();
		if (companyID == 0) { // TODO: what is this for?
			return 0;
		}
		
		SqlPreparedInsertStatementManager insertStatementManager;
		try {
			insertStatementManager = prepareInsertStatement(getQueryProperties(companyID), customer, false);
		} catch (Exception e) {
			logger.error("Exception in loadCustDBProfileStructure or new SqlPreparedInsertStatementManager", e);
			return 0;
		}

		int customerID = 0;
		// Execute insert
		if (AgnUtils.isOracleDB()) {
			// Set customerID for Oracle only, MySql gets it as an statement return value
			customerID = selectInt(logger, "SELECT customer_" + companyID + "_tbl_seq.NEXTVAL FROM DUAL");
			insertStatementManager.addValue("customer_id", customerID);
			update(logger, insertStatementManager.getPreparedSqlString(), insertStatementManager.getPreparedSqlParameters());
		} else {
			Object[] sqlParameters = insertStatementManager.getPreparedSqlParameters();
			SqlUpdate sqlUpdate = new SqlUpdate(getDataSource(), insertStatementManager.getPreparedSqlString(),
					                                        DbUtilities.getSqlTypes(sqlParameters));
			sqlUpdate.setReturnGeneratedKeys(true);
			sqlUpdate.setGeneratedKeysColumnNames(new String[] { "customer_id" });
			sqlUpdate.compile();
			GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
			sqlUpdate.update(sqlParameters, keyHolder);
			customerID = keyHolder.getKey().intValue();
		}

		if (logger.isDebugEnabled()) {
			logger.debug("new customerID: " + customerID);
		}

		customer.setCustomerID(customerID);
		return customer.getCustomerID();
	}
	
	/**
	 * Updates Customer in DB. customerID must be set to a valid id, customer-data is taken from this.customerData
	 * 
	 * @return true on success
	 */
	@Override
	public boolean updateInDB(Recipient customer) {
		if (customer.getCustomerID() == 0) {
			if (logger.isInfoEnabled()) {
				logger.info("updateInDB: creating new customer");
			}
			return insertNewCust(customer) > 0;
		} 
		if (!customer.isChangeFlag()) {
			if (logger.isInfoEnabled()) {
				logger.info("updateInDB: nothing changed");
			}
			return true;
		} 
		
		SqlPreparedUpdateStatementManager updateStatementManager;
		try {
			updateStatementManager = prepareUpdateStatement(getQueryProperties(customer.getCompanyID()), customer, true);
			// Execute update
			update(logger, updateStatementManager.getPreparedSqlString(), updateStatementManager.getPreparedSqlParameters());
		} catch (Exception e) {
			logger.error("Exception in prepareUpdateStatement or new getQueryProperties", e);
			return false;
		}

		return true;
	}

	/**
	 * Find Subscriber by providing a column-name and a value and an customer object. Fills the customer_id of this customer object if found. Only exact matches possible.
	 * 
	 * @return customerID or 0 if no matching record found
	 * @param col
	 *            Column-Name
	 * @param value
	 *            Value to search for in col
	 */
	@Override
	public int findByKeyColumn(Recipient customer, String keyColumn, String value) {
		try {
			CaseInsensitiveMap<ProfileField> customerDBProfileStructure = loadCustDBProfileStructure(customer.getCompanyID());
			String keyColumnType = customerDBProfileStructure.get(keyColumn).getDataType();
			if (keyColumnType != null) {
				List<Map<String, Object>> custList;
				if (keyColumnType.equalsIgnoreCase("VARCHAR") || keyColumnType.equalsIgnoreCase("CHAR")) {
					if ("email".equalsIgnoreCase(keyColumn)) {
						value = AgnUtils.normalizeEmail(value);
					}
					custList = select(logger, "SELECT customer_id FROM customer_" + customer.getCompanyID() + "_tbl cust WHERE cust." + SafeString.getSafeDbColumnName(keyColumn) + " = ?", value);
				} else {
					int intValue = 0;
					if (AgnUtils.isNumber(value)) {
						intValue = Integer.parseInt(value);
					}

					custList = select(logger, "SELECT customer_id FROM customer_" + customer.getCompanyID() + "_tbl cust WHERE cust." + SafeString.getSafeDbColumnName(keyColumn) + " = ?", intValue);
				}

				// cannot use queryForInt, because of possible existing duplicates
				if (custList != null && custList.size() > 0) {
					customer.setCustomerID(((Number) custList.get(0).get("customer_id")).intValue());
				} else {
					customer.setCustomerID(0);
				}
			}
		} catch (Exception e) {
			customer.setCustomerID(0);
		}
		return customer.getCustomerID();
	}

	@Override
	public int findByColumn(@VelocityCheck int companyID, String keyColumn, String value) {
		Recipient customer = new RecipientImpl();
		customer.setCompanyID(companyID);
		return findByKeyColumn(customer, keyColumn, value);
	}

	/**
	 * Find Subscriber by providing a username and password. Only exact machtes possible.
	 * 
	 * @return customerID or 0 if no matching record found
	 * @param userCol
	 *            Column-Name for Username
	 * @param userValue
	 *            Value for Username
	 * @param passCol
	 *            Column-Name for Password
	 * @param passValue
	 *            Value for Password
	 */
	@Override
	public int findByUserPassword(@VelocityCheck int companyID, String keyColumn, String keyColumnValue, String passwordColumn, String passwordColumnValue) {
		if (keyColumn.toLowerCase().equals("email")) {
			keyColumnValue = keyColumnValue.toLowerCase();
		}

		String sql = "SELECT customer_id FROM customer_" + companyID + "_tbl cust WHERE cust." + SafeString.getSafeDbColumnName(keyColumn) + " = ? AND cust." + SafeString.getSafeDbColumnName(passwordColumn) + " = ?";

		try {
			return selectInt(logger, sql, keyColumnValue, passwordColumnValue);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * Load complete Subscriber-Data from DB. customerID must be set first for this method.
	 * 
	 * @return Map with Key/Value-Pairs of customer data
	 */
	@Override
	public CaseInsensitiveMap<Object> getCustomerDataFromDb(@VelocityCheck int companyID, int customerID) {
		Recipient customer = new RecipientImpl();
		customer.setCompanyID(companyID);

		if (customer.getCustParameters() == null) {
			customer.setCustParameters(new CaseInsensitiveMap<Object>());
		}

		String sql = "SELECT * FROM customer_" + companyID + "_tbl WHERE customer_id = ?";

		try {
			List<Map<String, Object>> result = select(logger, sql, customerID);

			if (result.size() > 0) {
				CaseInsensitiveMap<ProfileField> customerDBProfileStructure = loadCustDBProfileStructure(customer.getCompanyID());
				Map<String, Object> row = result.get(0);
				for (String columnName : customerDBProfileStructure.keySet()) {
					String columnType = customerDBProfileStructure.get(columnName).getDataType();
					Object value = row.get(columnName);
					if ("DATE".equalsIgnoreCase(columnType)) {
						if (value == null) {
							customer.getCustParameters().put(columnName + "_DAY_DATE", "");
							customer.getCustParameters().put(columnName + "_MONTH_DATE", "");
							customer.getCustParameters().put(columnName + "_YEAR_DATE", "");
							customer.getCustParameters().put(columnName + "_HOUR_DATE", "");
							customer.getCustParameters().put(columnName + "_MINUTE_DATE", "");
							customer.getCustParameters().put(columnName + "_SECOND_DATE", "");
							customer.getCustParameters().put(columnName, "");
						} else {
							GregorianCalendar calendar = new GregorianCalendar();
							calendar.setTime((Date) value);
							customer.getCustParameters().put(columnName + "_DAY_DATE", Integer.toString(calendar.get(GregorianCalendar.DAY_OF_MONTH)));
							customer.getCustParameters().put(columnName + "_MONTH_DATE", Integer.toString(calendar.get(GregorianCalendar.MONTH) + 1));
							customer.getCustParameters().put(columnName + "_YEAR_DATE", Integer.toString(calendar.get(GregorianCalendar.YEAR)));
							customer.getCustParameters().put(columnName + "_HOUR_DATE", Integer.toString(calendar.get(GregorianCalendar.HOUR_OF_DAY)));
							customer.getCustParameters().put(columnName + "_MINUTE_DATE", Integer.toString(calendar.get(GregorianCalendar.MINUTE)));
							customer.getCustParameters().put(columnName + "_SECOND_DATE", Integer.toString(calendar.get(GregorianCalendar.SECOND)));
							SimpleDateFormat bdfmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							customer.getCustParameters().put(columnName, bdfmt.format(calendar.getTime()));
						}
					} else {
						if (value == null) {
							value = "";
						}
						customer.getCustParameters().put(columnName, value.toString());
					}
				}
			}
		} catch (Exception e) {
			logger.error("getCustomerDataFromDb: " + sql, e);
		}

		customer.setChangeFlag(false);

		Map<String, Object> result = customer.getCustParameters();
		if (result instanceof CaseInsensitiveMap) {
			return (CaseInsensitiveMap<Object>) result;
		} else {
			return new CaseInsensitiveMap<Object>(result);
		}
	}

	/**
	 * Delete complete Subscriber-Data from DB. customerID must be set first for this method.
	 */
	@Override
	public void deleteCustomerDataFromDb(@VelocityCheck int companyID, int customerID) {
		try {
			update(logger, "DELETE FROM customer_" + companyID + "_binding_tbl WHERE customer_id = ?", customerID);
			update(logger, "DELETE FROM customer_" + companyID + "_tbl WHERE customer_id = ?", customerID);
		} catch (Exception e) {
			// do nothing, logging is already done
		}
	}

	/**
	 * Loads complete Mailinglist-Binding-Information for given customer-id from Database
	 * 
	 * @return Map with key/value-pairs as combinations of mailinglist-id and BindingEntry-Objects
	 */
	@Override
	public Map<Integer, Map<Integer, BindingEntry>> loadAllListBindings(@VelocityCheck int companyID, int customerID) {
		Recipient cust = new RecipientImpl();
		cust.setListBindings(new Hashtable<Integer, Map<Integer, BindingEntry>>()); // MailingList_ID as keys

		Map<Integer, BindingEntry> mTable = new Hashtable<Integer, BindingEntry>(); // Media_ID as key, contains rest of data (user type, status etc.)

		String sqlGetLists = null;
		BindingEntry aEntry = null;
		int tmpMLID = 0;

		try {
			sqlGetLists = "SELECT mailinglist_id, user_type, user_status, user_remark, " + AgnUtils.changeDateName() + ", mediatype FROM customer_" + companyID + "_binding_tbl WHERE customer_id=" + customerID + " ORDER BY mailinglist_id, mediatype";
			List<Map<String, Object>> list = select(logger, sqlGetLists);
			Iterator<Map<String, Object>> i = list.iterator();

			while (i.hasNext()) {
				Map<String, Object> map = i.next();
				int listID = ((Number) map.get("mailinglist_id")).intValue();
				Integer mediaType = new Integer(((Number) map.get("mediatype")).intValue());

				aEntry = new BindingEntryImpl();
				aEntry.setBindingEntryDao(bindingEntryDao);
				
				aEntry.setCustomerID(customerID);
				aEntry.setMailinglistID(listID);
				aEntry.setUserType((String) map.get("user_type"));
				aEntry.setUserStatus(((Number) map.get("user_status")).intValue());
				aEntry.setUserRemark((String) map.get("user_remark"));
				aEntry.setChangeDate((java.sql.Timestamp) map.get(AgnUtils.changeDateName()));
				aEntry.setMediaType(mediaType.intValue());

				if (tmpMLID != listID) {
					if (tmpMLID != 0) {
						cust.getListBindings().put(tmpMLID, mTable);
						mTable = new Hashtable<Integer, BindingEntry>();
						mTable.put(mediaType, aEntry);
						tmpMLID = listID;
					} else {
						mTable.put(mediaType, aEntry);
						tmpMLID = listID;
					}
				} else {
					mTable.put(mediaType, aEntry);
				}
			}
			cust.getListBindings().put(tmpMLID, mTable);
		} catch (Exception e) {
			logger.error("loadAllListBindings: " + sqlGetLists, e);
			AgnUtils.sendExceptionMail("sql:" + sqlGetLists, e);
			return null;
		}
		return cust.getListBindings();
	}

	/**
	 * Checks if E-Mail-Adress given in customerData-HashMap is registered in blacklist(s)
	 * 
	 * @return true if E-Mail-Adress is blacklisted
	 */
	@Override
	public boolean blacklistCheck(String email, @VelocityCheck int companyID) {
		boolean returnValue = false;

		// First, check global blacklist
		try {
			int count = selectInt(logger, "SELECT count(email) FROM cust_ban_tbl WHERE ? LIKE email", email);

			if (count > 0)
				returnValue = true;
		} catch (Exception e) {
			logger.error("Error checking blacklist for email '" + email + "'", e);

			// For safety, assume email is blacklisted in case of an error
			return true;
		}

		if (AgnUtils.isProjectEMM()) {
			// Then, check company blacklist
			try {
				int count = selectInt(logger, "SELECT count(email) FROM cust" + companyID + "_ban_tbl WHERE ? LIKE email", email);

				if (count > 0)
					returnValue = true;
			} catch (Exception e) {
				logger.error("Error checking blacklist for email '" + email + "'", e);

				// For safety, assume email is blacklisted in case of an error
				return true;
			}
		}

		return returnValue;
	}

	/**
	 * Extract an int parameter from CustParameters
	 * 
	 * @return the int value or the default value in case of an exception
	 * 
	 * @param column
	 *            Column-Name
	 * 
	 * @param defaultValue
	 *            Value to be returned in case of exception
	 * 
	 *            TODO: Method not used. Remove it, when nobody misses it (Support team?) private int extractInt(String column, int defaultValue, Recipient cust) { try { return
	 *            Integer.parseInt(cust.getCustParameters(column)); } catch (Exception e1) { return defaultValue; } }
	 */
	@Override
	public String getField(String selectVal, int recipientID, @VelocityCheck int companyID) {
		String sql = "SELECT " + selectVal + " value FROM customer_" + companyID + "_tbl cust WHERE cust.customer_id = ?";

		try {
			List<Map<String, Object>> list = select(logger, sql, new Object[] { new Integer(recipientID) });

			if (list.size() > 0) {
				Map<String, Object> map = list.get(0);
				Object temp = map.get("value");
				if (temp != null) {
					return temp.toString();
				}
			}
		} catch (Exception e) {
			logger.error("processTag: " + sql, e);
			AgnUtils.sendExceptionMail("sql:" + sql, e);
			return null;
		}
		return "";
	}

	@Override
	public Map<Integer, Map<Integer, BindingEntry>> getAllMailingLists(int customerID, @VelocityCheck int companyID) {
		Map<Integer, Map<Integer, BindingEntry>> result = new HashMap<Integer, Map<Integer, BindingEntry>>();
		String sql = "SELECT mailinglist_id, user_type, user_status, user_remark, " + AgnUtils.changeDateName() + ", mediatype FROM customer_" + companyID + "_binding_tbl WHERE customer_id = ? ORDER BY mailinglist_id, mediatype";

		if (logger.isInfoEnabled()) {
			logger.info("getAllMailingLists: " + sql);
		}

		try {
			List<Map<String, Object>> list = select(logger, sql, new Object[] { new Integer(customerID) });
			Iterator<Map<String, Object>> i = list.iterator();
			BindingEntry entry = null;

			while (i.hasNext()) {
				Map<String, Object> map = i.next();
				int listID = ((Number) map.get("mailinglist_id")).intValue();
				int mediaType = ((Number) map.get("mediatype")).intValue();
				Map<Integer, BindingEntry> sub = result.get(new Integer(listID));

				if (sub == null) {
					sub = new HashMap<Integer, BindingEntry>();
				}
				entry = new BindingEntryImpl();
				entry.setBindingEntryDao(bindingEntryDao);
				
				entry.setCustomerID(customerID);
				entry.setMailinglistID(listID);
				entry.setUserType((String) map.get("user_type"));
				entry.setUserStatus(((Number) map.get("user_status")).intValue());
				entry.setUserRemark((String) map.get("user_remark"));
				entry.setChangeDate((java.sql.Timestamp) map.get(AgnUtils.changeDateName()));
				entry.setMediaType(mediaType);
				sub.put(new Integer(mediaType), entry);
				result.put(new Integer(listID), sub);
			}
		} catch (Exception e) {
			logger.error("getAllMailingLists (customer ID: " + customerID + "sql: " + sql + ")", e);
			AgnUtils.sendExceptionMail("sql:" + sql + ", " + customerID, e);
		}
		return result;
	}

	@Override
	public boolean createImportTables(@VelocityCheck int companyID, int datasourceID, CustomerImportStatus status) {
		String prefix = "cust_" + companyID + "_tmp";
		String tabName = prefix + datasourceID + "_tbl";
		String keyIdx = prefix + datasourceID + "$KEYCOL$IDX";
		String custIdx = prefix + datasourceID + "$CUSTID$IDX";
		String sql = null;

		try {
			sql = "CREATE TEMPORARY TABLE " + tabName + " AS (SELECT * FROM customer_" + companyID + "_tbl WHERE 1 = 0)";
			update(logger, sql);

			sql = "ALTER TABLE " + tabName + " MODIFY change_date TIMESTAMP NULL DEFAULT NULL";
			update(logger, sql);

			sql = "ALTER TABLE " + tabName + " MODIFY creation_date TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP";
			update(logger, sql);

			sql = "CREATE INDEX " + keyIdx + " ON " + tabName + " (" + SafeString.getSafeDbColumnName(status.getKeycolumn()) + ")";
			update(logger, sql);

			sql = "CREATE INDEX " + custIdx + " ON " + tabName + " (customer_id)";
			update(logger, sql);
		} catch (Exception e) {
			logger.error("createTemporaryTables: " + sql, e);
			return false;
		}
		return true;
	}

	@Override
	public boolean deleteImportTables(@VelocityCheck int companyID, int datasourceID) {
		String tabName = "cust_" + companyID + "_tmp" + datasourceID + "_tbl";

		if (AgnUtils.isOracleDB()) {
			try {
				update(logger, "DROP TABLE " + tabName);
			} catch (Exception e) {
				logger.error("deleteTemporarytables (table: " + tabName + ")", e);
				return false;
			}
		}
		return true;
	}

	/**
	 * Retrieves new Datasource-ID for newly imported Subscribers
	 * 
	 * @return new Datasource-ID or 0
	 * 
	 *         TODO: Method not used, remove when nobody misses it (Support team?) private DatasourceDescription getNewDatasourceDescription(int companyID, String description) { HibernateTemplate tmpl
	 *         = new HibernateTemplate((SessionFactory)applicationContext.getBean("sessionFactory")); DatasourceDescription dsDescription=(DatasourceDescription)
	 *         applicationContext.getBean("DatasourceDescription");
	 * 
	 *         dsDescription.setId(0); dsDescription.setCompanyID(companyID); dsDescription.setSourcegroupID(2); dsDescription.setCreationDate(new java.util.Date());
	 *         dsDescription.setDescription(description); tmpl.save("DatasourceDescription", dsDescription); return dsDescription; }
	 */
	@Override
	public int sumOfRecipients(@VelocityCheck int companyID, String target) {
		int recipients = 0;

		String sql = "select count(customer_id) from customer_" + companyID + "_tbl cust where " + target;
		try {
			recipients = selectInt(logger, sql);
		} catch (Exception e) {
			recipients = 0;
		}
		return recipients;
	}

	@Override
	public boolean deleteRecipients(@VelocityCheck int companyID, String target) {
		boolean returnValue = false;
		String sql;

		sql = "DELETE FROM customer_" + companyID + "_binding_tbl WHERE customer_id in (select customer_id from customer_" + companyID + "_tbl cust where " + target + ")";
		try {
			update(logger, sql);
		} catch (Exception e) {
			logger.error("error deleting recipient bindings", e);
			returnValue = false;
		}

		sql = "delete ";
		if (!AgnUtils.isOracleDB()) {
			sql = sql + "cust ";
		}
		sql = sql + "from customer_" + companyID + "_tbl cust where " + target;
		try {
			update(logger, sql);
			returnValue = true;
		} catch (Exception e) {
			logger.error("error deleting recipients", e);
			returnValue = false;
		}
		return returnValue;
	}

	@Override
	public PaginatedListImpl<DynaBean> getRecipientList(@VelocityCheck int companyID, Set<String> columns, String sqlStatementForData, String sort, String direction, int page,
			int rownums, int previousFullListSize) throws IllegalAccessException, InstantiationException {
		return getRecipientList(companyID, columns, sqlStatementForData, null, sort, direction, page, rownums, previousFullListSize);
	}

	@Override
	public PaginatedListImpl<DynaBean> getRecipientList(@VelocityCheck int companyID, Set<String> columns, String sqlStatementForData, Object[] parametersForData, String sort,
			String direction, int page, int rownums, int previousFullListSize) throws IllegalAccessException, InstantiationException {
		String selectTotalRows = "SELECT COUNT(*) FROM (" + sqlStatementForData + ")";
		if (!AgnUtils.isOracleDB()) {
			selectTotalRows += " AS data";
		}
		int totalRows = selectInt(logger, selectTotalRows, parametersForData);

		String sortClause = "";
	    
		int maxRecipients = companyDao.getCompany(companyID).getMaxRecipients();
		if (maxRecipients > 0 && totalRows > maxRecipients) {
			// if the maximum number of recipients to show is exceeded, only the first page of unsorted recipients is shown to discharge the database and its performance
			page = 1;
		} else if (previousFullListSize == 0 || previousFullListSize != totalRows) {
			// if the previousFullListSize to show was not the full list of recipients, only the first page of unsorted recipients is shown to discharge the database and its performance
			page = 1;
		} else {
			if (StringUtils.isNotBlank(sort)) {
				try {
					sortClause = " ORDER BY " + sort;
					if (StringUtils.isNotBlank(direction)) {
						sortClause = sortClause + " " + direction;
					}
				} catch (Exception e) {
					logger.error("Invalid sort field", e);
				}
			}
		}

        page = AgnUtils.getValidPageNumber(totalRows, page, rownums);

		int offset = (page - 1) * rownums;

		if (AgnUtils.isOracleDB()) {
			sqlStatementForData = "SELECT * from (SELECT " + StringUtils.join(columns, ", ") + ", rownum r FROM (" + sqlStatementForData + sortClause + ")) WHERE r BETWEEN " + (offset + 1) + " AND " + (offset + rownums);
		} else {
			sqlStatementForData = sqlStatementForData + sortClause + " LIMIT " + offset + ", " + rownums;
		}

		List<Map<String, Object>> tmpList = select(logger, sqlStatementForData, parametersForData);
		List<DynaBean> result = new ArrayList<DynaBean>();
		if (tmpList != null && !tmpList.isEmpty()) {
			DynaProperty[] properties = new DynaProperty[columns.size()];
			int i = 0;
			for (String c : columns) {
				properties[i++] = new DynaProperty(c.toLowerCase(), String.class);
			}
			BasicDynaClass dynaClass = new BasicDynaClass("recipient", null, properties);

			for (Map<String, Object> row : tmpList) {
				DynaBean bean = dynaClass.newInstance();
				for (String c : columns) {
					bean.set(c.toLowerCase(), row.get(c.toUpperCase()) != null ? row.get(c.toUpperCase()).toString() : "");
				}
				result.add(bean);
			}
		}

		return new PaginatedListImpl<DynaBean>(result, totalRows, rownums, page, sort, direction);
	}

	/**
	 * Holds value of property applicationContext.
	 */
	@Override
	public void deleteAllNoBindings(@VelocityCheck int companyID, String toBeDeletedTable) {
		String delete = "DELETE FROM customer_" + companyID + "_tbl "
			+ "WHERE customer_id NOT IN (SELECT customer_id FROM customer_" + companyID + "_binding_tbl" + ") AND customer_id IN (SELECT * FROM " + toBeDeletedTable + ")";

		update(logger, delete);
		update(logger, "DROP TABLE " + toBeDeletedTable);
	}

	@Override
	public String createTmpTableByMailinglistID(@VelocityCheck int companyID, int mailinglistID) {
		String tableName = "tmp_" + String.valueOf(System.currentTimeMillis()) + "_delete_tbl";

		String sql = "create table " + tableName + " as (" + "SELECT customer_id FROM customer_" + companyID + "_tbl"
			+ " WHERE customer_id IN (SELECT customer_id FROM customer_" + companyID + "_binding_tbl WHERE mailinglist_id = " + mailinglistID + "))";
		update(logger, String.format(sql, mailinglistID));
		return tableName;
	}

	@Override
	public void deleteRecipientsBindings(@VelocityCheck int mailinglistID, int companyID, boolean activeOnly, boolean notAdminsAndTests) {
		String delete = "DELETE from customer_" + companyID + "_binding_tbl";
		String where = "WHERE mailinglist_id = ?";
		StringBuffer sql = new StringBuffer(delete).append(" ").append(where);

		if (activeOnly) {
			sql.append(" ").append(String.format("AND user_status = %d", BindingEntry.USER_STATUS_ACTIVE));
		}
		if (notAdminsAndTests) {
			sql.append(" ").append(String.format("AND user_type <> '%s' AND user_type <> '%s' AND user_type <> '%s'", BindingEntry.USER_TYPE_ADMIN, BindingEntry.USER_TYPE_TESTUSER, BindingEntry.USER_TYPE_TESTVIP));
		}

		update(logger, sql.toString(), new Object[] { new Integer(mailinglistID) });
	}

	@Override
	public CaseInsensitiveMap<CsvColInfo> readDBColumns(@VelocityCheck int companyID) {
		CaseInsensitiveMap<CsvColInfo> dbAllColumns = new CaseInsensitiveMap<CsvColInfo>();

		try {
			CaseInsensitiveMap<DbColumnType> columnTypes = DbUtilities.getColumnDataTypes(getDataSource(), "customer_" + companyID + "_tbl");

			for (String columnName : columnTypes.keySet()) {
				if (!columnName.equalsIgnoreCase("change_date") && !columnName.equalsIgnoreCase("creation_date") && !columnName.equalsIgnoreCase("datasource_id")) {
					DbColumnType type = columnTypes.get(columnName);
					CsvColInfo csvColInfo = new CsvColInfo();

					csvColInfo.setName(columnName);
					csvColInfo.setLength(type.getSimpleDataType() == SimpleDataType.Characters ? type.getCharacterLength() : type.getNumericPrecision());
					csvColInfo.setActive(false);
					csvColInfo.setNullable(type.isNullable());
					csvColInfo.setType(dbTypeToCsvType(type.getSimpleDataType()));

					dbAllColumns.put(columnName, csvColInfo);
				}
			}
		} catch (Exception e) {
			logger.error("readDBColumns (companyID: " + companyID + ")", e);
		}
		return dbAllColumns;
	}

	private static int dbTypeToCsvType(SimpleDataType type) {
		switch (type) {
		case Numeric:
			return CsvColInfo.TYPE_NUMERIC;
		case Characters:
			return CsvColInfo.TYPE_CHAR;
		case Date:
			return CsvColInfo.TYPE_DATE;
		default:
			return CsvColInfo.TYPE_UNKNOWN;
		}
	}

	@Override
	public Set<String> loadBlackList(@VelocityCheck int companyID) throws Exception {
		Set<String> blacklist = new HashSet<String>();
		try {
			List<Map<String, Object>> result = select(logger, "SELECT email FROM cust_ban_tbl WHERE company_id = ? OR company_id = 0", companyID);
			for (Map<String, Object> row : result) {
				String email = (String) row.get("email");
				blacklist.add(email == null ? null : email.toLowerCase());
			}
		} catch (Exception e) {
			logger.error("loadBlacklist (company ID: " + companyID + ")", e);
			throw e;
		}

		return blacklist;
	}

	@Override
	public Map<Integer, String> getAdminAndTestRecipientsDescription(@VelocityCheck int companyId, int mailingId) {
		String sql = "SELECT bind.customer_id, cust.email, cust.firstname, cust.lastname"
				+ " FROM mailing_tbl mail, customer_" + companyId + "_tbl cust, customer_" + companyId + "_binding_tbl bind"
				+ " WHERE bind.user_type in ('A', 'T') AND bind.user_status = 1 AND bind.mailinglist_id = mail.mailinglist_id AND bind.customer_id = cust.customer_id AND mail.mailing_id = ?"
				+ " ORDER BY bind.user_type, bind.customer_id";
		List<Map<String, Object>> tmpList = select(logger, sql, mailingId);
		HashMap<Integer, String> result = new HashMap<Integer, String>();
		for (Map<String, Object> map : tmpList) {
			int id = ((Number) map.get("customer_id")).intValue();
			String email = (String) map.get("email");
			String firstName = (String) map.get("firstname");
			String lastName = (String) map.get("lastname");

			if (firstName == null)
				firstName = "";

			if (lastName == null)
				lastName = "";

			result.put(id, firstName + " " + lastName + " &lt;" + email + "&gt;");
		}
		return result;
	}

	@Override
	public int getCustomerIdWithEmailInMailingList(@VelocityCheck int companyId, int mailingId, String email) {
		String sql = "SELECT DISTINCT bind.customer_id" + " FROM mailing_tbl mail, customer_" + companyId + "_tbl cust, customer_" + companyId + "_binding_tbl bind"
				+ " WHERE cust.email = ? AND bind.user_status = 1 AND bind.mailinglist_id = mail.mailinglist_id AND bind.customer_id = cust.customer_id AND mail.mailing_id = ?";

		try {
			List<Map<String, Object>> tmpList = select(logger, sql, email, mailingId);
			if (tmpList.size() > 0) {
				return ((Number) tmpList.get(0).get("customer_id")).intValue();
			} else {
				return 0;
			}
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public List<Recipient> getBouncedMailingRecipients(@VelocityCheck int companyId, int mailingId) {
		String sqlStatement = "SELECT cust.email AS email, cust.firstname AS firstname, cust.lastname AS lastname, cust.gender AS gender"
				+ " FROM customer_" + companyId + "_binding_tbl bind, customer_" + companyId + "_tbl cust"
				+ "	WHERE bind.customer_id = cust.customer_id AND exit_mailing_id = ? AND user_status = 2 AND mailinglist_id = (SELECT mailinglist_id FROM mailing_tbl WHERE mailing_id = ?)";

		List<Map<String, Object>> tmpList = select(logger, sqlStatement, mailingId, mailingId);
		List<Recipient> result = new ArrayList<Recipient>();
		for (Map<String, Object> row : tmpList) {
			Map<String, Object> customerData = new HashMap<String, Object>();

			customerData.put("gender", row.get("GENDER"));
			customerData.put("firstname", row.get("FIRSTNAME"));
			customerData.put("lastname", row.get("LASTNAME"));
			customerData.put("email", row.get("EMAIL"));

			Recipient newBean = new RecipientImpl();
			newBean.setCustParameters(customerData);

			result.add(newBean);
		}
		return result;
	}

	@Override
	public void deleteRecipients(@VelocityCheck int companyID, List<Integer> list) {
		if (list == null || list.size() < 1) {
			throw new RuntimeException("Invalid customerID list size");
		}

		StringBuilder whereStringBuilder = new StringBuilder(" WHERE customer_id IN (");
		whereStringBuilder.append(StringUtils.join(list, ", "));
		whereStringBuilder.append(')');
		String where = whereStringBuilder.toString();

		update(logger, "DELETE FROM customer_" + companyID + "_binding_tbl" + where);
		update(logger, "DELETE FROM customer_" + companyID + "_tbl" + where);
	}

	@Override
	public boolean exist(int customerId, @VelocityCheck int companyId) {
		return selectInt(logger, "SELECT COUNT(*) FROM customer_" + companyId + "_tbl WHERE customer_id = ?", customerId) > 0;
	}

	protected String createDateDefaultValueExpression(String defaultValue) {
		if (defaultValue.toLowerCase().equals("now()") || defaultValue.toLowerCase().equals("current_timestamp") || defaultValue.toLowerCase().equals("sysdate") || defaultValue.toLowerCase().equals("sysdate()")) {
			return "CURRENT_TIMESTAMP";
		} else {
			if (AgnUtils.isOracleDB()) {
				return "TO_DATE('" + defaultValue + "', 'DD.MM.YYYY HH24:MI:SS')";
			} else {
				return "STR_TO_DATE('" + defaultValue + "', '%d-%m-%Y')";
			}
		}
	}
	
	public List<Object> getCustomers(List<Integer> customerIDs, int companyID) {
//		long startTime = System.nanoTime();
		CustomerQueryProperties props;
		try {
			props = getQueryProperties(companyID);
		} catch (Exception e) {
			logger.error("getCustomers: Exception in getQueryProperties", e);
			return Collections.emptyList();
		}
		CaseInsensitiveMap<ProfileField> profileMap = props.getCustomerDBProfileStructure();

		String query ="SELECT * FROM customer_"	+ companyID	+ "_tbl WHERE customer_id IN(:ids)";
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("ids", customerIDs);

		NamedParameterJdbcTemplate jTmpl = new NamedParameterJdbcTemplate(getDataSource());
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> queryResult = jTmpl.queryForList(query, parameters);
		
		List<Object> results = new ArrayList<Object>();
		
		final SimpleDateFormat bdfmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		GregorianCalendar calendar = new GregorianCalendar();
		
//		long estimatedTime = System.nanoTime() - startTime;
//		logger.warn("getCustomers: after query before processing " + estimatedTime + "ns");

		for (Map<String, Object> row : queryResult) {
			CaseInsensitiveMap<Object> params = new CaseInsensitiveMap<Object>();
			
			for (Entry<String, ProfileField> entry : profileMap.entrySet()) {
				String columnName = entry.getKey();
				String columnType = entry.getValue().getDataType();
				Object value = row.get(columnName);
				if ("DATE".equalsIgnoreCase(columnType)) {
					if (value == null) {
						params.put(columnName + "_DAY_DATE", "");
						params.put(columnName + "_MONTH_DATE", "");
						params.put(columnName + "_YEAR_DATE", "");
						params.put(columnName + "_HOUR_DATE", "");
						params.put(columnName + "_MINUTE_DATE", "");
						params.put(columnName + "_SECOND_DATE", "");
						params.put(columnName, "");
					} else {
						calendar.setTime((Date) value);
						params.put(columnName + "_DAY_DATE", Integer.toString(calendar.get(GregorianCalendar.DAY_OF_MONTH)));
						params.put(columnName + "_MONTH_DATE", Integer.toString(calendar.get(GregorianCalendar.MONTH) + 1));
						params.put(columnName + "_YEAR_DATE", Integer.toString(calendar.get(GregorianCalendar.YEAR)));
						params.put(columnName + "_HOUR_DATE", Integer.toString(calendar.get(GregorianCalendar.HOUR_OF_DAY)));
						params.put(columnName + "_MINUTE_DATE", Integer.toString(calendar.get(GregorianCalendar.MINUTE)));
						params.put(columnName + "_SECOND_DATE", Integer.toString(calendar.get(GregorianCalendar.SECOND)));
						params.put(columnName, bdfmt.format(calendar.getTime()));
					}
				} else {
					if (value == null) {
						value = "";
					}
					params.put(columnName, value.toString());
				}
			}
			
			results.add(params);
		}
		
//		estimatedTime = System.nanoTime() - startTime;
//		logger.warn("getCustomers: after processing " + estimatedTime + "ns");
			
		return results;
	}
	
	/**
	 * This method looks like it is not used at all.
	 * Not by velocity and not by Java code.
	 * Therefore it is deprecated to remove it some day.
	 * 
	 * Watchout: Recipient's Daos and DbStructure are not set 
	 */
	@Deprecated
	@Override
	public List<Recipient> getRecipients(@VelocityCheck int companyID, Set<String> columnsToSelect, String sqlWhereClause, Object[] sqlParameterValues, String sortingColumn,
			boolean sortingDirectionAscending, int pageNumber, int rowsPerPage) throws IllegalAccessException, InstantiationException {
		String countStatement = "SELECT COUNT(*) FROM customer_" + companyID + "_tbl WHERE " + sqlWhereClause;
		int totalRows = selectInt(logger, countStatement, sqlParameterValues);

		pageNumber = AgnUtils.getValidPageNumber(totalRows, pageNumber, rowsPerPage);

		String sortClause = "";
		if (!StringUtils.isBlank(sortingColumn)) {
			sortClause = " ORDER BY " + sortingColumn;
			if (!sortingDirectionAscending) {
				sortClause = sortClause + " DESC";
			}
		}

		int startIndex = (pageNumber - 1) * rowsPerPage;

		Set<String> columns = new CaseInsensitiveSet(columnsToSelect);
		if (!columns.contains("customer_id")) {
			columns.add("customer_id");
		}

		String sqlStatementForRows;
		if (AgnUtils.isOracleDB()) {
			sqlStatementForRows = "SELECT * FROM (SELECT " + StringUtils.join(columns, ", ") + ", rownum r" + " FROM (" + "SELECT " + StringUtils.join(columns, ", ")
					+ " FROM customer_" + companyID + "_tbl WHERE " + sqlWhereClause + ")" + " WHERE 1 = 1" + sortClause + ") WHERE r BETWEEN " + (startIndex + 1) + " AND "
					+ (startIndex + rowsPerPage);
		} else {
			sqlStatementForRows = "SELECT " + StringUtils.join(columns, ", ") + "" + " FROM customer_" + companyID + "_tbl" + " WHERE " + sqlWhereClause + sortClause + " LIMIT "
					+ startIndex + ", " + rowsPerPage;
		}

		List<Recipient> recipientList = new ArrayList<Recipient>();
		List<Map<String, Object>> result = select(logger, sqlStatementForRows, sqlParameterValues);
		for (Map<String, Object> resultRow : result) {
			Recipient recipient = new RecipientImpl();
			Map<String, Object> custDBParameters = new CaseInsensitiveMap<Object>();
			for (String key : resultRow.keySet()) {
				if ("customer_id".equalsIgnoreCase(key)) {
					recipient.setCustomerID(((Number) resultRow.get("customer_id")).intValue());
				} else {
					if (resultRow.get(key) == null) {
						custDBParameters.put(key.toLowerCase(), null);
					} else {
						custDBParameters.put(key.toLowerCase(), resultRow.get(key).toString());
					}
				}
			}
			recipient.setCustParameters(custDBParameters);
			recipientList.add(recipient);
		}

		return recipientList;
	}

	@Override
	public void updateStatusByColumn(@VelocityCheck int companyId, String columnName, String columnValue, int newStatus, String remark) {
		String query = "SELECT customer_id FROM customer_" + companyId + "_tbl WHERE " + columnName + " = ?";
		List<Integer> list = select(logger, query, new IntegerRowMapper(), columnValue);

		String update = "UPDATE customer_" + companyId + "_binding_tbl SET user_status = ?, user_remark = ?, " + getBindingChangeDateColumnName() + " = CURRENT_TIMESTAMP"
				+ " WHERE customer_id = ? AND user_status != ?";
		for (int customerId : list) {
			update(logger, update, newStatus, remark, customerId, newStatus);
		}
	}

	@Override
	public void updateStatusByEmailPattern(@VelocityCheck int companyId, String emailPattern, int newStatus, String remark) {
		String query = "SELECT customer_id FROM customer_" + companyId + "_tbl WHERE email LIKE ?";
		List<Integer> list = select(logger, query, new IntegerRowMapper(), emailPattern);

		String update = "UPDATE customer_" + companyId + "_binding_tbl SET user_status = ?, user_remark = ?, " + getBindingChangeDateColumnName() + " = CURRENT_TIMESTAMP"
				+ " WHERE customer_id = ? AND user_status != ?";
		for (int customerId : list) {
			update(logger, update, newStatus, remark, customerId, newStatus);
		}
	}

	/**
	 * Returns the name of the column in the binding table holding the change date. This method is required due to differences in the database structure.
	 * 
	 * @return &quot;change_date&quot;
	 */
	protected String getBindingChangeDateColumnName() {
		return "change_date";
	}
	
	/**
	 * Load structure of Customer-Table for the given Company-ID in member
	 * variable "companyID". Load profile data into map. Has to be done before
	 * working with customer-data in class instance
	 * 
	 * @return true on success
	 */
	protected CaseInsensitiveMap<ProfileField> loadCustDBProfileStructure(@VelocityCheck int companyID) throws Exception {
		CaseInsensitiveMap<ProfileField> custDBStructure = new CaseInsensitiveMap<ProfileField>();
		for (ProfileField fieldDescription : columnInfoService.getColumnInfos(companyID)) {
			custDBStructure.put(fieldDescription.getColumn(), fieldDescription);
		}
		return custDBStructure;
	}

	@Override
	public boolean isCustomerIdValid(int companyID, int customerID) {
		SimpleJdbcTemplate template = new SimpleJdbcTemplate(getDataSource());
		
		String sql = "SELECT count(*) FROM customer_" + companyID + "_tbl WHERE customer_id=?";
		
		int count = template.queryForInt(sql, customerID);
		
		return count > 0;
		
	}
	
	public int getDefaultDatasourceID(String username, int companyID) {
		String sql = "SELECT default_data_source_id FROM webservice_user_tbl WHERE username=? AND company_id=?";
		int id = selectInt(logger, sql, username, companyID);
		return id;
	}
}
