package org.agnitas.emm.core.action.service;

import java.util.Map;

import org.agnitas.emm.core.action.operations.AbstractActionOperation;

public interface EmmActionOperation {
	
	boolean execute(AbstractActionOperation operation, Map<String, Object> params);

}
