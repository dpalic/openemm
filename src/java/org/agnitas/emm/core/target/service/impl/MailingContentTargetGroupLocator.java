package org.agnitas.emm.core.target.service.impl;

import org.agnitas.emm.core.target.dao.MailingContentTargetGroupLocatorDao;
import org.agnitas.emm.core.target.exception.TargetGroupException;
import org.agnitas.emm.core.target.service.TargetGroupLocator;
import org.agnitas.emm.core.velocity.VelocityCheck;
import org.springframework.beans.factory.annotation.Required;

/**
 * Implementation of {@link TargetGroupLocator} working on mailing content blocks.
 * 
 * @author md
 *
 */
public class MailingContentTargetGroupLocator implements TargetGroupLocator {

	/**
	 * Locator DAO working on mailing content block.
	 */
	private MailingContentTargetGroupLocatorDao locatorDao;
	
	@Override
	public boolean isTargetGroupInUse(int targetGroupID, @VelocityCheck int companyID) throws TargetGroupException {
		return this.locatorDao.hasMailingContentWithTargetGroup(targetGroupID, companyID);
	}

	// ---------------------------------------------------- Dependency Injection
	/**
	 * Set locator DAO working on mailing components.
	 * 
	 * @param dao locator DAO working on mailing components
	 */
	@Required
	public void setMailingContentTargetGroupLocatorDao(MailingContentTargetGroupLocatorDao dao) {
		this.locatorDao = dao;
	}
}
