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
 --%><%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*, java.util.*, org.springframework.context.*, org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/ajaxanywhere.tld" prefix="aa" %>

<agn:CheckLogon/>
<agn:Permission token="recipient.show"/>

<% pageContext.setAttribute("sidemenu_active","Settings"); %>
<% pageContext.setAttribute("sidemenu_sub_active", "Blacklist"); %>
<% pageContext.setAttribute("agnTitleKey","Blacklist"); %>
<% pageContext.setAttribute("agnSubtitleKey","Blacklist"); %>
<% pageContext.setAttribute("agnNavigationKey","blacklist"); %>
<% pageContext.setAttribute("agnHighlightKey","Blacklist"); %>
<% pageContext.setAttribute("ACTION_LIST",BlacklistAction.ACTION_LIST); %>

<%@include file="/header.jsp"%>
<%@include file="/messages.jsp" %>

	<table>
	<html:form action="/blacklist">
		 <html:hidden property="action" value="${ACTION_LIST}" />
    <tr>
        <td><html:image src="button?msg=OK" border="0" /></td>
    </tr>
   
	</html:form> 
	</table>

<%@include file="/footer.jsp"%>
