<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.util.AgnUtils, org.agnitas.web.forms.IPStatForm" buffer="32kb" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<table border="0" cellspacing="0" cellpadding="0">

    <html:form action="/ip_stats">
        <html:hidden property="action"/>

        <tr>
            <td colspan="3">&nbsp;</td>
        </tr>

        <!-- select target group: -->
        <tr>
            <td><bean:message key="target.Target"/>:&nbsp;</td>
            <td>
                <html:select property="targetID" size="1">
                    <html:option value="0"><bean:message key="statistic.All_Subscribers"/></html:option>
                    <agn:ShowTable id="agntbl3"
                                   sqlStatement='<%= new String(\"SELECT target_id, target_shortname FROM dyn_target_tbl WHERE company_id=\"+AgnUtils.getCompanyID(request) ) %>'
                                   maxRows="50" encodeHtml="0">
                        <html:option
                                value='<%= (String)pageContext.getAttribute(\"_agntbl3_target_id\") %>'><%= pageContext.getAttribute("_agntbl3_target_shortname") %>
                        </html:option>
                    </agn:ShowTable>
                </html:select>
            </td>
            <td>
                <div align="right"><html:link page='<%= new String(\"/file_download?key=\" + timekey) %>'><img
                        src="${emmLayoutBase.imagesURL}/icon_save.gif"
                        border="0"></html:link></div>
            </td>
        </tr>

        <!-- select mailing list: -->
        <tr>
            <td><bean:message key="Mailinglist"/>:&nbsp;</td>
            <td>
                <html:select property="listID" size="1">
                    <html:option value="0"><bean:message key="statistic.All_Mailinglists"/></html:option>
                    <agn:ShowTable id="agntbl2"
                                   sqlStatement='<%= new String(\"SELECT mailinglist_id, shortname FROM mailinglist_tbl WHERE company_id=\"+AgnUtils.getCompanyID(request) ) %>'
                                   maxRows="100" encodeHtml="0">
                        <html:option
                                value='<%= (String)pageContext.getAttribute(\"_agntbl2_mailinglist_id\") %>'><%= pageContext.getAttribute("_agntbl2_shortname") %>
                        </html:option>
                    </agn:ShowTable>
                </html:select>
            </td>
            <td><html:image src="button?msg=button.OK" border="0"/></td>
        </tr>

        <tr>
            <td colspan="3">&nbsp;</td>
        </tr>
        <tr>
            <td colspan="3">&nbsp;</td>
        </tr>

        <% if ((((IPStatForm) session.getAttribute("ipStatForm")).getTotal()) != 0) {
        %>

        <tr>
            <td colspan="3">
                <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td><span class="head3"><bean:message key="statistic.IPAddress"/></span>&nbsp;&nbsp;</td>
                        <td colspan="2">&nbsp;&nbsp;<span class="head3"><bean:message key="Recipients"/></span></td>
                    </tr>

                    <tr>
                        <td colspan="3">
                            <hr size="1">
                        </td>
                    </tr>

                    <c:forEach begin="0" var="j" end="${ipStatForm.lines}">
                        <tr>
                            <td>${ipStatForm.ips[j]}&nbsp;&nbsp;</td>
                            <td align="right">${ipStatForm.subscribers[j]}&nbsp;</td>
                            <td><img src="${emmLayoutBase.imagesURL}/one_pixel_h.gif" width="${(ipStatForm.subscribers[j] * 1.0 ) / ipStatForm.biggest  * 250 }" height="10"></td>
                        </tr>
               	    </c:forEach>

                    <tr>
                        <td colspan="3">&nbsp;&nbsp;</td>
                    </tr>

                    <tr>
                        <td><bean:message key="statistic.Other"/>:&nbsp;&nbsp;</td>
                        <td align="right"><%= ((IPStatForm) session.getAttribute("ipStatForm")).getRest() %>&nbsp;</td>
                        <td>&nbsp;</td>
                    </tr>

                    <tr>
                        <td colspan="3">
                            <hr>
                        </td>
                    </tr>

                    <tr>
                        <td><b><bean:message key="statistic.Total"/>:</b>&nbsp;&nbsp;</td>
                        <td align="right"><b><%= ((IPStatForm) session.getAttribute("ipStatForm")).getTotal() %>
                        </b>&nbsp;</td>
                        <td>&nbsp;</td>
                    </tr>
                </table>
            </td>
        </tr>
        <% } else { %>

        <tr>
            <td colspan="3">
                <b><bean:message key="recipient.NoSubscribersForSelection"/></b>
            </td>
        </tr>

        <% } %>

    </html:form>

</table>

