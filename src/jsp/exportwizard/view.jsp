<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*" %>
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
