<%--
  The contents of this file are subject to the Common Public Attribution
  License Version 1.0 (the "License"); you may not use this file except in
  compliance with the License. You may obtain a copy of the License at
  http://www.openemm.org/cpal1.html. The License is based on the Mozilla
  Public License Version 1.1 but Sections 14 and 15 have been added to cover
  use of software over a computer network and provide for limited attribution
  for the Original Developer. In addition, Exhibit A has been modified to be
  consistent with Exhibit B.
  Software distributed under the License is distributed on an "AS IS" basis,
  WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
  the specific language governing rights and limitations under the License.

  The Original Code is OpenEMM.
  The Original Developer is the Initial Developer.
  The Initial Developer of the Original Code is AGNITAS AG. All portions of
  the code written by AGNITAS AG are Copyright (c) 2009 AGNITAS AG. All Rights
  Reserved.

  Contributor(s): AGNITAS AG.
  --%>
<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.service.NewImportWizardService" %>
<%@ page import="org.agnitas.util.AgnUtils" %>
<%@ page import="org.agnitas.web.NewImportWizardAction" %>
<%@ page import="org.agnitas.web.RecipientAction" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<% pageContext.setAttribute("DOWNLOAD_ACTION", NewImportWizardAction.ACTION_DOWNLOAD_CSV_FILE); %>
<% pageContext.setAttribute("VALID", NewImportWizardService.RECIPIENT_TYPE_VALID); %>
<% pageContext.setAttribute("INVALID", NewImportWizardService.RECIPIENT_TYPE_INVALID); %>
<% pageContext.setAttribute("FIXED", NewImportWizardService.RECIPIENT_TYPE_FIXED_BY_HAND); %>

<% pageContext.setAttribute("sidemenu_active", "Recipients"); %>
<% pageContext.setAttribute("sidemenu_sub_active", "csv_upload"); %>
<% pageContext.setAttribute("agnTitleKey", "UploadSubscribers"); %>
<% pageContext.setAttribute("agnSubtitleKey", "UploadSubscribers"); %>
<% pageContext.setAttribute("agnNavigationKey", "ImportProfileOverview"); %>
<% pageContext.setAttribute("agnHighlightKey", "ImportWizard"); %>

<%@ include file="/tags/taglibs.jsp" %>
<%@include file="/header.jsp" %>

<agn:CheckLogon/>
<%@include file="/messages.jsp" %>

<agn:Permission token="wizard.import"/>

<span class="head3"><bean:message key="csv_completed"/></span>
<br><br>

<controls:panelStart title="import.result.report"/>

<%-- Import result information (erorrs, updated, inserted etc.) --%>

<br>
<c:forEach var="reportEntry" items="${newImportWizardForm.reportEntries}">
    <bean:message key="${reportEntry.key}"/>:&nbsp;${reportEntry.value}<br>
</c:forEach>

<br>

<%-- assigned mailing lists statistics --%>

<c:forEach var="assignedList"
           items="${newImportWizardForm.assignedMailingLists}">
    ${assignedList.shortname} :&nbsp; ${newImportWizardForm.mailinglistAssignStats[assignedList.id]}
    &nbsp; <bean:message key="${newImportWizardForm.mailinglistAddMessage}"/>
    <br>
</c:forEach>

<%-- result files to download --%>
<br>

<c:if test="${newImportWizardForm.validRecipientsFile != null}">
    <bean:message key="import.recipients.valid"/>:&nbsp;
    <html:link
            page="/newimportwizard.do?action=${DOWNLOAD_ACTION}&downloadFileType=${VALID}">
        ${newImportWizardForm.validRecipientsFile.name}
        <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>icon_save.gif"
             border="0" alt="save">
    </html:link>
    <br>
</c:if>

<c:if test="${newImportWizardForm.invalidRecipientsFile != null}">
    <bean:message key="import.recipients.invalid"/>:&nbsp;
    <html:link
            page="/newimportwizard.do?action=${DOWNLOAD_ACTION}&downloadFileType=${INVALID}">
        ${newImportWizardForm.invalidRecipientsFile.name}
        <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>icon_save.gif"
             border="0" alt="save">
    </html:link>
    <br>
</c:if>

<c:if test="${newImportWizardForm.fixedRecipientsFile != null}">
    <bean:message key="import.recipients.fixed"/>:&nbsp;
    <html:link
            page="/newimportwizard.do?action=${DOWNLOAD_ACTION}&downloadFileType=${FIXED}">
        ${newImportWizardForm.fixedRecipientsFile.name}
        <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>icon_save.gif"
             border="0" alt="save">
    </html:link>
    <br>
</c:if>

<controls:panelEnd/>

<br>
<html:link page="<%="/recipient.do?action=" + RecipientAction.ACTION_LIST %>" target="_top">
    <html:img src="button?msg=Finish" border="0"/>
</html:link>

<%@include file="/footer.jsp" %>
