<%--
/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/
 --%><%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.web.forms.*, org.agnitas.beans.*, org.agnitas.stat.*, java.util.*, org.springframework.context.*, org.springframework.web.context.support.WebApplicationContextUtils" %>
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
 pageContext.setAttribute("sidemenu_sub_active", new String("NewCampaign"));


 pageContext.setAttribute("sidemenu_active", new String("Campaigns"));

 pageContext.setAttribute("agnTitleKey", new String("Campaigns"));
 pageContext.setAttribute("agnNavHrefAppend", new String("&campaignID="+tmpCampaignID));


	  // csv download stuff: 
		  
      org.agnitas.util.EmmCalendar my_calendar = new EmmCalendar(java.util.TimeZone.getDefault());
      TimeZone zone=TimeZone.getTimeZone(((Admin)session.getAttribute("emm.admin")).getAdminTimezone());

      my_calendar.changeTimeWithZone(zone);
      java.util.Date my_time = my_calendar.getTime();
      String Datum = my_time.toString();

      timekey = Long.toString(my_time.getTime());
      pageContext.setAttribute("time_key", timekey);
      my_map = null;
      
      if(pageContext.getSession().getAttribute("map") == null)	// original line
      {
          my_map = new java.util.Hashtable();
          pageContext.getSession().setAttribute("map",my_map);
          // pageContext.setAttribute("map",my_map);
      } else {
          // my_map = (java.util.Hashtable)(pageContext.getAttribute("map"));
          my_map = (java.util.Hashtable)(pageContext.getSession().getAttribute("map"));
      }

      file = ((CampaignForm)session.getAttribute("campaignForm")).getCsvfile();
      
//    put csv file in pagecontext:	
     // my_map.put(timekey,  file);
      // pageContext.setAttribute("map", my_map);
      //request.setAttribute("map", my_map);     

} %>

<%  pageContext.setAttribute("sidemenu_active", new String("Campaigns")); %>
<%  pageContext.setAttribute("agnTitleKey", new String("Campaigns")); %>
<%  pageContext.setAttribute("agnNavHrefAppend", new String("&campaignID="+tmpCampaignID)); %>



<%@include file="/header.jsp"%>
<%@include file="/messages.jsp" %>

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
                     <agn:ShowTable id="agntbl3" sqlStatement="<%= new String("SELECT target_id, target_shortname FROM dyn_target_tbl WHERE company_id="+AgnUtils.getCompanyID(request) + " and deleted=0") %>" maxRows="500">
                     <!-- 
						<html:option value="<%= (String)(session.getAttribute("_agntbl3_target_id")) %>"><%= session.getAttribute("_agntbl3_target_shortname") %></html:option>
                 	-->	
						<html:option value="${_agntbl3_target_id}">${_agntbl3_target_shortname}</html:option>
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
    file = "\"" + SafeString.getLocaleString("Mailing", (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\";";
    file += "\"" + SafeString.getLocaleString("Opened_Mails", (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\";";
    file += "\"" + SafeString.getLocaleString("Opt_Outs", (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\";";
    file += "\"" + SafeString.getLocaleString("Bounces", (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\";";
    file += "\"" + SafeString.getLocaleString("Recipients", (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\";";
    file += "\"" + SafeString.getLocaleString("Clicks", (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\"";
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

<%  CampaignForm aForm = (CampaignForm)session.getAttribute("campaignForm");
	// Hashtable mailingData = ((CampaignForm)session.getAttribute("campaignForm")).getMailingData();
	Hashtable mailingData = aForm.getMailingData();
    if(mailingData == null) {
        mailingData=new Hashtable();
    }
    // Enumeration keys = mailingData.keys();
    Iterator<Number> keys = aForm.getSortedKeys().iterator();
    ApplicationContext aContext=WebApplicationContextUtils.getWebApplicationContext(application);
    // CampaignStatEntry aktEntry = (CampaignStatEntry) aContext.getBean("CampaignStatEntry");
    CampaignStatEntry aktEntry = null;
    
    int aktKey = 0;

    while(keys.hasNext()) {
        aktKey = (keys.next()).intValue();
        aktEntry = (CampaignStatEntry)(mailingData.get(new Integer(aktKey)));
        
%>
    <tr>
        <td><html:link page="<%= new String("/mailing_stat.do?action=" + MailingStatAction.ACTION_MAILINGSTAT + "&mailingID=" + aktKey) %>"><b><nobr><% if(aktEntry.getShortname().length() > 25) { %><%= aktEntry.getShortname().substring(0,24) %><% } else { %><%= aktEntry.getShortname() %><% } %>&nbsp;</nobr></b></html:link></td>
        <% file += "\"" + aktEntry.getShortname() + "\";"; %>
        <td background="<bean:write name="emm.layout" property="baseUrl" scope="session"/>border_06.gif"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="10" border="0"></td>
        <td align="right">
            &nbsp;<img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif" width="<%=  ((float)(aktEntry.getOpened()) / (float)((CampaignForm)session.getAttribute("campaignForm")).getMaxOpened() ) * 50 + 1 %>" height="10"><br><%= aktEntry.getOpened() %>&nbsp;
        </td>
        <% file += "\"" + aktEntry.getOpened() + "\";"; %>
        <td background="<bean:write name="emm.layout" property="baseUrl" scope="session"/>border_06.gif"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="10" border="0"></td>
        <td align="right">
            &nbsp;<img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif" width="<%=  ((float)(aktEntry.getOptouts()) / (float)((CampaignForm)session.getAttribute("campaignForm")).getMaxOptouts() ) * 50 + 1 %>" height="10"><br><%= aktEntry.getOptouts() %>&nbsp;
        </td>
        <% file += "\"" + aktEntry.getOptouts() + "\";"; %>
        <td background="<bean:write name="emm.layout" property="baseUrl" scope="session"/>border_06.gif"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="10" border="0"></td>
        <td align="right">
            &nbsp;<img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif" width="<%=  ((float)(aktEntry.getBounces()) / (float)((CampaignForm)session.getAttribute("campaignForm")).getMaxBounces() ) * 50 + 1 %>" height="10"><br><%= aktEntry.getBounces() %>&nbsp;
        </td>
        <% file += "\"" + aktEntry.getBounces() + "\";"; %>
        <td background="<bean:write name="emm.layout" property="baseUrl" scope="session"/>border_06.gif"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="10" border="0"></td>
        <td align="right">
            &nbsp;<img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif" width="<%=  ((float)(aktEntry.getTotalMails()) / (float)((CampaignForm)session.getAttribute("campaignForm")).getMaxSubscribers() ) * 50 + 1 %>" height="10"><br><%= aktEntry.getTotalMails() %>&nbsp;
        </td>
        <% file += "\"" + aktEntry.getTotalMails() + "\";"; %>
        <td background="<bean:write name="emm.layout" property="baseUrl" scope="session"/>border_06.gif"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="10" border="0"></td>
        <td align="right">
            &nbsp;<img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif" width="<%=  ((float)(aktEntry.getClicks()) / (float)((CampaignForm)session.getAttribute("campaignForm")).getMaxClicks() ) * 50 + 1 %>" height="10"><br><%= aktEntry.getClicks() %>&nbsp;
        </td>
        <% file += "\"" + aktEntry.getClicks() + "\"" + "\r\n"; %>
    </tr>
    <% } %>

    <tr><td colspan="11"><hr></td></tr>

    <tr>
        <td><b><bean:message key="Total"/></b></td>
        <% file += "\"" + SafeString.getLocaleString("Total", (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\";"; %>
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

<%   // put csv file in request-Context.
     my_map.put(timekey,  file);
     pageContext.getSession().setAttribute("map", my_map);
     // pageContext.getSession().setAttribute("map", timekey);    
%>

  </html:form>

<%@include file="/footer.jsp"%>
