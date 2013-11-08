<%@ page language="java" contentType="text/html; charset=utf-8" errorPage="error.jsp" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>

<% request.setAttribute("sidemenu_active", new String("ContentManagement")); %>
<% request.setAttribute("sidemenu_sub_active", new String("none")); %>
<% request.setAttribute("agnTitleKey", new String("logon.title")); %>
<% request.setAttribute("agnSubtitleKey", new String("ContentManagement")); %>
<% request.setAttribute("agnNavigationKey", new String("none")); %>
<% request.setAttribute("agnHighlightKey", new String("none")); %>