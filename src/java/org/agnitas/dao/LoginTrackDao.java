/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2014 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/

package org.agnitas.dao;

import java.util.Date;
import java.util.List;

import org.agnitas.beans.FailedLoginData;
import org.agnitas.emm.core.logintracking.LoginData;

/**
 * Interface for accessing login tracking information.
 * The records contains IP-address, used user name, login status (success, failed, etc.) and a time stamp.
 * 
 * @author Markus Dorschmidt
 * 
 */
public interface LoginTrackDao {
	/**
	 * Returns informations abound failed logins for a given IP address.
	 * @param ipAddress IP address to retrieve login informations
	 * @return login informations
	 */
	public FailedLoginData getFailedLoginData(String ipAddress);
	
	/**
	 * Track successful login. All data about the login (IP address, user name, time stamp, etc.)
	 * are recorded.
	 * 
	 * @param ipAddress IP address of host
	 * @param username used user name
	 */
	public void trackSuccessfulLogin(String ipAddress, String username);
	
	/**
	 * Track failed login. All data about the login (IP address, user name, time stamp, etc.)
	 * are recorded.
	 * 
	 * @param ipAddress IP address of host
	 * @param username used user name
	 */
	public void trackFailedLogin(String ipAddress, String username);
	
	/**
	 * Track successful during lock period login. All data about the login (IP address, user name, time stamp, etc.)
	 * are recorded.
	 * 
	 * @param ipAddress IP address of host
	 * @param username used user name
	 */
	public void trackLoginDuringBlock(String ipAddress, String username);
	
	/**
	 * Deletes old login records. A record is supposed to be old, if its time stamp exceeds the given retention time.
	 * To prevent locking the persistence system behind, a maximum number of records can be specified to be deleted 
	 * with one method call.
	 *  
	 * @param retentionTime number of days a records must be stored
	 * @param maxRecords maximum number of records to be deleted at one method call
	 * @return number of records deleted
	 */
	public int deleteOldRecords(int retentionTime, int maxRecords);

	/**
	 * Returns the last successful login. 
	 * Returning {@code null} means that there is no successful login.
	 * If the record for the current login has already been written, set {@code skipLastSuccess} to true. Otherwise
	 * you well get the record for the current login.
	 * 
	 * @param username user name
	 * @param skipLastSuccess set to true, if record for current login has already been written
	 * 
	 * @return login track data of last successful login
	 */
	public LoginData getLastSuccessfulLogin( String username, boolean skipLastSuccess);

	/**
	 * Counts the failed login attempts since a given time stamp.
	 * If {@code since} is null, all failed logins are counted.
	 * 
	 * @param username user name
	 * @param since count fails since this time
	 * 
	 * @return number of failed logins
	 */
	public int countFailedLogins(String username, Date since);

	/**
	 * Returns all login attempts for given user since given time.
	 * 
	 * @param username user name
	 * @param since return all login attempts since this time
	 * 
	 * @return list of login attempts ordered descending by time stamp
	 */
	public List<LoginData> getLoginAttemptsSince(String username, Date since);
}
