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
 --%><%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*" contentType="text/html; charset=utf-8" buffer="32kb"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<agn:CheckLogon/>

<agn:Permission token="stats.mailing"/>

<% pageContext.setAttribute("sidemenu_active", new String("Statistics")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("MailStat")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Statistics")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Statistics")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("statsMailing")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("MailStat")); %>
<% pageContext.setAttribute("ACTION_LIST", MailingStatAction.ACTION_LIST ); %>
<% pageContext.setAttribute("ACTION_MAILINGSTAT", MailingStatAction.ACTION_MAILINGSTAT ); %>



<%@include file="/header.jsp"%>
<script src="js/tablecolumnresize.js" type="text/javascript" ></script>
<script type="text/javascript">
	var prevX = -1;
    var tableID = 'mailingStat';
    var columnindex = 0;
    var dragging = false;
	
   document.onmousemove = drag;
   document.onmouseup = dragstop;
</script>

<table border="0" cellspacing="0" cellpadding="0">

    <tr>
    <td>
    	 	<html:form action="/mailing_stat">
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
        		<td colspan="2">
        			<html:image src="button?msg=Show" border="0"/>
        		</td>
        	</tr>
        	</table>
        	<logic:iterate collection="${mailingStatForm.columnwidthsList}"	indexId="i" id="width">
								<html:hidden property="columnwidthsList[${i}]" />
			</logic:iterate>
        	</html:form>
        	</td>
    </tr>
    <tr><td>
    
    	<display:table class="dataTable" id="mailingStat" name="mailingStatlist" excludedParams="*" pagesize="${mailingStatForm.numberofRows}"  requestURI="/mailing_stat.do?action=${ACTION_LIST}&__fromdisplaytag=true" >    	
    		<display:column headerClass="head_name" class="name" titleKey="Mailing" property="shortname" sortable="true" paramId="mailingID" paramProperty="mailingid"  url="/mailing_stat.do?action=${ACTION_MAILINGSTAT}" />
    		<display:column headerClass="head_description" class="description" titleKey="Description"  property="description" sortable="true" paramId="mailingID" paramProperty="mailingid"  url="/mailing_stat.do?action=${ACTION_MAILINGSTAT}" />
    		<display:column headerClass="head_name" class="name" titleKey="Mailinglist"  property="listname" sortable="true" />
    	</display:table>
    		<script type="text/javascript">
				table = document.getElementById('mailingStat');
				rewriteTableHeader(table);  
				writeWidthFromHiddenFields(table);			
			</script>
 
    	</td>
    </tr>
</table>

<%@include file="/footer.jsp"%>
