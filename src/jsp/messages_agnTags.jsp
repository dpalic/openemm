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
 
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/displaytag.tld" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
 
<logic:messagesPresent message="true">
	<div class="success_box">
		<html:messages id="msg" message="true" >
			<span class="success_message">${msg}</span><br/>
		</html:messages>
	</div>
</logic:messagesPresent>

<logic:messagesPresent>
	<div class="error_box">
		<html:messages id="msg" message="false">
			<span class="error_message">${msg}</span><br />
		</html:messages>
		<c:if test="${not empty errorReport }">
			<br>
			<display:table name="errorReport" id="reportRow" class="errorTable" >
			<display:column  headerClass="head_name" class="name"  sortable="false" titleKey="mailing.tag">
			 <c:choose>
			 	<c:when test="${not empty reportRow[1] }">
					${reportRow[1]}
				</c:when>
				<c:otherwise>
				   ${reportRow[2]}
				</c:otherwise>	
			</c:choose>
			</display:column>
			</display:table>
		</c:if>
	</div>
</logic:messagesPresent>
 