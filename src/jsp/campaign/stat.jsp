<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*, org.agnitas.stat.*, java.util.*, org.springframework.context.*, org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="campaign.show"/> 

<% int tmpCampaignID=0;
   String tmpShortname=new String("");
   int tmpTargetID=0;
   if(session.getAttribute("campaignForm")!=null) {
      tmpCampaignID=((CampaignForm)session.getAttribute("campaignForm")).getCampaignID();
      tmpShortname=((CampaignForm)session.getAttribute("campaignForm")).getShortname();
      // tmpTargetID=((CampaignForm)session.getAttribute("campaignForm")).getTargetID();
   }
   String file = "";
   String timekey = "";
   java.util.Hashtable my_map = null;
%>

<% if(tmpCampaignID!=0) {
    pageContext.setAttribute("agnSubtitleKey", new String("Campaign")); 
    pageContext.setAttribute("agnSubtitleValue", tmpShortname); 
    pageContext.setAttribute("agnNavigationKey", new String("Campaign"));
    pageContext.setAttribute("agnHighlightKey", new String("Statistics"));

      // csv download stuff:
      org.agnitas.util.EmmCalendar my_calendar = new EmmCalendar(java.util.TimeZone.getDefault());
      TimeZone zone=TimeZone.getTimeZone(((Admin)session.getAttribute("emm.admin")).getAdminTimezone());

      my_calendar.changeTimeWithZone(zone);
      java.util.Date my_time = my_calendar.getTime();
      String Datum = my_time.toString();
      timekey = Long.toString(my_time.getTime());
      pageContext.setAttribute("time_key", timekey);
      my_map = null;
      if(pageContext.getSession().getAttribute("map") == null)
      {
          my_map = new java.util.Hashtable();
          pageContext.getSession().setAttribute("map",my_map);
          // System.out.println("map exists.");
      } else {
          my_map = (java.util.Hashtable)(pageContext.getSession().getAttribute("map"));
          // System.out.println("new map.");
      }
      
      file = ((CampaignForm)session.getAttribute("campaignForm")).getCsvfile();
} %>

<%  pageContext.setAttribute("sidemenu_active", new String("Campaigns")); %>
<%  pageContext.setAttribute("agnTitleKey", new String("Campaigns")); %>
<%  pageContext.setAttribute("agnNavHrefAppend", new String("&campaignID="+tmpCampaignID)); %>



<%@include file="/header.jsp"%>
<html:errors/>

  <html:form action="/campaign">
    <html:hidden property="action"/>
    <html:hidden property="campaignID"/>
    <html:hidden property="__STRUTS_CHECKBOX_netto" value="false"/>

    <table border="0" cellspacing="0" cellpadding="0" width="90%">

    <tr>
        <td colspan="2">
                <bean:message key="Target"/>:&nbsp;
                <html:select property="targetID">
                   <html:option value="0"><bean:message key="All_Subscribers"/></html:option>
                     <agn:ShowTable id="agntbl3" sqlStatement="<%= new String("SELECT target_id, target_shortname FROM dyn_target_tbl WHERE company_id="+AgnUtils.getCompanyID(request)) %>" maxRows="500">
                   <html:option value="<%= (String)(pageContext.getAttribute("_agntbl3_target_id")) %>"><%= pageContext.getAttribute("_agntbl3_target_shortname") %></html:option>
                     </agn:ShowTable>
                </html:select><br><br>
            </td>
            <td align="right"><html:link page="<%= new String("/file_download?key=" + timekey) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>icon_save.gif" border="0"></html:link></td>
        </tr>

        <tr><td colspan="3"> 
               <html:checkbox property="netto"/>&nbsp;<bean:message key="Unique_Clicks"/>&nbsp;&nbsp;<html:image src="button?msg=OK" border="0"/><br>&nbsp;
            </td>
        </tr>



        <tr>
            <td colspan=3>&nbsp;</td>
        </tr>

    </table>

<% if(tmpCampaignID!=0) { %>


<table border="0" cellspacing="0" cellpadding="0">

    <tr><td colspan="11">&nbsp;</td></tr>
<%
    file += "\"" + SafeString.getLocaleString("Mailing", (Locale)session.getAttribute("messages_lang")) + "\";";
    file += "\"" + SafeString.getLocaleString("Opened_Mails", (Locale)session.getAttribute("messages_lang")) + "\";";
    file += "\"" + SafeString.getLocaleString("Opt_Outs", (Locale)session.getAttribute("messages_lang")) + "\";";
    file += "\"" + SafeString.getLocaleString("Bounces", (Locale)session.getAttribute("messages_lang")) + "\";";
    file += "\"" + SafeString.getLocaleString("Recipients", (Locale)session.getAttribute("messages_lang")) + "\";";
    file += "\"" + SafeString.getLocaleString("Clicks", (Locale)session.getAttribute("messages_lang")) + "\"";
    file += "\r\n";
%>

    <tr>
        <td><b><bean:message key="Mailing"/>&nbsp;</b></td>
        <td background="<bean:write name="emm.layout" property="baseUrl" scope="session"/>border_06.gif"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="10" border="0"></td>
        <td align="right"><b>&nbsp;<bean:message key="Opened_Mails"/>&nbsp;</b></td>
        <td background="<bean:write name="emm.layout" property="baseUrl" scope="session"/>border_06.gif"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="10" border="0"></td>
        <td align="right"><b>&nbsp;<bean:message key="Opt_Outs"/>&nbsp;</b></td>
        <td background="<bean:write name="emm.layout" property="baseUrl" scope="session"/>border_06.gif"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="10" border="0"></td>
        <td align="right"><b>&nbsp;<bean:message key="Bounces"/>&nbsp;</b></td>
        <td background="<bean:write name="emm.layout" property="baseUrl" scope="session"/>border_06.gif"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="10" border="0"></td>
        <td align="right"><b>&nbsp;<bean:message key="Recipients"/>&nbsp;</b></td>
        <td background="<bean:write name="emm.layout" property="baseUrl" scope="session"/>border_06.gif"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="10" border="0"></td>
        <td align="right"><b>&nbsp;<bean:message key="Clicks"/>&nbsp;</b></td>
    </tr>

    <tr><td colspan="11"><hr></td></tr>

<%  Hashtable mailingData = ((CampaignForm)session.getAttribute("campaignForm")).getMailingData();
    if(mailingData == null) {
        mailingData=new Hashtable();
    }
    Enumeration keys = mailingData.keys();
    ApplicationContext aContext=WebApplicationContextUtils.getWebApplicationContext(application);
    CampaignStatEntry aktEntry = (CampaignStatEntry) aContext.getBean("CampaignStatEntry");
    int aktKey = 0;

    while(keys.hasMoreElements()) {
        aktKey = ((Integer)(keys.nextElement())).intValue();
        aktEntry = (CampaignStatEntry)(mailingData.get(new Integer(aktKey)));
%>
    <tr>
        <td><html:link page="<%= new String("/mailing_stat.do?action=" + MailingStatAction.ACTION_MAILINGSTAT + "&mailingID=" + aktKey) %>"><b><nobr><% if(aktEntry.getShortname().length() > 25) { %><%= aktEntry.getShortname().substring(0,24) %><% } else { %><%= aktEntry.getShortname() %><% } %>&nbsp;</nobr></b></html:link></td>
        <% file += "\"" + aktEntry.getShortname() + "\";"; %>
        <td background="<bean:write name="emm.layout" property="baseUrl" scope="session"/>border_06.gif"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="10" border="0"></td>
        <td align="right">
            &nbsp;<img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif" width="<%=  ((float)(aktEntry.getOpened()) / (float)((CampaignForm)session.getAttribute("campaignForm")).getMaxOpened() ) * 50 + 1 %>" height="10">
            <br><%= aktEntry.getOpened() %>&nbsp;
        </td>
        <% file += "\"" + aktEntry.getOpened() + "\";"; %>    
        <td background="<bean:write name="emm.layout" property="baseUrl" scope="session"/>border_06.gif"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="10" border="0"></td>
        <td align="right">
            &nbsp;<img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif" width="<%=  ((float)(aktEntry.getOptouts()) / (float)((CampaignForm)session.getAttribute("campaignForm")).getMaxOptouts() ) * 50 + 1 %>" height="10">
            <br><%= aktEntry.getOptouts() %>&nbsp;
        </td>
        <% file += "\"" + aktEntry.getOptouts() + "\";"; %>
        <td background="<bean:write name="emm.layout" property="baseUrl" scope="session"/>border_06.gif"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="10" border="0"></td>    
        <td align="right">
            &nbsp;<img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif" width="<%=  ((float)(aktEntry.getBounces()) / (float)((CampaignForm)session.getAttribute("campaignForm")).getMaxBounces() ) * 50 + 1 %>" height="10">
            <br><%= aktEntry.getBounces() %>&nbsp;
        </td>
        <% file += "\"" + aktEntry.getBounces() + "\";"; %>
        <td background="<bean:write name="emm.layout" property="baseUrl" scope="session"/>border_06.gif"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="10" border="0"></td>
        <td align="right">
            &nbsp;<img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif" width="<%=  ((float)(aktEntry.getTotalMails()) / (float)((CampaignForm)session.getAttribute("campaignForm")).getMaxSubscribers() ) * 50 + 1 %>" height="10">
            <br><%= aktEntry.getTotalMails() %>&nbsp;
        </td>
        <% file += "\"" + aktEntry.getTotalMails() + "\";"; %>
        <td background="<bean:write name="emm.layout" property="baseUrl" scope="session"/>border_06.gif"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="10" border="0"></td>
        <td align="right">
            &nbsp;<img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif" width="<%=  ((float)(aktEntry.getClicks()) / (float)((CampaignForm)session.getAttribute("campaignForm")).getMaxClicks() ) * 50 + 1 %>" height="10">
            <br><%= aktEntry.getClicks() %>&nbsp;
        </td>
        <% file += "\"" + aktEntry.getClicks() + "\"" + "\r\n"; %>
    </tr>
    <% } %>




    <tr><td colspan="11"><hr></td></tr>

    <tr>
        <td><b><bean:message key="Total"/></b></td>
        <% file += "\"" + SafeString.getLocaleString("Total", (Locale)session.getAttribute("messages_lang")) + "\";"; %>
        <td background="<bean:write name="emm.layout" property="baseUrl" scope="session"/>border_06.gif"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="10" border="0"></td>
        <td align="right">
            <b><%= ((CampaignForm)session.getAttribute("campaignForm")).getOpened() %>&nbsp;</b>
        </td>
        <% file += "\"" + ((CampaignForm)session.getAttribute("campaignForm")).getOpened() + "\";"; %>
        <td background="<bean:write name="emm.layout" property="baseUrl" scope="session"/>border_06.gif"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="10" border="0"></td>
        <td align="right">
            <b><%= ((CampaignForm)session.getAttribute("campaignForm")).getOptouts() %>&nbsp;</b>
        </td>
        <% file += "\"" + ((CampaignForm)session.getAttribute("campaignForm")).getOptouts() + "\";"; %>
        <td background="<bean:write name="emm.layout" property="baseUrl" scope="session"/>border_06.gif"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="10" border="0"></td>
        <td align="right">
            <b><%= ((CampaignForm)session.getAttribute("campaignForm")).getBounces() %>&nbsp;</b>
        </td>
        <% file += "\"" + ((CampaignForm)session.getAttribute("campaignForm")).getBounces() + "\";"; %>
        <td background="<bean:write name="emm.layout" property="baseUrl" scope="session"/>border_06.gif"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="10" border="0"></td>
        <td align="right">
            <b><%= ((CampaignForm)session.getAttribute("campaignForm")).getSubscribers() %>&nbsp;</b>
        </td>
        <% file += "\"" + ((CampaignForm)session.getAttribute("campaignForm")).getSubscribers() + "\";"; %>
        <td background="<bean:write name="emm.layout" property="baseUrl" scope="session"/>border_06.gif"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="10" border="0"></td>
        <td align="right">
            <b><%= ((CampaignForm)session.getAttribute("campaignForm")).getClicks() %></b>
        </td>
        <% file += "\"" + ((CampaignForm)session.getAttribute("campaignForm")).getClicks() + "\"\r\n" ; %>
    </tr>

    <tr><td colspan="11">&nbsp;</td></tr>

    <tr>
        <td colspan="11">
          <html:link page="<%= new String("/campaign.do?action=" + CampaignAction.ACTION_LIST) %>"><html:img src="button?msg=Cancel" border="0"/></html:link>
        </td>
    </tr>

</table>
<% } %>

<%   // put csv file in session:
     my_map.put(timekey,  file);
     pageContext.getSession().setAttribute("map", my_map);
%>

  </html:form>
  
<%@include file="/footer.jsp"%>
