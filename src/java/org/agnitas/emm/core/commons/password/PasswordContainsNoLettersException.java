package org.agnitas.emm.core.commons.password;

/**
 * Exception indicating that password does not containg letters.
 * 
 * @author md
 *
 */
public class PasswordContainsNoLettersException extends PasswordConstraintException {

	/** Serial version UID. */
	private static final long serialVersionUID = -8724520260060100968L;

	/**
	 * Instantiates a new password contains no letters exception.
	 */
	public PasswordContainsNoLettersException() {
		super("Password does not contain letters");
	}
}
