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

<agn:Permission token="mailinglist.show"/>

<% pageContext.setAttribute("sidemenu_active", new String("Mailinglists")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("Overview")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("MailinglistsOverview")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Overview")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Mailinglists")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Mailinglists")); %>

<%@include file="/header.jsp"%>
<html:errors/>

              <table border="0" cellspacing="0" cellpadding="0" width="100%">
                <tr>                        
                    <td><span class="head3"><bean:message key="MailinglistID"/>&nbsp;&nbsp;</span></td>
                    <td><span class="head3"><bean:message key="Mailinglist"/>&nbsp;&nbsp;</span></td>
                    <td><span class="head3"><bean:message key="Description"/>&nbsp;&nbsp;</span></td>
                    <td><span class="head3">&nbsp;</span></td>
                </tr>
                <tr><td colspan="3"><hr></td></tr>
              <agn:ShowTable id="agntbl1" sqlStatement="<%= "SELECT mailinglist_id, shortname, description FROM mailinglist_tbl WHERE company_id="+AgnUtils.getCompanyID(request)+" ORDER BY mailinglist_id DESC"%>" startOffset="<%= request.getParameter("startWith") %>" maxRows="50">
                  <tr>
                      <td align="right"><%= (String)pageContext.getAttribute ("_agntbl1_mailinglist_id") %> &nbsp;&nbsp;</td>
                      <td><html:link page="<%= "/mailinglist.do?action=" + MailinglistAction.ACTION_VIEW + "&mailinglistID=" + (String)pageContext.getAttribute ("_agntbl1_mailinglist_id") %>"><b><%= (String)pageContext.getAttribute ("_agntbl1_shortname") %></b></html:link>&nbsp;&nbsp;</td>
                      <td><%= SafeString.cutLength((String)pageContext.getAttribute ("_agntbl1_description"), 40) %>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                      <td>
                          <agn:ShowByPermission token="mailinglist.delete">
                              <html:link page="<%= "/mailinglist.do?action=" + MailinglistAction.ACTION_CONFIRM_DELETE + "&mailinglistID=" + (String)pageContext.getAttribute ("_agntbl1_mailinglist_id") %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>delete.gif" alt="<bean:message key="Delete"/>" border="0"></html:link>
                          </agn:ShowByPermission>
                          <html:link page="<%= "/mailinglist.do?action=" + MailinglistAction.ACTION_VIEW + "&mailinglistID=" + (String)pageContext.getAttribute ("_agntbl1_mailinglist_id") %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>bearbeiten.gif" alt="<bean:message key="Edit"/>" border="0"></html:link>
                      </td>
                  </tr>
              </agn:ShowTable>
              <tr><td colspan="5"><hr size="1"></td></tr>
              <!-- Multi-Page Indizes -->
                <tr><td colspan="5"><center>
                     <agn:ShowTableOffset id="agntbl1" maxPages="19">
                        <html:link page="<%= new String("/mailinglist.do?action=" + MailinglistAction.ACTION_LIST + "&listID=" + (String)pageContext.getAttribute ("_agntbl1_mailinglist_id") + "&startWith=" + startWith) %>">
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
