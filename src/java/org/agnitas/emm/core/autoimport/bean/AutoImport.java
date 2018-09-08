package org.agnitas.emm.core.autoimport.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class AutoImport implements Serializable {

	private int autoImportId;
	private int companyId;
	private int adminId;
	private int importProfileId;
	private String shortname;
	private String description;
	private String filePath;
	private String fileServer;
	private boolean allowUnknownHostKeys = false;
	private boolean oneTime;
	private boolean autoResume;
	private boolean executed;
	private boolean active;
	private Date autoActivationDate;

	private List<Integer> mailinglists = new ArrayList<Integer>();
	private List<ImportTime> times = new ArrayList<ImportTime>();

	public int getAutoImportId() {
		return autoImportId;
	}

	public void setAutoImportId(int autoImportId) {
		this.autoImportId = autoImportId;
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

	public int getImportProfileId() {
		return importProfileId;
	}

	public void setImportProfileId(int importProfileId) {
		this.importProfileId = importProfileId;
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

	public boolean isAutoResume() {
		return autoResume;
	}

	public void setAutoResume(boolean autoResume) {
		this.autoResume = autoResume;
	}

	public boolean isExecuted() {
		return executed;
	}

	public void setExecuted(boolean executed) {
		this.executed = executed;
	}

	public List<Integer> getMailinglists() {
		return mailinglists;
	}

	public void setMailinglists(List<Integer> mailinglists) {
		this.mailinglists = mailinglists;
	}

	public List<ImportTime> getTimes() {
		return times;
	}

	public void setTimes(List<ImportTime> times) {
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

    public static class ImportTime {
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
            if (object == null || !(object instanceof ImportTime)) {
                return false;
            } else {
                ImportTime importTime = (ImportTime) object;
                return this.dayOfWeek == importTime.dayOfWeek && this.hour == importTime.hour;
            }
        }
	}


	public static class UsedFile {
		private long fileSize;
		private Date fileDate;

		public long getFileSize() {
			return fileSize;
		}

		public void setFileSize(long fileSize) {
			this.fileSize = fileSize;
		}

		public Date getFileDate() {
			return fileDate;
		}

		public void setFileDate(Date fileDate) {
			this.fileDate = fileDate;
		}

	}
}
