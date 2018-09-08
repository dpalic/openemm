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

import org.agnitas.dao.CleanDBDao;
import org.agnitas.service.JobWorker;
import org.apache.log4j.Logger;

/**
 * Example Insert in DB:
 *  Insert into job_queue_tbl (ID,DESCRIPTION,CREATED,LASTSTART,RUNNING,LASTRESULT,STARTAFTERERROR,LASTDURATION,INTERVAL,NEXTSTART,HOSTNAME,RUNCLASS,DELETED)
 *    values ((select max(id) + 1 from job_queue_tbl),'DBCleaner',current_timestamp,null,0,'OK',0,0,'0300',current_timestamp,null,'org.agnitas.util.quartz.DBCleanerJobWorker',1);
 *
 * @author aso
 *
 */
public class DBCleanerJobWorker extends JobWorker {
	@SuppressWarnings("unused")
	private static final transient Logger logger = Logger.getLogger(DBCleanerJobWorker.class);

	@Override
	public void runJob() throws Exception {
		((CleanDBDao) applicationContext.getBean("CleanDBDao")).cleanup();
	}
}
