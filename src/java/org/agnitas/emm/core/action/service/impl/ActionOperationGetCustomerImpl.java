package org.agnitas.emm.core.action.service.impl;

import java.util.Map;

import org.agnitas.beans.Recipient;
import org.agnitas.emm.core.action.operations.AbstractActionOperation;
import org.agnitas.emm.core.action.operations.ActionOperationGetCustomer;
import org.agnitas.emm.core.action.service.EmmActionOperation;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ActionOperationGetCustomerImpl implements EmmActionOperation, ApplicationContextAware {

//	private static final Logger logger = Logger.getLogger(ActionOperationGetCustomerImpl.class);
	
	private ApplicationContext con;

	@Override
	public boolean execute(AbstractActionOperation operation, Map<String, Object> params) {
		
		ActionOperationGetCustomer op =(ActionOperationGetCustomer) operation;
        int customerID=0;
        Integer tmpNum=null;
        Recipient aCust=(Recipient)con.getBean("Recipient");
        boolean returnValue=false;
        
        aCust.setCompanyID(op.getCompanyId());
        if(params.get("customerID")!=null) {
            tmpNum=(Integer)params.get("customerID");
            customerID=tmpNum.intValue();
        }
        
        if(customerID!=0) {
            aCust.setCustomerID(customerID);
            aCust.loadCustDBStructure();
            aCust.getCustomerDataFromDb();
            aCust.loadAllListBindings();
            if(op.isLoadAlways() || aCust.isActiveSubscriber()) {
                if(!aCust.getCustParameters().isEmpty()) {
                    params.put("customerData", aCust.getCustParameters());
                    params.put("customerBindings", aCust.getListBindings());
                    returnValue=true;
                }
            }
        }
        
        return returnValue;
	}

	@Override
	public void setApplicationContext(ApplicationContext con) throws BeansException {
		this.con = con;
	}

}
