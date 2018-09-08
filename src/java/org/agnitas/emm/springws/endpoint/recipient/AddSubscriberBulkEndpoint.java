package org.agnitas.emm.springws.endpoint.recipient;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.agnitas.emm.core.recipient.service.RecipientBulkService;
import org.agnitas.emm.core.recipient.service.RecipientModel;
import org.agnitas.emm.springws.exceptionresolver.BulkExceptionResolver;
import org.agnitas.emm.springws.jaxb.AddSubscriberBulkRequest;
import org.agnitas.emm.springws.jaxb.AddSubscriberBulkResponse;
import org.agnitas.emm.springws.jaxb.AddSubscriberBulkResponse.Items.Item;
import org.agnitas.emm.springws.jaxb.AddSubscriberRequest;
import org.agnitas.emm.springws.jaxb.AddSubscriberResponse;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class AddSubscriberBulkEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private RecipientBulkService recipientBulkService;
	@Resource
	private ObjectFactory objectFactory; 
	@Resource
	private BulkExceptionResolver bulkExceptionResolver;

	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		AddSubscriberBulkRequest request = (AddSubscriberBulkRequest) arg0;
		AddSubscriberBulkResponse response = objectFactory.createAddSubscriberBulkResponse();
		response.setItems(objectFactory.createAddSubscriberBulkResponseItems());
		List<AddSubscriberRequest> list = request.getItems().getAddSubscriberRequest();
		List<RecipientModel> models = new ArrayList<RecipientModel>(list.size());
		for (AddSubscriberRequest recipient : list) {
			models.add(AddSubscriberEndpoint.parseModel(recipient));
		}

		List<Object> result = recipientBulkService.addSubscriber(models, request.isIgnoreErrors());
		
		List<Item> items = response.getItems().getItem();
		for (Object entry : result) {
			Item item = objectFactory.createAddSubscriberBulkResponseItemsItem();
			if (entry instanceof Exception) {
				item.setError(bulkExceptionResolver.processEndpointException(this, (Exception) entry));
			} else {
				item.setAddSubscriberResponse(convertResultObject(entry));
			}
			items.add(item);
		}
		
		return response;
	}
	
	private AddSubscriberResponse convertResultObject(Object object) {
		AddSubscriberResponse response = objectFactory.createAddSubscriberResponse();
		response.setCustomerID((Integer) object);
		return response;
	}

}

