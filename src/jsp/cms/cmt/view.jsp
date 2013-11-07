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
 * the code written by AGNITAS AG are Copyright (c) 2009 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/
 --%>
<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.cms.web.ContentModuleTypeAction" %>
<%@ page import="org.agnitas.cms.web.forms.ContentModuleTypeForm" %>
<%@ page import="org.agnitas.util.AgnUtils" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ include file="/cms/taglibs.jsp" %>

<agn:Permission token="cms.central_content_management"/>

<c:set var="sidemenu_active" value="ContentManagement" scope="page" />
<c:set var="sidemenu_sub_active" value="ContentModuleTypes" scope="page" />
<c:set var="agnTitleKey" value="ContentManagement" scope="page" />
<c:set var="agnSubtitleKey" value="ContentModuleTypes" scope="page" />
<c:set var="agnSubtitleValue" value="${contentModuleTypeForm.name}" scope="page" />
<c:choose>
	<c:when test="${contentModuleTypeForm.cmtId > 0}">
    	<c:set var="agnNavigationKey" value="CMTEdit" scope="page" />
	</c:when>
	<c:otherwise>	
    	<c:set var="agnNavigationKey" value="CMTNew" scope="page" />
    </c:otherwise>
</c:choose>
<c:set var="agnHighlightKey" value="ContentModuleType" scope="page" />
<c:set var="agnNavHrefAppend" value="&cmtId=${contentModuleTypeForm.cmtId}" scope="page" />

<c:set var="readOnly" value="${contentModuleTypeForm.readOnly}" scope="page" />

<c:set var="ACTION_COPY" value="<%= ContentModuleTypeAction.ACTION_COPY %>" scope="page" />
<c:set var="ACTION_CONFIRM_DELETE" value="<%= ContentModuleTypeAction.ACTION_CONFIRM_DELETE %>" scope="page" />

<%@include file="/header.jsp" %>
<%@include file="/messages.jsp" %>

<html:form action="/cms_cmt" focus="name">
    <html:hidden property="cmtId"/>
    <html:hidden property="action"/>
    <input type="hidden" name="save.x" value="0">

    <table border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td><bean:message key="Name"/>:&nbsp;</td>
            <td>
                 <html:text property="name" size="52" maxlength="99" readonly="${readOnly}"/>
            </td>
        </tr>
        <tr>
            <td><bean:message key="Description"/>:&nbsp;</td>
            <td>
                <html:textarea property="description" cols="40" rows="5" readonly="${readOnly}"/>
            </td>
        </tr>
        <tr>
            <td height="10"></td>
        </tr>
        <tr>
            <td colspan="2">
                <bean:message key="Content"/>:
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <html:textarea property="content" rows="13" cols="60" readonly="${readOnly}"/>
            </td>                       
        </tr>

    </table>

    <p>
        <logic:equal name="contentModuleTypeForm" property="readOnly" value="false">
            <html:image src="button?msg=Save" border="0" property="save" value="save"/>
        </logic:equal>
        <logic:notEqual name="contentModuleTypeForm" property="cmtId" value="0">
            <html:link
                    page="/cms_cmt.do?action=${ACTION_COPY}&cmtId=${contentModuleTypeForm.cmtId}"><html:img
                    src="button?msg=Copy" border="0"/></html:link>
            <logic:equal name="contentModuleTypeForm" property="readOnly" value="false">
                <html:link
                        page="/cms_cmt.do?action=${ACTION_CONFIRM_DELETE}&cmtId=${contentModuleTypeForm.cmtId}&fromListPage=false"><html:img
                        src="button?msg=Delete" border="0"/></html:link>
            </logic:equal>
        </logic:notEqual>
    </p>
</html:form>

<%@include file="/footer.jsp" %>
