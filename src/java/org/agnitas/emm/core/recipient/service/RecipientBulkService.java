package org.agnitas.emm.core.recipient.service;

import java.util.List;

public interface RecipientBulkService {

	List<Object> addSubscriber(List<RecipientModel> models, boolean ignoreErrors);

	List<Object> updateSubscriber(List<RecipientModel> models, boolean ignoreErrors);

	List<Object> deleteSubscriber(List<RecipientModel> models, boolean ignoreErrors);

	List<Object> getSubscriber(List<RecipientModel> models);

}
