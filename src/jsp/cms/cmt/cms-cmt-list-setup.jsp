<%@ page language="java" contentType="text/html; charset=utf-8"  errorPage="/error.jsp" %>
<%@ page import="org.agnitas.cms.web.ContentModuleAction" %>
<%@ page import="org.agnitas.cms.web.ContentModuleTypeAction" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>

<agn:Permission token="cms.central_content_management"/>

<% request.setAttribute("sidemenu_active", "Mailings"); %>
<% request.setAttribute("sidemenu_sub_active", "ContentManagement"); %>
<% request.setAttribute("agnTitleKey", "ContentManagement"); %>
<% request.setAttribute("agnSubtitleKey", "cms.ContentModuleTypes"); %>
<% request.setAttribute("agnNavigationKey", "ContentManagementSub"); %>
<% request.setAttribute("agnHighlightKey", "cms.ContentModuleTypes"); %>

<% request.setAttribute("ACTION_LIST", ContentModuleTypeAction.ACTION_LIST); %>
<% request.setAttribute("ACTION_VIEW", ContentModuleTypeAction.ACTION_VIEW); %>
<% request.setAttribute("ACTION_NEW_CM", ContentModuleAction.ACTION_VIEW); %>
<% request.setAttribute("ACTION_CONFIRM_DELETE", ContentModuleTypeAction.ACTION_CONFIRM_DELETE); %>
<% request.setAttribute("ACTION_PURE_PREVIEW", ContentModuleTypeAction.ACTION_PURE_PREVIEW); %>
<% request.setAttribute("PREVIEW_WIDTH", ContentModuleTypeAction.LIST_PREVIEW_WIDTH); %>
<% request.setAttribute("PREVIEW_HEIGHT", ContentModuleTypeAction.LIST_PREVIEW_HEIGHT); %>
<c:set var="agnHelpKey" value="cmModuleTypeList" scope="request" />

<script type="text/javascript" src="<%= request.getContextPath() %>/js/lib/cms/toggleElem.js"></script>
