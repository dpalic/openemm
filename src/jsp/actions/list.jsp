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
<agn:CheckLogon/>

<agn:Permission token="actions.show"/>
<% pageContext.setAttribute("sidemenu_active", new String("Actions")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("Overview")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Actions")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Actions")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("ActionsOverview")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Overview")); %>
<%@include file="/header.jsp"%>

              <table border="0" cellspacing="0" cellpadding="0" width="100%">

                <tr>
                    <td><span class="head3"><bean:message key="Action"/></span>&nbsp;&nbsp;</td>
                    <td><span class="head3"><bean:message key="Description"/></span></td>
                    <td><center><span class="head3">&nbsp;</span></center></td>
                </tr>
                <tr><td colspan="3"><hr></td></tr>
<%   String sqlStatement="SELECT action_id, shortname, description FROM rdir_action_tbl WHERE company_id=" + AgnUtils.getCompanyID(request) + " ORDER BY shortname"; %>

              <agn:ShowTable id="agntbl1" sqlStatement="<%= sqlStatement %>" startOffset="<%= request.getParameter("startWith") %>" maxRows="50">
                <tr>
                    <td><html:link page="<%= new String("/action.do?action=" + EmmActionAction.ACTION_VIEW + "&actionID=" + pageContext.getAttribute("_agntbl1_action_id")) %>"><b><%= pageContext.getAttribute("_agntbl1_shortname") %></b></html:link>&nbsp;&nbsp;</td>
                    <td><%= SafeString.cutLength((String)pageContext.getAttribute("_agntbl1_description"), 50) %>&nbsp;&nbsp;</td>
                    <td>
                        <html:link page="<%= new String("/action.do?action=" + EmmActionAction.ACTION_CONFIRM_DELETE + "&actionID=" + pageContext.getAttribute("_agntbl1_action_id")) %>">
                        <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>delete.gif" alt="L&ouml;schen" border="0"></html:link>
                        <html:link page="<%= new String("/action.do?action=" + EmmActionAction.ACTION_VIEW + "&actionID=" + pageContext.getAttribute("_agntbl1_action_id")) %>">
                        <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>bearbeiten.gif" alt="Bearbeiten" border="0"></html:link>
                    </td>
                </tr>
              </agn:ShowTable>
              <tr><td colspan="3"><hr size="1"></td></tr>
              <!-- Multi-Page Indizes -->
                <tr><td colspan="3"><center>
                     <agn:ShowTableOffset id="agntbl1" maxPages="20">
                        <html:link page="<%= new String("/action.do?action=" + EmmActionAction.ACTION_LIST + "&startWith=" + startWith) %>">
                        <% if(activePage!=null) { %>
                            <span class="activenumber">&nbsp;
                        <% } %>
                        <%= pageNum %>
                        <% if(activePage!=null) { %>
                            &nbsp;</span>
                        <% } %>
                        </html:link>&nbsp;
                     </agn:ShowTableOffset></center></td></tr>


              </table>
<%@include file="/footer.jsp"%>
