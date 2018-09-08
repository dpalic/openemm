package org.agnitas.emm.core.mediatypes.dao;

import java.util.Map;

import org.agnitas.beans.Mediatype;
import org.agnitas.emm.core.velocity.VelocityCheck;

/**
 * Interface for accessing media types of mailings.
 * 
 * @author md
 *
 */
public interface MediatypesDao {
	
	/**
	 * Read all mediatypes for given mailing.
	 * 
	 * @param mailingId mailing ID
	 * @param companyId company ID
	 * 
	 * @return mapping from media type code to media type
	 * 
	 * @throws MediatypesDaoException on errors during reading media types
	 */
	public Map<Integer, Mediatype> loadMediatypes( int mailingId, @VelocityCheck int companyId) throws MediatypesDaoException;
}
