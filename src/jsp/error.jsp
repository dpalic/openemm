<%--
/*********************************************************************************
* The contents of this file are subject to the Common Public Attribution
* License Version 1.0 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.openemm.org/cpal1.html. The License is based on the Mozilla
* Public License Version 1.1 but Sections 14 and 15 have been added to cover
* use of software over a computer network and provide for limited attribution
* for the Original Developer. In addition, Exhibit A has been modified to be
* consistent with Exhibit B.
* Software distributed under the License is distributed on an "AS IS" basis,
* WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
* the specific language governing rights and limitations under the License.
*
* The Original Code is OpenEMM.
* The Original Developer is the Initial Developer.
* The Initial Developer of the Original Code is AGNITAS AG. All portions of
* the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
* Reserved.
*
* Contributor(s): AGNITAS AG.
********************************************************************************/
--%>

<%@page import="org.agnitas.util.SafeString"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils, org.agnitas.emm.core.commons.util.ConfigService, java.text.MessageFormat"%>
<%@page import="org.agnitas.emm.core.commons.util.ConfigService"%>
<%@ page isErrorPage="true" language="java" pageEncoding="UTF-8" import="org.apache.log4j.Logger, org.agnitas.util.AgnUtils" %>
<%@ page import="java.util.Enumeration" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<%
    Logger logger = Logger.getLogger("error.jsp");

	ConfigService configService = (ConfigService) WebApplicationContextUtils.getRequiredWebApplicationContext(request.getSession().getServletContext()).getBean("ConfigService");

    StringBuilder errorBuilder = new StringBuilder();

    if (exception != null) {
        errorBuilder.append(exception.getMessage() + "\n" + AgnUtils.throwableToString(exception, -1));
    }

    errorBuilder.append("\nRequest Parameters:\n");
    Enumeration parameterNames = request.getParameterNames();
    while (parameterNames.hasMoreElements()) {
        String currentParamName = (String) parameterNames.nextElement();
        errorBuilder.append(currentParamName).append(": ").append(request.getParameter(currentParamName)).append("\n");
    }

    errorBuilder.append("\nRequest Attributes:\n");
    errorBuilder.append("IP: ").append(request.getRemoteAddr()).append("\n");
    Enumeration attrNames = request.getAttributeNames();
    while (attrNames.hasMoreElements()) {
        String currentAttrName = (String) attrNames.nextElement();
        errorBuilder.append(currentAttrName).append(": ").append(request.getAttribute(currentAttrName)).append("\n");
    }

    logger.error(errorBuilder.toString(), exception);
%>

<html:html>

<head>
    <title><bean:message key="error.global.title"/></title>
    <link type="text/css" rel="stylesheet" href="${emmLayoutBase.cssURL}/style.css">
    <link type="text/css" rel="stylesheet" href="${emmLayoutBase.cssURL}/structure.css">
    <link type="text/css" rel="stylesheet" href="${emmLayoutBase.cssURL}/displaytag.css">
    <link type="text/css" rel="stylesheet" href="${emmLayoutBase.cssURL}/ie7.css">
    <style type="text/css">
        html, body {
            height: 100%;
            margin: 0 auto;
            padding: 0;
        }

        body {
            position: relative;
        }
    </style>
</head>
<body>
    <div class="login_page_root_container">
        <div class="login_page_top_spacer"></div>
        <div class="loginbox_container">

            <div class="loginbox_top"></div>

            <div class="loginbox_content">
                <img src="${emmLayoutBase.imagesURL}/logo.png" border="0" class="logon_image">
                <br>
                <span class="logon_page_emm_title"><bean:message key="error.global.title"/></span>
            	<div class="loginbox_row"><br/></div>
            	<div class="loginbox_row">
 					<%= MessageFormat.format(SafeString.getLocaleString("error.global.message", request.getLocale()), configService.getValue(ConfigService.Value.SupportEmergencyUrl)) %>
 				</div>
            </div>

            <div class="loginbox_bottom"></div>
        </div>
    </div>
</body>
</html:html>