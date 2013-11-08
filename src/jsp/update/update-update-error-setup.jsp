<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>
<% request.setAttribute("sidemenu_active", new String("Administration")); %>
<% request.setAttribute("sidemenu_sub_active", new String("none")); %>
<% request.setAttribute("agnTitleKey", new String("logon.title")); %>
<% request.setAttribute("agnSubtitleKey", new String("Settings")); %>
<% request.setAttribute("agnNavigationKey", new String("update")); %>
<% request.setAttribute("agnHighlightKey", new String("settings.Update")); %>
