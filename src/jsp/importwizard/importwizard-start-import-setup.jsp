<%@ page language="java" contentType="text/html; charset=utf-8"  errorPage="/error.jsp" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<% request.setAttribute("sidemenu_active", "Recipients"); %>
<% request.setAttribute("sidemenu_sub_active", "import.csv_upload"); %>
<% request.setAttribute("agnTitleKey", "import.UploadSubscribers"); %>
<% request.setAttribute("agnSubtitleKey", "import.UploadSubscribers"); %>
<% request.setAttribute("agnNavigationKey", "ImportProfileOverview"); %>
<% request.setAttribute("agnHighlightKey", "import.Wizard"); %>
<% request.setAttribute("currentFormName","newImportWizardForm");%>
<c:set var="agnHelpKey" value="importStep1" scope="request" />

<agn:CheckLogon/>
