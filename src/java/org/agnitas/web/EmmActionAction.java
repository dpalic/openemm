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

import org.agnitas.actions.EmmAction;
import org.agnitas.beans.factory.ActionOperationFactory;
import org.agnitas.beans.factory.EmmActionFactory;
import org.agnitas.dao.CampaignDao;
import org.agnitas.dao.EmmActionDao;
import org.agnitas.dao.MailingDao;
import org.agnitas.emm.core.action.operations.AbstractActionOperation;
import org.agnitas.emm.core.action.operations.ActionOperationExecuteScript;
import org.agnitas.emm.core.action.service.EmmActionService;
import org.agnitas.emm.core.action.service.UnableConvertException;
import org.agnitas.emm.core.commons.util.ConfigService;
import org.agnitas.emm.core.velocity.scriptvalidator.IllegalVelocityDirectiveException;
import org.agnitas.emm.core.velocity.scriptvalidator.ScriptValidationException;
import org.agnitas.emm.core.velocity.scriptvalidator.VelocityDirectiveScriptValidator;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.forms.EmmActionForm;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Implementation of <strong>Action</strong> that handles Targets
 *
 * @author Martin Helff
 */

public class EmmActionAction extends StrutsActionBase {
	
	/** The logger. */
	private static final transient Logger logger = Logger.getLogger(EmmActionAction.class);
    
    public static final int ACTION_LIST = 1;
    public static final int ACTION_VIEW = 2;
    public static final int ACTION_SAVE = 4;
    public static final int ACTION_NEW = 6;
    public static final int ACTION_DELETE = 7;
    public static final int ACTION_CONFIRM_DELETE = 8;
    public static final int ACTION_ADD_MODULE = 9;

    private CampaignDao campaignDao;
    private EmmActionDao emmActionDao;
    private DataSource dataSource;
    private EmmActionFactory emmActionFactory;
    private ActionOperationFactory actionOperationFactory;
    private MailingDao mailingDao;
    protected VelocityDirectiveScriptValidator velocityDirectiveScriptValidator;
    private EmmActionService emmActionService;
    
	protected ConfigService configService;

	@Required
	public void setConfigService(ConfigService configService) {
		this.configService = configService;
	}
   
    // --------------------------------------------------------- Public Methods
    
    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     * <br>
	 * ACTION_LIST: initializes columns width list if necessary, forwards to emm action list page.
	 * <br><br>
	 * ACTION_SAVE: saves emm action data in database; sets new emm action id in form field; forwards
     *     to emm action view page.
	 * <br><br>
     * ACTION_VIEW: loads data of chosen emm action into form, forwards to emm action view page
     * <br><br>
     * ACTION_ADD_MODULE: adds new action operation module to the given emm action, forwards to emm action view page.
     * <br><br>
	 * ACTION_CONFIRM_DELETE: loads data of chosen emm action into form, forwards to jsp with question to confirm deletion
	 * <br><br>
	 * ACTION_DELETE: deletes the entry of certain emm action, forwards to emm action list page
	 * <br><br>
	 * Any other ACTION_* would cause a forward to "list"
     * <br><br>
     * If the forward is "list" - loads list of emm-actions to request; also loads list of campaigns and list of
     * sent actionbased-mailings (sets that to form)
     *
     * @param form ActionForm object, data for the action filled by the jsp
     * @param req  HTTP request
     * @param res HTTP response
     * @param mapping The ActionMapping used to select this instance
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     * @return destination specified in struts-config.xml to forward to next jsp
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
    	
        EmmActionForm aForm=null;
        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();
        ActionForward destination=null;
        
        if(!AgnUtils.isUserLoggedIn(req)) {
            return mapping.findForward("logon");
        }
        
        aForm=(EmmActionForm)form;
        
        req.setAttribute("oplist", getActionOperations(req));
        
		if (logger.isInfoEnabled()) logger.info("Action: " + aForm.getAction());
        try {
            switch(aForm.getAction()) {
                case EmmActionAction.ACTION_LIST:
                    if(allowed("actions.show", req)) {
                    	//loadActionUsed(aForm, req);
						if ( aForm.getColumnwidthsList() == null) {
                    		aForm.setColumnwidthsList(getInitializedColumnWidthList(4));
                    	}
                        destination=mapping.findForward("list");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
                case EmmActionAction.ACTION_VIEW:
                    if(allowed("actions.show", req)) {
                        if(aForm.getActionID()!=0) {
                            aForm.setAction(EmmActionAction.ACTION_SAVE);
                            loadAction(aForm, req);
                        } else {
                            aForm.setAction(EmmActionAction.ACTION_SAVE);
                        }
                        
                        // Some deserialized Actions need the mailings to show their configuration data
                        aForm.setMailings(mailingDao.getMailingsByStatusE(AgnUtils.getCompanyID(req)));
                        
                        destination=mapping.findForward("success");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
                case EmmActionAction.ACTION_SAVE:
                    if(allowed("actions.change", req)) {
                    	try {
	                        saveAction(aForm, req);
	
	                    	// Show "changes saved", only if we didn't request a module to be removed
	                        if(req.getParameter("deleteModule") == null) {
	                        	messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
	                        }
                    	} catch( IllegalVelocityDirectiveException e) {
                    		errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage( "error.action.illegal_directive", e.getDirective()));
                    	}
                        destination=mapping.findForward("success");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
                case EmmActionAction.ACTION_CONFIRM_DELETE:
                    loadAction(aForm, req);
                    destination=mapping.findForward("delete");
                    aForm.setAction(EmmActionAction.ACTION_DELETE);
                    break;
                case EmmActionAction.ACTION_DELETE:
                    if(allowed("actions.delete", req)) {
                        deleteAction(aForm, req);

                        // Show "changes saved"
                        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
                        aForm.setAction(EmmActionAction.ACTION_LIST);
                        destination=mapping.findForward("list");
                    } else {	
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
                case EmmActionAction.ACTION_ADD_MODULE:
                	AbstractActionOperation aMod = actionOperationFactory.newActionOperation(aForm.getNewModule());
                    List<AbstractActionOperation> actions = aForm.getActions();
                    if(actions == null) {
                        actions = new ArrayList<AbstractActionOperation>();
                        aForm.setActions(actions);
                    }
                    actions.add(aMod);
                    aForm.setAction(EmmActionAction.ACTION_SAVE);
                    destination=mapping.findForward("success");
                    break;
                default:
                    if(allowed("actions.show", req)) {
						if ( aForm.getColumnwidthsList() == null) {
                    		aForm.setColumnwidthsList(getInitializedColumnWidthList(4));
                    	}
                        destination=mapping.findForward("list");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
            }
        } catch (UnableConvertException e) {
        	logger.warn("Attempt to edit old action without converter", e);
        	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception.actop.convert"));
        } catch (Exception e) {
			logger.error("execute", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception", configService.getValue(ConfigService.Value.SupportEmergencyUrl)));
        }
        
        if(destination != null && "list".equals(destination.getName())) {
        	try {
				req.setAttribute("emmactionList", getActionList(req));
                if (!aForm.isNumberOfRowsChanged()) {
                    setNumberOfRows(req, aForm);
                }
                aForm.setNumberOfRowsChanged(false);
                aForm.setCampaigns(campaignDao.getCampaignList(AgnUtils.getCompanyID(req),"lower(shortname)",1));
                aForm.setMailings(mailingDao.getMailingsByStatusE(AgnUtils.getCompanyID(req)));
			} catch (Exception e) {
				logger.error("getActionList", e);
	            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception", configService.getValue(ConfigService.Value.SupportEmergencyUrl)));
			}
        }
        
        
        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(req, errors);
            return (new ActionForward(mapping.getInput()));
        }

        // Report any message (non-errors) we have discovered
	     if (!messages.isEmpty()) {
	     	saveMessages(req, messages);
	     }

        return destination;
    }

    /**
     * Loads an emm action data from DB into the form.
     *
     * @param aForm EmmActionForm object
     * @param req  HTTP request
     * @throws Exception
     */
    protected void loadAction(EmmActionForm aForm, HttpServletRequest req) throws Exception {
        EmmAction aAction = emmActionService.getEmmAction(aForm.getActionID(), AgnUtils.getCompanyID(req));
        
        if(aAction!=null && aAction.getId()!=0) {
            aForm.setShortname(aAction.getShortname());
            aForm.setDescription(aAction.getDescription());
            aForm.setType(aAction.getType());
            aForm.setActions(aAction.getActionOperations());
            if (logger.isInfoEnabled()) logger.info("loadAction: action "+aForm.getActionID()+" loaded");
            writeUserActivityLog(AgnUtils.getAdmin(req), "do load action", aForm.getShortname());
        } else {
			logger.warn("loadAction: could not load action " + aForm.getActionID());
        }
    }

    /**
     * Saves emm action data in database; resets emm action id in the form.
     *
     * @param aForm EmmActionForm object
     * @param req HTTP request
     * @throws Exception
     */
    protected void saveAction(EmmActionForm aForm, HttpServletRequest req) throws Exception {
    	checkScriptActions(aForm);
    	
        EmmAction aAction = emmActionFactory.newEmmAction();

        int companyId = AgnUtils.getCompanyID(req);
        aAction.setCompanyID(companyId);
        aAction.setId(aForm.getActionID());
        aAction.setType(aForm.getType());
        aAction.setShortname(aForm.getShortname());
        aAction.setDescription(aForm.getDescription());
        List<AbstractActionOperation> operations = aForm.getActions();
        if (operations == null) {
        	operations = new ArrayList<AbstractActionOperation>();
        }
        for (AbstractActionOperation operation : operations) {
			operation.setCompanyId(companyId);
		}
        aAction.setActionOperations(operations);

        final int newEmmActionId = emmActionService.saveEmmAction(aAction);
        if (aForm.getActionID() == 0) {
            writeUserActivityLog(AgnUtils.getAdmin(req), "create action", aForm.getShortname());
        } else {
            writeUserActivityLog(AgnUtils.getAdmin(req), "edit action", aForm.getShortname());
        }
        aForm.setActionID(newEmmActionId);
    }
    
    private void checkScriptActions( EmmActionForm form) throws ScriptValidationException {
    	List<AbstractActionOperation> list =  form.getActions();
    	
    	if( list != null) {
	    	for( Object action : list) {
	    		if( action instanceof ActionOperationExecuteScript) {
	    			ActionOperationExecuteScript scriptAction = (ActionOperationExecuteScript) action;
	    			
	    			this.velocityDirectiveScriptValidator.validateScript( scriptAction.getScript());
	    		}
	    	}
    	}
    }
    
    /**
     * Deletes an action.
     *
     * @param aForm EmmActionForm object
     * @param req HTTP request
     */
    protected void deleteAction(EmmActionForm aForm, HttpServletRequest req) {
    	emmActionService.deleteEmmAction(aForm.getActionID(), AgnUtils.getCompanyID(req));
        writeUserActivityLog(AgnUtils.getAdmin(req), "delete action", aForm.getShortname());
    }

    /**
     * Gets action operations map.
     *
     * @param req  HTTP request
     * @return Map object contains emm action operations
     */
    protected Map<String, String> getActionOperations(HttpServletRequest req) {
		Map<String, String> mapMessageKeyToActionClass = new TreeMap<String, String>();
		String[] names = actionOperationFactory.getTypes();
		for (String name : names) {
			String key = "action.op." + name;
			if (allowed(key, req)) {
				mapMessageKeyToActionClass.put(key, name);
			}
		}
		return mapMessageKeyToActionClass;
    }
    
    /**
     * @deprecated   is not in use yet
     */
    @Deprecated
    protected void loadActionUsed(EmmActionForm aForm, HttpServletRequest req) throws Exception {
    	Map<Integer, Integer> used = emmActionDao.loadUsed(AgnUtils.getCompanyID(req));
        aForm.setUsed(used);
    }

    public List<EmmAction> getActionList(HttpServletRequest request) throws IllegalAccessException, InstantiationException {
        return emmActionDao.getActionList(request);
    }

    public CampaignDao getCampaignDao() {
        return campaignDao;
    }

    public void setCampaignDao(CampaignDao campaignDao) {
        this.campaignDao = campaignDao;
    }

    public EmmActionDao getEmmActionDao() {
        return emmActionDao;
    }

    public void setEmmActionDao(EmmActionDao emmActionDao) {
        this.emmActionDao = emmActionDao;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public EmmActionFactory getEmmActionFactory() {
        return emmActionFactory;
    }

    public void setEmmActionFactory(EmmActionFactory emmActionFactory) {
        this.emmActionFactory = emmActionFactory;
    }

    public ActionOperationFactory getActionOperationFactory() {
        return actionOperationFactory;
    }

    public void setActionOperationFactory(ActionOperationFactory actionOperationFactory) {
        this.actionOperationFactory = actionOperationFactory;
    }

    public MailingDao getMailingDao() {
        return mailingDao;
    }

    public void setMailingDao(MailingDao mailingDao) {
        this.mailingDao = mailingDao;
    }
    
    public void setVelocityDirectiveScriptValidator( VelocityDirectiveScriptValidator validator) {
    	this.velocityDirectiveScriptValidator = validator;
    }

	public void setEmmActionService(EmmActionService emmActionService) {
		this.emmActionService = emmActionService;
	}
}