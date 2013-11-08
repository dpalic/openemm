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
 --%><%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*" contentType="text/html; charset=utf-8"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="mailing.send.show"/>

<% int tmpMailingID=0;
   String tmpShortname=new String("");
   MailingSendForm aForm=(MailingSendForm) request.getAttribute("mailingSendForm");

   if(aForm != null) {
      tmpMailingID=aForm.getMailingID();
      tmpShortname=aForm.getShortname();
   }
%>

<logic:equal name="mailingSendForm" property="isTemplate" value="true">
<% // template navigation:
  pageContext.setAttribute("sidemenu_active", new String("Templates"));
  pageContext.setAttribute("sidemenu_sub_active", new String("none"));
  pageContext.setAttribute("agnNavigationKey", new String("templateView"));
  pageContext.setAttribute("agnHighlightKey", new String("Send_Mailing"));
  pageContext.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
  pageContext.setAttribute("agnTitleKey", new String("Template"));
  pageContext.setAttribute("agnSubtitleKey", new String("Template"));
  pageContext.setAttribute("agnSubtitleValue", tmpShortname);
%>
</logic:equal>

<logic:equal name="mailingSendForm" property="isTemplate" value="false">
<%
// mailing navigation:
    pageContext.setAttribute("sidemenu_active", new String("Mailings"));
    pageContext.setAttribute("sidemenu_sub_active", new String("none"));
    pageContext.setAttribute("agnNavigationKey", new String("mailingView"));
    pageContext.setAttribute("agnHighlightKey", new String("Send_Mailing"));
    pageContext.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
    pageContext.setAttribute("agnTitleKey", new String("Mailing"));
    pageContext.setAttribute("agnSubtitleKey", new String("Mailing"));
    pageContext.setAttribute("agnSubtitleValue", tmpShortname);
%>
</logic:equal>

<%@include file="/header.jsp"%>
<%@include file="/messages.jsp" %>

    <html:form action="/mailingsend">
        <html:hidden property="mailingID"/>
        <html:hidden property="action"/>

        <html:hidden property="sendDate"/>
        <html:hidden property="sendHour"/>
        <html:hidden property="sendMinute"/>


        <html:hidden property="stepping"/>
        <html:hidden property="blocksize"/>

        <br>
        <b><bean:message key="mailing.send.confirm"/></b>
        <br>

        <p>
            <html:image src="button?msg=Send" property="send" value="send"/>
            <html:link page="<%= new String("/mailingsend.do?action=" + MailingSendAction.ACTION_VIEW_SEND + "&mailingID=" + tmpMailingID) %>"><html:img src="button?msg=Cancel" border="0"/></html:link>
        </p>

    </html:form>
<%@include file="/footer.jsp"%>
