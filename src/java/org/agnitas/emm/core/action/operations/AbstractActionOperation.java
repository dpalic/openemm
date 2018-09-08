package org.agnitas.emm.core.action.operations;

import java.io.Serializable;
import java.util.Map;

import org.agnitas.actions.ActionOperation;
import org.apache.struts.action.ActionErrors;
import org.springframework.context.ApplicationContext;

@SuppressWarnings("serial")
public abstract class AbstractActionOperation extends ActionOperation implements Serializable {

	private int id;
	private int companyId;
	private int actionId;
	private String type;
	
	public AbstractActionOperation(String type) {
		this.setType(type);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public int getActionId() {
		return actionId;
	}

	public void setActionId(int actionId) {
		this.actionId = actionId;
	}

	public String getType() {
		return type;
	}

	protected void setType(String type) {
		this.type = type;
	}

	/**
	 * Executes the action operation.
	 * @param con the applicartion context.
	 * @param companyID
	 * @param params parameters given to the request
	 * @return true on success
     * @deprecated replaced by <code>EmmActionOperationService.executeOperation(AbstractActionOperation, Map<String, Object>)</code>.
     */
	@Override
	@Deprecated
	public boolean executeOperation(ApplicationContext con, int companyID, Map<String, Object> params) {
		throw new RuntimeException("Not supported");
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractActionOperation other = (AbstractActionOperation) obj;
		if (id == 0) {
			return false;
		} else if (id != other.id)
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}
	
	public boolean validate(ActionErrors errors, ApplicationContext applicationContext) {
		return true;
	}

}
