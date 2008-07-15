<%@ page language="java" import="org.agnitas.util.*, org.agnitas.beans.*, org.agnitas.web.*, java.util.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%
EmmLayout aLayout=(EmmLayout)session.getAttribute("emm.layout");
String baseUrl=aLayout.getBaseUrl();
%>
<table border="0" cellspacing="0" cellpadding="0" height="100%" width="184">
    <tr>
        <td class="logo"><html:link page="/start.jsp"><img border="0" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>logo_ul.gif"></html:link></td>
    </tr>
    <tr>
        <td><table class="sidemenu" cellspacing=0 cellpadding=0 width="184">
          <tr>
              <td align="right" valign="top"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>border_nav.gif"></td>
          </tr>
          <agn:ShowNavigation navigation="sidemenu" highlightKey="<%= request.getParameter("sidemenu_active") %>">
            <agn:ShowByPermission token="<%= _navigation_token %>">
                <tr>
                <% if( _navigation_isHighlightKey.booleanValue() ){ %>
                    <td width="174" height="32" class="sidemenu_active">
                <% } else { %>
                    <td width="174" height="32">
                <% } %>
                        <html:link page="<%= _navigation_href %>"><img border="0" hspace=5 width="32" height="32" align="middle" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>navlogo_<%= _navigation_navMsg.toLowerCase() %>.gif" alt="<bean:message key="<%= _navigation_navMsg %>"/>"><bean:message key="<%= _navigation_navMsg %>"/></html:link>
                    </td>
                </tr>
                <% if( _navigation_isHighlightKey.booleanValue() ){ %>
                <agn:ShowNavigation navigation="<%= _navigation_navMsg+"Sub" %>" highlightKey="<%= request.getParameter("sidemenu_sub_active") %>" prefix="_sub">
                    <agn:ShowByPermission token="<%= _sub_navigation_token %>">
                    <tr>
                        <% if( _sub_navigation_isHighlightKey.booleanValue() ){ %>
                            <td class="sidemenu_sub_active">
                        <% } else { %>
                            <td class="sidemenu_sub">
                        <% } %>
                                <html:link page="<%= _sub_navigation_href %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>bullet.gif" border="0">&nbsp;<bean:message key="<%= _sub_navigation_navMsg %>"/></html:link>
                            </td>
                    </tr>
                    </agn:ShowByPermission>
                </agn:ShowNavigation>
                <% } %>
                <tr>
                    <td><img border="0" height="5" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="5"></td>
                </tr>
            </agn:ShowByPermission>          
          </agn:ShowNavigation>
    <tr>
        <td><img border="0" height="20" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="5"></td>
    </tr>
    <tr>
        <td width="174" height="32"><html:link page="/logon.do?action=2"><img border="0" width="32" height="32" hspace=5 align="middle" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>navlogo_logout.gif" alt="<bean:message key="Logout"/>"><bean:message key="Logout"/></html:link></td>
    </tr>
</table>
</td>
</tr>
    <tr height="100%">
        <td height="100%" class="sidemenu"><img border=0 src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="5"></td>
    </tr>
</table>
