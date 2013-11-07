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
<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.cms.web.forms.ContentModuleForm" %>
<%@ page import="org.agnitas.util.AgnUtils" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="ajax" uri="http://ajaxtags.org/tags/ajax" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ include file="/cms/taglibs.jsp" %>

<agn:CheckLogon/>

<agn:Permission token="cms.central_content_management"/>

<% ContentModuleForm aForm = (ContentModuleForm) session.getAttribute("contentModuleForm"); %>

<% pageContext.setAttribute("sidemenu_active", "ContentManagement"); %>
<% pageContext.setAttribute("sidemenu_sub_active", "ContentModules"); %>
<% pageContext.setAttribute("agnTitleKey", "ContentManagement"); %>
<% pageContext.setAttribute("agnSubtitleKey", "ContentModules"); %>
<% pageContext.setAttribute("agnNavigationKey", "ContentModuleOverview"); %>
<% pageContext.setAttribute("agnHighlightKey", "NewContentModule"); %>

<%@include file="/header.jsp" %>
<%@include file="/messages.jsp" %>

<html:form action="/cms_contentmodule" focus="cmtId">
    <html:hidden property="action"/>
    <html:hidden property="contentModuleId" value="0"/>
    <table border="0" cellspacing="0" cellpadding="4">
        <tr>
            <td><bean:message key="SelectCMT" bundle="cmsbundle"/>:</td>
            <td>
                <html:select property="cmtId">
                    <% for(int i = 0; i < aForm.getAllCMT().size(); i++) { %>
                    <html:option value="<%= String.valueOf(aForm.getAllCMT().get(i).getId()) %>">
                        <%= aForm.getAllCMT().get(i).getName() %>
                    </html:option>
                    <% } %>
                </html:select>
            </td>
        </tr>
        <tr>
            <td height="10"></td>
        </tr>
    </table>
    <html:image src="button?msg=Create" border="0" property="create"
                value="create"/>
</html:form>
<%@include file="/footer.jsp" %>

