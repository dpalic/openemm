package org.agnitas.emm.core.velocity;

import org.apache.velocity.util.introspection.Uberspect;

/**
 * Factory for Uberspect delegate targets. These targets perform different
 * runtime checks on Velocity scripts.
 * 
 * @author md
 *
 */
public interface UberspectDelegateTargetFactory {
	
	/**
	 * Create a new delegate for given company ID.
	 * 
	 * @param companyId company ID that executes the Velocity script
	 * 
	 * @return new Uberspect delegate target
	 */
	public Uberspect newDelegateTarget( int companyId);
}
