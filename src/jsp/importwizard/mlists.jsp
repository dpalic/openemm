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
 --%><%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.Admin" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<agn:CheckLogon/>

<agn:Permission token="wizard.import"/>

<% pageContext.setAttribute("sidemenu_active", new String("Recipient")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("csv_upload")); %>
<% pageContext.setAttribute("agnTitleKey", new String("UploadSubscribers")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("UploadSubscribers")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("subscriber_import")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("ImportWizard")); %>

<%@include file="/header.jsp"%>


<html:form action="/importwizard">
    <html:hidden property="action"/>

    <b><font color=#73A2D0><bean:message key="ImportWizStep_6_of_7"/></font></b>
    <br>
    
    <table border="0" cellspacing="0" cellpadding="0" width="100%">
    
        <tr>
            <td colspan=3>
                <br><span class="head3"><bean:message key="SubscribeLists"/>:</span><br><br>
            </td>
        </tr>

        <agn:ShowTable id="agnTbl" sqlStatement="<%= new String("SELECT mailinglist_id, shortname FROM mailinglist_tbl WHERE company_id=" + AgnUtils.getCompanyID(request)+ " ORDER BY shortname")%>" maxRows="1000">
            <tr>
                <td colspan="3"><input type="checkbox" name="agn_mlid_<%= pageContext.getAttribute("_agnTbl_mailinglist_id") %>"> <%= pageContext.getAttribute("_agnTbl_shortname") %> (ID <%= pageContext.getAttribute("_agnTbl_mailinglist_id") %>)</td>
            </tr>
        </agn:ShowTable>
        
        <tr><td colspan="3"><hr>
        <html:image src="button?msg=Back"  border="0" property="mlists_back" value="mlists_back"/>
        &nbsp;&nbsp;&nbsp;
        <html:image src="button?msg=Proceed" border="0"/></td></tr>
        
    </table>                      

</html:form>                
          


<%@include file="/footer.jsp"%>
