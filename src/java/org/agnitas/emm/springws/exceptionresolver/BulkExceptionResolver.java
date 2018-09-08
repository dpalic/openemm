package org.agnitas.emm.springws.exceptionresolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.OrderComparator;
import org.springframework.ws.soap.server.endpoint.SoapFaultDefinition;

public class BulkExceptionResolver implements ApplicationContextAware {
	private List<CommonExceptionResolver> endpointExceptionResolvers;

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		initEndpointExceptionResolvers(applicationContext);
	}

	private void initEndpointExceptionResolvers(ApplicationContext applicationContext) throws BeansException {
		if (endpointExceptionResolvers == null) {
			Map<String, CommonExceptionResolver> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, CommonExceptionResolver.class, true, false);
			endpointExceptionResolvers = new ArrayList<CommonExceptionResolver>(matchingBeans.values());
			Collections.sort(endpointExceptionResolvers, new OrderComparator());
		}
	}

	public String processEndpointException(Object endpoint, Exception ex) throws Exception {
		for (CommonExceptionResolver resolver : endpointExceptionResolvers) {
			SoapFaultDefinition faultDefinition = resolver.resolveException(endpoint, ex);
			if (faultDefinition != null) {
				return faultDefinition.getFaultStringOrReason();
			}
		}
		// exception not resolved
		throw ex;
	}
}
