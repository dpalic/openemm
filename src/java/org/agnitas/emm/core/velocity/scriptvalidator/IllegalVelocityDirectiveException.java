package org.agnitas.emm.core.velocity.scriptvalidator;

public class IllegalVelocityDirectiveException extends ScriptValidationException {

	/** Serial version UID. */
	private static final long serialVersionUID = -8453373390363036429L;
	
	private final String directive;
	
	public IllegalVelocityDirectiveException( String directive) {
		super( "Velocity script contains illegal directive '" + directive + "'");
		
		this.directive = directive;
	}

	public String getDirective() {
		return this.directive;
	}

}
