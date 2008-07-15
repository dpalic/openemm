<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, java.net.*, org.agnitas.beans.*" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<agn:CheckLogon/>

<agn:Permission token="recipient.show"/>

<% pageContext.setAttribute("sidemenu_active", new String("Settings")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("Blacklist")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Blacklist")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Blacklist")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("blacklist")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Blacklist")); %>

<%@include file="/header.jsp"%>

<table border="0" cellspacing="0" cellpadding="0" width="100%">
    <tr>
        <td><b><bean:message key="E-Mail"/></b>&nbsp;</td>
        <td><center><b><bean:message key="Delete"/></b></center></td>
    </tr>
    <tr><td colspan=2><hr></td></tr>
    <form action="<html:rewrite page="/blacklist.do"/>" method="post">
    <input type="hidden" name="action" value="3">
    <tr>
        <td colspan=2><input type="text" name="newemail" size="30">&nbsp;<input type="image" src="<html:rewrite page="/button?msg=Add"/>" border="0"></td>
    </tr>
    </form>
    <agn:ShowTable id="agntbl1" sqlStatement="<%= new String("SELECT email FROM cust_ban_tbl WHERE company_id=" + AgnUtils.getCompanyID(request) + " ORDER BY email") %>" startOffset="<%= request.getParameter("startWith") %>" maxRows="50">
    <tr>
        <td><%= pageContext.getAttribute("_agntbl1_email") %>&nbsp;</td>
        <td><center>
            <agn:ShowByPermission token="recipient.delete">
                <html:link page="<%= new String("/blacklist.do?action=5&delete=" + URLEncoder.encode((String)pageContext.getAttribute("_agntbl1_email"))) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>delete.gif" alt="L&ouml;schen" border="0"></html:link>&nbsp;
            </agn:ShowByPermission>
        </center></td>
    </tr>
    </agn:ShowTable>
    <tr><td colspan="2"><hr size="1"></td></tr>
    <!-- Multi-Page Indizes -->
    <tr>
        <td colspan="2"><center>
            <agn:ShowTableOffset id="agntbl1" maxPages="20">
                <html:link page="<%= new String("/blacklist.do?action=1&listID=" + pageContext.getRequest().getAttribute("mailingListID") + "&startWith=" + startWith) %>">
                    <% if(activePage!=null) { %>
                        <span class="activenumber">&nbsp;<%= pageNum %>&nbsp;</span>
                    <% } else { %>
                        <%= pageNum %>
                    <% } %>
                </html:link>&nbsp;
            </agn:ShowTableOffset>
        </center></td>
    </tr>
</table>
<%@include file="/footer.jsp"%> 
