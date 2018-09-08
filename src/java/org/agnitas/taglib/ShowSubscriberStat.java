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

package org.agnitas.taglib;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.sql.DataSource;

import org.agnitas.beans.BindingEntry;
import org.agnitas.dao.TargetDao;
import org.agnitas.target.Target;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.EmmCalendar;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ShowSubscriberStat extends BodyBase {
	private static final transient Logger logger = Logger.getLogger(ShowSubscriberStat.class);

	private static final long serialVersionUID = -8314097414954251274L;
	
	private Map<String, Integer> allSubscribes;
	private Map<String, Integer> allOptouts;
	private Map<String, Integer> allBounces;

	private int maxSubscribe = 0;
	private int maxOptout = 0;
	private int maxBounce = 0;

	private int numMonth = 0;

	private EmmCalendar aCal;
	private SimpleDateFormat aFormatYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	private SimpleDateFormat aFormatYYYYMM = new SimpleDateFormat("yyyyMM");

	private int targetID = 0;

	/**
	 * Holds value of property mailinglistID.
	 */
	private int mailinglistID;

	/**
	 * Holds value of property month.
	 */
	private String month;

	/**
	 * Holds value of property mediaType.
	 */
	private String mediaType = "0";

	/**
	 * Reads statistics to recipients
	 */
	@Override
	public int doStartTag() throws JspTagException, JspException {
		ApplicationContext aContext = WebApplicationContextUtils.getWebApplicationContext(pageContext.getServletContext());
		String thisMonth;
		Target aTarget = null;
		String dateFull = "";
		String dateMonth = "";

		super.doStartTag();

		aCal = new EmmCalendar(java.util.TimeZone.getDefault());
		TimeZone zone = TimeZone.getTimeZone(AgnUtils.getAdmin(pageContext).getAdminTimezone());
		double zoneOffset = aCal.getTimeZoneOffsetHours(zone);

		aCal.changeTimeWithZone(zone);
		try {
			aCal.setTime(aFormatYYYYMM.parse(month));
		} catch (Exception e) {
			aCal.set(Calendar.DAY_OF_MONTH, 1); // set to first day in month!
		}

		if (zoneOffset != 0.0) {
			dateFull = "date_add(bind." + AgnUtils.changeDateName() + " INTERVAL " + zoneOffset + " HOURS)";
		} else {
			dateFull = "bind." + AgnUtils.changeDateName();
		}

		dateMonth = AgnUtils.sqlDateString(dateFull, "yyyymm");
		dateFull = AgnUtils.sqlDateString(dateFull, "yyyymmdd");

		thisMonth = aFormatYYYYMM.format(aCal.getTime());
		numMonth = aCal.get(Calendar.MONTH);

		allBounces = new Hashtable<String, Integer>();
		allOptouts = new Hashtable<String, Integer>();
		allSubscribes = new Hashtable<String, Integer>();

		if (targetID != 0) {
			TargetDao targetDao = (TargetDao) aContext.getBean("TargetDao");

			aTarget = targetDao.getTarget(targetID, getCompanyID());
		}

		StringBuffer allQuery = new StringBuffer("SELECT " + dateFull);

		allQuery.append(" AS change_date, bind.user_status, COUNT(bind.customer_id) AS count_customer_id FROM customer_" + getCompanyID() + "_binding_tbl bind");
		
		if (aTarget != null) {
			allQuery.append(", customer_" + getCompanyID() + "_tbl cust");
		}

		if (mediaType == null) {
			// default mediatype = 0 (Email)
			allQuery.append(" WHERE bind.mediatype = 0");
		} else {
			allQuery.append(" WHERE bind.mediatype = " + mediaType);
		}

		allQuery.append(" AND ");
		allQuery.append(dateMonth + "='" + thisMonth + "'");
		
		if (mailinglistID != 0) {
			allQuery.append(" AND bind.mailinglist_id=" + mailinglistID);
		}
		
		if (aTarget != null) {
			allQuery.append(" AND ((" + aTarget.getTargetSQL() + ") AND cust.customer_id=bind.customer_id)");
		}
		
		allQuery.append(" GROUP BY " + dateFull + ", bind.user_status");

		if (aTarget != null) {
			pageContext.setAttribute("target_name", aTarget.getTargetName());
		} else {
			pageContext.setAttribute("target_name", "");
		}

		DataSource ds = (DataSource) aContext.getBean("dataSource");
		SimpleJdbcTemplate simpleJdbcTemplate = new SimpleJdbcTemplate(ds);
		try {
			List<Map<String, Object>> result = simpleJdbcTemplate.queryForList(allQuery.toString());
			for (Map<String, Object> row : result) {
				String dateString = (String) row.get("change_date");
				int tmpUserStatus = ((Number) row.get("user_status")).intValue();
				int tmpValue = ((Number) row.get("count_customer_id")).intValue();
				switch (tmpUserStatus) {
					case BindingEntry.USER_STATUS_ACTIVE:
						allSubscribes.put(dateString, new Integer(tmpValue));
						if (maxSubscribe < tmpValue) {
							maxSubscribe = tmpValue;
						}
						break;
	
					case BindingEntry.USER_STATUS_BOUNCED:
						allBounces.put(dateString, new Integer(tmpValue));
						if (maxBounce < tmpValue) {
							maxBounce = tmpValue;
						}
						break;
	
					case BindingEntry.USER_STATUS_OPTOUT:
					case BindingEntry.USER_STATUS_ADMINOUT:
						allOptouts.put(dateString, new Integer(tmpValue));
						if (maxOptout < tmpValue) {
							maxOptout = tmpValue;
						}
						break;
					}
			}
		} catch (Exception e) {
			logger.error("doStartTag (sql: " + allQuery + ")", e);
			AgnUtils.sendExceptionMail("sql: " + allQuery.toString(), e);
			return SKIP_BODY;
		}

		pageContext.setAttribute("max_subscribes", new Integer(maxSubscribe));
		pageContext.setAttribute("max_bounces", new Integer(maxBounce));
		pageContext.setAttribute("max_optouts", new Integer(maxOptout));

		return doAfterBody();
	}

	/**
	 * Sets attribute for the pagecontext.
	 */
	@Override
	public int doAfterBody() throws JspTagException, JspException {
		if (numMonth != aCal.get(Calendar.MONTH)) {
			return SKIP_BODY;
		}
		String dayKey = null;
		java.util.Date thisDay = null;
		Integer numBounce = new Integer(0);
		Integer numSubscribe = new Integer(0);
		Integer numOptout = new Integer(0);

		thisDay = aCal.getTime();
		dayKey = aFormatYYYYMMDD.format(thisDay);

		if (allSubscribes.containsKey(dayKey)) {
			numSubscribe = allSubscribes.get(dayKey);
		}
		if (allBounces.containsKey(dayKey)) {
			numBounce = allBounces.get(dayKey);
		}
		if (allOptouts.containsKey(dayKey)) {
			numOptout = allOptouts.get(dayKey);
		}

		pageContext.setAttribute("today", thisDay);
		pageContext.setAttribute("subscribes", numSubscribe);
		pageContext.setAttribute("bounces", numBounce);
		pageContext.setAttribute("optouts", numOptout);

		aCal.add(Calendar.DATE, 1);
		return EVAL_BODY_BUFFERED;
	}

	/**
	 * Getter for property targetID.
	 * 
	 * @return Value of property targetID.
	 */
	public int getTargetID() {
		return targetID;
	}

	/**
	 * Setter for property targetID.
	 * 
	 * @param targetID
	 *            New value of property targetID.
	 */
	public void setTargetID(int targetID) {
		this.targetID = targetID;
	}

	/**
	 * Getter for property mailinglistID.
	 * 
	 * @return Value of property mailinglistID.
	 */
	public int getMailinglistID() {
		return mailinglistID;
	}

	/**
	 * Setter for property mailinglistID.
	 * 
	 * @param mailinglistID
	 *            New value of property mailinglistID.
	 */
	public void setMailinglistID(int mailinglistID) {
		this.mailinglistID = mailinglistID;
	}

	/**
	 * Getter for property month.
	 * 
	 * @return Value of property month.
	 */
	public String getMonth() {
		return month;
	}

	/**
	 * Setter for property month.
	 * 
	 * @param month
	 *            New value of property month.
	 */
	public void setMonth(String month) {
		this.month = month;
	}

	/**
	 * Getter for property mediaType.
	 * 
	 * @return Value of property mediaType.
	 */
	public String getMediaType() {
		return mediaType;
	}

	/**
	 * Setter for property mediaType.
	 * 
	 * @param mediaType
	 *            New value of property mediaType.
	 */
	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}
}
