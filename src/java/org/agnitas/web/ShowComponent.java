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

import org.springframework.context.*;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.*;
import java.net.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.mail.*;
import javax.mail.internet.*;

public class ShowComponent extends HttpServlet {
    
    /**
     * Gets mailing components
     */
    public void service(HttpServletRequest req, HttpServletResponse res)
    throws IOException, ServletException {
        
        ServletOutputStream out=null;
        long len=0;
        int compId=0;
        
        if((req.getSession().getAttribute("emm.admin"))==null) {
            return;
        }
        
        try {
            compId=Integer.parseInt(req.getParameter("compID"));
        } catch (Exception e) {
            return;
        }
        
        if(compId==0) {
            return;
        }
        
        MailingComponentDao mDao=(MailingComponentDao)WebApplicationContextUtils.getWebApplicationContext(this.getServletContext()).getBean("MailingComponentDao");
        
        MailingComponent comp=mDao.getMailingComponent(compId, AgnUtils.getCompanyID(req));
        
        if(comp!=null) {
            
            switch(comp.getType()) {
                case MailingComponent.TYPE_IMAGE:
                case MailingComponent.TYPE_HOSTED_IMAGE:
                    res.setContentType(comp.getMimeType());
                    out=res.getOutputStream();
                    out.write(comp.getBinaryBlock());
                    out.flush();
                    out.close();
                    break;
                    
                case MailingComponent.TYPE_ATTACHMENT:
                    res.setHeader("Content-Disposition", "attachment; filename=" + comp.getComponentName() + ";");
                    out=res.getOutputStream();
                    len=comp.getBinaryBlock().length;
                    res.setContentLength((int)len);
                    out.write(comp.getBinaryBlock());
                    out.flush();
                    out.close();
                    break;
            }
        }
    }
}
