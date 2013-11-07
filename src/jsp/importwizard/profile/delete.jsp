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
<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.util.AgnUtils, org.agnitas.web.ImportProfileAction, org.agnitas.web.forms.ImportProfileForm" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<% ImportProfileForm aForm = (ImportProfileForm) session.getAttribute("importProfileForm"); %>

<% pageContext.setAttribute("sidemenu_active", "Recipients"); %>
<% pageContext.setAttribute("sidemenu_sub_active", "csv_upload"); %>
<% if (aForm.getFromListPage()) { %>
<% pageContext.setAttribute("agnTitleKey", "ImportProfile"); %>
<% pageContext.setAttribute("agnSubtitleKey", "ImportProfile"); %>
<% pageContext.setAttribute("agnNavigationKey", "ImportProfileOverview"); %>
<% pageContext.setAttribute("agnHighlightKey", "ProfileAdministration"); %>
<% } else { %>
<% pageContext.setAttribute("agnTitleKey", "ImportProfile"); %>
<% pageContext.setAttribute("agnSubtitleKey", "ImportProfile"); %>
<% pageContext.setAttribute("agnNavigationKey", "ImportProfile"); %>
<% pageContext.setAttribute("agnHighlightKey", "ImportProfile"); %>
<%
    pageContext.setAttribute("agnNavHrefAppend", "&profileId=" + aForm.getProfileId()); %>
<% } %>

<% int cancelAction = ImportProfileAction.ACTION_VIEW;
    if (aForm.getFromListPage()) {
        cancelAction = ImportProfileAction.ACTION_LIST;
    }
%>

<%@include file="/header.jsp" %>
<%@include file="/messages.jsp" %>

<span class="head1">${importProfileForm.profile.name}</span><br>
<br/>
<br/>
<span class="head3"><bean:message key="DeleteImportProfileQuestion"/></span><br>

<p>
    <html:form action="/importprofile.do">
        <html:hidden property="profileId"/>
        <html:hidden property="action"/>
        <html:image src="button?msg=Delete" property="kill" value="kill"/>
        <html:link
                page="<%= "/importprofile.do?action=" + cancelAction + "&cmTemplateId=" + aForm.getProfileId() %>"><html:img
                src="button?msg=Cancel" border="0"/></html:link>
    </html:form>
</p>

<%@include file="/footer.jsp" %>
