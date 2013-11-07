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
         import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.web.forms.*, org.agnitas.beans.*" %>
<%@ page import="org.agnitas.cms.web.CMTemplateAction" %>
<%@ page import="org.agnitas.cms.web.forms.CMTemplateForm" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<agn:Permission token="cms.central_content_management"/>

<c:set var="sidemenu_active" value="ContentManagement" scope="page" />
<c:set var="sidemenu_sub_active" value="CMTemplates" scope="page" />
<c:set var="agnTitleKey" value="ContentManagement" scope="page" />
<c:set var="agnSubtitleKey" value="CMTemplates" scope="page" />
<c:set var="agnSubtitleValue" value="${cmTemplateForm.name}" scope="page" />
<c:set var="agnNavigationKey" value="CMTemplateEdit" scope="page" />
<c:set var="agnHighlightKey" value="CMTemplate" scope="page" />

<c:choose>
	<c:when test="${cmTemplateForm.fromListPage}">
		<c:set var="cancelAction" value="<%= CMTemplateAction.ACTION_LIST %>" scope="page" />	
	</c:when>
	<c:otherwise>
		<c:set var="cancelAction" value="<%= CMTemplateAction.ACTION_VIEW %>" scope="page" />	
	</c:otherwise>
</c:choose>

<%@include file="/header.jsp" %>
<%@include file="/messages.jsp" %>

<span class="head1">${cmTemplateForm.name}</span><br/><br/>
<span class="head3"><bean:message key="DeleteCMTemplateQuestion" bundle="cmsbundle"/></span><br>

<p>
    <html:form action="/cms_cmtemplate.do">
        <html:hidden property="cmTemplateId"/>
        <html:hidden property="action"/>
        <html:image src="button?msg=Delete" property="kill" value="kill"/>
        <html:link
                page="/cms_cmtemplate.do?action=${cancelAction}&cmTemplateId=${cmTemplateForm.cmTemplateId}"><html:img
                src="button?msg=Cancel" border="0"/></html:link>
    </html:form>
</p>

<%@include file="/footer.jsp" %>
