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
package org.agnitas.beans.impl;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.agnitas.beans.ImportProfile;
import org.agnitas.beans.ProfileRecipientFields;
import org.agnitas.emm.springws.endpoint.component.DeleteAttachmentEndpoint;
import org.agnitas.service.csv.Toolkit;
import org.agnitas.service.impl.CSVColumnState;
import org.agnitas.util.ImportUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class ImportKeyColumnsKey {

    public static final String KEY_COLUMN_PREFIX = "key_column_";

    private Map<String, CSVColumnState> keyColumnsMap;
    private Map<String, Object> keyColumnsValues;
    private Map<String, String> rawValues;

    private ImportKeyColumnsKey() {
        keyColumnsMap = new HashMap<String, CSVColumnState>();
        keyColumnsValues = new HashMap<String, Object>();
        rawValues = new HashMap<String, String>();
    }

    public static ImportKeyColumnsKey createInstance(final ImportProfile profile, final ProfileRecipientFields recipientFields, final CSVColumnState[] columns) {
        ImportKeyColumnsKey key = new ImportKeyColumnsKey();
        List<String> keyColumns = profile.getKeyColumns();
        String keyColumn = profile.getKeyColumn();
        for (CSVColumnState column : columns) {
            String colName = column.getColName();
            if (keyColumns.isEmpty() && colName.equals(keyColumn) && column.getImportedColumn()) {
                key.getKeyColumnsMap().put(colName, column);
                String value = Toolkit.getValueFromBean(recipientFields, profile.getKeyColumn());
                value = value.toLowerCase();
                key.getRawValues().put(colName, value);
                key.getKeyColumnsValues().put(colName, formatValue(value, profile, column.getType()));
            } else if (keyColumns.contains(colName) && column.getImportedColumn()) {
                key.getKeyColumnsMap().put(colName, column);
                String value = Toolkit.getValueFromBean(recipientFields, colName);
                value = value.toLowerCase();
                key.getRawValues().put(colName, value);
                key.getKeyColumnsValues().put(colName, formatValue(value, profile, column.getType()));
            }
        }
        return key;
    }

    public static ImportKeyColumnsKey createInstance(Map row) {
        ImportKeyColumnsKey key = new ImportKeyColumnsKey();
        Iterator<String> iterator = row.keySet().iterator();
        while (iterator.hasNext()) {
            String dbColumn = iterator.next();
            if(dbColumn.equalsIgnoreCase("customer_id"))
                continue;
            String columnName = dbColumn.substring(KEY_COLUMN_PREFIX.length()).toLowerCase();
            Object dbValue = row.get(dbColumn) != null ? row.get(dbColumn): "";
            String value = String.valueOf(dbValue).toLowerCase();
            key.getRawValues().put(columnName, value);
            if (dbValue instanceof Date) {
                key.getKeyColumnsValues().put(columnName, dbValue);
            }
            else {
                key.getKeyColumnsValues().put(columnName, value);
            }
        }
        return key;
    }

    private static Object formatValue(String rawValue, ImportProfile profile, int type) {
        Object value = null;
        if (type == CSVColumnState.TYPE_CHAR) {
            value = rawValue;
        } else if (type == CSVColumnState.TYPE_NUMERIC) {
            value = Double.valueOf(rawValue);
        } else {
            value = ImportUtils.getDateAsString(rawValue, profile.getDateFormat());
        }
        return value;
    }

    public Map<String, CSVColumnState> getKeyColumnsMap() {
        return keyColumnsMap;
    }

    public Map<String, Object> getKeyColumnsValues() {
        return keyColumnsValues;
    }

    public Map<String, String> getRawValues() {
        return rawValues;
    }

    public void addParameters(Map<String, List<Object>> parameters) {
        Iterator<String> iterator = keyColumnsValues.keySet().iterator();
        while (iterator.hasNext()) {
            String columnName = iterator.next();
            Object columnValue = keyColumnsValues.get(columnName);
            List<Object> objectList = parameters.get(columnName);
            if(objectList == null){
                objectList = new ArrayList<Object>();
                parameters.put(columnName, objectList);
            }
            objectList.add(columnValue);
        }
    }

    public String getParametersString() {
        return " ?,";
    }

    private String getColumnForValue(Object value) {
        for (Map.Entry<String, Object> entry : keyColumnsValues.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        for (Object value : keyColumnsValues.values()) {
            if (value instanceof Date) {
                hashCodeBuilder.append(value);
            }
            else {
                String rawValue = rawValues.get(getColumnForValue(value));
                hashCodeBuilder.append(rawValue);
            }
        }
        return hashCodeBuilder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (obj.getClass() != getClass())
            return false;

        ImportKeyColumnsKey key = (ImportKeyColumnsKey) obj;
        boolean equals = this.getRawValues().equals(key.getRawValues());

        // we need to check if there are date columns and compare them differently
        if (!equals) {
            for (String columnName : keyColumnsValues.keySet()) {
                // if it's a date column - we need to compare key column values, if not - we compare raw values
                if (keyColumnsValues.get(columnName) instanceof Date && key.getKeyColumnsValues().get(columnName) instanceof Date) {
                    Date datevalue = (Date) keyColumnsValues.get(columnName);
                    Date keyDatevalue = (Date) key.getKeyColumnsValues().get(columnName);
                    if (datevalue.getTime() != keyDatevalue.getTime()) {
                        return false;
                    }
                }
                else if (!rawValues.get(columnName).equals(key.getRawValues().get(columnName))) {
                    return false;
                }
            }
        }
        return true;
    }

}
