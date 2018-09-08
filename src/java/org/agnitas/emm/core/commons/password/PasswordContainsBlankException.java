package org.agnitas.emm.core.commons.password;

/**
 * Exception indicating password does not contain punctuation characters.
 * 
 * @author md
 *
 */
public class PasswordContainsBlankException extends PasswordConstraintException {

	/** Serial version UID. */
	private static final long serialVersionUID = -6747926066775975710L;

	/**
	 * Instantiates a new password contains no punctuation exception.
	 */
	public PasswordContainsBlankException() {
		super("Password must not contain blanks");
	}
}
