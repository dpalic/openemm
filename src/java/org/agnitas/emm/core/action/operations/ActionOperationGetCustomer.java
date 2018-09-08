package org.agnitas.emm.core.action.operations;

public class ActionOperationGetCustomer extends AbstractActionOperation {
    
	private static final long serialVersionUID = 1L;

    private boolean loadAlways;

	public ActionOperationGetCustomer(String type) {
		super(type);
	}
	
	public boolean isLoadAlways() {
		return loadAlways;
	}

	public void setLoadAlways(boolean loadAlways) {
		this.loadAlways = loadAlways;
	}

}
