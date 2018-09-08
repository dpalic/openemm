package org.agnitas.emm.core.velocity;

import org.apache.struts.action.ActionErrors;

/**
 * Result of Velocity evaluation.
 * 
 * @author md
 */
public interface VelocityResult {
	
	/**
	 * Checks, if the evaluation was successful.
	 * 
	 * @return true, if evaluation was successful
	 */
	public boolean wasSuccessful();
	
	/**
	 * Checks, if errors occured.
	 * 
	 * @return true, if errors occured
	 */
	public boolean hasErrors();	
	
	/**
	 * Returns the errors occured during evaluation.
	 * 
	 * @return errors
	 */
	public ActionErrors getErrors();					// TODO: That's not quite good. We get a dependency to the view layer. This was done to keep refactoring smaller.
}
