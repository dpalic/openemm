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

public class SqlPreparedInsertStatementManager {
	private String baseStatement = null;
	private List<String> insertFieldNames = new ArrayList<String>();
	private List<Object> insertValues = new ArrayList<Object>();
	private List<Boolean> specialSqlValueMarkers = new ArrayList<Boolean>();

	public SqlPreparedInsertStatementManager(String baseStatement) throws Exception {
		if (baseStatement == null || !baseStatement.toLowerCase().startsWith("insert into ")) {
			throw new Exception("Invalid baseStatement for insert statement");
		}

		this.baseStatement = baseStatement.trim();
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
		insertFieldNames.add(fieldName);
		insertValues.add(value);
		specialSqlValueMarkers.add(isSpecialSqlValue);
	}

	public String getPreparedSqlString() {
		StringBuilder statement = new StringBuilder(baseStatement);
		statement.append(" (" + StringUtils.join(insertFieldNames, ", ") + ") VALUES (");

		for (int i = 0; i < specialSqlValueMarkers.size(); i++) {
			if (i > 0) {
				statement.append(", ");
			}

			if (insertValues.get(i) == null) {
				statement.append("null");
			} else if (specialSqlValueMarkers.get(i)) {
				statement.append(insertValues.get(i));
			} else {
				statement.append("?");
			}
		}

		statement.append(")");

		return statement.toString();
	}

	public String getPreparedInsertHead() {
		StringBuilder statement = new StringBuilder(baseStatement);
		statement.append(" (" + StringUtils.join(insertFieldNames, ", ") + ") ");
		return statement.toString();
	}

	public String getPreparedParameters() {
		StringBuilder statement = new StringBuilder();
		for (int i = 0; i < specialSqlValueMarkers.size(); i++) {
			if (i > 0) {
				statement.append(", ");
			}

			if (insertValues.get(i) == null) {
				statement.append("null");
			} else if (specialSqlValueMarkers.get(i)) {
				statement.append(insertValues.get(i));
			} else {
				Object val = insertValues.get(i);
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

		return statement.toString();
	}

	public String getReadySqlString() {
		StringBuilder statement = new StringBuilder(baseStatement);
		statement.append(" (" + StringUtils.join(insertFieldNames, ", ") + ") VALUES (");

		for (int i = 0; i < specialSqlValueMarkers.size(); i++) {
			if (i > 0) {
				statement.append(", ");
			}

			if (insertValues.get(i) == null) {
				statement.append("null");
			} else if (specialSqlValueMarkers.get(i)) {
				statement.append(insertValues.get(i));
			} else {
				Object val = insertValues.get(i);
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

		statement.append(")");

		return statement.toString();
	}

	public String getReadyUpdateString(String head, int customerID) {
		StringBuilder statement = new StringBuilder(head);

		for (int i = 0; i < specialSqlValueMarkers.size(); i++) {
			if (i > 0) {
				statement.append(", ");
			}
			statement.append(insertFieldNames.get(i) + "=");

			if (insertValues.get(i) == null) {
				statement.append("null");
			} else if (specialSqlValueMarkers.get(i)) {
				statement.append(insertValues.get(i));
			} else {
				Object val = insertValues.get(i);
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

		statement.append(" WHERE customer_id="+customerID);

		return statement.toString();
	}

	public Object[] getPreparedSqlParameters() {
		List<Object> preparedSqlParameters = new ArrayList<Object>();
		for (int i = 0; i < specialSqlValueMarkers.size(); i++) {
			if (!specialSqlValueMarkers.get(i) && insertValues.get(i) != null) {
				preparedSqlParameters.add(insertValues.get(i));
			}
		}

		if (preparedSqlParameters.size() > 0) {
			return preparedSqlParameters.toArray();
		} else {
			return null;
		}
	}
}
