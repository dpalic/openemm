<%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, java.util.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<% int tmpMailinglistID=0;
   String tmpShortname=new String("");
   if(request.getAttribute("mailinglistForm")!=null) {
      tmpMailinglistID=((MailinglistForm)request.getAttribute("mailinglistForm")).getMailinglistID();
      tmpShortname=((MailinglistForm)request.getAttribute("mailinglistForm")).getShortname();
   }
%>

<agn:Permission token="mailinglist.delete"/>

<% pageContext.setAttribute("sidemenu_active", new String("Mailinglists")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("Overview")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Mailinglist")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Mailinglist")); %>
<% pageContext.setAttribute("agnSubtitleValue", tmpShortname); %>
<% pageContext.setAttribute("agnNavigationKey", new String("show_mailinglist")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Mailinglist")); %>
<% pageContext.setAttribute("agnNavHrefAppend", new String("&mailinglistID="+tmpMailinglistID)); %>
<%@include file="/header.jsp"%>

<html:errors/>

<html:form action="/mailinglist">
                <html:hidden property="mailinglistID"/>
                <html:hidden property="action"/>
                <html:hidden property="shortname"/>
                <html:hidden property="description"/>
                <span class="head3"><bean:message key="mailinglist.delete.question"/></span>
                <br><br><br>
                <html:image src="button?msg=Delete" border="0" property="kill" value="kill"/>
                <html:link page="<%= new String("/mailinglist.do?action=" + Integer.toString(MailinglistAction.ACTION_VIEW) + "&mailinglistID=" + tmpMailinglistID) %>"><html:img src="button?msg=Cancel" border="0"/></html:link>
              </html:form>

<%@include file="/footer.jsp"%>
