<%@ page language="java" import="org.agnitas.util.*, java.util.*, org.agnitas.actions.ops.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<%
int index=((Integer)request.getAttribute("opIndex")).intValue();
GetArchiveList op=(GetArchiveList) request.getAttribute("op");
%>

<tr>
    <td>
        <bean:message key="Campaign"/>:&nbsp;
	<html:select property="<%= new String("actions["+index+"].campaignID") %>" size="1">
            <agn:ShowTable id="agnTbl3" sqlStatement="<%= new String("select a.campaign_id, a.shortname from campaign_tbl a where a.company_id=" + AgnUtils.getCompanyID(request))%>" maxRows="1000">
                <html:option value="<%= (String) pageContext.getAttribute("_agnTbl3_campaign_id") %>"><%= pageContext.getAttribute("_agnTbl3_shortname") %></html:option>
            </agn:ShowTable>
        </html:select>
        <html:image src="button?msg=Delete" border="0" property="deleteModule" value="<%= Integer.toString(index) %>"/>
    </td>
</tr>
