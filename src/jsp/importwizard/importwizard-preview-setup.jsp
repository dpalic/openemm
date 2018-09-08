<%@ page import="org.agnitas.web.forms.NewImportWizardForm"  errorPage="/error.jsp" %>
<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<% request.setAttribute("sidemenu_active", "Recipients"); %>
<% request.setAttribute("sidemenu_sub_active", "import.csv_upload"); %>
<% request.setAttribute("agnTitleKey", "import.UploadSubscribers"); %>
<% request.setAttribute("agnSubtitleKey", "import.UploadSubscribers"); %>
<% request.setAttribute("agnNavigationKey", "ImportProfileOverview"); %>
<% request.setAttribute("agnHighlightKey", "import.Wizard"); %>
<c:set var="agnHelpKey" value="importStep2" scope="request" />
<%
    NewImportWizardForm newImportWizardForm = (NewImportWizardForm) session.getAttribute("newImportWizardForm");
    request.setAttribute("size", newImportWizardForm.getAllMailingLists().size());
%>
<agn:CheckLogon/>