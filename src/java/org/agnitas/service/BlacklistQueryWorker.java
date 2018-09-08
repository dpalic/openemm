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

import java.io.Serializable;
import java.util.concurrent.Callable;

import org.agnitas.beans.BlackListEntry;
import org.agnitas.beans.impl.PaginatedListImpl;
import org.agnitas.dao.BlacklistDao;
import org.agnitas.emm.core.velocity.VelocityCheck;

/**
 * wrapper for a long sql query. It will be used for asynchronous tasks
 * @author ms
 *
 */
public class BlacklistQueryWorker implements Callable<PaginatedListImpl<BlackListEntry>>, Serializable {
	private static final long serialVersionUID = -3047853894976634885L;

	private BlacklistDao blacklistDao;
	private String sort;
	private String direction;
	private int page;
	private int rownums;
	private int companyID;

	public BlacklistQueryWorker(BlacklistDao dao, @VelocityCheck int companyID,
			String sort, String direction, int page, int rownums ) {
		this.blacklistDao = dao;
		this.sort = sort;
		this.direction = direction;
		this.page = page;
		this.rownums = rownums;
		this.companyID = companyID;
	}

	public PaginatedListImpl<BlackListEntry> call() throws Exception {
	   return blacklistDao.getBlacklistedRecipients(companyID, sort, direction, page, rownums);
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public BlacklistDao getBlacklistDao() {
		return blacklistDao;
	}

	public String getSort() {
		return sort;
	}

	public String getDirection() {
		return direction;
	}

	public int getPage() {
		return page;
	}

	public int getRownums() {
		return rownums;
	}

	public int getCompanyID() {
		return companyID;
	}
}
