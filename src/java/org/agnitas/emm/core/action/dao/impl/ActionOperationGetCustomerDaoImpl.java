package org.agnitas.emm.core.action.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.agnitas.emm.core.action.operations.ActionOperationGetCustomer;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

public class ActionOperationGetCustomerDaoImpl extends AbstractActionOperationDaoImpl<ActionOperationGetCustomer> {
	
	@Override
	protected void processGetOperation(ActionOperationGetCustomer operation) {
		Map<String, Object> row = jdbcTemplate.queryForObject("select load_always from actop_get_customer_tbl where action_operation_id=?", new ParameterizedRowMapper<Map<String, Object>>() {
			@Override
			public Map<String, Object> mapRow(ResultSet resultSet, int row) throws SQLException {
				Map<String, Object> map = new HashMap<String, Object>(1);
				map.put("load_always", resultSet.getBoolean("load_always"));
				return map;
			}
		}, operation.getId());
		operation.setLoadAlways((Boolean) row.get("load_always"));
	}

	@Override
	protected void processSaveOperation(ActionOperationGetCustomer operation) {
		jdbcTemplate.update("insert into actop_get_customer_tbl (action_operation_id, load_always) values (?,?)", 
				operation.getId(), 
				operation.isLoadAlways());
	}

	@Override
	protected void processUpdateOperation(ActionOperationGetCustomer operation) {
		jdbcTemplate.update("update actop_get_customer_tbl set load_always=? where action_operation_id=?", 
				operation.isLoadAlways(),
				operation.getId());
	}

	@Override
	protected void processDeleteOperation(ActionOperationGetCustomer operation) {
		jdbcTemplate.update("delete from actop_get_customer_tbl where action_operation_id=?", operation.getId());
	}

}
