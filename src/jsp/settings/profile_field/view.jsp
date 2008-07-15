<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<agn:CheckLogon/>

<agn:Permission token="profileField.show"/>

<% String tmpFieldname = new String("");
   if(request.getAttribute("profileFieldForm")!=null) {
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
                        <td><%= ((ProfileFieldForm)request.getAttribute("profileFieldForm")).getFieldDefault() %></td>
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
