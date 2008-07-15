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
 --%><%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.ImportWizardForm, org.agnitas.beans.CustomerImportStatus, org.agnitas.beans.Admin" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<agn:CheckLogon/>

<agn:Permission token="wizard.import"/>

<% pageContext.setAttribute("sidemenu_active", new String("Recipients")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("csv_upload")); %>
<% pageContext.setAttribute("agnTitleKey", new String("UploadSubscribers")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("UploadSubscribers")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("subscriber_import")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("ImportWizard")); %>

<%@include file="/header.jsp"%>

<b><font color=#73A2D0><bean:message key="ImportWizStep_2_of_7"/></font></b>
<br>
<table border="0" cellspacing="0" cellpadding="0" width="100%">
    <html:form action="/importwizard">
        <html:hidden property="action"/>


        <tr>
            <td>
                <br><b><bean:message key="Mode"/>:</b><br>
                <html:select property="mode" size="1">
                    <agn:ShowByPermission token="import.mode.add">
                        <html:option value="<%= Integer.toString(ImportWizardForm.MODE_ADD) %>"><bean:message key="import.mode.add"/></html:option>
                    </agn:ShowByPermission>
                    <agn:ShowByPermission token="import.mode.add_update">
                        <html:option value="<%= Integer.toString(ImportWizardForm.MODE_ADD_UPDATE) %>"><bean:message key="import.mode.add_update"/></html:option>
                    </agn:ShowByPermission>
                    <agn:ShowByPermission token="import.mode.only_update">
                        <html:option value="<%= Integer.toString(ImportWizardForm.MODE_ONLY_UPDATE) %>"><bean:message key="import.mode.only_update"/></html:option>
                    </agn:ShowByPermission>
                    <agn:ShowByPermission token="import.mode.unsubscribe">
                        <html:option value="<%= Integer.toString(ImportWizardForm.MODE_UNSUBSCRIBE) %>"><bean:message key="import.mode.unsubscribe"/></html:option>
                    </agn:ShowByPermission>
                    <agn:ShowByPermission token="import.mode.bounce">
                        <html:option value="<%= Integer.toString(ImportWizardForm.MODE_BOUNCE) %>"><bean:message key="import.mode.bounce"/></html:option>
                    </agn:ShowByPermission>
                    <agn:ShowByPermission token="import.mode.blacklist">
                        <html:option value="<%= Integer.toString(ImportWizardForm.MODE_BLACKLIST) %>"><bean:message key="import.mode.blacklist"/></html:option>
                    </agn:ShowByPermission>
                    <agn:ShowByPermission token="import.mode.remove_status">
                        <html:option value="<%= Integer.toString(ImportWizardForm.MODE_REMOVE_STATUS) %>"><bean:message key="import.mode.remove_status"/></html:option>
                    </agn:ShowByPermission>
                </html:select>
            </td>
        </tr>

        <agn:ShowByPermission token="import.mode.null_values">
            <tr>
                <td>
                    <br><b><bean:message key="import.null_value_handling"/>:</b><br>
                    <html:select property="status.ignoreNull" size="1">
                        <html:option value="0"><bean:message key="import.dont_ignore_null_values"/></html:option>
                        <html:option value="1"><bean:message key="import.ignore_null_values"/></html:option>                        
                    </html:select>
                </td>
            </tr>
        </agn:ShowByPermission>

        <tr>
            <td>
                <br><b><bean:message key="import.keycolumn"/>:</b><br>
                <html:select property="status.keycolumn" size="1">
                    <agn:ShowColumnInfo id="agnTbl" table="<%= AgnUtils.getCompanyID(request) %>">
                        <html:option value="<%= (String) pageContext.getAttribute("_agnTbl_shortname") %>"></html:option>
                    </agn:ShowColumnInfo>
                </html:select>
            </td>
        </tr>

        <agn:ShowByPermission token="ext_adr_check">
            <tr>
                <td>
                    <br><b><bean:message key="import.extended_check"/>:</b><br>
                    <html:select property="extendedEmailCheck" size="1">
                        <html:option value="true"><bean:message key="Yes"/></html:option>
                        <html:option value="false"><bean:message key="No"/></html:option>
                    </html:select>
                </td>
            </tr>
        </agn:ShowByPermission>

        <agn:ShowByPermission token="import.mode.doublechecking">
            <tr>
                <td>
                    <br><b><bean:message key="import.doublechecking"/>:</b><br>
                    <html:select property="status.doubleCheck" size="1">
                        <html:option value="<%= Integer.toString(CustomerImportStatus.DOUBLECHECK_FULL) %>"><bean:message key="import.doublechecking.full"/></html:option>
                        <html:option value="<%= Integer.toString(CustomerImportStatus.DOUBLECHECK_CSV) %>"><bean:message key="import.doublechecking.csv"/></html:option>                        
                        <html:option value="<%= Integer.toString(CustomerImportStatus.DOUBLECHECK_NONE) %>"><bean:message key="import.doublechecking.none"/></html:option>                        
                    </html:select>
                </td>
            </tr>
        </agn:ShowByPermission>




        <tr><td colspan="3"><hr></td></tr>
        <tr><td colspan="3">&nbsp;&nbsp;</td></tr>
        <tr>
            <td colspan="3">
            <html:image src="button?msg=Back"  border="0" property="mode_back" value="mode_back"/>
            &nbsp;&nbsp;&nbsp;
            <html:image src="button?msg=Proceed" border="0"/>&nbsp;&nbsp;</td>
        </tr>
    </html:form>

</table>
              
            

<%@include file="/footer.jsp"%>
<% out.flush(); %>
