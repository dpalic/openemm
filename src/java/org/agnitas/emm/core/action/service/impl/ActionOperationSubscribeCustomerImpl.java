package org.agnitas.emm.core.action.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.agnitas.beans.Company;
import org.agnitas.beans.Recipient;
import org.agnitas.dao.CompanyDao;
import org.agnitas.emm.core.action.operations.AbstractActionOperation;
import org.agnitas.emm.core.action.operations.ActionOperationSubscribeCustomer;
import org.agnitas.emm.core.action.service.EmmActionOperation;
import org.agnitas.emm.core.commons.uid.ExtensibleUID;
import org.agnitas.emm.core.commons.uid.ExtensibleUIDService;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ActionOperationSubscribeCustomerImpl implements EmmActionOperation, ApplicationContextAware {

	private static final Logger logger = Logger.getLogger(ActionOperationSubscribeCustomerImpl.class);
	
	private ApplicationContext con;
	private ExtensibleUIDService uidService;
	private CompanyDao companyDao;

	@Override
	public boolean execute(AbstractActionOperation operation, Map<String, Object> params) {
		
		ActionOperationSubscribeCustomer op =(ActionOperationSubscribeCustomer) operation;
        Integer tmpNum=null;
        Recipient aCust=(Recipient)con.getBean("Recipient");
        String keyVal=null;
        boolean isNewCust=false;
        boolean identifiedByUid=false;
        
        if(params.get("subscribeCustomer")!=null && params.get("subscribeCustomer").equals("no")) {
            return true; // do nothing, manually blocked
        }
        
        aCust.setCompanyID(operation.getCompanyId());
        aCust.loadCustDBStructure();

        if(params.get("customerID")!=null) {
            tmpNum=(Integer)params.get("customerID");
            aCust.setCustomerID(tmpNum.intValue());
            identifiedByUid=true;
        }

        if(aCust.getCustomerID()==0) {
            if(op.isDoubleCheck()) {
                Map req=new CaseInsensitiveMap((HashMap)params.get("requestParameters"));
                keyVal=(String)(req).get(op.getKeyColumn());
                aCust.findByKeyColumn(op.getKeyColumn(), keyVal);
            }
        }
        
        if(aCust.getCustomerID()!=0) {
            aCust.getCustomerDataFromDb();
        } else {
            isNewCust=true;
        }
        
        
        
        /* copy the request parameters into the customer */
		if(!aCust.importRequestParameters((HashMap)params.get("requestParameters"), null)) {
			return false;
		}
		
        /* is the email valid and not blacklisted? */
		if(!aCust.emailValid() || aCust.blacklistCheck()) {
			return false;	// abort, EMAIL is not allowed
		}
        
        if(!aCust.updateInDB()) {  // return error on failure
            return false;
        }
        
        aCust.loadAllListBindings();
        aCust.updateBindingsFromRequest(params, op.isDoubleOptIn(), identifiedByUid);
        
        if(op.isDoubleOptIn()) {
            params.put("__agn_USER_STATUS", "5"); // next Event-Mailing goes to a user with status 5
        }
        
        params.put("customerID", new Integer(aCust.getCustomerID()));

        if(isNewCust && aCust.getCustomerID()!=0) {
            // generate new agnUID
            try {
        	ExtensibleUID uid = uidService.newUID();
                uid.setCompanyID(op.getCompanyId());
                uid.setCustomerID(aCust.getCustomerID());
                Company company=companyDao.getCompany(op.getCompanyId());
                uid.setUrlID(0);
                uid.setMailingID(0);
				if( company != null) {
	                params.put("agnUID", uidService.buildUIDString(uid));
				}
            } catch (Exception e) {
                logger.error("executeOperation: "+e);
            }
        }
        
        return true;
	}

	@Override
	public void setApplicationContext(ApplicationContext con) throws BeansException {
		this.con = con;
	}

	public void setUidService(ExtensibleUIDService uidService) {
		this.uidService = uidService;
	}

	public void setCompanyDao(CompanyDao companyDao) {
		this.companyDao = companyDao;
	}

}
