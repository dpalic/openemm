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
package org.agnitas.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.agnitas.beans.TrackableLink;
import org.agnitas.dao.LinkcheckerDao;
import org.apache.log4j.Logger;

public class LinkcheckService {
	private static final transient Logger logger = Logger.getLogger(LinkcheckService.class);

	private LinkcheckerDao linkcheckerDao;	// injected by Spring.

	/**
	 * this method checks every item of the given link-list if the
	 * appropriate url is available.
	 * The returned list contains all urls which sent no response or have other failures.
	 * @param linkList
	 * @return
	 */
	public Collection<String> checkAvailability(Collection<TrackableLink> linkList) {
		// create usual <String> List
		Vector<String> checkList = new Vector<String>();
		TrackableLink tmpLink = null;
		Iterator<TrackableLink> it = linkList.iterator();
		while (it.hasNext()) {
			tmpLink = it.next();
			checkList.add(tmpLink.getFullUrl());
		}
		return checkURLAvailability(checkList);
	}

	/**
	 * @param linkList
	 * @return
	 */
	public Vector<String> checkURLAvailability(Vector<String> linkList) {
		// create Pool
		ExecutorService executor = Executors.newFixedThreadPool(linkcheckerDao.getThreadCount());

		ArrayList<String> loopList = new ArrayList<String>();
		loopList.addAll(linkList);

		// create runnables
		for (String url : loopList) {
			executor.execute(new LinkcheckWorker(linkcheckerDao.getLinkTimeout(), linkList, url));
		}
		executor.shutdown();	// no new task are scheduled
		try {
			executor.awaitTermination((linkcheckerDao.getLinkTimeout() + 1000), TimeUnit.MILLISECONDS );
		} catch (InterruptedException e) {
			logger.error(e);
		}
		return linkList;
	}

	public LinkcheckerDao getLinkcheckerDao() {
		return linkcheckerDao;
	}

	public void setLinkcheckerDao(LinkcheckerDao linkcheckerDao) {
		this.linkcheckerDao = linkcheckerDao;
	}
}
