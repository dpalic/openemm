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

import java.util.Date;

import org.agnitas.emm.core.velocity.VelocityCheck;

public interface MailingBase {

	/**
	 * Getter for property companyID.
	 *
	 * @return Value of property companyID.
	 */
	public abstract int getCompanyID();

	/**
	 * Getter for property campaignID.
	 *
	 * @return Value of property campaignID.
	 */
	public abstract int getCampaignID();

	/**
	 * Getter for property description.
	 *
	 * @return Value of property description.
	 */
	public abstract String getDescription();

	/**
	 * Getter for property id.
	 *
	 * @return Value of property id.
	 */
	public abstract int getId();

	/**
	 * Getter for property mailinglistID.
	 *
	 * @return Value of property mailinglistID.
	 */
	public abstract int getMailinglistID();

	public abstract Mailinglist getMailinglist();

	public abstract Date getSenddate();

	/**
	 * Getter for property shortname.
	 *
	 * @return Value of property shortname.
	 */
	public abstract String getShortname();

	/**
	 * Setter for property companyID.
	 *
	 * @param id New value of property companyID.
	 */
	public abstract void setCompanyID( @VelocityCheck int id);

	/**
	 * Setter for property campaignID.
	 *
	 * @param id New value of property campaignID.
	 */
	public abstract void setCampaignID(int id);

	/**
	 * Setter for property description.
	 *
	 * @param description New value of property description.
	 */
	public abstract void setDescription(String description);

	/**
	 * Setter for property id.
	 *
	 * @param id New value of proerty id.
	 */
	public abstract void setId(int id);

	/**
	 * Setter for property mailinglistID.
	 *
	 * @param id New value of proertymailinglistID.
	 */
	public abstract void setMailinglistID(int id);

	public abstract void setMailinglist(Mailinglist mailinglist);


	public abstract void setSenddate(Date sendDate);

	/**
	 * Setter for property shortname.
	 *
	 * @param shortname New value of property shortname.
	 */
	public abstract void setShortname(String shortname);


	public abstract void setHasActions(boolean hasActions);

    public abstract boolean isHasActions();

	public boolean getUseDynamicTemplate();
	public void setUseDynamicTemplate( boolean useDynamicTemplate);
}