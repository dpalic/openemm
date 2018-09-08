package org.agnitas.emm.core.commons.password;

/**
 * Exception indicating that password is too short.
 * 
 * @author md
 *
 */
public class PasswordTooShortException extends PasswordConstraintException {

	/** Serial version UID. */
	private static final long serialVersionUID = -9009546427977245067L;

	/**
	 * Instantiates a new password too short exception.
	 */
	public PasswordTooShortException() {
		super("Password too short");
	}
	
}
