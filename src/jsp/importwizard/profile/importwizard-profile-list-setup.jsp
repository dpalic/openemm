<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.web.ImportProfileAction"  errorPage="/error.jsp" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>

<% request.setAttribute("sidemenu_active", "Recipients"); %>
<% request.setAttribute("sidemenu_sub_active", "import.csv_upload"); %>
<% request.setAttribute("agnTitleKey", "import.ImportProfile"); %>
<% request.setAttribute("agnSubtitleKey", "import.ImportProfile"); %>
<% request.setAttribute("agnNavigationKey", "ImportProfileOverview"); %>
<% request.setAttribute("agnHighlightKey", "import.ProfileAdministration"); %>
<% request.setAttribute("ACTION_VIEW", ImportProfileAction.ACTION_VIEW); %>
<% request.setAttribute("ACTION_CONFIRM_DELETE", ImportProfileAction.ACTION_CONFIRM_DELETE); %>
<c:set var="agnHelpKey" value="manageProfile" scope="request" />