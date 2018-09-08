package org.agnitas.emm.core.action.operations;

 public class ActionOperationSendMailing extends AbstractActionOperation {
    
	private static final long serialVersionUID = 1L;

	private int mailingID;
	private int delayMinutes;

	public ActionOperationSendMailing(String type) {
		super(type);
	}
	
	public int getMailingID() {
		return mailingID;
	}

	public void setMailingID(int mailingID) {
		this.mailingID = mailingID;
	}

	public int getDelayMinutes() {
		return delayMinutes;
	}

	public void setDelayMinutes(int delayMinutes) {
		this.delayMinutes = delayMinutes;
	}

}
