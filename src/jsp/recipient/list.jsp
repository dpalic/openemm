<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.target.*, org.agnitas.target.impl.*, org.agnitas.beans.*, java.util.*, org.springframework.context.*, org.springframework.web.context.support.WebApplicationContextUtils" buffer="32kb" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>
<agn:Permission token="recipient.show"/>

<% pageContext.setAttribute("sidemenu_active", new String("Recipient")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("Overview")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Recipient")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Recipient")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("subscriber_editor")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Overview")); %>

<%@include file="/header.jsp"%>

   <%  
        int mailingListID;
        String user_type=null;
        int user_status=0;

        try {
            mailingListID=Integer.parseInt(request.getParameter("listID"));
        } catch (Exception e) {
            mailingListID=0;
        }

        if(request.getParameter("user_type")==null){   
            user_type=new String("E");
       } 
       else {
            user_type=new String(request.getParameter("user_type"));
       }
       if(request.getParameter("user_status")==null){  
            user_status=0;
       }
       else  {
            try {
                user_status=Integer.parseInt(request.getParameter("user_status"));
            } catch (Exception e) {
                user_status=0;
            }
       }
       TargetNode aNode=null;
       String className=null;
       int index=0;
       boolean isFirst=true;
       RecipientForm rec=(RecipientForm) request.getAttribute("recipientForm");
       TargetRepresentation targetRep=rec.getTarget();
%>

<agn:ShowColumnInfo id="colsel" table="<%= AgnUtils.getCompanyID(request) %>"/>

<table border="0">
    <html:form action="<%= new String("/recipient.do?action="+RecipientAction.ACTION_LIST) %>">
    <tr><td colspan="5"><b><bean:message key="recipient.search"/>:</b></td></tr>
    <tr><td colspan="5">
    <table border="0" cellspacing="2" cellpadding="0">
    <logic:iterate id="aNode1" name="recipientForm" property="target.allNodes">
        <% aNode=(TargetNode)pageContext.getAttribute("aNode1");
           className=aNode.getClass().getName(); 
           index++;
        %>
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
                           <% if(aNode.getPrimaryField().equals("sysdate")) { %>
                             <bean:message key="sysdate"/>
                           <% } else if(aNode.getPrimaryField().equals("bind.change_date")) { %>
                             ml.change_date
                           <% } else {
                                  TreeMap tm=(TreeMap) pageContext.getAttribute("__colsel_colmap");
                                  if(tm != null && tm.get(aNode.getPrimaryField()) != null) { %>
                                     <%= ((Map) tm.get(aNode.getPrimaryField())).get("shortname") %>
                           <%     }
                              } %>
                       </td>

                       <!-- Operator-Select -->
                       <td>
                         <select name="trgt_operator<%= index %>" style="width:100%"> size="1">
                           <%
                               int idx=1;
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
                               }
                           %>
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
                            <% if(aNode.getPrimaryField().equals("MAILTYPE")) { %>
                               <select name="trgt_value<%= index %>" size="1" style="width:100%">
                                   <option value="0"<% if(aNode.getPrimaryValue().equals("0")){%> selected<%}%>><bean:message key="Text"/></option>
                                   <option value="1"<% if(aNode.getPrimaryValue().equals("1")){%> selected<%}%>><bean:message key="HTML"/></option>
                                   <option value="2"<% if(aNode.getPrimaryValue().equals("2")){%> selected<%}%>><bean:message key="OfflineHTML"/></option>                                           
                               </select>
                            <% } else { if(aNode.getPrimaryField().equals("GENDER")) { %>
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
                                    <agn:ShowColumnInfo id="colsel" table="<%= AgnUtils.getCompanyID(request) %>">
                                        <% if(pageContext.getAttribute("_colsel_shortname").equals("email")) { %>
                                            <option value="<%= pageContext.getAttribute("_colsel_column_name") %>#<%= pageContext.getAttribute("_colsel_data_type") %>" selected><%= pageContext.getAttribute("_colsel_shortname") %></option>
                                        <% } else { %>
                                            <option value="<%= pageContext.getAttribute("_colsel_column_name") %>#<%= pageContext.getAttribute("_colsel_data_type") %>"><%= pageContext.getAttribute("_colsel_shortname") %></option>
                                        <% } %>
                                    </agn:ShowColumnInfo>
                                        <option value="sysdate#DATE"><bean:message key="sysdate"/></option>
                                        <option value="bind.change_date#DATE">ml.change_date"/></option>
                                    </select>
                                </td>

                                <!-- Operator-Select -->
                                <td>
                                  <select name="trgt_operator0" size="1">
                                    <%
                                        int idx=1;
                                        String aOp=null;
                                        Iterator aIt=(Arrays.asList(TargetNode.ALL_OPERATORS)).iterator();
                                        while(aIt.hasNext()) {
                                            aOp=(String)aIt.next(); 
                                            // if(!aOp.equals("IS")) { %>
                                               <option value="<%= idx %>"><%= aOp %></option> 
                                            <% // }
                                               idx++;
                                        }
                                    %>
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
                         <tr>
                             <td colspan="5">
                                 <html:image src="button?msg=Save" border="0" property="save" value="save"/>
                             </td>
                         </tr>
            </table>
        </td>
    </tr>
    <tr>
        <td colspan=5>
                <table border="0" cellspacing="0" cellpadding="0" width="100%">
                <tr><td colspan=4><br><hr></td></tr>

                <tr>
                  <td><b><bean:message key="Mailinglist"/>:</b><br>
                    <select name="listID">
                        <option value="0" <%if(mailingListID==0) {%> selected <%}%> ><bean:message key="All_Mailinglists"/></option>
                        <agn:ShowTable id="agntbl2" sqlStatement="<%= new String("SELECT mailinglist_id, shortname FROM mailinglist_tbl WHERE company_id="+AgnUtils.getCompanyID(request)) %>" maxRows="100">
                           <option value="<%= pageContext.getAttribute("_agntbl2_mailinglist_id") %>" <%if(Integer.toString(mailingListID).equals(pageContext.getAttribute("_agntbl2_mailinglist_id"))) {%> selected <%}%> ><%= pageContext.getAttribute("_agntbl2_shortname") %></option>
                        </agn:ShowTable>
                    </select>&nbsp;&nbsp;
                  </td>

                  <td><b><bean:message key="RecipientType"/>:</b><br>
                    <select name="user_type">  <!-- usr type; 'E' for everybody -->
                        <option value="E" <%if(user_type.equals("E")) {%> selected <%}%> ><bean:message key="All"/></option>
                        <option value="A" <%if(user_type.equals("A")) {%> selected <%}%> ><bean:message key="Administrator"/></option>
                        <option value="T" <%if(user_type.equals("T")) {%> selected <%}%> ><bean:message key="TestSubscriber"/></option>
                        <option value="W" <%if(user_type.equals("W")) {%> selected <%}%> ><bean:message key="NormalSubscriber"/></option>
                    </select>&nbsp;&nbsp;
                  </td>
                  
                  <td><b><bean:message key="RecipientStatus"/>:</b><br>
                    <select name="user_status">  <!-- usr status; '0' is for everybody -->
                        <option value="0" <%if(user_status==0) {%> selected <%}%> ><bean:message key="All"/></option>
                        <option value="1" <%if(user_status==1) {%> selected <%}%> ><bean:message key="Active"/></option>
                        <option value="2" <%if(user_status==2) {%> selected <%}%> ><bean:message key="Bounced"/></option>
                        <option value="3" <%if(user_status==3) {%> selected <%}%> ><bean:message key="OptOutAdmin"/></option>
                        <option value="4" <%if(user_status==4) {%> selected <%}%> ><bean:message key="OptOutUser"/></option>
                    </select>&nbsp;&nbsp;
                  </td>  
                  <td align="left"><html:image src="button?msg=OK" border="0" value="OK"/>
                  </td>
              </tr>
        </table>
        </td>
    </tr>
    </html:form>
    <tr><td colspan=5><hr></td></tr>

<%      String  sqlSelection=(String)pageContext.getAttribute("full_sql");
        if(sqlSelection==null) {
            sqlSelection=new String(" 1 ");
        } else {
            sqlSelection=" ("  + sqlSelection + ") ";
        }
        String sqlPrefix="SELECT distinct cust.customer_id, cust.gender, cust.firstname, cust.lastname, cust.email FROM customer_" + AgnUtils.getCompanyID(request) + "_tbl cust";

        String sqlStatement=" WHERE "+sqlSelection;

        boolean addBindingQuery=false;
        if(sqlSelection.indexOf("bind.")!=-1) {
            addBindingQuery=true;
        }

        String es = (String)(pageContext.getRequest().getParameter("user_type"));
        if((es != null) && (es.compareTo("E") != 0 )) {
            sqlStatement+= " AND bind.USER_TYPE ='" ;
            sqlStatement+= es;
            sqlStatement+="'";
            addBindingQuery=true;
        }

        int er=0;
        es = (String)(pageContext.getRequest().getParameter("user_status"));
        if (es != null) { 
            try {
                er = Integer.parseInt(es);
            } catch (Exception e) {
                er=0;
            }
            if(er != 0) {
                sqlStatement+= " AND bind.user_status =";
                sqlStatement+= er;
                addBindingQuery=true;
            }
        }
        if(mailingListID!=0) {
            sqlStatement+=" AND bind.mailinglist_id=";
            sqlStatement+=mailingListID;
            addBindingQuery=true;
        }
        if(targetRep.generateSQL().indexOf("bind.")!=-1) {
            addBindingQuery=true;
        }
        
        if(addBindingQuery) {
            sqlStatement+=" AND cust.customer_id=bind.customer_id";
            sqlPrefix+=", customer_" + AgnUtils.getCompanyID(request) + "_binding_tbl bind ";
        }
        sqlStatement=sqlPrefix+sqlStatement;
        if(targetRep.generateSQL().length() > 0) {
            sqlStatement+=" AND "+targetRep.generateSQL();
        }
        sqlStatement=sqlStatement.replaceAll("cust[.]bind", "bind");
%>

              <agn:ShowTable id="agntbl1" sqlStatement="<%= sqlStatement %>" startOffset="<%= request.getParameter("startWith") %>" maxRows="50">
                <tr>
                    <td><bean:message key="<%= new String("gender."+(String)pageContext.getAttribute("_agntbl1_gender")+".short") %>" />&nbsp;</td>
                    <td><%= pageContext.getAttribute("_agntbl1_firstname") %>&nbsp;</td>
                    <td><%= pageContext.getAttribute("_agntbl1_lastname") %>&nbsp;</td>
                    <td><agn:ShowByPermission token="recipient.view">
                            <a href="<html:rewrite page="<%= new String("/recipient.do?action=" + RecipientAction.ACTION_VIEW + "&recipientID=" + pageContext.getAttribute("_agntbl1_customer_id") + "&listID=" + mailingListID + "&user_type=" + user_type + "&user_status=" + user_status) %>"/>">
                        </agn:ShowByPermission>
                        <%= pageContext.getAttribute("_agntbl1_email") %>&nbsp;&nbsp;

                        <agn:ShowByPermission token="recipient.view">
                            </a>
                        </agn:ShowByPermission>    </td> 

                    <td>
                        <agn:ShowByPermission token="recipient.delete">
                                <html:link page="<%= new String("/recipient.do?action=" + RecipientAction.ACTION_CONFIRM_DELETE +"&recipientID=" + pageContext.getAttribute("_agntbl1_customer_id") + "&listID=" + mailingListID + "&user_type=" + user_type + "&user_status=" + user_status) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>delete.gif" alt="<bean:message key="Delete"/>" border="0"></html:link>
                        </agn:ShowByPermission>
                        <agn:ShowByPermission token="recipient.view">
                                <html:link page="<%= new String("/recipient.do?action=" + RecipientAction.ACTION_VIEW + "&recipientID=" + pageContext.getAttribute("_agntbl1_customer_id") + "&listID=" + mailingListID + "&user_type=" + user_type + "&user_status=" + user_status) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>bearbeiten.gif" alt="<bean:message key="Edit"/>" border="0"></html:link>
                        </agn:ShowByPermission>   </td>
                </tr>
              </agn:ShowTable>
              <tr><td colspan="5"><hr size="1"></td></tr>
              <!-- Multi-Page Indizes -->
                <tr><td colspan="5"><center>
                     <agn:ShowTableOffset id="agntbl1" maxPages="19">
                        <html:link page="<%= new String("/recipient.do?action=" + RecipientAction.ACTION_LIST + "&listID=" + mailingListID + "&startWith=" + startWith + "&user_type=" + user_type + "&user_status=" + user_status) %>">
                        <% if(activePage!=null) { %>
                            <span class="activenumber">&nbsp;
                        <% } %>
                        <%= pageNum %>
                        <% if(activePage!=null) { %>
                            &nbsp;</span>
                        <% } %>
                        </html:link>&nbsp;
                     </agn:ShowTableOffset></center></td></tr>
                         

              </table>
<%@include file="/footer.jsp"%>
