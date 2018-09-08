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
package org.agnitas.target.impl.validate;

import java.util.Collection;
import java.util.Vector;

import org.agnitas.beans.Mailing;
import org.agnitas.dao.MailingDao;
import org.agnitas.emm.core.velocity.VelocityCheck;
import org.agnitas.target.TargetError;
import org.agnitas.target.TargetNode;
import org.agnitas.target.TargetNodeValidator;

/**
 * Implementation of {@link TargetNodeValidator} that does no validation.
 * It does not report an error.
 *
 * @author md
 *
 */
public class MailingIdTargetNodeValidator implements TargetNodeValidator {

	/** DAO for accessing mailing data. */
	private MailingDao mailingDao;

	@Override
	public Collection<TargetError> validate(TargetNode node, @VelocityCheck int companyId) {
		try {
			int mailingId = Integer.parseInt( node.getPrimaryValue());

			Mailing mailing = mailingDao.getMailing( mailingId, companyId);

			if( mailing != null && mailing.getId() == mailingId)
				return null;

			return reportInvalidMailingId();
		} catch( Exception e) {
			return reportInvalidMailingId();
		}

	}

	/**
	 * Create error result for invalid mailing ID.
	 *
	 * @return error result for invalid mailing ID
	 */
	private Collection<TargetError> reportInvalidMailingId() {
		Collection<TargetError> errors = new Vector<TargetError>();
		errors.add( new TargetError( TargetError.ErrorKey.INVALID_MAILING));

		return errors;
	}

	// ------------------------------------------------------------- Dependency Injection
	/**
	 * Setter for mailing DAO.
	 *
	 * @param mailingDao mailing DAo
	 */
	public void setMailingDao( MailingDao mailingDao) {
		this.mailingDao = mailingDao;
	}
}
