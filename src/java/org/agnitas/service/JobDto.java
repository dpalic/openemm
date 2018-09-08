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
package org.agnitas.service;

import java.util.Date;
import java.util.Map;

public class JobDto {
	private int id;
	private String description;
	private Date created;
	private Date lastStart;
	private boolean running;
	private String lastResult;
	private boolean startAfterError;
	private int lastDuration; // in seconds
	private String interval;
	private Date nextStart;
	private String runClass;
	private String runOnlyOnHosts;
	private String emailOnError;
	private boolean deleted;
	private Map<String, String> parameters; // e.g.: ZipAttachement, SendWhenEmpty, SqlStatement, CompanyID

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getLastStart() {
		return lastStart;
	}

	public void setLastStart(Date lastStart) {
		this.lastStart = lastStart;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public String getLastResult() {
		return lastResult;
	}

	public void setLastResult(String lastResult) {
		this.lastResult = lastResult;
	}

	public boolean isStartAfterError() {
		return startAfterError;
	}

	public void setStartAfterError(boolean startAfterError) {
		this.startAfterError = startAfterError;
	}

	public int getLastDuration() {
		return lastDuration;
	}

	public void setLastDuration(int lastDuration) {
		this.lastDuration = lastDuration;
	}

	public String getInterval() {
		return interval;
	}

	public void setInterval(String interval) {
		this.interval = interval;
	}

	public Date getNextStart() {
		return nextStart;
	}

	public void setNextStart(Date nextStart) {
		this.nextStart = nextStart;
	}

	public String getRunClass() {
		return runClass;
	}

	public void setRunClass(String runClass) {
		this.runClass = runClass;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public String getRunOnlyOnHosts() {
		return runOnlyOnHosts;
	}

	public void setRunOnlyOnHosts(String runOnlyOnHosts) {
		this.runOnlyOnHosts = runOnlyOnHosts;
	}

	public String getEmailOnError() {
		return emailOnError;
	}

	public void setEmailOnError(String emailOnError) {
		this.emailOnError = emailOnError;
	}

	/**
	 * Output-Method to simplify debugging
	 */
	@Override
	public String toString() {
		return description + "(" + id + ")";
	}
}
