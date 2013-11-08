<%@ page language="java" import="org.agnitas.util.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<div id="left_navigation_container">
    <ul class="left_navigation_level1">
        <agn:ShowNavigation navigation="sidemenu"
                            highlightKey='<%= (String) request.getAttribute("sidemenu_active") %>'>
            <agn:ShowByPermission token="<%= _navigation_token %>">

                <%
                    String cssClassPostfix = _navigation_isHighlightKey.booleanValue() ? "_active" : "_no";
                %>
                <li><html:link page="<%= _navigation_href %>"
                               styleClass="<%= \"left_navigation_level1\" + cssClassPostfix + \"_round\" %>"><bean:message
                        key="<%= _navigation_navMsg %>"/></html:link>
                    <% if (_navigation_isHighlightKey.booleanValue()) { %>
                    <ul class="left_navigation_level2">
                        <agn:ShowNavigation navigation='<%= _navigation_navMsg+"Sub" %>'
                                            highlightKey="<%= (String) request.getAttribute(\"sidemenu_sub_active\") %>"
                                            prefix="_sub">
                            <agn:ShowByPermission token="<%= _sub_navigation_token %>">
                                <%
                                    String subCssClassPostfix = _sub_navigation_isHighlightKey.booleanValue() ? "_active" : "_no";
                                %>
                                <li><html:link page="<%= _sub_navigation_href %>"
                                               styleClass="<%= \"left_navigation_level2\" + subCssClassPostfix %>"><bean:message
                                        key="<%= _sub_navigation_navMsg %>"/></html:link></li>
                            </agn:ShowByPermission>
                        </agn:ShowNavigation>
                    </ul>
                    <% } %>
                </li>
            </agn:ShowByPermission>
        </agn:ShowNavigation>
    </ul>
</div>
