<%-- checked --%>
<%@ page language="java"
         import="org.agnitas.util.AgnUtils,org.agnitas.web.MailingSendAction,org.agnitas.web.MailingSendForm,java.text.DateFormat, java.text.DecimalFormat"
         contentType="text/html; charset=utf-8" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.GregorianCalendar" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.TimeZone" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<% MailingSendForm aForm = null;
    int tmpMailingID = 0;
    if (request.getAttribute("mailingSendForm") != null) {
        aForm = (MailingSendForm) request.getAttribute("mailingSendForm");
        tmpMailingID = aForm.getMailingID();
    }
    int i;
    TimeZone aZone = TimeZone.getTimeZone(AgnUtils.getAdmin(request).getAdminTimezone());
    GregorianCalendar aDate = new GregorianCalendar(aZone);
    DateFormat showFormat = DateFormat.getDateInstance(DateFormat.FULL, (Locale) session.getAttribute(org.apache.struts.Globals.LOCALE_KEY));
    showFormat.setTimeZone(aZone);
    DateFormat internalFormat = new SimpleDateFormat("yyyyMMdd");
    NumberFormat aFormat = new DecimalFormat("00");
%>

<div class="assistant_sendmail_confirm_form_item">

<div class="mailing_name_box_container">
    <div class="mailing_name_box_top"></div>
    <div class="mailing_name_box_content">
        <span class="send_page_header"><bean:message key="mailing.MailingSend"/></span>
        <br>
        <br><bean:message key="mailing.MailingSendXplain"/><br>
    </div>
    <div class="mailing_name_box_bottom"></div>
</div>

<div class="mailing_name_box_container">
<div class="mailing_name_box_top"></div>
<div class="mailing_name_box_content">
<span class="send_page_header"><bean:message key="recipient.RecipientSelection"/></span><br><br>

<html:form action="/mailingsend">
    <html:hidden property="action"/>
    <html:hidden property="mailingID"/>
    <bean:message key="mailing.RecipientsXplain1"/><b>
    <% if (aForm.getTargetGroups() == null) { %>
    <bean:message key="statistic.All_Subscribers"/>
    <% } else {
        boolean isFirst = true; %>
    <logic:iterate name="mailingSendForm" property="targetGroups" id="trgt">
        <agn:ShowTable id="agntbl3"
                       sqlStatement='<%= new String(\"SELECT target_shortname FROM dyn_target_tbl WHERE company_id=\"+AgnUtils.getCompanyID(request)+\" AND target_id=\"+pageContext.getAttribute(\"trgt\")) %>'
                       maxRows="1">
            <% if (isFirst) {
                isFirst = false;
            } else { %>/&nbsp;<% } %><%= pageContext.getAttribute("_agntbl3_target_shortname") %>
        </agn:ShowTable>
    </logic:iterate>
    <% } %></b> <bean:message key="mailing.RecipientsXplain2"/> <bean:write name="mailingSendForm"
                                                                            property="sendStatAll" scope="request"/>
    <bean:message key="mailing.RecipientsXplain3"/><br><br>
    <bean:write name="mailingSendForm" property="sendStatText" scope="request"/> Text-E-Mails<br>
    <bean:write name="mailingSendForm" property="sendStatHtml" scope="request"/> HTML-E-Mails<br>
    <bean:write name="mailingSendForm" property="sendStatOffline" scope="request"/> Offline-Html-E-Mails<br>
    </div>

    <div class="mailing_name_box_bottom"></div>
    </div>

    <div class="mailing_name_box_container">
        <div class="mailing_name_box_top"></div>
        <div class="mailing_name_box_content">
            <span class="send_page_header"><bean:message key="mailing.SendingTime"/></span><br><br>

            <div class="send_mail_send_time">
                <div>
                    <label for="sendDate">
                        <bean:message key="statistic.Date"/>:</label>
                    <html:select property="sendDate" size="1" styleId="sendDate">
                        <% for (i = 1; i <= 31; i++) { %>
                        <html:option
                                value="<%= internalFormat.format(aDate.getTime()) %>"><%= showFormat.format(aDate.getTime()) %>
                        </html:option>
                        <% aDate.add(Calendar.DATE, 1);
                        }
                        %>
                    </html:select>
                </div>
            </div>
            <label for="sendHour"><bean:message key="default.Time"/>:</label>

            <div id="sendHour" class="maildetail_button mailingwizard_add_button mailing_send_time_container">
                <html:select property="sendHour" size="1">
                    <%
                        for (i = 0; i <= 23; i++) { %>
                    <html:option value="<%= String.valueOf(i) %>"><%= aFormat.format((long) i) %>
                    </html:option>
                    <% }
                    %>
                </html:select>&nbsp;:
                <html:select property="sendMinute" size="1">
                    <%
                        for (i = 0; i <= 59; i++) { %>
                    <html:option value="<%= String.valueOf(i) %>"><%= aFormat.format((long) i) %>
                    </html:option>
                    <% }
                    %>
                </html:select>&nbsp;
            </div>
            <label><%= aZone.getID() %>
            </label>
        </div>
        <div class="mailing_name_box_bottom"></div>
    </div>

    <agn:ShowByPermission token="mailing.send.admin.options">
    <div class="mailing_name_box_container">
        <div class="mailing_name_box_top"></div>
        <div class="mailing_name_box_content">
            <div class="send_mail_field">
                <label><bean:message key="blocksize"/>:</label>
                <html:text property="blocksize" size="5"/>
            </div>
            <div class="send_mail_field">
                <label><bean:message key="stepping"/>:</label>
                <html:text property="stepping" size="2"/>
            </div>
        </div>
        <div class="mailing_name_box_bottom"></div>
    </div>
    </agn:ShowByPermission>
    <br>
    <div class="maildetail_button_container">
        <input type="hidden" name="send" value=""/>

        <div class="maildetail_button"><a href="#"
                                          onclick="document.mailingSendForm.send.value='send'; document.mailingSendForm.submit();return false;"><span><bean:message
                key="button.Send"/></span></a></div>
        <div class="maildetail_button"><html:link
                page='<%= new String("/mailingsend.do?action="+MailingSendAction.ACTION_VIEW_SEND+"&mailingID="+tmpMailingID) %>'><span><bean:message
                key="button.Cancel"/></span></html:link></div>
    </div>

</html:form>
</div>
