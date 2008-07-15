<%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, java.util.*, org.agnitas.beans.*" contentType="text/html; charset=utf-8" %>
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
    System.err.println("dynName:"+aForm.getDynName());
    int len=((DynamicTag)mailing.getDynTags().get(aForm.getDynName())).getDynContent().size();
    int i=0;
%>

<agn:Permission token="mailing.show"/>

<script type="text/javascript" src="fckeditor/fckeditor.js"></script>

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
            <script type="text/javascript">
                <!-- 
                var oFCKeditor<%= tagContent.getId() %>=null;
                function editHtml<%= tagContent.getId() %>() {
                if(oFCKeditor<%= tagContent.getId() %>==null) {
                oFCKeditor<%= tagContent.getId() %> = new FCKeditor( 'content(<%= index %>).dynContent' ) ;
                oFCKeditor<%= tagContent.getId() %>.Config[ "AutoDetectLanguage" ] = false ;
                oFCKeditor<%= tagContent.getId() %>.Config[ "DefaultLanguage" ] = "<%= ((Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)).getLanguage() %>" ;
                oFCKeditor<%= tagContent.getId() %>.Config[ "BaseHref" ] = baseUrl+"/fckeditor/" ;
                oFCKeditor<%= tagContent.getId() %>.Config[ "CustomConfigurationsPath" ] = "<html:rewrite page="<%= new String("/fckeditor/emmconfig.jsp?mailingID="+mailing.getId()) %>"/>" ;
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
                <logic:iterate id="dbTarget" name="__targets">
                    <html:option value="${dbTarget.getId()}">${dbTarget.getTargetName()}</html:option>
                </logic:iterate>
            </html:select></td></tr>
            <tr><td colspan="3"><br><br></td></tr>
            <tr><td colspan="3"><html:image src="button?msg=Save" border="0" property="save"/>&nbsp;
            <html:image src="button?msg=Delete" border="0" property="delete" onclick="<%= "document.getElementById('contentform').action.value="+MailingContentAction.ACTION_DELETE_TEXTBLOCK +";document.getElementById('contentform').contentID.value="+tagContent.getId() %>"/>
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
            oFCKeditorNew.Config[ "CustomConfigurationsPath" ] = "<html:rewrite page="<%= new String("/fckeditor/emmconfig.jsp?mailingID="+mailing.getId()) %>"/>" ;
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
        <html:select property="targetID" size="1">
            <html:option value="0"><bean:message key="All_Subscribers"/></html:option>
            <logic:iterate id="dbTarget" name="__targets">
                <html:option value="${dbTarget.getId()}">${dbTarget.getTargetName()}</html:option>
            </logic:iterate>
        </html:select></td></tr>
        <tr><td colspan="3"><br><br></td></tr>
        <tr><td colspan="3"><html:image src="button?msg=Add" border="0" property="insert" onclick="<%= "document.mailingWizardForm.action.value='textmodule_add'" %>"/>
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
                <html:image src="button?msg=Skip" border="0" onclick="<%= "document.mailingWizardForm.action.value='" + MailingWizardAction.ACTION_MEASURELINKS + "'" %>"/>
                &nbsp;
                <html:image src="button?msg=Finish" border="0" onclick="<%= "document.mailingWizardForm.action.value='" + MailingWizardAction.ACTION_FINISH + "'" %>"/>
                &nbsp;
            </td>
        </tr>
    </table>             

    
</html:form>
<%@include file="/footer.jsp"%>
