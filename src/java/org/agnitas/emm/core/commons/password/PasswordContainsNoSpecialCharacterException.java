package org.agnitas.emm.core.commons.password;

/**
 * Exception indicating password does not contain special characters.
 * 
 * @author md
 *
 */
public class PasswordContainsNoSpecialCharacterException extends PasswordConstraintException {

	/** Serial version UID. */
	private static final long serialVersionUID = -6747926066775975710L;

	/**
	 * Instantiates a new password contains no special character exception.
	 */
	public PasswordContainsNoSpecialCharacterException() {
		super("Password does not contain special characters");
	}
}
