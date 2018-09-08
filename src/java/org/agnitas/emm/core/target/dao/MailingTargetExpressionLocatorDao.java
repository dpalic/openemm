package org.agnitas.emm.core.target.dao;

import java.util.Set;

import org.agnitas.emm.core.target.service.impl.MailingTargetGroupLocator;
import org.agnitas.emm.core.velocity.VelocityCheck;

/**
 * Interface with highly specialized DB access methods used by the {@link MailingTargetGroupLocator}.
 * 
 * @author md
 *
 */
public interface MailingTargetExpressionLocatorDao {

	/**
	 * Returns the target expressions used by relevant mailings.
	 * 
	 * @param companyID company ID
	 * 
	 * @return set of all relevant target expressions
	 */
	public Set<String> getTargetExpressionsForMailings(@VelocityCheck int companyID);

}
