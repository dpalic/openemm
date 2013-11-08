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
import org.agnitas.beans.MailloopEntry;
import org.agnitas.beans.impl.MailloopPaginatedList;
import org.agnitas.beans.impl.MailloopEntryImpl;
import org.agnitas.dao.ProfileFieldDao;
import org.agnitas.util.AgnUtils;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.hibernate.dialect.Dialect;
import org.displaytag.pagination.PaginatedList;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 *
 * @author mhe
 */
public class ProfileFieldDaoImpl implements ProfileFieldDao {

	// ----------------------------------------------------------------------- dependecy injection
	
	protected DataSource dataSource;
	private SessionFactory sessionFactory;  // Made "private" so it can only be used in this class.
	
	public void setDataSource( DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public void setSessionFactory( SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	// ----------------------------------------------------------------------- business logic
	
	@Override
	public ProfileField getProfileField(int companyID, String column) {
		HibernateTemplate tmpl = new HibernateTemplate(this.sessionFactory);

		if(companyID==0) {
			return null;
		}

		return (ProfileField)AgnUtils.getFirstResult(tmpl.find("from ProfileField where companyID = ? and col_name=?", new Object [] { companyID, column} ));
	}
    
	@Override
	public List getProfileFields(int companyID) {
		HibernateTemplate tmpl = new HibernateTemplate(this.sessionFactory);

		if(companyID==0) {
			return null;
		}

		return tmpl.find("from ProfileField where companyID = ?", new Object [] { companyID } );
	}
    
	@Override
	public ProfileField getProfileFieldByShortname(int companyID, String shortName) {
		HibernateTemplate tmpl = new HibernateTemplate(this.sessionFactory);

		if(companyID==0) {
			return null;
		}

		return (ProfileField)AgnUtils.getFirstResult(tmpl.find("from ProfileField where companyID = ? and shortname=?", new Object [] { companyID, shortName} ));
	}

	@Override
	public void saveProfileField(ProfileField field) {
		HibernateTemplate tmpl=new HibernateTemplate(this.sessionFactory);

		tmpl.saveOrUpdate("ProfileField", field);
	}

	@Override
	public void deleteProfileField(ProfileField field) {
		HibernateTemplate tmpl=new HibernateTemplate(this.sessionFactory);

		tmpl.delete(field);
		tmpl.flush();
	}

	@Override
	public boolean	addProfileField(int companyID, String fieldname, String fieldType, int length, String fieldDefault, boolean notNull) throws Exception	{
		JdbcTemplate jdbc = new JdbcTemplate(this.dataSource);
		String  name=AgnUtils.getDefaultValue("jdbc.dialect");
		Dialect dia=null;
		int jsqlType=-1;
		String dbType = null;
		String defaultSQL = "";
		String sql = "";

		if(fieldDefault!=null && fieldDefault.compareTo("")!=0) {
			if(fieldType.equals("VARCHAR")) {
				defaultSQL = " DEFAULT '" + fieldDefault + "'";
			} else if( fieldType.equals( "DATE")) {
				// TODO: A fixed date format is not a good solution, should depend on language setting of the user
				/*
				 * Here raise a problem: The default value is not only used for the ALTER TABLE statement. It is also
				 * stored in customer_field_tbl.default_value as a string. A problem occurs, when two users with
				 * language settings with different date formats edit the profile field.
				 */
				if( AgnUtils.isOracleDB())
					defaultSQL = " DEFAULT to_date('" + fieldDefault + "', 'DD.MM.YYYY')";
				else
					defaultSQL = " DEFAULT '" + fieldDefault + "'";
			} else {
				defaultSQL = " DEFAULT " + fieldDefault;
			}
		}
		Class<?> cl=null;

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

	@Override
	public void removeProfileField(int companyID, String fieldname) {
		JdbcTemplate jdbc = new JdbcTemplate(this.dataSource);
		String sql = null;

		sql = "alter table customer_" + companyID + "_tbl drop column " + fieldname;
		jdbc.execute(sql);
		sql = "delete from customer_field_tbl where company_id=? and col_name=?";
		jdbc.update(sql, new Object[]{ companyID, fieldname });
	}

	@Override
   public PaginatedList getProfilefiledList(int companyID, String sort, String direction, int page, int rownums) {

		if(StringUtils.isEmpty(sort)) {
			sort = "shortname";
		}

		if(StringUtils.isEmpty(direction)) {
			direction = "asc";
		}


        String totalRowsQuery = "select count(column) from customer_field_tbl WHERE company_id = ?";

        JdbcTemplate templateForTotalRows = new JdbcTemplate(this.dataSource);

        int totalRows = -1;
        try {
            totalRows = templateForTotalRows.queryForInt(totalRowsQuery, new Object[]{companyID});
        } catch (Exception e) {
            totalRows = 0; // query for int has a bug , it doesn't return '0' in case of nothing is found...
        }


        int offset = (page - 1) * rownums;
        String profilefieldListQuery = "select shortname, description, rid from mailloop_tbl WHERE company_id = ? order by " + sort + " " + direction + "  LIMIT ? , ? ";

        JdbcTemplate templateForProfilefield = new JdbcTemplate(this.dataSource);
        List<Map> mailloopElements = templateForProfilefield.queryForList(profilefieldListQuery, new Object[]{companyID, offset, rownums});
        return new MailloopPaginatedList(toMailloopList(mailloopElements), totalRows, page, rownums, sort, direction);
    }


     private List<MailloopEntry> toMailloopList(List<Map> mailloopElements) {
        List<MailloopEntry> mailloopEntryList = new ArrayList<MailloopEntry>();
		for(Map row:mailloopElements) {
			Long id =  (Long) row.get("rid");
			String description = (String) row.get("description");
            String shortname=(String) row.get("shortname");
			MailloopEntry entry = new MailloopEntryImpl(id , description, shortname, "");
			mailloopEntryList.add(entry);
		}

		return mailloopEntryList;

    }
}
