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
<%
   int tmpMailingID=0;
   MailingWizardForm aForm=null;
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
<%@include file="/messages.jsp" %>

<html:form action="/mwLink" focus="Description" enctype="application/x-www-form-urlencoded">
    <html:hidden property="action"/>
 
    <b><font color=#73A2D0><bean:message key="MWizardStep_9_of_11"/></font></b>
    
    <br>
    <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="400" height="10" border="0">
    <br>
    <b><bean:message key="ChooseThenPressSave"/>.</b>
    <br><br>

                <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td><bean:message key="URL"/>:&nbsp;</td>
                        <td><bean:write name="mailingWizardForm" property="linkUrl"/></td>
                    </tr>
                    <tr><td colspan="2"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" height="2" border="0"></td></tr>
                    <tr>
                        <td><bean:message key="Description"/>:&nbsp;</td>
                        <td><html:text property="linkName" size="52" maxlength="99"/></td>
                    </tr>
                    <tr><td colspan="2"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" height="2" border="0"></td></tr>
                    <tr>
                        <td><bean:message key="Trackable"/>:&nbsp;</td>
                        <td><html:select property="trackable">
                                <html:option value="0"><bean:message key="Not_Trackable"/></html:option>
                                <html:option value="1"><bean:message key="Only_Text_Version"/></html:option>
                                <html:option value="2"><bean:message key="Only_HTML_Version"/></html:option>
                                <html:option value="3"><bean:message key="Text_and_HTML_Version"/></html:option>
                            </html:select></td>
                     </tr>
                     <tr><td colspan="2"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" height="2" border="0"></td></tr>
                     
                     <tr>
                        <td><bean:message key="Action"/>:&nbsp;</td>
                        <td><html:select property="linkAction" size="1">
                                <html:option value="0"><bean:message key="No_Action"/></html:option>
                                <agn:ShowTable id="agntbl2" sqlStatement="<%= new String("SELECT action_id, shortname FROM rdir_action_tbl WHERE company_id="+AgnUtils.getCompanyID(request)+" AND action_type<>1") %>" maxRows="500">
                                    <html:option value="<%= (String)pageContext.getAttribute("_agntbl2_action_id") %>"><%= pageContext.getAttribute("_agntbl2_shortname") %></html:option>
                                </agn:ShowTable>
                            </html:select>
                         </td>
                      </tr>
                      <tr><td colspan="2"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" height="2" border="0"></td></tr>
                      <tr><td></td><td><html:image src="button?msg=Save" border="0" property="mlink_save" onclick="document.mailingWizardForm.action.value='link'"/></td></tr>
                      
                  </table>
    
    <BR>
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
                <html:image src="button?msg=Proceed"  border="0" onclick="<%= "document.mailingWizardForm.action.value='link'" %>"/>
                &nbsp;
                <html:image src="button?msg=Skip"  border="0" onclick="<%= "document.mailingWizardForm.action.value='skip'" %>"/>
                &nbsp;
                <html:image src="button?msg=Finish"  border="0" onclick="<%= "document.mailingWizardForm.action.value='" + MailingWizardAction.ACTION_FINISH + "'" %>"/>
                &nbsp;
            </td>
        </tr>
    </table>             
    
</html:form>
<%@include file="/footer.jsp"%>
