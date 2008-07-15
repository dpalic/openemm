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
 --%><%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.Admin, java.util.*" contentType="text/html; charset=utf-8" errorPage="/error.jsp"%>
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
<script src="settings/admin/PasswordRanking.js" type="text/javascript"></script>
<script type="text/javascript">rank=new PasswordRanking();
</script>
<%@include file="/header.jsp"%>

<html:errors/>


<html:form action="admin" focus="username" onsubmit="return !rank.checkMatch('password', 'repeat');">
                
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
                     <html:password property="password" styleId="password" onkeyup="rank.securityCheck('bar', this.id);  rank.enableButton('save', 'Save', rank.checkMatch('password', 'repeat'));" size="52" maxlength="99"/>
                  </td>
                </tr>
                
                <tr>
                  <td></td>
                  <td>
                    <script type="text/javascript">rank.showBar("bar", "<bean:message key="secure"/>", "<bean:message key="insecure"/>");</script>
                  </td>
                </tr>

                <tr> 
                  <td><bean:message key="Confirm"/>:&nbsp;</td>
                  <td> 
                    <html:password property="passwordConfirm" styleId="repeat" onkeyup="rank.enableButton('save', 'Save', rank.checkMatch('password', 'repeat'));" size="52" maxlength="99"/>
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
                        <html:option value="<%= Locale.FRANCE.toString() %>"><bean:message key="French"/></html:option>
                        <html:option value="ES_es"><bean:message key="Spanish"/></html:option>
                        <html:option value="PT_pt"><bean:message key="Portugues"/></html:option>
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
                    <html:image src="button?msg=Save" border="0" styleId="save" property="save" value="save"/>
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
<script type="text/javascript">

rank.enableButton('save', 'Save', true);
</script>
<%@include file="/footer.jsp"%>
