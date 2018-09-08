<%@ page language="java" contentType="text/html; charset=utf-8"  errorPage="/error.jsp" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>

<agn:Permission token="campaign.show"/> 

<% request.setAttribute("sidemenu_active", new String("Mailings")); %>
<% request.setAttribute("sidemenu_sub_active", new String("Campaigns")); %>

<% request.setAttribute("agnNavigationKey", new String("CampaignsOverview")); %>
<% request.setAttribute("agnHighlightKey", new String("default.Overview")); %>

<% request.setAttribute("agnTitleKey", new String("Campaigns")); %>
<% request.setAttribute("agnSubtitleKey", new String("Campaigns")); %>
<c:set var="agnHelpKey" value="archiveView" scope="request" />
