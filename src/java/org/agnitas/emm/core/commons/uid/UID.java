package org.agnitas.emm.core.commons.uid;

import org.agnitas.emm.core.velocity.VelocityCheck;

/**
 *
 * @author mhe
 */
@Deprecated
public interface UID {

    /**
     * Getter for property companyID.
     *
     * @return Value of property companyID.
     */
    long getCompanyID();

    /**
     * Getter for property customerID.
     *
     * @return Value of property customerID.
     */
    long getCustomerID();

    /**
     * Getter for property mailingID.
     *
     * @return Value of property mailingID.
     */
    long getMailingID();

    /**
     * Getter for property password.
     *
     * @return Value of property password.
     */
    String getPassword();

    /**
     * Getter for property prefix.
     *
     * @return Value of property prefix.
     */
    String getPrefix();

    /**
     * Getter for property URLID.
     *
     * @return Value of property URLID.
     */
    long getURLID();

    /**
     * Create the base UID string
     *
     * @return the UID
     */
    String makeBaseUID();

    /**
     * Make the final UID string
     *
     * @return UID as string
     */
    String makeUID() throws Exception;

    /**
     * Make the final UID string using given customer id and URL ID
     *
     * @param customerID the customer ID to use
     * @param URLID the URL ID to use
     * @return UID as string
     */
    String makeUID(long customerID, long URLID) throws Exception;

    /**
     * Parses an uid
     */
    void parseUID(String uid) throws Exception;

    /**
     * Setter for property companyID.
     *
     * @param companyID New value for property companyID.
     */
    void setCompanyID(@VelocityCheck long companyID);

    /**
     * Setter for property customerID.
     *
     * @param customerID New value of property customerID.
     */
    void setCustomerID(long customerID);

    /**
     * Setter for property mailingID.
     *
     * @param mailingID new value of property mailingID.
     */
    void setMailingID(long mailingID);

    /**
     * Setter for property password.
     *
     * @param password New value of property password.
     */
    void setPassword(String password);

    /**
     * Setter for property prefix.
     *
     * @param prefix New value of property prefix.
     */
    void setPrefix(String prefix);

    /**
     * Setter for property URLID.
     *
     * @param URLID New value of property URLID.
     */
    void setURLID(long URLID);

    /**
     * Validate an UID
     *
     * @return true, if UID is valid
     */
    boolean validateUID() throws Exception;

    /**
     * Validate an UID
     *
     * @param password the password
     * @return true, if UID is valid
     */
    boolean validateUID(String password) throws Exception;

}
