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
 --%><%@ page language="java" contentType="text/html; charset=utf-8"
	import="org.agnitas.util.*,org.agnitas.web.*,org.agnitas.target.*,org.agnitas.target.impl.*,org.agnitas.beans.*,java.util.*,org.springframework.context.*,org.agnitas.dao.TargetDao,org.springframework.web.context.support.WebApplicationContextUtils"
	buffer="32kb"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib uri="http://ajaxtags.org/tags/ajax" prefix="ajax"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<agn:CheckLogon />
<agn:Permission token="recipient.show" />

<c:set var="sidemenu_active" value="Recipients" />
<c:set var="sidemenu_sub_active" value="Overview" />
<c:set var="agnTitleKey" value="Recipients" />
<c:set var="agnSubtitleKey" value="Recipients" />
<c:set var="agnNavigationKey" value="subscriber_list" />
<c:set var="agnHighlightKey" value="Overview" />
<c:set var="ACTION_LIST" value="<%= RecipientAction.ACTION_LIST %>" />
<c:set var="ACTION_CONFIRM_DELETE" value="<%= RecipientAction.ACTION_CONFIRM_DELETE %>" />
<c:set var="ACTION_VIEW" value="<%= RecipientAction.ACTION_VIEW %>" />
 
 
<%@include file="/header.jsp"%>
<script type="text/javascript">
<!--
	function parametersChanged(){
		document.getElementsByName('recipientForm')[0].numberOfRowsChanged.value = true;
	}
//-->
</script>
<script src="js/tablecolumnresize.js" type="text/javascript" ></script>
<script type="text/javascript">
	var prevX = -1;
    var tableID = 'recipient';
    var columnindex = 0;
    var dragging = false;
	
   document.onmousemove = drag;
   document.onmouseup = dragstop;
   
   function submitImageButton(btnName, btnValue) {
   	document.getElementById('imageButton').name = btnName + '.x';
   	document.getElementById('imageButton').value = btnValue;
   	
   	// document.getElementById('filterForm').submit();
   }
</script>

<%
	EmmLayout aLayout = (EmmLayout) session.getAttribute("emm.layout");
	int mailingListID;
	int targetID;
	String user_type = null;
	int user_status = 0;

	RecipientForm aForm = (RecipientForm) session
			.getAttribute("recipientForm");
	mailingListID = aForm.getListID();
	targetID = aForm.getTargetID();
	user_type = aForm.getUser_type();
	user_status = aForm.getUser_status();
	int index = 0;
	boolean isFirst = true;
	TargetRepresentation targetRep = aForm.getTarget();
%>

<agn:ShowColumnInfo id="colsel"
	table="<%=AgnUtils.getCompanyID(request)%>" />

<%@include file="/messages.jsp" %>

<table border="0" cellspacing="0">
	<html:form action="/recipient.do?action=${ACTION_LIST}" styleId="filterForm">
		<input type="hidden" id="imageButton" name="unset" value="unset" />							
		<html:hidden property="numberOfRowsChanged" />
		<tr>
			<td colspan="5">
				<b><bean:message key="recipient.search" />:</b>
			</td>
		</tr>
		<tr>
			<td colspan="5">
				<table border="0" cellspacing="2" cellpadding="0">
					<logic:iterate id="aNode1" name="recipientForm"
						property="target.allNodes">
						<%
							TargetNode aNode = (TargetNode) pageContext
												.getAttribute("aNode1");
										String className = aNode.getClass().getName();
										index++;
						%>
						<tr>
							<!-- AND/OR -->
							<td>
								<%
									if (!isFirst) {
								%>
								<select name="trgt_chainop<%=index%>" size="1"
									onchange="parametersChanged()">
									<option value="1" <% if(aNode.getChainOperator() == 1) { %>
										selected <% } %>>
										<bean:message key="and" />
									</option>
									<option value="2" <% if(aNode.getChainOperator() == 2) { %>
										selected <% } %>>
										<bean:message key="or" />
									</option>
								</select>
								<%
									} else {
								%>
								&nbsp;
								<input type="hidden" name="trgt_chainop<%=index%>" value="0">
								<%
									isFirst = false;
												}
								%>
							</td>
							<!-- Bracket-Open Y/N -->
							<td>
								<select name="trgt_bracketopen<%=index%>" size="1"
									onchange="parametersChanged()">
									<option value="0" <% if(!aNode.isOpenBracketBefore()) { %>
										selected <% } %>>
										&nbsp;
									</option>
									<option value="1" <% if(aNode.isOpenBracketBefore()) { %>
										selected <% } %>>
										(
									</option>
								</select>
							</td>
							<!-- Column-Select -->
							<td>
								<input type="hidden" name="trgt_column<%=index%>" size="1"
									value="<%=new String(aNode.getPrimaryField() + "#"
										+ aNode.getPrimaryFieldType())%>">
								<%
									if (aNode.getPrimaryField().equals("sysdate")) {
								%>
								<bean:message key="sysdate" />
								<%
									} else if (aNode.getPrimaryField().equals(
														"bind.change_date")) {
								%>
								ml.change_date
								<%
									} else {
													TreeMap tm = (TreeMap) pageContext
															.getAttribute("__colsel_colmap");
													if (tm != null
															&& tm.get(aNode.getPrimaryField()) != null) {
								%>
								<%=((Map) tm
												.get(aNode.getPrimaryField()))
												.get("shortname")%>
								<%
									}
												}
								%>
							</td>
							<!-- Operator-Select -->
							<td>
								<select name="trgt_operator<%=index%>" style="width: 100%"
									size="1" onchange="parametersChanged()">
									<%
										int idx = 1;
													String aOp = null;
													Iterator aIt = (Arrays.asList(aNode.OPERATORS))
															.iterator();
													while (aIt.hasNext()) {
														aOp = (String) aIt.next();
														if (aOp != null) {
															if (idx == aNode.getPrimaryOperator()) {
									%>
									<option value="<%=idx%>" selected><%=aOp%></option>
									<%
										} else {
									%>
									<option value="<%=idx%>"><%=aOp%></option>
									<%
										}
														}
														idx++;
													}
									%>
								</select>
							</td>
							<!-- Value-Input -->
							<td>
								<%
									if (className
														.equals("org.agnitas.target.impl.TargetNodeDate")
														&& (aNode.getPrimaryOperator() != TargetNode.OPERATOR_IS)) {
								%>
								<nobr>
									<input type="text" style="width: 53%"
										name="trgt_value<%=index%>"
										value="<%=aNode.getPrimaryValue()%>">
									<select name="trgt_dateformat<%=index%>" style="width: 45%"
										size="1" onchange="parametersChanged()">
										<option value="yyyymmdd"
											<% if(((TargetNodeDate)aNode).getDateFormat().equals("yyyymmdd")){%>
											selected <%}%>>
											<bean:message key="date.format.YYYYMMDD" />
										</option>
										<option value="mmdd"
											<% if(((TargetNodeDate)aNode).getDateFormat().equals("mmdd")){%>
											selected <%}%>>
											<bean:message key="date.format.MMDD" />
										</option>
										<option value="yyyymm"
											<% if(((TargetNodeDate)aNode).getDateFormat().equals("yyyymm")){%>
											selected <%}%>>
											<bean:message key="date.format.YYYYMM" />
										</option>
										<option value="dd"
											<% if(((TargetNodeDate)aNode).getDateFormat().equals("dd")){%>
											selected <%}%>>
											<bean:message key="date.format.DD" />
										</option>
										<option value="mm"
											<% if(((TargetNodeDate)aNode).getDateFormat().equals("mm")){%>
											selected <%}%>>
											<bean:message key="date.format.MM" />
										</option>
										<option value="yyyy"
											<% if(((TargetNodeDate)aNode).getDateFormat().equals("yyyy")){%>
											selected <%}%>>
											<bean:message key="date.format.YYYY" />
										</option>
									</select>
								</nobr>
								<%
									}
												if (className
														.equals("org.agnitas.target.impl.TargetNodeNumeric")
														&& (aNode.getPrimaryOperator() != TargetNode.OPERATOR_MOD)
														&& (aNode.getPrimaryOperator() != TargetNode.OPERATOR_IS)) {
													if (aNode.getPrimaryField().equalsIgnoreCase(
															"MAILTYPE")) {
								%>
								<select name="trgt_value<%=index%>" size="1"
									style="width: 100%" onchange="parametersChanged()">
									<option value="0" <% if(aNode.getPrimaryValue().equals("0")){%>
										selected <%}%>>
										<bean:message key="Text" />
									</option>
									<option value="1" <% if(aNode.getPrimaryValue().equals("1")){%>
										selected <%}%>>
										<bean:message key="HTML" />
									</option>
									<option value="2" <% if(aNode.getPrimaryValue().equals("2")){%>
										selected <%}%>>
										<bean:message key="OfflineHTML" />
									</option>
								</select>
								<%
									} else {
														if (aNode.getPrimaryField().equalsIgnoreCase(
																"GENDER")) {
								%>
								<select name="trgt_value<%=index%>" size="1"
									style="width: 100%" onchange="parametersChanged()">
									<option value="0"
										<% if(aNode.getPrimaryValue().equals("0")) { %> selected
										<% } %>>
										<bean:message key="gender.0.short" />
									</option>
									<option value="1"
										<% if(aNode.getPrimaryValue().equals("1")) { %> selected
										<% } %>>
										<bean:message key="gender.1.short" />
									</option>
									<agn:ShowByPermission token="use_extended_gender">
										<option value="3"
											<% if(aNode.getPrimaryValue().equals("3")) { %> selected
											<% } %>>
											<bean:message key="gender.3.short" />
										</option>
										<option value="4"
											<% if(aNode.getPrimaryValue().equals("4")) { %> selected
											<% } %>>
											<bean:message key="gender.4.short" />
										</option>
										<option value="5"
											<% if(aNode.getPrimaryValue().equals("5")) { %> selected
											<% } %>>
											<bean:message key="gender.5.short" />
										</option>
									</agn:ShowByPermission>
									<option value="2"
										<% if(aNode.getPrimaryValue().equals("2")) { %> selected
										<% } %>>
										<bean:message key="gender.2.short" />
									</option>
								</select>
								<%
									} else {
								%>
								<input type="text" style="width: 100%" size="60"
									name="trgt_value<%=index%>"
									value="<%=aNode.getPrimaryValue()%>"
									onchange="parametersChanged()">
								<%
									}
													}
												}
								%>
								<%
									if (className
														.equals("org.agnitas.target.impl.TargetNodeNumeric")
														&& (aNode.getPrimaryOperator() == TargetNode.OPERATOR_MOD)) {
								%>
								<input type="text" style="width: 38%"
									name="trgt_value<%=index%>"
									value="<%=aNode.getPrimaryValue()%>">
								<select style="width: 20%" name="trgt_sec_operator<%=index%>"
									onchange="parametersChanged()">
									<%
										String aOp2 = null;
														Iterator aIt2 = (Arrays
																.asList(TargetNode.ALL_OPERATORS))
																.iterator();
														for (int b = 1; b <= 4; b++) {
															aOp2 = (String) aIt2.next();
															if (b == ((TargetNodeNumeric) aNode)
																	.getSecondaryOperator()) {
									%>
									<option value="<%=b%>" selected><%=aOp2%></option>
									<%
										} else {
									%>
									<option value="<%=b%>"><%=aOp2%></option>
									<%
										}
														}
									%>
								</select>
								<input style="width: 38%" type="text"
									name="trgt_sec_value<%=index%>"
									value="<%=((TargetNodeNumeric) aNode)
											.getSecondaryValue()%>"
									onchange="parametersChanged()">
								<%
									}
												if (className
														.equals("org.agnitas.target.impl.TargetNodeString")
														&& (aNode.getPrimaryOperator() != TargetNode.OPERATOR_IS)) {
								%>
								<input type="text" style="width: 100%"
									name="trgt_value<%=index%>"
									value="<%=aNode.getPrimaryValue()%>"
									onchange="parametersChanged()">
								<%
									}
								%>
								<%
									if (aNode.getPrimaryOperator() == TargetNode.OPERATOR_IS) {
								%>
								<select name="trgt_value<%=index%>" size="1"
									style="width: 100%" onchange="parametersChanged()">
									<option value="null"
										<% if(aNode.getPrimaryValue().equals("null")){ %> selected
										<%}%>>
										null
									</option>
									<option value="not null"
										<% if(aNode.getPrimaryValue().equals("not null")){ %> selected
										<%}%>>
										not null
									</option>
								</select>
								<%
									}
								%>
							</td>
							<!-- Bracket-Close Y/N -->
							<td>
								<select name="trgt_bracketclose<%=index%>" size="1"
									onchange="parametersChanged()">
									<option value="0" <% if(!aNode.isCloseBracketAfter()) { %>
										selected <% } %>>
										&nbsp;
									</option>
									<option value="1" <% if(aNode.isCloseBracketAfter()) { %>
										selected <% } %>>
										)
									</option>
								</select>
							</td>
							<!-- Remove- / Add-Button -->
							<td>
								<c:set var="IMAGE_BTN" value="<%= "trgt_remove" + index %>" />
								<html:image src="button?msg=Remove" 
											border="0"
											property="${IMAGE_BTN}"
											value="${IMAGE_BTN}" 
											onclick="submitImageButton('${IMAGE_BTN}', '${IMAGE_BTN}')" />
							</td>
						</tr>
					</logic:iterate>
					<tr>
						<!-- AND/OR -->
						<td>
							<%
								if (!isFirst) {
							%>
							<select name="trgt_chainop0" size="1"
								onchange="parametersChanged()">
								<option value="1" selected>
									<bean:message key="and" />
								</option>
								<option value="2">
									<bean:message key="or" />
								</option>
							</select>
							<%
								} else {
							%>
							&nbsp;
							<input type="hidden" name="trgt_chainop0" value="0">
							<%
								isFirst = false;
									}
							%>
						</td>
						<!-- Bracket-Open Y/N -->
						<td>
							<select name="trgt_bracketopen0" size="1"
								onchange="parametersChanged()">
								<option value="0" selected>
									&nbsp;
								</option>
								<option value="1">
									(
								</option>
							</select>
						</td>
						<!-- Column-Select -->
						<td>
							<select name="trgt_column0" size="1"
								onchange="parametersChanged()">
								<agn:ShowColumnInfo id="colsel"
									table="<%=AgnUtils.getCompanyID(request)%>">
									<%
										if (pageContext.getAttribute("_colsel_shortname").equals(
														"email")) {
									%>
									<option
										value="<%=pageContext
										.getAttribute("_colsel_column_name")%>#<%= pageContext.getAttribute("_colsel_data_type") %>
										" selected><%=pageContext
												.getAttribute("_colsel_shortname")%></option>
									<%
										} else {
									%>
									<option
										value="<%=pageContext
										.getAttribute("_colsel_column_name")%>#<%= pageContext.getAttribute("_colsel_data_type") %>
										"><%=pageContext
												.getAttribute("_colsel_shortname")%></option>
									<%
										}
									%>
								</agn:ShowColumnInfo>
								<option value="sysdate#DATE">
									<bean:message key="sysdate" />
								</option>
								<option value="bind.change_date#DATE">
									ml.change_date
								</option>
							</select>
						</td>
						<!-- Operator-Select -->
						<td>
							<select name="trgt_operator0" size="1"
								onchange="parametersChanged()">
								<%
									int idx = 1;
										String aOp = null;
										Iterator aIt = (Arrays.asList(TargetNode.ALL_OPERATORS))
												.iterator();

										while (aIt.hasNext()) {
											aOp = (String) aIt.next();
											// if(!aOp.equals("IS")) {
								%>
								<option value="<%=idx%>"><%=aOp%></option>
								<%
									// }
											idx++;
										}
								%>
							</select>
						</td>
						<!-- Value-Input -->
						<td>
							<input type="text" style="width: 200px" name="trgt_value0"
								value="" onchange="parametersChanged()">
						</td>
						<!-- Bracket-Close Y/N -->
						<td>
							<select name="trgt_bracketclose0" size="1"
								onchange="parametersChanged()">
								<option value="0" selected>
									&nbsp;
								</option>
								<option value="1">
									)
								</option>
							</select>
						</td>
						<!-- Remove- / Add-Button -->
						<td>
							<html:image src="button?msg=Add" 
										border="0" 
										property="trgt_add"
										value="trgt_add" 
										onclick="submitImageButton('trgt_add', 'trgt_add')" />
						</td>
					</tr>
					<tr>
						<td colspan="5">
							<html:image src="button?msg=Update" 
										border="0"
										property="trgt_save" 
										value="trgt_save" 
										onclick="submitImageButton('trgt_save', 'trgt_save')" />
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td colspan=5>
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<td colspan=5>
							<br>
							<hr>
						</td>
					</tr>
					<tr>
						<td>
							<b><bean:message key="Mailinglist" />:</b>
							<br>
							<select name="listID" onchange="parametersChanged()">
								<option value="0" <%if(mailingListID==0) {%> selected <%}%>>
									<bean:message key="All_Mailinglists" />
								</option>
								<agn:ShowTable id="agntbl2"
									sqlStatement="<%=new String(
									"SELECT mailinglist_id, shortname FROM mailinglist_tbl WHERE company_id="
											+ AgnUtils.getCompanyID(request))%>"
									maxRows="100">
									<option
										value="<%=pageContext
									.getAttribute("_agntbl2_mailinglist_id")%>"
										<%if(Integer.toString(mailingListID).equals(pageContext.getAttribute("_agntbl2_mailinglist_id"))) {%>
										selected <%}%>><%=pageContext.getAttribute("_agntbl2_shortname")%></option>
								</agn:ShowTable>
							</select>
							&nbsp;&nbsp;
						</td>

						<td>
							<b><bean:message key="Target" />:</b>
							<br>
							<select name="targetID" onchange="parametersChanged()">
								<option value="0" <%if(targetID==0) {%> selected <%}%>>
									<bean:message key="All" />
								</option>
								<agn:ShowTable id="agntbl3"
									sqlStatement="<%=new String(
									"SELECT target_id, target_shortname FROM dyn_target_tbl WHERE company_id="
											+ AgnUtils.getCompanyID(request))%>"
									maxRows="200">
									<option
										value="<%=pageContext.getAttribute("_agntbl3_target_id")%>"
										<%if(Integer.toString(targetID).equals(pageContext.getAttribute("_agntbl3_target_id"))) {%>
										selected <%}%>><%=pageContext
									.getAttribute("_agntbl3_target_shortname")%></option>
								</agn:ShowTable>
							</select>
							&nbsp;&nbsp;
						</td>



						<td>
							<b><bean:message key="RecipientType" />:</b>
							<br>
							<select name="user_type" onchange="parametersChanged()">
								<!-- usr type; 'E' for everybody -->
								<option value="E" <%if(user_type.equals("E")) {%> selected <%}%>>
									<bean:message key="All" />
								</option>
								<option value="A" <%if(user_type.equals("A")) {%> selected <%}%>>
									<bean:message key="Administrator" />
								</option>
								<option value="T" <%if(user_type.equals("T")) {%> selected <%}%>>
									<bean:message key="TestSubscriber" />
								</option>
								<option value="W" <%if(user_type.equals("W")) {%> selected <%}%>>
									<bean:message key="NormalSubscriber" />
								</option>
							</select>
							&nbsp;&nbsp;
						</td>

						<td>
							<b><bean:message key="RecipientStatus" />:</b>
							<br>
							<select name="user_status" onchange="parametersChanged()">
								<!-- usr status; '0' is for everybody -->
								<option value="0" <%if(user_status==0) {%> selected <%}%>>
									<bean:message key="All" />
								</option>
								<option value="1" <%if(user_status==1) {%> selected <%}%>>
									<bean:message key="Active" />
								</option>
								<option value="2" <%if(user_status==2) {%> selected <%}%>>
									<bean:message key="Bounced" />
								</option>
								<option value="3" <%if(user_status==3) {%> selected <%}%>>
									<bean:message key="OptOutAdmin" />
								</option>
								<option value="4" <%if(user_status==4) {%> selected <%}%>>
									<bean:message key="OptOutUser" />
								</option>
								<option value="5" <%if(user_status == 5) {%> selected <%}%> >
									<bean:message key="MailingState5"/>
								</option>
							</select>
							&nbsp;&nbsp;
						</td>
						<td align="left">
							<html:image src="button?msg=OK" border="0" value="OK" />
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td colspan="5">
				<table>
					<tr>
						<td>
							<bean:message key="Admin.numberofrows" />
						</td>
						<td>
							<html:select property="numberofRows"
								onchange="parametersChanged()">
								<%
									String[] sizes = { "20", "50", "100" };
											for (int i = 0; i < sizes.length; i++) {
								%>
								<html:option value="<%=sizes[i]%>"><%=sizes[i]%></html:option>
								<%
									}
								%>

							</html:select>
							<logic:iterate collection="${recipientForm.columnwidthsList}"
								indexId="i" id="width">
								<html:hidden property="columnwidthsList[${i}]" />
							</logic:iterate>
						</td>
					</tr>
				</table>
			</td>
		</tr>

	</html:form>
	<tr>
		<td colspan="5">
			<display:table class="dataTable"
				pagesize="${recipientForm.numberofRows}" id="recipient"
				name="recipientList" sort="external" excludedParams="*"
				requestURI="/recipient.do?action=${ACTION_LIST}&__fromdisplaytag=true"
				partialList="true" size="${recipientList.fullListSize}">
				<display:column class="name" headerClass="head_name"
					titleKey="Salutation">
					<bean:message key="gender.${recipient.gender}.short" />
				</display:column>
				<display:column class="name" headerClass="head_name"
					property="firstname" titleKey="Firstname" sortable="true" />
				<display:column class="name" headerClass="head_name"
					property="lastname" titleKey="Lastname" sortable="true" />
				<display:column class="name" headerClass="head_name"
					property="email" titleKey="E-Mail" sortable="true"
					paramId="recipientID" paramProperty="customerid"
					url="/recipient.do?action=${ACTION_VIEW}" />
				<display:column class="edit" headerClass="head_edit">
					<agn:ShowByPermission token="recipient.delete">
						<html:link
							page="/recipient.do?action=${ACTION_CONFIRM_DELETE}&recipientID=${recipient.customerid}">
							<img
								src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>delete.gif"
								alt="<bean:message key="Delete"/>" border="0">
						</html:link>
					</agn:ShowByPermission>
					<agn:ShowByPermission token="recipient.view">
						<html:link
							page="/recipient.do?action=${ACTION_VIEW}&recipientID=${recipient.customerid}">
							<img
								src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>bearbeiten.gif"
								alt="<bean:message key="Edit"/>" border="0">
						</html:link>
					</agn:ShowByPermission>
				</display:column>
				<display:column style="overflow:hidden;white-space:nowrap;">
    		&nbsp;
    	</display:column>
	</display:table>
			<script type="text/javascript">
			table = document.getElementById('recipient');
			rewriteTableHeader(table);  
			writeWidthFromHiddenFields(table);			
		</script>
		</td>
	</tr>
	<tr>
		<td colspan="5">
			<bean:message key="Total" />
			&nbsp;
			<bean:message key="Recipients" />
			:&nbsp;<%=aForm.getAll()%></td>
	</tr>
</table>
<%@include file="/footer.jsp"%>
