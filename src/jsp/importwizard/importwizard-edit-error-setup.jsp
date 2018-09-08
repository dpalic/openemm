<%@ page language="java" contentType="text/html; charset=utf-8"  errorPage="/error.jsp" %>
<%@ page import="org.agnitas.service.impl.CSVColumnState" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<% request.setAttribute("sidemenu_active", "Recipients"); %>
<% request.setAttribute("sidemenu_sub_active", "import.csv_upload"); %>
<% request.setAttribute("agnTitleKey", "import.UploadSubscribers"); %>
<% request.setAttribute("agnSubtitleKey", "import.UploadSubscribers"); %>
<% request.setAttribute("agnNavigationKey", "ImportProfileOverview"); %>
<% request.setAttribute("agnHighlightKey", "import.Wizard"); %>
<% request.setAttribute("dateColumnType", CSVColumnState.TYPE_DATE); %>
<c:set var="agnHelpKey" value="importStep3" scope="request" />

<script type="text/javascript">
    <!--
    function parametersChanged() {
        document.getElementsByName('newImportWizardForm')[0].numberOfRowsChanged.value = true;
    }
    //-->
</script>
<agn:CheckLogon/>