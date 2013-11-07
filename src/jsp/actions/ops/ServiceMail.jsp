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
 --%><%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.actions.ops.*, java.util.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<% int index=((Integer)request.getAttribute("opIndex")).intValue();
   ServiceMail op=(ServiceMail)request.getAttribute("op"); %>  
   
<tr>
    <td>
        <bean:message key="To"/>:&nbsp;
	<html:text property="<%= new String("actions["+index+"].toAdr") %>"/>
    </td>
</tr>
<tr>
    <td>
        <bean:message key="Subject"/>:&nbsp;
        <html:text property="<%= new String("actions["+index+"].subjectLine") %>"/>
    </td>
</tr>
<tr>
    <td>
        <bean:message key="Text_Version"/>:<br>
        <html:textarea property="<%= new String("actions["+index+"].textMail") %>" rows="10" cols="70"/>
    </td>
</tr>
<tr>
    <td>
        <bean:message key="HTML_Version"/>:<br>
        <html:textarea property="<%= new String("actions["+index+"].htmlMail") %>" rows="10" cols="70"/>
    </td>
</tr>
<tr>
    <td>
        <bean:message key="Format"/>:&nbsp;
        <html:select property="<%= new String("actions["+index+"].mailtype") %>" size="1">
            <html:option value="0"><bean:message key="Text"/></html:option>
            <html:option value="1"><bean:message key="HTML"/></html:option>
        </html:select>
        &nbsp;<br>
    </td>
</tr>
<tr>
    <td>
        &nbsp;<br>
	<html:link page="<%= new String("/action.do?action=" + EmmActionAction.ACTION_SAVE + "&deleteModule=" + index) %>"><html:img src="button?msg=Delete" border="0"/></html:link>
    </td>
</tr>
