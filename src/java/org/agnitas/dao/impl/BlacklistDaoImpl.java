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
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/

package org.agnitas.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.agnitas.beans.BlackListEntry;
import org.agnitas.beans.impl.BlackListEntryImpl;
import org.agnitas.beans.impl.BlacklistPaginatedList;
import org.agnitas.dao.BlacklistDao;
import org.agnitas.util.SafeString;
import org.apache.commons.lang.StringUtils;
import org.displaytag.pagination.PaginatedList;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Andreas Rehak
 */
public class BlacklistDaoImpl implements BlacklistDao {

	public boolean	insert(int companyID, String email)	{
		JdbcTemplate jdbc = new JdbcTemplate(getDataSource());
		String sql="insert into cust_ban_tbl (company_id, email) valueS (?, ?)";

		if(SafeString.isBlank(email)) {
			return false;
		}
                                
		email=SafeString.getSQLSafeString(email.toLowerCase().trim());
		if(jdbc.update(sql, new Object[]{new Integer(companyID), email }) != 1) {
			return false;
		}
		return true;
	}

	protected DataSource getDataSource() {
		return (DataSource) applicationContext.getBean("dataSource");
	}

	public boolean	delete(int companyID, String email)	{
		JdbcTemplate jdbc = new JdbcTemplate(getDataSource());
		String sql="delete from cust_ban_tbl where company_id=? and email=?";

		if(SafeString.isBlank(email)) {
			return false;
		}
		if(jdbc.update(sql, new Object[]{new Integer(companyID), email }) != 1) {
			return false;
		}
		return true;
	}

	/**
	 * Holds value of property applicationContext.
	 */
	protected ApplicationContext applicationContext;
    
	

	public PaginatedList getBlacklistedRecipients(int companyID, String sort, String direction, int page, int rownums ) {
		
		if(StringUtils.isEmpty(sort)) {
			sort = "email";
		}
		
		if(StringUtils.isEmpty(direction)) {
			direction = "asc";
		}
		
		// BUG-FIX: sortName in display-tag has no effect
		String sqlSortParameter = sort;
		if("date".equalsIgnoreCase(sort)) {
			sqlSortParameter = "creation_date";
		}
		
		
		String totalRowsQuery = "select count(email) from cust_ban_tbl where company_id = ? ";
		JdbcTemplate templateForTotalRows = new JdbcTemplate(getDataSource());
		
		int  totalRows = -1;
		try {
			totalRows = templateForTotalRows.queryForInt(totalRowsQuery,new Object[]{ companyID} );			
		} catch(Exception e ) {
			totalRows = 0; // query for int has a bug , it doesn't return '0' in case of nothing is found...
		}
		
		
		int offset =  ( page - 1) * rownums; 
		String blackListQuery = "SELECT email, creation_date FROM cust_ban_tbl " +
				"						WHERE company_id= ? ORDER BY "+sqlSortParameter+" "+direction+"  LIMIT ? , ? ";
		JdbcTemplate templateForBlackList = new JdbcTemplate(getDataSource());
		List<Map> blacklistElements = templateForBlackList.queryForList(blackListQuery,new Object[]{companyID,offset ,rownums}); 		
		return new BlacklistPaginatedList(toBlacklistList(blacklistElements),totalRows,page, rownums,sort, direction );
	}
	
	/**
	 * Setter for property applicationContext.
	 * @param applicationContext New value of property applicationContext.
	 */
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	protected List<BlackListEntry> toBlacklistList(List<Map> queryResult) {
		List<BlackListEntry> blackListEntryList = new ArrayList<BlackListEntry>();
		for(Map row:queryResult) {
			String email =  (String) row.get("email");
			Date creationDate = (Date) row.get("creation_date");
			BlackListEntry entry = new BlackListEntryImpl( email , creationDate);
			blackListEntryList.add(entry);
		}	
		
		return blackListEntryList;
	}
	
	
}
