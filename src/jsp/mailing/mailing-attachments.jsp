<%--checked --%>
<%@ page language="java"
         import="org.agnitas.beans.MailingComponent, org.agnitas.dao.TargetDao, org.agnitas.target.Target, org.agnitas.util.AgnUtils, org.springframework.context.ApplicationContext, org.springframework.web.context.support.WebApplicationContextUtils"
         contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.web.forms.MailingAttachmentsForm" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    MailingAttachmentsForm aForm = null;
    if (request.getAttribute("mailingAttachmentsForm") != null) {
        aForm = (MailingAttachmentsForm) request.getAttribute("mailingAttachmentsForm");
    }
%>

<%--<div class="emailbox_container">--%>
<html:form action="/mailingattachments" enctype="multipart/form-data">
    <html:hidden property="mailingID"/>
    <html:hidden property="action"/>
    <div id="filterbox_container" style="margin-left:28px; float:none;">
        <div class="filterbox_form_container">
            <div id="filterbox_top"></div>
            <div id="suchbox_content" class="filterbox_form_container">
                <label style="margin-left:10px;">${mailingAttachmentsForm.shortname}&nbsp;&nbsp;<%if (aForm != null && aForm.getDescription() != null && !aForm.getDescription().isEmpty()) {%>
                    |&nbsp;&nbsp;${mailingAttachmentsForm.description}<% } %></label>
            </div>
            <div id="filterbox_bottom"></div>
        </div>
    </div>
    <div class="mailing_name_box_container">
        <div class="mailing_name_box_top"></div>
        <div class="mailing_name_box_content">
            <div class="assistant_step7_form_item">
                <label><bean:message key="mailing.New_Attachment"/>:</label>
            </div>
            <div class="assistant_step7_form_item">
                <label><bean:message key="mailing.Attachment"/>:&nbsp;</label>
                <html:file property="newAttachment" styleId="newAttachment" onchange="getFilename()"/>
            </div>
            <div class="assistant_step7_form_item">
                <label><bean:message key="mailing.attachment.name"/>:&nbsp;</label>
                <html:text property="newAttachmentName" styleId="newAttachmentName"/>
            </div>
            <div class="assistant_step7_form_item">
                <label><bean:message key="target.Target"/>:&nbsp;</label>
                <html:select property="attachmentTargetID" size="1">
                    <html:option value="0"><bean:message key="statistic.All_Subscribers"/></html:option>
                    <agn:ShowTable id="agntbl3"
                                   sqlStatement='<%= \"select target_id, target_shortname from dyn_target_tbl where company_id=\"+AgnUtils.getCompanyID(request) + \" and deleted=0\" %>'
                                   maxRows="500">
                        <html:option
                                value='<%= (String)(pageContext.getAttribute(\"_agntbl3_target_id\")) %>'><%= pageContext.getAttribute("_agntbl3_target_shortname") %>
                        </html:option>
                    </agn:ShowTable>
                </html:select>
            </div>
            <logic:equal name="mailingAttachmentsForm" property="worldMailingSend" value="false">
                <div class="maildetail_button mailingwizard_add_button">
                    <input type="hidden" name="add" value=""/>
                    <a href="#"
                       onclick="document.mailingAttachmentsForm.add.value='add'; document.mailingAttachmentsForm.submit(); return false;"><span><bean:message
                            key="button.Add"/></span></a>
                </div>
            </logic:equal>

        </div>
        <div class="mailing_name_box_bottom"></div>
    </div>


    <% int i = 1;
        boolean isFirst = true; %>
    <% if (isFirst) {
        isFirst = false; %>
    <%--<div class="assistant_step7_form_item">--%>
    <%--<label><bean:message key="mailing.Attachments"/>:</label>--%>
    <%--</div>--%>
    <% } %>
    <% MailingComponent comp = null; %>
    <agn:HibernateQuery id="attachment"
                        query='<%= \"from MailingComponent where companyID=\"+AgnUtils.getCompanyID(request)+\" and mailingID=\"+request.getAttribute("tmpMailingID")+\" and comptype=\"+MailingComponent.TYPE_ATTACHMENT %>'>
        <% comp = (MailingComponent) pageContext.getAttribute("attachment");
            ApplicationContext aContext = WebApplicationContextUtils.getWebApplicationContext(application);
            TargetDao dao = (TargetDao) aContext.getBean("TargetDao");
            Target aTarget = dao.getTarget(comp.getTargetID(), AgnUtils.getCompanyID(request));
            String targetShortname = null;
            if (aTarget != null) {
                targetShortname = aTarget.getTargetName();
            }
        %>
        <div class="mailing_name_box_container">
            <div class="mailing_name_box_top"></div>
            <div class="mailing_name_box_content">
                <div class="assistant_step7_form_item">
                    <bean:message key="mailing.Attachment"/>:&nbsp;<html:link
                        page='<%= \"/sc?compID=\" + comp.getId() %>'><%= comp.getComponentName() %>&nbsp;&nbsp;<img src="${emmLayoutBase.imagesURL}/download.gif" border="0" alt="<bean:message
                        key='button.Download'/>"></html:link> <br><br>
                    <input type="hidden" name="compid<%= i++ %>" value="${_agntbl1_component_id}">

                    <label for='<%= new String("target"+comp.getId()) %>' size="1"
                           value="<%= String.valueOf(comp.getTargetID()) %>"> <bean:message
                            key="target.Target"/>:&nbsp;</label>
                    <html:select property='<%= new String("target"+comp.getId()) %>' size="1"
                                 value="<%= String.valueOf(comp.getTargetID()) %>"
                                 styleId='<%= new String("target"+comp.getId()) %>'>
                        <html:option value="0"><bean:message key="statistic.All_Subscribers"/></html:option>
                        <agn:ShowTable id="agntbl3"
                                       sqlStatement='<%= new String("SELECT target_id, target_shortname,deleted FROM dyn_target_tbl WHERE company_id="+AgnUtils.getCompanyID(request)) %>'
                                       maxRows="500">
                            <html:option
                                    value='<%= (String)(pageContext.getAttribute("_agntbl3_target_id")) %>'><%= pageContext.getAttribute("_agntbl3_target_shortname") %>

                                <c:if test="${_agntbl3_deleted != null && _agntbl3_deleted==1}">
                                    (<bean:message key="target.Deleted"/>)
                                </c:if>
                            </html:option>
                        </agn:ShowTable>
                    </html:select>
                    &nbsp;<br>

                    &nbsp;<br>
                    <bean:message key="mailing.Mime_Type"/>:&nbsp;<%= comp.getMimeType() %>&nbsp;<br>
                    <bean:message key="mailing.Original_Size"/>:&nbsp;<%= comp.getBinaryBlock().length / 1024 %>
                    &nbsp;<bean:message key="default.KByte"/><br>
                    <bean:message key="default.Size_Mail"/>:&nbsp;<%= comp.getBinaryBlock().length / 1024 * 4/3 %>
                    &nbsp;<bean:message key="default.KByte"/><br><br>
                </div>
                <logic:equal name="mailingAttachmentsForm" property="worldMailingSend" value="false">
                    <div class="maildetail_button mailingwizard_add_button">
                        <div class="maildetail_button attachment_button">
                            <input type="hidden" name='<%= new String("delete"+comp.getId()) %>' value=""/>
                            <a href="#"
                               onclick="document.mailingAttachmentsForm.<%= new String("delete"+comp.getId()) %>.value='delete'; document.mailingAttachmentsForm.submit(); return false;"><span><bean:message
                                    key="button.Delete"/></span></a>
                        </div>
                    </div>
                </logic:equal>
            </div>
            <div class="mailing_name_box_bottom"></div>
        </div>

    </agn:HibernateQuery>

    <logic:equal name="mailingAttachmentsForm" property="worldMailingSend" value="false">
        <div class="maildetail_button_container attachments_button_container">
            <input type="hidden" name="save" value=""/>
            <div class="maildetail_button">
                <a href="#"
                   onclick="document.mailingAttachmentsForm.save.value='save'; document.mailingAttachmentsForm.submit(); return false;"><span><bean:message
                        key="button.Save"/></span></a>
            </div>
            <div class="maildetail_button"><bean:message key="mailing.Attachments"/>:</div>
        </div>
    </logic:equal>


</html:form>
<script language="JavaScript">
    <!--
    function getFilename() {
        document.getElementById("newAttachmentName").value = document.getElementById("newAttachment").value.match(/[^\\\/]+$/);
    }
    //-->
</script>