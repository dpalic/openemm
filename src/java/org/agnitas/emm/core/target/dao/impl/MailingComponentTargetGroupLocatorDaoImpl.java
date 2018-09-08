package org.agnitas.emm.core.target.dao.impl;

import javax.sql.DataSource;

import org.agnitas.beans.MaildropEntry;
import org.agnitas.beans.Mailing;
import org.agnitas.emm.core.target.dao.MailingComponentTargetGroupLocatorDao;
import org.agnitas.emm.core.velocity.VelocityCheck;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * Implementation of {@link MailingComponentTargetGroupLocatorDao}.
 * 
 * @author md
 */
public class MailingComponentTargetGroupLocatorDaoImpl implements MailingComponentTargetGroupLocatorDao {
	
	/**
	 * JDBC data source.
	 */
	private DataSource dataSource;

	@Override
	public boolean hasMailingComponentWithTargetGroup(int targetGroupID, @VelocityCheck int companyID) {
		SimpleJdbcTemplate template = new SimpleJdbcTemplate(dataSource);
	
		int count = countMailingComponentsForActivatableMailings(template, targetGroupID, companyID);
		if(count > 0) {	// Manually implemented "early termination" of this method
			return true;
		}
		
		count = countMailingComponentsForDirectlyDeliverableMailings(template, targetGroupID, companyID);
		
		return count > 0;
	}

	/**
	 * Counts the number of mailing components for activatable mailings using the given target group.
	 * 
	 * @param template {@link SimpleJdbcTemplate} for DB access
	 * @param targetGroupID ID of target group
	 * @param companyID company ID
	 * 
	 * @return number of mailing components using given target group
	 */
	private static int countMailingComponentsForDirectlyDeliverableMailings(SimpleJdbcTemplate template, int targetGroupID, @VelocityCheck int companyID) {
		String sql = "SELECT count(*) FROM mailing_tbl m, component_tbl c WHERE m.company_id=? AND m.deleted=0 AND NOT m.mailing_type IN (?, ?) AND c.mailing_id=m.mailing_id AND c.target_id=?";

		return template.queryForInt(sql, companyID, Mailing.TYPE_NORMAL, Mailing.TYPE_FOLLOWUP, targetGroupID);
	}

	/**
	 * Counts the number of mailing components for directly deliverable mailings using the given target group.
	 * 
	 * @param template {@link SimpleJdbcTemplate} for DB access
	 * @param targetGroupID ID of target group
	 * @param companyID company ID
	 * 
	 * @return number of mailing components using given target group
	 */
	 private static int countMailingComponentsForActivatableMailings(SimpleJdbcTemplate template, int targetGroupID, @VelocityCheck int companyID) {
		 String sql = "SELECT count(*) FROM mailing_tbl m, maildrop_status_tbl mds, component_tbl c WHERE m.mailing_id=mds.mailing_id AND m.company_id=? AND m.deleted=0 AND m.mailing_type IN (?, ?) AND mds.genstatus<>? AND c.mailing_id=m.mailing_id AND c.target_id=?";
		 
		return template.queryForInt(sql, companyID, Mailing.TYPE_NORMAL, Mailing.TYPE_FOLLOWUP, MaildropEntry.GEN_FINISHED, targetGroupID);
	}

	// ------------------------------------------------------------ Dependency Injection
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
