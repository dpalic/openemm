package org.agnitas.emm.core.logintracking;

/**
 * Exception indicating an error in {@link LoginTrackService}.
 * 
 * @author md
 */
public class LoginTrackServiceException extends Exception {

	/** Serial version UID. */
	private static final long serialVersionUID = 3936161378762014798L;

	
	/**
	 * Instantiates a new failed login service exception.
	 */
	public LoginTrackServiceException() {
		super();
	}

	/**
	 * Instantiates a new failed login service exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public LoginTrackServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new failed login service exception.
	 *
	 * @param message the message
	 */
	public LoginTrackServiceException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new failed login service exception.
	 *
	 * @param cause the cause
	 */
	public LoginTrackServiceException(Throwable cause) {
		super(cause);
	}

}
