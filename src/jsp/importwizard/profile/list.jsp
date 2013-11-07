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
         import="org.agnitas.util.AgnUtils, org.agnitas.web.ImportProfileAction" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://ajaxtags.org/tags/ajax" prefix="ajax" %>

<agn:CheckLogon/>

<% pageContext.setAttribute("sidemenu_active", "Recipients"); %>
<% pageContext.setAttribute("sidemenu_sub_active", "csv_upload"); %>
<% pageContext.setAttribute("agnTitleKey", "ImportProfile"); %>
<% pageContext.setAttribute("agnSubtitleKey", "ImportProfile"); %>
<% pageContext.setAttribute("agnNavigationKey", "ImportProfileOverview"); %>
<% pageContext.setAttribute("agnHighlightKey", "ProfileAdministration"); %>

<% pageContext.setAttribute("ACTION_VIEW", ImportProfileAction.ACTION_VIEW); %>
<% pageContext.setAttribute("ACTION_CONFIRM_DELETE", ImportProfileAction.ACTION_CONFIRM_DELETE); %>

<%@include file="/header.jsp" %>
<%@include file="/messages.jsp" %>

<html:form action="/importprofile">

    <table border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td>
                <table>
                    <tr>
                        <td><bean:message key="Admin.numberofrows"/></td>
                        <td>
                            <html:select property="numberofRows">
                                <% String[] sizes = {"20", "50", "100"};
                                    for (int i = 0; i < sizes.length; i++) { %>
                                <html:option
                                        value="<%= sizes[i] %>"><%= sizes[i] %>
                                </html:option>
                                <% } %>
                            </html:select>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <html:image src="button?msg=Show" border="0"/>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td>
                <ajax:displayTag id="importProfileTable" ajaxFlag="displayAjax">
                    <display:table class="dataTable" id="profile"
                                   name="profileList"
                                   pagesize="${importProfileForm.numberofRows}"
                                   requestURI="/importprofile.do?action=${ACTION_LIST}"
                                   excludedParams="*">
                        <display:column headerClass="head_name" class="name"
                                        titleKey="ImportProfile" property="name"
                                        sortable="true" paramId="profileId"
                                        paramProperty="id"
                                        url="/importprofile.do?action=${ACTION_VIEW}"/>
                        <display:column headerClass="head_description"
                                        class="description"
                                        titleKey="import.profile.default">
                            <html:radio name="importProfileForm"
                                        property="defaultProfileId"
                                        value="${profile.id}"/>
                        </display:column>
                        <display:column class="edit">
                            <html:link
                                    page="/importprofile.do?action=${ACTION_CONFIRM_DELETE}&profileId=${profile.id}&fromListPage=true"><img
                                    src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>delete.gif"
                                    alt="<bean:message key="Delete"/>"
                                    border="0"></html:link>
                            <html:link
                                    page="/importprofile.do?action=${ACTION_VIEW}&profileId=${profile.id}"><img
                                    src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>bearbeiten.gif"
                                    alt="<bean:message key="Edit"/>" border="0"></html:link>
                        </display:column>
                    </display:table>
                </ajax:displayTag>
            </td>
        </tr>
    </table>
    <br>
    <html:image src="button?msg=Save" border="0" property="setDefault"
                value="setDefault"/>

</html:form>

<%@include file="/footer.jsp" %>

