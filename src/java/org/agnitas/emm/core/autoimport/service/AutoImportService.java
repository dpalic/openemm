package org.agnitas.emm.core.autoimport.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.agnitas.beans.Admin;
import org.agnitas.beans.DatasourceDescription;
import org.agnitas.beans.ImportProfile;
import org.agnitas.beans.Mailinglist;
import org.agnitas.beans.impl.CustomerImportStatusImpl;
import org.agnitas.dao.AdminDao;
import org.agnitas.dao.ImportProfileDao;
import org.agnitas.dao.MailinglistDao;
import org.agnitas.emm.core.autoimport.bean.AutoImport;
import org.agnitas.emm.core.autoimport.dao.AutoImportDao;
import org.agnitas.emm.core.velocity.VelocityCheck;
import org.agnitas.service.NewImportWizardService;
import org.agnitas.service.impl.ImportWizardContentParseException;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.ImportReportEntry;
import org.agnitas.util.ImportUtils;
import org.agnitas.util.SFtpHelper;
import org.agnitas.util.SafeString;
import org.agnitas.util.importvalues.ImportMode;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

public class AutoImportService {

	private static final transient Logger logger = Logger.getLogger(AutoImportService.class);

	private AutoImportDao autoImportDao;
	private ImportProfileDao importProfileDao;
	private MailinglistDao mailinglistDao;
	private AdminDao adminDao;

	private NewImportWizardService importService;

	public List<AutoImport> getAutoImportsOverview(int companyId) {
		return autoImportDao.getAutoImportsOverview(companyId);
	}

	public List<AutoImport> getAutoImportsToRun() {
		return autoImportDao.getAutoImportsToRun();
	}

	public void saveAutoImport(AutoImport autoImport) {
		if (autoImport.getAutoImportId() == 0) {
			autoImportDao.createAutoImport(autoImport);
		}
		else {
			autoImportDao.updateAutoImport(autoImport);
		}
	}

	public AutoImport getAutoImport(int autoImportId, int companyId) {
		 return autoImportDao.getAutoImport(autoImportId, companyId);
	}

	public List<ImportProfile> getImportProfiles(int companyId) {
		return importProfileDao.getImportProfilesByCompanyId(companyId);
	}

	public List<Mailinglist> getMailinglists(int companyId) {
		return mailinglistDao.getMailinglists(companyId);
	}

	public void deleteAutoImport(int autoImportId, int companyId) {
		autoImportDao.deleteAutoImport(autoImportId, companyId);
	}

	public void setAutoImportDao(AutoImportDao autoImportDao) {
		this.autoImportDao = autoImportDao;
	}

	public void setImportProfileDao(ImportProfileDao importProfileDao) {
		this.importProfileDao = importProfileDao;
	}

	public void setMailinglistDao(MailinglistDao mailinglistDao) {
		this.mailinglistDao = mailinglistDao;
	}

	public void changeAutoImportActiveStatus(int autoImportId, int companyId, boolean active) {
		autoImportDao.changeActiveStatus(autoImportId, companyId, active);
	}

	public File getImportFile(AutoImport autoImport) throws Exception {
		SFtpHelper sFtpHelper = null;
		InputStream inputStream = null;
		OutputStream out = null;
		try {
			String directoryPath = "";
			
			int lastSlash = autoImport.getFilePath().lastIndexOf("/");
			if (lastSlash > 0) {
				directoryPath = autoImport.getFilePath().substring(0, lastSlash);
			}
			
			try {
				sFtpHelper = new SFtpHelper(autoImport.getFileServer());
				sFtpHelper.setAllowUnknownHostKeys(autoImport.isAllowUnknownHostKeys());
				sFtpHelper.connect();
			} catch (Exception e) {
				throw new Exception("Cannot connect to sftp server", e);
			}

			if (!sFtpHelper.directoryExists(directoryPath)) {
				throw new Exception("Directory not found on sftp server");
			}

			if (!sFtpHelper.fileExists(autoImport.getFilePath())) {
				throw new Exception("File not found on sftp server");
			}
			
			try {
				Date mofidyDate = sFtpHelper.getModifyDate(autoImport.getFilePath());
				inputStream = sFtpHelper.get(autoImport.getFilePath());
				File systemUploadDirectory = AgnUtils.createDirectory(AgnUtils.getDefaultValue("system.upload"));
				String filename = "auto_import_" + (new Random()).nextInt() + ".csv";
				out = new FileOutputStream(new File(systemUploadDirectory, filename));
				int read;
				byte[] bytes = new byte[1024];
				while ((read = inputStream.read(bytes)) != -1) {
					out.write(bytes, 0, read);
				}
				inputStream.close();
				out.flush();
				out.close();

				File newFile = new File(systemUploadDirectory, filename);
				newFile.setLastModified(mofidyDate.getTime());
				return newFile;
			} catch (Exception e) {
				throw new Exception("Cannot read file from sftp server", e);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (sFtpHelper != null) {
				sFtpHelper.close();
			}
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(inputStream);
		}
	}

	public void doImport(int autoImportId, int companyId, ApplicationContext applicationContext) {
		AutoImport autoImport = getAutoImport(autoImportId, companyId);
        Admin admin = adminDao.getAdmin(autoImport.getAdminId(), companyId);
        ImportProfile importProfile = importProfileDao.getImportProfileById(autoImport.getImportProfileId());
        String fakeSessionId = "FAKE_SESSION_ID_" + (new Random()).nextInt();
        boolean tempTableCreated = false;
        String autoImportInfo = "";

        try {
            if (admin == null) {
                throw new AutoImportException("autoImport.error.adminDoesNotExist");
            }

            if (importProfile == null) {
                throw new AutoImportException("autoImport.error.importProfileDoesNotExist");
            }

            autoImportInfo = generateAutoImportInfoForReportMail(autoImport, importProfile, admin.getLocale());

			// get file from server via sftp
			File importFile = getImportFile(autoImport);
			if (importFile == null) {
				throw new AutoImportException("autoImport.error.fileTransferError");
			}

			// check if the file was already imported with this auto-import (if yes - don't do import)
			List<AutoImport.UsedFile> usedFiles = autoImportDao.getUsedFiles(autoImportId, companyId);
			for (AutoImport.UsedFile usedFile : usedFiles) {
                Calendar calendarUsedFile = Calendar.getInstance();
                calendarUsedFile.setTime(usedFile.getFileDate());
                calendarUsedFile.set(Calendar.MILLISECOND, 0);
                Calendar calendarImportFile = Calendar.getInstance();
                calendarImportFile.setTimeInMillis(importFile.lastModified());
                calendarImportFile.set(Calendar.MILLISECOND, 0);
				if (calendarUsedFile.equals(calendarImportFile) && usedFile.getFileSize() == importFile.length()) {
					throw new AutoImportException("autoImport.error.fileWasUsed");
				}
			}

			// init import service
			importService.setStatus(new CustomerImportStatusImpl());
			importService.setProfileId(autoImport.getImportProfileId());
			importService.setInputFile(importFile);
			importService.setAdminId(autoImport.getAdminId());
			importService.setCompanyId(companyId);
			if (admin.permissionAllowed("recipient.gender.extended")) {
				importService.setMaxGenderValue(NewImportWizardService.MAX_GENDER_VALUE_EXTENDED);
			} else {
				importService.setMaxGenderValue(NewImportWizardService.MAX_GENDER_VALUE_BASIC);
			}

			// check that import profile matches given csv file
			try {
				importService.validateImportProfileMatchGivenCVSFile();
			} catch (ImportWizardContentParseException e) {
				throw new AutoImportException("autoImport.error.profileDoesntMatchCSV", e);
			} catch (IOException e) {
				throw new AutoImportException("autoImport.error.fileReadError", e);
			}

			// check that profile key column is contained in columns mapping
			if (!importService.isProfileKeyColumnValid()) {
				throw new AutoImportException("autoImport.error.keyColumnDoesntMatchMapping");
			}

			// set import id
			int importId = Math.abs((new Random()).nextInt());
			importService.getImportProfile().setImportId(importId);

			// if import mode is ADD or ADD_AND_UPDATE - there should be at least one mailinglist assigned
			if ((importProfile.getImportMode() == ImportMode.ADD.getIntValue() || importProfile.getImportMode() == ImportMode.ADD_AND_UPDATE.getIntValue())
					&& autoImport.getMailinglists().size() == 0) {
				throw new AutoImportException("error.import.no_mailinglist");
			}

			// create new datasource and temporary import table
			final DatasourceDescription dsDescription = ImportUtils.getNewDatasourceDescription(companyId, importFile.getName(), applicationContext);
			importService.getStatus().setDatasourceID(dsDescription.getId());
			ImportUtils.createTemporaryTable(autoImport.getAdminId(), dsDescription.getId(), importProfile, fakeSessionId,
                    importService.getImportRecipientsDao(), admin);

			tempTableCreated = true;

			// do the import
			try {
				importService.doParse();
			} catch (Exception e) {
				throw new AutoImportException("autoImport.error.importError", e);
			}

			// if there was incorrect data for some recipients and auto-resume is not set - we need
			// to remove already imported recipients and stop the import
			if (importService.isPresentErrorForErrorEditPage() && !autoImport.isAutoResume()) {
				importService.getImportRecipientsDao().removeImportedRecipients(companyId, importService.getStatus().getDatasourceID());
				throw new AutoImportException("autoImport.error.recipientsDataError");
			}

			// assign to selected mailinglists
			Map<Integer, Integer> mlistStats = importService.getImportRecipientsDao().assignToMailingLists(autoImport.getMailinglists(),
					companyId, importService.getStatus().getDatasourceID(), importProfile.getImportMode(), admin.getAdminID(), importService);

			// check if any limits reached
			if (importService.isImportLimitReached()) {
				throw new AutoImportException("error.import.too_many_records");
			} else if (importService.isRecipientLimitReached()) {
				throw new AutoImportException("error.import.maxCount");
			}

			// write used file data to DB (to avoid importing the same file again)
			AutoImport.UsedFile usedFile = new AutoImport.UsedFile();
			usedFile.setFileDate(new Date(importFile.lastModified()));
			usedFile.setFileSize(importFile.length());
			autoImportDao.addUsedFile(usedFile, autoImport.getAutoImportId(), companyId);

			// save flag that this import was executed
			autoImport.setExecuted(true);
			saveAutoImport(autoImport);

			// collect report data and send status email
			importService.generateResultStatistics();
			Collection<ImportReportEntry> reportEntries = importService.generateReportData(importService.getStatus(), importProfile);
			File invalidRecipients = importService.createRecipientsCsv(new Integer[]{NewImportWizardService.RECIPIENT_TYPE_INVALID,
					NewImportWizardService.RECIPIENT_TYPE_FIELD_INVALID}, "invalid_recipients");
			List<Mailinglist> importMailinglists = getImportMailinglists(companyId, autoImport);
			importService.sendReportEmail(invalidRecipients, null, admin.getLocale(), reportEntries, importMailinglists, mlistStats, autoImportInfo);

			// @todo: remove this
			logger.error("Import is successfull:\n" + autoImportInfo);

		} catch (AutoImportException e) {
			// if there was error during import - send error mail
			String errorMail = "";
            try {
                errorMail = SafeString.getLocaleString("autoImport.error.reason", admin.getLocale()) + ": " +
                        SafeString.getLocaleString(e.getErrorMessageKey(), admin.getLocale()) + "\n\n" + autoImportInfo;
                AgnUtils.sendEmail(AgnUtils.getDefaultValue("import.report.from.address"), importProfile.getMailForReport(),
                        SafeString.getLocaleString("autoImport.error", admin.getLocale()), errorMail, errorMail, 0, "UTF-8");
            } finally {
                AgnUtils.sendExceptionMail(errorMail, e);
            }
			logger.error("Error during Auto Import: " + SafeString.getLocaleString(e.getErrorMessageKey(), Locale.ENGLISH), e);
		} catch (Exception e) {
			// if there was error during import - send error mail
			String errorMail = "";
            try {
                errorMail = SafeString.getLocaleString("autoImport.error.reason", admin.getLocale()) + ": " +
                        SafeString.getLocaleString("autoImport.error.importError", admin.getLocale()) + "\n\n" + autoImportInfo;
                AgnUtils.sendEmail(AgnUtils.getDefaultValue("import.report.from.address"), importProfile.getMailForReport(),
                        SafeString.getLocaleString("autoImport.error", admin.getLocale()), errorMail, errorMail, 0, "UTF-8");
            } finally {
                AgnUtils.sendExceptionMail(errorMail, e);
            }
			logger.error("Error during Auto Import: " + SafeString.getLocaleString("autoImport.error.importError", Locale.ENGLISH), e);
		} finally {
			// remove temporary import table
			if (tempTableCreated) {
				importService.getImportRecipientsDao().removeTemporaryTable(admin.getAdminID(), importService.getStatus().getDatasourceID(), fakeSessionId);
			}
		}
	}

    public void setAutoActivationDateAndActivate(@VelocityCheck int companyId, int autoImportId, Date date) {
        AutoImport autoImport = autoImportDao.getAutoImport(autoImportId, companyId);
        autoImport.setAutoActivationDate(date);
        autoImport.setOneTime(true);
        autoImport.setActive(true);
        autoImport.setExecuted(false);
        autoImportDao.updateAutoImport(autoImport);
    }

    public void deactivateAutoImport(@VelocityCheck int companyId, int autoImportId) {
        AutoImport autoImport = autoImportDao.getAutoImport(autoImportId, companyId);
        autoImport.setActive(false);
        autoImportDao.updateAutoImport(autoImport);
    }

	private String generateAutoImportInfoForReportMail(AutoImport autoImport, ImportProfile profile, Locale locale) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(SafeString.getLocaleString("autoImport.autoImport", locale)).append(": ").append(autoImport.getShortname()).append("\n");
		stringBuilder.append(SafeString.getLocaleString("autoImport.filePath", locale)).append(": ").append(autoImport.getFilePath()).append("\n");
		stringBuilder.append(SafeString.getLocaleString("autoImport.fileServer", locale)).append(": ").append(autoImport.getFileServerForLog()).append("\n");
		stringBuilder.append(SafeString.getLocaleString("import.ImportProfile", locale)).append(": ").append(profile.getName()).append("\n");
		Calendar calendar = GregorianCalendar.getInstance();
		DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, locale);
		String executionTime = dateFormat.format(calendar.getTime());
		stringBuilder.append(SafeString.getLocaleString("autoImport.performedAt", locale)).append(": ").append(executionTime).append("\n");
		return stringBuilder.toString();
	}

	private List<Mailinglist> getImportMailinglists(int companyId, AutoImport autoImport) {
		List<Mailinglist> mailinglists = mailinglistDao.getMailinglists(companyId);
		for (Iterator<Mailinglist> iterator = mailinglists.iterator(); iterator.hasNext(); ) {
			Mailinglist mailinglist = iterator.next();
			if (!autoImport.getMailinglists().contains(mailinglist.getId())) {
				iterator.remove();
			}
		}
		return mailinglists;
	}

	public void setImportService(NewImportWizardService importService) {
		this.importService = importService;
	}

	public void setAdminDao(AdminDao adminDao) {
		this.adminDao = adminDao;
	}

}
