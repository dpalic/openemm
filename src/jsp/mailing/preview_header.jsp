<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*"%>
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

<html:errors/>
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