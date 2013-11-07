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
         import="org.agnitas.cms.web.CMTemplateAction, org.agnitas.cms.web.forms.CMTemplateForm" %>
<%@ page import="org.agnitas.util.AgnUtils" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="ajax" uri="http://ajaxtags.org/tags/ajax" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net" %>

<agn:CheckLogon/>

<agn:Permission token="cms.central_content_management"/>

<% ResourceBundle emmBundle = ResourceBundle.getBundle("messages", (Locale) pageContext.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)); %>
<% ResourceBundle cmsBundle = ResourceBundle.getBundle("cmsmessages", (Locale) pageContext.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)); %>

<% CMTemplateForm aForm = (CMTemplateForm) session.getAttribute("cmTemplateForm"); %>

<% pageContext.setAttribute("sidemenu_active", new String("ContentManagement")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("CMTemplates")); %>
<% pageContext.setAttribute("agnTitleKey", new String("ContentManagement")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("CMTemplates")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("CMTemplatesOverview")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("NewCMtemplate")); %>

<% pageContext.setAttribute("ACTION_LIST", CMTemplateAction.ACTION_LIST); %>
<% pageContext.setAttribute("ACTION_VIEW", CMTemplateAction.ACTION_VIEW); %>
<% pageContext.setAttribute("ACTION_CONFIRM_DELETE", CMTemplateAction.ACTION_CONFIRM_DELETE); %>

<%@include file="/header.jsp" %>
<%@include file="/messages.jsp" %>

<html:form action="/cms_cmtemplate" enctype="multipart/form-data">
    <table border="0" cellspacing="3" cellpadding="0" width="100%">
        <tr>
            <td><bean:message key="ChooseCMTemplate"
                              bundle="cmsbundle"/>:&nbsp;</td>
            <td><html:file property="templateFile"/></td>
        </tr>
        <tr>
            <td><bean:message key="Charset"/>:</td>
            <td><html:select property="charset" size="1">
                <% for (String charsetElement : CMTemplateForm.CHARTERSET_LIST) {%>
                <html:option value="<%=charsetElement%>"><bean:message key="<%=charsetElement%>"/>
                </html:option>
                <%}%>
            </html:select>
            </td>
        </tr>
        <tr>
            <td height="10"></td>
        </tr>
        <tr>
            <td colspan="2"><html:image src="button?msg=Create" property="upload"
                                        value="upload"/></td>
        </tr>
    </table>
</html:form>
<%@include file="/footer.jsp" %>

