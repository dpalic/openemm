<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ include file="/WEB-INF/taglibs.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

    <input type="hidden" name="save" value="0" id="save">

    <div class="mailing_name_box_container">
        <div class="mailing_name_box_top" style="background:none;"></div>
        <div class="mailing_name_box_content" style="background:none;">
            <div class="mailing_name_box_left_column">
                <label for="mailing_name"><bean:message key="default.Name"/>:</label>
                <html:text styleId="mailing_name" property="name" maxlength="99" size="42"/>
            </div>
            <div class="mailing_name_box_center_column">
                <label for="mailing_name"><bean:message key="default.description"/>:</label>
                <html:textarea styleId="mailing_description" property="description" rows="5" cols="32"/>
            </div>
            <div class="mailing_name_box_right_column"></div>
        </div>
        <div class="mailing_name_box_bottom" style="background:none;"></div>
    </div>

    <div class="target_button_container" style="margin-left:0px;">

        <logic:notEqual name="contentModuleCategoryForm" property="cmcId" value="0">
            <div class="maildetail_button">
                <html:link page="/cms_cmcategory.do?action=${ACTION_CONFIRM_DELETE}&cmcId=${contentModuleCategoryForm.cmcId}&fromListPage=false">
                    <span><bean:message key="button.Delete"/></span>
                </html:link>
            </div>
        </logic:notEqual>

        <input type="hidden" id="save" name="save" value=""/>
        <div class="maildetail_button">
            <a href="#" onclick="document.getElementById('save').value='true'; document.getElementById('action').value='3'; document.contentModuleCategoryForm.submit(); return false;"><span><bean:message key="button.Save"/></span></a>
        </div>

        <div class="maildetail_button"><bean:message key="cms.CMCategory"/>:</div>
    </div>

