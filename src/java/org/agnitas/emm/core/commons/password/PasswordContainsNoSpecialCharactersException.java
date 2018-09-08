package org.agnitas.emm.core.commons.password;

/**
 * Exception indicating password does not contain special characters.
 * 
 * @author md
 *
 */
public class PasswordContainsNoSpecialCharactersException extends PasswordConstraintException {

	/** Serial version UID. */
	private static final long serialVersionUID = -6747926066775975710L;

	/**
	 * Instantiates a new password contains no special characters exception.
	 */
	public PasswordContainsNoSpecialCharactersException() {
		super("Password does not contain special characters");
	}
}
