<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.web.SalutationForm"  errorPage="/error.jsp" %>
<%@ page import="org.agnitas.web.SalutationAction" %>
<%@ page import="org.agnitas.web.MailloopAction" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://ajaxanywhere.sourceforge.net/" prefix="aa" %>

<% pageContext.setAttribute("ACTION_LIST", MailloopAction.ACTION_LIST); %>

<script>
    function parametersChanged(){
        return false;
    }
    function go() {
        document.getElementsByName('mailloopForm')[0].submit();
    }

    ajaxAnywhere.getZonesToReload = function () {
        return "loading"
    };

    ajaxAnywhere.onAfterResponseProcessing = function () {
        if (! ${mailloopForm.error })
            window.setTimeout("go();", ${mailloopForm.refreshMillis});
    }
    ajaxAnywhere.showLoadingMessage = function() {
    };

    ajaxAnywhere.onAfterResponseProcessing();
</script>

<%
    String sortValue = (String) request.getSession().getAttribute("mailloop_sort");
    String orderValue = (String) request.getSession().getAttribute("mailloop_dir");
%>
<aa:zone name="loading">

    <html:form action="/mailloop">
        <html:hidden property="action" value="${ACTION_LIST}"/>
        <html:hidden property="error"/>
        <html:hidden property="numberofRows"/>
        <logic:iterate collection="${mailloopForm.columnwidthsList}" indexId="i" id="width">
            <html:hidden property="columnwidthsList[${i}]"/>
        </logic:iterate>
        <div class="loading_container">
            <table border="0" cellspacing="0" cellpadding="0" width="400">
                <tr>
                    <td>
                        <b>&nbsp;<b>
                    </td>
                </tr>
                <tr>
                    <td>
                        <logic:equal value="false" name="mailloopForm" property="error">
                            <img border="0" width="44" height="48"
                                 src="${emmLayoutBase.imagesURL}/wait.gif"/>
                        </logic:equal>
                        <logic:equal value="true" name="mailloopForm" property="error">
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
                            <logic:equal value="false" name="mailloopForm" property="error">
                                <bean:message key="default.loading"/>
                            </logic:equal>
                            <logic:equal value="true" name="mailloopForm" property="error">
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
