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
import org.agnitas.util.TimeoutLRUMap;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ShowImage extends HttpServlet {

    private static final long serialVersionUID = -595094416663851734L;
	private TimeoutLRUMap cacheMap=null;
    /**
     * Shows the image.
     */
    public void service(HttpServletRequest req, HttpServletResponse res)
    throws IOException, ServletException {
        ServletOutputStream out=null;
        DeliverableImage aImage=null;
        MailingComponent comp=null;
    
        if(cacheMap == null) {    
             cacheMap=(TimeoutLRUMap)WebApplicationContextUtils.getWebApplicationContext(this.getServletContext()).getBean("imageCache");
        }
        if(req.getParameter("ci")==null || req.getParameter("mi")==null || req.getParameter("name")==null) {
            return;
        }
        
        if(req.getParameter("name").length()==0) {
            return;
        }
        
        String cacheKey=req.getParameter("ci")+"-"+req.getParameter("mi")+"-"+req.getParameter("name");
        aImage=(DeliverableImage)cacheMap.get(cacheKey);
        
        if(aImage==null) {
            try {
                MailingComponentDao mDao=(MailingComponentDao)WebApplicationContextUtils.getWebApplicationContext(this.getServletContext()).getBean("MailingComponentDao");
                comp=mDao.getMailingComponentByName(Integer.parseInt(req.getParameter("mi")), Integer.parseInt(req.getParameter("ci")), req.getParameter("name"));
            } catch (Exception e) {
                System.err.println("Exception: "+e);
                System.err.println(AgnUtils.getStackTrace(e));
                return;
            }
           
            if(comp!=null) {
                aImage=new DeliverableImage();
                aImage.mtype=comp.getMimeType();
                aImage.imageData=comp.getBinaryBlock();
                cacheMap.put(cacheKey, aImage);
                AgnUtils.logger().debug("added to cache: "+cacheKey);
            } else {
                aImage=new DeliverableImage();
                aImage.mtype="text/html";
                aImage.imageData=new String("image not found").getBytes();
                cacheMap.put(cacheKey, aImage);
                AgnUtils.logger().debug("added not found to cache: "+cacheKey);
            }
        }
        
        if(aImage!=null) {
            try {
                res.setContentType(aImage.mtype);
                out=res.getOutputStream();
                out.write(aImage.imageData);
                out.flush();
                out.close();
            } catch (Exception e) {
                AgnUtils.logger().error(e.getMessage());
            }
        }
    }
    
    private class DeliverableImage {
        public byte[] imageData;
        public String mtype;
    }
}
