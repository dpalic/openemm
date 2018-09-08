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
package org.agnitas.backend;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * This context listener instantiates the backend datsource
 *
 * @author aso
 */
public class BackendDatasourceInitializationContextListener implements ServletContextListener {
	private static final transient Logger logger = Logger.getLogger( BackendDatasourceInitializationContextListener.class);

	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {
			ServletContext servletContext = event.getServletContext();
			ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
			DBase.DATASOURCE = (DataSource) applicationContext.getBean("dataSource");
			if (DBase.DATASOURCE == null) {
				logger.error("Datasource in DBase for Backend was empty. Backend will try to create its own datasource from emm.properties data");
			}
		} catch (Exception e) {
			logger.error("Cannot set Datasource in DBase for Backend. Backend will try to create its own datasource from emm.properties data", e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// nothing to do
	}
}
