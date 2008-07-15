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
 --%><%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, java.util.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="mailinglist.show"/>

<% int tmpMailinglistID=0;
   String tmpShortname=new String("");
   if(request.getAttribute("mailinglistForm")!=null) {
      tmpMailinglistID=((MailinglistForm)request.getAttribute("mailinglistForm")).getMailinglistID();
      System.out.println("tmpMailinglistID: " + tmpMailinglistID);
      tmpShortname=((MailinglistForm)request.getAttribute("mailinglistForm")).getShortname();
   }
%>

<% pageContext.setAttribute("sidemenu_active", new String("Mailinglists")); %>
<% if(tmpMailinglistID!=0) {
     pageContext.setAttribute("sidemenu_sub_active", new String("none"));
     pageContext.setAttribute("agnNavigationKey", new String("show_mailinglist"));
     pageContext.setAttribute("agnHighlightKey", new String("Mailinglist"));
     pageContext.setAttribute("agnTitleKey", new String("Mailinglist"));
     pageContext.setAttribute("agnSubtitleKey", new String("Mailinglist"));
     pageContext.setAttribute("agnSubtitleValue", tmpShortname);
   } else {
     pageContext.setAttribute("sidemenu_sub_active", new String("NewMailinglist"));
     pageContext.setAttribute("agnNavigationKey", new String("MailinglistNew"));
     pageContext.setAttribute("agnHighlightKey", new String("NewMailinglist"));
     pageContext.setAttribute("agnTitleKey", new String("NewMailinglist"));
     pageContext.setAttribute("agnSubtitleKey", new String("NewMailinglist"));
   }
%>

<% pageContext.setAttribute("agnNavHrefAppend", new String("&mailinglistID="+tmpMailinglistID)); %>
<%@include file="/header.jsp"%>

<html:errors/>



<html:form action="/mailinglist" focus="shortname">
                <html:hidden property="mailinglistID"/>
                <html:hidden property="action"/>
                <html:hidden property="targetID"/>
                <input type="hidden" name="save.x" value="0">

              <table border="0" cellspacing="0" cellpadding="0">
                <tr> 
                  <td><bean:message key="Name"/>:&nbsp;</td>
                  <td> 
                    <html:text property="shortname" size="52" maxlength="99"/>
                  </td>
                </tr>
		<tr> 
                  <td><bean:message key="Description"/>:&nbsp;</td>
                  <td> 
		    <html:textarea property="description" cols="40" rows="5"/>
                  </td>
                </tr>




                </table>
			  <p>
                <agn:ShowByPermission token="mailinglist.change">
                <html:image src="button?msg=Save" border="0" property="save" value="save"/>
                </agn:ShowByPermission>
                <agn:ShowByPermission token="mailinglist.delete">
                <% if(tmpMailinglistID!=0) { %>
                <html:link page="<%= "/mailinglist.do?action=" + MailinglistAction.ACTION_CONFIRM_DELETE + "&mailinglistID=" + tmpMailinglistID %>"><html:img src="button?msg=Delete" border="0"/></html:link>
                <% } %>
                </agn:ShowByPermission>
              </p>
              </html:form>

<%@include file="/footer.jsp"%>
