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
 --%><%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, java.util.*, org.agnitas.beans.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<agn:CheckLogon/>
<% MailingWizardForm aForm=null;
   aForm=(MailingWizardForm)session.getAttribute("mailingWizardForm");
   Mailing mailing=aForm.getMailing();
%>

<agn:Permission token="mailing.show"/>

<c:set var="sidemenu_active" value="Mailings" />
<c:set var="sidemenu_sub_active" value="New_Mailing" />
<c:set var="agnNavigationKey" value="MailingNew" />
<c:set var="agnHighlightKey" value="New_Mailing" />
<c:set var="agnTitleKey" value="Mailing" /> 
<c:set var="agnSubtitleKey" value="Mailing" /> 

<c:set var="ACTION_NEW" value="<%= MailingBaseAction.ACTION_NEW %>" />
<c:set var="ACTION_START" value="<%= MailingWizardAction.ACTION_START %>" />

<%@include file="/header.jsp"%>
<%@include file="/messages.jsp" %>

<html:form action="/mwStart" styleId="wizardForm">
    <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="400" height="10" border="0">
    <br>
    <b><bean:message key="NewMailingMethod"/>:</b>

    <BR>
    <BR>
    <BR>
    <html:link page="/mailingbase.do?action=${ACTION_NEW}&mailingID=0&isTemplate=false" style="color: #73A2D0;"><b><bean:message key="Normal"/>:</b> <bean:message key="NoWizard"/>.</html:link>
    <BR>
    <BR>
    <html:link page="/mwStart.do?action=${ACTION_START}" style="color: #73A2D0;"><b><bean:message key="Wizard"/>:</b> <bean:message key="WizardDescription"/>.</html:link>
    <BR>
    <BR>

<% // wizard navigation: %>
    <br>
    
    <script language="JavaScript" type="text/javascript">
    	function submitUseWizard(useWizard) {
    		if(useWizard) {
    			action = "${ACTION_START}";
    			actionForward = "start";	
    		} else {
    			action = "withoutWizard";
    			actionForward = "withoutWizard";
    		}
    		
    		document.getElementById('hidden_action').value = action;
    		document.getElementById('hidden_action_forward').value = actionForward;
    		
    		document.getElementById('wizardForm').submit();
    	}
    </script>
    
    <input type="hidden" id="hidden_action" name="action" value="" />
    <input type="hidden" id="hidden_action_forward" name="action_forward" value="" />
    <input type="hidden" name="mailingID" value="0" />
    <input type="hidden" name="isTemplate" value="false" />
        
     <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td>&nbsp;</td>
            <td align="right">
                &nbsp;
                <html:image property="action_forward" value="withoutWizard" src="button?msg=Normal" border="0" onclick="submitUseWizard(0)" />
                <html:image property="action_forward" value="start" src="button?msg=Wizard" border="0" onclick="submitUseWizard(1)" />
                &nbsp;
            </td>
        </tr>
    </table>         
</html:form>
<%@include file="/footer.jsp"%>
