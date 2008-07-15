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
 --%><%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="settings.show"/>

<%
int tmpSalutationID=0;
String tmpShortname=new String("");

if(request.getAttribute("salutationForm")!=null) {
    tmpSalutationID=((SalutationForm)request.getAttribute("salutationForm")).getSalutationID();
    tmpShortname=((SalutationForm)request.getAttribute("salutationForm")).getShortname();
}

pageContext.setAttribute("sidemenu_active", new String("Settings"));
pageContext.setAttribute("sidemenu_sub_active", new String("FormsOfAddress"));
pageContext.setAttribute("agnNavigationKey", new String("Salutation"));
pageContext.setAttribute("agnHighlightKey", new String("NewFormOfAddress"));
pageContext.setAttribute("agnSubtitleKey", new String("FormOfAddress"));
pageContext.setAttribute("agnTitleKey", new String("FormsOfAddress"));
pageContext.setAttribute("agnSubtitleValue", tmpShortname);
pageContext.setAttribute("agnNavHrefAppend", new String("&salutationID="+tmpSalutationID));
%>

<%@include file="/header.jsp"%>

<html:errors/>
<table border="0" cellspacing="0" cellpadding="0" width="100%">
<html:form action="/salutation">
<html:hidden property="salutationID"/>
<html:hidden property="action"/>
<% if(tmpSalutationID!=0) { %>
    <tr>
        <td><b>ID:</b></td>
        <td><b><%= tmpSalutationID %></b></td>
        <td colspan=2></td>
    </tr>
<% } %>
    <tr><td colspan=4><br></td></tr>
    <tr>
        <td><b><bean:message key="Description"/>:&nbsp;</b></td>
        <td colspan=2>
            <html:text property="shortname" size="32"/>
        </td>
        <td></td>
    </tr>
    <tr><td colspan=4 align=center><hr size="1" noshade></td></tr>
    <tr>
        <td><b><bean:message key="Salutation"/>:&nbsp;</b></td>
        <td colspan=2>
            <table>
                <tr>
                    <td>GENDER=0 (<bean:message key="Male"/>):<br><html:text property="salMale" size="32"/></td>
                </tr>
                <tr>
                    <td>GENDER=1 (<bean:message key="Female"/>):<br><html:text property="salFemale" size="32"/></td>
                </tr>
                <tr>
                    <td>GENDER=2 (<bean:message key="Unknown"/>):<br><html:text property="salUnknown" size="32"/></td>
                </tr>
            </table>
        </td>
        <td></td>
    </tr>
    <tr><td colspan=4 align=center><hr noshade></td></tr>
    <tr>
        <td colspan="4">
            <html:image src="button?msg=Save" border="0" property="save" value="save"/>&nbsp;&nbsp;<html:link page="<%= new String("/salutation.do?action=" + SalutationAction.ACTION_LIST) %>"><html:img src="button?msg=Cancel" border="0"/></html:link>
        </td>
    </tr>
</html:form>
</table>

<%@include file="/footer.jsp"%>
