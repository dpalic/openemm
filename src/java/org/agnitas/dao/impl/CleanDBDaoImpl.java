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

package org.agnitas.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.agnitas.beans.AdminGroup;
import org.agnitas.beans.Recipient;
import org.agnitas.beans.impl.RecipientImpl;
import org.agnitas.beans.impl.AdminGroupImpl;
import org.agnitas.dao.CleanDBDao;
import org.agnitas.util.AgnUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

/**
 * 
 * @author Nicole Serek
 */
public class CleanDBDaoImpl extends BaseDaoImpl implements CleanDBDao {
	private static final transient Logger logger = Logger.getLogger(CleanDBDaoImpl.class);

	@Override
	public void cleanup() {

		if (!AgnUtils.isOracleDB()) {
			update(logger, "DELETE FROM bounce_tbl WHERE " + AgnUtils.changeDateName() + " < date_sub(curdate(), interval " + AgnUtils.getDefaultIntValue("bounces.maxRemain.days") + " day)");
		} else {
			// OracleDB handles bounces differently
		}

		if (!AgnUtils.isOracleDB()) {
			update(logger, "DELETE FROM customer_1_binding_tbl WHERE user_status=5 AND " + AgnUtils.changeDateName() + " < date_sub(curdate(), interval " + AgnUtils.getDefaultIntValue("pending.maxRemain.days") + " day)");
			List<Recipient> customer = select(logger, "select * from customer_1_tbl c WHERE NOT EXISTS (SELECT 1 FROM customer_1_binding_tbl b WHERE b.customer_id = c.customer_id)", new CustomerID_RowMapper());
			for (Recipient customerList : customer) {
				update(logger, "DELETE FROM customer_1_tbl WHERE customer_id = ?", customerList.getCustomerID());
			}
		} else {
			// OracleDB handles pending subscribers differently
		}
	}
	
	public class CustomerID_RowMapper implements ParameterizedRowMapper<Recipient> {
		@Override
		public Recipient mapRow(ResultSet resultSet, int row) throws SQLException {
			Recipient custID = new RecipientImpl();
			
			custID.setCustomerID((resultSet.getInt("customer_id")));
			
			return custID;
		}
	}
}