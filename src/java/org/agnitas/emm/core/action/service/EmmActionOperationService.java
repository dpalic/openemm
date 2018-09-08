package org.agnitas.emm.core.action.service;

import java.util.Map;

import org.agnitas.actions.ActionOperation;
import org.agnitas.emm.core.action.operations.AbstractActionOperation;

public interface EmmActionOperationService {

	boolean executeOperation(AbstractActionOperation operation, Map<String, Object> params);
	
	AbstractActionOperation convert(ActionOperation operation) throws UnableConvertException;
	
	ActionOperation convert(AbstractActionOperation operation);
    
}
