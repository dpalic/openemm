package org.agnitas.emm.springws.endpoint.binding;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.agnitas.emm.core.binding.service.BindingBulkService;
import org.agnitas.emm.core.binding.service.BindingModel;
import org.agnitas.emm.springws.exceptionresolver.BulkExceptionResolver;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.agnitas.emm.springws.jaxb.SetSubscriberBindingBulkRequest;
import org.agnitas.emm.springws.jaxb.SetSubscriberBindingBulkResponse;
import org.agnitas.emm.springws.jaxb.SetSubscriberBindingRequest;
import org.agnitas.emm.springws.jaxb.SetSubscriberBindingBulkResponse.Items.Item;
import org.agnitas.emm.springws.jaxb.SetSubscriberBindingResponse;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class SetSubscriberBindingBulkEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private BindingBulkService bindingBulkService;
	@Resource
	private ObjectFactory objectFactory; 
	@Resource
	private BulkExceptionResolver bulkExceptionResolver;
	
	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		SetSubscriberBindingBulkRequest request = (SetSubscriberBindingBulkRequest) arg0;
		SetSubscriberBindingBulkResponse response = objectFactory.createSetSubscriberBindingBulkResponse();
		response.setItems(objectFactory.createSetSubscriberBindingBulkResponseItems());
		List<SetSubscriberBindingRequest> list = request.getItems().getSetSubscriberBindingRequest();
		List<BindingModel> models = new ArrayList<BindingModel>(list.size());
		for (SetSubscriberBindingRequest binding : list) {
			models.add(SetSubscriberBindingEndpoint.parseModel(binding));
		}

		List<Object> result = bindingBulkService.setBinding(models, request.isIgnoreErrors());
		
		List<Item> items = response.getItems().getItem();
		for (Object entry : result) {
			Item item = objectFactory.createSetSubscriberBindingBulkResponseItemsItem();
			if (entry instanceof Exception) {
				item.setError(bulkExceptionResolver.processEndpointException(this, (Exception) entry));
			} else {
				item.setSetSubscriberBindingResponse(convertResultObject(entry));
			}
			items.add(item);
		}

		return response;
	}

	private SetSubscriberBindingResponse convertResultObject(Object object) {
		return objectFactory.createSetSubscriberBindingResponse();
	}
	
}

