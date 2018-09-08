<%-- checked --%>
<%@ page language="java"
         import="org.agnitas.beans.DynamicTagContent, org.agnitas.util.AgnUtils, org.agnitas.web.MailingContentAction, org.agnitas.web.MailingContentForm, org.agnitas.web.forms.MailingBaseForm, java.util.Locale"
         contentType="text/html; charset=utf-8"  errorPage="/error.jsp" %>
<%@ page import="java.util.Map" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<% pageContext.setAttribute("CKEDITOR_PATH", AgnUtils.getEMMProperty("ckpath")); %>

<jsp:include page="/${CKEDITOR_PATH}/ckeditor-emm-helper.jsp"/>

<script type="text/javascript">
    function afterRemoveEditor(tagId) {
        if(document.getElementById('AHTMLEditor' + tagId)) document.getElementById('AHTMLEditor' + tagId).className = '';
        if(document.getElementById('LIHTMLEditor' + tagId)) document.getElementById('LIHTMLEditor' + tagId).className = '';
        if(document.getElementById('AHTMLCode' + tagId)) document.getElementById('AHTMLCode' + tagId).className = 'stat_tab_left';
        if(document.getElementById('LIHTMLCode' + tagId)) document.getElementById('LIHTMLCode' + tagId).className = 'stat_tab_right';
    }

    function beforeCreateEditor(tagId) {
        if(document.getElementById('AHTMLEditor' + tagId)) document.getElementById('AHTMLEditor' + tagId).className = 'stat_tab_left';
        if(document.getElementById('LIHTMLEditor' + tagId)) document.getElementById('LIHTMLEditor' + tagId).className = 'stat_tab_right';
        if(document.getElementById('AHTMLCode' + tagId)) document.getElementById('AHTMLCode' + tagId).className = '';
        if(document.getElementById('LIHTMLCode' + tagId)) document.getElementById('LIHTMLCode' + tagId).className = '';
    }
</script>

<c:set var="MAILING_CONTENT_HTML_CODE" value="<%= MailingContentForm.MAILING_CONTENT_HTML_CODE %>"/>
<c:set var="MAILING_CONTENT_HTML_EDITOR" value="<%= MailingContentForm.MAILING_CONTENT_HTML_EDITOR %>"/>

<% int tmpMailingID = 0;
   boolean fullPage = false;
    MailingContentForm aForm = null;
    if (session.getAttribute("mailingContentForm") != null) {
        aForm = (MailingContentForm) session.getAttribute("mailingContentForm");
        tmpMailingID = aForm.getMailingID();
        if (aForm.getDynName().equals("HTML-Version")) {
            fullPage = true;
        }
    }
    MailingBaseForm baseForm = (MailingBaseForm) session.getAttribute("mailingBaseForm");
    DynamicTagContent tagContent = null;
    String index = null;
    int i = 1;
    int len = aForm.getContent().size();
    aForm.setNewContent("");
%>

<%
    DynamicTagContent tagContent1=null;
    String index1=null;
%>

<html:form action="/mailingcontent" styleId="contentform">
<html:hidden property="dynNameID"/>
<html:hidden property="action"/>
<html:hidden property="mailingID"/>
<html:hidden property="contentID"/>

<div class="text_module_name_container">
    <div class="filterbox_form_container">
        <div id="filterbox_top"></div>
        <div class="grey_box_content grey_box_content_ie_fix">
            <label class="text_module_name_label"><bean:message key="mailing.Text_Module"/>:</label>${mailingContentForm.dynName}
        </div>
        <div id="filterbox_bottom"></div>
    </div>
</div>

<div class="blue_box_container">

<c:set var="contentHTMLCodeTitle" value="mailingContentText" scope="request" />
<logic:equal name="mailingContentForm" property="showHTMLEditor" value="true">
    <c:set var="contentHTMLCodeTitle" value="mailingContentHTMLCode" scope="request"/>
</logic:equal>

<logic:iterate id="dyncontent" name="mailingContentForm" property="content">
<% Map.Entry ent2 = (Map.Entry) pageContext.getAttribute("dyncontent");
    tagContent = (DynamicTagContent) ent2.getValue();
    index = (String) ent2.getKey();
    pageContext.setAttribute("index", index);
%>

<div class="assistant_step7_form_item ">
    <div class="switch_content_tabs" style="width:705px;">
        <ul>
            <% String editorId = "content_" + index + "_.dynContent"; %>
            <logic:equal name="mailingContentForm" property="showHTMLEditor" value="true">
                <li id="LIHTMLEditor<%= tagContent.getId() %>">
                    <a id="AHTMLEditor<%= tagContent.getId() %>" href="#"
                       onclick="beforeCreateEditor(<%= tagContent.getId() %>); createEditorExt('<%= editorId %>', 705, 400,  <%= tmpMailingID %>, <%= fullPage %>); return false;">
                        <img src="${emmLayoutBase.imagesURL}/icon_html_version.png" border="0"/>
                        <bean:message key="mailingContentHTMLEditor"/>
                    </a>
                </li>
            </logic:equal>

            <li id="LIHTMLCode<%= tagContent.getId() %>" class="stat_tab_right">
                <a id="AHTMLCode<%= tagContent.getId() %>" href="#" class="stat_tab_left"
                   onclick="removeEditor('<%= editorId %>'); afterRemoveEditor(<%= tagContent.getId() %>); return false;">
                    <img src="${emmLayoutBase.imagesURL}/icon_profis.png" border="0"/>
                    &nbsp;<bean:message key="${contentHTMLCodeTitle}"/>
                </a>
            </li>
        </ul>

    </div>
    <div id="Textarea<%= tagContent.getId() %>" style="float:left;">
        <html:hidden property='<%= new String(\"content(\"+index+\").dynOrder\") %>'/>
        <html:textarea property='<%= \"content(\"+index+\").dynContent\" %>'
                       styleId="<%= editorId %>" rows="20" cols="85"
                       readonly="<%= aForm.isWorldMailingSend() %>"/>&nbsp;
    </div>
    <c:if test="<%= len > 1%>">
        <div style="margin-top:150px;">
            <% if (len > 1 && i != 1) { %>
            <input type="image" src="${emmLayoutBase.imagesURL}/button_top.gif"
                   border="0" name="order"
                   onclick="<%= "document.getElementById('contentform').action.value="+MailingContentAction.ACTION_CHANGE_ORDER_TOP +";document.getElementById('contentform').contentID.value="+tagContent.getId() %>">
            <br>
            <input type="image" src="${emmLayoutBase.imagesURL}/button_up.gif"
                   border="0" name="order"
                   onclick="<%= "document.getElementById('contentform').action.value="+MailingContentAction.ACTION_CHANGE_ORDER_UP +";document.getElementById('contentform').contentID.value="+tagContent.getId() %>">
            <br>
            <% } %>
            <% if (len > 1 && i != len) { %>
            <input type="image" src="${emmLayoutBase.imagesURL}/button_down.gif"
                   border="0" name="order"
                   onclick="<%= "document.getElementById('contentform').action.value="+MailingContentAction.ACTION_CHANGE_ORDER_DOWN +";document.getElementById('contentform').contentID.value="+tagContent.getId() %>">
            <br>
            <input type="image"
                   src="${emmLayoutBase.imagesURL}/button_bottom.gif" border="0"
                   name="order"
                   onclick="<%= "document.getElementById('contentform').action.value="+MailingContentAction.ACTION_CHANGE_ORDER_BOTTOM +";document.getElementById('contentform').contentID.value="+tagContent.getId() %>">
            <% }
                i++; %>
        </div>
    </c:if>
</div>
<div class="assistant_step7_form_item">
    <label><bean:message key="target.Target"/>:&nbsp;</label>
    <c:set var="targetGroupDeleted" value="0" scope="page"/>
    <bean:define id="targetGroupSelected" name="mailingContentForm" property='<%= "content("+index+").targetID" %>'
                 toScope="page"/>

    <html:select property='<%= \"content(\"+index+\").targetID\" %>' size="1"
                 disabled="<%= aForm.isWorldMailingSend() %>">
        <html:option value="0"><bean:message key="statistic.All_Subscribers"/></html:option>
        <logic:iterate id="target" name="targetGroups" scope="request">
            <c:if test="${(target.deleted == 0) || (target.id == targetGroupSelected)}">
                <html:option value="${target.id}">${target.targetName}
                    <c:if test="${target.deleted != 0}">
                        &nbsp;(<bean:message key="target.Deleted"/>)
                        <c:set var="targetGroupDeleted" value="1" scope="page"/>
                    </c:if>
                </html:option>
            </c:if>
        </logic:iterate>
    </html:select>

    <c:if test="${targetGroupDeleted != 0}">
        <span class="warning">(<bean:message key="target.Deleted"/>)</span>
    </c:if>
</div>

<div class="button_container text_module_buttons">

    <% if (!aForm.isWorldMailingSend()) {%>
    <input type="hidden" id="save" name="save" value=""/>

    <div class="action_button mailingwizard_add_button">
        <a href="#"
           onclick="<%= "document.getElementById('contentform').action.value="+MailingContentAction.ACTION_SAVE_TEXTBLOCK +"; document.getElementById('contentform').save.value='save'; saveAllHtmlEditors();document.getElementById('contentform').submit(); return false;" %>"><span><bean:message
                key="button.Save"/></span></a>
    </div>
    <div class="action_button mailingwizard_add_button">
        <a href="#"
           onclick="<%= " document.getElementById('contentform').action.value="+MailingContentAction.ACTION_SAVE_TEXTBLOCK_AND_BACK +";document.getElementById('contentform').save.value='save'; saveAllHtmlEditors();document.getElementById('contentform').submit(); return false;" %>"><span><bean:message
                key="button.SaveAndBack"/></span></a>
    </div>
    <div class="action_button mailingwizard_add_button">
        <a href="#"
           onclick="<%= "document.getElementById('contentform').action.value="+MailingContentAction.ACTION_DELETE_TEXTBLOCK +";document.getElementById('contentform').contentID.value="+tagContent.getId()+"; ;document.getElementById('contentform').submit(); return false;" %>"><span><bean:message
                key="button.Delete"/></span></a>
    </div>


    <%}%>
    <div class="action_button mailingwizard_add_button">
        <html:link
                page='<%= new String(\"/mailingcontent.do?action=\" + MailingContentAction.ACTION_VIEW_CONTENT + \"&mailingID=\" + tmpMailingID) %>'><span><bean:message
                key="button.Back"/></span></html:link>
    </div>
</div>
    <logic:equal name="mailingContentForm" property="showHTMLEditor" value="true">
        <logic:equal name="mailingContentForm" property="mailingContentView" value="${MAILING_CONTENT_HTML_EDITOR}">
            <script type="text/javascript">

                beforeCreateEditor(<%= tagContent.getId() %>);
                createEditorExt('<%= editorId %>', 705, 400,  <%= tmpMailingID %>, <%= fullPage %>);
            </script>
        </logic:equal>
        <logic:equal name="mailingContentForm" property="mailingContentView" value="${MAILING_CONTENT_HTML_CODE}">
            <script type="text/javascript">

                removeEditor('<%= editorId %>');
                afterRemoveEditor(<%= tagContent.getId() %>);
            </script>
        </logic:equal>
    </logic:equal>
    <logic:equal name="mailingContentForm" property="showHTMLEditor" value="false">
        <script type="text/javascript">

            removeEditor('<%= editorId %>');
            afterRemoveEditor(<%= tagContent.getId() %>);
        </script>
    </logic:equal>

</logic:iterate>

<script type="text/javascript">
    function saveAllHtmlEditors() {
        removeAllEditors();
    }
</script>

<div class="dotted_line new_text_module_separator"></div>

<div class="assistant_step7_form_item new_text_module_label">
    <span class="head3"><bean:message key="mailing.New_Content"/></span>
</div>

<div class="assistant_step7_form_item ">
    <div class="switch_content_tabs">
        <ul>
            <logic:equal name="mailingContentForm" property="showHTMLEditor" value="true">
                <li id="LIHTMLEditor">
                    <a id="AHTMLEditor" href="#" onclick="beforeCreateEditor(''); createEditorExt('newContent', 888, 400, <%= tmpMailingID %>, <%= fullPage %>); return false;">
                    <img src="${emmLayoutBase.imagesURL}/icon_html_version.png" border="0"/>
                        <bean:message key="mailingContentHTMLEditor"/>
                    </a>
                </li>
            </logic:equal>
            <li id="LIHTMLCode" class="stat_tab_right">
                <a id="AHTMLCode" href="#" class="stat_tab_left" onclick="removeEditor('newContent'); afterRemoveEditor(''); return false;">
                    <img src="${emmLayoutBase.imagesURL}/icon_profis.png" border="0"/>
                    &nbsp;<bean:message key="${contentHTMLCodeTitle}"/>
                </a>
            </li>
        </ul>

    </div>
    <div id="Textarea">
        <html:textarea property="newContent" styleId="newContent" rows="20" styleClass="new_text_module_textarea"/>&nbsp;
    </div>
    <logic:equal name="mailingContentForm" property="showHTMLEditor" value="true">
        <logic:equal name="mailingContentForm" property="mailingContentView" value="${MAILING_CONTENT_HTML_EDITOR}">
            <script type="text/javascript">
                beforeCreateEditor('');
                createEditorExt('newContent', 888, 400, <%= tmpMailingID %>, <%= fullPage %>);
            </script>
        </logic:equal>
        <logic:equal name="mailingContentForm" property="mailingContentView" value="${MAILING_CONTENT_HTML_CODE}">
            <script type="text/javascript">
                removeEditor('newContent');
                afterRemoveEditor('');
            </script>
        </logic:equal>
    </logic:equal>
    <logic:equal name="mailingContentForm" property="showHTMLEditor" value="false">
        <script type="text/javascript">
            removeEditor('newContent');
            afterRemoveEditor('');
        </script>
    </logic:equal>

</div>
<div class="assistant_step7_form_item">
    <label for="newTargetID"><bean:message key="target.Target"/>:&nbsp;</label>
    <html:select property="newTargetID" size="1" disabled="<%= aForm.isWorldMailingSend() %>">
        <html:option value="0"><bean:message key="statistic.All_Subscribers"/></html:option>
        <logic:iterate id="target" name="targetGroups" scope="request">
        	<c:if test="${target.deleted == 0}">
	            <html:option value="${target.id}">${target.targetName}</html:option>
	        </c:if>
        </logic:iterate>
    </html:select>
</div>
<div class="button_container">

    <div class="action_button mailingwizard_add_button">
        <a href="#" id="dummy" onclick="saveAllHtmlEditors(); document.getElementById('contentform').action.value=${ACTION_ADD_TEXTBLOCK}; document.getElementById('contentform').submit(); return false;">
            <span><bean:message key="button.Add"/></span>
        </a>
    </div>

    <div class="action_button mailingwizard_add_button">
        <html:link
                page='<%= new String("/mailingcontent.do?action=" + MailingContentAction.ACTION_VIEW_CONTENT + "&mailingID=" + tmpMailingID) %>'><span><bean:message
                key="button.Back"/></span></html:link>
    </div>

</div>

</html:form>
</div>

