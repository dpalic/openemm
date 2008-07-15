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
 * @version $Revision: 3.0 $ $Date: 2006/07/14 09:57:11 $
 */

public class StrutsActionBase extends ActionSupport {
    
    public static final int ACTION_LIST = 1;

    public static final int ACTION_VIEW = 2;

    public static final int ACTION_SAVE = 3;

    public static final int ACTION_NEW = 4;

    public static final int ACTION_DELETE = 5;

    public static final int ACTION_CONFIRM_DELETE = 6;

    public static final int ACTION_LAST = 6;
    
    protected DataSource agnDBPool=null;
    protected SessionFactory sf=null;
    
    public Object getBean(String name) {
        return getWebApplicationContext().getBean(name);
    }
    
    protected HibernateTemplate getHibernateTemplate() {
        SessionFactory factory=null;
        
        factory=(SessionFactory) getBean("sessionFactory");
        
        return new HibernateTemplate(factory);
    }
    
    protected JdbcTemplate getJdbcTemplate() {
        
        DataSource aDS=(DataSource) getBean("dataSource");
       
        return new JdbcTemplate(aDS);
    }
    
    /**
     * Getter for property hibernateSession.
     *
     * @return Value of property hibernateSession.
     */
    protected Session getHibernateSession(HttpServletRequest req) {
        Session aSession=null;

        if(sf==null) {
            sf=AgnUtils.retrieveSessionFactory(this.getServlet().getServletContext());
        }
        aSession=sf.openSession();
        aSession.enableFilter("companyFilter").setParameter("companyFilterID", new Integer(this.getCompanyID(req)));
        return aSession;
    }
 
    /**
     * Closes the hibernateSession.
     */
    protected void closeHibernateSession(Session aSession) {
        Connection dbConn=null;

        dbConn=aSession.close();
        try {
            dbConn.close();
        } catch(SQLException e) {
            AgnUtils.logger().error(e);
        }
    }

    /**
     * Getter for property companyID.
     * 
     * @return Value of property companyID.
     * @param req 
     */
    public int getCompanyID(HttpServletRequest req) {
        
        int companyID=0;
        
        try {
            companyID=((Admin)req.getSession().getAttribute("emm.admin")).getCompany().getId();
        } catch (Exception e) {
            AgnUtils.logger().error("no companyID");
            companyID=0;
        }
        
        return companyID;
    }
    
    /**
     * Getter for property defaultMediaType.
     * 
     * @return Value of property defaultMediaType.
     * @param req 
     */
    public int getDefaultMediaType(HttpServletRequest req) {
        
        int mtype=0;
        
        try {
            mtype=((Integer)req.getSession().getAttribute("agnitas.defaultMediaType")).intValue();
        } catch (Exception e) {
            AgnUtils.logger().error("no default mediatype");
            mtype=0;
        }
        
        return mtype;
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
     * Checks the permission.
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
     * Getter for property message.
     *
     * @return Value of property message.
     */
    public String getMessage(String key, HttpServletRequest req) {
        return this.getMessageSourceAccessor().getMessage(key, (Locale)req.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY));
    }
    
    /**
     * Constructor
     */
    public StrutsActionBase() {
        super();
        //Protocol.registerProtocol("https", new Protocol("https", new EasySSLProtocolSocketFactory(), 443));
    }
    
}
