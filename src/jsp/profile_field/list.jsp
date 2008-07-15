<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<agn:CheckLogon/>

<agn:Permission token="profileField.show"/>

<% pageContext.setAttribute("sidemenu_active", new String("Settings")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("Profile_DB")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Profile_Database")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Profile_Database")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("profiledb")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Profile_DB")); %>

<%@include file="/header.jsp"%>

<html:errors/>

<table border="0" cellspacing="0" cellpadding="0" width="100%">
    <tr>
        <td><span class="head3"><bean:message key="FieldName"/>&nbsp;&nbsp;</span></td>
        <td><span class="head3"><bean:message key="FieldNameDB"/>&nbsp;&nbsp;</span></td>
        <td><span class="head3"><bean:message key="Type"/>&nbsp;&nbsp;</span></td>
        <td><span class="head3"><bean:message key="Length"/>&nbsp;&nbsp;</span></td>
        <td><span class="head3"><bean:message key="Default_Value"/>&nbsp;&nbsp;</span></td>
        <td><span class="head3">&nbsp;</span></td>
    </tr>

    <tr><td colspan="6"><hr></td></tr>

    <agn:ShowColumnInfo id="agnTbl" table="<%= AgnUtils.getCompanyID(request) %>" hide="change_date, creation_date, title, datasource_id, email, firstname, lastname, gender, mailtype, customer_id">
        <tr> <!-- MailingBaseAction.ACTION_VIEW -->
            <td><html:link page="<%= new String("/profiledb.do?action=" + ProfileFieldAction.ACTION_VIEW + "&fieldname=" + pageContext.getAttribute("_agnTbl_column_name")) %>"><b><%= pageContext.getAttribute("_agnTbl_shortname") %></b></html:link>&nbsp;&nbsp;</td>
            <td><html:link page="<%= new String("/profiledb.do?action=" + ProfileFieldAction.ACTION_VIEW + "&fieldname=" + pageContext.getAttribute("_agnTbl_column_name")) %>"><%= pageContext.getAttribute("_agnTbl_column_name") %></html:link>&nbsp;&nbsp;</td>
            <td><bean:message key="<%= "fieldType."+pageContext.getAttribute("_agnTbl_data_type") %>"/>&nbsp;&nbsp;</td>
            <td><div align="right"><% if(((String)pageContext.getAttribute("_agnTbl_data_type")).equals("VARCHAR")) { %><%=pageContext.getAttribute("_agnTbl_data_length")%><% } %>&nbsp;&nbsp;&nbsp;&nbsp;</div></td>
            <td><div align="right"><%=pageContext.getAttribute("_agnTbl_data_default").toString().trim()%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div></td>
            <td>
                <html:link page="<%= new String("/profiledb.do?action=" + ProfileFieldAction.ACTION_CONFIRM_DELETE + "&fieldname=" + pageContext.getAttribute("_agnTbl_column_name")) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>delete.gif" alt="<bean:message key="Delete"/>" border="0"></html:link>&nbsp;
                <html:link page="<%= new String("/profiledb.do?action=" + ProfileFieldAction.ACTION_VIEW + "&fieldname=" + pageContext.getAttribute("_agnTbl_column_name")) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>bearbeiten.gif" alt="<bean:message key="Edit"/>" border="0"></html:link>
            </td>
        </tr>

    </agn:ShowColumnInfo>

    <tr><td colspan="6"><hr></td></tr>

</table>
<%@include file="/footer.jsp"%>
