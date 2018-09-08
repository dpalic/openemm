package org.agnitas.emm.core.binding.service;

import java.util.List;

import org.agnitas.beans.BindingEntry;
import org.agnitas.emm.core.velocity.VelocityCheck;

public interface BindingService {

	BindingEntry getBinding(BindingModel model);

	void setBinding(BindingModel model);
	
	void deleteBinding(BindingModel model);
	
	List<BindingEntry> getBindings(BindingModel model);
	
	void updateBindingStatusByEmailPattern( @VelocityCheck int companyId, String emailPattern, int userStatus, String remark) throws BindingServiceException;
}
