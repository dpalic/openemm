package org.agnitas.emm.core.autoimport.web;

import org.agnitas.beans.ImportProfile;
import org.agnitas.beans.Mailinglist;
import org.agnitas.emm.core.autoimport.bean.AutoImport;
import org.agnitas.emm.core.autoimport.forms.AutoImportForm;
import org.agnitas.emm.core.autoimport.service.AutoImportService;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.SFtpHelper;
import org.agnitas.util.SafeString;
import org.agnitas.web.DispatchBaseAction;
import org.apache.struts.action.*;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AutoImportAction extends DispatchBaseAction {

	private AutoImportService autoImportService;

	public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!AgnUtils.isUserLoggedIn(request)) {
			return mapping.findForward("logon");
		}
		AutoImportForm autoImportForm = (AutoImportForm) form;
		setNumberOfRows(request, autoImportForm);
		if (autoImportForm.getColumnwidthsList() == null) {
			autoImportForm.setColumnwidthsList(getInitializedColumnWidthList(5));
		}
		request.setAttribute ("autoImports" , autoImportService.getAutoImportsOverview(AgnUtils.getCompanyID(request)));
		return mapping.findForward("list");
	}

	public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!AgnUtils.isUserLoggedIn(request)) {
			return mapping.findForward("logon");
		}
		AutoImportForm autoImportForm = (AutoImportForm) form;
		autoImportForm.setAutoImportId(0);
		autoImportForm.clearLists();
		AutoImport autoImport = new AutoImport();
		autoImport.setCompanyId(AgnUtils.getCompanyID(request));
		autoImport.setAdminId(AgnUtils.getAdmin(request).getAdminID());
		autoImportForm.setAutoImport(autoImport);
        autoImportForm.setConnectionStatusKey(null);
		prepareViewPage(form, request);
		return mapping.findForward("view");
	}

	// @todo: this method should be removed as the import should only be run by job worker
	public ActionForward doimport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!AgnUtils.isUserLoggedIn(request)) {
			return mapping.findForward("logon");
		}
		AutoImportForm autoImportForm = (AutoImportForm) form;
		autoImportService.doImport(autoImportForm.getAutoImportId(), AgnUtils.getCompanyID(request), WebApplicationContextUtils.getWebApplicationContext(request.getSession().getServletContext()));
		return list(mapping, form, request, response);
	}

	public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ActionMessages errors = new ActionErrors();
    	
		if (!AgnUtils.isUserLoggedIn(request)) {
			return mapping.findForward("logon");
		}
		AutoImportForm autoImportForm = (AutoImportForm) form;
        saveAutoImport(autoImportForm, errors);

        return prepareViewForwardAfterSaving(mapping, form, request, autoImportForm, errors);
	}

	public ActionForward checkConnectionStatus(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!AgnUtils.isUserLoggedIn(request)) {
			return mapping.findForward("logon");
		}
		AutoImportForm autoImportForm = (AutoImportForm) form;
		SFtpHelper sftpHelper = null;
		boolean connectionStatusOK = false;
		try {
			sftpHelper = new SFtpHelper(autoImportForm.getAutoImport().getFileServer());
			if (autoImportForm.getAutoImport().isAllowUnknownHostKeys()) {
				sftpHelper.setAllowUnknownHostKeys(true);
			}
			sftpHelper.connect();
			connectionStatusOK = true;
		} catch (Exception e) {
			connectionStatusOK = false;
		} finally {
			if (sftpHelper != null) {
				sftpHelper.close();
			}
		}
		
		loadAutoImport(form, request);
		prepareViewPage(form, request);
		autoImportForm.setConnectionStatusKey(connectionStatusOK ? "autoImportExport.connectionStatus.ok" : "autoImportExport.connectionStatus.notOk");
		
		return mapping.findForward("view");
	}

    protected ActionForward prepareViewForwardAfterSaving(ActionMapping mapping, ActionForm form, HttpServletRequest request, AutoImportForm autoImportForm, ActionMessages errors) {
    	if (!errors.isEmpty()) {
			saveErrors(request, errors);
		} else {
	        ActionMessages messages = new ActionMessages();
	        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("changes_saved"));
	        saveMessages(request, messages);
		}

        autoImportForm.setAutoImportId(autoImportForm.getAutoImport().getAutoImportId());
        loadAutoImport(form, request);
        prepareViewPage(form, request);
        return mapping.findForward("view");
    }

    protected void saveAutoImport(AutoImportForm autoImportForm, ActionMessages errors) {
        ArrayList<AutoImport.ImportTime> importTimes = new ArrayList<AutoImport.ImportTime>();
        for (Integer weekDay : autoImportForm.getWeekDays()) {
            AutoImport.ImportTime importTime = new AutoImport.ImportTime();
            importTime.setDayOfWeek(weekDay);
            importTime.setHour(autoImportForm.getWeekDaysTime().get(weekDay));
            importTimes.add(importTime);
        }
        autoImportForm.getAutoImport().setTimes(importTimes);

        ArrayList<Integer> mailinglists = new ArrayList<Integer>();
        mailinglists.addAll(autoImportForm.getMailinglists());
        autoImportForm.getAutoImport().setMailinglists(mailinglists);
        
        // Check for input data errors
        if (mailinglists.size() < 1) {
        	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.select_mailinglist"));
        } else {
        	autoImportService.saveAutoImport(autoImportForm.getAutoImport());
        }
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
		loadAutoImport(form, request);
		prepareViewPage(form, request);
		return mapping.findForward("view");
	}

	public ActionForward changeActiveStatus(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!AgnUtils.isUserLoggedIn(request)) {
			return mapping.findForward("logon");
		}

		AutoImportForm autoImportForm = (AutoImportForm) form;
		autoImportService.changeAutoImportActiveStatus(autoImportForm.getAutoImportId(), AgnUtils.getCompanyID(request), autoImportForm.isActiveStatus());
		ActionMessages messages = new ActionMessages();
		messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("changes_saved"));
		saveMessages(request, messages);
		return view(mapping, form, request, response);
	}

	public ActionForward deleteconfirm(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!AgnUtils.isUserLoggedIn(request)) {
			return mapping.findForward("logon");
		}
		AutoImportForm autoImportForm = (AutoImportForm) form;
		autoImportService.deleteAutoImport(autoImportForm.getAutoImportId(), AgnUtils.getCompanyID(request));
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
		List<ImportProfile> importProfiles = autoImportService.getImportProfiles(AgnUtils.getCompanyID(request));
		request.setAttribute("importProfiles", importProfiles);
		List<Mailinglist> mailinglists = autoImportService.getMailinglists(AgnUtils.getCompanyID(request));
		request.setAttribute("mailinglists", mailinglists);
		request.setAttribute("firstDayOfWeek", Calendar.getInstance(AgnUtils.getLocale(request)).getFirstDayOfWeek());
        request.setAttribute("isProjectOpenEMM", AgnUtils.isProjectOpenEMM());
	}

	protected void loadAutoImport(ActionForm form, HttpServletRequest request) {
		AutoImportForm autoImportForm = (AutoImportForm) form;
        if (autoImportForm.getAutoImportId() > 0) {
            AutoImport autoImport = autoImportService.getAutoImport(autoImportForm.getAutoImportId(), AgnUtils.getCompanyID(request));
            autoImportForm.setAutoImport(autoImport);
            autoImportForm.clearLists();
            for (AutoImport.ImportTime importTime : autoImport.getTimes()) {
                autoImportForm.getWeekDays().add(importTime.getDayOfWeek());
                autoImportForm.getWeekDaysTime().set(importTime.getDayOfWeek(), importTime.getHour());
            }
            autoImportForm.getMailinglists().addAll(autoImport.getMailinglists());
        }
        //we have to reset connection status key for avoiding restoring value from session and displaying not valid message
        autoImportForm.setConnectionStatusKey(null);
	}

	public void setAutoImportService(AutoImportService autoImportService) {
		this.autoImportService = autoImportService;
	}
}
