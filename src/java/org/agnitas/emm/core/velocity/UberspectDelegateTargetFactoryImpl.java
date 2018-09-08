package org.agnitas.emm.core.velocity;

import org.agnitas.emm.core.velocity.checks.CompanyContextVelocityChecker;
import org.apache.velocity.util.introspection.Uberspect;

/**
 * Implementation of {@link UberspectDelegateTargetFactory}.
 * 
 * @author md
 *
 */
public class UberspectDelegateTargetFactoryImpl implements UberspectDelegateTargetFactory {

	@Override
	public Uberspect newDelegateTarget(int companyId) {
		return new AgnVelocityUberspector( companyId, this.companyContextChecker);
	}

	// ------------------------------------------------------- Dependency Injection
	/** Checker for company context. */
	private CompanyContextVelocityChecker companyContextChecker;
	
	/**
	 * Set checker for company context.
	 * 
	 * @param checker checker for company context
	 */
	public void setCompanyContextVelocityChecker( CompanyContextVelocityChecker checker) {
		this.companyContextChecker = checker;
	}
}
