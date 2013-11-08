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
 --%><%@ page language="java" import="java.io.*, org.agnitas.util.*" contentType="text/html; charset=utf-8" %>
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
     <link type="text/css" rel="stylesheet" href="styles/displaytag.css">
     <link type="text/css" rel="stylesheet" href="styles/cms_displaytag.css">
     <link type="text/css" rel="stylesheet" href="styles/tooltiphelp.css">
     <link type="text/css" rel="stylesheet" href="styles/reportstyles.css">
     <link title="Aqua" href="/js/jscalendar/skins/aqua/theme.css" media="all" type="text/css" rel="stylesheet"/>
     <link type="text/css" rel="stylesheet" href="styles/pidstyles.css">
	 <link rel="shortcut icon" href="favicon.ico"/>
  </head>
  <script type="text/javascript" src="<%=request.getContextPath()%>/js/prototype.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/scriptaculous/scriptaculous.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/overlibmws/overlibmws.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/overlibmws/overlibmws_crossframe.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/overlibmws/overlibmws_iframe.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/overlibmws/overlibmws_hide.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/overlibmws/overlibmws_shadow.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/ajax/ajaxtags.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/ajax/ajaxtags_controls.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/ajax/ajaxtags_parser.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/aa.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/helpballoon/HelpBalloon.js" ></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/calendar/CalendarPopup.js" ></script>
<script type="text/javascript">
		<!--
		//
		// Override the default settings to point to the parent directory
		//
		HelpBalloon.Options.prototype = Object.extend(HelpBalloon.Options.prototype, {
			icon: 'images/icon.gif',
			button: 'images/button.png',
			balloonPrefix: 'images/balloon-'
		});

		//-->
		</script>

  <body>
    <table border="0" cellspacing="0" cellpadding="0" height="100%" width="100%">
      <tr align="left" valign="top">
        <td rowspan=3 width=184>
          <jsp:include page="/sidemenu.jsp" flush="false">
            <jsp:param name="sidemenu_active" value="<%= (String)(pageContext.getAttribute("sidemenu_active")) %>"/>
            <jsp:param name="sidemenu_sub_active" value="<%= (String)(pageContext.getAttribute("sidemenu_sub_active")) %>"/>
          </jsp:include>
        </td>
        <td height=100 colspan=3 class="border_up">
          <agn:layoutImg file="border_up.png" align="left" hspace="0"/>
          <div class="border_up_text"><bean:message key="logon.title"/></div>
<span class="headline"><bean:message key="<%= (String)(pageContext.getAttribute("agnSubtitleKey")) %>"/><% if(pageContext.getAttribute("agnSubtitleValue")!=null) {%>: <%= SafeString.getHTMLSafeString(pageContext.getAttribute("agnSubtitleValue").toString()) %><% } %></span>
        </td>
        </tr>
        <tr>
          <td class="sub_icon" width=1 height="1000">
          <% String submenu=((String) pageContext.getAttribute("sidemenu_sub_active")).toLowerCase();
            String mainmenu=((String) pageContext.getAttribute("sidemenu_active")).toLowerCase();

             if(submenu != null && mainmenu != null && !submenu.equals("none") && !mainmenu.equals("none")) {
                 %><agn:layoutImg width="42" height="42" file="<%= "sub_icons/"+mainmenu+"_"+submenu+".gif" %>"/><%
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
                            <td class="tag_active" onMouseOver="this.style.backgroundColor='#c2d3df'" onMouseOut="this.style.backgroundColor=''">
                          <% }else{ %>
                            <td class="tag" onMouseOver="this.style.backgroundColor='#c2d3df'" onMouseOut="this.style.backgroundColor=''">
                          <% } %>

                            <table border="0" cellpadding="0" cellspacing="0">
                              <tr>
                                <td><agn:layoutImg file="tag_left.png"/></td>
                                <td>
                          <% String nav_link=new String(_navigation_href);
                             if(pageContext.getAttribute("agnNavHrefAppend")!=null) {
                                nav_link=new String(nav_link+pageContext.getAttribute("agnNavHrefAppend"));
                             }
                          %>
                          <% if( _navigation_isHighlightKey.booleanValue() ){ %>
                                  <html:link page="<%= nav_link %>">
                                    <bean:message key="<%= _navigation_navMsg %>"/>
                                  </html:link>
                                </td>
                          <% } else { %>
                                  <html:link page="<%= nav_link %>">
                                    <bean:message key="<%= _navigation_navMsg %>"/>
                                  </html:link>
                                </td>
                          <% } %>
                                <td><agn:layoutImg file="tag_right.png"/></td>
							  </tr>
							</table>

					      </td>
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
