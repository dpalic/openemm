<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="settings.show"/>	 

<%
pageContext.setAttribute("sidemenu_active", new String("Settings"));
pageContext.setAttribute("sidemenu_sub_active", new String("FormsOfAddress"));
pageContext.setAttribute("agnNavigationKey", new String("Salutations"));
pageContext.setAttribute("agnHighlightKey", new String("FormsOfAddress"));
pageContext.setAttribute("agnSubtitleKey", new String("FormsOfAddress"));
pageContext.setAttribute("agnTitleKey", new String("FormsOfAddress"));
%>

<%@include file="/header.jsp"%>

<html:errors/>
<table border="0" cellspacing="0" cellpadding="0" width="100%">
    <tr><td colspan="3"><span class="head3"><bean:message key="FormsOfAddress"/>:</span><br><br></td></tr>
    <tr><td><b>ID</b>&nbsp;&nbsp;</td><td><b><bean:message key="FormOfAddress"/></b>&nbsp;&nbsp;</td><td><b>&nbsp;</b></td></tr>
    <tr><td colspan="3"><hr><center></td></tr>
    <agn:ShowTable id="agnTbl" sqlStatement="<%= new String("select title_id, description from title_tbl WHERE company_id = " + AgnUtils.getCompanyID(request)) %>" maxRows="100">
        <tr>
            <td><%= pageContext.getAttribute("_agnTbl_title_id") %></td>
            <td><html:link page="<%= new String("/salutation.do?action=" + SalutationAction.ACTION_VIEW + "&salutationID=" + pageContext.getAttribute("_agnTbl_title_id")) %>"><%= pageContext.getAttribute("_agnTbl_description") %>&nbsp;&nbsp;</html:link>&nbsp;&nbsp;</td>
            <td>
                <html:link page="<%= new String("/salutation.do?action=" + SalutationAction.ACTION_CONFIRM_DELETE + "&salutationID=" + pageContext.getAttribute("_agnTbl_title_id")) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>delete.gif" alt="<bean:message key="Delete"/>" border="0"></html:link>
                <html:link page="<%= new String("/salutation.do?action=" + SalutationAction.ACTION_VIEW + "&salutationID=" + pageContext.getAttribute("_agnTbl_title_id")) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>bearbeiten.gif" alt="<bean:message key="Edit"/>" border="0"></html:link>
            </td>
        </tr>
    </agn:ShowTable>
</table>
<%@include file="/footer.jsp"%>
