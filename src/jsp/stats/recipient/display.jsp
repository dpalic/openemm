<%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, java.util.*, java.text.*" contentType="text/html; charset=utf-8" buffer="32kb" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>
<agn:Permission token="stats.mailing"/>

<%
pageContext.setAttribute("sidemenu_active", new String("Statistics"));
pageContext.setAttribute("sidemenu_sub_active", new String("AboStat"));
pageContext.setAttribute("agnTitleKey", new String("Statistics"));
pageContext.setAttribute("agnSubtitleKey", new String("Statistics"));
pageContext.setAttribute("agnNavigationKey", new String("statsRecipients"));
pageContext.setAttribute("agnHighlightKey", new String("AboStat"));
%>

<%
int mailinglistID=0;
int targetID=0;
int mediaType=0;

try {
    mailinglistID=Integer.parseInt(request.getParameter("mailingListID"));
} catch (Exception e) {
    mailinglistID=0;
}

try {
    targetID=Integer.parseInt(request.getParameter("targetID"));
} catch (Exception e) {
    targetID=0;
}

try {
    mediaType=Integer.parseInt(request.getParameter("mediaType"));
} catch (Exception e) {
    mediaType=0;
}



DateFormat aFormat3=new SimpleDateFormat("yyyyMM");
DateFormat aFormat4=new SimpleDateFormat("yyyyMMdd");
GregorianCalendar aCal=new GregorianCalendar(TimeZone.getTimeZone(AgnUtils.getAdmin(request).getAdminTimezone()));
try {
      aCal.setTime(aFormat3.parse(request.getParameter("month")));
} catch (Exception e) {
      aCal.set(Calendar.DAY_OF_MONTH, 1);  // set to first day in month!
}

// key for the csv download
java.util.Date my_time = aCal.getTime();
String Datum = my_time.toString();
String timekey = Long.toString(my_time.getTime());
pageContext.setAttribute("time_key", timekey);     // Long.toString((aCal.getTime()).getTime())

// map for the csv download
Hashtable<String, String> my_map = null;
if(pageContext.getSession().getAttribute("map") == null)
{
    my_map = new Hashtable<String, String>();
    pageContext.getSession().setAttribute("map",my_map);
    // System.out.println("map exists.");
} else {
    my_map = (Hashtable<String, String>) pageContext.getSession().getAttribute("map");
    // System.out.println("new map.");
}
%>

<%@include file="/header.jsp"%>
<BR>
<html:form action="/recipient_stats" method="post">
    <html:hidden property="action"/>
    <html:hidden property="month"/>

  <table border="0" cellspacing="0" cellpadding="0">
     <tr>
        <td>
          <span class="head3"><bean:message key="Mailinglist"/>:&nbsp;</span></td>
        <td>
          <html:select property="mailingListID" size="1">
              <html:option value="0"><bean:message key="All_Mailinglists"/></html:option>
            <agn:ShowTable id="agntbl1" sqlStatement="<%= new String("SELECT mailinglist_id, shortname FROM mailinglist_tbl WHERE company_id="+AgnUtils.getCompanyID(request)+ " ORDER BY shortname") %>" >
              <html:option value="<%= (String)pageContext.getAttribute("_agntbl1_mailinglist_id") %>"><%= pageContext.getAttribute("_agntbl1_shortname") %></html:option>
            </agn:ShowTable>
          </html:select>
        </td>
        <td><div align="right"> <html:link page="<%= new String("/file_download?key=" + timekey) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>icon_save.gif" border="0"></html:link></div></td>
     </tr>
     <tr>
        <td>
           <span class="head3"><bean:message key="Target"/>:&nbsp;</span>
        </td>
        <td>
            <html:select property="targetID" size="1">
                <html:option value="0"><bean:message key="All_Subscribers"/></html:option>
                <agn:ShowTable id="agntbl3" sqlStatement="<%= new String("SELECT target_id, target_shortname FROM dyn_target_tbl WHERE company_id="+AgnUtils.getCompanyID(request))+" order by target_shortname" %>" >
                    <html:option value="<%= (String)(pageContext.getAttribute("_agntbl3_target_id")) %>"><%= pageContext.getAttribute("_agntbl3_target_shortname") %></html:option>
                </agn:ShowTable>
            </html:select>&nbsp;&nbsp;&nbsp;
        </td>
        <td>&nbsp;</td>
     </tr>
     <html:hidden property="mediaType" value="0"/>
     <tr>
        <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="5" border="0"></td>
        <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="5" border="0"></td>
        <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="5" border="0"></td>
     </tr>

     <tr>
        <td colspan="3">
            <html:image src="button?msg=OK" border="0"/>
        </td>
     </tr>

  </table>

  <table border="0" cellspacing="0" cellpadding="0">
         <tr>
              <td colspan="3"><BR><hr size="1">
                <span class="head3"><bean:message key="RecipientStatus"/>:</span>
           </td>
         </tr>
         <tr>
              <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="5" border="0"></td>
              <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="5" border="0"></td>
              <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="5" border="0"></td>
         </tr>

         <tr>
             <td><B><bean:message key="Unsubscribes"/>:&nbsp;&nbsp;</B></td>
             <td>
                 <table border="0" cellspacing="0" cellpadding="0">
                    <tr height="20">
                       <td bgcolor="<bean:write name="emm.layout" property="highlightColor" scope="session"/>" width="<bean:write name="recipientStatForm" property="blueOptout" scope="request"/>">&nbsp;</td>
                    </tr>
                </table>
             </td>
             <td><div align=right><b>&nbsp;<bean:write name="recipientStatForm" property="numOptout" scope="request"/><b></div></td>
          </tr>

          <tr>
              <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="5" border="0"></td>
              <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="5" border="0"></td>
              <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="5" border="0"></td>
          </tr>

          <tr>
             <td align="left"><b><bean:message key="Bounces"/>:&nbsp;&nbsp;</b></td>
             <td>
                 <table border="0" cellspacing="0" cellpadding="0">
                    <tr height="20">
                       <td bgcolor="<bean:write name="emm.layout" property="highlightColor" scope="session"/>" width="<bean:write name="recipientStatForm" property="blueBounce" scope="request"/>">&nbsp;</td>
                    </tr>
                </table>
             </td>
             <td><div align=right><b>&nbsp;<bean:write name="recipientStatForm" property="numBounce" scope="request"/><b></div></td>
          </tr>

          <tr>
              <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="5" border="0"></td>
              <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="5" border="0"></td>
              <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="5" border="0"></td>
          </tr>

          <tr>
             <td align="left"><b><bean:message key="Active"/>:&nbsp;&nbsp;</b></td>
             <td>
                 <table border="0" cellspacing="0" cellpadding="0">
                    <tr height="20">
                       <td bgcolor="<bean:write name="emm.layout" property="highlightColor" scope="session"/>" width="<bean:write name="recipientStatForm" property="blueActive" scope="request"/>">&nbsp;</td>
                    </tr>
                </table>
             </td>
             <td><div align=right><b>&nbsp;<bean:write name="recipientStatForm" property="numActive" scope="request"/><b></div></td>
          </tr>

          <tr>
              <td colspan=3><HR></td>
          </tr>

          <tr>
             <td align="left"><b><bean:message key="Total"/>:&nbsp;&nbsp;</b></td>
             <td>
                 <table border="0" cellspacing="0" cellpadding="0">
                    <tr height="20">
                       <td bgcolor="<bean:write name="emm.layout" property="highlightColor" scope="session"/>" width="200"></td>
                    </tr>
                </table>
             </td>
             <td>
                <div align="right"><b>&nbsp;<bean:write name="recipientStatForm" property="numRecipients" scope="request"/></b></div>
             </td>
          </tr>

          <tr>
              <td colspan=3><HR></td>
          </tr>


<% if(mediaType == 0) { %>


          <tr>
              <td colspan="3">
                <BR>
                <span class="head3"><bean:message key="RecipientMailtype"/>:</span>
              </td>
          </tr>

          </tr>
              <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="5" border="0"></td>
              <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="5" border="0"></td>
              <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="5" border="0"></td>
          </tr>

          <tr>
             <td align="left"><b><bean:message key="Text"/>:&nbsp;&nbsp;</b></td>
             <td>
                 <table border="0" cellspacing="0" cellpadding="0">
                    <tr height="20">
                       <td bgcolor="<bean:write name="emm.layout" property="highlightColor" scope="session"/>" width="<bean:write name="recipientStatForm" property="blueText" scope="request"/>">&nbsp;</td>
                    </tr>
                </table>
             </td>
             <td><div align=right><b><bean:write name="recipientStatForm" property="numText" scope="request"/>&nbsp;<b></div></td>
          </tr>

          </tr>
              <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="5" border="0"></td>
              <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="5" border="0"></td>
              <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="5" border="0"></td>
          </tr>

          <tr>
             <td align="left"><b><bean:message key="HTML"/>:&nbsp;&nbsp;</b></td>
             <td>
                 <table border="0" cellspacing="0" cellpadding="0">
                    <tr height="20">
                       <td bgcolor="<bean:write name="emm.layout" property="highlightColor" scope="session"/>" width="<bean:write name="recipientStatForm" property="blueHTML" scope="request"/>">&nbsp;</td>
                    </tr>
                </table>
             </td>
             <td><div align=right><b><bean:write name="recipientStatForm" property="numHTML" scope="request"/>&nbsp;<b></div></td>
          </tr>


          </tr>
              <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="5" border="0"></td>
              <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="5" border="0"></td>
              <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="5" border="0"></td>
          </tr>

          <tr>
             <td align="left"><b><bean:message key="OfflineHTML"/>:&nbsp;&nbsp;</b></td>
             <td>
                 <table border="0" cellspacing="0" cellpadding="0">
                    <tr height="20">
                       <td bgcolor="<bean:write name="emm.layout" property="highlightColor" scope="session"/>" width="<bean:write name="recipientStatForm" property="blueOffline" scope="request"/>">&nbsp;</td>
                    </tr>
                </table>
             </td>
             <td><div align=right><b><bean:write name="recipientStatForm" property="numOffline" scope="request"/>&nbsp;<b></div></td>
          </tr>

          <tr>
              <td colspan=3><HR></td>
          </tr>

     <% } %>

</table>

<!-- here month detail: -->
<%
DateFormat aFormat2=new SimpleDateFormat("MMMM yyyy", (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY));
DateFormat aFormat=DateFormat.getDateInstance(DateFormat.DEFAULT, (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY));
DateFormat aFormatWeekday=new SimpleDateFormat("EE", (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY));
   // GregorianCalendar aCal=(GregorianCalendar)pageContext.getAttribute("thisMonth");
boolean changeColor=true;
%>
<br>
<span class="head2"><bean:message key="Detail_Analysis"/>: <%= aFormat2.format(aCal.getTime()) %></span><br><br>
        <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td><span class="head3"><bean:message key="Day"/>&nbsp;</span></td>
                <td colspan="2"><span class="head3">&nbsp;&nbsp;&nbsp;<bean:message key="Opt_Ins"/>&nbsp;</span></td>
                <td colspan="2"><span class="head3">&nbsp;&nbsp;&nbsp;<bean:message key="Opt_Outs"/>&nbsp;</span></td>
                <td colspan="2"><span class="head3">&nbsp;&nbsp;&nbsp;<bean:message key="Bounces"/>&nbsp;</span></td>
            </tr>
<%
   String file = (String)(session.getAttribute("csvdata"));
   file += "\n";
   String detStr=SafeString.getLocaleString("Detail_Analysis", (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY));
   file += SafeString.getLocaleString("Detail_Analysis", (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ": ; " + aFormat2.format(aCal.getTime()) + "\n";
   file += "\n";
   file += SafeString.getLocaleString("Day", (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ": ;";
   file += SafeString.getLocaleString("Opt_Ins", (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ";";
   file += SafeString.getLocaleString("Opt_Outs", (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ";";
   file += SafeString.getLocaleString("Bounces", (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\r\n";
%>

            <tr><td colspan="7"><hr size="1"></td></tr>

<%  int totalSubscribes = 0;
    int totalOptouts    = 0;
    int totalBounces    = 0;
%>

            <agn:ShowSubscriberStat mailinglistID="<%= mailinglistID %>" targetID="<%= targetID %>" month="<%= request.getParameter("month") %>" mediaType="<%= request.getParameter("mediaType") %>">
                  <% if(changeColor) { %>
                    <tr bgcolor="<bean:write name="emm.layout" property="normalColor" scope="session"/>">
                  <% } else { %>
                    <tr>
                  <% } %>
                    <td colspan="7"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="1" height="1" border="0"></td>
                    </tr>

                  <% if(changeColor) { %>
                    <tr bgcolor="<bean:write name="emm.layout" property="normalColor" scope="session"/>">
                  <% } else { %>
                    <tr>
                  <% } %>
                    <td><b><nobr><%= aFormatWeekday.format(pageContext.getAttribute("today")) %>,&nbsp;<%= aFormat.format(pageContext.getAttribute("today")) %>&nbsp;&nbsp;</nobr></b></td>
                    <td align="right">&nbsp;<agn:ShowByPermission token="recipient.show"><a href="<html:rewrite page="<%= new String("/recipient.do?action=" + RecipientAction.ACTION_LIST + "&user_status=1&trgt_clear=1&trgt_add.x=1&trgt_bracketopen0=0&trgt_bracketclose0=0&trgt_chainop0=0&trgt_column0=bind."+AgnUtils.changeDateName()+"%23DATE&trgt_operator0=1&trgt_value0=" + aFormat4.format(pageContext.getAttribute("today")) + "&listID=" + mailinglistID) %>"/>"></agn:ShowByPermission><%= pageContext.getAttribute("subscribes") %><agn:ShowByPermission token="recipient.show"></a></agn:ShowByPermission>&nbsp;</td>
                    <td>
                        <table border="0" cellspacing="0" cellpadding="0">
                            <tr>
                                <td background="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif" width="1"></td>
                                <td bgcolor="<bean:write name="emm.layout" property="highlightColor" scope="session"/>" width="<%= (int)((double)((Integer)pageContext.getAttribute("subscribes")).intValue()/(double)((Integer) pageContext.getAttribute("max_subscribes")).intValue()*100.0) %>"></td>
                                <td width="<%= (int)(100.0-((double)((Integer)pageContext.getAttribute("subscribes")).intValue()/(double)((Integer)pageContext.getAttribute("max_subscribes")).intValue()*100.0)) %>">&nbsp;</td>
                            </tr>
                        </table>
                    </td>
                    <td align="right">&nbsp;&nbsp;&nbsp;<agn:ShowByPermission token="recipient.show"><a href="<html:rewrite page="<%= new String("/recipient.do?action=" + RecipientAction.ACTION_LIST + "&user_status=4&trgt_clear=1&trgt_add.x=1&trgt_bracketopen0=0&trgt_bracketclose0=0&trgt_chainop0=0&trgt_column0=bind."+AgnUtils.changeDateName()+"%23DATE&trgt_operator0=1&trgt_value0=" + aFormat4.format(pageContext.getAttribute("today")) + "&listID=" + mailinglistID) %>"/>"></agn:ShowByPermission><%= pageContext.getAttribute("optouts") %><agn:ShowByPermission token="recipient.show"></a></agn:ShowByPermission>&nbsp;</td>
                    <td>
                        <table border="0" cellspacing="0" cellpadding="0">
                            <tr>
                                <td background="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif" width="1"></td>
                                <td bgcolor="<bean:write name="emm.layout" property="highlightColor" scope="session"/>" width="<%= (int)((double)((Integer)pageContext.getAttribute("optouts")).intValue()/(double)((Integer)pageContext.getAttribute("max_optouts")).intValue()*100.0) %>"></td>
                                <td width="<%= (int)(100.0-((double)((Integer)pageContext.getAttribute("optouts")).intValue()/(double)((Integer)pageContext.getAttribute("max_optouts")).intValue()*100.0)) %>">&nbsp;</td>
                            </tr>
                        </table>
                    </td>
                    <td align="right">&nbsp;&nbsp;&nbsp;<agn:ShowByPermission token="recipient.show"><a href="<html:rewrite page="<%= new String("/recipient.do?action=" + RecipientAction.ACTION_LIST + "&user_status=2&trgt_clear=1&trgt_add.x=1&trgt_bracketopen0=0&trgt_bracketclose0=0&trgt_chainop0=0&trgt_column0=bind."+AgnUtils.changeDateName()+"%23DATE&trgt_operator0=1&trgt_value0=" + aFormat4.format(pageContext.getAttribute("today")) + "&listID=" + mailinglistID) %>"/>"></agn:ShowByPermission><%= pageContext.getAttribute("bounces") %><agn:ShowByPermission token="recipient.show"></a></agn:ShowByPermission>&nbsp;</td>
                    <td>
                        <table border="0" cellspacing="0" cellpadding="0">
                            <tr>
                                <td background="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif" width="1"></td>
                                <td bgcolor="<bean:write name="emm.layout" property="highlightColor" scope="session"/>" width="<%= (int)((double)((Integer)pageContext.getAttribute("bounces")).intValue()/(double)((Integer)pageContext.getAttribute("max_bounces")).intValue()*100.0) %>"></td>
                                <td width="<%= (int)(100.0-((double)((Integer)pageContext.getAttribute("bounces")).intValue()/(double)((Integer)pageContext.getAttribute("max_bounces")).intValue()*100.0)) %>">&nbsp;</td>
                            </tr>
                        </table>
                    </td>
                </tr>
                  <% if(changeColor) { %>
                    <tr bgcolor="<bean:write name="emm.layout" property="normalColor" scope="session"/>">
                  <% } else { %>
                    <tr>
                  <% } changeColor=!changeColor; %>
                    <td colspan="7"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="1" height="1" border="0"></td>
                </tr>
    <% file += aFormatWeekday.format(pageContext.getAttribute("today")) + ", " + aFormat.format(pageContext.getAttribute("today")) + ";";
       file += pageContext.getAttribute("subscribes") + ";";
       file += pageContext.getAttribute("optouts") + ";";
       file += pageContext.getAttribute("bounces") + "\r\n";


       totalSubscribes += ((Integer)pageContext.getAttribute("subscribes")).intValue();
       totalOptouts += ((Integer)pageContext.getAttribute("optouts")).intValue();
       totalBounces += ((Integer)pageContext.getAttribute("bounces")).intValue();
%>

   </agn:ShowSubscriberStat>


 <tr>
     <td colspan="7"><hr></td>
 </tr>


<% //total Overwiew:

       file += SafeString.getLocaleString("Total", (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ";;";
       file += totalSubscribes + ";";
       file += totalOptouts + ";";
       file += totalBounces + "\r\n";

       // insert csv file in hashtable:
       my_map.put(timekey, file);
       pageContext.getSession().setAttribute("map", my_map);

%>
<tr>
    <td><b><bean:message key="Total"/>:&nbsp;&nbsp;</b></td>
    <td align="right">&nbsp;<b><%= totalSubscribes %></b>&nbsp;</td>
    <td>&nbsp;</td>
    <td align="right">&nbsp;<b><%= totalOptouts %></b>&nbsp;</td>
    <td>&nbsp;</td>
    <td align="right">&nbsp;<b><%= totalBounces %></b>&nbsp;</td>
    <td>&nbsp;</td>

</tr>


       </table>

  <hr size="1">



  <% aCal.add(Calendar.MONTH, -1); %>
       <table border="0" cellspacing="0" cellpadding="0" width="100%">
            <tr width="100%">
                <td align="left"><html:link page="<%= new String("/recipient_stats.do?action=" + RecipientStatAction.ACTION_DISPLAY + "&month=" + aFormat3.format(aCal.getTime()) + "&mailingListID=" + mailinglistID + "&targetID=" + targetID + "&mediaType=" + mediaType) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>arrow_back.gif" border="0">&nbsp;<%= aFormat2.format(aCal.getTime()) %></html:link></td>
  <% aCal.add(Calendar.MONTH, 2); %>
                <td align="right"><html:link page="<%= new String("/recipient_stats.do?action=" + RecipientStatAction.ACTION_DISPLAY + "&month=" + aFormat3.format(aCal.getTime()) + "&mailingListID=" + mailinglistID + "&targetID=" + targetID + "&mediaType=" + mediaType) %>"><%= aFormat2.format(aCal.getTime()) %>&nbsp;<img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>arrow_next.gif" border="0"></html:link></td>
            </tr>
       </table>


</html:form>


<%@include file="/footer.jsp"%>
