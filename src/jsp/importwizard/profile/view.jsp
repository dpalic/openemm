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
<%@ page language="java"
         contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/tags/taglibs.jsp" %>

<agn:CheckLogon/>

<agn:Permission token="mailinglist.show"/>

<%
    ImportProfileForm aForm = (ImportProfileForm) session.getAttribute("importProfileForm");
%>

<% pageContext.setAttribute("sidemenu_active", "Recipients"); %>
<% pageContext.setAttribute("sidemenu_sub_active", "csv_upload"); %>
<% if (aForm.getProfileId() == 0) { %>
<% pageContext.setAttribute("agnTitleKey", "NewImportProfile"); %>
<% pageContext.setAttribute("agnSubtitleKey", "NewImportProfile"); %>
<% pageContext.setAttribute("agnNavigationKey", "ImportProfileNew"); %>
<% pageContext.setAttribute("agnHighlightKey", "NewImportProfile"); %>
<% } else { %>
<% pageContext.setAttribute("agnTitleKey", "ImportProfile"); %>
<% pageContext.setAttribute("agnSubtitleKey", "ImportProfile"); %>
<% pageContext.setAttribute("agnNavigationKey", "ImportProfile"); %>
<% pageContext.setAttribute("agnHighlightKey", "ImportProfile"); %>
<% pageContext.setAttribute("agnNavHrefAppend", "&profileId=" + aForm.getProfileId()); %>
<% pageContext.setAttribute("agnSubtitleValue", aForm.getProfile().getName()); %>
<% } %>
<% pageContext.setAttribute("ACTION_CONFIRM_DELETE", ImportProfileAction.ACTION_CONFIRM_DELETE); %>

<%@include file="/header.jsp" %>
<%@include file="/messages.jsp" %>

<html:form action="/importprofile" focus="profile.name">
    <html:hidden property="profileId"/>
    <html:hidden property="action"/>

    <table border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td>
                <bean:message key="Name"/>:&nbsp;
            </td>
            <td>
                <html:text property="profile.name" size="52" maxlength="99"/>
            </td>
        </tr>
    </table>

    <br>
    <%@include file="/importwizard/profile/file_settings.jsp" %>
    <br>
    <%@include file="/importwizard/profile/action_settings.jsp" %>
    <br>
    <%@include file="/importwizard/profile/gender_settings.jsp" %>
    <br>

    <html:image src="button?msg=Save" border="0" property="save" value="save"/>
    <c:if test="${importProfileForm.profileId != 0}">
        <html:link
                page="/importprofile.do?action=${ACTION_CONFIRM_DELETE}&profileId=${importProfileForm.profileId}&fromListPage=false">
            <html:img src="button?msg=Delete" border="0"/></html:link>
    </c:if>

</html:form>

<%@include file="/footer.jsp" %>
