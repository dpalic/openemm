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
 --%><%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*, java.util.*, org.apache.struts.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>
<% 	int tmpMailingID=0;
   	MailingWizardForm aForm=null;
    // String permToken=null; wird nicht benutzt
	String permToken=null;
   
   String tmpShortname=new String("");
   if((aForm=(MailingWizardForm)session.getAttribute("mailingWizardForm"))!=null) {
       tmpMailingID=aForm.getMailing().getId();
       tmpShortname=aForm.getMailing().getShortname();
	}	
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
    pageContext.setAttribute("agnSubtitleValue", tmpShortname);
%>


<%@include file="/header.jsp"%>

<html:errors/>

<html:form action="/mwFinish" focus="shortname">
	<html:hidden property="action"/>
    
    <b><font color=#73A2D0><bean:message key="MWizardStep_11_of_11"/></font></b>

    <br>
    <table border="0" cellspacing="0" cellpadding="0">
    
    <br>
    <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="400" height="10" border="0">
    <br>
	<span class="head3"><bean:message key="MailingWizardReadyMsg"/>!</span><BR><BR><b><bean:message key="TestAdminDeliveryMsg"/>:</b>
    
	<br>
                          <br><br><li><html:link page="<%= new String("/mailingsend.do?action=" + MailingSendAction.ACTION_PREVIEW_SELECT + "&mailingID=" + tmpMailingID) %>"><b>
                              <bean:message key="Preview"/>
                          </b></html:link></li><br>
                          <agn:ShowByPermission token="mailing.send.admin">
                              <br><li><html:link page="<%= new String("/mailingsend.do?action=" + MailingSendAction.ACTION_SEND_ADMIN + "&mailingID=" + tmpMailingID) %>"><b>
                                  <bean:message key="MailingTestAdmin"/>
                              </b></html:link></li><br>
                          </agn:ShowByPermission>
                          <agn:ShowByPermission token="mailing.send.test">
                              <li><html:link page="<%= new String("/mailingsend.do?action=" + MailingSendAction.ACTION_SEND_TEST + "&mailingID=" + tmpMailingID) %>"><b>
                                  <bean:message key="MailingTestDistrib"/>
                              </b></html:link></li>
                          </agn:ShowByPermission>
  </table>
<br><br>
<b><bean:message key="ClickFinishMsg"/>.</b>
<br>

    <% // wizard navigation: %>
    <br>
    <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td>&nbsp;</td>
            <td align="right">
                &nbsp;    
                <html:link page="<%=new String("/mailingbase.do?action=" + MailingBaseAction.ACTION_VIEW) + "&mailingID=" + tmpMailingID%>"><html:img src="button?msg=Finish" border="0"/></html:link>
                &nbsp;
            </td>
        </tr>
    </table>             
</html:form>
<%@include file="/footer.jsp"%>
