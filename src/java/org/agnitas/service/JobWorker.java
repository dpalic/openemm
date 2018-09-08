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

import java.util.Date;

import org.agnitas.dao.JobQueueDao;
import org.agnitas.util.AgnUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

/**
 * Worker class for queued jobs.
 *
 * This worker takes the relevant parameters from the job item and executes the implementation class of it.
 * It also takes control of the job state and manages,together with JobQueueService, the execution of jobs.
 *
 * Derviving classes must implement the method 'runJob' for executing actions.
 *
 * JobQueue documentation: http://wiki.agnitas.local/doku.php?id=abteilung:technik:entwicklung:cron
 *
 * @author aso
 */
public abstract class JobWorker implements Runnable {
	/**
	 * Logger of the JobWorker class. The implementing classes should have their own logger
	 */
	private static final transient Logger logger = Logger.getLogger(JobWorker.class);

	/**
	 * Service instance having control on this JobWorker object
	 */
	protected JobQueueService service;

	/**
	 * Database object containing all data on this jobqueue entry
	 */
	protected JobDto job;

	/**
	 * ApplicationContext for some special actions of the jobs.
	 * Use of this should be avoided whenever possible, because it is a dirty style
	 */
	protected ApplicationContext applicationContext;

	/**
	 * Dao instance for use only within this class
	 */
	protected JobQueueDao jobQueueDao;

	/**
	 * Pause sign
	 * This sign may be used optionally by the implementing class
	 * When set the executing thread will be paused until further notice
	 */
	protected boolean pause = false;

	/**
	 * Stop sign
	 * This sign may be used optionally by the implementing class
	 * When set the executing job should come to an end on a convenient position
	 */
	protected boolean stop = false;

	// ----------------------------------------------------------------------------------------------------------------
	// Dependency Injection

	public void setService(JobQueueService service) {
		this.service = service;
	}

	public void setJob(JobDto job) {
		this.job = job;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void setJobQueueDao(JobQueueDao jobQueueDao) {
		this.jobQueueDao = jobQueueDao;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Business Logic

	/**
	 * Runnable.run implementation for basic execution
	 *
	 * This method MUST NOT be overridden in deriving classes.
	 * 'runJob' is the method, which implements the code to execute in a job run.
	 */
	@Override
	public void run() {
		Date runStart = new Date();
		job.setRunning(true);
		job.setLastStart(runStart);
		jobQueueDao.updateJobStatus(job);

		logger.info("Starting JobWorker: " + job.getDescription() + " (" + job.getId() + ")");

		try {
			runJob();

			job.setLastResult("OK");
			job.setLastDuration((int) (new Date().getTime() - runStart.getTime()) / 1000);
		} catch (Throwable t) {
			logger.error("Error in " + this.getClass().getName() + ": " + t.getMessage(), t);
			// Watchout: NullpointerExceptions have Message "null", which would result in another jobrun, so enter some additional text (classname)
			job.setLastResult(t.getClass().getSimpleName() + ": " + t.getMessage() + "\n" + getStackTraceString(t));
			if (StringUtils.isNotBlank(job.getEmailOnError())) {
				String errorText = t.getClass().getSimpleName() + ": " + t.getMessage() + "\n" + getStackTraceString(t);
				AgnUtils.sendEmail("jobqueue@" + AgnUtils.getHostName(), job.getEmailOnError(), "Error in JobQueue Job " + job.getDescription() + "(" + job.getId() + ") on host '" + AgnUtils.getHostName() + "'", errorText, errorText, 0, "UTF-8");
			}
		}

		logger.info("JobWorker done: " + job.getDescription() + " (" + job.getId() + ")");

		job.setRunning(false);
		jobQueueDao.updateJobStatus(job);

		// Write JobResult after job has ended
		jobQueueDao.writeJobResult(job.getId(), new Date(), job.getLastResult(), job.getLastDuration(), AgnUtils.getHostName() + "/" + AgnUtils.getIpAddress());

		// show report ended
		service.showJobEnd(this);
	}

	/**
	 * Hook method for derived classes to execute some action
	 *
	 * JobQueue error management will be started on any Exception that is thrown on this method
	 *
	 * @throws Exception
	 */
	public abstract void runJob() throws Exception;

	/**
	 * Method for optional execution state check.
	 *
	 * This may be ignored by deriving classes, but it would be nice if they use it to controll execution
	 *
	 * @throws JobStopException
	 */
	protected void checkForPrematureEnd() throws JobStopException {
		try {
			while (pause) {
				Thread.sleep(500);
			}
		} catch (InterruptedException e) {
		}

		if (stop) {
			throw new JobStopException();
		}
	}

	/**
	 * Set signal to stop exection
	 *
	 * This method is used by JobQueueService to stop execution
	 */
	public void setStopSign() {
		stop = true;
		pause = false;
	}

	/**
	 * Utility method to show stacktrace in db table
	 *
	 * @param t
	 * @return
	 */
	public static String getStackTraceString(Throwable t) {
		String stackTraceString= "";

		if (t != null) {
			StackTraceElement[] stackTraceElements = t.getStackTrace();
			if (stackTraceElements != null && stackTraceElements.length > 0) {
				for (int level = 0; level < stackTraceElements.length && level < 5; level++) {
					stackTraceString += stackTraceElements[level].toString() + "\n";
				}
			}
		}

		if (StringUtils.isEmpty(stackTraceString)) {
			stackTraceString = "<empty Stacktrace>";
		}

		return stackTraceString;
    }
}
