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
 --%><%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, java.util.*" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<agn:CheckLogon/>

<agn:Permission token="wizard.export"/>

<% pageContext.setAttribute("sidemenu_active", new String("Recipients")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("Export")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Export")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Export")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("subscriber_export")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("ExportWizard")); %>

<%@include file="/header.jsp"%>
<%@include file="/messages.jsp" %>

              <html:form action="/exportwizard">
                  <html:hidden property="action"/>
                  <b><font color=#73A2D0><bean:message key="ExportWizStep_1_of_3"/></font></b>
                  <br><br>
                  <b><bean:message key="SelectExportDef"/>:</b><br>&nbsp;<br>
                  <table border="0" cellspacing="0" cellpadding="0">
                      <tr>
                          <td><span class="head3"><bean:message key="Name"/>&nbsp;&nbsp;</span></td>
                          <td><span class="head3"><bean:message key="Description"/>&nbsp;&nbsp;</span></td>

                          <td><span class="head3">&nbsp;</span></td>
                      </tr>
                      <tr><td colspan="4"><hr></td></tr>
                      <agn:ShowTable id="agnTbl" sqlStatement="<%= new String("SELECT id, shortname, description FROM export_predef_tbl WHERE company_id="+AgnUtils.getCompanyID(request) +" AND deleted=0")%>" startOffset="<%= request.getParameter("startWith") %>" maxRows="50">
                          <tr>
                              <td><html:link page="<%= new String("/exportwizard.do?action=" + ExportWizardAction.ACTION_QUERY + "&exportPredefID=" + pageContext.getAttribute("_agnTbl_id")) %>"><b><%= pageContext.getAttribute("_agnTbl_shortname") %></b></html:link>&nbsp;&nbsp;</td>
                              <td><html:link page="<%= new String("/exportwizard.do?action=" + ExportWizardAction.ACTION_QUERY + "&exportPredefID=" + pageContext.getAttribute("_agnTbl_id")) %>"><%= SafeString.cutLength((String)pageContext.getAttribute("_agnTbl_description"), 40) %></html:link>&nbsp;&nbsp;</td>
                              <td>
                                  <html:link page="<%= new String("/exportwizard.do?action=" + ExportWizardAction.ACTION_CONFIRM_DELETE + "&exportPredefID=" + pageContext.getAttribute("_agnTbl_id")) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>delete.gif" alt="<bean:message key="Delete"/>" border="0"></html:link>
                                  <html:link page="<%= new String("/exportwizard.do?action=" + ExportWizardAction.ACTION_QUERY + "&exportPredefID=" + pageContext.getAttribute("_agnTbl_id")) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>revise.gif" alt="<bean:message key="Edit"/>" border="0"></html:link>
                              </td>
                          </tr>
                      </agn:ShowTable>
                      <tr><td colspan="4"><hr></td></tr>
                      <tr><td colspan="3"><center>
                      <agn:ShowTableOffset id="agnTbl" maxPages="10">
                          <html:link page="<%= new String("/exportwizard.do?action=" + StrutsActionBase.ACTION_LIST + "&startWith=" + pageContext.getAttribute("startWith")) %>">
                              <% if(pageContext.getAttribute("activePage")!=null) { %>
                              <span class="activenumber">&nbsp;
                              <% } %>
                              <%= pageContext.getAttribute("pageNum") %>
                              <% if(pageContext.getAttribute("activePage")!=null) { %>
                              &nbsp;</span>
                              <% } %>
                          </html:link>&nbsp;
                      </agn:ShowTableOffset></center></td></tr>
                  </table>

                  <html:link page="<%= new String("/exportwizard.do?action=" + ExportWizardAction.ACTION_QUERY + "&exportPredefID=0") %>"><html:img src="button?msg=New" border="0"/></html:link>
      
      
      
              </html:form>

<%@include file="/footer.jsp"%>
