<%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<agn:CheckLogon/>

<agn:Permission token="stats.ip"/>

<% pageContext.setAttribute("sidemenu_active", new String("Statistics")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("IPStats")); %>


<% pageContext.setAttribute("agnTitleKey", new String("IPStats")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Statistics")); %>

<% pageContext.setAttribute("agnNavigationKey", new String("IPStats")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("IPStats")); %>
<% pageContext.setAttribute("agnRefresh", new String("2")); %>

<%@include file="/header.jsp"%>

<html:errors/>

<html:form action="/ip_stats">
<html:hidden property="action"/>

<table border="0" cellspacing="0" cellpadding="0" width="400">
    <tr>
        <td>
            <b>&nbsp;<b>
        </td>
    </tr>
    <tr>
        <td>
            <img border="0" width="44" height="48" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>wait.gif"/>
        </td>
    </tr>
    <tr>
        <td>
            <b>&nbsp;<b>
        </td>
    </tr>
    <tr>
        <td>
            <b><bean:message key="StatSplashMessage"/><b>
        </td>
    </tr>
    <tr>
        <td>
            <b>&nbsp;<b>
        </td>
    </tr>

</table>

</html:form>

<%@include file="/footer.jsp"%> 
