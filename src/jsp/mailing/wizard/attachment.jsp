<%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*, java.util.*, org.apache.struts.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<%	int tmpMailingID=0;
	MailingWizardForm aForm=null;
	// String permToken=null; wird nicht benutzt
	String tmpShortname=new String("");

	if((aForm=(MailingWizardForm)session.getAttribute("mailingWizardForm"))!=null) {
       tmpMailingID=aForm.getMailing().getId();
       tmpShortname=aForm.getMailing().getShortname();
	}
%>
<agn:Permission token="mailing.show"/>

<%
// mailing navigation:
pageContext.setAttribute("sidemenu_active", new String("Mailings"));
pageContext.setAttribute("sidemenu_sub_active", new String("New_Mailing"));
pageContext.setAttribute("agnNavigationKey", new String("MailingWizard"));
pageContext.setAttribute("agnHighlightKey", new String("MailingWizard"));
pageContext.setAttribute("agnTitleKey", new String("Mailing"));
pageContext.setAttribute("agnSubtitleKey", new String("Mailing"));
pageContext.setAttribute("agnSubtitleValue", tmpShortname);
%>

<%@include file="/header.jsp"%>
  
<html:errors/>

<html:form action="/mwAttachment" enctype="multipart/form-data">
	<html:hidden property="action"/>
    
    <b><font color=#73A2D0><bean:message key="MWizardStep_10_of_11"/></font></b>
    
<br><br>    
    <b><bean:message key="Attachments"/>:</b>
<br>
<br>


<agn:ShowByPermission token="mailing.attachments.show">

	<table border="0" cellspacing="0" cellpadding="0">
	<tr><td colspan="2"><b><bean:message key="New_Attachment"/>:<br><br></td></tr>
	<agn:ShowByPermission token="mailing.attachment.personalize">
		<tr>
			<td><bean:message key="attachment.type"/>:&nbsp;</td>
			<td>
				<html:select property="newAttachmentType" onchange="changeVisible()" styleId="newAttachmentType">
				<html:option value="0"><bean:message key="attachment.type.normal"/></html:option>
				<html:option value="1"><bean:message key="attachment.type.personalized"/></html:option>
				</html:select>
			</td>
                    </tr>
                </agn:ShowByPermission>
		<tr><td><bean:message key="Attachment"/>:&nbsp;</td><td><html:file property="newAttachment" styleId="newAttachment" onchange="getFilename()"/></td></tr>
                <tr><td><bean:message key="attachment.name"/>:&nbsp;</td><td><html:text property="newAttachmentName" styleId="newAttachmentName"/></td></tr>
                <agn:ShowByPermission token="mailing.attachment.personalize">
              		<tr><td><div id="attachmentBackground"><bean:message key="attachment.background"/>:&nbsp;</div></td><td><html:file property="newAttachmentBackground" styleId="newAttachmentBackground"/></td></tr>
		        </agn:ShowByPermission>
                <tr><td>
                <bean:message key="Target"/>:&nbsp;</td><td>
                    <html:select property="attachmentTargetID" size="1">
                        <html:option value="0"><bean:message key="All_Subscribers"/></html:option>
                        <agn:ShowTable id="agntbl3" sqlStatement="<%= new String("SELECT TARGET_ID, TARGET_SHORTNAME FROM dyn_target_tbl WHERE COMPANY_ID="+AgnUtils.getCompanyID(request)) %>" maxRows="500">
                            <html:option value="<%= (String)(pageContext.getAttribute("_agntbl3_target_id")) %>"><%= pageContext.getAttribute("_agntbl3_target_shortname") %></html:option>
                        </agn:ShowTable>
                    </html:select>
                </td></tr>
                <tr><td colspan="2">
                    <br><html:image src="button?msg=Add" border="0" onclick="document.mailingWizardForm.action.value='attachment'"/></p>
                </td></tr>
                <% int i=1; boolean isFirst=true; %>
                <% MailingComponent comp=null; %>
                <agn:HibernateQuery id="attachment" query="<%= "from MailingComponent where companyID="+AgnUtils.getCompanyID(request)+" and mailingID="+tmpMailingID+" and (comptype="+MailingComponent.TYPE_ATTACHMENT+" or comptype="+MailingComponent.TYPE_PERSONALIZED_ATTACHMENT+")" %>">
                <% comp=(MailingComponent)pageContext.getAttribute("attachment"); %>
                    <% if(isFirst) { isFirst=false; %>
                    <tr><td colspan="2"><hr><span class="head3"><bean:message key="Attachments"/></span><br><br></td></tr>
                    <% } %>
                    <tr>
                        <td colspan="2"><b><bean:message key="Attachment"/>:&nbsp;<html:link page="<%= new String("/sc?compID=" + pageContext.getAttribute("_agntbl1_component_id")) %>"><%= pageContext.getAttribute("_agntbl1_compname") %>&nbsp;&nbsp;<img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>download.gif" border="0" alt="<bean:message key="Download"/>"></html:link></b><br><br>
                        <input type="hidden" name="compid<%= i++ %>" value="<%= pageContext.getAttribute("_agntbl1_component_id") %>">
                        <% if(comp.getType() == 3) { %>
                        <bean:message key="Mime_Type"/>:&nbsp;<%= comp.getMimeType() %>&nbsp;<br>
                        <bean:message key="Original_Size"/>:&nbsp;<%= comp.getBinaryBlock().length %>&nbsp;<bean:message key="KByte"/><br>
		                <bean:message key="Size_Mail"/>:&nbsp;<%= comp.getEmmBlock().length() %>&nbsp;<bean:message key="KByte"/><br><br>
                        <% } else { %>
                        <bean:message key="attachment.type.personalized"/><br><br>
                        <% } %>
                        <bean:message key="Target"/>:&nbsp;<html:select property="<%= new String("targetID"+pageContext.getAttribute("_agntbl1_component_id")) %>" size="1" value="<%= (String)(pageContext.getAttribute("_agntbl1_target_id")) %>">
                            <html:option value="0"><bean:message key="All_Subscribers"/></html:option>
                            <agn:ShowTable id="agntbl3" sqlStatement="<%= new String("SELECT TARGET_ID, TARGET_SHORTNAME FROM DYN_TARGET_TBL WHERE COMPANY_ID="+AgnUtils.getCompanyID(request)) %>" maxRows="500">
                                <html:option value="<%= (String)(pageContext.getAttribute("_agntbl3_target_id")) %>"><%= pageContext.getAttribute("_agntbl3_target_shortname") %></html:option>
                            </agn:ShowTable>
                        </html:select><p>
                        <html:image src="button?msg=Save" border="0" onclick="document.mailingWizardForm.action.value='save'"/>&nbsp;&nbsp;<html:image src="button?msg=Delete" border="0" property="<%= new String("delete"+pageContext.getAttribute("_agntbl1_component_id")) %>" value="delete"/></p></td>
                    </tr>
                    <tr><td><hr></td></tr>
                </agn:HibernateQuery>
            </table>
</agn:ShowByPermission>

<br>
    <% // wizard navigation: %>
    <br>
    <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td>&nbsp;</td>
            <td align="right">
                &nbsp;
                <html:image src="button?msg=Back"  border="0" onclick="document.mailingWizardForm.action.value='previous'"/>
                &nbsp;
                <html:image src="button?msg=Proceed"  border="0" onclick="<%= "document.mailingWizardForm.action.value='finish'" %>"/>
                &nbsp;
                <html:image src="button?msg=Finish"  border="0" onclick="<%= "document.mailingWizardForm.action.value='finish'" %>"/>
                &nbsp;
            </td>
        </tr>
    </table>       

</html:form><script language="JavaScript">
    <!--
    function getFilename() {
    document.getElementById("newAttachmentName").value=document.getElementById("newAttachment").value.match(/[^\\\/]+$/);
    }
    <agn:ShowByPermission token="mailing.attachment.personalize">
    function changeVisible()
    {
    if(document.getElementById("newAttachmentType").value=="0") {
    document.getElementById("newAttachmentBackground").style.visibility = "hidden";
    document.getElementById("attachmentBackground").style.visibility = "hidden";
    } else {
    document.getElementById("newAttachmentBackground").style.visibility = "visible";
    document.getElementById("attachmentBackground").style.visibility = "visible";
    }
    }
    changeVisible();
    </agn:ShowByPermission>
    //-->
</script>
<%@include file="/footer.jsp"%>
