package org.agnitas.emm.core.target.service.impl;

import org.agnitas.emm.core.target.dao.ExportPredefTargetGroupLocatorDao;
import org.agnitas.emm.core.target.exception.TargetGroupException;
import org.agnitas.emm.core.target.service.TargetGroupLocator;
import org.agnitas.emm.core.velocity.VelocityCheck;
import org.springframework.beans.factory.annotation.Required;

/**
 * Implementation of {@link TargetGroupLocator} checking export profiles.
 * 
 * @author md
 *
 */
public class ExportPredefTargetGroupLocator implements TargetGroupLocator {

	/**
	 * DAO for checking export profiles.
	 */
	private ExportPredefTargetGroupLocatorDao locatorDao;
	
	@Override
	public boolean isTargetGroupInUse(int targetGroupID, @VelocityCheck int companyID) throws TargetGroupException {
		return this.locatorDao.hasExportProfilesForTargetGroup(targetGroupID, companyID);
	}

	// -------------------------------------------------------- Dependency Injection
	/**
	 * Set DAO for checking export profiles.
	 * 
	 * @param dao DAO for checking export profiles
	 */
	@Required
	public void setExportPredefTargetGroupLocatorDao(ExportPredefTargetGroupLocatorDao dao) {
		this.locatorDao = dao;
	}
}
