<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.target.*, org.agnitas.stat.*, java.util.*, org.agnitas.web.*, org.agnitas.beans.*" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="stats.rdir"/>

<% int tmpMailingID=0;
    int tmpTargetID=0;
    // int tmpUniqueClicks=0;
    String tmpNetto = "no";
    String tmpShortname=new String("");
    EmmLayout aLayout=(EmmLayout)session.getAttribute("emm.layout");
    MailingStatForm aForm=null;
    int maxblue = 0;
    int maxNRblue = 0;
    int maxSubscribers = 0;
    if(session.getAttribute("mailingStatForm")!=null) {
        aForm=(MailingStatForm)session.getAttribute("mailingStatForm");
        tmpMailingID=aForm.getMailingID();
        tmpTargetID=aForm.getTargetID();
        tmpShortname=aForm.getMailingShortname();
        maxblue=aForm.getMaxblue();
        maxNRblue=aForm.getMaxNRblue();
        maxSubscribers=aForm.getMaxSubscribers();
    }

    java.text.DecimalFormat prcFormat = new java.text.DecimalFormat("##0.#");
    prcFormat.setDecimalSeparatorAlwaysShown(false);
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
    my_calendar.changeTimeWithZone(TimeZone.getTimeZone(AgnUtils.getAdmin(request).getAdminTimezone()));
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

    <% //prepare loop over targetIDs:
        Hashtable statValues = new Hashtable();
        statValues = ((MailingStatForm)session.getAttribute("mailingStatForm")).getStatValues();
        LinkedList targets = null;
        ListIterator targetIter = null;
        targets = ((MailingStatForm)session.getAttribute("mailingStatForm")).getTargetIDs();
    %>


    <table border="0" cellspacing="0" cellpadding="0" width="100%">
    
        <tr>
            <% /* * * * * * * * * * */ %>
            <% /* add target group  */ %>
            <% /* * * * * * * * * * */ %>
            <% if(targets.size() < 5) { %>
            <td valign="bottom">
                <bean:message key="Target"/>:&nbsp;
                <!-- size="1" -->
                <html:select property="nextTargetID">
                    <html:option value="0"><bean:message key="All_Subscribers"/></html:option>
                    <agn:HibernateQuery id="trgt" query="<%= "from Target where companyID="+AgnUtils.getCompanyID(request) %>">
                        <% if(!targets.contains(new Integer(((Target)pageContext.getAttribute("trgt")).getId()))) { %>
                        <html:option value="<%= ""+((Target)pageContext.getAttribute("trgt")).getId() %>"><%= ((Target)pageContext.getAttribute("trgt")).getTargetName() %></html:option>
                        <% } %>
                    </agn:HibernateQuery>
                </html:select>
                <html:image align="bottom" src="button?msg=Add" border="0" property="add" value="add"/><br>&nbsp;
            </td>
            <% } %>
            <td align="right">
                <html:link page="<%= new String("/file_download?key=" + timekey) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>icon_save.gif" border="0"></html:link>
            </td>
        </tr>

    </table>


    <table border="0" cellspacing="0" cellpadding="0">

        <% /* * * * * * * */ %>
        <% /* CLICK STATS */ %>
        <% /* * * * * * * */ %>
        <tr>
            <td><span class="head3"><bean:message key="KlickStats"/>:<br><br></span></td>
            <% for(int columns=0; columns<targets.size(); columns++) { %>
            <td>&nbsp;</td>
            <% } %>
        </tr>

        <% /* * * * * * * * * * * */ %>
        <% /* clicks table header */ %>
        <% /* * * * * * * * * * * */ %>
        <tr>
            <td><b><bean:message key="URL"/>&nbsp;</b></td>
            <% file += "\r\n\r\n\"" + SafeString.getLocaleString("URL", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\";\"" + SafeString.getLocaleString("Description", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\"";
                for(int columns=0; columns<targets.size(); columns++) { %>
            <td align="right">&nbsp;<b><bean:message key="ClicksBruttoNetto"/></b></td>
        
            <% //file += ";\"" + SafeString.getLocaleString("Clicks", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\"";
                } %>
            </tr>

        <% /* * * * * * * * */ %>
        <% /* target groups */ %>
        <% /* * * * * * * * */ %>
        <tr>
            <td align="right">&nbsp;</td>
            <%
                targetIter = targets.listIterator();
                while(targetIter.hasNext()) {
                    int aktTargetID = ((Integer)targetIter.next()).intValue();
                    MailingStatEntry aktMailingStatEntry = (MailingStatEntry)statValues.get(new Integer(aktTargetID));
            %>
            <td align="right">&nbsp;<b><%=aktMailingStatEntry.getTargetName()%></b>
                <% file += ";\"" + SafeString.getLocaleString("ClicksBruttoNetto", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ", " + aktMailingStatEntry.getTargetName() + "\""; %>
                <% if(targets.size()>1) { %>
                &nbsp;<html:link page="<%= new String("/mailing_stat.do?action=" + Integer.toString(MailingStatAction.ACTION_MAILINGSTAT) + "&delTargetID=" + aktTargetID) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>delete.gif" alt="<bean:message key="Delete"/>" border="0"></html:link>&nbsp;
                <% } %>
            </td>
 
            <% } %>
        </tr>

        <tr>
            <td colspan="<%=(targets.size() + 1)%>"><hr></td>
        </tr>




        <% // * * * * * * * * * * * * * * * * * * * * %>
        <% // * *  R E L E V A N T  C L I C K S:  * * %>
        <% // * * * * *  ( b e g i n )  * * * * * * * %>
        <% // * * * * * * * * * * * * * * * * * * * * %>

        <% boolean changeColor=true; %>

        <% /* * * * * * * * * * * */ %>
        <% /* loop over all URLs  */ %>
        <% /* * * * * * * * * * * */ %>
        <%
            int aktTargetID = 0;
            int aktUrlID = 0;
            Hashtable urlNames = new Hashtable();
            Hashtable urlShortnames = new Hashtable();
            urlNames = ((MailingStatForm)session.getAttribute("mailingStatForm")).getUrlNames();
            urlShortnames = ((MailingStatForm)session.getAttribute("mailingStatForm")).getUrlShortnames();
            java.text.NumberFormat nf = java.text.NumberFormat.getCurrencyInstance((Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY));
            LinkedList tmpClickedUrls = new LinkedList();
            tmpClickedUrls = aForm.getClickedUrls();
            int urlIndex = 0;
            HashSet map=new HashSet();

            while(urlIndex < tmpClickedUrls.size()) {
                aktUrlID = ((URLStatEntry)(tmpClickedUrls.get(urlIndex))).getUrlID();
                map.add(new Integer(aktUrlID));

                urlIndex++;
            }
            urlIndex = 0;
            while(urlIndex < map.size()) {
                aktUrlID = ((URLStatEntry)(tmpClickedUrls.get(urlIndex))).getUrlID();
                urlIndex++;
                TrackableLink trkLnk=(TrackableLink) urlNames.get(new Integer(aktUrlID));
                file += "\r\n\"" + trkLnk.getFullUrl() + "\";\"" + urlShortnames.get(new Integer(aktUrlID)) + "\"";
        %>

        <%
            // * * * * * * * * * * * * * *
            // * *  outer loop start:  * *
            // * * * * * * * * * * * * * * %>
        

        <% /* * * * * * * * * * * * * * * * * * * * * * */ %>
        <% /* clicks table (inner loop over targetIDs)  */ %>
        <% /* * * * * * * * * * * * * * * * * * * * * * */ %>
        <% if(changeColor) { %>
        <tr bgcolor="<bean:write name="emm.layout" property="normalColor" scope="session"/>">
        <% } else { %>
        <tr>
            <% } changeColor=!changeColor; %>
            <td valign="center"><a href="<%= urlNames.get(new Integer(aktUrlID)) %>" target="_blank">
                <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>extlink.gif" border="0" alt="<%= urlNames.get(new Integer(aktUrlID)) %>">
            </a>&nbsp;
            <html:link page="<%= new String("/mailing_stat.do?action=" + MailingStatAction.ACTION_WEEKSTAT + "&mailingID=" + tmpMailingID + "&urlID=" + aktUrlID + "&targetID=0") %>">
            <% if(((String)urlShortnames.get(new Integer(aktUrlID))).compareTo("")!=0) { %><%= urlShortnames.get(new Integer(aktUrlID)) %><% } else { %><%= urlNames.get(new Integer(aktUrlID)) %><% } %></html:link>&nbsp;</td>
            <%
                targetIter = targets.listIterator();
                while(targetIter.hasNext()) {
                    aktTargetID = ((Integer)targetIter.next()).intValue();
                    MailingStatEntry aktMailingStatEntry = (MailingStatEntry)statValues.get(new Integer(aktTargetID));
                    Hashtable aktClickStatValues = (Hashtable)aktMailingStatEntry.getClickStatValues();
                    URLStatEntry aktURLStatEntry = (URLStatEntry)aktClickStatValues.get(new Integer(aktUrlID));
                    int barNetto = 1;
                    int barDiff = 0;
                    int barFree = 149;
                    double agnClkNetto = 0;
                    double agnClkDiff = 0;
                    if(aktURLStatEntry != null) {
                        agnClkNetto = (double)aktURLStatEntry.getClicksNetto() / (double)maxblue;
                        agnClkDiff = (double)(aktURLStatEntry.getClicks() - aktURLStatEntry.getClicksNetto()) / (double)maxblue;
                        barNetto=(int)(agnClkNetto * 150.0) +1 ;
                        barDiff=(int)(agnClkDiff * 150.0) ;
                        barFree=150-barNetto-barDiff;
                    }
            %>

            <td align="right" width="165"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="3" height="3" border="0"><table width="151" cellspacing="0" cellpadding="0" border="0">
                <tr>
                    <td align="right" style="border:1px solid #444444;" bgcolor="#ffffff">
                        <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="<%=barFree%>" height="10" border="0"><% if(barDiff!=0) { %><img border="0" width="<%=barDiff%>" height="10" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_s.gif"/><% } %><img border="0" width="<%=barNetto%>" height="10" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif"/>
                    </td>
                    <td>
                        <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="3" height="3" border="0">
                    </td>
                </tr>
            </table>
                <html:link page="<%= new String("/mailing_stat.do?action=" + MailingStatAction.ACTION_WEEKSTAT + "&mailingID=" + tmpMailingID + "&urlID=" + aktUrlID + "&targetID=" + aktTargetID) %>">
                    <% if(aktURLStatEntry != null) { %><%= aktURLStatEntry.getClicks() %> (<%= aktURLStatEntry.getClicksNetto() %>)<% } else { %>0 (0)<% } %>&nbsp;
                    <% // Prozent-Anzeige: %>
                    <% if( aktTargetID==0 && aktMailingStatEntry.getTotalMails()!=0 ) {
                            if(aktURLStatEntry != null) {
                            double prc = (double)aktURLStatEntry.getClicksNetto()/(double)aktMailingStatEntry.getTotalMails() * 100d;  %>
                    (<%=prcFormat.format(prc)%>&nbsp;%)&nbsp;
                    <% } else { %>
                    (0&nbsp;%)&nbsp;
                    <% }
                        } %>
                    
                </html:link>
            </td>        
            <% if(aktURLStatEntry != null) {
                            file += ";\"" + aktURLStatEntry.getClicks() + " (" + aktURLStatEntry.getClicksNetto() + ")\"";
                } else {
                            file += ";\"0 (0)\"";
                }%>
            
            
            <% /* * * * * * * * * * * * * * * * */ %>
            <% /* end inner loop over targetIDs */ %>
            <% /* * * * * * * * * * * * * * * * */ %>
      

            <% /* * * * * * * * * * * * * * * * */ %>
            <% /* end loop over clickStat URLs  */ %>
            <% /* * * * * * * * * * * * * * * * */ %>
            <% } %>
        </tr>
        <%
            }

            // * * * * * * * * * * * * *
            // * *  outer loop end:  * *
            // * * * * * * * * * * * * *
        %>

        <% // * * * * * * * * * * * * * * * * * * * * %>
        <% // * * * * * *  ( e n d )  * * * * * * * * %>
        <% // * *  R E L E V A N T  C L I C K S:  * * %>
        <% // * * * * * * * * * * * * * * * * * * * * %>





        <% /* * * * * * */ %>
        <% /* empty row */ %>
        <% /* * * * * * */ %>
        <tr>
            <td colspan="<%=(targets.size() + 1)%>">&nbsp;</td>
            <% file += "\r\n"; %>
        </tr>





        <% /* * * * * * * * * * * * * */ %>
        <% /* total clickSubscribers  */ %>
        <% /* * * * * * * * * * * * * */ %>
        <tr>
            <td><bean:message key="TotalClickSubscribers"/>:&nbsp;</td>
            <%  file += "\"" + SafeString.getLocaleString("TotalClickSubscribers", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) +"\";\"\"";
                targetIter = targets.listIterator();
                while(targetIter.hasNext()) {
                    aktTargetID = ((Integer)targetIter.next()).intValue();
                    MailingStatEntry aktMailingStatEntry = (MailingStatEntry)statValues.get(new Integer(aktTargetID));
                    file += ";\"" + aktMailingStatEntry.getTotalClickSubscribers() + "\"";
                    double agnTotal = (double)aktMailingStatEntry.getTotalClickSubscribers() / (double)maxblue;
                    int barNetto=(int)(agnTotal * 150.0) +1 ;
                    int barFree=150-barNetto;
            %>
            <td align="right">
                <table width="151" cellspacing="0" cellpadding="0" border="0">
                    <tr>
                        <td align="right" style="border:1px solid #444444;" bgcolor="#ffffff">
                            <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="<%=barFree%>" height="10" border="0"><img border="0" width="<%=barNetto%>" height="10" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif"/>
                        </td>
                        <td>
                            <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="3" height="3" border="0">
                        </td>
                    </tr>
                </table>
                &nbsp;<%=aktMailingStatEntry.getTotalClickSubscribers()%>&nbsp;
                <% // Prozent-Anzeige: %>
                <% if( aktTargetID==0 && aktMailingStatEntry.getTotalMails()!=0 ) {
                            double prc = (double)aktMailingStatEntry.getTotalClickSubscribers()/(double)aktMailingStatEntry.getTotalMails() * 100d;  %>
                (<%=prcFormat.format(prc)%>&nbsp;%)&nbsp;
                <% } %>
            </td>

            <% } %>
        </tr>

        <% /*  <HR> */ %>
        <tr>
            <td colspan="<%=(targets.size() + 1)%>"><hr size="1" noshade></td>
        </tr>



        <% /* * * * * * * * */ %>
        <% /* total clicks  */ %>
        <% /* * * * * * * * */ %>
        <tr>
            <td><html:link page="<%= new String("/mailing_stat.do?action=" + MailingStatAction.ACTION_WEEKSTAT + "&mailingID=" + tmpMailingID + "&urlID=0") %>"><b><bean:message key="TotalClicks"/>:&nbsp;</b></html:link></td>
            <%  file += "\r\n\"" + SafeString.getLocaleString("TotalClicks", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) +"\";\"\"";
                targetIter = targets.listIterator();
                while(targetIter.hasNext()) {
                    aktTargetID = ((Integer)targetIter.next()).intValue();
                    MailingStatEntry aktMailingStatEntry = (MailingStatEntry)statValues.get(new Integer(aktTargetID));
                    double agnClkNetto = (double)aktMailingStatEntry.getTotalClicksNetto() / (double)maxblue;
                    double agnClkDiff = ((double)aktMailingStatEntry.getTotalClicks() - (double)aktMailingStatEntry.getTotalClicksNetto()) / (double)maxblue;
                    int barNetto=(int)(agnClkNetto * 150.0) +1 ;
                    int barDiff=(int)(agnClkDiff * 150.0) ;
                    int barFree=150-barNetto-barDiff;
                    file += ";\"" + aktMailingStatEntry.getTotalClicks() + " (" + aktMailingStatEntry.getTotalClicksNetto() + ")\"";
            %>
            <td align="right">
            <table width="151" cellspacing="0" cellpadding="0" border="0">
                <tr>
                    <td align="right" style="border:1px solid #444444;" >
                        <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="<%=barFree%>" height="10" border="0"><% if(barDiff>0) { %><img border="0" width="<%=barDiff%>" height="10" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_s.gif"/><% } %><img border="0" width="<%=barNetto%>" height="10" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif"/>
                    </td>
                    <td>
                        <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="3" height="3" border="0">
                    </td>
                </tr>
            </table>

            &nbsp;<b><%=aktMailingStatEntry.getTotalClicks()%> (<%=aktMailingStatEntry.getTotalClicksNetto()%>)</b>&nbsp;</td>

            <% } %>
        </tr>

        <tr>
            <td colspan="<%=(targets.size() + 1)%>"><hr></td>
        </tr>

        <tr>
            <td colspan="<%=(targets.size() + 1)%>">&nbsp;</td>
            <% file += "\r\n"; %>
        </tr>




        <% // * * * * * * * * * * * * * * * * * * * * * * * * %>
        <% // * *  N O N - R E L E V A N T  C L I C K S:  * * %>
        <% // * * * * * * *  ( b e g i n )  * * * * * * * * * %>
        <% // * * * * * * * * * * * * * * * * * * * * * * * * %>
        <%
            LinkedList tmpNotRelevantUrls = new LinkedList();
            tmpNotRelevantUrls = aForm.getNotRelevantUrls();
            if(tmpNotRelevantUrls.size()>0) {
        %>


        <% /* headline */ %>
        <tr>
            <td><span class="head3"><bean:message key="OtherLinks"/>:&nbsp;</span></td>
            <% for(int columns=0; columns<targets.size(); columns++) { %>
            <td>&nbsp;</td>
            <% } %>
        </tr>



        <tr>
            <td colspan="<%=(targets.size() + 1)%>">&nbsp;</td>
        </tr>

        <%  file += "\r\n\" " + SafeString.getLocaleString("OtherLinks", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + " \""; %>

        <% changeColor=true; %>

        <% /* * * * * * * * * * * */ %>
        <% /* loop over all URLs  */ %>
        <% /* * * * * * * * * * * */ %>
        <%
            // int aktTargetID = 0;
            // int aktUrlID = 0;
            // Hashtable urlNames = new Hashtable();
            // Hashtable urlShortnames = new Hashtable();
            // urlNames = ((MailingStatForm)session.getAttribute("mailingStatForm")).getUrlNames();
            // urlShortnames = ((MailingStatForm)session.getAttribute("mailingStatForm")).getUrlShortnames();
            // java.text.NumberFormat nf = java.text.NumberFormat.getCurrencyInstance((Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY));

            urlIndex = 0;
            while(urlIndex < tmpNotRelevantUrls.size()) {
                aktUrlID = ((URLStatEntry)(tmpNotRelevantUrls.get(urlIndex))).getUrlID();

                file += "\r\n\"" + urlNames.get(new Integer(aktUrlID)) + "\";\"" + urlShortnames.get(new Integer(aktUrlID)) + "\"";
                urlIndex++;
        %>

        <%
            // * * * * * * * * * * * * *
            // * * outer loop start: * *
            // * * * * * * * * * * * * * %>
        
        <% /* * * * * * * * * * * * * * * * * * * * * * */ %>
        <% /* clicks table (inner loop over targetIDs)  */ %>
        <% /* * * * * * * * * * * * * * * * * * * * * * */ %>
        <% if(changeColor) { %>
        <tr bgcolor="<bean:write name="emm.layout" property="normalColor" scope="session"/>">
        <% } else { %>
        <tr>
            <% } changeColor=!changeColor; %>
            <td valign="center"><a href="<%= urlNames.get(new Integer(aktUrlID)) %>" target="_blank">
                <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>extlink.gif" border="0" alt="<%= urlNames.get(new Integer(aktUrlID)) %>">
            </a>&nbsp;
            <html:link page="<%= new String("/mailing_stat.do?action=" + MailingStatAction.ACTION_WEEKSTAT + "&mailingID=" + tmpMailingID + "&urlID=" + aktUrlID + "&targetID=0") %>">
            <% if(((String)urlShortnames.get(new Integer(aktUrlID))).compareTo("")!=0) { %><%= urlShortnames.get(new Integer(aktUrlID)) %><% } else { %><%= urlNames.get(new Integer(aktUrlID)) %><% } %></html:link>&nbsp;</td>
            <%
                targetIter = targets.listIterator();
                while(targetIter.hasNext()) {
                    aktTargetID = ((Integer)targetIter.next()).intValue();
                    MailingStatEntry aktMailingStatEntry = (MailingStatEntry)statValues.get(new Integer(aktTargetID));
                    Hashtable aktClickStatValues = (Hashtable)aktMailingStatEntry.getClickStatValues();
                    URLStatEntry aktURLStatEntry = (URLStatEntry)aktClickStatValues.get(new Integer(aktUrlID));
                    int barNetto = 1;
                    int barDiff = 0;
                    int barFree = 149;
                    double agnClkNetto = 0;
                    double agnClkDiff = 0;
                    if(aktURLStatEntry != null) {
                        agnClkNetto = (double)aktURLStatEntry.getClicksNetto() / (double)maxNRblue;
                        agnClkDiff = (double)(aktURLStatEntry.getClicks() - aktURLStatEntry.getClicksNetto()) / (double)maxNRblue;
                        barNetto=(int)(agnClkNetto * 150.0) +1 ;
                        barDiff=(int)(agnClkDiff * 150.0) ;
                        barFree=150-barNetto-barDiff;
                    }
            %>
      
            <td align="right" width="165"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="3" height="3" border="0"><table width="151" cellspacing="0" cellpadding="0" border="0">
                <tr>
                    <td align="right" style="border:1px solid #444444;" bgcolor="#ffffff">
                        <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="<%=barFree%>" height="10" border="0"><% if(barDiff!=0) { %><img border="0" width="<%=barDiff%>" height="10" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_s.gif"/><% } %><img border="0" width="<%=barNetto%>" height="10" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif"/>
                    </td>
                    <td>
                        <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="3" height="3" border="0">
                    </td>
                </tr>
            </table>
                <html:link page="<%= new String("/mailing_stat.do?action=" + MailingStatAction.ACTION_WEEKSTAT + "&mailingID=" + tmpMailingID + "&urlID=" + aktUrlID + "&targetID=" + aktTargetID) %>">
                    <% if(aktURLStatEntry != null) { %><%= aktURLStatEntry.getClicks() %> (<%= aktURLStatEntry.getClicksNetto() %>)<% } else { %>0 (0)<% } %>&nbsp;
                    <% // Prozent-Anzeige: %>
                    <% if( aktTargetID==0 && aktMailingStatEntry.getTotalMails()!=0 ) {
                            if(aktURLStatEntry != null) {
                            double prc = (double)aktURLStatEntry.getClicksNetto()/(double)aktMailingStatEntry.getTotalMails() * 100d;  %>
                    (<%=prcFormat.format(prc)%>&nbsp;%)&nbsp;
                    <% } else {%>  
                    (0&nbsp;%)&nbsp;
                    <% }
                        } %>
                    
                </html:link>
            </td>        
            <% if(aktURLStatEntry != null) {
                            file += ";\"" + aktURLStatEntry.getClicks() + " (" + aktURLStatEntry.getClicksNetto() + ")\"";
                } else {
                            file += ";\"0 (0)\"";
                }%>
            
           
            <% /* * * * * * * * * * * * * * * * */ %>
            <% /* end inner loop over targetIDs */ %>
            <% /* * * * * * * * * * * * * * * * */ %>
            <%      } %>
       

            <% /* * * * * * * * * * * * * * * * */ %>
            <% /* end loop over clickStat URLs  */ %>
            <% /* * * * * * * * * * * * * * * * */ %>
        </tr> 
        <%
            }
        %>

        <%
            // * * * * * * * * * * * *
            // * * outer loop end: * *
            // * * * * * * * * * * * *
        %>

        <% } %>

        <% // * * * * * * * * * * * * * * * * * * * * * * * * %>
        <% // * * * * * * * *  ( e n d )  * * * * * * * * * * %>
        <% // * *  N O N - R E L E V A N T  C L I C K S:  * * %>
        <% // * * * * * * * * * * * * * * * * * * * * * * * * %>





        <% /* empty row */ %>
        <tr>
            <td colspan="<%=(targets.size() + 1)%>">&nbsp;</td>
        </tr>
 
        <% file += "\r\n"; %>



        <% /* * * * * * * * * */ %>
        <% /* DELIVERY STATS  */ %>
        <% /* * * * * * * * * */ %>
        <tr>
            <td><span class="head3"><bean:message key="Delivery_Statistic"/>:<br><br></span></td>
            <% file += "\r\n\r\n\" " + SafeString.getLocaleString("Delivery_Statistic", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + " \"\r\n"; %>       
            <% for(int columns=0; columns<targets.size(); columns++) { %>
            <td>&nbsp;</td>
            <% } %>
        </tr>

        <%
            // delivery stats for each target group
        %>


        <% /* * * * * * * * */ %>
        <% /* opened mails  */ %>
        <% /* * * * * * * * */ %>
        <tr bgcolor="<bean:write name="emm.layout" property="normalColor" scope="session"/>">
            <td>&nbsp;<html:link page="<%= new String("/mailing_stat.do?action=" + MailingStatAction.ACTION_OPENEDSTAT + "&mailingID=" + tmpMailingID + "&urlID=" + aktUrlID) %>"><B><bean:message key="Opened_Mails"/>:</B></html:link>&nbsp;</td>
            <%
                file += "\r\n\" " + SafeString.getLocaleString("Opened_Mails", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + " \";\"\"";
                targetIter = targets.listIterator();
                while(targetIter.hasNext()) {
                    aktTargetID = ((Integer)targetIter.next()).intValue();
                    MailingStatEntry aktMailingStatEntry = (MailingStatEntry)statValues.get(new Integer(aktTargetID));
                    file += ";\"" + aktMailingStatEntry.getOpened() + "\"";
                    int barBlue=1;
                    int barFree=149;
                    if(maxSubscribers >= aktMailingStatEntry.getOpened()) {
                        double agnBlue = (double)aktMailingStatEntry.getOpened() / (double)maxSubscribers;
                        barBlue=(int)(agnBlue * 150.0) +1;
                        barFree=150-barBlue;
                    }
            %>
            <td align="right"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="3" border="0"><table width="151" cellspacing="0" cellpadding="0" border="0">
                <tr>
                    <td align="right" style="border:1px solid #444444;" bgcolor="#ffffff">
                        <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="<%=barFree%>" height="10" border="0"><img border="0" width="<%=barBlue%>" height="10" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif"/>
                    </td>
                    <td>
                        <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="3" height="3" border="0">
                    </td>
                </tr>
            </table>

                &nbsp;<b><%=aktMailingStatEntry.getOpened()%></b>&nbsp;
                <% // Prozent-Anzeige: %>
                <% if(aktMailingStatEntry.getTotalMails()!=0) {
                    double prc = (double)aktMailingStatEntry.getOpened()/(double)aktMailingStatEntry.getTotalMails() * 100d;  %>
                (<%=prcFormat.format(prc)%>&nbsp;%)&nbsp;
                <% } %>
            </td>
            <% } %>
        </tr>


        <% /* * * * * */ %>
        <% /* optouts */ %>
        <% /* * * * * */ %>
        <tr>
            <td>&nbsp;<B><bean:message key="Opt_Outs"/>:&nbsp;</B></td>
            <%
                file += "\r\n\" " + SafeString.getLocaleString("Opt_Outs", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + " \";\"\"";
                targetIter = targets.listIterator();
                while(targetIter.hasNext()) {
                    aktTargetID = ((Integer)targetIter.next()).intValue();
                    MailingStatEntry aktMailingStatEntry = (MailingStatEntry)statValues.get(new Integer(aktTargetID));
                    file += ";\"" + aktMailingStatEntry.getOptouts() + "\"";
                    int barBlue=1;
                    int barFree=149;
                    if(maxSubscribers >= aktMailingStatEntry.getOptouts()) {
                        double agnBlue = (double)aktMailingStatEntry.getOptouts() / (double)maxSubscribers;
                        barBlue=(int)(agnBlue * 150.0) +1 ;
                        barFree=150-barBlue;
                    }
            %>
            <td align="right"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="3" border="0"><table width="151" cellspacing="0" cellpadding="0" border="0">
                <tr>
                    <td align="right" style="border:1px solid #444444;" >
                        <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="<%=barFree%>" height="10" border="0"><img border="0" width="<%=barBlue%>" height="10" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif"/>
                    </td>
                    <td>
                        <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="3" height="3" border="0">
                    </td>
                </tr>
            </table>
    
                &nbsp;<b><%=aktMailingStatEntry.getOptouts()%></b>&nbsp;
                <% // Prozent-Anzeige: %>
                <% if(aktMailingStatEntry.getTotalMails()!=0) {
                    double prc = (double)aktMailingStatEntry.getOptouts()/(double)aktMailingStatEntry.getTotalMails() * 100d;  %>
                (<%=prcFormat.format(prc)%>&nbsp;%)&nbsp;
                <% } %>
            </td>
            <% } %>
        </tr>



        <% /* * * * * */ %>
        <% /* bounces */ %>
        <% /* * * * * */ %>
        <tr bgcolor="<bean:write name="emm.layout" property="normalColor" scope="session"/>">
            <td>&nbsp;<html:link page="<%= new String("/mailing_stat.do?action=" + MailingStatAction.ACTION_BOUNCESTAT + "&mailingID=" + tmpMailingID + "&urlID=" + aktUrlID) %>"><B><bean:message key="Bounces"/>:&nbsp;</B></html:link></td>
            <%
                file += "\r\n\" " + SafeString.getLocaleString("Bounces", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + " \";\"\"";
                targetIter = targets.listIterator();
                while(targetIter.hasNext()) {
                    aktTargetID = ((Integer)targetIter.next()).intValue();
                    MailingStatEntry aktMailingStatEntry = (MailingStatEntry)statValues.get(new Integer(aktTargetID));
                    file += ";\"" + aktMailingStatEntry.getBounces() + "\"";
                    int barBlue=1;
                    int barFree=149;
                    if(maxSubscribers >= aktMailingStatEntry.getBounces()) {
                        double agnBlue = (double)aktMailingStatEntry.getBounces() / (double)maxSubscribers;
                        barBlue=(int)(agnBlue * 150.0) +1 ;
                        barFree=150-barBlue;
                    }
            %>
            <td align="right"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="3" border="0"><table width="151" cellspacing="0" cellpadding="0" border="0">
                <tr>
                    <td align="right" style="border:1px solid #444444;" bgcolor="#ffffff">
                        <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="<%=barFree%>" height="10" border="0"><img border="0" width="<%=barBlue%>" height="10" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif"/>
                    </td>
                    <td>
                        <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="3" height="3" border="0">
                    </td>
                </tr>
            </table>
    
                &nbsp;<b><%=aktMailingStatEntry.getBounces()%></b>&nbsp;
                <% // Prozent-Anzeige: %>
                <% if(aktMailingStatEntry.getTotalMails()!=0) {
                    double prc = (double)aktMailingStatEntry.getBounces()/(double)aktMailingStatEntry.getTotalMails() * 100d;  %>
                (<%=prcFormat.format(prc)%>&nbsp;%)&nbsp;
                <% } %>
            </td>
            <% } %>
        </tr>


        <tr>
            <td colspan="<%=(targets.size() + 1)%>"><hr></td>
        </tr>



        <% /* * * * * * * */ %>
        <% /* total mails */ %>
        <% /* * * * * * * */ %>
        <tr>
            <td>&nbsp;<B><bean:message key="Receipients"/>:&nbsp;</B></td>
            <%
                file += "\r\n\" " + SafeString.getLocaleString("Receipients", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + " \";\"\"";
                targetIter = targets.listIterator();
                while(targetIter.hasNext()) {
                    aktTargetID = ((Integer)targetIter.next()).intValue();
                    MailingStatEntry aktMailingStatEntry = (MailingStatEntry)statValues.get(new Integer(aktTargetID));
                    file += ";\"" + aktMailingStatEntry.getTotalMails() + "\"";
                    int barBlue=1;
                    int barFree=149;
                    if(maxSubscribers >= aktMailingStatEntry.getTotalMails()) {
                        double agnBlue = (double)aktMailingStatEntry.getTotalMails() / (double)maxSubscribers;
                        barBlue=(int)(agnBlue * 150.0) +1 ;
                        barFree=150-barBlue;
                    }
            %>
            <td align="right">
                <table width="151" cellspacing="0" cellpadding="0" border="0">
                    <tr>
                        <td align="right" style="border:1px solid #444444;" >
                            <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="<%=barFree%>" height="10" border="0"><img border="0" width="<%=barBlue%>" height="10" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif"/>
                        </td>
                        <td>
                            <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="3" height="3" border="0">
                        </td>
                    </tr>
                </table>

                &nbsp;<b><%=aktMailingStatEntry.getTotalMails()%></b>&nbsp;
            </td>
            <% } %>
        </tr>

    </table>

    <%
        my_map.put(timekey,  file);
        /*
        System.out.println("#######################################");
        System.out.println(file);
        System.out.println("#######################################");
         */
        pageContext.getSession().setAttribute("map", my_map);

    %>

</html:form>

<%@include file="/footer.jsp"%>
