package org.agnitas.emm.core.action.service.impl;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.agnitas.beans.BindingEntry;
import org.agnitas.beans.Mailing;
import org.agnitas.beans.Recipient;
import org.agnitas.dao.MailingDao;
import org.agnitas.emm.core.action.operations.AbstractActionOperation;
import org.agnitas.emm.core.action.operations.ActionOperationActivateDoubleOptIn;
import org.agnitas.emm.core.action.service.EmmActionOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ActionOperationActivateDoubleOptInImpl implements EmmActionOperation, ApplicationContextAware {
	private static final transient Logger logger = Logger.getLogger(ActionOperationActivateDoubleOptInImpl.class);

	private MailingDao mailingDao;
	private ApplicationContext con;

	@Override
	public boolean execute(AbstractActionOperation operation, Map<String, Object> params) {
		ActionOperationActivateDoubleOptIn op =(ActionOperationActivateDoubleOptIn) operation;
		int companyID = op.getCompanyId();
		
        int customerID=0;
        int mailingID=0;
        Integer tmpNum=null;
        Recipient aCust=(Recipient)con.getBean("Recipient");
        boolean returnValue=false;
        HttpServletRequest request=(HttpServletRequest)params.get("_request");
        Mailing aMailing=null;

        aCust.setCompanyID(companyID);
        if(params.get("customerID")!=null) {
            tmpNum=(Integer)params.get("customerID");
            customerID=tmpNum.intValue();
        }
        if (customerID == 0) {
        	return false;
        }
        
        aCust.setCustomerID(customerID);
        aCust.loadCustDBStructure();
        aCust.loadAllListBindings();
        Map<Integer, Map<Integer, BindingEntry>> aTbl = aCust.getListBindings();
       
        if (op.isForAllLists()) {
        	for (Entry<Integer, Map<Integer, BindingEntry>> mailinglistEntry : aTbl.entrySet()) {
				Map<Integer, BindingEntry> bindingsMap = mailinglistEntry.getValue();
				for (Entry<Integer, BindingEntry> bindingMapEntry : bindingsMap.entrySet()) {
					BindingEntry aEntry = bindingMapEntry.getValue();
					logger.debug("ML: " + mailinglistEntry.getKey() + " BN: " 
							+ bindingMapEntry.getKey() + " ST: " + aEntry.getUserStatus());
					if (bindingMapEntry.getKey() == BindingEntry.MEDIATYPE_EMAIL) {
						returnValue |= changeBindingStatus(aEntry, companyID, request.getRemoteAddr());
					}
				}
			}
        	return returnValue;
        }

        	
        if(params.get("mailingID")!=null) {
            tmpNum=(Integer)params.get("mailingID");
            mailingID=tmpNum.intValue();
        }
        if (mailingID == 0) {
        	return false;
        }
        
//        if(customerID!=0 && mailingID!=0) {
//            aCust.setCustomerID(customerID);
//            aCust.loadCustDBStructure();
//            aCust.loadAllListBindings();
            
            aMailing=mailingDao.getMailing(mailingID, companyID);
            
            int mailinglistID=aMailing.getMailinglistID();
//            Map<Integer, Map<Integer, BindingEntry>> aTbl = aCust.getListBindings();
		    
            if (aTbl.containsKey(mailinglistID)) {
				Map<Integer, BindingEntry> aTbl2 = aTbl.get(mailinglistID);
			    if (aTbl2.containsKey(BindingEntry.MEDIATYPE_EMAIL)) {
			        BindingEntry aEntry = aTbl2.get(BindingEntry.MEDIATYPE_EMAIL);
			        
			        returnValue = changeBindingStatus(aEntry, companyID, request.getRemoteAddr());
			        
//			        switch(aEntry.getUserStatus()) {
//			            case BindingEntry.USER_STATUS_WAITING_FOR_CONFIRM:
//			                aEntry.setUserStatus(BindingEntry.USER_STATUS_ACTIVE);
//			                aEntry.setUserRemark("Opt-In-IP: " + request.getRemoteAddr());
//			                aEntry.updateStatusInDB(companyID);
//			                returnValue=true;
//			                break;
//			                
//			            case BindingEntry.USER_STATUS_ACTIVE:
//			                returnValue=true;
//			                break;
//			                
//			            default:
//			                returnValue=false;
//			        }
			    }
            }
        
//        }
        return returnValue;
	}
	
	private boolean changeBindingStatus(BindingEntry aEntry, int companyID, String remoteAddr) {
        switch(aEntry.getUserStatus()) {
            case BindingEntry.USER_STATUS_WAITING_FOR_CONFIRM:
                aEntry.setUserStatus(BindingEntry.USER_STATUS_ACTIVE);
                aEntry.setUserRemark("Opt-In-IP: " + remoteAddr);
                aEntry.updateStatusInDB(companyID);
                return  true;
                
            case BindingEntry.USER_STATUS_ACTIVE:
                return true;
                
            default:
                return false;
        }
	}

	public void setMailingDao(MailingDao mailingDao) {
		this.mailingDao = mailingDao;
	}

	@Override
	public void setApplicationContext(ApplicationContext con) throws BeansException {
		this.con = con;
	}

}
