package org.agnitas.emm.springws.endpoint.binding;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.agnitas.beans.BindingEntry;
import org.agnitas.emm.core.binding.service.BindingBulkService;
import org.agnitas.emm.core.binding.service.BindingModel;
import org.agnitas.emm.springws.exceptionresolver.BulkExceptionResolver;
import org.agnitas.emm.springws.jaxb.ListSubscriberBindingBulkRequest;
import org.agnitas.emm.springws.jaxb.ListSubscriberBindingBulkResponse;
import org.agnitas.emm.springws.jaxb.ListSubscriberBindingBulkResponse.Items.Item;
import org.agnitas.emm.springws.jaxb.ListSubscriberBindingRequest;
import org.agnitas.emm.springws.jaxb.ListSubscriberBindingResponse;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class ListSubscriberBindingBulkEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private BindingBulkService bindingBulkService;
	@Resource
	private ObjectFactory objectFactory; 
	@Resource
	private BulkExceptionResolver bulkExceptionResolver;
	
	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		ListSubscriberBindingBulkRequest request = (ListSubscriberBindingBulkRequest) arg0;
		ListSubscriberBindingBulkResponse response = objectFactory.createListSubscriberBindingBulkResponse();
		response.setItems(objectFactory.createListSubscriberBindingBulkResponseItems());
		List<ListSubscriberBindingRequest> list = request.getItems().getListSubscriberBindingRequest();
		List<BindingModel> models = new ArrayList<BindingModel>(list.size());
		for (ListSubscriberBindingRequest binding : list) {
			models.add(ListSubscriberBindingEndpoint.parseModel(binding));
		}
		
		List<Object> result = bindingBulkService.getBindings(models);
		
		List<Item> items = response.getItems().getItem();
		for (Object entry : result) {
			Item item = objectFactory.createListSubscriberBindingBulkResponseItemsItem();
			if (entry instanceof Exception) {
				item.setError(bulkExceptionResolver.processEndpointException(this, (Exception) entry));
			} else {
				item.setListSubscriberBindingResponse(convertResultObject(entry));
			}
			items.add(item);
		}
		
		return response;
	}

	@SuppressWarnings("unchecked")
	private ListSubscriberBindingResponse convertResultObject(Object object) {
		ListSubscriberBindingResponse response = objectFactory.createListSubscriberBindingResponse();
		for (BindingEntry binding : (List<BindingEntry>) object) {
			response.getItem().add(new ResponseBuilder(objectFactory).createResponse(binding));
		}
		return response;
	}

}
