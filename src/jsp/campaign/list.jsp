<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="campaign.show"/> 

<% pageContext.setAttribute("sidemenu_active", new String("Campaigns")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("Overview")); %>

<% pageContext.setAttribute("agnNavigationKey", new String("CampaignsOverview")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Overview")); %>

<% pageContext.setAttribute("agnTitleKey", new String("Campaigns")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Campaigns")); %>
<%@include file="/header.jsp"%>
<html:errors/>

  <html:form action="/campaign.do">
    <html:hidden property="action"/>
    <html:hidden property="campaignID"/>

      <table border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td><span class="head3"><bean:message key="Campaign"/>&nbsp;&nbsp;</span></td>
            <td><span class="head3"><bean:message key="Description"/>&nbsp;&nbsp;</span></td>

            <td><span class="head3">&nbsp;</span></td>
        </tr>
        <tr><td colspan="4"><hr></td></tr>
        <agn:ShowTable id="agnTbl" sqlStatement="<%= new String("SELECT campaign_id, shortname, description FROM campaign_tbl WHERE company_id="+AgnUtils.getCompanyID(request))%>" startOffset="<%= request.getParameter("startWith") %>" maxRows="50" encodeHtml="0">
            <tr>
                <td><html:link page="<%= new String("/campaign.do?action=" + CampaignAction.ACTION_VIEW + "&campaignID=" + pageContext.getAttribute("_agnTbl_campaign_id")) %>"><b><%= pageContext.getAttribute("_agnTbl_shortname") %></b></html:link>&nbsp;&nbsp;</td>
                <td><html:link page="<%= new String("/campaign.do?action=" + CampaignAction.ACTION_VIEW + "&campaignID=" + pageContext.getAttribute("_agnTbl_campaign_id")) %>"><%= SafeString.cutLength((String)pageContext.getAttribute("_agnTbl_description"), 40) %></html:link>&nbsp;&nbsp;</td>
                <td>
                    <agn:ShowByPermission token="campaign.delete">
                        <html:link page="<%= new String("/campaign.do?action=" + CampaignAction.ACTION_CONFIRM_DELETE + "&campaignID=" + pageContext.getAttribute("_agnTbl_campaign_id")) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>delete.gif" alt="<bean:message key="Delete"/>" border="0"></html:link>
                    </agn:ShowByPermission>
                    <agn:ShowByPermission token="campaign.change">
                        <html:link page="<%= new String("/campaign.do?action=" + CampaignAction.ACTION_VIEW + "&campaignID=" + pageContext.getAttribute("_agnTbl_campaign_id")) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>bearbeiten.gif" alt="<bean:message key="Edit"/>" border="0"></html:link>
                    </agn:ShowByPermission>
                </td>
            </tr>
        </agn:ShowTable>
        <tr><td colspan="4"><hr></td></tr>
        <tr><td colspan="3"><center>
             <agn:ShowTableOffset id="agnTbl" maxPages="10">
                <html:link page="<%= new String("/campaign.do?action=" + CampaignAction.ACTION_LIST + "&startWith=" + pageContext.getAttribute("startWith")) %>">
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
  </html:form>
<%@include file="/footer.jsp"%>
