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
import org.agnitas.dao.*;
import java.io.*;
import java.net.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.springframework.context.*;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class rdir extends HttpServlet {
    
    /**
     * Service-Method, gets called everytime a User calls the servlet
     */
    public void service(HttpServletRequest req, HttpServletResponse res)
    throws IOException, ServletException {
        
        ApplicationContext con=WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
        TimeoutLRUMap companyCache=(TimeoutLRUMap)con.getBean("companyCache");
        String param=null;
        TrackableLink aLink=null;
        TrackableLinkDao tDao=(TrackableLinkDao)con.getBean("TrackableLinkDao");
        CompanyDao cDao=(CompanyDao)con.getBean("CompanyDao");
        Company aCompany=null;
        String fullUrl=null;
        
        param=req.getParameter("uid");
        if(param == null) {
            AgnUtils.logger().error("service: uid missing");
            return;
        }
        
        try {
            // validate uid
            UID uid=(UID)con.getBean("UID");
            uid.parseUID(param);
            
            if(uid.getCompanyID()==0) {
                return;
            }
            
            aCompany=(Company)companyCache.get(Long.toString(uid.getCompanyID()));
            if(aCompany==null) {
                aCompany=cDao.getCompany((int)uid.getCompanyID());
            }
            
            if(aCompany==null) {
                return;
            }
            
            if(uid.validateUID(aCompany.getSecret())==false) {
                AgnUtils.logger().warn("uid invalid: "+param);
                return;
            }
            
            // get link and do actions
            aLink=tDao.getTrackableLink((int)uid.getURLID(), (int)uid.getCompanyID());
            
            if((fullUrl=aLink.personalizeLink((int)uid.getCustomerID(), param, con))==null) {
                AgnUtils.logger().error("service: could not personalize link");
                return;
            }
            
            res.sendRedirect(fullUrl);
            
            if(aLink.logClickInDB((int)uid.getCustomerID(), req.getRemoteAddr(), con)==false) {
                return;
            }
            
            aLink.performLinkAction(null, (int)uid.getCustomerID(), con);
            
        } catch (Exception e) {
            AgnUtils.logger().error(e.getMessage());
        }
        return;
    }
}
