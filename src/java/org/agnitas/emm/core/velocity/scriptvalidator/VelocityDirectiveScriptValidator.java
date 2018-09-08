package org.agnitas.emm.core.velocity.scriptvalidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VelocityDirectiveScriptValidator implements ScriptValidator {

	private final Pattern directivePattern = Pattern.compile( "^.*(#(?:include|parse)).*$", Pattern.DOTALL);
	
	@Override
	public void validateScript(String script) throws ScriptValidationException {
		Matcher m = directivePattern.matcher( script);
		
		if( m.matches()) {
			throw new IllegalVelocityDirectiveException( m.group( 1));
		}
	}

}
