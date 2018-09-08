package org.agnitas.emm.springws.endpoint.recipient;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.agnitas.emm.core.recipient.service.RecipientBulkService;
import org.agnitas.emm.core.recipient.service.RecipientModel;
import org.agnitas.emm.springws.exceptionresolver.BulkExceptionResolver;
import org.agnitas.emm.springws.jaxb.DeleteSubscriberBulkRequest;
import org.agnitas.emm.springws.jaxb.DeleteSubscriberBulkResponse;
import org.agnitas.emm.springws.jaxb.DeleteSubscriberBulkResponse.Items.Item;
import org.agnitas.emm.springws.jaxb.DeleteSubscriberRequest;
import org.agnitas.emm.springws.jaxb.DeleteSubscriberResponse;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class DeleteSubscriberBulkEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private RecipientBulkService recipientBulkService;
	@Resource
	private ObjectFactory objectFactory; 
	@Resource
	private BulkExceptionResolver bulkExceptionResolver;
	
	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		DeleteSubscriberBulkRequest request = (DeleteSubscriberBulkRequest) arg0;
		DeleteSubscriberBulkResponse response = objectFactory.createDeleteSubscriberBulkResponse();
		response.setItems(objectFactory.createDeleteSubscriberBulkResponseItems());
		List<DeleteSubscriberRequest> list = request.getItems().getDeleteSubscriberRequest();
		List<RecipientModel> models = new ArrayList<RecipientModel>(list.size());
		for (DeleteSubscriberRequest recipient : list) {
			models.add(DeleteSubscriberEndpoint.parseModel(recipient));
		}
		
		List<Object> result = recipientBulkService.deleteSubscriber(models, request.isIgnoreErrors());
		
		List<Item> items = response.getItems().getItem();
		for (Object entry : result) {
			Item item = objectFactory.createDeleteSubscriberBulkResponseItemsItem();
			if (entry instanceof Exception) {
				item.setError(bulkExceptionResolver.processEndpointException(this, (Exception) entry));
			} else {
				item.setDeleteSubscriberResponse(convertResultObject(entry));
			}
			items.add(item);
		}
		
		return response;
	}
	
	private DeleteSubscriberResponse convertResultObject(Object object) {
		DeleteSubscriberResponse response = objectFactory.createDeleteSubscriberResponse();
		return response;
	}

}

