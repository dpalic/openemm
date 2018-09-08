package org.agnitas.emm.springws.exceptionresolver.binding;

import org.agnitas.emm.core.commons.uid.parser.exception.DeprecatedUIDVersionException;
import org.agnitas.emm.core.commons.uid.parser.exception.InvalidUIDException;
import org.agnitas.emm.core.commons.uid.parser.exception.UIDParseException;
import org.agnitas.emm.core.mailing.service.MailingNotExistException;
import org.agnitas.emm.springws.exceptionresolver.CommonExceptionResolver;
import org.springframework.ws.soap.server.endpoint.SoapFaultDefinition;

public class BindingExceptionResolver extends CommonExceptionResolver {

	@Override
	protected SoapFaultDefinition getFaultDefinition(Object endpoint,
			Exception ex) {
		if (ex instanceof MailingNotExistException) {
			SoapFaultDefinition definition = getDefaultDefinition(ex);
			definition.setFaultStringOrReason("Unknown exit mailing ID");
			return definition;
		} else if (ex instanceof UIDParseException 
				|| ex instanceof InvalidUIDException 
				|| ex instanceof DeprecatedUIDVersionException) {
			SoapFaultDefinition definition = getDefaultDefinition(ex);
			definition.setFaultStringOrReason("Invalid UID");
			return definition;
		}
		return super.getFaultDefinition(endpoint, ex);
	}
}
