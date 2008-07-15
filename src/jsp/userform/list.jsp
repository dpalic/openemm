<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="forms.view"/>

<% pageContext.setAttribute("sidemenu_active", new String("Forms")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("Overview")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("FormsOverview")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Overview")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Forms")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Forms")); %>

<%@include file="/header.jsp"%>

<html:errors/>

              <table border="0" cellspacing="0" cellpadding="0" width="100%">
                <tr>
                    <td><span class="head3"><bean:message key="Form"/>&nbsp;&nbsp;</span></td>
                    <td><span class="head3"><bean:message key="Description"/>&nbsp;&nbsp;</span></td>
                    <td><span class="head3">&nbsp;</span></td>
                </tr>
                <tr><td colspan="3"><hr></td></tr>
                <agn:ShowTable id="agnTbl" sqlStatement="<%= new String("SELECT form_id, formname, description FROM userform_tbl WHERE company_id="+AgnUtils.getCompanyID(request)+ " ORDER BY formname")%>" startOffset="<%= request.getParameter("startWith") %>" maxRows="250">
                    <tr>
                        <td><html:link page="<%= new String("/userform.do?action=" + UserFormEditAction.ACTION_VIEW + "&formID=" + pageContext.getAttribute("_agnTbl_form_id")) %>"><b><%= pageContext.getAttribute("_agnTbl_formname") %></b></html:link>&nbsp;&nbsp;</td>
                        <td>
                            <%= pageContext.getAttribute("_agnTbl_description") %>
                            &nbsp;&nbsp;
                        </td>
                        <td>
                            <agn:ShowByPermission token="forms.delete">
                            <html:link page="<%= new String("/userform.do?action=" + UserFormEditAction.ACTION_CONFIRM_DELETE + "&formID=" + pageContext.getAttribute("_agnTbl_form_id")) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>delete.gif" alt="<bean:message key="Delete"/>" border="0"></html:link>
                            </agn:ShowByPermission>
                            <html:link page="<%= new String("/userform.do?action=" + UserFormEditAction.ACTION_VIEW + "&formID=" + pageContext.getAttribute("_agnTbl_form_id")) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>bearbeiten.gif" alt="<bean:message key="Edit"/>" border="0"></html:link>
                        </td>
                    </tr>
                </agn:ShowTable>
                <tr><td colspan="3"><hr></td></tr>
                <tr><td colspan="3"><center>
                     <agn:ShowTableOffset id="agnTbl" maxPages="10">
                        <html:link page="<%= new String("/userform.do?action=" + UserFormEditAction.ACTION_LIST + "&startWith=" + pageContext.getAttribute("startWith")) %>">
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

<%@include file="/footer.jsp"%>
