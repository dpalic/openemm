
package org.agnitas.emm.core.target.service;

import org.agnitas.emm.core.target.exception.TargetGroupException;
import org.agnitas.emm.core.velocity.VelocityCheck;

/**
 * Interface for code to locate target groups.
 * 
 * @author md
 */
public interface TargetGroupLocator {

	/**
	 * Checks, if the given target group is used somewhere.
	 * 
	 * @param targetGroupID ID of target group to look for
	 * @param companyID company ID
	 * 
	 * @return true, if target group is used somewhere, otherwise false
	 * 
	 * @throws TargetGroupException on errors during processing
	 */
	public boolean isTargetGroupInUse(int targetGroupID, @VelocityCheck int companyID) throws TargetGroupException;
	
}
