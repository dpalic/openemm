package org.agnitas.emm.core.useractivitylog.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.agnitas.beans.Admin;
import org.agnitas.beans.AdminEntry;
import org.agnitas.beans.impl.PaginatedListImpl;
import org.agnitas.emm.core.useractivitylog.UserActivityEntry;
import org.agnitas.emm.core.useractivitylog.dao.UserActivityLogDao;
import org.agnitas.service.UserActivityLogService;
import org.agnitas.web.forms.UserActivityLogForm;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * Implementation of {@link UserActivityLogService}.
 * This implementation accesses the activity data in database.
 * 
 * @author md
 *
 */
public class DbUserActivityLogServiceImpl implements UserActivityLogService {
	/** Date format used to parse start and end of period. */
	private static final transient DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	/** The logger. */
	private static final transient Logger logger = Logger.getLogger( DbUserActivityLogServiceImpl.class);
	
	/** Name of property holding date of user activity. */
	public static final String DATE_PROPERTY = "date";
	
	/** Name of property holding username of user activity. */
	public static final String USER_PROPERTY = "username";
	
	/** Name of property holding action of user activity. */
	public static final String ACTION_PROPERTY = "action"; 
	
	/** Name of property holding description of user activity. */
	public static final String DESCRIPTION_PROPERTY = "description";
	
	/** Name of property holding displayed name of user. */
	public static final String SHOWN_NAME_PROPERTY = "shownName";

	// ----------------------------------------------------------------------------------------------------------- Business Code
	
	@Override
	public PaginatedListImpl<UserActivityEntry> getUserActivityLogByFilter(UserActivityLogForm aForm,
			int pageNumber, int pageSize, int adminID, String sortColumn,
			String sortDirection, List<AdminEntry> visibleAdmins) throws Exception {
		
		// Extract date
		Date fromDate = DATE_FORMAT.parse(aForm.getFromDate());
		Date toDate = DATE_FORMAT.parse(aForm.getToDate());
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(toDate);
		c.add(GregorianCalendar.DAY_OF_MONTH, 1);
		toDate = c.getTime();
		
		// Get list of selected admins
		String selectedAdmin = aForm.getUsername();
		
		// Get selected action
		int selectedAction = aForm.getUserActivityLogAction();
		
		return userActivityLogDao.getUserActivityEntries(visibleAdmins, selectedAdmin, selectedAction, fromDate, toDate, sortColumn, sortDirection, pageNumber, pageSize);
	}
	
	@Override
	public void writeUserActivityLog( Admin admin, String action, String description) {
		userActivityLogDao.writeUserActivityLog( admin, action, description);
	}

    @Override
    public void writeUserActivityLog(Admin admin, String action, String description, Logger callerLog) {
        try {
            this.writeUserActivityLog(admin,action,description);
        } catch (Exception e) {
            logger.error("Error writing ActivityLog: " + e.getMessage(), e);
            logger.info("Userlog: " + admin.getUsername() + " " + action + " " +  description);
        }
    }

    // ----------------------------------------------------------------------------------------------------------- Dependency Injection
	
	/** DAO for accessing user activity data. */
	private UserActivityLogDao userActivityLogDao;
	
	/**
	 * Set DAO for accessing user activity data.
	 * 
	 * @param dao DAO for accessing user activity data
	 */
	@Required
	public void setUserActivityLogDao( UserActivityLogDao dao) {
		this.userActivityLogDao = dao;
	}
}
