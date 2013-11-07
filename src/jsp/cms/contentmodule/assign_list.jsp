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
<%@ page import="org.agnitas.cms.web.forms.ContentModuleForm" %>
<%@ page import="org.agnitas.util.AgnUtils" %>
<%@ page import="org.agnitas.web.MailingBaseAction" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib prefix="ajax" uri="http://ajaxtags.org/tags/ajax" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net" %>
<%@ include file="/cms/taglibs.jsp" %>

<agn:CheckLogon/>

<agn:Permission token="cms.central_content_management"/>

<% ContentModuleForm aForm = (ContentModuleForm) session.getAttribute("contentModuleForm"); %>

<c:set var="sidemenu_active" value="ContentManagement" scope="page" />
<c:set var="sidemenu_sub_active" value="ContentModules" scope="page" />
<c:set var="agnTitleKey" value="ContentManagement" scope="page" />
<c:set var="agnSubtitleKey" value="ContentModules" scope="page" />
<c:set var="agnSubtitleValue" value="${contentModuleForm.name}" scope="page" />
<c:set var="agnNavigationKey" value="ContentModuleEdit" scope="page" />
<c:set var="agnHighlightKey" value="AssignToMailing" scope="page" />
<c:set var="agnNavHrefAppend" value="&contentModuleId=${contentModuleForm.contentModuleId}" scope="page" />

<c:set var="ACTION_ASSIGN_LIST" value="<%= ContentModuleAction.ACTION_ASSIGN_LIST %>" scope="page" />
<c:set var="ACTION_VIEW_MAILING" value="<%= MailingBaseAction.ACTION_VIEW %>" scope="page" />


<%@include file="/header.jsp" %>
<%@include file="/messages.jsp" %>

    <html:form action="/cms_contentmodule">
    <html:hidden property="action"/>
      <table border="0" cellspacing="0" cellpadding="0">
        <tr>
        	<td>
                <controls:rowNumber/>
        	</td>
        </tr>
        <tr><td height="10"></td></tr>
        <tr><td>
        <tr><td>
        <ajax:displayTag id="cmMailBindTable" ajaxFlag="displayAjax">
         	<display:table class="dataTable" id="mailing" name="mailingsList" pagesize="${contentModuleForm.numberofRows}"  requestURI="/cms_contentmodule.do?action=${ACTION_ASSIGN_LIST}" excludedParams="*" sort="external">
                <display:column headerClass="cm_template_assignment" class="cm_template_mailing_id" titleKey="MailingId" url="/mailingbase.do?action=${ACTION_VIEW_MAILING}" property="mailingid" paramId="mailingID" paramProperty="mailingid" />
         		<display:column headerClass="head_mailing" class="mailing" titleKey="Mailing" sortable="true" url="/mailingbase.do?action=${ACTION_VIEW_MAILING}" property="shortname" paramId="mailingID" paramProperty="mailingid" />
                <display:column headerClass="cm_template_assignment" class="cm_template_assigned" titleKey="Assigned">
                    <input type="checkbox" name="assign_mailing_${mailing.mailingid}" value="mailing_${mailing.mailingid}"
                        <logic:equal name="mailing" property="assigned" value="true">
                            checked="1"
                        </logic:equal>
                        <logic:equal name="mailing" property="hasClassicTemplate" value="true">
                            disabled="true"
                        </logic:equal>
                    />
                </display:column>
         	</display:table>
        </ajax:displayTag>
        </td></tr>
          <tr><td height="10"></td></tr>
        <tr><td>
        <html:image src="button?msg=Save" border="0" property="assign" value="assign"/>
        </td></tr>
      </table>
    </html:form>
<%@include file="/footer.jsp" %>

