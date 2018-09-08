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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.agnitas.util.AgnUtils;
import org.agnitas.util.DbUtilities;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;

/**
 * Helper class which hides the dependency injection variables and eases some select and update actions and logging.
 * But still the datasource or the SimpleJdbcTemplate can be used directly if needed.
 *
 * The logger of this class is not used for db actions to log, because it would hide the calling from the derived classes.
 * Therefore every simplified update and select method demands an logger delivered as parameter.
 *
 * @author aso
 *
 */
public abstract class BaseDaoImpl {
	/**
	 * General logger of this class. This logger is not used for the select and update actions.
	 */
	private static final transient Logger baseDaoImplLogger = Logger.getLogger(BaseDaoImpl.class);

	// ----------------------------------------------------------------------------------------------------------------
	// Dependency Injection

	/**
	 * Datasource to be used in this dao. SimpleJdbcTemplate uses this, too.
	 * This member variable should be set by dependency injectors like spring via the setDataSource method
	 */
	private DataSource dataSource;

	/**
	 * Cache variable for the dataSource vendor, so it must not be recalculated everytime.
	 * This variable may be uninitialized before the first execution of the isOracleDB method
	 */
	private Boolean isOracleDB = null;

	/**
	 * Cache variable for the SimpleJdbcTemplate, so it must not be recreated everytime
	 */
	private SimpleJdbcTemplate simpleJdbcTemplate = null;

	/**
	 * Dependency injection method
	 * @param dataSource to be used by this dao object
	 */
	@Required
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * Auxiliary method to execute some actions on the dataSource without using SimpleJdbcTemplate
	 * @return dataSource used by this dao object
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Business Logic

	/**
	 * Getter method for the SimpleJdbcTemplate, which uses the cache of this object
	 * @return the cached SimpleJdbcTemplate used by this object
	 */
	protected SimpleJdbcTemplate getSimpleJdbcTemplate() {
		if (simpleJdbcTemplate == null)
			simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);

		return simpleJdbcTemplate;
	}

	/**
	 * Checks the db vendor of the dataSource and caches the result for further usage
	 * @return true if db vendor of dataSource is Oracle, false if any other vendor (e.g. mysql)
	 */
	protected boolean isOracleDB() {
		if (isOracleDB == null) {
			isOracleDB = DbUtilities.checkDbVendorIsOracle(dataSource);
		}
		return isOracleDB;
	}

	/**
	 * Logs the sql statement. This is typically used before db executions.
	 * This method is also included in select and update methods of this class, so they don't have to be called explicitly
	 * @param logger
	 * @param statement
	 * @param parameter
	 */
	protected void logSqlStatement(Logger logger, String statement, Object... parameter) {
        if (logger.isDebugEnabled()) {
        	if (parameter != null && parameter.length > 0) {
        		logger.debug("SQL:" + statement + "\nParameter: " + StringUtils.join(parameter, ", "));
        	} else {
        		logger.debug("SQL:" + statement);
        	}
        }
	}

	/**
	 * Logs the sql statement and an occurred error. This is typically used after db executions.
	 * This method is also included in select and update methods of this class, so they don't have to be called explicitly
	 *
	 * @param e
	 * @param logger
	 * @param statement
	 * @param parameter
	 */
	protected void logSqlError(Exception e, Logger logger, String statement, Object... parameter) {
        AgnUtils.sendExceptionMail("SQL: " + statement + "\nParameter: " + StringUtils.join(parameter, ", "), e);
    	if (parameter != null && parameter.length > 0) {
    		logger.error("Error: " + e.getMessage() + "\nSQL:" + statement + "\nParameter: " + StringUtils.join(parameter, ", "), e);
    	} else {
    		logger.error("Error: " + e.getMessage() + "\nSQL:" + statement, e);
    	}
	}

	/**
	 * Closes SQL Statement object without throwing Exceptions.
	 * Exceptions are still loged as errors.
	 * @param statements
	 */
	protected void closeSilently(Statement... statements) {
		for (Statement statement : statements) {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					baseDaoImplLogger.error(e);
				}
			}
		}
	}

	/**
	 * Closes SQL Statement object without throwing Exceptions.
	 * Exceptions are still loged as errors.
	 * @param statements
	 */
	protected void closeSilently(Connection... connections) {
		for (Connection connection : connections) {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					baseDaoImplLogger.error(e);
				}
			}
		}
	}

	/**
	 * Logs the statement and parameter in debug-level, executes select and logs error.
	 *
	 * @param logger
	 * @param statement
	 * @param parameter
	 * @return List of db entries represented as caseinsensitive maps
	 */
	protected List<Map<String, Object>> select(Logger logger, String statement, Object... parameter) {
		try {
    		logSqlStatement(logger, statement, parameter);
    		return getSimpleJdbcTemplate().queryForList(statement, parameter);
    	} catch (DataAccessException e) {
    		logSqlError(e, logger, statement, parameter);
    		throw e;
    	} catch (RuntimeException e) {
    		logSqlError(e, logger, statement, parameter);
    		throw e;
    	}
	}

	/**
	 * Logs the statement and parameter in debug-level, executes select and logs error.
	 *
	 * @param logger
	 * @param statement
	 * @param rowMapper
	 * @param parameter
	 * @return List of db entries represented as objects
	 */
	protected <T> List<T> select(Logger logger, String statement, ParameterizedRowMapper<T> rowMapper, Object... parameter) {
		try {
    		logSqlStatement(logger, statement, parameter);
    		return getSimpleJdbcTemplate().query(statement, rowMapper, parameter);
    	} catch (DataAccessException e) {
    		logSqlError(e, logger, statement, parameter);
    		throw e;
    	} catch (RuntimeException e) {
    		logSqlError(e, logger, statement, parameter);
    		throw e;
    	}
	}

	/**
	 * Logs the statement and parameter in debug-level, executes select and logs error.
	 *
	 * @param logger
	 * @param statement
	 * @param requiredType
	 * @param parameter
	 * @return single db entry as object
	 */
	protected <T> T select(Logger logger, String statement, Class<T> requiredType, Object... parameter) {
		try {
    		logSqlStatement(logger, statement, parameter);
    		return getSimpleJdbcTemplate().queryForObject(statement, requiredType, parameter);
    	} catch (DataAccessException e) {
    		logSqlError(e, logger, statement, parameter);
    		throw e;
    	} catch (RuntimeException e) {
    		logSqlError(e, logger, statement, parameter);
    		throw e;
    	}
	}

	/**
	 * Logs the statement and parameter in debug-level, executes select and logs error.
	 * If the searched entry does not exist an DataAccessException is thrown.
	 *
	 * @param logger
	 * @param statement
	 * @param rowMapper
	 * @param parameter
	 * @return single db entry as object
	 */
	protected <T> T selectObject(Logger logger, String statement, ParameterizedRowMapper<T> rowMapper, Object... parameter) {
		try {
    		logSqlStatement(logger, statement, parameter);
    		return getSimpleJdbcTemplate().queryForObject(statement, rowMapper, parameter);
    	} catch (DataAccessException e) {
    		logSqlError(e, logger, statement, parameter);
    		throw e;
    	} catch (RuntimeException e) {
    		logSqlError(e, logger, statement, parameter);
    		throw e;
    	}
	}

	/**
	 * Logs the statement and parameter in debug-level, executes select for an object and logs error.
	 * If the searched entry does not exist, then null is returned as default value.
	 * If more than one item is found it throws an error saying so.
	 *
	 * @param logger
	 * @param statement
	 * @param rowMapper
	 * @param parameter
	 * @return single db entry as object, null if no entry was found
	 */
	protected <T> T selectObjectDefaultNull(Logger logger, String statement, ParameterizedRowMapper<T> rowMapper, Object... parameter) {
		List<T> list = select(logger, statement, rowMapper, parameter);
		if (list.size() == 1) {
			return list.get(0);
		} else if (list.size() == 0) {
			return null;
		} else {
			throw new RuntimeException("Found invalid number of items: " + list.size());
		}
	}

	/**
	 * Logs the statement and parameter in debug-level, executes select and logs error.
	 *
	 * @param logger
	 * @param statement
	 * @param parameter
	 * @return single db entry as int value
	 */
	protected int selectInt(Logger logger, String statement, Object... parameter) {
		try {
    		logSqlStatement(logger, statement, parameter);
    		return getSimpleJdbcTemplate().queryForInt(statement, parameter);
    	} catch (DataAccessException e) {
    		logSqlError(e, logger, statement, parameter);
    		throw e;
    	} catch (RuntimeException e) {
    		logSqlError(e, logger, statement, parameter);
    		throw e;
    	}
	}

	/**
	 * Logs the statement and parameter in debug-level, executes select and logs error.
	 * The given default value is returned, if the statement return an EmptyResultDataAccessException,
	 * which indicates that the selected value is missing and no rows are returned by DB.
	 * All other Exceptions are not touched and will be thrown in the usual way.
	 *
	 * @param logger
	 * @param statement
	 * @param defaultValue
	 * @param parameter
	 * @return single db entry as int value, default value if not found
	 */
	protected int selectIntWithDefaultValue(Logger logger, String statement, int defaultValue, Object... parameter) {
		try {
			logSqlStatement(logger, statement, parameter);
			return getSimpleJdbcTemplate().queryForInt(statement, parameter);
    	} catch (EmptyResultDataAccessException e) {
    		if (logger.isDebugEnabled()) {
    			logger.debug("Empty result, using default value: " + defaultValue);
    		}
			return defaultValue;
		} catch (DataAccessException e) {
			logSqlError(e, logger, statement, parameter);
    		throw e;
    	} catch (RuntimeException e) {
    		logSqlError(e, logger, statement, parameter);
    		throw e;
    	}
	}

	/**
	 * Logs the statement and parameter in debug-level, executes update and logs error.
	 *
	 * @param logger
	 * @param statement
	 * @param parameter
	 * @return number of touched lines in db
	 */
	protected int update(Logger logger, String statement, Object... parameter) {
		try {
    		logSqlStatement(logger, statement, parameter);
    		int touchedLines = getSimpleJdbcTemplate().update(statement, parameter);
    		if (logger.isDebugEnabled()) {
    			logger.debug("lines changed by update: " + touchedLines);
    		}
    		return touchedLines;
    	} catch (DataAccessException e) {
    		logSqlError(e, logger, statement, parameter);
    		throw e;
    	} catch (RuntimeException e) {
    		logSqlError(e, logger, statement, parameter);
    		throw e;
    	}
	}

	/**
	 * Method to update the data of an blob.
	 * This method should be DB-Vendor independent.
	 * The update statement must contain at least one parameter for the blob data and this must be the first parameter within the statement.
	 *
	 * Example: updateBlob(logger, "UPDATE tableName SET blobField = ? WHERE idField1 = ? AND idField2 = ?", blobDataArray, id1Object, id2Object);
	 *
	 * @param logger
	 * @param statement
	 * @param blobData
	 * @param parameter
	 * @throws Exception
	 */
	public void updateBlob(Logger logger, String statement, final byte[] blobData, final Object... parameter) throws Exception {
		if (blobData == null) {
			try {
	    		logSqlStatement(logger, statement, "blobDataLength: NULL-Blob", parameter);
	    		Object[] parameterInclNull = new Object[parameter.length + 1];
	    		parameterInclNull[0] = null;
	    		for (int i = 0; i < parameter.length; i++) {
		    		parameterInclNull[i + 1] = parameter[i];
	    		}
				new JdbcTemplate(dataSource).update(statement, parameterInclNull);
			} catch(Exception e) {
	    		logSqlError(e, logger, statement, "blobDataLength: NULL-Blob", parameter);
	    		throw e;
	    	}
		} else {
			final InputStream dataStream = new ByteArrayInputStream(blobData);
			try {
	    		logSqlStatement(logger, statement, "blobDataLength:" + blobData.length, parameter);
				new JdbcTemplate(dataSource).execute(statement, new AbstractLobCreatingPreparedStatementCallback(new DefaultLobHandler()) {
					@Override
					protected void setValues(PreparedStatement preparedStatement, LobCreator lobCreator) throws SQLException {
						lobCreator.setBlobAsBinaryStream(preparedStatement, 1, dataStream, blobData.length);
						int parameterIndex = 2;
						for (Object parameterObject : parameter) {
							preparedStatement.setObject(parameterIndex++, parameterObject);
						}
					}
				});
			} catch(Exception e) {
	    		logSqlError(e, logger, statement, "blobDataLength:" + blobData.length, parameter);
	    		throw e;
	    	} finally {
				try {
					dataStream.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}
	}

	/**
	 * Method to update the data of an blob.
	 * Logs the statement and parameter in debug-level, executes update and logs error.
	 *
	 * @param logger
	 * @param statement
	 * @param values
	 * @return
	 */
	public int[] batchupdate(Logger logger, String statement, List<Object[]> values) {
		try {
    		logSqlStatement(logger, statement, "BatchUpdateParameterList(Size: " + values.size() + ")");
			int[] touchedLines = getSimpleJdbcTemplate().batchUpdate(statement, values);
    		if (logger.isDebugEnabled()) {
    			logger.debug("lines changed by update: " + touchedLines);
    		}
    		return touchedLines;
    	} catch (DataAccessException e) {
    		logSqlError(e, logger, statement, "BatchUpdateParameterList(Size: " + values.size() + ")");
    		throw e;
    	} catch (RuntimeException e) {
    		logSqlError(e, logger, statement, "BatchUpdateParameterList(Size: " + values.size() + ")");
    		throw e;
    	}
	}

	public static int getValueOrDefaultFromNumberField(Number numberField, int defaultValue) {
		if (numberField == null) {
			return defaultValue;
		} else {
			return numberField.intValue();
		}
	}
}
