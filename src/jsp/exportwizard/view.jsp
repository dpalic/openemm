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
 --%><%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<agn:CheckLogon/>

<agn:Permission token="wizard.export"/>

<% String tmpShortname=new String("");
if(session.getAttribute("exportWizardForm")!=null) {
    tmpShortname=((ExportWizardForm)session.getAttribute("exportWizardForm")).getShortname();
    //aForm=(CouponSeriesForm)session.getAttribute("couponSeriesForm");
}
%>

<% pageContext.setAttribute("sidemenu_active", new String("Recipients")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("Export")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Export")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Export")); %>
<% pageContext.setAttribute("agnSubtitleValue", tmpShortname); %>
<% pageContext.setAttribute("agnNavigationKey", new String("subscriber_export")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("ExportWizard")); %>

<%@include file="/header.jsp"%>

<b><font color=#73A2D0><bean:message key="ExportWizStep_3_of_3"/></font></b>
<br>
    <table border="0" cellspacing="0" cellpadding="0" width="100%">

        <tr><td colspan="3">
        <iframe name="ins_status" src="<html:rewrite page="<%= new String("/exportwizard.do?action=" + ExportWizardAction.ACTION_VIEW_STATUS_WINDOW) %>"/>" ALLOWTRANSPARENCY="true" width="400" height="300" bgcolor="#73A2D0" scrolling="no" frameborder="0">
            <bean:message key="csv_no_iframe"/>
        </iframe></td></tr>
    </table>
              
<html:form action="/exportwizard">
    <html:hidden property="action" value="2"/>
    <html:hidden property="exportPredefID"/>
      
    <html:image src="button?msg=Back"  border="0" property="exp_back" value="exp_back"/>

</html:form>
              
<%@include file="/footer.jsp"%>
<% out.flush(); %>
