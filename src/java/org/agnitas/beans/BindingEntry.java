/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2014 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/

package org.agnitas.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.agnitas.dao.BindingEntryDao;
import org.agnitas.emm.core.velocity.VelocityCheck;

/**
 * Bean representing the Status of a recipient on a mailinglist
 * 
 * @author Martin Helff, Andreas Rehak
 */
public interface BindingEntry extends Serializable {
    /**
	 * Global Constants
	 */
	public static final int MEDIATYPE_EMAIL = 0;
	public static final int MEDIATYPE_FAX = 1;
	public static final int MEDIATYPE_MMS = 3;
	public static final int MEDIATYPE_PRINT = 2;
	public static final int MEDIATYPE_SMS = 4;
	
	public static final int USER_STATUS_ACTIVE = 1;
	public static final int USER_STATUS_BOUNCED = 2;
	public static final int USER_STATUS_ADMINOUT = 3;
	public static final int USER_STATUS_OPTOUT = 4;
	public static final int USER_STATUS_WAITING_FOR_CONFIRM = 5;
	public static final int USER_STATUS_BLACKLIST = 6;
	public static final int USER_STATUS_SUSPEND = 7;
	public static final String USER_TYPE_ADMIN="A";
	public static final String USER_TYPE_TESTUSER="T";
	public static final String USER_TYPE_TESTVIP="t";
	public static final String USER_TYPE_WORLD="W";
	public static final String USER_TYPE_WORLDVIP="w";

    /**
     * Getter for property customerID.
     *
     * @return Value of property customerID.
     */
	public int getCustomerID();

    /**
     * Getter for property mailinglistID.
     *
     * @return Value of property mailinglistID.
     */
	public int getMailinglistID();

    /**
     * Getter for property userType.
     * 
     * @return Value of property userType.
     */
	public String getUserType();

    /**
     * Getter for property userStatus.
     * 
     * @return Value of property userStatus.
     */
	public int getUserStatus();

    /**
     * Getter for property userRemark.
     * 
     * @return Value of property userRemark.
     */
	public String getUserRemark();

    /**
     * Getter for property changeDate.
     * 
     * @return Value of property changeDate.
     */
	public Date getChangeDate();

    /**
     * Getter for property exitMailingID.
     * 
     * @return Value of property exitMailingID.
     */
	public int getExitMailingID();

    /**
     * Getter for property mediaType.
     * 
     * @return Value of property mediaType.
     */
	public int getMediaType();

    /**
     * Setter for property customerID.
     * 
     * @param ci New value of property customerID.
     */
	public void setCustomerID(int ci);

    /**
     * Setter for property exitMailingID.
     * 
     * @param mi New value of property exitMailingID.
     */
	public void setExitMailingID(int mi);

    /**
     * Setter for property mailinglistID.
     * 
     * @param ml New value of property mailinglistID.
     */
	public void setMailinglistID(int ml);

    /**
     * Setter for property mediaType.
     * 
     * @param mediaType New value of property mediaType.
     */
	public void setMediaType(int mediaType);

    /**
     * Setter for property userRemark.
     * 
     * @param remark New value of property userRemark.
     */
	public void setUserRemark(String remark);

    /**
     * Setter for property changeDate.
     * 
     * @param ts New value of property changeDate.
     */
	public void setChangeDate(Date ts);

    /**
     * Setter for property userStatus.
     *
     * @param us New value of property userStatus.
     */
	public void setUserStatus(int us);

    /**
     * Setter for property userType.
     * 
     * @param ut New value of property userType.
     */
	public void setUserType(String ut);

    /**
     * Inserts this Binding in the Database
     * 
     * @param companyID The company ID of the Binding
     * @return true on Sucess, false otherwise.
     */
	public boolean insertNewBindingInDB( @VelocityCheck int companyID);

    /**
     * Updates this Binding in the Database
     * 
     * @param companyID The company ID of the Binding
     * @return true on Sucess, false otherwise.
     */
	public boolean updateBindingInDB( @VelocityCheck int companyID);

    /**
     * Updates or Creates this Binding in the Database
     * 
     * @param companyID The company ID of the Binding
     * @param allCustLists bindings to check for save/update.
     * @return true on Sucess, false otherwise.
     */
	public boolean saveBindingInDB( @VelocityCheck int companyID, Map<Integer, Map<Integer, BindingEntry>> allCustLists);

    /**
     * Updates the status of this Binding in the Database
     * 
     * @param companyID The company ID of the Binding
     * @return true on Sucess, false otherwise.
     */
	public boolean updateStatusInDB( @VelocityCheck int companyID);
    
    /**
     * Mark binding as opted out.
     * 
     * @param email Emailaddress to set inactive.
     * @param companyID The company ID of the Binding
     * @return true if binding is active on the mailinglist, false otherwise.
     */
	public boolean optOutEmailAdr(String email, @VelocityCheck int companyID);

	public boolean getUserBindingFromDB( @VelocityCheck int companyID);

    public void setBindingEntryDao(BindingEntryDao bindingEntryDao);

    public BindingEntryDao getBindingEntryDao();

    public Date getCreationDate();

    public void setCreationDate(Date creationDate);
}
