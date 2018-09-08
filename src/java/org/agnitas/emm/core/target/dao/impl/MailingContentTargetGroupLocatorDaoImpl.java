package org.agnitas.emm.core.target.dao.impl;

import javax.sql.DataSource;

import org.agnitas.beans.MaildropEntry;
import org.agnitas.beans.Mailing;
import org.agnitas.emm.core.target.dao.MailingContentTargetGroupLocatorDao;
import org.agnitas.emm.core.velocity.VelocityCheck;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * Implementation of {@link MailingContentTargetGroupLocatorDao}.
 * 
 * @author md
 */
public class MailingContentTargetGroupLocatorDaoImpl implements MailingContentTargetGroupLocatorDao {
	
	/**
	 * JDBC data source.
	 */
	private DataSource dataSource;

	@Override
	public boolean hasMailingContentWithTargetGroup(int targetGroupID, @VelocityCheck int companyID) {
		SimpleJdbcTemplate template = new SimpleJdbcTemplate(dataSource);
	
		int count = countMailingContentForActivatableMailings(template, targetGroupID, companyID);
		if(count > 0) {	// Manually implemented "early termination" of this method
			return true;
		}
		
		count = countMailingContentForDirectlyDeliverableMailings(template, targetGroupID, companyID);
		
		return count > 0;
	}

	/**
	 * Counts the number of mailing content blocks for activatable mailings using the given target group.
	 * 
	 * @param template {@link SimpleJdbcTemplate} for DB access
	 * @param targetGroupID ID of target group
	 * @param companyID company ID
	 * 
	 * @return number of mailing content blocks using given target group
	 */
	private static int countMailingContentForDirectlyDeliverableMailings(SimpleJdbcTemplate template, int targetGroupID, @VelocityCheck int companyID) {
		String sql = "SELECT count(*) FROM mailing_tbl m, dyn_content_tbl c, dyn_name_tbl n WHERE m.company_id=? AND m.deleted=0 AND NOT m.mailing_type IN (?, ?) AND n.mailing_id=m.mailing_id AND n.dyn_name_id=c.dyn_name_id AND c.target_id=?";

		return template.queryForInt(sql, companyID, Mailing.TYPE_NORMAL, Mailing.TYPE_FOLLOWUP, targetGroupID);
	}

	/**
	 * Counts the number of mailing content blocks for directly deliverable mailings using the given target group.
	 * 
	 * @param template {@link SimpleJdbcTemplate} for DB access
	 * @param targetGroupID ID of target group
	 * @param companyID company ID
	 * 
	 * @return number of mailing content blocks using given target group
	 */
	 private static int countMailingContentForActivatableMailings(SimpleJdbcTemplate template, int targetGroupID, @VelocityCheck int companyID) {
		 String sql = "SELECT count(*) FROM mailing_tbl m, maildrop_status_tbl mds, dyn_content_tbl c, dyn_name_tbl n WHERE m.mailing_id=mds.mailing_id AND m.company_id=? AND m.deleted=0 AND m.mailing_type IN (?, ?) AND mds.genstatus<>? AND n.mailing_id=m.mailing_id AND n.dyn_name_id=c.dyn_name_id AND c.target_id=?";
		 
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
