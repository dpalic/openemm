package org.agnitas.emm.core.velocity.checks;

import java.lang.reflect.Method;

import org.agnitas.emm.core.velocity.CheckType;

/**
 * Generic interface for runtime checks on Velocity scripts.
 * 
 * @author md
 *
 */
public interface VelocityChecker {
	
	/**
	 * Performs a check on a specific parameter of a method call.
	 * 
	 * @param method called method
	 * @param argument argument
	 * @param checkType type of check to be performed
	 * @param companyId company ID that is executing the script
	 * 
	 * @throws VelocityCheckerException on errors during check (violation of access privileges, ...)
	 */
	public void performCheck( Method method, Object argument, CheckType checkType, int companyId) throws VelocityCheckerException;
}
