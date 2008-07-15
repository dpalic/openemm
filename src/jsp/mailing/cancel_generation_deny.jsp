<%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="mailing.send.show"/>

<% int tmpMailingID=0;
   String tmpShortname=new String("");
   MailingSendForm aForm=(MailingSendForm) request.getAttribute("mailingSendForm");

   if(aForm!=null) {
      tmpMailingID=aForm.getMailingID();
      tmpShortname=aForm.getShortname();
   }
%>

<logic:equal name="mailingSendForm" property="isTemplate" value="true">
<% // template navigation:
  pageContext.setAttribute("sidemenu_active", new String("Templates")); 
  pageContext.setAttribute("sidemenu_sub_active", new String("none"));
  pageContext.setAttribute("agnNavigationKey", new String("templateView"));
  pageContext.setAttribute("agnHighlightKey", new String("Send_Mailing"));
  pageContext.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
  pageContext.setAttribute("agnTitleKey", new String("Template")); 
  pageContext.setAttribute("agnSubtitleKey", new String("Template"));
  pageContext.setAttribute("agnSubtitleValue", tmpShortname);
%>
</logic:equal>

<logic:equal name="mailingSendForm" property="isTemplate" value="false">
<%
// mailing navigation:
    pageContext.setAttribute("sidemenu_active", new String("Mailings")); 
    pageContext.setAttribute("sidemenu_sub_active", new String("none"));
    pageContext.setAttribute("agnNavigationKey", new String("mailingView"));
    pageContext.setAttribute("agnHighlightKey", new String("Send_Mailing"));
    pageContext.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
    pageContext.setAttribute("agnTitleKey", new String("Mailing")); 
    pageContext.setAttribute("agnSubtitleKey", new String("Mailing"));
    pageContext.setAttribute("agnSubtitleValue", tmpShortname); 
%>
</logic:equal>

<%@include file="/header.jsp"%>

<html:errors/>

    <html:form action="/mailingsend">
        <html:hidden property="mailingID"/>
        <html:hidden property="action"/>
        
        <br>
        <b><bean:message key="mailing.generation.cancel.deny"/></b>
        <br>

        <p>
            <html:link page="<%= new String("/mailingsend.do?action=" + MailingSendAction.ACTION_VIEW_SEND + "&mailingID=" + tmpMailingID) %>"><html:img src="button?msg=Back" border="0"/></html:link>
        </p>

    </html:form>
    
<%@include file="/footer.jsp"%>
