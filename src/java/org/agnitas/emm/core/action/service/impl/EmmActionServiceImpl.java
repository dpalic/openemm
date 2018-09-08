package org.agnitas.emm.core.action.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.agnitas.actions.ActionOperation;
import org.agnitas.actions.EmmAction;
import org.agnitas.dao.EmmActionDao;
import org.agnitas.dao.EmmActionOperationDao;
import org.agnitas.emm.core.action.operations.AbstractActionOperation;
import org.agnitas.emm.core.action.service.EmmActionOperationService;
import org.agnitas.emm.core.action.service.EmmActionService;
import org.agnitas.emm.core.commons.util.ConfigService;
import org.agnitas.emm.core.commons.util.ConfigService.Value;
import org.agnitas.emm.core.velocity.VelocityCheck;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Transactional;

public class EmmActionServiceImpl implements EmmActionService, ApplicationContextAware {
	
    protected EmmActionDao emmActionDao;
    private EmmActionOperationDao emmActionOperationDao;
    private ConfigService configService;
    
    private EmmActionOperationService emmActionOperationService; 

    private ApplicationContext applicationContext;

	@Override
	public boolean executeActions(int actionID, @VelocityCheck int companyID, Map<String, Object> params) {
    	if(actionID == 0) {
    		return false;
    	}
    	
    	List<AbstractActionOperation> operations = checkUseOldOperations(companyID) ? new ArrayList<AbstractActionOperation>() : emmActionOperationDao.getOperations(actionID, companyID);
    	if (!operations.isEmpty()) {
    		for (AbstractActionOperation operation : operations) {
    			if (!emmActionOperationService.executeOperation(operation, params)) {
    				return false;
    			}
			}
    	} else {
    		// Backward compatibility with old action operations
    		EmmAction emmAction = emmActionDao.getEmmAction(actionID, companyID);
    		if(emmAction == null) {
    			return false;
    		}
    		List<ActionOperation> actions = emmAction.getActions();
    		for (ActionOperation action : actions) {
    			if (!action.executeOperation(applicationContext, companyID, params)) {
    				return false;
    			}
			}
    	}
        return true;
	}
	
	private boolean checkUseOldOperations(int companyId) {
		String value = configService.getValue(Value.ActionOperationsEnableStoreOld, companyId);
		return value != null && value.equalsIgnoreCase(Boolean.TRUE.toString());
	}
	
	@Override
	@Transactional
	public int copyEmmAction(EmmAction emmAction, int toCompanyId) {
		List<AbstractActionOperation> ops 
			= emmActionOperationDao.getOperations(emmAction.getId(), emmAction.getCompanyID());
		for (AbstractActionOperation op : ops) {
			op.setId(0);
			op.setCompanyId(toCompanyId);
		}
		emmAction.setActionOperations(ops);
		emmAction.setId(0);
		emmAction.setCompanyID(toCompanyId);
		return saveEmmAction(emmAction);
	}
	
	@Override
	@Transactional
	public int updateEmmAction(EmmAction fromEmmAction, EmmAction emmAction) {
		List<AbstractActionOperation> ops 
			= emmActionOperationDao.getOperations(fromEmmAction.getId(), fromEmmAction.getCompanyID());
		for (AbstractActionOperation op : ops) {
			op.setId(0);
			op.setCompanyId(emmAction.getCompanyID());
		}
		emmAction.setActionOperations(ops);
		return saveEmmAction(emmAction);
	}
	
	@Override
	@Transactional
	public int saveEmmAction(EmmAction action) {
		boolean useOldOperations = checkUseOldOperations(action.getCompanyID());
		
		// Store operation in old format always
		ArrayList<ActionOperation> convertedList = new ArrayList<ActionOperation>(action.getActionOperations().size());
		for (AbstractActionOperation operation : action.getActionOperations()) {
			convertedList.add(emmActionOperationService.convert(operation));
		};
		action.setActions(convertedList);
		
		List<AbstractActionOperation> existingOperations = null;
		if (!useOldOperations) {
			existingOperations = action.getId() != 0 ? emmActionOperationDao.getOperations(action.getId(), action.getCompanyID()) : new ArrayList<AbstractActionOperation>();
		}
		int actionId = emmActionDao.saveEmmAction(action);
		if (!useOldOperations) {
			for (AbstractActionOperation operation : action.getActionOperations()) {
				operation.setActionId(actionId);
				emmActionOperationDao.saveOperation(operation);
			};
			for (AbstractActionOperation operation : existingOperations) {
				if (!action.getActionOperations().contains(operation)) {
					emmActionOperationDao.deleteOperation(operation);
				}
			};
		} else {
			emmActionOperationDao.deleteOperations(actionId, action.getCompanyID());
		}
		return actionId;
	}
	
	@Override
	public EmmAction getEmmAction(int actionID, @VelocityCheck int companyID) {
		EmmAction action = emmActionDao.getEmmAction(actionID, companyID);
		if (action != null) {
			List<AbstractActionOperation> operations = checkUseOldOperations(companyID) ? new ArrayList<AbstractActionOperation>() : emmActionOperationDao.getOperations(actionID, companyID);
			if (operations.isEmpty() && action.getActions() != null) {
				operations = new ArrayList<AbstractActionOperation>(action.getActions().size());
				for (ActionOperation actionOperation : action.getActions()) {
					AbstractActionOperation converted = emmActionOperationService.convert(actionOperation);
					converted.setActionId(actionID);
					converted.setCompanyId(companyID);
					operations.add(converted);
				}
			} 
			action.setActionOperations(operations);
		}
		return action;
	}
	
	@Override
	@Transactional
	public boolean deleteEmmAction(int actionID, @VelocityCheck int companyID) {
		emmActionOperationDao.deleteOperations(actionID, companyID);
		return emmActionDao.deleteEmmAction(actionID, companyID);
	}
	
	@Override
	public EmmAction getEmmActionUnique(String shortName, @VelocityCheck int companyId) {
		List<EmmAction> actionList = emmActionDao.getEmmActionsByName(companyId, shortName);
		if (actionList.size() != 1) {
			return null;
		}
		
		return actionList.get(0);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public void setEmmActionDao(EmmActionDao emmActionDao) {
		this.emmActionDao = emmActionDao;
	}

	public void setEmmActionOperationDao(EmmActionOperationDao emmActionOperationDao) {
		this.emmActionOperationDao = emmActionOperationDao;
	}

	public void setEmmActionOperationService(EmmActionOperationService emmActionOperationService) {
		this.emmActionOperationService = emmActionOperationService;
	}

	@Required
	public void setConfigService(ConfigService configService) {
		this.configService = configService;
	}
}
