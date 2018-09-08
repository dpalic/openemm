package org.agnitas.emm.core.commons.util;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.agnitas.dao.impl.BaseDaoImpl;
import org.agnitas.util.DbUtilities;
import org.apache.log4j.Logger;

/**
 * This class is intended to simplify access to the config_tbl.
 * 
 * @author aso
 */
public class ConfigTableDao extends BaseDaoImpl {
	@SuppressWarnings("unused")
	
	/** The logger. */
	private static final transient Logger logger = Logger.getLogger(ConfigTableDao.class);
	
	private static final String SELECT_ALL_SIMPLIFIED_ORACLE = "SELECT TRIM(LEADING '.' FROM class || '.' || name) AS key_for_value, value AS value FROM config_tbl";
	private static final String SELECT_ALL_SIMPLIFIED_MYSQL = "SELECT TRIM(LEADING '.' FROM CONCAT(class, '.', name)) AS key_for_value, value AS value FROM config_tbl";
	
	private static final String SELECT_VALUE = "SELECT value FROM config_tbl WHERE class = ? AND name = ?";
	private static final String UPDATE_VALUE = "UPDATE config_tbl SET value = ? WHERE class = ? AND name = ?";
	private static final String INSERT_VALUE = "INSERT INTO config_tbl (class, classid, name, value) VALUES (?, 0, ?, ?)";
	
	public Map<String, String> getAllEntries() throws SQLException {
		boolean isOracleDb = DbUtilities.checkDbVendorIsOracle(getDataSource());
		List<Map<String, Object>> results = getSimpleJdbcTemplate().queryForList(isOracleDb ? SELECT_ALL_SIMPLIFIED_ORACLE : SELECT_ALL_SIMPLIFIED_MYSQL);
		Map<String, String> returnMap = new HashMap<String, String>();
		for (Map<String, Object> resultRow : results) {
			returnMap.put((String) resultRow.get("key_for_value"), (String) resultRow.get("value"));
		}
		
		if (isOracleDb) {
			returnMap.put(ConfigService.Value.DB_Vendor.toString(), "Oracle");
		} else {
			returnMap.put(ConfigService.Value.DB_Vendor.toString(), "MySQL");
		}
		
		return returnMap;
	}
	
	public void storeEntry(String classString, String name, String value)  {
		List<Map<String, Object>> results = getSimpleJdbcTemplate().queryForList(SELECT_VALUE, classString, name);
		if (results != null && results.size() > 0) {
			getSimpleJdbcTemplate().update(UPDATE_VALUE, value, classString, name);
		} else {
			getSimpleJdbcTemplate().update(INSERT_VALUE, classString, name, value);
		}
	}
}
