<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>

<agn:Permission token="wizard.export"/>

<% request.setAttribute("sidemenu_active", new String("Recipients")); %>
<% request.setAttribute("sidemenu_sub_active", new String("export")); %>
<% request.setAttribute("agnTitleKey", new String("export")); %>
<% request.setAttribute("agnSubtitleKey", new String("export")); %>
<% request.setAttribute("agnNavigationKey", new String("subscriber_export")); %>
<% request.setAttribute("agnHighlightKey", new String("export.Wizard")); %>