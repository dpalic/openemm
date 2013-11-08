<%--
/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/
 --%>
<%@ page language="java"
         import="org.agnitas.beans.Mailing, org.agnitas.beans.MediatypeEmail,org.agnitas.cms.utils.CmsUtils, org.agnitas.util.AgnUtils, org.agnitas.web.MailingBaseAction"
         contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.web.forms.MailingBaseForm" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<% String aNameBase = null;
    String aNamePart = null;
    String aName = null;
    String aktName = new String("");
%>

<% int tmpMailingID = 0;
    MailingBaseForm aForm = null;
    String tmpShortname = new String("");
    if ((aForm = (MailingBaseForm) session.getAttribute("mailingBaseForm")) != null) {
        tmpMailingID = ((MailingBaseForm) session.getAttribute("mailingBaseForm")).getMailingID();
        tmpShortname = ((MailingBaseForm) session.getAttribute("mailingBaseForm")).getShortname();
    }
    if (aForm.isIsTemplate()) {
        aForm.setShowTemplate(true);
    }
    String permToken = null;
    boolean showTargetMode = false;
    boolean showNeedsTarget = false;
    if (aForm.getTargetGroups() != null) {
        if (aForm.getTargetGroups().size() >= 2) {
            showTargetMode = true;
        }
    }
%>


<h3 class="header_coloured"><bean:message key="Settings"/>:</h3>

<div class="expand_box_container">
    <div class="expand_box_top toggle_open" id="settings_general_container_button"
         onclick="toggleContainer(this, 'generalContainerVisible');">
        <div class="expand_box_top_subcontainer"><a href="#"><bean:message key="General"/>:</a></div>
    </div>
    <div>
        <div class="expand_box_content">
            <div class="settings_general_left_column">
                <logic:equal name="mailingBaseForm" property="isTemplate" value="false">
                    <div class="settings_general_form_item">
                        <logic:equal name="mailingBaseForm" property="mailingID" value="0">
                            <logic:equal name="mailingBaseForm" property="copyFlag" value="false">
                                <label for="settings_general_template"><bean:message key="Template"/>:</label>
                                <html:select styleId="settings_general_template" property="templateID"
                                             onchange="document.mailingBaseForm.action.value=7; document.mailingBaseForm.submit();">
                                    <html:option value="0"><bean:message key="mailing.No_Template"/></html:option>
                                    <agn:ShowTable id="agntbl3"
                                                   sqlStatement='<%= new String("SELECT mailing_id, shortname FROM mailing_tbl WHERE company_id="+AgnUtils.getCompanyID(request)+" AND is_template=1 AND deleted=0 ORDER BY shortname") %>'
                                                   maxRows="500">
                                        <html:option
                                                value="<%= (String)(pageContext.getAttribute(\"_agntbl3_mailing_id\")) %>"><%= pageContext.getAttribute("_agntbl3_shortname") %>
                                        </html:option>
                                    </agn:ShowTable>
                                </html:select>
                            </logic:equal>
                            <logic:equal name="mailingBaseForm" property="copyFlag" value="true">
                                <label for="settings_general_schablone"><bean:message key="Template"/>:</label>
                                <% boolean template = false; %>
                                <agn:ShowTable id="agntbl44"
                                               sqlStatement='<%= new String("SELECT shortname FROM mailing_tbl WHERE mailing_id=" + ((MailingBaseForm)session.getAttribute("mailingBaseForm")).getTemplateID() + " AND company_id=" + AgnUtils.getCompanyID(request)) %>'
                                               maxRows="500">
                                    <% if (((String) (pageContext.getAttribute("_agntbl44_shortname"))).compareTo("") != 0) {
                                        template = true; %>
                                    <%= (String) (pageContext.getAttribute("_agntbl44_shortname")) %>
                                    <% } %>
                                </agn:ShowTable>
                                <% if (!template) { %>
                                <bean:message key="mailing.No_Template"/>
                                <% } %>
                            </logic:equal>
                        </logic:equal>
                        <logic:notEqual name="mailingBaseForm" property="mailingID" value="0">
                            <label for="settings_general_schablone"><bean:message key="Template"/>:</label>
                            <% boolean template = false; %>
                            <agn:ShowTable id="agntbl44"
                                           sqlStatement='<%= new String("SELECT shortname FROM mailing_tbl WHERE mailing_id=" + ((MailingBaseForm)session.getAttribute("mailingBaseForm")).getTemplateID() + " AND company_id=" + AgnUtils.getCompanyID(request)) %>'
                                           maxRows="500">
                                <% if (((String) (pageContext.getAttribute("_agntbl44_shortname"))).compareTo("") != 0) {
                                    template = true; %>
                                <%= (String) (pageContext.getAttribute("_agntbl44_shortname")) %>
                                <% } %>
                            </agn:ShowTable>
                            <% if (!template) { %>
                            <bean:message key="mailing.No_Template"/>
                            <% } %>
                        </logic:notEqual>
                    </div>
                </logic:equal>
                <div class="settings_general_form_item">
                    <label for="settings_general_mailingliste"><bean:message key="Mailinglist"/>:</label>
                    <html:select styleId="settings_general_mailingliste" property="mailinglistID" size="1"
                                 disabled="<%= aForm.isWorldMailingSend() %>" styleClass="dropdown">
                        <agn:ShowTable id="agntbl2"
                                       sqlStatement='<%= new String("SELECT mailinglist_id, shortname FROM mailinglist_tbl WHERE company_id="+AgnUtils.getCompanyID(request)+ " ORDER BY lower(shortname)") %>'>
                            <html:option
                                    value="<%= (String)(pageContext.getAttribute(\"_agntbl2_mailinglist_id\")) %>"><%= pageContext.getAttribute("_agntbl2_shortname") %>
                            </html:option>
                        </agn:ShowTable>
                    </html:select>
                </div>
                <agn:ShowByPermission token="campaign.show">
                    <div class="settings_general_form_item">
                        <label for="settings_general_campaign"><bean:message key="campaign.Campaign"/>:</label>
                        <html:select styleId="settings_general_campaign" property="campaignID">
                            <html:option value="0"><bean:message key="mailing.NoCampaign"/></html:option>
                            <agn:ShowTable id="agntbl55"
                                           sqlStatement='<%= new String("SELECT campaign_id, shortname FROM campaign_tbl WHERE company_id="+AgnUtils.getCompanyID(request)+ " ORDER BY lower(shortname)") %>'
                                           maxRows="500">
                                <html:option
                                        value="<%= (String)(pageContext.getAttribute(\"_agntbl55_campaign_id\")) %>"><%= pageContext.getAttribute("_agntbl55_shortname") %>
                                </html:option>
                            </agn:ShowTable>
                        </html:select>
                    </div>
                </agn:ShowByPermission>

                <agn:ShowByPermission token="action.op.GetArchiveList">
                    <div class="settings_general_form_item">
                        <input type="hidden" name="__STRUTS_CHECKBOX_archived" value="0"/>
                        <html:checkbox styleId="settings_general_in_archiv" property="archived"/>
                        <label for="settings_general_in_archiv" id="settings_general_in_archiv_label"><bean:message
                                key="mailing.archived"/></label>
                    </div>
                </agn:ShowByPermission>
            </div>
            <div class="settings_general_right_column">
                <agn:ShowByPermission token="mailing.show.types">
                    <div class="settings_general_form_item">
                        <label for="mailType"><bean:message key="mailing.Mailing_Type"/>:</label>
                        <html:select property="mailingType" size="1" styleId="mailType" onchange="checkButton(this.id)"
                                     disabled="<%= aForm.isWorldMailingSend() %>">
                            <html:option value="<%= Integer.toString(Mailing.TYPE_NORMAL) %>"><bean:message
                                    key="mailing.Normal_Mailing"/></html:option>
                            <html:option value="<%= Integer.toString(Mailing.TYPE_ACTIONBASED) %>"><bean:message
                                    key="mailing.Event_Mailing"/></html:option>
                            <html:option value="<%= Integer.toString(Mailing.TYPE_DATEBASED) %>"><bean:message
                                    key="mailing.Rulebased_Mailing"/></html:option>
                        </html:select>
                    </div>
                </agn:ShowByPermission>
            </div>
        </div>
        <div class="expand_box_bottom"></div>
    </div>
</div>


<div class="expand_box_container">
    <div class="expand_box_top toggle_open" id="settings_targetgroups_container_button"
         onclick="toggleContainer(this, 'targetgroupsContainerVisible');">
        <div class="expand_box_top_subcontainer">
            <a href="#"><bean:message key="Targets"/>:</a>
        </div>
    </div>
    <div>
        <div class="expand_box_content">
            <div class="settings_targetgroups_left_column">
                <div class="targetgroups_select_container">
                    <logic:equal name="mailingBaseForm" property="worldMailingSend" value="false">
                        <div class="float_left">
                            <html:select property="targetID" size="1" styleClass="dropdown">
                                <html:option value="0">---</html:option>
                                <agn:ShowTable id="agntbl3"
                                               sqlStatement='<%= new String("SELECT target_id, target_shortname FROM dyn_target_tbl WHERE company_id="+AgnUtils.getCompanyID(request)+" AND deleted=0 ORDER BY lower(target_shortname)") %>'
                                               maxRows="500">
                                    <% if (aForm.getTargetGroups() != null && !aForm.getTargetGroups().contains(Integer.valueOf((String) (pageContext.getAttribute("_agntbl3_target_id"))))) {
                                    %>
                                    <html:option
                                            value="<%= (String)(pageContext.getAttribute(\"_agntbl3_target_id\")) %>"><%= pageContext.getAttribute("_agntbl3_target_shortname") %>
                                    </html:option>
                                    <% } %>
                                    <% if (aForm.getTargetGroups() == null) {
                                    %>
                                    <html:option
                                            value="<%= (String)(pageContext.getAttribute(\"_agntbl3_target_id\")) %>"><%= pageContext.getAttribute("_agntbl3_target_shortname") %>
                                    </html:option>
                                    <% } %>

                                </agn:ShowTable>
                            </html:select>
                        </div>
                        <div class="float_left add_target_button">
                            <input type="hidden" name="addtarget" value=""/>
                            <a href="#" class="settings_targetgroups_add"
                               onclick="document.mailingBaseForm.addtarget.value='addtarget'; document.mailingBaseForm.submit();return false;"><bean:message key="button.Add"/></a>
                        </div>
                    </logic:equal>
                </div>
                <div class="settings_targetgroups_added_targetgroups">
                    <logic:present name="mailingBaseForm" property="targetGroups">
                        <logic:iterate name="mailingBaseForm" property="targetGroups" id="aTarget">
                            <agn:ShowTable id="agntbl3a"
                                           sqlStatement='<%= "SELECT target_shortname, deleted FROM dyn_target_tbl WHERE company_id="+AgnUtils.getCompanyID(request)+" AND target_id="+pageContext.getAttribute("aTarget") %>'
                                           maxRows="100">
                                <c:choose>
                                    <c:when test="${_agntbl3a_deleted == 0}">
                                        <div>${_agntbl3a_target_shortname}
                                            &nbsp;
                                            <logic:equal name="mailingBaseForm" property="worldMailingSend"
                                                         value="false">
                                                <input type="hidden" name="removetarget${aTarget}" value=""/>
                                                <a href="#"
                                                   onclick="document.mailingBaseForm.removetarget${aTarget}.value='removetarget${aTarget}';document.mailingBaseForm.submit();return false;"
                                                   class="removeTargetgroup"><img
                                                        src="${emmLayoutBase.imagesURL}/removetargetgroup.png"/></a>
                                            </logic:equal>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
						     	 <span class="warning">${_agntbl3a_target_shortname} (<bean:message
                                          key="target.Deleted"/>)&nbsp;
                                      <logic:equal name="mailingBaseForm" property="worldMailingSend" value="false">
                                          <input type="hidden" name="removetarget${aTarget}" value=""/>
                                          <a href="#"
                                             onclick="document.mailingBaseForm.removetarget${aTarget}.value='removetarget${aTarget}';document.mailingBaseForm.submit();return false;"
                                             class="removeTargetgroup">
                                              <img src="${emmLayoutBase.imagesURL}/removetargetgroup.png"/>
                                          </a>
                                      </logic:equal>
                                  </span></br>
                                    </c:otherwise>
                                </c:choose>
                            </agn:ShowTable>
                        </logic:iterate>
                    </logic:present>
                    <logic:notPresent name="mailingBaseForm" property="targetGroups">
                        <div><bean:message key="statistic.All_Subscribers"/></div>
                    </logic:notPresent>
                </div>
            </div>
            <div class="settings_targetgroups_right_column">
                <% if (showTargetMode) { %>
                <input type="hidden" name="__STRUTS_CHECKBOX_targetMode" value="0"/>

                <div class="settings_targetgroups_form_item">
                    <html:checkbox styleId="checkbox_target_mode" property="targetMode" value="1"
                                   disabled="<%= aForm.isWorldMailingSend() %>"/>
                    <label for="checkbox_target_mode" id="checkbox_target_mode_label"><bean:message
                            key="mailing.targetmode.and"/></label>
                </div>
                <% if (aForm.isWorldMailingSend()) { %>
                <html:hidden property="targetMode"/>
                <% } %>
                <% } else { %>
                <html:hidden property="targetMode"/>
                <% } %>
                <% if (showNeedsTarget) { %>
                <input type="hidden" name="__STRUTS_CHECKBOX_needsTarget" value="false"/>

                <div class="settings_targetgroups_form_item">
                    <html:checkbox styleId="checkbox_needs_target" property="needsTarget"
                                   disabled="<%= (aForm.isWorldMailingSend() || (aForm.getMailingType()==Mailing.TYPE_DATEBASED)) %>"/>
                    <label for="checkbox_needs_target" id="checkbox_needs_target_label"><bean:message
                            key="mailing.needsTarget"/></label>
                </div>
                <% if (aForm.isWorldMailingSend() || (aForm.getMailingType() == Mailing.TYPE_DATEBASED)) { %>
                <html:hidden property="needsTarget"/>
                <% } %>
                <% } else { %>
                <html:hidden property="needsTarget"/>
                <% } %>
            </div>

        </div>
        <div class="expand_box_bottom"></div>
    </div>
</div>
<br>