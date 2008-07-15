<%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, java.util.*" contentType="text/html; charset=utf-8" buffer="32kb" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<% int tmpAdminID=0;
   String tmpUsername=new String("");
   if(request.getAttribute("adminForm")!=null) {
      tmpAdminID=((AdminForm)request.getAttribute("adminForm")).getAdminID();
      tmpUsername=((AdminForm)request.getAttribute("adminForm")).getUsername();
   }
%>


<% pageContext.setAttribute("agnSubtitleKey", new String("Admins")); %>             <!-- ueber rechte Seite -->
<% pageContext.setAttribute("sidemenu_active", new String("Settings")); %>          <!-- links Button -->
<% pageContext.setAttribute("sidemenu_sub_active", new String("Admins")); %>        <!-- links unter Button -->
<% pageContext.setAttribute("agnTitleKey", new String("Admins")); %>                <!-- Titelleiste -->


<% if(tmpAdminID!=0) {
     pageContext.setAttribute("agnNavigationKey", new String("admins"));
     pageContext.setAttribute("agnHighlightKey", new String("Overview"));
   } else {
     pageContext.setAttribute("agnNavigationKey", new String("Admins"));
     pageContext.setAttribute("agnHighlightKey", new String("New_Admin"));
   } 
%>


<% pageContext.setAttribute("agnSubtitleValue", tmpUsername); %>
<% pageContext.setAttribute("agnNavHrefAppend", new String("&adminID="+tmpAdminID)); %>

<%@include file="/header.jsp"%>

<html:errors/>

<html:form action="admin">
    <html:hidden property="adminID"/>
    <html:hidden property="action"/>
    <span class="head3"><bean:message key="AdminDeleteQuestion"/></span>
    <br><br><br>
    <html:image src="button?msg=Delete" border="0" property="kill" value="kill"/>
    <html:link page="<%= new String("/admin.do?action=" + AdminAction.ACTION_VIEW + "&adminID=" + tmpAdminID) %>"><html:img src="button?msg=Cancel" border="0"/></html:link>
</html:form>

<%@include file="/footer.jsp"%>
