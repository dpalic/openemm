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
 --%><%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, java.util.*, org.agnitas.beans.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>
<% MailingWizardForm aForm=null;
    aForm=(MailingWizardForm)session.getAttribute("mailingWizardForm");
    Mailing mailing=aForm.getMailing();
    DynamicTagContent tagContent=null;
    String index=null;
    int len=((DynamicTag)mailing.getDynTags().get(aForm.getDynName())).getDynContent().size();
    int i=0;
%>

<agn:Permission token="mailing.show"/>

<script type="text/javascript" src="fckeditor2.5/fckeditor.js"></script>

<script type="text/javascript">
    <!--
    var baseUrl=window.location.pathname;
    pos=baseUrl.lastIndexOf('/');
    baseUrl=baseUrl.substring(0, pos);
    -->
</script>

<%
    // mailing navigation:
    pageContext.setAttribute("sidemenu_active", new String("Mailings"));
    pageContext.setAttribute("sidemenu_sub_active", new String("New_Mailing"));
    pageContext.setAttribute("agnNavigationKey", new String("MailingWizard"));
    pageContext.setAttribute("agnHighlightKey", new String("MailingWizard"));
    pageContext.setAttribute("agnTitleKey", new String("Mailing"));
    pageContext.setAttribute("agnSubtitleKey", new String("Mailing"));
    pageContext.setAttribute("agnSubtitleValue", mailing.getShortname());
%>

<%@include file="/header.jsp"%>

<html:errors/>

<agn:HibernateQuery id="targets" query="<%= "from Target where companyID="+AgnUtils.getCompanyID(request) %>"/>

<html:form action="/mwTextmodule">
    <html:hidden property="action"/>
    
    <b><font color=#73A2D0><bean:message key="MWizardStep_8_of_11"/></font></b>

    <br>
    <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="400" height="10" border="0">
    <br>
    <br>

    <table border="0" cellspacing="0" cellpadding="0">
        <tr><td colspan="3"><span class="head3"><bean:message key="Text_Module"/>:&nbsp;<%= aForm.getDynName() %></span></td></tr>
        <logic:iterate id="dyncontent" name="mailingWizardForm" property="<%= "mailing.dynTags("+aForm.getDynName()+").dynContent" %>">
            <% Map.Entry ent2=(Map.Entry)pageContext.getAttribute("dyncontent");
                tagContent=(DynamicTagContent)ent2.getValue();
                index=(String)ent2.getKey(); %>
          
            <tr><td colspan="3"><a name="${dyncontent.getId()}"><hr size="1"></td></tr>
            
            <tr><td><bean:message key="Content"/>:&nbsp;<img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>edit.gif" border="0" onclick="editHtml<%= tagContent.getId() %>();" alt="<bean:message key="htmled.title"/>"></td>
            <td>
                <html:hidden property="<%= "content["+index+"].dynOrder" %>"/>
                <html:textarea property="<%= "content["+index+"].dynContent" %>" rows="20" cols="85"/>&nbsp;
            </td>
            <td>
                <% if(len>1 && i!=1) { %>
                <input type="image" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>button_up.gif" border="0" name="order" onclick="<%= "document.getElementById('contentform').action.value="+MailingContentAction.ACTION_CHANGE_ORDER_UP +";document.getElementById('contentform').contentID.value="+tagContent.getId() %>">
                <br>
                <% } %>
                <% if(len>1 && i!=len) { %>
                <input type="image" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>button_down.gif" border="0" name="order" onclick="<%= "document.getElementById('contentform').action.value="+MailingContentAction.ACTION_CHANGE_ORDER_DOWN +";document.getElementById('contentform').contentID.value="+tagContent.getId() %>">
                <% } i++; %>
            </td></tr>
            <tr><td colspan="3"><br></td></tr>
            <tr><td><bean:message key="Target"/>:&nbsp;</td><td colspan="2">
            
            <html:select property="<%= "content["+index+"].targetID" %>" size="1">
                <html:option value="0"><bean:message key="All_Subscribers"/></html:option>
		<logic:notEmpty name="__targets">
			<logic:iterate id="dbTarget" name="__targets">
				<html:option value="${dbTarget.getId()}">${dbTarget.getTargetName()}</html:option>
			</logic:iterate>
		</logic:notEmpty>
            </html:select></td></tr>
            <tr><td colspan="3"><br><br></td></tr>
            <tr><td colspan="3"><html:image src="button?msg=Save" border="0" property="save" onclick="document.mailingWizardForm.action.value='textmodule_save'"/>&nbsp;
            <html:image src="button?msg=Delete" border="0" property="delete" onclick="<%= "document.getElementById('contentform').action.value="+MailingContentAction.ACTION_DELETE_TEXTBLOCK +";document.getElementById('contentform').contentID.value="+tagContent.getId() %>"/>
        </logic:iterate>
        <tr><td colspan="3"><a name="0"><hr size="1"><span class="head3"><bean:message key="New_Content"/></span></a><br></td></tr>
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
        oFCKeditorNew.Config[ "BaseHref" ] = baseUrl+"/fckeditor2.5/" ;
        oFCKeditorNew.Config[ "CustomConfigurationsPath" ] = "<html:rewrite page="<%= new String("/fckeditor2.5/emmconfig.jsp?mailingID="+mailing.getId()) %>"/>" ;
        oFCKeditorNew.ToolbarSet = "emm" ;
        oFCKeditorNew.BasePath = baseUrl+"/fckeditor2.5/" ;
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
       
       
       
       
        <tr><td><bean:message key="Content"/>:&nbsp;<img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>edit.gif" border="0" onclick="Toggle();" alt="<bean:message key="htmled.title"/>"></td>
        <td>
            <div id="Textarea">
        		<html:textarea property="newContent" styleId="newContent" rows="20" cols="85"/>&nbsp;
        	</div>
        	<div id="FCKeditor" style="display: none">
        		<textarea  id="DataFCKeditor" rows="20" cols="85"></textarea>
        	</div>
        </td>
        <td><br></td></tr>
        <tr><td colspan="3"><br></td></tr>
        <tr><td><bean:message key="Target"/>:&nbsp;</td><td colspan="2">
        <html:select property="targetID" size="1">
            <html:option value="0"><bean:message key="All_Subscribers"/></html:option>
            <logic:iterate id="dbTarget" name="__targets">
                <html:option value="${dbTarget.getId()}">${dbTarget.getTargetName()}</html:option>
            </logic:iterate>
        </html:select></td></tr>
        <tr><td colspan="3"><br><br></td></tr>
        <tr><td colspan="3"><html:image src="button?msg=Add" border="0" property="insert" onclick="<%= "save();document.mailingWizardForm.action.value='" + MailingWizardAction.ACTION_TEXTMODULE_ADD + "'" %>"/>
    </table>
    
    <br>
    <br> 

    <% // wizard navigation: %>
    <br>
    <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td>&nbsp;</td>
            <td align="right">
                &nbsp;
                <html:image src="button?msg=Back"  border="0" onclick="<%= "document.mailingWizardForm.action.value='previous'" %>"/>
                &nbsp;
                <html:image src="button?msg=Proceed"  onclick="<%= "document.mailingWizardForm.action.value='" + MailingWizardAction.ACTION_TEXTMODULE + "'" %>"/>
                &nbsp;
                <html:image src="button?msg=Skip" border="0" onclick="<%= "document.mailingWizardForm.action.value='skip'" %>"/>
                &nbsp;
                <html:image src="button?msg=Finish" border="0" onclick="<%= "document.mailingWizardForm.action.value='" + MailingWizardAction.ACTION_FINISH + "'" %>"/>
                &nbsp;
            </td>
        </tr>
    </table>             

    
</html:form>
<%@include file="/footer.jsp"%>
