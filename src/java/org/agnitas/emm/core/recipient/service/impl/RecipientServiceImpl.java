package org.agnitas.emm.core.recipient.service.impl;

import java.util.Arrays;
import java.util.Map;

import javax.annotation.Resource;

import org.agnitas.beans.Admin;
import org.agnitas.beans.Recipient;
import org.agnitas.beans.impl.AdminImpl;
import org.agnitas.dao.RecipientDao;
import org.agnitas.emm.core.recipient.service.RecipientModel;
import org.agnitas.emm.core.recipient.service.RecipientService;
import org.agnitas.emm.core.validator.annotation.Validate;
import org.agnitas.emm.core.velocity.VelocityCheck;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.service.UserActivityLogService;
import org.agnitas.util.CaseInsensitiveMap;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Transactional;

public class RecipientServiceImpl implements RecipientService, ApplicationContextAware {
    private static final transient Logger logger = Logger.getLogger(RecipientServiceImpl.class);

    @Resource(name="RecipientDao")
    private RecipientDao recipientDao;

    private ApplicationContext applicationContext;

    @Override
    @Transactional
    public int findSubscriber(@VelocityCheck int companyId, String keyColumn, String value) {
        try {
            return recipientDao.findByColumn(companyId, keyColumn, value);
        } catch (RuntimeException e) {
            logger.error("Exception", e);
            throw e;
        }
    }

    @Override
    @Transactional
    @Validate("deleteSubscriber")
    public Map<String, Object> getSubscriber(RecipientModel model) {
        return recipientDao.getCustomerDataFromDb(model.getCompanyId(), model.getCustomerId());
    }

    @Override
    @Transactional
    @Validate("addSubscriber")
    public int addSubscriber(RecipientModel model) {
		recipientDao.checkParameters(model.getParameters(), model.getCompanyId());

		int returnValue = 0;
        int tmpCustID = 0;
        final Admin admin = new AdminImpl();
        admin.setUsername(Utils.getUserName());

        model.setEmail(model.getEmail().toLowerCase());
        Recipient aCust = (Recipient) applicationContext.getBean("Recipient");
        aCust.setCompanyID(model.getCompanyId());
        aCust.setCustParameters(model.getParameters());
        supplySourceID(aCust);
        if(model.isDoubleCheck()) {
            tmpCustID = recipientDao.findByColumn(aCust.getCompanyID(), model.getKeyColumn().toLowerCase(), (String)model.getParameters().get(model.getKeyColumn()));
            if(tmpCustID == 0) {
                returnValue = recipientDao.insertNewCust(aCust);
                writeUserActivityLog(admin, "create recipient, ID = " + returnValue,
                        "Recipient " + returnValue + " created");
            } else {
                returnValue = tmpCustID;
                if(model.isOverwrite()) {
                    aCust.setCustomerID(tmpCustID);
                    CaseInsensitiveMap<Object> dataFromDb = recipientDao.getCustomerDataFromDb(model.getCompanyId(), tmpCustID);
                    Map<String, Object> parameters = aCust.getCustParameters();
                    StringBuilder description;
                    String value, oldValue;
                    for (String key : dataFromDb.keySet()) {
                        if (!parameters.containsKey(key)) {
                            parameters.put(key, dataFromDb.get(key));
                        }else{
                            value = (String)parameters.get(key);
                            oldValue = (String)dataFromDb.get(key);
                            if(!value.equals(oldValue)){
                                description = new StringBuilder();
                                description.append("Recipient ");
                                description.append(key);
                                description.append(" changed from ");
                                description.append(oldValue);
                                description.append(" to ");
                                description.append(value);
                                writeUserActivityLog(admin, "edit recipient, ID = " + returnValue, description.toString());
                            }
                        }
                    }
                    recipientDao.updateInDB(aCust);
                }
            }
        } else {
            returnValue = recipientDao.insertNewCust(aCust);
            writeUserActivityLog(admin, "create recipient, ID = " + returnValue,
                    "Recipient " + returnValue + " created");
        }
        return returnValue;
    }

    @Override
    @Transactional
    @Validate("updateSubscriber")
    public boolean updateSubscriber(RecipientModel model) {
		recipientDao.checkParameters(model.getParameters(), model.getCompanyId());

		final Admin admin = new AdminImpl();
        admin.setUsername(Utils.getUserName());

        Recipient aCust = (Recipient) applicationContext.getBean("Recipient");
        aCust.setCompanyID(model.getCompanyId());
        aCust.loadCustDBStructure();
        Map<String, Object> data = recipientDao.getCustomerDataFromDb(aCust.getCompanyID(), model.getCustomerId());

        String email = model.getEmail();
        if (email != null) {
            model.setEmail(email.toLowerCase());
        }

        aCust.setCustParameters(data);
        aCust.setCustomerID(model.getCustomerId());

        StringBuilder description;
        String name, value, oldValue;

        for (Object key : model.getParameters().keySet()) {
            name = (String)key;
            value = (String)model.getParameters().get(key);
            oldValue = (String)data.get(name);
            if (!oldValue.equals(value)){
                description = new StringBuilder();
                description.append("Recipient ");
                description.append(name);
                description.append(" changed from ");
                description.append(oldValue);
                description.append(" to ");
                description.append(value);
                writeUserActivityLog(admin, "edit recipient, ID = " + model.getCustomerId(), description.toString());
            }

            aCust.setCustParameters(name, value);
        }

        return recipientDao.updateInDB(aCust);
    }

    @Override
    @Transactional
    @Validate("deleteSubscriber")
    public void deleteSubscriber(RecipientModel model) {
		if (recipientDao.getCustomers(Arrays.asList(model.getCustomerId()), model.getCompanyId()).isEmpty()) {
			throw new IllegalStateException("Attempt to delete not existing recipient #" 
					+ model.getCustomerId() + " (company #" + model.getCompanyId() + ")");
		}

		Integer recipientId = model.getCustomerId();
        final Admin admin = new AdminImpl();
        admin.setUsername(Utils.getUserName());

        recipientDao.deleteRecipients(model.getCompanyId(), Arrays.asList(model.getCustomerId()));

        writeUserActivityLog(admin, "delete recipient, ID = " + recipientId, "Recipient " + recipientId + " deleted");
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
    
	void supplySourceID(Recipient recipient) {
//		supplySourceID(recipient, recipientDao.getDefaultDatasourceID(Utils.getUserName(), Utils.getUserCompany()));
		int defaultId = recipientDao.getDefaultDatasourceID(Utils.getUserName(), Utils.getUserCompany());
		if (recipient.getCustParameters("DATASOURCE_ID") == null) {
            logger.trace("Set default datasource_id = " + defaultId + " for recipient with email  " + recipient.getEmail());
			recipient.getCustParameters().put("DATASOURCE_ID", String.valueOf(defaultId));
		} else {
            logger.trace("Set datasource_id = " + recipient.getCustParameters("DATASOURCE_ID") + " from parameter for recipient with email  " + recipient.getEmail());
		}
	}

	public void supplySourceID(Recipient recipient, int defaultId) {
		if (recipient.getCustParameters("DATASOURCE_ID") == null) {
			recipient.getCustParameters().put("DATASOURCE_ID", String.valueOf(defaultId));
		}
	}

}