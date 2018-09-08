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
 --%>
 
<%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.target.*, org.agnitas.target.impl.*, org.agnitas.beans.*, java.util.*" contentType="text/html; charset=utf-8"  errorPage="/error.jsp" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<bean:define id="index" name="${FORM_NAME}" property="numTargetNodes" toScope="page" /> 

<c:set var="ACTION_SAVE" value="<%= TargetAction.ACTION_SAVE %>" scope="page" />

<c:if test="${empty TARGET_LOCKED}">
	<c:set var="TARGET_LOCKED" value="false" scope="page" />
</c:if>

<tr>
	<!-- chain operator -->
	<td>
		<c:choose>
			<c:when test="${index != 0}">
				<html:select property="chainOperatorNew" size="1" disabled="${TARGET_LOCKED}">
					<html:option value="<%= Integer.toString(TargetNode.CHAIN_OPERATOR_AND) %>" key="default.and" />
					<html:option value="<%= Integer.toString(TargetNode.CHAIN_OPERATOR_OR) %>" key="default.or" />
				</html:select>
			</c:when>
			<c:otherwise>
				<html:hidden property="chainOperatorNew" value="0"/>
                <div class="advanced_search_filter_left_space">&nbsp;</div>
			</c:otherwise>
		</c:choose>
	</td>

	<!-- opening parenthesis -->
	<td>
		<html:select property="parenthesisOpenedNew" size="1" disabled="${TARGET_LOCKED}">
			<html:option value="0">&nbsp</html:option>
			<html:option value="1">(</html:option>
		</html:select>
	</td>

	<!-- DB column -->
	<td>
		<html:select property="columnAndTypeNew" value="EMAIL" size="1"  styleClass="advanced_search_filter_select2" disabled="${TARGET_LOCKED}">
			<agn:ShowColumnInfo id="colsel">
				<html:option value="<%= ((String)pageContext.getAttribute(\"_colsel_column_name\")).toUpperCase() %>"><%= pageContext.getAttribute("_colsel_shortname") %></html:option>
			</agn:ShowColumnInfo>
			<agn:ShowByPermission token="mailing.interval">
				<html:option value="<%= TargetNodeIntervalMailing.PSEUDO_COLUMN_NAME %>"><bean:message key="receivedIntervalMailing"/></html:option>
			</agn:ShowByPermission>
			<agn:ShowByPermission token="targets.advancedRules.recipients" >
				<c:if test="${not HIDE_SPECIAL_TARGET_FEATURES}">
					<html:option value="<%= TargetNodeMailingOpened.PSEUDO_COLUMN_NAME %>"><bean:message key="target.rule.mailingOpened"/></html:option>
					<html:option value="<%= TargetNodeMailingClicked.PSEUDO_COLUMN_NAME %>"><bean:message key="target.rule.mailingClicked"/></html:option>
					<html:option value="<%= TargetNodeMailingReceived.PSEUDO_COLUMN_NAME %>"><bean:message key="target.rule.mailingReceived"/></html:option>
				</c:if>
			</agn:ShowByPermission>
			<html:option value="CURRENT_TIMESTAMP" key="default.sysdate" />
		</html:select>
	</td>

	<!-- operator -->
	<td>
		<html:select property="primaryOperatorNew" size="1"  styleClass="advanced_search_filter_select3" disabled="${TARGET_LOCKED}">
			<logic:iterate collection="<%= TargetNode.ALL_OPERATORS %>" id="all_operator">
				<c:if test="${not HIDE_SPECIAL_TARGET_FEATURES or (not (all_operator.operatorKey eq 'yes' or all_operator.operatorKey eq 'no'))}">
					<html:option value="${all_operator.operatorCode}"><bean:message key="target.operator.${all_operator.operatorKey}" /></html:option>
				</c:if>
			</logic:iterate>
		</html:select>
	</td>

	<!-- value -->
	<td>
	    <html:text property="primaryValueNew" styleClass="advanced_search_filter_select4" disabled="${TARGET_LOCKED}" />
	<%--
		<input type="text" name="primaryValueNew" value=""  class="advanced_search_filter_select4" disabled="${TARGET_LOCKED}">
		--%>
	</td>

	<!-- closing parenthesis -->
	<td>
		<html:select property="parenthesisClosedNew" size="1" disabled="${TARGET_LOCKED}">
			<html:option value="0">&nbsp</html:option>
			<html:option value="1">)</html:option>
		</html:select>
	</td>

	<!-- add / remove button -->
	<td>
        <input type="hidden" id="addTargetNode" name="addTargetNode" value=""/>
        
        <c:if test="${not TARGET_LOCKED}">
	        <html:link styleClass="advanced_search_add" href="#" onclick="document.getElementById('addTargetNode').value='true'; document.${FORM_NAME}.submit(); return false;"><bean:message key="button.Add"/></html:link>
    	</c:if>    
	</td>
</tr>
