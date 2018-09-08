package org.agnitas.emm.core.target.service;

/**
 * Exception indicating that a target group is unknown.
 * 
 * @author md
 */
public class TargetNotExistException extends RuntimeException {

	/**
	 * Serial version UID. 
	 */
	private static final long serialVersionUID = -859778424456594357L;

	/**
	 * Unknown target ID.
	 */
	private final Integer targetID;

	/**
	 * Creates a new exception.
	 * 
	 * @param targetID unknown target ID
	 */
	public TargetNotExistException(Integer targetID) {
		super("Targt group does not exist: " + targetID);
		
		this.targetID = targetID;
	}

	/**
	 * Returns unknown target ID.
	 * 
	 * @return unknown target ID
	 */
	public Integer getTargetID() {
		return targetID;
	}

}
