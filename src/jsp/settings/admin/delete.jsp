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
 --%><%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, java.util.*" contentType="text/html; charset=utf-8" buffer="32kb" errorPage="/error.jsp"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<% int tmpAdminID=0;
   String tmpUsername=new String("");
   if(request.getAttribute("adminForm")!=null) {
      tmpAdminID=((AdminForm)request.getAttribute("adminForm")).getAdminID();
      tmpUsername=((AdminForm)request.getAttribute("adminForm")).getUsername();
   }
%>


<% pageContext.setAttribute("agnSubtitleKey", new String("Admins")); %>             <!-- ueber rechte Seite -->
<% pageContext.setAttribute("sidemenu_active", new String("Settings")); %>          <!-- links Button -->
<% pageContext.setAttribute("sidemenu_sub_active", new String("Admins")); %>        <!-- links unter Button -->
<% pageContext.setAttribute("agnTitleKey", new String("Admins")); %>                <!-- Titelleiste -->


<% if(tmpAdminID!=0) {
     pageContext.setAttribute("agnNavigationKey", new String("admins"));
     pageContext.setAttribute("agnHighlightKey", new String("Overview"));
   } else {
     pageContext.setAttribute("agnNavigationKey", new String("Admins"));
     pageContext.setAttribute("agnHighlightKey", new String("New_Admin"));
   } 
%>


<% pageContext.setAttribute("agnSubtitleValue", tmpUsername); %>
<% pageContext.setAttribute("agnNavHrefAppend", new String("&adminID="+tmpAdminID)); %>

<%@include file="/header.jsp"%>

<html:errors/>

<html:form action="admin">
    <html:hidden property="adminID"/>
    <html:hidden property="action"/>
    <span class="head3"><bean:message key="AdminDeleteQuestion"/></span>
    <br><br><br>
    <html:image src="button?msg=Delete" border="0" property="kill" value="kill"/>
    <html:link page="<%= new String("/admin.do?action=" + AdminAction.ACTION_VIEW + "&adminID=" + tmpAdminID) %>"><html:img src="button?msg=Cancel" border="0"/></html:link>
</html:form>

<%@include file="/footer.jsp"%>
