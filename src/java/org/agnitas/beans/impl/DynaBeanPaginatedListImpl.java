package org.agnitas.beans.impl;

import java.util.List;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang.NotImplementedException;
import org.displaytag.pagination.PaginatedList;
import org.displaytag.properties.SortOrderEnum;

public class DynaBeanPaginatedListImpl implements PaginatedList {

	private List<DynaBean>  partialList;
	private int fullListSize;
	private int pageSize;
	private int pageNumber = 1;
	private String sortCriterion;
	private SortOrderEnum sortDirection = SortOrderEnum.ASCENDING; // DESC or ASC
		
	public DynaBeanPaginatedListImpl(List<DynaBean> partialList,
			int fullListSize, int pageSize, int pageNumber,
			String sortCriterion, String sortDirection) {
		super();
		this.partialList = partialList;
		this.fullListSize = fullListSize;
		this.pageSize = pageSize;
		this.pageNumber = pageNumber;
		this.sortCriterion = sortCriterion;
		setSortDirection(sortDirection);
	}

	public void setPartialList(List<DynaBean> partialList) {
		this.partialList = partialList;
	}

	public int getFullListSize() {
		return fullListSize;
	}

	public List<DynaBean> getList() {
		return partialList;
	}

	public int getObjectsPerPage() {
		return pageSize;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public String getSearchId()  {
		return null;
	}

	public String getSortCriterion() {
		return sortCriterion; 
	}

	public SortOrderEnum getSortDirection() {
		return sortDirection;
	}

	public void setSortDirection(String sortDirection) {
		this.sortDirection = "ASC".equalsIgnoreCase(sortDirection) ?  SortOrderEnum.ASCENDING : SortOrderEnum.DESCENDING;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setFullListSize(int fullListSize) {
		this.fullListSize = fullListSize;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public void setSortCriterion(String sortCriterion) {
		this.sortCriterion = sortCriterion;
	}
	
}
