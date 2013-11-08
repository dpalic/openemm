<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.beans.Mailloop, org.agnitas.util.AgnUtils" %>
<%@ page import="org.agnitas.web.MailloopAction" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<script type="text/javascript">
    <!--
    function parametersChanged() {
        document.getElementsByName('mailloopForm')[0].numberOfRowsChanged.value = true;
    }
    //-->
</script>
<script src="${emmLayoutBase.jsURL}/tablecolumnresize.js" type="text/javascript"></script>
<script type="text/javascript">
    var prevX = -1;
    var tableID = 'mailloop';
    var columnindex = 0;
    var dragging = false;
    document.onmousemove = drag;
    document.onmouseup = dragstop;
</script>
 <html:form action="/mailloop">
 <html:hidden property="action"/>    
 <div class="list_settings_container">
        <div class="filterbox_form_button"><a href="#"
                                              onclick="document.mailloopForm.submit(); return false;"><span><bean:message
                key="button.OK"/></span></a></div>
        <div class="list_settings_mainlabel"><bean:message key="settings.Admin.numberofrows"/>:</div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="20"/><label
                for="list_settings_length_0">20</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="50"/><label
                for="list_settings_length_1">50</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="100"/><label
                for="list_settings_length_2">100</label></div>
        <logic:iterate collection="${mailloopForm.columnwidthsList}" indexId="i" id="width">
            <html:hidden property="columnwidthsList[${i}]"/>
        </logic:iterate>
    </div>


  <display:table class="list_table" pagesize="${mailloopForm.numberofRows}" id="mailloop"
                       name="mailloopEntries" sort="external"
                       requestURI="/mailloop.do?action=${ACTION_LIST}&__fromdisplaytag=true&numberofRows=${mailloopForm.numberofRows}" excludedParams="*"
                       size="${mailloopEntries.fullListSize}" partialList="true">
            <display:column  property="shortname" titleKey="settings.Mailloop"
                            sortable="true" url="/mailloop.do?action=${ACTION_VIEW}&mailloopID=${mailloop.id}"/>
            <display:column headerClass="mailloop_head_desc header" class="mailing" property="description" sortProperty="description"
                      titleKey="default.description" sortable="true">
          <span class="ie7hack">
              <html:link page="/mailloop.do?action=${ACTION_VIEW}&mailloopID=${mailloop.id}">${mailloop.description} </html:link>
          </span>
      </display:column>

      <display:column titleKey="settings.mailloop.forward_adr" headerClass="mailloop_head_address header" class="mailing">
           ext_${mailloop.id}@<%= AgnUtils.getCompany(request).getMailloopDomain() %>
      </display:column>
      <display:column class="edit" title="&nbsp;">
          <html:link styleClass="mailing_edit" titleKey="mailloop.mailloopEdit"
                     page="/mailloop.do?action=${ACTION_VIEW}&mailloopID=${mailloop.id}"> </html:link>

          <html:link styleClass="mailing_delete" titleKey="mailloop.mailloopDelete"
                     page="/mailloop.do?action=${ACTION_CONFIRM_DELETE}&mailloopID=${mailloop.id}"> </html:link>
      </display:column>

        </display:table>

<script type="text/javascript">
    table = document.getElementById('mailloop');
    rewriteTableHeader(table);
    writeWidthFromHiddenFields(table);
    $$('#mailloop tbody tr').each(function(item) {
            item.observe('mouseover', function() {
                item.addClassName('list_highlight');
            });
            item.observe('mouseout', function() {
                item.removeClassName('list_highlight');
            });
        });
</script>

</html:form>
