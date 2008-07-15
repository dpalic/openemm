<%--
/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/
 --%><%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, java.util.*" %>
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
