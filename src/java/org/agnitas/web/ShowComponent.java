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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.beans.MailingComponent;
import org.agnitas.dao.MailingComponentDao;
import org.agnitas.util.AgnUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ShowComponent extends HttpServlet {
    
    private static final long serialVersionUID = 6640509099616089054L;

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
