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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.agnitas.beans.BindingEntry;
import org.agnitas.beans.BlackListEntry;
import org.agnitas.beans.Mailinglist;
import org.agnitas.beans.impl.BlackListEntryImpl;
import org.agnitas.beans.impl.PaginatedListImpl;
import org.agnitas.dao.BlacklistDao;
import org.agnitas.dao.impl.mapper.MailinglistRowMapper;
import org.agnitas.emm.core.velocity.VelocityCheck;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.DbUtilities;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

/**
 * @author Andreas Rehak
 */
public class BlacklistDaoImpl extends BaseDaoImpl implements BlacklistDao {
	/** The logger. */
	private static final transient Logger logger = Logger.getLogger( BlacklistDaoImpl.class);
	
	@Override
	public boolean insert( @VelocityCheck int companyID, String email) {
		if (StringUtils.isBlank(email)) {
			return false;
		} else {
			String sql = "INSERT into cust_ban_tbl (company_id, email) VALUES (?, ?)";
			if (update(logger, sql, companyID, AgnUtils.normalizeEmail(email)) != 1) {
				return false;
			} else {
				return true;
			}
		}
	}

	@Override
	public boolean delete( @VelocityCheck int companyID, String email) {
		if (StringUtils.isBlank(email)) {
			return false;
		} else {
			String sql = "DELETE FROM cust_ban_tbl WHERE company_id = ? AND email = ?";
			if (update(logger, sql, companyID, AgnUtils.normalizeEmail(email)) != 1) {
				return false;
			} else {
				return true;
			}
		}
	}

    @Override
    public List<BlackListEntry> getBlacklistedRecipients( @VelocityCheck int companyID) {
        String blackListQuery = "SELECT email, timestamp AS creation_date FROM cust" + companyID + "_ban_tbl ORDER BY email";
        return select(logger, blackListQuery, new BlackListEntry_RowMapper());
    }

    @Override
	public PaginatedListImpl<BlackListEntry> getBlacklistedRecipients( @VelocityCheck int companyID, String sort, String direction, int page, int rownums) {
		if (StringUtils.isEmpty(sort)) {
			sort = "email";
		} else if ("date".equalsIgnoreCase(sort)) {
			// BUG-FIX: sortName in display-tag has no effect
			sort = "creation_date";
		}
		
		String sortClause;
		try {
    		sortClause = " ORDER BY " + sort;
			if (StringUtils.isNotBlank(direction)) {
				sortClause = sortClause + " " + direction;
			}
		} catch (Exception e) {
			logger.error("Invalid sort field", e);
			sortClause = "";
		}

		int totalRows;
		try {
			String totalRowsQuery = "SELECT COUNT(email) FROM cust_ban_tbl WHERE company_id = ? ";
			totalRows = selectInt(logger, totalRowsQuery, companyID);
		} catch (Exception e) {
			totalRows = 0;
		}
        page = AgnUtils.getValidPageNumber(totalRows, page, rownums);

		int offset = (page - 1) * rownums;
		String blackListQuery = "SELECT email, creation_date FROM cust_ban_tbl WHERE company_id = ?" + sortClause + " LIMIT ?, ? ";
		List<BlackListEntry> blacklistElements = select(logger, blackListQuery, new BlackListEntry_RowMapper(), companyID, offset, rownums);
		return new PaginatedListImpl<BlackListEntry>(blacklistElements, totalRows, rownums, page, sort, direction);
	}

	public class BlackListEntry_RowMapper implements ParameterizedRowMapper<BlackListEntry> {	
		@Override
		public BlackListEntry mapRow(ResultSet resultSet, int row) throws SQLException {
			String email = resultSet.getString("email");
			Date creationDate = resultSet.getTimestamp("creation_date");
			BlackListEntry entry = new BlackListEntryImpl(email, creationDate);
			return entry;
		}
	}

	@Override
	public boolean exist( @VelocityCheck int companyID, String email) {
		try {
			String sql = "SELECT count(*) FROM cust_ban_tbl WHERE company_id = ? and email = ?";
			int resultCount = selectInt(logger, sql, companyID, AgnUtils.normalizeEmail(email));
			return resultCount > 0;
		} catch (DataAccessException e) {
			return false;
		}
	}

	@Override
	public List<String> getBlacklist( @VelocityCheck int companyID) {
		String blackListQuery = "SELECT email FROM cust_ban_tbl WHERE company_id = ?";
		List<Map<String, Object>> results = select(logger, blackListQuery, companyID);
		List<String> blacklistElements = new ArrayList<String>();
		for (Map<String, Object> row : results) {
			blacklistElements.add((String) row.get("email"));
		}
		return blacklistElements;
	}

	@Override
	public List<Mailinglist> getMailinglistsWithBlacklistedBindings( @VelocityCheck int companyId, String email) {
		String query = "SELECT DISTINCT m.mailinglist_id, m.company_id, m.shortname, m.description FROM mailinglist_tbl m, customer_" + companyId + "_tbl c, customer_" + companyId + "_binding_tbl b" +
				" WHERE c.email=? AND b.customer_id = c.customer_id AND b.user_status = ? AND b.mailinglist_id = m.mailinglist_id AND m.deleted=0 AND m.company_id = ?";

		MailinglistRowMapper rm = new MailinglistRowMapper();
		
		List<Mailinglist> list = select(logger, query, rm, email, BindingEntry.USER_STATUS_BLACKLIST, companyId);
		
		return list;
	}

	@Override
	public void updateBlacklistedBindings( @VelocityCheck int companyId, String email, List<Integer> mailinglistIds, int userStatus) {
		if( mailinglistIds.size() == 0) {
			if( logger.isInfoEnabled())
				logger.info( "List of mailinglist IDs is empty - doing nothing");
			
			return;
		}
		
		String update = 
				"UPDATE customer_" + companyId + "_binding_tbl" +
				" SET user_status=?, " + getBindingChangeDateColumnName() + " = CURRENT_TIMESTAMP" +
				" WHERE customer_id IN (SELECT customer_id FROM customer_" + companyId + "_tbl WHERE email=?)" +
				" AND user_status=? AND mailinglist_id=?";
		
		for( int mailinglistId : mailinglistIds) {
			if( logger.isDebugEnabled())
				logger.debug( email + ": updating user status for mailinglist " + mailinglistId);
			
			update(logger, update, userStatus, email, BindingEntry.USER_STATUS_BLACKLIST, mailinglistId);
		}
	}
	
	/**
	 * Returns the name of the column in the binding table holding the change date.
	 * This method is required due to differences in the database structure.
	 * 
	 * @return &quot;change_date&quot;
	 */
	protected String getBindingChangeDateColumnName() {
		return "change_date";
	}
}
