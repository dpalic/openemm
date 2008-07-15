<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, java.util.*" %>
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

<% pageContext.setAttribute("sidemenu_active", new String("Recipient")); %>
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
<html:errors/>
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
