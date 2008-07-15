<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, java.util.*" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<agn:CheckLogon/>

<agn:Permission token="wizard.export"/>

<% String tmpShortname=new String("");
   if(session.getAttribute("exportWizardForm")!=null) {
      tmpShortname=((ExportWizardForm)session.getAttribute("exportWizardForm")).getShortname();
      //aForm=(CouponSeriesForm)session.getAttribute("couponSeriesForm");
   }
%>


<% pageContext.setAttribute("sidemenu_active", new String("Recipient")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("Export")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Export")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Export")); %>
<% if( request.getParameter("exportPredefID").toString().compareTo("0")!=0) { %>
<% pageContext.setAttribute("agnSubtitleValue", tmpShortname); %>
<% } %>
<% pageContext.setAttribute("agnNavigationKey", new String("subscriber_export")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("ExportWizard")); %>

<%@include file="/header.jsp"%>
<html:errors/>

  <html:form action="/exportwizard">
      <html:hidden property="action"/>
      <html:hidden property="exportPredefID"/>
<!--
      <logic:notEqual name="exportWizardForm" property="exportPredefID" value="0">
          <table border="0" cellspacing="0" cellpadding="0" width="100%">              
              <tr> 
                  <td><bean:message key="Name"/>:&nbsp;</td>
                  <td> 
                      <html:text property="shortname" maxlength="99" size="42"/>
                  </td>
              </tr>
              <tr> 
                  <td><bean:message key="Description"/>:&nbsp;</td>
                  <td> 
                      <html:textarea property="description" rows="5" cols="32"/>
                  </td>
              </tr>              
          </table> 
      </logic:notEqual>
-->
      <b><font color=#73A2D0><bean:message key="ExportWizStep_2_of_3"/></font></b>     
      <br>
      <table border="0" cellspacing="0" cellpadding="0" width="100%">
          <tr>
              <td colspan="3">
                  <span class="head3"><bean:message key="export.selection"/>:</span><br><br>
              </td>
          </tr>

          <tr>
              <td><b><bean:message key="Mailinglist"/>:</b><br>
                  <html:select property="mailinglistID" size="1">
                      <html:option value="0"><bean:message key="All_Mailinglists"/></html:option>
                      <agn:ShowTable id="agntbl2" sqlStatement="<%= new String("SELECT mailinglist_id, shortname FROM mailinglist_tbl WHERE company_id=" + AgnUtils.getCompanyID(request)+" ORDER BY mailinglist_id DESC") %>" maxRows="200">
                          <html:option value="<%= (String)(pageContext.getAttribute("_agntbl2_mailinglist_id")) %>"><%= pageContext.getAttribute("_agntbl2_shortname") %></html:option>
                      </agn:ShowTable>
                  </html:select>&nbsp;&nbsp;
              </td>

              <td><b><bean:message key="RecipientType"/>:</b><br>
                  <html:select property="userType" size="1">  <!-- usr type; 'E' for everybody -->
                      <html:option value="E"><bean:message key="All"/></html:option>
                      <html:option value="A"><bean:message key="Administrator"/></html:option>
                      <html:option value="T"><bean:message key="TestSubscriber"/></html:option>
                      <html:option value="W"><bean:message key="NormalSubscriber"/></html:option>
                  </html:select>&nbsp;&nbsp;
              </td>

              <td><b><bean:message key="RecipientStatus"/>:</b><br>
                  <html:select property="userStatus" size="1">  <!-- usr status; '0' is for everybody -->
                      <html:option value="0"><bean:message key="All"/></html:option>
                      <html:option value="1"><bean:message key="Active"/></html:option>
                      <html:option value="2"><bean:message key="Bounced"/></html:option>
                      <html:option value="3"><bean:message key="OptOutAdmin"/></html:option>
                      <html:option value="4"><bean:message key="OptOutUser"/></html:option>
                  </html:select>&nbsp;&nbsp;
              </td>  
          </tr>
          <tr>
              <td colspan="3"><br><b><bean:message key="Target"/>:</b>&nbsp;
                  <html:select property="targetID" size="1">
                      <html:option value="0"><bean:message key="All_Subscribers"/></html:option>
                      <agn:ShowTable id="agntbl3" sqlStatement="<%= "SELECT target_id, target_shortname FROM dyn_target_tbl WHERE company_id=" + AgnUtils.getCompanyID(request) + " ORDER BY target_shortname"%>" maxRows="200">
                          <html:option value="<%= (String)(pageContext.getAttribute("_agntbl3_target_id")) %>"><%= pageContext.getAttribute("_agntbl3_target_shortname") %></html:option>
                      </agn:ShowTable>
                  </html:select>
              </td>
          </tr>
          <tr><td colspan="3"><br><hr size="1"><br></td></tr>
      </table>

      <table border="0" cellspacing="0" cellpadding="0" width="100%">

          <tr><td colspan="2"><span class="head3"><bean:message key="export.columns"/>:<br></span></td></tr>
          <tr><td>&nbsp;</td></tr>
          <tr>
              <td><b><bean:message key="Column_Name"/>&nbsp;</b></td>
              <td><b><bean:message key="Type"/>&nbsp;</b></td>
          </tr>
          <tr><td colspan="2"><hr size="1"></td></tr>

          <agn:ShowColumnInfo id="agnTbl" table="<%= AgnUtils.getCompanyID(request) %>">
              <%
              String colName=(String) pageContext.getAttribute("_agnTbl_column_name");
              String colType=(String) pageContext.getAttribute("_agnTbl_data_type");
              %>
              <tr>
                  <td><html:multibox property="columns" value="<%= colName %>"/>&nbsp;<%= colName %>&nbsp;&nbsp;&nbsp;</td>
                  <% if( colType.toUpperCase().indexOf("CHAR") != -1 ) {%>
                  <td><bean:message key="alphanumeric"/>&nbsp;</td>
                  <% } else if( colType.toUpperCase().indexOf("NUMBER") != -1 ) { %>
                  <td><bean:message key="numeric"/>&nbsp;</td>
                  <% } else if( colType.toUpperCase().indexOf("TIME") != -1 ) { %>
                  <td><bean:message key="Date"/>&nbsp;</td>
                  <% } else if( colType.toUpperCase().indexOf("DATE") != -1 ) { %>
                  <td><bean:message key="Date"/>&nbsp;</td>
                  <% } else { %>
                  <td><%= colType %>&nbsp;</td>
                  <% } %>
              </tr>
          </agn:ShowColumnInfo>
          <tr>
              <td colspan="2"><br><b><bean:message key="export.add_mailinglist_information"/>:</b></td>
          </tr>
          <agn:ShowTable id="agnTbl" sqlStatement="<%= new String("SELECT mailinglist_id, shortname FROM mailinglist_tbl WHERE company_id=" + AgnUtils.getCompanyID(request) + " ORDER BY shortname")%>" maxRows="1000">
              <tr>
                  <td><html:multibox property="mailinglists" value="<%= (String)(pageContext.getAttribute("_agnTbl_mailinglist_id")) %>"/>&nbsp;<%= pageContext.getAttribute("_agnTbl_shortname") %>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;</td>
              </tr>
          </agn:ShowTable>

      </table>  

      <table border="0" cellspacing="0" cellpadding="0" width="100%">
          <tr><td colspan="3"><br><hr size="1"><br></td></tr>
          <tr>
              <td colspan=3>
                  <span class="head3"><bean:message key="export.file_format"/>:</span>
              </td>
          </tr>

          <tr>
              <td>
                  <br><b><bean:message key="Separator"/>:</b><br>
                  <html:select property="separator" size="1">
                      <html:option value=";"><bean:message key="separator.semicolon"/></html:option>
                      <html:option value=","><bean:message key="separator.comma"/></html:option>
                      <html:option value="|"><bean:message key="separator.pipe"/></html:option>
                      <html:option value="t"><bean:message key="separator.tab"/></html:option>
                  </html:select>
              </td>
              <td>
                  <br><b><bean:message key="Delimiter"/>:</b><br>
                  <html:select property="delimiter" size="1">
                    <html:option value="&#34;"><bean:message key="delimiter.doublequote"/></html:option>
                    <html:option value="'"><bean:message key="delimiter.singlequote"/></html:option>
                  </html:select>
              </td>
              <td>
                  <agn:ShowByPermission token="mailing.show.charsets">
                      <br><b><bean:message key="Charset"/>:</b><br>
                      <html:select property="charset" size="1">
                          <agn:ShowNavigation navigation="charsets" highlightKey="">
                              <agn:ShowByPermission token="<%= _navigation_token %>">
                                  <html:option value="<%= _navigation_href %>"><bean:message key="<%= _navigation_navMsg %>"/></html:option>
                              </agn:ShowByPermission>          
                          </agn:ShowNavigation>
                      </html:select>
                  </agn:ShowByPermission>
              </td>
          </tr>
      </table>
      <br><br>
      <html:link page="<%= "/exportwizard.do?action=" + ExportWizardAction.ACTION_LIST + "&exportPredefID=" + request.getParameter("exportPredefID")%>"><html:img src="button?msg=Back" border="0"/></html:link>
      &nbsp;&nbsp;
      <html:image src="button?msg=Proceed" border="0"/>
   
  </html:form>

<%@include file="/footer.jsp"%>
