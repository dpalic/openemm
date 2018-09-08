package org.agnitas.emm.core.binding.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.agnitas.emm.core.binding.service.BindingBulkService;
import org.agnitas.emm.core.binding.service.BindingModel;
import org.agnitas.emm.core.binding.service.BindingService;
import org.agnitas.emm.core.service.impl.AbstractBulkServiceImpl;

public class BindingBulkServiceImpl extends AbstractBulkServiceImpl<BindingModel> implements BindingBulkService {

	@Resource
	private BindingService bindingService;
	
	@Override
	public List<Object> setBinding(final List<BindingModel> models, boolean ignoreErrors) {
		return processBulk(models, ignoreErrors, new BulkOperationWithoutResult() {
			@Override
			protected void runWithoutResult(BindingModel model) {
				bindingService.setBinding(model);
			}
		});
	}

	@Override
	public List<Object> deleteBinding(List<BindingModel> models, boolean ignoreErrors) {
		return processBulk(models, ignoreErrors, new BulkOperationWithoutResult() {
			@Override
			protected void runWithoutResult(BindingModel model) {
				bindingService.deleteBinding(model);
			}
		});
	}

	@Override
	public List<Object> getBindings(List<BindingModel> models) {
		return processBulk(models, true, new BulkOperation() {
			@Override
			protected Object run(BindingModel model) {
				return bindingService.getBindings(model);
			}
		});
	}

}
