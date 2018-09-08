package org.agnitas.emm.core.action.service.impl;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.agnitas.beans.Recipient;
import org.agnitas.emm.core.action.operations.AbstractActionOperation;
import org.agnitas.emm.core.action.operations.ActionOperationServiceMail;
import org.agnitas.emm.core.action.service.EmmActionOperation;
import org.agnitas.emm.core.util.service.SendEmailService;
import org.agnitas.emm.core.velocity.VelocityResult;
import org.agnitas.emm.core.velocity.VelocitySpringUtils;
import org.agnitas.emm.core.velocity.VelocityWrapper;
import org.agnitas.emm.core.velocity.VelocityWrapperFactory;
import org.agnitas.util.AgnUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ActionOperationServiceMailImpl implements EmmActionOperation, ApplicationContextAware {
	
	private static final Logger logger = Logger.getLogger(ActionOperationServiceMailImpl.class);

	private ApplicationContext con;
	private SendEmailService sendEmailService;

	@Override
	public boolean execute(AbstractActionOperation operation, Map<String, Object> params) {
		
		ActionOperationServiceMail op =(ActionOperationServiceMail) operation;
		int companyID = op.getCompanyId();
		
        // String email_from; // =(String)params.get("from");  comes from customer-record...
        HashMap request=(HashMap)params.get("requestParameters");
        String toAdr = "";


        if(params.get("sendServiceMail")!=null && params.get("sendServiceMail").equals("no")) {
            return true; // do nothing, manually blocked
        }

        if(params.get("sendServiceMailToAdr")!=null) {
            toAdr=(String)params.get("sendServiceMailToAdr");
        } else {
            toAdr=op.getToAdr();
        }

        // check sender
        Recipient fromCust=(Recipient) con.getBean("Recipient");

        fromCust.setCompanyID(companyID);
        fromCust.loadCustDBStructure();

        if(params.get("customerID")!=null) {
            Integer tmpNum=(Integer)params.get("customerID");
            fromCust.setCustomerID(tmpNum.intValue());
            if(fromCust.getCustomerID()!=0) {
                fromCust.getCustomerDataFromDb();
            }
        }

        if(fromCust.getCustomerID()==0) {
            String tmpMail=null;
            if(request.get("fromEmail")!=null) {
                tmpMail=(String)request.get("fromEmail");
            }
            if(params.get("sendServiceMailFromAdr")!=null) {
                tmpMail=(String)params.get("sendServiceMailFromAdr");
            }
            if(tmpMail!=null) {
                tmpMail=tmpMail.trim().toLowerCase();
                if(!AgnUtils.checkEmailAdress(tmpMail)) {
                    return false;
                }
                fromCust.setCustParameters("email", tmpMail);
            } else {
                return false;
            }
        }

        if(fromCust.blacklistCheck()) {
            return false;
        }

        StringWriter aWriter=new StringWriter();
        String emailtext = "";
        String emailhtml = "";
        String subject;

        try {
        	VelocityWrapperFactory factory = VelocitySpringUtils.getVelocityWrapperFactory(con);
        	VelocityWrapper velocity = factory.getWrapper( companyID);
        	
        	VelocityResult velocityResult = velocity.evaluate( params, op.getTextMail(), aWriter);
        	if (velocityResult.hasErrors()) {
            	logger.error("Velocity errors: " + velocityResult.getErrors());
            	return false;
        	}
            emailtext=aWriter.toString();

            aWriter=new StringWriter();
            velocityResult = velocity.evaluate( params, op.getSubjectLine(), aWriter);
            if (velocityResult.hasErrors()) {
            	logger.error("Velocity errors: " + velocityResult.getErrors());
            	return false;
        	}
            subject=aWriter.toString();

            if(op.getMailtype()!=0) {
                aWriter=new StringWriter();
                velocityResult = velocity.evaluate( params, op.getHtmlMail(), aWriter);
                if (velocityResult.hasErrors()) {
                	logger.error("Velocity errors: " + velocityResult.getErrors());
                	return false;
            	}
                emailhtml=aWriter.toString();
            }
        } catch(Exception e) {
        	logger.error( "Velocity error", e);

            return false;
        }

        return sendEmailService.sendEmail(fromCust.getCustParameters("email"), toAdr, subject, emailtext, emailhtml, op.getMailtype(), "iso-8859-1");
	}
	
	@Override
	public void setApplicationContext(ApplicationContext con) throws BeansException {
		this.con = con;
	}

	public void setSendEmailService(SendEmailService sendEmailService) {
		this.sendEmailService = sendEmailService;
	}

}
