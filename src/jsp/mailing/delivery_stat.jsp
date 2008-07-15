<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.stat.*, org.agnitas.beans.*, java.util.*, java.text.*" %>
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
                <td colspan="2"><b><bean:message key="LastDelivery"/>:</b> <%=showFormat.format(aDelstat.getLastDate())%>, <bean:message key="<%=new String("DeliveryType." + aDelstat.getLastType())%>"/>
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
        
    </body>
</html>
