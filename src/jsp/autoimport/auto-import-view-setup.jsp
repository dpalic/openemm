<%@ page language="java" contentType="text/html; charset=utf-8" errorPage="/error.jsp" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="logic" uri="http://struts.apache.org/tags-logic" %>

<agn:CheckLogon/>

<agn:Permission token="recipient.import.auto"/>

<c:set var="sidemenu_active" value="Recipients" scope="request" />
<c:set var="sidemenu_sub_active" value="autoImport.autoImport" scope="request" />
<c:set var="agnTitleKey" value="autoImport.autoImport" scope="request" />
<c:set var="agnNavigationKey" value="AutoImport" scope="request" />
<c:set var="agnHighlightKey" value="autoImport.new" scope="request" />
<c:set var="agnSubtitleKey" value="autoImport.new" scope="request" />

<logic:notEqual name="autoImportForm" property="autoImportId" value="0">
    <c:set var="agnSubtitleKey" value="autoImport.autoImport" scope="request" />
    <c:set var="agnNavigationKey" value="AutoImportView" scope="request" />
    <c:set var="agnHighlightKey" value="autoImport.autoImport" scope="request" />
    <c:set var="agnNavHrefAppend" value="&autoImportId=${autoImportForm.autoImportId}" scope="request" />
</logic:notEqual>

<c:set var="agnHelpKey" value="autoImport" scope="request" />
