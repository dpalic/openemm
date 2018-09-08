package org.agnitas.emm.core.action.dao.impl;

import java.util.Map;

import org.agnitas.emm.core.action.operations.ActionOperationServiceMail;

public class ActionOperationServiceMailDaoImpl extends AbstractActionOperationDaoImpl<ActionOperationServiceMail> {
	
	@Override
	protected void processGetOperation(ActionOperationServiceMail operation) {
		Map<String, Object> row = jdbcTemplate.queryForMap("select text_mail, subject_line, to_addr, mailtype, html_mail from actop_service_mail_tbl where action_operation_id=?", operation.getId());
		operation.setTextMail((String) row.get("text_mail"));
		operation.setSubjectLine((String) row.get("subject_line"));
		operation.setToAdr((String) row.get("to_addr"));
		operation.setMailtype(((Number) row.get("mailtype")).intValue());
		operation.setHtmlMail((String) row.get("html_mail"));
	}

	@Override
	protected void processSaveOperation(ActionOperationServiceMail operation) {
		jdbcTemplate.update("insert into actop_service_mail_tbl (action_operation_id, text_mail, subject_line, to_addr, mailtype, html_mail) values (?,?,?,?,?,?)", 
				operation.getId(), 
				operation.getTextMail(),
				operation.getSubjectLine(),
				operation.getToAdr(),
				operation.getMailtype(),
				operation.getHtmlMail());
	}

	@Override
	protected void processUpdateOperation(ActionOperationServiceMail operation) {
		jdbcTemplate.update("update actop_service_mail_tbl set text_mail=?, subject_line=?, to_addr=?, mailtype=?, html_mail=? where action_operation_id=?", 
				operation.getTextMail(),
				operation.getSubjectLine(),
				operation.getToAdr(),
				operation.getMailtype(),
				operation.getHtmlMail(),
				operation.getId());
	}

	@Override
	protected void processDeleteOperation(ActionOperationServiceMail operation) {
		jdbcTemplate.update("delete from actop_service_mail_tbl where action_operation_id=?", operation.getId());
	}

}
