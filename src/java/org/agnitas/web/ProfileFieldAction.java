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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.beans.Admin;
import org.agnitas.beans.ProfileField;
import org.agnitas.beans.factory.ProfileFieldFactory;
import org.agnitas.dao.ProfileFieldDao;
import org.agnitas.dao.TargetDao;
import org.agnitas.emm.core.commons.util.ConfigService;
import org.agnitas.service.ColumnInfoService;
import org.agnitas.target.Target;
import org.agnitas.target.TargetNode;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.DbUtilities;
import org.agnitas.util.KeywordList;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.springframework.beans.factory.annotation.Required;

/**
 * Handles all actions on profile fields.
 */
public class ProfileFieldAction extends StrutsActionBase {
	private static final transient Logger logger = Logger.getLogger(ProfileFieldAction.class);

    // --------------------------------------------------------- Dependency Injection code
    protected ProfileFieldFactory profileFieldFactory;
    protected ColumnInfoService columnInfoService;
    protected KeywordList databaseKeywordList;
    protected ProfileFieldDao profileFieldDao;
    protected TargetDao targetDao;
    protected ExecutorService workerExecutorService;
	protected ConfigService configService;

	@Required
	public void setConfigService(ConfigService configService) {
		this.configService = configService;
	}

    public void setProfileFieldFactory(ProfileFieldFactory factory) {
    	this.profileFieldFactory = factory;
    }

    public void setColumnInfoService(ColumnInfoService service) {
    	this.columnInfoService = service;
    }

    public void setDatabaseKeywordList(KeywordList keywordList) {
    	this.databaseKeywordList = keywordList;
    }

    public void setProfileFieldDao(ProfileFieldDao profileFieldDao) {
    	this.profileFieldDao = profileFieldDao;
    }

    public void setTargetDao(TargetDao targetDao) {
    	this.targetDao = targetDao;
    }

    public void setWorkerExecutorService(ExecutorService executorService) {
    	this.workerExecutorService = executorService;
    }

    // --------------------------------------------------------- Public Methods
    // bounceload = DB field for AGNEMM-1817, AGNEMM-1924 and AGNEMM-1925 
    private String[] HIDDEN_COLUMNS = {"change_date", "creation_date", "title", "datasource_id", "email", "firstname", "lastname", "gender", "mailtype", "customer_id", "timestamp", "bounceload"};

    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     *
     * ACTION_LIST: Forwards to list page.
     * <br><br>
     * ACTION_VIEW: if request parameter "fieldname" is set - loads field data into form, otherwise - sets default
     *     field type to double; forwards to profile field view page.
     * <br><br>
     * ACTION_SAVE: if reserved word is used for field name or if the error occurred during save - adds error and
     *     forwards to view page. If everything is ok - updates existing field in database and forwards to list page;
     * <br><br>
     * ACTION_NEW: if there is parameter "save" in request and new field name is not a reserved word and the
     *     shortname or fieldname is not duplicated - creates new profile field in database and forwards to list page.
     *     If there some error occurred - forwards to view page. If the reserved word is used for field name - forwards
     *     to list page.
     * <br><br>
     * ACTION_CONFIRM_DELETE: loads profile field data into form. Checks if the field is used by any target group:
     *     if the fields is unused - forwards to delete-confirmation page, if the field is used - stores the targets
     *     names using this field to form and forwards to "delete_error" page. If there was an error during targets
     *     check - forwards to list page.
     * <br><br>
     * ACTION_DELETE: deletes the profile field from database, forwards to profile field list page.
     * <br><br>
     * Any other ACTION_* would cause a forward to "list"
     * <br><br>
     * If the destination is "list" - gets list of profile fields excluding hidden fields and sets that into request.
     *
     * @param form ActionForm object
     * @param req request
     * @param res response
     * @param mapping The ActionMapping used to select this instance
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     * @return destination
     */
    @Override
    public ActionForward execute(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest req,
            HttpServletResponse res)
            throws IOException, ServletException, Exception {

        // Validate the request parameters specified by the user
        ProfileFieldForm aForm = null;
        ActionMessages errors = new ActionErrors();
        ActionMessages messages = new ActionMessages();
        ActionForward destination = null;

        if (!AgnUtils.isUserLoggedIn(req)) {
            return mapping.findForward("logon");
        }

        aForm = (ProfileFieldForm)form;

        if (req.getParameter("delete") != null) {
            aForm.setAction(ACTION_CONFIRM_DELETE);
        }

        if (logger.isInfoEnabled()) logger.info("Action: "+aForm.getAction());

        try {
            switch(aForm.getAction()) {
                case ProfileFieldAction.ACTION_LIST:
                    if (allowed("profileField.show", req)) {
                        destination = mapping.findForward("list");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case ProfileFieldAction.ACTION_VIEW:
                    if (allowed("profileField.show", req)) {
                        if (req.getParameter("fieldname") != null) {
                            loadProfileField(aForm, req);
                            aForm.setAction(ProfileFieldAction.ACTION_SAVE);
                        } else {
                            aForm.setAction(ProfileFieldAction.ACTION_NEW);
                        	// For creating a new field set default values
                        	aForm.setFieldType("INTEGER");
                        }
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    destination = mapping.findForward("view");
                    break;

                case ProfileFieldAction.ACTION_SAVE:
                	if (req.getParameter("save") != null) {
                    	if (isReservedWord(aForm.getFieldname())) {
                			destination = mapping.findForward("view");
                			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.profiledb.invalid_fieldname", aForm.getFieldname()));
                    	} else if (changeProfileField(aForm, req, errors)){
	                        destination = mapping.findForward("list");

	                        // Show "changes saved"
	                        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("changes_saved"));
                		} else {
	                        // Show error
                			destination = mapping.findForward("view");
                			if (errors.isEmpty()) {
                				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.profiledb.invalid_fieldname", aForm.getFieldname()));
                			}
                		}
                    }
                    break;

                case ProfileFieldAction.ACTION_NEW:
                	if (allowed("profileField.show", req)) {
                        if (req.getParameter("save") != null) {
                    		if (!isReservedWord(aForm.getFieldname())) {
                        		if (newProfileField(aForm, req, errors)){
                        			aForm.setAction(ProfileFieldAction.ACTION_LIST);
                        			destination = mapping.findForward("list");

                        			// Show "changes saved"
                        			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("changes_saved"));
                        		} else {
                        			// error message: NewProfileDBFieldError:
                        			errors.add("NewProfileDB_Field", new ActionMessage("error.profiledb.insert_in_db_error"));
                        			destination = mapping.findForward("view");
                        		}
                    		} else {
                    			destination = mapping.findForward("view");
                    			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.profiledb.invalid_fieldname", aForm.getFieldname()));
                    		}
                        }
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case ProfileFieldAction.ACTION_CONFIRM_DELETE:
                    loadProfileField(aForm, req);
                    String tg_ret = checkForTargetGroups(aForm, req);

                    if (tg_ret.compareTo("ok") == 0 ) {
                        aForm.setAction(ProfileFieldAction.ACTION_DELETE);
                        destination = mapping.findForward("delete");
                    } else {
                        if (tg_ret.compareTo("error") != 0 ) {
                            aForm.setAction(ProfileFieldAction.ACTION_LIST);
                            aForm.setTargetsDependent(tg_ret);
                            destination = mapping.findForward("delete_error");
                        } else {
                            aForm.setAction(ProfileFieldAction.ACTION_LIST);
                            destination = mapping.findForward("list");

                        }
                    }
                    break;

                case ProfileFieldAction.ACTION_DELETE:
                    if (req.getParameter("kill") != null) {
                        aForm.setAction(ProfileFieldAction.ACTION_LIST);
                        destination = mapping.findForward("list");
                        
                        if (!deleteProfileField(aForm, req)) {
                        	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.profiledb.cannotDropColumn", aForm.getFieldname()));
                        } else {
	                        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
                        }
                    }
                    break;

                default:
                    aForm.setAction(ProfileFieldAction.ACTION_LIST);
                    if (allowed("profileField.show", req)) {
                        destination = mapping.findForward("list");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
            }

        } catch (Exception e) {
        	logger.error("execute: "+e, e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception", configService.getValue(ConfigService.Value.SupportEmergencyUrl)));
            throw new ServletException(e);
        }

        if (destination != null && "list".equals(destination.getName())) {
            prepareList(mapping, req, errors, destination, aForm);
        }

        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(req, errors);
        }

        // Report any message (non-errors) we have discovered
        if (!messages.isEmpty()) {
        	saveMessages(req, messages);
        }

        return destination;
    }

    /**
     * Loads a profile field.
     */
    protected void loadProfileField(ProfileFieldForm aForm, HttpServletRequest req) {
        int compID = AgnUtils.getCompanyID(req);
        ProfileField profileField = null;

        try {
        	profileField = columnInfoService.getColumnInfo(compID, aForm.getFieldname());
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
            return;
        }

        aForm.setCompanyID(compID);
        if (profileField != null) {
            aForm.setShortname(profileField.getShortname());
            aForm.setDescription(profileField.getDescription());
            aForm.setFieldType(profileField.getDataType());
            aForm.setFieldLength(profileField.getDataTypeLength());
            aForm.setFieldDefault(profileField.getDefaultValue());
            aForm.setFieldNull(profileField.getNullable());
        } else {
            aForm.setShortname("");
            aForm.setDescription("");
            aForm.setFieldType("");
            aForm.setFieldLength(0);
            aForm.setFieldDefault("");
            aForm.setFieldNull(true);
        }
    }

    /**
     * Creates a profile field.
     * @param errors
     */
    protected boolean newProfileField(ProfileFieldForm aForm, HttpServletRequest req, ActionMessages errors) throws Exception {
        Admin admin = AgnUtils.getAdmin(req);
        int companyID = AgnUtils.getCompanyID(req);
		String columnName = aForm.getFieldname();
		String shortName = aForm.getShortname();

    	// check if columnname or shortname is already in use
		if (profileFieldDao.getProfileFieldByShortname(companyID, shortName) != null ) {
			errors.add("settings.NewProfileDB_Field", new ActionMessage("error.profiledb.exists"));
			return false;
		} else  if (profileFieldDao.getProfileField(companyID, columnName) != null) {
			errors.add("settings.NewProfileDB_Field", new ActionMessage("error.profiledb.exists"));
			return false;
		} else if (!DbUtilities.checkAllowedDefaultValue(aForm.getFieldType(), aForm.getFieldDefault())) {
			errors.add("settings.NewProfileDB_Field", new ActionMessage("error.profiledb.invalidDefaultValue"));
			return false;
		}  else {
			ProfileField field = profileFieldFactory.newProfileField();
            field.setCompanyID(companyID);
            field.setColumn(columnName);
			field.setShortname(shortName);
	        field.setDescription(aForm.getDescription());
			field.setDataType(aForm.getFieldType());
			field.setDataTypeLength(aForm.getFieldLength());
	        field.setDefaultValue(aForm.getFieldDefault());
            writeUserActivityLog(admin, "create profile field " + shortName, "Profile field " + shortName + " created");
	        try {
	        	return profileFieldDao.saveProfileField(field);
			} catch (Exception e) {
				return false;
			}
		}
	}

    /**
     * Changes a profile field.
     * @param errors
     */
    protected boolean changeProfileField(ProfileFieldForm aForm, HttpServletRequest req, ActionMessages errors) throws Exception {
        Admin admin = AgnUtils.getAdmin(req);
        int companyID = AgnUtils.getCompanyID(req);
		String columnName = aForm.getFieldname();

    	// check if columnname exists
		ProfileField existingProfileField = profileFieldDao.getProfileField(companyID, columnName);
		if (existingProfileField == null) {
			errors.add("ChangeProfileDB_Field", new ActionMessage("error.profiledb.notExists"));
			return false;
		} else if (!DbUtilities.checkAllowedDefaultValue(existingProfileField.getDataType(), aForm.getFieldDefault())) {
			errors.add("ChangeProfileDB_Field", new ActionMessage("error.profiledb.invalidDefaultValue"));
			return false;
		} else {
            //Log changes
            writeOpenEmmChangeProfileFieldLog(admin, aForm, existingProfileField);
            //Apply changes
			existingProfileField.setDataType(aForm.getFieldType());
			existingProfileField.setDataTypeLength(aForm.getFieldLength());
			existingProfileField.setDescription(aForm.getDescription());
			existingProfileField.setShortname(aForm.getShortname());
			existingProfileField.setDefaultValue(aForm.getFieldDefault());
			try {
				profileFieldDao.saveProfileField(existingProfileField);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
	}

	/**
	 * Removes a profile field.
	 * @throws Exception
	 */
	protected boolean deleteProfileField(ProfileFieldForm aForm, HttpServletRequest req) throws Exception {
		try {
            Admin admin = AgnUtils.getAdmin(req);
            int companyId = AgnUtils.getCompanyID(req);
            profileFieldDao.removeProfileField(companyId, aForm.getFieldname());
            writeUserActivityLog(admin, "remove profile field " + aForm.getFieldname(), "Profile field " + aForm.getFieldname() + " removed");
			return true;
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
	}

    /**
     * Checks for target groups.
     */
    protected String checkForTargetGroups(ProfileFieldForm aForm, HttpServletRequest req) {
        int compID = AgnUtils.getCompanyID(req);
        String fieldname = aForm.getFieldname();
        StringBuilder ids = new StringBuilder();
        List<Target> targets = targetDao.getTargets(compID);
        List<String> targetNamesFound = new ArrayList<String>();

        for (Target aTarget : targets) {
            if (aTarget != null && aTarget.getTargetStructure() != null) {
                for (TargetNode aNode : aTarget.getTargetStructure().getAllNodes()) {
                    if (aNode.getPrimaryField().equals(fieldname)) {
                        if (!targetNamesFound.contains(aTarget.getTargetName())) {
                        	targetNamesFound.add(aTarget.getTargetName());
                            if (ids.length() > 0) {
                            	ids.append("<br>");
                            }
                            ids.append(aTarget.getTargetName());
                        }
                    }
                }
            }
        }

        if (ids.length() > 0) {
            return ids.toString();
        } else {
            return "ok";
        }
    }

    protected ActionForward prepareList(ActionMapping mapping, HttpServletRequest req, ActionMessages errors, ActionForward destination,ProfileFieldForm profileFieldForm) {
       try {
			List<ProfileField> profileFieldList = columnInfoService.getColumnInfos(AgnUtils.getCompanyID(req));
			List<ProfileField> columnInfoListFiltered = new ArrayList<ProfileField>();
        	for (ProfileField comProfileField : profileFieldList) {
        		boolean hideColumn = false;
        		for (String hiddenColumn : HIDDEN_COLUMNS) {
            		if (hiddenColumn.equalsIgnoreCase(comProfileField.getColumn())) {
            			hideColumn = true;
            		}
            	}

        		if (!hideColumn) {
        			columnInfoListFiltered.add(comProfileField);
        		}
            }
			req.setAttribute("columnInfo", columnInfoListFiltered);

       } catch (Exception e) {
           logger.error("execute: " + e, e);
       }
       return mapping.findForward("list");
	}

    protected boolean isReservedWord(String word) {
        return databaseKeywordList.containsKeyWord(word);
    }

    protected void writeOpenEmmChangeProfileFieldLog(Admin admin, ProfileFieldForm aForm, ProfileField existingProfileField){

        String existingDescription = existingProfileField.getDescription().trim();
        String newDescription = aForm.getDescription().trim();
        String existingDefaultValue = existingProfileField.getDefaultValue().trim();
        String newDefaultValue = aForm.getFieldDefault().trim();
        String existingShortName = existingProfileField.getShortname();

        //log field name changes
        if(!existingShortName.equals(aForm.getShortname())){
            writeUserActivityLog(admin, "edit profile field " + existingShortName,
                    "Profile field name changed from " + existingShortName + " to " + aForm.getShortname());
        }
        //log description name changes
        if(!existingDescription.equals(newDescription)){
            if((existingDescription.length()<=0)&&(newDescription.length()>0))
                writeUserActivityLog(admin, "edit profile field " + existingShortName, "Profile field description added");
            if((existingDescription.length()>0)&&(newDescription.length()<=0))
                writeUserActivityLog(admin, "edit profile field " + existingShortName, "Profile field description removed");
            if((existingDescription.length()>0)&&(newDescription.length()>0))
                writeUserActivityLog(admin, "edit profile field " + existingShortName, "Profile field description changed");
        }
        //log default value changes
        if(!existingDefaultValue.equals(newDefaultValue)){
            if((existingDefaultValue.length()<=0)&&(newDefaultValue.length()>0))
                writeUserActivityLog(admin, "edit profile field " + existingShortName, "Profile field default value " + newDefaultValue + " added");
            if((existingDefaultValue.length()>0)&&(newDefaultValue.length()<=0))
                writeUserActivityLog(admin, "edit profile field " + existingShortName, "Profile field default value" + existingDefaultValue +" removed");
            if((existingDefaultValue.length()>0)&&(newDefaultValue.length()>0))
                writeUserActivityLog(admin, "edit profile field " + existingShortName, "Profile field default value changed from " + existingDefaultValue + " to " + newDefaultValue);
        }
    }
}