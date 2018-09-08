package org.agnitas.emm.core.logintracking;

import java.util.Date;
import java.util.List;

/**
 * Service for handling login data. 
 * 
 * @author md
 */
public interface LoginTrackService {
	
	/**
	 * Returns the number of failed logins since the last successful login.
	 * Set {@code skipLastSuccess} if the method is called after the record for the current login has been written, otherwise you will
	 * get 0.
	 * 
	 * @param username user name to check
	 * @param skipLastSuccess set to true, if record for current login has already been written 
	 * 
	 * @return number of failed logins since last successful 
	 * 
	 * @throws LoginTrackServiceException on errors accessing login data
	 */
	public int getNumFailedLoginsSinceLastSuccessful( String username, boolean skipLastSuccess) throws LoginTrackServiceException;
	
	/**
	 * Returns an ordered list of login informations since the given time.
	 * The list is ordered by login time descending.
	 * 
	 * @param username user name to get login informations
	 * @param since get all login data since this time
	 * 
	 * @return list of login informations to be displayed
	 * 
	 * @throws LoginTrackServiceException on errors accessing login data
	 */
	public List<LoginData> getLoginData( String username, Date since) throws LoginTrackServiceException;

	/**
	 * Returns the  data of the last successful login.
	 * Set {@code skipLastSuccess} if the method is called after the record for the current login has been written, otherwise you will
	 * get the current login.
	 * 
	 * @param username user name
	 * @param skipLastSuccess set to true, if record for current login has already been written 
	 * 
	 * @return last successful login or null
	 * 
	 * @throws LoginTrackServiceException on errors accessing login data
	 */
	public LoginData getLastSuccessfulLogin(String username, boolean skipLastSuccess) throws LoginTrackServiceException;

	/**
	 * Check if given IP is blocked.
	 * 
	 * @param hostIpAddress IP address
	 * @param maxLoginFails maximum number of failed login allowed before an IP gets blocked
	 * @param loginBlockTime number of seconds an IP is blocked since last failed attempt
	 * 
	 * @return true if IP is blocked otherwise false
	 */
	boolean isIPLogonBlocked(String hostIpAddress, int maxLoginFails, int loginBlockTime);

	/**
	 * Write tracking record about successful login.
	 * 
	 * @param ipAddress IP address
	 * @param username user name
	 */
	void trackLoginSuccessful(String ipAddress, String username);

	/**
	 * Write tracking record about failed login.
	 * 
	 * @param ipAddress IP address
	 * @param username user name
	 */
	void trackLoginFailed(String ipAddress, String username);

	/**
	 * Write tracking record about successful login while IP address is blocked.
	 * 
	 * @param ipAddress IP address
	 * @param username user name
	 */
	void trackLoginSuccessfulButBlocked(String ipAddress, String username);
}
