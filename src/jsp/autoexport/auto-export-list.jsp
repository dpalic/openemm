<%@ page errorPage="/error.jsp" %>
<%@ page language="java" contentType="text/html; charset=utf-8" buffer="32kb" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script src="<%=request.getContextPath()%>/js/lib/tablecolumnresize.js" type="text/javascript"></script>
<script type="text/javascript">
    var prevX = -1;
    var tableID = 'autoexport';
    var columnindex = 0;
    var dragging = false;
    document.onmousemove = drag;
    document.onmouseup = dragstop;
    window.onload = onPageLoad;
</script>
<html:form action="/autoexport">
    <input type="hidden" name="method" value="list"/>
    <html:hidden property="numberOfRowsChanged"/>

    <div class="list_settings_container">
        <div class="filterbox_form_button">
            <a href="#" onclick="document.autoExportForm.numberOfRowsChanged.value = true; document.autoExportForm.submit(); return false;">
                <span><bean:message key="button.Show"/></span>
            </a>
        </div>
        <div class="list_settings_mainlabel"><bean:message key="settings.Admin.numberofrows"/>:</div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="20"/>
            <label>20</label>
        </div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="50"/>
            <label>50</label>
        </div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="100"/>
            <label>100</label>
        </div>
        <logic:iterate collection="${autoExportForm.columnwidthsList}" indexId="i" id="width">
            <html:hidden property="columnwidthsList[${i}]"/>
        </logic:iterate>
    </div>

    <display:table class="list_table" export="false" id="autoexport" name="autoExports" pagesize="50" requestURI="/autoexport.do">
        <display:column property="shortname" href="autoexport.do?method=view" paramId="autoExportId" paramProperty="autoExportId"
                        titleKey="autoExport.autoExport" sortable="true" class="autoexport_name" headerClass="autoexport_name_header"/>
        <display:column property="description" href="autoexport.do?method=view" paramId="autoExportId" paramProperty="autoExportId"
                        titleKey="Description" sortable="true" class="autoexport_description" headerClass="autoexport_description_header"/>
        <display:column property="fileServer" href="autoexport.do?method=view" paramId="autoExportId" paramProperty="autoExportId"
                        titleKey="autoImport.fileServer" sortable="true" class="autoexport_file_server" headerClass="autoexport_file_server_header"/>
        <display:column titleKey="autoImport.status" sortable="true" sortProperty="active" class="autoexport_active_status" headerClass="autoexport_active_status_header">
            <c:if test="${autoexport.active}">
                <bean:message key="autoImport.statusActive"/>
            </c:if>
            <c:if test="${not autoexport.active}">
                <bean:message key="autoImport.statusNotActive"/>
            </c:if>
        </display:column>
        <display:column>
            <html:link styleClass="mailing_edit" titleKey="button.Edit" page="/autoexport.do?method=view&autoExportId=${autoexport.autoExportId}"/>
            <html:link styleClass="mailing_delete" titleKey="button.Delete" page="/autoexport.do?method=delete&autoExportId=${autoexport.autoExportId}&fromListPage=true"/>
        </display:column>
    </display:table>

</html:form>

<script type="text/javascript">
    table = document.getElementById('autoexport');
    rewriteTableHeader(table);
    writeWidthFromHiddenFields(table);
    $$('#autoexport tbody tr').each(function(item) {
        item.observe('mouseover', function() {
            item.addClassName('list_highlight');
        });
        item.observe('mouseout', function() {
            item.removeClassName('list_highlight');
        });
    });
</script>