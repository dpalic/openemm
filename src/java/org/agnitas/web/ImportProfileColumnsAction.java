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
 * the code written by AGNITAS AG are Copyright (c) 2009 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/

package org.agnitas.web;

import org.agnitas.beans.ColumnMapping;
import org.agnitas.beans.ImportProfile;
import org.agnitas.beans.ProfileField;
import org.agnitas.beans.impl.ColumnMappingImpl;
import org.agnitas.dao.ImportProfileDao;
import org.agnitas.dao.ProfileFieldDao;
import org.agnitas.dao.RecipientDao;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.CsvTokenizer;
import org.agnitas.util.ImportUtils;
import org.agnitas.util.importvalues.Charset;
import org.agnitas.util.importvalues.Separator;
import org.agnitas.util.importvalues.TextRecognitionChar;
import org.agnitas.web.forms.ImportProfileColumnsForm;
import org.apache.commons.io.FileUtils;
import org.apache.struts.action.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Action that handles import profile column mapping management
 *
 * @author Vyacheslav Stepanov
 */
public class ImportProfileColumnsAction extends ImportBaseFileAction {

    public static final int ACTION_UPLOAD = ACTION_LAST + 1;

    public static final int ACTION_ADD_COLUMN = ACTION_LAST + 2;

    public static final int ACTION_SKIP = ACTION_LAST + 3;

    public static final int ACTION_REMOVE_MAPPING = ACTION_LAST + 4;

    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form    The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param res     The HTTP response we are creating
     * @throws java.io.IOException            if an input/output error occurs
     * @throws javax.servlet.ServletException if a servlet exception occurs
     */
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse res)
            throws IOException, ServletException {

        super.execute(mapping, form, request, res);

        // Validate the request parameters specified by the user
        ImportProfileColumnsForm aForm;
        ActionMessages messages = new ActionMessages();
        ActionMessages errors = new ActionMessages();
        ActionForward destination = null;

        if (!this.checkLogon(request)) {
            return mapping.findForward("logon");
        }
        if (form != null) {
            aForm = (ImportProfileColumnsForm) form;
        } else {
            aForm = new ImportProfileColumnsForm();
        }

        AgnUtils.logger().info("Action: " + aForm.getAction());

        if (fileUploadPerformed) {
            aForm.setAction(ACTION_UPLOAD);
        }

        if (fileRemovePerformed) {
            aForm.setAction(ACTION_SKIP);
        }

        if (request.getParameter("add.x") != null) {
            aForm.setAction(ACTION_ADD_COLUMN);
        }

        if (ImportUtils.hasParameterStartsWith(request, "removeMapping")) {
            aForm.setAction(ACTION_REMOVE_MAPPING);
        }

        try {
            switch (aForm.getAction()) {

                case ImportProfileColumnsAction.ACTION_VIEW:
                    aForm.reset(mapping, request);
                    aForm.resetFormData();
                    loadImportProfile(aForm, request);
                    loadDbColumns(aForm, request);
                    if (aForm.getHasFile()) {
                        addColumnsFromFile(aForm, request);
                    }
                    aForm.setAction(ImportProfileColumnsAction.ACTION_SAVE);
                    destination = mapping.findForward("view");
                    break;

                case ImportProfileColumnsAction.ACTION_UPLOAD:
                    addColumnsFromFile(aForm, request);
                    aForm.setAction(ImportProfileColumnsAction.ACTION_SAVE);
                    destination = mapping.findForward("view");
                    break;

                case ImportProfileColumnsAction.ACTION_ADD_COLUMN:
                    addColumn(aForm);
                    aForm.setAction(ImportProfileColumnsAction.ACTION_SAVE);
                    destination = mapping.findForward("view");
                    break;

                case ImportProfileColumnsAction.ACTION_REMOVE_MAPPING:
                    removeMapping(aForm, request);
                    aForm.setAction(ImportProfileColumnsAction.ACTION_SAVE);
                    destination = mapping.findForward("view");
                    break;

                case ImportProfileColumnsAction.ACTION_SAVE:
                    saveMappings(aForm);
                    aForm.setAction(ImportProfileColumnsAction.ACTION_SAVE);
                    destination = mapping.findForward("view");
                    
                    messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("changes_saved"));
                    break;

                default:
                    aForm.setAction(ImportProfileColumnsAction.ACTION_SAVE);
                    destination = mapping.findForward("view");
            }

        } catch (Exception e) {
            AgnUtils.logger().error("execute: " + e + "\n" + AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        }

        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }
        
        if (!messages.isEmpty()) {
        	saveMessages(request, messages);
        }

        return destination;
    }

    /**
     * Handle user action to remove column mapping
     *
     * @param aForm   a form
     * @param request request
     */
    private void removeMapping(ImportProfileColumnsForm aForm, HttpServletRequest request) {
        String value = ImportUtils.getValueFromParameter(request, "removeMapping_", ".x");
        Integer columnIndex = Integer.valueOf(value);
        List<ColumnMapping> mappingList = aForm.getProfile().getColumnMapping();
        mappingList.remove(columnIndex.intValue());
    }

    /**
     * Saves column mappings done by user on edit-page
     *
     * @param aForm a form
     */
    private void saveMappings(ImportProfileColumnsForm aForm) {
        ImportProfileDao profileDao = (ImportProfileDao) getWebApplicationContext().getBean("ImportProfileDao");
        profileDao.removeColumnMappings(aForm.getProfileId());
        profileDao.saveColumnMappings(aForm.getProfile().getColumnMapping());
    }

    /**
     * Handles column adding that user performed on rdit-page
     *
     * @param aForm a form
     */
    private void addColumn(ImportProfileColumnsForm aForm) {
        String newColumn = aForm.getNewColumn();
        ColumnMappingImpl mapping = new ColumnMappingImpl();
        mapping.setFileColumn(newColumn);
        mapping.setDatabaseColumn("");
        mapping.setProfileId(aForm.getProfileId());
        mapping.setMandatory(false);
        aForm.getProfile().getColumnMapping().add(mapping);
        aForm.setNewColumn("");
    }

    /**
     * Loads columns of recipient table
     *
     * @param aForm   a form
     * @param request request
     */
    private void loadDbColumns(ImportProfileColumnsForm aForm, HttpServletRequest request) {
        RecipientDao recipientDao = (RecipientDao) getWebApplicationContext().getBean("RecipientDao");
        Map dbColumns = recipientDao.readDBColumns(AgnUtils.getCompanyID(request));
        ImportUtils.removeHiddenColumns(dbColumns);
        final Set keySet = dbColumns.keySet();
        List<String> lKeySet = new ArrayList();
        for(String tempString : (String[]) keySet.toArray(new String[0])){
                lKeySet.add(tempString);
        }
        Collections.sort(lKeySet);
        aForm.setDbColumns(lKeySet.toArray(new String[0]));
		// load DB columns default values
		ProfileFieldDao profileFieldDao = (ProfileFieldDao) getWebApplicationContext().getBean("ProfileFieldDao");
		List<ProfileField> profileFields = profileFieldDao.getProfileFields(AgnUtils.getCompanyID(request));
		for(ProfileField profileField : profileFields) {
			aForm.getDbColumnsDefaults().put(profileField.getColumn(), profileField.getDefaultValue());
		}
    }

    /**
     * Adds column mappings from uploaded csv-file using import profile settings
     * for parsing csv file
     *
     * @param aForm   a form
     * @param request request
     */
    private void addColumnsFromFile(ImportProfileColumnsForm aForm, HttpServletRequest request) {
        RecipientDao recipientDao = (RecipientDao) getWebApplicationContext().getBean("RecipientDao");
        if (aForm.getProfile() == null) {
            loadImportProfile(aForm, request);
        }
        ImportProfile profile = aForm.getProfile();
        List<ColumnMapping> columnMappings = profile.getColumnMapping();
        File file = getCurrentFile(request);
        if (file == null) {
            return;
        }
        try {
            Map dbColumns = recipientDao.readDBColumns(AgnUtils.getCompanyID(request));
            String profileCharset = Charset.getValue(profile.getCharset());
            String fileString = FileUtils.readFileToString(file, profileCharset);
            LineNumberReader aReader = new LineNumberReader(new StringReader(fileString));
            String firstLine = aReader.readLine();
            firstLine = firstLine.trim();
            CsvTokenizer tokenizer = new CsvTokenizer(firstLine,
                    Separator.getValue(profile.getSeparator()),
                    TextRecognitionChar.getValue(profile.getTextRecognitionChar()));
            String[] newCsvColumns = tokenizer.toArray();
            List<ColumnMapping> newMappings = new ArrayList<ColumnMapping>();
            for (String newCsvColumn : newCsvColumns) {
                if (!aForm.columnExists(newCsvColumn, columnMappings)) {
                    ColumnMapping newMapping = new ColumnMappingImpl();
                    newMapping.setProfileId(profile.getId());
                    newMapping.setMandatory(false);
                    newMapping.setFileColumn(newCsvColumn);
                    if (dbColumns.get(newCsvColumn) != null) {
                        newMapping.setDatabaseColumn(newCsvColumn);
						String defaultValue = aForm.getDbColumnsDefaults().get(newCsvColumn);
						if (defaultValue != null) {
							newMapping.setDefaultValue(defaultValue);
						}
                    }
                    newMappings.add(newMapping);
                }
            }
            profile.getColumnMapping().addAll(newMappings);
        } catch (IOException e) {
            AgnUtils.logger().error("Error reading csv-file: " + e + "\n" + AgnUtils.getStackTrace(e));
        } catch (Exception e) {
            AgnUtils.logger().error("Error reading csv-file: " + e + "\n" + AgnUtils.getStackTrace(e));
        }

    }

    /**
     * Loads import profile from DB using Dao
     *
     * @param aForm   a form
     * @param request request
     */
    private void loadImportProfile(ImportProfileColumnsForm aForm, HttpServletRequest request) {
        ImportProfileDao profileDao = (ImportProfileDao) getWebApplicationContext().getBean("ImportProfileDao");
        ImportProfile profile = profileDao.getImportProfileFull(aForm.getProfileId());
		aForm.setProfile(profile);
	}

}