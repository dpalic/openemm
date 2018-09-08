package org.agnitas.emm.core.commons.password;

/**
 * Callback interface for reporting password errors.
 * 
 * @author md
 */
public interface PasswordCheckHandler {

	/**
	 * Callback method indicating that password does not contain letters.
	 */
	public void handleNoLettersException();

	/**
	 * Callback method indicating that password does not contain digits.
	 */
	public void handleNoDigitsException();

	/**
	 * Callback method indicating that password does not contain punctuation.
	 */
	public void handleNoPunctuationException();

	/**
	 * Callback method indicating that password is too short.
	 */
	public void handlePasswordTooShort();

	/**
	 * Callback method indicating that password does not differ from current password.
	 */
	public void handleMatchesCurrentPassword();

	/**
	 * Callback method indicating a general or uncategorized error in password.
	 */
	public void handleGenericError();

}
