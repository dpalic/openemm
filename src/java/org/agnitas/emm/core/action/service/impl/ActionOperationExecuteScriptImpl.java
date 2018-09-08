package org.agnitas.emm.core.action.service.impl;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;

import org.agnitas.beans.BindingEntry;
import org.agnitas.beans.Mailing;
import org.agnitas.beans.Recipient;
import org.agnitas.dao.MailingDao;
import org.agnitas.emm.core.action.operations.AbstractActionOperation;
import org.agnitas.emm.core.action.operations.ActionOperationExecuteScript;
import org.agnitas.emm.core.action.service.EmmActionOperation;
import org.agnitas.emm.core.velocity.VelocityResult;
import org.agnitas.emm.core.velocity.VelocitySpringUtils;
import org.agnitas.emm.core.velocity.VelocityWrapper;
import org.agnitas.emm.core.velocity.VelocityWrapperFactory;
import org.agnitas.util.AgnUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ActionOperationExecuteScriptImpl implements EmmActionOperation, ApplicationContextAware {

	private static final Logger logger = Logger.getLogger(ActionOperationExecuteScriptImpl.class);
	
	private MailingDao mailingDao;
	private ApplicationContext con;

	@Override
	public boolean execute(AbstractActionOperation operation, Map<String, Object> params) {
		
		boolean result=false;
		
		ActionOperationExecuteScript op =(ActionOperationExecuteScript) operation;
		int companyID = op.getCompanyId();
		String script = op.getScript();
		
		Recipient cust=(Recipient) con.getBean("Recipient");
		cust.setCompanyID(companyID);
		params.put("Customer", cust);

		// neu von ma
		BindingEntry binding=(BindingEntry) con.getBean("BindingEntry");
		params.put("BindingEntry", binding);

		Mailing mail=(Mailing) con.getBean("Mailing");
		mail.setCompanyID(companyID);
		params.put("Mailing", mail);

		params.put("MailingDao", mailingDao);

		if(!params.containsKey("ScriptHelper")) {
			params.put("ScriptHelper", con.getBean("ScriptHelper"));
		}

		try {
			VelocityWrapperFactory factory = VelocitySpringUtils.getVelocityWrapperFactory(con);
			VelocityWrapper velocity = factory.getWrapper( companyID);
			
            StringWriter aWriter=new StringWriter();
            VelocityResult velocityResult = velocity.evaluate( params, script, aWriter);

            if(velocityResult.hasErrors()) {
            	Iterator it = velocityResult.getErrors().get();
            	while( it.hasNext()) {
            		logger.warn( "Error in velocity script: " + it.next());
            	}
            }

            if(params.containsKey("scriptResult")) {
                if(params.get("scriptResult").equals("1")) {
                    result=true;
                }
            }
        } catch(Exception e) {
        	logger.error( "Velocity error", e);

            params.put("velocity_error", AgnUtils.getUserErrorMessage(e));
            AgnUtils.sendVelocityExceptionMail((String) params.get("formURL"),e);
        }

		return result;
	}

	public void setMailingDao(MailingDao mailingDao) {
		this.mailingDao = mailingDao;
	}

	@Override
	public void setApplicationContext(ApplicationContext con) throws BeansException {
		this.con = con;
	}

}
