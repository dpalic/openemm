package org.agnitas.emm.core.useractivitylog.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.agnitas.beans.Admin;
import org.agnitas.beans.AdminEntry;
import org.agnitas.beans.impl.PaginatedListImpl;
import org.agnitas.dao.impl.PaginatedBaseDaoImpl;
import org.agnitas.emm.core.useractivitylog.UserActivityEntry;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.SqlPreparedStatementManager;
import org.agnitas.util.UserActivityLogActions;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Implementation of {@link UserActivityLogDao}.
 * 
 * @author md
 */
public class UserActivityLogDaoImpl extends PaginatedBaseDaoImpl implements UserActivityLogDao {
	
	/** The logger. */
	private static final transient Logger logger = Logger.getLogger(UserActivityLogDaoImpl.class);
	
	@Override
    public void writeUserActivityLog(Admin admin, String action, String description)  {
		String username = admin != null ? admin.getUsername() : "";
		
    	String insertSql = "INSERT INTO userlog_tbl (logtime, username, action, description) VALUES (CURRENT_TIMESTAMP, ?, ?, ?)";
		update(logger, insertSql, username, action, description);
    }

	public PaginatedListImpl<UserActivityEntry> getUserActivityEntries(List<AdminEntry> visibleAdmins, String selectedAdmin, int selectedAction, Date from, Date to, String sortColumn, String sortDirection, int pageNumber, int pageSize) throws Exception {
		if (StringUtils.isBlank(sortColumn)) {
			sortColumn = "logtime";
		}
		
		boolean sortDirectionAscending = "asc".equalsIgnoreCase(sortDirection) || "ascending".equalsIgnoreCase(sortDirection);
		
		SqlPreparedStatementManager sqlPreparedStatementManager = new SqlPreparedStatementManager("SELECT logtime, username, action, description FROM userlog_tbl");
		sqlPreparedStatementManager.addWhereClause("logtime >= ?", from);
		sqlPreparedStatementManager.addWhereClause("logtime <= ?", to);
        
        //  If set, any of the visible admins must match
        if (visibleAdmins != null && visibleAdmins.size() > 0) {
        	List<String> visibleAdminNameList = new ArrayList<String>();
        	for (AdminEntry visibleAdmin : visibleAdmins) {
        		if (visibleAdmin != null) {
	        		visibleAdminNameList.add(visibleAdmin.getUsername());
        		}
        	}
        	if (visibleAdminNameList.size() > 0) {
        		sqlPreparedStatementManager.addWhereClause("username IN (" + AgnUtils.repeatString("?", visibleAdminNameList.size(), ", ") + ")", visibleAdminNameList.toArray());
        	}
        }
        
        // If set, the selected admin must match
        if (StringUtils.isNotBlank(selectedAdmin) && !"0".equals(selectedAdmin)) {
        	sqlPreparedStatementManager.addWhereClause("username = ?", selectedAdmin);
        }
        
        // If set, the selected action must match
        if (UserActivityLogActions.ANY.getIntValue() != selectedAction) {
        	if (UserActivityLogActions.LOGIN_LOGOUT.getIntValue() == selectedAction) {
            	sqlPreparedStatementManager.addWhereClause("action IN ('do login', 'do logout', 'login', 'logout', ?)", UserActivityLogActions.getLocalValue(selectedAction));
        	} else if (UserActivityLogActions.ANY_WITHOUT_LOGIN.getIntValue() == selectedAction) {
            	sqlPreparedStatementManager.addWhereClause("action NOT IN ('do login', 'do logout', 'login', 'logout', 'login_logout')");
        	} else {
            	sqlPreparedStatementManager.addWhereClause("(LOWER(action) LIKE ? OR LOWER(action) = ?)", UserActivityLogActions.getLocalValue(selectedAction).toLowerCase() + " %", UserActivityLogActions.getLocalValue(selectedAction).toLowerCase());
            	sqlPreparedStatementManager.addWhereClause("action NOT IN ('do login', 'do logout', 'login', 'logout', 'login_logout')");
        	}
        }
            
        return selectPaginatedList(logger, sqlPreparedStatementManager.getPreparedSqlString(), "userlog_tbl", sortColumn, sortDirectionAscending, pageNumber, pageSize, new UserActivityLogRowMapper(), sqlPreparedStatementManager.getPreparedSqlParameters());
    }
}
