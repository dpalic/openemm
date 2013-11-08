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
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.agnitas.beans.Admin;
import org.agnitas.beans.MailloopEntry;
import org.agnitas.beans.AdminEntry;
import org.agnitas.beans.impl.MailloopPaginatedList;
import org.agnitas.beans.impl.MailloopEntryImpl;
import org.agnitas.beans.impl.AdminPaginatedList;
import org.agnitas.beans.impl.AdminEntryImpl;
import org.agnitas.dao.AdminDao;
import org.agnitas.util.AgnUtils;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.displaytag.pagination.PaginatedList;
import org.apache.commons.lang.StringUtils;

import javax.sql.DataSource;

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
		tmpl.flush();
	}

	public void delete(Admin admin) {
		  HibernateTemplate tmpl =  new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
	      tmpl.delete(admin);
	      tmpl.flush();
	}

	public void delete(int adminID, int companyID) {
       HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
      	if(adminID==0) {
			return;
		}
        Admin admin = (Admin) AgnUtils.getFirstResult(tmpl.find("from Admin adm where adm.adminID=? AND (adm.companyID=? OR adm.companyID IN (SELECT comp.id FROM Company comp WHERE comp.creatorID=?))", new Object[] { new Integer(adminID), new Integer(companyID), new Integer(companyID) }));
       	tmpl.delete(admin);
       	tmpl.flush();
	}

    public List<Admin> getAllAdminsByCompanyId(int companyID) {
      HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
       return   tmpl.find("from Admin where companyID=?", new Object [] {new Integer(companyID)} );

    }

    public List<Admin> getAllAdmins() {
        HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
              return   tmpl.find("from Admin", new Object [] {} );
    }


	public boolean adminExists(int companyId, String username) {
		JdbcTemplate jdbc = new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
		String sql = "select admin_id from admin_tbl where company_id=? and username=?";
		List<Map> list = jdbc.queryForList(sql, new Object[] {new Integer(companyId), username});
		return list != null && list.size() > 0;
	}

    public PaginatedList getAdminList(int companyID, String sort, String direction, int page, int rownums) {

        if (StringUtils.isBlank(sort)) {
            sort = "adm.username";
        }

        if (StringUtils.isEmpty(direction)) {
            direction = "asc";
        }


        String totalRowsQuery = "select count(adm.admin_id) from admin_tbl adm, company_tbl comp  WHERE (adm.company_id=? OR adm.company_id IN (SELECT company_id FROM company_tbl WHERE creator_company_id=?)) AND status<>'deleted' AND comp.company_ID=adm.company_id";

        JdbcTemplate templateForTotalRows = new JdbcTemplate(getDataSource());

        int totalRows = -1;
        try {
            totalRows = templateForTotalRows.queryForInt(totalRowsQuery, new Object[]{companyID, companyID});
        } catch (Exception e) {
            totalRows = 0; // query for int has a bug , it doesn't return '0' in case of nothing is found...
        }
        // the page numeration begins with 1
        if (page < 1) {
        	page = 1;
        }

        int offset = (page - 1) * rownums;
        String adminListQuery = "SELECT adm.admin_id, adm.username, adm.fullname, comp.shortname, adm.company_id FROM admin_tbl adm, company_tbl comp WHERE (adm.company_id=? OR adm.company_id IN (SELECT company_id FROM company_tbl WHERE creator_company_id=?)) AND status<>'deleted' AND comp.company_ID=adm.company_id ORDER BY " + sort + " "+direction+" LIMIT ? , ? ";

        JdbcTemplate templateForAdmin = new JdbcTemplate(getDataSource());
        List<Map> adminElements = templateForAdmin.queryForList(adminListQuery, new Object[]{companyID, companyID, offset, rownums});
        return new AdminPaginatedList(toAdminList(adminElements), totalRows, page, rownums, sort, direction);
    }

    protected List<AdminEntry> toAdminList(List<Map> adminElements) {
        List<AdminEntry> mailloopEntryList = new ArrayList<AdminEntry>();
        for (Map row : adminElements) {
            Integer id = (Integer) row.get("admin_id");
            String username = (String) row.get("username");
            String fullname = (String) row.get("fullname");
            String shortname = (String) row.get("shortname");
            AdminEntry entry = new AdminEntryImpl(id, username, fullname, shortname);
            mailloopEntryList.add(entry);
        }

        return mailloopEntryList;

    }

    protected DataSource getDataSource() {
		return (DataSource) applicationContext.getBean("dataSource");
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