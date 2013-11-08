/**
 * This class is intended to simplify access to the config_tbl.
 */
package org.agnitas.emm.core.commons.util;

import java.util.ArrayList;

import javax.sql.DataSource;

import org.agnitas.util.AgnUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class ConfigTableDao {
	
	private DataSource dataSource;

	public String getEntry(String classname, String classid, String key) throws Exception {
		String returnValue = null;
		if (dataSource == null) {
			throw new Exception("ConfigTableDao: no dataSource set. Please use setDataSource(dataSource)");
		}
		if (key == null) {
			throw new Exception("ConfigTableDao: You must provide a key.");
		}
		JdbcTemplate jdbc = new JdbcTemplate(dataSource);
		
		ArrayList<String> liste = new ArrayList<String>();		
				
		String sql = "SELECT value FROM config_tbl WHERE name=? ";
		liste.add(key);
		if (classname != null) {
			sql += " AND class=? ";
			liste.add(classname);
		}
		if (classid != null) { 
			sql += " AND classid=? ";
			liste.add(classid);
		}
		
		Object[] args = liste.toArray();
		try {
			returnValue = (String) jdbc.queryForObject(sql, args, String.class);
		} catch (IncorrectResultSizeDataAccessException e) {
			// We log only at info level because its normal to NOT find a value
			AgnUtils.logger().info("Nothing found for query: " + sql + " values: class: " + classname + " classid: " + classid + " key: " + key);			
		} catch (DataAccessException e) {
			AgnUtils.logger().error("Problems getting a result for query: " + sql + " values: class: " + classname + " classid: " + classid + " key: " + key);
		}
		return returnValue;
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
