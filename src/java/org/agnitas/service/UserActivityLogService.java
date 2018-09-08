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

import java.util.List;

import org.agnitas.beans.Admin;
import org.agnitas.beans.AdminEntry;
import org.agnitas.web.forms.UserActivityLogForm;
import org.apache.log4j.Logger;
import org.displaytag.pagination.PaginatedList;

/**
 * @author Viktor Gema
 */
public interface UserActivityLogService {
	public PaginatedList getUserActivityLogByFilter(UserActivityLogForm aForm, int pageNumber, int rownums, int adminID, String sort, String direction, List<AdminEntry> admins) throws Exception;
	
	/**
	 * Write user activity log for given {@link Admin}.
	 * 
	 * @param admin admin
	 * @param action action
	 * @param description description of action
	 */
	public void writeUserActivityLog(Admin admin, String action, String description);

    /**
     * Write user activity log for given {@link Admin}.
     *
     * Trace errors in case of service failure to caller log.
     *
     * @param admin admin
     * @param action action
     * @param description description of action
     * @param callerLog caller logging class to report in case of service failure
     */
    public void writeUserActivityLog(Admin admin, String action, String description, Logger callerLog);
}
