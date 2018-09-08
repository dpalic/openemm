<%@ page language="java"
         import="org.agnitas.beans.MediatypeEmail, org.agnitas.util.AgnUtils, org.agnitas.web.MailingBaseAction, org.agnitas.web.forms.MailingBaseForm, java.util.Locale"
         contentType="text/html; charset=utf-8" buffer="32kb"  errorPage="/error.jsp" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<% pageContext.setAttribute("CKEDITOR_PATH", AgnUtils.getEMMProperty("ckpath")); %>
<jsp:include page="/${CKEDITOR_PATH}/ckeditor-emm-helper.jsp"/>

<% int tmpMailingID = 0;
    MailingBaseForm aForm = null;

    if ((aForm = (MailingBaseForm) session.getAttribute("mailingBaseForm")) != null) {
        tmpMailingID = aForm.getMailingID();
    }
    Locale aLocale = (Locale) session.getAttribute(org.apache.struts.Globals.LOCALE_KEY);
%>

<!-- E-Mail-Settings -->
<div class="blue_box_container">
    <div class="blue_box_top"></div>
    <div class="blue_box_content">
        <h2 class="blue_box_header email_blue_box_header"><bean:message key="mailing.MediaType.Email"/></h2>

        <div class="blue_box_left_column">
            <div class="blue_box_form_item">
                <label for="emailbox_betreff"><bean:message key="mailing.Subject"/>:</label>
                <html:text styleId="emailbox_betreff" property="emailSubject" maxlength="199"
                           readonly="<%= aForm.isWorldMailingSend() %>"/>
            </div>
            <div class="blue_box_form_item">
                <label for="emailbox_absendermail"><bean:message key="mailing.SenderEmail"/>:</label>
                <html:text styleId="emailbox_absendermail" property="media[0].fromEmail" maxlength="99"
                           readonly="<%= aForm.isWorldMailingSend() %>"/>
            </div>
            <div class="blue_box_form_item">
                <label for="emailbox_absendername"><bean:message key="mailing.SenderFullname"/>:</label>
                <html:text styleId="emailbox_absendername" property="media[0].fromFullname" maxlength="99"
                           readonly="<%= aForm.isWorldMailingSend() %>"/>
            </div>
            <div class="blue_box_form_item">
                <label for="emailbox_antwortemail"><bean:message key="mailing.ReplyEmail"/>:</label>
                <html:text styleId="emailbox_antwortemail" property="media[0].replyEmail" maxlength="99"
                           readonly="<%= aForm.isWorldMailingSend() %>"/>
            </div>
            <div class="blue_box_form_item">
                <label for="emailbox_antwortname"><bean:message key="mailing.ReplyFullName"/>:</label>
                <html:text styleId="emailbox_antwortname" property="media[0].replyFullname" maxlength="99"
                           readonly="<%= aForm.isWorldMailingSend() %>"/>
            </div>
        </div>
        <div class="blue_box_right_column">
            <agn:ShowByPermission token="mailing.show.charsets">
                <div class="blue_box_form_item">
                    <label for="emailbox_zeichensatz"><bean:message key="mailing.Charset"/>:</label>
                    <html:select styleId="emailbox_zeichensatz" property="emailCharset" size="1"
                                 disabled="<%= aForm.isWorldMailingSend() %>">
                        <agn:ShowNavigation navigation="charsets" highlightKey="">
                            <agn:ShowByPermission token="<%= _navigation_token %>">
                                <html:option value="<%= _navigation_href %>"><bean:message
                                        key="<%= _navigation_navMsg %>"/></html:option>
                            </agn:ShowByPermission>
                        </agn:ShowNavigation>
                    </html:select>
                </div>
            </agn:ShowByPermission>
            <div class="blue_box_form_item">
                <label for="emailbox_zeilenumbruch"><bean:message key="mailing.Linefeed_After"/>:</label>
                <html:select styleId="emailbox_zeilenumbruch" property="emailLinefeed" size="1"
                             disabled="<%= aForm.isWorldMailingSend() %>">
                    <html:option value="0"><bean:message key="mailing.No_Linefeed"/></html:option>
                    <%
                        int a;
                        for (a = 60; a <= 80; a++) { %>
                    <html:option value="<%= Integer.toString(a) %>"><%= a %> <bean:message
                            key="mailing.Characters"/></html:option>
                    <% }
                    %>
                </html:select>
            </div>
            <div class="blue_box_form_item">
                <label for="emailbox_format"><bean:message key="action.Format"/>:</label>
                <html:select styleId="emailbox_format" property="mediaEmail.mailFormat" size="1"
                             disabled="<%= aForm.isWorldMailingSend() %>">
                    <html:option value="0"><bean:message key="mailing.only_Text"/></html:option>
                    <html:option value="1"><bean:message key="mailing.Text_HTML"/></html:option>
                    <html:option value="2"><bean:message key="mailing.Text_HTML_OfflineHTML"/></html:option>
                </html:select>
            </div>
            <div class="blue_box_form_item">
                <label for="emailbox_oeffnungsrate"><bean:message key="mailing.openrate.measure"/>:</label>
                <html:select styleId="emailbox_oeffnungsrate" property="emailOnepixel" size="1">
                    <html:option value="<%= MediatypeEmail.ONEPIXEL_TOP %>"><bean:message
                            key="mailing.openrate.top"/></html:option>
                    <html:option value="<%= MediatypeEmail.ONEPIXEL_BOTTOM %>"><bean:message
                            key="mailing.openrate.bottom"/></html:option>
                    <html:option value="<%= MediatypeEmail.ONEPIXEL_NONE %>"><bean:message
                            key="mailing.openrate.none"/></html:option>
                </html:select>
            </div>
        </div>
    </div>
    <div class="blue_box_bottom"></div>
</div>
<agn:ShowByPermission token="template.show">

    <% if (false && !aForm.isIsTemplate()) { %>
    <html:link
            page='<%= new String("/mailingbase.do?action=" + MailingBaseAction.ACTION_VIEW_WITHOUT_LOAD + "&mailingID=" + tmpMailingID + "&showTemplate=false") %>'>&lt;&lt;&lt;&nbsp;<bean:message
            key="mailing.HideTemplate"/></html:link><br><br>
    <% } %>
    <div class="blue_box_container">
        <div class="expand_blue_box_top toggle_open" id="schablonen_container_button"
             onclick="toggleContainer(this, 'templateContainerVisible');toggleContainerStyles(this, 'expand_blue_box_top_bordered', 'expand_blue_box_top');">
            <div class="expand_blue_box_top_subcontainer">
                <a href="#"><bean:message key="mailing.ShowTemplate"/></a>
            </div>
        </div>
        <div>
            <div class="expand_blue_box_content">
                <% if (!(aForm.isCmsMailing(aForm.getMailingID()) && aForm.getMailingID() != 0)) {%>
                <h3><bean:message key="mailing.Text_Version"/>:</h3>

                <div>
                    <html:textarea styleId="schablone_text" property="textTemplate" rows="14" cols="75"/>
                </div>
                <% if (!(aForm.isCmsMailing(aForm.getMailingID()) && aForm.getMailingID() != 0)) {%>
                <c:if test="${mailingBaseForm.mediaEmail.mailFormat != 0}">
                    <h3 class="html_version"><bean:message key="mailing.HTML_Version"/>: <img
                            src="${emmLayoutBase.imagesURL}/edit.gif" border="0" class="ckeditor-toggle-button"
                            onclick="toggleEditor('schablone_html', 700, 400, <%= aForm.getMailingID() %>); return false;" alt="<bean:message key="htmled.title"/>"></h3>

                    <div id="Textarea">
                        <html:textarea property="htmlTemplate" styleId="schablone_html" rows="14" cols="75"
                                       readonly="<%= aForm.isWorldMailingSend() %>"/>
                    </div>
                </c:if>
                <% } %>
                <% } %>
                
                <c:if test="${show_dynamic_template_checkbox}">
	                <html:checkbox styleId="dynamic_template" property="dynamicTemplateString" />
					<label for="dynamic_template">
						<c:if test="${mailingBaseForm.isTemplate}">
							<bean:message key="mailing.dynamic_template.preset" />
						</c:if>
						<c:if test="${not mailingBaseForm.isTemplate}">
							<bean:message key="mailing.dynamic_template" />
						</c:if>
					</label>
                </c:if>

            </div>
            <div class="expand_blue_box_bottom"></div>
        </div>
    </div>
</agn:ShowByPermission>
