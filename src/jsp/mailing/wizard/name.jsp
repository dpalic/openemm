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

<agn:CheckLogon/>
<% MailingWizardForm aForm=null;
    aForm=(MailingWizardForm)session.getAttribute("mailingWizardForm");
    Mailing mailing=aForm.getMailing();
%>

<agn:Permission token="mailing.show"/>

<%
    // mailing navigation:
    pageContext.setAttribute("sidemenu_active", new String("Mailings"));
    pageContext.setAttribute("sidemenu_sub_active", new String("New_Mailing"));
    pageContext.setAttribute("agnNavigationKey", new String("MailingWizard"));
    pageContext.setAttribute("agnHighlightKey", new String("MailingWizard"));
    pageContext.setAttribute("agnTitleKey", new String("Mailing"));
    pageContext.setAttribute("agnSubtitleKey", new String("Mailing"));
    pageContext.setAttribute("agnSubtitleValue", mailing.getShortname());
%>

<%@include file="/header.jsp"%>
<%@include file="/messages.jsp" %>

<html:form styleId="mwiz" action="/mwName" focus="shortname">

    <html:hidden property="action"/>
    
    <b><font color=#73A2D0><bean:message key="MWizardStep_1_of_11"/></font></b>
    
    <br><br> 
    <div class="tooltiphelp" id="mailingnamedescription"><b> <bean:message key="Name"/>:</b></div>
				<script type="text/javascript">
					var hb1 = new HelpBalloon({
						dataURL: 'help_${helplanguage}/mailingwizard/step_01/MailingNameDescription.xml' 		
						});
						$('filename').appendChild(hb1.icon); 
				</script>   
    <br>    
    <table border="0" cellspacing="0" cellpadding="0">
        <tr> 
            <td><bean:message key="Name"/>:&nbsp;</td>
            <td> 
                <html:text property="mailing.shortname" maxlength="99" size="32"/>
            </td>
        </tr>

        <tr> 
            <td><bean:message key="Description"/>:&nbsp;</td>
            <td> 
                <html:textarea property="mailing.description" rows="3" cols="32"/>
            </td>
        </tr>
    </table>
    <BR>


    <% // wizard navigation: %>
    <br>
    <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td>&nbsp;</td>
            <td align="right">
                &nbsp;
                <html:image src="button?msg=Back"  border="0" onclick="document.mailingWizardForm.action.value='previous'"/>
                &nbsp;
                <html:image src="button?msg=Proceed"  border="0"  onclick="<%= "document.mailingWizardForm.action.value='" + MailingWizardAction.ACTION_NAME + "'" %>"/>
                &nbsp;
            </td>
        </tr>
    </table>         
</html:form>
<%@include file="/footer.jsp"%>
