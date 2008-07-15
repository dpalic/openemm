<%@ page language="java" import="org.agnitas.util.*,java.util.*,java.text.*,org.agnitas.web.*, org.agnitas.beans.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>
<agn:Permission token="mailing.send.world"/>

<% MailingSendForm aForm=null;
    int tmpMailingID=0;
    String tmpShortname=new String("");
    if(request.getAttribute("mailingSendForm")!=null) {
        aForm=(MailingSendForm)request.getAttribute("mailingSendForm");
        tmpMailingID=aForm.getMailingID();
        tmpShortname=aForm.getShortname();
    }
    int i;
    TimeZone aZone=TimeZone.getTimeZone(AgnUtils.getAdmin(request).getAdminTimezone());
    GregorianCalendar aDate=new GregorianCalendar(aZone);
    DateFormat showFormat=DateFormat.getDateInstance(DateFormat.FULL, (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY));
    DateFormat internalFormat=new SimpleDateFormat("yyyyMMdd");
    NumberFormat aFormat=new DecimalFormat("00");
%>

<% pageContext.setAttribute("sidemenu_active", new String("Mailings")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("none")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Mailing")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Mailing")); %>
<% pageContext.setAttribute("agnSubtitleValue", tmpShortname); %>
<% pageContext.setAttribute("agnNavigationKey", new String("mailingView")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Send_Mailing")); %>
<% pageContext.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID)); %>
<%@include file="/header.jsp"%>

<table border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td><span class="head3"><bean:message key="MailingSend"/></span></td>
        <td></td>
    </tr>
    <tr><td>
        <br><bean:message key="MailingSendXplain"/><br><br><hr>
        <span class="head3"><bean:message key="RecipientSelection"/></span><br><br>

        <html:form action="/mailingsend">
            <html:hidden property="action"/>
            <html:hidden property="mailingID"/>
            <bean:message key="RecipientsXplain1"/><b>
            <% if(aForm.getTargetGroups()==null) { %>
            <bean:message key="All_Subscribers"/>
            <% } else { boolean isFirst=true; %>
            <logic:iterate name="mailingSendForm" property="targetGroups" id="trgt">
                <agn:ShowTable id="agntbl3" sqlStatement="<%= new String("SELECT target_shortname FROM dyn_target_tbl WHERE company_id="+AgnUtils.getCompanyID(request)+" AND target_id="+pageContext.getAttribute("trgt")) %>" maxRows="1">
                    <% if(isFirst) { isFirst=false; } else { %>/&nbsp;<% } %><%= pageContext.getAttribute("_agntbl3_target_shortname") %>
                </agn:ShowTable>
            </logic:iterate>
            <% } %></b> <bean:message key="RecipientsXplain2"/> <bean:write name="mailingSendForm" property="sendStatAll" scope="request"/> <bean:message key="RecipientsXplain3"/><br><br>
            <bean:write name="mailingSendForm" property="sendStatText" scope="request"/> Text-E-Mails<br>
            <bean:write name="mailingSendForm" property="sendStatHtml" scope="request"/> HTML-E-Mails<br>
            <bean:write name="mailingSendForm" property="sendStatOffline" scope="request"/> Offline-Html-E-Mails<br>

            <br><hr>

            <span class="head3"><bean:message key="SendingTime"/></span><br><br>
            <bean:message key="Date"/>:
            <html:select property="sendDate" size="1">
                <% for(i=1; i<=31; i++) { %>
                <html:option value="<%= internalFormat.format(aDate.getTime()) %>"><%= showFormat.format(aDate.getTime()) %></html:option>
                <%   aDate.add(Calendar.DATE, 1);
                    }
                %>
            </html:select>
            &nbsp;&nbsp;&nbsp;<bean:message key="Time"/>:
            <html:select property="sendHour" size="1">
                <%
                    for(i=0; i<=23; i++) { %>
                <html:option value="<%= aFormat.format((long)i) %>"><%= aFormat.format((long)i) %></html:option>
                <%  }
                %>
            </html:select>:
            <html:select property="sendMinute" size="1">
                <%
                    for(i=0; i<=59; i++) { %>
                <html:option value="<%= aFormat.format((long)i) %>"><%= aFormat.format((long)i) %></html:option>
                <%  }
                %>
            </html:select>&nbsp;<%= aZone.getID() %>
            <br><br>
            <agn:ShowByPermission token="mailing.send.admin.options">
                Stepping:&nbsp;<input type="text" name="step" value="0"><br>
                Blocksize:&nbsp;<input type="text" name="blocksize" value="0"><br><br>
            </agn:ShowByPermission>
            <html:image src="button?msg=Send" property="send" border="0" value="send"/>
            &nbsp;
            <html:link page="<%= new String("/mailingsend.do?action="+MailingSendAction.ACTION_VIEW_SEND+"&mailingID="+tmpMailingID) %>">
            <html:img src="button?msg=Cancel" border="0"/></html:link><br>

        </html:form>
				</td></tr>
</table>
<%@include file="/footer.jsp"%>
