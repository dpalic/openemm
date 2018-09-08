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
 * the code written by AGNITAS AG are Copyright (c) 2014 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/

package org.agnitas.web;

import org.agnitas.beans.Admin;
import org.agnitas.beans.AdminGroup;
import org.agnitas.beans.Company;
import org.agnitas.beans.FailedLoginData;
import org.agnitas.beans.VersionObject;
import org.agnitas.dao.AdminDao;
import org.agnitas.dao.AdminGroupDao;
import org.agnitas.dao.AdminPreferencesDao;
import org.agnitas.dao.CompanyDao;
import org.agnitas.dao.DocMappingDao;
import org.agnitas.dao.EmmLayoutBaseDao;
import org.agnitas.dao.LoginTrackDao;
import org.agnitas.emm.core.commons.password.PasswordCheck;
import org.agnitas.emm.core.commons.password.PasswordCheckHandler;
import org.agnitas.emm.core.commons.password.StrutsPasswordCheckHandler;
import org.agnitas.emm.core.commons.util.ConfigService;
import org.agnitas.emm.core.logintracking.LoginTrackService;
import org.agnitas.emm.core.logintracking.LoginTrackServiceException;
import org.agnitas.service.VersionControlService;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.DbUtilities;
import org.agnitas.util.UserActivityLogActions;
import org.agnitas.web.forms.LogonForm;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import java.io.IOException;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * Implementation of <strong>Action</strong> that validates a user logon.
 *
 * @author Martin Helff
 */

public class LogonAction extends StrutsActionBase {
	
	/** The logger. */
	private static final transient Logger logger = Logger.getLogger( LogonAction.class);
	
	/** Name of the attribute containing the number of failed logins. */
	public static final String FAILED_LOGINS_ATTRIBUTE_NAME = "failed_logins";

    public static final int ACTION_LOGON = 1;
    public static final int ACTION_LOGOFF = 2;

	protected AdminDao adminDao;
	protected AdminGroupDao adminGroupDao;
	protected CompanyDao companyDao;
	protected EmmLayoutBaseDao emmLayoutBaseDao;
    protected AdminPreferencesDao adminPreferencesDao;
	protected VersionControlService versionControlService;
	protected LoginTrackDao loginTrackDao;
	protected LoginTrackService loginTrackService;
	protected DocMappingDao docMappingDao;
	protected ConfigService configService;
    private DataSource dataSource;
	
	/** Password checker and error reporter. */
	private PasswordCheck passwordCheck;

	@Required
	public void setConfigService(ConfigService configService) {
		this.configService = configService;
	}

	@Required
	public void setAdminDao(AdminDao adminDao) {
		this.adminDao = adminDao;
	}
	
	@Required
	public void setAdminGroupDao(AdminGroupDao adminGroupDao) {
		this.adminGroupDao = adminGroupDao;
	}
	
	@Required
	public void setCompanyDao(CompanyDao companyDao) {
		this.companyDao = companyDao;
	}
	
	@Required
	public void setEmmLayoutBaseDao(EmmLayoutBaseDao emmLayoutBaseDao) {
		this.emmLayoutBaseDao = emmLayoutBaseDao;
	}

    @Required
    public void setAdminPreferencesDao(AdminPreferencesDao adminPreferencesDao) {
        this.adminPreferencesDao = adminPreferencesDao;
    }

    public void setVersionControlService(VersionControlService versionControlService) {
		this.versionControlService = versionControlService;
	}
	
	@Required
	public void setLoginTrackDao(LoginTrackDao loginTrackDao) {
		this.loginTrackDao = loginTrackDao;
	}
	
	@Required
	public void setLoginTrackService(LoginTrackService loginTrackService) {
		this.loginTrackService = loginTrackService;
	}
	
	@Required
	public void setDocMappingDao(DocMappingDao docMappingDao) {
		this.docMappingDao = docMappingDao;
	}
	
	public void setPasswordCheck(PasswordCheck check) {
		this.passwordCheck = check;
	}

	@Required
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // --------------------------------------------------------- Public Methods


    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     *
     * <br>
	 * ACTION_LOGON: tries to logon a user. If user with passed name and password doesn't exists or user is blocked forwards to logon page again with error messages.<br>
     * Also checks if user password is expired ("password.expire.days"). If true forwards to "suggest_password_change".
     * Otherwise loads user settings into the session (language, layout settings) and forwards to "success".
     * <br><br>
     * ACTION_LOGOFF: logs off a user, loads default layout settings and forwards to "logged_out".
     * <br><br>
     * ACTION_PASSWORD_CHANGE_REQ: only forwards to jsp for changing user password.
     * <br><br>
     * ACTION_PASSWORD_CHANGE: tries to save a new password. If new password is invalid forwards to jsp for changing user password again and shows corresponding error messages. <br>
     *     Otherwise to "change_password_success".
     * <br><br>
     * ACTION_FORWARD: only forwards to jsp with message of the day for current user.
     * <br><br>
     * ACTION_LAYOUT: loads layout settings and forwards to logon page.
     * <br><br>
	 * Any other ACTION_* would loads default layout settings and forwards to logon page.
	 * <br>
	 * @param form data for the action filled by the jsp
	 * @param req request from jsp
	 * @param res response
     * @param mapping The ActionMapping used to select this instance
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     * @return destination specified in struts-config.xml to forward to next jsp
     */
    @Override
    public ActionForward execute(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest req,
            HttpServletResponse res)
            throws IOException, ServletException {
    	
		// Check datasource connection
		try {
			DbUtilities.checkDatasourceConnection(dataSource);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			res.setContentType("text/plain");
			res.getOutputStream().write(("DB Error: \n" + e.getMessage()).getBytes("UTF-8"));
			return null;
		}

        // Validate the request parameters specified by the user
        ActionMessages errors = new ActionMessages();
        LogonForm aForm=(LogonForm)form;
        ActionForward destination=null;

		checkForUpdate(req);

        try {
        	if( logger.isInfoEnabled()) {
        		logger.info("execute: action " + aForm.getAction());
        	}
        	
            switch(aForm.getAction()) {
                case ACTION_LOGON:
                	if(logon(aForm, req, errors)) {
                		setLayout(aForm, req);
                		destination=mapping.findForward(checkPassword(req));
                	}
                    break;

                case ACTION_LOGOFF:
                	if( logger.isInfoEnabled()) {
                		logger.info("execute: logoff");
                	}
                	
                    logoff(aForm, req);
                    setLayout(aForm, req);
                    destination=mapping.findForward("logged_out");
                    aForm.setAction(LogonAction.ACTION_LOGON);
                    break;

                default:
                	if( logger.isInfoEnabled()) {
                		logger.info("execute: default");
                	}
                	
                    setLayout(aForm, req);
                    aForm.setAction(LogonAction.ACTION_LOGON);
                    destination=mapping.findForward("view_logon");
            }

        } catch (Exception e) {
            logger.error( "Error", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception", configService.getValue(ConfigService.Value.SupportEmergencyUrl)));
        }

        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {

            saveErrors(req, errors);
            return new ActionForward(mapping.getInput());
        }
        return destination;
    }

    /**
     * Checks is password is secured.
     *
     * @param pwd password to check
     * @param errors ActionMessages for errors
     * @param admin {@link Admin}
     * 
     * @return true if password is secured, otherwise false
     */
	boolean	checkSecurity(String pwd, ActionMessages errors, Admin admin) {
		PasswordCheckHandler handler = new StrutsPasswordCheckHandler(errors, "password");
		
		return this.passwordCheck.checkAdminPassword(pwd, admin, handler);
	}

    /**
     * Validates new password and if no errors found updates password for current user.
     *
     * @param aForm LogonForm
     * @param req request
     * @param errors ActionMessages for errors
     * @return true on success, otherwise false
     */
	protected boolean changePassword(LogonForm aForm, HttpServletRequest req, ActionMessages errors) {
		Admin admin = AgnUtils.getAdmin(req);

		if (admin == null) {
			return false;
		}

		if(!aForm.getPassword_new1().equals(aForm.getPassword())) {
			errors.add("password", new ActionMessage("error.password.mismatch"));
			return false;
		}
		if(checkSecurity(aForm.getPassword_new1(),errors, admin)) {
			admin.setPassword(aForm.getPassword_new1());
			admin.setLastPasswordChange(new Date());

	        AdminGroup group=adminGroupDao.getAdminGroup(admin.getGroup().getGroupID());
	        admin.setGroup(group);
			adminDao.save(admin);
		} else {
			logger.warn("password problem");
			
			return false;
		}
		return true;
	}

    /**
     * Checks password expiration for current logged in user.
     *
     * @param req request
     * @return forward name
     */
	protected String	checkPassword(HttpServletRequest req)	{
		return "success";
	}

	private void checkForUpdate(HttpServletRequest request) {
		try{
			StringBuffer referrer = request.getRequestURL();
			VersionObject latestVersion = versionControlService.getLatestVersion( AgnUtils.getCurrentVersion(), referrer != null ? referrer.toString() : "" );

			request.setAttribute( "latestVersion", latestVersion );
		}
		catch ( Exception ex ) {
			logger.error( "Error while retrieving latest version", ex );
		}
	}

	/**
	 * Loads special layout for given design details
	 */
	private void setLayout(LogonForm aForm, HttpServletRequest req) {
		HttpSession session=req.getSession();
		int companyID = 0;
		int layoutID = 0;
		if(aForm.getDesign() != null && !aForm.getDesign().isEmpty()) {
			layoutID = AgnUtils.decryptLayoutID(aForm.getDesign());
			companyID = AgnUtils.decryptCompanyID(aForm.getDesign());
			session.setAttribute("emmLayoutBase", emmLayoutBaseDao.getEmmLayoutBase(companyID, layoutID));
		} else {
			Object layout = session.getAttribute("emmLayoutBase");
			if (layout == null) {
				session.setAttribute("emmLayoutBase", emmLayoutBaseDao.getEmmLayoutBase(companyID, layoutID));
			}
		}

	}

	/**
	 * Tries to logon a user.
	 */
	protected boolean	logon(LogonForm aForm, HttpServletRequest req, ActionMessages errors) {
		HttpSession session=req.getSession();
		Admin aAdmin=adminDao.getAdminByLogin(aForm.getUsername(), aForm.getPassword());

		if(aAdmin!=null) {
			if (isIPLogonBlocked(req, aAdmin)) {
				logger.warn("logon: login FAILED (IP " + req.getRemoteAddr() + " blocked) User: " + aForm.getUsername() + " Password-Length: " + aForm.getPassword().length());
				errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("error.login"));

				loginTrackDao.trackLoginDuringBlock(req.getRemoteAddr(), aForm.getUsername());

				return false;
			} else {
				req.getSession().invalidate();
				session = req.getSession();
				try {
					session.setAttribute( FAILED_LOGINS_ATTRIBUTE_NAME, loginTrackService.getNumFailedLoginsSinceLastSuccessful( aAdmin.getUsername(), false));	// Record for current login is written later => disable skipping of current login
				} catch( LoginTrackServiceException e) {
					logger.warn( "Error counting number of failed logins", e);
				}
				session.setAttribute("emm.admin", aAdmin);
				session.setAttribute("emm.adminPreferences", adminPreferencesDao.getAdminPreferences(aAdmin.getAdminID()));
				session.setAttribute("emmLayoutBase", emmLayoutBaseDao.getEmmLayoutBase(aForm.getCompanyID(req), aAdmin.getLayoutBaseID()));
				session.setAttribute("emm.locale", aAdmin.getLocale());
				session.setAttribute(org.apache.struts.Globals.LOCALE_KEY, aAdmin.getLocale());
				String helplanguage = getHelpLanguage(req);
			    session.setAttribute("helplanguage", helplanguage) ;
			    writeUserActivityLog(aAdmin, UserActivityLogActions.LOGIN_LOGOUT.getLocalValue(), "Log in");
			    loginTrackDao.trackSuccessfulLogin(req.getRemoteAddr(), aForm.getUsername());
                session.setAttribute("docMapping",docMappingDao.getDocMapping());

                //set admin's user name to the session and use this data from template
                String userName = AgnUtils.getAdmin(req).getFullname();
                if (StringUtils.isEmpty(userName)) {
                    userName = AgnUtils.getAdmin(req).getUsername();
                }
                session.setAttribute("userName", userName);

				return true;
			}
		} else {
			logger.warn("logon: login FAILED User: " + aForm.getUsername() + " Password-Length: " + aForm.getPassword().length());
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("error.login"));

			loginTrackDao.trackFailedLogin(req.getRemoteAddr(), aForm.getUsername());

			return false;
		}
	}
    
    /**
     * Gets the language which will be used for the online help. Method gets the list of available languages for help
     * and checks if admin language is contained in that list. If yes - returns that language, if not - returns "en"
     * (english language)
     *
     * @param req servlet request object
     * @return help language String value
     */
    protected String getHelpLanguage(HttpServletRequest req) {
		String helplanguage = "en";
        String availableHelpLanguages = (String) WebApplicationContextUtils.getRequiredWebApplicationContext(req.getSession().getServletContext()).getBean("onlinehelp.languages");
        
        if( availableHelpLanguages != null ) {
        	Admin admin = AgnUtils.getAdmin(req);
        	StringTokenizer tokenizer = new StringTokenizer(availableHelpLanguages,",");
        	while (tokenizer.hasMoreTokens() ) {
        		String token = tokenizer.nextToken();
        		if( token.trim().equalsIgnoreCase( admin.getAdminLang()) ) {
        			helplanguage = token.toLowerCase();
        			break;
        		}        		
        	}
        }
		return helplanguage;
	}

    /**
     * Logs off a user
     */
    protected void logoff(LogonForm aForm, HttpServletRequest req) {
    	if( logger.isInfoEnabled()) {
    		logger.info("logoff: logout "+aForm.getUsername()+"!");
    	}
    	
        // writeUserActivityLog((AgnUtils.getAdmin(req) == null ? aForm.getUsername() : AgnUtils.getAdmin(req).getUsername()), UserActivityLogActions.LOGIN_LOGOUT.getLocalValue(), "log out");
        writeUserActivityLog(AgnUtils.getAdmin(req), UserActivityLogActions.LOGIN_LOGOUT.getLocalValue(), "Log out");

        req.getSession().removeAttribute("emm.admin");
        req.getSession().removeAttribute("emm.adminPreferences");
        req.getSession().invalidate();
    }

    /**
     * Determines how long the current IP address is blocked.
     * @param request Servlet request with IP address
     * @return true, if IP is temporarily blocked, otherwise false
     */
    protected boolean isIPLogonBlocked(HttpServletRequest request, Admin admin) {
    	// TODO: The code has moved (in a more generalized form) to LoginTrackServiceImpl 
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
