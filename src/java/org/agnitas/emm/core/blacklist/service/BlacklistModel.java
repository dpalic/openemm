package org.agnitas.emm.core.blacklist.service;

import org.agnitas.emm.core.velocity.VelocityCheck;

public class BlacklistModel {
	
	private int companyId;
	private String email;

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(@VelocityCheck int companyId) {
		this.companyId = companyId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
