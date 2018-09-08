/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2014 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/

package org.agnitas.beans.impl;

import org.agnitas.beans.Company;
import org.agnitas.emm.core.velocity.VelocityCheck;

public class CompanyImpl implements Company {
	private static final long serialVersionUID = -3486669974391290220L;

	private int companyID;
	private int creatorID;
	private String shortname;
	private String description;
	private String status;
	private int mailtracking;
	private int maxLoginFails;
	private int loginBlockTime;
	private Number minimumSupportedUIDVersion;
	private int maxRecipients;
	private String rdirDomain = "http://rdir.de";
	private String secret;
	private String mailloopDomain = "filter.agnitas.de";
	private int useUTF;

	@Override
	public int getId() {
		return companyID;
	}
	
	@Override
	public void setId(@VelocityCheck int id) {
		companyID = id;
	}

	@Override
	public int getCreatorID() {
		return creatorID;
	}
	
	@Override
	public void setCreatorID(int creatorID) {
		this.creatorID = creatorID;
	}

	@Override
	public String getShortname() {
		return shortname;
	}
	
	@Override
	public void setShortname(String name) {
		shortname = name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String sql) {
		description = sql;
	}

	@Override
	public String getStatus() {
		return status;
	}

	@Override
	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public int getMailtracking() {
		return mailtracking;
	}

	@Override
	public void setMailtracking(int tracking) {
		this.mailtracking = tracking;
	}

	@Override
	public int getMaxLoginFails() {
		return maxLoginFails;
	}

	@Override
	public void setMaxLoginFails(int maxLoginFails) {
		this.maxLoginFails = maxLoginFails;
	}

	@Override
	public int getLoginBlockTime() {
		return loginBlockTime;
	}

	@Override
	public void setLoginBlockTime(int loginBlockTime) {
		this.loginBlockTime = loginBlockTime;
	}

	@Override
	public Number getMinimumSupportedUIDVersion() {
		return minimumSupportedUIDVersion;
	}

	@Override
	public void setMinimumSupportedUIDVersion(Number minimumSupportedUIDVersion) {
		this.minimumSupportedUIDVersion = minimumSupportedUIDVersion;
	}

	@Override
	public int getMaxRecipients() {
		return maxRecipients;
	}

	@Override
	public void setMaxRecipients(int maxRecipients) {
		this.maxRecipients = maxRecipients;
	}

	@Override
	public String getRdirDomain() {
		return rdirDomain;
	}

	@Override
	public void setRdirDomain(String rdirDomain) {
		this.rdirDomain = rdirDomain;
	}

	@Override
	public String getSecret() {
		return secret;
	}

	@Override
	public void setSecret(String secret) {
		this.secret = secret;
	}

	@Override
	public String getMailloopDomain() {
		return mailloopDomain;
	}

	@Override
	public void setMailloopDomain(String mailloopDomain) {
		this.mailloopDomain = mailloopDomain;
	}

	@Override
	public int getUseUTF() {
		return useUTF;
	}

	@Override
	public void setUseUTF(int useUTF) {
		this.useUTF = useUTF;
	}
}
