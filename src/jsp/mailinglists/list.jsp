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
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://ajaxtags.org/tags/ajax" prefix="ajax" %>


<agn:CheckLogon/>

<agn:Permission token="mailinglist.show"/>

<% pageContext.setAttribute("sidemenu_active", new String("Mailinglists")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("Overview")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("MailinglistsOverview")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Overview")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Mailinglists")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Mailinglists")); %>
<% pageContext.setAttribute("ACTION_LIST" , MailinglistAction.ACTION_LIST); %>
<% pageContext.setAttribute("ACTION_VIEW" , MailinglistAction.ACTION_VIEW); %>
<% pageContext.setAttribute("ACTION_CONFIRM_DELETE" , MailinglistAction.ACTION_CONFIRM_DELETE); %>

<%@include file="/header.jsp"%>
<%@include file="/messages.jsp" %>

<script src="js/tablecolumnresize.js" type="text/javascript" ></script>
<script type="text/javascript">
	var prevX = -1;
    var tableID = 'mailinglist';
    var columnindex = 0;
    var dragging = false;

    document.onmousemove = drag;
    document.onmouseup = dragstop;
</script>

              <table border="0" cellspacing="0" cellpadding="0" width="100%">
              	<tr>
              		<td> 
              	<html:form action="/mailinglist.do">
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
                                <logic:iterate collection="${mailinglistForm.columnwidthsList}" indexId="i" id="width">
                                    <html:hidden property="columnwidthsList[${i}]" />
                                </logic:iterate>
							</td>
						</tr>
						<tr>
						<td colspan="2" valign="bottom">
							<html:image src="button?msg=Show" border="0"/>
						</td>
					</tr>	
				</table>
				</html:form>	
              </td>
              	</tr>
                <tr>
                	<td>
                		<display:table class="dataTable"  id="mailinglist" name="mailinglistList" pagesize="${mailinglistForm.numberofRows}" sort="external" requestURI="/mailinglist.do?action=${ACTION_LIST}&__fromdisplaytag=true" excludedParams="*">
                			<display:column  headerClass="head_id" class="id" property="mailinglistId" titleKey="MailinglistID"/>
                			<display:column  headerClass="head_name" class="name" property="shortname" titleKey="Mailinglist" sortable="true" url="/mailinglist.do?action=${ACTION_VIEW}" paramId="mailinglistID" paramProperty="mailinglistId" />
                			<display:column  headerClass="head_description" class="description" property="description" titleKey="Description" sortable="true" url="/mailinglist.do?action=${ACTION_VIEW}" paramId="mailinglistID" paramProperty="mailinglistId" />
                			<display:column  class="edit">
                				 <agn:ShowByPermission token="mailinglist.delete">
                              <html:link page="/mailinglist.do?action=${ACTION_CONFIRM_DELETE}&mailinglistID=${mailinglist.mailinglistId}" ><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>delete.gif" alt="<bean:message key="Delete"/>" border="0"></html:link>
                          </agn:ShowByPermission>
                          <html:link page="/mailinglist.do?action=${ACTION_VIEW}&mailinglistID=${mailinglist.mailinglistId}"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>bearbeiten.gif" alt="<bean:message key="Edit"/>" border="0"></html:link>
                		  </display:column>
                		</display:table>
                        <script type="text/javascript">
                            table = document.getElementById('mailinglist');
                            rewriteTableHeader(table);
                            writeWidthFromHiddenFields(table);
                        </script>

                	</td>
                </tr>
              </table>
<%@include file="/footer.jsp"%>
