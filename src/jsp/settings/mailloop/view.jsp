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
 --%><%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*, java.text.*, java.util.*" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="mailing.show"/>

<% int tmpLoopID=0;
   String tmpShortname=new String("");
   MailloopForm aForm=null;
   
   if(request.getAttribute("mailloopForm")!=null) {
      tmpLoopID=((MailloopForm)request.getAttribute("mailloopForm")).getMailloopID();
      tmpShortname=((MailloopForm)request.getAttribute("mailloopForm")).getShortname();

      aForm=(MailloopForm)request.getAttribute("mailloopForm");
   }
   Locale aLocale=(Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY);
%>

<% pageContext.setAttribute("sidemenu_active", new String("Settings")); %>


<% if(tmpLoopID!=0) { %>

<% pageContext.setAttribute("agnTitleKey", new String("Mailloop")); %>    
<% pageContext.setAttribute("agnSubtitleKey", new String("Mailloop")); %>
<% pageContext.setAttribute("agnSubtitleValue", tmpShortname); %>
 
<% } else { %>
<% pageContext.setAttribute("agnTitleKey", new String("NewMailloop")); %>   
<% pageContext.setAttribute("agnSubtitleKey", new String("NewMailloop")); %>

<% } %>

<% pageContext.setAttribute("sidemenu_sub_active", new String("Mailloops"));  %>
<% pageContext.setAttribute("agnNavigationKey", new String("Mailloops")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("NewMailloop")); %>


<% pageContext.setAttribute("agnNavHrefAppend", new String("&mailloopID="+tmpLoopID)); %>

<%@include file="/header.jsp"%>
<html:errors/>

<script type="text/javascript" src="fckeditor/fckeditor.js"></script>

<script type="text/javascript">
<!--
   var baseUrl=window.location.pathname;
   pos=baseUrl.lastIndexOf('/');
   baseUrl=baseUrl.substring(0, pos);
-->
</script>

  <html:form action="/mailloop">
      <html:hidden property="action"/>
      <html:hidden property="mailloopID"/>
      <table border="0" cellspacing="0" cellpadding="0">

     

    
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
          
          <tr> 
              <td colspan="2"> 
                  <html:checkbox property="doForward"><bean:message key="mailloop.forward"/></html:checkbox>
              </td>
          </tr>

          <tr> 
              <td><bean:message key="mailloop.forward_adr"/>:&nbsp;</td>
              <td> 
                  <html:text property="forwardEmail" maxlength="99" size="42"/>
              </td>
          </tr>

          <tr> 
              <td colspan="2"> 
                  <html:checkbox property="doAutoresponder"><bean:message key="mailloop.autoresponder"/></html:checkbox>
              </td>
          </tr>

          <tr> 
              <td><bean:message key="mailloop.ar_sender"/>:&nbsp;</td>
              <td> 
                  <html:text property="arSender" maxlength="99" size="42"/>
              </td>
          </tr>
          
          <tr> 
              <td><bean:message key="mailloop.ar_subject"/>:&nbsp;</td>
              <td> 
                  <html:text property="arSubject" maxlength="99" size="42"/>
              </td>
          </tr>
          
          <tr> 
              <td><bean:message key="Text_Version"/>:&nbsp;</td>
              <td> 
                  <html:textarea property="arText" rows="14" cols="75"/>
              </td>
          </tr>
  
            <script type="text/javascript">
                <!--
                   var oFCKeditorHtml=null;
                   function editHtmlHtml() {
                     if(oFCKeditorHtml==null) {
                       oFCKeditorHtml = new FCKeditor( 'arHtml' ) ;
                       oFCKeditorHtml.Config[ "AutoDetectLanguage" ] = false ;
                       oFCKeditorHtml.Config[ "DefaultLanguage" ] = "<%= aLocale.getLanguage() %>" ;
                       oFCKeditorHtml.Config[ "BaseHref" ] = baseUrl+"/fckeditor/" ;
                       oFCKeditorHtml.Config[ "CustomConfigurationsPath" ] = "<html:rewrite page="/fckeditor/emmconfig.jsp?mailingID=0"/>" ;
                       oFCKeditorHtml.ToolbarSet = "emm" ;
                       oFCKeditorHtml.BasePath = baseUrl+"/fckeditor/" ;
                       oFCKeditorHtml.Height = "400" ; // 400 pixels
                       oFCKeditorHtml.Width = 650 ; // 400 pixels
                       oFCKeditorHtml.ReplaceTextarea();
                     }
                     return true;
                   }
                  //-->
                  </script>
               
          <tr> 
              <td><bean:message key="HTML_Version"/>:&nbsp;</td>
              <td>
                  <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>edit.gif" border="0" onclick="editHtmlHtml();" alt="<bean:message key="htmled.title"/>"><br>
                  <html:textarea property="arHtml" rows="14" cols="75"/>
              </td>
          </tr>
 
          <tr>
              <td colspan=2>

                  <html:image src="button?msg=Save" border="0" property="save" value="save"/>&nbsp;
              
              
                  <logic:notEqual name="mailloopForm" property="mailloopID" value="0">
                      <html:link page="<%= new String("/mailloop.do?action=" + MailloopAction.ACTION_CONFIRM_DELETE) + "&mailloopID=" + tmpLoopID%>"><html:img src="button?msg=Delete" border="0"/></html:link>    
                  </logic:notEqual>
              

              </td>
          </tr>
      </table>
  </html:form>
<%@include file="/footer.jsp"%>
