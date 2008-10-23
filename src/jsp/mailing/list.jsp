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
 --%><%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*, java.text.*, java.util.*" buffer="32kb" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://ajaxtags.org/tags/ajax" prefix="ajax" %>

<agn:CheckLogon/>

<% pageContext.setAttribute("sidemenu_sub_active", new String("Overview")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Overview")); %>

<logic:equal name="mailingBaseForm" property="isTemplate" value="false">
<% pageContext.setAttribute("sidemenu_active", new String("Mailings")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("MailingsOverview")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Mailings")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Mailings")); %>
<% pageContext.setAttribute("ACTION_VIEW", MailingBaseAction.ACTION_VIEW ); %>
<% pageContext.setAttribute("ACTION_USED_ACTIONS", MailingBaseAction.ACTION_USED_ACTIONS ); %>
<% pageContext.setAttribute("ACTION_CONFIRM_DELETE", MailingBaseAction.ACTION_CONFIRM_DELETE ); %>
</logic:equal>

<logic:equal name="mailingBaseForm" property="isTemplate" value="true">
<% pageContext.setAttribute("sidemenu_active", new String("Templates")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("TemplatesOverview")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Templates")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Templates")); %>
<% pageContext.setAttribute("ACTION_VIEW", MailingBaseAction.ACTION_VIEW ); %>
</logic:equal>

<% SimpleDateFormat parsedate=new SimpleDateFormat("yyyy-MM-dd");
   DateFormat showdate=DateFormat.getDateInstance(DateFormat.MEDIUM, (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY));
   String aDate=new String("");
   Date tmpDate=null;
   int isTemplate=0;
   if(((MailingBaseForm)session.getAttribute("mailingBaseForm")).isIsTemplate()) {
       isTemplate=1;
   }
%>

<% if(isTemplate==0) { %>
<agn:Permission token="mailing.show"/>
<% } else { %>
<agn:Permission token="template.show"/>
<% } %>

<%@include file="/header.jsp"%>
<script type="text/javascript">
<!--
	function parametersChanged(){
		document.getElementsByName('mailingBaseForm')[0].numberOfRowsChanged.value = true;
	}
//-->
</script>
<html:errors/>
<html:form action="/mailingbase">
	<html:hidden property="numberOfRowsChanged" />   
	<% if(isTemplate==0) { %>
		<table>
			<tr>
				<td colspan="2">
					<html:hidden property="__STRUTS_CHECKBOX_mailingTypeNormal" value="false"/>
					<html:hidden property="__STRUTS_CHECKBOX_mailingTypeEvent" value="false"/>
					<html:hidden property="__STRUTS_CHECKBOX_mailingTypeDate" value="false"/>
					<html:checkbox property="mailingTypeNormal" onchange="parametersChanged()"><bean:message key="Mailing_normal_show"/></html:checkbox>&nbsp;&nbsp;&nbsp;
					<html:checkbox property="mailingTypeEvent" onchange="parametersChanged()"><bean:message key="Mailing_event_show"/></html:checkbox>&nbsp;&nbsp;&nbsp;
					<html:checkbox property="mailingTypeDate" onchange="parametersChanged()"><bean:message key="Mailing_date_show"/></html:checkbox>&nbsp;
				</td>
			</tr>
			<tr>
				<td><bean:message key="Admin.numberofrows"/>&nbsp;									
					<html:select property="numberofRows" onchange="parametersChanged()">
                		<%
                			String[] sizes={"20","50","100"};
                			for( int i=0;i< sizes.length; i++ )
                			{
                					 %>
                				<html:option value="<%= sizes[i] %>"><%= sizes[i] %></html:option>	
                			<%
                			}                			
                			%>		 
                					 
                		</html:select></td>
            	<td align="right"><html:image src="button?msg=Show" border="0"/></td>
			</tr>			
		</table>
	<% } 
	  else { %>
	  <table>
			<tr>
				<td><bean:message key="Admin.numberofrows"/></td>
				<td>
					<html:select property="numberofRows">
                		<%
                			String[] sizes={"20","50","100"};
                			for( int i=0;i< sizes.length; i++ )
                			{
                					 %>
                				<html:option value="<%= sizes[i] %>"><%= sizes[i] %></html:option>	
                			<%
                			}                			
                			%>		 
                					 
                		</html:select>
				</td>
			</tr>
			<tr>
			<td colspan="2" valign="bottom">
					<html:image src="button?msg=Show" border="0"/>
				</td>
			</tr>	
	  	</table>	
	  <% } %>
</html:form>
                <table border="0" cellspacing="0" cellpadding="0">
                <tr><td><hr></td></tr>
<%	EmmLayout aLayout=(EmmLayout)session.getAttribute("emm.layout");
	String dyn_bgcolor=null;
    boolean bgColor=true;
    String types = "0,1,2";
    MailingBaseForm aForm=(MailingBaseForm)session.getAttribute("mailingBaseForm");
    if(aForm != null) {
        types = aForm.getTypes();
    }
 %>         
 	<tr>
 		<td>
 			<display:table class="dataTable"  id="mailing" name="mailinglist" pagesize="${mailingBaseForm.numberofRows}" requestURI="/mailingbase.do?action=${mailingBaseForm.action}&isTemplate=${mailingBaseForm.isTemplate}" excludedParams="*" partialList="true" size="${mailinglist.fullListSize}" sort="external">
				<logic:equal name="mailingBaseForm" property="isTemplate" value="false">
				<display:column headerClass="head_action" class="action">
					<html:link page="/mailingbase.do?action=${ACTION_USED_ACTIONS}&mailingID=${mailing.mailingid}"><img border="0" title="<bean:message key="action_link" />" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>extlink.gif"></html:link>&nbsp;&nbsp;                   
	   			</display:column>
	   			<display:column headerClass="head_mailing" class="mailing" titleKey="Mailing"  maxLength="20" sortable="true" url="/mailingbase.do?action=${ACTION_VIEW}" property="shortname" paramId="mailingID" paramProperty="mailingid" />
	   			<display:column headerClass="head_description" class="description" titleKey="Description" maxLength="35" maxWords="5" property="description" url="/mailingbase.do?action=${ACTION_VIEW}"  paramId="mailingID" paramProperty="mailingid" sortable="true" /> 
 	   			<display:column headerClass="head_mailinglist" class="mailinglist" titleKey="Mailinglist" property="mailinglist" sortable="true"/>
 	   			<display:column headerClass="senddate" class="senddate" titleKey="mailing.senddate" format="{0,date,yyyy-MM-dd}" property="senddate" sortable="true"/> 	      
 	   			<display:column class="edit">
		 	   	 <agn:ShowByPermission token="mailing.delete">
        		     <html:link page="/mailingbase.do?action=${ACTION_CONFIRM_DELETE}&mailingID=${mailing.mailingid}"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>delete.gif" alt="<bean:message key="Delete"/>" border="0"></html:link>
         		</agn:ShowByPermission>
             		<html:link page="/mailingbase.do?action=${ACTION_VIEW}&mailingID=${mailing.mailingid}"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>bearbeiten.gif" alt="<bean:message key="Edit"/>" border="0"></html:link>
 	   			</display:column>
 	   </logic:equal>
 	   <logic:equal name="mailingBaseForm" property="isTemplate" value="true"> 	
 		<display:column headerClass="head_mailing" class="mailing" titleKey="Template"  maxLength="20" sortable="true" url="/mailingbase.do?action=${ACTION_VIEW}" property="shortname" paramId="mailingID" paramProperty="mailingid" />
		<display:column headerClass="head_description" class="description" titleKey="Description" maxLength="35" maxWords="5" property="description" url="/mailingbase.do?action=${ACTION_VIEW}"  paramId="mailingID" paramProperty="mailingid" sortable="true" />
		<display:column headerClass="head_mailinglist" class="mailinglist" titleKey="Mailinglist" property="mailinglist" sortable="true"/>
		<display:column class="edit">
		<agn:ShowByPermission token="mailing.delete">
        		     <html:link page="/mailingbase.do?action=6&mailingID=${mailing.mailingid}"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>delete.gif" alt="<bean:message key="Delete"/>" border="0"></html:link>
         		</agn:ShowByPermission>
             		<html:link page="/mailingbase.do?action=${ACTION_VIEW}&mailingID=${mailing.mailingid}"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>bearbeiten.gif" alt="<bean:message key="Edit"/>" border="0"></html:link>
 	   	</display:column> 	
	   	</logic:equal>
	 </display:table>
		 </td>
 	</tr>
 </table>
<%@include file="/footer.jsp"%>
