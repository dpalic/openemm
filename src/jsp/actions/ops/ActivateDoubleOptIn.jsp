<%@ page language="java" import="org.agnitas.util.*, java.util.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<% int index=((Integer)request.getAttribute("opIndex")).intValue(); %>

<tr>
    <td>
        &nbsp;<br>
        <html:image src="button?msg=Delete" border="0" property="deleteModule" value="<%= Integer.toString(index) %>"/>
    </td>
</tr>
