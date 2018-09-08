package org.agnitas.emm.core.commons.exceptions;

/**
 * Exception indicating insufficient permissions to perform a specific action.
 * 
 * @author md
 */
public class InsufficientPermissionException extends Exception {

	/** Serial version UID. */
	private static final long serialVersionUID = 630473028713480518L;

	/**
	 * Create new exception with admin ID and action description.
	 * 
	 * @param adminID admin ID
	 * @param action description of action
	 */
	public InsufficientPermissionException(int adminID, String action) {
		super("Admin " + adminID + " has not enough permissions for: " + action);
	}
	
}
