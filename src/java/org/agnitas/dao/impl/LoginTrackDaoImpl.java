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
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;
import javax.swing.tree.RowMapper;

import org.agnitas.beans.FailedLoginData;
import org.agnitas.beans.impl.FailedLoginDataImpl;
import org.agnitas.dao.LoginTrackDao;
import org.agnitas.emm.core.logintracking.LoginData;
import org.agnitas.emm.core.logintracking.LoginDataImpl;
import org.agnitas.emm.core.logintracking.LoginStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * DAO implementation for login tracking using a MySQL database.
 * 
 * @author Markus Dorschmidt
 */
public class LoginTrackDaoImpl implements LoginTrackDao {
	/**
	 * Status for a successful login
	 */
	public static final int LOGIN_TRACK_STATUS_SUCCESS = 10;
	
	/**
	 * Status for a failed login
	 */
	public static final int LOGIN_TRACK_STATUS_FAILED = 20;
	
	/**
	 * Status for a successful login during lock period
	 */
	public static final int LOGIN_TRACK_STATUS_DURING_BLOCK = 40;
	
	/**
	 * RowMapper for DB access
	 */
	protected final static ParameterizedRowMapper<FailedLoginData> failedLoginDataRowMapper = new FailedLoginDataRowMapper();
	
	/** Row mapper for login track data. */
	protected final static ParameterizedRowMapper<LoginData> loginDataRowMapper = new LoginDataRowMapper();
	
	/** JDBC data source. */
	protected DataSource dataSource;
	
	/**
	 * Set JDBC data source.
	 * 
	 * @param dataSource JDBC data source
	 */
	public void setDataSource( DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	/**
	 * Implementation of the RowMapper
	 * 
	 * @author Markus Dorschmidt
	 */
	static class FailedLoginDataRowMapper implements ParameterizedRowMapper<FailedLoginData> {
		@Override
		public FailedLoginData mapRow(ResultSet rs, int row) throws SQLException {
			FailedLoginDataImpl result = new FailedLoginDataImpl();
			
			result.setNumFailedLogins(rs.getInt(1));
			result.setLastFailedLoginTimeDifference(rs.getInt(2));
			
			return result;
		}
	
	}
	
	/**
	 * Implementation of {@link RowMapper} for all kinds of login track data.
	 * 
	 * @author md
	 */
	static class LoginDataRowMapper implements ParameterizedRowMapper<LoginData> {

		@Override
		public LoginData mapRow(ResultSet rs, int row) throws SQLException {
			int trackId = rs.getInt( "login_track_id");
			Date loginTime = new Date( rs.getTimestamp( "creation_date").getTime());	// Required. Otherwise java.util.Date.before() does not work correctly
			String loginIP = rs.getString( "ip_address");
			LoginStatus loginStatus = LoginStatus.getLoginStatusFromStatusCode( rs.getInt( "login_status"));
			String username = rs.getString( "username");
			 
			
			return new LoginDataImpl( trackId, loginTime, loginIP, loginStatus, username);
		}
	}

	@Override
	public FailedLoginData getFailedLoginData(String ipAddress) {
		SimpleJdbcTemplate template = new SimpleJdbcTemplate( dataSource);
		String sql = "SELECT count(ip_address), ifnull(timestampdiff(second, max(ifnull(creation_date, 0)), now()),0) " +
			"FROM login_track_tbl " +
			"WHERE ip_address = ? " +
			"AND login_status = " + LoginTrackDaoImpl.LOGIN_TRACK_STATUS_FAILED + " " +
			"AND creation_date > (" +
			"     SELECT ifnull(max(creation_date), 0) " +
			"     FROM login_track_tbl " +
			"     WHERE ip_address = ? " +
			"     AND login_status = " + LoginTrackDaoImpl.LOGIN_TRACK_STATUS_SUCCESS + ")";
		
		List<FailedLoginData> list = template.query( sql, failedLoginDataRowMapper, ipAddress, ipAddress);
		
		if (list.size() == 1)
			return list.get(0);
		else
			return new FailedLoginDataImpl();  // No failed logins found
	}

	@Override
	public void trackFailedLogin(String ipAddress, String username) {
		trackLoginStatus(ipAddress, username, LoginTrackDaoImpl.LOGIN_TRACK_STATUS_FAILED);
	}

	@Override
	public void trackSuccessfulLogin(String ipAddress, String username) {
		trackLoginStatus(ipAddress, username, LoginTrackDaoImpl.LOGIN_TRACK_STATUS_SUCCESS);
	}
	
	@Override
	public void trackLoginDuringBlock(String ipAddress, String username) {
		trackLoginStatus(ipAddress, username, LoginTrackDaoImpl.LOGIN_TRACK_STATUS_DURING_BLOCK);
	}
	
	/**
	 * Generic method for recording logins.
	 * 
	 * @param ipAddress IP address of host
	 * @param username use username in login
	 * @param status login status
	 */
	protected void trackLoginStatus(String ipAddress, String username, int status) {
		JdbcTemplate template = new JdbcTemplate( dataSource);
		String sql = "INSERT INTO login_track_tbl (ip_address, login_status, username) VALUES (?,?,?)";
		
		template.update(sql, new Object[] { ipAddress, status, username });
	}
	
	@Override
	public int deleteOldRecords(int holdBackDays, int maxRecords) {
		JdbcTemplate template = new JdbcTemplate( dataSource);
		String sql = "DELETE FROM login_track_tbl WHERE timestampdiff(day, creation_date, now()) > ? LIMIT ?";
		
		return template.update(sql, new Object[] { new Integer(holdBackDays), new Integer(maxRecords) });
	}

	@Override
	public LoginData getLastSuccessfulLogin(String username, boolean skipLastSuccess) {
		LoginData login = getLastSuccessfulLoginBefore( username, null);
		
		if( login == null)
			return null;
		
		// Skip this login? Query again, this time with time...
		if( skipLastSuccess)
			login = getLastSuccessfulLoginBefore( username, login.getLoginTime());
		
		return login;
	}
	
	/**
	 * Returns the last successful login before given date.
	 * {@code before} can be {@code null} to disable check for date. 
	 * 
	 * @param username user name 
	 * @param before get last successful login before this date or {@code null}
	 * 
	 * @return last successful login
	 */
	private LoginData getLastSuccessfulLoginBefore( String username, Date before) {
		// This query works with Oracle and MySQL
		String query = "SELECT login_track_id, username, creation_date, ip_address, login_status " +
				"FROM login_track_tbl WHERE login_track_id = " +
				"(SELECT max(login_track_id) FROM login_track_tbl WHERE username = ? AND login_status = 10 AND (creation_date < ? OR ? IS NULL))";
		
		SimpleJdbcTemplate template = new SimpleJdbcTemplate( dataSource);
		
		List<LoginData> list = template.query( query, loginDataRowMapper, username, before, before);
		if( list.size() == 0)
			return null;
		else
			return list.get( 0);
	}

	@Override
	public int countFailedLogins(String username, Date since) {
		String query = "SELECT count(login_track_id) AS fails FROM login_track_tbl WHERE username=? " +
				"AND (creation_date >= ? or ? IS NULL) " +
				"AND login_status=20";
		
		SimpleJdbcTemplate template = new SimpleJdbcTemplate( dataSource);
		
		return template.queryForInt( query, username, since, since);
	}

	@Override
	public List<LoginData> getLoginAttemptsSince(String username, Date since) {
		String query = "SELECT login_track_id, username, creation_date, ip_address, login_status " +
				"FROM login_track_tbl WHERE username = ? AND creation_date >= ? AND login_status IN (?,?,?) " +
				"ORDER BY creation_date DESC";
		
		SimpleJdbcTemplate template = new SimpleJdbcTemplate( dataSource);

		return template.query( query, loginDataRowMapper, 
				username, 
				since, 
				LoginStatus.SUCCESS.getStatusCode(),
				LoginStatus.FAIL.getStatusCode(),
				LoginStatus.SUCCESS_BUT_BLOCKED.getStatusCode());
	}
	
}
