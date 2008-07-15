<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, java.util.*" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<agn:CheckLogon/>

<agn:Permission token="wizard.export"/>

<% ExportWizardForm aForm=(ExportWizardForm)session.getAttribute("exportWizardForm"); %>

<html>
    <logic:lessThan name="exportWizardForm" property="dbExportStatus" value="1000" scope="session">
        <meta http-equiv="Page-Exit" content="RevealTrans(Duration=1,Transition=1)">
    </logic:lessThan>
    
    <head>
        <link rel="stylesheet" href="<bean:write name="emm.layout" property="baseUrl" scope="session"/>stylesheet.css">
    </head>
    
    <body <logic:lessThan name="exportWizardForm" property="dbExportStatus" value="1000" scope="session">onLoad="window.setTimeout('window.location.reload()',1500)"</logic:lessThan> STYLE="background-image:none;background-color:transparent">
        <table border="0" cellspacing="0" cellpadding="0" width="300" height="20">
        <tr width="100%">
            <td width="100%">
                <b><bean:message key="export.progress"/>:&nbsp;<bean:write name="exportWizardForm" property="linesOK"/>&nbsp;<bean:message key="Recipients"/></b><br>
                <logic:greaterThan name="exportWizardForm" property="dbExportStatus" value="1000" scope="session">
                    <br>
                    <bean:message key="export.finished"/>:<br><br>
                    <html:link page="<%= new String("/exportwizard.do?action=" + ExportWizardAction.ACTION_DOWNLOAD) %>"><b><%= aForm.getCsvFile().getName() %></b>&nbsp;<img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>icon_save.gif" border="0"></html:link>
                    <br>
                    <br>
                    <hr>
                    <bean:message key="ExportDefSave"/>:&nbsp;&nbsp;<br>
                    <html:link target="_parent" page="<%= new String("/exportwizard.do?action=" + ExportWizardAction.ACTION_SAVE_QUESTION) %>"><html:img src="button?msg=Save" border="0"/></html:link>
    
                </logic:greaterThan>
            </td>
            <td width="100%"></td>

        </tr>
    </body>
</html>
