package org.agnitas.dao.impl;

import javax.sql.DataSource;

import org.agnitas.dao.LinkcheckerDao;
import org.agnitas.util.AgnUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

public class LinkcheckerDaoImpl implements LinkcheckerDao {

	protected DataSource dataSource;
	protected ApplicationContext applicationContext;
	
	@Override
	public int getLinkTimeout() {	
		JdbcTemplate jdbc = new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
		String sql = "SELECT value FROM config_tbl WHERE class='linkchecker' AND classid='0' AND name='linktimeout'";
		int returnValue = 30000;	// default
		try {
			returnValue = jdbc.queryForInt(sql);
		} catch (Exception e) {
			// error getting properties, setting default to 30s!
			AgnUtils.logger().error("Error reading link-timeout... Setting default");
			returnValue = 30000;
		}
		return returnValue;
	}

	@Override
	public int getThreadCount() {		
		JdbcTemplate jdbc = new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
		String sql = "SELECT value FROM config_tbl WHERE class='linkchecker' AND classid='0' AND name='threadcount'";
		int returnValue = 25;	// default
		try {
			returnValue = jdbc.queryForInt(sql);
		} catch (Exception e) {
			// error getting properties, setting default to 25 threads!
			AgnUtils.logger().error("Error reading link-timeout... Setting default");
			returnValue = 25;
		}
		return returnValue;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;		
	}
	
}
