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
package org.agnitas.dao.impl;

import javax.sql.DataSource;

import org.agnitas.dao.LinkcheckerDao;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

public class LinkcheckerDaoImpl implements LinkcheckerDao {

	private static final transient Logger logger = Logger.getLogger( LinkcheckerDaoImpl.class);

	protected DataSource dataSource;
	protected ApplicationContext applicationContext;

	@Override
	public int getLinkTimeout() {
		JdbcTemplate jdbc = new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
		String sql = "SELECT value FROM config_tbl WHERE class='linkchecker' AND classid='0' AND name='linktimeout'";
		int returnValue = 30000;	// default
		try {
			returnValue = jdbc.queryForInt(sql);
		} catch (Exception e) {
			// error getting properties, setting default to 30s!
			logger.error("Error reading link-timeout... Setting default", e);
			returnValue = 30000;
		}
		return returnValue;
	}

	@Override
	public int getThreadCount() {
		JdbcTemplate jdbc = new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
		String sql = "SELECT value FROM config_tbl WHERE class='linkchecker' AND classid='0' AND name='threadcount'";
		int returnValue = 25;	// default
		try {
			returnValue = jdbc.queryForInt(sql);
		} catch (Exception e) {
			// error getting properties, setting default to 25 threads!
			logger.error("Error reading link-timeout... Setting default", e);
			returnValue = 25;
		}
		return returnValue;
	}

	@Override
	public DataSource getDataSource() {
		return dataSource;
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

}
