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
 --%><%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>

<agn:Permission token="profileField.show"/>

<c:if test="${empty hasErrors}">	
	<c:set var="TMP_FIELDNAME" value="${profileFieldForm.fieldname}" scope="page" />
</c:if>

<c:set var="sidemenu_active" value="Settings" scope="page" />
<c:set var="sidemenu_sub_active" value="Profile_DB" scope="page" />
<c:set var="agnTitleKey" value="Profile_Database" scope="page" />
<c:set var="agnSubtitleKey" value="Profile_Database" scope="page" />
<c:set var="agnNavigationKey" value="profiledb" scope="page" />
<c:choose>
	<c:when test="${not empty TMP_FIELDNAME}">
		<c:set var="agnHighlightKey" value="Profile_DB" scope="page" />
	</c:when>
	<c:otherwise>
		<c:set var="agnHighlightKey" value="NewProfileDB_Field" scope="page" />
	</c:otherwise>
</c:choose>

<c:set var="ACTION_CONFIRM_DELETE" value="<%= ProfileFieldAction.ACTION_CONFIRM_DELETE %>" scope="page" />
<c:set var="ACTION_LIST" value="<%= ProfileFieldAction.ACTION_LIST %>" scope="page" />

<%@include file="/header.jsp"%>
<%@include file="/messages.jsp" %>

<br>
            <table border="0" cellspacing="0" cellpadding="0" width="100%">
               <html:form action="/profiledb">
                 <html:hidden property="companyID"/>
                 <html:hidden property="action"/>
                 <html:hidden property="oldStyle"/>
                 <tr>
                    <td><b><bean:message key="FieldName"/>:&nbsp;</b></td>
                    <td><html:text property="shortname" size="32"/></td>
                 </tr>

                 <tr>
                    <td><b><bean:message key="Description"/>:&nbsp;</b></td>
                    <td><html:textarea property="description" cols="32" rows="5"/></td>
                 </tr>

                 <c:choose>
                 	<c:when test="${not empty TMP_FIELDNAME}">
                     <tr>
                        <td><b><bean:message key="FieldNameDB"/>:</b></td>
                        <td><c:out value="${TMP_FIELDNAME}" /></td>
                     </tr>
                     <html:hidden property="fieldname"/>

                     <tr>
                        <td><b><bean:message key="Type"/>:&nbsp;</b></td>
                        <td><bean:message key="fieldType.${profileFieldForm.fieldType}"/></td>
                     </tr>

                     <c:if test="${profileFieldForm.fieldType == 'VARCHAR'}">
	                     <tr>
    	                    <td><b><bean:message key="Length"/>:&nbsp;</b></td>
        	                <td><c:out value="${profileFieldForm.fieldLength}" /></td>
            	         </tr>
                     </c:if>
                     
                     <tr>
                        <td><b><bean:message key="Default_Value"/>:&nbsp;</b></td>
                        <td><html:text property="fieldDefault" size="32"/></td>
                     </tr>

                     <tr>
                        <td><b><bean:message key="NullAllowed"/>:&nbsp;</b></td>
                        <c:choose>
	                        <c:when test="${profileFieldForm.fieldNull}">
    	                        <td><bean:message key="Yes"/></td>
        					</c:when>
        					<c:otherwise>
	                            <td><bean:message key="No"/></td>
                            </c:otherwise>
						</c:choose>
                     </tr>
					</c:when>
					<c:otherwise>
                     <tr>
                        <td><b><bean:message key="FieldNameDB"/>:&nbsp;</b></td>
                        <td><html:text property="fieldname" size="32"/></td>
                     </tr>

                     <tr>
                        <td><b><bean:message key="Type"/>:&nbsp</b></td>
                        <td>    
                            <html:select property="fieldType" size="1">
                                <html:option value="DOUBLE"><bean:message key="fieldType.DOUBLE"/></html:option>
                                <html:option value="VARCHAR"><bean:message key="fieldType.VARCHAR"/></html:option>
                                <html:option value="DATE"><bean:message key="fieldType.DATE"/></html:option>
                            </html:select>
                        </td>
                     </tr>
                     
                     <tr>
                        <td><b><bean:message key="Length"/>:&nbsp;</b></td>
                        <td><html:text property="fieldLength" size="5"/>&nbsp;<bean:message key="profile.hint" /></td>
                     </tr>

                     <tr>
                        <td><b><bean:message key="Default_Value"/>:&nbsp;</b></td>
                        <td><html:text property="fieldDefault" size="32"/></td>
                     </tr>
					</c:otherwise>
				</c:choose>
                <tr>
                  <td colspan="2">
                    <hr>
                  </td>
                </tr>
                <tr>
                  <td colspan="2">
                    <html:image src="button?msg=Save" border="0" property="save" value="save"/>  
                    <c:if test="${not empty param.fieldname}">
	                    <html:link page="/profiledb.do?action=${ACTION_CONFIRM_DELETE}&fieldname=${TMP_FIELDNAME}"><html:img src="button?msg=Delete" border="0"/></html:link>
                    </c:if>
                    <html:link page="/profiledb.do?action=${ACTION_LIST}"><html:img src="button?msg=Cancel" border="0"/></html:link>
                  </td>
                </tr>
              </html:form>  
            </table>
<%@include file="/footer.jsp"%>