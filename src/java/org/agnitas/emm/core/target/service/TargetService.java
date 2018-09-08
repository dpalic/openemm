package org.agnitas.emm.core.target.service;

import java.util.Set;

import org.agnitas.beans.Mailing;
import org.agnitas.dao.exception.target.TargetGroupPersistenceException;
import org.agnitas.emm.core.target.exception.TargetGroupException;
import org.agnitas.emm.core.velocity.VelocityCheck;

/**
 * Service layer interface for target groups.
 * 
 * @author md
 *
 */
public interface TargetService {
	public boolean hasMailingDeletedTargetGroups( Mailing mailing);
    public Set<Integer> getTargetIdsFromExpression(Mailing mailing);
    
    public Set<Integer> getTargetIdsFromExpressionString(String targetExpression);
    
    /**
     * Checks, if given target group is in use somewhere.
     * 
     * @param targetID ID of target group
     * @param companyID company ID
     * 
     * @return true, if target group is in use otherwise false
     * 
     * @throws TargetGroupException on errors during processing
     */
    public boolean isTargetGroupInUse(int targetID, @VelocityCheck int companyID) throws TargetGroupException;

	/**
	 * Delete target group.
	 * 
	 * @param targetGroupID target group ID to be deleted 
	 * @param companyID company ID of target group
	 * @throws TargetGroupException on errors during processing
	 * @throws TargetGroupPersistenceException on errors during processing 
	 */
	
	public void deleteTargetGroup(int targetGroupID, @VelocityCheck int companyID) throws TargetGroupException, TargetGroupPersistenceException;

}
