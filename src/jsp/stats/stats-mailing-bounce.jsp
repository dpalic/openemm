<%@ page language="java"
         import="org.agnitas.util.AgnUtils, org.agnitas.util.SafeString, java.util.Hashtable, java.util.Locale"
         contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%
    Integer tmpMailingID = (Integer) request.getAttribute("tmpMailingID");
    String shortname = (String) request.getAttribute("shortname");
    String timekey = (String) request.getAttribute("timekey");
    // put csv file from the form in the hash table:
    String file = "";
%>

<div class="import_start_container">

<script type="text/javascript">
    var tableID = 'customertbl';
</script>

<html:form action="/mailing_stat">
    <html:hidden property="action"/>

    <table border="0" cellspacing="0" cellpadding="0">

        <tr>
            <td><B>Mailing: </B></td>
            <td>&nbsp;&nbsp;<B><%= shortname %>
            </B>&nbsp;&nbsp;</td>
            <td>
                <div align="right"><html:link page='<%= new String(\"/file_download?key=\" + timekey) %>'><img
                        src="${emmLayoutBase.imagesURL}/icon_save.gif"
                        border="0"></html:link></div>
            </td>
        </tr>

    </table>

    </div>

    <br>

    <table border="0" cellspacing="0" cellpadding="0" class="list_table" id="customertbl">
    <tr>
        <th><bean:message key="recipient.Salutation"/>&nbsp;</th>
        <th><bean:message key="recipient.Firstname"/></th>
        <th><bean:message key="recipient.Lastname"/></th>
        <th><bean:message key="mailing.E-Mail"/></th>
    </tr>

    <% String sqlStatement = "select cust.email as email, cust.firstname as firstname, cust.lastname as lastname, cust.gender as gender from customer_" + AgnUtils.getCompanyID(request) + "_binding_tbl bind, customer_" + AgnUtils.getCompanyID(request) + "_tbl cust	where bind.customer_id=cust.customer_id and exit_mailing_id=" + tmpMailingID + " and user_status = 2 and mailinglist_id=(select mailinglist_id from mailing_tbl where mailing_id = " + tmpMailingID + ")"; %>
    <% System.err.println(sqlStatement); %>
    <% file += SafeString.getLocaleString("recipient.Salutation", (Locale) session.getAttribute("emm.locale")) + ";" + SafeString.getLocaleString("recipient.Firstname", (Locale) session.getAttribute("emm.locale")) + ";" + SafeString.getLocaleString("recipient.Lastname", (Locale) session.getAttribute("emm.locale")) + ";" + SafeString.getLocaleString("mailing.E-Mail", (Locale) session.getAttribute("emm.locale")); %>
    <agn:ShowTable id="agntbl3" sqlStatement="<%= sqlStatement %>" maxRows="500">
        <tr>
            <span class="ie7hack">
                <td><% if (((String) pageContext.getAttribute("_agntbl3_gender")).compareTo("0") == 0) { %>
                    <% file += "\n \"" + SafeString.getLocaleString("settings.MisterShort", (Locale) session.getAttribute("emm.locale")); %>
                    <bean:message key="settings.MisterShort"/>
                    <% } else if (((String) pageContext.getAttribute("_agntbl3_gender")).compareTo("1") == 0) { %>
                    <% file += "\n \"" + SafeString.getLocaleString("settings.MissesShort", (Locale) session.getAttribute("emm.locale")); %>
                    <bean:message key="settings.MissesShort"/>
                    <% } else { %>
                    <% file += "\n \"" + SafeString.getLocaleString("recipient.Unknown", (Locale) session.getAttribute("emm.locale")); %>
                    <bean:message key="recipient.Unknown"/>
                    <% }%>
                </td>
            </span>
            <td>
                <span class="ie7hack"><%= (String) (pageContext.getAttribute("_agntbl3_firstname")) %></span>
            </td>
            <td>
                <span class="ie7hack"><%= (String) (pageContext.getAttribute("_agntbl3_lastname")) %></span>
            </td>
            <td>
                <span class="ie7hack"><%= (String) (pageContext.getAttribute("_agntbl3_email")) %></span>
            </td>
        </tr>
        <% file += "\";\"" + pageContext.getAttribute("_agntbl3_firstname"); %>
        <% file += "\";\"" + pageContext.getAttribute("_agntbl3_lastname"); %>
        <% file += "\";\"" + pageContext.getAttribute("_agntbl3_email") + "\""; %>
    </agn:ShowTable>

</html:form>
<%((Hashtable) pageContext.getSession().getAttribute("map")).put(timekey, file); %>
</table>

<script type="text/javascript">
    table = document.getElementById('customertbl');

    $$('#customertbl tbody tr').each(function(item) {
        item.observe('mouseover', function() {
            item.addClassName('list_highlight');
        });
        item.observe('mouseout', function() {
            item.removeClassName('list_highlight');
        });
    });
</script>