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
 --%><%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.actions.*" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="mailing.content.show"/>

<% int tmpMailingID=0;
    String tmpShortname=new String("");
    TrackableLinkForm aForm=null;
    if(request.getAttribute("trackableLinkForm")!=null) {
        aForm=(TrackableLinkForm)request.getAttribute("trackableLinkForm");
        tmpMailingID=aForm.getMailingID();
        tmpShortname=aForm.getShortname();
    }
%>

<logic:equal name="trackableLinkForm" property="isTemplate" value="true">
    <% // template navigation:
        pageContext.setAttribute("sidemenu_active", new String("Templates"));
        pageContext.setAttribute("sidemenu_sub_active", new String("none"));
        pageContext.setAttribute("agnNavigationKey", new String("templateView"));
        pageContext.setAttribute("agnHighlightKey", new String("Trackable_Links"));
        pageContext.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
        pageContext.setAttribute("agnTitleKey", new String("Trackable_Link"));
        pageContext.setAttribute("agnSubtitleKey", new String("Trackable_Link"));
        pageContext.setAttribute("agnSubtitleValue", tmpShortname);
    %>
</logic:equal>

 <logic:equal name="trackableLinkForm" property="isTemplate" value="false">
     <% // mailing navigation:
         pageContext.setAttribute("sidemenu_active", new String("Mailings"));
         pageContext.setAttribute("sidemenu_sub_active", new String("none"));
         pageContext.setAttribute("agnNavigationKey", new String("mailingView"));
         pageContext.setAttribute("agnHighlightKey", new String("Trackable_Links"));
         pageContext.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
         pageContext.setAttribute("agnTitleKey", new String("Trackable_Link"));
         pageContext.setAttribute("agnSubtitleKey", new String("Trackable_Link"));
         pageContext.setAttribute("agnSubtitleValue", tmpShortname);
     %>
 </logic:equal>

<%@include file="/header.jsp"%>

<html:errors/>

<html:form action="/tracklink">
    <html:hidden property="mailingID"/>
    <html:hidden property="linkID"/>
    <html:hidden property="action"/>
    <table border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td><bean:message key="URL"/>:&nbsp;</td>
            <td><bean:write name="trackableLinkForm" property="linkUrl"/></td>
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
                <agn:HibernateQuery id="action" query="<%= "from EmmAction where companyID="+AgnUtils.getCompanyID(request)+" and type<>"+EmmAction.TYPE_FORM %>">
                    <html:option value="${action.getId()}">${action.getShortname()}</html:option>
                </agn:HibernateQuery>
            </html:select>
            </td>
        </tr>
    </table>
    <br>
    <html:image src="button?msg=Save"/>&nbsp;&nbsp;<html:link page="<%= new String("/tracklink.do?action=" + TrackableLinkAction.ACTION_LIST + "&mailingID=" + tmpMailingID) %>"><html:img src="button?msg=Cancel" border="0"/></html:link>
</html:form>
<%@include file="/footer.jsp"%>
