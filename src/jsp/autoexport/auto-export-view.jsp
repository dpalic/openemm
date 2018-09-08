<%@ page errorPage="/error.jsp" %>
<%@ page language="java" contentType="text/html; charset=utf-8" buffer="32kb" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html:form action="/autoexport">
    <input type="hidden" name="method" value="save" id="action_method" />

    <div class="grey_box_container">
        <div class="grey_box_top"></div>
        <div class="grey_box_content">
            <div class="grey_box_left_column">
                <label for="mailing_name"><bean:message key="default.Name"/>:</label>
                <html:text styleId="mailing_name" property="autoExport.shortname" maxlength="99" size="42"/>
            </div>
            <div class="grey_box_center_column">
                <label for="mailing_name"><bean:message key="default.description"/>:</label>
                <html:textarea styleId="mailing_description" property="autoExport.description" rows="5" cols="32"/>
            </div>
            <div class="grey_box_right_column"></div>
        </div>
        <div class="grey_box_bottom"></div>
    </div>

    <div class="blue_box_container">
        <div class="blue_box_top"></div>
        <div class="blue_box_content">
            <h2 class="blue_box_header"><bean:message key="autoImport.baseSettings"/></h2>
            <div class="auto_import_row">
                <label class="auto_import_property_label"><bean:message key="export.ExportProfile"/>:</label>
                <html:select property="autoExport.exportProfileId" styleClass="auto_import_property_select">
                    <logic:iterate id="profile" collection="${exportProfiles}">
                        <html:option value="${profile.id}">${profile.shortname}</html:option>
                    </logic:iterate>
                </html:select>
            </div>
            <div class="auto_import_row">
                <label class="auto_import_property_label"><bean:message key="autoImport.fileServer"/>:</label>
                <html:text property="autoExport.fileServer" styleClass="auto_import_property_input"/>
            </div>
            <div class="auto_import_row">
                <label class="auto_import_property_label"><bean:message key="autoImportExport.allowUnknownHostKeys"/>:</label>
                <html:hidden property="__STRUTS_CHECKBOX_autoExport.allowUnknownHostKeys" value="false"/>
                <html:checkbox property="autoExport.allowUnknownHostKeys"/>
            </div>

            <div class="auto_import_row">
                <div><label class="auto_import_property_label"><bean:message key="autoImport.filePath"/>:</label></div>
                <div class="float_left">
                    <div style="margin-bottom: 5px;"><html:text property="autoExport.filePath" styleClass="auto_import_property_input"/></div>
                    <div>
                        <div class="float_right">
                            <c:if test="${autoExportForm.connectionStatusKey != null}">
                                <bean:message key="${autoExportForm.connectionStatusKey}"/>
                            </c:if>
                        </div>
                        <div class="action_button float_left">
                            <a href='<html:rewrite page="/autoexport.do?method=checkConnectionStatus"/>'>
                                <span><bean:message key="autoImportExport.checkConnection"/></span>
                            </a>
                        </div>
                    </div>
                </div>
            </div>

            <div class="auto_import_row">
                <label class="auto_import_property_label"><bean:message key="autoImport.executeOneTime"/>:</label>
                <html:hidden property="__STRUTS_CHECKBOX_autoExport.oneTime" value="false"/>
                <html:checkbox property="autoExport.oneTime"/>
            </div>
            <c:if test="${not isProjectOpenEMM}">
                <div class="auto_import_row">
                    <label class="auto_import_property_label"><bean:message key="autoExport.deactivateByCampaign"/>:</label>
                    <html:hidden property="__STRUTS_CHECKBOX_autoExport.deactivateByCampaign" value="false"/>
                    <html:checkbox property="autoExport.deactivateByCampaign"/>
                </div>
            </c:if>
            <div class="auto_import_row">
                <label class="auto_import_property_label"><bean:message key="autoImport.status"/>:</label>
                <c:if test="${autoExportForm.autoExport.active}">
                    <bean:message key="autoImport.statusActive"/>
                </c:if>
                <c:if test="${not autoExportForm.autoExport.active}">
                    <bean:message key="autoImport.statusNotActive"/>
                </c:if>
            </div>

        </div>
        <div class="blue_box_bottom"></div>
    </div>

    <div class="blue_box_container">
        <div class="blue_box_top"></div>
        <div class="blue_box_content">
            <h2 class="blue_box_header"><bean:message key="autoExport.dayTime"/></h2>
            <c:forEach var="day" begin="1" end="7" step="1" >
                <div class="auto_import_day_time_container">

                    <c:set var="dayIndex" value="${day}" scope="request"/>
                    <%-- if first day of week is monday: adjust days order --%>
                    <c:if test="${firstDayOfWeek == 2 && day < 7}">
                        <c:set var="dayIndex" value="${day + 1}" scope="request"/>
                    </c:if>
                    <c:if test="${firstDayOfWeek == 2 && day == 7}">
                        <c:set var="dayIndex" value="${1}" scope="request"/>
                    </c:if>

                    <html:hidden property="__STRUTS_CHECKBOX_weekDay[${dayIndex}]" value="false"/>
                    <html:checkbox property="weekDay[${dayIndex}]"/>
                    <bean:message key="calendar.dayOfWeek.${dayIndex}"/>
                    <br>
                    <bean:message key="default.Time"/>:
                    <html:select property="weekDayTime[${dayIndex}]">
                        <c:forEach var="hour" begin="0" end="23" step="1" >
                            <html:option value="${hour}">
                                <fmt:formatNumber type="number" minIntegerDigits="2" maxIntegerDigits="2" value="${hour}" />:00
                            </html:option>
                        </c:forEach>
                    </html:select>
                </div>
            </c:forEach>
        </div>
        <div class="blue_box_bottom"></div>
    </div>

    <div class="button_container">
        <jsp:include page="auto-export-view-buttons.jsp"/>
    </div>

</html:form>
