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
 --%><%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.web.forms.*, org.agnitas.beans.*, java.text.*, java.util.*" buffer="32kb" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<agn:CheckLogon/>

<agn:Permission token="stats.domains"/>

<% pageContext.setAttribute("sidemenu_active", new String("Statistics")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("domains")); %>
<% pageContext.setAttribute("agnTitleKey", new String("domains")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Statistics")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("statsDomain")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("domains")); %>

<%@include file="/header.jsp"%>
<%@include file="/messages.jsp" %>

<%
// key for the csv download
java.util.TimeZone tz = TimeZone.getTimeZone(((Admin)session.getAttribute("emm.admin")).getAdminTimezone());
java.util.GregorianCalendar aCal=new java.util.GregorianCalendar(tz);
java.util.Date my_time = aCal.getTime();
String Datum = my_time.toString();
String timekey = Long.toString(my_time.getTime());

// map for the csv download
java.util.Hashtable my_map = null;
if(pageContext.getSession().getAttribute("map") == null)
{
    my_map = new java.util.Hashtable();
    pageContext.getSession().setAttribute("map",my_map);
    // System.out.println("map exists.");
} else {
    my_map = (java.util.Hashtable)(pageContext.getSession().getAttribute("map"));
    // System.out.println("new map.");
}

// put csv file from the form in the hash table:
   String file = ((DomainStatForm)(session.getAttribute("domainStatForm"))).getCsvfile();
   my_map.put(timekey,  file);
   pageContext.getSession().setAttribute("map", my_map);

%>


<table border="0" cellspacing="0" cellpadding="0">

<html:form action="/domain_stats">
<html:hidden property="action"/>

<tr><td colspan="3">&nbsp;</td></tr>


<tr><td><bean:message key="Target"/>:&nbsp;</td>
    <td>
        <html:select property="targetID" size="1">
        <html:option value="0"><bean:message key="All_Subscribers"/></html:option>
        <agn:ShowTable id="agntbl3" sqlStatement="<%= new String("SELECT target_id, target_shortname FROM dyn_target_tbl WHERE company_id="+AgnUtils.getCompanyID(request) ) %>" maxRows="50" encodeHtml="0">
        <html:option value="<%= (String)pageContext.getAttribute("_agntbl3_target_id") %>"><%= pageContext.getAttribute("_agntbl3_target_shortname") %></html:option>
        </agn:ShowTable>
        </html:select>
    </td>

    <td><div align="right"> <html:link page="<%= new String("/file_download?key=" + timekey) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>icon_save.gif" border="0"></html:link></div></td>

</tr>
<tr>
    <td><bean:message key="Mailinglist"/>:&nbsp;</td>
    <td>
        <html:select property="listID" size="1">
        <html:option value="0"><bean:message key="All_Mailinglists"/></html:option>
        <agn:ShowTable id="agntbl2" sqlStatement="<%= new String("SELECT mailinglist_id, shortname FROM mailinglist_tbl WHERE company_id="+AgnUtils.getCompanyID(request)) %>" maxRows="100" encodeHtml="0">
        <html:option value="<%= (String)pageContext.getAttribute("_agntbl2_mailinglist_id") %>"><%= pageContext.getAttribute("_agntbl2_shortname") %></html:option>
        </agn:ShowTable>
        </html:select>
    </td>
    <td><html:image src="button?msg=OK" border="0"/></td>
</tr>



<tr><td colspan="3">&nbsp;</td></tr>
<tr><td colspan="3">&nbsp;</td></tr>

<% if ( (((DomainStatForm)session.getAttribute("domainStatForm")).getTotal()) != 0)
    { %>

<tr><td colspan="3">
        <table border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td><span class="head3"><bean:message key="domain"/></span>&nbsp;&nbsp;</td>
                    <td colspan="2">&nbsp;&nbsp;<span class="head3"><bean:message key="Recipients"/></span></td>
                </tr>

                <tr><td colspan="3"><hr size ="1"></td></tr>

                <% int j = 0;
                   while (j < ((DomainStatForm)session.getAttribute("domainStatForm")).getLines())
                   { %>
                <tr>
                    <td><%= ((DomainStatForm)session.getAttribute("domainStatForm")).getDomains(j) %>&nbsp;&nbsp;</td>
                    <td align="right"><%= (((DomainStatForm)session.getAttribute("domainStatForm")).getSubscribers(j)) %>&nbsp;</td>
                    <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif" width="<%= ((float) ( (DomainStatForm)session.getAttribute("domainStatForm")).getSubscribers(j)  )/ (float) ((DomainStatForm)session.getAttribute("domainStatForm")).getTotal()  * 250 %>" height="10"><td>
                </tr>
                <%   j++;
                     } %>

                <tr><td colspan="3">&nbsp;&nbsp;</td></tr>

                <tr>
                    <td><bean:message key="Other"/>:&nbsp;&nbsp;</td>
                    <td align="right"><%= ((DomainStatForm)session.getAttribute("domainStatForm")).getRest() %>&nbsp;</td>
                    <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif" width="<%= ((float) ( (DomainStatForm)session.getAttribute("domainStatForm")).getRest() ) / (float) ((DomainStatForm)session.getAttribute("domainStatForm")).getTotal() * 250 %>" height="10"></td>
                </tr>

                <tr><td colspan="3"><hr></td></tr>

                <tr>
                    <td><b><bean:message key="Total"/>:</b>&nbsp;&nbsp;</td>
                    <td align="right"><b><%= ((DomainStatForm)session.getAttribute("domainStatForm")).getTotal() %></b>&nbsp;</td>
                    <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif" width="250" height="10"></td>
                </tr>
        </table>
    </td>
</tr>
<% } else { %>

<tr>
    <td colspan="3">
        <b><bean:message key="NoSubscribersForSelection"/></b>
    </td>
</td>

<% } %>


</html:form>

</table>
<%@include file="/footer.jsp"%>
