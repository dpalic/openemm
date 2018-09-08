package org.agnitas.emm.core.logintracking;

import java.util.Date;

/**
 * Interface containing informations of a login attempt.
 * 
 * @author md
 */
public interface LoginData {
	
	/**
	 * Returns the ID of the login track record.
	 * 
	 * @return ID of tracking record
	 */
	public int getLoginTrackId();
	
	/** 
	 * Returns time stamp of login. 
	 *
	 * @return time stamp of login 
	 */
	public Date getLoginTime();

	/**
	 * Returns IP of login.
	 * 
	 * @return IP of login
	 */
	public String getLoginIP();

	/**
	 * Returns the login status (success, failed, ...).
	 * 
	 * @return login status
	 */
	public LoginStatus getLoginStatus();
	
	/**
	 * Returns the use rname of the login attempt.
	 * 
	 * @return user name 
	 */
	public String getUsername();
}
