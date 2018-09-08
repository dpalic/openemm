package org.agnitas.emm.core.target.service.impl;

import java.util.List;
import java.util.Vector;

import org.agnitas.emm.core.target.exception.TargetGroupException;
import org.agnitas.emm.core.target.service.TargetGroupLocator;
import org.apache.log4j.Logger;

/**
 * Chains a list of {@link TargetGroupLocator} instances.
 * 
 * @author md
 */
public class TargetGroupLocatorChain implements TargetGroupLocator {

	/**
	 * The logger. 
	 */
	private static final transient Logger logger = Logger.getLogger(TargetGroupLocatorChain.class);
	
	/**
	 * List of {@link TargetGroupLocator}s to use.
	 */
	private final List<TargetGroupLocator> locators;
	
	/**
	 * Creates a new instance with empty list of locators.
	 */
	public TargetGroupLocatorChain() {
		this.locators = new Vector<>();
	}
	
	/**
	 * Invokes each locator until the first locator found an objects, that uses given target group.
	 */
	@Override
	public boolean isTargetGroupInUse(int targetGroupID, int companyID) throws TargetGroupException {
		for(TargetGroupLocator locator : this.locators) {
			if(logger.isDebugEnabled()) {
				logger.debug("Invoking target group locator type " + locator.getClass().getCanonicalName());
			}
			
			if(locator.isTargetGroupInUse(targetGroupID, companyID)) {
				return true;
			}
		}
		
		// No locator indicated that the target group was used
		return false;
	}
	
	// ------------------------------------------------------------- Dependency Injection
	/**
	 * Set list of {@link TargetGroupLocator} instances. The order of the locators in the list is the order in which the locators are used.
	 * 
	 * @param locators list of {@link TargetGroupLocator}s
	 */
	public void setTargetGroupLocators(List<TargetGroupLocator> locators) {
		this.locators.clear();
		this.locators.addAll(locators);
	}

}
