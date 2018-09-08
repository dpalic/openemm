package org.agnitas.emm.core.commons.password;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

/**
 * Implementation of {@link PasswordCheckHandler} adding Struts {@link ActionMessage}s to
 * {@link ActionMessages} collection.
 * 
 * @author md
 */
public class StrutsPasswordCheckHandler implements PasswordCheckHandler {
	
	/** {@link ActionMessages} for collecting password errors. */
	private final ActionMessages errors;
	
	/** Name of the property to which the error messages is added. */
	private final String propertyName;
	
	/**
	 * Creates a new {@link PasswordCheckHandler} to add Struts messages.
	 * 
	 * @param errors {@link ActionMessages} collection to add error messages to
	 * @param propertyName name of the property to which the messages are added
	 */
	public StrutsPasswordCheckHandler(ActionMessages errors, String propertyName) {
		this.errors = errors;
		this.propertyName = propertyName;
	}
	
	@Override
	public void handleNoLettersException() {
		errors.add(propertyName, new ActionMessage("error.password_no_letters"));
	}

	@Override
	public void handleNoDigitsException() {
		errors.add(propertyName, new ActionMessage("error.password_no_digits"));
	}

	@Override
	public void handleNoPunctuationException() {
		errors.add(propertyName, new ActionMessage("error.password_no_special_chars"));
	}

	@Override
	public void handlePasswordTooShort() {
		errors.add(propertyName, new ActionMessage("error.password.tooShort"));
	}

	@Override
	public void handleMatchesCurrentPassword() {
		errors.add(propertyName, new ActionMessage("error.password_must_differ"));
	}

	@Override
	public void handleGenericError() {
		errors.add(propertyName, new ActionMessage("error.password_general"));
	}

}
