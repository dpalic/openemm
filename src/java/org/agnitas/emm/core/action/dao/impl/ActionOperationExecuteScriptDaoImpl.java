package org.agnitas.emm.core.action.dao.impl;

import java.util.Map;

import org.agnitas.emm.core.action.operations.ActionOperationExecuteScript;

public class ActionOperationExecuteScriptDaoImpl extends AbstractActionOperationDaoImpl<ActionOperationExecuteScript> {

	@Override
	protected void processGetOperation(ActionOperationExecuteScript operation) {
		Map<String, Object> row = jdbcTemplate.queryForMap("select script from actop_execute_script_tbl where action_operation_id=?", operation.getId());
		operation.setScript((String) row.get("script"));
	}

	@Override
	protected void processSaveOperation(ActionOperationExecuteScript operation) {
		jdbcTemplate.update("insert into actop_execute_script_tbl (action_operation_id, script) values (?, ?)", operation.getId(), operation.getScript());
	}

	@Override
	protected void processUpdateOperation(ActionOperationExecuteScript operation) {
		jdbcTemplate.update("update actop_execute_script_tbl set script=? where action_operation_id=?", operation.getScript(), operation.getId());
	}

	@Override
	protected void processDeleteOperation(ActionOperationExecuteScript operation) {
		jdbcTemplate.update("delete from actop_execute_script_tbl where action_operation_id=?", operation.getId());
	}

}
