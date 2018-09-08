package org.agnitas.emm.core.commons.password;

/**
 * Exception indicating that new password and current password are the same.
 * 
 * @author md
 *
 */
public class PasswordMatchesCurrentPasswordException extends PasswordConstraintException {

	/** Serial version UID. */
	private static final long serialVersionUID = -1650930017421850206L;

	/**
	 * Create new exception.
	 */
	public PasswordMatchesCurrentPasswordException() {
		super("Password matches current password");
	}
	
}
