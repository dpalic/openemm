package org.agnitas.emm.core.target.dao;

import org.agnitas.emm.core.target.service.impl.MailingContentTargetGroupLocator;

/**
 * DAO with special methods for {@link MailingContentTargetGroupLocator}.
 * 
 * @author md
 */
public interface MailingContentTargetGroupLocatorDao {

	/**
	 * Checks, if there is a mailing content block using given target group.
	 * 
	 * @param targetGroupID ID of target group
	 * @param companyID company ID
	 * 
	 * @return true, if a mailing content block using given target group was found
	 */
	public boolean hasMailingContentWithTargetGroup(int targetGroupID, int companyID);

}
