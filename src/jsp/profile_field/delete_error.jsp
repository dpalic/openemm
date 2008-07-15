<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<agn:CheckLogon/>

<agn:Permission token="profileField.show"/>

<% pageContext.setAttribute("sidemenu_active", new String("Settings")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("Profile_DB")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Profile_Database")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Profile_Database")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("profiledb")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Profile_DB")); %>

<%@include file="/header.jsp"%>

<html:errors/>

     

     <html:form action="/profiledb">
         <br>   
         <span class="head1"><%= ((ProfileFieldForm)request.getAttribute("profileFieldForm")).getFieldname() %></span><br>
         <br>
         <b><bean:message key="ProfileFieldErrorMsg"/><br><%=((ProfileFieldForm)request.getAttribute("profileFieldForm")).getTargetsDependent()%></b><br>
         <html:hidden property="companyID"/>
         <html:hidden property="action"/>
         <html:hidden property="fieldname"/>
         <html:hidden property="targetsDependent"/>
         <br>
         <html:link page="<%= new String("/profiledb.do?action=" + ProfileFieldAction.ACTION_LIST) %>"><html:img src="button?msg=Back" border="0"/></html:link>

     </html:form>  

<%@include file="/footer.jsp"%>
