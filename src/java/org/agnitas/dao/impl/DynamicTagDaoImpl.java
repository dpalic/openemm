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
import java.util.List;

import org.agnitas.beans.DynamicTag;
import org.agnitas.beans.impl.DynamicTagImpl;
import org.agnitas.dao.DynamicTagDao;
import org.agnitas.emm.core.velocity.VelocityCheck;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

/**
 * @author Andreas Rehak
 */
public class DynamicTagDaoImpl extends BaseDaoImpl implements DynamicTagDao {
	private static final transient Logger logger = Logger.getLogger(DynamicTagDaoImpl.class);

	@Override
	public int getIdForName(int mailingID, String name) {
		try {
			return selectInt(logger, "SELECT dyn_name_id FROM dyn_name_tbl WHERE mailing_id = ? AND dyn_name = ?", mailingID, name);
		} catch (Exception e) {
			logger.error("Error getting ID for tag: " + name, e);

			return 0;
		}
	}

	@Override
	public List<DynamicTag> getNameList(@VelocityCheck int companyId, int mailingId) {
		return select(logger, "SELECT dyn_name_id, dyn_name FROM dyn_name_tbl WHERE mailing_id = ? AND company_id = ? AND deleted = 0", new DynamicTag_RowMapper(), mailingId,
				companyId);
	}

	private class DynamicTag_RowMapper implements ParameterizedRowMapper<DynamicTag> {
		@Override
		public DynamicTag mapRow(ResultSet resultSet, int row) throws SQLException {
			DynamicTag dynamicTag = new DynamicTagImpl();

			dynamicTag.setId(resultSet.getInt("dyn_name_id"));
			dynamicTag.setDynName(resultSet.getString("dyn_name"));

			return dynamicTag;
		}
	}
}
