package org.agnitas.emm.core.target.exception;


/**
 * Exception indicating that operation (like deleting) cannot be performed, because
 * target group is in use by some other item.
 * 
 * @author md
 *
 */
public class TargetGroupIsInUseException extends TargetGroupException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -6955856329115705793L;
	
	/**
	 * Affected target group ID.
	 */
	private final int targetGroupID;
	
	/**
	 * Creates a new expception.
	 * 
	 * @param targetGroupID affected target group ID
	 */
	public TargetGroupIsInUseException(int targetGroupID) {
		super("Target group is in use: " + targetGroupID);
		this.targetGroupID = targetGroupID;
	}

	/**
	 * Returns affected target group ID.
	 * 
	 * @return affected target group ID
	 */
	public int getTargetGroupID() {
		return this.targetGroupID;
	}

}
