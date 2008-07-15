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
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.agnitas.beans.Admin;
import org.agnitas.beans.Mailing;
import org.agnitas.beans.MailingComponent;
import org.agnitas.dao.MailingComponentDao;
import org.agnitas.dao.MailingDao;
import org.agnitas.util.AgnUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;


/**
 * Implementation of <strong>Action</strong> that validates a user logon.
 *
 * @author Martin Helff
 */

public final class MailingComponentsAction extends StrutsActionBase {

    public static final int ACTION_SAVE_COMPONENTS = ACTION_LAST+1;

    public static final int ACTION_SAVE_COMPONENT_EDIT = ACTION_LAST+2;


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
        MailingComponentsForm aForm=null;
        ActionMessages errors = new ActionMessages();
        ActionForward destination=null;

        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }

        aForm=(MailingComponentsForm)form;
        AgnUtils.logger().info("Action: "+aForm.getAction());

        try {
            switch(aForm.getAction()) {
                case MailingComponentsAction.ACTION_LIST:
                    if(allowed("mailing.components.show", req)) {
                        loadMailing(aForm, req);
                        aForm.setAction(MailingComponentsAction.ACTION_SAVE_COMPONENTS);
                        destination=mapping.findForward("list");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case MailingComponentsAction.ACTION_SAVE_COMPONENTS:
                    if(allowed("mailing.components.change", req)) {
                        destination=mapping.findForward("list");
                        saveComponent(aForm, req);
                        loadMailing(aForm, req);
                        aForm.setAction(MailingComponentsAction.ACTION_SAVE_COMPONENTS);
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                        destination=mapping.findForward("list");
                    }
                    break;

                case MailingComponentsAction.ACTION_SAVE_COMPONENT_EDIT:
                    if(allowed("mailing.components.change", req)) {
                        destination=mapping.findForward("component_edit");
                        saveComponent(aForm, req);
                        aForm.setAction(MailingComponentsAction.ACTION_SAVE_COMPONENTS);
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                default:
                    aForm.setAction(MailingComponentsAction.ACTION_LIST);
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
     * Loads mailing.
     */
    protected void loadMailing(MailingComponentsForm aForm, HttpServletRequest req) throws Exception {
        MailingDao mDao=(MailingDao) getBean("MailingDao");
        Mailing aMailing=mDao.getMailing(aForm.getMailingID(), this.getCompanyID(req));

        aForm.setShortname(aMailing.getShortname());
        aForm.setDescription(aMailing.getDescription());
        aForm.setIsTemplate(aMailing.isIsTemplate());

        AgnUtils.logger().info("loadMailing: mailing loaded");
        return;
    }

    /**
     * Saves components.
     */
    protected void saveComponent(MailingComponentsForm aForm, HttpServletRequest req) {
        MailingComponent aComp=null;
        String aParam=null;
        Vector deleteEm=new Vector();

        MailingDao mDao=(MailingDao) getBean("MailingDao");
        Mailing aMailing=mDao.getMailing(aForm.getMailingID(), this.getCompanyID(req));

        FormFile newImage=aForm.getNewFile();
        try {
            if(newImage.getFileSize()!=0) {
                aComp=(MailingComponent)aMailing.getComponents().get(newImage.getFileName());
                if(aComp!=null && aComp.getType()==MailingComponent.TYPE_HOSTED_IMAGE) {
                    aComp.setBinaryBlock(newImage.getFileData());
                    aComp.setEmmBlock(aComp.makeEMMBlock());
                    aComp.setMimeType(newImage.getContentType());
                } else {
                    aComp=(MailingComponent) getBean("MailingComponent");
                    aComp.setCompanyID(this.getCompanyID(req));
                    aComp.setMailingID(aForm.getMailingID());
                    aComp.setType(MailingComponent.TYPE_HOSTED_IMAGE);
                    aComp.setComponentName(newImage.getFileName());
                    aComp.setBinaryBlock(newImage.getFileData());
                    aComp.setEmmBlock(aComp.makeEMMBlock());
                    aComp.setMimeType(newImage.getContentType());
                    aMailing.addComponent(aComp);
                }
            }
        } catch(Exception e) {
            AgnUtils.logger().error("saveComponent: " + e);
        }

        if(aForm.getAction()==MailingComponentsAction.ACTION_SAVE_COMPONENT_EDIT) {
            HttpSession sess=req.getSession();
            req.setAttribute("file_path", new String(((Admin)sess.getAttribute("emm.admin")).getCompany().getRdirDomain()+"/image?ci="+this.getCompanyID(req)+"&mi="+aForm.getMailingID()+"&name="+newImage.getFileName()));
        }

        Iterator it=aMailing.getComponents().values().iterator();
        while (it.hasNext()) {
            aComp=(MailingComponent)it.next();
            switch(aComp.getType()) {
                case MailingComponent.TYPE_IMAGE:
                    aParam=req.getParameter("update"+aComp.getId()+".x");
                    if(aParam!=null) {
                        aComp.loadContentFromURL();
                    }
                    break;

                case MailingComponent.TYPE_HOSTED_IMAGE:
                    aParam=req.getParameter("delete"+aComp.getId()+".x");
                    if(aParam!=null) {
                        deleteEm.add(aComp);
                        if ( AgnUtils.isOracleDB() ){
                        	MailingComponentDao mcDao=(MailingComponentDao) getBean("MailingComponentDao");
	                        MailingComponent amComponent=mcDao.getMailingComponentByName(aComp.getMailingID(), aComp.getCompanyID(), aComp.getComponentName());
    	                    mcDao.deleteMailingComponent(amComponent);
                        }
                    }
                    break;
            }
        }

        Enumeration en=deleteEm.elements();
        while(en.hasMoreElements()) {
        	aMailing.getComponents().remove(((MailingComponent)en.nextElement()).getComponentName());
        }

        mDao.saveMailing(aMailing);

        return;
    }
}
