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
<%@ page import="org.agnitas.cms.utils.TagUtils" %>
<%@ page import="org.agnitas.cms.web.CmsImageTag" %>
<%@ page import="org.agnitas.cms.web.ContentModuleAction" %>
<%@ page import="org.agnitas.cms.web.forms.ContentModuleForm" %>
<%@ page import="org.agnitas.cms.webservices.generated.CmsTag" %>
<%@ page import="org.agnitas.util.AgnUtils" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ include file="/cms/taglibs.jsp" %>

<agn:Permission token="cms.central_content_management"/>

<% ContentModuleForm aForm = (ContentModuleForm) session.getAttribute("contentModuleForm"); %>

<% pageContext.setAttribute("sidemenu_active", "ContentManagement"); %>
<% pageContext.setAttribute("sidemenu_sub_active", "ContentModules"); %>
<% pageContext.setAttribute("agnTitleKey", "ContentManagement"); %>
<% pageContext.setAttribute("agnSubtitleKey", "ContentModules"); %>
<%
    if (aForm.getContentModuleId() > 0) {
        pageContext.setAttribute("agnNavigationKey", "ContentModuleEdit");
    } else {
        pageContext.setAttribute("agnNavigationKey", "ContentModuleNew");
    }%>
<% pageContext.setAttribute("agnHighlightKey", "ContentModule"); %>
<% pageContext.setAttribute("agnNavHrefAppend", "&contentModuleId=" + aForm.getContentModuleId()); %>

<c:set var="agnSubtitleValue" value="${contentModuleForm.name}" scope="page" />

<% String previewUrl = "/cms_contentmodule.do?action=" + ContentModuleAction.ACTION_PURE_PREVIEW +
        "&contentModuleId=" + aForm.getContentModuleId() + "&cmtId=" + aForm.getCmtId() + "&sourceCMId=" + aForm.getSourceCMId(); %>

<%@include file="/header.jsp" %>

<script type="text/javascript" src="<%= request.getContextPath() %>/js/cms/cmPreviewResize.js">
</script>

<%@ include file="/messages.jsp" %>

<html:form action="/cms_contentmodule" focus="name"
           enctype="multipart/form-data">
<html:hidden property="contentModuleId"/>
<html:hidden property="cmtId"/>
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
        <td height="5"></td>
    </tr>
</table>

<table cellspacing="4" width="100%">
    <tr>
        <td><bean:message key="Content"/>:</td>
        <td><bean:message key="Preview"/>:</td>
    </tr>
    <tr>
        <td valign="top">
            <table cellspacing="0" cellpadding="4" width="100%"
                   bgcolor="#ebebeb">
                <% List<CmsTag> tags = aForm.getTags(); %>
                <% for (CmsTag tag : tags) {%>
                <tr>
                    <td bgcolor="#96b9dc" width="100%" colspan="5">
                        <b>
                            <%= tag.getName() %>
                        </b>
                    </td>
                </tr>

                <% if (tag.getType() == TagUtils.TAG_TEXT) {%>
                <tr>
                    <td width="40px"></td>
                    <td width="100%">
                        <textarea
                                name="<%= "cm." + tag.getType() + "." + tag.getName() %>"
                                style="width:350px;"
                                rows="4" cols="75"><%=tag.getValue()%></textarea>
                    </td>
                </tr>

                <% } else if (tag.getType() == TagUtils.TAG_LABEL || tag.getType() == TagUtils.TAG_LINK) {%>

                <tr>
                    <td width="40px"></td>
                    <td width="100%">
                        <% if (tag.getType() == TagUtils.TAG_LINK) { %>
                           <html:errors property="<%="url_link_"+tag.getName()%>"/>
                        <%}%>
                        <input type="text"
                               name="<%= "cm." + tag.getType() + "." + tag.getName() %>"
                               value="<%=tag.getValue() %>"
                               style="width:350px;"/>
                    </td>
                </tr>

                <% } else if (tag.getType() == TagUtils.TAG_IMAGE) {%>
                <% if (aForm.isValidState()) {%>
                <tr>
                    <td colspan="5">
                        <table width="100%">
                            <tr>
                                <td colspan="2">
                                    <iframe src="<%= tag.getValue() %>"
                                            width="150"
                                            height="150">"Your Browser does
                                        not support IFRAMEs, please
                                        update!
                                    </iframe>
                                </td>
                                <td width="100%" valign="top">
                                    <table cellpadding="1" width="100%">
                                        <tr>
                                            <td width="100%" colspan="2">
                                                <input type="radio"
                                                       name="<%= "cm." + tag.getType() + "." + tag.getName() + ".select"%>"
                                                       value="upload"
                                                       checked="1">
                                                Upload image
                                            </td>
                                        </tr>
                                        <tr>
                                            <td>
                                                &nbsp;&nbsp;&nbsp;&nbsp;</td>
                                            <td width="100%">
                                                <input type="file"
                                                       name="<%= "cm." + tag.getType() + "." + tag.getName() + ".file" %>">
                                            </td>
                                        </tr>
                                        <tr>
                                            <td width="100%" colspan="2">
                                                <input type="radio"
                                                       name="<%= "cm." + tag.getType() + "." + tag.getName() + ".select"%>"
                                                       value="external">
                                                Use external image
                                            </td>
                                        </tr>
                                        <tr>
                                            <td>
                                                &nbsp;&nbsp;&nbsp;&nbsp;</td>
                                            <td width="100%">
                                                <input type="text"
                                                       name="<%= "cm." + tag.getType() + "." + tag.getName() + ".url" %>"
                                                       style="width:250px;">
                                            </td>
                                        </tr>
                                    </table>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <% } else {%>
                <% if (tag instanceof CmsImageTag) {
                    final CmsImageTag cmsImageTag = (CmsImageTag) tag;%>
                <tr>
                    <td colspan="5">
                        <table width="100%">
                            <tr>
                                <td colspan="2">
                                    <iframe src="<%= cmsImageTag.getValue() %>"
                                            width="150"
                                            height="150">"Your Browser does
                                        not support IFRAMEs, please
                                        update!
                                    </iframe>
                                </td>
                                <td width="100%" valign="top">
                                    <table cellpadding="1" width="100%">
                                        <tr>
                                            <td width="100%" colspan="2">
                                                <input type="radio"
                                                       name="<%= "cm." + tag.getType() + "." + tag.getName() + ".select"%>"
                                                       value="upload"
                                                        <% if (cmsImageTag.isUpload()){%>
                                                       checked="1"
                                                        <%}%>
                                                        >
                                                Upload image
                                            </td>
                                        </tr>
                                        <tr>
                                            <td>
                                                &nbsp;&nbsp;&nbsp;&nbsp;</td>
                                            <td width="100%">
                                                <input type="file"
                                                       name="<%= "cm." + tag.getType() + "." + tag.getName() + ".file" %>"
                                                       value="<%=cmsImageTag.getNewValue()%>">
                                            </td>
                                        </tr>
                                        <tr>
                                            <td width="100%" colspan="2">
                                                <input type="radio"
                                                       name="<%= "cm." + tag.getType() + "." + tag.getName() + ".select"%>"
                                                       value="external"
                                                        <% if (!cmsImageTag.isUpload()){%>
                                                       checked="1"
                                                        <%}%>
                                                        >
                                                Use external image
                                            </td>
                                        </tr>
                                        <tr>
                                            <td>
                                                &nbsp;&nbsp;&nbsp;&nbsp;</td>
                                            <td width="100%">
                                                <html:errors property="<%="url_img_"+tag.getName()%>"/>
                                                <input type="text"
                                                       name="<%= "cm." + tag.getType() + "." + tag.getName() + ".url" %>"
                                                        <% if (!cmsImageTag.isUpload()){ %>
                                                       value="<%=cmsImageTag.getNewValue()%>"
                                                        <% } %>
                                                       style="width:250px;">
                                            </td>
                                        </tr>
                                    </table>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <% } %>
                <% } %>
                <% } %>
                <% } %>
            </table>
        </td>

        <td valign="top">
            <controls:panelStart title="Preview"/>
            <div style="padding:4px;">
                <iframe width="400" scrolling="auto" height="300" id="cm_preview"
                        src="<html:rewrite page="<%= previewUrl %>"/>"
                        style="background-color : #FFFFFF;">
                    "Your Browser does not support IFRAMEs, please
                    update!
                </iframe>
            </div>
            <controls:panelEnd/>
        </td>
    </tr>
</table>

<p>
    <html:image src="button?msg=Save" border="0" property="save"
                value="save"/>
    <logic:notEqual name="contentModuleForm" property="contentModuleId"
                    value="0">
        <html:link
                page="<%= "/cms_contentmodule.do?action=" + ContentModuleAction.ACTION_COPY + "&contentModuleId=" + aForm.getContentModuleId() %>">
            <html:img src="button?msg=Copy" border="0"/></html:link>

        <html:link
                page="<%= "/cms_contentmodule.do?action=" + ContentModuleAction.ACTION_CONFIRM_DELETE + "&contentModuleId=" + aForm.getContentModuleId() + "&fromListPage=false" %>">
            <html:img src="button?msg=Delete" border="0"/></html:link>
    </logic:notEqual>
</p>

</html:form>

<%@include file="/footer.jsp" %>
