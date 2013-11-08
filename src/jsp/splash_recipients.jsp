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
 --%><%@ page language="java" contentType="text/html; charset=utf-8" errorPage="error.jsp" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>
<% pageContext.setAttribute("sidemenu_active", new String("Recipients")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("none")); %>
<% pageContext.setAttribute("agnTitleKey", new String("A_EMM")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Recipients")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("none")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("none")); %>

<%@include file="/header.jsp"%>
<%@include file="/messages.jsp" %>

<% int i=1; %>

    <table border="0" cellspacing="10" cellpadding="0">
    <tr>
        <agn:ShowNavigation navigation="RecipientsSub" highlightKey="">
            <agn:ShowByPermission token="<%= _navigation_token %>">
                <td>
                    <table width="300" cellspacing="0" cellpadding="0">
                        <tr>
                            <td width ="40"><html:link page="<%= _navigation_href %>"><agn:layoutImg width="40" height="38" file='<%= "splash_sub_"+_navigation_navMsg.toLowerCase().replace('.', '_')+".gif" %>' altKey="<%= _navigation_navMsg %>"/></html:link></td>
                            <td class="boxhead" width="250"><html:link page="<%= _navigation_href %>"><span class="head1"><bean:message key="<%= _navigation_navMsg %>"/></span></html:link></td>
                            <td width="10"><agn:layoutImg width="10" height="38" file="box_topright.gif"/></td>
                        </tr>
                        <tr>
                            <td colspan=3 class="boxmiddle" height="80" width="300"><agn:layoutImg file="one_pixel.gif" width="1" height="60" align="left"/><html:link page="<%= _navigation_href %>"><bean:message key='<%= new String("splash.recipient."+_navigation_navMsg) %>'/></html:link></td>
                        </tr>
                        <tr>
                            <td width="40"><agn:layoutImg width="40" height="10" file="box_bottomleft.gif" altKey="<%= _navigation_navMsg %>"/></td>
                            <td class="boxbottom"></td>
                            <td width="10"><agn:layoutImg width="10" height="10" file="box_bottomright.gif"/></td>
                        </tr>
                    </table>
                </td>
                <% if(i==2) { %> </tr><tr> <% i=0; } i++; %>
            </agn:ShowByPermission>
        </agn:ShowNavigation>
    </tr>
    </table>
<%@include file="/footer.jsp"%>
