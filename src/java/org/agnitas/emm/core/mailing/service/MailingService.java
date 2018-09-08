package org.agnitas.emm.core.mailing.service;

import java.util.List;

import org.agnitas.beans.MaildropEntry;
import org.agnitas.beans.Mailing;
import org.agnitas.emm.core.velocity.VelocityCheck;


public interface MailingService {

	int addMailing(MailingModel model);
	
	int addMailingFromTemplate(MailingModel model);

	Mailing getMailing(MailingModel model);

	void updateMailing(MailingModel model);

	void deleteMailing(MailingModel model);
	
	List<Mailing> getMailings(MailingModel model);
	
	List<Mailing> getMailingsForMLID(MailingModel model);

	void sendMailing(MailingModel model);

	MaildropEntry addMaildropEntry(MailingModel model);
	
	/**
	 * Returns the number of minutes, a mailing is generated before delivery.
	 *  
	 * @param companyID companyID
	 *  
	 * @return number of minutes
	 */
	public int getMailGenerationMinutes(@VelocityCheck int companyID);

	/**
	 * Checks, if given mailing is already world-sent or scheduled for world-send.
	 * 
	 * @param mailingID mailing ID
	 * @param companyID company ID of mailing
	 * 
	 * @return {@code true}, if mailing is world sent
	 * 
	 * @throws MailingNotExistException if mailing ID is unknown
	 */
	boolean isMailingWorldSent(int mailingID, @VelocityCheck int companyID) throws MailingNotExistException;

}
