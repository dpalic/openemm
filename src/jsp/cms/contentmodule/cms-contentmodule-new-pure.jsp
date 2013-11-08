<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.cms.web.forms.ContentModuleForm" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<div class="mailing_name_box_container">
    <div class="mailing_name_box_top" style="background:none;"></div>
    <div class="mailing_name_box_content" style="background:none;">
        <logic:equal name="contentModuleForm" property="moduleTypeNumber" value="0">
            <bean:message key="cms.NoModuleTypeExists"/>
        </logic:equal>
        <logic:notEqual name="contentModuleForm" property="moduleTypeNumber" value="0">
            <bean:message key="SelectCMT" bundle="cmsbundle"/>:
            <html:select property="cmtId">
                <logic:iterate collection="${contentModuleForm.allCMT}" id="cmt">
                    <html:option value="${cmt.id}">
                        ${cmt.name}
                    </html:option>
                </logic:iterate>
            </html:select>
        </logic:notEqual>
        <div class="mailing_name_box_center_column"></div>
        <div class="mailing_name_box_right_column"></div>
    </div>
    <div class="mailing_name_box_bottom" style="background:none;"></div>
</div>

<div class="target_button_container" style="margin-left:0px;">
    <logic:notEqual name="contentModuleForm" property="moduleTypeNumber" value="0">
        <div class="maildetail_button">
            <html:link page="#" onclick="document.getElementById('action').value='2'; document.contentModuleForm.submit(); return false;">
                <span><bean:message key="button.Create"/></span>
            </html:link>
        </div>

        <div class="maildetail_button"><bean:message key="cms.ContentModule"/>:</div>
    </logic:notEqual>
</div>

    