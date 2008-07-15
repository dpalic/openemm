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
 --%><%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*, java.util.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<agn:CheckLogon/>

<% pageContext.setAttribute("sidemenu_active", new String("Statistics")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("Mailing")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Mailing")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Statistics")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("mailingView")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Statistics")); %>

<%@include file="/header.jsp"%>
<html:errors/>

<%
// key for the csv download
TimeZone tz = TimeZone.getTimeZone( ((Admin)session.getAttribute("emm.admin")).getAdminTimezone() );
GregorianCalendar aCal=new GregorianCalendar( tz );
java.util.Date my_time = aCal.getTime();
String timekey = Long.toString(my_time.getTime());
// pageContext.setAttribute("time_key", timekey);     // Long.toString((aCal.getTime()).getTime())

MailingStatForm aForm=null;
int tmpMailingID=0;
String shortname="";
if(session.getAttribute("mailingStatForm")!=null) {
      aForm=(MailingStatForm)session.getAttribute("mailingStatForm");
      tmpMailingID=aForm.getMailingID();
      shortname = aForm.getMailingShortname();
   }

// map for the csv download
java.util.Hashtable my_map = null;

if(pageContext.getSession().getAttribute("map") == null)
{   my_map = new java.util.Hashtable();
    pageContext.getSession().setAttribute("map",my_map);
    // System.out.println("map exists.");
} else {
    my_map = (java.util.Hashtable)(pageContext.getSession().getAttribute("map"));
    // System.out.println("new map.");
}

// put csv file from the form in the hash table:
   String file = "";

   pageContext.getSession().setAttribute("map", my_map);
%>


<table border="0" cellspacing="0" cellpadding="0">

<html:form action="/mailing_stat">
<html:hidden property="action"/>
	<tr>
		<td><B>Mailing: </B></td>
		<td><B><%= shortname %></B></td>
		<td><div align="right"> <html:link page="<%= new String("/file_download?key=" + timekey) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>icon_save.gif" border="0"></html:link></div></td>
	</tr>
	<tr>
		<td><br></td>
	</tr>
	<tr>
    	<td><span class="head3"><bean:message key="Salutation"/>&nbsp;&nbsp;</span></td>
        <td><span class="head3"><bean:message key="Firstname"/>&nbsp;&nbsp;</span></td>
        <td><span class="head3"><bean:message key="Lastname"/>&nbsp;&nbsp;</span></td>
        <td><span class="head3"><bean:message key="E-Mail"/>&nbsp;&nbsp;</span></td>
    </tr>

    <tr><td colspan="4"><hr></td></tr>
<% String sqlStatement="select cust.email as email, cust.firstname as firstname, cust.lastname as lastname, cust.gender as gender from customer_"+AgnUtils.getCompanyID(request)+"_binding_tbl bind, customer_"+AgnUtils.getCompanyID(request)+"_tbl cust	where bind.customer_id=cust.customer_id and exit_mailing_id="+tmpMailingID+" and user_status = 2 and mailinglist_id=(select mailinglist_id from mailing_tbl where mailing_id = "+tmpMailingID+")"; %>
<% System.err.println(sqlStatement); %>
    <% file += SafeString.getLocaleString("Salutation", (Locale)session.getAttribute("emm.locale")) + ";" + SafeString.getLocaleString("Firstname", (Locale)session.getAttribute("emm.locale")) + ";" + SafeString.getLocaleString("Lastname", (Locale)session.getAttribute("emm.locale")) + ";" + SafeString.getLocaleString("E-Mail", (Locale)session.getAttribute("emm.locale")); %>
	<agn:ShowTable id="agntbl3" sqlStatement="<%= sqlStatement %>" maxRows="500">
	<tr>
		<td><% if(((String)pageContext.getAttribute("_agntbl3_gender")).compareTo("0")==0) { %>
            <% file += "\n \"" + SafeString.getLocaleString("MisterShort", (Locale)session.getAttribute("emm.locale")); %> <bean:message key="MisterShort"/>
            <% } %>
            <% if(((String)pageContext.getAttribute("_agntbl3_gender")).compareTo("1")==0) { %>
            <% file += "\n \"" + SafeString.getLocaleString("MissesShort", (Locale)session.getAttribute("emm.locale")); %> <bean:message key="MissesShort"/>
            <% } %>
        </td>
        <td><%= (String)(pageContext.getAttribute("_agntbl3_firstname")) %></td>
        <td><%= (String)(pageContext.getAttribute("_agntbl3_lastname")) %></td>
        <td><%= (String)(pageContext.getAttribute("_agntbl3_email")) %></td>
    </tr>
    <% file += "\";\"" + pageContext.getAttribute("_agntbl3_firstname"); %>
    <% file += "\";\"" + pageContext.getAttribute("_agntbl3_lastname"); %>
    <% file += "\";\"" + pageContext.getAttribute("_agntbl3_email") + "\""; %>
    </agn:ShowTable>

    <tr><td colspan="4"><hr></td></tr>

</html:form>
<% my_map.put(timekey,  file); %>
</table>
<%@include file="/footer.jsp"%>