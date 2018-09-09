<%@ page language="java" contentType="text/html; charset=utf-8" errorPage="/error.jsp" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>

<agn:Permission token="recipient.import.auto"/>

<c:set var="sidemenu_active" value="Recipients" scope="request" />
<c:set var="sidemenu_sub_active" value="autoImport.autoImport" scope="request" />
<c:set var="agnTitleKey" value="autoImport.autoImport" scope="request" />
<c:set var="agnSubtitleKey" value="autoImport.autoImport" scope="request" />
<c:set var="agnNavigationKey" value="AutoImport" scope="request" />
<c:set var="agnHighlightKey" value="default.Overview" scope="request" />
<c:set var="agnHelpKey" value="autoImport" scope="request" />
