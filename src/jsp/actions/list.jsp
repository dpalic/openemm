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
 --%><%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*, java.util.*" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://ajaxtags.org/tags/ajax" prefix="ajax" %>
<agn:CheckLogon/>

<agn:Permission token="actions.show"/>
<% pageContext.setAttribute("sidemenu_active", new String("Actions")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("Overview")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Actions")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Actions")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("ActionsOverview")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Overview")); %>
<% pageContext.setAttribute("ACTION_LIST",EmmActionAction.ACTION_LIST ); %>
<% pageContext.setAttribute("ACTION_VIEW",EmmActionAction.ACTION_VIEW ); %>
<% pageContext.setAttribute("ACTION_CONFIRM_DELETE",EmmActionAction.ACTION_CONFIRM_DELETE ); %>

<%@include file="/header.jsp"%>

<% 	EmmActionForm aForm = null;
	if(session.getAttribute("emmActionForm")!=null) {
		aForm = (EmmActionForm) session.getAttribute("emmActionForm");
	}	
 %>

              <table border="0" cellspacing="0" cellpadding="0" width="100%">
                <tr>
        	<td>
        	<html:form action="/action">
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
        	</html:form>
        	</td>
        </tr>
              
              
				<tr>
					<td >
					<ajax:displayTag id="actionsTable" ajaxFlag="displayAjax" tableClass="dataTable">
						<display:table class="dataTable" id="emmaction" name="emmactionList" pagesize="${emmActionForm.numberofRows}" sort="external" requestURI="/action.do?action=${ACTION_LIST}" excludedParams="*" > 
							<display:column headerClass="head_name" class="name" titleKey="Action" property="shortname" maxLength="20"  sortable="true" paramId="actionID" paramProperty="actionId" url="/action.do?action=${ACTION_VIEW}"/>
						    <display:column headerClass="head_description" class="description"  titleKey="Description" property="description" maxLength="35" maxWords="5" sortable="true" paramId="actionID" paramProperty="actionId" url="/action.do?action=${ACTION_VIEW}"  />
							<display:column headerClass="head_name" class="name" titleKey="used">
							<logic:greaterThan name="emmaction" property="used" value="0">
									<bean:message key="Yes"/>
							</logic:greaterThan>
							<logic:lessThan name="emmaction" property="used" value="1">
										<bean:message key="No"/>
							</logic:lessThan>
							
								
							</display:column>
							<display:column class="edit">
								<html:link page="/action.do?action=${ACTION_CONFIRM_DELETE}&actionID=${emmaction.actionId}"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>delete.gif" alt="<bean:message key="Delete"/>" border="0"></html:link>
         			            <html:link page="/action.do?action=${ACTION_VIEW}&actionID=${emmaction.actionId}"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>bearbeiten.gif" alt="<bean:message key="Edit"/>" border="0"></html:link>
							</display:column>
						</display:table>
					</ajax:displayTag>	
					</td>
				</tr>

              </table>
<%@include file="/footer.jsp"%>
