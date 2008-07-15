<%@ page language="java" import="org.agnitas.util.*, java.util.*, java.text.*" contentType="text/html; charset=utf-8" buffer="32kb" %>
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
pageContext.setAttribute("agnHighlightKey", new String("RecipientStatistics"));
%>

<%@include file="/header.jsp"%> 
<html:form action="/recipient_stats" method="post">
<html:hidden property="action"/>
  <BR>
    <table border="0" cellspacing="0" cellpadding="0">
         <tr>
             <td colspan=2>
                <span class="head3"><bean:message key="AbostatSelectInvitation"/></span>
             </td>
         </tr>

         <tr>
             <td colspan=2>
                &nbsp;
             </td>
         </tr>

        <tr>
            <td>
              <b><bean:message key="Mailinglist"/>:&nbsp;</b>
            </td>
            <td>
              <html:select property="mailingListID" size="1">
                  <html:option value="0"><bean:message key="All_Mailinglists"/></html:option>
                <agn:ShowTable id="agntbl1" sqlStatement="<%= new String("SELECT mailinglist_id, shortname FROM mailinglist_tbl WHERE company_id="+AgnUtils.getCompanyID(request)) %>">
                  <html:option value="<%= (String)pageContext.getAttribute("_agntbl1_mailinglist_id") %>"><%= pageContext.getAttribute("_agntbl1_shortname") %></html:option>
                </agn:ShowTable>
              </html:select>
            </td>
         </tr>

         <tr>
              <td>
                <b><bean:message key="Target"/>:</b>&nbsp;
              </td>
              <td>
                <html:select property="targetID" size="1">
                    <html:option value="0"><bean:message key="All_Subscribers"/></html:option>
                    <agn:ShowTable id="agntbl2" sqlStatement="<%= new String("SELECT target_id, target_shortname FROM dyn_target_tbl WHERE company_id=" + AgnUtils.getCompanyID(request)+ " ORDER BY target_shortname") %>">
                        <html:option value="<%= (String) pageContext.getAttribute("_agntbl2_target_id") %>"><%= pageContext.getAttribute("_agntbl2_target_shortname") %></html:option>
                    </agn:ShowTable>
                </html:select>&nbsp;&nbsp;&nbsp;
              </td>
         </tr>    
            <html:hidden property="mediaType" value="0"/>
         <tr>
             <td colspan=2>
                &nbsp;
             </td>
         </tr>

         <tr>
             <td colspan=2>
                <html:image src="button?msg=Submit" border="0"/>
             </td>
         </tr>
  
   </table>


</html:form>

<%@include file="/footer.jsp"%>
