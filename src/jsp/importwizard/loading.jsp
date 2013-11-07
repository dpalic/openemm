<%--
  The contents of this file are subject to the Common Public Attribution
  License Version 1.0 (the "License"); you may not use this file except in
  compliance with the License. You may obtain a copy of the License at
  http://www.openemm.org/cpal1.html. The License is based on the Mozilla
  Public License Version 1.1 but Sections 14 and 15 have been added to cover
  use of software over a computer network and provide for limited attribution
  for the Original Developer. In addition, Exhibit A has been modified to be
  consistent with Exhibit B.
  Software distributed under the License is distributed on an "AS IS" basis,
  WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
  the specific language governing rights and limitations under the License.

  The Original Code is OpenEMM.
  The Original Developer is the Initial Developer.
  The Initial Developer of the Original Code is AGNITAS AG. All portions of
  the code written by AGNITAS AG are Copyright (c) 2009 AGNITAS AG. All Rights
  Reserved.

  Contributor(s): AGNITAS AG.
  --%>
<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.util.AgnUtils, org.agnitas.web.NewImportWizardAction, org.agnitas.web.forms.NewImportWizardForm" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/ajaxanywhere.tld" prefix="aa" %>

<agn:CheckLogon/>
<agn:Permission token="recipient.show"/>

<% pageContext.setAttribute("sidemenu_active", "Recipients"); %>
<% pageContext.setAttribute("sidemenu_sub_active", "csv_upload"); %>
<% pageContext.setAttribute("agnTitleKey", "UploadSubscribers"); %>
<% pageContext.setAttribute("agnSubtitleKey", "UploadSubscribers"); %>
<% pageContext.setAttribute("agnNavigationKey", "ImportProfileOverview"); %>
<% pageContext.setAttribute("agnHighlightKey", "ImportWizard"); %>


<%@include file="/header.jsp" %>
<script>

    function go() {
        document.getElementsByName('newImportWizardForm')[0].submit();
    }

    ajaxAnywhere.getZonesToReload = function () {
        return "loading"
    };

    ajaxAnywhere.onAfterResponseProcessing = function () {
        if (! ${newImportWizardForm.error })
            window.setTimeout("go();", ${newImportWizardForm.refreshMillis});
    }
    ajaxAnywhere.showLoadingMessage = function() {
    };

    ajaxAnywhere.onAfterResponseProcessing();
</script>


<%
    NewImportWizardForm recipient = (NewImportWizardForm) session.getAttribute("newImportWizardForm");
    recipient.setAction(NewImportWizardAction.ACTION_ERROR_EDIT);
%>

<aa:zone name="loading">
    <%@include file="/messages.jsp" %>
	   <html:form action="/newimportwizard">
        <html:hidden property="action"/>
        <html:hidden property="error"/>
        <table border="0" cellspacing="0" cellpadding="0" width="400">
            <tr>
                <td>
                    <b>&nbsp;<b>
                </td>
            </tr>
            <tr>
                <td>
                    <logic:equal value="false" name="newImportWizardForm"
                                 property="error">
                        <img border="0" width="44" height="48"
                             src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>wait.gif"/>
                    </logic:equal>
                    <logic:equal value="true" name="newImportWizardForm"
                                 property="error">
                        <img border="0" width="29" height="30"
                             src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>warning.gif"/>
                    </logic:equal>
                </td>
            </tr>
            <tr>
                <td>
                    <b>&nbsp;<b>
                </td>
            </tr>
            <tr>
                <td>
                    <b>
                        <logic:equal value="false" name="newImportWizardForm"
                                     property="error">
                            <bean:message key="loading"/>
                        </logic:equal>
                        <logic:equal value="true" name="newImportWizardForm"
                                     property="error">
                            <bean:message key="loading.stopped"/>
                        </logic:equal>

                        <b>
                </td>
            </tr>
            <tr>
                <td>
                    <b>&nbsp;<b>
                </td>
            </tr>

        </table>
    </html:form>
</aa:zone>
<%@include file="/footer.jsp" %>
