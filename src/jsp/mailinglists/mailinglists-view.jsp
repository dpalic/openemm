<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html:form action="/mailinglist" focus="shortname">
    <html:hidden property="mailinglistID"/>
    <html:hidden property="action"/>
    <html:hidden property="targetID"/>
    <input type="hidden" name="save.x" value="0">

   
    <div class="mailing_name_box_container">
        <div class="mailing_name_box_top"></div>
        <div class="mailing_name_box_content">
            <div class="mailing_name_box_left_column">
                <label for="mailing_name"><bean:message key="default.Name"/>:</label>
                <html:text styleId="mailing_name" property="shortname" maxlength="99" size="42"/>
            </div>
            <div class="mailing_name_box_center_column">
                <label for="mailing_name"><bean:message key="default.description"/>:</label>
                <html:textarea styleId="mailing_description" property="description" rows="5" cols="32"/>
            </div>
            <div class="mailing_name_box_right_column"></div>
        </div>
        <div class="mailing_name_box_bottom"></div>
    </div>
        <div class="mailinglist_button_container">
        <agn:ShowByPermission token="mailinglist.change">
            <input type="hidden" name="save" value="" id="save">
            <div class="maildetail_button">
                <a href="#"
                   onclick="document.getElementById('save').value='save'; document.mailinglistForm.submit();return false;">
                    <span><bean:message key="button.Save"/></span>
                </a>
            </div>
        </agn:ShowByPermission>
        <agn:ShowByPermission token="mailinglist.delete">
            <c:if test="${mailinglistForm.mailinglistID != 0}">
                <input type="hidden" id="delete" name="delete" value=""/>

                <div class="maildetail_button">
                    <a href="#"
                       onclick="document.getElementById('delete').value='true'; document.mailinglistForm.submit(); return false;"><span><bean:message
                            key="button.Delete"/></span></a>
                </div>
            </c:if>
        </agn:ShowByPermission>
        <div class="maildetail_button"><bean:message key="Mailinglist"/>:</div>
    </div>
</html:form>
