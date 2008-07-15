<%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*, org.agnitas.target.*, java.util.*" contentType="text/html; charset=utf-8" %>
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
        <tr><td colspan="2">
            <br><html:image src="button?msg=Add" border="0" property="add" value="add"/></p>
        </td></tr>
        <% int i=1; boolean isFirst=true; %>
        <% if(isFirst) { isFirst=false; %>
        <tr><td colspan="2"><hr><span class="head3"><bean:message key="Attachments"/></span><br><br></td></tr>
        <% } %>
        <% MailingComponent comp=null; %>
        <agn:HibernateQuery id="attachment" query="<%= "from MailingComponent where companyID="+AgnUtils.getCompanyID(request)+" and mailingID="+tmpMailingID+" and comptype="+MailingComponent.TYPE_ATTACHMENT %>">
            <% comp=(MailingComponent)pageContext.getAttribute("attachment"); %>            
            <tr>
                <td colspan="2"><b><bean:message key="Attachment"/>:&nbsp;<html:link page="<%= "/sc?compID=" + comp.getId() %>"><%= comp.getComponentName() %>&nbsp;&nbsp;<img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>download.gif" border="0" alt="<bean:message key="Download"/>"></html:link></b><br><br>
                <input type="hidden" name="compid<%= i++ %>" value="<%= pageContext.getAttribute("_agntbl1_component_id") %>">
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
