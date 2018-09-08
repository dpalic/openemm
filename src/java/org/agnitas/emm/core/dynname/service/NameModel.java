package org.agnitas.emm.core.dynname.service;

import org.agnitas.emm.core.velocity.VelocityCheck;

public class NameModel {

	private int companyId;
	private int mailingId;
	
	public int getCompanyId() {
		return companyId;
	}
	
	public void setCompanyId(@VelocityCheck int companyId) {
		this.companyId = companyId;
	}
	
	public int getMailingId() {
		return mailingId;
	}

	public void setMailingId(int mailingId) {
		this.mailingId = mailingId;
	}
	
}
