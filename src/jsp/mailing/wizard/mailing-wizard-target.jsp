<%-- checked --%>
<%@ page language="java"
         import="org.agnitas.beans.Mailing, org.agnitas.beans.MediatypeEmail, org.agnitas.target.Target, org.agnitas.util.AgnUtils"
         contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.web.MailingWizardAction" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<%
    Mailing mailing = (Mailing) request.getAttribute("mailing");
%>
<div class="new_mailing_content">

    <ul class="new_mailing_step_display">
        <li class="step_display_first"><span>1</span></li>
        <li><span>2</span></li>
        <li><span>3</span></li>
        <li><span>4</span></li>
        <li><span>5</span></li>
        <li><span>6</span></li>
        <li><span class="step_active">7</span></li>
        <li><span>8</span></li>
        <li><span>9</span></li>
        <li><span>10</span></li>
        <li><span>11</span></li>
    </ul>
    <html:form action="/mwTarget">

        <html:hidden property="action"/>
        <input type="hidden" name="removeTargetID" value="0">


        <p><bean:message key="mailing.wizard.MlistTargetMsg"/></p>

        <BR>

        <div class="assistant_step7_form_item">
            <label><bean:message key="Mailinglist"/>:&nbsp;</label>

            <html:select property="mailing.mailinglistID" size="1">
                <agn:ShowTable id="agntbl2"
                               sqlStatement='<%= \"SELECT mailinglist_id, shortname FROM mailinglist_tbl WHERE company_id=\"+AgnUtils.getCompanyID(request) %>'
                               maxRows="100">
                    <html:option
                            value='<%= (String)(pageContext.getAttribute(\"_agntbl2_mailinglist_id\")) %>'><%= pageContext.getAttribute("_agntbl2_shortname") %>
                    </html:option>
                </agn:ShowTable>
            </html:select>

        </div>

        <agn:ShowByPermission token="campaign.show">
            <div class="assistant_step7_form_item">
                <label><bean:message key="campaign.Campaign"/>:&nbsp;</label>

                <html:select property="mailing.campaignID">
                    <html:option value="0"><bean:message key="mailing.NoCampaign"/></html:option>
                    <agn:ShowTable id="agntbl55"
                                   sqlStatement='<%= new String(\"SELECT campaign_id, shortname FROM campaign_tbl WHERE company_id=\"+AgnUtils.getCompanyID(request)+ \" ORDER BY shortname\") %>'
                                   maxRows="500">
                        <html:option
                                value='<%= (String)(pageContext.getAttribute(\"_agntbl55_campaign_id\")) %>'><%= pageContext.getAttribute("_agntbl55_shortname") %>
                        </html:option>
                    </agn:ShowTable>
                </html:select>&nbsp;

            </div>
        </agn:ShowByPermission>
        <div class="assistant_step7_form_item">
            <label><bean:message key="mailing.openrate.measure"/>:&nbsp;</label>

            <html:select property="emailOnepixel" size="1">
                <html:option value="<%= MediatypeEmail.ONEPIXEL_TOP %>"><bean:message
                        key="mailing.openrate.top"/></html:option>
                <html:option value="<%= MediatypeEmail.ONEPIXEL_BOTTOM %>"><bean:message key="mailing.openrate.bottom"/></html:option>
                <html:option value="<%= MediatypeEmail.ONEPIXEL_NONE %>"><bean:message
                        key="mailing.openrate.none"/></html:option>

            </html:select>
        </div>


        <BR><BR>
        <agn:HibernateQuery id="targets"
                            query='<%= \"from Target where companyID=\"+AgnUtils.getCompanyID(request) + \" and deleted=0\" %>'/>
        <div class="assistant_step7_form_item">
            <label><bean:message key="Targets"/>:</label>
            <select name="targetID" size="1">
                <option value="0" selected>---</option>
                <% System.out.println("3"); %>
                <logic:notEmpty name="__targets">
                    <logic:iterate id="dbTarget" name="__targets">
                        <% if (mailing.getTargetGroups() != null && !mailing.getTargetGroups().contains(new Integer(((Target) pageContext.getAttribute("dbTarget")).getId()))) {
                        %>
                        <option value='<%= Integer.toString(((Target)pageContext.getAttribute("dbTarget")).getId()) %>'><%= ((Target) pageContext.getAttribute("dbTarget")).getTargetName() %>
                        </option>
                        <% } %>
                        <% if (mailing.getTargetGroups() == null) {
                        %>
                        <option value='<%= Integer.toString(((Target)pageContext.getAttribute("dbTarget")).getId()) %>'><%= ((Target) pageContext.getAttribute("dbTarget")).getTargetName() %>
                        </option>
                        <% } %>

                    </logic:iterate>
                </logic:notEmpty>

            </select>
            <input type="hidden" name="add_action" value=""/>
            <a href="#"
               onclick="document.mailingWizardForm.add_action.value='add_action' ;document.mailingWizardForm.action.value='${ACTION_TARGET}' ; document.mailingWizardForm.submit(); return false;"
               class="assistant_step7_targetgroups_add"><bean:message key="button.Add"/></a>
        </div>
        <div class="assistant_step7_targetgroups_added_targetgroups">
            <% if (mailing.getTargetGroups() != null && mailing.getTargetGroups().size() > 0) {
                System.out.println("1"); %>
            <logic:iterate name="mailingWizardForm" property="mailing.targetGroups" id="aTarget">
                <% System.out.println("2"); %>
                <logic:notEmpty name="__targets">
                    <logic:iterate id="dbTarget" name="__targets">
                        <logic:equal name="dbTarget" property="id"
                                     value='<%= ((Integer)pageContext.getAttribute(\"aTarget\")).toString() %>'>
                            <div>
                                <a onclick="document.mailingWizardForm.action.value='${ACTION_TARGET}'; document.mailingWizardForm.removeTargetID.value='${dbTarget.id}'; document.mailingWizardForm.targetID.value='0'; document.mailingWizardForm.submit(); return false;"
                                   class="removeTargetgroup" href="#"><img
                                        src="${emmLayoutBase.imagesURL}/removetargetgroup2.png"/></a><%= ((Target) pageContext.getAttribute("dbTarget")).getTargetName() %>
                                &nbsp;</div>
                        </logic:equal>
                    </logic:iterate>
                </logic:notEmpty>
            </logic:iterate>
            <% } else { %>
            <bean:message key="statistic.All_Subscribers"/><br>
            <% } %>
        </div>
        <div class="assistant_step7_checkbox">
            <% if (mailing.getTargetGroups() != null && mailing.getTargetGroups().size() > 1) { %>
            <br><input type="hidden" name="__STRUTS_CHECKBOX_mailing.targetMode" value="0"/>
            <html:checkbox property="mailing.targetMode" value="1">&nbsp;<bean:message
                    key="mailing.targetmode.and"/></html:checkbox>
            <% } else { %>
            <html:hidden property="mailing.targetMode"/>
            <% } %>
        </div>

        <div class="assistant_step7_button_container">
            <div class="maildetail_button"><a href="#"
                                              onclick="document.mailingWizardForm.action.value='${ACTION_FINISH}'; document.mailingWizardForm.submit(); return false;"><span><bean:message
                    key="button.Finish"/></span></a></div>
            <div class="maildetail_button"><a href="#"
                                              onclick="document.mailingWizardForm.action.value='next'; document.mailingWizardForm.submit(); return false;"><span><bean:message
                    key="button.Proceed"/></span></a></div>
            <div class="maildetail_button"><a href="#"
                                              onclick="document.mailingWizardForm.action.value='previous'; document.mailingWizardForm.submit(); return false;"><span><bean:message
                    key="button.Back"/></span></a></div>
        </div>
    </html:form>
</div>

