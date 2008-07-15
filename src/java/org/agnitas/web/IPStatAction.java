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
import org.agnitas.stat.*;
import org.agnitas.stat.impl.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import java.text.*;
import javax.servlet.http.*;
import org.xml.sax.*;
import org.apache.struts.action.*;
import org.apache.struts.util.*;
import org.apache.struts.upload.*;
import org.springframework.web.context.WebApplicationContext;


public final class IPStatAction extends StrutsActionBase {
    
    public static final int ACTION_STAT = 1;
    public static final int ACTION_SPLASH = 2;
  
    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     * 
     * @param form 
     * @param req 
     * @param res 
     * @param mapping The ActionMapping used to select this instance
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     * @return destination
     */

    public ActionForward execute(ActionMapping mapping,
    ActionForm form,
    HttpServletRequest req,
    HttpServletResponse res)
    throws IOException, ServletException {
        
        // Validate the request parameters specified by the user
        IPStatForm aForm=null;
        ActionMessages errors = new ActionMessages();
        ActionForward destination=null;

        
        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }

        
        if(form!=null) {
            AgnUtils.logger().info("execute: IPStatForm exists");
            aForm=(IPStatForm)form;
        } else {
            AgnUtils.logger().info("execute: IPStatForm new");
            aForm=new IPStatForm();
        }
        
        if(!allowed("stats.ip", req)) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
            saveErrors(req, errors);
            return null;
        }
        
        try {
            switch(aForm.getAction()) {
                case IPStatAction.ACTION_STAT:
                    
                    if(aForm.isStatInProgress()==false) {
                        
                        if(aForm.isStatReady()) {
                            destination=mapping.findForward("stat");
                            aForm.setStatReady(false);
                            break;
                            
                        } else {

                            // display splash in browser
                            RequestDispatcher dp=req.getRequestDispatcher(mapping.findForward("splash").getPath());
                            dp.forward(req, res);
                            res.flushBuffer();
                            destination=null;

                            // get stats
                            aForm.setStatInProgress(true);
                            loadIPStats(aForm, req);
                            aForm.setStatInProgress(false);
                            aForm.setStatReady(true);
                            break;
                        }
                    } 
                    break;
                    
                    
                case IPStatAction.ACTION_SPLASH:
                    if(aForm.isStatReady()) {
                        destination=mapping.findForward("stat");
                    }
                    // just display splash
                    destination=mapping.findForward("splash");
                    break;
                    

                    
                default:
                    aForm.setAction(IPStatAction.ACTION_STAT);
                    loadIPStats(aForm, req);
                    destination=mapping.findForward("stat");
            }

        } catch (Exception e) {
            AgnUtils.logger().error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        }
        
        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(req, errors);
            AgnUtils.logger().info("execute: saving errors "+destination);
        }
        
        return destination;
        
    }
    
    /**
     * Loads IP statistics
     */
    protected void loadIPStats(IPStatForm aForm, HttpServletRequest req) {
        
        org.agnitas.stat.impl.IPStatImpl aIPStat=null;
        aIPStat = new IPStatImpl();
        
        aIPStat.setCompanyID(this.getCompanyID(req));
        aIPStat.setTargetID(aForm.getTargetID());
        aIPStat.setListID(aForm.getListID());
        aIPStat.setMaxIPs(aForm.getMaxIPs());
        
        WebApplicationContext myContext = this.getWebApplicationContext();
        
        if(aIPStat.getStatFromDB(myContext, req)==true) {
            aForm.setIps(aIPStat.getIps());
            aForm.setSubscribers(aIPStat.getSubscribers());
            aForm.setTotal(aIPStat.getTotal());
            aForm.setBiggest(aIPStat.getBiggest());
            aForm.setLines(aIPStat.getLines());
            aForm.setRest(aIPStat.getRest());
            aForm.setCsvfile(aIPStat.getCsvfile());
            AgnUtils.logger().info("loadIPStats: loaded.");
        } else {
            AgnUtils.logger().warn("loadIPStats: could not load.");
        }
        
        return;
    }  
}
