<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*,java.util.*"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="campaign.show"/> 

<% int tmpCampaignID=0;
   String tmpShortname=new String("");
   if(session.getAttribute("campaignForm")!=null) {
      tmpCampaignID=((CampaignForm)session.getAttribute("campaignForm")).getCampaignID();
      tmpShortname=((CampaignForm)session.getAttribute("campaignForm")).getShortname();
   }
%>

<% pageContext.setAttribute("agnSubtitleKey", new String("Campaign")); %>
<% pageContext.setAttribute("agnSubtitleValue", tmpShortname); %>
<% pageContext.setAttribute("agnNavigationKey", new String("Campaign")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Statistics")); %>
<% pageContext.setAttribute("sidemenu_active", new String("Campaigns")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Campaigns")); %>
<% pageContext.setAttribute("agnNavHrefAppend", new String("&campaignID="+tmpCampaignID)); %>
<% pageContext.setAttribute("agnRefresh", new String("2")); %>

<%@include file="/header.jsp"%>

<html:errors/>

  <html:form action="/campaign.do">
    <html:hidden property="action"/>
    <html:hidden property="campaignID"/>

        <table border="0" cellspacing="0" cellpadding="0" width="400">
            <tr>
                <td>
                    <b>&nbsp;<b>
                </td>
            </tr>
            <tr>
                <td>
                    <img border="0" width="44" height="48" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>wait.gif"/>
                </td>
            </tr>
            <tr>
                <td>
                    <b>&nbsp;<b>
                </td>
            </tr>
            <tr>
                <td>
                    <b><bean:message key="StatSplashMessage"/><b>
                </td>
            </tr>
            <tr>
                <td>
                    <b>&nbsp;<b>
                </td>
            </tr>

        </table>

  </html:form>
                        
<div align="right"><a href="#"><img onclick="window.open('help_de/index.htm','help1','width=250,height=600,left=0,top=0,scrollbars=yes');" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>help.gif" border="0"></a></div>                        
<%@include file="/footer.jsp"%>
