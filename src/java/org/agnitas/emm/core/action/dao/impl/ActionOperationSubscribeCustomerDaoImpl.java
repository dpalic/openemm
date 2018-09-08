package org.agnitas.emm.core.action.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.agnitas.emm.core.action.operations.ActionOperationSubscribeCustomer;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

public class ActionOperationSubscribeCustomerDaoImpl extends AbstractActionOperationDaoImpl<ActionOperationSubscribeCustomer> {
	
	@Override
	protected void processGetOperation(ActionOperationSubscribeCustomer operation) {
		Map<String, Object> row = jdbcTemplate.queryForObject("select double_check, key_column, double_opt_in from actop_subscribe_customer_tbl where action_operation_id=?", new ParameterizedRowMapper<Map<String, Object>>() {
			@Override
			public Map<String, Object> mapRow(ResultSet resultSet, int row) throws SQLException {
				Map<String, Object> map = new HashMap<String, Object>(3);
				map.put("double_check", resultSet.getBoolean("double_check"));
				map.put("key_column", resultSet.getString("key_column"));
				map.put("double_opt_in", resultSet.getBoolean("double_opt_in"));
				return map;
			}
		}, operation.getId());
		operation.setDoubleCheck((Boolean) row.get("double_check"));
		operation.setKeyColumn((String) row.get("key_column"));
		operation.setDoubleOptIn((Boolean) row.get("double_opt_in"));
	}

	@Override
	protected void processSaveOperation(ActionOperationSubscribeCustomer operation) {
		jdbcTemplate.update("insert into actop_subscribe_customer_tbl (action_operation_id, double_check, key_column, double_opt_in) values (?,?,?,?)", 
				operation.getId(), 
				operation.isDoubleCheck(),
				operation.getKeyColumn(),
				operation.isDoubleOptIn());
	}

	@Override
	protected void processUpdateOperation(ActionOperationSubscribeCustomer operation) {
		jdbcTemplate.update("update actop_subscribe_customer_tbl set double_check=?, key_column=?, double_opt_in=? where action_operation_id=?", 
				operation.isDoubleCheck(),
				operation.getKeyColumn(),
				operation.isDoubleOptIn(),
				operation.getId());
	}

	@Override
	protected void processDeleteOperation(ActionOperationSubscribeCustomer operation) {
		jdbcTemplate.update("delete from actop_subscribe_customer_tbl where action_operation_id=?", operation.getId());
	}

}
