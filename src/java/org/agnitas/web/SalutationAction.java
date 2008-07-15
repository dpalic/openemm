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
import org.agnitas.dao.TitleDao;
import org.agnitas.web.StrutsActionBase;
import java.io.IOException;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.struts.action.*;
import org.hibernate.*;
import org.springframework.context.*;
import org.springframework.jdbc.core.*;
import org.springframework.orm.hibernate3.*;


/**
 * Implementation of <strong>Action</strong> that handles Mailinglists
 *
 * @author Martin Helff
 */

public final class SalutationAction extends StrutsActionBase {
    
    // --------------------------------------------------------- Public Methods
    
    
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
        ApplicationContext aContext=this.getWebApplicationContext();
        SalutationForm aForm=null;
        ActionMessages errors = new ActionMessages();
        ActionForward destination=null;
        
        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }

        if(form!=null) {
            aForm=(SalutationForm)form;
        } else {
            aForm=new SalutationForm();
        }
        
        AgnUtils.logger().info("Action: "+aForm.getAction());
        
        if(req.getParameter("delete.x")!=null) {
            aForm.setAction(this.ACTION_CONFIRM_DELETE);
        }
        
        try {
            switch(aForm.getAction()) {
                case SalutationAction.ACTION_LIST:
                    if(allowed("settings.show", req)) {
                        destination=mapping.findForward("list");
                    }
                    break;
                    
                case SalutationAction.ACTION_VIEW:
                    if(aForm.getSalutationID() != 0) {
                        aForm.setAction(SalutationAction.ACTION_SAVE);
                        loadSalutation(aForm, aContext, req);
                    } else {
                        aForm.setAction(SalutationAction.ACTION_NEW);
                    }
                    destination=mapping.findForward("view");
                    break;
                case SalutationAction.ACTION_SAVE:
                    if(req.getParameter("save.x")!=null) {
                        saveSalutation(aForm, aContext, req);
                        destination=mapping.findForward("view");
                    }
                    break;
                    
                case SalutationAction.ACTION_NEW:
                    if(allowed("settings.show", req)) {
                        if(req.getParameter("save.x")!=null) {
                            aForm.setSalutationID(0);
                            saveSalutation(aForm, aContext, req);
                            aForm.setAction(SalutationAction.ACTION_SAVE);
                            destination=mapping.findForward("view");
                        }
                    }
                    break;
                    
                case SalutationAction.ACTION_CONFIRM_DELETE:
                    loadSalutation(aForm, aContext, req);
                    aForm.setAction(SalutationAction.ACTION_DELETE);
                    destination=mapping.findForward("delete");
                    break;
                    
                case SalutationAction.ACTION_DELETE:
                    if(req.getParameter("kill.x")!=null) {
                        this.deleteSalutation(aForm, aContext, req);
                        aForm.setAction(SalutationAction.ACTION_LIST);
                        destination=mapping.findForward("list");
                    }
                    break;

                default:
                    aForm.setAction(SalutationAction.ACTION_LIST);
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
     * Loads salutation.
     */
    protected void loadSalutation(SalutationForm aForm, ApplicationContext aContext, HttpServletRequest req) {
        int compID = getCompanyID(req);
        int titID = aForm.getSalutationID();
        TitleDao titleDao=(TitleDao) getBean("TitleDao"); 
        Title title=titleDao.getTitle(titID, compID);

        Map map=title.getTitleGender();

        aForm.setSalMale((String) map.get(new Integer(Title.GENDER_MALE)));
        aForm.setSalFemale((String) map.get(new Integer(Title.GENDER_FEMALE)));
        aForm.setSalUnknown((String) map.get(new Integer(Title.GENDER_UNKNOWN)));
        aForm.setSalCompany((String) map.get(new Integer(Title.GENDER_COMPANY)));
        aForm.setShortname(title.getDescription());
        
        return;
    }

    /**
     * Saves salutation.
     */
    protected void saveSalutation(SalutationForm aForm, ApplicationContext aContext, HttpServletRequest req) {
        int compID = getCompanyID(req);
        int titID = aForm.getSalutationID();
        TitleDao titleDao=(TitleDao) getBean("TitleDao"); 
        Title title=titleDao.getTitle(titID, compID);
        Map map=new HashMap(); 

        if(title == null) {
            title=(Title) getBean("Title"); 
            title.setId(titID);
            title.setCompanyID(compID);
        }
        title.setDescription(aForm.getShortname());
        map.put(new Integer(Title.GENDER_MALE), aForm.getSalMale());
        map.put(new Integer(Title.GENDER_FEMALE), aForm.getSalFemale());
	if(aForm.getSalUnknown() != null && aForm.getSalUnknown().length() > 0) {
            map.put(new Integer(Title.GENDER_UNKNOWN), aForm.getSalUnknown());
        }
	if(aForm.getSalCompany() != null && aForm.getSalCompany().length() > 0) {
            map.put(new Integer(Title.GENDER_COMPANY), aForm.getSalCompany());
        }
        title.setTitleGender(map);
        getHibernateTemplate().saveOrUpdate("Title", title);
        getHibernateTemplate().flush();
        if(aForm.getSalutationID() == 0) {
            aForm.setSalutationID(title.getId());
        }
        return;
    }
    
    /**
     * Removes salutation.
     */
    protected void deleteSalutation(SalutationForm aForm, ApplicationContext aContext, HttpServletRequest req) {
        int compID = getCompanyID(req);
        int titID = aForm.getSalutationID();
        TitleDao titleDao=(TitleDao) getBean("TitleDao"); 
        Title title=titleDao.getTitle(titID, compID);

        if(title != null) {
            getHibernateTemplate().delete(title);
            getHibernateTemplate().flush();
        }
    }
}
