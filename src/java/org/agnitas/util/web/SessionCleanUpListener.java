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
package org.agnitas.util.web;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * in case of an error the webcontainer restarts itself. It could happen that the persisted sessions will move to an other user...
 * It will be better to cleanup the existing session before startup
 * @author ms
 *
 */
public class SessionCleanUpListener implements ServletContextListener {
	private static final transient Logger logger = Logger.getLogger(SessionCleanUpListener.class);

	private static final String SESSIONFILESTORE = "sessionfilestore";

	@Override
	public void contextDestroyed(ServletContextEvent event) {
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		String sessionfilestoreLocation = event.getServletContext().getRealPath((String) event.getServletContext().getInitParameter(SESSIONFILESTORE));
		if (sessionfilestoreLocation != null) {
			File sessionfilestoreDirectory = new File(sessionfilestoreLocation);
			if (sessionfilestoreDirectory.exists() && sessionfilestoreDirectory.isDirectory()) {
				try {
					FileUtils.deleteDirectory(sessionfilestoreDirectory);
				} catch (IOException exception) {
					logger.fatal("Sessionfilestore Cleanup: Could not delete sessionfilestore: " + sessionfilestoreLocation, exception);
				}
			} else if (logger.isInfoEnabled()) {
				logger.info("Sessionfilestore Cleanup: The provided context-parameter 'sessionfilestore' <" + sessionfilestoreLocation + "> does not exist or is not a directory");
			}
		}
	}
}
