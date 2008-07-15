<%@ page language="java" import="com.agnitas.util.*, com.agnitas.struts.*" contentType="text/html; charset=utf-8" errorPage="error.jsp" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<agn:CheckLogon/>

<agn:Permission token="stats.rdir"/>

<% 
   int tmpMailingID=0;
   int tmpTargetID=0;
   String tmpShortname=new String("");
   EmmLayout aLayout=(EmmLayout)session.getAttribute("emm.layout");
   MailingStatForm aForm=null;
   int maxblue = 0;
   int maxSubscribers = 0;

   if(session.getAttribute("mailingStatForm")!=null) {
      aForm=(MailingStatForm)session.getAttribute("mailingStatForm");
      tmpMailingID=aForm.getMailingID();
      tmpTargetID=aForm.getTargetID();
      tmpShortname=aForm.getMailingShortname();
      maxblue=aForm.getMaxblue();
      maxSubscribers=aForm.getMaxSubscribers();
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

<% pageContext.setAttribute("agnRefresh", new String("2")); %>

<%@include file="/header.jsp"%>

<html:errors/>

<html:form action="/mailing_stat">
    <html:hidden property="mailingID"/>
    <html:hidden property="action"/>

<table border="0" cellspacing="0" cellpadding="0" width="400">
    <tr>
        <td>
            <b>&nbsp;<b>
        </td>
    </tr>
    <tr>
        <td>
            <img border="0" width="44" height="48" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>wait.gif"/>
        </td>
    </tr>
    <tr>
        <td>
            <b>&nbsp;<b>
        </td>
    </tr>
    <tr>
        <td>
            <b><agn:GetLocalMsg key="StatSplashMessage"/><b>
        </td>
    </tr>
    <tr>
        <td>
            <b>&nbsp;<b>
        </td>
    </tr>

</table>

</html:form>

<%@include file="/footer.jsp"%> 
