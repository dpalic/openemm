package org.agnitas.emm.core.useractivitylog.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.agnitas.emm.core.useractivitylog.UserActivityEntry;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

/**
 * Implementatin of {@link ParameterizedRowMapper} for conversion of {@link ResultSet} 
 * to {@link UserActivityEntry}.
 * 
 * @author md
 *
 */
class UserActivityLogRowMapper implements ParameterizedRowMapper<UserActivityEntry> {

	@Override
	public UserActivityEntry mapRow(ResultSet rs, int row) throws SQLException {
		Timestamp logtime = rs.getTimestamp( "logtime");
		String username = rs.getString( "username");
		String action = rs.getString( "action");
		String description = rs.getString( "description");
		
		return new UserActivityEntry( username, new Date( logtime.getTime()), action, description);
	}

}
