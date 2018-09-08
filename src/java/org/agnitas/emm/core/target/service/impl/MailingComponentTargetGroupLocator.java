package org.agnitas.emm.core.target.service.impl;

import org.agnitas.emm.core.target.dao.MailingComponentTargetGroupLocatorDao;
import org.agnitas.emm.core.target.exception.TargetGroupException;
import org.agnitas.emm.core.target.service.TargetGroupLocator;
import org.agnitas.emm.core.velocity.VelocityCheck;
import org.springframework.beans.factory.annotation.Required;

/**
 * Implementation of {@link TargetGroupLocator} working on mailing components.
 * 
 * @author md
 *
 */
public class MailingComponentTargetGroupLocator implements TargetGroupLocator {

	/**
	 * Locator DAO working on mailing components.
	 */
	private MailingComponentTargetGroupLocatorDao locatorDao;
	
	@Override
	public boolean isTargetGroupInUse(int targetGroupID, @VelocityCheck int companyID) throws TargetGroupException {
		return this.locatorDao.hasMailingComponentWithTargetGroup(targetGroupID, companyID);
	}

	// ---------------------------------------------------- Dependency Injection
	/**
	 * Set locator DAO working on mailing components.
	 * 
	 * @param dao locator DAO working on mailing components
	 */
	@Required
	public void setMailingComponentTargetGroupLocatorDao(MailingComponentTargetGroupLocatorDao dao) {
		this.locatorDao = dao;
	}
}
