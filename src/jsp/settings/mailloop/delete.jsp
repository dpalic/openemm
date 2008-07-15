<%@ page language="java" import="org.agnitas.util.*, org.agnitas.beans.*, org.agnitas.web.*, java.util.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="mailing.show"/>

<% int tmpLoopID=0;
   String tmpShortname=new String("");
   MailloopForm aForm=null;
   if(request.getAttribute("mailloopForm")!=null) {
      aForm=(MailloopForm)request.getAttribute("mailloopForm");
      tmpLoopID=aForm.getMailloopID();
      tmpShortname=aForm.getShortname();
   }
%>

<% pageContext.setAttribute("sidemenu_active", new String("Settings")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("Mailloops"));  %>
<% pageContext.setAttribute("agnNavigationKey", new String("Mailloops")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("NewMailloop")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Mailloop")); %>    
<% pageContext.setAttribute("agnSubtitleKey", new String("Mailloop")); %>
<% pageContext.setAttribute("agnSubtitleValue", tmpShortname); %>
<% pageContext.setAttribute("agnNavHrefAppend", new String("&mailloopID="+tmpLoopID)); %>

<%@include file="/header.jsp"%>

<html:errors/>

<html:form action="/mailloop">
                <html:hidden property="mailloopID"/>
                <html:hidden property="action"/>
                <span class="head3"><bean:message key="mailloop.delete"/></span>
                <br><br><br>
                <html:image src="button?msg=Delete" border="0" property="inactive" value="inactive"/>&nbsp;
                <html:link page="<%= new String("/mailloop.do?action=" + MailloopAction.ACTION_VIEW + "&mailloopID="+tmpLoopID) %>"><html:img src="button?msg=Cancel" border="0"/></html:link>
              </html:form>

<%@include file="/footer.jsp"%>
