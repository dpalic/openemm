package org.agnitas.emm.springws.endpoint.recipient;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.agnitas.emm.core.recipient.service.RecipientBulkService;
import org.agnitas.emm.core.recipient.service.RecipientModel;
import org.agnitas.emm.springws.exceptionresolver.BulkExceptionResolver;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.agnitas.emm.springws.jaxb.UpdateSubscriberBulkRequest;
import org.agnitas.emm.springws.jaxb.UpdateSubscriberBulkResponse;
import org.agnitas.emm.springws.jaxb.UpdateSubscriberRequest;
import org.agnitas.emm.springws.jaxb.UpdateSubscriberResponse;
import org.agnitas.emm.springws.jaxb.UpdateSubscriberBulkResponse.Items.Item;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class UpdateSubscriberBulkEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private RecipientBulkService recipientBulkService;
	@Resource
	private ObjectFactory objectFactory; 
	@Resource
	private BulkExceptionResolver bulkExceptionResolver;
	
	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		UpdateSubscriberBulkRequest request = (UpdateSubscriberBulkRequest) arg0;
		UpdateSubscriberBulkResponse response = objectFactory.createUpdateSubscriberBulkResponse();
		response.setItems(objectFactory.createUpdateSubscriberBulkResponseItems());
		List<UpdateSubscriberRequest> list = request.getItems().getUpdateSubscriberRequest();
		List<RecipientModel> models = new ArrayList<RecipientModel>(list.size());
		for (UpdateSubscriberRequest recipient : list) {
			models.add(UpdateSubscriberEndpoint.parseModel(recipient));
		}
		
		List<Object> result = recipientBulkService.updateSubscriber(models, request.isIgnoreErrors());
		
		List<Item> items = response.getItems().getItem();
		for (Object entry : result) {
			Item item = objectFactory.createUpdateSubscriberBulkResponseItemsItem();
			if (entry instanceof Exception) {
				item.setError(bulkExceptionResolver.processEndpointException(this, (Exception) entry));
			} else {
				item.setUpdateSubscriberResponse(convertResultObject(entry));
			}
			items.add(item);
		}

		return response;
	}
	
	private UpdateSubscriberResponse convertResultObject(Object object) {
		UpdateSubscriberResponse response = objectFactory.createUpdateSubscriberResponse();
		response.setValue((Boolean) object);
		return response;
	}

}

