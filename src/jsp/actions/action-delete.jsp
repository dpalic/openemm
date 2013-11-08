<%--checked--%>
<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.web.EmmActionAction" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<% int tmpActionID = 0;

    if (request.getAttribute("tmpActionID") != null) {
        tmpActionID = (Integer) request.getAttribute("tmpActionID");
    }
%>
<div class="new_mailing_start_description"><bean:message
        key="action.Action"/>:&nbsp;<%= request.getAttribute("tmpShortname") %><br><bean:message key="action.deleteQuestion"/>
</div>
<div class="remove_element_button_container">
    <div class="greybox_small_top"></div>
    <div class="greybox_small_content">
        <div class="new_mailing_step1_left_column">
            <input type="hidden" id="delete" name="delete" value=""/>
			<agn:ShowByPermission token="actions.delete">
            	<div class="big_button">
                	<a href="<html:rewrite page='<%= new String(\"/action.do?actionID=\" + tmpActionID + \"&action=\" + EmmActionAction.ACTION_DELETE) %>'/>">
                    	<span> <bean:message key="button.Delete"/></span>
                	</a>
            	</div>
            </agn:ShowByPermission>
        </div>
        <div class="new_mailing_step1_right_column">
            <div class="big_button"><a
                    href="<html:rewrite page='<%= new String(\"/action.do?actionID=\" + tmpActionID + \"&action=\" + EmmActionAction.ACTION_VIEW) %>'/>"><span><bean:message
                    key="button.Cancel"/></span></a></div>
        </div>
    </div>
    <div class="greybox_small_bottom"></div>
</div>