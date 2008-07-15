<%@ page language="java" import="org.agnitas.util.*, org.agnitas.actions.ops.*, java.util.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<% int index=((Integer)request.getAttribute("opIndex")).intValue(); %>

<table border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td><b><%= index+1 %>.&nbsp;<bean:message key="Send_Mailing"/>:</b></td>
    </tr>
    <tr>
        <td>
            <bean:message key="Mailing"/>:&nbsp;
            <html:select property="<%= "actions["+index+"].mailingID" %>" size="1">
                <agn:ShowTable id="agnTbl2" sqlStatement="<%= new String("SELECT a.mailing_id, a.shortname FROM mailing_tbl a, maildrop_status_tbl b WHERE a.company_id=" + AgnUtils.getCompanyID(request)+ " AND b.status_field='E' AND a.mailing_id=b.mailing_id")%>" maxRows="1000">
                    <html:option value="<%= (String)pageContext.getAttribute("_agnTbl2_mailing_id") %>"><%= pageContext.getAttribute("_agnTbl2_shortname") %></html:option>
                </agn:ShowTable>
            </html:select>
            &nbsp;<br>
            <bean:message key="Delay"/>:&nbsp;
            <html:select property="<%= "actions["+index+"].delayMinutes" %>" size="1">
                <html:option value="0"><bean:message key="No_Delay"/></html:option>
                <html:option value="60">1&nbsp;<bean:message key="Hour"/></html:option>
                <html:option value="360">6&nbsp;<bean:message key="Hours"/></html:option>
                <html:option value="720">12&nbsp;<bean:message key="Hours"/></html:option>
                <html:option value="1440">1&nbsp;<bean:message key="Day"/></html:option>
                <html:option value="2880">2&nbsp;<bean:message key="Days"/></html:option>
                <html:option value="5760">4&nbsp;<bean:message key="Days"/></html:option>
                <html:option value="10080">7&nbsp;<bean:message key="Days"/></html:option>
            </html:select>
            <html:image src="button?msg=Delete" border="0" property="deleteModule" value="<%= Integer.toString(index) %>"/>
        </td>
    </tr>
</table>