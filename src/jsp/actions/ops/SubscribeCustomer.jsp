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
 --%><%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, java.util.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<%
int index=((Integer)request.getAttribute("opIndex")).intValue();
String checkbox=null;
%>

<tr>
    <td>
        <% checkbox=new String("actions["+index+"].doubleOptIn"); %>
        <html:hidden property="<%= new String("__STRUTS_CHECKBOX_"+checkbox) %>" value="false"/>
        <html:checkbox property="<%= checkbox %>"><bean:message key="UseDblOptIn"/></html:checkbox>
    </td>
</tr>
                        
<tr>
    <td>
        <% checkbox=new String("actions["+index+"].doubleCheck"); %>
        <html:hidden property="<%= new String("__STRUTS_CHECKBOX_"+checkbox) %>" value="false"/>
        <html:checkbox property="<%= checkbox %>"><bean:message key="import.doublechecking"/></html:checkbox>
    </td>
</tr>
                        
<tr>
    <td>
        <bean:message key="import.keycolumn"/>:&nbsp;
        <html:select property="<%= new String("actions["+index+"].keyColumn") %>" size="1">
            <agn:ShowColumnInfo id="tbl">
                <html:option value="<%= (String)pageContext.getAttribute("_tbl_column_name") %>"><%= pageContext.getAttribute("_tbl_shortname") %></html:option>
            </agn:ShowColumnInfo>
        </html:select>
        &nbsp;<br>
	<html:link page="<%= new String("/action.do?action=" + EmmActionAction.ACTION_SAVE + "&deleteModule=" + index) %>"><html:img src="button?msg=Delete" border="0"/></html:link>
    </td>
</tr>
