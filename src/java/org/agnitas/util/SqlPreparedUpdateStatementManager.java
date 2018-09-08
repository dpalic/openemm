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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class SqlPreparedUpdateStatementManager {
	private static final String SQL_SET_SIGN = "SET";
	private static final String SQL_WHERE_SIGN = "WHERE";
	private static final String SQL_OPERATOR_AND = "AND";
	private static final String SQL_OPERATOR_OR = "OR";

	private String baseStatement = null;
	private List<String> updateFieldNames = new ArrayList<String>();
	private List<Object> updateValues = new ArrayList<Object>();
	private List<Boolean> specialSqlValueMarkers = new ArrayList<Boolean>();
	private StringBuilder whereClauseBuilder = new StringBuilder("");
	private List<Object> whereClauseValues = new ArrayList<Object>();

	public SqlPreparedUpdateStatementManager(String baseStatement) throws Exception {
		if (baseStatement == null || !baseStatement.toLowerCase().startsWith("update ")) {
			throw new Exception("Invalid baseStatement for update statement");
		}

		if (baseStatement.toLowerCase().trim().endsWith(" " + SQL_SET_SIGN.toLowerCase())) {
			this.baseStatement = baseStatement.substring(0, baseStatement.length() - SQL_SET_SIGN.length() - 1).trim();
		} else {
			this.baseStatement = baseStatement.trim();
		}

		this.baseStatement += " " + SQL_SET_SIGN + " ";
	}

	/**
	 * Add a Value that will be used as a preparedStatement value (uses ?)
	 * @param fieldName
	 * @param value
	 */
	public void addValue(String fieldName, Object value) {
		addValue(fieldName, value, false);
	}

	/**
	 * Add a Value to the insert statement
	 * @param fieldName
	 * @param value
	 * @param isSpecialSqlValue
	 * 							if set that value will not be used as a preparedStatement value (no encapsulation by single quote, no use of ?)
	 * 							if NOT set that value will used as a normal preparedStatement value (uses ?)
	 */
	public void addValue(String fieldName, Object value, boolean isSpecialSqlValue) {
		updateFieldNames.add(fieldName);
		updateValues.add(value);
		specialSqlValueMarkers.add(isSpecialSqlValue);
	}

	/**
	 * Append a where clause to the statement concatenated by " AND "
	 *
	 * @param whereClause
	 * @param parameter
	 * @throws Exception
	 */
	public void addWhereClause(String whereClause, Object... parameter) throws Exception {
		addWhereClause(false, whereClause, parameter);
	}

	/**
	 * Append a where clause to the statement.
	 *
	 * @param concatenateByOr type of concatenation (false = AND / true = OR)
	 * @param whereClause
	 * @param parameter
	 * @throws Exception
	 */
	public void addWhereClause(boolean concatenateByOr, String whereClause, Object... parameter) throws Exception {
		if (StringUtils.isBlank(whereClause)) {
			throw new Exception("Invalid empty where clause");
		}

		int numberOfQuestionMarks = StringUtils.countMatches(whereClause, "?");
		if ((parameter == null && numberOfQuestionMarks != 0) || (numberOfQuestionMarks != parameter.length)) {
			throw new Exception("Invalid number of parameters in where clause");
		}

		if (whereClauseBuilder.length() > 0) {
			whereClauseBuilder.append(" ");
			whereClauseBuilder.append(concatenateByOr ? SQL_OPERATOR_OR : SQL_OPERATOR_AND);
		} else {
			whereClauseBuilder.append(" ");
			whereClauseBuilder.append(SQL_WHERE_SIGN);
		}

		whereClauseBuilder.append(" (");
		whereClauseBuilder.append(whereClause.trim());
		whereClauseBuilder.append(")");
		if (parameter != null) {
			for (Object item : parameter) {
				whereClauseValues.add(item);
			}
		}
	}

	public String getPreparedSqlString() {
		StringBuilder statement = new StringBuilder(baseStatement);

		for (int i = 0; i < specialSqlValueMarkers.size(); i++) {
			if (i > 0) {
				statement.append(", ");
			}

			statement.append(updateFieldNames.get(i));
			statement.append(" = ");

			if (updateValues.get(i) == null) {
				statement.append("null");
			} else if (specialSqlValueMarkers.get(i)) {
				statement.append(updateValues.get(i));
			} else {
				statement.append("?");
			}
		}

		statement.append(whereClauseBuilder.toString());

		return statement.toString();
	}

	public String getReadyUpdateString(int customerID) {
		StringBuilder statement = new StringBuilder(baseStatement);

		for (int i = 0; i < specialSqlValueMarkers.size(); i++) {
			if (i > 0) {
				statement.append(", ");
			}

			statement.append(updateFieldNames.get(i));
			statement.append(" = ");

			if (updateValues.get(i) == null) {
				statement.append("null");
			} else if (specialSqlValueMarkers.get(i)) {
				statement.append(updateValues.get(i));
			} else {
				Object val = updateValues.get(i);
				if (val.getClass() == String.class) {
					statement.append("\'");
					statement.append(val);
					statement.append("\'");
				} else if (val.getClass() == Date.class) {
					statement.append(val);
				} else {
					statement.append(val);
				}
			}
		}

		statement.append(" WHERE customer_id=" + customerID);

		return statement.toString();
	}

	public Object[] getPreparedSqlParameters() {
		List<Object> preparedSqlParameters = new ArrayList<Object>();
		for (int i = 0; i < specialSqlValueMarkers.size(); i++) {
			if (!specialSqlValueMarkers.get(i) && updateValues.get(i) != null) {
				preparedSqlParameters.add(updateValues.get(i));
			}
		}

		for (Object whereClauseValue : whereClauseValues) {
			preparedSqlParameters.add(whereClauseValue);
		}

		if (preparedSqlParameters.size() > 0) {
			return preparedSqlParameters.toArray();
		} else {
			return null;
		}
	}
}
