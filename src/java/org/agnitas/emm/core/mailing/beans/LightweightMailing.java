package org.agnitas.emm.core.mailing.beans;

import org.agnitas.beans.Mailing;
import org.agnitas.emm.core.velocity.VelocityCheck;

/**
 * Light-weight mailing containing only data used in lists, tables, etc.
 * 
 * @author md
 */
public interface LightweightMailing {
	
	/**
	 * Set ID of mailing.
	 * 
	 * @param mailingID ID of mailing
	 */
	public void setMailingID(Integer mailingID);
	
	/**
	 * Returns ID of mailing.
	 * 
	 * @return ID of mailing
	 */
	public Integer getMailingID();
	
	/**
	 * Set company ID of mailing.
	 * 
	 * @param companyID company ID of mailing
	 */
	public void setCompanyID(@VelocityCheck Integer companyID);
	
	/**
	 * Returns company ID of mailing.
	 * 
	 * @return company ID of mailing
	 */
	public Integer getCompanyID();
	
	/**
	 * Set description of mailing.
	 * 
	 * @param mailingDescription description of mailing
	 */
	public void setMailingDescription(String mailingDescription);
	
	/**
	 * Return description of mailing.
	 * 
	 * @return description of mailing
	 */
	public String getMailingDescription();	
	
	/**
	 * Set name of mailing.
	 * 
	 * @param shortname name of mailing.
	 */
	public void setShortname(String shortname);
	
	/**
	 * Return name of mailing.
	 * 
	 * @return name of mailing
	 */
	public String getShortname();

	/**
	 * Transfer data from heavy-weight mailing object to the light-weight mailing
	 * object.
	 * 
	 * @param tmpMailing heavy-weight mailing object
	 */
	public void compressMailingInfo( Mailing tmpMailing);	

}
