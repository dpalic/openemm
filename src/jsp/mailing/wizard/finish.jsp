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

<html:errors/>

<html:form action="/mailingwizard" focus="shortname" enctype="application/x-www-form-urlencoded">

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

    <b><font color=#73A2D0><agn:GetLocalMsg key="MWizardStep_11_of_11"/></font></b>

    <br>
    <table border="0" cellspacing="0" cellpadding="0">
    
    <br>
    <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="400" height="10" border="0">
    <br>
<span class="head3"><agn:GetLocalMsg key="MailingWizardReadyMsg"/>!</span><BR><BR><b><agn:GetLocalMsg key="TestAdminDeliveryMsg"/>:</b>
    
<br>
                          <br><br><li><html:link page="<%= new String("/mailing.do?action=" + MailingAction.ACTION_PREVIEW_SELECT + "&mailingID=" + tmpMailingID) %>"><b>
                              <agn:GetLocalMsg key="Preview"/>
                          </b></html:link><br>
                          <agn:ShowByPermission token="mailing.send.admin">
                              <br><li><html:link page="<%= new String("/mailing.do?action=18&mailingID=" + tmpMailingID) %>"><b>
                                  <agn:GetLocalMsg key="MailingTestAdmin"/>
                              </b></html:link><br>
                          </agn:ShowByPermission>
                          <agn:ShowByPermission token="mailing.send.test">
                              <li><html:link page="<%= new String("/mailing.do?action=19&mailingID=" + tmpMailingID) %>"><b>
                                  <agn:GetLocalMsg key="MailingTestDistrib"/>
                              </b></html:link>
                          </agn:ShowByPermission>
<br><br>
<b><agn:GetLocalMsg key="ClickFinishMsg"/>.</b>
<br>

    <% // wizard navigation: %>
    <br>
    <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td>&nbsp;</td>
            <td align="right">
                &nbsp;
                <html:image src="button?msg=Back"  border="0" property="back" value="back"/>
                &nbsp;           
                <html:link page="<%=new String("/mailingbase.do?action=" + MailingBaseAction.ACTION_VIEW) + "&mailingID=" + tmpMailingID%>"><html:img src="button?msg=Finish" border="0"/></html:link>
                &nbsp;
            </td>
        </tr>
    </table>             

</html:form>
<%@include file="/footer.jsp"%>
