package org.agnitas.emm.core.velocity;

/**
 * Implementation of {@link AbstractVelocityWrapper} using the
 * {@link AgnVelocityUberspector}.
 * 
 * @author md
 *
 */
class VelocityWrapperImpl extends AbstractVelocityWrapper {

	/**
	 * Creates a new VelocityWrapperImpl running in the context of the
	 * given company ID.
	 * 
	 * @param companyId company ID
	 * @param factory factory for Uberspect delegate targets
	 * 
	 * @throws Exception on errors initializing the {@link VelocityWrapper}
	 */
	public VelocityWrapperImpl( int companyId, UberspectDelegateTargetFactory factory) throws Exception {
		super( companyId, factory);
	}

}
