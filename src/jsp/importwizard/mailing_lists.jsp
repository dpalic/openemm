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
         import="org.agnitas.util.AgnUtils" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<agn:CheckLogon/>

<agn:Permission token="wizard.import"/>

<% pageContext.setAttribute("sidemenu_active", "Recipients"); %>
<% pageContext.setAttribute("sidemenu_sub_active", "csv_upload"); %>
<% pageContext.setAttribute("agnTitleKey", "UploadSubscribers"); %>
<% pageContext.setAttribute("agnSubtitleKey", "UploadSubscribers"); %>
<% pageContext.setAttribute("agnNavigationKey", "ImportProfileOverview"); %>
<% pageContext.setAttribute("agnHighlightKey", "ImportWizard"); %>

<%@include file="/header.jsp" %>

<html:form action="/newimportwizard">
    <html:hidden property="action"/>
    <html:errors/>
    <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td colspan=3>
                <div class="tooltiphelp" id="subscribelists">
                    <br><span class="head3"><bean:message key="SubscribeLists"/>:</span></div>
                <script type="text/javascript">
                    var hb1 = new HelpBalloon({
                        dataURL: 'help_${helplanguage}/importwizard/step_6/SubscribeLists.xml'
                    });
                    $('subscribelists').appendChild(hb1.icon);
                </script>
            </td>
        </tr>
        <tr>
            <td height="12px"/>
        </tr>

        <c:forEach var="mlist" items="${newImportWizardForm.allMailingLists}">
            <tr>
                <td width="20px"><input type="checkbox" name="agn_mlid_${mlist.id}"/></td>
                <td>
                        ${mlist.shortname}
                </td>
            </tr>
        </c:forEach>

        <tr>
            <td colspan="3">
                <hr>
                <html:image src="button?msg=Proceed" border="0"/>
            </td>
        </tr>

    </table>

</html:form>


<%@include file="/footer.jsp" %>
