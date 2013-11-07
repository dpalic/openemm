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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>

<agn:Permission token="targets.show"/>

<c:set var="sidemenu_active" value="Targets" scope="page" />
<c:set var="sidemenu_sub_active" value="none" scope="page" />
<c:set var="agnTitleKey" value="Target" scope="page" />
<c:set var="agnSubtitleKey" value="Target" scope="page" />
<c:set var="agnSubtitleValue" value="${targetForm.shortname}" scope="page" />
<c:set var="agnNavigationKey" value="targetView" scope="page" />
<c:set var="agnHighlightKey" value="Target" scope="page" />
<c:set var="agnNavHrefAppend" value="&targetID=${targetForm.targetID}" scope="page" />

<c:set var="ACTION_VIEW" value="<%= TargetAction.ACTION_VIEW %>" scope="page" />

<%@include file="/header.jsp"%>
<%@include file="/messages.jsp" %>

<html:form action="/target">
                <html:hidden property="targetID"/>
                <html:hidden property="action"/>
                
				<span class="head1">${targetForm.shortname}</span>
				<br>
				<br>
                
                <span class="head3"><bean:message key="target.delete.question"/></span>
                <br><br>
                <html:image src="button?msg=Delete" border="0" property="kill" value="kill"/>&nbsp;<html:link page="/target.do?action=${targetForm.previousAction}&targetID=${targetForm.targetID}"><html:img src="button?msg=Cancel" border="0"/></html:link>
              </html:form>

<%@include file="/footer.jsp"%>
