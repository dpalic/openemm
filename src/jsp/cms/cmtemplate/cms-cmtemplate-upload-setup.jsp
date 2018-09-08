<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.cms.web.CMTemplateAction"  errorPage="/error.jsp" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>

<agn:Permission token="cms.central_content_management"/>

<% request.setAttribute("sidemenu_active", new String("Mailings")); %>
<% request.setAttribute("sidemenu_sub_active", new String("ContentManagement")); %>
<% request.setAttribute("agnTitleKey", new String("ContentManagement")); %>
<% request.setAttribute("agnSubtitleKey", new String("cms.CMTemplates")); %>
<% request.setAttribute("agnNavigationKey", new String("ContentManagementSub")); %>
<% request.setAttribute("agnHighlightKey", new String("cms.CMTemplates")); %>
<c:set var="agnHelpKey" value="cmTemplateView" scope="request" />

<% request.setAttribute("ACTION_LIST", CMTemplateAction.ACTION_LIST); %>
<% request.setAttribute("ACTION_VIEW", CMTemplateAction.ACTION_VIEW); %>
<% request.setAttribute("ACTION_CONFIRM_DELETE", CMTemplateAction.ACTION_CONFIRM_DELETE); %>