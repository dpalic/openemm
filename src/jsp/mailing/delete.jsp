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
 --%><%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*,org.agnitas.web.forms.*, org.agnitas.beans.*" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="mailing.delete"/>

<% int tmpMailingID=0;
   String tmpShortname=new String("");
   int isTemplate=0;
   MailingBaseForm aForm=null;
   if(session.getAttribute("mailingBaseForm")!=null) {
      aForm=(MailingBaseForm)session.getAttribute("mailingBaseForm");
      tmpMailingID=aForm.getMailingID();
      tmpShortname=aForm.getShortname();
      if(aForm.isIsTemplate()) {
         isTemplate=1;
      }
   }
%>

<% if(isTemplate==0) { %>
<agn:Permission token="mailing.delete"/>
<% } else { %>
<agn:Permission token="template.delete"/>
<% } %>

<logic:equal name="mailingBaseForm" property="isTemplate" value="true">
<% // template navigation:
  pageContext.setAttribute("sidemenu_active", new String("Templates")); 
  pageContext.setAttribute("sidemenu_sub_active", new String("none"));
  pageContext.setAttribute("agnNavigationKey", new String("templateView"));
  pageContext.setAttribute("agnHighlightKey", new String("Template"));
  pageContext.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
  pageContext.setAttribute("agnTitleKey", new String("Template")); 
  pageContext.setAttribute("agnSubtitleKey", new String("Template"));
  pageContext.setAttribute("agnSubtitleValue", tmpShortname);
%>
</logic:equal>

<logic:equal name="mailingBaseForm" property="isTemplate" value="false">
<%
// mailing navigation:
    pageContext.setAttribute("sidemenu_active", new String("Mailings")); 
    pageContext.setAttribute("sidemenu_sub_active", new String("none"));
    pageContext.setAttribute("agnNavigationKey", new String("mailingView"));
    pageContext.setAttribute("agnHighlightKey", new String("Mailing"));
    pageContext.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
    pageContext.setAttribute("agnSubtitleValue", tmpShortname);
    pageContext.setAttribute("agnTitleKey", new String("Mailing")); 
    pageContext.setAttribute("agnSubtitleKey", new String("Mailing")); 
%>
</logic:equal>

<%@include file="/header.jsp"%>
<%@include file="/messages.jsp" %>

           <html:form action="/mailingbase">
            <html:hidden property="mailingID"/>
            <html:hidden property="action"/>
            <html:hidden property="isTemplate"/>

	<span class="head1"><%= tmpShortname %></span>            
	<br>
	<br>
            
               <span class="head3">
               <% if(isTemplate==0) { %>
                <bean:message key="MailingDeleteQuestion"/>
                <% } else { %>
               <bean:message key="Delete_Template_Question"/>
               <% } %>
               </span><br>
			  <p>
	        <html:image src="button?msg=Delete" border="0" property="delete" value="delete"/>&nbsp;
                <html:link page="<%= new String("/mailingbase.do?action=" + aForm.getPreviousAction() + "&mailingID=" + tmpMailingID) %>"><html:img src="button?msg=Cancel" border="0"/></html:link>
              </p>
              </html:form>
<%@include file="/footer.jsp"%>
