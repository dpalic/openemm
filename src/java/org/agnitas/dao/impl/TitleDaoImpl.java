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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.agnitas.beans.SalutationEntry;
import org.agnitas.beans.Title;
import org.agnitas.beans.impl.PaginatedListImpl;
import org.agnitas.beans.impl.SalutationEntryImpl;
import org.agnitas.beans.impl.TitleImpl;
import org.agnitas.dao.TitleDao;
import org.agnitas.emm.core.velocity.VelocityCheck;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.DbUtilities;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.displaytag.pagination.PaginatedList;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 * 
 * @author mhe
 */
public class TitleDaoImpl extends BaseHibernateDaoImpl implements TitleDao {
	private static final transient Logger logger = Logger.getLogger(TitleDaoImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public Title getTitle(int titleID, @VelocityCheck int companyID) {
		if (titleID == 0) {
			return null;
		} else {
			HibernateTemplate tmpl = new HibernateTemplate(sessionFactory);
				return (Title) AgnUtils.getFirstResult(tmpl.find("from Title where id = ? and (companyID = ? or companyID=0)", new Object[] { new Integer(titleID), new Integer(companyID) }));
		}
	}

	@Override
	public boolean delete(int titleID, @VelocityCheck int companyID) {
		Title title = getTitle(titleID, companyID);

		if (title != null) {
			HibernateTemplate tmpl = new HibernateTemplate(sessionFactory);
			tmpl.delete(title);
			tmpl.flush();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public PaginatedList getSalutationList(@VelocityCheck int companyID, String sort, String direction, int page, int rownums) {
		if (StringUtils.isBlank(sort)) {
			sort = "title_id";
		}

		String sortClause;
		try {
			sortClause = " ORDER BY " + sort;
			if (StringUtils.isNotBlank(direction)) {
				sortClause = sortClause + " " + direction;
			}
		} catch (Exception e) {
			logger.error("Invalid sort field", e);
			sortClause = "";
		}

		String totalRowsQuery = "SELECT COUNT(title_id) FROM title_tbl WHERE company_id = ?";
		
		int totalRows = -1;
		try {
			totalRows = selectInt(logger, totalRowsQuery, companyID);
		} catch (Exception e) {
			// query for int has a bug , it doesn't return '0' in case of nothing is found...
			totalRows = 0;
		}
		// the page numeration begins with 1
		if (page < 1) {
			page = 1;
		}

		int offset = (page - 1) * rownums;
		String salutationListQuery = "SELECT title_id, description FROM title_tbl WHERE company_id = ?" + sortClause + " LIMIT ?, ?";

		List<Map<String, Object>> salutationElements = select(logger, salutationListQuery, companyID, offset, rownums);
		return new PaginatedListImpl<SalutationEntry>(toSalutationList(salutationElements), totalRows, rownums, page, sort, direction);

	}

	protected List<SalutationEntry> toSalutationList(List<Map<String, Object>> salutationElements) {
		List<SalutationEntry> salutationEntryList = new ArrayList<SalutationEntry>();
		for (Map<String, Object> row : salutationElements) {
			Integer titleId = ((Number) row.get("title_id")).intValue();
			String description = (String) row.get("description");
			SalutationEntry entry = new SalutationEntryImpl(titleId, description);
			salutationEntryList.add(entry);
		}

		return salutationEntryList;
	}

	@Override
	public List<Title> getTitles(@VelocityCheck int companyID) {
		List<Title> result = new ArrayList<Title>();
		String sql = "SELECT DISTINCT(title_id), description FROM title_tbl WHERE company_id IN (0, ?) ORDER BY description";
		List<Map<String, Object>> list = select(logger, sql, companyID);
		for (Map<String, Object> map : list) {
			Title title = new TitleImpl();
			title.setCompanyID(companyID);
			title.setDescription((String) map.get("description"));
			title.setId(((Number) map.get("title_id")).intValue());
			result.add(title);
		}
		
		return result;
	}
}
