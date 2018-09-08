package org.agnitas.emm.core.action.operations;

public class ActionOperationGetArchiveList extends AbstractActionOperation {
    
	private static final long serialVersionUID = 1L;

	private int campaignID;

	public ActionOperationGetArchiveList(String type) {
		super(type);
	}
	
	public int getCampaignID() {
		return campaignID;
	}

	public void setCampaignID(int campaignID) {
		this.campaignID = campaignID;
	}

}
