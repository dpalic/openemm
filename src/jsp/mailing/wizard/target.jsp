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
 --%><%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, java.util.*, org.agnitas.beans.*, org.agnitas.target.*" contentType="text/html; charset=utf-8" %>
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

<html:form action="/mwTarget">

    <html:hidden property="action"/>
    <input type="hidden" name="removeTargetID" value="0">

    <b><font color=#73A2D0><bean:message key="MWizardStep_7_of_11"/></font></b>
    
    <br>
    <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="400" height="10" border="0">
    <br>
    
    <b><bean:message key="MlistTargetMsg"/></b><br><br>
    
    <BR> 

    <table border="0" cellspacing="0" cellpadding="0">
    
        <tr>
            <td><bean:message key="Mailinglist"/>:&nbsp;</td>
            <td> 
                <html:select property="mailing.mailinglistID" size="1">
                    <agn:ShowTable id="agntbl2" sqlStatement="<%= "SELECT mailinglist_id, shortname FROM mailinglist_tbl WHERE company_id="+AgnUtils.getCompanyID(request) %>" maxRows="100">
                        <html:option value="<%= (String)(pageContext.getAttribute("_agntbl2_mailinglist_id")) %>"><%= pageContext.getAttribute("_agntbl2_shortname") %></html:option>
                    </agn:ShowTable>
                </html:select>
            </td>
        </tr>
        <agn:ShowByPermission token="campaign.show">
                <tr>
                <td><bean:message key="Campaign"/>:&nbsp;</td>
                <td>
                    <html:select property="mailing.campaignID">
                        <html:option value="0"><bean:message key="NoCampaign"/></html:option>
                        <agn:ShowTable id="agntbl55" sqlStatement="<%= new String("SELECT campaign_id, shortname FROM campaign_tbl WHERE company_id="+AgnUtils.getCompanyID(request)+ " ORDER BY shortname") %>" maxRows="500">
                            <html:option value="<%= (String)(pageContext.getAttribute("_agntbl55_campaign_id")) %>"><%= pageContext.getAttribute("_agntbl55_shortname") %></html:option>
                        </agn:ShowTable>
                    </html:select>&nbsp;
                </td>
                </tr>
            </agn:ShowByPermission>   
        <tr>
            <td><bean:message key="openrate.measure"/>:&nbsp;</td>
            <td> 
                <html:select property="emailOnepixel" size="1">
                    <html:option value="<%= MediatypeEmail.ONEPIXEL_TOP %>"><bean:message key="openrate.top"/></html:option>
                    <html:option value="<%= MediatypeEmail.ONEPIXEL_BOTTOM %>"><bean:message key="openrate.bottom"/></html:option>
                    <html:option value="<%= MediatypeEmail.ONEPIXEL_NONE %>"><bean:message key="openrate.none"/></html:option>
                
                </html:select>
            </td>
        </tr>
    </table>
    <BR><BR>
    <agn:HibernateQuery id="targets" query="<%= "from Target where companyID="+AgnUtils.getCompanyID(request)+ " and deleted=0" %>"/>
    <b><bean:message key="Targets"/>:</b>
    <BR>
    <table border="0" cellspacing="0" cellpadding="0">
        <tr width="100%">
            <td colspan="2">
                <% if(mailing.getTargetGroups()!=null && mailing.getTargetGroups().size()>0) { 
                    System.out.println("1"); %>
                    <logic:iterate name="mailingWizardForm" property="mailing.targetGroups" id="aTarget">
                    <% System.out.println("2"); %>
                        <logic:notEmpty name="__targets">
                            <logic:iterate id="dbTarget" name="__targets">
                                <logic:equal name="dbTarget" property="id" value="<%= ((Integer)pageContext.getAttribute("aTarget")).toString() %>">
                                    <%= ((Target)pageContext.getAttribute("dbTarget")).getTargetName() %>&nbsp;<html:image src="<%= new String(((EmmLayout)session.getAttribute("emm.layout")).getBaseUrl() + "delete.gif") %>" border="0" onclick="<%= "document.mailingWizardForm.action.value='" + MailingWizardAction.ACTION_TARGET + "'; document.mailingWizardForm.removeTargetID.value='"+((Target)pageContext.getAttribute("dbTarget")).getId()+"'; document.mailingWizardForm.targetID.value='0'" %>"/><br>
                                </logic:equal>
                            </logic:iterate>
                        </logic:notEmpty>
                    </logic:iterate>
                <% } else { %>
                    <bean:message key="All_Subscribers"/><br>
                <% } %>
                <select name="targetID" size="1">
                    <option value="0" selected>---</option>
                    <% System.out.println("3"); %>
                    <logic:notEmpty name="__targets">
                        <logic:iterate id="dbTarget" name="__targets">
                            <% if(mailing.getTargetGroups()!=null && !mailing.getTargetGroups().contains(new Integer(((Target)pageContext.getAttribute("dbTarget")).getId()))) {
                            %>
                            <option value="<%= Integer.toString(((Target)pageContext.getAttribute("dbTarget")).getId()) %>"><%= ((Target)pageContext.getAttribute("dbTarget")).getTargetName() %></option>
                            <% } %>
                            <% if(mailing.getTargetGroups()==null) {
                            %>
                            <option value="<%= Integer.toString(((Target)pageContext.getAttribute("dbTarget")).getId()) %>"><%= ((Target)pageContext.getAttribute("dbTarget")).getTargetName() %></option>
                            <% } %>

                        </logic:iterate>
                    </logic:notEmpty>

                </select>
                &nbsp;<html:image src="button?msg=Add" border="0" onclick="<%= "document.mailingWizardForm.action.value='" + MailingWizardAction.ACTION_TARGET + "'" %>"/>
                <% if(mailing.getTargetGroups()!=null && mailing.getTargetGroups().size()>1) { %>
                <br><input type="hidden" name="__STRUTS_CHECKBOX_mailing.targetMode" value="0"/>
                <html:checkbox property="mailing.targetMode" value="1">&nbsp;<bean:message key="mailing.targetmode.and"/></html:checkbox>
                <% } else { %>
                <html:hidden property="mailing.targetMode"/>
                <% } %>
            </td>
        </tr>
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
                <html:image src="button?msg=Proceed"  border="0" onclick="<%= "document.mailingWizardForm.action.value='" + MailingWizardAction.ACTION_TARGET + "'" %>"/>
                &nbsp;
                <html:image src="button?msg=Finish"  border="0" onclick="<%= "document.mailingWizardForm.action.value='" + MailingWizardAction.ACTION_FINISH + "'" %>"/>
                &nbsp;
            </td>
        </tr>
    </table>         
</html:form>
<%@include file="/footer.jsp"%>
