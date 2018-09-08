package org.agnitas.emm.core.autoexport.forms;

import org.agnitas.emm.core.autoexport.bean.AutoExport;
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

public class AutoExportForm extends StrutsFormBase {

    private int autoExportId;

    private AutoExport autoExport = new AutoExport();

    private boolean activeStatus;
	
	private String connectionStatusKey = null;

    Set<Integer> weekDays = new HashSet<Integer>();
    private List<Integer> weekDaysTime = new ArrayList<Integer>();

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
    }

    @Override
    public ActionErrors formSpecificValidate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        String method = request.getParameter("method");
        if ("save".equals(method)) {
            if (autoExport.getShortname().length() < 3) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.nameToShort"));
            }
            if (autoExport.getFileServer().isEmpty()) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("autoImport.error.noFileName"));
            }
            if (autoExport.getFilePath().isEmpty()) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("autoImport.error.noServerPath"));
            }
            if (autoExport.isOneTime()) {
                if (weekDays.size() > 1) {
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("autoImport.error.tooManyDaysSelected"));
                }
            }
            if (weekDays.isEmpty()) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("autoExport.error.noDaySelected"));
            }
        }
        return errors;
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

    public AutoExport getAutoExport() {
        return autoExport;
    }

    public void setAutoExport(AutoExport autoExport) {
        this.autoExport = autoExport;
    }

    public int getAutoExportId() {
        return autoExportId;
    }

    public void setAutoExportId(int autoExportId) {
        this.autoExportId = autoExportId;
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
