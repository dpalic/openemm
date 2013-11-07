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
         import="org.agnitas.web.ImportProfileAction"
         contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.web.forms.ImportProfileForm" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/tags/taglibs.jsp" %>

<controls:panelStart title="import.profile.gender.settings"/>
<table cellpadding="0" cellspacing="2">
    <tr>
        <td><strong><bean:message
                key="import.profile.gender.string"/></strong></td>
        <td width="6px"/>
        <td><strong><bean:message
                key="import.profile.gender.int"/></strong></td>
    </tr>
    <c:forEach var="entry"
               items="${importProfileForm.profile.genderMapping}">
        <tr>
            <td>${entry.key}</td>
            <td width="6px"/>
            <td>
                <select name="gender_${entry.key}" size="1">
                    <c:forEach var="gender"
                               items="${importProfileForm.genderValues}">
                        <c:if test="${entry.value == gender}">
                            <option value="${gender}" selected="selected">
                                    ${gender}
                            </option>
                        </c:if>
                        <c:if test="${entry.value != gender}">
                            <option value="${gender}">
                                    ${gender}
                            </option>
                        </c:if>
                    </c:forEach>
                </select>
            </td>
            <td>
                <input type="image"
                       src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>delete.gif"
                       border="0" name="removeGender_${entry.key}"
                       value="${entry.key}"/>
            </td>
        </tr>
    </c:forEach>
</table>
<br>
<table cellpadding="0">
    <tr>
        <td colspan="3">
            <strong>
                <bean:message key="import.profile.add.gender.mapping"/>
            </strong>
        </td>
    </tr>
    <tr>
        <td colspan="5">
            <html:errors property="newGender"/>
        </td>
    </tr>
    <tr>
        <td>
            <html:text property="addedGender" size="10" maxlength="100"/>
        </td>
        <td width="2px"/>
        <td>
            <html:select property="addedGenderInt" size="1">
                <c:forEach var="gender"
                           items="${importProfileForm.genderValues}">
                    <html:option value="${gender}">
                        ${gender}
                    </html:option>
                </c:forEach>
            </html:select>
        </td>
        <td width="2px"/>
        <td><html:image src="button?msg=Add" border="0" property="addGender"
                        value="add"/></td>
    </tr>
</table>
<controls:panelEnd/>