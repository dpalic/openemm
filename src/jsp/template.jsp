<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>

<%@ page import="org.agnitas.util.AgnUtils" %>
<%@ page import="org.agnitas.beans.EmmLayoutBase" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="MENU_POSITION_LEFT" value="<%= EmmLayoutBase.MENU_POSITION_LEFT%>" scope="page"/>
<c:set var="MENU_POSITION_TOP" value="<%= EmmLayoutBase.MENU_POSITION_TOP%>" scope="page"/>


<tiles:insert attribute="page-setup"/>

<html>
<tiles:insert attribute="head-tag"/>
<bean:define id="login_title">
    <bean:message key="logon.title"/>
</bean:define>
<c:set var="login_title_array" value="${fn:split(login_title,' ')}"/>
<body>
<div class="emm-container">
    <div id="header_container">
        <div id="header_background">
            <div>
                <div id="logo_container_title">
                    <html:link page="/start.do"><img src="${emmLayoutBase.imagesURL}/logo.png"/></html:link>
                </div>

                <div id="logo_container_title">
                    <div id="logo_container_title_text">
                        <c:out value="${login_title_array[1]}"/>
                    </div>
                    <div id="logo_container_title_text">
                        <c:out value="${login_title_array[2]}"/>
                    </div>
                    <div id="logo_container_title_text">
                        <c:out value="${login_title_array[3]}"/>
                    </div>
                </div>
            </div>
            <div id="header_navigation_container">
                <%--<html:link page="/dashboard.do?action=2"><%= AgnUtils.getCompany(request).getShortname() %></html:link>--%>
                <html:link page="/start.do"><%= AgnUtils.getCompany(request).getShortname() %>
                </html:link>
                <span class="header_navigation_trenner">|</span>
                <html:link page="/logon.do?action=2"><bean:message key="default.Logout"/></html:link>
                <span class="header_navigation_trenner">|</span>
                <a href="#"
                   onclick="window.open('<%= AgnUtils.getHelpURL(request) %>','help1','width=310,height=600,left=0,top=0,scrollbars=yes');"
                   class="help_link"><bean:message key="help"/></a>
            </div>
            <c:if test="${emmLayoutBase.menuPosition == MENU_POSITION_TOP}">
                <div id="topmenu-module">
                    <tiles:insert attribute="topmenu"/>
                </div>
            </c:if>
        </div>
        <div id="header_background_filler"></div>
    </div>
    <div id="content_wrapper">
        <c:if test="${emmLayoutBase.menuPosition == MENU_POSITION_LEFT}">
            <div id="sidemenu-module">
                <tiles:insert attribute="sidemenu"/>
            </div>
            <div id="main_container">
        </c:if>
        <c:if test="${emmLayoutBase.menuPosition == MENU_POSITION_TOP}">
            <div id="main_container" class="main_container_top_menu">
        </c:if>

            <!--   <div class="head-module">
                        <tiles:insert attribute="header"/>
                    </div>
            -->
            <div class="main-module">
                <div class="view-container">

                    <%-- Page tabs --%>
                    <tiles:insert attribute="tabsmenu"/>

                    <%-- Main box --%>
                    <div class="view-module">
                        <div id="view_content">
                            <div id="contentbox_wrapper">
                                <%-- Top white corners --%>
                                <div id="contentbox_top"></div>

                                <%-- The body --%>
                                <div id="contentbox">
                                    <tiles:insert attribute="messages"/>
                                    <tiles:insert attribute="body"/>
                                </div>
                                <div id="contentbox_bottom"></div>
                            </div>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    </div>
</div>

<%-- Footer --%>
<div class="footer">
    <tiles:insert attribute="footer"/>
</div>

</body>
</html>