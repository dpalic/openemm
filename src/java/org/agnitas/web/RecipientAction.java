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
import org.agnitas.target.*;
import org.agnitas.beans.Recipient;
import org.agnitas.beans.BindingEntry;
import java.io.IOException;
import java.util.*;
import java.sql.*;
import javax.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.hibernate.dialect.*;
import org.apache.struts.action.*;
import org.springframework.context.*;
import org.springframework.jdbc.core.*;

/**
 * Handles all actions on profile fields.
 */
public final class RecipientAction extends StrutsActionBase {

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
        RecipientForm aForm=null;
        ActionMessages errors = new ActionErrors();
        ActionForward destination=null;
        ApplicationContext aContext=this.getWebApplicationContext();

        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }

        if(form!=null) {
            aForm=(RecipientForm)form;
        } else {
            aForm=new RecipientForm();
        }

        if(req.getParameter("delete.x")!=null) {
            aForm.setAction(this.ACTION_CONFIRM_DELETE);
        }

        AgnUtils.logger().info("Recipient Action: "+aForm.getAction());
        try {
            switch(aForm.getAction()) {
                case ACTION_LIST:
                    if(allowed("recipient.show", req)) {
                        TargetRepresentation targetRep=aForm.getTarget();

                        destination=mapping.findForward("list");
                        if(!targetRep.checkBracketBalance()) {
                            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.target.bracketbalance"));
                        }
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case ACTION_VIEW:
                    if(allowed("recipient.show", req)) {
                        if(req.getParameter("recipientID")!=null) {
                            loadRecipient(aContext, aForm, req);
                            aForm.setAction(RecipientAction.ACTION_SAVE);
                        } else {
                            aForm.setAction(RecipientAction.ACTION_NEW);
                        }
                        destination=mapping.findForward("view");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case ACTION_SAVE:
                    if(allowed("recipient.change", req)) {
                        if(req.getParameter("save.x")!=null) {
                            saveRecipient(aContext, aForm, req);
                            aForm.setAction(RecipientAction.ACTION_LIST);
                            destination=mapping.findForward("list");
                        }
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case ACTION_NEW:
                    if(allowed("recipient.new", req)) {
                       if(req.getParameter("save.x")!=null) {
                            aForm.setRecipientID(0);
                            if(saveRecipient(aContext, aForm, req)){
                                aForm.setAction(RecipientAction.ACTION_LIST);
                                destination=mapping.findForward("list");
                            } else {
                                errors.add("NewRecipient", new ActionMessage("error.subscriber.insert_in_db_error"));
                                aForm.setAction(RecipientAction.ACTION_VIEW);
                                destination=mapping.findForward("view");
                            }
                        }
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case ACTION_CONFIRM_DELETE:
                    if(allowed("recipient.delete", req)) {
                        loadRecipient(aContext, aForm, req);
                        destination=mapping.findForward("delete");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case ACTION_DELETE:
                    if(allowed("recipient.delete", req)) {
                        if(req.getParameter("kill.x")!=null) {
                            deleteRecipient(aContext, aForm, req);
                            aForm.setAction(RecipientAction.ACTION_LIST);
                            destination=mapping.findForward("list");
                        }
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                default:
                    aForm.setAction(RecipientAction.ACTION_LIST);
                    destination=mapping.findForward("list");
            }

        } catch (Exception e) {
            AgnUtils.logger().error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        }

        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(req, errors);
            // return new ActionForward(mapping.getForward());
        }

        return destination;
    }

    /**
     * Loads recipient.
     */
    protected void loadRecipient(ApplicationContext aContext, RecipientForm aForm, HttpServletRequest req) {
        Recipient cust=(Recipient) aContext.getBean("Recipient");
        Map data=null;
        Iterator i=null;

        cust.setCompanyID(this.getCompanyID(req));
        cust.setCustomerID(aForm.getRecipientID());
        data=cust.getCustomerDataFromDb();
        i=data.keySet().iterator();
        while(i.hasNext()) {
            String key=(String) i.next();

            if(key.equals("gender")) {
                aForm.setGender(Integer.parseInt((String) data.get("gender")));
            } else if(key.equals("title")) {
                aForm.setTitle((String) data.get(key));
            } else if(key.equals("firstname")) {
                aForm.setFirstname((String) data.get(key));
            } else if(key.equals("lastname")) {
                aForm.setLastname((String) data.get(key));
            } else if(key.equals("email")) {
                aForm.setEmail((String) data.get(key));
            } else if(key.equals("mailtype")) {
                aForm.setMailtype(Integer.parseInt((String) data.get("mailtype")));
            } else {
                aForm.setColumn(key, data.get(key));
            }
        }
    }

    /**
     * Saves bindings.
     */
    protected void saveBindings(ApplicationContext aContext, RecipientForm aForm, HttpServletRequest req) {
        Recipient cust=(Recipient) aContext.getBean("Recipient");
        int companyID=getCompanyID(req);
        int customerID=aForm.getRecipientID();
        Map allCustLists=null;
        Map bindings=aForm.getAllBindings();
        Iterator mit=bindings.keySet().iterator();

        cust.setCompanyID(companyID);
        cust.setCustomerID(customerID);
        allCustLists=cust.getAllMailingLists();
        while(mit.hasNext()) {
            Integer mid=(Integer) mit.next();
            Map mailing=(Map) bindings.get(mid);
            Iterator tit=mailing.keySet().iterator();

            while(tit.hasNext()) {
                Integer tid=(Integer) tit.next();
                BindingEntry binding=(BindingEntry) mailing.get(tid);

                if(binding.getUserStatus() != 0) {
                    binding.setCustomerID(customerID);
                    if(!binding.saveBindingInDB(companyID, allCustLists)) {
		        AgnUtils.logger().error("saveBindings: Binding could not be saved");
                    }
                }
            }
        }
    }

    /**
     * Saves recipient.
     */
    protected boolean saveRecipient(ApplicationContext aContext, RecipientForm aForm, HttpServletRequest req) {
        Recipient cust=(Recipient) aContext.getBean("Recipient");
        Map data=null;
        Map column=null;
        Iterator i=null;

        cust.setCompanyID(this.getCompanyID(req));
        if(aForm.getRecipientID() != 0) {
            cust.setCustomerID(aForm.getRecipientID());
        } else {
            cust.setCustomerID(cust.insertNewCust());
            aForm.setRecipientID(cust.getCustomerID());
        }
        saveBindings(aContext, aForm, req);
        data=cust.getCustomerDataFromDb();
        data.put("gender", new Integer(aForm.getGender()).toString());
        data.put("title", aForm.getTitle());
        data.put("firstname", aForm.getFirstname());
        data.put("lastname", aForm.getLastname());
        data.put("email", aForm.getEmail());
        data.put("mailtype", new Integer(aForm.getMailtype()).toString());
        column=aForm.getColumnMap();
        i=column.keySet().iterator();
        while(i.hasNext()) {
            String key=(String) i.next();
            String value=(String) column.get(key);

            data.put(key, value);
        }
        cust.setCustParameters(data);
        cust.updateInDB();

        updateCustBindingsFromAdminReq(cust, aContext, req);
        return true;
    }

    /**
     * Updates customer bindings.
     */
    public boolean updateCustBindingsFromAdminReq(Recipient cust, ApplicationContext aContext, HttpServletRequest req) {

        String aKey=null;
        String newKey=null;
        String aParam=null;
        int aMailinglistID;
        int oldSubStatus, newSubStatus;
        String tmpUT=null;
        String tmpOrgUT=null;
        Iterator aEnum=req.getParameterMap().keySet().iterator();
        BindingEntry aEntry=(BindingEntry) aContext.getBean("BindingEntry");

        int i;
        int nst;
        String mt = "";
        String aK2 = "";

        while(aEnum.hasNext()) {
            aKey=(String)aEnum.next();
            if(aKey.startsWith("AGN_0_ORG_MT")) {
                oldSubStatus = Integer.parseInt((String) req.getParameter(aKey));
                aMailinglistID=Integer.parseInt(aKey.substring(12));
                newKey=new String("AGN_0_MTYPE" + aMailinglistID);
                aParam=(String) req.getParameter(newKey);
                if(aParam!=null) {
                    newSubStatus=1;
                } else {
                    newSubStatus=0;
                }

                newKey=new String("AGN_0_MLUT" + aMailinglistID);
                tmpUT=(String) req.getParameter(newKey);
                newKey=new String("AGN_0_ORG_UT" + aMailinglistID);
                tmpOrgUT=(String) req.getParameter(newKey);

                if((newSubStatus!=oldSubStatus) || (tmpUT.compareTo(tmpOrgUT)!=0)) {
                    aEntry.setMediaType(0);
                    aEntry.setCustomerID(cust.getCustomerID());
                    aEntry.setMailinglistID(aMailinglistID);
                    aEntry.setUserType(tmpUT);
                    if(newSubStatus==0) { // Opt-Out
                        aEntry.setUserStatus(BindingEntry.USER_STATUS_ADMINOUT);
              //          aEntry.setUserRemark("Opt-Out by ADMIN");
                    } else { // Opt-In
                        aEntry.setUserStatus(BindingEntry.USER_STATUS_ACTIVE);
              //          aEntry.setUserRemark("Opt-In by ADMIN");
                    }
                    if(aEntry.updateBindingInDB(cust.getCompanyID())==false) {
                        // aEntry.setUserType(BindingEntry.USER_TYPE_WORLD); // Bei Neu-Eintrag durch User entsprechenden Typ setzen
                        if(newSubStatus==1) {
                            aEntry.insertNewBindingInDB(cust.getCompanyID());
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Removes recipient.
     */
    protected void deleteRecipient(ApplicationContext aContext, RecipientForm aForm, HttpServletRequest req) {
        Recipient cust=(Recipient) aContext.getBean("Recipient");

        cust.setCompanyID(this.getCompanyID(req));
        cust.setCustomerID(aForm.getRecipientID());
        cust.deleteCustomerDataFromDb();
    }
}
