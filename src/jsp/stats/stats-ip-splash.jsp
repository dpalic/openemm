<%@ page language="java" contentType="text/html; charset=utf-8"  errorPage="/error.jsp" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<html:form action="/ip_stats">
    <html:hidden property="action"/>
    <div class="loading_container">
        <table border="0" cellspacing="0" cellpadding="0" width="400">
            <tr>
                <td>
                    <b>&nbsp;</b>
                </td>
            </tr>
            <tr>
                <td>
                    <img border="0" width="44" height="48"
                         src="${emmLayoutBase.imagesURL}/wait.gif"/>
                </td>
            </tr>
            <tr>
                <td>
                    <b>&nbsp;</b>
                </td>
            </tr>
            <tr>
                <td>
                    <b><bean:message key="statistic.StatSplashMessage"/></b>
                </td>
            </tr>
            <tr>
                <td>
                    <b>&nbsp;</b>
                </td>
            </tr>

        </table>
    </div>

</html:form>

