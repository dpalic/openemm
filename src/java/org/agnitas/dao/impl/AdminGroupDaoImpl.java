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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.agnitas.beans.AdminGroup;
import org.agnitas.beans.impl.AdminGroupImpl;
import org.agnitas.dao.AdminGroupDao;
import org.agnitas.emm.core.velocity.VelocityCheck;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

/**
 * @author mhe
 */
public class AdminGroupDaoImpl extends BaseDaoImpl implements AdminGroupDao {
	private static final transient Logger logger = Logger.getLogger(AdminGroupDaoImpl.class);
	
	protected static final String TABLE = "admin_group_tbl";
	protected static final String TABLE_PERMISSIONS = "admin_group_permission_tbl";
	
	protected static final String FIELD_ID = "admin_group_id";
	protected static final String FIELD_COMPANY_ID = "company_id";
	protected static final String FIELD_SHORTNAME = "shortname";
	protected static final String FIELD_DESCRIPTION = "description";
	
	protected static final String[] FIELD_NAMES = new String[]{FIELD_ID, FIELD_COMPANY_ID, FIELD_SHORTNAME, FIELD_DESCRIPTION};
	
	protected static final String SELECT = "SELECT " + StringUtils.join(FIELD_NAMES, ", ") + " FROM " + TABLE + " WHERE " + FIELD_ID + " = ?";
	protected static final String SELECT_BY_COMPANYID = "SELECT " + StringUtils.join(FIELD_NAMES, ", ") + " FROM " + TABLE + " WHERE " + FIELD_COMPANY_ID + " = ?";
	
	protected static final String SELECT_PERMISSIONS_BY_GROUPID = "SELECT security_token FROM " + TABLE_PERMISSIONS + " WHERE admin_group_id = ?";
	
	@Override
	public AdminGroup getAdminGroup(int groupID) {
		try {
			List<AdminGroup> groups = select(logger, SELECT, new AdminGroup_RowMapper(), groupID);
			if (groups.size() > 0) {
				return groups.get(0);
			} else {
				// No Group found
				return null;
			}
		} catch (DataAccessException e) {
			// No Group found
			return null;
		}
	}

	@Override
	public List<AdminGroup> getAdminGroupsByCompanyId(@VelocityCheck int companyID) {
		if (logger.isDebugEnabled()) logger.debug("stmt:" + SELECT_BY_COMPANYID);
		List<AdminGroup> groupList = getSimpleJdbcTemplate().query(SELECT_BY_COMPANYID, new AdminGroup_RowMapper(), companyID);
		return groupList;
    } 

	public class AdminGroup_RowMapper implements ParameterizedRowMapper<AdminGroup> {
		@Override
		public AdminGroup mapRow(ResultSet resultSet, int row) throws SQLException {
			AdminGroup group = new AdminGroupImpl();
			
			group.setGroupID((resultSet.getInt(FIELD_ID)));
			group.setCompanyID((resultSet.getInt(FIELD_COMPANY_ID)));
			group.setShortname(resultSet.getString(FIELD_SHORTNAME));
			group.setDescription(resultSet.getString(FIELD_DESCRIPTION));
			
			// Read additional data
			
			if (logger.isDebugEnabled()) logger.debug("stmt:" + SELECT_PERMISSIONS_BY_GROUPID);
			List<Map<String, Object>> result = getSimpleJdbcTemplate().queryForList(SELECT_PERMISSIONS_BY_GROUPID, group.getGroupID());
			Set<String> groupPermissions = new HashSet<String>();
			for (Map<String, Object> resultRow : result) {
				groupPermissions.add((String) resultRow.get("security_token"));
			}
			group.setGroupPermissions(groupPermissions);
			
			return group;
		}
	}
}
