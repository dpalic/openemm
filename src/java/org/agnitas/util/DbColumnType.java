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

import org.apache.commons.lang.StringUtils;

public class DbColumnType {
	private String typeName;
	private int characterLength; // only for VARCHAR and VARCHAR2 types
	private int numericPrecision; // only for numeric types
	private int numericScale; // only for numeric types
	private boolean nullable;

	public enum SimpleDataType {
		Characters,
		Numeric,
		Date
	}

	public DbColumnType(String typeName, int characterLength, int numericPrecision, int numericScale, boolean nullable) {
		this.typeName = typeName;
		this.characterLength = characterLength;
		this.numericPrecision = numericPrecision;
		this.numericScale = numericScale;
		this.nullable = nullable;
	}

	public String getTypeName() {
		return typeName;
	}

	public int getCharacterLength() {
		return characterLength;
	}

	public int getNumericPrecision() {
		return numericPrecision;
	}

	public int getNumericScale() {
		return numericScale;
	}

	public boolean isNullable() {
		return nullable;
	}

	public SimpleDataType getSimpleDataType() {
		if (typeName.toUpperCase().startsWith("VARCHAR") || typeName.toUpperCase().startsWith("CHAR")) {
			return SimpleDataType.Characters;
		} else if (typeName.toUpperCase().contains("DATE") || typeName.toUpperCase().contains("TIME")) {
			return SimpleDataType.Date;
		} else {
			return SimpleDataType.Numeric;
		}
	}

	/**
	 * Return a simple dbFieldType which can be interpreted by a bean:message-Tag
	 * @param typeName
	 * @return
	 */
	public static String dbType2String(String typeName) {
		if (StringUtils.isBlank(typeName)) {
			return null;
		} else if (typeName.equalsIgnoreCase("BIGINT")
				|| typeName.equalsIgnoreCase("INT")
				|| typeName.equalsIgnoreCase("INTEGER")
				|| typeName.equalsIgnoreCase("NUMBER")
				|| typeName.equalsIgnoreCase("SMALLINT")) {
			return "INTEGER";
		} else if (typeName.equalsIgnoreCase("DECIMAL")
				|| typeName.equalsIgnoreCase("DOUBLE")
				|| typeName.equalsIgnoreCase("FLOAT")
				|| typeName.equalsIgnoreCase("NUMERIC")
				|| typeName.equalsIgnoreCase("REAL")) {
			return "DOUBLE";
		} else if (typeName.equalsIgnoreCase("CHAR")) {
			return "CHAR";
		} else if (typeName.equalsIgnoreCase("VARCHAR")
				|| typeName.equalsIgnoreCase("VARCHAR2")
				|| typeName.equalsIgnoreCase("LONGVARCHAR")
				|| typeName.equalsIgnoreCase("CLOB")) {
			return "VARCHAR";
		} else if (typeName.equalsIgnoreCase("DATE")
				|| typeName.equalsIgnoreCase("TIMESTAMP")
				|| typeName.equalsIgnoreCase("TIME")) {
			return "DATE";
		} else {
			return "UNKNOWN(" + typeName + ")";
		}
	}

	/**
	 * Read a bean:message-Tag text to a db data type
	 * @param typeName
	 * @return
	 */
	public static String string2DbType(String beanTypeString) throws Exception {
		if (StringUtils.isBlank(beanTypeString)) {
			throw new Exception("Invalid dbtype");
		}

		beanTypeString = beanTypeString.toUpperCase();
		if (beanTypeString.startsWith("FIELDTYPE.")) {
			beanTypeString = beanTypeString.substring(10);
		}

		switch (beanTypeString) {
			case "INTEGER":
				return "NUMBER";
			case "DOUBLE":
				return "DOUBLE";
			case "CHAR":
				return "CHAR";
			case "VARCHAR":
				return "VARCHAR";
			case "STRING":
				return "VARCHAR";
			case "DATE":
				return "TIMESTAMP";
			default:
				throw new Exception("Invalid dbtype");
		}
	}
}
