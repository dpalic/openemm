package org.agnitas.emm.core.action.service.impl;

import java.util.Map;

import org.agnitas.beans.Mailing;
import org.agnitas.dao.MailingDao;
import org.agnitas.emm.core.action.operations.AbstractActionOperation;
import org.agnitas.emm.core.action.operations.ActionOperationSendMailing;
import org.agnitas.emm.core.action.service.EmmActionOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ActionOperationSendMailingImpl implements EmmActionOperation, ApplicationContextAware {
	
	private static final Logger logger = Logger.getLogger(ActionOperationSendMailingImpl.class);

	private MailingDao mailingDao;
	private ApplicationContext con;

	@Override
	public boolean execute(AbstractActionOperation operation, Map<String, Object> params) {
		
		ActionOperationSendMailing op =(ActionOperationSendMailing) operation;
		int companyID = op.getCompanyId();
		int mailingID = op.getMailingID();
		
        int customerID=0;
        Integer tmpNum=null;
        Mailing aMailing=null;
        boolean exitValue=false;
        String userStatus=null;

        if(params.get("customerID")==null) {
            return false;
        }
        tmpNum=(Integer)params.get("customerID");
        customerID=tmpNum.intValue();
        
        if(params.get("__agn_USER_STATUS")!=null) {
            userStatus=(String)params.get("__agn_USER_STATUS");
        }
        
        aMailing=mailingDao.getMailing(mailingID, companyID);
        if(aMailing!=null) {
            if(aMailing.sendEventMailing(customerID, op.getDelayMinutes(), userStatus, null, con)) {
            	if (logger.isInfoEnabled()) logger.info("executeOperation: Mailing "+mailingID+" to "+customerID+" sent");
                exitValue=true;
            } else {
            	logger.error("executeOperation: Mailing "+mailingID+" to "+customerID+" failed");
            }
        }
        return exitValue;
	}
	
	public void setMailingDao(MailingDao mailingDao) {
		this.mailingDao = mailingDao;
	}

	@Override
	public void setApplicationContext(ApplicationContext con) throws BeansException {
		this.con = con;
	}

}
