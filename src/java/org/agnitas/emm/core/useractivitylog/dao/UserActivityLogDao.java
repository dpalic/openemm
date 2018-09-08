package org.agnitas.emm.core.useractivitylog.dao;

import java.util.Date;
import java.util.List;

import org.agnitas.beans.Admin;
import org.agnitas.beans.AdminEntry;
import org.agnitas.beans.impl.PaginatedListImpl;
import org.agnitas.emm.core.useractivitylog.UserActivityEntry;

/**
 * Interface for accessing user activity log.
 * 
 * @author md
 *
 */
public interface UserActivityLogDao {
	public void writeUserActivityLog( Admin admin, String action, String description);

	public PaginatedListImpl<UserActivityEntry> getUserActivityEntries(List<AdminEntry> visibleAdmins, String selectedAdmin, int selectedAction, Date from, Date to, String sortColumn, String sortDirection, int pageNumber, int pageSize) throws Exception;
}
