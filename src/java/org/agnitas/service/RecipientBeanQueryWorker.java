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

import java.util.Set;
import java.util.concurrent.Callable;

import org.agnitas.beans.impl.PaginatedListImpl;
import org.agnitas.dao.RecipientDao;
import org.agnitas.emm.core.velocity.VelocityCheck;
import org.apache.commons.beanutils.DynaBean;

/**
 * wrapper for a long sql query. It will be used for asynchronous tasks
 *
 * @author ms
 */
public class RecipientBeanQueryWorker implements Callable<PaginatedListImpl<DynaBean>> {
	protected RecipientDao dao;
	protected String sqlStatementForData;
	protected Object[] sqlParametersForData;
	protected String sort;
	protected String direction;
	protected int previousFullListSize;
	protected int page;
	protected int rownums;
	protected int companyID;
	protected Set<String> columns;
	protected Exception error;

	public RecipientBeanQueryWorker(RecipientDao dao, @VelocityCheck int companyID, Set<String> columns, String sqlStatementForData, Object[] sqlParametersForData, String sort, String direction, int page, int rownums, int previousFullListSize) {
		this.dao = dao;
		this.sqlStatementForData = sqlStatementForData;
		this.sqlParametersForData = sqlParametersForData;
		this.sort = sort;
		this.direction = direction;
		this.page = page;
		this.rownums = rownums;
		this.previousFullListSize = previousFullListSize;
		this.companyID = companyID;
		this.columns = columns;
	}

	@Override
	public PaginatedListImpl<DynaBean> call() throws Exception {
		try {
			return dao.getRecipientList(companyID, columns, sqlStatementForData, sqlParametersForData, sort, direction, page, rownums, previousFullListSize);
		} catch (Exception e) {
			error = e;
			return null;
		}
	}

	public Exception getError() {
		return error;
	}
}
