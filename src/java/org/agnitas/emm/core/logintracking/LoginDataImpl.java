package org.agnitas.emm.core.logintracking;

import java.util.Date;

/**
 * Implementation of {@link LoginData}.
 * 
 * @author md
 *
 */
public class LoginDataImpl implements LoginData {

	/** Tracking ID. */
	private final int trackId;
	
	/** Time of login attempt. */
	private final Date loginTime;
	
	/** IP of login attempt. */
	private final String loginIP;
	
	/** Status of login attempt. */
	private final LoginStatus loginStatus;
	
	/** User name. */
	private final String username;
	
	/**
	 * Instantiates a new login data.
	 *
	 * @param trackId ID of tracking record
	 * @param loginTime the login time
	 * @param loginIP the login IP
	 * @param loginStatus the login status
	 * @param username user name
	 */
	public LoginDataImpl( int trackId, Date loginTime, String loginIP, LoginStatus loginStatus, String username) {
		this.trackId = trackId;
		this.loginTime = loginTime;
		this.loginIP = loginIP;
		this.loginStatus = loginStatus;
		this.username = username;
	}

	@Override
	public int getLoginTrackId() {
		return this.trackId;
	}
	
	@Override
	public Date getLoginTime() {
		return this.loginTime;
	}

	@Override
	public String getLoginIP() {
		return this.loginIP;
	}

	@Override
	public LoginStatus getLoginStatus() {
		return this.loginStatus;
	}

	@Override
	public String getUsername() {
		return this.username;
	}
}
