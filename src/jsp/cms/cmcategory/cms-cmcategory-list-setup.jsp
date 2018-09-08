<%@ page language="java" contentType="text/html; charset=utf-8"  errorPage="/error.jsp" %>
<%@ page import="org.agnitas.cms.web.ContentModuleCategoryAction" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>

<agn:Permission token="cms.central_content_management"/>

<% request.setAttribute("sidemenu_active", "Mailings"); %>
<% request.setAttribute("sidemenu_sub_active", "ContentManagement"); %>
<% request.setAttribute("agnTitleKey", "ContentManagement"); %>
<% request.setAttribute("agnSubtitleKey", "cms.CMCategories"); %>
<% request.setAttribute("agnNavigationKey", "ContentManagementSub"); %>
<% request.setAttribute("agnHighlightKey", "cms.CMCategories"); %>
<% request.setAttribute("ACTION_VIEW", ContentModuleCategoryAction.ACTION_VIEW); %>
<% request.setAttribute("ACTION_CONFIRM_DELETE", ContentModuleCategoryAction.ACTION_CONFIRM_DELETE); %>
<% request.setAttribute("ACTION_LIST", ContentModuleCategoryAction.ACTION_LIST); %>
<c:set var="agnHelpKey" value="cmCategoryList" scope="request" />
