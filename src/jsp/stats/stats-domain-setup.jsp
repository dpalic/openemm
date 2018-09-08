<%@ page language="java" contentType="text/html; charset=utf-8" buffer="32kb"  errorPage="/error.jsp" %>
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
