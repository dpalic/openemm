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

import java.io.ObjectInputStream;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.agnitas.actions.ActionOperation;
import org.agnitas.actions.EmmAction;
import org.agnitas.actions.impl.EmmActionImpl;
import org.agnitas.dao.EmmActionDao;
import org.agnitas.emm.core.velocity.VelocityCheck;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.ImportUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.displaytag.tags.TableTagParameters;
import org.displaytag.util.ParamEncoder;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

/**
    <class name="org.agnitas.actions.impl.EmmActionImpl" entity-name="EmmAction" table="rdir_action_tbl">
	<id name="id" column="action_id" type="integer" unsaved-value="0">
            <generator class="native"><param name="sequence">rdir_action_tbl_seq</param></generator>
        </id>
        <property name="companyID" column="company_id" type="integer" update="false"/>
        <property name="description" column="description" type="string"/>
        <property name="shortname" column="shortname" type="string"/>
        <property name="actions" column="operations" type="java.util.ArrayList"/>
        <property name="type" column="action_type" type="integer"/>
		<property name="creationDate" column="creation_date" type="timestamp" update="false" insert="true" />
        <property name="changeDate" column="change_date" type="timestamp"/>
    </class>
    
	SELECT action_id, company_id, description, shortname, operations, action_type FROM rdir_action_tbl WHERE action_id = ? AND company_id = ?
 *
 * @author mhe, Nicole Serek
 */
public class EmmActionDaoImpl extends BaseDaoImpl implements EmmActionDao {
	private static final transient Logger logger = Logger.getLogger(EmmActionDaoImpl.class);

	@Override
	public EmmAction getEmmAction(int actionID, @VelocityCheck int companyID) {
		if (actionID == 0 || companyID == 0) {
			return null;
		} else {
			try {
				String sql = "SELECT action_id, company_id, description, shortname, operations, action_type FROM rdir_action_tbl WHERE action_id = ? AND company_id = ?";
				List<EmmAction> actions = select(logger, sql, new EmmAction_RowMapper(), actionID, companyID);
				if (actions != null && actions.size() != 0) {
					return actions.get(0);
				} else {
					return null;
				}
			} catch (Exception e) {
				return null;
			}
		}
	}

	@Override
	public int saveEmmAction(EmmAction action) {
		if (action == null || action.getCompanyID() == 0) {
			return 0;
		} else {
			try {
				if (action.getId() == 0) {
					String sql = "INSERT INTO rdir_action_tbl (company_id, description, shortname, creation_date, change_date, action_type)"
						+ " VALUES (?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, ?)";
					logSqlStatement(logger, sql);
					SqlUpdate sqlUpdate = new SqlUpdate(getDataSource(), sql,
			                new int[]{Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.INTEGER});
					sqlUpdate.setReturnGeneratedKeys(true);
					sqlUpdate.setGeneratedKeysColumnNames(new String[] { "action_id" });
			        sqlUpdate.compile();
			        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
					
			        Object[] values = new Object[] {
						action.getCompanyID(),
						action.getDescription(),
						action.getShortname(),
						action.getType()};

			        int touchedLines = sqlUpdate.update(values, generatedKeyHolder);
			        int newID = generatedKeyHolder.getKey().intValue();
					
					if (touchedLines != 1)
						throw new RuntimeException("Illegal insert result");

					// set the new id to refresh the item
					action.setId(newID);
				} else {
					String updateSql = "UPDATE rdir_action_tbl SET description = ?, shortname = ?, change_date = CURRENT_TIMESTAMP, action_type = ? WHERE action_id = ? AND company_id = ?";
					update(logger, updateSql, action.getDescription(), action.getShortname(), action.getType(), action.getId(), action.getCompanyID());
				}
				
				updateBlob(logger, "UPDATE rdir_action_tbl SET operations = ? WHERE action_id = ?", ImportUtils.getObjectAsBytes(action.getActions()), action.getId());
				
				return action.getId();
			} catch (Exception e) {
				logger.error("Coulnd't save target_representation", e);
				throw (new RuntimeException(e));
			}
		}
	}

	@Override
	public boolean deleteEmmAction(int actionID, @VelocityCheck int companyID) {
		try {
			int touchedLines = update(logger, "UPDATE rdir_action_tbl SET deleted = 1 WHERE action_id = ? AND company_id = ?", actionID, companyID);
			return touchedLines > 0;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public List<EmmAction> getEmmActions(@VelocityCheck int companyID) {
		if (companyID == 0) {
			return null;
		} else {
			try {
				return select(logger, "SELECT action_id, company_id, description, shortname, operations, action_type FROM rdir_action_tbl WHERE company_id = ? and deleted = 0 ORDER BY shortname", new EmmAction_RowMapper(), companyID);
			} catch (Exception e) {
				return null;
			}
		}
	}

	@Override
	public List<EmmAction> getEmmActionsByName(@VelocityCheck int companyID, String shortName) {
		if (companyID == 0) {
			return null;
		} else {
			try {
				return select(logger, "SELECT action_id, company_id, description, shortname, operations, action_type FROM rdir_action_tbl WHERE company_id = ? and deleted = 0 and shortname = ?", new EmmAction_RowMapper(), companyID, shortName);
			} catch (Exception e) {
				return null;
			}
		}
	}

	@Override
	public List<EmmAction> getEmmNotFormActions(@VelocityCheck int companyID) {
		return select(logger, "SELECT action_id, company_id, description, shortname, operations, action_type FROM rdir_action_tbl WHERE company_id = ? and action_type != ? and deleted = 0 ORDER BY shortname", new EmmAction_RowMapper(), companyID, EmmAction.TYPE_FORM);
	}

	@Override
	public List<EmmAction> getEmmNotLinkActions(@VelocityCheck int companyID) {
		return select(logger, "SELECT action_id, company_id, description, shortname, operations, action_type FROM rdir_action_tbl WHERE company_id = ? and action_type != ? and deleted = 0 ORDER BY shortname", new EmmAction_RowMapper(), companyID, EmmAction.TYPE_LINK);
	}

	public Map<Integer, Integer> loadUsed(@VelocityCheck int companyID) {
		try {
			List<Map<String, Object>> result = select(logger,
				"SELECT r.action_id, COUNT(u.form_id) used"
				+ " FROM rdir_action_tbl r"
					+ " LEFT JOIN userform_tbl u ON ((u.startaction_id = r.action_id OR u.endaction_id = r.action_id) AND r.company_id = u.company_id)"
				+ " WHERE r.company_id = ?"
				+ " GROUP BY r.action_id",
				companyID);
			Map<Integer, Integer> used = new HashMap<Integer, Integer>();
			for (Map<String, Object> row : result) {
				int action_id = ((Number) row.get("action_id")).intValue();
				int count = ((Number) row.get("used")).intValue();
				used.put(action_id, count);
			}
			return used;
		} catch (Exception e) {
			return new HashMap<Integer, Integer>();
		}
	}

	@Override
	public String getUserFormNames(int actionId, @VelocityCheck int companyId) {
		String result = "";
		List<Map<String, Object>> resultList = select(logger, "SELECT formname FROM userform_tbl WHERE company_id = ? AND (startaction_id = ? OR endaction_id = ?) ORDER BY formname", companyId, actionId, actionId);
		for (Map<String, Object> row : resultList) {
			if (!result.equals("")) {
				result += "; ";
			}
			result += row.get("formname");
		}
		return result;
	}

	@Override
	public List<EmmAction> getActionList(HttpServletRequest request) {
		List<Integer> charColumns = Arrays.asList(new Integer[] { 0, 1, 2, 4, 5 });
		String[] columns = new String[] { "r.shortname", "r.shortname", "r.description", "r.shortname", "r.creation_date", "r.change_date", "" };

		int sortcolumnindex = 0;
		String sortParam = request.getParameter(new ParamEncoder("emmaction").encodeParameterName(TableTagParameters.PARAMETER_SORT));
		if (sortParam != null && !sortParam.equals("null")) {
			sortcolumnindex = Integer.parseInt(sortParam);
		}

		String sort = columns[sortcolumnindex];
		if (charColumns.contains(sortcolumnindex)) {
			sort = "UPPER( " + sort + " )";
		}

		int order = 1;
		String orderParam = request.getParameter(new ParamEncoder("emmaction").encodeParameterName(TableTagParameters.PARAMETER_ORDER));
		if (orderParam != null && !orderParam.equals("null")) {
			order = new Integer(orderParam);
		}

		String sqlStatement = "SELECT r.company_id, r.action_id, r.shortname, r.description, r.creation_date, r.change_date, count(u.form_id) used"
				+ " FROM rdir_action_tbl r"
					+ " LEFT JOIN userform_tbl u ON (u.startaction_id = r.action_id OR u.endaction_id = r.action_id)"
				+ " WHERE r.company_id = ? and r.deleted = 0"
				+ " GROUP BY r.company_id, r.action_id, r.shortname, r.description, r.creation_date, r.change_date"
				+ " ORDER BY " + sort + " " + (order == 1 ? "ASC" : "DESC");

		List<Map<String, Object>> resultList = select(logger, sqlStatement, AgnUtils.getCompanyID(request));

		List<EmmAction> result = new ArrayList<EmmAction>();
		for (Map<String, Object> row : resultList) {
			EmmAction newBean = new EmmActionImpl();
			newBean.setId(((Number) row.get("action_id")).intValue());
			newBean.setCompanyID(((Number) row.get("company_id")).intValue());
			newBean.setShortname((String) row.get("shortname"));
			newBean.setDescription((String) row.get("description"));
			newBean.setUsed(((Number) row.get("used")).intValue());
			newBean.setCreationDate((Timestamp) row.get("creation_date"));
			newBean.setChangeDate((Timestamp) row.get("change_date"));
			if (newBean.getUsed() > 0) {
				newBean.setFormNames(getUserFormNames(newBean.getId(), AgnUtils.getCompanyID(request)));
			} else {
				newBean.setFormNames("");
			}
			result.add(newBean);
		}
		return result;
	}

	protected class EmmAction_RowMapper implements ParameterizedRowMapper<EmmAction> {
		public EmmAction_RowMapper() {
		}
		
		@Override
		public EmmAction mapRow(ResultSet resultSet, int row) throws SQLException {
			EmmAction readItem = new EmmActionImpl();
			
			readItem.setId(resultSet.getInt("action_id"));
			readItem.setCompanyID(resultSet.getInt("company_id"));
			readItem.setShortname(resultSet.getString("shortname"));
			readItem.setDescription(resultSet.getString("description"));
			readItem.setType(resultSet.getInt("action_type"));
			
			Blob operationsBlob = resultSet.getBlob("operations");
			if (operationsBlob != null && operationsBlob.length() > 0) {
				ObjectInputStream stream = null;
				try {
					stream = new ObjectInputStream(operationsBlob.getBinaryStream());
					@SuppressWarnings("unchecked")
					List<ActionOperation> operations = (List<ActionOperation>) stream.readObject();
					stream.close();
					readItem.setActions(operations);
				} catch (Exception e) {
					throw new SQLException("Cannot read operations", e);
				} finally {
					IOUtils.closeQuietly(stream);
				}
			} else {
				readItem.setActions(null);
			}

			return readItem;
		}
	}
}
