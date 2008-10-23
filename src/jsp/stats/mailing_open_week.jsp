<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.target.*, org.agnitas.beans.*, org.agnitas.stat.*" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="stats.rdir"/>

<%  // date formats:
    java.text.SimpleDateFormat format01=new java.text.SimpleDateFormat("yyyyMMdd");
    java.text.SimpleDateFormat format02=new java.text.SimpleDateFormat("dd.MM.yy");

    // for csv-download:
    String statfile = "";
    EmmCalendar my_calendar = new EmmCalendar(java.util.TimeZone.getDefault());
    java.util.Date my_time = my_calendar.getTime();
    String Datum = my_time.toString();
    String timekey = Long.toString(my_time.getTime());
    pageContext.setAttribute("time_key", timekey);
    int tmpMailingID=0;
    int tmpTargetID=0;
    int tmpUrlID=0;
    int tmpMaxblue=0;
    String tmpStartdate = "no";
    String aktURL = "";
    java.util.Hashtable tmpValues = null;
    String tmpShortname=new String("");
    MailingStatForm aForm=(MailingStatForm)session.getAttribute("mailingStatForm");
    if(aForm!=null) {
        tmpValues=(java.util.Hashtable)aForm.getValues();
        tmpMaxblue=aForm.getMaxblue();
        tmpMailingID=aForm.getMailingID();
        tmpShortname=aForm.getMailingShortname();
        if(aForm.getStartdate().compareTo("no")!=0)
            tmpStartdate = aForm.getStartdate();
    }

    // map for the csv download
    java.util.Hashtable my_map = null;
    if(pageContext.getSession().getAttribute("map") == null) {
        my_map = new java.util.Hashtable();
        pageContext.getSession().setAttribute("map",my_map);
    } else {
        my_map = (java.util.Hashtable)(pageContext.getSession().getAttribute("map"));
    }
    %>

<% pageContext.setAttribute("sidemenu_active", new String("Mailings")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("none")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Mailing")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Mailing")); %>
<% pageContext.setAttribute("agnSubtitleValue", tmpShortname); %>
<% pageContext.setAttribute("agnNavigationKey", new String("mailingView")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Statistics")); %>
<% pageContext.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID)); %>

<%@include file="/header.jsp"%>
<html:errors/>

<html:form action="/mailing_stat">
    <html:hidden property="action"/>
    <html:hidden property="mailingID"/>
    <html:hidden property="urlID"/>
    <html:hidden property="startdate"/>

    <table border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td>
                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td>
                            <span class="head3"><bean:message key="OpenTime"/><br></span>
                        </td>
                    </tr>
                </table>
                <br><bean:message key="KlickForDay"/>
            </td>    
        </tr>

        <tr>
            <td><hr size="1" noshade>
                <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td width="80" valign=bottom>
                            <bean:message key="Opened_Mails"/>:
                            <hr size="1" noshade>
                            <bean:message key="Date"/>:
                            </a>
                        </td>

                        <!-- * * * * * * *-->        
                        <!-- VALUES BEGIN -->
                        <%  EmmCalendar aCal = new EmmCalendar(java.util.TimeZone.getDefault());
                            double zoneOffset=0.0;
                            zoneOffset=aCal.getTimeZoneOffsetHours(java.util.TimeZone.getDefault(), AgnUtils.getTimeZone(request));
                            java.util.TimeZone my_zone = AgnUtils.getTimeZone(request);
                            String aktDate = "";
                            int i=0;
                            aCal.set( new Integer(((MailingStatForm)session.getAttribute("mailingStatForm")).getStartdate().substring(0,4)).intValue() ,
                                    (new Integer(((MailingStatForm)session.getAttribute("mailingStatForm")).getStartdate().substring(4,6)).intValue() - 1) ,
                                    new Integer(((MailingStatForm)session.getAttribute("mailingStatForm")).getStartdate().substring(6,8)).intValue() );
                            aCal.setTimeZone(java.util.TimeZone.getDefault());
                            aCal.changeTimeWithZone(my_zone);
                            java.util.Enumeration ke = ((MailingStatForm)session.getAttribute("mailingStatForm")).getValues().keys();
                        %>

                        <%  if(tmpStartdate.compareTo(((MailingStatForm)session.getAttribute("mailingStatForm")).getFirstdate()) >= 0) {
                           		aCal.add(aCal.DATE, -7);
	                            aktDate = format01.format(aCal.getTime()); %>
                        <td valign=bottom><hr size="1" noshade>
                            <html:link page="<%= new String("/mailing_stat.do?action=" + MailingStatAction.ACTION_OPEN_TIME + "&mailingID=" + tmpMailingID + "&startdate=" + aktDate) %>">
                                <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>arrow_back.gif" border="0">&nbsp;
                            </html:link>
                        </td>
                        <%     aCal.add(aCal.DATE, 7);
                        	} %>
                        <%
                            while(i<7) {
                                    aktDate = format01.format(aCal.getTime());
                        %>

                        <td width="80" valign=bottom>
                            <center>  
                            <html:link page="<%= new String("/mailing_stat.do?action=" + MailingStatAction.ACTION_OPEN_DAYSTAT + "&mailingID=" + tmpMailingID + "&startdate=" + aktDate) %>">
                                <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif" width="20" border="0" height="<% if(tmpValues.containsKey(aktDate)) {%><%=java.lang.StrictMath.floor(((Integer)(tmpValues.get(aktDate))).doubleValue()/(double)tmpMaxblue*200)%><% } else { %>0<% } %>">&nbsp;
                                <br>
                                <% if( tmpValues.containsKey(aktDate) ) {%><%= tmpValues.get(aktDate) %><% } else { %>0<% } %>&nbsp;
                            </html:link>
                            <hr size="1" noshade>
                            <html:link page="<%= new String("/mailing_stat.do?action=" + MailingStatAction.ACTION_OPEN_DAYSTAT + "&mailingID=" + tmpMailingID + "&startdate=" + aktDate) %>">
                                <%= format02.format(aCal.getTime()) %>&nbsp;
                            </html:link></center>
                        </td>

                        <% statfile += "\r\n\"" + format02.format(aCal.getTime()) + "\";\"";
                            if(tmpValues.containsKey(aktDate)) {
                                statfile += tmpValues.get(aktDate);
                            } else {
                                statfile += "0";
                            }
                            statfile += "\"";
                            i++;
                            aCal.add(aCal.DATE, 1);
                                } %>
                        <!-- VALUES END -->
                        <!-- * * * * * *-->     

                        <% // aCal.add(aCal.DATE, 1); 

                            //my_calendar.roll(my_calendar.DATE, 1);
                            my_calendar.add(my_calendar.MINUTE, 3);
                            if(my_calendar.getTime().after(aCal.getTime())) {
                                aktDate = format01.format(aCal.getTime());

                                // put csv file from the form in the hash table:
                                my_map.put(timekey,  statfile);
                                pageContext.getSession().setAttribute("map", my_map);

                        %>   
                        <td valign=bottom><hr size="1" noshade>
                            <html:link page="<%= new String("/mailing_stat.do?action=" + MailingStatAction.ACTION_OPEN_TIME + "&mailingID=" + tmpMailingID + "&startdate=" + aktDate) %>">                          
                                <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>arrow_next.gif" border="0">&nbsp;
                            </html:link>
                        </td>
                        <% } %>   
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td>              
                <hr size="1" noshade>
                <B><bean:message key="Total"/>:</B>&nbsp;<%= ((MailingStatForm)session.getAttribute("mailingStatForm")).getClicks()  %>&nbsp;<bean:message key="Opened_Mails"/>
                <html:link page="<%= new String("/mailing_stat.do?action=" + MailingStatAction.ACTION_MAILINGSTAT + "&mailingID=" + tmpMailingID + "&targetID=" + tmpTargetID) %>"><html:img src="button?msg=Back" border="0"/></html:link>
            </td>
        </tr>
    </table>
</html:form>
<%@include file="/footer.jsp"%>