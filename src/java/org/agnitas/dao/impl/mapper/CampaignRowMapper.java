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

import org.agnitas.beans.Campaign;
import org.agnitas.beans.impl.CampaignImpl;
import org.springframework.jdbc.core.RowMapper;

public class CampaignRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet resultSet, int row) throws SQLException {

		Campaign campaign = new CampaignImpl();
		campaign.setId(resultSet.getBigDecimal("campaign_id").intValue());
		campaign.setCompanyID(resultSet.getBigDecimal("company_id").intValue());
		campaign.setDescription(resultSet.getString("description"));
		campaign.setShortname(resultSet.getString("shortname"));

		return campaign;
	}

}
