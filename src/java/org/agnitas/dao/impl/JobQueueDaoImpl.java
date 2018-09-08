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
package org.agnitas.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.agnitas.dao.JobQueueDao;
import org.agnitas.emm.core.velocity.VelocityCheck;
import org.agnitas.service.JobDto;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.DbUtilities;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

/**
 * DAO handler for JobDto-Objects
 * 
 * @author Andreas Soderer
 * @date 08.03.2012
 */
public class JobQueueDaoImpl extends BaseDaoImpl implements JobQueueDao {
	private static final transient Logger logger = Logger.getLogger(JobQueueDaoImpl.class);
	
	private static final String TABLE = "job_queue_tbl";

	private static final String FIELD_ID = "id";
	private static final String FIELD_DESCRIPTION = "description";
	private static final String FIELD_CREATED = "created";
	private static final String FIELD_LASTSTART = "lastStart";
	private static final String FIELD_RUNNING = "running";
	private static final String FIELD_LASTRESULT = "lastResult";
	private static final String FIELD_STARTAFTERERROR = "startAfterError";
	private static final String FIELD_LASTDURATION = "lastDuration";
	private static final String FIELD_INTERVAL = "`interval`";
	private static final String FIELD_INTERVAL_NO_BACKTICKS = "interval";
	private static final String FIELD_NEXTSTART = "nextStart";
	private static final String FIELD_HOSTNAME = "hostname";
	private static final String FIELD_RUNCLASS = "runClass";
	private static final String FIELD_DELETED = "deleted";
	private static final String FIELD_RUNONLYONHOSTS = "runonlyonhosts";
	private static final String FIELD_EMAILONERROR = "emailonerror";
	
	private static final String SELECT_UPCOMING_JOBS = "SELECT * FROM " + TABLE + " WHERE " + FIELD_RUNNING + " <= 0 AND " + FIELD_NEXTSTART + " IS NOT NULL AND " + FIELD_NEXTSTART + " < CURRENT_TIMESTAMP AND " + FIELD_DELETED + " <= 0 AND (" + FIELD_LASTRESULT + " IS NULL OR " + FIELD_LASTRESULT + " = 'OK' OR " + FIELD_STARTAFTERERROR + " > 0) AND " + FIELD_RUNCLASS + " IS NOT NULL";
	private static final String SELECT_NOT_DELETED_JOBS = "SELECT * FROM " + TABLE + " WHERE " + FIELD_DELETED + " <= 0 ORDER BY " + FIELD_ID;
	private static final String SELECT_ERRORNEOUS_JOBS = "SELECT * FROM " + TABLE + " WHERE " + FIELD_DELETED + " <= 0 AND ((" + FIELD_LASTRESULT + " IS NOT NULL AND " + FIELD_LASTRESULT + " != 'OK') OR " + FIELD_NEXTSTART + " IS NULL OR " + FIELD_NEXTSTART + " < DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 0.01 DAY) OR " + FIELD_INTERVAL + " IS NULL OR " + FIELD_RUNCLASS + " IS NULL)";
	private static final String SELECT_BY_ID = "SELECT * FROM " + TABLE + " WHERE " + FIELD_ID + " = ?";
	private static final String UPDATE = "UPDATE " + TABLE + " SET " + FIELD_DESCRIPTION + " = ?, " + FIELD_CREATED + " = ?, " + FIELD_LASTSTART + " = ?, " + FIELD_RUNNING + " = ?, " + FIELD_LASTRESULT + " = ?, " + FIELD_STARTAFTERERROR + " = ?, " + FIELD_LASTDURATION + " = ?, " + FIELD_INTERVAL + " = ?, " + FIELD_NEXTSTART + " = ?, " + FIELD_HOSTNAME + " = ?," + FIELD_RUNCLASS + " = ?, " + FIELD_RUNONLYONHOSTS + " = ?, " + FIELD_EMAILONERROR + " = ?, " + FIELD_DELETED + " = ? WHERE " + FIELD_ID + " = ?";
	private static final String SELECT_JOB_STATUS_FOR_UPDATE = "SELECT " + FIELD_RUNNING + " FROM " + TABLE + " WHERE " + FIELD_RUNNING + " <= 0 AND " + FIELD_NEXTSTART + " IS NOT NULL AND " + FIELD_NEXTSTART + " < CURRENT_TIMESTAMP AND " + FIELD_DELETED + " <= 0 AND (" + FIELD_LASTRESULT + " IS NULL OR " + FIELD_LASTRESULT + " = 'OK' OR " + FIELD_STARTAFTERERROR + " > 0) AND " + FIELD_ID + " = ? FOR UPDATE";
	private static final String UPDATE_JOB_STATUS = "UPDATE " + TABLE + " SET " + FIELD_RUNNING + " = 1, " + FIELD_NEXTSTART + " = ?, " + FIELD_HOSTNAME + " = ? WHERE " + FIELD_ID + " = ?";
	
	private static final String DELETE_BY_ID = "UPDATE " + TABLE + " SET " + FIELD_DELETED + " = 1 WHERE " + FIELD_DELETED + " <= 0 AND " + FIELD_ID + " = ?";
	
	// job_queue_parameter_tbl
	private static final String PARAMETER_TABLE = "job_queue_parameter_tbl";
	
	private static final String PARAMETER_FIELD_JOBID = "job_id";
	private static final String PARAMETER_FIELD_PARAMETERNAME = "parameter_name";
	private static final String PARAMETER_FIELD_PARAMETERVALUE = "parameter_value";
	
	private static final String SELECT_PARAMETERS_BY_JOBID = "SELECT * FROM " + PARAMETER_TABLE + " WHERE " + PARAMETER_FIELD_JOBID + " = ?";
	private static final String INSERT_PARAMETER_FOR_JOBID = "INSERT INTO " + PARAMETER_TABLE + " (" + PARAMETER_FIELD_JOBID + ", " + PARAMETER_FIELD_PARAMETERNAME + ", " + PARAMETER_FIELD_PARAMETERVALUE + ") VALUES (?, ?, ?)";
	private static final String DELETE_PARAMETERS_BY_JOBID = "DELETE FROM " + PARAMETER_TABLE + " WHERE " + PARAMETER_FIELD_JOBID + " = ?";

	// ----------------------------------------------------------------------------------------------------------------
	// Dependency Injection

	// ----------------------------------------------------------------------------------------------------------------
	// Business Logic

	@Override
	public List<JobDto> readUpcomingJobsForExecution() {
		try {
			return select(logger, SELECT_UPCOMING_JOBS, new Job_RowMapper());
		} catch (Exception e) {
			throw new RuntimeException("Error while reading jobs from database", e);
		}
	}

	@Override
	public JobDto getJob(int id) {
		if (id <= 0) {
			return null;
		} else {
			try {
				return selectObject(logger, SELECT_BY_ID, new Job_RowMapper(), id);
			} catch (DataAccessException e) {
				// No Job found
				return null;
			} catch (Exception e) {
				throw new RuntimeException("Error while reading job from database", e);
			}
		}
	}

	@Override
	public boolean initJobStart(int id, Date nextStart) {
		if (id < 0) {
			return false;
		} else {
			Connection connection = null;
			PreparedStatement lockStatement = null;
			ResultSet lockQueryResult = null;
			PreparedStatement updateStatement = null;
            boolean previousAutoCommit = true;
	
			try {
				connection = getDataSource().getConnection();
            	previousAutoCommit = connection.getAutoCommit();
				connection.setAutoCommit(false);

				logSqlStatement(logger, SELECT_JOB_STATUS_FOR_UPDATE);
				lockStatement = connection.prepareStatement(SELECT_JOB_STATUS_FOR_UPDATE);
				lockStatement.setInt(1, id);
				lockQueryResult = lockStatement.executeQuery();
				if (lockQueryResult.next()) {
					// Lock this job by setting to running and calculating next start
					logSqlStatement(logger, UPDATE_JOB_STATUS);
					updateStatement = connection.prepareStatement(UPDATE_JOB_STATUS);
					if (nextStart == null) updateStatement.setTimestamp(1, null);
					else updateStatement.setTimestamp(1, new Timestamp(nextStart.getTime()));
					updateStatement.setString(2, AgnUtils.getHostName());
					updateStatement.setInt(3, id);
					updateStatement.executeUpdate();
					connection.commit();
					return true;
				} else {
					connection.commit();
					return false;
				}
			} catch (Exception e) {
				logger.error("Error while setting job job status", e);
				throw new RuntimeException("Error while setting job status", e);
			} finally {
				DbUtilities.closeQuietly(lockQueryResult);
				DbUtilities.closeQuietly(lockStatement);
				DbUtilities.closeQuietly(updateStatement);
                try {
    				if (connection != null) {
    					connection.setAutoCommit(previousAutoCommit);
    				}
    			} catch (SQLException e) {
    				logger.error("Cannot reset autocommit to " + previousAutoCommit, e);
    			}
				DbUtilities.closeQuietly(connection);
			}
		}
	}
	
	@Override
	public boolean updateJob(JobDto job) {
		if (job == null) {
			return false;
		} else {
			try {
				if (updateJobStatus(job)) {
		            // clear parameters of existing job for new storing afterwards
		            update(logger, DELETE_PARAMETERS_BY_JOBID, job.getId());
		
		            // write new parameters
					if (job.getParameters() != null && !job.getParameters().isEmpty()){
						List<Object[]> parameterList = new ArrayList<Object[]>();
		                for (Entry<String, String> parameterEntry : job.getParameters().entrySet()) {
							parameterList.add(new Object[] { job.getId(), parameterEntry.getKey(), parameterEntry.getValue() });
		                }
		                logSqlStatement(logger, INSERT_PARAMETER_FOR_JOBID);
		                getSimpleJdbcTemplate().batchUpdate(INSERT_PARAMETER_FOR_JOBID, parameterList);
		            }
					
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				logger.error("Error while updating job job", e);
				throw new RuntimeException("Error while updating job", e);
			}
		}
	}
	
	/**
	 * Update the jobs status only and ignore the optional additional parameters in JOB_QUEUE_PARAM_TBL
	 */
	@Override
	public boolean updateJobStatus(JobDto job) {
		if (job == null) {
			return false;
		} else {
			try {
				// Update Job in DB
				String lastResult = job.getLastResult();
				if (lastResult != null && lastResult.length() > 512) {
					lastResult = lastResult.substring(0, 508) + " ...";
				}
				int touchedLines = update(logger,
						UPDATE,
						job.getDescription(),
						job.getCreated(),
						job.getLastStart(),
						job.isRunning(),
						lastResult,
						job.isStartAfterError(),
						job.getLastDuration(),
						job.getInterval(),
						job.getNextStart(),
						AgnUtils.getHostName(),
						job.getRunClass(),
						job.getRunOnlyOnHosts(),
						job.getEmailOnError(),
						job.isDeleted(),
						job.getId());
				
				if (touchedLines != 1) {
					throw new RuntimeException("Invalid touched lines amount");
				} else {
					return true;
				}
			} catch (Exception e) {
				logger.error("Error while updating job job status", e);
				throw new RuntimeException("Error while updating job job status", e);
			}
		}
	}

	@Override
	public boolean deleteJob(int id) {
		if (id <= 0) {
			return false;
		} else {
			if (logger.isDebugEnabled()) logger.debug("stmt:" + DELETE_BY_ID);
			try {
				return update(logger, DELETE_BY_ID, id) > 0;
			} catch (DataAccessException e) {
				// No Job found
				return false;
			} catch (Exception e) {
				throw new RuntimeException("Error while deleting job from database", e);
			}
		}
	}

	@Override
	public int resetJobsForCurrentHost() {
		return update(logger,
			"UPDATE " + TABLE
			+ " SET " + FIELD_RUNNING + " = 0, " + FIELD_NEXTSTART + " = CURRENT_TIMESTAMP"
			+ " WHERE " + FIELD_HOSTNAME + " = ? AND " + FIELD_RUNNING + " = 1 AND (" + FIELD_DELETED + " IS NULL OR " + FIELD_DELETED + " = 0)",
			AgnUtils.getHostName());
	}

	private class Job_RowMapper implements ParameterizedRowMapper<JobDto> {
		@Override
		public JobDto mapRow(ResultSet resultSet, int row) throws SQLException {
			JobDto newJob = new JobDto();

			newJob.setId(resultSet.getInt(FIELD_ID));
			newJob.setDescription(resultSet.getString(FIELD_DESCRIPTION));
			newJob.setCreated(resultSet.getTimestamp(FIELD_CREATED));
			newJob.setLastStart(resultSet.getTimestamp(FIELD_LASTSTART));
			newJob.setRunning(resultSet.getInt(FIELD_RUNNING) > 0);
			newJob.setLastResult(resultSet.getString(FIELD_LASTRESULT));
			newJob.setStartAfterError(resultSet.getInt(FIELD_STARTAFTERERROR) > 0);
			newJob.setLastDuration(resultSet.getInt(FIELD_LASTDURATION));
			newJob.setInterval(resultSet.getString(FIELD_INTERVAL_NO_BACKTICKS));
			newJob.setNextStart(resultSet.getTimestamp(FIELD_NEXTSTART));
			newJob.setRunClass(resultSet.getString(FIELD_RUNCLASS));
			newJob.setDeleted(resultSet.getInt(FIELD_DELETED) > 0);
			newJob.setRunOnlyOnHosts(resultSet.getString(FIELD_RUNONLYONHOSTS));
			newJob.setEmailOnError(resultSet.getString(FIELD_EMAILONERROR));

			// Read parameters for this job
			if (logger.isDebugEnabled()) logger.debug("stmt:" + SELECT_PARAMETERS_BY_JOBID);
			List<Map<String, Object>> result = select(logger, SELECT_PARAMETERS_BY_JOBID, newJob.getId());
			
			Map<String, String> parameters = new HashMap<String, String>();
			for (Map<String, Object> resultRow : result) {
				parameters.put((String) resultRow.get(PARAMETER_FIELD_PARAMETERNAME), (String) resultRow.get(PARAMETER_FIELD_PARAMETERVALUE));
			}
			newJob.setParameters(parameters);
			
			return newJob;
		}
	}

	@Override
	public List<Map<String, Object>> queryForList(String statement, Object... parameters) {
		return select(logger, statement, parameters);
	}

	@Override
	public <T> T queryForObject(String statement, Class<T> requiredType, Object... parameters) {
		return select(logger, statement, requiredType, parameters);
	}

	@Override
	public String createTempTableForActiveTargetCustomers(@VelocityCheck int companyID, int mailinglistID, String targetWhereCondition) {
		String tempTableName = "targetTemp_" + AgnUtils.getRandomString(10);
		// Attention: "Create table as select"-statement does not allow ?-parameters
		String createSql = "CREATE TABLE " + tempTableName + " TABLESPACE data_temp AS SELECT customer_id FROM customer_" + companyID + "_tbl cust WHERE customer_id IN (SELECT customer_id FROM customer_" + companyID + "_binding_tbl WHERE mailinglist_id = " + mailinglistID + " AND user_status = 1) AND " + targetWhereCondition;
		update(logger, createSql);
		return tempTableName;
	}

	@Override
	public void dropTempTableForTargetCustomers(String tempTableName) {
		if (tempTableName.startsWith("targetTemp_")) {
			String dropSql = "DROP TABLE " + tempTableName;
			update(logger, dropSql);
		} else {
			throw new IllegalArgumentException("TempTableName is invalid for drop");
		}
	}

	@Override
	public List<JobDto> selectErrorneousJobs() {
		try {
			return select(logger, SELECT_ERRORNEOUS_JOBS, new Job_RowMapper());
		} catch (Exception e) {
			throw new RuntimeException("Error while reading errorneous jobs from database", e);
		}
	}

	@Override
	public List<JobDto> getAllActiveJobs() {
		try {
			return select(logger, SELECT_NOT_DELETED_JOBS, new Job_RowMapper());
		} catch (Exception e) {
			throw new RuntimeException("Error while reading not deleted jobs from database", e);
		}
	}
	
	@Override
	public List<JobDto> getHangingJobs(Date timeLimit) {
		try {
			return select(logger, "SELECT * FROM " + TABLE + " WHERE " + FIELD_DELETED + " <= 0 AND " + FIELD_RUNNING + " > 0 AND " + FIELD_LASTSTART + " < ? ORDER BY " + FIELD_ID, new Job_RowMapper(), timeLimit);
		} catch (Exception e) {
			throw new RuntimeException("Error while reading hanging jobs from database", e);
		}
	}

	@Override
	public void writeJobResult(int job_id, Date time, String result, int durationInSeconds, String hostname) {
		try {
			if (result != null && result.length() > 512) {
				result = result.substring(0, 508) + " ...";
			}
			update(logger, "INSERT INTO job_queue_result_tbl (job_id, time, result, duration, hostname) VALUES (?, ?, ?, ?, ?)", job_id, time, result, durationInSeconds, hostname);
		} catch (Exception e) {
			throw new RuntimeException("Error while writing JobResult", e);
		}
	}
}