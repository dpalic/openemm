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
 --%><%@ page language="java" import="org.agnitas.beans.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>
<% pageContext.setAttribute("sidemenu_active", new String("Settings")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("none")); %>
<% pageContext.setAttribute("agnTitleKey", new String("A_EMM")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Settings")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("none")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("none")); %>
<%
String	host = request.getHeader ("host");
if (host != null) {
	int	n = host.indexOf (':');

	if (n != -1) {
		host = host.substring (0, n);
	}
} else
	host = "localhost";
pageContext.setAttribute("agnRefresh", new String ("2; URL=http://" + host + ":8044/"));
%>

<%@include file="/header.jsp"%>
<%@include file="/messages.jsp" %>

              <table border="0" cellspacing="0" cellpadding="0">
                <tr>  
                  <td>
                    <bean:message key="update.success"/>
                  </td>
                </tr>
              </table>
<%@include file="/footer.jsp"%>
