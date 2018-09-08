package org.agnitas.emm.core.hashtag;

import org.agnitas.beans.Recipient;
import org.springframework.context.ApplicationContext;

/**
 * Context providing common data used for processing hash tags.
 * 
 * @author md
 */
public class HashTagContext {

	/** Customer ID, for that the tag is processed. */
	private final int customerID;
	
	/** Company ID. */
	private final int companyID;

	// TODO: Not sure, what this is, but this data is provided by the method head and used by some tags.
	/** Original UID. */
	private final String originalUID;
	
	/** ApplicationContext of Spring. */
	private final ApplicationContext applicationContext;
	
	/** Recipient for customer ID. Will be filled the first time {@link #getRecipient()} is called. */
	private Recipient recipient;
	
	/**
	 * Creates a new hash tag context.
	 * 
	 * @param companyID company ID, for that the tag is processed
	 * @param customerID customer ID, for that the tag is processed
	 * @param originalUID original UID (?)
	 * @param appCtxt application context of Spring
	 */
	public HashTagContext(int companyID, int customerID, String originalUID, ApplicationContext appCtxt) {
		this.companyID = companyID;
		this.customerID = customerID;
		this.originalUID = originalUID;
		this.applicationContext = appCtxt;
	}

	/**
	 * Returns the customer ID, for that the hash tag is processed. 
	 * 
	 * @return customer ID
	 */
	public int getCustomerId() {
		return this.customerID;
	}
	
	/**
	 * Returns the original UID.
	 * 
	 * @return original UID
	 */
	public String getOriginalUID() {
		return this.originalUID;
	}
	
	/**
	 * Returns the application context of Spring.
	 * 
	 * This method is for backward compatibility only and should be removed
	 * in future versions.
	 * 
	 * @return {@link ApplicationContext} of Spring
	 */
	@Deprecated
	public ApplicationContext getApplicationContext() {
		return this.applicationContext;
	}
	
	/**
	 * Returns the recipient for given IDs of customer and company.
	 * 
	 * @return recipient
	 */
	public Recipient getRecipient() {
		if (recipient == null) {
			recipient = (Recipient)applicationContext.getBean("Recipient");
			recipient.setCompanyID(companyID);
			recipient.setCustomerID(customerID);
			recipient.loadCustDBStructure();
			recipient.getCustomerDataFromDb();
		}
		
		return recipient;
	}
}
