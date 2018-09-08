package org.agnitas.emm.core.action.service.impl;

import java.util.Map;

import org.agnitas.beans.BindingEntry;
import org.agnitas.beans.Mailing;
import org.agnitas.beans.Recipient;
import org.agnitas.dao.MailingDao;
import org.agnitas.emm.core.action.operations.AbstractActionOperation;
import org.agnitas.emm.core.action.operations.ActionOperationUnsubscribeCustomer;
import org.agnitas.emm.core.action.service.EmmActionOperation;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ActionOperationUnsubscribeCustomerImpl implements EmmActionOperation, ApplicationContextAware {

	private MailingDao mailingDao;
	private ApplicationContext con;

	@Override
	public boolean execute(AbstractActionOperation operation, Map<String, Object> params) {
		ActionOperationUnsubscribeCustomer op =(ActionOperationUnsubscribeCustomer) operation;
		int companyID = op.getCompanyId();

        int customerID=0;
        int mailingID=0;
        Integer tmpNum=null;
        Recipient aCust=(Recipient)con.getBean("Recipient");
        boolean returnValue=false;
        
        aCust.setCompanyID(companyID);
        if(params.get("customerID")!=null) {
            tmpNum=(Integer)params.get("customerID");
            customerID=tmpNum.intValue();
        }
        
        if(params.get("mailingID")!=null) {
            tmpNum=(Integer)params.get("mailingID");
            mailingID=tmpNum.intValue();
        }
        
        if(customerID!=0 && mailingID!=0) {
            aCust.setCustomerID(customerID);
            aCust.loadCustDBStructure();
            aCust.loadAllListBindings();
           
            Mailing aMailing=mailingDao.getMailing(mailingID, companyID);
            
            int mailinglistID=aMailing.getMailinglistID();
            Map<Integer, Map<Integer, BindingEntry>> aTbl = aCust.getListBindings();
            
            if (aTbl.containsKey(mailinglistID)) {
            	Map<Integer, BindingEntry> aTbl2 = aTbl.get(mailinglistID);
                if (aTbl2.containsKey(BindingEntry.MEDIATYPE_EMAIL)) {
                    BindingEntry aEntry = aTbl2.get(BindingEntry.MEDIATYPE_EMAIL);
                    switch(aEntry.getUserStatus()) {
                        case BindingEntry.USER_STATUS_ACTIVE:
                            aEntry.setUserStatus(BindingEntry.USER_STATUS_OPTOUT);
                            aEntry.setUserRemark("Opt-Out-Mailing: " + mailingID);
                            aEntry.setExitMailingID(mailingID);
                            aEntry.updateBindingInDB(companyID);
                            params.put("__agn_USER_STATUS", "" + BindingEntry.USER_STATUS_OPTOUT); // next Event-Mailing goes to a user with status 4
                            returnValue=true;
                            break;
                            
                        default:
                            returnValue=false;
                    }
                }
            }
        }
        
        return returnValue;

	}

	public void setMailingDao(MailingDao mailingDao) {
		this.mailingDao = mailingDao;
	}

	@Override
	public void setApplicationContext(ApplicationContext con) throws BeansException {
		this.con = con;
	}

}
