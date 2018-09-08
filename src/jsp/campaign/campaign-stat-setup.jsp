<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.web.forms.CampaignForm"  errorPage="/error.jsp" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>

<agn:Permission token="campaign.show"/>

<%
    int tmpCampaignID = 0;
    String tmpShortname = new String("");
    if(session.getAttribute("campaignForm") != null) {
        tmpCampaignID = ((CampaignForm) session.getAttribute("campaignForm")).getCampaignID();
        tmpShortname=((CampaignForm)session.getAttribute("campaignForm")).getShortname();
        request.setAttribute("tmpCampaignID", tmpCampaignID);
    }
    if(tmpCampaignID != 0) {
        request.setAttribute("agnSubtitleKey", new String("campaign.Campaign"));
        request.setAttribute("agnSubtitleValue", tmpShortname);
        request.setAttribute("agnNavigationKey", new String("Campaign"));
        request.setAttribute("agnHighlightKey", new String("Statistics"));
        request.setAttribute("sidemenu_sub_active", new String("Campaigns"));
        request.setAttribute("sidemenu_active", new String("Campaigns"));
        request.setAttribute("agnTitleKey", new String("Campaigns"));
        request.setAttribute("agnNavHrefAppend", new String("&campaignID=" + tmpCampaignID));
    }
    request.setAttribute("sidemenu_active", new String("Mailings"));
    request.setAttribute("agnTitleKey", new String("Campaigns"));
    request.setAttribute("agnNavHrefAppend", new String("&campaignID=" + tmpCampaignID));
%>
<c:set var="agnHelpKey" value="archiveView" scope="request" />
