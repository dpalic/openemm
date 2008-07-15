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

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.agnitas.beans.Company;
import org.agnitas.dao.CompanyDao;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.TimeoutLRUMap;
import org.agnitas.util.UID;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class OnePixelCount extends HttpServlet {
    private static final long serialVersionUID = -3837933074485365451L;
	byte[] onePixelGif={71 ,73 ,70 ,56 ,57 ,97 ,1 ,0 ,1 ,0 ,-128 ,-1 ,0 ,-64 ,-64 ,-64 ,0 ,0 ,0 ,33 ,-7 ,4 ,1 ,0 ,0 ,0 ,0 ,44 ,0 ,0 ,0 ,0 ,1 ,0 ,1 ,0 ,0 ,2 ,2 ,68 ,1 ,0 ,59};
    
    /**
     * Sends image to browser.
     */
    public void service(HttpServletRequest req, HttpServletResponse res)
    throws IOException, ServletException {
        
        ApplicationContext con=WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
        TimeoutLRUMap companyCache=(TimeoutLRUMap)con.getBean("companyCache");
        String param=null;
        CompanyDao cDao=(CompanyDao)con.getBean("CompanyDao");
        Company aCompany=null;

        // send gif to Browser.
        res.setContentType("image/gif");
        OutputStream out=res.getOutputStream();
        out.write(onePixelGif);
        out.close();
        
        param=req.getParameter("uid");
        if(param == null) {
            AgnUtils.logger().error("service: no uid set");
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
  
            String sqlUpdateLog="update onepixel_log_tbl set open_count=open_count+1 where company_id=? and customer_id=? and mailing_id=?";
            
            JdbcTemplate tmpl=new JdbcTemplate((DataSource)con.getBean("dataSource"));
            
            if(tmpl.update(sqlUpdateLog, new Object[] {new Integer((int)uid.getCompanyID()), new Integer((int)uid.getCustomerID()), new Integer((int)uid.getMailingID()) })==0) {
                // insert
                String sqlInsertLog="insert into onepixel_log_tbl (company_id, customer_id, mailing_id, ip_adr) values (?, ?, ?, ?)";
                tmpl.update(sqlInsertLog, new Object[] {new Integer((int)uid.getCompanyID()), new Integer((int)uid.getCustomerID()), new Integer((int)uid.getMailingID()), req.getRemoteAddr() });
            }
            
            AgnUtils.logger().info("Onepixel: cust: "+uid.getCustomerID()+" mi: "+uid.getMailingID()+" ci: "+uid.getCompanyID());
        } catch (Exception e) {
            AgnUtils.logger().error(e.getMessage());
        }
    }
}
