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

<agn:CheckLogon/>
<agn:Permission token="recipient.delete"/>

<% pageContext.setAttribute("sidemenu_active", new String("Recipient")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("Overview")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Recipient")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Recipient")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("subscriber_editor")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Overview")); %>

<%@include file="/header.jsp"%>

<%
RecipientForm recipient=(RecipientForm)session.getAttribute("recipientForm");
recipient.setAction(RecipientAction.ACTION_DELETE);
%>
<html:errors/>
    <html:form action="/recipient">
        <html:hidden property="recipientID"/>
        <html:hidden property="action"/>
    <html:hidden property="user_type"/>
    <html:hidden property="user_status"/>
    <html:hidden property="listID"/>
        <span class="head1"><%= recipient.getFirstname()+" "+recipient.getLastname() %></span><br>
        <br>
        <b><bean:message key="recipient.confirm_delete"/></b><br>
          <p>

                <html:image src="button?msg=Delete" property="kill" value="kill"/>
                <html:link page="<%= new String("/recipient.do?action=" + RecipientAction.ACTION_VIEW + "&recipientID=" + recipient.getRecipientID() +"&user_type=" + request.getParameter("user_type") + "&user_status=" + request.getParameter("user_status") + "&listID=" + request.getParameter("listID")) %>"><html:img src="button?msg=Cancel" border="0"/></html:link>
          </p>

    </html:form>

<%@include file="/footer.jsp"%>
