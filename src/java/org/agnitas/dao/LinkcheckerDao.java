package org.agnitas.dao;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContextAware;

public interface LinkcheckerDao extends ApplicationContextAware {
	/**
	 * This method returns the timeout for ONE Link, not all together.
	 * @return
	 */
	public int getLinkTimeout();
	
	/**
	 * returns the amount of parallel threads which are started to check
	 * if a link is valid. If the value is 50, then up to 50 links are parallel checked.
	 * @return
	 */
	public int getThreadCount();
	
	public DataSource getDataSource();
	public void setDataSource(DataSource dataSource);
}
