<%@ page language="java" import="org.agnitas.util.*, java.util.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<% int index=((Integer)request.getAttribute("opIndex")).intValue(); %>

<tr>
    <td>
        <bean:message key="Column_Name"/>:
        <html:select property="<%= "actions["+index+"].nameType" %>" size="1">
            <agn:ShowColumnInfo id="colsel">
                <% if(!((String)pageContext.getAttribute("_colsel_column_name")).equalsIgnoreCase("email") && !((String)pageContext.getAttribute("_colsel_column_name")).equalsIgnoreCase("customer_id")) { %>
                <html:option value="<%= ""+pageContext.getAttribute("_colsel_column_name") +"#"+ pageContext.getAttribute("_colsel_data_type") %>"><%= pageContext.getAttribute("_colsel_shortname") %></html:option>
                <% } %>
                </agn:ShowColumnInfo>
        </html:select>
        &nbsp;
        <html:select property="<%= "actions["+index+"].updateType" %>" size="1">
            <html:option value="1">+</html:option>
            <html:option value="2">-</html:option>
            <html:option value="3">=</html:option>
        </select>
        &nbsp;
        <html:text property="<%= "actions["+index+"].updateValue" %>"/>
        &nbsp;
        <html:image src="button?msg=Delete" border="0" property="deleteModule" value="<%= Integer.toString(index) %>"/>
    </td>
</tr>
