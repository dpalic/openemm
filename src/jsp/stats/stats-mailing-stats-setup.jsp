<%@ page language="java" import="org.agnitas.web.MailingStatAction" contentType="text/html; charset=utf-8"
         buffer="32kb"  errorPage="/error.jsp" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>

<agn:Permission token="stats.mailing"/>

<% request.setAttribute("sidemenu_active", new String("Statistics")); %>
<% request.setAttribute("sidemenu_sub_active", new String("statistic.MailStat")); %>
<% request.setAttribute("agnTitleKey", new String("Statistics")); %>
<% request.setAttribute("agnSubtitleKey", new String("Statistics")); %>
<% request.setAttribute("agnNavigationKey", new String("statsMailing")); %>
<% request.setAttribute("agnHighlightKey", new String("statistic.MailStat")); %>
<% request.setAttribute("ACTION_LIST", MailingStatAction.ACTION_LIST); %>
<% request.setAttribute("ACTION_MAILINGSTAT", MailingStatAction.ACTION_MAILINGSTAT); %>
<c:set var="agnHelpKey" value="mailingStatistic" scope="request" />
