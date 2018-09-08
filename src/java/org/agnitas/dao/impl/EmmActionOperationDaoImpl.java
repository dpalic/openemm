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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.agnitas.beans.factory.ActionOperationFactory;
import org.agnitas.dao.EmmActionOperationDao;
import org.agnitas.emm.core.action.dao.ActionOperationDao;
import org.agnitas.emm.core.action.operations.AbstractActionOperation;
import org.agnitas.emm.core.velocity.VelocityCheck;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

public class EmmActionOperationDaoImpl extends BaseDaoImpl implements EmmActionOperationDao, InitializingBean {

	private static final String LIST_QUERY = "select action_operation_id, action_id, company_id, type from actop_tbl where action_id=? and company_id=? order by action_operation_id";
	private static final String INSERT_QUERY = "insert into actop_tbl (action_operation_id, action_id, company_id, type) values (?, ?, ?, ?)";
	private static final String GET_ID_QUERY = "select ifnull(max(action_operation_id) + 1, 1) from actop_tbl";
	private static final String DELETE_QUERY = "delete from actop_tbl where action_operation_id=? and company_id=?";

	private ActionOperationFactory actionOperationFactory;

	private SimpleJdbcTemplate jdbcTemplate;
	
	private Map<String, ActionOperationDao> daos = new HashMap<String, ActionOperationDao>();

	@Override
	public void afterPropertiesSet() throws Exception {
		jdbcTemplate = getSimpleJdbcTemplate();
	}

	@Override
	public List<AbstractActionOperation> getOperations(int actionId, @VelocityCheck int companyId) {
		List<Map<String, Object>> list = jdbcTemplate.queryForList(LIST_QUERY, actionId, companyId);
		List<AbstractActionOperation> resultList = new ArrayList<AbstractActionOperation>(list.size());
		for (Map<String, Object> row : list) {
			String type = (String) row.get("type");
			AbstractActionOperation actionOperation = (AbstractActionOperation) actionOperationFactory.newActionOperation(type);
			actionOperation.setId(((Number) row.get("action_operation_id")).intValue());
			actionOperation.setActionId(((Number) row.get("action_id")).intValue());
			actionOperation.setCompanyId(((Number) row.get("company_id")).intValue());
			ActionOperationDao dao = daos.get(type);
			// check for subtable dao
			if (dao != null) {
				dao.getOperation(actionOperation);
			}
			resultList.add(actionOperation);
		}
		return resultList;
	}
	
	protected String getIdQuery() {
		return GET_ID_QUERY;
	}
	
	@Override
	public void saveOperation(AbstractActionOperation operation) {
		ActionOperationDao dao = daos.get(operation.getType());
		if (operation.getId() == 0) {
			operation.setId(jdbcTemplate.queryForInt(getIdQuery()));
			jdbcTemplate.update(INSERT_QUERY, operation.getId(), operation.getActionId(), operation.getCompanyId(), operation.getType());
			// check for subtable dao
			if (dao != null) {
				dao.saveOperation(operation);
			}
		} else {
			// update only subtable
			if (dao != null) {
				dao.updateOperation(operation);
			}
		}
	}

	@Override
	public void deleteOperation(AbstractActionOperation operation) {
		ActionOperationDao dao = daos.get(operation.getType());
		if (dao != null) {
			dao.deleteOperation(operation);
		}
		jdbcTemplate.update(DELETE_QUERY, operation.getId(), operation.getCompanyId());
		
	}

	@Override
	public void deleteOperations(int actionID, int companyId) {
		List<AbstractActionOperation> operations = getOperations(actionID, companyId);
		for (AbstractActionOperation operation : operations) {
			deleteOperation(operation);
		}
	}

	public void setActionOperationFactory(ActionOperationFactory actionOperationFactory) {
		this.actionOperationFactory = actionOperationFactory;
	}

	public void setDaos(Map<String, ActionOperationDao> daos) {
		this.daos = daos;
	}

}
