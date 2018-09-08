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
package org.agnitas.web.forms;

import java.util.List;

import org.agnitas.beans.Mailinglist;
import org.apache.struts.action.ActionMessages;

public class BlacklistForm extends StrutsFormBase {
	/**
	 *
	 */
	private static final long serialVersionUID = -437130377990091064L;
	private ActionMessages messages;
	private ActionMessages errors;

	private String newemail;

	/** List of mailinglists with blacklisted status for recipient. */
	private List<Mailinglist> blacklistedMailinglists;

	/** Array containing the status of the checkboxes for the mailinglists with blacklisted status for the recipient. */
	private int[] checkedBlacklistedMailingslists;

	public String getNewemail() {
		return newemail;
	}

	public void setNewemail(String newemail) {
		this.newemail = newemail;
	}

	public void setMessages(ActionMessages messages) {
		this.messages = messages;
	}

	public ActionMessages getMessages() {
		return this.messages;
	}

	public void setErrors(ActionMessages errors) {
		this.errors = errors;
	}

	public ActionMessages getErrors() {
		return errors;
	}

	public void setBlacklistedMailinglists( List<Mailinglist> mailinglists) {
		this.blacklistedMailinglists = mailinglists;
		this.checkedBlacklistedMailingslists = new int[mailinglists.size()];
	}

	public List<Mailinglist> getBlacklistedMailinglists() {
		return this.blacklistedMailinglists;
	}

	public int getCheckedBlacklistedMailinglists( int index) {
		return this.checkedBlacklistedMailingslists[index];
	}

	public void setCheckedBlacklistedMailinglists( int index, int value) {
		this.checkedBlacklistedMailingslists[index] = value;
	}
}
