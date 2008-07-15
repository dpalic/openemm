/*********************************************************************************
 * The contents of this file are subject to the OpenEMM Public License Version 1.1
 * ("License"); You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.agnitas.org/openemm.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is OpenEMM.
 * The Initial Developer of the Original Code is AGNITAS AG. Portions created by
 * AGNITAS AG are Copyright (C) 2006 AGNITAS AG. All Rights Reserved.
 *
 * All copies of the Covered Code must include on each user interface screen,
 * visible to all users at all times
 *    (a) the OpenEMM logo in the upper left corner and
 *    (b) the OpenEMM copyright notice at the very bottom center
 * See full license, exhibit B for requirements.
 ********************************************************************************/

package org.agnitas.taglib;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.sql.*;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.context.*;
import org.springframework.jdbc.core.*;
import java.util.*;
import org.agnitas.util.*;
import org.agnitas.beans.*;

/**
 *
 * @author Martin Helff, Andreas Rehak
 */
public class BlacklistAction extends BodyBase {
    
    /**
     * Adds or removes a data set to blacklist.
     */
    public int doStartTag() throws JspTagException {
        ApplicationContext aContext=WebApplicationContextUtils.getWebApplicationContext(this.pageContext.getServletContext());
        JdbcTemplate aTemplate=new JdbcTemplate((DataSource)aContext.getBean("dataSource")); 
        ServletRequest req=null;

        req=pageContext.getRequest();
        if(req.getParameter("newemail")!=null && req.getParameter("newemail").length()>0) {
                String sqlInsert="INSERT INTO cust_ban_tbl (company_id, email) VALUES (" + this.getCompanyID() + ", '" +
                SafeString.getSQLSafeString(req.getParameter("newemail").toLowerCase().trim()) + "')";
                
                aTemplate.update(sqlInsert);
        }
        
        if(req.getParameter("delete")!=null && req.getParameter("delete").length()>0) {
                String sqlDelete="DELETE FROM cust_ban_tbl WHERE company_id=" + this.getCompanyID() + " AND email='" +
                SafeString.getSQLSafeString(req.getParameter("delete").toLowerCase()) + "'";
                
                aTemplate.update(sqlDelete);
        }
        return SKIP_BODY;
    }
}
