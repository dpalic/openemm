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
 --%><%@ page language="java" import="org.agnitas.util.*, java.util.*, org.agnitas.web.*, org.agnitas.target.*, org.agnitas.beans.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<% pageContext.setAttribute("FCKEDITOR_PATH", AgnUtils.getEMMProperty("fckpath")); %>

<agn:CheckLogon/>

<agn:Permission token="mailing.content.show"/>

<script type="text/javascript" src="${FCKEDITOR_PATH}/fckeditor.js"></script>

<script type="text/javascript">
    <!--
    var baseUrl=window.location.pathname;
    pos=baseUrl.lastIndexOf('/');
    baseUrl=baseUrl.substring(0, pos);
    -->
</script>

<% int tmpMailingID=0;
    String tmpShortname=new String("");
    MailingContentForm aForm=null;
    if(session.getAttribute("mailingContentForm")!=null) {
        aForm=(MailingContentForm)session.getAttribute("mailingContentForm");
        tmpMailingID=aForm.getMailingID();
        tmpShortname=aForm.getShortname();
    }
    DynamicTagContent tagContent=null;
    String index=null;
    int i=1;
    int len=aForm.getContent().size();
%>

<logic:equal name="mailingContentForm" property="isTemplate" value="true">
    <% // template navigation:
        pageContext.setAttribute("sidemenu_active", new String("Templates"));
        pageContext.setAttribute("sidemenu_sub_active", new String("none"));
        pageContext.setAttribute("agnNavigationKey", new String("templateView"));
        pageContext.setAttribute("agnHighlightKey", new String("Content"));
        pageContext.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
        pageContext.setAttribute("agnTitleKey", new String("Template"));
        pageContext.setAttribute("agnSubtitleKey", new String("Template"));
        pageContext.setAttribute("agnSubtitleValue", tmpShortname);
    %>
</logic:equal>

<logic:equal name="mailingContentForm" property="isTemplate" value="false">
    <%
        // mailing navigation:
        pageContext.setAttribute("sidemenu_active", new String("Mailings"));
        pageContext.setAttribute("sidemenu_sub_active", new String("none"));
        pageContext.setAttribute("agnNavigationKey", new String("mailingView"));
        pageContext.setAttribute("agnHighlightKey", new String("Content"));
        pageContext.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
        pageContext.setAttribute("agnTitleKey", new String("Mailing"));
        pageContext.setAttribute("agnSubtitleKey", new String("Mailing"));
        pageContext.setAttribute("agnSubtitleValue", tmpShortname);
    %>
</logic:equal>

<%@include file="/header.jsp"%>
<%@include file="/messages.jsp" %>

<html:form action="/mailingcontent" styleId="contentform">
    <html:hidden property="dynNameID"/>
    <html:hidden property="action"/>
    <html:hidden property="mailingID"/>
    <html:hidden property="contentID"/>
    <table border="0" cellspacing="0" cellpadding="0">
        <tr><td colspan="3"><span class="head3"><bean:message key="Text_Module"/>:&nbsp;<%= aForm.getDynName() %></span></td></tr>
       
        <logic:iterate id="dyncontent" name="mailingContentForm" property="content">
            <% Map.Entry ent2=(Map.Entry)pageContext.getAttribute("dyncontent");
            tagContent=(DynamicTagContent)ent2.getValue();
            index=(String)ent2.getKey(); 
            pageContext.setAttribute("index",index);
           %>
            <script type="text/javascript">
                
        	// have a look @ sample13.html from the fckeditor docs
	        var isFCKEditorActive<%= tagContent.getId() %> = false;                    
		
		function Toggle<%= tagContent.getId() %>()
		{
				// Try to get the FCKeditor instance, if available.
			var oEditor;	
			if ( typeof( FCKeditorAPI ) != 'undefined' )
				oEditor = FCKeditorAPI.GetInstance( 'DataFCKeditor<%= tagContent.getId() %>' ) ;

			// Get the _Textarea and _FCKeditor DIVs.
			var eTextareaDiv	= document.getElementById( 'Textarea<%= tagContent.getId() %>' ) ;
			var eFCKeditorDiv	= document.getElementById( 'FCKeditor<%= tagContent.getId() %>' ) ;

			// If the _Textarea DIV is visible, switch to FCKeditor.
			if ( eTextareaDiv.style.display != 'none' )
			{
			// If it is the first time, create the editor.
			if ( !oEditor )
			{
				CreateEditor<%= tagContent.getId() %>() ;
			}
			else
			{
				// Set the current text in the textarea to the editor.
				oEditor.SetData( document.getElementById('content_<%= index %>_.dynContent').value ) ;
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
			
			isFCKEditorActive<%= tagContent.getId() %>=true;
		}
		else
		{
			// Set the textarea value to the editor value.
			document.getElementById('content_<%= index %>_.dynContent').value = oEditor.GetXHTML() ;

			// Switch the DIVs display.
			eTextareaDiv.style.display = '' ;
			eFCKeditorDiv.style.display = 'none' ;
			isFCKEditorActive<%= tagContent.getId() %>=false;
		}
	}

	function CreateEditor<%= tagContent.getId() %>()
	{
		// Copy the value of the current textarea, to the textarea that will be used by the editor.
		document.getElementById('DataFCKeditor<%= tagContent.getId() %>').value = document.getElementById('content_<%= index %>_.dynContent').value ;

		// Automatically calculates the editor base path based on the _samples directory.
		// This is usefull only for these samples. A real application should use something like this:
		// oFCKeditor.BasePath = '/fckeditor/' ;	// '/fckeditor/' is the default value.
	
		// Create an instance of FCKeditor (using the target textarea as the name).
		
		oFCKeditorNew = new FCKeditor( 'DataFCKeditor<%= tagContent.getId() %>' ) ;
        oFCKeditorNew.Config[ "AutoDetectLanguage" ] = false ;
        oFCKeditorNew.Config[ "DefaultLanguage" ] = "<%= ((Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)).getLanguage() %>" ;
        oFCKeditorNew.Config[ "BaseHref" ] = baseUrl+"/${FCKEDITOR_PATH}/" ;
        oFCKeditorNew.Config[ "CustomConfigurationsPath" ] = "<html:rewrite page="<%= new String("/"+AgnUtils.getEMMProperty("fckpath")+"/emmconfig.jsp?mailingID="+tmpMailingID) %>"/>" ;
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
	}

	function save<%= tagContent.getId() %>() {
		if(isFCKEditorActive<%= tagContent.getId() %>== true || document.getElementById( 'Textarea"+tagContent.getId()+"' ).style.display == 'none' ) {
			var oEditor =  FCKeditorAPI.GetInstance( 'DataFCKeditor<%= tagContent.getId() %>' ) ;
			document.getElementById('content_<%= index %>_.dynContent').value = oEditor.GetXHTML() ;
		}
	}
	
            </script>

            <tr><td colspan="3"><a name="<%= tagContent.getId() %>"><hr size="1"></td></tr>
            <c:if test="${! empty agnDBTagErrors[index] }">
            <tr>
            	<td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>warning.gif" border="0"></td>
            	<td><bean:message key="error.mailing.content.agnDBTag"/></td>
            </tr>
            <tr>            
            <td colspan="3">
                 	<c:forEach var="error" items="${agnDBTagErrors[index]}">
            		${error.invalidTag}<br>
            	</c:forEach>
            </td>
            </tr>
            </c:if>
            <tr><td><bean:message key="Content"/>:&nbsp;<img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>edit.gif" border="0" onclick="Toggle<%= tagContent.getId() %>();" alt="<bean:message key="htmled.title"/>"></td>
            
            <td>
              <div id="Textarea<%= tagContent.getId() %>" >
                <html:hidden property="<%= new String("content("+index+").dynOrder") %>"/>
                <html:textarea property="<%= "content("+index+").dynContent" %>" styleId="<%= "content_"+index+"_.dynContent" %>" rows="20" cols="85"/>&nbsp;
             </div>
              <div id="FCKeditor<%= tagContent.getId() %>" style="display: none">
        		<textarea  id="DataFCKeditor<%= tagContent.getId() %>" rows="20" cols="85"></textarea>
        	</div>
            </td>
            <td>
                <% if(len>1 && i!=1) { %>
                <input type="image" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>button_top.gif" border="0" name="order" onclick="<%= "document.getElementById('contentform').action.value="+MailingContentAction.ACTION_CHANGE_ORDER_TOP +";document.getElementById('contentform').contentID.value="+tagContent.getId() %>">
                <br>
                <input type="image" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>button_up.gif" border="0" name="order" onclick="<%= "document.getElementById('contentform').action.value="+MailingContentAction.ACTION_CHANGE_ORDER_UP +";document.getElementById('contentform').contentID.value="+tagContent.getId() %>">
                <br>
                <% } %>
                <% if(len>1 && i!=len) { %>
                <input type="image" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>button_down.gif" border="0" name="order" onclick="<%= "document.getElementById('contentform').action.value="+MailingContentAction.ACTION_CHANGE_ORDER_DOWN +";document.getElementById('contentform').contentID.value="+tagContent.getId() %>">
                <br>
                <input type="image" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>button_bottom.gif" border="0" name="order" onclick="<%= "document.getElementById('contentform').action.value="+MailingContentAction.ACTION_CHANGE_ORDER_BOTTOM +";document.getElementById('contentform').contentID.value="+tagContent.getId() %>">
                <% } i++; %>
            </td></tr>
            <tr><td colspan="3"><br></td></tr>
            <tr><td><bean:message key="Target"/>:&nbsp;</td><td colspan="2">
            
                <html:select property="<%= "content("+index+").targetID" %>" size="1">
                    <html:option value="0"><bean:message key="All_Subscribers"/></html:option>
                    <logic:iterate id="target" name="targetGroups" scope="request">
                        <html:option value="${target.getId()}">${target.getTargetName()}</html:option>
                    </logic:iterate>
                </html:select></td></tr>
            <tr><td colspan="3"><br><br></td></tr>
            <tr><td colspan="3">
              <logic:equal name="mailingContentForm" property="isTemplate" value="true">
                <html:image src="button?msg=Save" border="0" property="save" onclick="<%= "save"+tagContent.getId()+"();document.getElementById('contentform').submit()" %>"/>&nbsp;
                <html:image src="button?msg=Delete" border="0" property="delete" onclick="<%= "document.getElementById('contentform').action.value="+MailingContentAction.ACTION_DELETE_TEXTBLOCK +";document.getElementById('contentform').contentID.value="+tagContent.getId() %>"/>&nbsp;
              </logic:equal>
              <logic:equal name="mailingContentForm" property="isTemplate" value="false">
                <logic:equal value="false" name="mailingContentForm" property="worldMailingSend">
                  <html:image src="button?msg=Save" border="0" property="save" onclick="<%= "save"+tagContent.getId()+"();document.getElementById('contentform').submit()" %>"/>&nbsp;
                  <html:image src="button?msg=Delete" border="0" property="delete" onclick="<%= "document.getElementById('contentform').action.value="+MailingContentAction.ACTION_DELETE_TEXTBLOCK +";document.getElementById('contentform').contentID.value="+tagContent.getId() %>"/>&nbsp;
                </logic:equal>
              </logic:equal>
            <html:link page="<%= new String("/mailingcontent.do?action=" + MailingContentAction.ACTION_VIEW_CONTENT + "&mailingID=" + tmpMailingID + "#" + request.getParameter("dynNameID")) %>"><img src="<html:rewrite page="/button?msg=Back"/>" border="0"></html:link>
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
        oFCKeditorNew.Config[ "BaseHref" ] = baseUrl+"/${FCKEDITOR_PATH}/" ;
        oFCKeditorNew.Config[ "CustomConfigurationsPath" ] = "<html:rewrite page="<%= new String("/"+AgnUtils.getEMMProperty("fckpath") +"/emmconfig.jsp?mailingID="+tmpMailingID) %>"/>" ;
        oFCKeditorNew.ToolbarSet = "emm" ;
        oFCKeditorNew.BasePath = baseUrl+"/${FCKEDITOR_PATH}/" ;
        oFCKeditorNew.Height = "400" ; // 400 pixels
        oFCKeditorNew.Width = "650" ;
        oFCKeditorNew.ReplaceTextarea();
		
		
	}
		function save() {
		if(isFCKEditorActive== true || document.getElementById( 'Textarea' ).style.display == 'none')  {
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
            <html:select property="newTargetID" size="1">
                <html:option value="0"><bean:message key="All_Subscribers"/></html:option>
                <logic:iterate id="target" name="targetGroups" scope="request">
                    <html:option value="${target.getId()}">${target.getTargetName()}</html:option>
                </logic:iterate>
            </html:select></td></tr>
        <tr><td colspan="3"><br><br></td></tr>
        <tr><td colspan="3"><html:image src="button?msg=Add" border="0" property="insert" onclick="<%= "save();  document.getElementById('contentform').action.value="+MailingContentAction.ACTION_ADD_TEXTBLOCK %>"/>&nbsp;
        <html:link page="<%= new String("/mailingcontent.do?action=" + MailingContentAction.ACTION_VIEW_CONTENT + "&mailingID=" + tmpMailingID) %>"><img src="<html:rewrite page="/button?msg=Back"/>" border="0"></html:link>
    	</td>
    	</tr>
    </table>
</html:form>

<%@include file="/footer.jsp"%>
