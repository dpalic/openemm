<%@ page language="java" import="com.agnitas.util.*, com.agnitas.struts.*, java.util.*, org.apache.struts.*" contentType="text/html; charset=utf-8" errorPage="error.jsp" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<% int tmpMailingID=0;
int tmpReferrerAction=0;
MailingWizardForm aForm=null;
int defaultMediaType=((Integer)(session.getAttribute("agnitas.defaultMediaType"))).intValue();
int numOfMediaTypes=((Integer)(session.getAttribute("agnitas.numOfMediaTypes"))).intValue();
String permToken=null;

String tmpShortname=new String("");
if((aForm=(MailingWizardForm)session.getAttribute("mailingWizardForm"))!=null) {
    tmpMailingID=((MailingWizardForm)session.getAttribute("mailingWizardForm")).getMailingID();
    tmpShortname=((MailingWizardForm)session.getAttribute("mailingWizardForm")).getShortname();
    tmpReferrerAction=((MailingWizardForm)session.getAttribute("mailingWizardForm")).getReferrerAction();
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
  <%  String sqlStatement="SELECT COMPONENT_ID, COMPNAME, MTYPE, TARGET_ID, to_char(trunc((DBMS_LOB.GETLENGTH(BINARY)/1024.0),2)) AS LENGTH, to_char(trunc((DBMS_LOB.GETLENGTH (EMMBLOCK)/1024.0), 2)) AS CHAR_LENGTH, COMPTYPE FROM COMPONENT_TBL WHERE (COMPTYPE=3 OR COMPTYPE=4) AND MAILING_ID=" + 
          aForm.getMailingID() +
          " AND COMPANY_ID=" + session.getAttribute("companyID") + " ORDER BY COMPONENT_ID";
   %>
<html:errors/>

<html:form action="/mailingwizard" enctype="multipart/form-data">
    <html:hidden property="mailingID"/>
    <html:hidden property="action"/>
    <html:hidden property="aktContentID"/>
    <html:hidden property="aktTracklinkID"/>    
    <html:hidden property="archived"/>
    <html:hidden property="campaignID"/>
    <html:hidden property="copyFlag"/>
    <html:hidden property="description"/>
    <html:hidden property="directTarget"/>
    <html:hidden property="emailFormat"/>
    <html:hidden property="emailFrom"/>
    <html:hidden property="emailSubject"/>
    <html:hidden property="htmlTemplate"/>
    <html:hidden property="isTemplate"/>
    <html:hidden property="mailingID"/>
    <html:hidden property="mailinglistID"/>
    <html:hidden property="mailingType"/>
    <html:hidden property="oldMailingID"/>
    <html:hidden property="replyFullname"/>
    <html:hidden property="senderFullname"/>
    <html:hidden property="senderEmail"/>
    <html:hidden property="showMtypeOptions"/>
    <html:hidden property="shortname"/>
    <html:hidden property="templateID"/>
    <html:hidden property="templSel"/>
    <html:hidden property="textTemplate"/>
    <html:hidden property="useMediaType[0]"/>
    <html:hidden property="useMediaType[1]"/>
    <html:hidden property="useMediaType[2]"/>
    <html:hidden property="useMediaType[3]"/>
    <html:hidden property="useMediaType[4]"/>

    <b><font color=#73A2D0><agn:GetLocalMsg key="MWizardStep_10_of_11"/></font></b>
    
<br><br>    
    <b><agn:GetLocalMsg key="Attachments"/>:</b>
<br>
<br>


<agn:ShowByPermission token="mailing.attachments.show">

            <table border="0" cellspacing="0" cellpadding="0">
                <tr><td colspan="2"><b><agn:GetLocalMsg key="New_Attachment"/>:<br><br></td></tr>
                <agn:ShowByPermission token="mailing.attachment.personalize">
                    <tr>
                        <td><agn:GetLocalMsg key="attachment.type"/>:&nbsp;</td>
                        <td>
                            <html:select property="newAttachmentType" onchange="changeVisible()" styleId="newAttachmentType">
                                <html:option value="0"><agn:GetLocalMsg key="attachment.type.normal"/></html:option>
                                <html:option value="1"><agn:GetLocalMsg key="attachment.type.personalized"/></html:option>
                            </html:select>
                        </td>
                    </tr>
                </agn:ShowByPermission>
                <tr><td><agn:GetLocalMsg key="Attachment"/>:&nbsp;</td><td><html:file property="newAttachment" styleId="newAttachment" onchange="getFilename()"/></td></tr>
                <tr><td><agn:GetLocalMsg key="attachment.name"/>:&nbsp;</td><td><html:text property="newAttachmentName" styleId="newAttachmentName"/></td></tr>
                <agn:ShowByPermission token="mailing.attachment.personalize">
                    <tr><td><div id="attachmentBackground"><agn:GetLocalMsg key="attachment.background"/>:&nbsp;</div></td><td><html:file property="newAttachmentBackground" styleId="newAttachmentBackground"/></td></tr>
                </agn:ShowByPermission>
                <tr><td>
                <agn:GetLocalMsg key="Target"/>:&nbsp;</td><td>
                    <html:select property="attachmentTargetID" size="1">
                        <html:option value="0"><agn:GetLocalMsg key="All_Subscribers"/></html:option>
                        <agn:ShowTable id="agntbl3" sqlStatement="<%= new String("SELECT TARGET_ID, TARGET_SHORTNAME FROM DYN_TARGET_TBL WHERE COMPANY_ID="+session.getAttribute("companyID")) %>" maxRows="500">
                            <html:option value="<%= (String)(pageContext.getAttribute("_agntbl3_target_id")) %>"><%= pageContext.getAttribute("_agntbl3_target_shortname") %></html:option>
                        </agn:ShowTable>
                    </html:select>
                </td></tr>
                <tr><td colspan="2">
                    <br><html:image src="button?msg=Add" border="0" property="att_add" value="att_add"/></p>
                </td></tr>
                <% int i=1; boolean isFirst=true; %>
                <agn:ShowTable id="agntbl1" sqlStatement="<%= sqlStatement %>" maxRows="100">
                    <% if(isFirst) { isFirst=false; %>
                    <tr><td colspan="2"><hr><span class="head3"><agn:GetLocalMsg key="Attachments"/></span><br><br></td></tr>
                    <% } %>
                    <tr>
                        <td colspan="2"><b><agn:GetLocalMsg key="Attachment"/>:&nbsp;<html:link page="<%= new String("/sc?compID=" + pageContext.getAttribute("_agntbl1_component_id")) %>"><%= pageContext.getAttribute("_agntbl1_compname") %>&nbsp;&nbsp;<img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>download.gif" border="0" alt="<agn:GetLocalMsg key="Download"/>"></html:link></b><br><br>
                        <input type="hidden" name="compid<%= i++ %>" value="<%= pageContext.getAttribute("_agntbl1_component_id") %>">
                        <% if(pageContext.getAttribute("_agntbl1_comptype").equals("3")) { %>
                        <agn:GetLocalMsg key="Mime_Type"/>:&nbsp;<%= pageContext.getAttribute("_agntbl1_mtype") %>&nbsp;<br>
                        <agn:GetLocalMsg key="Original_Size"/>:&nbsp;<%= pageContext.getAttribute("_agntbl1_length") %>&nbsp;<agn:GetLocalMsg key="KByte"/><br>
                        <agn:GetLocalMsg key="Size_Mail"/>:&nbsp;<%= pageContext.getAttribute("_agntbl1_char_length") %>&nbsp;<agn:GetLocalMsg key="KByte"/><br><br>
                        <% } else { %>
                        <agn:GetLocalMsg key="attachment.type.personalized"/><br><br>
                        <% } %>
                        <agn:GetLocalMsg key="Target"/>:&nbsp;<html:select property="<%= new String("targetID"+pageContext.getAttribute("_agntbl1_component_id")) %>" size="1" value="<%= (String)(pageContext.getAttribute("_agntbl1_target_id")) %>">
                            <html:option value="0"><agn:GetLocalMsg key="All_Subscribers"/></html:option>
                            <agn:ShowTable id="agntbl3" sqlStatement="<%= new String("SELECT TARGET_ID, TARGET_SHORTNAME FROM DYN_TARGET_TBL WHERE COMPANY_ID="+session.getAttribute("companyID")) %>" maxRows="500">
                                <html:option value="<%= (String)(pageContext.getAttribute("_agntbl3_target_id")) %>"><%= pageContext.getAttribute("_agntbl3_target_shortname") %></html:option>
                            </agn:ShowTable>
                        </html:select><p>
                        <html:image src="button?msg=Save" border="0" property="att_save" value="att_save"/>&nbsp;&nbsp;<html:image src="button?msg=Delete" border="0" property="<%= new String("delete"+pageContext.getAttribute("_agntbl1_component_id")) %>" value="delete"/></p></td>
                    </tr>
                    <tr><td><hr></td></tr>
                </agn:ShowTable>
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
                <html:image src="button?msg=Back"  border="0" property="att_back" value="att_back"/>
                &nbsp;
                <html:image src="button?msg=Proceed"  border="0" property="att_proceed" value="att_proceed"/>
                &nbsp;
                <html:link page="<%=new String("/mailingwizard.do?action=" + MailingWizardAction.ACTION_SEND_ADMINTEST)%>"><html:img src="button?msg=Finish" border="0"/></html:link>
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
<jsp:include page="<%= ((EmmLayout)session.getAttribute("emm.layout")).getFooterUrl() %>" flush="true"/>
