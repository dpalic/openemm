package org.agnitas.emm.core.autoexport.web;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.beans.ExportPredef;
import org.agnitas.emm.core.autoexport.bean.AutoExport;
import org.agnitas.emm.core.autoexport.forms.AutoExportForm;
import org.agnitas.emm.core.autoexport.service.AutoExportService;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.FtpHelper;
import org.agnitas.util.SFtpHelper;
import org.agnitas.web.DispatchBaseAction;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class AutoExportAction  extends DispatchBaseAction {
    private static final transient Logger logger = Logger.getLogger(AutoExportAction.class);

    private AutoExportService autoExportService;

    public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!AgnUtils.isUserLoggedIn(request)) {
            return mapping.findForward("logon");
        }
        AutoExportForm autoExportForm = (AutoExportForm) form;
        setNumberOfRows(request, autoExportForm);
        if (autoExportForm.getColumnwidthsList() == null) {
            autoExportForm.setColumnwidthsList(getInitializedColumnWidthList(5));
        }
        request.setAttribute ("autoExports" , autoExportService.getAutoExportsOverview(AgnUtils.getCompanyID(request)));
        return mapping.findForward("list");
    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!AgnUtils.isUserLoggedIn(request)) {
            return mapping.findForward("logon");
        }
        AutoExportForm autoExportForm = (AutoExportForm) form;
        saveAutoExport(autoExportForm);

        return prepareViewForwardAfterSaving(mapping, form, request, autoExportForm);
    }

	public ActionForward checkConnectionStatus(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!AgnUtils.isUserLoggedIn(request)) {
			return mapping.findForward("logon");
		}
		AutoExportForm autoExportForm = (AutoExportForm) form;
		boolean connectionStatusOK = false;
		
		if (autoExportForm.getAutoExport().getFileServer().toLowerCase().startsWith("ftp://")) {
        	// if configured so, send via FTP
			FtpHelper ftpHelper = null;
			try {
				ftpHelper = new FtpHelper(autoExportForm.getAutoExport().getFileServer());
				ftpHelper.connect();
				connectionStatusOK = true;
			} catch (Exception e) {
				logger.error("AutoExportAction - error while connecting to ftp", e);
				connectionStatusOK = false;
			} finally {
				if (ftpHelper != null) {
					ftpHelper.close();
				}
			}
        } else {
        	// default: send via SFTP
        	SFtpHelper sftpHelper = null;
    		try {
    			sftpHelper = new SFtpHelper(autoExportForm.getAutoExport().getFileServer());
    			if (autoExportForm.getAutoExport().isAllowUnknownHostKeys()) {
    				sftpHelper.setAllowUnknownHostKeys(true);
    			}
    			sftpHelper.connect();
    			connectionStatusOK = true;
    		} catch (Exception e) {
    			logger.error("AutoExportAction - error while connecting to sftp", e);
    			connectionStatusOK = false;
    		} finally {
    			if (sftpHelper != null) {
    				sftpHelper.close();
    			}
    		}
        }
		
		loadAutoExport(form, request);
		prepareViewPage(form, request);
		autoExportForm.setConnectionStatusKey(connectionStatusOK ? "autoImportExport.connectionStatus.ok" : "autoImportExport.connectionStatus.notOk");
		
		return mapping.findForward("view");
	}

    protected ActionForward prepareViewForwardAfterSaving(ActionMapping mapping, ActionForm form, HttpServletRequest request, AutoExportForm autoExportForm) {
        ActionMessages messages = new ActionMessages();
        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("changes_saved"));
        saveMessages(request, messages);

        autoExportForm.setAutoExportId(autoExportForm.getAutoExport().getAutoExportId());
        loadAutoExport(form, request);
        prepareViewPage(form, request);
        return mapping.findForward("view");
    }

    protected void saveAutoExport(AutoExportForm autoExportForm) {
        ArrayList<AutoExport.ExportTime> exportTimes = new ArrayList<AutoExport.ExportTime>();
        for (Integer weekDay : autoExportForm.getWeekDays()) {
            AutoExport.ExportTime exportTime = new AutoExport.ExportTime();
            exportTime.setDayOfWeek(weekDay);
            exportTime.setHour(autoExportForm.getWeekDaysTime().get(weekDay));
            exportTimes.add(exportTime);
        }
        autoExportForm.getAutoExport().setTimes(exportTimes);

        autoExportService.saveAutoExport(autoExportForm.getAutoExport());
    }

    // @todo: this method should be removed as the export should only be run by job worker
    public ActionForward doExport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!AgnUtils.isUserLoggedIn(request)) {
            return mapping.findForward("logon");
        }
        AutoExportForm autoExportForm = (AutoExportForm) form;
        autoExportService.doExport(autoExportForm.getAutoExportId(), AgnUtils.getCompanyID(request), WebApplicationContextUtils.getWebApplicationContext(request.getSession().getServletContext()));
        return list(mapping, form, request, response);
    }

    public ActionForward changeActiveStatus(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!AgnUtils.isUserLoggedIn(request)) {
            return mapping.findForward("logon");
        }

        AutoExportForm autoExportForm = (AutoExportForm) form;
        autoExportService.changeAutoExportActiveStatus(autoExportForm.getAutoExportId(), AgnUtils.getCompanyID(request), autoExportForm.isActiveStatus());
        ActionMessages messages = new ActionMessages();
        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("changes_saved"));
        saveMessages(request, messages);
        return view(mapping, form, request, response);
    }

    public ActionForward error(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!AgnUtils.isUserLoggedIn(request)) {
            return mapping.findForward("logon");
        }
        prepareViewPage(form, request);
        return mapping.findForward("view");
    }
    
    public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!AgnUtils.isUserLoggedIn(request)) {
            return mapping.findForward("logon");
        }
        loadAutoExport(form, request);
        prepareViewPage(form, request);
        return mapping.findForward("view");
    }

    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!AgnUtils.isUserLoggedIn(request)) {
            return mapping.findForward("logon");
        }
        AutoExportForm autoExportForm = (AutoExportForm) form;
        autoExportForm.setAutoExportId(0);
        autoExportForm.clearLists();
        AutoExport autoExport = new AutoExport();
        autoExport.setCompanyId(AgnUtils.getCompanyID(request));
        autoExport.setAdminId(AgnUtils.getAdmin(request).getAdminID());
        autoExportForm.setAutoExport(autoExport);
        autoExportForm.setConnectionStatusKey(null);
        prepareViewPage(form, request);
        return mapping.findForward("view");
    }

    protected void loadAutoExport(ActionForm form, HttpServletRequest request) {
        AutoExportForm autoExportForm = (AutoExportForm) form;
        if (autoExportForm.getAutoExportId() > 0) {
            AutoExport autoExport = autoExportService.getAutoExport(autoExportForm.getAutoExportId(), AgnUtils.getCompanyID(request));
            autoExportForm.setAutoExport(autoExport);
            autoExportForm.clearLists();
            for (AutoExport.ExportTime exportTime : autoExport.getTimes()) {
                autoExportForm.getWeekDays().add(exportTime.getDayOfWeek());
                autoExportForm.getWeekDaysTime().set(exportTime.getDayOfWeek(), exportTime.getHour());
            }
        }
        //we have to reset connection status key for avoiding restoring value from session and displaying not valid message
        autoExportForm.setConnectionStatusKey(null);
    }

    public ActionForward deleteconfirm(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!AgnUtils.isUserLoggedIn(request)) {
            return mapping.findForward("logon");
        }
        AutoExportForm autoExportForm = (AutoExportForm) form;
        autoExportService.deleteAutoExport(autoExportForm.getAutoExportId(), AgnUtils.getCompanyID(request));
        return list(mapping, form, request, response);
    }

    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!AgnUtils.isUserLoggedIn(request)) {
            return mapping.findForward("logon");
        }
        request.setAttribute("fromListPage", request.getParameter("fromListPage"));
        return mapping.findForward("delete");
    }
     
    protected void prepareViewPage(ActionForm form, HttpServletRequest request) {
        List<ExportPredef> exportProfiles = autoExportService.getExportProfiles(AgnUtils.getCompanyID(request));
        request.setAttribute("exportProfiles", exportProfiles);
        request.setAttribute("firstDayOfWeek", Calendar.getInstance(AgnUtils.getLocale(request)).getFirstDayOfWeek());
        request.setAttribute("isProjectOpenEMM", AgnUtils.isProjectOpenEMM());
    }

    public void setAutoExportService(AutoExportService autoExportService) {
        this.autoExportService = autoExportService;
    }

}
