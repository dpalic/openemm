package org.agnitas.emm.core.action.operations;

public class ActionOperationSubscribeCustomer extends AbstractActionOperation {

	private static final long serialVersionUID = 1L;

	private boolean doubleCheck;
	private String keyColumn;
	private boolean doubleOptIn;
	
	public ActionOperationSubscribeCustomer(String type) {
		super(type);
	}

	public boolean isDoubleCheck() {
		return doubleCheck;
	}

	public void setDoubleCheck(boolean doubleCheck) {
		this.doubleCheck = doubleCheck;
	}

	public String getKeyColumn() {
		return keyColumn;
	}

	public void setKeyColumn(String keyColumn) {
		this.keyColumn = keyColumn;
	}

	public boolean isDoubleOptIn() {
		return doubleOptIn;
	}

	public void setDoubleOptIn(boolean doubleOptIn) {
		this.doubleOptIn = doubleOptIn;
	}

}
