<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html:form action="/importprofile" focus="profile.name">
    <html:hidden property="profileId"/>
    <html:hidden property="action"/>

    <div class="mailing_name_box_container">
        <div class="mailing_name_box_left_column">
            <label for="mailing_name"><bean:message key="default.Name"/>:</label>
            <html:text styleId="mailing_name" property="profile.name" maxlength="99" size="52"/>
        </div>
    </div>

    <br>

    <div class="emailbox_container">
        <div class="emailbox_top"></div>
        <div class="emailbox_content">
            <h2 class="targetgroup_nodes_header"><bean:message key="import.profile.file.settings"/></h2>
            <%@include file="/importwizard/profile/file_settings.jsp" %>
        </div>
        <div class="emailbox_bottom"></div>
    </div>
    <br>

    <div class="emailbox_container">
        <div class="emailbox_top"></div>
        <div class="emailbox_content">
            <h2 class="targetgroup_nodes_header"><bean:message key="import.profile.process.settings"/></h2>
            <%@include file="/importwizard/profile/action_settings.jsp" %>
        </div>
        <div class="emailbox_bottom"></div>
    </div>
    <br>

    <div class="emailbox_container">
        <div class="emailbox_top"></div>
        <div class="emailbox_content">
            <h2 class="targetgroup_nodes_header"><bean:message key="import.profile.gender.settings"/></h2>
            <%@include file="/importwizard/profile/gender_settings.jsp" %>
        </div>
        <div class="emailbox_bottom"></div>
    </div>
    <br>

    <div class="maildetail_button_container">
        <input type="hidden" id="save" name="save" value=""/>

        <div class="maildetail_button">
            <a href="#"
               onclick="document.importProfileForm.save.value='save'; document.importProfileForm.submit();return false;">
                <span><bean:message key="button.Save"/></span>
            </a>
        </div>

        <c:if test="${importProfileForm.profileId != 0}">
            <div class="maildetail_button">
                <html:link
                        page="/importprofile.do?action=${ACTION_CONFIRM_DELETE}&profileId=${importProfileForm.profileId}&fromListPage=false">
                    <span><bean:message key="button.Delete"/></span>
                </html:link>
            </div>
        </c:if>
        <div class="maildetail_button"><bean:message key="import.ImportProfile"/>:</div>
    </div>


</html:form>