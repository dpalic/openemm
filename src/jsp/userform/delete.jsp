<%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, java.util.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<% int tmpFormID=0;
   String tmpFormName=new String("");
   if(request.getAttribute("userFormEditForm")!=null) {
      tmpFormID=((UserFormEditForm)request.getAttribute("userFormEditForm")).getFormID();
      tmpFormName=((UserFormEditForm)request.getAttribute("userFormEditForm")).getFormName();
   }
%>

<agn:Permission token="forms.delete"/>

<% pageContext.setAttribute("sidemenu_active", new String("Forms")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("Overview")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Form")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Form")); %>
<% pageContext.setAttribute("agnSubtitleValue", tmpFormName); %>
<% pageContext.setAttribute("agnNavigationKey", new String("formView")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Form")); %>
<%@include file="/header.jsp"%>

<html:errors/>

<html:form action="/userform">
                <html:hidden property="formID"/>
                <html:hidden property="action"/>
                <span class="head3"><bean:message key="form.delete.question"/></span>
                <br><br><br>
                <html:image src="button?msg=Delete" border="0" property="delete" value="delete"/>
                <html:link page="<%= new String("/userform.do?action=" + UserFormEditAction.ACTION_VIEW + "&formID=" + tmpFormID) %>"><html:img src="button?msg=Cancel" border="0"/></html:link>
              </html:form>

<%@include file="/footer.jsp"%>