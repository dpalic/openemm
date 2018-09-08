package org.agnitas.emm.core.velocity;

import org.agnitas.util.EventHandler;
import org.apache.struts.action.ActionErrors;

/**
 * Default implementation of {@link VelocityResult}.
 * 
 * @author md
 */
class VelocityResultImpl implements VelocityResult {

	/** Indicates successful executing of the Velocity script. */
	private final boolean successful;
	
	/** Velocity event handler collecting error messages. */
	private final EventHandler eventHandler;
	
	/**
	 * Creates a new VelocityresultImpl.
	 * 
	 * @param successful true, if executing of script was successful
	 * @param eventHandler Event handler that collected the script errors
	 */
	public VelocityResultImpl( boolean successful, EventHandler eventHandler) {
		this.successful = successful;
		this.eventHandler = eventHandler;
	}
	
	@Override
	public boolean wasSuccessful() {
		return this.successful;
	}

	@Override
	public boolean hasErrors() {
		return eventHandler.getErrors() != null && !eventHandler.getErrors().isEmpty();
	}

	@Override
	public ActionErrors getErrors() {
		if( hasErrors())
			return eventHandler.getErrors();
		else
			return null;
	}

}
