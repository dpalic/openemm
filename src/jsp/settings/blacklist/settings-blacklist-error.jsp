<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8"  errorPage="/error.jsp" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

	<table>
	<html:form action="/blacklist">
		 <html:hidden property="action" value="${ACTION_LIST}" />
    <tr>
        <td><html:image src="button?msg=button.OK" border="0" /></td>
    </tr>
   
	</html:form> 
	</table>
