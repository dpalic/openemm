/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2014 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/
package org.agnitas.util.quartz;

import org.agnitas.dao.LoginTrackDao;
import org.agnitas.service.JobWorker;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Example Insert in DB:
 *  Insert into job_queue_tbl (ID,DESCRIPTION,CREATED,LASTSTART,RUNNING,LASTRESULT,STARTAFTERERROR,LASTDURATION,INTERVAL,NEXTSTART,HOSTNAME,RUNCLASS,DELETED)
 *    values ((select max(id) + 1 from job_queue_tbl),'LoginTrackTableCleaner',current_timestamp,null,0,'OK',0,0,'0400',current_timestamp,null,'org.agnitas.util.quartz.LoginTrackTableCleanerJobWorker',1);
 *
 *  Optional:
 *  Insert into job_queue_parameter_tbl (JOB_ID,PARAMETER_NAME,PARAMETER_VALUE) values ((select id from job_queue_tbl where DESCRIPTION = 'LoginTrackTableCleaner'),'retentionTime','7');
 *  Insert into job_queue_parameter_tbl (JOB_ID,PARAMETER_NAME,PARAMETER_VALUE) values ((select id from job_queue_tbl where DESCRIPTION = 'LoginTrackTableCleaner'),'deleteBlockSize','1000');
 *
 * @author aso
 *
 */
public class LoginTrackTableCleanerJobWorker extends JobWorker {
	private static final transient Logger logger = Logger.getLogger(LoginTrackTableCleanerJobWorker.class);

	/**
	 * Default value of retention time (in days) for old records.
	 */
	public static final int DEFAULT_RETENTION_TIME = 60;

	/**
	 * Number of records deleted with one statement.
	 */
	public static final int DEFAULT_DELETE_BLOCK_SIZE = 1000;

	@Override
	public void runJob() throws Exception {
		int retentionTime;
		try {
			if (StringUtils.isBlank(job.getParameters().get("retentionTime"))) {
				retentionTime = DEFAULT_RETENTION_TIME;
			} else {
				retentionTime = Integer.parseInt(job.getParameters().get("retentionTime"));
			}
		} catch (Exception e) {
			throw new Exception("Parameter retentionTime is missing or invalid", e);
		}

		int deleteBlockSize;
		try {
			if (StringUtils.isBlank(job.getParameters().get("deleteBlockSize"))) {
				deleteBlockSize = DEFAULT_DELETE_BLOCK_SIZE;
			} else {
				deleteBlockSize = Integer.parseInt(job.getParameters().get("deleteBlockSize"));
			}
		} catch (Exception e) {
			throw new Exception("Parameter deleteBlockSize is missing or invalid", e);
		}

		LoginTrackDao loginTrackDao = ((LoginTrackDao) applicationContext.getBean("LoginTrackDao"));

		int affectedRows;

		if(loginTrackDao == null) {
			logger.error("no LoginTrackDao object defined - job stopped");
		} else {
			// Delete in blocks
			while((affectedRows = loginTrackDao.deleteOldRecords(retentionTime, deleteBlockSize)) > 0) {
				if (logger.isInfoEnabled()) logger.info("deleted " + affectedRows + " records");
			}
		}
	}
}
