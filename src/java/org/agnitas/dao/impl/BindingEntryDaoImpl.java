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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.agnitas.beans.BindingEntry;
import org.agnitas.beans.impl.BindingEntryImpl;
import org.agnitas.dao.BindingEntryDao;
import org.agnitas.dao.BindingEntryDaoException;
import org.agnitas.emm.core.velocity.VelocityCheck;
import org.agnitas.target.Target;
import org.agnitas.util.AgnUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

/**
 * 
 * @author nse
 */
public class BindingEntryDaoImpl extends BaseDaoImpl implements BindingEntryDao {

	/** The logger. */
	private static final transient Logger logger = Logger.getLogger(BindingEntryDaoImpl.class);

	@Override
	public BindingEntry get(int recipientID, @VelocityCheck int companyID, int mailinglistID, int mediaType) {
		try {
			String sql = "SELECT customer_id, mailinglist_id, mediatype, user_type, user_status, " + AgnUtils.changeDateName() + ", exit_mailing_id, user_remark, creation_date FROM customer_" + companyID + "_binding_tbl WHERE customer_id = ? AND mailinglist_id = ? AND mediatype = ?";
			List<BindingEntry> list = select(logger, sql, new BindingEntry_RowMapper(this), recipientID, mailinglistID, mediaType);
			if (list.size() > 0) {
				return list.get(0);
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void save(@VelocityCheck int companyID, BindingEntry entry) {
		String existsSql = "SELECT * FROM customer_" + companyID + "_binding_tbl WHERE customer_id = ? AND mailinglist_id = ? AND mediatype = ?";
		List<BindingEntry> list = select(logger, existsSql, new BindingEntry_RowMapper(this), entry.getCustomerID(), entry.getMailinglistID(), entry.getMediaType());
		if (list.size() > 0) {
			updateBinding(entry, companyID);
		} else {
			insertNewBinding(entry, companyID);
		}
	}

	/**
	 * Updates this Binding in the Database
	 * 
	 * @return True: Sucess False: Failure
	 * @param companyID
	 *            The company ID of the Binding
	 */
	@Override
	public boolean updateBinding(BindingEntry entry, @VelocityCheck int companyID) {
		try {
			String sql = "UPDATE customer_" + companyID + "_binding_tbl SET user_status = ?, user_remark = ?, exit_mailing_id = ?, user_type = ?, " + AgnUtils.changeDateName() + " = CURRENT_TIMESTAMP WHERE customer_id = ? AND mailinglist_id = ? AND mediatype = ?";
			int touchedLines = update(logger,
				sql,
				entry.getUserStatus(),
				entry.getUserRemark(),
				entry.getExitMailingID(),
				entry.getUserType(),
				entry.getCustomerID(),
				entry.getMailinglistID(),
				entry.getMediaType());

			return touchedLines >= 1;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean insertNewBinding(BindingEntry entry, @VelocityCheck int companyID) {
		try {
			String insertSql = "INSERT INTO customer_" + companyID + "_binding_tbl (mailinglist_id, customer_id, user_type, user_status, " + AgnUtils.changeDateName() + ", user_remark, creation_date, exit_mailing_id, mediatype) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?, ?)";
			update(logger,
				insertSql,
				entry.getMailinglistID(),
				entry.getCustomerID(),
				entry.getUserType(),
				entry.getUserStatus(),
				entry.getUserRemark(),
				entry.getExitMailingID(),
				entry.getMediaType());
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean updateStatus(BindingEntry entry, @VelocityCheck int companyID) {
		try {
			String sqlUpdateStatus = "UPDATE customer_" + companyID + "_binding_tbl SET user_status = ?, exit_mailing_id = ?, user_remark = ?, " + AgnUtils.changeDateName() + " = CURRENT_TIMESTAMP WHERE customer_id = ? AND mailinglist_id = ? AND mediatype = ?";
			int touchedLines = update(logger,
				sqlUpdateStatus,
				entry.getUserStatus(),
				entry.getExitMailingID(),
				entry.getUserRemark(),
				entry.getCustomerID(),
				entry.getMailinglistID(),
				entry.getMediaType());
			return touchedLines >= 1;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean optOutEmailAdr(String email, @VelocityCheck int companyID) {
		String operator;
		if (email.contains("%") || email.contains("_")) {
			operator = "LIKE";
		} else {
			operator = "=";
		}

		try {
			String sql = "UPDATE customer_" + companyID + "_binding_tbl SET user_status = ? WHERE customer_id IN (SELECT customer_id FROM customer_" + companyID + "_tbl WHERE email " + operator + " ?)";
			int touchedLines = update(logger, sql, BindingEntry.USER_STATUS_ADMINOUT, email);
			return touchedLines >= 1;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean addTargetsToMailinglist(@VelocityCheck int companyID, int mailinglistID, Target target) {
		try {
			String sql = "INSERT INTO customer_" + companyID + "_binding_tbl (customer_id, mailinglist_id, user_type, user_status, user_remark, " + AgnUtils.changeDateName() + ", exit_mailing_id, creation_date, mediatype) (SELECT cust.customer_id, " + mailinglistID + ", 'W', 1, " + "'From Target " + target.getId() + "', CURRENT_TIMESTAMP, 0, CURRENT_TIMESTAMP, 0 FROM customer_" + companyID + "_tbl cust WHERE " + target.getTargetSQL() + ")";
			update(logger, sql);
			return true;
		} catch (Exception e3) {
			return false;
		}
	}

	@Override
	public boolean getUserBindingFromDB(BindingEntry entry, @VelocityCheck int companyID) {
		String sqlGetBinding = "SELECT * FROM customer_" + companyID + "_binding_tbl WHERE mailinglist_id = ? AND customer_id = ? AND mediatype = ?";
		List<BindingEntry> list = select(logger, sqlGetBinding, new BindingEntry_RowMapper(this), entry.getMailinglistID(), entry.getCustomerID(), entry.getMediaType());
		if (list.size() > 0) {
			BindingEntry foundEntry = list.get(0);
			entry.setUserType(foundEntry.getUserType());
            entry.setUserStatus(foundEntry.getUserStatus());
            entry.setUserRemark(foundEntry.getUserRemark());
            entry.setChangeDate(foundEntry.getChangeDate());
            entry.setExitMailingID(foundEntry.getExitMailingID());
            entry.setCreationDate(foundEntry.getCreationDate());
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean exist(int customerId, @VelocityCheck int companyId, int mailinglistId, int mediatype) {
		String sql = "SELECT COUNT(*) FROM customer_" + companyId + "_binding_tbl WHERE customer_id = ? AND mailinglist_id = ? AND mediatype = ?";
		return selectInt(logger, sql, customerId, mailinglistId, mediatype) > 0;
	}

	@Override
	public boolean exist(@VelocityCheck int companyId, int mailinglistId) {
		String sql = "SELECT COUNT(*) FROM customer_" + companyId + "_binding_tbl WHERE mailinglist_id = ?";
		return selectInt(logger, sql, mailinglistId) > 0;
	}

	@Override
	public void delete(int customerId, @VelocityCheck int companyId, int mailinglistId, int mediatype) {
		String sql = "DELETE FROM customer_" + companyId + "_binding_tbl WHERE customer_id = ? AND mailinglist_id = ? AND mediatype = ?";
		update(logger, sql, customerId, mailinglistId, mediatype);
	}

	@Override
	public List<BindingEntry> getBindings(@VelocityCheck int companyID, int recipientID) {
		String sql = "SELECT customer_id, mailinglist_id, mediatype, user_type, user_status, " + AgnUtils.changeDateName() + ", exit_mailing_id, user_remark, creation_date FROM customer_" + companyID + "_binding_tbl WHERE customer_id = ?";
		return select(logger, sql, new BindingEntry_RowMapper(this), recipientID);
	}

	@Override
	public void updateBindingStatusByEmailPattern(@VelocityCheck int companyId, String emailPattern, int userStatus, String remark) throws BindingEntryDaoException {
		String sql = "UPDATE customer_" + companyId + "_binding_tbl " + "SET user_status = ?, user_remark = ?, " + AgnUtils.changeDateName() + " = CURRENT_TIMESTAMP WHERE customer_id IN (SELECT customer_id FROM customer_" + companyId + "_tbl WHERE email LIKE ?)";
		update(logger, sql, userStatus, remark, emailPattern);
	}

	protected class BindingEntry_RowMapper implements ParameterizedRowMapper<BindingEntry> {
		private BindingEntryDao bindingEntryDao;
		
		public BindingEntry_RowMapper(BindingEntryDao bindingEntryDao) {
			this.bindingEntryDao = bindingEntryDao;
		}
		
		@Override
		public BindingEntry mapRow(ResultSet resultSet, int row) throws SQLException {
			BindingEntry readEntry = new BindingEntryImpl();
			readEntry.setBindingEntryDao(bindingEntryDao);
			
			readEntry.setCustomerID(resultSet.getInt("customer_id"));
			readEntry.setMailinglistID(resultSet.getInt("mailinglist_id"));
			readEntry.setMediaType(resultSet.getInt("mediatype"));
			readEntry.setUserType(resultSet.getString("user_type"));
			readEntry.setUserStatus(resultSet.getInt("user_status"));
			readEntry.setChangeDate(resultSet.getTimestamp(AgnUtils.changeDateName()));
			readEntry.setExitMailingID(resultSet.getInt("exit_mailing_id"));
			if (resultSet.wasNull()) {
				readEntry.setExitMailingID(0);
			}
			readEntry.setUserRemark(resultSet.getString("user_remark"));
			readEntry.setCreationDate(resultSet.getTimestamp("creation_date"));

			return readEntry;
		}
	}
}
