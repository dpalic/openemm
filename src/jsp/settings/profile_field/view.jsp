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
 --%><%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<agn:CheckLogon/>

<agn:Permission token="profileField.show"/>

<% String tmpFieldname = new String("");
   if(request.getAttribute("profileFieldForm")!=null && request.getAttribute("hasErrors") == null ) {
      tmpFieldname=((ProfileFieldForm)request.getAttribute("profileFieldForm")).getFieldname();
   }
%>

<% pageContext.setAttribute("sidemenu_active", new String("Settings")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("Profile_DB")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Profile_Database")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Profile_Database")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("profiledb")); %>
    <% if(tmpFieldname!=null && tmpFieldname.compareTo("")!=0) { %>
<% pageContext.setAttribute("agnHighlightKey", new String("Profile_DB")); %>
    <% } else { %>
<% pageContext.setAttribute("agnHighlightKey", new String("NewProfileDB_Field")); %>
    <% } %>

<%@include file="/header.jsp"%>

<html:errors/>

<br>
            <table border="0" cellspacing="0" cellpadding="0" width="100%">
               <html:form action="/profiledb">
                 <html:hidden property="companyID"/>
                 <html:hidden property="action"/>
                 <html:hidden property="oldStyle"/>
                 <tr>
                    <td><b><bean:message key="FieldName"/>:&nbsp</b></td>
                    <td><html:text property="shortname" size="32"/></td>
                 </tr>

                 <tr>
                    <td><b><bean:message key="Description"/>:&nbsp</b></td>
                    <td><html:textarea property="description" cols="32" rows="5"/></td>
                 </tr>

                 <% if(tmpFieldname!=null && tmpFieldname.compareTo("")!=0) { %>
                     <tr>
                        <td><b><bean:message key="FieldNameDB"/>:</b></td>
                        <td><%=tmpFieldname%></td>
                     </tr>
                     <html:hidden property="fieldname"/>

                     <tr>
                        <td><b><bean:message key="Type"/>:&nbsp</b></td>
                        <td><bean:message key="<%= "fieldType."+((ProfileFieldForm)request.getAttribute("profileFieldForm")).getFieldType() %>"/></td>
                     </tr>

                     <% if(((ProfileFieldForm)request.getAttribute("profileFieldForm")).getFieldType().equals("java.lang.String")) { %>
                     <tr>
                        <td><b><bean:message key="Length"/>:&nbsp</b></td>
                        <td><%= ((ProfileFieldForm)request.getAttribute("profileFieldForm")).getFieldLength() %></td>
                     </tr>
                     <% } %>
                     
                     <tr>
                        <td><b><bean:message key="Default_Value"/>:&nbsp</b></td>
                        <td><html:text property="fieldDefault" size="32"/></td>
                     </tr>

                     <tr>
                        <td><b><bean:message key="NullAllowed"/>:&nbsp</b></td>
                        <% if( ((ProfileFieldForm)request.getAttribute("profileFieldForm")).isFieldNull() ) { %>
                            <td><bean:message key="Yes"/></td>
                        <% } else { %>
                            <td><bean:message key="No"/></td>
                        <% } %>
                     </tr>


                 <% } else {%>
                     <tr>
                        <td><b><bean:message key="FieldNameDB"/>:&nbsp</b></td>
                        <td><html:text property="fieldname" size="32"/></td>
                     </tr>

                     <tr>
                        <td><b><bean:message key="Type"/>:&nbsp</b></td>
                        <td>    
                            <html:select property="fieldType" size="1">
                                <html:option value="DOUBLE"><bean:message key="fieldType.DOUBLE"/></html:option>
                                <html:option value="VARCHAR"><bean:message key="fieldType.VARCHAR"/></html:option>
                                <html:option value="DATE"><bean:message key="fieldType.DATE"/></html:option>
                            </html:select>
                        </td>
                     </tr>
                     
                     <tr>
                        <td><b><bean:message key="Length"/>:&nbsp</b></td>
                        <td><html:text property="fieldLength" size="32"/></td>
                     </tr>

                     <tr>
                        <td><b><bean:message key="Default_Value"/>:&nbsp</b></td>
                        <td><html:text property="fieldDefault" size="32"/></td>
                     </tr>

                     <agn:ShowByPermission token="showNULL_checkbox">
                         <tr>
                            <td><b><bean:message key="NullAllowed"/>:&nbsp</b></td>
                            <td><html:checkbox property="fieldNull" value="true"/></td>
                         </tr>
                     </agn:ShowByPermission>
                 <% } %>
       

                <tr>
                  <td colspan="2">
                    <hr>
                  </td>
                </tr>

                <tr>
                  <td colspan="2">
                    <html:image src="button?msg=Save" border="0" property="save" value="save"/>  
                  <% if(request.getParameter("fieldname")!=null && request.getParameter("fieldname").compareTo("")!=0) { %>
                    <html:link page="<%= new String("/profiledb.do?action=" + ProfileFieldAction.ACTION_CONFIRM_DELETE + "&fieldname=" + tmpFieldname) %>"><html:img src="button?msg=Delete" border="0"/></html:link>
                  <% } %>
                    <html:link page="<%= new String("/profiledb.do?action=" + ProfileFieldAction.ACTION_LIST) %>"><html:img src="button?msg=Cancel" border="0"/></html:link>
                  </td>
                </tr>

              </html:form>  
            </table>

<%@include file="/footer.jsp"%>
