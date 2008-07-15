<%@ page language="java" import="java.io.*, org.agnitas.util.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <% if(pageContext.getAttribute("agnRefresh") != null) { %>
    <meta http-equiv="refresh" content="<%= (String)(pageContext.getAttribute("agnRefresh")) %>">
    <meta http-equiv="Page-Exit" content="RevealTrans(Duration=1,Transition=1)">
    <% } %>
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="expires" content="0">
    <title>
      <bean:message key="<%= (String)(pageContext.getAttribute("agnTitleKey")) %>"/>
    </title>
    <link type="text/css" rel="stylesheet" href="<bean:write name="emm.layout" property="baseUrl" scope="session"/>stylesheet.css">
    <link rel="shortcut icon" href="favicon.ico"/>
  </head>
  <body>
    <table border="0" cellspacing="0" cellpadding="0" height="100%" width="100%">
      <tr align="left" valign="top">
        <td rowspan=3 width=184>
          <jsp:include page="/sidemenu.jsp" flush="true">
            <jsp:param name="sidemenu_active" value="<%= (String)(pageContext.getAttribute("sidemenu_active")) %>"/>
            <jsp:param name="sidemenu_sub_active" value="<%= (String)(pageContext.getAttribute("sidemenu_sub_active")) %>"/>
          </jsp:include>
        </td>
        <td height=100 colspan=3 class="border_up">
          <img src="images/emm/border_up.gif" align="left" hspace="0">
          <div class="border_up_text"><bean:message key="logon.title"/></div>
<span class="headline"><bean:message key="<%= (String)(pageContext.getAttribute("agnSubtitleKey")) %>"/><% if(pageContext.getAttribute("agnSubtitleValue")!=null) {%>: <%= pageContext.getAttribute("agnSubtitleValue") %><% } %></span>
        </td>
        </tr>
        <tr>
          <td class="sub_icon" width=1 height="1000">
          <% String submenu=((String) pageContext.getAttribute("sidemenu_sub_active")).toLowerCase();
            String mainmenu=((String) pageContext.getAttribute("sidemenu_active")).toLowerCase();

             if(submenu != null && mainmenu != null && !submenu.equals("none") && !mainmenu.equals("none")) {
                 %><img width="42" height="42" src="images/emm/sub_icons/<%=mainmenu%>_<%= submenu %>.gif"><%
             }
          %>
          </td>
          <td class="right">
         
          <table border="0" cellspacing="0" cellpadding="0">
            <tr>
              <td> 
                <agn:layoutImg file="one_pixel.gif" width="10" height="10"/></td>
              <td>
                <table border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <agn:ShowNavigation navigation="<%= (String)(pageContext.getAttribute("agnNavigationKey")) %>" highlightKey="<%= (String)(pageContext.getAttribute("agnHighlightKey")) %>">
                      <agn:ShowByPermission token="<%= _navigation_token %>">
                          <% if( _navigation_isHighlightKey.booleanValue() ){ %>
                            <td><agn:layoutImg file="taga_left.gif"/></td>
                            <td class="tag_active">
                          <% }else{ %>   
                            <td><agn:layoutImg file="tag_left.gif"/></td>
                            <td class="tag">
                          <% } %>
                          <% String nav_link=new String(_navigation_href); 
                             if(pageContext.getAttribute("agnNavHrefAppend")!=null) {
                                nav_link=new String(nav_link+pageContext.getAttribute("agnNavHrefAppend"));
                             }
                          %>
                          <% if( _navigation_isHighlightKey.booleanValue() ){ %>
                            <html:link page="<%= nav_link %>">
                            <bean:message key="<%= _navigation_navMsg %>"/>
                            </html:link></td>
                            <td><agn:layoutImg file="taga_right.gif"/></td>
                          <% } else { %>
                            <html:link page="<%= nav_link %>">
                            <bean:message key="<%= _navigation_navMsg %>"/>
                            </html:link></td>
                            <td><agn:layoutImg file="tag_right.gif"/></td>
                          <% } %>
                        <td><agn:layoutImg file="one_pixel.gif" width="10" height="10"/></td>
                      </agn:ShowByPermission>          
                    </agn:ShowNavigation>
                  </tr>
                </table>
              </td>
              <td><agn:layoutImg file="one_pixel.gif" width="10" height="10"/></td>
            </tr>
            <tr>
              <td><agn:layoutImg file="border_01.gif" width="10" height="10"/></td>
              <td class="content"><agn:layoutImg file="one_pixel.gif" width="10" height="10"/></td>
              <td><agn:layoutImg file="border_03.gif" width="10" height="10"/></td>
            </tr>
            <tr>
              <td class="content"><agn:layoutImg file="one_pixel.gif" width="10" height="10"/></td>
              <td class="content">
