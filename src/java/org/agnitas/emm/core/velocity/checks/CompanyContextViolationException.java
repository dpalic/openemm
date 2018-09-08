package org.agnitas.emm.core.velocity.checks;

// TODO: Auto-generated Javadoc
/**
 * Exception indicating a violation of the company context.
 * 
 * @author md
 */
public class CompanyContextViolationException extends VelocityCheckerException {
	
	/** Serial version UID.*/
	private static final long serialVersionUID = 5211377912170861899L;
	
	/** Company ID that is violating the context. */
	private final int invalidCompanyId;
	
	/** ID of company that is executing the script. */
	private final int contextCompanyId;
	
	/**
	 * Instantiates a new company context violation exception.
	 *
	 * @param invalidCompanyId the invalid company id
	 * @param contextCompanyId the context company id
	 */
	public CompanyContextViolationException( int invalidCompanyId, int contextCompanyId) {
		super( "Script violates context of company " + contextCompanyId + " (invalid company ID is " + invalidCompanyId + ")");
		
		this.invalidCompanyId = invalidCompanyId;
		this.contextCompanyId = contextCompanyId;
	}
	
	/**
	 * Gets the context company id.
	 *
	 * @return the context company id
	 */
	public int getContextCompanyId() {
		return this.contextCompanyId;
	}
	
	/**
	 * Gets the invalid company id.
	 *
	 * @return the invalid company id
	 */
	public int getInvalidCompanyId() {
		return this.invalidCompanyId;
	}
}
