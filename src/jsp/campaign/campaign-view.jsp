<%--checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.AgnUtils, org.agnitas.util.SafeString, org.agnitas.web.CampaignAction, org.agnitas.web.MailingBaseAction, org.agnitas.web.forms.CampaignForm, java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>

<%  int tmpCampaignID = 0;
    if(session.getAttribute("campaignForm")!=null) {
       tmpCampaignID = ((CampaignForm)session.getAttribute("campaignForm")).getCampaignID();
    }
 %>

<c:set var="ACTION_CONFIRM_DELETE" value="<%= CampaignAction.ACTION_CONFIRM_DELETE %>" scope="page" />
<c:set var="ACTION_NEW" value="<%= MailingBaseAction.ACTION_NEW %>" scope="page" />
<c:set var="ACTION_VIEW" value="<%= MailingBaseAction.ACTION_VIEW %>" scope="page" />
<c:set var="ACTION_LIST" value="<%= CampaignAction.ACTION_LIST %>" scope="page" />


<% SimpleDateFormat parsedate=new SimpleDateFormat("yyyyMMdd");
   DateFormat showdate = DateFormat.getDateInstance(DateFormat.MEDIUM, (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY));
   String aDate = "";
   Date tmpDate = null;
%>

<script src="${emmLayoutBase.jsURL}/tablecolumnresize.js" type="text/javascript"></script>
<script type="text/javascript">
    var prevX = -1;
    var tableID = 'archive_mailing';
    var columnindex = 0;
    var dragging = false;

    document.onmousemove = drag;
    document.onmouseup = dragstop;
</script>

<html:form action="/campaign.do">
    <html:hidden property="action"/>
    <html:hidden property="campaignID"/>

      <div class="mailing_name_box_container">
          <div class="mailing_name_box_top"></div>
          <div class="mailing_name_box_content">
              <div class="mailing_name_box_left_column">
                  <label for="mailing_name"><bean:message key="default.Name"/>:</label>
                  <html:text styleId="mailing_name" property="shortname" maxlength="99" size="42"/>
              </div>
              <div class="mailing_name_box_center_column">
                  <label for="mailing_name"><bean:message key="default.description"/>:</label>
                  <html:textarea styleId="mailing_description" property="description" rows="5" cols="32"/>
              </div>
              <div class="mailing_name_box_right_column"></div>
          </div>
          <div class="mailing_name_box_bottom"></div>
      </div>

      <div class="target_button_container">

          <input type="hidden" id="save" name="save" value=""/>
          <logic:notEqual name="campaignForm" property="campaignID" value="0">
              <agn:ShowByPermission token="campaign.delete">
                  <div class="maildetail_button">
                      <html:link page="/campaign.do?action=${ACTION_CONFIRM_DELETE}&campaignID=${campaignForm.campaignID}">
                          <span><bean:message key="button.Delete"/></span>
                      </html:link>
                  </div>
              </agn:ShowByPermission>
          </logic:notEqual>
          <agn:ShowByPermission token="campaign.change">
              <div class="maildetail_button">
                  <a href="#" onclick="document.campaignForm.submit(); return false;">
                      <span><bean:message key="button.Save"/></span>
                  </a>
              </div>
          </agn:ShowByPermission>
          <div class="maildetail_button"><bean:message key="campaign.Campaign"/>:</div>
      </div>

<c:if test="${campaignForm.campaignID != 0}">

    <div class="export_wizard_content">
        <h2 class="targetgroup_nodes_header"><bean:message key="Mailings"/>:</h2>
    </div>

    <agn:ShowByPermission token="mailing.new">
        <div class="target_button_container before_table_button_container">
            <div class="maildetail_button">
      	        <html:link page="/mailingbase.do?action=${ACTION_NEW}&mailingID=0&campaignID=${campaignForm.campaignID}"><span><bean:message key="mailing.New_Mailing"/></span></html:link>
            </div>
            <div class="maildetail_button"><bean:message key="Mailing"/>:</div>
        </div>
    </agn:ShowByPermission>

    <table border="0" cellspacing="0" cellpadding="0" class="list_table" id="archive_mailing">
        <tr>
            <th><bean:message key="Mailing"/></th>
            <th><bean:message key="default.description"/></th>
            <th><bean:message key="Mailinglist"/></th>
            <th><bean:message key="mailing.senddate"/></th>
            <th class="edit">&nbsp;</th>
        </tr>

        <% int rows = 0; %>
        <c:set var="index" value="0" scope="request"/>
        <agn:ShowTable id="agnTbl" sqlStatement='<%= new String(\"SELECT a.mailing_id, a.shortname, a.description, b.shortname AS listname, (SELECT \"+AgnUtils.sqlDateString(\"min(c.\"+AgnUtils.changeDateName()+\")\", \"yyyymmdd\")+\" FROM mailing_account_tbl c WHERE a.mailing_id=c.mailing_id AND c.status_field=\'W\') AS senddate FROM mailing_tbl a, mailinglist_tbl b WHERE a.company_id=\"+AgnUtils.getCompanyID(request)+ \" AND a.campaign_id=\" + tmpCampaignID + \" AND a.deleted<>1 AND a.is_template=0 AND a.mailinglist_id=b.mailinglist_id ORDER BY senddate DESC, mailing_id DESC\")%>' startOffset="<%= request.getParameter(\"startWith\") %>" maxRows="50" encodeHtml="0">
            <c:set var="trStyle" value="even" scope="request"/>
            <c:if test="${(index mod 2) == 0}">
                <c:set var="trStyle" value="odd" scope="request"/>
            </c:if>
            <c:set var="index" value="${index + 1}" scope="request"/>
            <tr  class="${trStyle}">
                <td class="ie7hack">
                    <span class="ie7hack">
                        <html:link page="/mailingbase.do?action=${ACTION_VIEW}&mailingID=${_agnTbl_mailing_id}">${_agnTbl_shortname}</html:link>
                    </span>
                </td>
                <td class="ie7hack">
                    <span class="ie7hack">
                        <html:link page="/mailingbase.do?action=${ACTION_VIEW}&mailingID=${_agnTbl_mailing_id}"><%= SafeString.cutLength((String)pageContext.getAttribute("_agnTbl_description"), 40) %></html:link>&nbsp;&nbsp;
                    </span>
                </td>
                <td class="ie7hack">
                    <span class="ie7hack">
                        ${_agnTbl_listname}
                    </span>
                </td>
                        <% try{
                             tmpDate = parsedate.parse((String)pageContext.getAttribute("_agnTbl_senddate"));
                             aDate = showdate.format(tmpDate);
                            } catch (Exception e) {
                                 aDate = new String("");
                            }
                        %>
                <td class="ie7hack">
                    <span class="ie7hack">
                        <%= aDate %>&nbsp;
                    </span>
                </td>
                <td>
                    <html:link styleClass="mailing_edit" titleKey="mailing.MailingEdit"
                            page="/mailingbase.do?action=${ACTION_VIEW}&mailingID=${_agnTbl_mailing_id}"/>
                    <agn:ShowByPermission token="mailing.delete">
                        <html:link styleClass="mailing_delete" titleKey="mailing.MailingDelete"
                           page="/mailingbase.do?action=${ACTION_CONFIRM_DELETE}&mailingID=${_agnTbl_mailing_id}"/>
                    </agn:ShowByPermission>
                </td>
            </tr>
            <% rows++; %>
        </agn:ShowTable>
        <% if(rows == 0) { %>
        <tr><td colspan="5"><b><bean:message key="campaign.NoMailingsInCampaign"/>&nbsp;&nbsp;</b></td></tr>
        <% } %>
    </table>

    <div class="table_pages_container">
        <center>
            <agn:ShowTableOffset id="agnTbl" maxPages="10">
                <c:set var="pageClass" value="table_page" scope="request"/>
                <c:if test="${not empty activePage}">
                    <c:set var="pageClass" value="table_page_current" scope="request"/>
                </c:if>
                <html:link page="/campaign.do?action=${ACTION_VIEW}&startWith=${startWith}" styleClass="${pageClass}">
                    ${pageNum}
                </html:link>&nbsp;
            </agn:ShowTableOffset>
        </center>
    </div>


</c:if>
</html:form>

<script type="text/javascript">
    table = document.getElementById('archive_mailing');
    rewriteTableHeader(table);
    writeWidthFromHiddenFields(table);

    $$('#archive_mailing tbody tr').each(function(item) {
        item.observe('mouseover', function() {
            item.addClassName('list_highlight');
        });
        item.observe('mouseout', function() {
            item.removeClassName('list_highlight');
        });
    });
</script>