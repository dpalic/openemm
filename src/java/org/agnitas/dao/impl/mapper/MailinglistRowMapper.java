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
package org.agnitas.dao.impl.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.agnitas.beans.Mailinglist;
import org.agnitas.beans.impl.MailinglistImpl;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

public class MailinglistRowMapper implements ParameterizedRowMapper<Mailinglist> {

	@Override
	public Mailinglist mapRow( ResultSet rs, int row) throws SQLException {
		Mailinglist mailinglist = new MailinglistImpl();

		mailinglist.setCompanyID( rs.getInt( "company_id"));
		mailinglist.setDescription( rs.getString( "description"));
		mailinglist.setId( rs.getInt( "mailinglist_id"));
		mailinglist.setShortname( rs.getString( "shortname"));

		return mailinglist;
	}


}
