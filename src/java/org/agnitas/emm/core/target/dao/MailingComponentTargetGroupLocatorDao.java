package org.agnitas.emm.core.target.dao;

import org.agnitas.emm.core.target.service.impl.MailingComponentTargetGroupLocator;
import org.agnitas.emm.core.velocity.VelocityCheck;

/**
 * DAO with special methods for {@link MailingComponentTargetGroupLocator}.
 * 
 * @author md
 */
public interface MailingComponentTargetGroupLocatorDao {

	/**
	 * Checks, if there is a mailing component using given target group.
	 * 
	 * @param targetGroupID ID of target group
	 * @param companyID company ID
	 * 
	 * @return true, if a mailing component using given target group was found
	 */
	public boolean hasMailingComponentWithTargetGroup(int targetGroupID, @VelocityCheck int companyID);
	
}
