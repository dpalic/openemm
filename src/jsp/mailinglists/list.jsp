<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="mailinglist.show"/>

<% pageContext.setAttribute("sidemenu_active", new String("Mailinglists")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("Overview")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("MailinglistsOverview")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Overview")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Mailinglists")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Mailinglists")); %>

<%@include file="/header.jsp"%>
<html:errors/>

              <table border="0" cellspacing="0" cellpadding="0" width="100%">
                <tr>                        
                    <td><span class="head3"><bean:message key="Mailinglist"/>&nbsp;&nbsp;</span></td>
                    <td><span class="head3"><bean:message key="Description"/>&nbsp;&nbsp;</span></td>
                    <td><span class="head3">&nbsp;</span></td>
                </tr>
                <tr><td colspan="3"><hr></td></tr>
<!--
                <% Mailinglist list=null; %>
                <logic:iterate id="mlist" name="mailinglists" scope="request">
                    <% list=(Mailinglist)pageContext.getAttribute("mlist"); %>
                    <tr>
                        <td><html:link page="<%= "/mailinglist.do?action=" + MailinglistAction.ACTION_VIEW + "&mailinglistID=" + list.getId() %>"><b><%= list.getShortname() %></b></html:link>&nbsp;&nbsp;</td>
                        <td><%= SafeString.cutLength(list.getDescription(), 40) %>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                        
                        <td>
                            <agn:ShowByPermission token="mailinglist.delete">
                            <html:link page="<%= "/mailinglist.do?action=" + MailinglistAction.ACTION_CONFIRM_DELETE + "&mailinglistID=" + list.getId() %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>delete.gif" alt="<bean:message key="Delete"/>" border="0"></html:link>
                            </agn:ShowByPermission>
                            <html:link page="<%= "/mailinglist.do?action=" + MailinglistAction.ACTION_VIEW + "&mailinglistID=" + list.getId() %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>bearbeiten.gif" alt="<bean:message key="Edit"/>" border="0"></html:link>
                        </td>
                    </tr>
                </logic:iterate>
-->
              <agn:ShowTable id="agntbl1" sqlStatement="<%= "SELECT mailinglist_id, shortname, description FROM mailinglist_tbl WHERE company_id="+AgnUtils.getCompanyID(request)+" ORDER BY mailinglist_id DESC"%>" startOffset="<%= request.getParameter("startWith") %>" maxRows="50">
                  <tr>
                      <td><html:link page="<%= "/mailinglist.do?action=" + MailinglistAction.ACTION_VIEW + "&mailinglistID=" + (String)pageContext.getAttribute ("_agntbl1_mailinglist_id") %>"><b><%= (String)pageContext.getAttribute ("_agntbl1_shortname") %></b></html:link>&nbsp;&nbsp;</td>
                      <td><%= SafeString.cutLength((String)pageContext.getAttribute ("_agntbl1_description"), 40) %>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                      <td>
                          <agn:ShowByPermission token="mailinglist.delete">
                              <html:link page="<%= "/mailinglist.do?action=" + MailinglistAction.ACTION_CONFIRM_DELETE + "&mailinglistID=" + (String)pageContext.getAttribute ("_agntbl1_mailinglist_id") %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>delete.gif" alt="<bean:message key="Delete"/>" border="0"></html:link>
                          </agn:ShowByPermission>
                          <html:link page="<%= "/mailinglist.do?action=" + MailinglistAction.ACTION_VIEW + "&mailinglistID=" + (String)pageContext.getAttribute ("_agntbl1_mailinglist_id") %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>bearbeiten.gif" alt="<bean:message key="Edit"/>" border="0"></html:link>
                      </td>
                  </tr>
              </agn:ShowTable>
              <tr><td colspan="5"><hr size="1"></td></tr>
              <!-- Multi-Page Indizes -->
                <tr><td colspan="5"><center>
                     <agn:ShowTableOffset id="agntbl1" maxPages="19">
                        <html:link page="<%= new String("/mailinglist.do?action=" + MailinglistAction.ACTION_LIST + "&listID=" + (String)pageContext.getAttribute ("_agntbl1_mailinglist_id") + "&startWith=" + startWith) %>">
                        <% if(activePage!=null) { %>
                            <span class="activenumber">&nbsp;
                        <% } %>
                        <%= pageNum %>
                        <% if(activePage!=null) { %>
                            &nbsp;</span>
                        <% } %>
                        </html:link>&nbsp;
                     </agn:ShowTableOffset></center></td></tr>

              </table>

<%@include file="/footer.jsp"%>
