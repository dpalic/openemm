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
 --%><%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, java.util.*, java.text.*, org.agnitas.web.*, org.agnitas.beans.*" %>
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
<% pageContext.setAttribute("MAILTYPE_TEXT", Recipient.MAILTYPE_TEXT); %>
<% pageContext.setAttribute("MAILTYPE_HTML", Recipient.MAILTYPE_HTML); %>
<% pageContext.setAttribute("MAILTYPE_HTML_OFFLINE", Recipient.MAILTYPE_HTML_OFFLINE); %>

<%@include file="/header.jsp"%>

<html:form action="/importwizard" enctype="multipart/form-data">
    <html:hidden property="action"/>

    <b><font color=#73A2D0><bean:message key="ImportWizStep_4_of_7"/></font></b>
    <br>
	 	 <table border="0" cellspacing="0" cellpadding="0" width="100%">
 	 	<logic:equal value="true" name="importWizardForm" property="genderMissing">
 	 	<tr>
 	 		<td><bean:message key="error.import.no_gender_mapping"/>&nbsp;&nbsp;</td>
 	 		<td><bean:message key="error.import.no_gender_mapping.solution"/></td>
 	 		<html:hidden property="manualAssignedGender"/>
 	 	<tr>
 	 	</logic:equal>
 	 	<logic:equal value="true" name="importWizardForm"  property="mailingTypeMissing">
 	 	<tr>
 	 		<td><bean:message key="recipient.mailingtype"/>&nbsp;&nbsp;</td>
 	 		<td>
  				<html:select property="manualAssignedMailingType" >
   					<html:option value="${MAILTYPE_TEXT}"><bean:message key="recipient.mailingtype.text"/></html:option>
   					<html:option value="${MAILTYPE_HTML}"><bean:message key="recipient.mailingtype.html"/></html:option>
   					<html:option value="${MAILTYPE_HTML_OFFLINE}"><bean:message key="recipient.mailingtype.htmloffline"/></html:option>
   				</html:select>
 	 		</td>
 	 	<tr>
 	 	</logic:equal>
 	 </table>	
 	
 	
 
    <hr>
    <html:image src="button?msg=Back"  border="0" property="verify_back" value="verify_back"/>
    &nbsp;&nbsp;&nbsp;
    <html:image src="button?msg=Proceed" border="0"/>

</html:form>

<%@include file="/footer.jsp"%>
