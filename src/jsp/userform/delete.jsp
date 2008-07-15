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
 --%><%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, java.util.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<% int tmpFormID=0;
   String tmpFormName=new String("");
   if(request.getAttribute("userFormEditForm")!=null) {
      tmpFormID=((UserFormEditForm)request.getAttribute("userFormEditForm")).getFormID();
      tmpFormName=((UserFormEditForm)request.getAttribute("userFormEditForm")).getFormName();
   }
%>

<agn:Permission token="forms.delete"/>

<% pageContext.setAttribute("sidemenu_active", new String("Forms")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("Overview")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Form")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Form")); %>
<% pageContext.setAttribute("agnSubtitleValue", tmpFormName); %>
<% pageContext.setAttribute("agnNavigationKey", new String("formView")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Form")); %>
<%@include file="/header.jsp"%>

<html:errors/>

<html:form action="/userform">
                <html:hidden property="formID"/>
                <html:hidden property="action"/>
                <span class="head3"><bean:message key="form.delete.question"/></span>
                <br><br><br>
                <html:image src="button?msg=Delete" border="0" property="delete" value="delete"/>
                <html:link page="<%= new String("/userform.do?action=" + UserFormEditAction.ACTION_VIEW + "&formID=" + tmpFormID) %>"><html:img src="button?msg=Cancel" border="0"/></html:link>
              </html:form>

<%@include file="/footer.jsp"%>