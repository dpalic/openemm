<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.AgnUtils, org.agnitas.util.SafeString, org.agnitas.web.ExportWizardAction" %>
<%@ page import="org.agnitas.web.StrutsActionBase" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>


<script src="${emmLayoutBase.jsURL}/tablecolumnresize.js" type="text/javascript"></script>
<script type="text/javascript">
    var prevX = -1;
    var tableID = 'exportwizard';
    var columnindex = 0;
    var dragging = false;

    document.onmousemove = drag;
    document.onmouseup = dragstop;
</script>
  <html:form action="/exportwizard">
      <html:hidden property="action"/>

      <div class="export_wizard_content">
          <ul class="new_mailing_step_display">
              <li class="step_display_first"><span class="step_active">1</span></li>
              <li><span>2</span></li>
              <li><span>3</span></li>
          </ul>

          <div class="export_wizard_message">
              <bean:message key="export.SelectExportDef"/>:<br>&nbsp;<br>
          </div>
      </div>

      <c:set var="index" value="0" scope="request"/>
      <table border="0" cellspacing="0" cellpadding="0" class="list_table" id="exportwizard">
          <tr>
              <th class="exportwizard_head_name"><bean:message key="default.Name"/>&nbsp;&nbsp;</th>
              <th class="exportwizard_head_description"><bean:message key="default.description"/>&nbsp;&nbsp;</th>

              <th>&nbsp;</th>
          </tr>

          <agn:ShowTable id="agnTbl"
                         sqlStatement='<%= new String(\"SELECT id, shortname, description FROM export_predef_tbl WHERE company_id=\"+AgnUtils.getCompanyID(request) +\" AND deleted=0\")%>'
                         startOffset="<%= request.getParameter(\"startWith\") %>" maxRows="20">
              <tr>
                  <td>
                      <span class="ie7hack">
                          <html:link
                              page='<%= new String(\"/exportwizard.do?action=\" + ExportWizardAction.ACTION_QUERY + \"&exportPredefID=\" + pageContext.getAttribute(\"_agnTbl_id\")) %>'><b><%= pageContext.getAttribute("_agnTbl_shortname") %>
                            </b></html:link>&nbsp;&nbsp;
                           </span>
                  </td>
                  <td>
                      <span class="ie7hack">
                          <html:link
                              page='<%= new String(\"/exportwizard.do?action=\" + ExportWizardAction.ACTION_QUERY + \"&exportPredefID=\" + pageContext.getAttribute(\"_agnTbl_id\")) %>'><%= SafeString.cutLength((String) pageContext.getAttribute("_agnTbl_description"), 40) %>
                            </html:link>&nbsp;&nbsp;
                       </span>
                  </td>
                  <td>
                      <html:link styleClass="mailing_edit" titleKey="export.ExportEdit"
                                 page='<%= new String(\"/exportwizard.do?action=\" + ExportWizardAction.ACTION_QUERY + \"&exportPredefID=\" + pageContext.getAttribute(\"_agnTbl_id\")) %>'></html:link>
                      <html:link styleClass="mailing_delete" titleKey="export.ExportDelete"
                                 page='<%= new String(\"/exportwizard.do?action=\" + ExportWizardAction.ACTION_CONFIRM_DELETE + \"&exportPredefID=\" + pageContext.getAttribute(\"_agnTbl_id\")) %>'></html:link>

                  </td>
              </tr>
          </agn:ShowTable>
      </table>

    <div class="export_wizard_content">
        <div class="table_pages_container">
            <center>
                <agn:ShowTableOffset id="agnTbl" maxPages="20">
                    <%  String styleClass="table_page";
                        if (pageContext.getAttribute("activePage") != null) {
                            styleClass="table_page_current";
                        }
                        if (pageContext.getAttribute("endPage") != null && !"1".equals(pageContext.getAttribute("endPage"))) { %>
                    <html:link styleClass="<%= styleClass %>"
                         page='<%= new String("/exportwizard.do?action=" + StrutsActionBase.ACTION_LIST + "&startWith=" + pageContext.getAttribute("startWith")) %>'>
                        <%= pageContext.getAttribute("pageNum") %>
                    </html:link>
                    <% } %>
                </agn:ShowTableOffset>
            </center>
        </div>

      <div class="maildetail_button">
          <html:link
                  page='<%= new String("/exportwizard.do?action=" + ExportWizardAction.ACTION_QUERY + "&exportPredefID=0") %>'>
              <span><bean:message key="button.New"/></span></html:link>
      </div>
</html:form>
</div>
<script type="text/javascript">
    table = document.getElementById('exportwizard');
    rewriteTableHeader(table);
    writeWidthFromHiddenFields(table);

    $$('#exportwizard tbody tr').each(function(item) {
        item.observe('mouseover', function() {
            item.addClassName('list_highlight');
        });
        item.observe('mouseout', function() {
            item.removeClassName('list_highlight');
        });
    });
</script>