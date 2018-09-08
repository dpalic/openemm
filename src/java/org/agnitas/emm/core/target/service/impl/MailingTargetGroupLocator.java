package org.agnitas.emm.core.target.service.impl;

import java.util.Set;

import org.agnitas.emm.core.target.dao.MailingTargetExpressionLocatorDao;
import org.agnitas.emm.core.target.exception.TargetGroupException;
import org.agnitas.emm.core.target.service.TargetGroupLocator;
import org.agnitas.emm.core.target.service.TargetService;
import org.agnitas.emm.core.velocity.VelocityCheck;
import org.springframework.beans.factory.annotation.Required;

/**
 * Searches for target groups in mailings (Does not include graphic components, 
 * content block, etc.)
 * 
 * The following mailings are taken into account:
 * <ul>
 *   <li>all activatable mailings, that are not deleted (action-based, event-based, ...)</li>
 *   <li>all world mailings (and therefore followup mailings, too), that are not deleted and currently scheduled for delivery</li>
 * </ul>
 * 
 * Templates are also considered. 
 * Deleted mailings and delivered or new world mailings are not checked.
 * 
 * @author md
 */
public class MailingTargetGroupLocator implements TargetGroupLocator {

	
	/**
	 * Service for accessing target groups.
	 */
	private TargetService targetService; 
	
	/**
	 * DAO used by the {@link MailingTargetGroupLocator}.
	 */
	private MailingTargetExpressionLocatorDao locatorDao;
	
	@Override
	public boolean isTargetGroupInUse(int targetGroupID, @VelocityCheck int companyID) throws TargetGroupException {
		 Set<String> allExpressions = this.locatorDao.getTargetExpressionsForMailings(companyID); 
				 
		 return isTargetGroupInUse(targetGroupID, allExpressions);
	}
	
	/**
	 * Checks, if one of the given target group expressions contains the given target group ID.
	 * 
	 * @param targetGroupID target group ID to look for
	 * @param targetExpressions target expressions to look into
	 * 
	 * @return true, if target group ID is found in one of the target expressions
	 */
	private boolean isTargetGroupInUse(int targetGroupID, Set<String> targetExpressions) {
		for(String targetExpression : targetExpressions) {
			Set<Integer> ids = this.targetService.getTargetIdsFromExpressionString(targetExpression);
			
			if(ids.contains(targetGroupID)) {
				return true;
			}
		}
		
		return false;
	}
	
	// ----------------------------------------------------------- Dependency Injection
	
	/**
	 * Set target service.
	 * 
	 * @param service target service
	 */
	@Required
	public void setTargetService(TargetService service) {
		this.targetService = service;
	}
	
	/**
	 * Set DAO with specific methods for this locator.
	 * 
	 * @param dao DAO with specific methods
	 */
	@Required
	public void setMailingTargetExpressionLocatorDao(MailingTargetExpressionLocatorDao dao) {
		this.locatorDao = dao;
	}
}
