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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.agnitas.beans.TagDefinition;
import org.agnitas.dao.TagDao;
import org.agnitas.dao.impl.mapper.StringRowMapper;
import org.agnitas.emm.core.velocity.VelocityCheck;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

/**
 * @author aso
 */
public class TagDaoImpl extends BaseDaoImpl implements TagDao {
	/** The logger. */
	private static final transient Logger logger = Logger.getLogger(TagDaoImpl.class);

	@Override
    public TagDefinition getTag(@VelocityCheck int companyID, String name) {
		return selectObjectDefaultNull(logger, "SELECT tagname, type, selectvalue FROM tag_tbl WHERE company_id IN (0, ?) AND tagname = ? ORDER BY tagname", new TagRowMapper(), companyID, name);
	}
	
	@Override
    public List<String> getTagNames(@VelocityCheck int companyID) {
		return select(logger, "SELECT tagname FROM tag_tbl WHERE company_id IN (0, ?) ORDER BY tagname", new StringRowMapper(), companyID);
	}
	
	@Override
    public List<TagDefinition> getTagDefinitions(@VelocityCheck int companyID) {
		return select(logger, "SELECT tagname, type, selectvalue FROM tag_tbl WHERE company_id IN (0, ?) ORDER BY tagname", new TagRowMapper(), companyID);
	}
	
	@Override
    public Map<String, TagDefinition> getTagDefinitionsMap(@VelocityCheck int companyID) {
		List<TagDefinition> tagDefinitions = getTagDefinitions(companyID);
		Map<String, TagDefinition> returnMap = new HashMap<String, TagDefinition>();
		for (TagDefinition tagDefinition : tagDefinitions) {
			returnMap.put(tagDefinition.getName(), tagDefinition);
		}
		return returnMap;
	}
	
    protected class TagRowMapper implements ParameterizedRowMapper<TagDefinition> {
		@Override
		public TagDefinition mapRow(ResultSet resultSet, int row) throws SQLException {
			TagDefinition readObject = new TagDefinition();

			readObject.setName(resultSet.getString("tagname"));
			try {
				readObject.setTypeString(resultSet.getString("type"));
			} catch (Exception e) {
				throw new SQLException("Error in TagDefinitionType", e);
			}
			readObject.setSelectValue(resultSet.getString("selectvalue"));
			
			return readObject;
		}
	}
}