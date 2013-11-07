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

import javax.sql.DataSource;

import org.agnitas.beans.ProfileField;
import org.agnitas.dao.ProfileFieldDao;
import org.agnitas.util.AgnUtils;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.hibernate.dialect.Dialect;

import java.util.List;

/**
 *
 * @author mhe
 */
public class ProfileFieldDaoImpl implements ProfileFieldDao {

	public ProfileField getProfileField(int companyID, String column) {
		HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));

		if(companyID==0) {
			return null;
		}

		return (ProfileField)AgnUtils.getFirstResult(tmpl.find("from ProfileField where companyID = ? and col_name=?", new Object [] {new Integer(companyID), column} ));
	}
    
	public List getProfileFields(int companyID) {
		HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));

		if(companyID==0) {
			return null;
		}

		return tmpl.find("from ProfileField where companyID = ?", new Object [] {new Integer(companyID)} );
	}
    
	public ProfileField getProfileFieldByShortname(int companyID, String shortName) {
		HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));

		if(companyID==0) {
			return null;
		}

		return (ProfileField)AgnUtils.getFirstResult(tmpl.find("from ProfileField where companyID = ? and shortname=?", new Object [] {new Integer(companyID), shortName} ));
	}

	public void saveProfileField(ProfileField field) {
		HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));

		tmpl.saveOrUpdate("ProfileField", field);
	}

	public void deleteProfileField(ProfileField field) {
		HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));

		tmpl.delete(field);
		tmpl.flush();
	}

	public boolean	addProfileField(int companyID, String fieldname, String fieldType, int length, String fieldDefault, boolean notNull) throws Exception	{
		JdbcTemplate jdbc=new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
		String  name=AgnUtils.getDefaultValue("jdbc.dialect");
		Dialect dia=null;
		int jsqlType=-1;
		String dbType = null;
		String defaultSQL = "";
		String sql = "";

		if(fieldDefault!=null && fieldDefault.compareTo("")!=0) {
			if(fieldType.compareTo("VARCHAR")==0 || fieldType.compareTo("DATE")==0) {
				defaultSQL = " DEFAULT '" + fieldDefault + "'";
			} else {
				defaultSQL = " DEFAULT " + fieldDefault;
			}
		}
		Class   cl=null;

		cl=Class.forName("java.sql.Types");
		jsqlType=cl.getDeclaredField(fieldType).getInt(null);
		cl=Class.forName(name);
		dia=(Dialect) cl.getConstructor(new Class[0]).newInstance(new Object[0]);
		dbType=dia.getTypeName(jsqlType);
		/* Bugfix for oracle
		 * Oracle dialect returns long for varchar
		 */
		if(fieldType.equals("VARCHAR")) {
			dbType="VARCHAR";
		}
		/* Bugfix for mysql.
		 * The jdbc-Driver for mysql maps VARCHAR to longtext.
		 * This might be ok in most cases, but longtext doesn't support
		 * length restrictions. So the correct tpye for mysql should be
		 * varchar.
		 */
		if(fieldType.equals("VARCHAR") && dbType.equals("longtext") && length > 0) {
			dbType="varchar";
		}
        
		sql = "ALTER TABLE customer_" + companyID + "_tbl ADD (";
		sql += fieldname.toLowerCase() + " " + dbType;
		if(fieldType.compareTo("VARCHAR")==0) {
			if(length <=  0) {
				length=100;
			}
			sql += "(" + length + ")";
		}
		sql += defaultSQL;

		if(notNull) {
			sql += " NOT NULL";
		}
		sql += ")";
		try {
			jdbc.execute(sql);
		} catch(Exception e) {
			AgnUtils.sendExceptionMail("sql:" + sql, e);
			AgnUtils.logger().error("SQL: "+sql);
			AgnUtils.logger().error("Exception: "+e);
			AgnUtils.logger().error(AgnUtils.getStackTrace(e));
			return false;
		}
		return true;
	}

	public void removeProfileField(int companyID, String fieldname) {
		JdbcTemplate jdbc=new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
		String sql = null;

		sql = "alter table customer_" + companyID + "_tbl drop column " + fieldname;
		jdbc.execute(sql);
		sql = "delete from customer_field_tbl where company_id=? and col_name=?";
		jdbc.update(sql, new Object[]{ new Integer(companyID), fieldname });
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
