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
 --%><%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*, java.util.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="forms.view"/>

<% int tmpFormID=0;
   String tmpFormName=new String("");
   if(request.getAttribute("userFormEditForm")!=null) {
      tmpFormID=((UserFormEditForm)request.getAttribute("userFormEditForm")).getFormID();
      tmpFormName=((UserFormEditForm)request.getAttribute("userFormEditForm")).getFormName();
   }
%>

<% pageContext.setAttribute("sidemenu_active", new String("Forms")); %>
<% if(tmpFormID!=0) {
     pageContext.setAttribute("sidemenu_sub_active", new String("Overview"));
     pageContext.setAttribute("agnNavigationKey", new String("formView"));
     pageContext.setAttribute("agnHighlightKey", new String("Form"));
   } else {
     pageContext.setAttribute("sidemenu_sub_active", new String("New_Form"));
     pageContext.setAttribute("agnNavigationKey", new String("formView"));
     pageContext.setAttribute("agnHighlightKey", new String("New_Form"));
   }
%>
<% pageContext.setAttribute("agnTitleKey", new String("Form")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Form")); %>
<% pageContext.setAttribute("agnSubtitleValue", tmpFormName); %>
<%@include file="/header.jsp"%>

<html:errors/>

<html:form action="/userform">
                <html:hidden property="formID"/>
                <html:hidden property="action"/>
              <table border="0" cellspacing="0" cellpadding="0">
                <tr> 
                  <td><bean:message key="Name"/>:&nbsp;</td>
                  <td> 
                    <html:text property="formName" size="52" maxlength="99"/>
                  </td>
                </tr>
                <tr> 
                  <td><bean:message key="Description"/>:&nbsp;</td>
                  <td> 
                    <html:textarea property="description" rows="5" cols="32"/><br><br>
                  </td>
                </tr>

                <tr>
                   <td><bean:message key="Action"/>:&nbsp;</td>
                      <td><html:select property="startActionID" size="1">
                           <html:option value="0"><bean:message key="No_Action"/></html:option>
                           <agn:ShowTable id="agntbl2" sqlStatement="<%= new String("SELECT action_id, shortname FROM rdir_action_tbl WHERE company_id="+AgnUtils.getCompanyID(request)+" AND action_type<>0 ORDER BY shortname") %>" maxRows="500">
                                    <html:option value="<%= (String)pageContext.getAttribute("_agntbl2_action_id") %>"><%= pageContext.getAttribute("_agntbl2_shortname") %></html:option>
                           </agn:ShowTable>
                       </html:select><br>
                       </td>
                </tr>
                <tr> 
                  <td colspan="2"><b><bean:message key="form.success_template"/>:</b><br>
                    <html:textarea property="successTemplate" rows="14" cols="75"/>
                  </td>
                </tr>
                <tr>
                  <td colspan="2"><br><b><bean:message key="form.error_template"/>:</b><br>
                    <html:textarea property="errorTemplate" rows="14" cols="75"/>
                  </td>
                </tr>
                <tr>
                   <td><bean:message key="Action"/>:&nbsp;</td>
                   <td><html:select property="endActionID" size="1">
                        <html:option value="0"><bean:message key="No_Action"/></html:option>
                        <agn:ShowTable id="agntbl3" sqlStatement="<%= new String("SELECT action_id, shortname FROM rdir_action_tbl WHERE company_id="+AgnUtils.getCompanyID(request)+" AND action_type<>0 ORDER BY shortname") %>" maxRows="500">
                             <html:option value="<%= (String)pageContext.getAttribute("_agntbl3_action_id") %>"><%= pageContext.getAttribute("_agntbl3_shortname") %></html:option>
                        </agn:ShowTable>
                     </html:select>
                    </td>
                </tr>

                </table>
			  <p>
                <agn:ShowByPermission token="forms.change">
                <html:image src="button?msg=Save" border="0" property="save" value="save"/>
                </agn:ShowByPermission>
                <agn:ShowByPermission token="forms.delete">
                <% if(tmpFormID!=0) { %>
	        <html:link page="<%= new String("/userform.do?action=" + UserFormEditAction.ACTION_CONFIRM_DELETE + "&formID=" + tmpFormID) %>"><html:img src="button?msg=Delete" border="0"/></html:link>
                <% } %>
                </agn:ShowByPermission>
              </p>
              </html:form>
<%@include file="/footer.jsp"%>
