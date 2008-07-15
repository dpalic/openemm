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
    pageContext.setAttribute("agnNavigationKey", new String("MailingWizard"));
    pageContext.setAttribute("agnHighlightKey", new String("MailingWizard"));
    pageContext.setAttribute("agnTitleKey", new String("Mailing"));
    pageContext.setAttribute("agnSubtitleKey", new String("Mailing"));
    pageContext.setAttribute("agnSubtitleValue", mailing.getShortname());
%>

<%@include file="/header.jsp"%>

<html:errors/>

<html:form styleId="mwiz" action="/mwName" focus="shortname">

    <html:hidden property="action"/>
    
    <b><font color=#73A2D0><bean:message key="MWizardStep_1_of_11"/></font></b>
    
    <br><br> 
    
    <b><bean:message key="MailingNameDescription"/>:</b><br><br>
    
    
    <table border="0" cellspacing="0" cellpadding="0">
        <tr> 
            <td><bean:message key="Name"/>:&nbsp;</td>
            <td> 
                <html:text property="mailing.shortname" maxlength="99" size="32"/>
            </td>
        </tr>

        <tr> 
            <td><bean:message key="Description"/>:&nbsp;</td>
            <td> 
                <html:textarea property="mailing.description" rows="3" cols="32"/>
            </td>
        </tr>
    </table>
    <BR>


    <% // wizard navigation: %>
    <br>
    <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td>&nbsp;</td>
            <td align="right">
                &nbsp;
                <html:image src="button?msg=Back"  border="0" onclick="document.mailingWizardForm.action.value='previous'"/>
                &nbsp;
                <html:image src="button?msg=Proceed"  border="0"  onclick="<%= "document.mailingWizardForm.action.value='" + MailingWizardAction.ACTION_NAME + "'" %>"/>
                &nbsp;
            </td>
        </tr>
    </table>         
</html:form>
<%@include file="/footer.jsp"%>
