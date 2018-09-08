package org.agnitas.emm.core.logintracking;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.agnitas.beans.Admin;
import org.agnitas.beans.Company;
import org.agnitas.beans.FailedLoginData;
import org.agnitas.dao.CompanyDao;
import org.agnitas.dao.LoginTrackDao;
import org.apache.log4j.Logger;

/**
 * Implementation of {@link LoginTrackService}.
 * 
 * @author md
 *
 */
public class LoginTrackServiceImpl implements LoginTrackService {

	/** The logger. */
	private static final transient Logger logger = Logger.getLogger( LoginTrackServiceImpl.class);
	
	// -------------------------------------------------------------------------------------------------- Business Code
	@Override
	public void trackLoginSuccessful( String ipAddress, String username) {
		this.loginTrackDao.trackSuccessfulLogin( ipAddress, username);
	}
	
	@Override
	public void trackLoginFailed( String ipAddress, String username) {
		this.loginTrackDao.trackFailedLogin( ipAddress, username);
	}
	
	@Override
	public void trackLoginSuccessfulButBlocked( String ipAddress, String username) {
		this.loginTrackDao.trackLoginDuringBlock( ipAddress, username);
	}
	
	
	@Override
	public int getNumFailedLoginsSinceLastSuccessful( String username, boolean skipLastSuccess) throws LoginTrackServiceException {
		try {
			if( logger.isInfoEnabled()) {
				logger.info( "Get number of failed logins since last successful login of user " + username);
			}

			LoginData lastSuccessful = this.loginTrackDao.getLastSuccessfulLogin( username, skipLastSuccess);
			Date since = lastSuccessful != null ? lastSuccessful.getLoginTime() :  null;
			
			if( logger.isDebugEnabled()) {
				if( since == null)
					logger.debug( "No last successful login found");
				else
					logger.debug( "Last successful login was on " + since);
			}
				
			
			return this.loginTrackDao.countFailedLogins( username, since);
		} catch( Exception e) {
			logger.error( "Cannot check for failed logins (user: " + username + ")", e);
			
			throw new LoginTrackServiceException( "Error check for failed logins", e);
		}
	}

	@Override
	public List<LoginData> getLoginData(String username, Date since) throws LoginTrackServiceException {
		try {
			if( logger.isInfoEnabled()) {
				logger.info( "Listing login attempts for user " + username + " since " + since);
			}
				
			return this.loginTrackDao.getLoginAttemptsSince( username, since);
		} catch( Exception e) {
			logger.warn( "Cannot list login attempts", e);
			
			throw new LoginTrackServiceException( "Error listing login attempts", e);
		}
	}

	@Override
	public LoginData getLastSuccessfulLogin(String username, boolean skipLastSuccess) throws LoginTrackServiceException {
		try {
			return this.loginTrackDao.getLastSuccessfulLogin( username, skipLastSuccess);
		} catch( Exception e) {
			logger.error( "Cannot read last successful login", e);
			
			throw new LoginTrackServiceException( "Error reading last successful login", e);
		}
	}

	@Override
    public boolean isIPLogonBlocked( String hostIpAddress, int maxLoginFails, int loginBlockTime) {
   		FailedLoginData data = loginTrackDao.getFailedLoginData( hostIpAddress);

   		if (data.getNumFailedLogins() > maxLoginFails) {
   			return data.getLastFailedLoginTimeDifference() < loginBlockTime;
   		} else {
   			return false;
   		}
    }

	
	// -------------------------------------------------------------------------------------------------- Dependency Injection
	/** DAO for login tracking. */
	private LoginTrackDao loginTrackDao;
	
	/**
	 * Set DAO for login tracking.
	 * 
	 * @param dao DAO for login tracking
	 */
	public void setLoginTrackDao( LoginTrackDao dao) {
		this.loginTrackDao = dao;
	}
	
}
