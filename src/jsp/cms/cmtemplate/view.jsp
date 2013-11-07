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
         import="org.agnitas.cms.web.CMTemplateAction, org.agnitas.cms.web.forms.CMTemplateForm, org.agnitas.util.AgnUtils" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<%@ include file="/cms/taglibs.jsp" %>

<agn:Permission token="cms.central_content_management"/>

<% CMTemplateForm aForm = (CMTemplateForm) session.getAttribute("cmTemplateForm"); %>

<c:set var="sidemenu_active" value="ContentManagement" scope="page" />
<c:set var="sidemenu_sub_active" value="CMTemplates" scope="page" />
<c:set var="agnTitleKey" value="ContentManagement" scope="page" />
<c:set var="agnSubtitleKey" value="CMTemplates" scope="page" />
<c:set var="agnSubtitleValue" value="${cmTemplateForm.name}" scope="page" />
<c:set var="agnNavigationKey" value="CMTemplateEdit" scope="page" />
<c:set var="agnHighlightKey" value="CMTemplate" scope="page" />
<c:set var="agnNavHrefAppend" value="&cmTemplateId=${cmTemplateForm.cmTemplateId}" scope="page" />

<c:set var="ACTION_PURE_PREVIEW" value="<%= CMTemplateAction.ACTION_PURE_PREVIEW %>" scope="page" />
<c:set var="ACTION_CONFIRM_DELETE" value="<%= CMTemplateAction.ACTION_CONFIRM_DELETE %>" scope="page" />

<c:set var="PREVIEW_URL" value="/cms_cmtemplate.do?action=${ACTION_PURE_PREVIEW}&cmTemplateId=${cmTemplateForm.cmTemplateId}" />

<%@include file="/header.jsp" %>
<%@include file="/messages.jsp" %>

<html:form action="/cms_cmtemplate" focus="name">
    <html:hidden property="cmTemplateId"/>
    <html:hidden property="action"/>
    <input type="hidden" name="save.x" value="0">
    
    <table border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td><bean:message key="Name"/>:&nbsp;</td>
            <td>
                <html:text property="name" size="52" maxlength="99"/>
            </td>
        </tr>
        <tr>
            <td><bean:message key="Description"/>:&nbsp;</td>
            <td>
                <html:textarea property="description" cols="40" rows="5"/>
            </td>
        </tr>
        <tr>
            <td height="10"></td>
        </tr>

    </table>

    <controls:panelStart title="Preview"/>
    <table border="0" cellspacing="4" cellpadding="0">
        <tr align="left">
            <td>
                <table>
                    <tr>
                        <td><bean:message key="Size"/>:</td>
                        <td>
                            <html:select property="previewSize" size="1">
                            <% for(int i=0; i < aForm.getPreviewValues().length; i++) {%>
                                <html:option value="<%= String.valueOf(aForm.getPreviewValues()[i]) %>">
                                    <%= aForm.getPreviewSizes()[i] %>
                                </html:option>
                            <% } %>
                            </html:select>
                        </td>
                        <td>
                            <html:image src="button?msg=Preview"
                                        border="0"
                                        property="changePreviewSize"
                                        value="true"/>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td>
                <iframe width="${cmTemplateForm.previewWidth}" scrolling="auto"
                        height="${cmTemplateForm.previewHeight}" border="0"
                        src="<html:rewrite page="${PREVIEW_URL}"/>"
                        style="background-color : #FFFFFF;">
                    "Your Browser does not support IFRAMEs, please
                    update!
                </iframe>
            </td>
        </tr>
    </table>
    </td>
    <controls:panelEnd/>

    <p>
        <html:image src="button?msg=Save" border="0" property="save"
                    value="save"/>
        <html:link
                page="/cms_cmtemplate.do?action=${ACTION_CONFIRM_DELETE}&cmTemplateId=${cmTemplateForm.cmTemplateId}&fromListPage=false"><html:img
                src="button?msg=Delete" border="0"/></html:link>
    </p>
</html:form>

<%@include file="/footer.jsp" %>
