package org.agnitas.emm.core.target.dao.impl;

import javax.sql.DataSource;

import org.agnitas.emm.core.target.dao.ExportPredefTargetGroupLocatorDao;
import org.agnitas.emm.core.velocity.VelocityCheck;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * Implementation of {@link ExportPredefTargetGroupLocatorDao}.
 * 
 * @author md
 *
 */
public class ExportPredefTargetGroupLocatorDaoImpl implements ExportPredefTargetGroupLocatorDao {

	/**
	 * JDBC data source.
	 */
	private DataSource dataSource;
	
	@Override
	public boolean hasExportProfilesForTargetGroup(int targetGroupID, @VelocityCheck int companyID) {
		SimpleJdbcTemplate template = new SimpleJdbcTemplate(this.dataSource);
		
		int count = template.queryForInt("SELECT count(*) FROM export_predef_tbl WHERE company_id=? AND target_id=? AND deleted=0", companyID, targetGroupID);
		
		return count > 0;
	}

	// --------------------------------------------------- Dependency Injection
	/**
	 * Set JDBC data source.
	 * 
	 * @param dataSource JDBC data source
	 */
	@Required
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
