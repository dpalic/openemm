package org.agnitas.emm.core.util.service.impl;

import org.agnitas.emm.core.util.service.SendEmailService;
import org.agnitas.util.AgnUtils;

public class SendEmailServiceImpl implements SendEmailService {

	@Override
	public boolean sendEmail(String fromAdr, String toAdrList, String subject, String bodyText, String bodyHtml,
			int mailtype, String charset) {
        return AgnUtils.sendEmail(fromAdr, toAdrList, subject, bodyText, bodyHtml, mailtype, charset);
	}

	@Override
	public boolean sendEmail(String fromAdr, String toAdrList, String ccAdrList, String subject, String bodyText,
			String bodyHtml, int mailtype, String charset) {
        return AgnUtils.sendEmail(fromAdr, toAdrList, ccAdrList, subject, bodyText, bodyHtml, mailtype, charset);
	}

}
