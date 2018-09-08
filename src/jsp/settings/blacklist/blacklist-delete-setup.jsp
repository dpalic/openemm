<%@ page language="java" contentType="text/html; charset=utf-8"  errorPage="/error.jsp" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>

<agn:Permission token="recipient.list"/>

<% request.setAttribute("sidemenu_active", new String("Recipients")); %>
<% request.setAttribute("sidemenu_sub_active", new String("recipient.Blacklist")); %>
<% request.setAttribute("agnTitleKey", new String("recipient.Blacklist")); %>
<% request.setAttribute("agnSubtitleKey", new String("recipient.Blacklist")); %>
<% request.setAttribute("agnNavigationKey", new String("blacklist")); %>
<% request.setAttribute("agnHighlightKey", new String("recipient.Blacklist")); %>
<c:set var="agnHelpKey" value="blacklist" scope="request" />
