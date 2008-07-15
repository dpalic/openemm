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

package org.agnitas.web;

import org.agnitas.util.*;
import org.agnitas.beans.*;
import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.struts.action.*;
import javax.sql.*;
import org.apache.commons.httpclient.protocol.Protocol;
import org.hibernate.*;
import org.springframework.web.struts.*;
import org.springframework.orm.hibernate3.*;
import org.springframework.jdbc.core.*;


/**
 * Implementation of <strong>Action</strong> that validates a user logon.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 2.2 $ $Date: 2006/07/11 11:05:34 $
 */

public class StrutsDispatchActionBase extends DispatchActionSupport {
   
    protected Object getBean(String name) { 
        return getWebApplicationContext().getBean(name);
    }

    protected HibernateTemplate getHibernateTemplate() {
        SessionFactory factory=null;
        
        factory=(SessionFactory)this.getWebApplicationContext().getBean("sessionFactory");
        
        return new HibernateTemplate(factory);
    }
    
    protected JdbcTemplate getJdbcTemplate() {
        DataSource aDS=(DataSource)this.getWebApplicationContext().getBean("dataSource");
        
        return new JdbcTemplate(aDS);
    }
    
    /**
     * Getter for property companyID.
     * 
     * @return Value of property companyID.
     * @param req 
     */
    public int getCompanyID(HttpServletRequest req) {
        
        return AgnUtils.getCompanyID(req);
    }
        
    /**
     * Checks logon.
     */
    public boolean checkLogon(HttpServletRequest req) {
        // Is there a valid user logged on?
        boolean valid = false;
        HttpSession session = req.getSession();
        if ((session != null) && (session.getAttribute("emm.admin") != null)) {
            valid = true;
        }
        
        return valid;
    }
    
    /**
     * checks permission.
     */
    protected boolean allowed(String id, HttpServletRequest req) {
        Admin aAdmin=null;
        HttpSession session=req.getSession();
        
        if(session==null) {
            return false; //Nothing allowed if there is no permission set in Session
        }
        
        aAdmin=(Admin)session.getAttribute("emm.admin");
        
        if(aAdmin==null) {
            return false; //Nothing allowed if there is no permission set in Session
        }
        
        return aAdmin.permissionAllowed(id);
    }
    
    /**
     * Constructor
     */
    public StrutsDispatchActionBase() {
        super();
    }  
}
