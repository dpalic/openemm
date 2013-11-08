<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:ShowColumnInfo id="colsel"/>

<script type="text/javascript">
	function submitAction(actionId) {
		document.getElementsByName("action")[0].value = actionId;
		document.targetForm.submit();
	}
</script>

<html:form action="/target" focus="shortname">
    <html:hidden property="targetID"/>
    <html:hidden property="action"/>

    <div class="mailing_name_box_container">
        <div class="mailing_name_box_top"></div>
        <div class="mailing_name_box_content">
            <div class="mailing_name_box_left_column">
                <label for="mailing_name"><bean:message key="default.Name"/>:</label>
                <html:text styleId="mailing_name" property="shortname" size="42" maxlength="99"/>
            </div>
            <div class="mailing_name_box_center_column">
                <label for="mailing_name"><bean:message key="default.description"/>:</label>
                <html:textarea styleId="mailing_description" property="description" cols="32" rows="5"/>
            </div>
            <div class="mailing_name_box_right_column"></div>
        </div><script type="text/javascript">
	function submitAction(actionId) {
		document.getElementsByName("action")[0].value = actionId;
		document.targetForm.submit();
	}
</script>
        
        <div class="mailing_name_box_bottom"></div>
    </div>

    <div class="emailbox_container">
        <div class="emailbox_top"></div>
        <div class="emailbox_content">
            <h2 class="targetgroup_nodes_header"><bean:message key="target.TargetDefinition"/>:</h2>

            <table border="0" cellspacing="2" cellpadding="0">
                <!-- list of defined rules -->
                <c:set var="FORM_NAME" value="targetForm" scope="page"/>
                <%@include file="/rules/rules_list.jsp" %>
            </table>
        </div>
        <div class="emailbox_bottom"></div>
    </div>
    <div class="emailbox_container">
        <div class="emailbox_top"></div>
        <div class="emailbox_content">
            <h2 class="targetgroup_nodes_header"><bean:message key="target.NewRule"/>:</h2>
            <table border="0" cellspacing="2" cellpadding="0">

                <!-- new rule to add -->
                <%@include file="/rules/rule_add.jsp" %>
            </table>
        </div>
        <div class="emailbox_bottom"></div>
    </div>
    <div class="target_button_container">

        <input type="hidden" id="save" name="save" value=""/>

        <div class="maildetail_button">
            <a href="#"
               onclick="submitAction(${ACTION_SAVE}); return false;"><span><bean:message
                    key="button.Save"/></span></a>
        </div>

        <c:if test="${not empty targetForm.targetID and targetForm.targetID != 0}">
            <input type="hidden" id="delete" name="delete" value=""/>

            <div class="maildetail_button">
                <a href="#"
                   onclick="submitAction(${ACTION_CONFIRM_DELETE}); return false;"><span><bean:message
                        key="button.Delete"/></span></a>
            </div>

            <input type="hidden" id="copy" name="copy" value=""/>

            <div class="maildetail_button">
                <a href="#"
                   onclick="submitAction(${ACTION_CLONE}); return false;"><span><bean:message
                        key="button.Copy"/></span></a>
            </div>
        </c:if>

        <div class="maildetail_button"><bean:message key="target.Target"/>:</div>
    </div>

    <c:if test="${not empty targetForm.targetID and targetForm.targetID != 0}">
        <div class="target_button_container">
            <div align=right><html:link styleClass="target_view_link"
                                        page="/recipient_stats.do?action=2&mailinglistID=0&targetID=${targetForm.targetID}"><bean:message
                    key="Statistics"/>...</html:link></div>
            <agn:ShowByPermission token="targets.createml">
                <br>

                <div align=right><html:link styleClass="target_view_link"
                                            page="/target.do?action=${ACTION_CREATE_ML}&targetID=${targetForm.targetID}"><bean:message
                        key="target.createMList"/></html:link></div>
            </agn:ShowByPermission>

            <agn:ShowByPermission token="recipient.delete">
                <br>

                <div align=right><html:link styleClass="target_view_link"
                                            page="/target.do?action=${ACTION_DELETE_RECIPIENTS_CONFIRM}&targetID=${targetForm.targetID}"><bean:message
                        key="target.delete.recipients"/></html:link></div>
            </agn:ShowByPermission>
        </div>
    </c:if>
</html:form>
