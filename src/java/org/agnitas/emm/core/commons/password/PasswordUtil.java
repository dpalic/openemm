package org.agnitas.emm.core.commons.password;

/**
 * Utility class dealing with passwords.
 * 
 * @author md
 *
 */
public class PasswordUtil {

	/** Minimum length of password. */
	public static final int MINIMUM_PASSWORD_LENGTH = 8;
	
	/**
	 * Checks basic properties of given password:
	 * <ul>
	 *   <li>Password must contain letters</li>
	 *   <li>Password must contain digits</li>
	 *   <li>Password must contain punctuation</li>
	 *   <li>Password must be at least {@value PasswordUtil#MINIMUM_PASSWORD_LENGTH} characters long</li>
	 * </ul>
	 *   
	 * @param password password to check
	 * 
	 * @throws PasswordConstraintException when password violates one of the checked rules
	 */
	public static void checkPasswordConstraints(String password) throws PasswordConstraintException {
		if(!password.matches(".*\\p{Alpha}.*")) {
			throw new PasswordContainsNoLettersException();
		} else if(!password.matches(".*\\p{Digit}.*")) {
			throw new PasswordContainsNoDigitsException();
		} else if(!password.matches(".*[^\\p{Alnum}].*")) {	// Every character, that is not alpha or digit is treated as special character
			throw new PasswordContainsNoSpecialCharactersException();
		} else if (password.length() < MINIMUM_PASSWORD_LENGTH) {
			throw new PasswordTooShortException();
		}
	}
	
}
