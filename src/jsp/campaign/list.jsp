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
 --%><%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.web.forms.*, org.agnitas.beans.*" %>
 <%@page import="org.apache.commons.beanutils.DynaBean"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://ajaxtags.org/tags/ajax" prefix="ajax" %>

<agn:CheckLogon/>

<agn:Permission token="campaign.show"/> 

<% pageContext.setAttribute("sidemenu_active", new String("Campaigns")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("Overview")); %>

<% pageContext.setAttribute("agnNavigationKey", new String("CampaignsOverview")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Overview")); %>

<% pageContext.setAttribute("agnTitleKey", new String("Campaigns")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Campaigns")); %>
<% pageContext.setAttribute("ACTION_LIST", CampaignAction.ACTION_LIST); %>
<% pageContext.setAttribute("ACTION_VIEW", CampaignAction.ACTION_VIEW); %>
<% pageContext.setAttribute("ACTION_CONFIRM_DELETE", CampaignAction.ACTION_CONFIRM_DELETE); %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://ajaxtags.org/tags/ajax" prefix="ajax" %>

<%@include file="/header.jsp"%>
<html:errors/>
      <table border="0" cellspacing="0" cellpadding="0">
        <tr>
        	<td>
        	<html:form action="/campaign">
        	<table> 
        	<tr>       	
				<td><bean:message key="Admin.numberofrows"/></td> 
				<td>									
					<html:select property="numberofRows">
                		<%	String[] sizes={"20","50","100"};
                			for( int i=0;i< sizes.length; i++ )
                			{ %>
                				<html:option value="<%= sizes[i] %>"><%= sizes[i] %></html:option>	
              			<%  } %>		 
                	</html:select>
				</td>
        	</tr>
        	<tr>
        		<td colspan="2">
        			<html:image src="button?msg=Show" border="0"/>
        		</td>
        	</tr>
        	</table>
        	</html:form>
        	</td>
        </tr>
        <tr><td>
         <ajax:displayTag id="campaignTable" ajaxFlag="displayAjax">
         	<display:table class="dataTable"  id="campaign" name="campaignlist" pagesize="${campaignForm.numberofRows}"  requestURI="/campaign.do?action=${ACTION_LIST}" excludedParams="*" sort="external">
         		<display:column headerClass="head_name" class="name" titleKey="Campaign"  maxLength="20" property="shortname" sortable="true" paramId="campaignID" paramProperty="campaignId"  url="/campaign.do?action=${ACTION_VIEW}" />
         	    <display:column headerClass="head_description" class="description" titleKey="Description"  maxLength="20" property="description" sortable="true" paramId="campaignID" paramProperty="campaignId"  url="/campaign.do?action=${ACTION_VIEW}" />
         		<display:column class="edit">
         			<agn:ShowByPermission token="campaign.delete">
                        <html:link page="/campaign.do?action=${ACTION_CONFIRM_DELETE}&campaignID=${campaign.campaignId}"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>delete.gif" alt="<bean:message key="Delete"/>" border="0"></html:link>
                    </agn:ShowByPermission>
                    <agn:ShowByPermission token="campaign.change">
                        <html:link page="/campaign.do?action=${ACTION_VIEW}&campaignID=${campaign.campaignId}"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>bearbeiten.gif" alt="<bean:message key="Edit"/>" border="0"></html:link>
                    </agn:ShowByPermission>
         		</display:column>
         	</display:table>
         </ajax:displayTag>
        </td></tr>
      </table>
<%@include file="/footer.jsp"%>