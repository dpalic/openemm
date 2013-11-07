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
<%@ page import="org.agnitas.util.AgnUtils" %>
<%@ page language="java"
         contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/tags/taglibs.jsp" %>

<% pageContext.setAttribute("sidemenu_active", "Recipients"); %>
<% pageContext.setAttribute("sidemenu_sub_active", "csv_upload"); %>
<% pageContext.setAttribute("agnTitleKey", "UploadSubscribers"); %>
<% pageContext.setAttribute("agnSubtitleKey", "UploadSubscribers"); %>
<% pageContext.setAttribute("agnNavigationKey", "ImportProfileOverview"); %>
<% pageContext.setAttribute("agnHighlightKey", "ImportWizard"); %>

<%@include file="/header.jsp" %>

<agn:CheckLogon/>
<html:errors property="default"/>
<span class="head3"><bean:message key="import.title.preview"/>:</span>
<br>
<br>
<html:form action="/newimportwizard" enctype="multipart/form-data">
    <table border="0" cellspacing="0" cellpadding="2" width="100%">
        <logic:iterate id="row" name="newImportWizardForm"
                       property="previewParsedContent" scope="session"
                       indexId="counter">
            <tr>
                <logic:iterate id="element" name="row" scope="page">
                    <c:if test="${counter==0}">
                        <td align="center"><b><bean:write name="element"/></b>
                        </td>
                    </c:if>
                    <c:if test="${counter!=0}">
                        <td>
                            <input name="dummy" type="text" size="13"
                                   value="${element}" readonly>
                        </td>
                    </c:if>
                </logic:iterate>
            </tr>
        </logic:iterate>

    </table>
    <hr>
    <html:image src="button?msg=Back" border="0" property="preview_back"
                value="back"/>
    &nbsp;&nbsp;&nbsp;
    <html:image src="button?msg=Proceed" border="0" property="preview_proceed"
                value="proceed"/>
</html:form>
<%@include file="/footer.jsp" %>
