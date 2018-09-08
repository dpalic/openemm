package org.agnitas.emm.core.velocity.checks;

/**
 * Exception indicating errors on runtime checks of Velocity scripts.
 * 
 * @author md
 */
public class VelocityCheckerException extends Exception {

	/** Serial version UID. */
	private static final long serialVersionUID = 2710246852864510005L;

	/**
	 * Instantiates a new velocity checker exception.
	 */
	public VelocityCheckerException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Instantiates a new velocity checker exception.
	 *
	 * @param message the message
	 */
	public VelocityCheckerException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Instantiates a new velocity checker exception.
	 *
	 * @param cause the cause
	 */
	public VelocityCheckerException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Instantiates a new velocity checker exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public VelocityCheckerException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
