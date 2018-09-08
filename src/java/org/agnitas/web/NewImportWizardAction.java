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

import java.beans.IntrospectionException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.agnitas.beans.Admin;
import org.agnitas.beans.CustomerImportStatus;
import org.agnitas.beans.DatasourceDescription;
import org.agnitas.beans.ImportProfile;
import org.agnitas.beans.Mailinglist;
import org.agnitas.beans.ProfileRecipientFields;
import org.agnitas.dao.ImportProfileDao;
import org.agnitas.dao.ImportRecipientsDao;
import org.agnitas.dao.MailinglistDao;
import org.agnitas.emm.core.commons.util.ConfigService;
import org.agnitas.service.ImportErrorRecipientQueryWorker;
import org.agnitas.service.ImportRecipientsAssignMailinglistsWorker;
import org.agnitas.service.ImportRecipientsProcessWorker;
import org.agnitas.service.NewImportWizardService;
import org.agnitas.service.csv.Toolkit;
import org.agnitas.service.impl.CSVColumnState;
import org.agnitas.service.impl.ImportWizardContentParseException;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.GuiConstants;
import org.agnitas.util.ImportUtils;
import org.agnitas.util.importvalues.Charset;
import org.agnitas.util.importvalues.DateFormat;
import org.agnitas.util.importvalues.ImportMode;
import org.agnitas.util.importvalues.Separator;
import org.agnitas.util.importvalues.TextRecognitionChar;
import org.agnitas.web.forms.NewImportWizardForm;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;
import org.displaytag.pagination.PaginatedList;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author Viktor Gema
 */
public class NewImportWizardAction extends ImportBaseFileAction {
	
	/** The logger. */
	private static final transient Logger logger = Logger.getLogger(NewImportWizardAction.class);

    public static final String FUTURE_TASK1 = "IMPORT_RECIPIENT_LIST";
    public static final String FUTURE_TASK2 = "IMPORT_RECIPIENT_PROCESS";
    public static final String FUTURE_TASK3 = "IMPORT_RECIPIENT_WORKER";
    public static final int ACTION_START = 1;
    public static final int ACTION_PREVIEW = 2;
    public static final int ACTION_PROCEED = 3;
    public static final int ACTION_ERROR_EDIT = 4;
    
    public static final int ACTION_MLISTS = 5;
    public static final int ACTION_RESULT_PAGE = 6;
    public static final int ACTION_DOWNLOAD_CSV_FILE = 7;
    public static final int ACTION_MLISTS_SAVE = 8;

	/**
     * Process the specified HTTP request, and create the corresponding HTTP
	 * response (or forward to another web component that will create it).
	 * Return an <code>ActionForward</code> instance describing where and how
	 * control should be forwarded, or <code>null</code> if the response has
	 * already been completed.<br>
	 * Error and success messages are set during the action to give the user
	 * a feedback in the forwarded web component.<br>
	 * <br>
	 * ACTION_START: Loads the list of all available profiles for import and ID of default import profile for current user.<br>
     *     Resets import status and resultPagePrepared property of form. Forwards to "start".
	 * <br><br>
	 * ACTION_PREVIEW: performs general validation before starting recipients import.<br>
     *     Initializes import helper with initial values; <br>
     *     Created unique import ID and sets it to import profile; <br>
     *     If csv-file is missing or there are no import profiles in system - forwards to "start" with corresponding
     *     error messages.<br>
     *     If import profile doesn't match the csv-file or has no key column in its column mappings forwards to
     *     "profile_edit" with corresponding error messages.<br>
     *     If validation is passed loads first 20 recipients from csv-file for preview, loads mailinglists into form,
     *     chooses mailinglist message according to import mode, forwards to "preview".<br>
     *     If the session parameter "IMPORT_KEY_COLUMNS" is set - import ignores key column from import profile and uses
     *     list of key columns from "IMPORT_KEY_COLUMNS" (example "email, firstname, lastname"). The values from this
     *     session parameter is set to import profile.
     * <br><br>
	 * ACTION_PROCEED:<br>
     *     In general performs recipient import using settings from selected import profile.<br><br>
     *     If the FutureHolder doesn't contain yet the recipient import worker:<br>
     *     Checks if there are mailinglists selected. If the current import mode needs mailinglists and no list is
     *     selected - forwards to "preview" with appropriate error message. Stores the list of mailinglists to assign in
     *     form. Creates import recipient worker and puts it to FutureHolder. Stores import parameters (importMode,
     *     separator, delimiter etc.) to status property of form.<br><br>
     *     If FutureHolder is finished and there are errors for edit error page - forwards to error editing page and
     *     also checks for import limits.<br><br>
     *     If FutureHolder is finished and there are no errors for edit error page: puts worker for assigning
     *     mailinglists into FutureHolder (and forwards to "progress"), if that FutureHolder is finished - removes
     *     temporary table of import recipients from DB, removes stored csv, forwards to "result_page"; if it is still
     *     running - increases refresh rate and forwards to "progress". Also checks for import limits.<br><br>
     *     If FutureHolder is still running - increases refresh time and forwards to "progress"<br><br>
     *     If there were errors during the import - removed the recipient import worker from the FutureHolder and
     *     forwards to "preview" page with error messages.
	 * <br><br>
     * ACTION_ERROR_EDIT:
     *     If the FutureHolder doesn't have task running and there's attribute "recipientsInCurrentTable" in request -
     *     validates recipient fixed on error edit page and performs the import for fixed recipients.<br>
     *     If there are still errors for error-edit-page - forwards to error-edit page. If there are no errors left -
     *     prepares result page: calls future holder for assigning mailing lists and forwards to "progress" or
     *     "result_page" depending on FutureHolder finished or not.
	 * <br><br>
     * ACTION_MLISTS: puts worker for assigning mailinglists into FutureHolder (and forwards to "progress"), if that
     *     FutureHolder is finished - removes temporary table of import recipients from DB, removes stored csv,
     *     forwards to "result_page"; if it is still running - increases refresh rate and forwards to "progress".
	 * <br><br>
     * ACTION_DOWNLOAD_CSV_FILE: checks what type of file user wants to download. Gets the file depending on that type.
     *     Writes that file to response (for user to download). Forwards to null destination.
	 * <br><br>
	 * Any other ACTION_* would cause a forward to null
	 * <br><br>
     * If current destination is "error_edit":<br>
     * If the FutureHolder is not running yet - puts there a worker for getting invalid recipients from temporary table
     * containing beans of import-recipients from csv-file<br>
     * If the FutureHolder is not finished yet - increases refresh time and forwards to "loading"<br>
     * If FutureHolder is finished - puts found invalid recipients to request and to session attribute
     * "recipientsInCurrentTable", sets the total number of invalid recipients to form, removes current task from
     * FutureHolder
     * <br><br>
	 * @param form data for the action filled by the jsp
	 * @param req request from jsp <br>
     *   If the request parameter "start_proceed" is set - changes action to ACTION_PREVIEW.<br>
     *   If the request parameter "preview_back" is set - changes action to ACTION_START.<br>
     *   If the request parameter "preview_proceed" is set - changes action to ACTION_PROCEED.<br>
     *   If the request parameter "edit_page_save" is set - changes action to ACTION_ERROR_EDIT.<br>
	 * @param res response
	 * @param mapping
	 *            The ActionMapping used to select this instance
	 * @exception IOException
	 *                if an input/output error occurs
	 * @exception ServletException
	 *                if a servlet exception occurs
	 * @return destination specified in struts-config.xml to forward to next jsp
	*/
    @Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        super.execute(mapping, form, req, res);
        
        @SuppressWarnings("unchecked")
		AbstractMap<String, Object> futureHolder = (AbstractMap<String, Object>) WebApplicationContextUtils.getRequiredWebApplicationContext(req.getSession().getServletContext()).getBean("futureHolder");
        String futureKeyList = FUTURE_TASK1 + "@" + req.getSession(false).getId();
        String futureKeyProcess = FUTURE_TASK2 + "@" + req.getSession(false).getId();
        String futureKeyWorker = FUTURE_TASK3 + "@" + req.getSession(false).getId();

        // Validate the request parameters specified by the user
        NewImportWizardForm aForm = null;
        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();
        ActionForward destination = null;

        if (!AgnUtils.isUserLoggedIn(req)) {
            return mapping.findForward("logon");
        }

        if (form != null) {
            aForm = (NewImportWizardForm) form;
        } else {
            aForm = new NewImportWizardForm();
        }

        if (logger.isInfoEnabled()) logger.info("NewImportWizard action: " + aForm.getAction());

        if (AgnUtils.parameterNotEmpty(req, "start_proceed")) {
            aForm.setAction(NewImportWizardAction.ACTION_PREVIEW);
        }
        if (AgnUtils.parameterNotEmpty(req, "preview_back")) {
            aForm.setAction(NewImportWizardAction.ACTION_START);
        }
        if (AgnUtils.parameterNotEmpty(req, "preview_proceed")) {
            aForm.setAction(NewImportWizardAction.ACTION_PROCEED);
        }
        if (req.getParameter("edit_page_save") != null) {
            aForm.setAction(NewImportWizardAction.ACTION_ERROR_EDIT);
        }

        if (!allowed("wizard.import", req)) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
            saveErrors(req, errors);
            return null;
        }


        try {
            NewImportWizardService importWizardHelper = aForm.getImportWizardHelper();
            switch (aForm.getAction()) {

                case NewImportWizardAction.ACTION_START:
                    final Map<Integer, String> importProfiles = new LinkedHashMap<Integer, String>();
                    aForm.setDefaultProfileId(AgnUtils.getAdmin(req).getDefaultImportProfileID());

                    final List<ImportProfile> importProfileList = getProfileList(req);
                    for (ImportProfile importProfile : importProfileList) {
                        importProfiles.put(importProfile.getId(), importProfile.getName());
                    }
                    aForm.setImportProfiles(importProfiles);
                    aForm.setStatus((CustomerImportStatus) WebApplicationContextUtils.getRequiredWebApplicationContext(req.getSession().getServletContext()).getBean("CustomerImportStatus"));
                    destination = mapping.findForward("start");
					aForm.setResultPagePrepared(false);
					
                    break;

                case NewImportWizardAction.ACTION_PREVIEW:
					if (!aForm.getHasFile() && (aForm.getCsvFile() == null || StringUtils.isEmpty(aForm.getCsvFile().getFileName()))) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.import.no_file"));
						destination = mapping.findForward("start");
                    } else if (aForm.getImportProfiles().size() == 0) {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.import.no_profile"));
						destination = mapping.findForward("start");
                    } else {
						boolean profileFits = initImportWizardHelper(req, aForm, errors);
						boolean keyColumnValid = isProfileKeyColumnValid(aForm, errors);
						logImportStart(aForm);
						if ((!profileFits || !keyColumnValid) && !errors.isEmpty()) {
							HttpSession session = req.getSession();
							session.setAttribute(ImportProfileAction.IMPORT_PROFILE_ERRORS_KEY, errors);
							session.setAttribute(ImportProfileAction.IMPORT_PROFILE_ID_KEY, aForm.getDefaultProfileId());
							destination = mapping.findForward("profile_edit");
						} else if (!errors.isEmpty()) {
							destination = mapping.findForward("start");
						} else {
                            aForm.setPreviewParsedContent(importWizardHelper.getPreviewParsedContent(errors));
                            if (!errors.isEmpty()) {
                                destination = mapping.findForward("start");
                                break;
                            }

							aForm.setAllMailingLists(getAllMailingLists(req));
							aForm.setMailinglistAddMessage(importWizardHelper.createMailinglistAddMessage());
                            checkProfileKeyColumnIndexed(aForm, req, messages);

							destination = mapping.findForward("preview");
						}
					}
                    break;

                case NewImportWizardAction.ACTION_PROCEED:
                    aForm.setAction(NewImportWizardAction.ACTION_PROCEED);
                    destination = mapping.findForward("progress");
                    if (!futureHolder.containsKey(futureKeyProcess) && aForm.getErrorsDuringImport() == null) {
                        List<Integer> assignedLists = getAssignedMailingLists(req, aForm);
                        if (!ignoreEmptyAssignedList()) {
                            int importMode = aForm.getImportWizardHelper().getImportProfile().getImportMode();
                            if ((importMode == ImportMode.ADD.getIntValue() || importMode == ImportMode.ADD_AND_UPDATE.getIntValue()) && assignedLists.size() == 0) {
                                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.import.no_mailinglist"));
                                destination = mapping.findForward("preview");
                                break;
                            }
                        }

                        aForm.setListsToAssign(assignedLists);
                        final ImportRecipientsProcessWorker worker = new ImportRecipientsProcessWorker(importWizardHelper);
                        ImportProfile importProfile = aForm.getImportWizardHelper().getImportProfile();
                        futureHolder.put(futureKeyProcess, importRecipientsProcess(aForm, req, worker, importProfile));
                        futureHolder.put(futureKeyWorker, worker);

                        String charset = Charset.getPublicValue(importProfile.getCharset());
                        String separator = Separator.getValue(importProfile.getSeparator());
                        int mode = importProfile.getImportMode();
                        int doublette = importProfile.getCheckForDuplicates();
                        int nullValues = importProfile.getNullValuesAction();
                        String recognitionChar = TextRecognitionChar.getValue(importProfile.getTextRecognitionChar());

                        aForm.getStatus().setCharset(charset);
                        aForm.getStatus().setSeparator(separator);
                        aForm.getStatus().setMode(mode);
                        aForm.getStatus().setDoubleCheck(doublette);
                        aForm.getStatus().setIgnoreNull(nullValues);
                        aForm.getStatus().setDelimiter(recognitionChar);
                    }
					if (aForm.getErrorsDuringImport() != null) {
						errors.add(aForm.getErrorsDuringImport());
						destination = mapping.findForward("preview");
						futureHolder.remove(futureKeyProcess);
                        futureHolder.remove(futureKeyWorker);
                        aForm.setRefreshMillis(RecipientForm.DEFAULT_REFRESH_MILLIS);
						aForm.setErrorsDuringImport(null);
						break;
					}
                    if (futureHolder.containsKey(futureKeyWorker) && futureHolder.get(futureKeyWorker) != null) {
                        final ActionMessage message = ((ImportRecipientsProcessWorker) futureHolder.get(futureKeyWorker)).getMessage();
                        if (message != null) {
                            errors.add(ActionMessages.GLOBAL_MESSAGE, message);
                            futureHolder.remove(futureKeyProcess);
                            futureHolder.remove(futureKeyWorker);
                            aForm.setRefreshMillis(RecipientForm.DEFAULT_REFRESH_MILLIS);
                            destination = mapping.findForward("preview");
                            break;
                        }
                    }
                    if (futureHolder.containsKey(futureKeyProcess) && ((Future) futureHolder.get(futureKeyProcess)).isDone()) {
                        futureHolder.remove(futureKeyProcess);
                        futureHolder.remove(futureKeyWorker);
                        if (importWizardHelper.isPresentErrorForErrorEditPage()) {
                            destination = mapping.findForward("error_edit");
                        } else {
                            destination = prepareResultPage(mapping, req, aForm, futureHolder, futureKeyProcess);
                        }
                        destination = checkImportLimits(mapping, errors, destination, importWizardHelper, aForm, req, futureHolder,futureKeyProcess);

                        if( "start".equals(destination.getName())) {
                        	futureHolder.remove(futureKeyProcess);
                        	futureHolder.remove(futureKeyWorker);	
                        }

                        aForm.setRefreshMillis(RecipientForm.DEFAULT_REFRESH_MILLIS);
                    } else {
                        if (aForm.getRefreshMillis() < 100000) { // raise the refresh time
                            aForm.setRefreshMillis(aForm.getRefreshMillis() + 50);
                        }
                        //aForm.set
                    }
                    break;
                case NewImportWizardAction.ACTION_ERROR_EDIT:
                    if (!futureHolder.containsKey(futureKeyList) && (PaginatedList) req.getSession().getAttribute("recipientsInCurrentTable") != null) {
                        final HashMap<String, ProfileRecipientFields> mapRecipientsFromTable = new HashMap<String, ProfileRecipientFields>();
                        final PaginatedList recipientsFromTable = (PaginatedList) req.getSession().getAttribute("recipientsInCurrentTable");
                        for (Object object : recipientsFromTable.getList()) {
                            final Map dynaBean = (Map) object;
                            final ProfileRecipientFields recipient = (ProfileRecipientFields) dynaBean.get(NewImportWizardService.ERROR_EDIT_RECIPIENT_EDIT_RESERVED);
                            mapRecipientsFromTable.put(recipient.getTemporaryId(), recipient);
                        }
                        final Map<String, String> changedRecipients = getChangedRecipients(req);
                        for (String key : changedRecipients.keySet()) {
                            final String temporaryId = key.substring(0, key.indexOf("/RESERVED/"));
                            final String propertyName = key.substring(key.indexOf("/RESERVED/") + 10, key.length());
                            final ProfileRecipientFields recipient = mapRecipientsFromTable.get(temporaryId);
                            Toolkit.setValueFromBean(recipient, propertyName, changedRecipients.get(key));
                        }

                        importWizardHelper.setBeansAfterEditOnErrorEditPage(new ArrayList<ProfileRecipientFields>(mapRecipientsFromTable.values()));
                        importWizardHelper.doValidate(true);
                    }
                    if (importWizardHelper.isPresentErrorForErrorEditPage()) {
                        destination = mapping.findForward("error_edit");
                    } else {
                        destination = prepareResultPage(mapping, req, aForm, futureHolder, futureKeyProcess);
                    }
					destination = checkImportLimits(mapping, errors, destination, importWizardHelper, aForm, req, futureHolder, futureKeyProcess);
                    break;
                case NewImportWizardAction.ACTION_MLISTS:
                    destination = prepareResultPage(mapping, req, aForm, futureHolder, futureKeyProcess);
                    break;

                case NewImportWizardAction.ACTION_DOWNLOAD_CSV_FILE:
                    File outfile = null;
                    if (aForm.getDownloadFileType() ==
                            NewImportWizardService.RECIPIENT_TYPE_VALID) {
                        outfile = aForm.getValidRecipientsFile();
                    } else if (aForm.getDownloadFileType() ==
                            NewImportWizardService.RECIPIENT_TYPE_INVALID) {
                        outfile = aForm.getInvalidRecipientsFile();
                    } else if (aForm.getDownloadFileType() ==
                            NewImportWizardService.RECIPIENT_TYPE_FIXED_BY_HAND) {
                        outfile = aForm.getFixedRecipientsFile();
                    }   else if (aForm.getDownloadFileType() ==
                            NewImportWizardService.RECIPIENT_TYPE_DUPLICATE_RECIPIENT) {
                        outfile = aForm.getDuplicateRecipientsFile();
                    } else if (aForm.getDownloadFileType() ==
                            NewImportWizardService.RESULT_TYPE){
                        outfile = aForm.getResultFile();
                    }
                    transferFile(res, errors, outfile);
                    destination = null;
                    break;
            }

        } catch (Exception e) {
            logger.error("execute: " + e, e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception", configService.getValue(ConfigService.Value.SupportEmergencyUrl)));
        }

        if (destination != null && "error_edit".equals(destination.getName())) {
            try {
                setNumberOfRows(req, aForm);
                destination = mapping.findForward("loading");

                if (! futureHolder.containsKey(futureKeyList)) {
                	futureHolder.put(futureKeyList,getRecipientListFuture(req, aForm));
                }

                if (futureHolder.containsKey(futureKeyList) && ((Future) futureHolder.get(futureKeyList)).isDone()) {
                    req.setAttribute("recipientList", ((Future) futureHolder.get(futureKeyList)).get());
                    req.getSession().setAttribute("recipientsInCurrentTable", ((Future) futureHolder.get(futureKeyList)).get());
                    destination = mapping.findForward("error_edit");
                    aForm.setAll(((PaginatedList) ((Future) futureHolder.get(futureKeyList)).get()).getFullListSize());
                    futureHolder.remove(futureKeyList);
                    aForm.setRefreshMillis(RecipientForm.DEFAULT_REFRESH_MILLIS);
                } else {
                    if (aForm.getRefreshMillis() < 1000) { // raise the refresh time
                        aForm.setRefreshMillis(aForm.getRefreshMillis() + 50);
                    }
                    aForm.setError(false);
                }

            } catch (Exception e) {
                logger.error("recipientList: " + e, e);
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception", configService.getValue(ConfigService.Value.SupportEmergencyUrl)));
                aForm.setError(true); // do not refresh when an error has been occurred
            }
        }

        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty() && destination != null)

        {
            saveErrors(req, errors);
			if (destination.getName().equals("progress")) {
				aForm.setErrorsDuringImport(errors);
			}
            // return new ActionForward(mapping.getForward());
        }

        if (!messages.isEmpty()) {
        	saveMessages(req, messages);
        }

        return destination;
    }

	private void logImportStart(NewImportWizardForm aForm) {
		Random random = new Random();
		int importId = Math.abs(random.nextInt());
		aForm.getImportWizardHelper().getImportProfile().setImportId(importId);
		if (logger.isInfoEnabled()) logger.info("Import ID: " + importId +
			"\nImport Profile ID: " + aForm.getImportWizardHelper().getImportProfile().getId() +
			"\nImport Profile Name: " + aForm.getImportWizardHelper().getImportProfile().getName() +
			"\nFile Name: " + aForm.getCurrentFileName());
	}

	protected boolean ignoreEmptyAssignedList(){
        return false;
    }

    private ActionForward prepareResultPage(ActionMapping mapping, HttpServletRequest req, NewImportWizardForm aForm, AbstractMap<String, Object> futureHolder, String futureKeyProcess) {
        if (!aForm.isResultPagePrepared()) {

            if (!futureHolder.containsKey(futureKeyProcess) && aForm.getErrorsDuringImport() == null) {

                futureHolder.put(futureKeyProcess, assignMailinglists(aForm, req));
                aForm.setAction(NewImportWizardAction.ACTION_MLISTS);
                return mapping.findForward("progress");

            }
            if (futureHolder.containsKey(futureKeyProcess) && ((Future) futureHolder.get(futureKeyProcess)).isDone()) {
                removeTemporaryTable(req, aForm);
                removeStoredCsvFile(req);
                aForm.setResultPagePrepared(true);

                futureHolder.remove(futureKeyProcess);
                aForm.setRefreshMillis(RecipientForm.DEFAULT_REFRESH_MILLIS);
                NewImportWizardService importWizardHelper = aForm.getImportWizardHelper();
                if (importWizardHelper != null){
                    destroyTemporaryConnection(importWizardHelper.getImportRecipientsDao());
                }
                return mapping.findForward("result_page");
            } else {
                if (aForm.getRefreshMillis() < 100000) { // raise the refresh time
                    aForm.setRefreshMillis(aForm.getRefreshMillis() + 50);
                }
            }
          aForm.setAction(NewImportWizardAction.ACTION_MLISTS);
          return mapping.findForward("progress");
        }

        NewImportWizardService importWizardHelper = aForm.getImportWizardHelper();
        if (importWizardHelper != null){
            destroyTemporaryConnection(importWizardHelper.getImportRecipientsDao());
        }
        return mapping.findForward("result_page");
    }

    private void destroyTemporaryConnection(ImportRecipientsDao importRecipientsDao) {
        if (importRecipientsDao != null){
            SingleConnectionDataSource temporaryConnection = importRecipientsDao.getTemporaryConnection();
            if (temporaryConnection != null){
                temporaryConnection.destroy();
                importRecipientsDao.setTemporaryConnection(null);
            }
        }
    }

    private Object assignMailinglists(NewImportWizardForm aForm, HttpServletRequest req) {
        ExecutorService service = (ExecutorService) WebApplicationContextUtils.getRequiredWebApplicationContext(req.getSession().getServletContext()).getBean("workerExecutorService");
        Locale locale = (Locale) req.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
        TimeZone zone = TimeZone.getTimeZone(AgnUtils.getAdmin(req).getAdminTimezone());
        final Future future = service.submit(new ImportRecipientsAssignMailinglistsWorker(aForm, locale, zone));
        return future;
    }

    /**
	 * Method removes temporary recipient table used by import
	 * @param req request
	 * @param aForm form
	 */
	private void removeTemporaryTable(HttpServletRequest req, NewImportWizardForm aForm) {
		final String prefix = "cust_" + AgnUtils.getAdmin(req).getAdminID() + "_tmp_";
		final String tableName = prefix + aForm.getDatasourceId() + "_tbl";
		aForm.getImportWizardHelper().getImportRecipientsDao().removeTemporaryTable(tableName, req.getSession().getId());
	}

	/**
	 * Method checks if current import reaches the recipient limits (recipients
	 * per import; max recipient number in DB)
	 * @param mapping action mapping
	 * @param errors errors
	 * @param destination the current destination
	 * @param importWizardHelper the import service
	 * @param aForm the form
	 * @param req request
	 * @param futureHolder
     *@param futureKeyProcess @return the action destination
	 */
	private ActionForward checkImportLimits(ActionMapping mapping, ActionMessages errors, ActionForward destination, NewImportWizardService importWizardHelper, NewImportWizardForm aForm, HttpServletRequest req, AbstractMap<String, Object> futureHolder, String futureKeyProcess) {
		if (importWizardHelper.isImportLimitReached()) {
			errors.add("global", new ActionMessage("error.import.too_many_records", AgnUtils.getDefaultValue("import.maxRows")));
			destination = mapping.findForward("start");
			removeTemporaryTable(req, aForm);
		}
		else if (importWizardHelper.isRecipientLimitReached()) {
			errors.add("global", new ActionMessage("error.import.maxCount"));
			destination = prepareResultPage(mapping, req, aForm, futureHolder, futureKeyProcess);
		}
		else if (importWizardHelper.isNearLimit()) {
			errors.add("global", new ActionMessage("warning.import.maxCount"));
		}
		return destination;
	}

    /**
     * Method checks if profile has key column in its column mappings
     *
     * @param aForm  a form
     * @param errors errors to add error to if key column is not imported
     * @return true if key column is contained in one of column mappings
     */
    private boolean isProfileKeyColumnValid(NewImportWizardForm aForm, ActionMessages errors) {
		boolean keyColumnValid = aForm.getImportWizardHelper().isProfileKeyColumnValid();
		if (!keyColumnValid) {
			errors.add("profile", new ActionMessage("error.import.keycolumn_not_imported"));
		}
		return keyColumnValid;
    }

    /**
     * Method checks if key column is indexed in database
     *
     * @param aForm  a form
     * @param req request
     * @param messages messages to add warning to if key column is not indexed
     * @return true if key column is contained in one of column mappings
     */
     private void checkProfileKeyColumnIndexed(NewImportWizardForm aForm, HttpServletRequest req, ActionMessages messages) {
         ImportProfile profile = aForm.getImportWizardHelper().getImportProfile();
         boolean keyColumnIndexed = aForm.getImportWizardHelper().getImportRecipientsDao().isKeyColumnIndexed(AgnUtils.getCompanyID(req), profile.getKeyColumn());
         if(!keyColumnIndexed){
             messages.add(GuiConstants.ACTIONMESSAGE_CONTAINER_WARNING, new ActionMessage("warning.import.keyColumn.index"));
         }
     }

    /**
     * Method takes changed recipient fields that were changed on error-edit-page
     * Gets changed data from request
     *
     * @param request request
     * @return result map (temporary id -> new value)
     */
    private Map<String, String> getChangedRecipients(HttpServletRequest request) {
        Map<String, String> result = new HashMap<String, String>();
        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String pName = (String) parameterNames.nextElement();
            String paramBeginStr = "changed_recipient_";
            if (pName.startsWith(paramBeginStr)) {
                String id = pName.substring(paramBeginStr.length());
                String value = request.getParameter(pName);
                result.put(id, value);
            }
        }
        return result;
    }

    /**
     * Method creates date format for error-edit-page calendar using import
     * profile date format
     *
     * @param aForm a form
     * @return date format for jscalendar
     */
    private String createCalendarDateFormat(NewImportWizardForm aForm) {
        int dateFormat = aForm.getImportWizardHelper().getImportProfile().getDateFormat();
        String csvFormat = DateFormat.getValue(dateFormat);
        csvFormat = csvFormat.replace("yyyy", "%Y");
        csvFormat = csvFormat.replace("MM", "%m");
        csvFormat = csvFormat.replace("dd", "%d");
        csvFormat = csvFormat.replace("HH", "%H");
        csvFormat = csvFormat.replace("mm", "%M");
        csvFormat = csvFormat.replace("ss", "%S");
        return csvFormat;
    }

    /**
     * Gets all mailing lists for current company id
     *
     * @param req request to take company id
     * @return all mailing lists for current company id
     */
    private List<Mailinglist> getAllMailingLists(HttpServletRequest req) {
        MailinglistDao dao = (MailinglistDao) WebApplicationContextUtils.getRequiredWebApplicationContext(req.getSession().getServletContext()).getBean("MailinglistDao");
        List mailinglists = dao.getMailinglists(AgnUtils.getCompanyID(req));
        return mailinglists;
    }


    /**
     * Gets list of mailing lists ids that were assigned on
     * assign-mailinglist-page; takes data from request
     *
     * @param req   request
     * @param aForm form
     * @return ids of assigned mailing lists
     */
    private List<Integer> getAssignedMailingLists(HttpServletRequest req, NewImportWizardForm aForm) {
        String aParam = null;
        List<Integer> mailingLists = new ArrayList<Integer>();
        Enumeration<String> e = req.getParameterNames();
        while (e.hasMoreElements()) {
            aParam = e.nextElement();
            if (aParam.startsWith("agn_mlid_")) {
                mailingLists.add(Integer.valueOf(aParam.substring(9)));
            }
        }
        return mailingLists;
    }

    /**
     * Inits import wizard helper, checks if import profile matches the csv-file
     *
     * @param req    request
     * @param aForm  form
     * @param errors list of errors
     * @return true if no profile-match errors occured, false otherwise
     */
    private boolean initImportWizardHelper(HttpServletRequest req, NewImportWizardForm aForm, ActionMessages errors) {
        NewImportWizardService importWizardHelper = aForm.getImportWizardHelper();
        try {
            importWizardHelper.setProfileId(aForm.getDefaultProfileId());
            importWizardHelper.setInputFile(getCurrentFile(req));
            importWizardHelper.setAdminId(AgnUtils.getAdmin(req).getAdminID());
            importWizardHelper.setCompanyId(AgnUtils.getCompanyID(req));
            if (AgnUtils.getAdmin(req).permissionAllowed("recipient.gender.extended")) {
                importWizardHelper.setMaxGenderValue(NewImportWizardService.MAX_GENDER_VALUE_EXTENDED);
            } else {
                importWizardHelper.setMaxGenderValue(NewImportWizardService.MAX_GENDER_VALUE_BASIC);
            }            
            importWizardHelper.validateImportProfileMatchGivenCVSFile();

            String importKeyColumns = (String) req.getSession().getAttribute("IMPORT_KEY_COLUMNS");
            if(!StringUtils.isEmpty(importKeyColumns)){
                List<String> keyColumns = importWizardHelper.getImportProfile().getKeyColumns();
                keyColumns.clear();
                String[] columns = importKeyColumns.split(", ");
                keyColumns.addAll(Arrays.asList(columns));
                // remove session var
                req.getSession().removeAttribute("IMPORT_KEY_COLUMNS");
            }
            return true;
        } catch (ImportWizardContentParseException e) {
            aForm.setAction(NewImportWizardAction.ACTION_START);
            if (e.getParameterValue() != null) {
                errors.add("profile", new ActionMessage(e.getErrorMessageKey(), e.getParameterValue()));
            } else {
                errors.add("profile", new ActionMessage(e.getErrorMessageKey()));
            }
            return false;
        } catch (IOException e) {
            errors.add("csvFile", new ActionMessage("error.import.no_file"));
            return true;
        }
    }

    /**
     * Method transfers given file to action response (for user to download)
     *
     * @param response action response
     * @param errors   errors
     * @param outfile  file to transfer
     * @throws IOException exceptions that can occur while working with IO
     */
	private void transferFile(HttpServletResponse response, ActionMessages errors, File outfile) throws IOException {
        if (outfile != null) {
            byte bytes[] = new byte[16384];
            int len;
            FileInputStream instream = new FileInputStream(outfile);
            try {
	            if (outfile.getName().endsWith(".zip")) {
	                response.setContentType("application/zip");
	            } else if (outfile.getName().endsWith(".txt")) {
	                response.setContentType("application/txt");
	            }
	            response.setHeader("Content-Disposition", "attachment; filename=\"" + outfile.getName() + "\";");
	            response.setContentLength((int) outfile.length());
	            
	            @SuppressWarnings("resource")		// Do not close this stream, it's managed by the servlet container
	            ServletOutputStream ostream = response.getOutputStream();
	            while ((len = instream.read(bytes)) != -1) {
	                ostream.write(bytes, 0, len);
	            }
            } finally {
            	instream.close();
            }
        } else {
            errors.add("global", new ActionMessage("error.export.file_not_ready"));
        }
    }

    /**
     * @param request request
     * @return list of import profiles for overview page with current company id
     */
	private List<ImportProfile> getProfileList(HttpServletRequest request) throws InstantiationException, IllegalAccessException {
        ImportProfileDao profileDao = (ImportProfileDao) WebApplicationContextUtils.getRequiredWebApplicationContext(request.getSession().getServletContext()).getBean("ImportProfileDao");
        return profileDao.getImportProfilesByCompanyId(AgnUtils.getCompanyID(request));
    }

    /**
     * Get a list of recipients according to your validation
     *
     * @param request
     * @param aContext
     * @param aForm
     * @return
     * @throws NumberFormatException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws java.util.concurrent.ExecutionException
     *
     * @throws InterruptedException
     */

    public Future getRecipientListFuture(HttpServletRequest request, NewImportWizardForm aForm)
            throws NumberFormatException, IllegalAccessException, InstantiationException, InterruptedException, ExecutionException, IntrospectionException, InvocationTargetException {

        //ImportRecipientsDao recipientDao = (ImportRecipientsDao) aContext.getBean("ImportRecipientsDao");
        String sort = getSort(request, aForm);
        String direction = request.getParameter("dir");

        int rownums = aForm.getNumberofRows();
        if (direction == null) {
            direction = aForm.getOrder();
        } else {
            aForm.setOrder(direction);
        }

        String pageStr = request.getParameter("page");
        if (pageStr == null || "".equals(pageStr.trim())) {
            if (aForm.getPage() == null || "".equals(aForm.getPage().trim())) {
                aForm.setPage("1");
            }
            pageStr = aForm.getPage();
        } else {
            aForm.setPage(pageStr);
        }

        if (aForm.isNumberOfRowsChanged()) {
            aForm.setPage("1");
            aForm.setNumberOfRowsChanged(false);
            pageStr = "1";
        }
        final CSVColumnState[] columns = aForm.getImportWizardHelper().getColumns();
        ApplicationContext aContext = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getSession().getServletContext());
        ExecutorService service = (ExecutorService) aContext.getBean("workerExecutorService");
        Future future = service.submit(new ImportErrorRecipientQueryWorker(aForm.getImportWizardHelper().getImportRecipientsDao(), AgnUtils.getAdmin(request).getAdminID(), sort, direction, Integer.parseInt(pageStr), rownums, aForm.getAll(), columns, aForm.getImportWizardHelper().getStatus().getDatasourceID()));
        //return aForm.getImportWizardHelper().getImportRecipientsDao().getInvalidRecipientList(columns, sort, direction, Integer.parseInt(pageStr), rownums, 0, AgnUtils.getAdmin(request).getAdminID(), aForm.getImportWizardHelper().getStatus().getDatasourceID());
        return future;

    }


    public Future importRecipientsProcess(NewImportWizardForm aForm, HttpServletRequest req, ImportRecipientsProcessWorker worker, ImportProfile importProfile) {
        Admin admin = AgnUtils.getAdmin(req);

        String startImport = new SimpleDateFormat("yyyy/MM/dd HH-mm-ss").format(Calendar.getInstance().getTime());
        String calendarDateFormat = createCalendarDateFormat(aForm);
        aForm.setCalendarDateFormat(calendarDateFormat);
        //set datasource id
        ApplicationContext aContext = WebApplicationContextUtils.getRequiredWebApplicationContext(req.getSession().getServletContext());
        final DatasourceDescription dsDescription = ImportUtils.getNewDatasourceDescription(AgnUtils.getCompanyID(req), aForm.getCurrentFileName(), aContext);
        aForm.getStatus().setDatasourceID(dsDescription.getId());
        aForm.setDatasourceId(dsDescription.getId());
        ImportUtils.createTemporaryTable(admin.getAdminID(), dsDescription.getId(), importProfile,
                req.getSession().getId(), aForm.getImportWizardHelper().getImportRecipientsDao(), admin);


        ExecutorService service = (ExecutorService) aContext.getBean("workerExecutorService");
		final Future future = service.submit(worker);
        writeImportLog(admin, aForm, startImport);
        return future;
    }

    /**
     * Initialize the list which keeps the current width of the columns, with a default value of '-1'
     * A JavaScript in the corresponding jsp will set the style.width of the column.
     *
     * @param size number of columns
     * @return
     */
    protected List<String> getInitializedColumnWidthList(int size) {
        List<String> columnWidthList = new ArrayList<String>();
        for (int i = 0; i < size; i++) {
            columnWidthList.add("-1");
        }
        return columnWidthList;
    }

    protected String getSort(HttpServletRequest request, NewImportWizardForm aForm) {
        String sort = request.getParameter("sort");
		if(sort == null) {
			sort = aForm.getSort();
		} else {
			aForm.setSort(sort);
		}
		return sort;
	}

    private void writeImportLog(Admin admin, NewImportWizardForm aForm, String startImport){

        try {
            List<Mailinglist> allMailingLists = aForm.getAllMailingLists();
            List<Integer> listsToAssign = aForm.getListsToAssign();
            StringBuilder assignedMailingLists = new StringBuilder();

            for(Integer assignedList : listsToAssign){

                for(Mailinglist list : allMailingLists){

                    if(list.getId() == assignedList){
                        assignedMailingLists.append(list.getShortname());
                        assignedMailingLists.append(", ");
                    }
                }
            }

            int profileId = aForm.getDefaultProfileId();
            Map<Integer, String> importProfiles = aForm.getImportProfiles();
            String importProfileName = importProfiles.get(profileId);

            String fileName = aForm.getCsvFile() != null ? aForm.getCsvFile().getFileName() : "not set";

            StringBuilder description = new StringBuilder();
            description.append("Import started at: ");
            description.append(startImport);
            description.append(". File name: ");
            description.append(fileName);
            description.append(", mailing list(s): ");
            description.append(assignedMailingLists);
            description.append("used profile: ");
            description.append(importProfileName);
            description.append(".");

            writeUserActivityLog(admin, "do import from file", description.toString());
        } catch (Exception e) {
            logger.error("import recipients" + e, e);
        }

    }

}
