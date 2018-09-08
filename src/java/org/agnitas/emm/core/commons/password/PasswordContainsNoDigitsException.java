package org.agnitas.emm.core.commons.password;

/**
 * Exception indicating that password does not contain digits.
 * 
 * @author md
 *
 */
public class PasswordContainsNoDigitsException extends PasswordConstraintException {

	/** Serial version UID. */
	private static final long serialVersionUID = 7307091114271221321L;

	/**
	 * Instantiates a new password contains no digits exception.
	 */
	public PasswordContainsNoDigitsException() {
		super("Password does not contain digits");
	}
	
}
