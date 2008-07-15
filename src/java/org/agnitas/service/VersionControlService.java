package org.agnitas.service;

import org.agnitas.beans.VersionObject;
import org.agnitas.dao.VersionControlDao;

/**
 * This class checks if the currently installed version of OpenEMM is also the latest on the server.
 */
public interface VersionControlService {
	
	/** Checks if a new version of OpenEMM is available.
	 * 
	 * @param currentVersion The currently used version of OpenEMM
	 * @param referrer 
	 */
	public VersionObject getLatestVersion( String currentVersion, String referrer );

	public void setVersionControlDao(VersionControlDao dao);
}
