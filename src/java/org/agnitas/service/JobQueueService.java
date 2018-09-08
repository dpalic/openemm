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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.agnitas.dao.JobQueueDao;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.DateUtilities;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class JobQueueService implements ApplicationContextAware {
	private static final transient Logger logger = Logger.getLogger(JobQueueService.class);

	private static final int PARALLEL_WORKERS_PER_CLASS = 1;
	private static final int WORKERS_CLASS_SHORT_DURATION = 10 * 60; // in seconds

	private JobQueueDao jobQueueDao;
	private ApplicationContext applicationContext;

	private List<JobWorker> queuedJobWorkers_LongRunning= new ArrayList<JobWorker>();
	private List<JobWorker> queuedJobWorkers_ShortRunning= new ArrayList<JobWorker>();
	private List<JobWorker> queuedJobWorkers_UnknownDuration= new ArrayList<JobWorker>();
	private List<JobDto> queuedJobsTodo_LongRunning = new ArrayList<JobDto>();
	private List<JobDto> queuedJobsTodo_ShortRunning = new ArrayList<JobDto>();
	private List<JobDto> queuedJobsTodo_UnknownDuration = new ArrayList<JobDto>();

	public synchronized void checkAndRunJobs() {
		logger.info("Looking for queued jobs to execute");

		alertOnHangingJobs();

		List<JobDto> upcomingQueuedJobs = jobQueueDao.readUpcomingJobsForExecution();

		if (logger.isInfoEnabled()) {
			logger.info("Found " + upcomingQueuedJobs.size() + " queued job(s)");
		}

		for (JobDto queuedJob : upcomingQueuedJobs) {
			if (!containsJob(queuedJobsTodo_UnknownDuration, queuedJob)
				&& !containsJob(queuedJobsTodo_ShortRunning, queuedJob)
				&& !containsJob(queuedJobsTodo_LongRunning, queuedJob)
				&& (StringUtils.isBlank(queuedJob.getRunOnlyOnHosts())
					|| Arrays.asList(queuedJob.getRunOnlyOnHosts().split(";|,")).contains(AgnUtils.getIpAddress())
					|| Arrays.asList(queuedJob.getRunOnlyOnHosts().split(";|,")).contains(AgnUtils.getHostName()))) {

				if (queuedJob.getLastDuration() <= 0) {
					queuedJobsTodo_UnknownDuration.add(queuedJob);
				} else if (queuedJob.getLastDuration() <= WORKERS_CLASS_SHORT_DURATION) {
					queuedJobsTodo_ShortRunning.add(queuedJob);
				} else {
					queuedJobsTodo_LongRunning.add(queuedJob);
				}
			}
		}

		checkAndStartNewWorkers();
	}

	private void alertOnHangingJobs() {
		int hoursErrorLimit = 5;
		List<JobDto> hangingJobs = jobQueueDao.getHangingJobs(DateUtilities.getDateOfHoursAgo(hoursErrorLimit));
		for (JobDto job : hangingJobs) {
			String errorSubject = "Error in JobQueue Job " + job.getDescription() + "(" + job.getId() + ") on host '" + AgnUtils.getHostName() + "'";
			String errorText = "Hanging job working for more than " + hoursErrorLimit + " hours";
			if (StringUtils.isNotBlank(job.getEmailOnError())) {
				AgnUtils.sendEmail("jobqueue@" + AgnUtils.getHostName(), job.getEmailOnError(), errorSubject, errorText, errorText, 0, "UTF-8");
			}
			if (StringUtils.isNotBlank(AgnUtils.getDefaultValue("system.alert.mail"))) {
				AgnUtils.sendEmail("jobqueue@" + AgnUtils.getHostName(), AgnUtils.getDefaultValue("system.alert.mail"), errorSubject, errorText, errorText, 0, "UTF-8");
			}
		}
	}

	private boolean containsJob(List<JobDto> jobList, JobDto job) {
		for (JobDto item : jobList) {
			if (item.getId() == job.getId()) {
				return true;
			}
		}
		return false;
	}

	private synchronized void checkAndStartNewWorkers() {
		// Fill the queue for jobs with unknown duration
		while (queuedJobsTodo_UnknownDuration.size() > 0 && queuedJobWorkers_UnknownDuration.size() < PARALLEL_WORKERS_PER_CLASS) {
			JobDto jobToStart = queuedJobsTodo_UnknownDuration.get(0);
			queuedJobsTodo_UnknownDuration.remove(jobToStart);

			try {
				jobToStart.setNextStart(DateUtilities.calculateNextJobStart(jobToStart.getInterval()));
				// update in db will be done by worker
			} catch (Exception e) {
				jobToStart.setNextStart(null);
				jobToStart.setLastResult("Cannot calculate next start!");
				jobQueueDao.updateJobStatus(jobToStart);
			}

			if (jobQueueDao.initJobStart(jobToStart.getId(), jobToStart.getNextStart())) {
				jobToStart.setRunning(true);

				try {
					JobWorker worker = (JobWorker) Class.forName(jobToStart.getRunClass()).newInstance();
					worker.setJob(jobToStart);
					worker.setService(this);
					worker.setApplicationContext(applicationContext);
					worker.setJobQueueDao(jobQueueDao);

					queuedJobWorkers_UnknownDuration.add(worker);
					Thread newThread = new Thread(worker);
					newThread.start();
					if (logger.isDebugEnabled()) {
						logger.debug("Created worker for queued job #" + jobToStart.getId());
					}
				} catch (Exception e) {
					logger.error("Cannot create worker for queued job #" + jobToStart.getId(), e);
					jobToStart.setRunning(false);
					jobToStart.setNextStart(null);
					jobToStart.setLastResult("Cannot create worker: " + e.getClass().getSimpleName() + ": " + e.getMessage() + "\n" + JobWorker.getStackTraceString(e));
					jobQueueDao.updateJobStatus(jobToStart);
				}
			}
		}

		// Fill the queue for jobs with short duration
		while (queuedJobsTodo_ShortRunning.size() > 0 && queuedJobWorkers_ShortRunning.size() < PARALLEL_WORKERS_PER_CLASS) {
			JobDto jobToStart = queuedJobsTodo_ShortRunning.get(0);
			queuedJobsTodo_ShortRunning.remove(jobToStart);

			try {
				jobToStart.setNextStart(DateUtilities.calculateNextJobStart(jobToStart.getInterval()));
				// update in db will be done by worker
			} catch (Exception e) {
				jobToStart.setNextStart(null);
				jobToStart.setLastResult("Cannot calculate next start!");
				jobQueueDao.updateJobStatus(jobToStart);
			}

			if (jobQueueDao.initJobStart(jobToStart.getId(), jobToStart.getNextStart())) {
				jobToStart.setRunning(true);

				try {
					JobWorker worker = (JobWorker) Class.forName(jobToStart.getRunClass()).newInstance();
					worker.setJob(jobToStart);
					worker.setService(this);
					worker.setApplicationContext(applicationContext);
					worker.setJobQueueDao(jobQueueDao);

					queuedJobWorkers_ShortRunning.add(worker);
					Thread newThread = new Thread(worker);
					newThread.start();
					if (logger.isDebugEnabled()) {
						logger.debug("Created worker for queued job #" + jobToStart.getId());
					}
				} catch (Exception e) {
					logger.error("Cannot create worker for queued job #" + jobToStart.getId(), e);
					jobToStart.setRunning(false);
					jobToStart.setNextStart(null);
					jobToStart.setLastResult("Cannot create worker: " + e.getClass().getSimpleName() + ": " + e.getMessage() + "\n" + JobWorker.getStackTraceString(e));
					jobQueueDao.updateJobStatus(jobToStart);
				}
			}
		}

		// Fill the queue for jobs with long duration
		while (queuedJobsTodo_LongRunning.size() > 0 && queuedJobWorkers_LongRunning.size() < PARALLEL_WORKERS_PER_CLASS) {
			JobDto jobToStart = queuedJobsTodo_LongRunning.get(0);
			queuedJobsTodo_LongRunning.remove(jobToStart);

			try {
				jobToStart.setNextStart(DateUtilities.calculateNextJobStart(jobToStart.getInterval()));
				// update in db will be done by worker
			} catch (Exception e) {
				jobToStart.setNextStart(null);
				jobToStart.setLastResult("Cannot calculate next start!");
				jobQueueDao.updateJobStatus(jobToStart);
			}

			if (jobQueueDao.initJobStart(jobToStart.getId(), jobToStart.getNextStart())) {
				jobToStart.setRunning(true);

				try {
					JobWorker worker = (JobWorker) Class.forName(jobToStart.getRunClass()).newInstance();
					worker.setJob(jobToStart);
					worker.setService(this);
					worker.setApplicationContext(applicationContext);
					worker.setJobQueueDao(jobQueueDao);

					queuedJobWorkers_LongRunning.add(worker);
					Thread newThread = new Thread(worker);
					newThread.start();
					if (logger.isDebugEnabled()) {
						logger.debug("Created worker for queued job #" + jobToStart.getId());
					}
				} catch (Exception e) {
					logger.error("Cannot create worker for queued job #" + jobToStart.getId(), e);
					jobToStart.setRunning(false);
					jobToStart.setNextStart(null);
					jobToStart.setLastResult("Cannot create worker: " + e.getClass().getSimpleName() + ": " + e.getMessage() + "\n" + JobWorker.getStackTraceString(e));
					jobQueueDao.updateJobStatus(jobToStart);
				}
			}
		}
	}

	public void showJobEnd(JobWorker worker) {
		queuedJobWorkers_UnknownDuration.remove(worker);
		queuedJobWorkers_ShortRunning.remove(worker);
		queuedJobWorkers_LongRunning.remove(worker);

		checkAndStartNewWorkers();
	}

	public void setJobQueueDao(JobQueueDao jobQueueDao) {
		this.jobQueueDao = jobQueueDao;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public boolean isStatusOK() {
		return jobQueueDao.selectErrorneousJobs().size() == 0;
	}

	public List<JobDto> getAllActiveJobs() {
		return jobQueueDao.getAllActiveJobs();
	}
}
