<%@ page language="java" contentType="text/html; charset=utf-8" import="com.agnitas.util.*, java.util.*, com.agnitas.struts.*" errorPage="error.jsp" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>
<agn:Permission token="stats.rdir"/>

<% int tmpMailingID=0;
   //int tmpTargetID=0;
   //int tmpUniqueClicks=0;
   String tmpShortname=new String("");
   EmmLayout aLayout=(EmmLayout)session.getAttribute("emm.layout");
   MailingStatForm aForm=null;
   if(session.getAttribute("mailingStatForm")!=null) {
      aForm=(MailingStatForm)session.getAttribute("mailingStatForm");
      tmpMailingID=aForm.getMailingID();
      //tmpTargetID=aForm.getTempTargetID();
      tmpShortname=aForm.getMailingShortname();
   }
%>

<% pageContext.setAttribute("sidemenu_active", new String("Mailings")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("none")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Mailing")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Mailing")); %>
<% pageContext.setAttribute("agnSubtitleValue", tmpShortname); %>
<% pageContext.setAttribute("agnNavigationKey", new String("mailingView")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Statistics")); %>
<% pageContext.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID)); %>
<%@include file="/header.jsp"%>

<html:errors/>

    <table border="0" cellspacing="0" cellpadding="0">
        <html:form action="mailing_stat">
            
             <html:hidden property="mailingID"/>
             <html:hidden property="action"/>
             <html:hidden property="targetID"/>
             <html:hidden property="netto"/>

            <tr>       
                <td><span class="head3"><agn:GetLocalMsg key="DeleteAdminClicks"/></span></td>
            </tr> 

            <tr>       
                <td>&nbsp;&nbsp;<td>
            </tr> 

            <tr>       
                <td>&nbsp;&nbsp;<td>
            </tr> 

            <tr>       
                <td><b><agn:GetLocalMsg key="AreYouSure"/></b><td>
            </tr> 

            <tr>       
                <td>&nbsp;&nbsp;<td>
            </tr> 

            <tr>    
                <td><html:link page="<%= new String( "/mailing_stat.do?action=" + MailingStatAction.ACTION_CLEAN + "&mailingID="+tmpMailingID) %>"><html:img src="button?msg=OK" border="0"/></html:link>&nbsp;&nbsp;<html:link href="<%= new String( "mailing_stat.do?action=" + MailingStatAction.ACTION_MAILINGSTAT + "&mailingID=" + tmpMailingID ) %>"><html:img src="button?msg=Cancel" border="0"/></html:link></td>
            </tr>

        </html:form>
    </table>
<%@include file="/footer.jsp"%>
