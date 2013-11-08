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
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/
package org.agnitas.web;

import org.agnitas.service.UserActivityLogQueryWorker;
import org.agnitas.service.UserActivityLogService;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.forms.UserActivityLogForm;
import org.agnitas.dao.AdminDao;
import org.agnitas.beans.Admin;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.displaytag.pagination.PaginatedList;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author Viktor Gema
 */
public class UserActivityLogAction extends StrutsActionBase {

    public static final String FUTURE_TASK = "USER_ACTIVITY_LOG_LIST";

    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form
     * @param req
     * @param res
     * @return destination
     * @throws java.io.IOException            if an input/output error occurs
     * @throws javax.servlet.ServletException if a servlet exception occurs
     */
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest req,
                                 HttpServletResponse res)
            throws IOException, ServletException {

        AbstractMap<String, Future> futureHolder = (AbstractMap<String, Future>) getBean("futureHolder");
        String futureKey = FUTURE_TASK + "@" + req.getSession(false).getId();
        ApplicationContext aContext = this.getWebApplicationContext();
        UserActivityLogForm aForm = null;
        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();
        ActionForward destination = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Admin admin = AgnUtils.getAdmin(req);
        SimpleDateFormat localeFormat = getLocaleFormat(admin);
        if (!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }

        if (form != null) {
            aForm = (UserActivityLogForm) form;
            if (StringUtils.isEmpty(aForm.getFromDate())) {
                aForm.setFromDate(localeFormat.format(new Date()));
            }
            if (StringUtils.isEmpty(aForm.getToDate())) {
                aForm.setToDate(localeFormat.format(new Date()));
            }
            if (StringUtils.isEmpty(aForm.getUsername())) {
                if (!allowed("adminlog.show", req) && !allowed("masterlog.show", req)) {
                    aForm.setUsername(AgnUtils.getAdmin(req).getUsername());
                }
            }
        } else {
            aForm = new UserActivityLogForm();
            aForm.setFromDate(localeFormat.format(new Date()));
            aForm.setToDate(localeFormat.format(new Date()));
            if (!allowed("adminlog.show", req) && !allowed("masterlog.show", req)) {
                aForm.setUsername(AgnUtils.getAdmin(req).getUsername());
            }
        }

        AgnUtils.logger().info("Action: " + aForm.getAction());

        switch (aForm.getAction()) {
            case UserActivityLogAction.ACTION_LIST:
                if ( aForm.getColumnwidthsList() == null) {
                    	aForm.setColumnwidthsList(getInitializedColumnWidthList(5));
                    }
                if (allowed("userlog.show", req) || allowed("adminlog.show", req) || allowed("masterlog.show", req)) {
                    destination = mapping.findForward("list");
                } else {
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                }
                break;
            default:
                aForm.setAction(UserActivityLogAction.ACTION_LIST);
                destination = mapping.findForward("list");
        }
        if (destination != null && "list".equals(destination.getName())) {
            try {
                setNumberOfRows(req, aForm);
                destination = mapping.findForward("loading");

                if (!futureHolder.containsKey(futureKey)) {
                    // normalize dates by pattern "yyyy-MM-dd"
                    Date fromDate = localeFormat.parse(aForm.getFromDate());
                    aForm.setFromDate(dateFormat.format(fromDate));
                    Date toDate = localeFormat.parse(aForm.getToDate());
                    aForm.setToDate(dateFormat.format(toDate));
                    futureHolder.put(futureKey, getRecipientListFuture(req, aContext, aForm));
                }

                if (futureHolder.containsKey(futureKey) && futureHolder.get(futureKey).isDone()) {
                    req.setAttribute("userActivitylogList", futureHolder.get(futureKey).get());
                    //req.getSession().setAttribute("recipientsInCurrentTable", futureHolder.get(futureKeyList).get());
                    destination = mapping.findForward("list");
                    aForm.setAll(((PaginatedList) futureHolder.get(futureKey).get()).getFullListSize());
                    futureHolder.remove(futureKey);
                    aForm.setRefreshMillis(RecipientForm.DEFAULT_REFRESH_MILLIS);
                    Date fromDate = dateFormat.parse(aForm.getFromDate());
                    aForm.setFromDate(localeFormat.format(fromDate));
                    Date toDate = dateFormat.parse(aForm.getToDate());
                    aForm.setToDate(localeFormat.format(toDate));
                    SimpleDateFormat localeTableFormat = getLocaleTableFormat(admin);
                    req.setAttribute("localDatePattern", localeFormat.toPattern());
                    req.setAttribute("localeTablePattern", localeTableFormat.toPattern());
                } else {
                    if (aForm.getRefreshMillis() < 1000) { // raise the refresh time
                        aForm.setRefreshMillis(aForm.getRefreshMillis() + 50);
                    }
                    aForm.setError(false);
                }

            } catch (Exception e) {
                AgnUtils.logger().error("useractivitylogList: " + e + "\n" + AgnUtils.getStackTrace(e));
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
                aForm.setError(true); // do not refresh when an error has been occurred
            }
        }
        return destination;
    }

    /**
     * Get a list of logs according to your filter
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

    public Future getRecipientListFuture(HttpServletRequest request, ApplicationContext aContext, UserActivityLogForm aForm)
            throws NumberFormatException, IllegalAccessException, InstantiationException, InterruptedException, ExecutionException, IntrospectionException, InvocationTargetException {

        UserActivityLogService userActivityLogService = (UserActivityLogService) aContext.getBean("UserActivityLogService");
        String sort = getSort(request, aForm);
        String direction = request.getParameter("dir");
        int rownums = aForm.getNumberofRows();

        AdminDao adminDao = (AdminDao) aContext.getBean("AdminDao");
        List<Admin> admins = null;
        if (AgnUtils.allowed("masterlog.show", request)) {
            admins = adminDao.getAllAdmins();
        } else {
            admins = adminDao.getAllAdminsByCompanyId(AgnUtils.getCompanyID(request));
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
        ExecutorService service = (ExecutorService) aContext.getBean("workerExecutorService");
        Future future = service.submit(new UserActivityLogQueryWorker(userActivityLogService, AgnUtils.getAdmin(request).getAdminID(), Integer.parseInt(pageStr), rownums, aForm, sort, direction, admins));
        return future;

    }

    protected SimpleDateFormat getLocaleFormat(Admin admin) {
		Locale locale = admin.getLocale();
		return (SimpleDateFormat) SimpleDateFormat.getDateInstance( DateFormat.SHORT, locale);
	}

    protected SimpleDateFormat getLocaleTableFormat(Admin admin) {
		Locale locale = admin.getLocale();
		return (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale);
	}

}
