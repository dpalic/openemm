<%@ page language="java" contentType="text/html; charset=utf-8"  errorPage="/error.jsp" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>
<agn:Permission token="settings.show"/>


<%request.setAttribute("sidemenu_active", new String("Recipients"));
request.setAttribute("sidemenu_sub_active", new String("settings.FormsOfAddress"));
request.setAttribute("agnNavigationKey", new String("Salutations"));
request.setAttribute("agnHighlightKey", new String("default.Overview"));
request.setAttribute("agnSubtitleKey", new String("settings.FormsOfAddress"));
request.setAttribute("agnTitleKey", new String("settings.FormsOfAddress"));
%>
<c:set var="agnHelpKey" value="salutationForms" scope="request" />
