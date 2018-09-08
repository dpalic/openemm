package org.agnitas.emm.core.target.dao.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.agnitas.beans.MaildropEntry;
import org.agnitas.beans.Mailing;
import org.agnitas.dao.impl.mapper.StringRowMapper;
import org.agnitas.emm.core.target.dao.MailingTargetExpressionLocatorDao;
import org.agnitas.emm.core.velocity.VelocityCheck;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * Implementation of {@link MailingTargetExpressionLocatorDao}.
 * 
 * @author md
 */
public class MailingTargetExpressionLocatorDaoImpl implements MailingTargetExpressionLocatorDao {
	
	/**
	 * JDBC DataSource.
	 */
	private DataSource dataSource;  
	
	@Override
	public Set<String> getTargetExpressionsForMailings(@VelocityCheck int companyID) {
		 Set<String> allExpressions = new HashSet<>();

		 allExpressions.addAll(getTargetExpressionsForActivatableMailing(companyID));
		 allExpressions.addAll(getTargetExpressionsForDirectlyDeliverableMailing(companyID));
		 
		 return allExpressions;
	}
	
	/**
	 * Determines all target expression of relevant activatable mailings (action-, event-based, ...).
	 * 
	 * @param companyID company ID to check
	 * 
	 * @return set of target expressions
	 */
	private List<String> getTargetExpressionsForActivatableMailing(@VelocityCheck int companyID) {  
		SimpleJdbcTemplate template = new SimpleJdbcTemplate(this.dataSource);
		
		String sql = "SELECT target_expression FROM mailing_tbl WHERE company_id=? AND deleted=0 AND NOT mailing_type IN (?, ?)";
		
		return template.query(sql, new StringRowMapper(), companyID, Mailing.TYPE_NORMAL, Mailing.TYPE_FOLLOWUP);
	}
	
	/**
	 * Determines all target expression of relevant directly deliverable mailings (normal and follow-up mailings).
	 * 
	 * @param companyID company ID to check
	 * 
	 * @return set of target expressions
	 */
	private List<String> getTargetExpressionsForDirectlyDeliverableMailing(@VelocityCheck int companyID) {  
		SimpleJdbcTemplate template = new SimpleJdbcTemplate(this.dataSource);
		
		String sql = "SELECT m.target_expression FROM mailing_tbl m, maildrop_status_tbl mds WHERE m.mailing_id=mds.mailing_id AND m.company_id=? AND m.deleted=0 AND m.mailing_type IN (?, ?) AND mds.genstatus<>?";
		
		return template.query(sql, new StringRowMapper(), companyID, Mailing.TYPE_NORMAL, Mailing.TYPE_FOLLOWUP, MaildropEntry.GEN_FINISHED);
	}
	
	
	// ----------------------------------------------------------------------------------- Dependency Injection
	/**
	 * Set JDBC DataSource.
	 * 
	 * @param dataSource JDBC DataSource
	 */
	@Required
	public void setDataSource(DataSource dataSource) { 
		this.dataSource = dataSource;
	}

}
