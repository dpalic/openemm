<%@ page language="java" contentType="text/html; charset=utf-8"  errorPage="/error.jsp" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>

<agn:Permission token="actions.show"/>

<% request.setAttribute("sidemenu_active", new String("SiteActions")); %>
<% request.setAttribute("sidemenu_sub_active", new String("Actions")); %>
<% request.setAttribute("agnTitleKey", new String("Actions")); %>
<% request.setAttribute("agnSubtitleKey", new String("Actions")); %>
<% request.setAttribute("agnNavigationKey", new String("ActionsOverview")); %>
<% request.setAttribute("agnHighlightKey", new String("default.Overview")); %>
<c:set var="agnHelpKey" value="actionList" scope="request" />

