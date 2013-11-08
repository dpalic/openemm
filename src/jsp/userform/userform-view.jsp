<%-- checked --%>
<%@ page language="java" import="org.agnitas.web.UserFormEditAction,org.agnitas.util.AgnUtils" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="ACTION_CONFIRM_DELETE" value="<%= UserFormEditAction.ACTION_CONFIRM_DELETE %>" scope="request" />

<html:form action="/userform">
	<html:hidden property="formID" />
	<html:hidden property="action" />

    <div class="mailing_name_box_container">
        <div class="mailing_name_box_top"></div>
        <div class="mailing_name_box_content">
            <div class="mailing_name_box_left_column">
                <label for="mailing_name"><bean:message key="default.Name"/>:</label>
                <html:text styleId="mailing_name" property="formName" maxlength="99" size="42"/>
            </div>
            <div class="mailing_name_box_center_column">
                <label for="mailing_name"><bean:message key="default.description"/>:</label>
                <html:textarea styleId="mailing_description" property="description" rows="5" cols="32"/>
            </div>
            <div class="mailing_name_box_right_column"></div>
        </div>
        <div class="mailing_name_box_bottom"></div>
    </div>

    <div class="emailbox_container">
        <div class="emailbox_top"></div>
        <div class="emailbox_content">
            <bean:message key="action.Action"/>:&nbsp;

            <html:select property="startActionID" size="1">
                <html:option value="0">
                    <bean:message key="settings.No_Action"/>
                </html:option>
                <agn:ShowTable id="agntbl2"
                               sqlStatement='<%= new String(\"SELECT action_id, shortname FROM rdir_action_tbl WHERE company_id=\"+AgnUtils.getCompanyID(request)+\" AND action_type<>0 ORDER BY shortname\") %>'
                               maxRows="500">
                    <html:option
                            value='<%= (String)pageContext.getAttribute(\"_agntbl2_action_id\") %>'><%= pageContext.getAttribute("_agntbl2_shortname") %>
                    </html:option>
                </agn:ShowTable>
            </html:select>
            <br>
            <br>
            <b><bean:message key="settings.form.success"/>:</b>
            <br>
            <html:textarea property="successTemplate" rows="14" cols="75"/>
        </div>
        <div class="emailbox_bottom"></div>
    </div>

    <div class="emailbox_container">
        <div class="emailbox_top"></div>
        <div class="emailbox_content">
            <br>
            <b><bean:message key="settings.form.error"/>:</b>
            <br>
            <html:textarea property="errorTemplate" rows="14" cols="75"/>
            <br>
            <br>
            <bean:message key="action.Action"/>:&nbsp;

            <html:select property="endActionID" size="1">
                <html:option value="0">
                    <bean:message key="settings.No_Action"/>
                </html:option>
                <agn:ShowTable id="agntbl3"
                               sqlStatement='<%= new String(\"SELECT action_id, shortname FROM rdir_action_tbl WHERE company_id=\"+AgnUtils.getCompanyID(request)+\" AND action_type<>0 ORDER BY shortname\") %>'
                               maxRows="500">
                    <html:option
                            value='<%= (String)pageContext.getAttribute(\"_agntbl3_action_id\") %>'><%= pageContext.getAttribute("_agntbl3_shortname") %>
                    </html:option>
                </agn:ShowTable>
            </html:select>
        </div>
        <div class="emailbox_bottom"></div>
    </div>


    <div class="target_button_container">
        <agn:ShowByPermission token="forms.change">
            <input type="hidden" id="save" name="save" value=""/>

            <div class="maildetail_button">
                <a href="#"
                   onclick="document.getElementById('save').value='save'; document.userFormEditForm.submit(); return false;"><span><bean:message
                        key="button.Save"/></span></a>
            </div>
        </agn:ShowByPermission>
        <agn:ShowByPermission token="forms.delete">
            <c:if test="${userFormEditForm.formID != 0}">
                <div class="maildetail_button">
                    <html:link
                            page='/userform.do?action=${ACTION_CONFIRM_DELETE}&formID=${userFormEditForm.formID}'>
                        <span><bean:message key="button.Delete"/></span>
                    </html:link>
                </div>
            </c:if>
        </agn:ShowByPermission>

    </div>
</html:form>
