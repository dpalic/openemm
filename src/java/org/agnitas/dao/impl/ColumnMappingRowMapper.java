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
 * the code written by AGNITAS AG are Copyright (c) 2009 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/

package org.agnitas.dao.impl;

import org.agnitas.beans.ColumnMapping;
import org.agnitas.beans.impl.ColumnMappingImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class for mapping import_column_mapping_tbl table row to ColumnMapping bean
 *
 * @author Vyacheslav Stepanov
 */
public class ColumnMappingRowMapper implements RowMapper {

    /**
     * Maps import_column_mapping_tbl table row to ColumnMapping bean
     *
     * @param resultSet result set for table row
     * @param line      line number
     * @return ColumnMapping created from table row
     * @throws SQLException
     */
    public Object mapRow(ResultSet resultSet, int line) throws SQLException {
        ColumnMapping mapping = new ColumnMappingImpl();
        mapping.setId((int) resultSet.getLong("id"));
        mapping.setProfileId((int) resultSet.getLong("profile_id"));
        mapping.setMandatory(resultSet.getBoolean("mandatory"));
        mapping.setDatabaseColumn(resultSet.getString("db_column"));
        mapping.setFileColumn(resultSet.getString("file_column"));
        final String defaultValue = resultSet.getString("default_value");
        if (!StringUtils.isEmpty(defaultValue)) {
            mapping.setDefaultValue(defaultValue);
        }
        return mapping;
	}

}