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

import org.agnitas.stat.*;
import org.agnitas.util.*;
import org.agnitas.beans.Campaign;
import org.agnitas.dao.CampaignDao;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.agnitas.beans.impl.CampaignImpl;
import org.apache.struts.action.*;
import org.apache.struts.util.*;


public final class CampaignAction extends StrutsActionBase {
    
    public static final int ACTION_STAT = ACTION_LAST+1;
    public static final int ACTION_SPLASH = ACTION_LAST+2;
    
    
    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param req The HTTP request we are processing
     * @param res The HTTP response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */
    public ActionForward execute(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest req,
            HttpServletResponse res)
            throws IOException, ServletException {
        
        // Validate the request parameters specified by the user
        
        CampaignForm aForm=null;
        ActionMessages errors = new ActionMessages();
        ActionForward destination=null;
        
        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }
        
        if(form!=null) {
            aForm=(CampaignForm)form;
        } else {
            aForm=new CampaignForm();
        }
        
    
        AgnUtils.logger().info("Action: "+aForm.getAction());
        
        if(req.getParameter("delete.x")!=null) {
            aForm.setAction(this.ACTION_CONFIRM_DELETE);
        }
        
        try {
            switch(aForm.getAction()) {
                case CampaignAction.ACTION_LIST:
                    if(allowed("campaign.show", req)) {
                        destination=mapping.findForward("list");
                    }
                    break;
                    
                case CampaignAction.ACTION_VIEW:
                    if(allowed("campaign.show", req)) {
                        loadCampaign(aForm, req);
                        aForm.setAction(CampaignAction.ACTION_SAVE);
                        destination=mapping.findForward("view");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
                    
                case CampaignAction.ACTION_SAVE:
                    if(allowed("campaign.change", req)) {
                        saveCampaign(aForm, req);
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    destination=mapping.findForward("list");
                    break;
                    
                case CampaignAction.ACTION_NEW:
                    if(allowed("campaign.show", req)) {
                        aForm.reset(mapping, req);
                        aForm.setAction(CampaignAction.ACTION_SAVE);
                        aForm.setCampaignID(0);
                        destination=mapping.findForward("view");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                        destination=mapping.findForward("list");
                    }
                    break;
                    
                case CampaignAction.ACTION_CONFIRM_DELETE:
                    loadCampaign(aForm, req);
                    aForm.setAction(CampaignAction.ACTION_DELETE);
                    destination=mapping.findForward("delete");
                    break;
                    
                case CampaignAction.ACTION_DELETE:
                    if(allowed("campaign.show", req)) {
                        if(req.getParameter("kill.x")!=null) {
                            this.deleteCampaign(aForm, req);
                            aForm.setAction(CampaignAction.ACTION_LIST);
                        }
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    destination=mapping.findForward("list");
                    break;
                    
                case CampaignAction.ACTION_STAT:
                    loadCampaign(aForm, req);
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
                            loadCampaignStats(aForm, req);
                            aForm.setStatInProgress(false);
                            aForm.setStatReady(true);
                            break;
                        }
                    }
                    
                case CampaignAction.ACTION_SPLASH:
                    loadCampaign(aForm, req);
                    if(aForm.isStatReady()) {
                        aForm.setAction(CampaignAction.ACTION_STAT);
                        destination=mapping.findForward("stat");
                        break;
                    }
                    // just display splash
                    destination=mapping.findForward("splash");
                    break;
                    
                default:
                    aForm.setAction(CampaignAction.ACTION_LIST);
                    destination=mapping.findForward("list");
            }
            
        } catch (Exception e) {
            AgnUtils.logger().error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        }
        
        
        
        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(req, errors);
        }
        
        return destination;
    }
    
    /**
     * Loads campaign.
     */    
    protected void loadCampaign(CampaignForm aForm, HttpServletRequest req) {
        int campaignID=aForm.getCampaignID();
        int companyID = getCompanyID(req);
        CampaignDao campaignDao = (CampaignDao) getBean("CampaignDao");
        Campaign myCamp = campaignDao.getCampaign(campaignID, companyID);
        
        if(myCamp != null) {
            aForm.setShortname(myCamp.getShortname());
            aForm.setDescription(myCamp.getDescription());
        } else {
            AgnUtils.logger().error("could not load campaign: "+aForm.getTargetID());
        }
        return;        
    }
    
    /**
     * Loads campaign statistics.
     */
    protected void loadCampaignStats(CampaignForm aForm, HttpServletRequest req) {
        int campaignID=aForm.getCampaignID();
        int companyID = getCompanyID(req);
        CampaignDao campaignDao = (CampaignDao) getBean("CampaignDao");
        Campaign myCamp = campaignDao.getCampaign(campaignID, companyID);
        
/*
        if(myCamp.getStatFromDB(useMailtracking, aLocale, null)) {
            aForm.setOpened(myCamp.getOpened());
            aForm.setOptouts(myCamp.getOptouts());
            aForm.setBounces(myCamp.getBounces());
            aForm.setSubscribers(myCamp.getSubscribers());
            aForm.setClicks(myCamp.getClicks());
            aForm.setMailingData(myCamp.getMailingData());
            aForm.setMaxClicks(myCamp.getMaxClicks());
            aForm.setMaxOpened(myCamp.getMaxOpened());
            aForm.setMaxOptouts(myCamp.getMaxOptouts());
            aForm.setMaxSubscribers(myCamp.getMaxSubscribers());
            aForm.setMaxBounces(myCamp.getMaxBounces());
            aForm.setCsvfile(myCamp.getCsvfile());
        }
*/
        System.out.println("campaign ststs loaded.");
        
        return;
    }
    
    /**
     * Saves campaign.
     */    
    protected void saveCampaign(CampaignForm aForm, HttpServletRequest req) {
        int campaignID=aForm.getCampaignID();
        int companyID = getCompanyID(req);
        CampaignDao campaignDao = (CampaignDao) getBean("CampaignDao");
        Campaign myCamp = campaignDao.getCampaign(campaignID, companyID);
        
        if(myCamp == null) {
            aForm.setCampaignID(0);
            myCamp=(Campaign) getBean("Campaign");
            myCamp.setCompanyID(companyID);
        }
        
        myCamp.setShortname(aForm.getShortname());
        myCamp.setDescription(aForm.getDescription());
        
        getHibernateTemplate().saveOrUpdate("Campaign", myCamp);
        return;
    }
    
    /**
     * Deletes campaign.
     */    
    protected void deleteCampaign(CampaignForm aForm, HttpServletRequest req) {
        int campaignID=aForm.getCampaignID();
        int companyID = getCompanyID(req);
        CampaignDao campaignDao = (CampaignDao) getBean("CampaignDao");
        Campaign myCamp = campaignDao.getCampaign(campaignID, companyID);
        
        if(myCamp!=null) {
            getHibernateTemplate().delete(myCamp);
            getHibernateTemplate().flush();
        }
        
        return;
    }    
}
