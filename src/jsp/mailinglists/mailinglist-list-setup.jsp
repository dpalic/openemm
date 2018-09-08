<%@ page language="java" contentType="text/html; charset=utf-8"  errorPage="/error.jsp" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>

<agn:Permission token="mailinglist.show"/>

<% request.setAttribute("sidemenu_active", new String("Mailinglists")); %>
<% request.setAttribute("sidemenu_sub_active", new String("default.Overview")); %>
<% request.setAttribute("agnNavigationKey", new String("MailinglistsOverview")); %>
<% request.setAttribute("agnHighlightKey", new String("default.Overview")); %>
<% request.setAttribute("agnTitleKey", new String("Mailinglists")); %>
<% request.setAttribute("agnSubtitleKey", new String("Mailinglists")); %>
<c:set var="agnHelpKey" value="mailinglists" scope="request" />
