package org.agnitas.emm.core.autoexport.service;

import com.jcraft.jsch.ChannelSftp;

import org.agnitas.beans.*;
import org.agnitas.dao.AdminDao;
import org.agnitas.dao.ExportPredefDao;
import org.agnitas.emm.core.autoexport.bean.AutoExport;
import org.agnitas.emm.core.autoexport.dao.AutoExportDao;
import org.agnitas.emm.core.velocity.VelocityCheck;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.SFtpHelper;
import org.agnitas.util.SafeString;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class AutoExportService {

    private static final transient Logger logger = Logger.getLogger(AutoExportService.class);

    private AutoExportDao autoExportDao;
    private ExportPredefDao exportPredefDao;
    private AdminDao adminDao;
    private ExportWizardService exportWizardService;

    public List<AutoExport> getAutoExportsOverview(int companyId) {
        return autoExportDao.getAutoExportsOverview(companyId);
    }

    public List<AutoExport> getAutoExportsToRun() {
        return autoExportDao.getAutoExportsToRun();
    }

    public void doExport(int autoExportId, int companyId, ApplicationContext applicationContext) {
        AutoExport autoExport = getAutoExport(autoExportId, companyId);
        Admin admin = adminDao.getAdmin(autoExport.getAdminId(), companyId);
        ExportPredef exportProfile = exportPredefDao.get(autoExport.getExportProfileId(), companyId);
        String autoExportInfo = generateAutoExportInfoForReportMail(autoExport, exportProfile, admin.getLocale());
        File exportFile = exportWizardService.createExportFile(autoExport, exportProfile, companyId);
        SFtpHelper sFtpHelper = null;
        InputStream inputStream = null;
        OutputStream out = null;
        try {
			try {
				sFtpHelper = new SFtpHelper(autoExport.getFileServer());
				sFtpHelper.setAllowUnknownHostKeys(autoExport.isAllowUnknownHostKeys());
				sFtpHelper.connect();
			} catch (Exception e) {
				throw new Exception("Cannot connect to sftp server", e);
			}

			if (!sFtpHelper.directoryExists(autoExport.getFilePath())) {
				throw new Exception("Directory not found on sftp server");
			}
            
            try {
				inputStream = new FileInputStream(exportFile);
				String slash = autoExport.getFilePath().lastIndexOf("/") == autoExport.getFilePath().length() - 1 ? "" : "/";
				sFtpHelper.put(inputStream, autoExport.getFilePath() + slash + exportFile.getName(), ChannelSftp.OVERWRITE);
				sFtpHelper.close();
			} catch (Exception e) {
				throw new Exception("Cannot write file to sftp server", e);
			}

            // save flag that this export was executed
            autoExport.setExecuted(true);
            saveAutoExport(autoExport);

        } catch (Exception ex) {
            String errorMail = SafeString.getLocaleString("autoExport.error.reason", admin.getLocale()) + ": " +
                    ex.getMessage() + "\n\n" + autoExportInfo;
            AgnUtils.sendEmail(AgnUtils.getDefaultValue("export.report.from.address"), AgnUtils.getDefaultValue("export.report.to.address"),
                    SafeString.getLocaleString("autoExport.error", admin.getLocale()), errorMail, errorMail, 0, "UTF-8");
            logger.error("Auto Export - error while transferring file to sftp", ex);
        } finally {
            if (sFtpHelper != null) {
                sFtpHelper.close();
            }
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(inputStream);
        }
    }

    public void setAutoActivationDateAndActivate(@VelocityCheck int companyId, int autoExportId, Date date) {
        AutoExport autoExport = autoExportDao.getAutoExport(autoExportId, companyId);
        autoExport.setAutoActivationDate(date);
        autoExport.setOneTime(true);
        autoExport.setActive(true);
        autoExport.setExecuted(false);
        autoExportDao.updateAutoExport(autoExport);
    }

    public void deactivateAutoExport(@VelocityCheck int companyId, int autoExportId) {
        AutoExport autoExport = autoExportDao.getAutoExport(autoExportId, companyId);
        autoExport.setActive(false);
        autoExportDao.updateAutoExport(autoExport);
    }

    private String generateAutoExportInfoForReportMail(AutoExport autoExport, ExportPredef profile, Locale locale) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(SafeString.getLocaleString("autoExport.autoExport", locale)).append(": ").append(autoExport.getShortname()).append("\n");
        stringBuilder.append(SafeString.getLocaleString("autoImport.filePath", locale)).append(": ").append(autoExport.getFilePath()).append("\n");
        stringBuilder.append(SafeString.getLocaleString("autoImport.fileServer", locale)).append(": ").append(autoExport.getFileServerForLog()).append("\n");
        stringBuilder.append(SafeString.getLocaleString("export.ExportProfile", locale)).append(": ").append(profile.getShortname()).append("\n");
        Calendar calendar = GregorianCalendar.getInstance();
        DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, locale);
        String exportTime = dateFormat.format(calendar.getTime());
        stringBuilder.append(SafeString.getLocaleString("autoImport.performedAt", locale)).append(": ").append(exportTime).append("\n");
        return stringBuilder.toString();
    }
    
    public void saveAutoExport(AutoExport autoExport) {
        if (autoExport.getAutoExportId() == 0) {
            autoExportDao.createAutoExport(autoExport);
        }
        else {
            autoExportDao.updateAutoExport(autoExport);
        }
    }
    
    public void changeAutoExportActiveStatus(int autoExportId, int companyId, boolean active) {
        autoExportDao.changeActiveStatus(autoExportId, companyId, active);
    }

    public AutoExport getAutoExport(int autoExportId, int companyId) {
        return autoExportDao.getAutoExport(autoExportId, companyId);
    }

    public List<ExportPredef> getExportProfiles(int companyId) {
        return exportPredefDao.getAllByCompany(companyId);
    }

    public void deleteAutoExport(int autoExportId, int companyId) {
        autoExportDao.deleteAutoExport(autoExportId, companyId);
    }

    public void setAutoExportDao(AutoExportDao autoExportDao) {
        this.autoExportDao = autoExportDao;
    }

    public void setExportPredefDao(ExportPredefDao exportPredefDao) {
        this.exportPredefDao = exportPredefDao;
    }

    public void setAdminDao(AdminDao adminDao) {
        this.adminDao = adminDao;
    }

    public void setExportWizardService(ExportWizardService exportWizardService) {
        this.exportWizardService = exportWizardService;
    }

}
