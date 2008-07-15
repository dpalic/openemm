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

<agn:Permission token="profileField.show"/>

<% pageContext.setAttribute("sidemenu_active", new String("Settings")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("Profile_DB")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Profile_Database")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Profile_Database")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("profiledb")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Overview")); %>

<%@include file="/header.jsp"%>

<html:errors/>

<table border="0" cellspacing="0" cellpadding="0" width="100%">
    <tr>
        <td><span class="head3"><bean:message key="FieldName"/>&nbsp;&nbsp;</span></td>
        <td><span class="head3"><bean:message key="FieldNameDB"/>&nbsp;&nbsp;</span></td>
        <td><span class="head3"><bean:message key="Type"/>&nbsp;&nbsp;</span></td>
        <td><span class="head3"><bean:message key="Length"/>&nbsp;&nbsp;</span></td>
        <td><span class="head3"><bean:message key="Default_Value"/>&nbsp;&nbsp;</span></td>
        <td><span class="head3"><bean:message key="NullAllowed"/>&nbsp;&nbsp;</span></td>
        <td><span class="head3">&nbsp;</span></td>
    </tr>

    <tr><td colspan="7"><hr></td></tr>
<%	EmmLayout aLayout=(EmmLayout)session.getAttribute("emm.layout");
	String dyn_bgcolor=null;
    boolean bgColor=true;
 %>
    <agn:ShowColumnInfo id="agnTbl" table="<%= AgnUtils.getCompanyID(request) %>" hide="change_date, creation_date, title, datasource_id, email, firstname, lastname, gender, mailtype, customer_id, timestamp, bounceload">
<% 	if(bgColor) {
   		dyn_bgcolor=aLayout.getNormalColor();
    	bgColor=false;
    } else {
    	dyn_bgcolor=new String("#FFFFFF");
        bgColor=true;
    }
 %>        
            <tr bgcolor="<%= dyn_bgcolor %>"> <!-- MailingBaseAction.ACTION_VIEW -->
            <td><html:link page="<%= new String("/profiledb.do?action=" + ProfileFieldAction.ACTION_VIEW + "&fieldname=" + pageContext.getAttribute("_agnTbl_column_name")) %>"><b><%= pageContext.getAttribute("_agnTbl_shortname") %></b></html:link>&nbsp;&nbsp;</td>
            <td><html:link page="<%= new String("/profiledb.do?action=" + ProfileFieldAction.ACTION_VIEW + "&fieldname=" + pageContext.getAttribute("_agnTbl_column_name")) %>"><%= pageContext.getAttribute("_agnTbl_column_name") %></html:link>&nbsp;&nbsp;</td>
            <td><bean:message key="<%= "fieldType."+pageContext.getAttribute("_agnTbl_data_type") %>"/>&nbsp;&nbsp;</td>
            <td><div align="right"><% if(((String)pageContext.getAttribute("_agnTbl_data_type")).equals("VARCHAR")) { %><%=pageContext.getAttribute("_agnTbl_data_length")%><% } %>&nbsp;&nbsp;&nbsp;&nbsp;</div></td>
            <td><div align="right"><%=pageContext.getAttribute("_agnTbl_data_default").toString().trim()%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div></td>
            <td>
            	<% 
            	Integer isNullable = (Integer) pageContext.getAttribute("_agnTbl_nullable");
            	if(isNullable != null && isNullable.intValue() == 1) { %>
            		<bean:message key="Yes"/>
            	<% } else { %>
            		<bean:message key="No"/>
            	<% } %>
            </td>
            <td>
                <html:link page="<%= new String("/profiledb.do?action=" + ProfileFieldAction.ACTION_CONFIRM_DELETE + "&fieldname=" + pageContext.getAttribute("_agnTbl_column_name")) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>delete.gif" alt="<bean:message key="Delete"/>" border="0"></html:link>&nbsp;
                <html:link page="<%= new String("/profiledb.do?action=" + ProfileFieldAction.ACTION_VIEW + "&fieldname=" + pageContext.getAttribute("_agnTbl_column_name")) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>bearbeiten.gif" alt="<bean:message key="Edit"/>" border="0"></html:link>
            </td>
        </tr>

    </agn:ShowColumnInfo>

    <tr><td colspan="7"><hr></td></tr>

</table>
<%@include file="/footer.jsp"%>
