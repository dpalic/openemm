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

<% int index=((Integer)request.getAttribute("opIndex")).intValue(); %>

<tr>
    <td>
        <bean:message key="Mailing"/>:&nbsp;
        <html:select property="<%= "actions["+index+"].mailingID" %>" size="1">
            <agn:ShowTable id="agnTbl2" sqlStatement="<%= new String("SELECT a.mailing_id, a.shortname FROM mailing_tbl a, maildrop_status_tbl b WHERE a.company_id=" + AgnUtils.getCompanyID(request)+ " AND b.status_field='E' AND a.mailing_id=b.mailing_id")%>" maxRows="1000">
                <html:option value="<%= (String)pageContext.getAttribute("_agnTbl2_mailing_id") %>"><%= pageContext.getAttribute("_agnTbl2_shortname") %></html:option>
            </agn:ShowTable>
        </html:select>
        &nbsp;<br>
        <bean:message key="Delay"/>:&nbsp;
        <html:select property="<%= "actions["+index+"].delayMinutes" %>" size="1">
            <html:option value="0"><bean:message key="No_Delay"/></html:option>
            <html:option value="60">1&nbsp;<bean:message key="Hour"/></html:option>
            <html:option value="360">6&nbsp;<bean:message key="Hours"/></html:option>
            <html:option value="720">12&nbsp;<bean:message key="Hours"/></html:option>
            <html:option value="1440">1&nbsp;<bean:message key="Day"/></html:option>
            <html:option value="2880">2&nbsp;<bean:message key="Days"/></html:option>
            <html:option value="5760">4&nbsp;<bean:message key="Days"/></html:option>
            <html:option value="10080">7&nbsp;<bean:message key="Days"/></html:option>
        </html:select>
	<html:link page="<%= new String("/action.do?action=" + EmmActionAction.ACTION_SAVE + "&deleteModule=" + index) %>"><html:img src="button?msg=Delete" border="0"/></html:link>
    </td>
</tr>
