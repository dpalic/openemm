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

import java.util.List;
import java.util.Map;


import org.agnitas.beans.Mailing;
import org.agnitas.beans.MailingBase;
import org.displaytag.pagination.PaginatedList;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * @author mhe
 */
public interface MailingDao extends ApplicationContextAware {
    /**
     * Gets mailing
     */
    Mailing getMailing(int mailingID, int companyID);

    /**
     * Saves mailing
     */
    int saveMailing(Mailing mailing);
    
    /**
     * Deletes mailing
     */
    boolean deleteMailing(int mailingID, int companyID);
    
    /**
     * Getter for list of mailingIDs.
     *
     * @return list of mailingIDs.
     */
    List getMailingsForMLID(int companyID, int mailinglistID); 
    
    Map<String, String> loadAction(int mailingID, int companyID);

	int	findLastNewsletter(int customerID, int companyID, int mailinglist);

	String[] getTag(String name, int companyID);

    boolean deleteContentFromMailing(MailingBase mailing, int contentID);
	String getAutoURL(int mailingID);
	String getAutoURL(int mailingID, int companyID);
	
	public PaginatedList getMailingList(int companyID, String types, boolean isTemplate, String sort, String direction, int page, int rownums);
	
	public String getFormat(int type);
	
	/**
	 * if a mailing has been as a world mailing a statusid has been generated
	 * @param mailingID
	 * @param companyID
	 * @return 0 if no worldmailing has been generated
	 */
	public int getStatusidForWorldMailing(int mailingID, int companyID);
	
	
	public int getGenstatusForWorldMailing(int mailingID) throws Exception;
	
	/**
	 * Checks, if a mailing has at least one recipient required for preview.
	 *
	 * @param mailingId 
	 * @param companyID
	 * @return true, if at least one recipient is present, otherwise false
	 */
	public boolean hasPreviewRecipients(int mailingId, int companyID);

    public Map<Integer, Integer> getAllMailingsOnTheSystem();
    
    /**
     * Is there any transmission for that mailing running ?
     * - There is no entry in maildrop_status_tbl for that mailing_id -> ready
     * - There are matching entries in both maildrop_status_tbl and mailing_account_tbl that means -> ready
     * - There are only entries in maildrop_status_tbl -> not ready
     * 
     * @param mailingID
     * @return 
     */
    public boolean isTransmissionRunning(int mailingID);

    public boolean hasActions(int mailingId, int companyID);

    public boolean cleanupContentForDynName(int mailingID, String dynName, int companyID);

}
