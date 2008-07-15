<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*, java.util.*, org.springframework.context.*, org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>
<agn:Permission token="recipient.delete"/>

<% pageContext.setAttribute("sidemenu_active", new String("Recipient")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("Overview")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Recipient")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Recipient")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("subscriber_editor")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Overview")); %>

<%@include file="/header.jsp"%>

<%
RecipientForm recipient=(RecipientForm)session.getAttribute("recipientForm");
recipient.setAction(RecipientAction.ACTION_DELETE);
%>
<html:errors/>
    <html:form action="/recipient">
        <html:hidden property="recipientID"/>
        <html:hidden property="action"/>
        <span class="head1"><%= recipient.getFirstname()+" "+recipient.getLastname() %></span><br>
        <br>
        <b><bean:message key="recipient.confirm_delete"/></b><br>
          <p>

                <html:image src="button?msg=Delete" property="kill" value="kill"/>
                <html:link page="<%= new String("/recipient.do?action=" + RecipientAction.ACTION_VIEW + "&recipientID=" + recipient.getRecipientID()) %>"><html:img src="button?msg=Cancel" border="0"/></html:link>
          </p>

    </html:form>

<%@include file="/footer.jsp"%>
