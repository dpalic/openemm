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
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/

package org.agnitas.dao.impl;

import org.agnitas.util.AgnUtils;
import org.agnitas.dao.MaildropStatusDao;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Andreas Rehak
 */
public class MaildropStatusDaoImpl implements MaildropStatusDao {
	public boolean	delete(int id) {
		JdbcTemplate jdbc=new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
		String sql = "delete from maildrop_status_tbl where status_id=?";

		try {
			if(jdbc.update(sql, new Object[] {new Integer(id)}) < 1) {
				return false;
			}
		} catch(Exception e) {
			AgnUtils.sendExceptionMail("sql:" + sql, e);
			AgnUtils.logger().debug("Error:"+e);
			AgnUtils.logger().debug(AgnUtils.getStackTrace(e));
			return false;
		}
		return true;
	}

	/**
	 * Holds value of property applicationContext.
	 */
	protected ApplicationContext applicationContext;

	/**
	 * Setter for property applicationContext.
	 * @param applicationContext New value of property applicationContext.
	 */
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
}