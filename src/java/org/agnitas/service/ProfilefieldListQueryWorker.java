package org.agnitas.service;

import java.io.Serializable;
import java.util.concurrent.Callable;

import org.agnitas.dao.ProfileFieldDao;
import org.displaytag.pagination.PaginatedList;

/**
 * wrapper for a long sql query. It will be used for asynchronous tasks
 * @author viktor gema
 *
 */
public class ProfilefieldListQueryWorker implements Callable, Serializable {


	/**
	 *
	 */
	private static final long serialVersionUID = -3047853895576634885L;


	private ProfileFieldDao profilefieldDao;
	private String sort;
	private String direction;
	private int page;
	private int rownums;
	private int companyID;


    public ProfilefieldListQueryWorker(ProfileFieldDao dao, int companyID,
			String sort, String direction, int page, int rownums ) {
		this.profilefieldDao = dao;
		this.sort = sort;
		this.direction = direction;
		this.page = page;
		this.rownums = rownums;
		this.companyID = companyID;
	}

    public PaginatedList call() throws Exception {
	   return profilefieldDao.getProfilefiledList(companyID, sort, direction, page, rownums); 
	}



}