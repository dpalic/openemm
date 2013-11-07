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
 --%><%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.web.forms.*, org.agnitas.beans.*" errorPage="/error.jsp"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="admin.show"/>

<% pageContext.setAttribute("sidemenu_active", new String("Settings")); %>          <!-- links Button -->
<% pageContext.setAttribute("sidemenu_sub_active", new String("Admins")); %>  <!-- links unter Button -->
<% pageContext.setAttribute("agnTitleKey", new String("Admins")); %>          <!-- Titelleiste -->
<% pageContext.setAttribute("agnSubtitleKey", new String("Admins")); %>       <!-- ueber rechte Seite -->
<% pageContext.setAttribute("agnNavigationKey", new String("admins")); %>         <!-- welche Reiterleiste -->
<% pageContext.setAttribute("agnHighlightKey", new String("Overview")); %>          <!-- welcher Reiter -->

<%@include file="/header.jsp"%>
<%@include file="/messages.jsp" %>

              <table border="0" cellspacing="0" cellpadding="0" width="100%">
                <tr>
                    <td><span class="head3"><bean:message key="User_Name"/>&nbsp;&nbsp;</span></td>
                    <td><span class="head3"><bean:message key="Description"/>&nbsp;&nbsp;</span></td>
                    <td><span class="head3"><bean:message key="Account"/>&nbsp;&nbsp;</span></td>
                    <td><span class="head3">&nbsp;</span></td>
                </tr>

                <tr><td colspan="4"><hr></td></tr>
<%	EmmLayout aLayout=(EmmLayout)session.getAttribute("emm.layout");
	String dyn_bgcolor=null;
    boolean bgColor=true;
 %>                  
                <agn:ShowTable id="agnTbl" sqlStatement="<%= new String("SELECT adm.admin_id, adm.username, adm.fullname, comp.shortname, adm.company_id FROM admin_tbl adm, company_tbl comp WHERE (adm.company_id="+AgnUtils.getCompanyID(request)+ " OR adm.company_id IN (SELECT company_id FROM company_tbl WHERE creator_company_id="+ AgnUtils.getCompanyID(request)+")) AND status<>'deleted' AND comp.company_ID=adm.company_id ORDER BY adm.username")%>" startOffset="<%= request.getParameter("startWith") %>" maxRows="50">
<% 	if(bgColor) {
   		dyn_bgcolor=aLayout.getNormalColor();
    	bgColor=false;
    } else {
    	dyn_bgcolor=new String("#FFFFFF");
        bgColor=true;
    }
 %>        
            <tr bgcolor="<%= dyn_bgcolor %>">
                        <td><html:link page="<%= new String("/admin.do?action=" + AdminAction.ACTION_VIEW + "&adminID=" + pageContext.getAttribute("_agnTbl_admin_id")) %>"><b><%= pageContext.getAttribute("_agnTbl_username") %></b></html:link>&nbsp;&nbsp;</td>
                        <td><%= SafeString.cutLength((String)pageContext.getAttribute("_agnTbl_fullname"), 40) %>&nbsp;</td>
                        <td><%= SafeString.cutLength((String)pageContext.getAttribute("_agnTbl_shortname"), 40) %>&nbsp;&nbsp;&nbsp;</td>
                        <td>
                            <agn:ShowByPermission token="admin.delete">
                            <logic:notEqual name="adminID" scope="session" value="<%= (String)pageContext.getAttribute("_agnTbl_admin_id") %>">
                            <html:link page="<%= new String("/admin.do?action=" + AdminAction.ACTION_CONFIRM_DELETE + "&previousAction=" + AdminAction.ACTION_LIST + "&adminID=" + pageContext.getAttribute("_agnTbl_admin_id")) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>delete.gif" alt="<bean:message key="Delete"/>" border="0"></html:link>
                            </logic:notEqual>
                            </agn:ShowByPermission>
                            <html:link page="<%= new String("/admin.do?action=" + AdminAction.ACTION_VIEW + "&adminID=" + pageContext.getAttribute("_agnTbl_admin_id")) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>bearbeiten.gif" alt="<bean:message key="Edit"/>" border="0"></html:link>
                        </td>
                    </tr>
                </agn:ShowTable>
                <tr><td colspan="4"><hr></td></tr>
                <tr><td colspan="4"><center>
                     <agn:ShowTableOffset id="agnTbl" maxPages="10">
                        <html:link page="<%= new String("/admin.do?action=" + AdminAction.ACTION_LIST + "&startWith=" + pageContext.getAttribute("startWith")) %>">
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

<%@include file="/footer.jsp"%>
