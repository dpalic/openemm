package org.agnitas.emm.core.logintracking;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.agnitas.beans.Admin;
import org.agnitas.web.LogonAction;
import org.apache.log4j.Logger;

/**
 * Helper class to save login data in HTTP request.
 * 
 * @author md
 *
 */
public class LoginTrackServiceRequestHelper {
	
	/** The logger. */
	private static final transient Logger logger = Logger.getLogger( LoginTrackServiceRequestHelper.class);
		
	/** Default value for minimum period of days for login data. */
	public static final int DEFAULT_LOGIN_MIN_PERIOD_DAYS = 14;
	
	/** Name of request attribute. */
	public static final String LOGIN_DATA_ATTRIBUTE_NAME = "login_tracking_list";

	// ---------------------------------------------------------------------- Business Code
	/**
	 * Determines the login data since the last successful login (before the current one), at least the data of the last {@minPeriodDays} days.
	 * The login data is set in HTTP request.
	 * 
	 * @param request HttpServletRequest to store login data
	 * @param admin admin to list for
	 * @param minPeriodDays minimum number of days to list login data
	 * 
	 * @throws LoginTrackServiceException on errors accessing login data
	 */
	public void setLoginTrackingDataToRequest( HttpServletRequest request, Admin admin, int minPeriodDays) throws LoginTrackServiceException {
		Calendar before14DaysCal = new GregorianCalendar();
		before14DaysCal.add( Calendar.DAY_OF_YEAR, -minPeriodDays);
		Date before14Days = before14DaysCal.getTime();
		
		LoginData lastLogin = this.loginTrackService.getLastSuccessfulLogin( admin.getUsername(), true);
		
		try {
			List<LoginData> list;
			
			if( lastLogin == null) {
				list = this.loginTrackService.getLoginData( admin.getUsername(), before14Days);
			} else {
				if( before14Days.before( lastLogin.getLoginTime()))
					list = this.loginTrackService.getLoginData( admin.getUsername(), before14Days);
				else
					list = this.loginTrackService.getLoginData( admin.getUsername(), lastLogin.getLoginTime());
			}
			
			request.setAttribute( LOGIN_DATA_ATTRIBUTE_NAME, list);
		} catch( LoginTrackServiceException e) {
			logger.warn( "Cannot access login tracking data", e);
		}
	}
	
	/**
	 * Removes data about failed logins from request. 
	 * 
	 * @param request HTTP request
	 */
	public void removeFailedLoginWarningFromRequest( HttpServletRequest request) {
		request.getSession().removeAttribute( LogonAction.FAILED_LOGINS_ATTRIBUTE_NAME);
	}
	
	// ---------------------------------------------------------------------- Dependency Injection
	/** Service for accessing login tracking data. */
	private LoginTrackService loginTrackService;
	
	/**
	 * Set the service for accessing login tracking data. 
	 * 
	 * @param service service for accessing login tracking data
	 */
	public void setLoginTrackService( LoginTrackService service) {
		this.loginTrackService = service;
	}

}
