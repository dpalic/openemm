package org.agnitas.emm.core.util.service;

public interface SendEmailService {

	boolean sendEmail(String from_adr, String to_adrList, String subject, String body_text, String body_html, int mailtype, String charset);
	
    boolean sendEmail(String from_adr, String to_adrList, String cc_adrList, String subject, String body_text, String body_html, int mailtype, String charset);
    
}
