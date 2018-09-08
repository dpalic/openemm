package org.agnitas.emm.core.action.service;

import java.util.Map;

import org.agnitas.actions.EmmAction;
import org.agnitas.emm.core.velocity.VelocityCheck;

public interface EmmActionService {

    boolean executeActions(int actionID, @VelocityCheck int companyID, Map<String, Object> params);
    
    int copyEmmAction(EmmAction emmAction, int toCompanyId);
    
    int updateEmmAction(EmmAction fromEmmAction, EmmAction emmAction);
    
    int saveEmmAction(EmmAction action);

	EmmAction getEmmAction(int actionID, int companyID);

	boolean deleteEmmAction(int actionID, int companyID);
	
	EmmAction getEmmActionUnique(String shortName, @VelocityCheck int companyId);
    
}
