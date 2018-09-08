<%@ page language="java"
         import="org.agnitas.beans.Admin, org.agnitas.web.MailingStatForm, java.util.GregorianCalendar, java.util.*"
         contentType="text/html; charset=utf-8"  errorPage="/error.jsp" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>

<% request.setAttribute("sidemenu_active", new String("Statistics")); %>
<% request.setAttribute("sidemenu_sub_active", new String("Mailing")); %>
<% request.setAttribute("agnTitleKey", new String("Mailing")); %>
<% request.setAttribute("agnSubtitleKey", new String("Statistics")); %>
<% request.setAttribute("agnNavigationKey", new String("mailingView")); %>
<% request.setAttribute("agnHighlightKey", new String("Statistics")); %>
<c:set var="agnHelpKey" value="feedbackAnalysis" scope="request" />

<%
    // key for the csv download
    TimeZone tz = TimeZone.getTimeZone(((Admin) session.getAttribute("emm.admin")).getAdminTimezone());
    GregorianCalendar aCal = new GregorianCalendar(tz);
    java.util.Date my_time = aCal.getTime();
    String timekey = Long.toString(my_time.getTime());
    request.setAttribute("timekey", timekey);

    MailingStatForm aForm = null;
    Integer tmpMailingID = 0;
    String shortname = "";
    if (session.getAttribute("mailingStatForm") != null) {
        aForm = (MailingStatForm) session.getAttribute("mailingStatForm");
        tmpMailingID = aForm.getMailingID();
        shortname = aForm.getMailingShortname();
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
    request.setAttribute("shortname", shortname);
    pageContext.getSession().setAttribute("map", my_map);
%>
