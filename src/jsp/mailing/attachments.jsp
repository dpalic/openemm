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
 --%><%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*, org.agnitas.target.*, java.util.*" contentType="text/html; charset=utf-8" %>
<jsp:directive.page import="org.springframework.context.ApplicationContext"/>
<jsp:directive.page import="org.springframework.web.context.support.WebApplicationContextUtils"/>
<jsp:directive.page import="org.agnitas.dao.TargetDao"/>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>
<agn:Permission token="mailing.attachments.show"/>

<% int tmpMailingID=0;
    String tmpShortname=new String("");
    MailingAttachmentsForm aForm=(MailingAttachmentsForm)request.getAttribute("mailingAttachmentsForm");
    if(request.getAttribute("mailingAttachmentsForm")!=null) {
        tmpMailingID=aForm.getMailingID();
        tmpShortname=aForm.getShortname();
    }
%>

<logic:equal name="mailingAttachmentsForm" property="isTemplate" value="true">
    <% // template navigation:
        pageContext.setAttribute("sidemenu_active", new String("Templates"));
        pageContext.setAttribute("sidemenu_sub_active", new String("none"));
        pageContext.setAttribute("agnNavigationKey", new String("templateView"));
        pageContext.setAttribute("agnHighlightKey", new String("Attachments"));
        pageContext.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
        pageContext.setAttribute("agnTitleKey", new String("Template"));
        pageContext.setAttribute("agnSubtitleKey", new String("Template"));
        pageContext.setAttribute("agnSubtitleValue", tmpShortname);
    %>
</logic:equal>

<logic:equal name="mailingAttachmentsForm" property="isTemplate" value="false">
    <%
        // mailing navigation:
        pageContext.setAttribute("sidemenu_active", new String("Mailings"));
        pageContext.setAttribute("sidemenu_sub_active", new String("none"));
        pageContext.setAttribute("agnNavigationKey", new String("mailingView"));
        pageContext.setAttribute("agnHighlightKey", new String("Attachments"));
        pageContext.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
        pageContext.setAttribute("agnTitleKey", new String("Mailing"));
        pageContext.setAttribute("agnSubtitleKey", new String("Mailing"));
        pageContext.setAttribute("agnSubtitleValue", tmpShortname);
    %>
</logic:equal>

<%@include file="/header.jsp"%>

<html:errors/>
<html:form action="/mailingattachments" enctype="multipart/form-data">
    <html:hidden property="mailingID"/>
    <html:hidden property="action"/>
    <table border="0" cellspacing="0" cellpadding="0">
        <tr><td colspan="2"><b><bean:message key="New_Attachment"/>:<br><br></td></tr>
        <tr><td><bean:message key="Attachment"/>:&nbsp;</td><td><html:file property="newAttachment" styleId="newAttachment" onchange="getFilename()"/></td></tr>
        <tr><td><bean:message key="attachment.name"/>:&nbsp;</td><td><html:text property="newAttachmentName" styleId="newAttachmentName"/></td></tr>
        <tr><td>
              <bean:message key="Target"/>:&nbsp;</td><td>
                  <html:select property="attachmentTargetID" size="1">
                      <html:option value="0"><bean:message key="All_Subscribers"/></html:option>
                      <agn:ShowTable id="agntbl3" sqlStatement="<%= "select target_id, target_shortname from dyn_target_tbl where company_id="+AgnUtils.getCompanyID(request) %>" maxRows="500">
                           <html:option value="<%= (String)(pageContext.getAttribute("_agntbl3_target_id")) %>"><%= pageContext.getAttribute("_agntbl3_target_shortname") %></html:option>
                      </agn:ShowTable>
                  </html:select>
        </td></tr>
        <tr><td colspan="2">
            <br><html:image src="button?msg=Add" border="0" property="add" value="add"/></p>
        </td></tr>
        <% int i=1; boolean isFirst=true; %>
        <% if(isFirst) { isFirst=false; %>
        <tr><td colspan="2"><hr><span class="head3"><bean:message key="Attachments"/></span><br><br></td></tr>
        <% } %>
        <% MailingComponent comp=null; %>
        <agn:HibernateQuery id="attachment" query="<%= "from MailingComponent where companyID="+AgnUtils.getCompanyID(request)+" and mailingID="+tmpMailingID+" and comptype="+MailingComponent.TYPE_ATTACHMENT %>">
            <% comp=(MailingComponent)pageContext.getAttribute("attachment");
            ApplicationContext aContext=WebApplicationContextUtils.getWebApplicationContext(application);
        TargetDao dao = (TargetDao) aContext.getBean("TargetDao");
        Target aTarget= dao.getTarget(comp.getTargetID(), AgnUtils.getCompanyID(request));
        String targetShortname = null;
        if(aTarget != null) {
            targetShortname = aTarget.getTargetName();
   		 }
   		 %>        
            <tr>
                <td colspan="2"><b><bean:message key="Attachment"/>:&nbsp;<html:link page="<%= "/sc?compID=" + comp.getId() %>"><%= comp.getComponentName() %>&nbsp;&nbsp;<img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>download.gif" border="0" alt="<bean:message key="Download"/>"></html:link></b><br><br>
                <input type="hidden" name="compid<%= i++ %>" value="<%= pageContext.getAttribute("_agntbl1_component_id") %>">
                <%
                	if ( targetShortname != null ) {
                %><bean:message key="Target"/>:&nbsp;<%= targetShortname %>&nbsp;<br>
                <%
                }
                %>
                <bean:message key="Mime_Type"/>:&nbsp;<%= comp.getMimeType() %>&nbsp;<br>
                <bean:message key="Original_Size"/>:&nbsp;<%= comp.getBinaryBlock().length %>&nbsp;<bean:message key="KByte"/><br>
                <bean:message key="Size_Mail"/>:&nbsp;<%= comp.getEmmBlock().length() %>&nbsp;<bean:message key="KByte"/><br><br>
                <html:image src="button?msg=Save" border="0" property="save" value="save"/>&nbsp;&nbsp;<html:image src="button?msg=Delete" border="0" property="<%= "delete"+comp.getId() %>" value="delete"/></p></td>
            </tr>
            <tr><td><hr></td></tr>
        </agn:HibernateQuery>
    </table>
</html:form>
<script language="JavaScript">
    <!--
    function getFilename() {
    document.getElementById("newAttachmentName").value=document.getElementById("newAttachment").value.match(/[^\\\/]+$/);
    }
    //-->
</script>
<%@include file="/footer.jsp"%>
