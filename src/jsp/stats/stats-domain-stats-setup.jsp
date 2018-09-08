<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.util.AgnUtils, org.agnitas.beans.Admin, org.agnitas.web.forms.DomainStatForm, java.util.*" buffer="32kb"  errorPage="/error.jsp" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>

<agn:Permission token="stats.domains"/>

<% request.setAttribute("sidemenu_active", new String("Statistics")); %>
<% request.setAttribute("sidemenu_sub_active", new String("statistic.domains")); %>
<% request.setAttribute("agnTitleKey", new String("statistic.domains")); %>
<% request.setAttribute("agnSubtitleKey", new String("Statistics")); %>
<% request.setAttribute("agnNavigationKey", new String("statsDomain")); %>
<% request.setAttribute("agnHighlightKey", new String("statistic.domains")); %>
<c:set var="agnHelpKey" value="domainStatistic" scope="request" />

<%
    // key for the csv download
    java.util.TimeZone tz = TimeZone.getTimeZone(((Admin) session.getAttribute("emm.admin")).getAdminTimezone());
    java.util.GregorianCalendar aCal = new java.util.GregorianCalendar(tz);
    java.util.Date my_time = aCal.getTime();
    String Datum = my_time.toString();
    String timekey = Long.toString(my_time.getTime());

	// map for the csv download
    Map<String, String> my_map = null;
    if (pageContext.getSession().getAttribute("map") == null) {
        my_map = new Hashtable<String, String>();
        pageContext.getSession().setAttribute("map", my_map);
    } else {
        my_map = (Map<String, String>) (pageContext.getSession().getAttribute("map"));
    }

	// put csv file from the form in the hash table:
    String file = ((DomainStatForm) (session.getAttribute("domainStatForm"))).getCsvfile();
    my_map.put(timekey, file);
    request.setAttribute("timekey", timekey);
    pageContext.getSession().setAttribute("map", my_map);

%>

