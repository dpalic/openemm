<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.web.BlacklistAction"  errorPage="/error.jsp" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>

<agn:Permission token="recipient.list"/>

<% request.setAttribute("sidemenu_active","Recipients"); %>
<% request.setAttribute("sidemenu_sub_active", "recipient.Blacklist"); %>
<% request.setAttribute("agnTitleKey","recipient.Blacklist"); %>
<% request.setAttribute("agnSubtitleKey","recipient.Blacklist"); %>
<% request.setAttribute("agnNavigationKey","blacklist"); %>
<% request.setAttribute("agnHighlightKey","recipient.Blacklist"); %>
<% request.setAttribute("ACTION_CONFIRM_DELETE", BlacklistAction.ACTION_CONFIRM_DELETE ); %>
<% request.setAttribute("ACTION_SAVE", BlacklistAction.ACTION_SAVE ); %>
<% request.setAttribute("ACTION_LIST", BlacklistAction.ACTION_LIST); %>
<c:set var="agnHelpKey" value="blacklist" scope="request" />
