package org.agnitas.service.impl;

import org.agnitas.beans.VersionObject;
import org.agnitas.dao.VersionControlDao;
import org.agnitas.service.VersionControlService;

public class VersionControlServiceImpl implements VersionControlService {
	
	private VersionControlDao vcDao;

	public VersionObject getLatestVersion(String currentVersion, String referrer ) {
		return vcDao.getServerVersion( currentVersion, referrer );
	}

	public void setVersionControlDao(VersionControlDao vcDao) {
		this.vcDao = vcDao;
	}

}
