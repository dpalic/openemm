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
 --%>
<%@ page language="java" import="org.agnitas.web.EmmActionAction"
         contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.util.AgnUtils" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<% int index = ((Integer) request.getAttribute("opIndex")).intValue(); %>

<div class="send_mailing_action_box">
    <label><bean:message key="Mailing"/>:</label>
    <html:select property="actions[${opIndex}].mailingID" size="1">
        <agn:ShowTable id="agnTbl2"
                       sqlStatement='<%= new String(\"SELECT a.mailing_id, a.shortname FROM mailing_tbl a, maildrop_status_tbl b WHERE a.company_id=\" + AgnUtils.getCompanyID(request)+ \" AND a.deleted <> 1 AND b.status_field=\'E\' AND a.mailing_id=b.mailing_id AND a.mailing_type=1\")%>'
                       maxRows="1000">
            <html:option
                    value='<%= (String)pageContext.getAttribute(\"_agnTbl2_mailing_id\") %>'><%= pageContext.getAttribute("_agnTbl2_shortname") %>
            </html:option>
        </agn:ShowTable>
    </html:select>
</div>
<div class="send_mailing_action_box">
    <label><bean:message key="action.Delay"/>:&nbsp;</label>
    <html:select property='<%= \"actions[\"+index+\"].delayMinutes\" %>' size="1">
        <html:option value="0"><bean:message key="action.No_Delay"/></html:option>
        <html:option value="60">1&nbsp;<bean:message key="report.Hour"/></html:option>
        <html:option value="360">6&nbsp;<bean:message key="action.Hours"/></html:option>
        <html:option value="720">12&nbsp;<bean:message key="action.Hours"/></html:option>
        <html:option value="1440">1&nbsp;<bean:message key="statistic.Day"/></html:option>
        <html:option value="2880">2&nbsp;<bean:message key="statistic.Days"/></html:option>
        <html:option value="5760">4&nbsp;<bean:message key="statistic.Days"/></html:option>
        <html:option value="10080">7&nbsp;<bean:message key="statistic.Days"/></html:option>
    </html:select>
</div>
<agn:ShowByPermission token="actions.change">
    <div class="maildetail_button">
        <a href="<html:rewrite page='<%= new String("/action.do?action=" + EmmActionAction.ACTION_SAVE + "&deleteModule=" + index) %>'/>"><span><bean:message
                key="button.Delete"/></span></a>
    </div>
</agn:ShowByPermission>
