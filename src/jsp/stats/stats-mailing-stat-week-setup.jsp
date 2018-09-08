<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.util.EmmCalendar, org.agnitas.web.MailingStatForm, java.util.*"  errorPage="/error.jsp" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>

<agn:Permission token="stats.rdir"/>

<% // date formats:
    java.text.SimpleDateFormat format01 = new java.text.SimpleDateFormat("yyyyMMdd");
    java.text.SimpleDateFormat format02 = new java.text.SimpleDateFormat("dd.MM.yy");

    // for csv-download:
    String statfile = "";
    EmmCalendar my_calendar = new EmmCalendar(java.util.TimeZone.getDefault());
    java.util.Date my_time = my_calendar.getTime();
    String Datum = my_time.toString();
    String timekey = Long.toString(my_time.getTime());
    request.setAttribute("time_key", timekey);
    Integer tmpMailingID = 0;
    Integer tmpTargetID = 0;
    Integer tmpUrlID = 0;
    Integer tmpMaxblue = 0;
    String tmpStartdate = "no";
    String aktURL = "";
    String tmpNetto = "no";
    Map<Object, Object> tmpValues = null;
    String tmpShortname = new String("");
    MailingStatForm aForm = (MailingStatForm) session.getAttribute("mailingStatForm");
    if (aForm != null) {
        tmpValues = (Map<Object, Object>) aForm.getValues();
        tmpMaxblue = aForm.getMaxblue();
        tmpMailingID = aForm.getMailingID();
        tmpTargetID = aForm.getTargetID();
        tmpShortname = aForm.getMailingShortname();
        tmpUrlID = aForm.getUrlID();
        aktURL = aForm.getAktURL();
        statfile = aForm.getCsvfile();
        if (aForm.isNetto())
            tmpNetto = "on";
        if (aForm.getStartdate().compareTo("no") != 0)
            tmpStartdate = aForm.getStartdate();
    }

    // map for the csv download
    Map<Object, Object> my_map = null;
    if (pageContext.getSession().getAttribute("map") == null) {
        my_map = new Hashtable<Object, Object>();
        pageContext.getSession().setAttribute("map", my_map);
    } else {
        my_map = (Map<Object, Object>) (pageContext.getSession().getAttribute("map"));
    }

    request.setAttribute("tmpMailingID", tmpMailingID);
    request.setAttribute("tmpTargetID", tmpTargetID);
    request.setAttribute("tmpUrlID", tmpUrlID);
    request.setAttribute("tmpMaxblue", tmpMaxblue);
    request.setAttribute("tmpStartdate", tmpStartdate);
    request.setAttribute("aktURL", aktURL);
    request.setAttribute("statfile", statfile);
    request.setAttribute("tmpValues", tmpValues);
    request.setAttribute("my_calendar", my_calendar);
%>

<% request.setAttribute("sidemenu_active", new String("Mailings")); %>
<% request.setAttribute("sidemenu_sub_active", new String("none")); %>
<% request.setAttribute("agnTitleKey", new String("Mailing")); %>
<% request.setAttribute("agnSubtitleKey", new String("Mailing")); %>
<% request.setAttribute("agnSubtitleValue", tmpShortname); %>
<% request.setAttribute("agnNavigationKey", new String("mailingView")); %>
<% request.setAttribute("agnHighlightKey", new String("Statistics")); %>
<% request.setAttribute("agnNavHrefAppend", new String("&mailingID=" + tmpMailingID)); %>
<c:set var="agnHelpKey" value="feedbackAnalysis" scope="request" />
