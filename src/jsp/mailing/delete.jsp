<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="mailing.delete"/>

<% int tmpMailingID=0;
   String tmpShortname=new String("");
   int isTemplate=0;
   MailingBaseForm aForm=null;
   if(session.getAttribute("mailingBaseForm")!=null) {
      aForm=(MailingBaseForm)session.getAttribute("mailingBaseForm");
      tmpMailingID=aForm.getMailingID();
      tmpShortname=aForm.getShortname();
      if(aForm.isIsTemplate()) {
         isTemplate=1;
      }
   }
%>

<% if(isTemplate==0) { %>
<agn:Permission token="mailing.delete"/>
<% } else { %>
<agn:Permission token="template.delete"/>
<% } %>

<logic:equal name="mailingBaseForm" property="isTemplate" value="true">
<% // template navigation:
  pageContext.setAttribute("sidemenu_active", new String("Templates")); 
  pageContext.setAttribute("sidemenu_sub_active", new String("none"));
  pageContext.setAttribute("agnNavigationKey", new String("templateView"));
  pageContext.setAttribute("agnHighlightKey", new String("Template"));
  pageContext.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
  pageContext.setAttribute("agnTitleKey", new String("Template")); 
  pageContext.setAttribute("agnSubtitleKey", new String("Template"));
  pageContext.setAttribute("agnSubtitleValue", tmpShortname);
%>
</logic:equal>

<logic:equal name="mailingBaseForm" property="isTemplate" value="false">
<%
// mailing navigation:
    pageContext.setAttribute("sidemenu_active", new String("Mailings")); 
    pageContext.setAttribute("sidemenu_sub_active", new String("none"));
    pageContext.setAttribute("agnNavigationKey", new String("mailingView"));
    pageContext.setAttribute("agnHighlightKey", new String("Mailing"));
    pageContext.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
    pageContext.setAttribute("agnSubtitleValue", tmpShortname);
    pageContext.setAttribute("agnTitleKey", new String("Mailing")); 
    pageContext.setAttribute("agnSubtitleKey", new String("Mailing")); 
%>
</logic:equal>

<%@include file="/header.jsp"%>

<html:errors/>
            <html:form action="/mailingbase">
            <html:hidden property="mailingID"/>
            <html:hidden property="action"/>
            <html:hidden property="isTemplate"/>
               <span class="head3">
               <% if(isTemplate==0) { %>
                <bean:message key="MailingDeleteQuestion"/>
                <% } else { %>
               <bean:message key="Delete_Template_Question"/>
               <% } %>
               </span><br>
			  <p>
	        <html:image src="button?msg=Delete" border="0" property="delete" value="delete"/>&nbsp;
                <html:link page="<%= new String("/mailingbase.do?action="+MailingBaseAction.ACTION_VIEW+"&mailingID=" + tmpMailingID) %>"><html:img src="button?msg=Cancel" border="0"/></html:link>
              </p>
              </html:form>
<%@include file="/footer.jsp"%>
