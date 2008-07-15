<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<agn:CheckLogon/>

<agn:Permission token="actions.delete"/>

<% 
int tmpActionID=0;
String tmpShortname=new String("");

if(session.getAttribute("emmActionForm")!=null) {
    tmpActionID=((EmmActionForm)session.getAttribute("emmActionForm")).getActionID();
    tmpShortname=((EmmActionForm)session.getAttribute("emmActionForm")).getShortname();
}
%>
<% pageContext.setAttribute("sidemenu_active", new String("Actions")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("none")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Action")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Action")); %>
<% pageContext.setAttribute("agnSubtitleValue", SafeString.getHTMLSafeString(tmpShortname)); %>
<% pageContext.setAttribute("agnNavigationKey", new String("Action")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Action")); %>
<% pageContext.setAttribute("agnNavHrefAppend", new String("?actionID="+tmpActionID)); %>
<%@include file="/header.jsp"%>

               <span class="head3"><bean:message key="action.deleteQuestion"/></span><br>
			  <p>
	        <html:link page="<%= new String("/action.do?actionID=" + tmpActionID + "&action=" + EmmActionAction.ACTION_DELETE) %>"><html:img src="button?msg=Delete" border="0"/></html:link>
                <html:link page="<%= new String("/action.do?actionID=" + tmpActionID + "&action=" + EmmActionAction.ACTION_VIEW) %>"><html:img src="button?msg=Cancel" border="0"/></html:link>
              </p>

<%@include file="/footer.jsp"%>
