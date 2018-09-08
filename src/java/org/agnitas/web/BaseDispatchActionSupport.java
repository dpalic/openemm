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
package org.agnitas.web;

import org.agnitas.beans.Admin;
import org.agnitas.service.UserActivityLogService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.struts.DispatchActionSupport;

public class BaseDispatchActionSupport extends DispatchActionSupport {

	/** The logger. */
	private static final transient Logger logger = Logger.getLogger(BaseDispatchActionSupport.class);

	/** Service for accessing user activity log. */
	private UserActivityLogService userActivityLogService;

	@Required
	public void setUserActivityLogService(UserActivityLogService userActivityLogService) {
		this.userActivityLogService = userActivityLogService;
	}

	protected void writeUserActivityLog(Admin admin, String action, String description) {
    	try {
			if (userActivityLogService != null) {
				userActivityLogService.writeUserActivityLog(admin, action, description);
			} else {
				logger.error("Missing userActivityLogService in " + this.getClass().getSimpleName());
				logger.info("Userlog: " + admin.getUsername() + " " + action + " " +  description);
			}
		} catch (Exception e) {
			logger.error("Error writing ActivityLog: " + e.getMessage(), e);
			logger.info("Userlog: " + admin.getUsername() + " " + action + " " +  description);
		}
    }

    protected void writeUserActivityLog(Admin admin, String action, int description)  {
    	try {
	    	if (userActivityLogService != null) {
	    		userActivityLogService.writeUserActivityLog(admin, action, Integer.toString(description));
	    	} else {
	    		logger.error("Missing userActivityLogService in " + this.getClass().getSimpleName());
	    		logger.info("Userlog: " + admin.getUsername() + " " + action + " " +  description);
	    	}
		} catch (Exception e) {
			logger.error("Error writing ActivityLog: " + e.getMessage(), e);
			logger.info("Userlog: " + admin.getUsername() + " " + action + " " +  description);
		}
    }
}
