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
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.beans.Company;
import org.agnitas.beans.UserForm;
import org.agnitas.dao.UserFormDao;
import org.agnitas.emm.core.commons.uid.DeprecatedUIDVersionException;
import org.agnitas.emm.core.commons.uid.UID;
import org.agnitas.emm.core.commons.uid.UIDParser;
import org.agnitas.exceptions.FormNotFoundException;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.TimeoutLRUMap;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;


/**
 * Implementation of <strong>Action</strong> that processes a form.do request
 *
 * @author mhe
 * @version $Revision: 1.1 $ $Date: 2006/08/03 08:47:47 $
 */

public class UserFormExecuteAction extends StrutsActionBase {
    
    // --------------------------------------------------------- Public Methods
    // TimeoutLRUMap companys=new TimeoutLRUMap(AgnUtils.getDefaultIntValue("onepixel.keys.maxCache"), AgnUtils.getDefaultIntValue("onepixel.keys.maxCacheTimeMillis"));
    
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
     * @return the action to forward to. 
     */
    public ActionForward execute(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest req,
            HttpServletResponse res)
            throws IOException, ServletException {

        // Validate the request parameters specified by the user
        ActionMessages errors = new ActionMessages();
        UserFormExecuteForm aForm=(UserFormExecuteForm)form;
        ActionForward destination=null;
        HashMap params=new HashMap();
        
        try {
            res.setBufferSize(65535);

            if ( AgnUtils.isOracleDB() ) {
            	res.setCharacterEncoding(req.getCharacterEncoding());
            }
	/* Daimler Hack */
           // res.setCharacterEncoding("utf-8");
            this.processUID(req, params, aForm.getAgnUseSession());
            params.put("requestParameters", AgnUtils.getReqParameters(req));
            params.put("_request", req);

            try {
            	String responseContent=executeForm(aForm, params, req, errors);
            	sendFormResult(res, params, responseContent);
            } catch (FormNotFoundException formNotFoundEx) {
            	destination = handleFormNotFound(mapping, req, res, params);
            }
            
            if(params.get("_error")==null) {
                this.evaluateFormEndAction(aForm, params);
            }
        } catch (Exception e) {
            System.err.println("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            AgnUtils.logger().error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        }
        
        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(req, errors);
            return new ActionForward(mapping.getInput());
        }
        
        return destination;
        
    }
    
    protected void sendFormResult(HttpServletResponse res, HashMap params, String responseContent) throws IOException {
        if(params.get("responseRedirect")!=null) {
            res.sendRedirect((String)params.get("responseRedirect"));
        } else {
            String responseMimetype = "text/html";
            if(params.get("responseMimetype")!=null) {
                responseMimetype=(String)params.get("responseMimetype");
            }
            res.setContentType(responseMimetype);

            PrintWriter out=res.getWriter();
            out.print(responseContent);
            out.close();
        }
        
        res.flushBuffer();
    }
    
    protected ActionForward handleFormNotFound(ActionMapping mapping, HttpServletRequest request, HttpServletResponse res, HashMap param) throws IOException {
    	sendFormResult(res, param, "form not found");
    	
    	return null;
    }

    /** Execute the requested form.
     * Reads the form defined by aForm.getAgnFN() and aForm.getAgnCI() from the
     * database and executes it.
     * @param aForm form info.
     * @param params a map containing the form values.
     * @param req the ServletRequest, used to get the ApplicationContext.
     * @param errors used to sotre error descriptions.
     */  
    protected String executeForm(UserFormExecuteForm aForm, HashMap params, HttpServletRequest req, ActionMessages errors) throws IOException, FormNotFoundException {
        String result = "no parameters";
        UserFormDao dao=(UserFormDao) getBean("UserFormDao");
        UserForm aUserForm=dao.getUserFormByName(aForm.getAgnFN(), aForm.getAgnCI());
        
        if(aUserForm!=null) {
            result=aUserForm.evaluateForm(this.getWebApplicationContext(), params);
        } else {
            throw new FormNotFoundException();
        }
        
        return result;
    }
    
    /** Execute the end action of the requested form.
     * Reads the form defined by aForm.getAgnFN() and aForm.getAgnCI() from the
     * database and executes it's end action.
     * @param aForm form info.
     * @param params a map containing the form values.
     */  
    protected boolean evaluateFormEndAction(UserFormExecuteForm aForm, HashMap params) throws IOException {
        
        UserFormDao dao=(UserFormDao) getBean("UserFormDao");
        UserForm aUserForm=dao.getUserFormByName(aForm.getAgnFN(), aForm.getAgnCI());
        
        if(aUserForm == null || aUserForm.getEndActionID()==0) {
            return false;
        }
        
        return aUserForm.evaluateEndAction(this.getWebApplicationContext(), params);
    }
   
    /* information from a given url. 
     * Parses an url and returns the retrieved values in a hash.
     * @param req ServletRequest, used to get the Session.
     * @param params HashMap to store the retrieved values in.
     * @param useSession also store the result in the session if this is not 0.
     */ 
    public void processUID(HttpServletRequest req, HashMap params, int useSession) {
        UID uid=null;
        int compID = 0;
        String par=req.getParameter("agnUID");

        if(par!=null) {
            uid=this.decodeTagString(par);
        }
        
        if(req.getParameter("agnCI") != null) {
        	try {
        		compID = Integer.parseInt(req.getParameter("agnCI"));
        	} catch( NumberFormatException e) {
        		compID = 0;
        	}
        }
        
        if(uid!=null) {
        	if(compID == uid.getCompanyID()) {
        		params.put("customerID", new Integer((int)uid.getCustomerID()));
        		params.put("mailingID", new Integer((int)uid.getMailingID()));
        		params.put("urlID", new Integer((int)uid.getURLID()));
        		params.put("agnUID", par);
        		if(useSession!=0) {
        			HashMap tmpPars=new HashMap();
        			tmpPars.putAll(params);
        			req.getSession().setAttribute("agnFormParams", tmpPars);
        			params.put("sessionID", req.getSession().getId());
        		}
        	}
        } else {
            if(useSession!=0) {
                if(req.getSession().getAttribute("agnFormParams")!=null){
                    params.putAll((HashMap)req.getSession().getAttribute("agnFormParams"));
                }
            }
        }
    }
   
    /** Use a tag to get a UID.
     * Retrieves a UID according to a given tag.
     * @param tag a string defining the uid.
     * @return the resulting UID.
     */
    public UID decodeTagString(String tag) {
        int companyID=0;
        Company company=null;
        UID uid=null;
        
        try {
        	/*
            uid = (UID) getBean("UID");
            
            uid.parseUID(tag);
            companyID=(int)uid.getCompanyID();
            if(companyID==0) {
                return null;
            }
            
            if(companys!=null) {
                company=(Company)companys.get(Integer.toString(companyID));
            }
            
            if(company==null) {
                CompanyDao dao=(CompanyDao) getBean("CompanyDao");
                
                company=dao.getCompany(companyID);
            }
            
            if(company!=null) {
                uid.setPassword(company.getSecret());
                
                boolean valideUID = uid.validateUID(company.getSecret());
				boolean valideHackUID = uid.validateUID("");
				if(!valideUID) {
                	if ( !valideHackUID ) {
    					AgnUtils.logger().warn("uid invalid: "+tag);
    					return null;
                	}
                }
                uid.setPassword(company.getSecret());
            }
            */
        	UIDParser uidParser = (UIDParser) this.getWebApplicationContext().getBean( "UIDParser");
        	try {
        		uid = uidParser.parseUID(tag);
        	} catch( DeprecatedUIDVersionException e) {
        		uid = null;
				Logger.getLogger(this.getClass()).warn("deprecated UID version: " + tag);
				Logger.getLogger(this.getClass()).debug( e);
        	}
        } catch (Exception e) {
            AgnUtils.logger().error("decodeTagString: " + e);
            System.err.println("decodeTagString: " + e);
            return null;
        }
        
        
        return uid;
    }

}
