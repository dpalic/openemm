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
 --%><%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, java.net.*, org.agnitas.beans.*, org.agnitas.web.*" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://ajaxtags.org/tags/ajax" prefix="ajax" %>

<agn:CheckLogon/>

<agn:Permission token="recipient.show"/>

<% pageContext.setAttribute("sidemenu_active","Settings"); %>
<% pageContext.setAttribute("sidemenu_sub_active", "Blacklist"); %>
<% pageContext.setAttribute("agnTitleKey","Blacklist"); %>
<% pageContext.setAttribute("agnSubtitleKey","Blacklist"); %>
<% pageContext.setAttribute("agnNavigationKey","blacklist"); %>
<% pageContext.setAttribute("agnHighlightKey","Blacklist"); %>
<% pageContext.setAttribute("ACTION_CONFIRM_DELETE", BlacklistAction.ACTION_CONFIRM_DELETE ); %>
<% pageContext.setAttribute("ACTION_SAVE", BlacklistAction.ACTION_SAVE ); %>
<% pageContext.setAttribute("ACTION_LIST", BlacklistAction.ACTION_LIST); %>

<%@include file="/header.jsp"%>
<script type="text/javascript">
<!--
	function parametersChanged(){
		document.getElementsByName('blacklistForm')[0].numberOfRowsChanged.value = true;
	}
//-->
</script>
<script src="js/tablecolumnresize.js" type="text/javascript" ></script>
<script type="text/javascript">
	var prevX = -1;
    var tableID = 'recipient';
    var columnindex = 0;
    var dragging = false;	
   document.onmousemove = drag;
   document.onmouseup = dragstop;
</script>

<%@include file="/messages.jsp" %>

<table border="0" cellspacing="0" cellpadding="0" width="100%">
    
    
    <tr>
    <html:form action="/blacklist" >
    <html:hidden property="action" value="${ACTION_SAVE}" />
        <td><html:text property="newemail" size="30"/></td>
        <td><html:image src="button?msg=Add" border="0" /></td>
   	</html:form>
	</tr>
   	
	<tr>
	 <td colspan="2">
	 	<table>
	 	<html:form action="/blacklist" >
   			<html:hidden property="action" value="${ACTION_LIST}" />
	 	<tr>
			<td>
				<bean:message key="Admin.numberofrows" />
			</td>
			<td>
				<html:select property="numberofRows">
						<%
							String[] sizes = { "20", "50", "100" };
							for (int i = 0; i < sizes.length; i++) {
								%>
							<html:option value="<%=sizes[i]%>"><%=sizes[i]%></html:option>
							<%
								}
						%>

		 		</html:select>
			</td>
		</tr>
		<tr>
			<td colspan="2"><html:image src="button?msg=OK" border="0" /></td>
		</tr>
    	</html:form>
	 	
	 	</table>
   	 </td>
   	</tr> 
   	    
    <tr>
    <td colspan="2">
    	<display:table class="dataTable" pagesize="${blacklistForm.numberofRows}" id="recipient" name="blackListEntries" sort="external" requestURI="/blacklist.do?action=${ACTION_LIST}&__fromdisplaytag=true" excludedParams="*" size="${blackListEntries.fullListSize}"  partialList="true" >
    		<display:column class="email" headerClass="head_email" property="email"  titleKey="E-Mail" sortable="true" />
    		<display:column class="senddate" headerClass="head_senddate " property="date" sortName="creation_date" titleKey="Date" sortable="true" format="{0,date,yyyy-MM-dd}" />
    		
    		<agn:ShowByPermission token="recipient.delete">
    			<display:column>
	                <html:link page="<%= "/blacklist.do?action="+BlacklistAction.ACTION_CONFIRM_DELETE+"&delete=" + URLEncoder.encode(((BlackListEntry)pageContext.getAttribute("recipient")).getEmail() , "UTF-8") %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>delete.gif" alt="<bean:message key="Delete"/>"  border="0"></html:link>&nbsp;
    			</display:column>
            </agn:ShowByPermission>
    	</display:table>
    	<script type="text/javascript">
			table = document.getElementById('recipient');
			rewriteTableHeader(table);  
			writeWidthFromHiddenFields(table);			
		</script>
    	
    </td>	
    </tr>
</table>    
<%@include file="/footer.jsp"%> 
