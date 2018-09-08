package org.agnitas.emm.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public abstract class AbstractBulkServiceImpl<T> {

	@Resource
	private PlatformTransactionManager transactionManager;
	protected PlatformTransactionManager getTransactionManager() {
		return transactionManager;
	}
	
	protected abstract class BulkOperation {
		
		protected abstract Object run(T model);
		
	}
	
	protected abstract class BulkOperationWithoutResult extends BulkOperation {

		public final Object run(T model) {
			runWithoutResult(model);
			return null;
		}
	
		protected abstract void runWithoutResult(T model);

	}
	
	@SuppressWarnings("unchecked")
	protected List<Object> processBulk(final List<T> models, final boolean ignoreErrors, final BulkOperation operation) {
		if (ignoreErrors) {
			return processList(models, ignoreErrors, operation);
		} else {
			return (List<Object>) new TransactionTemplate(transactionManager).execute(new TransactionCallback() {
				@Override
				public Object doInTransaction(TransactionStatus status) {
					List<Object> result = processList(models, ignoreErrors, operation);
					if (!result.isEmpty()) {
						for (Object r : result) {
							if (r instanceof Exception) {
								status.setRollbackOnly();
								break;
							}
						}
					}
					return result;
				}
			});
		}
	}
	
	private List<Object> processList(List<T> models, boolean ignoreErrors, BulkOperation operation) {
		List<Object> result = new ArrayList<Object>(models.size());
		int size = models.size();
		for (int i = 0; i < size; i++) {
			try {
				result.add(operation.run(models.get(i)));
			} catch (Exception e) {
				result.add(e);
				if (!ignoreErrors) {
					break;
				}
			}
		}
		return result;
	}

}
