package org.agnitas.emm.core.recipient.service;

import java.util.Map;

import org.agnitas.beans.Recipient;
import org.agnitas.emm.core.velocity.VelocityCheck;
import org.springframework.context.ApplicationContext;

public interface RecipientService {

	public int findSubscriber(@VelocityCheck int companyId, String keyColumn, String value);
	
	public int addSubscriber(RecipientModel model);
	
	public Map<String, Object> getSubscriber(RecipientModel model);
	
	public void deleteSubscriber(RecipientModel model);

	public boolean updateSubscriber(RecipientModel model);

    public void setApplicationContext(ApplicationContext applicationContext);
    
	public void supplySourceID(Recipient recipient, int defaultId);
}
