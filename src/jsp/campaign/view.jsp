<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*, java.text.*, java.util.*" %>
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

<% if(tmpCampaignID!=0) {
 pageContext.setAttribute("agnSubtitleKey", new String("Campaign")); 
 pageContext.setAttribute("agnSubtitleValue", tmpShortname); 
 pageContext.setAttribute("agnNavigationKey", new String("Campaign"));
 pageContext.setAttribute("agnHighlightKey", new String("Campaign"));
} else {
 pageContext.setAttribute("agnSubtitleKey", new String("NewCampaign"));
 pageContext.setAttribute("agnNavigationKey", new String("CampaignNew")); 
 pageContext.setAttribute("agnHighlightKey", new String("NewCampaign"));
}pageContext.setAttribute("sidemenu_sub_active", new String("NewCampaign")); 
  %>

<% pageContext.setAttribute("sidemenu_active", new String("Campaigns")); %>

<% pageContext.setAttribute("agnTitleKey", new String("Campaigns")); %>
<% pageContext.setAttribute("agnNavHrefAppend", new String("&campaignID="+tmpCampaignID)); %>

<% SimpleDateFormat parsedate=new SimpleDateFormat("yyyyMMdd");
   DateFormat showdate=DateFormat.getDateInstance(DateFormat.MEDIUM, (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY));
   String aDate=new String("");
   Date tmpDate=null;
%>

<%@include file="/header.jsp"%>
<html:errors/>

  <html:form action="/campaign.do">
    <html:hidden property="action"/>
    <html:hidden property="campaignID"/>


    <table border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td><bean:message key="Name"/>:&nbsp;</td>
          <td> 
            <html:text property="shortname" maxlength="99" size="42"/>
          </td>
        </tr>

        <tr> 
          <td><bean:message key="Description_opt"/>:&nbsp;</td>
          <td> 
            <html:textarea property="description" rows="5" cols="32"/>
          </td>
        </tr>

        <tr>
            <td colspan=2>&nbsp;</td>
        </tr>

        <tr>
            <td colspan=2>

              <agn:ShowByPermission token="campaign.change">  
                  <html:image src="button?msg=Save" border="0" property="save" value="save"/>&nbsp;
              </agn:ShowByPermission>
    
              <logic:notEqual name="campaignForm" property="campaignID" value="0">
                  <agn:ShowByPermission token="campaign.delete">    
                      <html:link page="<%= new String("/campaign.do?action=" + CampaignAction.ACTION_CONFIRM_DELETE) %>"><html:img src="button?msg=Delete" border="0"/></html:link>    
                  </agn:ShowByPermission>
              </logic:notEqual>
              

            </td>
        </tr>

    </table>
    
   
    
<% if(tmpCampaignID!=0) { %>

    <br><br>

    <span class="head3"><bean:message key="Mailings"/>:</span>
                               <!-- MailingBaseAction.ACTION_VIEW -->
      <br><br><html:link page="<%= new String("/mailingbase.do?action=" + MailingBaseAction.ACTION_NEW+"&mailingID=0&campaignID="+tmpCampaignID) %>"><bean:message key="New_Mailing"/>...</html:link>
      <table border="0" cellspacing="0" cellpadding="0">
      

        <tr><td colspan="4">&nbsp;</td></tr>

        <tr>
            <td><b><bean:message key="Mailing"/>&nbsp;&nbsp;</b></td>
            <td><b><bean:message key="Description"/>&nbsp;&nbsp;</b></td>
            <td><b><bean:message key="Mailinglist"/>&nbsp;&nbsp;</b></td>
            <td><b><bean:message key="mailing.senddate"/>&nbsp;&nbsp;</b></td>
            <td><span class="head3">&nbsp;</span></td>
        </tr>
        <tr><td colspan="5"><hr></td></tr>
        <% int rows = 0; %>
        
        
        <agn:ShowTable id="agnTbl" sqlStatement="<%= new String("SELECT a.mailing_id, a.shortname, a.description, b.shortname AS listname, (SELECT "+AgnUtils.sqlDateString("min(c.change_date)", "yyyymmdd")+" FROM mailing_account_tbl c WHERE a.mailing_id=c.mailing_id AND c.status_field='W') AS senddate FROM mailing_tbl a, mailinglist_tbl b WHERE a.company_id="+AgnUtils.getCompanyID(request)+ " AND a.campaign_id=" + tmpCampaignID + " AND a.deleted<>1 AND a.is_template=0 AND a.mailinglist_id=b.mailinglist_id ORDER BY senddate DESC, mailing_id DESC")%>" startOffset="<%= request.getParameter("startWith") %>" maxRows="50" encodeHtml="0">
            <tr> <!-- MailingBaseAction.ACTION_VIEW -->
                <td><html:link page="<%= new String("/mailingbase.do?action=" + MailingBaseAction.ACTION_VIEW + "&mailingID=" + pageContext.getAttribute("_agnTbl_mailing_id")) %>"><b><%= pageContext.getAttribute("_agnTbl_shortname") %></b></html:link>&nbsp;&nbsp;</td>
                <td><html:link page="<%= new String("/mailingbase.do?action=" + MailingBaseAction.ACTION_VIEW + "&mailingID=" + pageContext.getAttribute("_agnTbl_mailing_id")) %>"><%= SafeString.cutLength((String)pageContext.getAttribute("_agnTbl_description"), 40) %></html:link>&nbsp;&nbsp;</td>
                <td><%= pageContext.getAttribute("_agnTbl_listname") %>&nbsp;&nbsp;</td>
                        <% try{
                             tmpDate=parsedate.parse((String)pageContext.getAttribute("_agnTbl_senddate"));
                             aDate=showdate.format(tmpDate);
                            } catch (Exception e) {
                                 aDate=new String("");
                            }
                        %>
                <td><%= aDate %>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                <td>
                    <agn:ShowByPermission token="mailing.delete">  <!-- MailingBaseAction.ACTION_CONFIRM_DELETE -->
                        <html:link page="<%= new String("/mailing.do?action=" + "3" + "&mailingID=" + pageContext.getAttribute("_agnTbl_mailing_id")) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>delete.gif" alt="<bean:message key="Delete"/>" border="0"></html:link>
                    </agn:ShowByPermission>        <!-- MailingBaseAction.ACTION_VIEW -->
                    <html:link page="<%= new String("/mailingbase.do?action=" + MailingBaseAction.ACTION_DELETE + "&mailingID=" + pageContext.getAttribute("_agnTbl_mailing_id")) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>bearbeiten.gif" alt="<bean:message key="Edit"/>" border="0"></html:link>
                </td> 
            </tr>
            <% rows++; %>
        </agn:ShowTable>
        <% if(rows==0) { %>
        <tr><td colspan="5"><b><bean:message key="NoMailingsInCampaign"/>&nbsp;&nbsp;</b></td></tr>
        <% } %>

        <tr><td colspan="5"><hr></td></tr>
        <tr><td colspan="5"><center>
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
<% } %>




  </html:form>
<%@include file="/footer.jsp"%>
