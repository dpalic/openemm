package org.agnitas.emm.core.action.dao.impl;

import java.util.Map;

import org.agnitas.emm.core.action.operations.ActionOperationUpdateCustomer;

public class ActionOperationUpdateCustomerDaoImpl extends AbstractActionOperationDaoImpl<ActionOperationUpdateCustomer> {
	
	@Override
	protected void processGetOperation(ActionOperationUpdateCustomer operation) {
		Map<String, Object> row = jdbcTemplate.queryForMap("select column_name, column_type, update_type, update_value from actop_update_customer_tbl where action_operation_id=?", operation.getId());
		operation.setColumnName((String) row.get("column_name"));
		operation.setColumnType((String) row.get("column_type"));
		operation.setUpdateType(((Number) row.get("update_type")).intValue());
		operation.setUpdateValue((String) row.get("update_value"));
	}

	@Override
	protected void processSaveOperation(ActionOperationUpdateCustomer operation) {
		jdbcTemplate.update("insert into actop_update_customer_tbl (action_operation_id, column_name, column_type, update_type, update_value) values (?,?,?,?,?)", 
				operation.getId(), 
				operation.getColumnName(),
				operation.getColumnType(),
				operation.getUpdateType(),
				operation.getUpdateValue());
	}

	@Override
	protected void processUpdateOperation(ActionOperationUpdateCustomer operation) {
		jdbcTemplate.update("update actop_update_customer_tbl set column_name=?, column_type=?, update_type=?, update_value=? where action_operation_id=?", 
				operation.getColumnName(),
				operation.getColumnType(),
				operation.getUpdateType(),
				operation.getUpdateValue(),
				operation.getId());
	}

	@Override
	protected void processDeleteOperation(ActionOperationUpdateCustomer operation) {
		jdbcTemplate.update("delete from actop_update_customer_tbl where action_operation_id=?", operation.getId());
	}

}
