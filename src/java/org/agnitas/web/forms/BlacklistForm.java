package org.agnitas.web.forms;

import org.apache.struts.action.ActionMessages;

public class BlacklistForm extends StrutsFormBase {
	private ActionMessages messages;
	private ActionMessages errors;
	
	private String newemail;

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
}
