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
import java.util.Date;
import java.util.List;

import org.agnitas.beans.Mailinglist;
import org.agnitas.beans.impl.MailinglistImpl;
import org.agnitas.beans.impl.PaginatedListImpl;
import org.agnitas.dao.MailinglistDao;
import org.agnitas.dao.TargetDao;
import org.agnitas.emm.core.velocity.VelocityCheck;
import org.agnitas.target.Target;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.DbUtilities;
import org.agnitas.util.DbColumnType.SimpleDataType;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.displaytag.pagination.PaginatedList;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

/**
 * @author mhe
 */
public class MailinglistDaoImpl extends BaseDaoImpl implements MailinglistDao {
	private static final transient Logger logger = Logger.getLogger(MailinglistDaoImpl.class);

	private TargetDao targetDao;

	@Required
	public void setTargetDao(TargetDao targetDao) {
		this.targetDao = targetDao;
	}

	@Override
	public Mailinglist getMailinglist(int listID, @VelocityCheck int companyID) {
		if (listID == 0 || companyID == 0) {
			return null;
		} else {
			return selectObjectDefaultNull(logger, "SELECT mailinglist_id, company_id, shortname, description, creation_date, change_date FROM mailinglist_tbl WHERE mailinglist_id = ? AND deleted=0 AND company_id = ?", new Mailinglist_RowMapper(), listID, companyID);
		}
	}

	@Override
	public int saveMailinglist(Mailinglist list) {
		if (list == null || list.getCompanyID() == 0) {
			return 0;
		} else {
			list.setChangeDate(new Date());
			
			if (list.getId() == 0) {
				list.setCreationDate(new Date());
				
				// Execute insert
				if (AgnUtils.isOracleDB()) {
					int newID = selectInt(logger, "SELECT mailinglist_tbl_seq.NEXTVAL FROM DUAL");
					int touchedLines = update(
						logger, 
						"INSERT INTO mailinglist_tbl (mailinglist_id, company_id, shortname, description, creation_date, change_date) VALUES (?, ?, ?, ?, ?, ?)",
						newID,
						list.getCompanyID(),
						list.getShortname(),
						list.getDescription(),
						list.getCreationDate(),
						list.getChangeDate()
					);
					
					if (touchedLines == 1) {
						list.setId(newID);
					}
					
					return list.getId();
				} else {
					Object[] sqlParameters = new Object[]{ list.getCompanyID(), list.getShortname(), list.getDescription(), list.getCreationDate(), list.getChangeDate() };
					SqlUpdate sqlUpdate = new SqlUpdate(getDataSource(), "INSERT INTO mailinglist_tbl (company_id, shortname, description, creation_date, change_date) VALUES (?, ?, ?, ?, ?)", DbUtilities.getSqlTypes(sqlParameters));
					sqlUpdate.setReturnGeneratedKeys(true);
					sqlUpdate.setGeneratedKeysColumnNames(new String[] { "mailinglist_id" });
					sqlUpdate.compile();
					GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
					sqlUpdate.update(sqlParameters, keyHolder);
					int newID = keyHolder.getKey().intValue();
					list.setId(newID);
					return list.getId();
				}
			} else {
				//execute update
				int touchedLines = update(
					logger, 
					"UPDATE mailinglist_tbl SET shortname = ?, description = ?, creation_date = ?, change_date = ? WHERE mailinglist_id = ? AND deleted=0 AND company_id = ?",
					list.getShortname(),
					list.getDescription(),
					list.getCreationDate(),
					list.getChangeDate(),
					list.getId(),
					list.getCompanyID()
				);
				
				if (touchedLines == 1) {
					return list.getId();
				} else {
					return 0;
				}
			}
		}
	}

	@Override
	public boolean deleteMailinglist(int listID, @VelocityCheck int companyID) {
		return update(logger, "UPDATE mailinglist_tbl SET deleted = 1 WHERE mailinglist_id = ? AND company_id = ?", listID, companyID) > 0;
	}

	@Override
	public List<Mailinglist> getMailinglists(@VelocityCheck int companyID) {
		return select(logger, "SELECT mailinglist_id, company_id, shortname, description, creation_date, change_date FROM mailinglist_tbl WHERE deleted = 0 AND company_id = ? ORDER BY shortname ASC", new Mailinglist_RowMapper(), companyID);
	}

	@Override
	public PaginatedList getMailinglist(String sort, String direction, int page, int rownums, int companyID) throws IllegalAccessException, InstantiationException {
		String sortForQuery = StringUtils.isBlank(sort) ? "shortname" : sort;
		
		if (StringUtils.isBlank(direction) || (!"asc".equalsIgnoreCase(direction)
				 && !"desc".equalsIgnoreCase(direction))) {
			direction = "ASC";
		}
		
    	String sortClause;
    	try {
        	if (DbUtilities.getColumnDataType(getDataSource(), "mailinglist_tbl", sortForQuery).getSimpleDataType() 
        			== SimpleDataType.Characters) {
    			sortClause = " ORDER BY LOWER(" + sortForQuery + ")";
    		} else {
    			sortClause = " ORDER BY " + sortForQuery;
    		}
    	} catch (Exception e) {
    		sortClause = " ORDER BY LOWER(shortname)";
			logger.info("Invalid sort field", e);
		}
		sortClause = sortClause + " " + direction;


		String sqlStatement;

		int totalRows = selectInt(logger, "SELECT COUNT(*) FROM mailinglist_tbl WHERE deleted=0 AND company_id = ?", companyID);

		page = AgnUtils.getValidPageNumber(totalRows, page, rownums);

		int offset = (page - 1) * rownums;

		if (AgnUtils.isOracleDB()) {
			String sqlStatementInnerPart = "SELECT mailinglist_id, company_id, shortname, description, creation_date, change_date, rownum r FROM mailinglist_tbl WHERE deleted=0 AND company_id = ? " + sortClause;
			sqlStatement = "SELECT * FROM (" + sqlStatementInnerPart + ") WHERE r BETWEEN " + (offset + 1) + " AND " + (offset + rownums);
		} else {
			sqlStatement = "SELECT mailinglist_id, company_id, shortname, description, creation_date, change_date FROM mailinglist_tbl WHERE deleted=0 AND company_id = ?  " + sortClause + " LIMIT  " + offset + " , " + rownums;
		}

		List<Mailinglist> tmpList = select(logger, sqlStatement, new Mailinglist_RowMapper(), companyID);

		return new PaginatedListImpl<Mailinglist>(tmpList, totalRows, rownums, page, sort, direction);
	}

	/**
	 * deletes the bindings for this mailinglist (invocated before the mailinglist is deleted to avoid orphaned mailinglist bindings)
	 * 
	 * @return return code
	 */
	@Override
	public boolean deleteBindings(int id, @VelocityCheck int companyID) {
		try {
			return update(logger, "DELETE FROM customer_" + companyID + "_binding_tbl WHERE mailinglist_id = ?", id) > 0;
		} catch (Exception e) {
			// logging was already done
			return false;
		}
	}

	@Override
	public int getNumberOfActiveSubscribers(boolean admin, boolean test, boolean world, int targetID, @VelocityCheck int companyID, int id) {
		if (!world) {
			// do not use target-group if pure admin/test-mailing
			targetID = 0;
		}
		
		String sqlStatement = "SELECT COUNT(*) FROM customer_" + companyID + "_tbl cust, customer_" + companyID + "_binding_tbl bind WHERE bind.mailinglist_id = ? AND cust.customer_id = bind.customer_id AND bind.user_status = 1";
		
		if (targetID > 0) {
			Target aTarget = targetDao.getTarget(targetID, companyID);
			if (aTarget != null) {
				sqlStatement += " AND (" + aTarget.getTargetSQL() + ")";
			}
		}

		if (admin && !world) {
			if (!test) {
				sqlStatement += " AND bind.user_type = 'A'";
			} else {
				sqlStatement += " AND (bind.user_type = 'A' OR bind.user_type = 'T')";
			}
		}

		try {
			return selectInt(logger, sqlStatement, id);
		} catch (Exception e) {
			// logging was already done
			return 0;
		}
	}

	@Override
	public boolean mailinglistExists(String mailinglistName, @VelocityCheck int companyID) {
		return selectInt(logger, "SELECT COUNT(*) FROM mailinglist_tbl WHERE deleted=0 AND company_id = ? AND shortname = ?", companyID, mailinglistName) > 0;
	}

	@Override
	public boolean exist(int mailinglistID, @VelocityCheck int companyID) {
		return selectInt(logger, "SELECT COUNT(*) FROM mailinglist_tbl WHERE deleted=0 AND company_id = ? AND mailinglist_id = ?", companyID, mailinglistID) > 0;
	}
	
	protected class Mailinglist_RowMapper implements ParameterizedRowMapper<Mailinglist> {
		@Override
		public Mailinglist mapRow(ResultSet resultSet, int row) throws SQLException {
			Mailinglist readItem = new MailinglistImpl();
			
			readItem.setId(resultSet.getInt("mailinglist_id"));
			readItem.setCompanyID(resultSet.getInt("company_id"));
			readItem.setShortname(resultSet.getString("shortname"));
			readItem.setDescription(resultSet.getString("description"));
			readItem.setCreationDate(resultSet.getTimestamp("creation_date"));
			readItem.setChangeDate(resultSet.getTimestamp("change_date"));
			
			return readItem;
		}
	}
}
