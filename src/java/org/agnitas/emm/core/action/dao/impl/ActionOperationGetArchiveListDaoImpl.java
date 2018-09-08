package org.agnitas.emm.core.action.dao.impl;

import java.util.Map;

import org.agnitas.emm.core.action.operations.ActionOperationGetArchiveList;

public class ActionOperationGetArchiveListDaoImpl extends AbstractActionOperationDaoImpl<ActionOperationGetArchiveList> {
	
	@Override
	protected void processGetOperation(ActionOperationGetArchiveList operation) {
		Map<String, Object> row = jdbcTemplate.queryForMap("select campaign_id from actop_get_archive_list_tbl where action_operation_id=?", operation.getId());
		operation.setCampaignID(((Number) row.get("campaign_id")).intValue());
	}

	@Override
	protected void processSaveOperation(ActionOperationGetArchiveList operation) {
		jdbcTemplate.update("insert into actop_get_archive_list_tbl (action_operation_id, campaign_id) values (?,?)", 
				operation.getId(), 
				operation.getCampaignID());
	}

	@Override
	protected void processUpdateOperation(ActionOperationGetArchiveList operation) {
		jdbcTemplate.update("update actop_get_archive_list_tbl set campaign_id=? where action_operation_id=?", 
				operation.getCampaignID(),
				operation.getId());
	}

	@Override
	protected void processDeleteOperation(ActionOperationGetArchiveList operation) {
		jdbcTemplate.update("delete from actop_get_archive_list_tbl where action_operation_id=?", operation.getId());
	}

}
