<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<script src="${emmLayoutBase.jsURL}/tablecolumnresize.js" type="text/javascript"></script>
<script type="text/javascript">
    var prevX = -1;
    var tableID = 'profilefields';
    var columnindex = 0;
    var dragging = false;

    document.onmousemove = drag;
    document.onmouseup = dragstop;
</script>

<table border="0" cellspacing="0" cellpadding="0" width="100%" class="list_table" id="profilefields"
       style="margin-top:22px;">
    <tr>
        <th class="profile_fields_name"><bean:message key="settings.FieldName"/>&nbsp;&nbsp;</th>
        <th class="profile_fields_dbname"><bean:message key="settings.FieldNameDB"/>&nbsp;&nbsp;</th>
        <th><bean:message key="default.Type"/>&nbsp;&nbsp;</th>
        <th class="profile_fields_length"><bean:message key="settings.Length"/>&nbsp;&nbsp;</th>
        <th class="profile_fields_defvalue"><bean:message key="settings.Default_Value"/>&nbsp;&nbsp;</th>
        <th><bean:message key="settings.NullAllowed"/>&nbsp;&nbsp;</th>
        <th class="edit">&nbsp;</th>
    </tr>

    <agn:ShowColumnInfo id="agnTbl" table="<%= AgnUtils.getCompanyID(request) %>"
                        hide="change_date, creation_date, title, datasource_id, email, firstname, lastname, gender, mailtype, customer_id, timestamp, bounceload">
        <tr class="trStyle"> <!-- MailingBaseAction.ACTION_VIEW -->
            <td><span class="ie7hack"><html:link
                    page='<%= new String("/profiledb.do?action=" + ProfileFieldAction.ACTION_VIEW + "&fieldname=" + pageContext.getAttribute("_agnTbl_column_name")) %>'><b><%= pageContext.getAttribute("_agnTbl_shortname") %>
            </b></html:link>&nbsp;&nbsp;</span></td>
            <td><span class="ie7hack"><html:link
                    page='<%= new String("/profiledb.do?action=" + ProfileFieldAction.ACTION_VIEW + "&fieldname=" + pageContext.getAttribute("_agnTbl_column_name")) %>'><%= pageContext.getAttribute("_agnTbl_column_name") %>
            </html:link>&nbsp;&nbsp;</span></td>
            <td><span class="ie7hack"><bean:message
                    key='<%= "settings.fieldType."+pageContext.getAttribute("_agnTbl_data_type") %>'/>&nbsp;&nbsp;</span>
            </td>
            <td>
                <div align="right"><span
                        class="ie7hack"><% if (((String) pageContext.getAttribute("_agnTbl_data_type")).equals("VARCHAR")) { %><%=pageContext.getAttribute("_agnTbl_data_length")%><% } %>&nbsp;&nbsp;&nbsp;&nbsp;</span>
                </div>
            </td>
            <td>
                <div align="right"><span
                        class="ie7hack"><%=pageContext.getAttribute("_agnTbl_data_default").toString().trim()%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
                </div>
            </td>
            <td>
                <span class="ie7hack">
                    <%
                        Integer isNullable = (Integer) pageContext.getAttribute("_agnTbl_nullable");
                        if (isNullable != null && isNullable.intValue() == 1) { %>
                        <bean:message key="default.Yes"/>
                    <% } else { %>
                        <bean:message key="default.No"/>
                    <% } %>
                </span>
            </td>
            <td>

                <html:link styleClass="mailing_edit" titleKey="settings.profile.ProfileEdit"
                           page='<%= new String("/profiledb.do?action=" + ProfileFieldAction.ACTION_VIEW + "&fieldname=" + pageContext.getAttribute("_agnTbl_column_name")) %>'/>


                <html:link styleClass="mailing_delete" titleKey="settings.profile.ProfileDelete"
                           page='<%= new String("/profiledb.do?action=" + ProfileFieldAction.ACTION_CONFIRM_DELETE + "&fieldname=" + pageContext.getAttribute("_agnTbl_column_name")) %>'/>
            </td>
        </tr>

    </agn:ShowColumnInfo>

</table>
<script type="text/javascript">
    table = document.getElementById('profilefields');
    rewriteTableHeader(table);
    writeWidthFromHiddenFields(table);

    $$('#profilefields tbody tr').each(function(item) {
        item.observe('mouseover', function() {
            item.addClassName('list_highlight');
        });
        item.observe('mouseout', function() {
            item.removeClassName('list_highlight');
        });
    });
</script>
