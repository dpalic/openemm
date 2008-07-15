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
 --%><%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.ImportWizardForm" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<agn:CheckLogon/>

<agn:Permission token="wizard.import"/>

<% pageContext.setAttribute("sidemenu_active", new String("Recipient")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("csv_upload")); %>
<% pageContext.setAttribute("agnTitleKey", new String("UploadSubscribers")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("UploadSubscribers")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("subscriber_import")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("ImportWizard")); %>

<%@include file="/header.jsp"%>

<b><font color=#73A2D0><bean:message key="ImportWizStep_1_of_7"/></font></b>
<br>
<html:errors/>
<table border="0" cellspacing="0" cellpadding="0" width="100%">
    <html:form action="/importwizard" enctype="multipart/form-data">
        <html:hidden property="action"/>
        <tr>
            <td colspan="2"><b><bean:message key="FileName"/>:</b><br>
                <html:file property="csvFile"/>
            </td>

            <td>&nbsp;&nbsp;</td>
        </tr>

        <tr>
            <td>
                <br><b><bean:message key="Separator"/>:</b><br>
                <html:select property="status.separator" size="1">
                    <html:option value=";"><bean:message key="separator.semicolon"/></html:option>
                    <html:option value=","><bean:message key="separator.comma"/></html:option>
                    <html:option value="|"><bean:message key="separator.pipe"/></html:option>
                    <html:option value="t"><bean:message key="separator.tab"/></html:option>
                </html:select>
            </td>
        </tr>

        <tr>
            <td>
                <br><b><bean:message key="Delimiter"/>:</b><br>
                <html:select property="status.delimiter" size="1">
                    <html:option value=""><bean:message key="delimiter.none"/></html:option>
                    <html:option value="&#34;"><bean:message key="delimiter.doublequote"/></html:option>
                    <html:option value="'"><bean:message key="delimiter.singlequote"/></html:option>
                </html:select>
            </td>
        </tr>
<agn:ShowByPermission token="mailing.show.charsets">
        <tr>
            <td>

                    <br><b><bean:message key="Charset"/>:</b><br>
                    <html:select property="status.charset" size="1">
                        <agn:ShowNavigation navigation="charsets" highlightKey="">
                            <agn:ShowByPermission token="<%= _navigation_token %>">
Token: <%= _navigation_token %><br>
                                <html:option value="<%= _navigation_href %>"><bean:message key="<%= _navigation_navMsg %>"/></html:option>
                            </agn:ShowByPermission>
                        </agn:ShowNavigation>
                    </html:select>

            </td>
        </tr>
</agn:ShowByPermission>
        <tr>
            <td>
                    <br><b><bean:message key="dateFormat"/>:</b><br>
                    <html:select property="dateFormat" size="1">
                         <html:option value="dd.MM.yyyy HH:mm">dd.MM.yyyy HH:mm</html:option>
                         <html:option value="dd.MM.yyyy">dd.MM.yyyy</html:option>
                         <html:option value="yyyyMMdd">yyyyMMdd</html:option>
                         <html:option value="yyyyMMdd HH:mm">yyyyMMdd HH:mm</html:option>
                    </html:select>
            </td>
        </tr>


        <tr><td colspan="3">&nbsp;&nbsp;</td></tr>
        <tr>
            <td><html:image src="button?msg=Proceed" border="0"/>&nbsp;&nbsp;</td>
            <td colspan="2">&nbsp;&nbsp;</td>
        </tr>

    </html:form>

</table>

<%@include file="/footer.jsp"%>
