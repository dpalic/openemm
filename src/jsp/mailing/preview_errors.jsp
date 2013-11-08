<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="org.agnitas.web.MailingSendAction;"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	
	 <link type="text/css" rel="stylesheet" href="<bean:write name="emm.layout" property="baseUrl" scope="session"/>stylesheet.css">
     <link type="text/css" rel="stylesheet" href="styles/displaytag.css">
     <link type="text/css" rel="stylesheet" href="styles/tooltiphelp.css">
     <link type="text/css" rel="stylesheet" href="styles/reportstyles.css">  
     <link type="text/css" rel="stylesheet" href="styles/pidstyles.css"> 
      
</head>
<body>
<logic:messagesPresent>
	<div style="padding: 10px">
		<div class="error_box">
			<html:messages id="msg_key" message="false">
				<span class="error_message"><bean:write name="msg_key" /></span><br />
			</html:messages>
		</div>
	</div>
</logic:messagesPresent>
	
</body>
</html>