package org.agnitas.emm.springws.endpoint.binding;

import javax.annotation.Resource;

import org.agnitas.emm.core.commons.uid.ExtensibleUID;
import org.agnitas.emm.core.commons.uid.ExtensibleUIDService;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.emm.springws.jaxb.DecryptLinkDataRequest;
import org.agnitas.emm.springws.jaxb.DecryptLinkDataResponse;
import org.agnitas.emm.springws.jaxb.ObjectFactory;

public class DecryptLinkDataEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private ObjectFactory objectFactory;
	
	@Resource
	private ExtensibleUIDService uidService;

	@Override
	protected Object invokeInternal(Object requestObject) throws Exception {
		DecryptLinkDataRequest request = (DecryptLinkDataRequest) requestObject;
		DecryptLinkDataResponse response = objectFactory.createDecryptLinkDataResponse();

		ExtensibleUID uid = uidService.parse(request.getLinkparam());
		response.setCompanyID(Utils.getUserCompany());
		response.setCustomerID(uid.getCustomerID());
		response.setMailingID(uid.getMailingID());
		response.setUrlID(uid.getUrlID());

		return response;
	}

}
