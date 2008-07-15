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
 --%><%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://ajaxtags.org/tags/ajax" prefix="ajax" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<agn:CheckLogon/>

<agn:Permission token="forms.view"/>

<% pageContext.setAttribute("sidemenu_active", new String("Forms")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("Overview")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("FormsOverview")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Overview")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Forms")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Forms")); %>
<% pageContext.setAttribute( "ACTION_LIST",UserFormEditAction.ACTION_LIST); %>
<% pageContext.setAttribute( "ACTION_CONFIRM_DELETE",UserFormEditAction.ACTION_CONFIRM_DELETE); %>
<% pageContext.setAttribute( "ACTION_VIEW",UserFormEditAction.ACTION_VIEW); %>

<%@include file="/header.jsp"%>

<html:errors/>

              <table border="0" cellspacing="0" cellpadding="0" width="100%">
               <tr>
                	<td>
                		<html:form action="/userform">
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
              		<td>
              			<ajax:displayTag id="userFormTable" ajaxFlag="displayAjax" tableClass="dataTable">
              			<display:table class="dataTable"  id="userform" name="userformlist" pagesize="${userFormEditForm.numberofRows}" requestURI="/userform.do?action=${ACTION_LIST}" excludedParams="*">
              				<display:column headerClass="head_name" class="name" property="formname" titleKey="Form"  maxLength="20" sortable="true" url="/userform.do?action=${ACTION_VIEW}" paramId="formID" paramProperty="formid" />
              				<display:column headerClass="head_description" class="description" property="description" titleKey="Description"  maxLength="35"  sortable="true" url="/userform.do?action=${ACTION_VIEW}" paramId="formID" paramProperty="formid" />
              				<display:column class="edit">
            					  <agn:ShowByPermission token="forms.delete">
                        			<html:link page="/userform.do?action=${ACTION_CONFIRM_DELETE}&formID=${userform.formid}" ><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>delete.gif" alt="<bean:message key="Delete"/>" border="0"></html:link>
                            	</agn:ShowByPermission>
              					    <html:link page="/userform.do?action=${ACTION_VIEW}&formID=${userform.formid}"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>bearbeiten.gif" alt="<bean:message key="Edit"/>" border="0"></html:link>
              				</display:column>
              			</display:table> 
              			</ajax:displayTag>             		
              		</td>
              	</tr>
                </table>

<%@include file="/footer.jsp"%>
