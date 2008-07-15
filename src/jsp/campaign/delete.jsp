<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>
<agn:Permission token="campaign.delete"/>

<% int tmpCampaignID=0;
   String tmpShortname=new String("");

   if(session.getAttribute("campaignForm")!=null) {
      tmpCampaignID=((CampaignForm)session.getAttribute("campaignForm")).getCampaignID();
      tmpShortname=((CampaignForm)session.getAttribute("campaignForm")).getShortname();
   }
%>

<% pageContext.setAttribute("sidemenu_active", new String("Campaigns")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Campaigns")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Campaign")); %>
<% pageContext.setAttribute("agnSubtitleValue", tmpShortname); %>

<% pageContext.setAttribute("agnNavHrefAppend", new String("&campaignID="+tmpCampaignID)); %>


<%@include file="/header.jsp"%>

<html:errors/>

             <span class="head3"><bean:message key="DeleteCampaignQuestion"/></span><br>
              <p>
                <html:form action="/campaign.do">
                <html:hidden property="campaignID"/>
                <html:hidden property="action"/>
                <html:image src="button?msg=Delete" property="kill" value="kill"/>
                <html:link page="<%= new String("/campaign.do?action=" + CampaignAction.ACTION_LIST + "&campaignID=" + tmpCampaignID) %>"><html:img src="button?msg=Cancel" border="0"/></html:link>
                </html:form>
              </p>

<%@include file="/footer.jsp"%>
