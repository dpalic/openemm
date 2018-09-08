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
package org.agnitas.util;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.BatchUpdateException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.FloatValidator;
import org.apache.log4j.Logger;
import org.hibernate.dialect.Dialect;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;

public class DbUtilities {
	private static final transient Logger logger = Logger.getLogger(DbUtilities.class);

	private static final NumberFormat TWO_DIGIT_FORMAT = new DecimalFormat("00");
	private static final NumberFormat FOUR_DIGIT_FORMAT = new DecimalFormat("0000");

    public static int getResultRowValueAsInt(String valueName, Map<String, Object> resultRow) {
    	return ((Number)resultRow.get(valueName)).intValue();
    }

    public static Integer getResultRowValueAsInteger(String valueName, Map<String, Object> resultRow) {
    	return new Integer(((Number)resultRow.get(valueName)).intValue());
    }

    public static String getResultRowValueAsString(String valueName, Map<String, Object> resultRow) {
    	return (String)resultRow.get(valueName);
    }

    public static Timestamp getResultRowValueAsTimestamp(String valueName, Map<String, Object> resultRow) {
    	return (Timestamp)resultRow.get(valueName);
    }

	public static int readoutTableInFile(DataSource dataSource, String tableName, File outputFile, String encoding, char separator, Character stringQuote) throws Exception {
    	return readoutInFile(dataSource, "SELECT * FROM " + tableName, outputFile, encoding, separator, stringQuote);
    }

    public static int readoutInFile(DataSource dataSource, String statementString, File outputFile, String encoding, char separator, Character stringQuote) throws Exception {
		if (outputFile.exists()) throw new Exception("Outputfile already exists");

		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		CsvWriter csvWriter = null;
		try {
			connection = DataSourceUtils.getConnection(dataSource);
			statement = connection.createStatement();
			resultSet = statement.executeQuery(statementString);
			csvWriter = new CsvWriter(new FileOutputStream(outputFile), encoding, separator, stringQuote);
			ResultSetMetaData metaData = resultSet.getMetaData();
			// write headers
			List<String> headers = new ArrayList<String>();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				headers.add(metaData.getColumnName(i));
			}
			csvWriter.writeValues(headers);

			// write values
			while (resultSet.next()) {
				List<String> values = new ArrayList<String>();
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					values.add(resultSet.getString(i));
				}
				csvWriter.writeValues(values);
			}

			return csvWriter.getWrittenLines();
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}

			if (connection != null) {
				DataSourceUtils.releaseConnection(connection, dataSource);
			}

			IOUtils.closeQuietly(csvWriter);
		}
	}

    public static int readoutInOutputStream(DataSource dataSource, String statementString, OutputStream outputStream, String encoding, char separator, Character stringQuote) throws Exception {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		CsvWriter csvWriter = null;
		try {
			connection = DataSourceUtils.getConnection(dataSource);
			statement = connection.createStatement();
			resultSet = statement.executeQuery(statementString);
			csvWriter = new CsvWriter(outputStream, encoding, separator, stringQuote);
			ResultSetMetaData metaData = resultSet.getMetaData();
			// write headers
			List<String> headers = new ArrayList<String>();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				headers.add(metaData.getColumnName(i));
			}
			csvWriter.writeValues(headers);

			// write values
			while (resultSet.next()) {
				List<String> values = new ArrayList<String>();
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					values.add(resultSet.getString(i));
				}
				csvWriter.writeValues(values);
			}

			return csvWriter.getWrittenLines();
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}

			if (connection != null) {
				DataSourceUtils.releaseConnection(connection, dataSource);
			}

			IOUtils.closeQuietly(csvWriter);
		}
	}

    public static String readout(Connection databaseConnection, String statementString, char separator) throws Exception {
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = databaseConnection.createStatement();
			resultSet = statement.executeQuery(statementString);
			StringBuilder tableDataString = new StringBuilder();
			ResultSetMetaData metaData = resultSet.getMetaData();
			// write headers
			List<String> headers = new ArrayList<String>();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				headers.add(metaData.getColumnName(i));
			}
			tableDataString.append(CsvWriter.getCsvLine(separator, '"', headers));
			tableDataString.append("\n");

			// write values
			while (resultSet.next()) {
				List<String> values = new ArrayList<String>();
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					values.add(resultSet.getString(i));
				}
				tableDataString.append(CsvWriter.getCsvLine(separator, '"', values));
				tableDataString.append("\n");
			}

			return tableDataString.toString();
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}
		}
	}

    public static String readout(JdbcTemplate jdbcTemplate, String statementString, char separator) throws Exception {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> result = jdbcTemplate.queryForList(statementString);
		StringBuilder tableDataString = new StringBuilder();
		// write headers
		List<String> headers = new ArrayList<String>();
		if (result.size() > 0) {
			for (String key : result.get(0).keySet()) {
				headers.add(key);
			}

			tableDataString.append(CsvWriter.getCsvLine(separator, '"', headers));
			tableDataString.append("\n");

			// write values
			for (Map<String, Object> row : result) {
				List<String> values = new ArrayList<String>();
				for (String key : row.keySet()) {
					Object value = row.get(key);
					if (value == null) {
						values.add("<null>");
					} else {
						values.add(value.toString());
					}
				}
				tableDataString.append(CsvWriter.getCsvLine(separator, '"', values));
				tableDataString.append("\n");
			}
		}

		return tableDataString.toString();
	}

    public static String readout(SimpleJdbcTemplate jdbcTemplate, String statementString, char separator) throws Exception {
		List<Map<String, Object>> result = jdbcTemplate.queryForList(statementString);
		StringBuilder tableDataString = new StringBuilder();
		// write headers
		List<String> headers = new ArrayList<String>();
		if (result.size() > 0) {
			for (String key : result.get(0).keySet()) {
				headers.add(key);
			}

			tableDataString.append(CsvWriter.getCsvLine(separator, '"', headers));
			tableDataString.append("\n");

			// write values
			for (Map<String, Object> row : result) {
				List<String> values = new ArrayList<String>();
				for (String key : row.keySet()) {
					Object value = row.get(key);
					if (value == null) {
						values.add("<null>");
					} else {
						values.add(value.toString());
					}
				}
				tableDataString.append(CsvWriter.getCsvLine(separator, '"', values));
				tableDataString.append("\n");
			}
		}

		return tableDataString.toString();
	}

    public static String readout(DataSource dataSource, String statementString, char separator) throws Exception {
		Connection connection = null;
		try {
			connection = DataSourceUtils.getConnection(dataSource);

			return readout(connection, statementString, separator);
		} finally {
			if (connection != null) {
				DataSourceUtils.releaseConnection(connection, dataSource);
			}
		}
	}

    public static String readoutTable(DataSource dataSource, String tableName, char separator) throws Exception {
    	return readout(dataSource, "SELECT * FROM " + tableName, separator);
    }

    public static String readoutTable(Connection connection, String tableName, char separator) throws Exception {
    	return readout(connection, "SELECT * FROM " + tableName, separator);
    }

    public static String readoutTable(JdbcTemplate jdbcTemplate, String tableName, char separator) throws Exception {
    	return readout(jdbcTemplate, "SELECT * FROM " + tableName, separator);
    }

    public static String readoutTable(SimpleJdbcTemplate jdbcTemplate, String tableName, char separator) throws Exception {
    	return readout(jdbcTemplate, "SELECT * FROM " + tableName, separator);
    }

	/**
	 *	Requests own dataSource connection and commits DB changes.
	 *
	 */
	public static Map<Integer, Object[]> importDataInTable(DataSource dataSource, String tableName, String[] tableColumns, List<Object[]> dataSets, boolean commitOnFullSuccessOnly) throws Exception {
		if (StringUtils.isBlank(tableName)) {
			throw new Exception("Missing parameter tableName for dataimport");
		}

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			statement = connection.createStatement();

			checkTableAndColumnsExist(connection, tableName, tableColumns, true);

			// Insert data
			Map<Integer, Object[]> notInsertedData = new HashMap<Integer, Object[]>();
			String insertStatementString = "INSERT INTO " + tableName + " (" + StringUtils.join(tableColumns, ", ") + ") VALUES (" + AgnUtils.repeatString("?", tableColumns.length, ", ") + ")";
			preparedStatement = connection.prepareStatement(insertStatementString);
			boolean hasOpenData = false;
			List<Object[]> currentUncommitedLines = new ArrayList<Object[]>();
			int datasetIndex;
			for (datasetIndex = 0; datasetIndex < dataSets.size(); datasetIndex++) {
				Object[] dataSet = dataSets.get(datasetIndex);
				currentUncommitedLines.add(dataSet);
				hasOpenData = true;

				if (dataSet.length != tableColumns.length) {
					if (!commitOnFullSuccessOnly) {
						notInsertedData.put(datasetIndex, dataSet);
					} else {
						connection.rollback();
						throw new Exception("Error on insert of dataset at index " + datasetIndex + ": invalid number of dataitems");
					}
				} else {
					preparedStatement.clearParameters();
					for (int parameterIndex = 0; parameterIndex < dataSet.length; parameterIndex++) {
						preparedStatement.setObject(parameterIndex + 1, dataSet[parameterIndex]);
					}
					preparedStatement.addBatch();

					if ((datasetIndex + 1) % 100 == 0) {
						hasOpenData = false;
						try {
							preparedStatement.executeBatch();
							if (!commitOnFullSuccessOnly) {
								connection.commit();
							}
							currentUncommitedLines.clear();
						} catch (BatchUpdateException bue) {
							if (commitOnFullSuccessOnly) {
								connection.rollback();
								throw new Exception("Error on insert of dataset between index " + (datasetIndex - currentUncommitedLines.size()) + " and index " + datasetIndex + ": " + bue.getMessage());
							} else {
								connection.rollback();
								importDataInTable(datasetIndex - currentUncommitedLines.size(), connection, preparedStatement, tableColumns, currentUncommitedLines, notInsertedData);
							}
						} catch (Exception e) {
							connection.rollback();
							throw new Exception("Error on insert of dataset between index " + (datasetIndex - currentUncommitedLines.size()) + " and index " + datasetIndex + ": " + e.getMessage());
						}
					}
				}
			}

			if (hasOpenData) {
				hasOpenData = false;
				try {
					preparedStatement.executeBatch();
					if (!commitOnFullSuccessOnly) {
						connection.commit();
					}
					currentUncommitedLines.clear();
				} catch (BatchUpdateException bue) {
					if (commitOnFullSuccessOnly) {
						connection.rollback();
						throw new Exception("Error on insert of dataset between index " + (datasetIndex - currentUncommitedLines.size()) + " and index " + datasetIndex + ": " + bue.getMessage());
					} else {
						connection.rollback();
						importDataInTable(datasetIndex - currentUncommitedLines.size(), connection, preparedStatement, tableColumns, currentUncommitedLines, notInsertedData);
					}
				} catch (Exception e) {
					connection.rollback();
					throw new Exception("Error on insert of dataset between index " + (datasetIndex - currentUncommitedLines.size()) + " and index " + datasetIndex + ": " + e.getMessage());
				}
			}

			if (commitOnFullSuccessOnly) {
				connection.commit();
			}

			return notInsertedData;
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}

			if (preparedStatement != null) {
				preparedStatement.close();
			}

			if (connection != null) {
				connection.rollback();
				connection.close();
			}
		}
	}

	private static void importDataInTable(int offsetIndex, Connection connection, PreparedStatement preparedStatement, String[] columnMapping, List<Object[]> data, Map<Integer, Object[]> notInsertedData) throws Exception {
		int dataLineIndex = offsetIndex;
		for (Object[] dataLine : data) {
			dataLineIndex++;
			if (dataLine.length != columnMapping.length) {
				notInsertedData.put(dataLineIndex, dataLine);
			} else {
				int parameterIndex = 1;
				for (int csvValueIndex = 0; csvValueIndex < dataLine.length; csvValueIndex++) {
					if (columnMapping[csvValueIndex] != null) {
						preparedStatement.setObject(parameterIndex++, dataLine[csvValueIndex]);
					}
				}

				try {
					preparedStatement.execute();
					connection.commit();
				} catch (Exception e) {
					notInsertedData.put(dataLineIndex, dataLine);
					connection.rollback();
				}
			}
		}
	}

	public static Map<Integer, Tuple<List<String>, String>> importCsvFileInTable(DataSource dataSource, String tableName, String[] columnMapping, File csvFile, String encoding, boolean commitOnFullSuccessOnly) throws Exception {
		InputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(csvFile);
			return importCsvFileInTable(dataSource, tableName, columnMapping, fileInputStream, encoding, false, commitOnFullSuccessOnly);
		} finally {
			IOUtils.closeQuietly(fileInputStream);
		}
	}

	public static Map<Integer, Tuple<List<String>, String>> importCsvFileInTable(DataSource dataSource, String tableName, String[] columnMapping, File csvFile, String encoding, boolean fillMissingTrailingColumnsWithNull, boolean commitOnFullSuccessOnly, boolean containsHeadersInFirstRow) throws Exception {
		InputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(csvFile);
			return importCsvFileInTable(dataSource, tableName, columnMapping, fileInputStream, encoding, fillMissingTrailingColumnsWithNull, commitOnFullSuccessOnly, containsHeadersInFirstRow);
		} finally {
			IOUtils.closeQuietly(fileInputStream);
		}
	}

	public static Map<Integer, Tuple<List<String>, String>> importCsvFileInTable(DataSource dataSource, String tableName, String[] columnMapping, InputStream csvFileInputStream, String encoding, boolean commitOnFullSuccessOnly, boolean containsHeadersInFirstRow) throws Exception {
		return importCsvFileInTable(dataSource, tableName, columnMapping, csvFileInputStream, encoding, false, commitOnFullSuccessOnly, containsHeadersInFirstRow);
	}

	public static Map<Integer, Tuple<List<String>, String>> importCsvFileInTable(DataSource dataSource, String tableName, String[] columnMapping, InputStream csvFileInputStream, String encoding, boolean fillMissingTrailingColumnsWithNull, boolean commitOnFullSuccessOnly, boolean containsHeadersInFirstRow) throws Exception {
		return importCsvFileInTable(dataSource, tableName, columnMapping, null, csvFileInputStream, encoding, fillMissingTrailingColumnsWithNull, commitOnFullSuccessOnly, containsHeadersInFirstRow);
	}

	public static Map<Integer, Tuple<List<String>, String>> importCsvFileInTable(DataSource dataSource, String tableName, String[] columnMapping, String[] columnDateFormats, InputStream csvFileInputStream, String encoding, boolean fillMissingTrailingColumnsWithNull, boolean commitOnFullSuccessOnly, boolean containsHeadersInFirstRow) throws Exception {
		return importCsvFileInTable(dataSource, tableName, columnMapping, columnDateFormats, csvFileInputStream, encoding, ';', null, fillMissingTrailingColumnsWithNull, commitOnFullSuccessOnly, containsHeadersInFirstRow);
	}

	/**
	 *	Requests own dataSource connection and commits DB changes.
	 *
	 */
	public static Map<Integer, Tuple<List<String>, String>> importCsvFileInTable(DataSource dataSource, String tableName, String[] columnMapping, String[] columnDateFormats, InputStream csvFileInputStream, String encoding, char separatorChar, Character stringQuoteChar, boolean fillMissingTrailingColumnsWithNull, boolean commitOnFullSuccessOnly, boolean containsHeadersInFirstRow) throws Exception {
		if (StringUtils.isBlank(tableName)) {
			throw new Exception("Missing parameter tableName for dataimport");
		}

		Connection connection = null;
		PreparedStatement preparedStatement = null;

		CsvReader csvReader = null;

		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);

			csvReader = new CsvReader(csvFileInputStream, encoding, separatorChar, stringQuoteChar);
			csvReader.setFillMissingTrailingColumnsWithNull(fillMissingTrailingColumnsWithNull);

			// First line may contain headers
			List<String> csvLine;
			if (containsHeadersInFirstRow) {
				csvLine = csvReader.readNextCsvLine();
				if (columnMapping == null) {
					columnMapping = csvLine.toArray(new String[0]);
				}
			}

			checkTableAndColumnsExist(connection, tableName, columnMapping, true);

			List<String> dbColumns = new ArrayList<String>();
			for (String column : columnMapping) {
				if (column != null) {
					dbColumns.add(column);
				}
			}

			Map<Integer, Tuple<List<String>, String>> notInsertedData = new HashMap<Integer, Tuple<List<String>, String>>();
			String insertStatementString = "INSERT INTO " + tableName + " (" + StringUtils.join(dbColumns, ", ") + ") VALUES (" + AgnUtils.repeatString("?", dbColumns.size(), ", ") + ")";
			preparedStatement = connection.prepareStatement(insertStatementString);

			// Read and insert data
			int csvLineIndex = 1; // index obeys headerline => real lineindex in csv-file
			boolean hasOpenData = false;
			List<List<String>> currentUncommitedLines = new ArrayList<List<String>>();
			while ((csvLine = csvReader.readNextCsvLine()) != null) {
				csvLineIndex++;
				currentUncommitedLines.add(csvLine);
				hasOpenData = true;

				if (csvLine.size() != columnMapping.length) {
					if (!commitOnFullSuccessOnly) {
						notInsertedData.put(csvLineIndex, new Tuple<List<String>, String>(csvLine, "Not enough values"));
					} else {
						connection.rollback();
						throw new Exception("Error on insert of dataset at line " + csvLineIndex + ": invalid number of dataitems");
					}
				} else {
					int parameterIndex = 1;
					for (int csvValueIndex = 0; csvValueIndex < csvLine.size(); csvValueIndex++) {
						if (columnMapping[csvValueIndex] != null) {
							preparedStatement.setString(parameterIndex++, csvLine.get(csvValueIndex));
						}
					}
					preparedStatement.addBatch();

					if (csvLineIndex % 100 == 0) {
						hasOpenData = false;
						try {
							preparedStatement.executeBatch();
							if (!commitOnFullSuccessOnly) {
								connection.commit();
							}
							currentUncommitedLines.clear();
						} catch (BatchUpdateException bue) {
							if (commitOnFullSuccessOnly) {
								connection.rollback();
								throw new Exception("Error on insert of dataset between line " + (csvLineIndex - currentUncommitedLines.size()) + " and line " + csvLineIndex + ": " + bue.getMessage());
							} else {
								connection.rollback();
								importCsvDataInTable(csvLineIndex - currentUncommitedLines.size(), connection, preparedStatement, columnMapping, columnDateFormats, currentUncommitedLines, notInsertedData);
							}
						} catch (Exception e) {
							if (!commitOnFullSuccessOnly) {
								notInsertedData.put(csvLineIndex, new Tuple<List<String>, String>(csvLine, e.getMessage()));
								connection.rollback();
							} else {
								connection.rollback();
								throw new Exception("Error on insert of dataset at line " + csvLineIndex + ": " + e.getMessage());
							}
						}
					}
				}
			}

			if (hasOpenData) {
				hasOpenData = false;
				try {
					preparedStatement.executeBatch();
					if (!commitOnFullSuccessOnly) {
						connection.commit();
					}
					currentUncommitedLines.clear();
				} catch (BatchUpdateException bue) {
					if (commitOnFullSuccessOnly) {
						connection.rollback();
						throw new Exception("Error on insert of dataset between line " + (csvLineIndex - currentUncommitedLines.size()) + " and line " + csvLineIndex + ": " + bue.getMessage());
					} else {
						connection.rollback();
						importCsvDataInTable(csvLineIndex - currentUncommitedLines.size(), connection, preparedStatement, columnMapping, columnDateFormats, currentUncommitedLines, notInsertedData);
					}
				} catch (Exception e) {
					connection.rollback();
					throw new Exception("Error on insert of dataset between line " + (csvLineIndex - currentUncommitedLines.size()) + " and line " + csvLineIndex + ": " + e.getMessage());
				}
			}

			if (commitOnFullSuccessOnly) {
				connection.commit();
			}

			return notInsertedData;
		} finally {
			if (csvReader != null) {
				csvReader.close();
			}

			if (preparedStatement != null) {
				preparedStatement.close();
			}

			if (connection != null) {
				connection.rollback();
				connection.close();
			}
		}
	}

	private static void importCsvDataInTable(int offsetIndex, Connection connection, PreparedStatement preparedStatement, String[] columnMapping, String[] columnDateFormats, List<List<String>> data, Map<Integer, Tuple<List<String>, String>> notInsertedData) throws Exception {
		int csvLineIndex = offsetIndex;
		for (List<String> csvLine : data) {
			csvLineIndex++;
			if (csvLine.size() != columnMapping.length) {
				notInsertedData.put(csvLineIndex, new Tuple<List<String>, String>(csvLine, "Not enough values"));
			} else {
				int parameterIndex = 1;
				for (int csvValueIndex = 0; csvValueIndex < csvLine.size(); csvValueIndex++) {
					if (columnMapping[csvValueIndex] != null) {
						if (columnDateFormats != null && columnDateFormats[csvValueIndex] != null) {
							preparedStatement.setObject(parameterIndex++, new SimpleDateFormat(columnDateFormats[csvValueIndex]).parse(csvLine.get(csvValueIndex)));
						} else {
							preparedStatement.setString(parameterIndex++, csvLine.get(csvValueIndex));
						}
					}
				}

				try {
					preparedStatement.execute();
					connection.commit();
				} catch (Exception e) {
					notInsertedData.put(csvLineIndex, new Tuple<List<String>, String>(csvLine, e.getMessage()));
					connection.rollback();
				}
			}
		}
	}

	public static boolean checkDbVendorIsOracle(DataSource dataSource) {
		Connection connection = null;
		try {
			connection = DataSourceUtils.getConnection(dataSource);
			return checkDbVendorIsOracle(connection);
		} finally {
			DataSourceUtils.releaseConnection(connection, dataSource);
		}
	}

	public static boolean checkDbVendorIsOracle(Connection connection) {
		try {
			DatabaseMetaData databaseMetaData = connection.getMetaData();
			if (databaseMetaData != null) {
				String productName = databaseMetaData.getDatabaseProductName();
				if ("oracle".equalsIgnoreCase(productName)) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (SQLException e) {
			return false;
		}
	}

	public static String getDbUrl(DataSource dataSource) throws SQLException {
		Connection connection = null;
		try {
			connection = DataSourceUtils.getConnection(dataSource);
			return getDbUrl(connection);
		} finally {
			DataSourceUtils.releaseConnection(connection, dataSource);
		}
	}

	public static String getDbUrl(Connection connection) {
		try {
			DatabaseMetaData databaseMetaData = connection.getMetaData();
			if (databaseMetaData != null) {
				return databaseMetaData.getURL();
			} else {
				return null;
			}
		} catch (SQLException e) {
			return null;
		}
	}

	public static boolean checkTableAndColumnsExist(DataSource dataSource, String tableName, String[] columns) throws Exception {
		Connection connection = null;
		try {
			connection = DataSourceUtils.getConnection(dataSource);
			return checkTableAndColumnsExist(connection, tableName, columns, false);
		} finally {
			 DataSourceUtils.releaseConnection(connection, dataSource);
		}
	}

	public static boolean checkTableAndColumnsExist(Connection connection, String tableName, String[] columns) throws Exception {
		return checkTableAndColumnsExist(connection, tableName, columns, false);
	}

	public static boolean checkTableAndColumnsExist(DataSource dataSource, String tableName, String[] columns, boolean throwExceptionOnError) throws Exception {
    	Connection connection = null;
    	try {
			connection = DataSourceUtils.getConnection(dataSource);
    		return checkTableAndColumnsExist(connection, tableName, columns, throwExceptionOnError);
    	} finally {
			DataSourceUtils.releaseConnection(connection, dataSource);
    	}
	}

	public static boolean checkTableAndColumnsExist(Connection connection, String tableName, String[] columns, boolean throwExceptionOnError) throws Exception {
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			statement = connection.createStatement();

			// Check if table exists
			try {
				resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE 1 = 0");
			} catch (Exception e) {
				if (throwExceptionOnError) {
					throw new Exception("Table '" + tableName + "' does not exist");
				} else {
					return false;
				}
			}

			// Check if all needed columns exist
			Set<String> dbTableColumns = new HashSet<String>();
			ResultSetMetaData metaData = resultSet.getMetaData();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				dbTableColumns.add(metaData.getColumnName(i).toUpperCase());
			}
			for (String column : columns) {
				if (column != null && !dbTableColumns.contains(column.toUpperCase())) {
					if (throwExceptionOnError) {
						throw new Exception("Column '" + column + "' does not exist in table '" + tableName + "'");
					} else {
						return false;
					}
				}
			}
			return true;
		} finally {
			closeQuietly(resultSet);
			resultSet = null;
			closeQuietly(statement);
			statement = null;
		}
	}

	public static boolean checkIfTableExists(DataSource dataSource, String tableName) {
		try {
			checkTableExists(dataSource, tableName);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static void checkTableExists(DataSource dataSource, String tableName) throws Exception {
		Connection connection = null;
		try {
			connection = DataSourceUtils.getConnection(dataSource);
			checkTableExists(connection, tableName);
		} finally {
			 DataSourceUtils.releaseConnection(connection, dataSource);
		}
	}

	public static void checkTableExists(Connection connection, String tableName) throws Exception {
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			statement = connection.createStatement();

			// Check if table exists
			try {
				resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE 1 = 0");
			} catch (Exception e) {
				throw new Exception("Table '" + tableName + "' does not exist");
			}
		} finally {
			closeQuietly(resultSet);
			resultSet = null;
			closeQuietly(statement);
			statement = null;
		}
	}

	public static String callStoredProcedureWithDbmsOutput(Connection connection, String procedureName, Object... parameters) throws SQLException {
		CallableStatement callableStatement = null;
		try {
			callableStatement = connection.prepareCall("begin dbms_output.enable(:1); end;");
			callableStatement.setLong(1, 10000);
			callableStatement.executeUpdate();
			callableStatement.close();
			callableStatement = null;

			if (parameters != null) {
				callableStatement = connection.prepareCall("{call " + procedureName + "(" + AgnUtils.repeatString("?", parameters.length, ", ") + ")}");
				for (int i = 0; i < parameters.length; i++) {
					callableStatement.setObject(i + 1, parameters[i]);
				}
			} else {
				callableStatement = connection.prepareCall("{call " + procedureName + "()}");
			}
			callableStatement.execute();
			callableStatement.close();
			callableStatement = null;

			callableStatement = connection
				.prepareCall(
					"declare "
					+ "    l_line varchar2(255); "
					+ "    l_done number; "
					+ "    l_buffer long; "
					+ "begin "
					+ "  loop "
					+ "    exit when length(l_buffer)+255 > :maxbytes OR l_done = 1; "
					+ "    dbms_output.get_line( l_line, l_done ); "
					+ "    l_buffer := l_buffer || l_line || chr(10); "
					+ "  end loop; " + " :done := l_done; "
					+ " :buffer := l_buffer; "
					+ "end;");

			callableStatement.registerOutParameter(2, Types.INTEGER);
			callableStatement.registerOutParameter(3, Types.VARCHAR);
			StringBuffer dbmsOutput = new StringBuffer(1024);
			while (true) {
				callableStatement.setInt(1, 32000);
				callableStatement.executeUpdate();
				dbmsOutput.append(callableStatement.getString(3).trim());
				if (callableStatement.getInt(2) == 1) {
					break;
				}
			}
			callableStatement.close();
			callableStatement = null;

			callableStatement = connection.prepareCall("begin dbms_output.disable; end;");
			callableStatement.executeUpdate();
			callableStatement.close();
			callableStatement = null;

			return dbmsOutput.toString();
		} finally {
			closeQuietly(callableStatement);
		}
	}

	public static void closeQuietly(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				// do nothing
			}
		}
	}

	public static void closeQuietly(Statement statement) {
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				// do nothing
			}
		}
	}

	public static void closeQuietly(ResultSet resultSet) {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				// do nothing
			}
		}
	}

	public static TextTable getResultAsTextTable(DataSource datasource, String selectString) throws Exception {
		List<Map<String, Object>> results = new SimpleJdbcTemplate(datasource).queryForList(selectString);
		if (results != null && results.size() > 0) {
			TextTable textTable = new TextTable();
			for (String column : results.get(0).keySet()) {
				textTable.addColumn(column);
			}

			if (results != null && results.size() > 0) {
				for (Map<String, Object> row : results) {
					textTable.startNewLine();
					for (String column : row.keySet()) {
						if (row.get(column) != null) {
							textTable.addValueToCurrentLine(row.get(column).toString());
						} else {
							textTable.addValueToCurrentLine("<null>");
						}
					}
				}
			}

			return textTable;
		} else {
			return null;
		}
	}

	public static List<String> getColumnNames(DataSource dataSource, String tableName) throws Exception {
		if (dataSource == null) {
			throw new Exception("Invalid empty dataSource for getColumnNames");
		} else if (StringUtils.isBlank(tableName)) {
			throw new Exception("Invalid empty tableName for getColumnNames");
		} else {
			Connection connection = null;
			Statement stmt = null;
			ResultSet rset = null;
			try {
				connection = DataSourceUtils.getConnection(dataSource);
				stmt = connection.createStatement();
		    	String sql = "SELECT * FROM " + SafeString.getSafeDbTableName(tableName) + " WHERE 1 = 0";
		        rset = stmt.executeQuery(sql);
		        List<String> columnNamesList = new ArrayList<String>();
		        for (int i = 1; i <= rset.getMetaData().getColumnCount(); i++) {
		        	columnNamesList.add(rset.getMetaData().getColumnName(i));
		        }
		        return columnNamesList;
			} finally {
		        closeQuietly(rset);
		        closeQuietly(stmt);
				DataSourceUtils.releaseConnection(connection, dataSource);

			}
		}
	}

	public static DbColumnType getColumnDataType(DataSource dataSource, String tableName, String columnName) throws Exception {
		if (dataSource == null) {
			throw new Exception("Invalid empty dataSource for getColumnDataType");
		} else if (StringUtils.isBlank(tableName)) {
			throw new Exception("Invalid empty tableName for getColumnDataType");
		} else if (StringUtils.isBlank(columnName)) {
			throw new Exception("Invalid empty columnName for getColumnDataType");
		} else {
			Connection connection = null;
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			try {
				connection = DataSourceUtils.getConnection(dataSource);
				int characterLength;
				int numericPrecision;
				int numericScale;
				boolean isNullable;
				if (checkDbVendorIsOracle(dataSource)) {
					// Watchout: Oracle's timestamp datatype is "TIMESTAMP(6)", so remove the bracket value
					String sql = "SELECT NVL(substr(data_type, 1, instr(data_type, '(') - 1), data_type) as data_type, data_length, data_precision, data_scale, nullable FROM user_tab_columns WHERE table_name = ? AND column_name = ?";
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, tableName.toUpperCase());
					preparedStatement.setString(2, columnName.toUpperCase());
					resultSet = preparedStatement.executeQuery();

					if (resultSet.next()) {
						String dataType = resultSet.getString("data_type");

						characterLength = resultSet.getInt("data_length");
						if (resultSet.wasNull() || "DATE".equalsIgnoreCase(dataType) || "TIMESTAMP".equalsIgnoreCase(dataType)) {
							characterLength = -1;
						}
						numericPrecision = resultSet.getInt("data_precision");
						if (resultSet.wasNull()) {
							numericPrecision = -1;
						}
						numericScale = resultSet.getInt("data_scale");
						if (resultSet.wasNull()) {
							numericScale = -1;
						}
						isNullable = resultSet.getString("nullable").equalsIgnoreCase("y");

						return new DbColumnType(dataType, characterLength, numericPrecision, numericScale, isNullable);
					} else {
						return null;
					}
	        	} else {
	        		String sql = "SELECT data_type, character_maximum_length, numeric_precision, numeric_scale, is_nullable FROM information_schema.columns WHERE table_schema = SCHEMA() AND lower(table_name) = lower(?) AND column_name = ?";
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, tableName);
					preparedStatement.setString(2, columnName);
					resultSet = preparedStatement.executeQuery();

					if (resultSet.next()) {
						String dataType = resultSet.getString("data_type");

						characterLength = resultSet.getInt("character_maximum_length");
						if (resultSet.wasNull() || "DATE".equalsIgnoreCase(dataType) || "TIMESTAMP".equalsIgnoreCase(dataType)) {
							characterLength = -1;
						}
						numericPrecision = resultSet.getInt("numeric_precision");
						if (resultSet.wasNull()) {
							numericPrecision = -1;
						}
						numericScale = resultSet.getInt("numeric_scale");
						if (resultSet.wasNull()) {
							numericScale = -1;
						}
						isNullable = resultSet.getString("is_nullable").equalsIgnoreCase("yes");

						return new DbColumnType(dataType, characterLength, numericPrecision, numericScale, isNullable);
					} else {
						return null;
					}
	        	}
			} catch (Exception e) {
				return null;
			} finally {
		        closeQuietly(resultSet);
		        closeQuietly(preparedStatement);
				DataSourceUtils.releaseConnection(connection, dataSource);
			}
		}
	}

	public static CaseInsensitiveMap<DbColumnType> getColumnDataTypes(DataSource dataSource, String tableName) throws Exception {
		if (dataSource == null) {
			throw new Exception("Invalid empty dataSource for getColumnDataTypes");
		} else if (StringUtils.isBlank(tableName)) {
			throw new Exception("Invalid empty tableName for getColumnDataTypes");
		} else {
			Connection connection = null;
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			try {
				CaseInsensitiveMap<DbColumnType> returnMap = new CaseInsensitiveMap<DbColumnType>();
				connection = DataSourceUtils.getConnection(dataSource);
				if (checkDbVendorIsOracle(dataSource)) {
					// Watchout: Oracle's timestamp datatype is "TIMESTAMP(6)", so remove the bracket value
					String sql = "SELECT column_name, NVL(substr(data_type, 1, instr(data_type, '(') - 1), data_type) as data_type, data_length, data_precision, data_scale, nullable FROM user_tab_columns WHERE table_name = ?";
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, tableName.toUpperCase());
					resultSet = preparedStatement.executeQuery();
					while (resultSet.next()) {
						String dataType = resultSet.getString("data_type");

						int characterLength = resultSet.getInt("data_length");
						if (resultSet.wasNull() || "DATE".equalsIgnoreCase(dataType) || "TIMESTAMP".equalsIgnoreCase(dataType)) {
							characterLength = -1;
						}
						int numericPrecision = resultSet.getInt("data_precision");
						if (resultSet.wasNull()) {
							if ("number".equalsIgnoreCase(dataType)) {
								// maximum precision of oracle number is 38 which is represented by null
								numericPrecision = 38;
							} else {
								numericPrecision = -1;
							}
						}
						int numericScale = resultSet.getInt("data_scale");
						if (resultSet.wasNull()) {
							numericScale = -1;
						}
						boolean isNullable = resultSet.getString("nullable").equalsIgnoreCase("y");

						returnMap.put(resultSet.getString("column_name"), new DbColumnType(dataType, characterLength, numericPrecision, numericScale, isNullable));
					}
	        	} else {
	        		String sql = "SELECT column_name, data_type, character_maximum_length, numeric_precision, numeric_scale, is_nullable FROM information_schema.columns WHERE table_schema = SCHEMA() AND lower(table_name) = lower(?)";
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, tableName);
					resultSet = preparedStatement.executeQuery();
					while (resultSet.next()) {
						String dataType = resultSet.getString("data_type");

						int characterLength = resultSet.getInt("character_maximum_length");
						if (resultSet.wasNull() || "DATE".equalsIgnoreCase(dataType) || "TIMESTAMP".equalsIgnoreCase(dataType)) {
							characterLength = -1;
						}
						int numericPrecision = resultSet.getInt("numeric_precision");
						if (resultSet.wasNull()) {
							numericPrecision = -1;
						}
						int numericScale = resultSet.getInt("numeric_scale");
						if (resultSet.wasNull()) {
							numericScale = -1;
						}
						boolean isNullable = resultSet.getString("is_nullable").equalsIgnoreCase("yes");

						returnMap.put(resultSet.getString("column_name"), new DbColumnType(dataType, characterLength, numericPrecision, numericScale, isNullable));
					}
	        	}
		        return returnMap;
			} catch (Exception e) {
				throw e;
			} finally {
		        closeQuietly(resultSet);
		        closeQuietly(preparedStatement);
				DataSourceUtils.releaseConnection(connection, dataSource);
			}
		}
	}

	public static int getColumnCount(DataSource dataSource, String tableName) throws Exception {
		if (dataSource == null) {
			throw new Exception("Invalid empty dataSource for getColumnCount");
		} else if (StringUtils.isBlank(tableName)) {
			throw new Exception("Invalid empty tableName for getColumnCount");
		} else {
			Connection connection = null;
			try {
				connection = DataSourceUtils.getConnection(dataSource);
		        return getColumnCount(connection, tableName);
			} finally {
				DataSourceUtils.releaseConnection(connection, dataSource);
			}
		}
	}

	public static int getColumnCount(Connection connection, String tableName) throws Exception {
		if (StringUtils.isBlank(tableName)) {
			throw new Exception("Invalid empty tableName for getColumnCount");
		} else {
			Statement stmt = null;
			ResultSet rset = null;
			try {
				stmt = connection.createStatement();
		    	String sql = "SELECT * FROM " + SafeString.getSafeDbTableName(tableName) + " WHERE 1 = 0";
		        rset = stmt.executeQuery(sql);
		        return rset.getMetaData().getColumnCount();
			} finally {
		        closeQuietly(rset);
		        closeQuietly(stmt);
			}
		}
	}

	public static int getTableEntriesCount(Connection connection, String tableName) throws Exception {
		if (StringUtils.isBlank(tableName)) {
			throw new Exception("Invalid empty tableName for getTableEntriesNumber");
		} else {
			Statement stmt = null;
			ResultSet rset = null;
			try {
				stmt = connection.createStatement();
		    	String sql = "SELECT COUNT(*) FROM " + SafeString.getSafeDbTableName(tableName);
		        rset = stmt.executeQuery(sql);
		        if (rset.next()) {
		        	return rset.getInt(1);
		        } else {
		        	return 0;
		        }
			} finally {
		        closeQuietly(rset);
		        closeQuietly(stmt);
			}
		}
	}

	@SuppressWarnings("resource")
	public static boolean containsColumnName(DataSource dataSource, String tableName, String columnName) throws Exception {
		if (dataSource == null) {
			throw new Exception("Invalid empty dataSource for containsColumnName");
		} else if (StringUtils.isBlank(tableName)) {
			throw new Exception("Invalid empty tableName for containsColumnName");
		} else if (StringUtils.isBlank(columnName)) {
			throw new Exception("Invalid empty columnName for containsColumnName");
		} else {
			Connection connection = null;
	    	Statement stmt = null;
	    	ResultSet rset = null;
	    	try {
				connection = DataSourceUtils.getConnection(dataSource);
	    		stmt = connection.createStatement();
	        	String sql = "SELECT * FROM " + SafeString.getSafeDbTableName(tableName) + " WHERE 1 = 0";
	            rset = stmt.executeQuery(sql);
	            for (int columnIndex = 1; columnIndex <= rset.getMetaData().getColumnCount(); columnIndex++) {
	            	if (rset.getMetaData().getColumnName(columnIndex).equalsIgnoreCase(columnName.trim())) {
	            		return true;
	            	}
	            }
	    		return false;
	    	} finally {
	            closeQuietly(rset);
	            closeQuietly(stmt);
				DataSourceUtils.releaseConnection(connection, dataSource);
	    	}
		}
	}

	public static String getColumnDefaultValue(DataSource dataSource, String tableName, String columnName) throws Exception {
		try {
			if (dataSource == null) {
				throw new Exception("Invalid empty dataSource for getDefaultValueOf");
			} else if (StringUtils.isBlank(tableName)) {
				throw new Exception("Invalid empty tableName for getDefaultValueOf");
			} else if (StringUtils.isBlank(columnName)) {
				throw new Exception("Invalid empty columnName for getDefaultValueOf");
			} else {
				if (checkDbVendorIsOracle(dataSource)) {
					String sql = "SELECT data_default FROM user_tab_columns WHERE table_name = ? AND column_name = ?";
					String defaultvalue = new SimpleJdbcTemplate(dataSource).queryForObject(sql, String.class, tableName.toUpperCase(), columnName.toUpperCase());
					if (defaultvalue == null || "null".equalsIgnoreCase(defaultvalue)) {
						return null;
					} else if (defaultvalue.startsWith("'") && defaultvalue.trim().endsWith("'")) {
						// Oracle may append blanks to the trailing apostrophe
						return defaultvalue.substring(1, defaultvalue.trim().length() - 1);
					} else {
						return defaultvalue;
					}
				} else {
					String sql = "SELECT column_default FROM information_schema.columns WHERE table_schema = SCHEMA() AND table_name = ? AND column_name = ?";
			    	return new SimpleJdbcTemplate(dataSource).queryForObject(sql, String.class, tableName, columnName);
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public static String getDateDefaultValue(String fieldDefault) {
		if (fieldDefault.toLowerCase().equals("current_timestamp")
				|| fieldDefault.toLowerCase().startsWith("sysdate")) {
			return "CURRENT_TIMESTAMP";
		} else {
			if (AgnUtils.isOracleDB()) {
				// TODO: A fixed date format is not a good solution, should
				// depend on language setting of the user
				/*
				 * Here raise a problem: The default value is not only used for
				 * the ALTER TABLE statement. It is also stored in
				 * customer_field_tbl.default_value as a string. A problem
				 * occurs, when two users with language settings with different
				 * date formats edit the profile field.
				 */
				return "to_date('" + fieldDefault + "', 'DD.MM.YYYY')";
			} else {
				return "'" + fieldDefault + "'";
			}
		}
	}

	public static boolean addColumnToDbTable(DataSource dataSource, String tablename, String fieldname, String fieldType, int length, String fieldDefault, boolean notNull) throws Exception {
		if (StringUtils.isBlank(fieldname)) {
			return false;
		} else if (!tablename.equalsIgnoreCase(SafeString.getSafeDbTableName(tablename))) {
			logger.error("Cannot create db column: Invalid tablename " + tablename);
			return false;
		} else if (StringUtils.isBlank(fieldname)) {
			return false;
		}  else if (!fieldname.equalsIgnoreCase(SafeString.getSafeDbTableName(fieldname))) {
			logger.error("Cannot create db column: Invalid fieldname " + fieldname);
			return false;
		} else if (StringUtils.isBlank(fieldType)) {
			return false;
		} else if (containsColumnName(dataSource, tablename, fieldname)) {
			return false;
		} else {
			if (fieldType != null) {
				fieldType = fieldType.toUpperCase().trim();
				if (fieldType.startsWith("VARCHAR")) {
					fieldType = "VARCHAR";
				}
			}

			// ColumnType
			int jsqlType = java.sql.Types.class.getDeclaredField(fieldType).getInt(null);
			Dialect dia = AgnUtils.getHibernateDialect();

			String dbType = dia.getTypeName(jsqlType);

			// Bugfix for Oracle: Oracle dialect returns long for varchar
			if (fieldType.equalsIgnoreCase("VARCHAR")) {
				dbType = "VARCHAR";
			}

			/*
			 * Bugfix for mysql: The jdbc-Driver for mysql maps VARCHAR to
			 * longtext. This might be ok in most cases, but longtext doesn't
			 * support length restrictions. So the correct tpye for mysql should
			 * be varchar.
			 */
			if (fieldType.equalsIgnoreCase("VARCHAR") && dbType.equalsIgnoreCase("longtext") && length > 0) {
				dbType = "VARCHAR";
			}

			String addColumnStatement = "ALTER TABLE " + tablename + " ADD (" + fieldname.toLowerCase() + " " + dbType;
			if (fieldType.equalsIgnoreCase("VARCHAR")) {
				if (length <= 0) {
					length = 100;
				}
				addColumnStatement += "(" + length + ")";
			}

			// Default Value
			if (StringUtils.isNotEmpty(fieldDefault)) {
				if (fieldType.equalsIgnoreCase("VARCHAR")) {
					addColumnStatement += " DEFAULT '" + fieldDefault + "'";
				} else if (fieldType.equalsIgnoreCase("DATE")) {
					addColumnStatement += " DEFAULT " + getDateDefaultValue(fieldDefault);
				} else {
					addColumnStatement += " DEFAULT " + fieldDefault;
				}
			}

			// Maybe null
			if (notNull) {
				addColumnStatement += " NOT NULL";
			}

			addColumnStatement += ")";

			try {
				new SimpleJdbcTemplate(dataSource).update(addColumnStatement);
				return true;
			} catch (Exception e) {
				logger.error("Cannot create db column: " + addColumnStatement, e);
				return false;
			}
		}
	}

	public static boolean dropColumnFromDbTable(DataSource dataSource, String tablename, String fieldname) throws Exception {
		if (StringUtils.isBlank(fieldname)) {
			return false;
		} else if (!tablename.equalsIgnoreCase(SafeString.getSafeDbTableName(tablename))) {
			logger.error("Cannot drop db column: Invalid tablename " + tablename);
			return false;
		} else if (StringUtils.isBlank(fieldname)) {
			return false;
		}  else if (!fieldname.equalsIgnoreCase(SafeString.getSafeDbTableName(fieldname))) {
			logger.error("Cannot drop db column: Invalid fieldname " + fieldname);
			return false;
		} else if (!containsColumnName(dataSource, tablename, fieldname)) {
			return false;
		} else {
			String dropColumnStatement = "ALTER TABLE " + tablename + " DROP COLUMN " + fieldname.toLowerCase();

			try {
				new SimpleJdbcTemplate(dataSource).update(dropColumnStatement);
				return true;
			} catch (Exception e) {
				logger.error("Cannot drop db column: " + dropColumnStatement, e);
				return false;
			}
		}
	}

	public static boolean alterColumnDefaultValueInDbTable(DataSource dataSource, String tablename, String fieldname, String fieldDefault, boolean notNull) throws Exception {
		return alterColumnTypeInDbTable(dataSource, tablename, fieldname, null, -1, -1, fieldDefault, notNull);
	}

	public static boolean alterColumnTypeInDbTable(DataSource dataSource, String tablename, String fieldname, String fieldType, int length, int precision, String fieldDefault, boolean notNull) throws Exception {
		if (StringUtils.isBlank(fieldname)) {
			return false;
		} else if (!tablename.equalsIgnoreCase(SafeString.getSafeDbTableName(tablename))) {
			logger.error("Cannot create db column: Invalid tablename " + tablename);
			return false;
		} else if (StringUtils.isBlank(fieldname)) {
			return false;
		}  else if (!fieldname.equalsIgnoreCase(SafeString.getSafeDbTableName(fieldname))) {
			logger.error("Cannot create db column: Invalid fieldname " + fieldname);
			return false;
		} else if (!containsColumnName(dataSource, tablename, fieldname)) {
			return false;
		} else {
			boolean dbChangeIsNeeded = false;
			boolean isDefaultChangeOnly = true;

			// ColumnType
			DbColumnType dbType;
			if (StringUtils.isBlank(fieldType)) {
				dbType = getColumnDataType(dataSource, tablename, fieldname);
			} else {
				String tempFieldType = fieldType.toUpperCase().trim();

				if (tempFieldType.startsWith("VARCHAR")) {
					// Bugfix for Oracle: Oracle dialect returns long for varchar
					// Bugfix for MySQL: The jdbc-Driver for mysql maps VARCHAR to longtext. This might be ok in most cases, but longtext doesn't support length restrictions. So the correct tpye for mysql should be varchar
					dbType = new DbColumnType("VARCHAR", length, -1, -1, !notNull);
				} else {
					int jsqlType = java.sql.Types.class.getDeclaredField(tempFieldType).getInt(null);
					Class<?> cl = Class.forName(AgnUtils.getDefaultValue("jdbc.emmDB.dialect"));
					Dialect dia = (Dialect) cl.getConstructor(new Class[0]).newInstance(new Object[0]);

					dbType = new DbColumnType(dia.getTypeName(jsqlType), length, precision, -1, !notNull);
				}
			}

			String changeColumnStatementPart = fieldname.toLowerCase();

			// Datatype, length (only change when fieldType is set)
			if (StringUtils.isNotEmpty(fieldType)) {
				dbChangeIsNeeded = true;
				isDefaultChangeOnly = false;
				if (dbType.getTypeName().toUpperCase().startsWith("VARCHAR")) {
					// varchar datatype
					changeColumnStatementPart += " " + dbType.getTypeName() + "(" + dbType.getCharacterLength() + ")";
				} else if (dbType.getTypeName().toUpperCase().contains("DATE") || dbType.getTypeName().toUpperCase().contains("TIME")) {
					// date or time type
					changeColumnStatementPart += " " + dbType.getTypeName();
				} else {
					// Numeric datatype
					if (dbType.getNumericScale() > -1) {
						changeColumnStatementPart += " " + dbType.getTypeName() + "(" + dbType.getNumericPrecision() + ", " + dbType.getNumericScale() + ")";
					} else {
						changeColumnStatementPart += " " + dbType.getTypeName() + "(" + dbType.getNumericPrecision() + ")";
					}
				}
			}

			// Default value
			String currentDefaultValue = getColumnDefaultValue(dataSource, tablename, fieldname);
			if ((currentDefaultValue == null && fieldDefault != null) || currentDefaultValue != null && !currentDefaultValue.equals(fieldDefault)) {
				dbChangeIsNeeded = true;
				if (fieldDefault == null || "".equals(fieldDefault)) {
					// null value as default
					changeColumnStatementPart += " DEFAULT NULL";
				} else if (dbType.getTypeName().toUpperCase().startsWith("VARCHAR")) {
					// varchar datatype
					changeColumnStatementPart += " DEFAULT '" + SafeString.getSQLSafeString(fieldDefault) + "'";
				} else if (dbType.getTypeName().toUpperCase().contains("DATE") || dbType.getTypeName().toUpperCase().contains("TIME")) {
					// date or time type
					changeColumnStatementPart += " DEFAULT " + getDateDefaultValue(SafeString.getSQLSafeString(fieldDefault));
				} else {
					// Numeric datatype
					changeColumnStatementPart += " DEFAULT " + SafeString.getSQLSafeString(fieldDefault);
				}
			}

			// Maybe null
			if (dbType.isNullable() == notNull) {
				dbChangeIsNeeded = true;
				isDefaultChangeOnly = false;
				changeColumnStatementPart += " NOT NULL";
			}

			if (dbChangeIsNeeded) {
				String changeColumnStatement;
				if (checkDbVendorIsOracle(dataSource)) {
					changeColumnStatement = "ALTER TABLE " + tablename + " MODIFY (" + changeColumnStatementPart + ")";
				} else {
					if (isDefaultChangeOnly) {
						changeColumnStatement = "ALTER TABLE " + tablename + " ALTER " + changeColumnStatementPart.replaceFirst("DEFAULT", "SET DEFAULT");
					} else {
						changeColumnStatement = "ALTER TABLE " + tablename + " MODIFY " + changeColumnStatementPart;
					}
				}

				try {
					new SimpleJdbcTemplate(dataSource).update(changeColumnStatement);
					return true;
				} catch (Exception e) {
					logger.error("Cannot change db column: " + changeColumnStatement, e);
					return false;
				}
			} else {
				// No change is needed, but everything is OK
				return true;
			}
		}
	}

	public static boolean checkAllowedDefaultValue(String dataType, String defaultValue) {
		if (defaultValue == null) {
			return false;
		} else if ("".equals(defaultValue)) {
			// default value null
			return true;
		} else if (dataType.toUpperCase().contains("DATE") || dataType.toUpperCase().contains("TIME")) {
			if (defaultValue.equalsIgnoreCase("SYSDATE") || defaultValue.equalsIgnoreCase("SYSDATE()") || defaultValue.equalsIgnoreCase("CURRENT_TIMESTAMP")) {
				return true;
			} else {
				try {
					DateUtilities.DD_MM_YYYY.parse(defaultValue);
					return true;
				} catch (ParseException e) {
					return false;
				}
			}
		} else if (dataType.equalsIgnoreCase("NUMBER") || dataType.equalsIgnoreCase("INTEGER") || dataType.equalsIgnoreCase("FLOAT") || dataType.equalsIgnoreCase("DOUBLE")) {
			return new FloatValidator().isValid(defaultValue);
		} else {
			return true;
		}
	}

	public static String getToDateString_Oracle(int day, int month, int year, int hour, int minute, int second) {
		StringBuilder stringBuilder = new StringBuilder("TO_DATE('");
		stringBuilder.append(TWO_DIGIT_FORMAT.format(day));
		stringBuilder.append(".");
		stringBuilder.append(TWO_DIGIT_FORMAT.format(month));
		stringBuilder.append(".");
		stringBuilder.append(FOUR_DIGIT_FORMAT.format(year));
		stringBuilder.append(" ");
		stringBuilder.append(TWO_DIGIT_FORMAT.format(hour));
		stringBuilder.append(":");
		stringBuilder.append(TWO_DIGIT_FORMAT.format(minute));
		stringBuilder.append(":");
		stringBuilder.append(TWO_DIGIT_FORMAT.format(second));
		stringBuilder.append("', 'DD.MM.YYYY HH24:MI:SS')");
		return stringBuilder.toString();
	}

	public static String getToDateString_MySQL(int day, int month, int year, int hour, int minute, int second) {
		StringBuilder stringBuilder = new StringBuilder("STR_TO_DATE('");
		stringBuilder.append(TWO_DIGIT_FORMAT.format(day));
		stringBuilder.append("-");
		stringBuilder.append(TWO_DIGIT_FORMAT.format(month));
		stringBuilder.append("-");
		stringBuilder.append(FOUR_DIGIT_FORMAT.format(year));
		stringBuilder.append(" ");
		stringBuilder.append(TWO_DIGIT_FORMAT.format(hour));
		stringBuilder.append(":");
		stringBuilder.append(TWO_DIGIT_FORMAT.format(minute));
		stringBuilder.append(":");
		stringBuilder.append(TWO_DIGIT_FORMAT.format(second));
		stringBuilder.append("', '%d-%m-%Y %H:%i:%s')");
		return stringBuilder.toString();
	}

	public static int[] getSqlTypes(Object[] values) {
		int[] returnTypes = new int[values.length];
		for (int i = 0; i < values.length; i++) {
			Object value = values[i];
			if (value == null) {
				returnTypes[i] = Types.NULL;
			} else if (value instanceof String) {
				returnTypes[i] = Types.VARCHAR;
			} else if (value instanceof Integer) {
				returnTypes[i] = Types.INTEGER;
			} else if (value instanceof Double) {
				returnTypes[i] = Types.DOUBLE;
			} else if (value instanceof Date || value instanceof Timestamp || value instanceof java.sql.Date) {
				returnTypes[i] = Types.TIMESTAMP;
			} else {
				returnTypes[i] = Types.VARCHAR;
			}
		}
        return returnTypes;
	}

	public static boolean renameTableField(DataSource dataSource, String tableName, String fieldNameOld, String fieldNameNew) {
		String changeColumnStatement;
		if (checkDbVendorIsOracle(dataSource)) {
			changeColumnStatement = "ALTER TABLE " + tableName + " RENAME COLUMN " + fieldNameOld + " TO " + fieldNameNew;
		} else {
			changeColumnStatement = "ALTER TABLE " + tableName + " CHANGE " + fieldNameOld + " " + fieldNameNew;
		}

		try {
			new SimpleJdbcTemplate(dataSource).update(changeColumnStatement);
			return true;
		} catch (Exception e) {
			logger.error("Cannot rename db column: " + changeColumnStatement, e);
			return false;
		}
	}
	
	public static void checkDatasourceConnection(DataSource dataSource) throws Exception {
		if (dataSource == null) {
			throw new Exception("Cannot acquire datasource");
		} else {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
			} catch (Exception e) {
				BeanInfo beanInfo = java.beans.Introspector.getBeanInfo(dataSource.getClass());
				String username = null;
				String password = null;
				String url = null;
				String driverClassName = null;
				for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
					if ("username".equals(propertyDescriptor.getName())) {
						username = (String) propertyDescriptor.getReadMethod().invoke(dataSource);
					} else if ("password".equals(propertyDescriptor.getName())) {
						password = (String) propertyDescriptor.getReadMethod().invoke(dataSource);
					} else if ("url".equals(propertyDescriptor.getName())) {
						url = (String) propertyDescriptor.getReadMethod().invoke(dataSource);
					} else if ("driverClassName".equals(propertyDescriptor.getName())) {
						driverClassName = (String) propertyDescriptor.getReadMethod().invoke(dataSource);
					}
				}
				
				if (StringUtils.isEmpty(username)){
					throw new Exception("Cannot acquire connection: Missing username");
				} else if (StringUtils.isEmpty(password)){
					throw new Exception("Cannot acquire connection: Missing password");
				} else if (StringUtils.isBlank(url)) {
					throw new Exception("Cannot acquire connection: Missing Url");
				} else {
					try {
						Class.forName(driverClassName);
					} catch (Exception e1) {
						throw new Exception("Cannot acquire connection, caused by unknown DriverClassName: " + e1.getMessage(), e1);
					}
					
					try {
						String hostname;
						String portString;
						if (url.toLowerCase().contains("oracle")) {
							String[] urlParts = url.split(":");
							hostname = urlParts[urlParts.length - 3];
							if (hostname.contains("@")) {
								hostname = hostname.substring(hostname.indexOf("@") + 1);
							}
							portString = urlParts[urlParts.length - 2];
						} else {
							String[] urlParts = url.split("/"); // jdbc:mysql://localhost/emm?zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=UTF-8
							hostname = urlParts[urlParts.length - 2];
							if (hostname.contains(":")) {
								portString = hostname.substring(hostname.indexOf(":") + 1);
								hostname = hostname.substring(0, hostname.indexOf(":"));
							} else {
								portString = "3306";
							}
						}
						int port = Integer.parseInt(portString);
						AgnUtils.checkHostConnection(hostname, port);
					} catch (Exception e1) {
						throw new Exception("Cannot acquire connection, caused by Url: " + e1.getMessage(), e1);
					}
					
					throw new Exception("Cannot acquire connection: " + e.getMessage());
				}
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		}
	}
}
