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
 --%><%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*, java.util.*, org.agnitas.stat.*, java.text.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="mailing.send.show"/>

<% int tmpMailingID=0;
    String tmpShortname=new String("");
    DeliveryStat aDelstat=null;
    MailingSendForm aForm=null;
    if(request.getAttribute("mailingSendForm")!=null) {
        aForm=(MailingSendForm)request.getAttribute("mailingSendForm");
        tmpMailingID=aForm.getMailingID();
        tmpShortname=aForm.getShortname();
        aDelstat=aForm.getDeliveryStat();
    }
%>

<logic:equal name="mailingSendForm" property="isTemplate" value="true">
    <% // template navigation:
        pageContext.setAttribute("sidemenu_active", new String("Templates"));
        pageContext.setAttribute("sidemenu_sub_active", new String("none"));
        pageContext.setAttribute("agnNavigationKey", new String("templateView"));
        pageContext.setAttribute("agnHighlightKey", new String("Send_Mailing"));
        pageContext.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
        pageContext.setAttribute("agnTitleKey", new String("Template"));
        pageContext.setAttribute("agnSubtitleKey", new String("Template"));
        pageContext.setAttribute("agnSubtitleValue", tmpShortname);
    %>
</logic:equal>

<logic:equal name="mailingSendForm" property="isTemplate" value="false">
    <%
        // mailing navigation:
        pageContext.setAttribute("sidemenu_active", new String("Mailings"));
        pageContext.setAttribute("sidemenu_sub_active", new String("none"));
        pageContext.setAttribute("agnNavigationKey", new String("mailingView"));
        pageContext.setAttribute("agnHighlightKey", new String("Send_Mailing"));
        pageContext.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
        pageContext.setAttribute("agnTitleKey", new String("Mailing"));
        pageContext.setAttribute("agnSubtitleKey", new String("Mailing"));
        pageContext.setAttribute("agnSubtitleValue", tmpShortname);
    %>
</logic:equal>

<%@include file="/header.jsp"%>
<%@include file="/messages.jsp" %>

<table border="0" cellspacing="0" cellpadding="0">
    <tr> 
        <td><span class="head2"><bean:message key="Send_Mailing"/></span></td>
        <td></td>
    </tr>
    <tr>
        <td> 
                   
            <logic:equal name="mailingSendForm" property="worldMailingSend" value="true">
                <logic:equal name="mailingSendForm" property="mailingtype" value="<%= Integer.toString(Mailing.TYPE_NORMAL) %>">
                    <bean:message key="MailingSentAllready"/>
                </logic:equal>
                <logic:equal name="mailingSendForm" property="mailingtype" value="<%= Integer.toString(Mailing.TYPE_ACTIONBASED) %>">
                    <bean:message key="mailing.deactivate_event_explain"/>
                </logic:equal>
                <logic:equal name="mailingSendForm" property="mailingtype" value="<%= Integer.toString(Mailing.TYPE_DATEBASED) %>">
                    <bean:message key="mailing.deactivate_rule_explain"/>
                    <logic:equal name="mailingSendForm" property="worldMailingSend" value="true">
                        <% DateFormat timeFormat=DateFormat.getTimeInstance(DateFormat.SHORT, (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)); %>
                        <br><bean:message key="SendingTimeDaily"/>:&nbsp;<%= timeFormat.format(aDelstat.getScheduledSendTime()) %>
                    </logic:equal>
                </logic:equal>
            </logic:equal>
                    
            <logic:equal name="mailingSendForm" property="worldMailingSend" value="false">
                <logic:equal name="mailingSendForm" property="mailingtype" value="<%= Integer.toString(Mailing.TYPE_NORMAL) %>">
                    <bean:message key="MailingReadyForSending"/>
                </logic:equal>
                <logic:equal name="mailingSendForm" property="mailingtype" value="<%= Integer.toString(Mailing.TYPE_ACTIONBASED) %>">
                    <bean:message key="mailing.activate_event_explain"/>
                </logic:equal>
                <logic:equal name="mailingSendForm" property="mailingtype" value="<%= Integer.toString(Mailing.TYPE_DATEBASED) %>">
                    <bean:message key="mailing.activate_rule_explain"/>
                </logic:equal>
            </logic:equal>
                    
            <br><br><li><html:link page="<%= new String("/mailingsend.do?action=" + MailingSendAction.ACTION_PREVIEW_SELECT + "&mailingID=" + tmpMailingID) %>"><b>
                <bean:message key="Preview"/>
            </b></html:link><br>
            <agn:ShowByPermission token="mailing.send.admin">
                <br><li><html:link page="<%= new String("/mailingsend.do?action=" + MailingSendAction.ACTION_SEND_ADMIN + "&mailingID=" + tmpMailingID) %>"><b>
                    <bean:message key="MailingTestAdmin"/>
                </b></html:link><br>
            </agn:ShowByPermission>
            <agn:ShowByPermission token="mailing.send.test">
                <li><html:link page="<%= new String("/mailingsend.do?action=" + MailingSendAction.ACTION_SEND_TEST + "&mailingID=" + tmpMailingID) %>"><b>
                    <bean:message key="MailingTestDistrib"/>
                </b></html:link>
            </agn:ShowByPermission>

            <logic:equal name="mailingSendForm" property="isTemplate" value="false">
                <logic:equal name="mailingSendForm" property="mailingtype" value="<%= Integer.toString(Mailing.TYPE_ACTIONBASED) %>">
                    <logic:equal name="mailingSendForm" property="worldMailingSend" value="true">
                        <agn:ShowByPermission token="mailing.send.world">
                            <br><br><li><html:link page="<%= new String("/mailingsend.do?action=" + MailingSendAction.ACTION_DEACTIVATE_MAILING + "&mailingID=" + tmpMailingID) %>"><b>
                                <bean:message key="MailingDeactivate"/>
                            </b></html:link><br>
                        </agn:ShowByPermission>
                    </logic:equal>
                </logic:equal>

                <logic:equal name="mailingSendForm" property="mailingtype" value="<%= Integer.toString(Mailing.TYPE_DATEBASED) %>">
                    <logic:equal name="mailingSendForm" property="worldMailingSend" value="true">
                        <agn:ShowByPermission token="mailing.send.world">
                            <br><br><li><html:link page="<%= new String("/mailingsend.do?action=" + MailingSendAction.ACTION_DEACTIVATE_MAILING + "&mailingID=" + tmpMailingID) %>"><b>
                                <bean:message key="MailingDeactivate"/>
                            </b></html:link><br>
                        </agn:ShowByPermission>
                    </logic:equal>
                </logic:equal>

                <logic:equal name="mailingSendForm" property="mailingtype" value="<%= Integer.toString(Mailing.TYPE_ACTIONBASED) %>">
                    <logic:equal name="mailingSendForm" property="worldMailingSend" value="false">
                        <agn:ShowByPermission token="mailing.send.world">
                            <br><br><li><html:link page="<%= new String("/mailingsend.do?action=" + MailingSendAction.ACTION_ACTIVATE_CAMPAIGN + "&to=3&mailingID=" + tmpMailingID) %>"><b>
                                <bean:message key="MailingActivate"/>
                            </b></html:link><br>
                        </agn:ShowByPermission>
                    </logic:equal>
                </logic:equal>

                <logic:equal name="mailingSendForm" property="mailingtype" value="<%= Integer.toString(Mailing.TYPE_DATEBASED) %>">
                    <logic:equal name="mailingSendForm" property="worldMailingSend" value="false">
                        <agn:ShowByPermission token="mailing.send.world">
                            <html:form action="/mailingsend">
                                <input type="hidden" name="action" value="<%= MailingSendAction.ACTION_ACTIVATE_RULEBASED %>">
                                <input type="hidden" name="to" value="4">
                                <html:hidden property="mailingID"/>
                                <br><br><li><a href="#" onclick="document.forms.mailingSendForm.submit()"><b>
                                    <bean:message key="MailingActivate"/>
                                </b></a><br>                            
                                <%
                                    int i;
                                    TimeZone aZone=AgnUtils.getTimeZone(request);
                                    GregorianCalendar aDate=new GregorianCalendar(aZone);
                                    // aDate.setTimeZone(aZone);
                                    // DateFormat showFormat=new SimpleDateFormat("dd.MM.yyyy");
                                    DateFormat showFormat=DateFormat.getDateInstance(DateFormat.FULL, (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY));
                                    DateFormat internalFormat=new SimpleDateFormat("yyyyMMdd");
                                    NumberFormat aFormat=new DecimalFormat("00");
                                %>
                                <input type="hidden" name="sendDate" value="<%= internalFormat.format(aDate.getTime()) %>">
                                &nbsp;&nbsp;&nbsp;<bean:message key="SendingTimeDaily"/>:&nbsp;
                                <html:select property="sendHour" size="1">
                                    <% for(i=0; i<=23; i++) { %>
                                    <html:option value="<%= Integer.toString(i) %>"><%=aFormat.format((long)i) %>:00h</html:option>
                                    <% } %>
                                    </html:select>
                                <input type="hidden" name="sendMinute" value="0">
                                &nbsp;<%= aZone.getID() %>
                            </html:form>
                        </agn:ShowByPermission>
                    </logic:equal>
                </logic:equal>

                <agn:ShowByPermission token="mailing.send.world">
                    <logic:equal name="mailingSendForm" property="canSendWorld" value="true">
                        <br><br><li><html:link page="<%= new String("/mailingsend.do?action=" + MailingSendAction.ACTION_VIEW_SEND2 + "&mailingID=" + tmpMailingID) %>"><b>
                            <bean:message key="MailingSendNow"/>
                        </b></html:link><br>
                    </logic:equal>
                </agn:ShowByPermission>

            </logic:equal>

        </td>
    </tr>
</table>

<logic:equal name="mailingSendForm" property="mailingtype" value="<%= Integer.toString(Mailing.TYPE_NORMAL) %>">
    <iframe name="delstatbox" src="<html:rewrite page="<%= new String("/mailingsend.do?action=" + MailingSendAction.ACTION_VIEW_DELSTATBOX + "&mailingID=" + tmpMailingID) %>"/>" ALLOWTRANSPARENCY="true" width="500" height="300" bgcolor="#73A2D0" scrolling="no" frameborder="0">
        <bean:message key="csv_no_iframe"/>
    </iframe>                
    <br>     
</logic:equal>
              
<%@include file="/footer.jsp"%>
