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
<%@ page import="org.agnitas.cms.web.ContentModuleAction" %>
<%@ page import="org.agnitas.util.AgnUtils" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="ajax" uri="http://ajaxtags.org/tags/ajax" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net" %>
<%@ include file="/cms/taglibs.jsp" %>

<agn:CheckLogon/>

<agn:Permission token="cms.central_content_management"/>

<% pageContext.setAttribute("sidemenu_active", "ContentManagement"); %>
<% pageContext.setAttribute("sidemenu_sub_active", "ContentModules"); %>
<% pageContext.setAttribute("agnTitleKey", "ContentManagement"); %>
<% pageContext.setAttribute("agnSubtitleKey", "ContentModules"); %>
<% pageContext.setAttribute("agnNavigationKey", "ContentModuleOverview"); %>
<% pageContext.setAttribute("agnHighlightKey", "Overview"); %>

<% pageContext.setAttribute("ACTION_LIST", ContentModuleAction.ACTION_LIST); %>
<% pageContext.setAttribute("ACTION_VIEW", ContentModuleAction.ACTION_VIEW); %>
<% pageContext.setAttribute("ACTION_CONFIRM_DELETE", ContentModuleAction.ACTION_CONFIRM_DELETE); %>
<% pageContext.setAttribute("ACTION_PURE_PREVIEW", ContentModuleAction.ACTION_PURE_PREVIEW); %>
<% pageContext.setAttribute("PREVIEW_WIDTH", ContentModuleAction.LIST_PREVIEW_WIDTH); %>
<% pageContext.setAttribute("PREVIEW_HEIGHT", ContentModuleAction.LIST_PREVIEW_HEIGHT); %>

<%@include file="/header.jsp" %>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/cms/toggleElem.js"></script>
<%@include file="/messages.jsp" %>

<table border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td>
            <html:form action="/cms_contentmodule">
                <html:hidden property="action"/>
                <controls:rowNumber/>
            </html:form>
        </td>
    </tr>
    <tr>
        <td>
            <ajax:displayTag id="cmTemplateTable" ajaxFlag="displayAjax">
                <display:table class="dataTable" id="contentModule" name="contentModuleList"
                               pagesize="${contentModuleForm.numberofRows}"
                               requestURI="/cms_contentmodule.do?action=${ACTION_LIST}" excludedParams="*" sort="list"
                               defaultsort="1">
                    <display:column headerClass="head_name" class="cm_template_name" titleKey="Name"
                                    property="name" sortable="true" paramId="contentModuleId" paramProperty="id"
                                    url="/cms_contentmodule.do?action=${ACTION_VIEW}"/>
                    <display:column headerClass="head_cm_template_preview" class="cm_template_preview"
                                    titleKey="Preview">
                        <div id="img_preview${contentModule.id}">
                            <table align="center" style=" border: 1px solid #888; background:white;cursor: pointer"
                                   onclick="
                                        var imgPreview = document.getElementById('img_preview${contentModule.id}');
                                        imgPreview.style.display = 'none';
                                        Effect.toggle('frame_preview${contentModule.id}', 'appear');
                                        return false;">
                                <tbody>
                                <tr>
                                    <td align="center"
                                        style="width: <%=ContentModuleAction.PREVIEW_MAX_WIDTH%>px; height: <%=ContentModuleAction.PREVIEW_MAX_HEIGHT%>px;">
                                        <img src="<html:rewrite page="/cms_image?cmId=${contentModule.id}&preview=true"/>"
                                             alt="preview thumbnail"><br>
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                        <div id="frame_preview${contentModule.id}" style="overflow: visible; display: none;text-align:left">
                                    <a onclick="
                                        var imgPreview = document.getElementById('frame_preview${contentModule.id}');
                                        imgPreview.style.display = 'none';
                                        Effect.toggle('img_preview${contentModule.id}', 'appear');
                                        return false;"
                                       href="#"><bean:message key="hidePreview" bundle="cmsbundle"/></a>
                            <iframe width="${PREVIEW_WIDTH}" scrolling="auto" height="${PREVIEW_HEIGHT}" border="0"
                                    src="<html:rewrite page="/cms_contentmodule.do?action=${ACTION_PURE_PREVIEW}&contentModuleId=${contentModule.id}"/>"
                                    style="background-color : #FFFFFF;">
                                Your Browser does not support IFRAMEs, please update!
                            </iframe>
                        </div>

                    </display:column>
                    <display:column class="cm_template_edit">
                        <html:link
                                page="/cms_contentmodule.do?action=${ACTION_CONFIRM_DELETE}&contentModuleId=${contentModule.id}&fromListPage=true"><img
                                src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>delete.gif"
                                alt="<bean:message key="Delete"/>" border="0"></html:link>
                        <html:link
                                page="/cms_contentmodule.do?action=${ACTION_VIEW}&contentModuleId=${contentModule.id}"><img
                                src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>revise.gif"
                                alt="<bean:message key="Edit"/>" border="0"></html:link>
                    </display:column>
                </display:table>
            </ajax:displayTag>
        </td>
    </tr>
</table>
<%@include file="/footer.jsp" %>

