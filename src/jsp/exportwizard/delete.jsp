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
 --%><%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*,org.agnitas.web.forms.* , java.util.*" %>
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
<% pageContext.setAttribute("agnHighlightKey", new String("Export")); %>

<% int tmpExportPredefID=0;
   
if(session.getAttribute("exportWizardForm")!=null) {       
       tmpExportPredefID=((ExportWizardForm)session.getAttribute("exportWizardForm")).getExportPredefID();
}
%>

<%@include file="/header.jsp"%>
<%@include file="/messages.jsp" %>

  <html:form action="/exportwizard">
      <html:hidden property="action"/>      
      <html:hidden property="exportPredefID"/>


     

       
                <b><bean:message key="ExportWizardDeleteQuestion"/></b>
<br>
<br>
                <html:image src="button?msg=Delete" border="0"/>&nbsp;&nbsp;
                <html:link page="<%= new String("/exportwizard.do?action=" + ExportWizardAction.ACTION_LIST + "&exportPredefID=" + tmpExportPredefID) %>"><html:img src="button?msg=Cancel" border="0"/></html:link>

  </html:form>

<%@include file="/footer.jsp"%>
