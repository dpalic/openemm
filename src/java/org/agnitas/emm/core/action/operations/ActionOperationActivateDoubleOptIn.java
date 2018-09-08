package org.agnitas.emm.core.action.operations;

public class ActionOperationActivateDoubleOptIn extends AbstractActionOperation {
    
	private static final long serialVersionUID = 1L;
    
    private boolean forAllLists;

	public ActionOperationActivateDoubleOptIn(String type) {
		super(type);
	}

	public boolean isForAllLists() {
		return forAllLists;
	}

	public void setForAllLists(boolean forAllLists) {
		this.forAllLists = forAllLists;
	}

}
