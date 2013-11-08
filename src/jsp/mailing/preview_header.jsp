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

<% int tmpMailingID=0;
   int tmpCustID=0;
   MailingSendForm aForm=(MailingSendForm)request.getAttribute("mailingSendForm");
   if(aForm!=null) {
      tmpMailingID=aForm.getMailingID();
      tmpCustID=aForm.getPreviewCustomerID();
   }
%>

<bean:message key="From"/>:&nbsp;<b><bean:write name="mailingSendForm" property="senderPreview"/></b><br>
<bean:message key="Subject"/>:&nbsp;<b><bean:write name="mailingSendForm" property="subjectPreview"/></b>
  <%  String sqlStatement="SELECT component_id, compname, target_id FROM component_tbl WHERE (comptype=3 OR comptype=4) AND mailing_id=" + 
                          tmpMailingID + 
                          " AND company_id=" + AgnUtils.getCompanyID(request) + " ORDER BY component_id";
   %>
<% boolean isFirst=true; %>
  <agn:ShowTable id="agntbl1" sqlStatement="<%= sqlStatement %>" maxRows="100">
    <agn:CustomerMatchTarget customerID="<%= tmpCustID %>" targetID="<%= Integer.parseInt(((String)pageContext.getAttribute("_agntbl1_target_id"))) %>">
    <%
        if(isFirst) { isFirst=false; %>
          <br><br>
          <b><bean:message key="Attachments"/>:</b><br>
        <% } %>
        <html:link page="<%= new String("/sc?compID=" + pageContext.getAttribute("_agntbl1_component_id") + "&mailingID=" + tmpMailingID + "&customerID=" + tmpCustID) %>"><%= pageContext.getAttribute("_agntbl1_compname") %>&nbsp;&nbsp;<img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>download.gif" border="0" alt="<bean:message key="Download"/>"></html:link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     </agn:CustomerMatchTarget>
  </agn:ShowTable>
<br><br>