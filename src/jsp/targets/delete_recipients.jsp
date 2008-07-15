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

<agn:Permission token="recipient.delete"/>

<% int tmpTargetID = 0;
   String tmpShortname = new String("");
   int tmpNumOfRecipients = 0;
   if(request.getAttribute("targetForm")!=null) {
      tmpTargetID = ((TargetForm)request.getAttribute("targetForm")).getTargetID();
      tmpShortname = ((TargetForm)request.getAttribute("targetForm")).getShortname();
      tmpNumOfRecipients = ((TargetForm)request.getAttribute("targetForm")).getNumOfRecipients();
   }
%>

<% pageContext.setAttribute("sidemenu_active", new String("Targets")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("none")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Target")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Target")); %>
<% pageContext.setAttribute("agnSubtitleValue", new String(tmpShortname)); %>
<% pageContext.setAttribute("agnNavigationKey", new String("targetView")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Target")); %>
<% pageContext.setAttribute("agnNavHrefAppend", new String("&targetID="+tmpTargetID)); %>
<%@include file="/header.jsp"%>

<html:errors/>

	<span class="head3"><bean:message key="target.delete.recipients.question.first"/>&nbsp;<%= tmpNumOfRecipients %>&nbsp;<bean:message key="target.delete.recipients.question.last"/></span>
    <br><br>
    <html:link page="<%= new String("/target.do?action=" + TargetAction.ACTION_DELETE_RECIPIENTS + "&targetID=" + tmpTargetID) %>"><img src="button?msg=Delete" border="0"></html:link>&nbsp;<html:link page="<%= new String("/target.do?action=" + TargetAction.ACTION_VIEW + "&targetID=" + tmpTargetID) %>"><html:img src="button?msg=Cancel" border="0"/></html:link>

<%@include file="/footer.jsp"%>