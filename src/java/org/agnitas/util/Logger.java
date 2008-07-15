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
package org.agnitas.util;

import javax.servlet.http.*;
import javax.sql.DataSource;

import org.agnitas.util.AgnUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;


/**
 * Helper class to do log for EMM frondend. It is wrapper class for log4j to add
 * costomized logging.
 * @author Zuochang Zheng
 * @version $Revision: 1.2 $ $Date: 2006/07/21 06:45:40 $
 */

public class Logger {
    
    public static final int CAT_DEBUG=0;
    public static final int CAT_INFO=10;
    public static final int CAT_NORMAL=20;
    public static final int CAT_ERROR=30;
    public static final int CAT_CRITICAL=40;
    
    public Logger() {
    }
    
    /**
     * Writes the specified message to a daily log file in text or xml format
     *
     * @param msg A <code>String</code> specifying the message to be written
     *  to the log file
     */
    public static void log(int category, String msg, HttpServletRequest req, ApplicationContext applicationContext) {
        JdbcTemplate jdbc=new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
        String sql= "INSERT INTO EMM_LOG_TBL (LOG_ID, COMPANY_ID, ADMIN_ID, CATEGORY, IP_ADR, MESSAGE) VALUES (EMM_LOG_TBL_SEQ.NEXTVAL, ?, ?, ?, ?, ?)";
        int	adminID=0;
        if (AgnUtils.isMySQLDB()) {
        	sql= "insert into log_tbl ( company_id, admin_id, category, ip_adr, message) values ( ?, ?, ?, ?, ?)";
        }
    	try {

		if(AgnUtils.getAdmin(req) != null) {
			adminID=AgnUtils.getAdmin(req).getAdminID();
		}
    		jdbc.update(sql, new Object[] {new Integer(AgnUtils.getCompanyID(req)), new Integer(adminID), new Integer(category), req.getRemoteAddr(), msg});
    	} catch(Exception e) {
    		AgnUtils.sendExceptionMail("sql: " + sql + ", " + AgnUtils.getCompanyID(req) + ", " + adminID + ", " + category + ", " + req.getRemoteAddr() + ", " + msg, e);
    		System.out.println("Problem in logging: "+e);
    		AgnUtils.logger().debug("Error:"+e);
    		AgnUtils.logger().debug(AgnUtils.getStackTrace(e));
    	}
    }
}
