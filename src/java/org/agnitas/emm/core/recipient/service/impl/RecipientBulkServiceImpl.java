package org.agnitas.emm.core.recipient.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.agnitas.beans.Admin;
import org.agnitas.beans.Recipient;
import org.agnitas.beans.impl.AdminImpl;
import org.agnitas.dao.RecipientDao;
import org.agnitas.emm.core.recipient.service.RecipientBulkService;
import org.agnitas.emm.core.recipient.service.RecipientModel;
import org.agnitas.emm.core.recipient.service.RecipientService;
import org.agnitas.emm.core.service.impl.AbstractBulkServiceImpl;
import org.agnitas.emm.core.validator.ModelValidator;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.service.UserActivityLogService;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class RecipientBulkServiceImpl extends AbstractBulkServiceImpl<RecipientModel> implements RecipientBulkService, ApplicationContextAware {
	private static final transient Logger logger = Logger.getLogger(RecipientBulkServiceImpl.class);

	@Resource(name="RecipientDao")
	private RecipientDao recipientDao;

	private ApplicationContext applicationContext;

	@Resource
	private RecipientService recipientService;
	
	@Resource
	private ModelValidator validator;
	
	private int defaultDataSourceID = 0;
	
	private int validateModels(String annotation, final List<RecipientModel> models, boolean ignoreErrors, Object dummyResult, List<Object> results) {
		int invalidCnt = 0;
		results.clear();
		for (RecipientModel recipientModel : models) {
			try {
				validator.validate(annotation, recipientModel);
				results.add(dummyResult);
			} catch (Exception e) {
				results.add(e);
				invalidCnt++;
				if (!ignoreErrors) {
					break;
				}
			}
		}
		return invalidCnt;
	}
	
	private List<Object> validatedOperation(final List<RecipientModel> models, final boolean ignoreErrors, 
			String annotation, Object dummyResult, ProcessRecipientModels todo) {
		
		List<Object> results = new ArrayList<Object>();
		// Validate each model in list with required 'annotation' rule
		int invalids = validateModels(annotation, models, ignoreErrors, dummyResult, results);
		if (!ignoreErrors && invalids > 0) {
			return results;
		}
		
		// Filter valid models
		final List<RecipientModel> validModels = new ArrayList<RecipientModel>();
		for (int i = 0; i < results.size(); i++) {
			if (results.get(i) == dummyResult) {
				validModels.add(models.get(i));
			}
		}
		
		// Run required operation for valid models only
		List<Object> validResults = todo.exec(validModels);
		
		// Merge validation and operation results 
		int k = 0;
		for (int i = 0; i < results.size(); i++) {
			if (k >= validResults.size()) {
				break;
			}
			if (results.get(i) == dummyResult) {
				results.set(i, validResults.get(k++));
			}
		}
		return results;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Object> addSubscriber(final List<RecipientModel> models, final boolean ignoreErrors) {
		defaultDataSourceID = recipientDao.getDefaultDatasourceID(Utils.getUserName(), Utils.getUserCompany());
		return validatedOperation(models, ignoreErrors, "addSubscriber", new Integer(0) /*RecipientBulkNotAppliedException()*/, new ProcessRecipientModels() {
			@Override
			public List<Object> exec(final List<RecipientModel> validModels) {
				if (ignoreErrors) {
					return addCustomersIgnoreErrors(validModels);
				} else {
					return (List<Object>) new TransactionTemplate(getTransactionManager()).execute(new TransactionCallback() {
						@Override
						public Object doInTransaction(TransactionStatus status) {
							List<Object> result = addCustomers(validModels);
							if (result.isEmpty()) {
								status.setRollbackOnly();
							}
							return result;
						}
					});
				}
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	private List<Object> processCustomersIgnoreErrors(List<RecipientModel> models, 
			final ProcessRecipientModels todo, ProcessRecipientModels todoSafe) {
		
		final int PACK_SIZE = 200;
		List<Object> result = new ArrayList<Object>();

		int startPosition = 0;
		while (startPosition < models.size()) {
			int endPosition = startPosition + PACK_SIZE;
			if (endPosition > models.size()) {
				endPosition = models.size();
			}
			final List<RecipientModel> modelsSublist = models.subList(startPosition, endPosition);
			
			TransactionTemplate tx = new TransactionTemplate(getTransactionManager());
			
			// attempt to add with batch
			List<Object> res = (List<Object>) tx.execute(new TransactionCallback() {
				@Override
				public Object doInTransaction(TransactionStatus status) {
					List<Object> result = todo.exec(modelsSublist);
					if (result.isEmpty()) {
						status.setRollbackOnly();
					}
					return result;
				}
			});
			
			// in case of batch fault - add by one
			if (res.isEmpty()) {
				res = (List<Object>) tx.execute(new TransactionCallback() {
					@Override
					public Object doInTransaction(TransactionStatus status) {
						return todo.exec(modelsSublist);
					}
				});
			}
			result.addAll(res);
			startPosition = endPosition;
		}
		return result;
	}
	
	private List<Object> addCustomersIgnoreErrors(List<RecipientModel> models) {
		return processCustomersIgnoreErrors(models, new ProcessRecipientModels() {
			@Override
			public List<Object> exec(List<RecipientModel> subList) {
				return addCustomersChunkIgnoreErrors(subList);
			}
		}, new ProcessRecipientModels() {
			@Override
			public List<Object> exec(List<RecipientModel> subList) {
				return processBulk(subList, true, new BulkOperation() {
					@Override
					public Object run(RecipientModel model) {
						return recipientService.addSubscriber(model);
					}
				});
			}
		});
	}
	
	private List<Object> updateCustomersIgnoreErrors(List<RecipientModel> models) {
		return processCustomersIgnoreErrors(models, new ProcessRecipientModels() {
			@Override
			public List<Object> exec(List<RecipientModel> subList) {
				return updateCustomersChunk(subList);
			}
		}, new ProcessRecipientModels() {
			@Override
			public List<Object> exec(List<RecipientModel> subList) {
				return processBulk(subList, true, new BulkOperation() {
					@Override
					public Object run(RecipientModel model) {
						return recipientService.updateSubscriber(model);
					}
				});
			}
		});
	}

	private List<Object> addCustomersChunkIgnoreErrors(List<RecipientModel> models) {
		List<Recipient> toInsert = new ArrayList<Recipient>();
		List<Boolean> doubleCheck = new ArrayList<Boolean>();
		List<Boolean> overwrite = new ArrayList<Boolean>();
		List<String> keyFields = new ArrayList<String>();
		
		int companyID = -1;
		for (RecipientModel model : models) {
			if (model.getCompanyId() == 0) {
				logger.error("addCustomersChunckIgnoreErrors: model.getCompanyId() == 0 for model " + model.toString());
				return Collections.emptyList();
			}
			if (companyID == -1) {
				companyID = model.getCompanyId();
			} else if (model.getCompanyId() != companyID) {
				logger.error("addCustomersChunckIgnoreErrors: model.getCompanyId differs for model " + model.toString());
				return Collections.emptyList();
			}
			model.setEmail(model.getEmail().toLowerCase());
			Recipient aCust = (Recipient) applicationContext.getBean("Recipient");
	        aCust.setCompanyID(companyID);
	        aCust.setCustParameters(model.getParameters());
	        recipientService.supplySourceID(aCust, defaultDataSourceID);
        	toInsert.add(aCust);
        	doubleCheck.add(model.isDoubleCheck());
        	overwrite.add(model.isOverwrite());
        	keyFields.add(model.getKeyColumn());
		}
		
		List<Object> results = recipientDao.insertCustomers(toInsert, doubleCheck, overwrite, keyFields);
        final Admin admin = new AdminImpl();
        admin.setUsername(Utils.getUserName());

        StringBuilder description = new StringBuilder("Recipient");
        if (results.size() > 1) {
            description.append("s");
        }
        description.append(" ");

        for (Iterator<Object> iterator = results.iterator(); iterator.hasNext(); ) {
            Object customerID = iterator.next();
            description.append(customerID);
            if (iterator.hasNext()) {
                description.append(", ");
            }
        }

        description.append(" created");
        writeUserActivityLog(admin, "create recipient" + (results.size() > 1 ? "s":""), description.toString());

        return results;
	} 
	
	private interface ProcessRecipientModels {
		List<Object> exec(List<RecipientModel> models);
	}
	
	private List<Object> processCustomers(List<RecipientModel> models, ProcessRecipientModels todo) {
		final int PACK_SIZE = 1000;
		List<Object> result = new ArrayList<Object>();

		int startPosition = 0;
		while (startPosition < models.size()) {
			int endPosition = startPosition + PACK_SIZE;
			if (endPosition > models.size()) {
				endPosition = models.size();
			}
			final List<RecipientModel> modelsSublist = models.subList(startPosition, endPosition);
			List<Object> res;
			res = todo.exec(modelsSublist);
			result.addAll(res);
			startPosition = endPosition;
		}
		return result;
	}
	
	private List<Object> addCustomers(final List<RecipientModel> models) {
		return processCustomers(models, new ProcessRecipientModels() {
			@Override
			public List<Object> exec(List<RecipientModel> subList) {
				return addCustomersChunk(subList);
			}
		}
		);
	}

	/**
	 * For bulk insert of new recipients only. 
	 * isDoubleCheck & isOverwrite don't matter.
	 * If recipients' CompanyId differs for any recipients, or companyID == 0 - it's assumed as error. 
	 * 
	 * @param models CompanyId should be the same for all models in list.
	 * @return list of recipient ID's or empty list in case of errors
	 */
	private List<Object> addCustomersChunk(List<RecipientModel> models) {
		List<Recipient> toInsert = new ArrayList<Recipient>();
		
		int companyID = -1;
		for (RecipientModel model : models) {
			if (model.getCompanyId() == 0) {
				logger.error("addSubscriberInternal: model.getCompanyId() == 0 for model " + model.toString());
				return Collections.emptyList();
			}
			if (companyID == -1) {
				companyID = model.getCompanyId();
			} else if (model.getCompanyId() != companyID) {
				logger.error("addSubscriberInternal: model.getCompanyId differs for model " + model.toString());
				return Collections.emptyList();
			}
			model.setEmail(model.getEmail().toLowerCase());
			Recipient aCust = (Recipient) applicationContext.getBean("Recipient");
	        aCust.setCompanyID(companyID);
	        aCust.setCustParameters(model.getParameters());
	        recipientService.supplySourceID(aCust, defaultDataSourceID);
        	toInsert.add(aCust);
		}
        final Admin admin = new AdminImpl();
        admin.setUsername(Utils.getUserName());

        List<Object> results = recipientDao.insertCustomers(toInsert);

        StringBuilder description = new StringBuilder("Recipient");
        if (results.size() > 1) {
            description.append("s");
        }
        description.append(" ");

        for (Iterator<Object> iterator = results.iterator(); iterator.hasNext();) {
            Object customerID = iterator.next();
            description.append(customerID);
            if(iterator.hasNext()){
                description.append(", ");
            }
        }

        description.append(" created");
        writeUserActivityLog(admin, "create recipient" + (results.size() > 1 ? "s":""), description.toString());
        return results;
	}

	
	private List<Object> updateCustomers(List<RecipientModel> models) {
		return processCustomers(models, new ProcessRecipientModels() {
			@Override
			public List<Object> exec(List<RecipientModel> subList) {
				return updateCustomersChunk(subList);
			}
		}
		);
	}

	/**
	 * For bulk update of recipients. 
	 * isDoubleCheck & isOverwrite don't matter.
	 * If recipients' CompanyId differs for any recipients, or companyID == 0 - it's assumed as error. 
	 * 
	 * @param models CompanyId should be the same for all models in list.
	 * @return successful updated flags list
	 */
	private List<Object> updateCustomersChunk(List<RecipientModel> models) {
		List<Recipient> toUpdate = new ArrayList<Recipient>();
        final Admin admin = new AdminImpl();
        admin.setUsername(Utils.getUserName());

		StringBuilder description = new StringBuilder("Recipient");
        if (models.size() > 1) {
            description.append("s");
        }
        description.append(" ");

		int companyID = -1;
        for (Iterator<RecipientModel> iterator = models.iterator(); iterator.hasNext();) {
            RecipientModel model = iterator.next();
			if (model.getCompanyId() == 0) {
				logger.error("updateCustomersChunk: model.getCompanyId() == 0 for model " + model.toString());
				return Collections.emptyList();
			}
			if (companyID == -1) {
				companyID = model.getCompanyId();
			} else if (model.getCompanyId() != companyID) {
				logger.error("updateCustomersChunk: model.getCompanyId differs for model " + model.toString());
				return Collections.emptyList();
			}
			
			String email = model.getEmail();
			if (email != null) {
				model.setEmail(email.toLowerCase());
			}
			Recipient aCust = (Recipient) applicationContext.getBean("Recipient");
	        aCust.setCompanyID(companyID);
	        aCust.setCustomerID(model.getCustomerId());
       		aCust.setCustParameters(model.getParameters());
	       	toUpdate.add(aCust);

            description.append(model.getCustomerId());
            if (iterator.hasNext()) {
                description.append(", ");
            }
		}

        description.append(" updated");
        writeUserActivityLog(admin, "update recipient" + (models.size() > 1 ? "s":""), description.toString());

        return recipientDao.updateCustomers(toUpdate);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> updateSubscriber(final List<RecipientModel> models, final boolean ignoreErrors) {
		
		return validatedOperation(models, ignoreErrors, "updateSubscriber", 
				Boolean.FALSE, new ProcessRecipientModels() {
			
			@Override
			public List<Object> exec(final List<RecipientModel> validModels) {
				if (ignoreErrors) {
					return updateCustomersIgnoreErrors(validModels);
				} else {
					return (List<Object>) new TransactionTemplate(getTransactionManager()).execute(new TransactionCallback() {
						@Override
						public Object doInTransaction(TransactionStatus status) {
							List<Object> result = updateCustomers(validModels);
							if (result.isEmpty()) {
								status.setRollbackOnly();
							}
							return result;
						}
					});
				}
			}
		});
	}

	@Override
	public List<Object> deleteSubscriber(List<RecipientModel> models, boolean ignoreErrors) {
		return processBulk(models, ignoreErrors, new BulkOperationWithoutResult() {
			@Override
			protected void runWithoutResult(RecipientModel model) {
				recipientService.deleteSubscriber(model);
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getSubscriber(final List<RecipientModel> models) {
		
		return validatedOperation(models, true, "deleteSubscriber", null, new ProcessRecipientModels() {
			@Override
			public List<Object> exec(final List<RecipientModel> validModels) {
				return (List<Object>) new TransactionTemplate(getTransactionManager()).execute(new TransactionCallback() {
					@Override
					public Object doInTransaction(TransactionStatus status) {
						return processCustomers(models, new ProcessRecipientModels() {
							@Override
							public List<Object> exec(List<RecipientModel> subList) {
								return getCustomers(subList);
							}
						});
					}
				});
			}
		});
	}
	
	public List<Object> getCustomers(List<RecipientModel> models) {
		List<Integer> customerIDs = new ArrayList<Integer>();
		int companyID = -1;
		for (RecipientModel model : models) {
			if (companyID == -1) {
				companyID = model.getCompanyId();
			} else if (model.getCompanyId() != companyID) {
				logger.error("getCustomers: model.getCompanyId differs for model " + model.toString());
				return Collections.emptyList();
			}
			customerIDs.add(model.getCustomerId());
		}
		return recipientDao.getCustomers(customerIDs, companyID);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

    private UserActivityLogService userActivityLogService;

    @Required
    public void setUserActivityLogService(UserActivityLogService userActivityLogService) {
        this.userActivityLogService = userActivityLogService;
    }

    protected void writeUserActivityLog(Admin admin, String action, String description) {
        if (userActivityLogService != null) {
            userActivityLogService.writeUserActivityLog(admin, action, description, logger);
        } else {
            logger.error("Missing userActivityLogService in " + this.getClass().getSimpleName());
            logger.info("Userlog: " + admin.getUsername() + " " + action + " " + description);
        }
    }
}