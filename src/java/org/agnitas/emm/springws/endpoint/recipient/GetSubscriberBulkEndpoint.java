package org.agnitas.emm.springws.endpoint.recipient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.agnitas.emm.core.recipient.service.RecipientBulkService;
import org.agnitas.emm.core.recipient.service.RecipientModel;
import org.agnitas.emm.springws.exceptionresolver.BulkExceptionResolver;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.agnitas.emm.springws.jaxb.GetSubscriberBulkRequest;
import org.agnitas.emm.springws.jaxb.GetSubscriberBulkResponse;
import org.agnitas.emm.springws.jaxb.GetSubscriberRequest;
import org.agnitas.emm.springws.jaxb.GetSubscriberResponse;
import org.agnitas.emm.springws.jaxb.GetSubscriberBulkResponse.Items.Item;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class GetSubscriberBulkEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private RecipientBulkService recipientBulkService;
	@Resource
	private ObjectFactory objectFactory; 
	@Resource
	private BulkExceptionResolver bulkExceptionResolver;
	
	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		GetSubscriberBulkRequest request = (GetSubscriberBulkRequest) arg0;
		GetSubscriberBulkResponse response = objectFactory.createGetSubscriberBulkResponse();
		response.setItems(objectFactory.createGetSubscriberBulkResponseItems());
		List<GetSubscriberRequest> list = request.getItems().getGetSubscriberRequest();
		List<RecipientModel> models = new ArrayList<RecipientModel>(list.size());
		for (GetSubscriberRequest recipient : list) {
			models.add(GetSubscriberEndpoint.parseModel(recipient));
		}
		
		List<Object> result = recipientBulkService.getSubscriber(models);
		
		List<Item> items = response.getItems().getItem();
		int i = 0;
		for (Object entry : result) {
			GetSubscriberRequest requestItem = list.get(i);
			Item item = objectFactory.createGetSubscriberBulkResponseItemsItem();
			if (entry instanceof Exception) {
				item.setError(bulkExceptionResolver.processEndpointException(this, (Exception) entry));
			} else {
				GetSubscriberResponse responseItem = objectFactory.createGetSubscriberResponse();
				@SuppressWarnings("unchecked")
				Map<String, Object> parameters = (Map<String, Object>) entry;
				GetSubscriberEndpoint.populateResponse(requestItem, responseItem, parameters, objectFactory);
				item.setGetSubscriberResponse(responseItem);
			}
			items.add(item);
		}

		return response;
	}
	
}

