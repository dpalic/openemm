package org.agnitas.emm.springws.exceptionresolver.recipient;

import org.agnitas.emm.core.recipient.service.impl.RecipientBulkNotAppliedException;
import org.agnitas.emm.springws.exceptionresolver.CommonExceptionResolver;
import org.springframework.ws.soap.server.endpoint.SoapFaultDefinition;

public class RecipientExceptionResolver extends CommonExceptionResolver {

	@Override
	protected SoapFaultDefinition getFaultDefinition(Object endpoint,
			Exception ex) {
		if (ex instanceof RecipientBulkNotAppliedException) {
			SoapFaultDefinition definition = getDefaultDefinition(ex);
			definition.setFaultStringOrReason("Data is correct, but transaction is aborted due to error in some other item");
			return definition;
		}
		return super.getFaultDefinition(endpoint, ex);
	}
}
