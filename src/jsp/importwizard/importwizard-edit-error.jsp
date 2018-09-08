<%@ page import="org.agnitas.service.NewImportWizardService"  errorPage="/error.jsp" %>
<%@ page import="org.agnitas.util.ImportUtils" %>
<%@ page import="org.agnitas.web.NewImportWizardAction" %>
<%@ page import="org.apache.commons.validator.ValidatorResults" %>
<%@ page import="java.util.Map" %>
<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ include file="/WEB-INF/taglibs.jsp" %>

<link rel="stylesheet" type="text/css" media="all" href="js/lib/jscalendar/skins/aqua/theme.css" title="Aqua">
<script src="<%=request.getContextPath()%>/js/lib/tableResultInfoRemover.js" type="text/javascript"></script>

<script type="text/javascript"
        src='js/lib/jscalendar/calendar_stripped.js'></script>

<script type="text/javascript"
        src='js/lib/jscalendar/lang/calendar-<bean:write name="emm.locale" property="language" scope="session"/>.js'></script>

<script type="text/javascript"
        src='js/lib/jscalendar/calendar-setup_stripped.js'></script>


<html:form action="/newimportwizard">
    <html:hidden property="numberOfRowsChanged"/>
    <div class="content_element_container">
        <span class="head3"><bean:message key="import.title.error_edit"/></span>
    </div>
    <html:errors/>

    <div class="list_settings_container">
        <div class="filterbox_form_button">
            <a href="#" onclick="document.newImportWizardForm.numberOfRowsChanged.value = true; document.newImportWizardForm.submit(); return false;">
                <span><bean:message key="button.Show"/></span>
            </a>
        </div>
        <div class="list_settings_mainlabel"><bean:message key="settings.Admin.numberofrows"/>:</div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="20"/><label
                for="list_settings_length_0">20</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="50"/><label
                for="list_settings_length_1">50</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="100"/><label
                for="list_settings_length_2">100</label></div>
    </div>

    <div class="list_table_importwizard_wrapper">

        <display:table class="list_table_importwizard"
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
                    <display:column class="import_errors_head_name"
                                    headerClass="import_eroors_head_name"
                                    title="${item.colName}"
                                    >
                        <div class="cell_importwizard">

                            <c:set scope="page" value="${item.colName}"
                                   var="propertyName"/>
                            <%
                                if (ImportUtils.checkIsCurrentFieldValid((ValidatorResults) ((Map) recipient).get(NewImportWizardService.VALIDATOR_RESULT_RESERVED), (String) pageContext.getAttribute("propertyName"))) {
                            %>

                            <c:out value='<%= ((Map)recipient).get((String) pageContext.getAttribute("propertyName")) %>'/>

                            <%
                            } else {
                            %>
                            <c:set var="id"
                                   value="${recipient.ERROR_EDIT_RECIPIENT_EDIT_RESERVED.temporaryId}_${item.colName}"/>
                                    <input type="text" id="${id}"
                                           name="changed_recipient_${recipient.ERROR_EDIT_RECIPIENT_EDIT_RESERVED.temporaryId}/RESERVED/${item.colName}"
                                           value='<%= ((String)((Map)recipient).get((String) pageContext.getAttribute("propertyName"))) %>'
                                           style="background: yellow; width: 120px">
                                    <c:if test="${item.type == dateColumnType}">
                                            <img src="${emmLayoutBase.imagesURL}/calendar-small.png"
                                                 width="18"
                                                 height="18"
                                                 id="calendar${id}"
                                                 alt="date"
                                                 style="line-height: 22px; cursor: pointer;"/>
                                            <script type="text/javascript">
                                                Calendar.setup(
                                                {
                                                    inputField : "${id}",
                                                    ifFormat : "<bean:write name="newImportWizardForm" property="calendarDateFormat"/>",
                                                    button : "calendar${id}",
                                                    showsTime: true
                                                });
                                            </script>
                                    </c:if>
                            <%
                                }
                            %>
                        </div>
                    </display:column>
                </c:if>
            </c:forEach>
        </display:table>

        <script>
            (function () {
                removeTableResultLists('list_result_container_top', 'pagelinks');

                var tableDOM = document.getElementById("recipient");

                if (parseInt(tableDOM.offsetWidth) < 870) {
                    tableDOM.style.width = "870px";
                    $(document.body).select('input[type="text"]').each(function (e) {
                        e.style.width = "100%";
                    });
                }
                else {
                    $(document.body).select('input[type="text"]').each(function (e) {
                        e.style.width = "120px";
                    });
                }
            })();
        </script>
    </div>

    <br>
    <br>

    <div class="button_container importwizard_button_container">
        <div class="action_button">
            <input type="hidden" id="edit_page_save" name="edit_page_save" value=""/>
            <a href="#"
               onclick="document.getElementById('edit_page_save').value='save'; document.newImportWizardForm.submit(); return false;"><span><bean:message
                    key="button.Save"/></span></a>
        </div>
        <div class="action_button">
            <html:link page='<%="/newimportwizard.do?action=" + NewImportWizardAction.ACTION_MLISTS %>'>
                <span><bean:message key="button.Ignore"/></span>
            </html:link>
        </div>
    </div>
</html:form>
