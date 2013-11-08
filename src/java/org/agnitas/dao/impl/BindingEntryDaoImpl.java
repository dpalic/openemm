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
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/

package org.agnitas.dao.impl;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.agnitas.target.Target;
import org.agnitas.beans.BindingEntry;
import org.agnitas.dao.BindingEntryDao;
import org.agnitas.util.AgnUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author nse
 */
public class BindingEntryDaoImpl implements BindingEntryDao {

	public BindingEntry get(int recipientID, int companyID,
				int mailinglistID, int mediaType) {
		JdbcTemplate jdbc=new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
		BindingEntry entry = null;
		String sql = "select user_type, user_status, " + AgnUtils.changeDateName() + ", exit_mailing_id, user_remark from customer_" + companyID + "_binding_tbl where customer_id=? and mailinglist_id=? and mediatype=?";
		try {
			List list = jdbc.queryForList(sql, new Object[] {
				new Integer(recipientID),
				new Integer(mailinglistID),
				new Integer(mediaType) });

			if (list.size() > 0) {
				Map map = (Map) list.get(0);

				entry=(BindingEntry) applicationContext.getBean("BindingEntry");
				entry.setCustomerID(recipientID);
				entry.setMailinglistID(mailinglistID);
				entry.setMediaType(mediaType);
				entry.setUserType((String) map.get("user_type"));
				entry.setUserStatus(((Number) map.get("user_status")).intValue());
				entry.setChangeDate((java.util.Date) map.get(AgnUtils.changeDateName()));
				if(map.get("exit_mailing_id") != null) {
					entry.setExitMailingID(((Number) map.get("exit_mailing_id")).intValue());
				} else {
					entry.setExitMailingID(0);
				}
				entry.setUserRemark((String) map.get("user_remark"));
			}
		} catch (Exception e) {
			AgnUtils.logger().error("sql: " + e.getMessage());
			AgnUtils.sendExceptionMail("SQL: "+sql, e);
			System.err.println(e);
			System.err.println(AgnUtils.getStackTrace(e));
		}
		return entry;
	}

    public void save(int companyID, BindingEntry entry) {
        JdbcTemplate jdbc = AgnUtils.getJdbcTemplate(this.applicationContext);
        String currentTimestamp = AgnUtils.getSQLCurrentTimestamp();

        String sql = "select * from customer_"
                + companyID
                + "_binding_tbl where customer_id=? and mailinglist_id=? and mediatype=?";
        try {
            List list = jdbc.queryForList(sql, new Object[] {
                    new Integer(entry.getCustomerID()),
                    new Integer(entry.getMailinglistID()),
                    new Integer(entry.getMediaType()) });
            if (list.size() > 0) {
                sql = "update customer_"
                        + companyID
                        + "_binding_tbl set user_type=?, user_status=?, " + AgnUtils.changeDateName() + "=current_timestamp, exit_mailing_id=?, user_remark=? where customer_id=? and mailinglist_id=? and mediatype=?";
                jdbc.update(sql, new Object[] { entry.getUserType(),
                        new Integer(entry.getUserStatus()),
                        new Integer(entry.getExitMailingID()),
                        entry.getUserRemark(),
                        new Integer(entry.getCustomerID()),
                        new Integer(entry.getMailinglistID()),
                        new Integer(entry.getMediaType())});
            } else {
                sql = "insert into customer_"
                        + companyID
                        + "_binding_tbl (mailinglist_id, customer_id, user_type, user_status, " + AgnUtils.changeDateName() + ", user_remark, creation_date, exit_mailing_id, mediatype) VALUES (?, ?, ?, ?, "
                        + currentTimestamp + ", ?, " + currentTimestamp
                        + ", ?, ?)";
                jdbc.update(sql, new Object[] {
                        new Integer(entry.getMailinglistID()),
                        new Integer(entry.getCustomerID()),
                        entry.getUserType(),
                        new Integer(entry.getUserStatus()),
                        entry.getUserRemark(),
                        new Integer(entry.getExitMailingID()),
                        new Integer(entry.getMediaType()) });
            }
        } catch (Exception e) {
        	AgnUtils.sendExceptionMail("sql:" + sql, e);
            AgnUtils.logger().error("sql: " + e.getMessage());
        }
    }

	/**
	 * Updates this Binding in the Database
	 *
	 * @return True: Sucess
	 * False: Failure
	 * @param companyID The company ID of the Binding
	 */
	public boolean updateBinding(BindingEntry entry, int companyID) {
		String dbTimeExpression = AgnUtils.isOracleDB()?"sysdate":"now()";
		String sql="UPDATE customer_" + companyID + "_binding_tbl SET user_status=?, user_remark=?, exit_mailing_id=?, user_type=?, mediatype=?, " + AgnUtils.changeDateName() + "=" + dbTimeExpression + "  WHERE customer_id=? AND mailinglist_id=? AND mediatype=?";
		Object[] param=new Object[] {
			new Integer(entry.getUserStatus()),
			entry.getUserRemark(),
			new Integer(entry.getExitMailingID()),
			entry.getUserType(),
			new Integer(entry.getMediaType()),
			/* Where parameters */
			new Integer(entry.getCustomerID()),
			new Integer(entry.getMailinglistID()),
			new Integer(entry.getMediaType())
		};
		JdbcTemplate jdbc=new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));

		try {
			if(jdbc.update(sql, param) < 1) {
				return false;
			}
		} catch (Exception e) {
			AgnUtils.sendExceptionMail("sql:" + sql, e);
			AgnUtils.logger().error("updateBindingInDB: " + e.getMessage());
			return false;
		}
		return true;
	}

	public boolean insertNewBinding(BindingEntry entry, int companyID) {
		String currentTimestamp=AgnUtils.getSQLCurrentTimestampName();
		String sql="INSERT INTO customer_" + companyID + "_binding_tbl "
			+"(mailinglist_id, customer_id, user_type, user_status, user_remark, creation_date, exit_mailing_id, mediatype) "
			+"VALUES (?, ?, ?, ?, ?, "+currentTimestamp+", ?, ?)";
		Object[] params=new Object[] {
			new Integer(entry.getMailinglistID()),
			new Integer(entry.getCustomerID()),
			entry.getUserType(),
			new Integer(entry.getUserStatus()),
			entry.getUserRemark(),
			new Integer(entry.getExitMailingID()),
			new Integer(entry.getMediaType())
		};
		JdbcTemplate jdbc=new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));

		try {
			jdbc.update(sql, params);
		} catch (Exception e) {
			AgnUtils.sendExceptionMail("sql:" + sql, e);
			AgnUtils.logger().error("insertNewBindingInDB: " + e.getMessage());
			return false;
		}
		return true;
	}

	public boolean updateStatus(BindingEntry entry, int companyID) {
		String currentTimestamp=AgnUtils.getSQLCurrentTimestampName();
		JdbcTemplate jdbc=new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
		String sqlUpdateStatus="UPDATE customer_" + companyID + "_binding_tbl SET user_status=?, exit_mailing_id=?, user_remark=?, " + AgnUtils.changeDateName() + "=" + currentTimestamp + " WHERE customer_id=? AND mailinglist_id=? AND mediatype=?";
		Object[] params=new Object[] {
			new Integer(entry.getUserStatus()),
			new Integer(entry.getExitMailingID()), entry.getUserRemark(),
			new Integer(entry.getCustomerID()), new Integer(entry.getMailinglistID()),
			new Integer(entry.getMediaType())
		};

		try {
			if(jdbc.update(sqlUpdateStatus, params) < 1) {
				return false;
			}
		} catch (Exception e) {
			AgnUtils.sendExceptionMail("sql:" + sqlUpdateStatus, e);
			AgnUtils.logger().error("updateStatusInDB: "+e.getMessage());
			return false;
		}
		return true;
	}

	public boolean optOutEmailAdr(String email, int CompanyID) {
		String operator = " = ";

		if((email.indexOf('%')!=-1) || (email.indexOf('_')!=-1)) {
			operator = " LIKE ";
		}
		String sql="UPDATE customer_"+CompanyID+"_binding_tbl SET user_status=? WHERE customer_id IN (SELECT customer_id FROM customer_"+ CompanyID + "_tbl WHERE lower(email)"+operator+"?)";
		Object[] params=new Object[] {
			new Integer(BindingEntry.USER_STATUS_ADMINOUT), email
		};
		JdbcTemplate jdbc=new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));

		try {
			if(jdbc.update(sql, params) == 1) {
				return true;
			}
		} catch (Exception e) {
			AgnUtils.sendExceptionMail("sql:" + sql + ", " + BindingEntry.USER_STATUS_ADMINOUT + ", " + email, e);
			AgnUtils.logger().error("optOutEmailAdr: " + e.getMessage());
		}
		return false;
	}

	public boolean addTargetsToMailinglist(int companyID, int mailinglistID,
			Target target) {
		String timestamp = AgnUtils.getSQLCurrentTimestampName();
		String sql = "insert into customer_"
				+ companyID
				+ "_binding_tbl (customer_id, mailinglist_id, user_type, user_status, user_remark, "
				+ AgnUtils.changeDateName()
				+ ", exit_mailing_id, creation_date, mediatype) (select cust.customer_id, "
				+ mailinglistID + ", 'W', 1, " + "'From Target "
				+ target.getId() + "', " + timestamp + ", 0, " + timestamp
				+ ", 0 " + " from  customer_" + companyID + "_tbl cust where  "
				+ target.getTargetSQL() + ")";
		JdbcTemplate jdbc = new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));

		try {
			jdbc.execute(sql);
		} catch (Exception e3) {
			AgnUtils.sendExceptionMail("sql:" + sql, e3);
			AgnUtils.logger().error("insertIntoDB: " + sql);
			AgnUtils.logger().error("insertIntoDB: " + e3.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Holds value of property applicationContext.
	 */
	protected ApplicationContext applicationContext;

	/**
	 * Setter for property applicationContext.
	 *
	 * @param applicationContext
	 *            New value of property applicationContext.
	 */
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

}
