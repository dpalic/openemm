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
 --%><%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="settings.show"/>	 

<%
pageContext.setAttribute("sidemenu_active", new String("Settings"));
pageContext.setAttribute("sidemenu_sub_active", new String("FormsOfAddress"));
pageContext.setAttribute("agnNavigationKey", new String("Salutations"));
pageContext.setAttribute("agnHighlightKey", new String("Overview"));
pageContext.setAttribute("agnSubtitleKey", new String("FormsOfAddress"));
pageContext.setAttribute("agnTitleKey", new String("FormsOfAddress"));
%>

<%@include file="/header.jsp"%>
<%@include file="/messages.jsp" %>

<table border="0" cellspacing="0" cellpadding="0" width="100%">
    <tr><td colspan="3"><span class="head3"><bean:message key="FormsOfAddress"/>:</span><br><br></td></tr>
    <tr><td><b>ID</b>&nbsp;&nbsp;</td><td><b><bean:message key="FormOfAddress"/></b>&nbsp;&nbsp;</td><td><b>&nbsp;</b></td></tr>
    <tr><td colspan="3"><hr><center></td></tr>
<%	EmmLayout aLayout=(EmmLayout)session.getAttribute("emm.layout");
	String dyn_bgcolor=null;
    boolean bgColor=true;
 %>    
    <agn:ShowTable id="agnTbl" sqlStatement="<%= new String("select title_id, description from title_tbl WHERE company_id = " + AgnUtils.getCompanyID(request)) %>" maxRows="100">
<% 	if(bgColor) {
   		dyn_bgcolor=aLayout.getNormalColor();
    	bgColor=false;
    } else {
    	dyn_bgcolor=new String("#FFFFFF");
        bgColor=true;
    }
 %>        
            <tr bgcolor="<%= dyn_bgcolor %>">
            <td><%= pageContext.getAttribute("_agnTbl_title_id") %></td>
            <td><html:link page="<%= new String("/salutation.do?action=" + SalutationAction.ACTION_VIEW + "&salutationID=" + pageContext.getAttribute("_agnTbl_title_id")) %>"><%= pageContext.getAttribute("_agnTbl_description") %>&nbsp;&nbsp;</html:link>&nbsp;&nbsp;</td>
            <td>
                <html:link page="<%= new String("/salutation.do?action=" + SalutationAction.ACTION_CONFIRM_DELETE + "&salutationID=" + pageContext.getAttribute("_agnTbl_title_id")) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>delete.gif" alt="<bean:message key="Delete"/>" border="0"></html:link>
                <html:link page="<%= new String("/salutation.do?action=" + SalutationAction.ACTION_VIEW + "&salutationID=" + pageContext.getAttribute("_agnTbl_title_id")) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>revise.gif" alt="<bean:message key="Edit"/>" border="0"></html:link>
            </td>
        </tr>
    </agn:ShowTable>
</table>
<%@include file="/footer.jsp"%>
