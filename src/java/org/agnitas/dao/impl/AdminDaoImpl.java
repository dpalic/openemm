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

import java.security.MessageDigest;

import org.agnitas.beans.Admin;
import org.agnitas.dao.AdminDao;
import org.agnitas.util.AgnUtils;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 *
 * @author mhe
 */
public class AdminDaoImpl implements AdminDao {

	public Admin getAdmin(int adminID, int companyID) {
		HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
        
		if(adminID==0) {
			return null;
		}
        
		return (Admin) AgnUtils.getFirstResult(tmpl.find("from Admin adm where adm.adminID=? AND (adm.companyID=? OR adm.companyID IN (SELECT comp.id FROM Company comp WHERE comp.creatorID=?))", new Object[] { new Integer(adminID), new Integer(companyID), new Integer(companyID) }));
	}
    
	public Admin getAdminByLogin(String name, String password) {
		HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
		byte[] pwdHash=null;

		try {
			pwdHash=MessageDigest.getInstance("MD5").digest(password.getBytes());
		} catch (Exception e) {
			AgnUtils.logger().error("fatal: "+e);
			return null;
		}
		if (AgnUtils.isOracleDB()) {
			return (Admin) AgnUtils.getFirstResult(tmpl.find("from Admin where username=? and password=?", new Object[] {name, password}));
		} else {
			return (Admin) AgnUtils.getFirstResult(tmpl.find("from Admin where username=? and pwd_hash=?", new Object[] {name, pwdHash}));
		}
	}

	public void	save(Admin admin) {
		HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
    	
		tmpl.saveOrUpdate("Admin", admin);
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