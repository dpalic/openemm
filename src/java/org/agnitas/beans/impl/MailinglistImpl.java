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

package org.agnitas.beans.impl;

import java.util.Date;

import org.agnitas.beans.Mailinglist;
import org.agnitas.emm.core.velocity.VelocityCheck;

public class MailinglistImpl implements Mailinglist {
	private static final long serialVersionUID = -3657876518429344904L;
	
	/**
	 * ID of the mailinglist.
	 */
	protected int id;
	
	/**
	 * Company ID of the account
	 */
	protected int companyID;
	
	/**
	 * shortname to be displayed in mailinglist list.
	 */
	protected String shortname;
	
	/**
	 * a short mailinglist description for the frontend
	 */
	protected String description = "";
	
	/**
	 * Last changedate of this entry
	 */
	protected Date changeDate;

	/**
	 * Creationdate of this entry
	 */
	protected Date creationDate;

	public int getId() {
		return id;
	}

	public void setId(int listid) {
		this.id = listid;
	}

	public int getCompanyID() {
		return companyID;
	}
	
	public void setCompanyID(@VelocityCheck int cid) {
		companyID = cid;
	}

	public String getShortname() {
		return shortname;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		if (description != null) {
			this.description = description;
		} else {
			this.description = "";
		}
	}

	public Date getChangeDate() {
		return changeDate;
	}
	
	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
}
