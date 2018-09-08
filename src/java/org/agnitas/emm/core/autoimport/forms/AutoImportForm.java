package org.agnitas.emm.core.autoimport.forms;

import org.agnitas.emm.core.autoimport.bean.AutoImport;
import org.agnitas.web.forms.StrutsFormBase;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AutoImportForm extends StrutsFormBase {

	private int autoImportId;

	private AutoImport autoImport = new AutoImport();

	private Set<Integer> weekDays = new HashSet<Integer>();
	private List<Integer> weekDaysTime = new ArrayList<Integer>();

	private Set<Integer> mailinglists = new HashSet<Integer>();

	private boolean activeStatus;
	
	private String connectionStatusKey = null;

	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
	}

	public void clearLists() {
		weekDays.clear();
		weekDaysTime.clear();
		for (int i = 0; i <= 7; i++){
		    weekDaysTime.add(0);
		}
		mailinglists.clear();
	}

	@Override
	public ActionErrors formSpecificValidate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		String method = request.getParameter("method");
		if ("save".equals(method)) {
			if (autoImport.getShortname().length() < 3) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.nameToShort"));
			}
			if (autoImport.getFileServer().isEmpty()) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("autoImport.error.noFileName"));
			}
			if (autoImport.getFilePath().isEmpty()) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("autoImport.error.noServerPath"));
			}
			if (autoImport.isOneTime()) {
				if (weekDays.size() > 1) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("autoImport.error.tooManyDaysSelected"));
				}
			}
			if (weekDays.isEmpty()) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("autoImport.error.noDaySelected"));
			}
		}
		return errors;
	}

	public void setMailinglist(int id, String value) {
		if (value != null && (value.equals("on") || value.equals("yes") || value.equals("true"))) {
			mailinglists.add(id);
		}
		else {
			mailinglists.remove(id);
		}
	}

	public String getMailinglist(int id) {
		return mailinglists.contains(id) ? "on" : "";
	}

	public Set<Integer> getMailinglists() {
		return mailinglists;
	}

	public void setWeekDay(int id, String value) {
		if (value != null && (value.equals("on") || value.equals("yes") || value.equals("true"))) {
			weekDays.add(id);
		}
		else {
			weekDays.remove(id);
		}
	}

	public String getWeekDay(int id) {
		return weekDays.contains(id) ? "on" : "";
	}

	public Set<Integer> getWeekDays() {
		return weekDays;
	}

	public void setWeekDayTime(int id, String value) {
		weekDaysTime.set(id, Integer.parseInt(value));
	}

	public String getWeekDayTime(int id) {
		return weekDaysTime.get(id).toString();
	}

	public List<Integer> getWeekDaysTime() {
		return weekDaysTime;
	}

	public AutoImport getAutoImport() {
		return autoImport;
	}

	public void setAutoImport(AutoImport autoImport) {
		this.autoImport = autoImport;
	}

	public int getAutoImportId() {
		return autoImportId;
	}

	public void setAutoImportId(int autoImportId) {
		this.autoImportId = autoImportId;
	}

	public boolean isActiveStatus() {
		return activeStatus;
	}

	public void setActiveStatus(boolean activeStatus) {
		this.activeStatus = activeStatus;
	}

	public String getConnectionStatusKey() {
		return connectionStatusKey;
	}

	public void setConnectionStatusKey(String connectionStatusKey) {
		this.connectionStatusKey = connectionStatusKey;
	}
}
