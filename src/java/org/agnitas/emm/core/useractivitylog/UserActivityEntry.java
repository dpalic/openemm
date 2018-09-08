package org.agnitas.emm.core.useractivitylog;

import java.util.Date;

public class UserActivityEntry {
	
	private final String username;
	private final String action;
	private final String description;
	private final Date logDate;
	
	public UserActivityEntry( String username, Date logDate, String action, String description) {
		this.username = username;
		this.logDate = logDate;
		this.action = action;
		this.description = description;
	}
	
	public String getAction() {
		return action;
	}

	public String getDescription() {
		return description;
	}

	public Date getLogDate() {
		return logDate;
	}

	public String getUsername() {
		return this.username;
	}
	
	public String getShownName() {
		return getUsername();
	}
}
