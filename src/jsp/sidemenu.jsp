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
 --%><%@ page language="java" import="org.agnitas.util.*, org.agnitas.beans.*, org.agnitas.web.*, java.util.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<table border="0" cellspacing="0" cellpadding="0" height="100%" width="184">
    <tr>
        <td class="logo"><html:link page="/start.jsp"><agn:layoutImg file="logo_ul.png"/></html:link></td>
    </tr>
    <tr>
        <td><table class="sidemenu" cellspacing=0 cellpadding=0 width="184">
          <tbody>
          <tr>
              <td align="right" valign="top"><agn:layoutImg file="border_nav.png" border="0"/></td>
          </tr>
          <agn:ShowNavigation navigation="sidemenu" highlightKey="<%= request.getParameter("sidemenu_active") %>">
            <agn:ShowByPermission token="<%= _navigation_token %>">
                <tr>
                <% if( _navigation_isHighlightKey.booleanValue() ){ %>
                    <td width="174" height="32" class="sidemenu_active" onMouseOver="this.bgColor='#6699CC'" onMouseOut="this.bgColor=''">
                <% } else { %>
                    <td width="174" height="32" onMouseOver="this.style.backgroundColor='#c2d3df'" onMouseOut="this.style.backgroundColor=''">
                <% } %>
                        <html:link page="<%= _navigation_href %>"><agn:layoutImg hspace="5" width="32" height="32" border="0" align="absmiddle" file="<%= "navlogo_"+_navigation_navMsg.toLowerCase()+".png" %>" altKey="<%= _navigation_navMsg %>"/><bean:message key="<%= _navigation_navMsg %>"/></html:link>
                    </td>
                </tr>
                <% if( _navigation_isHighlightKey.booleanValue() ){ %>
                <agn:ShowNavigation navigation="<%= _navigation_navMsg+"Sub" %>" highlightKey="<%= request.getParameter("sidemenu_sub_active") %>" prefix="_sub">
                    <agn:ShowByPermission token="<%= _sub_navigation_token %>">
                    <tr>
                        <% if( _sub_navigation_isHighlightKey.booleanValue() ){ %>
                            <td class="sidemenu_sub_active" onMouseOver="this.style.backgroundColor='#c2d3df'" onMouseOut="this.style.backgroundColor=''">
                        <% } else { %>
                            <td class="sidemenu_sub" onMouseOver="this.style.backgroundColor='#c2d3df'" onMouseOut="this.style.backgroundColor=''">
                        <% } %>
                                <html:link page="<%= _sub_navigation_href %>">&nbsp;<bean:message key="<%= _sub_navigation_navMsg %>"/></html:link>
                            </td>
                    </tr>
                    </agn:ShowByPermission>
                </agn:ShowNavigation>
                <% } %>
                <tr>
                    <td><agn:layoutImg height="5" width="5" border="0" file="one_pixel.gif"/></td>
                </tr>
            </agn:ShowByPermission>
          </agn:ShowNavigation>
    <tr>
        <td><agn:layoutImg height="20" width="5" file="one_pixel.gif"/></td>
    </tr>
    <tr>
        <td width="174" height="32" onMouseOver="this.style.backgroundColor='#c2d3df'" onMouseOut="this.style.backgroundColor=''"><html:link page="/logon.do?action=2"><agn:layoutImg width="32" height="32" hspace="5" border="0" align="absmiddle" file="navlogo_logout.png" altKey="Logout"/><bean:message key="Logout"/></html:link></td>
    </tr>
</tbody>
</table>

</td>
</tr>
<tr height="100%" class="sidemenu">
	<td valign="bottom" align="center"><span class="companyname"><%= AgnUtils.getCompany(request).getShortname() %></span>
	</td>
</tr>
    <tr>
        <td class="sidemenu"><agn:layoutImg file="one_pixel.gif" width="5"/></td>
    </tr>
</table>
