<%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, java.util.*, org.agnitas.beans.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>
<% MailingWizardForm aForm=null;
   aForm=(MailingWizardForm)session.getAttribute("mailingWizardForm");
   Mailing mailing=aForm.getMailing();
%>

<agn:Permission token="mailing.show"/>

<%
// mailing navigation:
   pageContext.setAttribute("sidemenu_active", new String("Mailings")); 
    pageContext.setAttribute("sidemenu_sub_active", new String("New_Mailing"));
    pageContext.setAttribute("agnNavigationKey", new String("MailingNew"));
    pageContext.setAttribute("agnHighlightKey", new String("New_Mailing"));
    pageContext.setAttribute("agnTitleKey", new String("Mailing")); 
    pageContext.setAttribute("agnSubtitleKey", new String("Mailing")); 
%>

<%@include file="/header.jsp"%>

<html:errors/>

<html:form action="/mwStart">

    <html:hidden property="action"/>
    
    <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="400" height="10" border="0">
    <br>
    <b><bean:message key="NewMailingMethod"/>:</b>

    <BR>
    <BR>
    <BR>
    <html:link page="<%=new String("/mailingbase.do?action=" + MailingBaseAction.ACTION_NEW) + "&mailingID=0&isTemplate=false"%>" style="color: #73A2D0;"><b><bean:message key="Normal"/>:</b> <bean:message key="NoWizard"/>.</html:link>
    <BR>
    <BR>
    <html:link page="<%=new String("/mwStart.do?action=" + MailingWizardAction.ACTION_START)%>" style="color: #73A2D0;"><b><bean:message key="Wizard"/>:</b> <bean:message key="WizardDescription"/>.</html:link>
    <BR>
    <BR>

<% // wizard navigation: %>
    <br>
    <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td>&nbsp;</td>
            <td align="right">
                &nbsp;
                <html:link page="<%=new String("/mailingbase.do?action=" + MailingBaseAction.ACTION_NEW) + "&mailingID=0&isTemplate=false"%>"><html:img src="button?msg=Normal" border="0"/></html:link>
                <html:image property="action_forward" value="start" src="button?msg=Wizard" border="0"/>
                &nbsp;
            </td>
        </tr>
    </table>         
</html:form>
<%@include file="/footer.jsp"%>
