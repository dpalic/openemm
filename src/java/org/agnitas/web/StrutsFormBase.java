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
import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.struts.action.*;
import org.apache.struts.util.*;
import javax.sql.*;
import org.apache.commons.beanutils.*;
import org.agnitas.beans.*;
import org.springframework.context.*;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Implementation of <strong>Action</strong> that validates a user logon.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 3.0 $ $Date: 2006/07/14 09:57:11 $
 */

public class StrutsFormBase extends org.apache.struts.action.ActionForm implements java.io.Serializable {
    
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
            AgnUtils.logger().error("getCompanyID: "+e.getMessage());
            companyID=0;
        }
        
        return companyID;
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
     * Checks permission.
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
     * Resets parameters.
     */
    public void reset(ActionMapping map, HttpServletRequest request) {
        String aCBox=null;
        String name=null;
        String value=null;
        
        Enumeration names=request.getParameterNames();
        while(names.hasMoreElements()) {
            name=(String)names.nextElement();
            if(name.startsWith("__STRUTS_CHECKBOX_") && name.length()>18) {
                aCBox=name.substring(18);
                try {
                    if((value=request.getParameter(name))!=null) {
                        BeanUtils.setProperty(this, aCBox, value);
                    }
                } catch (Exception e) {
                    AgnUtils.logger().error("reset: "+e.getMessage());
                }
            }
        }
    }
    
    /**
     * Getter for property webApplicationContext.
     * 
     * @return Value of property webApplicationContext.
     */
    public ApplicationContext getWebApplicationContext() {
        return WebApplicationContextUtils.getWebApplicationContext(this.getServlet().getServletContext());
    }
}
