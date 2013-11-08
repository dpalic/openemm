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
 --%><%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.stat.*, org.agnitas.beans.*, java.util.*, java.text.*" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<agn:CheckLogon/>

<agn:Permission token="mailing.send.show"/>

<%
    java.text.NumberFormat intFormat = new java.text.DecimalFormat("###,###,###,###,##0");
    DeliveryStat aDelstat = null;
    int tmpMailingID=0;
    MailingSendForm aForm=(MailingSendForm)request.getAttribute("mailingSendForm");
    if(aForm!=null) {
        aDelstat = aForm.getDeliveryStat();
        tmpMailingID = aForm.getMailingID();
    }

    TimeZone aZone=TimeZone.getTimeZone(AgnUtils.getAdmin(request).getAdminTimezone());
    GregorianCalendar aDate=new GregorianCalendar(aZone);
    DateFormat showFormat=DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY));
	showFormat.setCalendar( aDate);

    DateFormat internalFormat=new SimpleDateFormat("yyyyMMdd");

%>

<html>
    <meta http-equiv="Page-Exit" content="RevealTrans(Duration=1,Transition=1)">


    <head>
        <link type="text/css" rel="stylesheet" href="<bean:write name="emm.layout" property="baseUrl" scope="session"/>stylesheet.css">
    </head>
    
    <body onLoad="window.setTimeout('window.location.reload()',5000)" STYLE="background-image:none;background-color:transparent">
    
        <span class="head2"><bean:message key="DistribStatus"/>:</span><br>&nbsp;&nbsp;<b><bean:message key="<%=new String("DeliveryStatus." + aDelstat.getDeliveryStatus())%>"/></b>

        <br>
        <br>

            
        <table border="0" cellspacing="0" cellpadding="0" width="350">
            <% //java.util.Date =aDelstat.getLastDate()%>
        
            <logic:notEqual name="mailingSendForm" property="deliveryStat.lastType" value="NO">
                <tr>
                <td colspan="2"><b><bean:message key="LastDelivery"/>:</b> <%=showFormat.format(aDelstat.getLastDate())%>, 
                <logic:notEqual  name="mailingSendForm" property="deliveryStat.lastType" value="E">
                	<bean:message key="<%=new String("DeliveryType." + aDelstat.getLastType())%>"/>
                </logic:notEqual>
                <logic:equal  name="mailingSendForm" property="deliveryStat.lastType" value="E">
                	<bean:message key="DeliveryType.W"/>
                </logic:equal>
                
                
                    <br><%=aDelstat.getLastGenerated()%> <bean:message key="OutOf"/> <%=aDelstat.getLastTotal()%> <bean:message key="RecipientsRecieved"/>
                </td></tr>
            </logic:notEqual>

        
            <logic:greaterThan name="mailingSendForm" property="deliveryStat.deliveryStatus" value="0">

            <tr>
                <td colspan="2">&nbsp;</td>
            </tr>

            <tr>
                <td colspan="2"><b><bean:message key="Generation"/>:</b><br></td>
            </tr>

            <logic:greaterThan name="mailingSendForm" property="deliveryStat.deliveryStatus" value="1">
                <% if(aDelstat.getGenerateStartTime()!=null) { %>
                <tr>
                    <td>&nbsp;&nbsp;<bean:message key="GenerateStartTime"/>:</td>
                    <td>&nbsp;&nbsp;<%=showFormat.format(aDelstat.getGenerateStartTime())%></td>
                </tr> 
                <% } %>
            </logic:greaterThan>
            
            <logic:greaterThan name="mailingSendForm" property="deliveryStat.deliveryStatus" value="2">
                <% if(aDelstat.getGenerateEndTime()!=null) { %>
                <tr>
                    <td>&nbsp;&nbsp;<bean:message key="GenerateEndTime"/>:</td>
                    <td>&nbsp;&nbsp;<%=showFormat.format(aDelstat.getGenerateEndTime())%></td>
                </tr>    
                <% } %>    
            </logic:greaterThan>

            <logic:lessThan name="mailingSendForm" property="deliveryStat.deliveryStatus" value="2">
                <tr>
                    <td>&nbsp;&nbsp;<bean:message key="ScheduledGenerateTime"/>:</td>
                    <td>&nbsp;&nbsp;<%=showFormat.format(aDelstat.getScheduledGenerateTime())%></td>
                </tr>    
            </logic:lessThan>
                
            <tr>
                <td colspan="2"><br><b><bean:message key="Delivery"/>:</b><br></td>
            </tr>
                
            <logic:greaterThan name="mailingSendForm" property="deliveryStat.deliveryStatus" value="3">
                <% if(aDelstat.getSendStartTime()!=null) { %>
                <tr>
                    <td>&nbsp;&nbsp;<bean:message key="SendStartTime"/>:</td>
                    <td>&nbsp;&nbsp;<%=showFormat.format(aDelstat.getSendStartTime())%></td>
                </tr>  
                <%  } %>
            </logic:greaterThan>
                
                
                
                
            <logic:greaterThan name="mailingSendForm" property="deliveryStat.deliveryStatus" value="4">
                <% if(aDelstat.getSendEndTime()!=null) { %>
                <tr>
                    <td>&nbsp;&nbsp;<bean:message key="SendEndTime"/>:</td>
                    <td>&nbsp;&nbsp;<%=showFormat.format(aDelstat.getSendEndTime())%></td>
                </tr>    
                <%  } %>
            </logic:greaterThan>

            <logic:lessThan name="mailingSendForm" property="deliveryStat.deliveryStatus" value="4">
                <tr>
                    <td>&nbsp;&nbsp;<bean:message key="ScheduledSendTime"/>:</td>
                    <td>&nbsp;&nbsp;<%=showFormat.format(aDelstat.getScheduledSendTime())%></td>
                </tr>    
            </logic:lessThan>
            
            <tr>
                <td colspan="2"><hr></td>
            </tr>

            <logic:greaterThan name="mailingSendForm" property="deliveryStat.deliveryStatus" value="1">
                <tr>
                    <td>&nbsp;&nbsp;<bean:message key="GeneratedMails"/>:</td>
                    <td align="right">&nbsp;&nbsp;<b><%=intFormat.format(aDelstat.getGeneratedMails())%></b></td>
                </tr>    
            </logic:greaterThan>

            <logic:greaterThan name="mailingSendForm" property="deliveryStat.deliveryStatus" value="3">
                <tr>
                    <td>&nbsp;&nbsp;<bean:message key="SentMails"/>:</td>
                    <td align="right">&nbsp;&nbsp;<b><%=intFormat.format(aDelstat.getSentMails())%></b></td>
                </tr>
            </logic:greaterThan>
                
            <tr>
                <td>&nbsp;&nbsp;<b><bean:message key="TotalMails"/>:</b></td>
                <td align="right">&nbsp;&nbsp;<b><%=intFormat.format(aDelstat.getTotalMails())%></b></td>
            </tr>
           
        </table>
            
        </logic:greaterThan>
        
           <% if( (((MailingSendForm)request.getAttribute("mailingSendForm")).getDeliveryStat())!=null   ) { %>
    <logic:equal name="mailingSendForm" property="deliveryStat.cancelable" value="true">
        <b><bean:message key="CancelGeneration"/>:</b>&nbsp;<html:link page="<%= new String("/mailingsend.do?action=" + MailingSendAction.ACTION_CANCEL_MAILING_REQUEST + "&mailingID=" + tmpMailingID) %>" target="parent" ><html:img src="button?msg=Cancel" border="0"/></html:link>
    </logic:equal>
    <% } %>
        
    </body>
</html>
