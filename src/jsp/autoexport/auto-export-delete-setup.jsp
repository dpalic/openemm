<%@ page language="java" contentType="text/html; charset=utf-8" errorPage="/error.jsp" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>

<agn:Permission token="recipient.export.auto"/>

<c:set var="sidemenu_active" value="Recipients" scope="request" />
<c:set var="sidemenu_sub_active" value="autoExport.autoExport" scope="request" />
<c:set var="agnTitleKey" value="autoExport.autoExport" scope="request" />
<c:set var="agnSubtitleKey" value="autoExport.autoExport" scope="request" />
<c:set var="agnNavigationKey" value="AutoExportView" scope="request" />
<c:set var="agnHighlightKey" value="autoExport.autoExport" scope="request" />
<c:set var="agnNavHrefAppend" value="&autoExportId=${autoExportForm.autoExportId}" scope="request" />
<c:set var="agnHelpKey" value="autoExport" scope="request" />
