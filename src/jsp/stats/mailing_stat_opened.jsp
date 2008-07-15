<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.stat.*, java.util.*, org.agnitas.web.*, org.agnitas.beans.*" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="stats.rdir"/>

<% int tmpMailingID=0;
   // int tmpUniqueClicks=0;
   String tmpShortname=new String("");
   EmmLayout aLayout=(EmmLayout)session.getAttribute("emm.layout");
   MailingStatForm aForm=null;
   if(session.getAttribute("mailingStatForm")!=null) {
      aForm=(MailingStatForm)session.getAttribute("mailingStatForm");
      tmpMailingID=aForm.getMailingID();
      tmpShortname=aForm.getMailingShortname();
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

<%
EmmCalendar my_calendar = new EmmCalendar(java.util.TimeZone.getDefault());
my_calendar.changeTimeWithZone(AgnUtils.getTimeZone(request));
java.util.Date my_time = my_calendar.getTime();
String Datum = my_time.toString();
String timekey = Long.toString(my_time.getTime());
pageContext.setAttribute("time_key", timekey);


// map for the csv download
java.util.Hashtable my_map = null;
if(pageContext.getSession().getAttribute("map") == null) {
    my_map = new java.util.Hashtable();
    pageContext.getSession().setAttribute("map",my_map);
    // System.out.println("map exists.");
} else {
    my_map = (java.util.Hashtable)(pageContext.getSession().getAttribute("map"));
    // System.out.println("new map.");
}
  // put csv file from the form in the hash table:
  String file = ((MailingStatForm)(session.getAttribute("mailingStatForm"))).getCsvfile();
%>

<html:form action="/mailing_stat">
    <html:hidden property="mailingID"/>
    <html:hidden property="action"/>
    
<table border="0" cellspacing="0" cellpadding="0" width="100%">
    
    <tr>
        <td><span class="head3"><bean:message key="Opened_Mails"/><br><br></span></td>
        <td align="right">
        &nbsp;
        
            <html:link page="<%= new String("/file_download?key=" + timekey) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>icon_save.gif" border="0"></html:link>
        
       </td>
    </tr>

</table>




<table border="0" cellspacing="0" cellpadding="0">


    <tr>
       <td><b><bean:message key="domain"/>&nbsp;&nbsp;</b></td>
       <td><b><bean:message key="Opened_Mails"/></b></td>

    </tr>

    <tr>
       <td colspan="2"><hr></td>
    </tr>


<% Hashtable values = aForm.getValues();
   int i = 1;
   while (values.containsKey(new Integer(i))) { 
        MailingStatEntry aEntry = (MailingStatEntry)(values.get(new Integer(i)));
        int aktOpened = aEntry.getOpened();
        String aktDomain = aEntry.getTargetName();
        i++;%>
    <tr>
       <td><%=aktDomain %>&nbsp;&nbsp;</td>
       <td align="right"><%=aktOpened %>&nbsp;&nbsp;</td>
    </tr>
        
<% } 
   MailingStatEntry aEntry = (MailingStatEntry)(values.get(new Integer(0)));
   int totalOp = aEntry.getOpened();
   int restOp = aEntry.getTotalClicks();
   if(restOp > 0) {
%>    
    <tr>
       <td colspan="2">&nbsp;</td>
    </tr>
    <tr>
       <td><bean:message key="Other"/>&nbsp;&nbsp;</td>
       <td align="right"><%=restOp %>&nbsp;&nbsp;</td>
    </tr>
<% } %>
    <tr>
       <td colspan="2"><hr></td>
    </tr>

    <tr>
       <td><b><bean:message key="Total"/></b>&nbsp;&nbsp;</td>
       <td align="right"><b><%=totalOp %>&nbsp;&nbsp;</b></td>
    </tr>

<%
   my_map.put(timekey,  file);
   pageContext.getSession().setAttribute("map", my_map);
 %>

    <tr>
       <td colspan="2">&nbsp;</td>
    </tr>
    <tr>
       <td colspan="2"><html:link page="<%= new String("/mailing_stat.do?action=" + MailingStatAction.ACTION_MAILINGSTAT) %>"><html:img src="button?msg=Back" border="0"/></html:link></td>
    </tr>
    
</table>

</html:form>

<%@include file="/footer.jsp"%>
