package org.agnitas.dao;

import java.util.Map;

public interface ColumnInfoDao {
	 public Map getColumnInfo(int companyID, String column) throws Exception;
}
