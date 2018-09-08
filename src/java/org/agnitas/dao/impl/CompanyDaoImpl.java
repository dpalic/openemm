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

import org.agnitas.beans.Company;
import org.agnitas.beans.impl.CompanyImpl;
import org.agnitas.dao.CompanyDao;
import org.agnitas.emm.core.velocity.VelocityCheck;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

/**
 * 
 * @author mhe
 */
public class CompanyDaoImpl extends BaseDaoImpl implements CompanyDao {
	private static final transient Logger logger = Logger.getLogger(CompanyDaoImpl.class);

	@Override
	public Company getCompany(@VelocityCheck int companyID) {
		try {
			if (companyID == 0) {
				return null;
			} else {
				return selectObjectDefaultNull(
					logger,
					"SELECT company_id, creator_company_id, shortname, description, xor_key, rdir_domain, mailloop_domain, status, mailtracking, max_login_fails, login_block_time, uid_version, max_recipients FROM company_tbl WHERE company_id = ?",
					new Company_RowMapper(), companyID);
			}
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void saveCompany(Company company) {
		if (company.getId() != 1) {
			throw new NotImplementedException();
		} else if (getCompany(1) == null) {
			update(
				logger, 
				"INSERT INTO company_tbl (company_id, creator_company_id, shortname, description, xor_key, rdir_domain, mailloop_domain, status, mailtracking, max_login_fails, login_block_time, uid_version, max_recipients) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				company.getId(),
				company.getCreatorID(),
				company.getShortname(),
				company.getDescription(),
				company.getSecret(),
				company.getRdirDomain(),
				company.getMailloopDomain(),
				company.getStatus(),
				company.getMailtracking(),
				company.getMaxLoginFails(),
				company.getLoginBlockTime(),
				company.getMinimumSupportedUIDVersion(),
				company.getMaxRecipients());
		} else {
			update(
				logger, 
				"UPDATE company_tbl SET creator_company_id = ?, shortname = ?, description = ?, xor_key = ?, rdir_domain = ?, mailloop_domain = ?, status = ?, mailtracking = ?, max_login_fails = ?, login_block_time = ?, uid_version = ?, max_recipients = ? WHERE company_id = ?",
				company.getCreatorID(),
				company.getShortname(),
				company.getDescription(),
				company.getSecret(),
				company.getRdirDomain(),
				company.getMailloopDomain(),
				company.getStatus(),
				company.getMailtracking(),
				company.getMaxLoginFails(),
				company.getLoginBlockTime(),
				company.getMinimumSupportedUIDVersion(),
				company.getMaxRecipients(),
				company.getId());
		}
	}

	@Override
	public void deleteCompany(Company company) {
		if (company.getId() != 1) {
			throw new NotImplementedException();
		} else {
			update(logger, "DELETE FROM company_tbl WHERE company_id = ?", company.getId());
		}
	}

	protected class Company_RowMapper implements ParameterizedRowMapper<Company> {
		@Override
		public Company mapRow(ResultSet resultSet, int row) throws SQLException {
			Company readItem = new CompanyImpl();

			readItem.setId(resultSet.getInt("company_id"));
			readItem.setCreatorID(resultSet.getInt("creator_company_id"));
			readItem.setShortname(resultSet.getString("shortname"));
			readItem.setDescription(resultSet.getString("description"));
			readItem.setSecret(resultSet.getString("xor_key"));
			readItem.setRdirDomain(resultSet.getString("rdir_domain"));
			readItem.setMailloopDomain(resultSet.getString("mailloop_domain"));
			readItem.setStatus(resultSet.getString("status"));
			readItem.setMailtracking(resultSet.getInt("mailtracking"));
			readItem.setMaxLoginFails(resultSet.getInt("max_login_fails"));
			readItem.setLoginBlockTime(resultSet.getInt("login_block_time"));
			readItem.setMinimumSupportedUIDVersion(resultSet.getInt("uid_version"));
			readItem.setMaxRecipients(resultSet.getInt("max_recipients"));

			return readItem;
		}
	}
}
