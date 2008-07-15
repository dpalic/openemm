<%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, java.util.*" contentType="text/html; charset=utf-8" buffer="32kb" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="stats.mailing"/>


<% pageContext.setAttribute("sidemenu_active",      new String("Statistics")); %>
<% pageContext.setAttribute("sidemenu_sub_active",  new String("comparison")); %>
<% pageContext.setAttribute("agnTitleKey",          new String("comparison")); %>
<% pageContext.setAttribute("agnSubtitleKey",       new String("Statistics")); %>
<% pageContext.setAttribute("agnNavigationKey",     new String("statsCompare")); %>
<% pageContext.setAttribute("agnHighlightKey",      new String("comparison")); %>

<%@include file="/header.jsp"%>

<html:errors/>

<html:form action="/mailing_compare">
<html:hidden property="action"/>
<% // define Hashtable for Download file:
org.agnitas.util.EmmCalendar my_calendar = new EmmCalendar(java.util.TimeZone.getDefault());
java.util.Date my_time = my_calendar.getTime();
String Datum = my_time.toString();
String timekey = Long.toString(my_time.getTime());
pageContext.setAttribute("time_key", timekey);

if(pageContext.getSession().getAttribute("map") == null)
{
    java.util.Hashtable my_map = new java.util.Hashtable();
    pageContext.getSession().setAttribute("map",my_map);
}  %>

<div align=right><html:link page="<%= new String("/file_download?key=" + timekey) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>icon_save.gif" border="0"></html:link></div>


        <bean:message key="Target"/>:&nbsp;&nbsp;
        <html:select property="targetID" size="1">
            <html:option value="0"><bean:message key="All_Subscribers"/></html:option>
            <agn:ShowTable id="agntbl3" sqlStatement="<%= new String("SELECT target_id, target_shortname FROM dyn_target_tbl WHERE company_id="+AgnUtils.getCompanyID(request)) %>" maxRows="500">
                <html:option value="<%= (String)(pageContext.getAttribute("_agntbl3_target_id")) %>"><%= pageContext.getAttribute("_agntbl3_target_shortname") %></html:option>
            </agn:ShowTable>
        </html:select>&nbsp;<html:image src="button?msg=OK" border="0"/>

<br><br><br>
<%  CompareMailingForm form = null;
    if(session.getAttribute("compareMailingForm") != null) {
        form = ((CompareMailingForm)session.getAttribute("compareMailingForm"));
    }

    String file = form.getCvsfile();
%>


<table border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td width="70"><b><bean:message key="Mailing"/></b>&nbsp;&nbsp;</td>
        <td></td>
        <td width="70"><b><bean:message key="Receipients"/><b>&nbsp;&nbsp;</td>
        <td></td>
        <td width="70"><b><bean:message key="Clicks"/><b>&nbsp;&nbsp;</td>
        <td></td>
        <td width="70"><b><bean:message key="opened"/></b>&nbsp;&nbsp;</td>
        <td></td>
        <td width="70"><b><bean:message key="Bounces"/></b>&nbsp;&nbsp;</td>
        <td></td>
        <td width="70"><b><bean:message key="Opt_Outs"/></b></td>
    </tr>
    <tr><td colspan=11><hr></td></tr>
    <% // Integer mailingID=null;
        Hashtable allClicks=form.getNumClicks();
        Hashtable allReceive=form.getNumRecipients();
        Hashtable allOpen=form.getNumOpen();
        Hashtable allBounce=form.getNumBounce();
        Hashtable allOptout=form.getNumOptout();
        Hashtable allNames=form.getMailingName();
        Hashtable allDesc=form.getMailingDescription();
%>
     <logic:iterate name="compareMailingForm" property="mailings" id="mailingID">
        <% Integer aMailingID=(Integer)pageContext.getAttribute("mailingID"); 
           file+=allNames.get(aMailingID)+";";
           file+=allReceive.get(aMailingID)+";";
           file+=allClicks.get(aMailingID)+";";
           file+=allOpen.get(aMailingID)+";";
           file+=allBounce.get(aMailingID)+";";
           file+=allOptout.get(aMailingID)+"\n";
        %>
        <tr>
            <td><html:link page="<%= new String("/mailing_stat.do?action=7&mailingID=" + aMailingID.intValue()) %>"><b><%= allNames.get(aMailingID) %></b><br>(<%= allDesc.get(mailingID) %>)</html:link></td>
            <td background="<bean:write name="emm.layout" property="baseUrl" scope="session"/>border_06.gif"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="10" border="0"></td>
            <td>&nbsp;<img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif" width="<%=  ((float)((Integer)allReceive.get(aMailingID)).intValue() / (float)form.getBiggestRecipients() ) * 50 %>" height="10">&nbsp;
                <br><div align=left>&nbsp;<%= allReceive.get(aMailingID) %></div></td>
            <td background="<bean:write name="emm.layout" property="baseUrl" scope="session"/>border_06.gif"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="10" border="0"></td>    
            <td>&nbsp;<img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif" width="<%=  ((float)((Integer)allClicks.get(aMailingID)).intValue() / (float)form.getBiggestClicks() ) * 50 %>" height="10">&nbsp;
                <br><div align=left>&nbsp;<%= allClicks.get(aMailingID) %></div></td>
            <td background="<bean:write name="emm.layout" property="baseUrl" scope="session"/>border_06.gif"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="10" border="0"></td>
            <td>&nbsp;<img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif" width="<%=  ((float)((Integer)allOpen.get(aMailingID)).intValue() / (float)form.getBiggestOpened() ) * 50 %>" height="10">&nbsp;
                <br><div align=left>&nbsp;<%= allOpen.get(aMailingID) %></div></td>
            <td background="<bean:write name="emm.layout" property="baseUrl" scope="session"/>border_06.gif"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="10" border="0"></td>
            <td>&nbsp;<img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif" width="<%=  ((float)((Integer)allBounce.get(aMailingID)).intValue() / (float)form.getBiggestBounce() ) * 50 %>" height="10">&nbsp;
                <br><div align=left>&nbsp;<%= allBounce.get(aMailingID) %></div></td>
            <td background="<bean:write name="emm.layout" property="baseUrl" scope="session"/>border_06.gif"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="10" border="0"></td>
            <td>&nbsp;<img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif" width="<%=  ((float)((Integer)allOptout.get(aMailingID)).intValue() / (float)form.getBiggestOptouts() ) * 50 %>" height="10">&nbsp;
                <br><div align=left>&nbsp;<%= allOptout.get(aMailingID) %></div></td>
        </tr>
     </logic:iterate>

        <tr><td colspan=11><hr></td></tr>
</table>

<% ((java.util.Hashtable)pageContext.getSession().getAttribute("map")).put(pageContext.getAttribute("time_key"), file); %>

</html:form>
<html:link page="/mailing_compare.do?action=1"><html:img src="button?msg=Back" border="0"/></html:link>

<%@include file="/footer.jsp"%>
