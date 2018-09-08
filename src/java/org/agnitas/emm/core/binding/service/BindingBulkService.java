package org.agnitas.emm.core.binding.service;

import java.util.List;

public interface BindingBulkService {

	List<Object> setBinding(List<BindingModel> models, boolean ignoreErrors);
	
	List<Object> deleteBinding(List<BindingModel> models, boolean ignoreErrors);

	List<Object> getBindings(List<BindingModel> models);
	
}
