<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.util.AgnUtils, org.agnitas.web.NewImportWizardAction, org.agnitas.web.forms.NewImportWizardForm"  errorPage="/error.jsp" %>
<%@ page import="org.agnitas.web.forms.UserActivityLogForm" %>
<%@ page import="org.agnitas.web.UserActivityLogAction" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://ajaxanywhere.sourceforge.net/" prefix="aa" %>

<script>

    function go() {
        document.getElementsByName('userActivityLogForm')[0].submit();
    }

    ajaxAnywhere.getZonesToReload = function () {
        return "loading"
    };

    ajaxAnywhere.onAfterResponseProcessing = function () {
        if (! ${userActivityLogForm.error })
            window.setTimeout("go();", ${userActivityLogForm.refreshMillis});
    }
    ajaxAnywhere.showLoadingMessage = function() {
    };

    ajaxAnywhere.onAfterResponseProcessing();
</script>

<%
    UserActivityLogForm recipient = (UserActivityLogForm) session.getAttribute("userActivityLogForm");
    recipient.setAction(UserActivityLogAction.ACTION_LIST);
%>

<aa:zone name="loading">
    <%@include file="/messages.jsp" %>
    <html:form action="/useractivitylog">
        <html:hidden property="action"/>
        <html:hidden property="error"/>
        <div class="loading_container">
            <table border="0" cellspacing="0" cellpadding="0" width="300">
                <tr>
                    <td>
                        <b>&nbsp;<b>
                    </td>
                </tr>
                <tr>
                    <td>
                        <logic:equal value="false" name="userActivityLogForm"
                                     property="error">
                            <img border="0" width="44" height="48"
                                 src="${emmLayoutBase.imagesURL}/wait.gif"/>
                        </logic:equal>
                        <logic:equal value="true" name="userActivityLogForm"
                                     property="error">
                            <img border="0" width="29" height="30"
                                 src="${emmLayoutBase.imagesURL}/warning.gif"/>
                        </logic:equal>
                    </td>
                </tr>
                <tr>
                    <td>
                        <b>&nbsp;<b>
                    </td>
                </tr>
                <tr>
                    <td>
                        <b>
                            <logic:equal value="false" name="userActivityLogForm"
                                         property="error">
                                <bean:message key="default.loading"/>
                            </logic:equal>
                            <logic:equal value="true" name="userActivityLogForm"
                                         property="error">
                                <bean:message key="default.loading.stopped"/>
                            </logic:equal>

                            <b>
                    </td>
                </tr>
                <tr>
                    <td>
                        <b>&nbsp;<b>
                    </td>
                </tr>

            </table>
        </div>
    </html:form>
</aa:zone>