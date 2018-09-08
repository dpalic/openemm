package org.agnitas.emm.core.velocity.checks;

import java.lang.reflect.Method;

import org.agnitas.emm.core.velocity.CheckType;
import org.apache.log4j.Logger;

/**
 * Implementation of {@link VelocityChecker} performing a check on company IDs
 * used in scripts.
 * 
 * @author md
 *
 */
public class CompanyContextVelocityChecker implements VelocityChecker {

	/** The logger. */
	private static final transient Logger logger = Logger.getLogger( CompanyContextVelocityChecker.class);
	
	@Override
	public void performCheck(Method method, Object argument, CheckType checkType, int contextCompanyId) throws VelocityCheckerException {
		try {
			Integer companyValue = (Integer) argument;

			if( !isValidCompanyId( companyValue, contextCompanyId)) {
				throw new CompanyContextViolationException( companyValue, contextCompanyId);
			}
		} catch( ClassCastException e) {
			logger.info( "Cannot perform company context check", e);
		}
	}
	
	/**
	 * Checks, if the company ID used in the script is valid.
	 * 
	 * @param scriptCompanyId company ID used in script
	 * @param companyContext ID of company executing the script
	 * @return
	 */
	protected boolean isValidCompanyId( int scriptCompanyId, int companyContext) {
		return scriptCompanyId == companyContext;
	}
}