<%@ page language="java" contentType="text/html; charset=utf-8"  errorPage="/error.jsp" %>
<%@ page import="org.agnitas.cms.web.ContentModuleAction" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>

<agn:Permission token="cms.central_content_management"/>

<% request.setAttribute("sidemenu_active", "Mailings"); %>
<% request.setAttribute("sidemenu_sub_active", "ContentManagement"); %>
<% request.setAttribute("agnTitleKey", "ContentManagement"); %>
<% request.setAttribute("agnSubtitleKey", "cms.ContentModules"); %>
<% request.setAttribute("agnNavigationKey", "ContentManagementSub"); %>
<% request.setAttribute("agnHighlightKey", "cms.ContentModules"); %>
<c:set var="agnHelpKey" value="cmContentModuleList" scope="request" />
<%--
<% request.setAttribute("sidemenu_active", "ContentManagement"); %>
<% request.setAttribute("sidemenu_sub_active", "cms.ContentModuleTypes"); %>
<% request.setAttribute("agnTitleKey", "ContentManagement"); %>
<% request.setAttribute("agnSubtitleKey", "cms.ContentModuleTypes"); %>
<% request.setAttribute("agnNavigationKey", "CMTOverview"); %>
<% request.setAttribute("agnHighlightKey", "default.Overview"); %>--%>

<% request.setAttribute("ACTION_LIST", ContentModuleAction.ACTION_LIST); %>
<% request.setAttribute("ACTION_VIEW", ContentModuleAction.ACTION_VIEW); %>
<% request.setAttribute("ACTION_CONFIRM_DELETE", ContentModuleAction.ACTION_CONFIRM_DELETE); %>
<% request.setAttribute("ACTION_PURE_PREVIEW", ContentModuleAction.ACTION_PURE_PREVIEW); %>
<% request.setAttribute("PREVIEW_WIDTH", ContentModuleAction.LIST_PREVIEW_WIDTH); %>
<% request.setAttribute("PREVIEW_HEIGHT", ContentModuleAction.LIST_PREVIEW_HEIGHT); %>

<script type="text/javascript" src="<%= request.getContextPath() %>/js/lib/cms/toggleElem.js"></script>

