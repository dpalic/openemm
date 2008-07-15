<%@ page language="java" import="org.agnitas.util.*, java.util.*, org.agnitas.web.*, org.agnitas.target.*, org.agnitas.beans.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="mailing.content.show"/>

<script type="text/javascript" src="fckeditor/fckeditor.js"></script>

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

<html:errors/>

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
            index=(String)ent2.getKey(); %>
            <script type="text/javascript">
                <!-- 
                var oFCKeditor<%= tagContent.getId() %>=null;
                function editHtml<%= tagContent.getId() %>() {
                if(oFCKeditor<%= tagContent.getId() %>==null) {
                oFCKeditor<%= tagContent.getId() %> = new FCKeditor( 'content(<%= index %>).dynContent' ) ;
                oFCKeditor<%= tagContent.getId() %>.Config[ "AutoDetectLanguage" ] = false ;
                oFCKeditor<%= tagContent.getId() %>.Config[ "DefaultLanguage" ] = "<%= ((Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)).getLanguage() %>" ;
                oFCKeditor<%= tagContent.getId() %>.Config[ "BaseHref" ] = baseUrl+"/fckeditor/" ;
                oFCKeditor<%= tagContent.getId() %>.Config[ "CustomConfigurationsPath" ] = "<html:rewrite page="<%= new String("/fckeditor/emmconfig.jsp?mailingID="+tmpMailingID) %>"/>" ;
                oFCKeditor<%= tagContent.getId() %>.ToolbarSet = "emm" ;
                oFCKeditor<%= tagContent.getId() %>.BasePath = baseUrl+"/fckeditor/" ;
                oFCKeditor<%= tagContent.getId() %>.Height = "400" ; // 400 pixels
                oFCKeditor<%= tagContent.getId() %>.Width = 650 ; // 400 pixels
                oFCKeditor<%= tagContent.getId() %>.ReplaceTextarea();
                }
                return true;
                }
                //-->
            </script>

            <tr><td colspan="3"><a name="${dyncontent.getId()}"><hr size="1"></td></tr>
            
            <tr><td><bean:message key="Content"/>:&nbsp;<img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>edit.gif" border="0" onclick="editHtml<%= tagContent.getId() %>();" alt="<bean:message key="htmled.title"/>"></td>
            <td>
                <html:hidden property="<%= new String("content("+index+").dynOrder") %>"/>
                <html:textarea property="<%= "content("+index+").dynContent" %>" rows="20" cols="85"/>&nbsp;
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
            
                <html:select property="<%= "content("+index+").targetID" %>" size="1">
                    <html:option value="0"><bean:message key="All_Subscribers"/></html:option>
                    <logic:iterate id="target" name="targetGroups" scope="request">
                        <html:option value="${target.getId()}">${target.getTargetName()}</html:option>
                    </logic:iterate>
                </html:select></td></tr>
            <tr><td colspan="3"><br><br></td></tr>
            <tr><td colspan="3"><html:image src="button?msg=Save" border="0" property="save"/>&nbsp;
            <html:image src="button?msg=Delete" border="0" property="delete" onclick="<%= "document.getElementById('contentform').action.value="+MailingContentAction.ACTION_DELETE_TEXTBLOCK +";document.getElementById('contentform').contentID.value="+tagContent.getId() %>"/>&nbsp;
            <html:link page="<%= new String("/mailingcontent.do?action=" + MailingContentAction.ACTION_VIEW_CONTENT + "&mailingID=" + tmpMailingID + "#" + request.getParameter("dynNameID")) %>"><img src="<html:rewrite page="/button?msg=Back"/>" border="0"></html:link>
        </logic:iterate>
        <tr><td colspan="3"><a name="0"><hr size="1"><span class="head3"><bean:message key="New_Content"/></span></a><br></td></tr>
        <script type="text/javascript">
            <!--
            var oFCKeditorNew=null;
            function editHtmlNew() {
            if(oFCKeditorNew==null) {
            oFCKeditorNew = new FCKeditor( 'new_dyn_content' ) ;
            oFCKeditorNew.Config[ "AutoDetectLanguage" ] = false ;
            oFCKeditorNew.Config[ "DefaultLanguage" ] = "<%= ((Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)).getLanguage() %>" ;
            oFCKeditorNew.Config[ "BaseHref" ] = baseUrl+"/fckeditor/" ;
            oFCKeditorNew.Config[ "CustomConfigurationsPath" ] = "<html:rewrite page="<%= new String("/fckeditor/emmconfig.jsp?mailingID="+tmpMailingID) %>"/>" ;
            oFCKeditorNew.ToolbarSet = "emm" ;
            oFCKeditorNew.BasePath = baseUrl+"/fckeditor/" ;
            oFCKeditorNew.Height = "400" ; // 400 pixels
            oFCKeditorNew.Width = "650" ;
            oFCKeditorNew.ReplaceTextarea();
            }
            return true;
            }
            //-->
        </script>
        <tr><td><bean:message key="Content"/>:&nbsp;<img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>edit.gif" border="0" onclick="editHtmlNew();" alt="<bean:message key="htmled.title"/>"></td>
        <td>
            <html:textarea property="newContent" rows="20" cols="85"/>&nbsp;
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
        <tr><td colspan="3"><html:image src="button?msg=Add" border="0" property="insert" onclick="<%= "document.getElementById('contentform').action.value="+MailingContentAction.ACTION_ADD_TEXTBLOCK %>"/>&nbsp;
        <html:link page="<%= new String("/mailingcontent.do?action=" + MailingContentAction.ACTION_VIEW_CONTENT + "&mailingID=" + tmpMailingID) %>"><img src="<html:rewrite page="/button?msg=Back"/>" border="0"></html:link>
    </table>
</html:form>

<%@include file="/footer.jsp"%>
