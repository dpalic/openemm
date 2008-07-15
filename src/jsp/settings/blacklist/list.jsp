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
<agn:CheckLogon/>

<agn:Permission token="recipient.show"/>

<% pageContext.setAttribute("sidemenu_active", new String("Settings")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("Blacklist")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Blacklist")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Blacklist")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("blacklist")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Blacklist")); %>

<%@include file="/header.jsp"%>

<table border="0" cellspacing="0" cellpadding="0" width="100%">
    <tr>
        <td><b><bean:message key="E-Mail"/></b>&nbsp;</td>
        <td><center><b><bean:message key="Delete"/></b></center></td>
    </tr>
    <tr><td colspan=2><hr></td></tr>
    <form action="<html:rewrite page="/blacklist.do"/>" method="post">
    <input type="hidden" name="action" value="3">
    <tr>
        <td colspan=2><input type="text" name="newemail" size="30">&nbsp;<input type="image" src="<html:rewrite page="/button?msg=Add"/>" border="0"></td>
    </tr>
    </form>
<%	EmmLayout aLayout=(EmmLayout)session.getAttribute("emm.layout");
	String dyn_bgcolor=null;
    boolean bgColor=true;
 %>    
    <agn:ShowTable id="agntbl1" sqlStatement="<%= new String("SELECT email FROM cust_ban_tbl WHERE company_id=" + AgnUtils.getCompanyID(request) + " ORDER BY email") %>" startOffset="<%= request.getParameter("startWith") %>" maxRows="50">
<% 	if(bgColor) {
   		dyn_bgcolor=aLayout.getNormalColor();
    	bgColor=false;
    } else {
    	dyn_bgcolor=new String("#FFFFFF");
        bgColor=true;
    }
 %>        
            <tr bgcolor="<%= dyn_bgcolor %>">
        <td><%= pageContext.getAttribute("_agntbl1_email") %>&nbsp;</td>
        <td><center>
            <agn:ShowByPermission token="recipient.delete">
                <html:link page="<%= "/blacklist.do?action="+BlacklistAction.ACTION_CONFIRM_DELETE+"&delete=" + URLEncoder.encode((String)pageContext.getAttribute("_agntbl1_email")) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>delete.gif" alt="L&ouml;schen" border="0"></html:link>&nbsp;
            </agn:ShowByPermission>
        </center></td>
    </tr>
    </agn:ShowTable>
    <tr><td colspan="2"><hr size="1"></td></tr>
    <!-- Multi-Page Indizes -->
    <tr>
        <td colspan="2"><center>
            <agn:ShowTableOffset id="agntbl1" maxPages="20">
                <html:link page="<%= new String("/blacklist.do?action=1&listID=" + pageContext.getRequest().getAttribute("mailingListID") + "&startWith=" + startWith) %>">
                    <% if(activePage!=null) { %>
                        <span class="activenumber">&nbsp;<%= pageNum %>&nbsp;</span>
                    <% } else { %>
                        <%= pageNum %>
                    <% } %>
                </html:link>&nbsp;
            </agn:ShowTableOffset>
        </center></td>
    </tr>
</table>
<%@include file="/footer.jsp"%> 
