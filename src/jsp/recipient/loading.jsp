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
 --%><%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*, java.util.*, org.springframework.context.*, org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/ajaxanywhere.tld" prefix="aa" %>

<agn:CheckLogon/>
<agn:Permission token="recipient.delete"/>

<% pageContext.setAttribute("sidemenu_active", new String("Recipients")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("Overview")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Recipients")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Recipients")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("subscriber_editor")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Overview")); %>

<%@include file="/header.jsp"%>
<script>

    function go() {
        document.getElementsByName('recipientForm')[0].submit();
    }

    ajaxAnywhere.getZonesToReload = function () {
        return "loading"
    };

    ajaxAnywhere.onAfterResponseProcessing = function () {
		if(! ${recipientForm.error } )
    		window.setTimeout("go();", ${recipientForm.refreshMillis});
    }
    ajaxAnywhere.showLoadingMessage = function(){};

    ajaxAnywhere.onAfterResponseProcessing();
</script>





<%
RecipientForm recipient=(RecipientForm)session.getAttribute("recipientForm");
recipient.setAction(RecipientAction.ACTION_LIST);
%>

<aa:zone name="loading" >
<html:errors/>
    <html:form action="/recipient" >
       <html:hidden property="action"/>
       <html:hidden property="error"/>
       <table border="0" cellspacing="0" cellpadding="0" width="400">
    <tr>
        <td>
            <b>&nbsp;<b>
        </td>
    </tr>
    <tr>
        <td>
        	<logic:equal value="false" name="recipientForm" property="error">
        	    <img border="0" width="44" height="48" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>wait.gif"/>
        	</logic:equal>
        	 <logic:equal value="true" name="recipientForm" property="error">
            	 <img border="0" width="29" height="30" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>warning.gif"/>
            </logic:equal>
        </td>
    </tr>
    <tr>
        <td>
            <b>&nbsp;<b>
        </td>
    </tr>
    <tr>
        <td>
            <b>
            <logic:equal value="false" name="recipientForm" property="error">
            	 <bean:message key="loading"/>
            </logic:equal>
            <logic:equal value="true" name="recipientForm" property="error">
            	 <bean:message key="loading.stopped"/>
            </logic:equal>
            
            <b>
        </td>
    </tr>
    <tr>
        <td>
            <b>&nbsp;<b>
        </td>
    </tr>

</table>
    </html:form>
</aa:zone>
<%@include file="/footer.jsp"%>
