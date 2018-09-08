package org.agnitas.emm.springws.endpoint.binding;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.agnitas.emm.core.binding.service.BindingBulkService;
import org.agnitas.emm.core.binding.service.BindingModel;
import org.agnitas.emm.springws.exceptionresolver.BulkExceptionResolver;
import org.agnitas.emm.springws.jaxb.DeleteSubscriberBindingBulkRequest;
import org.agnitas.emm.springws.jaxb.DeleteSubscriberBindingBulkResponse;
import org.agnitas.emm.springws.jaxb.DeleteSubscriberBindingRequest;
import org.agnitas.emm.springws.jaxb.DeleteSubscriberBindingResponse;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.agnitas.emm.springws.jaxb.DeleteSubscriberBindingBulkResponse.Items.Item;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class DeleteSubscriberBindingBulkEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private BindingBulkService bindingBulkService;
	@Resource
	private ObjectFactory objectFactory; 
	@Resource
	private BulkExceptionResolver bulkExceptionResolver;
	
	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		DeleteSubscriberBindingBulkRequest request = (DeleteSubscriberBindingBulkRequest) arg0;
		DeleteSubscriberBindingBulkResponse response = objectFactory.createDeleteSubscriberBindingBulkResponse();
		response.setItems(objectFactory.createDeleteSubscriberBindingBulkResponseItems());
		List<DeleteSubscriberBindingRequest> list = request.getItems().getDeleteSubscriberBindingRequest();
		List<BindingModel> models = new ArrayList<BindingModel>(list.size());
		for (DeleteSubscriberBindingRequest binding : list) {
			models.add(DeleteSubscriberBindingEndpoint.parseModel(binding));
		}
		
		List<Object> result = bindingBulkService.deleteBinding(models, request.isIgnoreErrors());
		
		List<Item> items = response.getItems().getItem();
		for (Object entry : result) {
			Item item = objectFactory.createDeleteSubscriberBindingBulkResponseItemsItem();
			if (entry instanceof Exception) {
				item.setError(bulkExceptionResolver.processEndpointException(this, (Exception) entry));
			} else {
				item.setDeleteSubscriberBindingResponse(convertResultObject(entry));
			}
			items.add(item);
		}
		
		return response;
	}

	private DeleteSubscriberBindingResponse convertResultObject(Object object) {
		return objectFactory.createDeleteSubscriberBindingResponse();
	}

}

