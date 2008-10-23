package org.agnitas.service;

import java.util.concurrent.Callable;

import org.agnitas.dao.RecipientDao;
import org.displaytag.pagination.PaginatedList;

/**
 * wrapper for a long sql query. It will be used for asynchronous tasks 
 * @author ms
 *
 */
public class RecipientQueryWorker implements Callable {

	
	private RecipientDao dao;
	private  String sqlStatement;
	private String sort;
	private String direction;
	private int previousFullListSize; 
	private int page;
	private int rownums;
	
	
	public RecipientQueryWorker(RecipientDao dao, String sqlStatement,
			String sort, String direction, int page, int rownums, int previousFullListSize) {
		this.dao = dao;
		this.sqlStatement = sqlStatement;
		this.sort = sort;
		this.direction = direction;
		this.page = page;
		this.rownums = rownums;
		this.previousFullListSize = previousFullListSize;
	}

	public PaginatedList call() throws Exception {
	   return dao.getRecipientList(sqlStatement, sort, direction, page, rownums, previousFullListSize);
	}

}
