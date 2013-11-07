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
         import="org.agnitas.beans.ColumnMapping"
         contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.util.AgnUtils" %>
<%@ page import="org.agnitas.web.forms.ImportProfileColumnsForm" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/tags/taglibs.jsp" %>

<agn:CheckLogon/>

<agn:Permission token="mailinglist.show"/>

<%
    ImportProfileColumnsForm aForm = (ImportProfileColumnsForm) session.getAttribute("importProfileColumnsForm");
%>

<% pageContext.setAttribute("sidemenu_active", "Recipients"); %>
<% pageContext.setAttribute("sidemenu_sub_active", "csv_upload"); %>
<% pageContext.setAttribute("agnTitleKey", "ManageColumns"); %>
<% pageContext.setAttribute("agnSubtitleKey", "ManageColumns"); %>
<% pageContext.setAttribute("agnNavigationKey", "ImportProfile"); %>
<% pageContext.setAttribute("agnHighlightKey", "ManageColumns"); %>
<% pageContext.setAttribute("agnNavHrefAppend", "&profileId=" + aForm.getProfileId()); %>
<% pageContext.setAttribute("agnSubtitleValue", aForm.getProfile().getName()); %>
<%@include file="/header.jsp" %>

<script type="text/javascript">
    function columnChanged(selectId, rowIndex) {
        var selectElement = document.getElementById(selectId);
        if (selectElement == null) return;
        var selectedDBColumn = selectElement.value;
        var defaultElement = document.getElementById('default.' + selectedDBColumn);
        if (defaultElement != null) {
            var defaultValue = defaultElement.value;
            var columnDefaultField = document.getElementById('id_default_value_' + rowIndex);
            columnDefaultField.value = defaultValue;
        }
    }
</script>

<%@include file="/messages.jsp" %>

<html:form action="/importprofile_columns" enctype="multipart/form-data">
    <html:hidden property="profileId"/>
    <html:hidden property="action"/>

<c:forEach var="columnsDefaults" items="${importProfileColumnsForm.dbColumnsDefaults}">
        <input type="hidden" id="default.${columnsDefaults.key}" value="${columnsDefaults.value}"/>
</c:forEach>
    
    <input type="hidden" name="save.x" value="0">

    <controls:filePanel currentFileName="${importProfileColumnsForm.currentFileName}"
                        hasFile="${importProfileColumnsForm.hasFile}"
                        uploadButton="true"/>

    <c:if test="${importProfileColumnsForm.mappingNumber > 0}">
        <br>
        <bean:message key="CsvMappingMsg"/>
        <table border="0" cellspacing="3" cellpadding="0">
            <tr>
                <td>
                    <strong><bean:message key="CsvColumn"/></strong>
                </td>
                <td width="10px"/>
                <td>
                    <strong><bean:message key="DbColumn"/></strong>
                </td>
                <td width="10px"/>
                <td>
                    <strong><bean:message key="import.profile.column.mandatory"/></strong>
                </td>
                <td width="10px"/>
                <td>
                    <strong><bean:message key="defaultValue"/></strong>
                </td>
                <td width="10px"/>
                <td>
                    <strong><bean:message key="delete"/></strong>
                </td>
            </tr>
            <tr>
                <td colspan="9">
                    <hr>
                </td>
            </tr>
            <c:set var="column_index" value="0"/>
            <c:forEach var="mapping"
                       items="${importProfileColumnsForm.profile.columnMapping}">
                <tr>
                    <td colspan="9"><html:errors property="mapping_${mapping.fileColumn}"/></td>
                </tr>
                <tr>
                    <td>${mapping.fileColumn}</td>
                    <td/>
                    <td>
                        <select size="1" name="dbColumn_${column_index}" id="id_dbColumn_${column_index}"
                                style="width:150px" onchange="columnChanged('id_dbColumn_${column_index}', '${column_index}');">
                            <option value="<%= ColumnMapping.DO_NOT_IMPORT %>">
                                <bean:message key="NoMapping"/>
                            </option>
                            <c:forEach var="dbColumn"
                                       items="${importProfileColumnsForm.dbColumns}">
                                <c:if test="${dbColumn == mapping.databaseColumn}">
                                    <option value="${dbColumn}"
                                            selected="selected">
                                            ${dbColumn}
                                    </option>
                                </c:if>
                                <c:if test="${dbColumn != mapping.databaseColumn}">
                                    <option value="${dbColumn}">
                                            ${dbColumn}
                                    </option>
                                </c:if>
                            </c:forEach>
                        </select>
                    </td>
                    <td/>
                    <td style="text-align:center">
                        <c:if test="${mapping.mandatory}">
                            <input name="mandatory_${column_index}"
                                   type="checkbox" checked="checked"/>
                        </c:if>
                        <c:if test="${!mapping.mandatory}">
                            <input name="mandatory_${column_index}"
                                   type="checkbox"/>
                        </c:if>
                    </td>
                    <td/>
                    <td>
                        <input type="text" name="default_value_${column_index}" id="id_default_value_${column_index}"
                               value="${mapping.defaultValue}" style="width:150px">
                    </td>
                    <td/>
                    <td>
                        <input type="image"
                               src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>delete.gif"
                               border="0" name="removeMapping_${column_index}"
                               value="${column_index}"/>
                    </td>
                </tr>
                <c:set var="column_index" value="${column_index + 1}"/>
            </c:forEach>
        </table>
    </c:if>

    <br>
    <table cellspacing="3">
        <tr>
            <td colspan="3">
                <hr>
                <html:errors property="newColumn"/>
            </td>
        </tr>
        <tr>
            <td><bean:message key="import.profile.new.column"/>:</td>
            <td><html:text property="newColumn" style="width:150px"/></td>
            <td>
                <html:image src="button?msg=Add" border="0" property="add"
                            value="add"/>
            </td>
        </tr>
    </table>

        <br>
        <html:image src="button?msg=Save" border="0" property="save"
                    value="save"/>

</html:form>

<%@include file="/footer.jsp" %>
