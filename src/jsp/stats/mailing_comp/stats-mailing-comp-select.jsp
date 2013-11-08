<%-- checked --%>
<%@ page language="java" import="org.agnitas.util.AgnUtils"
         contentType="text/html; charset=utf-8" buffer="32kb" %>
<%@ page import="org.agnitas.util.SafeString" %>
<%@ page import="org.agnitas.web.MailingStatAction" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script src="${emmLayoutBase.jsURL}/tablecolumnresize.js" type="text/javascript"></script>
<script type="text/javascript">
    var prevX = -1;
    var tableID = 'mailings';
    var columnindex = 0;
    var dragging = false;

    document.onmousemove = drag;
    document.onmouseup = dragstop;
</script>

<html:form action="/mailing_compare">
    <html:hidden property="action"/>

    <div class="import_start_container">

        <span class="head3"><bean:message key="Mailing"/> <bean:message key="statistic.comparison"/></span>
        <br><br>
        <bean:message key="target.Target"/>:
        <html:select property="targetID" size="1">
            <html:option value="0"><bean:message key="statistic.All_Subscribers"/></html:option>
            <agn:ShowTable id="agntbl3"
                           sqlStatement='<%= new String(\"SELECT target_id, target_shortname FROM dyn_target_tbl WHERE company_id=\"+AgnUtils.getCompanyID(request) + \" AND deleted=0 ORDER BY lower(target_shortname)\") %>'
                           maxRows="500">
                <html:option
                        value='<%= (String)(pageContext.getAttribute(\"_agntbl3_target_id\")) %>'><%= pageContext.getAttribute("_agntbl3_target_shortname") %>
                </html:option>
            </agn:ShowTable>
        </html:select>
        <br>
        <br>
    </div>

    <table id="mailings" border="0" cellspacing="0" cellpadding="0" class="list_table compare_select_table" width="100%">
        <tr>
            <th class="comparasion_select_name"><bean:message key="Mailing"/></th>
            <th class="comparasion_select_desc"><bean:message key="default.description"/></th>
            <th class="comparasion_select_comp">
                <div align="right"><bean:message key="statistic.compare"/></div>
            </th>
        </tr>

        <c:set var="index" value="0" scope="request"/>

        <agn:ShowTable id="agnTbl"
            sqlStatement='<%= new String(\"SELECT mailing_id, shortname, description FROM mailing_tbl A WHERE company_id=\"+AgnUtils.getCompanyID(request)+ \" AND deleted<>1 AND is_template=0 and A.mailing_id in (select mailing_id from maildrop_status_tbl where status_field in (\'W\', \'E\', \'C\') and company_id = \"+AgnUtils.getCompanyID(request)+ \") ORDER BY mailing_id DESC\")%>'
                       maxRows="500">

            <c:set var="trStyle" value="even" scope="request"/>
            <c:if test="${(index mod 2) == 0}">
                <c:set var="trStyle" value="odd" scope="request"/>
            </c:if>
            <c:set var="index" value="${index + 1}" scope="request"/>

            <tr class="trStyle">
                <td class="comparasion_select_name"><span class="ie7hack"><html:link
                        page='<%= new String(\"/mailing_stat.do?action=\" + MailingStatAction.ACTION_MAILINGSTAT + \"&mailingID=\" + pageContext.getAttribute(\"_agnTbl_mailing_id\")) %>'><%= pageContext.getAttribute("_agnTbl_shortname") %>
                        </html:link>&nbsp;&nbsp;
                    </span>
                </td>
                <td class="comparasion_select_desc"><span class="ie7hack"><html:link
                        page='<%= new String(\"/mailing_stat.do?action=\" + MailingStatAction.ACTION_MAILINGSTAT + \"&mailingID=\" + pageContext.getAttribute(\"_agnTbl_mailing_id\")) %>'><%= SafeString.cutLength((String) pageContext.getAttribute("_agnTbl_description"), 40) %>
                </html:link>&nbsp;&nbsp;</span></td>
                <td class="comparasion_select_comp">
                    <div align=right><input type="checkbox"
                                            name='MailCompID_<%= pageContext.getAttribute("_agnTbl_mailing_id") %>'>
                    </div>
                </td>
            </tr>
        </agn:ShowTable>
    </table>
    <br>

    <div class="maildetail_button_container">
        <div class="maildetail_button">
            <a href="#"
               onclick="document.compareMailingForm.submit();return false;">
                <span><bean:message key="statistic.compare"/></span>
            </a>
        </div>
    </div>

</html:form>

<script type="text/javascript">
    table = document.getElementById('mailings');
    rewriteTableHeader(table);
    writeWidthFromHiddenFields(table);

    $$('#mailings tbody tr').each(function(item) {
        item.observe('mouseover', function() {
            item.addClassName('list_highlight');
        });
        item.observe('mouseout', function() {
            item.removeClassName('list_highlight');
        });
    });
</script>
