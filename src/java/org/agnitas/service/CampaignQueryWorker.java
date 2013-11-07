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

package org.agnitas.service;

import java.io.Serializable;
import java.util.Locale;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;

import org.agnitas.beans.Campaign;
import org.agnitas.beans.impl.CampaignStatsImpl;
import org.agnitas.dao.CampaignDao;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.forms.CampaignForm;
import org.springframework.context.ApplicationContext;

/**
 * wrapper for a long sql query. It will be used for asynchronous tasks 
 * @author mu
 *
 */
public class CampaignQueryWorker implements Callable, Serializable {
	protected CampaignDao campaignDao = null;
	protected String sqlStatement = "";
	protected CampaignForm aForm = null;
	protected HttpServletRequest req = null;
	protected boolean mailTracking = false;
	protected ApplicationContext aContext = null;
	protected Locale aLoc = null;
	

	// Constructor. You have to set all needed Parameters here
	// because the "call"-Method has no parameters!
	public CampaignQueryWorker(CampaignDao campaignDao, Locale aLoc, CampaignForm aForm, HttpServletRequest req, boolean mailTracking, ApplicationContext aContext ) {
		this.campaignDao = campaignDao;		
		this.aForm = aForm;
		this.req = req;
		this.mailTracking = mailTracking;
		this.aContext = aContext;
		this.aLoc = aLoc;
	}

	// this method will be called asynchron to get the Database-Entries.
	// the return-value is a Hashtable containing the stats.
	public CampaignStatsImpl call() throws Exception {
		Campaign campaign = campaignDao.getCampaign(aForm.getCampaignID(), AgnUtils.getCompanyID(req));		
		CampaignStatsImpl stat = campaignDao.getStats(mailTracking, aLoc, null, campaign, aContext, null);		
		return stat;
	}	
}
