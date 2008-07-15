<%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.Admin, java.util.*" contentType="text/html; charset=utf-8" errorPage="/error.jsp"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="admin.show"/>

<% int tmpAdminID=0;
   String tmpUsername=new String("");
   int tmpCompanyID=0;
   if(request.getAttribute("adminForm")!=null) {
      tmpAdminID=((AdminForm)request.getAttribute("adminForm")).getAdminID();
      tmpCompanyID=((AdminForm)request.getAttribute("adminForm")).getCompanyID();
      tmpUsername=((AdminForm)request.getAttribute("adminForm")).getUsername();
   }
%>


<% pageContext.setAttribute("sidemenu_active", new String("Settings")); %>          <!-- links Button -->
<% pageContext.setAttribute("sidemenu_sub_active", new String("Admins")); %>        <!-- links unter Button -->
<% pageContext.setAttribute("agnTitleKey", new String("Admins")); %>                <!-- Titelleiste -->


<% if(tmpAdminID!=0) {
     pageContext.setAttribute("agnSubtitleKey", new String("Admin"));
     pageContext.setAttribute("agnNavigationKey", new String("admin"));
     pageContext.setAttribute("agnHighlightKey", new String("Admin"));
   } else {
     pageContext.setAttribute("agnSubtitleKey", new String("Admins"));
     pageContext.setAttribute("agnNavigationKey", new String("admins"));
     pageContext.setAttribute("agnHighlightKey", new String("New_Admin"));
   } 
%>


<% pageContext.setAttribute("agnSubtitleValue", tmpUsername); %>
<% pageContext.setAttribute("agnNavHrefAppend", new String("&adminID="+tmpAdminID)); %>

<%@include file="/header.jsp"%>

<html:errors/>


<html:form action="admin" focus="username">
                
            <html:hidden property="action"/>
            <html:hidden property="adminID"/>

            <table border="0" cellspacing="0" cellpadding="0">


                <tr> 
                  <td><bean:message key="Name"/>:&nbsp;</td>
                  <td> 
                    <html:text property="fullname" size="52" maxlength="99"/>
                  </td>
                </tr>

                <tr> 
                  <td><bean:message key="User_Name"/>:&nbsp;</td>
                  <td> 
                    <html:text property="username" size="52" maxlength="99"/>
                  </td>
                </tr>


                <tr> 
                  <td><bean:message key="password"/>:&nbsp;</td>
                  <td> 
                    <html:password property="password" size="52" maxlength="99"/>
                  </td>
                </tr>

                <tr> 
                  <td><bean:message key="Confirm"/>:&nbsp;</td>
                  <td> 
                    <html:password property="passwordConfirm" size="52" maxlength="99"/>
                  </td>
                </tr>

                <agn:ShowByPermission token="admin.setgroup">
                <tr> 
                  <td><bean:message key="Usergroup"/>:&nbsp;</td>
                  <td>
                    <html:select property="groupID" size="1">
                        <html:option value="0"><bean:message key="Usergroup.none"/></html:option>
                        <agn:ShowTable id="agntbl5" sqlStatement="<%= new String("SELECT admin_group_id, shortname FROM admin_group_tbl WHERE company_id="+AgnUtils.getCompanyID(request)) %>" maxRows="500">
                           <html:option value="<%= (String)(pageContext.getAttribute("_agntbl5_admin_group_id")) %>"><%= pageContext.getAttribute("_agntbl5_shortname") %></html:option>
                        </agn:ShowTable>
                    </html:select>
                  </td>
                </tr>
                </agn:ShowByPermission>
                <% if(!AgnUtils.allowed("admin.setgroup", request)) { %>
                   <html:hidden property="groupID"/>
                <% } %>
                    
                <tr> 
                  <td><bean:message key="Language"/>:&nbsp;</td>
                  <td> 
                    <html:select property="language" size="1">
                        <html:option value="<%= Locale.GERMANY.toString() %>"><bean:message key="German"/></html:option>
                        <html:option value="<%= Locale.US.toString() %>"><bean:message key="English"/></html:option>
                    </html:select>
                  </td>
                </tr>

                <tr>
                  <td><bean:message key="Timezone"/>:&nbsp;</td>
                  <td> 
                    <html:select property="adminTimezone" size="1">
                        <% String allZones[]=TimeZone.getAvailableIDs();
                           int len=allZones.length;
                           TimeZone tmpZone=TimeZone.getDefault();
                           Locale aLoc=(Locale)session.getAttribute("messages_lang");
                           for(int i=0; i<len; i++) {
                              tmpZone.setID(allZones[i]);
                        %>
                        <html:option value="<%= allZones[i] %>"><%= /* tmpZone.getDisplayName(aLoc) */ allZones[i] %></html:option>
                        <% } %>
                    </html:select>
                  </td>
                </tr>

                <html:hidden property="companyID" value="1"/>

                </table>

          <p>

                <% if(tmpAdminID!=0) { %>
                    <agn:ShowByPermission token="admin.change">
                    <html:image src="button?msg=Save" border="0" property="save" value="save"/>
                    </agn:ShowByPermission>
                <% } else {%>
                    <agn:ShowByPermission token="admin.new">
                    <html:image src="button?msg=Create" border="0" property="save" value="save"/>
                    </agn:ShowByPermission>
                <% } %>            

                <agn:ShowByPermission token="admin.delete">
                <% if(tmpAdminID!=0) { %>
                <logic:notEqual name="adminID" scope="session" value="<%= Integer.toString(tmpAdminID) %>">
	        <html:image src="button?msg=Delete" border="0" property="delete" value="delete"/>
                </logic:notEqual>
                <% } %>
                </agn:ShowByPermission>


          </p>

</html:form>          

<%@include file="/footer.jsp"%>
