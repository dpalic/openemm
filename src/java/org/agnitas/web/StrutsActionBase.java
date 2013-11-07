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

package org.agnitas.web;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.agnitas.beans.Admin;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.forms.StrutsFormBase;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.web.struts.ActionSupport;


/**
 * Implementation of <strong>Action</strong> that validates a user logon.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.1 $ $Date: 2006/08/03 08:47:47 $
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
    protected static boolean allowed(String id, HttpServletRequest req) {
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
    
    /**
	 * sets the number of rows in the form if the form has not been initialized 
	 * @param req
	 * @param aForm
	 */
	public void setNumberOfRows(HttpServletRequest req, StrutsFormBase aForm) {
		if( aForm.getNumberofRows() == -1 ) {
			int numberofrows = AgnUtils.getAdmin(req).getPreferredListSize();
			if( numberofrows == 0 ) {
				aForm.setNumberofRows(StrutsFormBase.DEFAULT_NUMBER_OF_ROWS);
			}else {
				aForm.setNumberofRows(numberofrows);
			}
		}
	}
	
    /**
     * Initialize the list which keeps the current width of the columns, with a default value of '-1'
     * A JavaScript in the corresponding jsp will set the style.width of the column.
     * @param size number of columns
     * @return
     */
    protected List<String> getInitializedColumnWidthList(int size) {
		List<String> columnWidthList = new ArrayList<String>();
		for ( int i=0; i< size ; i++ ) {
			columnWidthList.add("-1");
		}
		return columnWidthList;
	}
    
    /**
     * Get the language which is used for the online help
     * @param req
     * @return
     */
    protected String getHelpLanguage(HttpServletRequest req) {
		String helplanguage = "en";
        String availableHelpLanguages = (String) getBean("onlinehelp.languages");
        
        if( availableHelpLanguages != null ) {
        	Admin admin = AgnUtils.getAdmin(req);
        	StringTokenizer tokenizer = new StringTokenizer(availableHelpLanguages,",");
        	while (tokenizer.hasMoreTokens() ) {
        		String token = tokenizer.nextToken();
        		if( token.trim().equalsIgnoreCase( admin.getAdminLang()) ) {
        			helplanguage = token.toLowerCase();
        			break;
        		}        		
        	}
        }
		return helplanguage;
	}
    
    protected String getSort(HttpServletRequest request, StrutsFormBase aForm) {
		String sort = request.getParameter("sort");  
		 if( sort == null ) {
			 sort = aForm.getSort();			 
		 } else {
			 aForm.setSort(sort);
		 }
		return sort;
	}
    

}
