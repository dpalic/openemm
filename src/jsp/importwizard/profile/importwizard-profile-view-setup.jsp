<%@ page language="java" contentType="text/html; charset=utf-8"  errorPage="/error.jsp" %>
<%@ page import="org.agnitas.web.ImportProfileAction" %>
<%@ page import="org.agnitas.web.forms.ImportProfileForm" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>

<agn:Permission token="mailinglist.show"/>

<%
    ImportProfileForm aForm = (ImportProfileForm) session.getAttribute("importProfileForm");
%>

<% request.setAttribute("sidemenu_active", "Recipients"); %>
<% request.setAttribute("sidemenu_sub_active", "import.csv_upload"); %>
<% if (aForm.getProfileId() == 0) { %>
<% request.setAttribute("agnTitleKey", "import.NewImportProfile"); %>
<% request.setAttribute("agnSubtitleKey", "import.NewImportProfile"); %>
<% request.setAttribute("agnNavigationKey", "ImportProfileNew"); %>
<% request.setAttribute("agnHighlightKey", "import.NewImportProfile"); %>
<c:set var="agnHelpKey" value="newImportProfile" scope="request" />
<% } else { %>
<% request.setAttribute("agnTitleKey", "import.ImportProfile"); %>
<% request.setAttribute("agnSubtitleKey", "import.ImportProfile"); %>
<% request.setAttribute("agnNavigationKey", "ImportProfile"); %>
<% request.setAttribute("agnHighlightKey", "import.EditImportProfile"); %>
<% request.setAttribute("agnNavHrefAppend", "&profileId=" + aForm.getProfileId()); %>
<% request.setAttribute("agnSubtitleValue", aForm.getProfile().getName()); %>
<c:set var="agnHelpKey" value="manageProfile" scope="request" />
<% } %>
<% request.setAttribute("ACTION_CONFIRM_DELETE", ImportProfileAction.ACTION_CONFIRM_DELETE); %>