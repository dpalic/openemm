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
 --%><%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<agn:CheckLogon/>

<agn:Permission token="wizard.import"/>

<% pageContext.setAttribute("sidemenu_active", new String("Recipients")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("csv_upload")); %>
<% pageContext.setAttribute("agnTitleKey", new String("UploadSubscribers")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("UploadSubscribers")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("subscriber_import")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("ImportWizard")); %>

<%@include file="/header.jsp"%>

<html:form action="/importwizard" enctype="multipart/form-data">
    <html:hidden property="action"/>
    
    <b><font color=#73A2D0><bean:message key="ImportWizStep_5_of_7"/></font></b>
    <br>
    
    <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td colspan=3><br><span class="head3"><bean:message key="ResultMsg"/>:</span><br><br>
            </td>
        </tr>
        <tr><td colspan="3"><br><br>
            <bean:message key="csv_errors_email"/>: <bean:write name="importWizardForm" property="status.error(email)" scope="session"/><logic:greaterThan name="importWizardForm" scope="session" value="0" property="status.error(email)">&nbsp;<html:link page="<%= new String("/importwizard.do?action=" + ImportWizardAction.ACTION_GET_ERROR_EMAIL + "&downloadName=error_email") %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>icon_save.gif" border="0" alt="<bean:message key="Download"/>"></html:link></logic:greaterThan><br>
            <bean:message key="csv_errors_blacklist"/>: <bean:write name="importWizardForm" property="status.error(blacklist)" scope="session"/><logic:greaterThan name="importWizardForm" scope="session" value="0" property="status.error(blacklist)">&nbsp;<html:link page="<%= new String("/importwizard.do?action=" + ImportWizardAction.ACTION_GET_ERROR_BLACKLIST + "&downloadName=error_blacklist") %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>icon_save.gif" border="0" alt="<bean:message key="Download"/>"></html:link></logic:greaterThan><br>
            <bean:message key="csv_errors_double"/>: <bean:write name="importWizardForm" property="status.error(emailDouble)" scope="session"/><logic:greaterThan name="importWizardForm" scope="session" value="0" property="status.error(emailDouble)">&nbsp;<html:link page="<%= new String("/importwizard.do?action=" + ImportWizardAction.ACTION_GET_ERROR_EMAILDOUBLE + "&downloadName=double_email") %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>icon_save.gif" border="0" alt="<bean:message key="Download"/>"></html:link></logic:greaterThan><br>
            <bean:message key="csv_errors_numeric"/>: <bean:write name="importWizardForm" property="status.error(numeric)" scope="session"/><logic:greaterThan name="importWizardForm" scope="session" value="0" property="status.error(numeric)">&nbsp;<html:link page="<%= new String("/importwizard.do?action=" + ImportWizardAction.ACTION_GET_ERROR_NUMERIC + "&downloadName=error_numeric") %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>icon_save.gif" border="0" alt="<bean:message key="Download"/>"></html:link></logic:greaterThan><br>
            <bean:message key="csv_errors_mailtype"/>: <bean:write name="importWizardForm" property="status.error(mailtype)" scope="session"/><logic:greaterThan name="importWizardForm" scope="session" value="0" property="status.error(mailtype)">&nbsp;<html:link page="<%= new String("/importwizard.do?action=" + ImportWizardAction.ACTION_GET_ERROR_MAILTYPE + "&downloadName=error_mailtype") %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>icon_save.gif" border="0" alt="<bean:message key="Download"/>"></html:link></logic:greaterThan><br>
            <bean:message key="csv_errors_gender"/>: <bean:write name="importWizardForm" property="status.error(gender)" scope="session"/><logic:greaterThan name="importWizardForm" scope="session" value="0" property="status.error(gender)">&nbsp;<html:link page="<%= new String("/importwizard.do?action=" + ImportWizardAction.ACTION_GET_ERROR_GENDER + "&downloadName=error_gender") %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>icon_save.gif" border="0" alt="<bean:message key="Download"/>"></html:link></logic:greaterThan><br>
            <bean:message key="csv_errors_date"/>: <bean:write name="importWizardForm" property="status.error(date)" scope="session"/><logic:greaterThan name="importWizardForm" scope="session" value="0" property="status.error(date)">&nbsp;<html:link page="<%= new String("/importwizard.do?action=" + ImportWizardAction.ACTION_GET_ERROR_DATE + "&downloadName=error_date") %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>icon_save.gif" border="0" alt="<bean:message key="Download"/>"></html:link></logic:greaterThan><br>
            <bean:message key="csv_errors_linestructure"/>: <bean:write name="importWizardForm" property="status.error(structure)" scope="session"/><logic:greaterThan name="importWizardForm" scope="session" value="0" property="status.error(structure)">&nbsp;<html:link page="<%= new String("/importwizard.do?action=" + ImportWizardAction.ACTION_GET_ERROR_STRUCTURE + "&downloadName=error_structure") %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>icon_save.gif" border="0" alt="<bean:message key="Download"/>"></html:link></logic:greaterThan><br><br>
        </td></tr>
        <tr><td colspan="3"><bean:write name="importWizardForm" property="linesOK" scope="session"/>&nbsp;<bean:message key="csv_summary"/><logic:greaterThan name="importWizardForm" scope="session" value="0" property="linesOK">&nbsp;<html:link page="<%= new String("/importwizard.do?action=" + ImportWizardAction.ACTION_GET_DATA_PARSED + "&downloadName=import_ok") %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>icon_save.gif" border="0" alt="<bean:message key="Download"/>"></html:link></logic:greaterThan><br><br></td></tr>

        
        <tr>
            <td colspan="3">
                <hr>
                <html:image src="button?msg=Back"  border="0" property="prescan_back" value="prescan_back"/>
                &nbsp;&nbsp;&nbsp;
                <html:image src="button?msg=Proceed" border="0"/>
            </td>
        </tr>
        
        
        
    </table>                      

</html:form>     
<%@include file="/footer.jsp"%>
