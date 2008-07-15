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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.beans.Company;
import org.agnitas.beans.TrackableLink;
import org.agnitas.dao.TrackableLinkDao;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.TimeoutLRUMap;
import org.agnitas.util.UID;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class rdir extends HttpServlet {

    private static final long serialVersionUID = -133097955106781586L;
    protected TimeoutLRUMap urlCache=new TimeoutLRUMap(AgnUtils.getDefaultIntValue("rdir.keys.maxCache"), AgnUtils.getDefaultIntValue("rdir.keys.maxCacheTimeMillis"));

    /**
     * Service-Method, gets called everytime a User calls the servlet
     */
    public void service(HttpServletRequest req, HttpServletResponse res)
    throws IOException, ServletException {

        ApplicationContext con=WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
        String param=null;
        TrackableLink aLink=null;
        TrackableLinkDao tDao=(TrackableLinkDao)con.getBean("TrackableLinkDao");
        Company aCompany=null;
        String fullUrl=null;

        try {
            // validate uid
            UID uid=(UID)con.getBean("UID");

            param=req.getParameter("uid");
            if(param != null) {
            	uid=(UID)con.getBean("UID");
            	uid.parseUID(param);
            } else {
            	AgnUtils.logger().error("service: uid missing");
            }
            if(uid.getCompanyID()==0) {
                return;
            }

            aCompany = AgnUtils.getCompanyCache((int)uid.getCompanyID(), con);
            if(aCompany==null) {
                return;
            }

/* TODO: check validateUID -> didn't recognize valid UIDs (maybe unittest) */
            if(uid.validateUID(aCompany.getSecret()) == false) {
                AgnUtils.logger().warn("uid invalid: "+param);
                return;
            }

            aLink=(TrackableLink) urlCache.get(new Long(uid.getURLID()));
            if(aLink == null || aLink.getCompanyID() != (int)uid.getCompanyID()) {
                // get link and do actions
                aLink=tDao.getTrackableLink((int)uid.getURLID(), (int)uid.getCompanyID());
                if(aLink != null) {
                    urlCache.put(new Long(uid.getURLID()), aLink);
                }
            }

            // link is beeing personalized, replaces AGNUID 
            if((fullUrl=aLink.personalizeLink((int)uid.getCustomerID(), param, con))==null) {
                AgnUtils.logger().error("service: could not personalize link");
                return;
            }

            // send redirect 
            res.sendRedirect(fullUrl);

            // log click in db
            if(aLink.logClickInDB((int)uid.getCustomerID(), req.getRemoteAddr(), con)==false) {
            	return;
            }
            // execute configured actions
            aLink.performLinkAction(null, (int)uid.getCustomerID(), con);

        } catch (Exception e) {
            System.err.println("Exception: "+e);
            System.err.println(AgnUtils.getStackTrace(e));
        }
    }
}
