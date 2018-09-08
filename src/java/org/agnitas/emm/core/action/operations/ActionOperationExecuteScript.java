package org.agnitas.emm.core.action.operations;

public class ActionOperationExecuteScript extends AbstractActionOperation {
    
	private static final long serialVersionUID = 1L;

	private String script;

	public ActionOperationExecuteScript(String type) {
		super(type);
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

}
