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
 --%><%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.EmmLayout" contentType="text/html; charset=utf-8" buffer="256kb" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<agn:CheckLogon/>

<agn:Permission token="stats.rdir"/>

<% 
   int tmpMailingID=0;
   int tmpTargetID=0;
   String tmpShortname=new String("");
   EmmLayout aLayout=(EmmLayout)session.getAttribute("emm.layout");
   MailingStatForm aForm=null;
   int maxblue = 0;
   int maxSubscribers = 0;

   if(session.getAttribute("mailingStatForm")!=null) {
      aForm=(MailingStatForm)session.getAttribute("mailingStatForm");
      tmpMailingID=aForm.getMailingID();
      tmpTargetID=aForm.getTargetID();
      tmpShortname=aForm.getMailingShortname();
      maxblue=aForm.getMaxblue();
      maxSubscribers=aForm.getMaxSubscribers();
   }
%>

<% pageContext.setAttribute("sidemenu_active", new String("Mailings")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("none")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Mailing")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Mailing")); %>
<% pageContext.setAttribute("agnSubtitleValue", tmpShortname); %>
<% pageContext.setAttribute("agnNavigationKey", new String("mailingView")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Statistics")); %>
<% pageContext.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID)); %>

<% pageContext.setAttribute("agnRefresh", new String("2")); %>

<%@include file="/header.jsp"%>
<%@include file="/messages.jsp" %>

<html:form action="/mailing_stat">
    <html:hidden property="mailingID"/>
    <html:hidden property="action"/>

<table border="0" cellspacing="0" cellpadding="0" width="400">
    <tr>
        <td>
            <b>&nbsp;<b>
        </td>
    </tr>
    <tr>
        <td>
            <img border="0" width="44" height="48" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>wait.gif"/>
        </td>
    </tr>
    <tr>
        <td>
            <b>&nbsp;<b>
        </td>
    </tr>
    <tr>
        <td>
            <b><bean:message key="StatSplashMessage"/><b>
        </td>
    </tr>
    <tr>
        <td>
            <b>&nbsp;<b>
        </td>
    </tr>

</table>

</html:form>

<%@include file="/footer.jsp"%> 
