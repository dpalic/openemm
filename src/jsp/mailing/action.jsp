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
 --%><%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*,org.agnitas.web.forms.*, org.agnitas.beans.*, java.util.*" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="mailing.show"/>

<% 	int tmpMailingID=0;
	String tmpShortname=new String("");
   	MailingBaseForm aForm=null;
   	if(session.getAttribute("mailingBaseForm")!=null) {
    	aForm=(MailingBaseForm)session.getAttribute("mailingBaseForm");
      	tmpShortname=aForm.getShortname();
      	tmpMailingID=aForm.getMailingID();
   	}

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

<%@include file="/header.jsp"%>
<%@include file="/messages.jsp" %>

	<html:form action="/mailingbase">
    	<html:hidden property="mailingID"/>
    	<html:hidden property="action"/>
    	<table border="0" cellspacing="0" cellpadding="0" width="100%">
	    	<tr>
    			<td><span class="head3"><bean:message key="Action"/></span></td>
		    	<td><span class="head3"><bean:message key="URL"/>&nbsp;</span></td>
		    </tr>
		    <tr><td colspan="2"><hr></td></tr>
			<%	if(aForm.getActions().size() > 0) {
		    		Iterator it = aForm.getActions().keySet().iterator();
		    		while(it.hasNext()) {
		    			String shortname = (String) it.next();
    		%>			<tr>
    						<td><%= shortname %></td>
    						<td><%=  aForm.getActions().get(shortname)%></td>
    					</tr>
				<% } %>
			<% } else { %>
					<tr><td colspan="2"><bean:message key="noActionsLinked"/></td></tr>
		    <% } %>
		</table>
    </html:form>
<%@include file="/footer.jsp"%>
