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
 --%><%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<agn:CheckLogon/>

<agn:Permission token="profileField.show"/>

<% pageContext.setAttribute("sidemenu_active", new String("Settings")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("Profile_DB")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Profile_Database")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Profile_Database")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("profiledb")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Profile_DB")); %>

<%@include file="/header.jsp"%>
<%@include file="/messages.jsp" %>

     <html:form action="/profiledb">
         <br>   
         <span class="head1"><%= ((ProfileFieldForm)request.getAttribute("profileFieldForm")).getFieldname() %></span><br>
         <br>
         <b><bean:message key="ProfileFieldErrorMsg"/><br><%=((ProfileFieldForm)request.getAttribute("profileFieldForm")).getTargetsDependent()%></b><br>
         <html:hidden property="companyID"/>
         <html:hidden property="action"/>
         <html:hidden property="fieldname"/>
         <html:hidden property="targetsDependent"/>
         <br>
         <html:link page="<%= new String("/profiledb.do?action=" + ProfileFieldAction.ACTION_LIST) %>"><html:img src="button?msg=Back" border="0"/></html:link>

     </html:form>  

<%@include file="/footer.jsp"%>
