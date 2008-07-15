<%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*" contentType="text/html; charset=utf-8" buffer="32kb"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<agn:CheckLogon/>

<agn:Permission token="stats.mailing"/>

<% pageContext.setAttribute("sidemenu_active", new String("Statistics")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("MailStat")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Statistics")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Statistics")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("statsMailing")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("MailStat")); %>

<%@include file="/header.jsp"%>

<table border="0" cellspacing="0" cellpadding="0">

    <tr>
       <td><b><bean:message key="Mailing"/>&nbsp;</b></td>
       <td><b><bean:message key="Description"/>&nbsp;</b></td>
       <td><b><bean:message key="Mailinglist"/>&nbsp;</b></td>
    </tr>

    <tr><td colspan="3"><hr></td></tr>

 <agn:ShowTable id="agnTbl" sqlStatement="<%= new String("SELECT a.mailing_id, a.shortname, a.description, b.shortname AS listname FROM mailing_tbl a, mailinglist_tbl b WHERE a.company_id="+AgnUtils.getCompanyID(request)+ " AND a.mailinglist_id=b.mailinglist_id AND a.deleted=0 AND a.is_template=0 ORDER BY mailing_id DESC")%>" startOffset="<%= request.getParameter("startWith") %>" maxRows="50">

    <tr>
       <td><html:link page="<%= new String("/mailing_stat.do?action=" + MailingStatAction.ACTION_MAILINGSTAT + "&mailingID=" + pageContext.getAttribute("_agnTbl_mailing_id")) %>"><b><%= pageContext.getAttribute("_agnTbl_shortname") %></b></html:link>&nbsp;&nbsp;</td>
       <td><html:link page="<%= new String("/mailing_stat.do?action=" + MailingStatAction.ACTION_MAILINGSTAT + "&mailingID=" + pageContext.getAttribute("_agnTbl_mailing_id")) %>"><%= SafeString.cutLength((String)pageContext.getAttribute("_agnTbl_description"), 40) %></html:link>&nbsp;&nbsp;</td>
       <td><%= pageContext.getAttribute("_agnTbl_listname") %>&nbsp;</td>
    </tr>

 </agn:ShowTable>
    <tr><td colspan="3"><hr></td></tr>
    <tr><td colspan="3"><center>
         <agn:ShowTableOffset id="agnTbl" maxPages="10">
            <html:link page="<%= new String("/mailing_stat.do?action=" + MailingStatAction.ACTION_LIST + "&startWith=" + pageContext.getAttribute("startWith")) %>">
            <% if(pageContext.getAttribute("activePage")!=null) { %>
                <span class="activenumber">&nbsp;
            <% } %>
            <%= pageContext.getAttribute("pageNum") %>
            <% if(pageContext.getAttribute("activePage")!=null) { %>
                &nbsp;</span>
            <% } %>
            </html:link>&nbsp;
         </agn:ShowTableOffset></center></td></tr>




</table>

<%@include file="/footer.jsp"%>
