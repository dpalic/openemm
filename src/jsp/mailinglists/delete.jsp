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

<% int tmpMailinglistID=0;
   String tmpShortname=new String("");
   if(request.getAttribute("mailinglistForm")!=null) {
      tmpMailinglistID=((MailinglistForm)request.getAttribute("mailinglistForm")).getMailinglistID();
      tmpShortname=((MailinglistForm)request.getAttribute("mailinglistForm")).getShortname();
   }
%>

<agn:Permission token="mailinglist.delete"/>

<% pageContext.setAttribute("sidemenu_active", new String("Mailinglists")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("Overview")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Mailinglist")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Mailinglist")); %>
<% pageContext.setAttribute("agnSubtitleValue", tmpShortname); %>
<% pageContext.setAttribute("agnNavigationKey", new String("mailinglists")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Mailinglist")); %>
<% pageContext.setAttribute("agnNavHrefAppend", new String("&mailinglistID="+tmpMailinglistID)); %>
<%@include file="/header.jsp"%>

<html:errors/>

<html:form action="/mailinglist">
                <html:hidden property="mailinglistID"/>
                <html:hidden property="action"/>
                <html:hidden property="shortname"/>
                <html:hidden property="description"/>
                <span class="head3"><bean:message key="mailinglist.delete.question"/></span>
                <br><br><br>
                <html:image src="button?msg=Delete" border="0" property="kill" value="kill"/>
                <html:link page="<%= new String("/mailinglist.do?action=" + Integer.toString(MailinglistAction.ACTION_VIEW) + "&mailinglistID=" + tmpMailinglistID) %>"><html:img src="button?msg=Cancel" border="0"/></html:link>
              </html:form>

<%@include file="/footer.jsp"%>
