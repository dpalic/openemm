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
package org.agnitas.beans.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.displaytag.pagination.PaginatedList;
import org.displaytag.properties.SortOrderEnum;

public class PaginatedListImpl<T> implements PaginatedList {

	private List<T>  partialList;
	private int fullListSize;
	private int pageSize;
	private int pageNumber = 1;
	private String sortCriterion;
	private SortOrderEnum sortDirection = SortOrderEnum.ASCENDING; // DESC or ASC
		
	public PaginatedListImpl(List<T> partialList, int fullListSize, int pageSize, int pageNumber, String sortCriterion, String sortDirection) {
		super();
		this.partialList = partialList;
		this.fullListSize = fullListSize;
		this.pageSize = pageSize;
		this.pageNumber = pageNumber;
		this.sortCriterion = sortCriterion;
		
		if ((StringUtils.isBlank(sortDirection) || "ASC".equalsIgnoreCase(sortDirection.trim()) || "ASCENDING".equalsIgnoreCase(sortDirection.trim()))) {
			this.sortDirection = SortOrderEnum.ASCENDING;
		} else if ("DESC".equalsIgnoreCase(sortDirection.trim()) || "DESCENDING".equalsIgnoreCase(sortDirection.trim())) {
			this.sortDirection =  SortOrderEnum.DESCENDING;
		} else {
			throw new RuntimeException("Invalid sorting direction");
		}
	}
	
	public PaginatedListImpl(List<T> partialList, int fullListSize, int pageSize, int pageNumber, String sortCriterion, boolean sortedAscending) {
		super();
		this.partialList = partialList;
		this.fullListSize = fullListSize;
		this.pageSize = pageSize;
		this.pageNumber = pageNumber;
		this.sortCriterion = sortCriterion;
		
		sortDirection = sortedAscending ? SortOrderEnum.ASCENDING : SortOrderEnum.DESCENDING;
	}

	@Override
	public int getFullListSize() {
		return fullListSize;
	}

	@Override
	public List<T> getList() {
		return partialList;
	}

	@Override
	public int getObjectsPerPage() {
		return pageSize;
	}

	@Override
	public int getPageNumber() {
		return pageNumber;
	}

	@Override
	public String getSearchId()  {
		return null;
	}

	@Override
	public String getSortCriterion() {
		return sortCriterion; 
	}

	@Override
	public SortOrderEnum getSortDirection() {
		return sortDirection;
	}

	public int getPageSize() {
		return pageSize;
	}
}
