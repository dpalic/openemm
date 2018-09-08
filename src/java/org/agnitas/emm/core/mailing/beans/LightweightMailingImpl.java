package org.agnitas.emm.core.mailing.beans;

import org.agnitas.beans.Mailing;

/**
 * Implementation of {@link LightweightMailing}.
 * 
 * @author md
 */
public class LightweightMailingImpl implements LightweightMailing {

	/** ID of mailing. */
	private int mailingID;
	
	/** ID of company of mailing. */
	private int companyID;
	
	/** Description of mailing. */
	private String description;
	
	/** Name of mailing. */
	private String shortname;
	
	/**
	 * Creates new light-weight mailing object.
	 */
	public LightweightMailingImpl() {
		// Only here to provide default constructor
	}
	
	/**
	 * Creates new lightweight mailing object. Copies data
	 * from given heavy-weight mailing object.
	 * 
	 * @param mailing heavy-weight mailing object
	 */
	public LightweightMailingImpl( Mailing mailing) {
		compressMailingInfo( mailing);
	}
	
	@Override
	public void setMailingID(Integer mailingID) {
		this.mailingID = mailingID;
	}

	@Override
	public Integer getMailingID() {
		return this.mailingID;
	}

	@Override
	public void setCompanyID(Integer companyID) {
		this.companyID = companyID;
	}

	@Override
	public Integer getCompanyID() {
		return this.companyID;
	}

	@Override
	public void setMailingDescription(String mailingDescription) {
		this.description = mailingDescription;
	}

	@Override
	public String getMailingDescription() {
		return this.description;
	}

	@Override
	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

	@Override
	public String getShortname() {
		return this.shortname;
	}

	@Override
	public void compressMailingInfo(Mailing tmpMailing) {
		setMailingID( tmpMailing.getId());
		setCompanyID( tmpMailing.getCompanyID());
		setShortname( tmpMailing.getShortname());
		setMailingDescription( tmpMailing.getDescription());
	}

}
