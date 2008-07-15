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
 --%><%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, java.util.*, org.agnitas.beans.Admin" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<agn:CheckLogon/>
<html:errors/>
<agn:Permission token="wizard.import"/>

<% ImportWizardForm aForm=(ImportWizardForm)session.getAttribute("importWizardForm"); 
   int tmpInserted = aForm.getStatus().getInserted();
   int tmpUpdated = aForm.getStatus().getUpdated();
%>

<% // map for the csv download:
   String csvfile = "";
   EmmCalendar my_calendar = new EmmCalendar(java.util.TimeZone.getDefault());
   TimeZone zone=TimeZone.getTimeZone(((Admin)session.getAttribute("emm.admin")).getAdminTimezone());

   my_calendar.changeTimeWithZone(zone);
   java.util.Date my_time = my_calendar.getTime();
   String Datum = my_time.toString();
   String timekey = Long.toString(my_time.getTime());
   pageContext.setAttribute("time_key", timekey);
   java.util.Hashtable my_map = null;
   if(pageContext.getSession().getAttribute("map") == null)
   {
      my_map = new java.util.Hashtable();
      pageContext.getSession().setAttribute("map",my_map);
   } else {
       my_map = (java.util.Hashtable)(pageContext.getSession().getAttribute("map"));
   }
   // fill up csv file
   csvfile += SafeString.getLocaleString("SubscriberImport", (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY));
   csvfile += "\n" + SafeString.getLocaleString("Date", (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ": ; \"" + my_time + "\"\n";
 %>
<html>
<logic:lessThan name="importWizardForm" property="dbInsertStatus" value="1000" scope="session">
<meta http-equiv="Page-Exit" content="RevealTrans(Duration=1,Transition=1)">
</logic:lessThan>
<head>
<link rel="stylesheet" href="<bean:write name="emm.layout" property="baseUrl" scope="session"/>stylesheet.css">
</head>
<body <logic:lessThan name="importWizardForm" property="dbInsertStatus" value="1000" scope="session">onLoad="window.setTimeout('window.location.reload()',1500)"</logic:lessThan> STYLE="background-image:none;background-color:transparent">
<table border="0" cellspacing="0" cellpadding="0" width="300" height="20">
<tr width="100%">
   <td width="100%">
        <bean:message key="csv_importing_data"/>:<br>
        <logic:lessEqual name="importWizardForm" property="dbInsertStatus" value="100" scope="session">
            <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif" width="<%= aForm.getDbInsertStatus()*2 %>" height="20">
        </logic:lessEqual>
        <logic:greaterThan name="importWizardForm" property="dbInsertStatus" value="100" scope="session">
            <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif" width="200" height="20"><br><br>
            <logic:iterate name="importWizardForm" property="dbInsertStatusMessages" scope="session" id="aMsg" type="java.lang.String">
                <bean:message key="<%= (String)pageContext.getAttribute("aMsg") %>"/><br>
            </logic:iterate>
        </logic:greaterThan>
        <logic:greaterEqual name="importWizardForm" property="dbInsertStatus" value="1000" scope="session">
            <br><br>
            <span class="head3"><bean:message key="import.result.report"/></span>&nbsp;<html:link page="<%= new String("/file_download?key=" + timekey) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>icon_save.gif" border="0"></html:link><br>
            <br>

            <bean:message key="csv_errors_email"/>: <bean:write name="importWizardForm" property="status.error(email)" scope="session"/><br>
            <% csvfile += "\n" + SafeString.getLocaleString("csv_errors_email", (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ":;" + aForm.getStatus().getError("email"); %>

            <bean:message key="csv_errors_blacklist"/>: <bean:write name="importWizardForm" property="status.error(blacklist)" scope="session"/><br>
            <% csvfile += "\n" + SafeString.getLocaleString("csv_errors_blacklist", (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ":;" + aForm.getStatus().getError("blacklist"); %>

            <bean:message key="csv_errors_double"/>: <bean:write name="importWizardForm" property="status.error(emailDouble)" scope="session"/><br>
            <% csvfile += "\n" + SafeString.getLocaleString("csv_errors_double", (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ":;" + aForm.getStatus().getError("emailDouble"); %>

            <bean:message key="csv_errors_numeric"/>: <bean:write name="importWizardForm" property="status.error(numeric)" scope="session"/><br>
            <% csvfile += "\n" + SafeString.getLocaleString("csv_errors_numeric", (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ":;" + aForm.getStatus().getError("numeric"); %>
            
            <bean:message key="csv_errors_mailtype"/>: <bean:write name="importWizardForm" property="status.error(mailtype)" scope="session"/><br>
            <% csvfile += "\n" + SafeString.getLocaleString("csv_errors_mailtype", (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ":;" + aForm.getStatus().getError("mailtype"); %>

            <bean:message key="csv_errors_gender"/>: <bean:write name="importWizardForm" property="status.error(gender)" scope="session"/><br>
            <% csvfile += "\n" + SafeString.getLocaleString("csv_errors_gender", (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ":;" + aForm.getStatus().getError("gender"); %>

            <bean:message key="csv_errors_date"/>: <bean:write name="importWizardForm" property="status.error(date)" scope="session"/><br>
            <% csvfile += "\n" + SafeString.getLocaleString("csv_errors_date", (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ":;" + aForm.getStatus().getError("date"); %>

            <bean:message key="csv_errors_linestructure"/>: <bean:write name="importWizardForm" property="status.error(structure)" scope="session"/><br>
            <% csvfile += "\n" + SafeString.getLocaleString("csv_errors_linestructure", (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ":;" + aForm.getStatus().getError("structure"); %>

            <bean:message key="RecipientsAllreadyinDB"/>: <bean:write name="importWizardForm" property="status.updated" scope="session"/><br>
            <% csvfile += "\n" + SafeString.getLocaleString("RecipientsAllreadyinDB", (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ":;" + tmpUpdated; %>

            <% if(aForm.getMode()==ImportWizardForm.MODE_ADD || aForm.getMode()==ImportWizardForm.MODE_ADD_UPDATE) { %>
            <bean:message key="import.result.imported"/>:&nbsp;<bean:write name="importWizardForm" property="status.inserted" scope="session"/><br>
            <% csvfile += "\n" + SafeString.getLocaleString("import.result.imported", (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ":;" + tmpInserted;
               }
               if(aForm.getMode()==ImportWizardForm.MODE_ONLY_UPDATE || aForm.getMode()==ImportWizardForm.MODE_ADD_UPDATE) { %>
            <bean:message key="import.result.updated"/>:&nbsp;<bean:write name="importWizardForm" property="status.updated" scope="session"/><br>
            <%  csvfile += "\n" + SafeString.getLocaleString("import.result.updated", (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ":;" + tmpUpdated;
                }
                Hashtable allLists=aForm.getResultMailingListAdded();
                Enumeration keys=allLists.keys();
                String aKey=null;
                while(keys.hasMoreElements()) {
                    aKey=(String)keys.nextElement();
            %>
                <agn:ShowTable id="agnTbl" sqlStatement="<%= new String("SELECT shortname FROM mailinglist_tbl WHERE company_id=" + AgnUtils.getCompanyID(request) + " AND mailinglist_id="+aKey)%>">
                <%= pageContext.getAttribute("_agnTbl_shortname") %>:&nbsp;<%= allLists.get(aKey) %>&nbsp;
                <% switch(aForm.getMode()) {
                    case ImportWizardForm.MODE_ADD:
                    case ImportWizardForm.MODE_ADD_UPDATE:
                    case ImportWizardForm.MODE_ONLY_UPDATE: %>
                        <bean:message key="import.result.subscribersAdded"/>
                    <% break;
                    case ImportWizardForm.MODE_UNSUBSCRIBE:
                    case ImportWizardForm.MODE_BLACKLIST: %>
                        <bean:message key="import.result.subscribersUnsubscribed"/>
                    <% break;
                    case ImportWizardForm.MODE_BOUNCE: %>
                        <bean:message key="import.result.subscribersBounced"/>
                    <% break;
                    case ImportWizardForm.MODE_REMOVE_STATUS: %>
                        <bean:message key="import.result.bindingsRemoved"/>
                    <% } %>
                <br>
                </agn:ShowTable>
            <%
            }
            %>
            <% if(aForm.getMode()==ImportWizardForm.MODE_ADD || aForm.getMode()==ImportWizardForm.MODE_ADD_UPDATE) { %>
            <br><bean:message key="import.result.datasource_id"/>:&nbsp;<bean:write name="importWizardForm" property="datasourceID" scope="session"/>
            <% } %>
            <br><br>
                <html:link page="<%= new String("/recipient.do?action=" + RecipientAction.ACTION_LIST) %>" target="_top"><html:img src="button?msg=Finish" border="0"/></html:link>

        </logic:greaterEqual>
        </td>
   <td width="100%"></td>

</tr>
<% // put csv file from the form in the hash table:
   my_map.put(timekey, csvfile);
   pageContext.getSession().setAttribute("map", my_map);
%>

</body>
</html>
