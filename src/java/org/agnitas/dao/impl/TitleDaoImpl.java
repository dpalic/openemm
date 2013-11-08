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

import org.agnitas.beans.Title;
import org.agnitas.beans.BlackListEntry;
import org.agnitas.beans.SalutationEntry;
import org.agnitas.beans.impl.BlacklistPaginatedList;
import org.agnitas.beans.impl.BlackListEntryImpl;
import org.agnitas.beans.impl.SalutationEntryImpl;
import org.agnitas.beans.impl.SalutationPaginatedList;
import org.agnitas.dao.TitleDao;
import org.agnitas.util.AgnUtils;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.displaytag.pagination.PaginatedList;
import org.apache.commons.lang.StringUtils;

import javax.sql.DataSource;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author mhe
 */
public class TitleDaoImpl implements TitleDao {
    
	public Title getTitle(int titleID, int companyID) {
		HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
        
		if(titleID==0) {
			return null;
		}

		return (Title)AgnUtils.getFirstResult(tmpl.find("from Title where id = ? and (companyID = ? or companyID=0)", new Object [] {new Integer(titleID), new Integer(companyID)} ));
	}
    

	public boolean	delete(int titleID, int companyID) {
		HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
		Title title=getTitle(titleID, companyID);

		if(title != null) {
			tmpl.delete(title);
			tmpl.flush();
			return true;
		}
		return false;
	}

    public PaginatedList getSalutationList(int companyID, String sort, String direction, int page, int rownums) {

        if (StringUtils.isEmpty(sort)) {
            sort = "title_id";
        }

        if (StringUtils.isEmpty(direction)) {
            direction = "asc";
        }


        String totalRowsQuery = "select count(title_id) from title_tbl WHERE company_id = ?";

        JdbcTemplate templateForTotalRows = new JdbcTemplate(getDataSource());

        int totalRows = -1;
        try {
            totalRows = templateForTotalRows.queryForInt(totalRowsQuery, new Object[]{companyID});
        } catch (Exception e) {
            totalRows = 0; // query for int has a bug , it doesn't return '0' in case of nothing is found...
        }
         // the page numeration begins with 1
        if (page < 1) {
        	page = 1;
        }

        int offset = (page - 1) * rownums;
        String salutationListQuery = "select title_id, description from title_tbl WHERE company_id = ? order by " + sort + " " + direction + "  LIMIT ? , ? ";

        JdbcTemplate templateForSalutation = new JdbcTemplate(getDataSource());
        List<Map> salutationElements = templateForSalutation.queryForList(salutationListQuery, new Object[]{companyID, offset, rownums});
        return new SalutationPaginatedList(toSalutationList(salutationElements), totalRows, page, rownums, sort, direction);

    }

    protected List<SalutationEntry> toSalutationList(List<Map> salutationElements) {
        List<SalutationEntry> salutationEntryList = new ArrayList<SalutationEntry>();
        for (Map row : salutationElements) {
            Integer titleId = (Integer) row.get("title_id");
            String description = (String) row.get("description");
            SalutationEntry entry = new SalutationEntryImpl(titleId, description);
            salutationEntryList.add(entry);
        }

        return salutationEntryList;

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
