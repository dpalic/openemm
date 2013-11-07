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

package org.agnitas.dao;

import java.util.LinkedList;
import java.util.Locale;

import org.agnitas.beans.Campaign;
import org.agnitas.beans.impl.CampaignStatsImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * @author Andreas Rehak, Nicole Serek, Markus Unger
 */
public interface CampaignDao extends ApplicationContextAware {
    
    /**
     * Getter for property campaign by campaign id and company id.
     *
     * @return Value of property campaign.
     */
    Campaign getCampaign(int campaignID, int companyID);
    
    public CampaignStatsImpl getStats(boolean useMailtracking, Locale aLocale, LinkedList mailingIDs, Campaign campaign, ApplicationContext con, String mailingSelection);
    
    /**
     * 
     * @param campaign
     * @return the ID of the saved campaign
     */
    public int save(Campaign campaign);
    
    /**
     * 
     * @param campaign
     * @return true if the Campaign has been deleted
     */
	public boolean delete(Campaign campaign);


}
