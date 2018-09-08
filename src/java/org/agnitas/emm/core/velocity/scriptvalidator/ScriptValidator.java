package org.agnitas.emm.core.velocity.scriptvalidator;


public interface ScriptValidator {
	public void validateScript( String script) throws ScriptValidationException;
}
