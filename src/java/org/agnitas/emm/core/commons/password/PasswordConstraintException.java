package org.agnitas.emm.core.commons.password;

/**
 * Exception indication violation of a contraint on passwords.
 * 
 * @author md
 *
 */
public class PasswordConstraintException extends Exception {

	/** Serial version UID. */
	private static final long serialVersionUID = 6551291765269605934L;

	/**
	 * Instantiates a new password constraint exception.
	 */
	public PasswordConstraintException() {
		super();
	}

	/**
	 * Instantiates a new password constraint exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 * @param enableSuppression the enable suppression
	 * @param writableStackTrace the writable stack trace
	 */
	public PasswordConstraintException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Instantiates a new password constraint exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public PasswordConstraintException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new password constraint exception.
	 *
	 * @param message the message
	 */
	public PasswordConstraintException(String message) {
		super(message);
	}
	
	/**
	 * Instantiates a new password constraint exception.
	 *
	 * @param cause the cause
	 */
	public PasswordConstraintException(Throwable cause) {
		super(cause);
	}

}
