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

<agn:Permission token="mailing.components.show"/>

<% int tmpMailingID=0;
   String tmpShortname=new String("");
   MailingComponentsForm aForm=null;
   if(request.getAttribute("mailingComponentsForm")!=null) {
      aForm=(MailingComponentsForm)request.getAttribute("mailingComponentsForm");
      tmpMailingID=aForm.getMailingID();
      tmpShortname=aForm.getShortname();
   }
%>

<logic:equal name="mailingComponentsForm" property="isTemplate" value="true">
<% // template navigation:
  pageContext.setAttribute("sidemenu_active", new String("Templates")); 
  pageContext.setAttribute("sidemenu_sub_active", new String("none"));
  pageContext.setAttribute("agnNavigationKey", new String("templateView"));
  pageContext.setAttribute("agnHighlightKey", new String("Graphics_Components"));
  pageContext.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
  pageContext.setAttribute("agnTitleKey", new String("Template")); 
  pageContext.setAttribute("agnSubtitleKey", new String("Template"));
  pageContext.setAttribute("agnSubtitleValue", tmpShortname);
%>
</logic:equal>

<logic:equal name="mailingComponentsForm" property="isTemplate" value="false">
<%
// mailing navigation:
    pageContext.setAttribute("sidemenu_active", new String("Mailings")); 
    pageContext.setAttribute("sidemenu_sub_active", new String("none"));
    pageContext.setAttribute("agnNavigationKey", new String("mailingView"));
    pageContext.setAttribute("agnHighlightKey", new String("Graphics_Components"));
    pageContext.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
    pageContext.setAttribute("agnTitleKey", new String("Mailing")); 
    pageContext.setAttribute("agnSubtitleKey", new String("Mailing"));
    pageContext.setAttribute("agnSubtitleValue", tmpShortname); 
%>
</logic:equal>

<%@include file="/header.jsp"%>

<html:errors/>

<html:form action="/mcomponents" enctype="multipart/form-data">
<html:hidden property="mailingID"/>
<html:hidden property="action"/>
	<table border="0" cellspacing="0" cellpadding="0">
    	<agn:ShowByPermission token="mailing.graphics_upload">
        	<tr>
        		<td><bean:message key="New_Component"/>:&nbsp;</td>
            	<td colspan="2"><html:file property="newFile"/></td>
            </tr>
            <tr>
            	<td><bean:message key="ComponentLink"/>:&nbsp;</td>
              	<td colspan="2"><html:text property="link"/></td>
            </tr>
			<tr><td><br></td></tr>
            <tr><td><html:image src="button?msg=Add" property="save" value="save"/></td></tr>
            <tr><td colspan="3"><hr></td></tr>
		</agn:ShowByPermission>
        <% MailingComponent comp=null; %>
        <agn:HibernateQuery id="component" query="<%= new String("from MailingComponent where companyID="+AgnUtils.getCompanyID(request)+" and mailingID="+tmpMailingID+" ORDER BY componentName") %>">
        <% comp=(MailingComponent)pageContext.getAttribute("component"); %> 
        <% if(comp.getType()==MailingComponent.TYPE_IMAGE) { %>
	        <tr>
    	        <td><b><bean:message key="Graphics_Component.external"/>:</b><br><%= comp.getComponentName() %>&nbsp;<br><br>
        	        <html:image src="button?msg=Update" property="<%= "update"+comp.getId() %>" value="update"/>
                    <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="5" height="5" border="0">
            	</td>
                <td background="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="1" height="1" border="0"></td>
                <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="5" height="5" border="0"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="10" border="0"><html:img src="<%= "sc?compID=" + comp.getId() %>" border="1"/></td>
			</tr>
            <tr><td colspan="3"><hr></td></tr>
		<% } 
        if(comp.getType()==MailingComponent.TYPE_HOSTED_IMAGE) { %>
        	<tr>
    	        <td><b><bean:message key="Graphics_Component"/>:</b>&nbsp;&nbsp;<%= comp.getComponentName() %>&nbsp;<br>
    	        	<b><bean:message key="Mime_Type"/>:</b>&nbsp;&nbsp;<%= comp.getMimeType() %>&nbsp;<br>
    	        	<% TrackableLink link = null; %>
    	        	<agn:HibernateQuery id="url" query="<%= new String("from TrackableLink where companyID=" + AgnUtils.getCompanyID(request) + " and mailingID=" + tmpMailingID + " and url_id=" + comp.getUrlID()) %>">
    	        	<% link = (TrackableLink) pageContext.getAttribute("url");
    	        	if(link != null) {
	    	        	String full = link.getFullUrl();
	    	        	System.err.println("link" + full);
    	        	    if(!full.equals("")) { %>
	    	        	<b><bean:message key="htmled.link"/>:</b>&nbsp;&nbsp;<%= full %>&nbsp;<br><br>    	        	
    	        	<% } 
    	        	} %>
    	        	</agn:HibernateQuery>
	                <html:image src="button?msg=Delete" property="<%= "delete"+comp.getId() %>" value="delete"/>
                	<img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="5" height="5" border="0">
				</td>
            	<td background="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="1" height="1" border="0"></td>
            	<td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="5" height="5" border="0"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="10" border="0"><html:img src="<%= "sc?compID=" + comp.getId() %>" border="1"/></td>
            </tr>
            <tr><td colspan="3"><hr></td></tr>
		<% } %>
		</agn:HibernateQuery>
	</table>
</html:form>
<%@include file="/footer.jsp"%>