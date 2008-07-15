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
 --%><%@ page language="java" import="org.agnitas.util.*, org.agnitas.beans.*" contentType="text/html; charset=utf-8" buffer="32kb" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<agn:CheckLogon/>

<agn:Permission token="stats.mailing"/>

<% pageContext.setAttribute("sidemenu_active", new String("Statistics")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("comparison")); %>
<% pageContext.setAttribute("agnTitleKey", new String("comparison")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Statistics")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("statsCompare")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("comparison")); %>

<%@include file="/header.jsp"%> 
<html:errors/>
          <table border="0" cellspacing="0" cellpadding="0">
<html:form action="/mailing_compare">
<html:hidden property="action"/>
<tr><td><span class="head3"><bean:message key="Mailing"/> <bean:message key="comparison"/></span></td></tr>
<tr><td colspan="3">&nbsp;</td></tr>

<tr>
    <td><bean:message key="Target"/>:&nbsp;            
            <html:select property="targetID" size="1">
                <html:option value="0"><bean:message key="All_Subscribers"/></html:option>
                <agn:ShowTable id="agntbl3" sqlStatement="<%= new String("SELECT target_id, target_shortname FROM dyn_target_tbl WHERE company_id="+AgnUtils.getCompanyID(request)) %>" maxRows="500">
                    <html:option value="<%= (String)(pageContext.getAttribute("_agntbl3_target_id")) %>"><%= pageContext.getAttribute("_agntbl3_target_shortname") %></html:option>
                </agn:ShowTable>
            </html:select>&nbsp;</td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
</tr>

<tr><td colspan="3">&nbsp;</td></tr>

<tr><td colspan="3">
<table border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td><b><bean:message key="Mailing"/>&nbsp;&nbsp;</b></td>
                    <td><b><bean:message key="Description"/>&nbsp;&nbsp;</b></td>
                    <td><div align=right><b><bean:message key="compare"/></b></div></td>
                </tr>
                <tr><td colspan="3"><hr size='1'></td></tr>
<%	EmmLayout aLayout=(EmmLayout)session.getAttribute("emm.layout");
	String dyn_bgcolor=null;
    boolean bgColor=true;
 %>                
                <agn:ShowTable id="agnTbl" sqlStatement="<%= new String("SELECT mailing_id, shortname, description FROM mailing_tbl A WHERE company_id="+AgnUtils.getCompanyID(request)+ " AND deleted<>1 AND is_template=0 and A.mailing_id in (select mailing_id from maildrop_status_tbl where status_field in ('W', 'E', 'C') and company_id = "+AgnUtils.getCompanyID(request)+ ") ORDER BY mailing_id DESC")%>" maxRows="50">
<% 	if(bgColor) {
   		dyn_bgcolor=aLayout.getNormalColor();
    	bgColor=false;
    } else {
    	dyn_bgcolor=new String("#FFFFFF");
        bgColor=true;
    }
 %>        
            <tr bgcolor="<%= dyn_bgcolor %>">
                        <td><html:link page="<%= new String("/mailing_stat.do?action=7&mailingID=" + pageContext.getAttribute("_agnTbl_mailing_id")) %>"><b><%= pageContext.getAttribute("_agnTbl_shortname") %></b></html:link>&nbsp;&nbsp;</td>
                        <td><html:link page="<%= new String("/mailing_stat.do?action=7&mailingID=" + pageContext.getAttribute("_agnTbl_mailing_id")) %>"><%= SafeString.cutLength((String)pageContext.getAttribute("_agnTbl_description"), 40) %></html:link>&nbsp;&nbsp;</td>
                        <td><div align=right><input type="checkbox" name="MailCompID_<%= pageContext.getAttribute("_agnTbl_mailing_id") %>"></div></td>
                    </tr>
                </agn:ShowTable>
                <tr><td colspan="3"><hr></td></tr>
                <tr>
                    <td colspan="2"><html:image src="button?msg=compare" border="0"/></td>
                    <td></td>
                </tr>

</table></td></tr>
</html:form>

        </table>
<%@include file="/footer.jsp"%>
