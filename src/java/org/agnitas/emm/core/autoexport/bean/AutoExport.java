package org.agnitas.emm.core.autoexport.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class AutoExport  implements Serializable {

    private int autoExportId;
    private int companyId;
    private int adminId;
    private int exportProfileId;
    private String shortname;
    private String description;
    private String filePath;
    private String fileServer;
	private boolean allowUnknownHostKeys = false;
    private boolean oneTime;
    private boolean executed;
    private boolean active;
    private Date autoActivationDate;

    private List<ExportTime> times = new ArrayList<ExportTime>();

    public int getAutoExportId() {
        return autoExportId;
    }

    public void setAutoExportId(int autoExportId) {
        this.autoExportId = autoExportId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public int getExportProfileId() {
        return exportProfileId;
    }

    public void setExportProfileId(int exportProfileId) {
        this.exportProfileId = exportProfileId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileServer() {
        return fileServer;
    }

    public String getFileServerForLog() {
    	if (StringUtils.isNotBlank(fileServer) && fileServer.contains("@")) {
    		return fileServer.substring(fileServer.indexOf("@") + 1);
    	} else {
    		return fileServer;
    	}
    }

    public void setFileServer(String fileServer) {
        this.fileServer = fileServer;
    }

    public boolean isOneTime() {
        return oneTime;
    }

    public void setOneTime(boolean oneTime) {
        this.oneTime = oneTime;
    }

    public boolean isExecuted() {
        return executed;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

    public List<ExportTime> getTimes() {
        return times;
    }

    public void setTimes(List<ExportTime> times) {
        this.times = times;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getAutoActivationDate() {
        return autoActivationDate;
    }

    public void setAutoActivationDate(Date autoActivationDate) {
        this.autoActivationDate = autoActivationDate;
    }

	public boolean isAllowUnknownHostKeys() {
		return allowUnknownHostKeys;
	}

	public void setAllowUnknownHostKeys(boolean allowUnknownHostKeys) {
		this.allowUnknownHostKeys = allowUnknownHostKeys;
	}

    public static class ExportTime {
        private int dayOfWeek;
        private int hour;

        public int getDayOfWeek() {
            return dayOfWeek;
        }

        public void setDayOfWeek(int dayOfWeek) {
            this.dayOfWeek = dayOfWeek;
        }

        public int getHour() {
            return hour;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public boolean equals(Object object) {
            // According to Object.equals(Object), equals(null) returns false
            if (object == null || !(object instanceof ExportTime)) {
                return false;
            } else {
                ExportTime exportTime = (ExportTime) object;
                return this.dayOfWeek == exportTime.dayOfWeek && this.hour == exportTime.hour;
            }
        }
    }
}
