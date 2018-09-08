package org.agnitas.emm.core.action.dao.impl;

import java.util.Map;

import org.agnitas.emm.core.action.operations.ActionOperationSendMailing;

public class ActionOperationSendMailingDaoImpl extends AbstractActionOperationDaoImpl<ActionOperationSendMailing> {
	
	@Override
	protected void processGetOperation(ActionOperationSendMailing operation) {
		Map<String, Object> row = jdbcTemplate.queryForMap("select mailing_id, delay_minutes from actop_send_mailing_tbl where action_operation_id=?", operation.getId());
		operation.setMailingID(((Number) row.get("mailing_id")).intValue());
		operation.setDelayMinutes(((Number) row.get("delay_minutes")).intValue());
	}

	@Override
	protected void processSaveOperation(ActionOperationSendMailing operation) {
		jdbcTemplate.update("insert into actop_send_mailing_tbl (action_operation_id, mailing_id, delay_minutes) values (?,?,?)", 
				operation.getId(), 
				operation.getMailingID(),
				operation.getDelayMinutes());
	}

	@Override
	protected void processUpdateOperation(ActionOperationSendMailing operation) {
		jdbcTemplate.update("update actop_send_mailing_tbl set mailing_id=?, delay_minutes=? where action_operation_id=?", 
				operation.getMailingID(),
				operation.getDelayMinutes(),
				operation.getId());
	}

	@Override
	protected void processDeleteOperation(ActionOperationSendMailing operation) {
		jdbcTemplate.update("delete from actop_send_mailing_tbl where action_operation_id=?", operation.getId());
	}

}
