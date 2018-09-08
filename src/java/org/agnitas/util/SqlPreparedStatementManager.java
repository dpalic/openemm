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
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class SqlPreparedStatementManager {
	private static final String SQL_WHERE_SIGN = "WHERE";
	private static final String SQL_OPERATOR_AND = "AND";
	private static final String SQL_OPERATOR_OR = "OR";

	private StringBuilder statement = new StringBuilder();
	private List<Object> statementParameters = new ArrayList<Object>();
	private boolean hasAppendedClauses = false;

	public SqlPreparedStatementManager(String baseStatement) {
        this(baseStatement, null);
	}

	public SqlPreparedStatementManager(String baseStatement,  Object... parameter) {
		String baseStatementString = baseStatement.trim();
		if (baseStatementString.toLowerCase().endsWith(" " + SQL_WHERE_SIGN.toLowerCase())) {
			statement.append(baseStatementString.substring(0, baseStatementString.length() - SQL_WHERE_SIGN.length() - 1).trim());
		} else {
			statement.append(baseStatementString.trim());
		}

        if (parameter != null) {
            for (Object item : parameter) {
                statementParameters.add(item);
            }
        }
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

		if (hasAppendedClauses) {
			statement.append(" ");
			statement.append(concatenateByOr ? SQL_OPERATOR_OR : SQL_OPERATOR_AND);
		} else {
			statement.append(" ");
			statement.append(SQL_WHERE_SIGN);
		}

		statement.append(" (");
		statement.append(whereClause.trim());
		statement.append(")");
		if (parameter != null) {
			for (Object item : parameter) {
				statementParameters.add(item);
			}
		}

		hasAppendedClauses = true;
	}

    /**
     * Append a limit clause to the statement.
     *
     * @param limitClause
     * @param parameter
     * @throws Exception
     */
    public void appendLimitClause(String limitClause, Object... parameter)  throws Exception {
        if (StringUtils.isBlank(limitClause)) {
            throw new Exception("Invalid empty where clause");
        }

        int numberOfQuestionMarks = StringUtils.countMatches(limitClause, "?");
        if ((parameter == null && numberOfQuestionMarks != 0) || (numberOfQuestionMarks != parameter.length)) {
            throw new Exception("Invalid number of parameters in where clause");
        }

        statement.append(" ");
        statement.append(limitClause.trim());

        if (parameter != null) {
            for (Object item : parameter) {
                statementParameters.add(item);
            }
        }
    }

	public void finalizeStatement(String statementTail) {
		statement.append(" ");
		statement.append(statementTail.trim());
	}

	public boolean hasAppendedWhereClauses() {
		return hasAppendedClauses;
	}

	public void setHasAppendedClauses(boolean hasAppendedClauses) {
		this.hasAppendedClauses = hasAppendedClauses;
	}

	public String getPreparedSqlString() {
		return statement.toString();
	}

	public void appendToPreparedSqlString(String append) {
		statement.append(append);
	}

	public Object[] getPreparedSqlParameters() {
		if (statementParameters.size() > 0) {
			return statementParameters.toArray();
		} else {
			return null;
		}
	}
}
