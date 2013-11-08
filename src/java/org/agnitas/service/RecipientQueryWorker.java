package org.agnitas.service;

import java.io.Serializable;
import java.util.concurrent.Callable;

import org.agnitas.dao.RecipientDao;
import org.displaytag.pagination.PaginatedList;

/**
 * wrapper for a long sql query. It will be used for asynchronous tasks 
 * @author ms
 *
 */
public class RecipientQueryWorker implements Callable, Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5322355587337222824L;
	
	private RecipientDao dao;
	private  String sqlStatementForCount;
	private  String sqlStatementForRows;
	private String sort;
	private String direction;
	private int previousFullListSize; 
	private int page;
	private int rownums;
	
	
	public RecipientQueryWorker(RecipientDao dao, String sqlStatementForCount, String sqlStatementForRows, 
			String sort, String direction, int page, int rownums, int previousFullListSize) {
		this.dao = dao;
		this.sqlStatementForCount = sqlStatementForCount;
		this.sqlStatementForRows = sqlStatementForRows;
		this.sort = sort;
		this.direction = direction;
		this.page = page;
		this.rownums = rownums;
		this.previousFullListSize = previousFullListSize;
	}

	public PaginatedList call() throws Exception {
	   return dao.getRecipientList(sqlStatementForCount, sqlStatementForRows, sort, direction, page, rownums, previousFullListSize);
	}

}
