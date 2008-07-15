<%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, java.util.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="targets.show"/>

<% int tmpTargetID=0;
   String tmpShortname=new String("");
   if(request.getAttribute("targetForm")!=null) {
      tmpTargetID=((TargetForm)request.getAttribute("targetForm")).getTargetID();
      tmpShortname=((TargetForm)request.getAttribute("targetForm")).getShortname();
   }
%>

<% pageContext.setAttribute("sidemenu_active", new String("Targets")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("none")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Target")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Target")); %>
<% pageContext.setAttribute("agnSubtitleValue", new String(tmpShortname)); %>
<% pageContext.setAttribute("agnNavigationKey", new String("targetView")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Target")); %>
<% pageContext.setAttribute("agnNavHrefAppend", new String("&targetID="+tmpTargetID)); %>
<%@include file="/header.jsp"%>

<html:errors/>

<html:form action="/target">
                <html:hidden property="targetID"/>
                <html:hidden property="action"/>
                <span class="head3"><bean:message key="target.delete.question"/></span>
                <br><br>
                <html:image src="button?msg=Delete" border="0" property="kill" value="kill"/>&nbsp;<html:link page="<%= new String("/target.do?action=" + TargetAction.ACTION_VIEW + "&targetID=" + tmpTargetID) %>"><html:img src="button?msg=Cancel" border="0"/></html:link>
              </html:form>

<%@include file="/footer.jsp"%>
