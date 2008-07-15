<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*" %>
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

<b><font color=#73A2D0><bean:message key="ImportWizStep_7_of_7"/></font></b>
<br>
    <table border="0" cellspacing="0" cellpadding="0" width="100%">

        <tr><td colspan="3">
        <iframe name="ins_status" src="<html:rewrite page="<%= new String("/importwizard.do?action=" + ImportWizardAction.ACTION_VIEW_STATUS_WINDOW) %>"/>" ALLOWTRANSPARENCY="true" width="400" height="520" bgcolor="#73A2D0" scrolling="no" frameborder="0">
            <bean:message key="csv_no_iframe"/>
        </iframe></td></tr>
    </table>
<%@include file="/footer.jsp"%>
<% out.flush(); %>
