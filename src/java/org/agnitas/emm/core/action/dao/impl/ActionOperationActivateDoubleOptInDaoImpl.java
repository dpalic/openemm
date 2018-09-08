package org.agnitas.emm.core.action.dao.impl;

import java.util.Map;

import org.agnitas.emm.core.action.operations.ActionOperationActivateDoubleOptIn;
import org.springframework.dao.EmptyResultDataAccessException;

public class ActionOperationActivateDoubleOptInDaoImpl extends AbstractActionOperationDaoImpl<ActionOperationActivateDoubleOptIn>  {

	@Override
	protected void processGetOperation(ActionOperationActivateDoubleOptIn operation) {
		try {
			Map<String, Object> row = jdbcTemplate.queryForMap("select for_all_lists from actop_activate_doi_tbl where action_operation_id=?", operation.getId());
			Object r = row.get("for_all_lists");
			operation.setForAllLists(((Integer)r).intValue() != 0);
		} catch (EmptyResultDataAccessException e) {
			operation.setForAllLists(false);
			processSaveOperation(operation);
		}
	}

	@Override
	protected void processSaveOperation(ActionOperationActivateDoubleOptIn operation) {
		jdbcTemplate.update("insert into actop_activate_doi_tbl (action_operation_id, for_all_lists) values (?,?)", 
				operation.getId(), 
				operation.isForAllLists());
	}

	@Override
	protected void processUpdateOperation(ActionOperationActivateDoubleOptIn operation) {
		jdbcTemplate.update("update actop_activate_doi_tbl set for_all_lists=? where action_operation_id=?", 
				operation.isForAllLists() ? 1 : 0,
				operation.getId());
	}

	@Override
	protected void processDeleteOperation(ActionOperationActivateDoubleOptIn operation) {
		jdbcTemplate.update("delete from actop_activate_doi_tbl where action_operation_id=?", operation.getId());
	}


}
