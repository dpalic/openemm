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
 --%><%@ page language="java" import="org.agnitas.util.*, java.util.*, java.text.*" contentType="text/html; charset=utf-8" buffer="32kb" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<agn:CheckLogon/>

<agn:Permission token="stats.mailing"/>

<%
pageContext.setAttribute("sidemenu_active", new String("Statistics"));
pageContext.setAttribute("sidemenu_sub_active", new String("AboStat"));
pageContext.setAttribute("agnTitleKey", new String("Statistics"));
pageContext.setAttribute("agnSubtitleKey", new String("Statistics"));
pageContext.setAttribute("agnNavigationKey", new String("statsRecipients"));
pageContext.setAttribute("agnHighlightKey", new String("RecipientStatistics"));
%>

<%@include file="/header.jsp"%>
<html:form action="/recipient_stats" method="post">
<html:hidden property="action"/>
  <BR>
    <table border="0" cellspacing="0" cellpadding="0">
         <tr>
             <td colspan=2>
                <span class="head3"><bean:message key="AbostatSelectInvitation"/></span>
             </td>
         </tr>

         <tr>
             <td colspan=2>
                &nbsp;
             </td>
         </tr>

        <tr>
            <td>
              <b><bean:message key="Mailinglist"/>:&nbsp;</b>
            </td>
            <td>
              <html:select property="mailingListID" size="1">
                  <html:option value="0"><bean:message key="All_Mailinglists"/></html:option>
                <agn:ShowTable id="agntbl1" sqlStatement="<%= new String("SELECT mailinglist_id, shortname FROM mailinglist_tbl WHERE company_id="+AgnUtils.getCompanyID(request)+ " ORDER BY shortname") %>">
                  <html:option value="<%= (String)pageContext.getAttribute("_agntbl1_mailinglist_id") %>"><%= pageContext.getAttribute("_agntbl1_shortname") %></html:option>
                </agn:ShowTable>
              </html:select>
            </td>
         </tr>

         <tr>
              <td>
                <b><bean:message key="Target"/>:</b>&nbsp;
              </td>
              <td>
                <html:select property="targetID" size="1">
                    <html:option value="0"><bean:message key="All_Subscribers"/></html:option>
                    <agn:ShowTable id="agntbl2" sqlStatement="<%= new String("SELECT target_id, target_shortname FROM dyn_target_tbl WHERE company_id=" + AgnUtils.getCompanyID(request)+ " and deleted=0 ORDER BY target_shortname") %>">
                        <html:option value="<%= (String) pageContext.getAttribute("_agntbl2_target_id") %>"><%= pageContext.getAttribute("_agntbl2_target_shortname") %></html:option>
                    </agn:ShowTable>
                </html:select>&nbsp;&nbsp;&nbsp;
              </td>
         </tr>
            <html:hidden property="mediaType" value="0"/>
         <tr>
             <td colspan=2>
                &nbsp;
             </td>
         </tr>

         <tr>
             <td colspan=2>
                <html:image src="button?msg=Submit" border="0"/>
             </td>
         </tr>

   </table>


</html:form>

<%@include file="/footer.jsp"%>
