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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.agnitas.beans.Admin;
import org.agnitas.beans.AdminGroup;
import org.agnitas.beans.Company;
import org.agnitas.beans.FailedLoginData;
import org.agnitas.beans.VersionObject;
import org.agnitas.dao.AdminDao;
import org.agnitas.dao.AdminGroupDao;
import org.agnitas.dao.CompanyDao;
import org.agnitas.dao.EmmLayoutBaseDao;
import org.agnitas.dao.LoginTrackDao;
import org.agnitas.service.VersionControlService;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.forms.LogonForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;


/**
 * Implementation of <strong>Action</strong> that validates a user logon.
 *
 * @author Martin Helff
 */

public class LogonAction extends StrutsActionBase {

    public static final int ACTION_LOGON = 1;
    public static final int ACTION_LOGOFF = 2;
    public static final int ACTION_PASSWORD_CHANGE_REQ = 3;
    public static final int ACTION_PASSWORD_CHANGE = 4;
    public static final int ACTION_FORWARD = 5;
    public static final int ACTION_LAYOUT = 6;


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
        ActionMessages errors = new ActionMessages();
        LogonForm aForm=(LogonForm)form;
        ActionForward destination=null;

        checkForUpdate( req );

        try {
            AgnUtils.logger().info("execute: action " + aForm.getAction());
            switch(aForm.getAction()) {
                case ACTION_LOGON:
                	if(logon(aForm, req, errors)) {
                		setLayout(aForm, req);
                		destination=mapping.findForward(checkPassword(req));
                	}
                    break;

                case ACTION_LOGOFF:
                    AgnUtils.logger().info("execute: logoff");
                    logoff(aForm, req);
                    setLayout(aForm, req);
                    destination=mapping.findForward("view_logon");
                    aForm.setAction(LogonAction.ACTION_LOGON);
                    break;

                case ACTION_PASSWORD_CHANGE_REQ:
                	aForm.setAction(ACTION_PASSWORD_CHANGE);
                	destination=mapping.findForward("change_password");
                	break;

                case ACTION_PASSWORD_CHANGE:
                	if(changePassword(aForm, req, errors)) {
                		destination=mapping.findForward("change_password_success");
                	} else {
                		aForm.setAction(ACTION_PASSWORD_CHANGE);
                		saveErrors(req, errors);
                		return mapping.findForward("change_password");
                	}
                	break;

                case ACTION_FORWARD:
                	destination = mapping.findForward("motd");
                	break;

                case ACTION_LAYOUT:
                	setLayout(aForm, req);
                	aForm.setAction(LogonAction.ACTION_LOGON);
                    destination=mapping.findForward("view_logon");
                	break;

                default:
                    AgnUtils.logger().debug("execute: default");
                    setLayout(aForm, req);
                    aForm.setAction(LogonAction.ACTION_LOGON);
                    destination=mapping.findForward("view_logon");
            }

        } catch (Exception e) {
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

	boolean	checkSecurity(String pwd, ActionMessages errors) {
		// TODO: check for length, digits, chars & special chars:

		if(!pwd.matches(".*\\p{Alpha}.*")) {
			errors.add("password", new ActionMessage("error.password_no_letters"));
			return false;
		}
		if(!pwd.matches(".*\\p{Digit}.*")) {
			errors.add("password", new ActionMessage("error.password_no_digits"));
			return false;
		}
		// if(!pwd.matches(".*[,.-;:_#+*!=].*")) {
		if(!pwd.matches(".*\\p{Punct}.*")) {
			errors.add("password", new ActionMessage("error.password_no_special_chars"));
			return false;
		}
		return true;
	}

	protected boolean changePassword(LogonForm aForm, HttpServletRequest req, ActionMessages errors) {
        	HttpSession	session=req.getSession();
		Admin		admin=(Admin) session.getAttribute("emm.admin");

		if(admin == null) {
			return false;
		}

		if(!aForm.getPassword_new1().equals(aForm.getPassword())) {
			errors.add("password", new ActionMessage("error.password.mismatch"));
			return false;
		}
		if(checkSecurity(aForm.getPassword_new1(),errors)) {
			AdminDao	dao=(AdminDao) getBean("AdminDao");

			admin.setPassword(aForm.getPassword_new1());
			admin.setLastPasswordChange(new Date());

			AdminGroupDao groupDao=(AdminGroupDao) getBean("AdminGroupDao");
	        AdminGroup group=(AdminGroup) groupDao.getAdminGroup(admin.getGroup().getGroupID());
	        admin.setGroup(group);
			dao.save(admin);
		} else {
			System.out.println("password problem");
			return false;
		}
		return true;
	}

	protected String	checkPassword(HttpServletRequest req)	{
        	HttpSession	session=req.getSession();
		Admin		admin=(Admin) session.getAttribute("emm.admin");
		Date		lastChange=admin.getLastPasswordChange();
		Calendar	expire=new GregorianCalendar();
		int			days=-1;

		try	{
			days=Integer.parseInt(AgnUtils.getDefaultValue("password.expire.days"));
		} catch(Exception e) {
			days=-1;
		}

		/* No expire set, so don't request password change. */
		if(days <= 0) {
			return "success";
		}

		expire.add(Calendar.DAY_OF_MONTH, -days);
		if(lastChange.before(expire.getTime())) {
			return "suggest_password_change";
		}
		return "success";
	}

	private void checkForUpdate(HttpServletRequest request) {
		if(AgnUtils.isMySQLDB()) {
			try{
				StringBuffer referrer = request.getRequestURL();
				VersionControlService vcService = ( VersionControlService ) getBean( "versionControlService" );
				VersionObject latestVersion = vcService.getLatestVersion( AgnUtils.getCurrentVersion(), referrer != null ? referrer.toString() : "" );

				request.setAttribute( "latestVersion", latestVersion );
			}
			catch ( Exception ex ) {
				AgnUtils.logger().error( "Error while retrieving latest version", ex );
			}
		}
	}

	/**
	 * Loads special layout for given design details
	 */
	private void setLayout(LogonForm aForm, HttpServletRequest req) {
		HttpSession session=req.getSession();
		EmmLayoutBaseDao layoutDao=(EmmLayoutBaseDao) getBean("EmmLayoutBaseDao");
		int companyID = 0;
		int layoutID = 0;
		if(aForm.getDesign() != null && !aForm.getDesign().isEmpty()) {
			layoutID = AgnUtils.decryptLayoutID(aForm.getDesign());
			companyID = AgnUtils.decryptCompanyID(aForm.getDesign());
			session.setAttribute("emmLayoutBase", layoutDao.getEmmLayoutBase(companyID, layoutID));
		} else {
			Object layout = session.getAttribute("emmLayoutBase");
			if (layout == null) {
				session.setAttribute("emmLayoutBase", layoutDao.getEmmLayoutBase(companyID, layoutID));
			}
		}

	}

	/**
	 * Tries to logon a user.
	 */
	protected boolean	logon(LogonForm aForm, HttpServletRequest req, ActionMessages errors) {
		HttpSession session=req.getSession();
		AdminDao adminDao=(AdminDao) getBean("AdminDao");
		EmmLayoutBaseDao layoutDao=(EmmLayoutBaseDao) getBean("EmmLayoutBaseDao");
		Admin aAdmin=adminDao.getAdminByLogin(aForm.getUsername(), aForm.getPassword());
		LoginTrackDao loginTrackDao = (LoginTrackDao) getBean("LoginTrackDao");

		if(aAdmin!=null) {
			if (isIPLogonBlocked(req, aAdmin)) {
				AgnUtils.logger().warn("logon: login FAILED (IP " + req.getRemoteAddr() + " blocked) User: " + aForm.getUsername() + " Password-Length: " + aForm.getPassword().length());
				errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("error.login"));

				loginTrackDao.trackLoginDuringBlock(req.getRemoteAddr(), aForm.getUsername());

				return false;
			} else {
				session.setAttribute("emm.admin", aAdmin);
				session.setAttribute("emmLayoutBase", layoutDao.getEmmLayoutBase(aForm.getCompanyID(req), aAdmin.getLayoutBaseID()));
				session.setAttribute("emm.locale", aAdmin.getLocale());
				session.setAttribute(org.apache.struts.Globals.LOCALE_KEY, aAdmin.getLocale());
				String helplanguage = getHelpLanguage(req);
			    session.setAttribute("helplanguage", helplanguage) ;
			     AgnUtils.userlogger().info(aAdmin.getUsername()+": do login");
			    loginTrackDao.trackSuccessfulLogin(req.getRemoteAddr(), aForm.getUsername());

				return true;
			}
		} else {
			AgnUtils.logger().warn("logon: login FAILED User: " + aForm.getUsername() + " Password-Length: " + aForm.getPassword().length());
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("error.login"));

			loginTrackDao.trackFailedLogin(req.getRemoteAddr(), aForm.getUsername());

			return false;
		}
	}

    /**
     * Logs off a user
     */
    protected void logoff(LogonForm aForm, HttpServletRequest req) {
    	AgnUtils.logger().info("logoff: logout "+aForm.getUsername()+"!");
        AgnUtils.userlogger().info((AgnUtils.getAdmin(req) == null ? aForm.getUsername() : AgnUtils.getAdmin(req).getUsername()) + ": do logout");
        req.getSession().removeAttribute("emm.admin");
        req.getSession().invalidate();
    }

    /**
     * Determines how long the current IP address is blocked.
     * @param request Servlet request with IP address
     * @return true, if IP is temporarily blocked, otherwise false
     */
    protected boolean isIPLogonBlocked(HttpServletRequest request, Admin admin) {
    	LoginTrackDao loginTrackDao = (LoginTrackDao) getBean("LoginTrackDao");
    	CompanyDao companyDao = (CompanyDao) getBean("CompanyDao");

    	if (loginTrackDao != null) {
    		FailedLoginData data = loginTrackDao.getFailedLoginData(request.getRemoteAddr());
    		Company company = companyDao.getCompany(admin.getCompanyID());

    		if( data != null && company != null) {
	    		if (data.getNumFailedLogins() > company.getMaxLoginFails()) {
	    			return data.getLastFailedLoginTimeDifference() < company.getLoginBlockTime();
	    		} else {
	    			return false;
	    		}
    		} else {
    			return false; // No data found, IP not blocked
    		}
    	} else {
    		// No bean instance, no check!
    		return false;
    	}
    }
}
