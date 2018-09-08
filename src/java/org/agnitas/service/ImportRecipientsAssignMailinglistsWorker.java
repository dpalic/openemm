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
package org.agnitas.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.concurrent.Callable;

import org.agnitas.beans.CustomerImportStatus;
import org.agnitas.beans.ImportProfile;
import org.agnitas.beans.Mailinglist;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.EmmCalendar;
import org.agnitas.util.ImportReportEntry;
import org.agnitas.util.ImportUtils;
import org.agnitas.util.SafeString;
import org.agnitas.util.importvalues.CheckForDuplicates;
import org.agnitas.util.importvalues.ImportMode;
import org.agnitas.util.importvalues.NullValuesAction;
import org.agnitas.web.forms.NewImportWizardForm;
import org.apache.log4j.Logger;
import org.springframework.context.NoSuchMessageException;

/**
 * @author viktor 11-Aug-2010 2:50:45 PM
 */
public class ImportRecipientsAssignMailinglistsWorker implements Callable<Object> {
	private static final transient Logger logger = Logger.getLogger(ImportRecipientsAssignMailinglistsWorker.class);

    private NewImportWizardForm aForm;
    private Locale locale;
    private TimeZone zone;

    public ImportRecipientsAssignMailinglistsWorker(NewImportWizardForm aForm, Locale locale, TimeZone zone) {
        this.aForm = aForm;
        this.locale = locale;
        this.zone = zone;

    }

    public Object call() throws Exception {
        // assign mailinglists (should take 90% of progress bar)
        assignMailinglists(aForm);
        // generateReportData (should take 10% of progress bar)
        generateReportData(aForm);
        return null;
    }

    private void assignMailinglists(NewImportWizardForm aForm) {
        storeAssignedMailingLists(aForm.getListsToAssign(), aForm);
    }

    /**
     * Stores mailing list assignments to DB, stored assignment statistics to form
     *
     * @param assignedLists mailing lists that were assigned by user
     * @param aForm         form
     */
    private void storeAssignedMailingLists(List<Integer> assignedLists, NewImportWizardForm aForm) {
        final NewImportWizardService wizardHelper = aForm.getImportWizardHelper();
        ImportProfile profile = wizardHelper.getImportProfile();

        Map<Integer, Integer> statisitcs = aForm.getImportWizardHelper().getImportRecipientsDao().assignToMailingLists(assignedLists,
                wizardHelper.getCompanyId(), aForm.getDatasourceId(), profile.getImportMode(), wizardHelper.getAdminId(), wizardHelper);
        // store data to form for result page
        aForm.setMailinglistAssignStats(statisitcs);
        List<Mailinglist> allMailingLists = aForm.getAllMailingLists();
        List<Mailinglist> assignedMailingLists = new ArrayList<Mailinglist>();
        for (Mailinglist mailinglist : allMailingLists) {
            if (assignedLists.contains(mailinglist.getId())) {
                assignedMailingLists.add(mailinglist);
            }
        }
        aForm.setAssignedMailingLists(assignedMailingLists);
    }

    /* Methods moved from NewImportWizardAction */


    /**
     * Generates report data: import statistics, recipient files; sends report email
     *
     * @param aForm   a form
     */
    private void generateReportData(NewImportWizardForm aForm) {
        NewImportWizardService wizardHelper = aForm.getImportWizardHelper();
        CustomerImportStatus status = aForm.getImportWizardHelper().getStatus();
        ImportProfile profile = aForm.getImportWizardHelper().getImportProfile();

        wizardHelper.generateResultStatistics();
        wizardHelper.setCompletedPercent(91);

        Collection<ImportReportEntry> reportEntries = wizardHelper.generateReportData(status, profile);
        wizardHelper.setCompletedPercent(92);

        final Map<String, String> statusReportMap = localizeReportItems(reportEntries);
        wizardHelper.setCompletedPercent(93);

        wizardHelper.log(aForm.getDatasourceId(), status.getInserted() + status.getUpdated(), ImportUtils.describeMap(statusReportMap));
        aForm.setReportEntries(reportEntries);
        generateResultFiles(aForm);

        wizardHelper.sendReportEmail(aForm.getInvalidRecipientsFile(), aForm.getFixedRecipientsFile(), locale, aForm.getReportEntries(),
				aForm.getAssignedMailingLists(), aForm.getMailinglistAssignStats(), null);
        wizardHelper.setCompletedPercent(100);
    }

    /**
     * Method generates result recipient files (valid, invalid, fixed)
     * and stores them to form. If there are no recipients of some type
     * (i.e. invalid) the corresponding form file will be set to null
     *
     * @param aForm   a form
     */
    private void generateResultFiles(NewImportWizardForm aForm) {
        NewImportWizardService wizardHelper = aForm.getImportWizardHelper();
        // generate valid recipients file
        File validRecipients = wizardHelper.createRecipientsCsv(new Integer[]{NewImportWizardService.RECIPIENT_TYPE_VALID,
				NewImportWizardService.RECIPIENT_TYPE_DUPLICATE_RECIPIENT}, "valid_recipients");
        aForm.setValidRecipientsFile(validRecipients);
        wizardHelper.setCompletedPercent(94);
        // generate invalid recipietns file (invalid by wrong field values + other invalid: blacklisted etc.)
        File invalidRecipients = wizardHelper.createRecipientsCsv(new Integer[]{NewImportWizardService.RECIPIENT_TYPE_INVALID,
				NewImportWizardService.RECIPIENT_TYPE_FIELD_INVALID}, "invalid_recipients");
        aForm.setInvalidRecipientsFile(invalidRecipients);
        wizardHelper.setCompletedPercent(95);
        // generate fixed recipients file (fixed on error edit page)
        File fixedRecipients = wizardHelper.createRecipientsCsv(new Integer[]{NewImportWizardService.RECIPIENT_TYPE_FIXED_BY_HAND}, "fixed_recipients");
        aForm.setFixedRecipientsFile(fixedRecipients);
        wizardHelper.setCompletedPercent(96);
        // generate duplicate recipients file
        File duplicateRecipients = wizardHelper.createDuplicateRecipientsCsv(new Integer[]{NewImportWizardService.RECIPIENT_TYPE_DUPLICATE_IN_NEW_DATA_RECIPIENT,
				NewImportWizardService.RECIPIENT_TYPE_DUPLICATE_RECIPIENT}, "duplicate_recipients");
        aForm.setDuplicateRecipientsFile(duplicateRecipients);
        wizardHelper.setCompletedPercent(97);
        //generate result file
        File resultFile = generateResultFile(aForm);
        aForm.setResultFile(resultFile);
        wizardHelper.setCompletedPercent(98);

    }

    private File generateResultFile(NewImportWizardForm aForm) {
        String log_entry = "\n* * * * * * * * * * * * * * * * * *\n";
        EmmCalendar my_calendar = new EmmCalendar(java.util.TimeZone.getDefault());
        my_calendar.changeTimeWithZone(zone);
        java.util.Date my_time = my_calendar.getTime();
        String datum = my_time.toString();
        java.text.SimpleDateFormat format01 = new java.text.SimpleDateFormat("yyyyMMdd");
        String aktDate = format01.format(my_calendar.getTime());

        ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
        String modeString = bundle.getString(ImportMode.getPublicValue(aForm.getStatus().getMode()));
        String doubletteString = bundle.getString(CheckForDuplicates.getPublicValue(aForm.getStatus().getDoubleCheck()));
        String ignoreString = bundle.getString(NullValuesAction.getPublicValue(aForm.getStatus().getIgnoreNull()));

        NewImportWizardService importWizardHelper = aForm.getImportWizardHelper();
        File resultFile = null;
        try {
            File systemUploadDirectory = AgnUtils.createDirectory(AgnUtils.getDefaultValue("system.upload"));
            resultFile = File.createTempFile(aktDate + "_", ".txt", systemUploadDirectory);
        } catch (IOException e) {
            logger.error("execute: " + e, e);
        }

        log_entry += datum + "\n";
        log_entry += "company_id: " + importWizardHelper.getCompanyId() + "\n";
        log_entry += "admin_id: " + importWizardHelper.getAdminId() + "\n";
        log_entry += "datasource_id: " + aForm.getDatasourceId() + "\n";
        log_entry += "mode: " + modeString + "\n";
        log_entry += "doublette-check: " + doubletteString + "\n";
        log_entry += "ignore null-values: " + ignoreString + "\n";
        log_entry += "separator: " + aForm.getStatus().getSeparator() + "\n";
        log_entry += "delimiter: " + aForm.getStatus().getDelimiter() + "\n";
        log_entry += "key-column: " + aForm.getStatus().getKeycolumn() + "\n";
        log_entry += "charset: " + aForm.getStatus().getCharset() + "\n";
        log_entry += "  csv_errors_email: " + aForm.getStatus().getError(NewImportWizardService.EMAIL_ERROR) + "\n";
        log_entry += "  csv_errors_blacklist: " + aForm.getStatus().getError(NewImportWizardService.BLACKLIST_ERROR) + "\n";
        log_entry += "  csv_errors_double: " + aForm.getStatus().getError(NewImportWizardService.EMAILDOUBLE_ERROR) + "\n";
        log_entry += "  csv_errors_numeric: " + aForm.getStatus().getError(NewImportWizardService.NUMERIC_ERROR) + "\n";
        log_entry += "  csv_errors_mailtype: " + aForm.getStatus().getError(NewImportWizardService.MAILTYPE_ERROR) + "\n";
        log_entry += "  csv_errors_gender: " + aForm.getStatus().getError(NewImportWizardService.GENDER_ERROR) + "\n";
        log_entry += "  csv_errors_date: " + aForm.getStatus().getError(NewImportWizardService.DATE_ERROR) + "\n";
        log_entry += "  csv_errors_linestructure: " + aForm.getStatus().getError(NewImportWizardService.STRUCTURE_ERROR) + "\n\n";

        if (aForm.getStatus().getUpdated() >= 0) {
            log_entry += "  csv records allready in db: " + aForm.getStatus().getAlreadyInDb() + "\n";
        }

        NewImportWizardService wizardHelper = aForm.getImportWizardHelper();

        if (wizardHelper.getImportProfile().getImportMode() == ImportMode.ADD.getIntValue() ||
                wizardHelper.getImportProfile().getImportMode() == ImportMode.ADD_AND_UPDATE.getIntValue()) {
            log_entry += "  inserted: " + aForm.getStatus().getInserted() + "\n";
        }

        if (wizardHelper.getImportProfile().getImportMode() == ImportMode.UPDATE.getIntValue() ||
                wizardHelper.getImportProfile().getImportMode() == ImportMode.ADD_AND_UPDATE.getIntValue()) {
            log_entry += "  updated: " + aForm.getStatus().getUpdated() + "\n";
        }

        log_entry += "* * * * * * * * * * * * * * * * * * *\n";

        try {
            Writer output = null;
            try {
                //use buffering
                output = new BufferedWriter(new FileWriter(resultFile, true));
                output.write(log_entry);
            } finally {
                //flush and close both "output" and its underlying FileWriter
                if (output != null) output.close();
            }
        } catch (Exception e) {
            logger.error("execute: " + e, e);
        }
        return resultFile;
    }


    private Map<String, String> localizeReportItems(Collection<ImportReportEntry> reportEntries) {
        final Map<String, String> statusReportMap = new HashMap<String, String>();
        for (ImportReportEntry entry : reportEntries) {
            final String messageKey = entry.getKey();
            String localizedMessage;
			try {
				localizedMessage = SafeString.getLocaleString(messageKey, locale);
			} catch (NoSuchMessageException e) {
				logger.error(e);
				localizedMessage = messageKey;
			}
            statusReportMap.put(localizedMessage, entry.getValue());
        }
        return statusReportMap;
    }



}
