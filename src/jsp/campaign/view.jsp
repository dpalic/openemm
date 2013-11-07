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
 --%><%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.forms.*, org.agnitas.web.*, org.agnitas.beans.*, java.text.*, java.util.*" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<agn:CheckLogon/>

<agn:Permission token="campaign.show"/> 

<%  int tmpCampaignID = 0;
    if(session.getAttribute("campaignForm")!=null) {
       tmpCampaignID = ((CampaignForm)session.getAttribute("campaignForm")).getCampaignID();
    }
 %>

<c:choose>
	<c:when test="${campaignForm.campaignID != 0}">
		<c:set var="agnSubtitleKey" value="Campaign" scope="page" />
		<c:set var="agnSubtitleValue" value="${campaignForm.shortname}" scope="page" /> 
		<c:set var="agnNavigationKey" value="Campaign" scope="page" />
		<c:set var="agnHighlightKey" value="Campaign" scope="page" />
	</c:when>
	<c:otherwise>
 		<c:set var="agnSubtitleKey" value="NewCampaign" scope="page" />
		<c:set var="agnNavigationKey" value="CampaignNew" scope="page" /> 
		<c:set var="agnHighlightKey" value="NewCampaign" scope="page" />
	</c:otherwise>
</c:choose>
<c:set var="sidemenu_sub_active" value="NewCampaign" scope="page" />

<c:set var="sidemenu_active" value="Campaigns" scope="page" />
<c:set var="agnTitleKey" value="Campaigns" scope="page" />
<c:set var="agnNavHrefAppend" value="&campaignID=${campaignForm.campaignID}" scope="page" />

<c:set var="ACTION_CONFIRM_DELETE" value="<%= CampaignAction.ACTION_CONFIRM_DELETE %>" scope="page" />
<c:set var="ACTION_NEW" value="<%= MailingBaseAction.ACTION_NEW %>" scope="page" />
<c:set var="ACTION_VIEW" value="<%= MailingBaseAction.ACTION_VIEW %>" scope="page" />
<c:set var="ACTION_LIST" value="<%= CampaignAction.ACTION_LIST %>" scope="page" />


<% SimpleDateFormat parsedate=new SimpleDateFormat("yyyyMMdd");
   DateFormat showdate = DateFormat.getDateInstance(DateFormat.MEDIUM, (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY));
   String aDate = "";
   Date tmpDate = null;
%>

<%@include file="/header.jsp"%>
<%@include file="/messages.jsp" %>

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
                      <html:link page="/campaign.do?action=${ACTION_CONFIRM_DELETE}&campaignID=${campaignForm.campaignID}"><html:img src="button?msg=Delete" border="0"/></html:link>    
                  </agn:ShowByPermission>
              </logic:notEqual>
            </td>
        </tr>
    </table>

<c:if test="${campaignForm.campaignID != 0}">
    <br><br>
    <span class="head3"><bean:message key="Mailings"/>:</span>
        <!-- MailingBaseAction.ACTION_VIEW -->
      <agn:ShowByPermission token="mailing.new">
      	<br><br><html:link page="/mailingbase.do?action=${ACTION_NEW}&mailingID=0&campaignID=${campaignForm.campaignID}"><bean:message key="New_Mailing"/>...</html:link>
      </agn:ShowByPermission>
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
        <agn:ShowTable id="agnTbl" sqlStatement="<%= new String("SELECT a.mailing_id, a.shortname, a.description, b.shortname AS listname, (SELECT "+AgnUtils.sqlDateString("min(c."+AgnUtils.changeDateName()+")", "yyyymmdd")+" FROM mailing_account_tbl c WHERE a.mailing_id=c.mailing_id AND c.status_field='W') AS senddate FROM mailing_tbl a, mailinglist_tbl b WHERE a.company_id="+AgnUtils.getCompanyID(request)+ " AND a.campaign_id=" + tmpCampaignID + " AND a.deleted<>1 AND a.is_template=0 AND a.mailinglist_id=b.mailinglist_id ORDER BY senddate DESC, mailing_id DESC")%>" startOffset="<%= request.getParameter("startWith") %>" maxRows="50" encodeHtml="0">
            <tr> <!-- MailingBaseAction.ACTION_VIEW -->
                <td><html:link page="/mailingbase.do?action=${ACTION_VIEW}&mailingID=${_agnTbl_mailing_id}"><b>${_agnTbl_shortname}</b></html:link>&nbsp;&nbsp;</td>
                <td><html:link page="/mailingbase.do?action=${ACTION_VIEW}&mailingID=${_agnTbl_mailing_id}"><%= SafeString.cutLength((String)pageContext.getAttribute("_agnTbl_description"), 40) %></html:link>&nbsp;&nbsp;</td>
                <td>${_agnTbl_listname}&nbsp;&nbsp;</td>
                        <% try{
                             tmpDate = parsedate.parse((String)pageContext.getAttribute("_agnTbl_senddate"));
                             aDate = showdate.format(tmpDate);
                            } catch (Exception e) {
                                 aDate = new String("");
                            }
                        %>
                <td><%= aDate %>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                <td>
                    <agn:ShowByPermission token="mailing.delete">  <!-- MailingBaseAction.ACTION_CONFIRM_DELETE -->
                        <html:link page="/mailingbase.do?action=${ACTION_CONFIRM_DELETE}&mailingID=${_agnTbl_mailing_id}"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>delete.gif" alt="<bean:message key="Delete"/>" border="0"></html:link>
                    </agn:ShowByPermission>        <!-- MailingBaseAction.ACTION_VIEW -->
                    <html:link page="/mailingbase.do?action=${ACTION_VIEW}&mailingID=${_agnTbl_mailing_id}"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>bearbeiten.gif" alt="<bean:message key="Edit"/>" border="0"></html:link>
                </td> 
            </tr>
            <% rows++; %>
        </agn:ShowTable>
        <% if(rows == 0) { %>
        <tr><td colspan="5"><b><bean:message key="NoMailingsInCampaign"/>&nbsp;&nbsp;</b></td></tr>
        <% } %>

        <tr><td colspan="5"><hr></td></tr>
        <tr><td colspan="5"><center>
             <agn:ShowTableOffset id="agnTbl" maxPages="10">
                <html:link page="/campaign.do?action=${ACTION_LIST}&startWith=${startWith}">
                <c:if test="${not empty activePage}">
                    <span class="activenumber">&nbsp;
				</c:if>
                ${pageNum}
                <c:if test="${not empty activePage}">
                    &nbsp;</span>
				</c:if>
                </html:link>&nbsp;
             </agn:ShowTableOffset></center></td></tr>

      </table>
	</c:if>
  </html:form>
<%@include file="/footer.jsp"%>