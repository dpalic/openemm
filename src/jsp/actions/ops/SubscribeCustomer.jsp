<%@ page language="java" import="org.agnitas.util.*, java.util.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<% int index=((Integer)request.getAttribute("opIndex")).intValue(); %>

<table border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td><b><%= index+1 %>.&nbsp;<bean:message key="Subscribe"/>:</b></td>
    </tr>

    <tr>
        <td>
            <html:checkbox property="<%= new String("actions["+index+"].doubleOptIn") %>"><bean:message key="UseDblOptIn"/></html:checkbox>
        </td>
    </tr>
                            
    <tr>
        <td>
            <html:checkbox property="<%= new String("actions["+index+"].doubleCheck") %>"><bean:message key="import.doublechecking"/></html:checkbox>
        </td>
    </tr>
                            
    <tr>
        <td>
            <bean:message key="import.keycolumn"/>:&nbsp;
            <html:select property="<%= new String("actions["+index+"].keyColumn") %>" size="1">
                <agn:ShowColumnInfo id="tbl">
                    <html:option value="<%= (String)pageContext.getAttribute("_tbl_column_name") %>"><%= pageContext.getAttribute("_tbl_shortname") %></html:option>
                </agn:ShowColumnInfo>
            </html:select>
            &nbsp;<br>
            <html:image src="button?msg=Delete" border="0" property="deleteModule" value="<%= Integer.toString(index) %>"/>
        </td>
    </tr>
                            
</table>