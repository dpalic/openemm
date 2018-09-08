package org.agnitas.emm.core.velocity;

/**
 * Factory that creates a {@link VelocityWrapper}.
 * Depending on the implementation, the factory can cache {@link VelocityWrapper} instances
 * by company IDs. 
 * 
 * @author md
 */
public interface VelocityWrapperFactory {
	
	/**
	 * Returns a {@link VelocityWrapper} that runs in the context of the given company ID.
	 * 
	 * @param companyId company ID
	 * 
	 * @return instance of {@link VelocityWrapper} for given company ID
	 * 
	 * @throws Exception on errors creating a new instance
	 */
	public VelocityWrapper getWrapper( int companyId) throws Exception; 
}
