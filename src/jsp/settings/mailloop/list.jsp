<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="mailing.show"/>

<% pageContext.setAttribute("sidemenu_active", new String("Settings")); %>


<% pageContext.setAttribute("sidemenu_sub_active", new String("Mailloops"));  %>

<% pageContext.setAttribute("agnNavigationKey", new String("Mailloops")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Overview")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Mailloops")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Mailloops")); %>

<%@include file="/header.jsp"%>
<html:errors/>

      <table border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td><span class="head3"><bean:message key="Mailloop"/>&nbsp;&nbsp;</span></td>
            <td><span class="head3"><bean:message key="Description"/>&nbsp;&nbsp;</span></td>

            <td><span class="head3"><bean:message key="mailloop.forward_adr"/>&nbsp;</span></td>
        </tr>
        <tr><td colspan="4"><hr></td></tr>
        
        <logic:iterate id="loop" name="mailloopForm" property="mailloops" type="org.agnitas.beans.Mailloop">
            <tr>
                <td><html:link page="<%= new String("/mailloop.do?action=" + MailloopAction.ACTION_VIEW + "&mailloopID=" + ((Mailloop)pageContext.getAttribute("loop")).getId()) %>"><b><bean:write name="loop" property="shortname"/></b></html:link>&nbsp;&nbsp;</td>
                <td><html:link page="<%= new String("/mailloop.do?action=" + MailloopAction.ACTION_VIEW + "&mailloopID=" + ((Mailloop)pageContext.getAttribute("loop")).getId()) %>"><bean:write name="loop" property="description"/></html:link>&nbsp;&nbsp;</td>
                <td><html:link page="<%= new String("/mailloop.do?action=" + MailloopAction.ACTION_VIEW + "&mailloopID=" + ((Mailloop)pageContext.getAttribute("loop")).getId()) %>">
                ext_<%= ((Mailloop)pageContext.getAttribute("loop")).getId() %>@<%= AgnUtils.getCompany(request).getMailloopDomain() %>
                </html:link>&nbsp;&nbsp;</td>
                <td>
                        <html:link page="<%= new String("/mailloop.do?action=" + MailloopAction.ACTION_CONFIRM_DELETE + "&mailloopID=" + ((Mailloop)pageContext.getAttribute("loop")).getId()) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>delete.gif" alt="<bean:message key="Delete"/>" border="0"></html:link>
                        <html:link page="<%= new String("/mailloop.do?action=" + MailloopAction.ACTION_VIEW + "&mailloopID=" + ((Mailloop)pageContext.getAttribute("loop")).getId()) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>bearbeiten.gif" alt="<bean:message key="Edit"/>" border="0"></html:link>
                </td>
            </tr>
        </logic:iterate>
        
      </table>

<%@include file="/footer.jsp"%>
