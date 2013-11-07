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

<%@ page import="org.agnitas.service.NewImportWizardService" %>
<%@ page import="org.agnitas.service.impl.CSVColumnState" %>
<%@ page import="org.agnitas.util.AgnUtils" %>
<%@ page import="org.agnitas.util.ImportUtils" %>
<%@ page import="org.agnitas.web.NewImportWizardAction" %>
<%@ page import="org.apache.commons.beanutils.DynaBean" %>
<%@ page import="org.apache.commons.validator.ValidatorResults" %>
<%@ page language="java"
         contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ajax" uri="http://ajaxtags.org/tags/ajax" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net" %>
<%@ include file="/tags/taglibs.jsp" %>

<% pageContext.setAttribute("sidemenu_active", "Recipients"); %>
<% pageContext.setAttribute("sidemenu_sub_active", "csv_upload"); %>
<% pageContext.setAttribute("agnTitleKey", "UploadSubscribers"); %>
<% pageContext.setAttribute("agnSubtitleKey", "UploadSubscribers"); %>
<% pageContext.setAttribute("agnNavigationKey", "ImportProfileOverview"); %>
<% pageContext.setAttribute("agnHighlightKey", "ImportWizard"); %>
<% pageContext.setAttribute("dateColumnType", CSVColumnState.TYPE_DATE); %>

<%@include file="/header.jsp" %>
<script type="text/javascript"
        src='/js/jscalendar/calendar_stripped.js'></script>
<script type="text/javascript"
        src='/js/jscalendar/lang/calendar-<bean:write name="emm.locale" property="language" scope="session"/>.js'></script>
<script type="text/javascript"
        src='/js/jscalendar/calendar-setup_stripped.js'></script>
<script type="text/javascript">
    <!--
    function parametersChanged() {
        document.getElementsByName('newImportWizardForm')[0].numberOfRowsChanged.value = true;
    }
    //-->
</script>
<script src="js/tablecolumnresize.js" type="text/javascript"></script>
<script type="text/javascript">
    var prevX = -1;
    var tableID = 'recipient';
    var columnindex = 0;
    var dragging = false;

    document.onmousemove = drag;
    document.onmouseup = dragstop;
</script>
<agn:CheckLogon/>
<html:form action="/newimportwizard">
    <html:hidden property="numberOfRowsChanged"/>
    <span class="head3"><bean:message key="import.title.error_edit"/></span>
    <br>
    <html:errors/>
    <br>
    <table border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td>
                <table>
                    <tr>
                        <td><bean:message key="Admin.numberofrows"/></td>
                        <td>
                            <html:select property="numberofRows"
                                         onchange="parametersChanged()">
                                <%
                                    String[] sizes = {"20", "50", "100"};
                                    for (int i = 0; i < sizes.length; i++) {
                                %>
                                <html:option value="<%=sizes[i]%>"><%=sizes[i]%>
                                </html:option>
                                <%
                                    }
                                %>

                            </html:select>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td>
                <display:table class="dataTable"
                               pagesize="${newImportWizardForm.numberofRows}"
                               id="recipient"
                               name="recipientList" sort="external"
                               excludedParams="*"
                               requestURI="/newimportwizard.do?action=4&__fromdisplaytag=true"
                               partialList="true"
                               size="${recipientList.fullListSize}">
                    <c:forEach
                            items="${newImportWizardForm.importWizardHelper.columns}"
                            var="item">
                        <c:if test="${item.importedColumn}">
                            <display:column class="name"
                                            headerClass="head_name"
                                            title="${item.colName}"
                                            sortable="true">
                                <c:set scope="page" value="${item.colName}"
                                       var="propertyName"/>
                                <%
                                    if (ImportUtils.checkIsCurrentFieldValid((ValidatorResults) ((DynaBean) recipient).get(NewImportWizardService.VALIDATOR_RESULT_RESERVED), (String) pageContext.getAttribute("propertyName"))) {
                                %>

                                <c:out value="<%= ((DynaBean)recipient).get((String) pageContext.getAttribute("propertyName")) %>"/>

                                <%
                                } else {
                                %>
                                <c:set var="id"
                                       value="${recipient.ERROR_EDIT_RECIPIENT_EDIT_RESERVED.temporaryId}_${item.colName}"/>
                                <table cellpadding="0" cellspacing="0">
                                    <tr>
                                        <td>
                                            <input type="text" id="${id}"
                                                   name="changed_recipient_${recipient.ERROR_EDIT_RECIPIENT_EDIT_RESERVED.temporaryId}/RESERVED/${item.colName}"
                                                   value="<%= ((String)((DynaBean)recipient).get((String) pageContext.getAttribute("propertyName"))) %>"
                                                   style="background: yellow;">
                                        </td>
                                        <c:if test="${item.type == dateColumnType}">
                                            <td>
                                                <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>calendar.gif"
                                                     id="calendar${id}"
                                                     alt="date"
                                                     style="cursor: pointer;"/>
                                                <script type="text/javascript">
                                                    Calendar.setup(
                                                    {
                                                        inputField : "${id}",
                                                        ifFormat : "<bean:write name="newImportWizardForm" property="calendarDateFormat"/>",
                                                        button : "calendar${id}",
                                                        showsTime: true
                                                    });
                                                </script>
                                            </td>
                                        </c:if>
                                    </tr>
                                </table>
                                <%
                                    }
                                %>
                            </display:column>
                        </c:if>
                    </c:forEach>
                </display:table>
            </td>
        </tr>
    </table>
    <br>
    <html:image src="button?msg=Save" border="0" property="edit_page_save" value="save"/>
    <html:link
            page="<%="/newimportwizard.do?action=" + NewImportWizardAction.ACTION_MLISTS %>"><html:img
            src="button?msg=Ignore" border="0"/></html:link>
</html:form>
<%@include file="/footer.jsp" %>