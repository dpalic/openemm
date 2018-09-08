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
package org.agnitas.service;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class JobQueueQuartzJob extends QuartzJobBean {
	private static final transient Logger logger = Logger.getLogger(JobQueueQuartzJob.class);

	private JobQueueService jobQueueService;

	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		// look for queued jobs and execute them
		try {
			logger.debug("JobQueueQuartzJob was called");
			jobQueueService.checkAndRunJobs();
		} catch (Exception e) {
			logger.error("JobQueueQuartzJob call returned an error: " + e.getMessage(), e);
			throw new JobExecutionException("JobQueueQuartzJob call returned an error", e, false);
		}
	}

	public void setJobQueueService(JobQueueService jobQueueService) {
		this.jobQueueService = jobQueueService;
	}
}
