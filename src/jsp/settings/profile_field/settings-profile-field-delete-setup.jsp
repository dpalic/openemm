<%@ page language="java" contentType="text/html; charset=utf-8"  errorPage="/error.jsp" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>

<agn:Permission token="profileField.show"/>

<% request.setAttribute("sidemenu_active", new String("Administration")); %>
<% request.setAttribute("sidemenu_sub_active", new String("settings.Profile_DB")); %>
<% request.setAttribute("agnTitleKey", new String("Profile_Database")); %>
<% request.setAttribute("agnSubtitleKey", new String("Profile_Database")); %>
<% request.setAttribute("agnNavigationKey", new String("profiledb")); %>
<% request.setAttribute("agnHighlightKey", new String("default.Overview")); %>
<c:set var="agnHelpKey" value="newProfileField" scope="request" />
