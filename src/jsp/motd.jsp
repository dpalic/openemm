<%@ page language="java" contentType="text/html; charset=utf-8" errorPage="error.jsp" import="com.agnitas.util.*"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>
<% pageContext.setAttribute("sidemenu_active", new String("none")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("none")); %>
<% pageContext.setAttribute("agnTitleKey", new String("A_EMM")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Welcome")); %>
<% pageContext.setAttribute("agnSubtitleValue", session.getAttribute("fullName")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("none")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("none")); %>

<%@include file="/header.jsp"%>
<% boolean direct = true; %>
<% int i=1; %>
<html:errors/>

    <% // admin message %>
    <agn:ShowTable id="admTbl" sqlStatement="<%= new String("SELECT MESSAGE FROM MOTD_TBL WHERE COMPANY_ID="+session.getAttribute("companyID")+" AND ADMIN_ID="+session.getAttribute("adminID"))%>" maxRows="1" encodeHtml="0">
      <%  if(pageContext.getAttribute("_admTbl_message")!=null) { %>
        <p><%= pageContext.getAttribute("_admTbl_message") %></p><hr>
        <% direct = false; %>
      <% } %>        
    </agn:ShowTable>

    <% // company message %>
    <agn:ShowTable id="compTbl" sqlStatement="<%= new String("SELECT MESSAGE FROM MOTD_TBL WHERE COMPANY_ID="+session.getAttribute("companyID")+" AND ADMIN_ID=0")%>" maxRows="1" encodeHtml="0">
      <%  if(pageContext.getAttribute("_compTbl_message")!=null) { %>
        <p><%= pageContext.getAttribute("_compTbl_message") %></p><hr>
        <% direct = false; %>
      <% } %>        
    </agn:ShowTable>

    <% // all customers message %>
    <agn:ShowTable id="allTbl" sqlStatement="<%= new String("SELECT MESSAGE FROM MOTD_TBL WHERE COMPANY_ID=0 AND ADMIN_ID=0")%>" maxRows="1" encodeHtml="0">
        <p><%= (String)pageContext.getAttribute("_allTbl_message") %></p>
        <% direct = false; %>
    </agn:ShowTable>   


    <%if(direct) { %>
        <% // no message => normal menue %>
        <table border="0" cellspacing="0" cellpadding="0">
          <tr>  
           <td>
             <table border="0" cellspacing="10" cellpadding="0">
                <tr>
                <agn:ShowNavigation navigation="sidemenu" highlightKey="">
                   <agn:ShowByPermission token="<%= _navigation_token %>">
                      <td><html:link page="<%= _navigation_href %>"><img border="0" width="60" height="60" align="left" hspace="10" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>splash_<%= _navigation_navMsg.toLowerCase() %>.gif" alt="<agn:GetLocalMsg key="<%= _navigation_navMsg %>"/>"><span class="head1"><agn:GetLocalMsg key="<%= _navigation_navMsg %>"/></span><br><agn:GetLocalMsg key="<%= new String("splash."+_navigation_navMsg) %>"/></html:link></td>
                      <% if(i==2) { %> </tr><tr> <% i=0; } i++; %>
                   </agn:ShowByPermission>
                </agn:ShowNavigation>
                </tr>
             </table>
           </td>
          </tr>
        </table>
    
    <% } else  { %>
        <% // "Proceed"-Button %>
        <p><html:link page="/start.jsp"><html:image align="bottom" src="button?msg=Proceed" border="0"/></html:link></p>
    <% } %>
    

<%@include file="/footer.jsp"%>
