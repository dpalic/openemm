package org.agnitas.emm.core.logintracking;

/**
 * Enumeration containing all login status to be displayed.
 * 
 * @author md
 *
 */
public enum LoginStatus {
	/** Successful login. */
	SUCCESS(10),
	
	/** Failed login. */
	FAIL(20),

	/** Successful login, but blocked due to security restrictions. */
	SUCCESS_BUT_BLOCKED(40);
	
	/** Code of status. */
	private final int statusCode;
	
	/**
	 * Creates a new login status. 
	 * 
	 * @param statusCode code of login status
	 */
	LoginStatus( int statusCode) {
		this.statusCode = statusCode;
	}
	
	/**
	 * Returns the status code of the login status.
	 * 
	 * @return status code of login status
	 */
	public int getStatusCode() {
		return this.statusCode;
	}
	
	/**
	 * Converts the status code to the login status.
	 * If the status code is invalid, null is returned.
	 * 
	 * @param code status code
	 * 
	 * @return LoginStatus or null
	 */
	public static LoginStatus getLoginStatusFromStatusCode( int code) {
		for( LoginStatus status : LoginStatus.values())
			if( status.getStatusCode() == code)
				return status;
		
		return null;
	}
}
