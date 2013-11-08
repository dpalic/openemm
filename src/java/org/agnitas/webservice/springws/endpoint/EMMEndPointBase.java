package org.agnitas.webservice.springws.endpoint;


import java.util.List;

import javax.sql.DataSource;

import org.agnitas.util.SafeString;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

/**
 * Agnitas base class for all spring web services end points 
 * @author benno
 *
 */
public abstract class EMMEndPointBase extends AbstractMarshallingPayloadEndpoint {

	/*
	 * injected
	 */
	private DataSource datasource;

	@SuppressWarnings("unchecked")
	protected boolean authenticateUser(String user, String pwd, int companyID) {
		boolean result = false;
		
		JdbcTemplate jdbc = new JdbcTemplate(getDatasource());

		try {
			List rset = jdbc.queryForList("select a.ws_admin_id from ws_admin_tbl a where a.username='"
							+ SafeString.getSQLSafeString(user)
							+ "' and a.password='"
							+ SafeString.getSQLSafeString(pwd) + "'");
			if (rset != null && rset.size() > 0) {
				result = true;
			} else {
				result = false;
			}
		} catch (Exception e) {
			System.out.println("soap authentication: " + e);
			result = false;
		}

		return result;
	}

	/**
	 * @param datasource the datasource to set
	 */
	public void setDatasource(DataSource datasource) {
		this.datasource = datasource;
	}

	/**
	 * @return the datasource
	 */
	public DataSource getDatasource() {
		return datasource;
	}
}
