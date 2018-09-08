package org.agnitas.emm.core.target.dao;

import org.agnitas.emm.core.target.service.impl.ExportPredefTargetGroupLocator;
import org.agnitas.emm.core.velocity.VelocityCheck;

/**
 * Interface for highly specific methods for {@link ExportPredefTargetGroupLocator}.

 * @author md
 *
 */
public interface ExportPredefTargetGroupLocatorDao {

	/**
	 * Check, if there is an export profile, that uses the given target group.
	 * Deleted export profiles are ignored.
	 * 
	 * @param targetGroupID ID of target group
	 * @param companyID company ID of profiles to check
	 * 
	 * @return true, if there is an export profile, that uses the given target group.
	 */
	public boolean hasExportProfilesForTargetGroup(int targetGroupID, @VelocityCheck int companyID);
	
}
