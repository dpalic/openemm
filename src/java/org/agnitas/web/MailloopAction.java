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
import java.util.GregorianCalendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.beans.Mailloop;
import org.agnitas.dao.MailloopDao;
import org.agnitas.util.AgnUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

/**
 * Implementation of <strong>Action</strong> that handles Mailloop-Data
 *
 * @author Martin Helff
 */

public final class MailloopAction extends StrutsActionBase {

    public static final int ACTION_SEND_TEST = ACTION_LAST+1;


    // --------------------------------------------------------- Public Methods


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
            throws Exception {

        // Validate the request parameters specified by the user
        MailloopForm aForm=null;
        ActionMessages errors = new ActionMessages();
        ActionForward destination=null;

        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }

        aForm=(MailloopForm)form;


        try {
            switch(aForm.getAction()) {
                case MailloopAction.ACTION_LIST:
                    if(allowed("mailing.show", req)) {
                        loadMailloops(aForm, req);
                        destination=mapping.findForward("list");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case MailloopAction.ACTION_VIEW:
                    if(allowed("mailing.show", req)) {
                        if(aForm.getMailloopID()!=0) {
                            loadMailloop(aForm, req);
                        } else {
                            aForm.clearData();
                        }
                        aForm.setAction(MailloopAction.ACTION_SAVE);
                        destination=mapping.findForward("view");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case MailloopAction.ACTION_NEW:
                    if(allowed("mailing.show", req)) {
                        aForm.clearData();
                        aForm.setAction(MailloopAction.ACTION_SAVE);
                        destination=mapping.findForward("view");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case MailloopAction.ACTION_SAVE:
                    if(allowed("mailing.show", req)) {
                        saveMailloop(aForm, req);
                        loadMailloops(aForm, req);
                        destination=mapping.findForward("list");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case MailloopAction.ACTION_CONFIRM_DELETE:
                    if(allowed("mailing.show", req)) {
                        loadMailloop(aForm, req);
                        destination=mapping.findForward("delete");
                        aForm.setAction(MailloopAction.ACTION_DELETE);
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case MailloopAction.ACTION_DELETE:
                    if(allowed("mailing.show", req)) {
                        deleteMailloop(aForm, req);
                        loadMailloops(aForm, req);
                        aForm.setAction(MailloopAction.ACTION_LIST);
                        destination=mapping.findForward("list");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;


                default:
                    destination=mapping.findForward("list");
                    break;
            }

        } catch (Exception e) {
            AgnUtils.logger().error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        }

        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(req, errors);
            return (new ActionForward(mapping.getInput()));
        }

        return destination;
    }

    /**
     * Loads mailloops.
     */
    protected void loadMailloops(MailloopForm aForm, HttpServletRequest req) {
        MailloopDao mDao=(MailloopDao) getBean("MailloopDao");

        aForm.setMailloops(mDao.getMailloops(getCompanyID(req)));
    }

    /**
     * Loads mailloop.
     */
    protected void loadMailloop(MailloopForm aForm, HttpServletRequest req) {

        Mailloop aLoop=null;
        MailloopDao mDao=(MailloopDao) getBean("MailloopDao");

        aLoop=mDao.getMailloop(aForm.getMailloopID(), getCompanyID(req));
        if(aLoop!=null) {
            try {
                BeanUtils.copyProperties(aForm, aLoop);
            } catch (Exception e) {
                AgnUtils.logger().error("loadMailloop: "+e);
            }
        } else {
            AgnUtils.logger().error("loadMailloop: could not load Mailloop");
        }
    }

    /**
     * Saves mailloop.
     */
    protected void saveMailloop(MailloopForm aForm, HttpServletRequest req) {
    	java.util.Calendar cal=new GregorianCalendar();
        Mailloop aLoop=null;
        int loopID=aForm.getMailloopID();
        MailloopDao mDao=(MailloopDao) getBean("MailloopDao");
        java.sql.Timestamp ts = new java.sql.Timestamp(cal.getTime().getTime());
        aForm.setChangedate(ts);

        if(loopID!=0) {
            aLoop=mDao.getMailloop(aForm.getMailloopID(), getCompanyID(req));
        }

        if(aLoop==null) {
            aLoop=(Mailloop) getBean("Mailloop");
            aLoop.setCompanyID(getCompanyID(req));
            loopID=0;
        }

        try {
            BeanUtils.copyProperties(aLoop, aForm);
            aLoop.setId(loopID);
        } catch (Exception e) {
            AgnUtils.logger().error("saveMailloop: "+e);
        }

        mDao.saveMailloop(aLoop);
    }

    /**
     * Removes mailloop.
     */
    protected void deleteMailloop(MailloopForm aForm, HttpServletRequest req) {
        MailloopDao mDao=(MailloopDao) getBean("MailloopDao");

        if(aForm.getMailloopID()!=0) {
            mDao.deleteMailloop(aForm.getMailloopID(), getCompanyID(req));
        }
    }
}
