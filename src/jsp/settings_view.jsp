<%@ page language="java" contentType="text/html; charset=utf-8" import="com.agnitas.util.*" errorPage="error.jsp" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<% if(session.getAttribute("agnLogged") == null) {
     response.sendRedirect("/login.jsp");
	 return; } %>
<agn:Permission token="settings.show"/>	 

<html>
<head>
<title><agn:GetLocalMsg key="Settings"/></title>
<link rel="stylesheet" href="/style_interface.css">
</head>

<body bgcolor="#FFFFFF" scroll="AUTO">
<table border="0" cellspacing="0" cellpadding="0" height="100%">
  <tr align="left" valign="top"> 
    <td>
      <% pageContext.setAttribute("sidemenu_active", new String("Settings")); %>
      <%@include file="/sidemenu.jsp"%>
    </td>
    <td><img src="/images/one_pixel.gif" width="10" height="100"></td>
    <td bgcolor="#73A2D0"><img src="/images/one_pixel.gif" width="1" height="100" border="0"></td>
    <td><img src="/images/one_pixel.gif" width="10" height="100"></td>
    <td>
      <p><span class="head1"><agn:GetLocalMsg key="Settings"/></span></p>
<table border="0" cellspacing="0" cellpadding="0">
<tr><td><img src="/images/one_pixel.gif" width="10" height="10" border="0"></td>
<td>
      <table border="0" cellspacing="0" cellpadding="0">
        <tr>
          <agn:ShowNavigation navigation="settings" highlightKey="Overview">
            <agn:ShowByPermission token="<%= _navigation_token %>">
              <td><img src="/images/reiter_links_<%= _navigation_switch %>.gif" width="8" height="20" border="0"></td>
              <% if( _navigation_isHighlightKey.booleanValue() ){ %>                  
                 <td bgcolor="#73A2D0">
              <% }else{ %>
                 <td bgcolor="#D5E3F1">
              <% } %>
              <a href="<%= _navigation_href %>">
              <% if( _navigation_isHighlightKey.booleanValue() ){ %>
                <span class="menuon2">
              <% } %>
              <agn:GetLocalMsg key="<%= _navigation_navMsg %>"/>
              <% if( _navigation_isHighlightKey.booleanValue() ){ %>
                </span>
              <% } %>
              </a></td>
              <td><img src="/images/reiter_rechts_<%= _navigation_switch %>.gif" width="8" height="20" border="0"></td>
	      <td><img src="/images/one_pixel.gif" width="10" height="20" border="0"></td>
            </agn:ShowByPermission>          
          </agn:ShowNavigation>
        </tr>
      </table>
      </td><td><img src="/images/one_pixel.gif" width="10" height="10" border="0"></td></tr>
        <tr> 
          <td><img src="/images/rand_01.gif" width="10" height="10" border="0"></td>
          <td background="/images/rand_02.gif"><img src="/images/one_pixel.gif" width="10" height="10" border="0"></td>
          <td><img src="/images/rand_03.gif" width="10" height="10" border="0"></td>
        </tr>
        <tr> 
          <td background="/images/rand_04.gif"><img src="/images/one_pixel.gif" width="10" height="10" border="0"></td>
          <td> 
            <table border="0" cellspacing="0" cellpadding="0" width="100%">
              <tr>
                <td colspan="3">
                  <span class="head3"><agn:GetLocalMsg key="Settings"/>
                  <br><i>(This table is just a placeholder !!!)</i></span>
                  <br>&nbsp;
                </td>
              </tr>
              <tr><td><b><agn:GetLocalMsg key="Target"/></b>&nbsp;&nbsp;</td><td><b><agn:GetLocalMsg key="Description"/></b>&nbsp;&nbsp;</td><td><center><b><agn:GetLocalMsg key="Action"/></b></center></td></tr>
              <tr><td colspan="3"><hr><center></td></tr>
              <agn:ShowTable id="agnTbl" sqlStatement="<%= new String("SELECT TARGET_ID, TARGET_SHORTNAME, TARGET_DESCRIPTION FROM DYN_TARGET_TBL WHERE COMPANY_ID="+session.getAttribute("companyID") + " ORDER BY TARGET_SHORTNAME") %>" maxRows="100">
                  <tr>
                    <td>
                        <a href="/target_view.jsp?targetID=<%= pageContext.getAttribute("_agnTbl_target_id") %>">
                        <b><%= pageContext.getAttribute("_agnTbl_target_shortname") %></b></a>&nbsp;&nbsp;
                    </td>
                    <td>
                        <%= SafeString.getHTMLSafeString((String)pageContext.getAttribute("_agnTbl_target_description"), 40) %>
                    </td>
                    <td>
                        <center><a href="/target_view.jsp?action=delete&targetID=<%= pageContext.getAttribute("_agnTbl_target_id") %>">
                        <img src="/images/delete.gif" alt="L&ouml;schen" border="0"></a>
                        &nbsp;<a href="/target_view.jsp?targetID=<%= pageContext.getAttribute("_agnTbl_target_id") %>">
                        <img src="/images/bearbeiten.gif" alt="Bearbeiten" border="0"></a>
                        </center>
                    </td>
                  </tr>
               </agn:ShowTable>
            </table>
          </td>
          <td background="/images/rand_06.gif"><img src="/images/one_pixel.gif" width="10" height="10" border="0"></td>
        </tr>
        <tr> 
          <td><img src="/images/rand_07.gif" width="10" height="10" border="0"></td>
          <td background="/images/rand_08.gif"><img src="/images/one_pixel.gif" width="10" height="10" border="0"></td>
          <td><img src="/images/rand_09.gif" width="10" height="10" border="0"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
</body>
</html>
