<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<% int i=1; %>

    <table border="0" cellspacing="10" cellpadding="0">
        <tr>
        <agn:ShowNavigation navigation="StatisticsSub" highlightKey="">
            <agn:ShowByPermission token="<%= _navigation_token %>">
                <td>
                    <table width="300" cellspacing="0" cellpadding="0">
                        <tr>
                            <td width ="40"><html:link page="<%= _navigation_href %>"><img border="0" width="40" height="38" src="${emmLayoutBase.imagesURL}/splash_stat_<%= _navigation_navMsg.toLowerCase().replace('.', '_') %>.gif" alt="<bean:message key="<%= _navigation_navMsg %>"/>"></html:link></td>
                            <td class="boxhead" width="250"><html:link page="<%= _navigation_href %>"><span class="head1"><bean:message key="<%= _navigation_navMsg %>"/></span></html:link></td>
                            <td width="10"><img width="10" height="38" src="${emmLayoutBase.imagesURL}/box_topright.gif"></td>
                        </tr>
                        <tr>
                            <td colspan=3 class="boxmiddle" height="80" width="300"><img src="images/emm/one_pixel.gif" width=1 height=60 align="left"><html:link page="<%= _navigation_href %>"><bean:message key='<%= new String(\"splash.\"+_navigation_navMsg) %>'/></html:link></td>
                        </tr>
                        <tr>
                            <td width="40"><img width="40" height="10" src="${emmLayoutBase.imagesURL}/box_bottomleft.gif" alt="<bean:message key='<%= _navigation_navMsg %>'/>"></td>
                            <td class="boxbottom"></td>
                            <td width="10"><img width="10" height="10" src="${emmLayoutBase.imagesURL}/box_bottomright.gif"></td>
                        </tr>
                    </table>
                </td>
                <% if(i==2) { %> </tr><tr> <% i=0; } i++; %>
            </agn:ShowByPermission>
        </agn:ShowNavigation>
        </tr>
     </table>