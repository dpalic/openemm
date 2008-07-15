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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.sql.DataSource;

import org.agnitas.dao.TargetDao;
import org.agnitas.target.Target;
import org.agnitas.util.AgnUtils;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 * 
 * @author mhe
 */
public class TargetDaoImpl implements TargetDao {

	/** Creates a new instance of MailingDaoImpl */
	public TargetDaoImpl() {
	}

	public Target getTarget(int targetID, int companyID) {
		Target ret = null;
		try {
			HibernateTemplate tmpl = new HibernateTemplate(
					(SessionFactory) this.applicationContext
							.getBean("sessionFactory"));

			if (targetID == 0 || companyID == 0) {
				return null;
			}

			ret = (Target) AgnUtils.getFirstResult(tmpl.find(
					"from Target where id = ? and companyID = ?", new Object[] {
							new Integer(targetID), new Integer(companyID) }));
		} catch (Exception e) {
			System.err.println("Target load error: " + e);
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * Getter for target by target name and company id.
	 * 
	 * @return target.
	 */
	public Target getTargetByName(String targetName, int companyID) {
		HibernateTemplate tmpl = new HibernateTemplate(
				(SessionFactory) this.applicationContext
						.getBean("sessionFactory"));

		targetName = targetName.trim();

		if (targetName.length() == 0 || companyID == 0) {
			return null;
		}

		return (Target) AgnUtils
				.getFirstResult(tmpl
						.find(
								"from Target where targetName = ? and (companyID = ? or companyID=0)",
								new Object[] { new String(targetName),
										new Integer(companyID) }));
	}

	public int saveTarget(Target target) {
		int result = 0;
		Target tmpTarget = null;

		if (target == null || target.getCompanyID() == 0) {
			return 0;
		}

		HibernateTemplate tmpl = new HibernateTemplate(
				(SessionFactory) this.applicationContext
						.getBean("sessionFactory"));
		if (target.getId() != 0) {
			tmpTarget = (Target) AgnUtils.getFirstResult(tmpl.find(
					"from Target where id = ? and companyID = ?", new Object[] {
							new Integer(target.getId()),
							new Integer(target.getCompanyID()) }));
			if (tmpTarget == null) {
				target.setId(0);
			}
		}

		tmpl.saveOrUpdate("Target", target);
		result = target.getId();

		return result;
	}

	public boolean deleteTarget(int targetID, int companyID) {
		Target tmp = null;
		boolean result = false;

		if ((tmp = this.getTarget(targetID, companyID)) != null) {
			HibernateTemplate tmpl = new HibernateTemplate(
					(SessionFactory) this.applicationContext
							.getBean("sessionFactory"));
			try {
				tmpl.delete(tmp);
				tmpl.flush();
				result = true;
			} catch (Exception e) {
				result = false;
			}
		}
		return result;
	}

	public List getTargets(int companyID) {
		HibernateTemplate tmpl = new HibernateTemplate(
				(SessionFactory) this.applicationContext
						.getBean("sessionFactory"));

		return tmpl.find("from Target where companyID = ? order by targetName",
				new Object[] { new Integer(companyID) });
	}

	public Map getAllowedTargets(int companyID) {
		JdbcTemplate jdbc = new JdbcTemplate((DataSource) applicationContext
				.getBean("dataSource"));
		Map targets = new HashMap();
		String sql = "select target_id, target_shortname, target_description, target_sql from dyn_target_tbl where company_id="
				+ companyID + " order by target_id";

		try {
			List list = jdbc.queryForList(sql);
			Iterator i = list.iterator();

			while (i.hasNext()) {
				Map map = (Map) i.next();
				int id = ((Number) map.get("target_id")).intValue();
				String shortname = (String) map.get("target_shortname");
				String description = (String) map.get("target_description");
				String targetsql = (String) map.get("target_sql");
				Target target = (Target) applicationContext.getBean("Target");

				target.setCompanyID(companyID);
				target.setId(id);
				if (shortname != null) {
					target.setTargetName(shortname);
				}
				if (description != null) {
					target.setTargetDescription(description);
				}
				if (targetsql != null) {
					target.setTargetSQL(targetsql);
				}
				targets.put(new Integer(id), target);
			}
		} catch (Exception e) {
			AgnUtils.sendExceptionMail("sql:" + sql, e);
			System.out.println("getAllowedTargets: " + e);
			return null;
		}
		return targets;
	}

	/**
	 * Holds value of property applicationContext.
	 */
	protected ApplicationContext applicationContext;

	/**
	 * Setter for property applicationContext.
	 * 
	 * @param applicationContext
	 *            New value of property applicationContext.
	 */
	public void setApplicationContext(ApplicationContext applicationContext) {

		this.applicationContext = applicationContext;
	}
}
