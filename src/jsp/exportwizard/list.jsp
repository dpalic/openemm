<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, java.util.*" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<agn:CheckLogon/>

<agn:Permission token="wizard.export"/>

<% pageContext.setAttribute("sidemenu_active", new String("Recipient")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("Export")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Export")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Export")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("subscriber_export")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("ExportWizard")); %>

<%@include file="/header.jsp"%>
<html:errors/>
              <html:form action="/exportwizard">
                  <html:hidden property="action"/>
                  <b><font color=#73A2D0><bean:message key="ExportWizStep_1_of_3"/></font></b>
                  <br><br>
                  <b><bean:message key="SelectExportDef"/>:</b><br>&nbsp;<br>
                  <table border="0" cellspacing="0" cellpadding="0">
                      <tr>
                          <td><span class="head3"><bean:message key="Name"/>&nbsp;&nbsp;</span></td>
                          <td><span class="head3"><bean:message key="Description"/>&nbsp;&nbsp;</span></td>

                          <td><span class="head3">&nbsp;</span></td>
                      </tr>
                      <tr><td colspan="4"><hr></td></tr>
                      <agn:ShowTable id="agnTbl" sqlStatement="<%= new String("SELECT id, shortname, description FROM export_predef_tbl WHERE company_id="+AgnUtils.getCompanyID(request) +" AND deleted=0")%>" startOffset="<%= request.getParameter("startWith") %>" maxRows="50">
                          <tr>
                              <td><html:link page="<%= new String("/exportwizard.do?action=" + ExportWizardAction.ACTION_QUERY + "&exportPredefID=" + pageContext.getAttribute("_agnTbl_id")) %>"><b><%= pageContext.getAttribute("_agnTbl_shortname") %></b></html:link>&nbsp;&nbsp;</td>
                              <td><html:link page="<%= new String("/exportwizard.do?action=" + ExportWizardAction.ACTION_QUERY + "&exportPredefID=" + pageContext.getAttribute("_agnTbl_id")) %>"><%= SafeString.cutLength((String)pageContext.getAttribute("_agnTbl_description"), 40) %></html:link>&nbsp;&nbsp;</td>
                              <td>
                                  <html:link page="<%= new String("/exportwizard.do?action=" + ExportWizardAction.ACTION_CONFIRM_DELETE + "&exportPredefID=" + pageContext.getAttribute("_agnTbl_id")) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>delete.gif" alt="<bean:message key="Delete"/>" border="0"></html:link>
                                  <html:link page="<%= new String("/exportwizard.do?action=" + ExportWizardAction.ACTION_QUERY + "&exportPredefID=" + pageContext.getAttribute("_agnTbl_id")) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>bearbeiten.gif" alt="<bean:message key="Edit"/>" border="0"></html:link>
                              </td>
                          </tr>
                      </agn:ShowTable>
                      <tr><td colspan="4"><hr></td></tr>
                      <tr><td colspan="3"><center>
                      <agn:ShowTableOffset id="agnTbl" maxPages="10">
                          <html:link page="<%= new String("/exportwizard.do?action=" + StrutsActionBase.ACTION_LIST + "&startWith=" + pageContext.getAttribute("startWith")) %>">
                              <% if(pageContext.getAttribute("activePage")!=null) { %>
                              <span class="activenumber">&nbsp;
                              <% } %>
                              <%= pageContext.getAttribute("pageNum") %>
                              <% if(pageContext.getAttribute("activePage")!=null) { %>
                              &nbsp;</span>
                              <% } %>
                          </html:link>&nbsp;
                      </agn:ShowTableOffset></center></td></tr>
                  </table>

                  <html:link page="<%= new String("/exportwizard.do?action=" + ExportWizardAction.ACTION_QUERY + "&exportPredefID=0") %>"><html:img src="button?msg=New" border="0"/></html:link>
      
      
      
              </html:form>

<%@include file="/footer.jsp"%>
