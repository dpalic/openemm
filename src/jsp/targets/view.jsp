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
 --%><%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.target.*, org.agnitas.target.impl.*, org.agnitas.beans.*, java.util.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="targets.show"/>

<% int tmpTargetID=0;
   String tmpShortname=new String("");
   if(request.getAttribute("targetForm")!=null) {
      tmpTargetID=((TargetForm)request.getAttribute("targetForm")).getTargetID();
      tmpShortname=((TargetForm)request.getAttribute("targetForm")).getShortname();
   }
%>

<% pageContext.setAttribute("sidemenu_active", new String("Targets")); %>
<% if(tmpTargetID!=0) {
     pageContext.setAttribute("sidemenu_sub_active", new String("none"));
   } else {
     pageContext.setAttribute("sidemenu_sub_active", new String("NewTarget"));
   }
%>
<% pageContext.setAttribute("agnTitleKey", new String("Target")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Target")); %>
<% pageContext.setAttribute("agnSubtitleValue", tmpShortname); %>
<% pageContext.setAttribute("agnNavigationKey", new String("targetView")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("NewTarget")); %>
<% pageContext.setAttribute("agnNavHrefAppend", new String("&targetID="+tmpTargetID)); %>
<%@include file="/header.jsp"%>

<html:errors/>

<agn:ShowColumnInfo id="colsel"/>

<html:form action="/target" focus="shortname">
	<html:hidden property="targetID"/>
    <html:hidden property="action"/>
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
        <tr><td colspan="2"><hr><span class="head3"><bean:message key="TargetDefinition"/>:<br></span><br></td></tr>
        <tr><td colspan="2">
			<table border="0" cellspacing="2" cellpadding="0">
		    <% TargetNode aNode=null;
		       String className=null;
		       int index=0;
		       boolean isFirst=true; %>
		    	<logic:iterate id="aNode1" name="targetForm" property="target.allNodes">
		        <% aNode=(TargetNode)pageContext.getAttribute("aNode1");
		           className=aNode.getClass().getName(); 
		           index++; %>
		        	<tr>
		            <!-- AND/OR -->
        		    	<td>
                		<% if(!isFirst) { %>
		                	<select name="trgt_chainop<%= index %>" size="1">
        		            	<option value="1" <% if(aNode.getChainOperator()==1) { %>selected<% } %>><bean:message key="and"/></option>
                		        <option value="2" <% if(aNode.getChainOperator()==2) { %>selected<% } %>><bean:message key="or"/></option>
		                    </select>
        		        <% } else { %>
                			&nbsp;<input type="hidden" name="trgt_chainop<%= index %>" value="0">
		                    <% isFirst=false; } %>
        		        </td>
                		<!-- Bracket-Open Y/N -->
		                <td>
        		        	<select name="trgt_bracketopen<%= index %>" size="1">
                		    	<option value="0" <% if(!aNode.isOpenBracketBefore()) { %>selected<% } %>>&nbsp;</option>
                        		<option value="1" <% if(aNode.isOpenBracketBefore()) { %>selected<% } %>>(</option>
		                    </select>
        		        </td>
                		<!-- Column-Select -->
		                <td>
	    		            <input type="hidden" name="trgt_column<%= index %>" size="1" value="<%= new String(aNode.getPrimaryField()+"#"+aNode.getPrimaryFieldType()) %>">
                		    <% if(aNode.getPrimaryField().equals(AgnUtils.getSQLCurrentTimestampName())) { %>
    	                		<bean:message key="sysdate"/>
		                    <% } else { %>
        			            <% System.out.println(aNode.getPrimaryField()); %>
                		        <%= ((Map)((TreeMap)pageContext.getAttribute("__colsel_colmap")).get(aNode.getPrimaryField())).get("shortname") %>
		                    <% } %>
        		        </td>
                		<!-- Operator-Select -->
		                <td>
	    		            <select name="trgt_operator<%= index %>" style="width:100%"> size="1">
                		    <% int idx=1;
		                       String aOp=null;
        		               Iterator aIt=(Arrays.asList(aNode.OPERATORS)).iterator();
                		       while(aIt.hasNext()) {
                       				aOp=(String)aIt.next();
		                            if(aOp!=null) {
        		                    	if(idx==aNode.getPrimaryOperator()) { %>
                		                	<option value="<%= idx %>" selected><%= aOp %></option> 
                        		        <% } else { %>
                                			<option value="<%= idx %>"><%= aOp %></option> 
		                                <% }
        		                    }
                		            idx++;
		                       } %>
        		            </select>
						</td>
		                <!-- Value-Input -->
        		        <td>
                		<% if(className.equals("org.agnitas.target.impl.TargetNodeDate") && (aNode.getPrimaryOperator()!=TargetNode.OPERATOR_IS)) { %>
			                <nobr><input type="text" style="width:53%" name="trgt_value<%= index %>" value="<%= aNode.getPrimaryValue() %>">
        		            <select name="trgt_dateformat<%= index %>" style="width:45%" size="1">
    	        		        <option value="yyyymmdd"<% if(((TargetNodeDate)aNode).getDateFormat().equals("yyyymmdd")){%> selected<%}%>><bean:message key="date.format.YYYYMMDD"/></option>
		                        <option value="mmdd"<% if(((TargetNodeDate)aNode).getDateFormat().equals("mmdd")){%> selected<%}%>><bean:message key="date.format.MMDD"/></option>
        		                <option value="yyyymm"<% if(((TargetNodeDate)aNode).getDateFormat().equals("yyyymm")){%> selected<%}%>><bean:message key="date.format.YYYYMM"/></option>
                		        <option value="dd"<% if(((TargetNodeDate)aNode).getDateFormat().equals("dd")){%> selected<%}%>><bean:message key="date.format.DD"/></option>
                        		<option value="mm"<% if(((TargetNodeDate)aNode).getDateFormat().equals("mm")){%> selected<%}%>><bean:message key="date.format.MM"/></option>
		                        <option value="yyyy"<% if(((TargetNodeDate)aNode).getDateFormat().equals("yyyy")){%> selected<%}%>><bean:message key="date.format.YYYY"/></option>
        		            </select></nobr>
                		<% } %>
		                <% if(className.equals("org.agnitas.target.impl.TargetNodeNumeric") && (aNode.getPrimaryOperator()!=TargetNode.OPERATOR_MOD) && (aNode.getPrimaryOperator()!=TargetNode.OPERATOR_IS)) { %>
	    		            <% if(aNode.getPrimaryField().equalsIgnoreCase("MAILTYPE")) { %>
    	        		        <select name="trgt_value<%= index %>" size="1" style="width:100%">
        	            		    <option value="0"<% if(aNode.getPrimaryValue().equals("0")){%> selected<%}%>><bean:message key="Text"/></option>
		                            <option value="1"<% if(aNode.getPrimaryValue().equals("1")){%> selected<%}%>><bean:message key="HTML"/></option>
        		                    <option value="2"<% if(aNode.getPrimaryValue().equals("2")){%> selected<%}%>><bean:message key="OfflineHTML"/></option>                                           
                		        </select>
		                    <% } else { if(aNode.getPrimaryField().equalsIgnoreCase("GENDER")) { %>
        		            	<select name="trgt_value<%= index %>" size="1" style="width:100%">
                		        	<option value="0" <% if(aNode.getPrimaryValue().equals("0")) { %> selected <% } %>><bean:message key="gender.0.short"/></option>
                        		    <option value="1" <% if(aNode.getPrimaryValue().equals("1")) { %> selected <% } %>><bean:message key="gender.1.short"/></option>
		                            <agn:ShowByPermission token="use_extended_gender">
        		                    	<option value="3" <% if(aNode.getPrimaryValue().equals("3")) { %> selected <% } %>><bean:message key="gender.3.short"/></option>
                		                <option value="4" <% if(aNode.getPrimaryValue().equals("4")) { %> selected <% } %>><bean:message key="gender.4.short"/></option>
                        		        <option value="5" <% if(aNode.getPrimaryValue().equals("5")) { %> selected <% } %>><bean:message key="gender.5.short"/></option>
		                            </agn:ShowByPermission>
        		                    <option value="2" <% if(aNode.getPrimaryValue().equals("2")) { %> selected <% } %>><bean:message key="gender.2.short"/></option>
								</select>
							<% } else { %>
        		            	<input type="text" style="width:100%" size="60" name="trgt_value<%= index %>" value="<%= aNode.getPrimaryValue() %>">
                		    <% } } } %>
		                    <% if(className.equals("org.agnitas.target.impl.TargetNodeNumeric") && (aNode.getPrimaryOperator()==TargetNode.OPERATOR_MOD)) { %>
	    		                <input type="text" style="width:38%" name="trgt_value<%= index %>" value="<%= aNode.getPrimaryValue() %>">
                		        <select style="width:20%" name="trgt_sec_operator<%= index %>">
                        		<% String aOp2=null;
		                           Iterator aIt2=(Arrays.asList(TargetNode.ALL_OPERATORS)).iterator();
        		                   for(int b=1; b<=4; b++) {
    	        		               aOp2=(String)aIt2.next();
                        		       if(b==((TargetNodeNumeric)aNode).getSecondaryOperator()) { %>
		        	                       <option value="<%= b %>" selected><%= aOp2 %></option> 
        		                    <% } else { %>
                		                   <option value="<%= b %>"><%= aOp2 %></option> 
                        		    <% } 
		                           } %>
        		                </select>
                		        <input style="width:38%" type="text" name="trgt_sec_value<%= index %>" value="<%= ((TargetNodeNumeric)aNode).getSecondaryValue() %>">
		                    <% } %>
        		            <% if(className.equals("org.agnitas.target.impl.TargetNodeString") && (aNode.getPrimaryOperator()!=TargetNode.OPERATOR_IS)) { %>
	            		        <input type="text" style="width:100%" name="trgt_value<%= index %>" value="<%= aNode.getPrimaryValue() %>">
		                    <% } %>
        		            <% if(aNode.getPrimaryOperator()==TargetNode.OPERATOR_IS) { %>
	            		        <select name="trgt_value<%= index %>" size="1" style="width:100%">
    	                		    <option value="null" <% if(aNode.getPrimaryValue().equals("null")){ %>selected<%}%>>null</option>
		                            <option value="not null" <% if(aNode.getPrimaryValue().equals("not null")){ %>selected<%}%>>not null</option>
        		                </select>
                		    <% } %>
						</td>
        		        <!-- Bracket-Close Y/N -->
                		<td>
		 	               <select name="trgt_bracketclose<%= index %>" size="1">
    			               <option value="0" <% if(!aNode.isCloseBracketAfter()) { %>selected<% } %>>&nbsp;</option>
                		       <option value="1" <% if(aNode.isCloseBracketAfter()) { %>selected<% } %>>)</option>
		                   </select>
        		        </td>
                		<!-- Remove- / Add-Button -->
		                <td>
        		 	       <html:image src="button?msg=Remove" border="0" property="<%= new String("trgt_remove"+index) %>" value="<%= new String("trgt_remove"+index) %>"/>
                		</td>
					</tr>
				</logic:iterate>
		        <!-- Fixed new-Rule-Line -->
        		<tr>
		        	<td colspan=7>
        		    	<br><hr><b><bean:message key="NewRule"/>:</b>
		            </td>
        		</tr>
		        <tr>
        		<!-- AND/OR -->
			    	<td>
        		    <% if(!isFirst) { %>
		            	<select name="trgt_chainop0" size="1">
        		        	<option value="1" selected><bean:message key="and"/></option>
                		    <option value="2"><bean:message key="or"/></option>
		                </select>
        		    <% } else { %>
            			&nbsp;<input type="hidden" name="trgt_chainop0" value="0">
		             <% isFirst=false; } %>
        		    </td>
		            <!-- Bracket-Open Y/N -->
        		    <td>
		            	<select name="trgt_bracketopen0" size="1">
        		        	<option value="0" selected>&nbsp;</option>
                		    <option value="1">(</option>
		                </select>
        		    </td>
		            <!-- Column-Select -->
        		    <td>
	            		<select name="trgt_column0" size="1">
		    	            <agn:ShowColumnInfo id="colsel">
        			            <option value="<%= pageContext.getAttribute("_colsel_column_name") %>#<%= pageContext.getAttribute("_colsel_data_type") %>"><%= pageContext.getAttribute("_colsel_shortname") %></option>
                		    </agn:ShowColumnInfo>
		                    <option value="<%= AgnUtils.getSQLCurrentTimestampName() %>#DATE"><bean:message key="sysdate"/></option>
        		        </select>
					</td>
        		    <!-- Operator-Select -->
		            <td>
	    		    	<select name="trgt_operator0" size="1">
		                <% int idx=1;
        		           String aOp=null;
                		   Iterator aIt=(Arrays.asList(TargetNode.ALL_OPERATORS)).iterator();
		                   while(aIt.hasNext()) {
        		           		aOp=(String)aIt.next(); 
                		    // if(!aOp.equals("IS")) { %>
		                        <option value="<%= idx %>"><%= aOp %></option> 
        		         <% // }
                		        idx++;
		                   } %>
        		        </select>
					</td>
        		    <!-- Value-Input -->
		            <td>
 	    		    	<input type="text" style="width:200px" name="trgt_value0" value="">
		            </td>
        		    <!-- Bracket-Close Y/N -->
		            <td>
 	    		    	<select name="trgt_bracketclose0" size="1">
                			<option value="0" selected>&nbsp;</option>
		                    <option value="1">)</option>
        		        </select>
		            </td>
        		    <!-- Remove- / Add-Button -->
		            <td>
        		    	<html:image src="button?msg=Add" border="0" property="trgt_add" value="trgt_add"/>
		            </td>
				</tr>
			</table>
		</td>
	</tr>
    <tr>
    	<td colspan="2"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="1" height="5" border="0"></td>
    </tr>
    <tr>
    	<td colspan="2">                
        	<html:image src="button?msg=Save" border="0" property="save" value="save"/>
            <% if(tmpTargetID!=0) { %>
            	<html:image src="button?msg=Delete" border="0" property="delete" value="delete"/>
            <% } %>                
            <html:image src="button?msg=Copy" border="0" property="copy" value="copy"/>
		</td>
	</tr>
</table>
<div align = right><html:link page="<%= new String("/recipient_stats.do?action=2&mailinglistID=0&targetID=" + tmpTargetID) %>"><bean:message key="Statistics"/>...</html:link></div>     
	<agn:ShowByPermission token="targets.createml">
    <% if(tmpTargetID!=0) { %>
    	<br><div align = right><html:link page="<%= new String("/target.do?action=" + TargetAction.ACTION_CREATE_ML + "&targetID=" + tmpTargetID) %>"><bean:message key="createMList"/></html:link></div>
    <% } %>
    </agn:ShowByPermission>
              
    <agn:ShowByPermission token="recipient.delete">
    <% if(tmpTargetID!=0) { %>
    	<br><div align = right><html:link page="<%= new String("/target.do?action=" + TargetAction.ACTION_DELETE_RECIPIENTS_CONFIRM + "&targetID=" + tmpTargetID) %>"><bean:message key="target.delete.recipients"/></html:link></div>
    <% } %>
    </agn:ShowByPermission>
</html:form>
<%@include file="/footer.jsp"%>