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
 --%><%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*,org.agnitas.web.forms.*, java.util.*, org.agnitas.beans.*, org.agnitas.cms.utils.CmsUtils" contentType="text/html; charset=utf-8" buffer="32kb" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<% pageContext.setAttribute("FCKEDITOR_PATH", AgnUtils.getEMMProperty("fckpath")); %>

<agn:CheckLogon/>

<% int tmpMailingID=0;
   MailingBaseForm aForm=null;
   String tmpShortname=new String(""); 
   if((aForm=(MailingBaseForm)session.getAttribute("mailingBaseForm"))!=null) {
      tmpMailingID=((MailingBaseForm)session.getAttribute("mailingBaseForm")).getMailingID();
      tmpShortname=((MailingBaseForm)session.getAttribute("mailingBaseForm")).getShortname();
   }
   if(aForm.isIsTemplate()) {
       aForm.setShowTemplate(true);
   }
   String permToken=null;
   
   Locale aLocale=(Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY);
%>

<% if(aForm.isIsTemplate()) { %>
<agn:Permission token="template.show"/>
<% } else { %>
<agn:Permission token="mailing.show"/>
<% } %>

<logic:equal name="mailingBaseForm" property="isTemplate" value="true">
<% // template navigation:
  pageContext.setAttribute("sidemenu_active", new String("Templates")); 
  if(tmpMailingID!=0) {
     pageContext.setAttribute("sidemenu_sub_active", new String("none"));
     pageContext.setAttribute("agnNavigationKey", new String("templateView"));
     pageContext.setAttribute("agnHighlightKey", new String("Template"));
     pageContext.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
     pageContext.setAttribute("agnSubtitleValue", tmpShortname);
  } else {
     pageContext.setAttribute("sidemenu_sub_active", new String("New_Template"));
     pageContext.setAttribute("agnNavigationKey", new String("TemplateNew"));
     pageContext.setAttribute("agnHighlightKey", new String("New_Template"));
  }
  pageContext.setAttribute("agnTitleKey", new String("Template")); 
  pageContext.setAttribute("agnSubtitleKey", new String("Template")); 
%>
</logic:equal>

<logic:equal name="mailingBaseForm" property="isTemplate" value="false">
<%
// mailing navigation:
   pageContext.setAttribute("sidemenu_active", new String("Mailings")); 
    if(tmpMailingID!=0) {
        pageContext.setAttribute("sidemenu_sub_active", new String("none"));
        pageContext.setAttribute("agnNavigationKey", new String("mailingView"));
        pageContext.setAttribute("agnHighlightKey", new String("Mailing"));
        pageContext.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
        pageContext.setAttribute("agnSubtitleValue", tmpShortname);
    } else {
        pageContext.setAttribute("sidemenu_sub_active", new String("New_Mailing"));
        pageContext.setAttribute("agnNavigationKey", new String("MailingNew"));
        pageContext.setAttribute("agnHighlightKey", new String("New_Mailing"));
    }
    pageContext.setAttribute("agnTitleKey", new String("Mailing")); 
    pageContext.setAttribute("agnSubtitleKey", new String("Mailing")); 
%>
</logic:equal>


<%@include file="/header.jsp"%>

<script type="text/javascript" src="${FCKEDITOR_PATH}/fckeditor.js"></script>

<script type="text/javascript">
<!--
   var baseUrl=window.location.pathname;
   pos=baseUrl.lastIndexOf('/');
   baseUrl=baseUrl.substring(0, pos);
-->
</script>

<%@include file="/messages.jsp" %>

            <html:form action="/mailingbase" focus="shortname">
                <html:hidden property="mailingID"/>
                <html:hidden property="action"/>
                <html:hidden property="isTemplate"/>
                <html:hidden property="oldMailingID"/>
                <html:hidden property="copyFlag"/>
                <table border="0" cellspacing="0" cellpadding="0">
                <tr> 
                  <td><bean:message key="Name"/>:&nbsp;</td>
                  <td> 
                    <html:text property="shortname" maxlength="99" size="42"/>
                  </td>
                </tr>

         	<tr> 
                  <td><bean:message key="Description_opt"/>:&nbsp;</td>
                  <td> 
                    <html:textarea property="description" rows="5" cols="32"/>
                  </td>
                </tr>
                
                <tr><td colspan="2"><br><br></td></tr>
                
                <tr>
                   <td colspan="2">
                     <jsp:include page="/mailing/view_base_settings.jsp"/>
                  </td>
                </tr>
       
                <tr> 
                  <td colspan="2">
                      <table border="0" cellspacing="0" cellpadding="0">
                        <tr><td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="10" border="0"></td>
                            <td><table border="0" cellspacing="0" cellpadding="0">
                                  <tr>
                                                <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>tagwa_left.gif" border="0"></td>
                                                <td class="tag_active"><bean:message key="MediaType.Email"/></td>
                                                <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>tagwa_right.gif" border="0"></td>
                                            
                                                <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="10" border="0"></td>
                                </tr>
                            </table>
                        </td>
                        <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="10" border="0"></td>
                    </tr>
                    <tr>
                        <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>frame_01.gif" width="10" height="10" border="0"></td>
                        <td background="<bean:write name="emm.layout" property="baseUrl" scope="session"/>border_02.gif"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="10" border="0"></td>
                        <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>frame_03.gif" width="10" height="10" border="0"></td>
                    </tr>
                    <tr>
                        <td background="<bean:write name="emm.layout" property="baseUrl" scope="session"/>border_04.gif"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="10" border="0"></td>
                        <td>

<!-- E-Mail-Settings -->

                <table border="0" cellspacing="0" cellpadding="0">

                <tr> 
                  <td><bean:message key="Subject"/>:&nbsp;</td>
                  <td> 
                    <html:text property="emailSubject" maxlength="199" size="42"/>
                  </td>
                </tr>
                
                <tr> 
                  <td><bean:message key="SenderEmail"/>:&nbsp;</td>
                  <td> 
                    <html:text property="media[0].fromEmail" maxlength="99" size="42"/>
                  </td>
                </tr>
                <tr> 
                  <td><bean:message key="SenderFullname"/>:&nbsp;</td>
                  <td> 
                    <html:text property="media[0].fromFullname" maxlength="99" size="42"/>
                  </td>
                </tr>
                <tr> 
				  <td><bean:message key="ReplyEmail"/>:&nbsp;</td>
				  <td>
				  	<html:text property="media[0].replyEmail" maxlength="99" size="42"/>
				  </td>
				</tr>
                <tr> 
                  <td><bean:message key="ReplyFullName"/>:&nbsp;</td>
                  <td> 
                    <html:text property="media[0].replyFullname" maxlength="99" size="42"/>
                  </td>
                </tr>
               
                <agn:ShowByPermission token="mailing.show.charsets">
                <tr>
                <td><bean:message key="Charset"/>:&nbsp;</td>
                  <td>
                    <html:select property="emailCharset" size="1">
                       <agn:ShowNavigation navigation="charsets" highlightKey="">
                          <agn:ShowByPermission token="<%= _navigation_token %>">
                             <html:option value="<%= _navigation_href %>"><bean:message key="<%= _navigation_navMsg %>"/></html:option>
                          </agn:ShowByPermission>          
                       </agn:ShowNavigation>
                    </html:select>
                  </td>
                </tr>
                </agn:ShowByPermission>
                <tr> 
                  <td><bean:message key="Linefeed_After"/>:&nbsp;</td>
                  <td> 
                    <html:select property="emailLinefeed" size="1">
                    <html:option value="0"><bean:message key="No_Linefeed"/></html:option>
                    <%
                        int a;
                        for(a=60; a<=80; a++) { %>
                            <html:option value="<%= Integer.toString(a) %>"><%= a %> <bean:message key="Characters"/></html:option>
                        <% }
                    %>
                    </html:select>
                  </td>
                  </tr>
                  <tr>
                  <td><bean:message key="Format"/>:&nbsp;</td>
                  <td> 
                    <html:select property="mediaEmail.mailFormat" size="1">
                        <html:option value="0"><bean:message key="only_Text"/></html:option>
                        <html:option value="1"><bean:message key="Text_HTML"/></html:option>
                        <html:option value="2"><bean:message key="Text_HTML_OfflineHTML"/></html:option>
                    </html:select>
                  </td>
                </tr>
                
                <tr>
                  <td><bean:message key="openrate.measure"/>:&nbsp;</td>
                  <td> 
                    <html:select property="emailOnepixel" size="1">
                        <html:option value="<%= MediatypeEmail.ONEPIXEL_TOP %>"><bean:message key="openrate.top"/></html:option>
                        <html:option value="<%= MediatypeEmail.ONEPIXEL_BOTTOM %>"><bean:message key="openrate.bottom"/></html:option>
                        <html:option value="<%= MediatypeEmail.ONEPIXEL_NONE %>"><bean:message key="openrate.none"/></html:option>
                    </html:select>
                  </td>
                </tr>


                <tr> 
                  <td colspan="2">&nbsp;</td>
                </tr>

<agn:ShowByPermission token="template.show">
                
<% if(aForm.isShowTemplate() == false) { %>
    <% if(!(CmsUtils.isCmsMailing(aForm.getMailingID(), aForm.getWebApplicationContext()) && aForm.getMailingID() != 0)) {%>
                <tr> 
                  <td colspan="2"><html:link page="<%= new String("/mailingbase.do?action=" + MailingBaseAction.ACTION_VIEW_WITHOUT_LOAD + "&mailingID=" + tmpMailingID + "&showTemplate=true")%>"><bean:message key="ShowTemplate"/>&nbsp;&gt;&gt;&gt;</html:link></td>
                </tr>
    <% } %>
<% } else {%>
    <% if(!(CmsUtils.isCmsMailing(aForm.getMailingID(), aForm.getWebApplicationContext()) && aForm.getMailingID() != 0)) {%>
            <% if(!aForm.isIsTemplate()) { %>
                <tr> 
                  <td colspan="2"><html:link page="<%= new String("/mailingbase.do?action=" + MailingBaseAction.ACTION_VIEW_WITHOUT_LOAD + "&mailingID=" + tmpMailingID + "&showTemplate=false")%>">&lt;&lt;&lt;&nbsp;<bean:message key="HideTemplate"/></html:link><br><br></td>
                </tr>
            <% } %>
                <tr> 
                  <td colspan="2"><b><bean:message key="Text_Version"/>:</b><br>
                    <html:textarea property="textTemplate" rows="14" cols="75"/>
                  </td>
                </tr>
    <% } %>
            <script type="text/javascript">
                  
            var isFCKEditorActive=false;
        function Toggle()
		{
				// Try to get the FCKeditor instance, if available.
			var oEditor ;
			if ( typeof( FCKeditorAPI ) != 'undefined' )
				oEditor = FCKeditorAPI.GetInstance( 'DataFCKeditor' ) ;

			// Get the _Textarea and _FCKeditor DIVs.
			var eTextareaDiv	= document.getElementById( 'Textarea' ) ;
			var eFCKeditorDiv	= document.getElementById( 'FCKeditor' ) ;

			// If the _Textarea DIV is visible, switch to FCKeditor.
			if ( eTextareaDiv.style.display != 'none' )
			{
			// If it is the first time, create the editor.
			if ( !oEditor )
			{
				CreateEditor() ;
			}
			else
			{
				// Set the current text in the textarea to the editor.
				oEditor.SetData( document.getElementById('newContent').value ) ;
			}

			// Switch the DIVs display.
			eTextareaDiv.style.display = 'none' ;
			eFCKeditorDiv.style.display = '' ;

			// This is a hack for Gecko 1.0.x ... it stops editing when the editor is hidden.
			if ( oEditor && !document.all )
			{
				if ( oEditor.EditMode == FCK_EDITMODE_WYSIWYG )
				oEditor.MakeEditable() ;
			}
			isFCKEditorActive=true;
		}
		else
		{
			// Set the textarea value to the editor value.
			document.getElementById('newContent').value = oEditor.GetXHTML() ;

			// Switch the DIVs display.
			eTextareaDiv.style.display = '' ;
			eFCKeditorDiv.style.display = 'none' ;
			isFCKEditorActive=false;
		}
	}

	function CreateEditor()
	{
		// Copy the value of the current textarea, to the textarea that will be used by the editor.
		document.getElementById('DataFCKeditor').value = document.getElementById('newContent').value ;

		// Automatically calculates the editor base path based on the _samples directory.
		// This is usefull only for these samples. A real application should use something like this:
		// oFCKeditor.BasePath = '/fckeditor/' ;	// '/fckeditor/' is the default value.
	
		// Create an instance of FCKeditor (using the target textarea as the name).
		
		oFCKeditorNew = new FCKeditor( 'DataFCKeditor' ) ;
        oFCKeditorNew.Config[ "AutoDetectLanguage" ] = false ;
        oFCKeditorNew.Config[ "DefaultLanguage" ] = "<%= ((Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)).getLanguage() %>" ;
        oFCKeditorNew.Config[ "BaseHref" ] = baseUrl+"/${FCKEDITOR_PATH}/" ;
        oFCKeditorNew.Config[ "CustomConfigurationsPath" ] = "<html:rewrite page="<%= new String("/"+ AgnUtils.getEMMProperty("fckpath") +"/emmconfig.jsp?mailingID="+tmpMailingID) %>"/>" ;
        oFCKeditorNew.ToolbarSet = "emm" ;
        oFCKeditorNew.BasePath = baseUrl+"/${FCKEDITOR_PATH}/" ;
        oFCKeditorNew.Height = "400" ; // 400 pixels
        oFCKeditorNew.Width = "650" ;
        oFCKeditorNew.ReplaceTextarea();
	}

	// The FCKeditor_OnComplete function is a special function called everytime an
	// editor instance is completely loaded and available for API interactions.
	function FCKeditor_OnComplete( editorInstance )
	{
		// Switch Image ??
	}

	function PrepareSave()
	{
		// If the textarea isn't visible update the content from the editor.
		if ( document.getElementById( 'Textarea' ).style.display == 'none' )
		{
			var oEditor = FCKeditorAPI.GetInstance( 'DataFCKeditor' ) ;
			document.getElementById( 'newContent' ).value = oEditor.GetXHTML() ;
		}
		
	}
	function save() {
		if(isFCKEditorActive== true)  {
			var oEditor = FCKeditorAPI.GetInstance( 'DataFCKeditor' ) ;
			document.getElementById('newContent').value = oEditor.GetXHTML() ;
		}
	}
   </script>
    <% if(!(CmsUtils.isCmsMailing(aForm.getMailingID(), aForm.getWebApplicationContext()) && aForm.getMailingID() != 0)) {%>
                <tr>
                  <td colspan="2"><br><b><bean:message key="HTML_Version"/>:</b>&nbsp;<img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>edit.gif" border="0" onclick="Toggle();" alt="<bean:message key="htmled.title"/>"><br>
                    <div id="Textarea">
        					<html:textarea property="htmlTemplate" styleId="newContent" rows="14" cols="75"/>&nbsp;
        			</div>
        			<div id="FCKeditor" style="display: none">
        				<textarea  id="DataFCKeditor" rows="14" cols="75"></textarea>
        			</div>             
                  </td>
                </tr>
    <% } %>
<% } %>
</agn:ShowByPermission>
                </table>


                                        </td>
                                        <td background="<bean:write name="emm.layout" property="baseUrl" scope="session"/>border_06.gif"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="10" border="0"></td>
                                    </tr>
                                    <tr>
                                        <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>frame_07.gif" width="10" height="10" border="0"></td>
                                        <td background="<bean:write name="emm.layout" property="baseUrl" scope="session"/>border_08.gif"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="10" border="0"></td>
                                        <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>frame_09.gif" width="10" height="10" border="0"></td>
                                    </tr>
                                </table>
                            </td>
                        </tr>

                    </table>
                <p>
                <% if(aForm.isIsTemplate()) {
                   permToken="template.change";
                } else { 
                   permToken="mailing.change";
                } %>
                <agn:ShowByPermission token="<%= permToken %>">
                   <logic:equal name="mailingBaseForm" property="isTemplate" value="true">
                        <html:image src="button?msg=Save" border="0" property="save" value="save" onclick="save();"/>
                   </logic:equal>
                   <logic:equal name="mailingBaseForm" property="isTemplate" value="false">
                        <logic:equal value="false" name="mailingBaseForm" property="worldMailingSend">
                  <html:image src="button?msg=Save" border="0" property="save" value="save" onclick="save();"/>
                        </logic:equal>
                   </logic:equal>
                </agn:ShowByPermission>
                <% if(tmpMailingID!=0) { %>

                <agn:ShowByPermission token="mailing.copy">   
                  <html:link page="<%= new String("/mailingbase.do?action=" + MailingBaseAction.ACTION_CLONE_AS_MAILING + "&mailingID=" + tmpMailingID) %>"><html:img src="button?msg=Copy" border="0"/></html:link>
                </agn:ShowByPermission>
                <% } %>
                <% if(aForm.isIsTemplate()) {
                   permToken="template.delete";
                } else { 
                   permToken="mailing.delete";
                } %>
                <agn:ShowByPermission token="<%= permToken %>">
                  <html:link page="<%= new String("/mailingbase.do?action=" + MailingBaseAction.ACTION_CONFIRM_DELETE + "&previousAction=" + MailingBaseAction.ACTION_VIEW + "&mailingID=" + tmpMailingID) %>"><html:img src="button?msg=Delete" border="0"/></html:link>
                </agn:ShowByPermission>
                
              </p>



</html:form>

              
<%@include file="/footer.jsp"%>
