<%-- checked --%>
<%@ page language="java" import="org.agnitas.beans.DynamicTagContent, org.agnitas.beans.Mailing, org.agnitas.util.AgnUtils, org.agnitas.web.MailingContentAction, org.agnitas.web.MailingWizardAction" contentType="text/html; charset=utf-8"  errorPage="/error.jsp" %>
<%@ page import="org.agnitas.web.MailingWizardForm" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.Map" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

 <%
    Mailing mailing = (Mailing) request.getAttribute("mailing");
     MailingWizardForm aForm= (MailingWizardForm) request.getAttribute("aForm");
     DynamicTagContent tagContent=null;
    String index=null;
    Integer len= (Integer)request.getAttribute("len");
    int i=1;

  %>

<% pageContext.setAttribute("CKEDITOR_PATH", AgnUtils.getEMMProperty("ckpath")); %>
<jsp:include page="/${CKEDITOR_PATH}/ckeditor-emm-helper.jsp"/>

<div class="new_mailing_content">

    <ul class="new_mailing_step_display">
        <li class="step_display_first"><span>1</span></li>
        <li><span>2</span></li>
        <li><span>3</span></li>
        <li><span>4</span></li>
        <li><span>5</span></li>
        <li><span>6</span></li>
        <li><span>7</span></li>
        <li><span class="step_active">8</span></li>
        <li><span>9</span></li>
        <li><span>10</span></li>
        <li><span>11</span></li>
    </ul>

    <p><bean:message key="mailing.Text_Module"/>:&nbsp;<%= aForm.getDynName() %></p><br>

<html:form action="/mwTextmodule">
    <html:hidden property="action"/>
    <html:hidden property="contentID"/>
    <logic:iterate id="dyncontent" name="mailingWizardForm" property='<%= \"mailing.dynTags(\"+aForm.getDynName()+\").dynContent\" %>'>
        <% Map.Entry ent2=(Map.Entry)pageContext.getAttribute("dyncontent");
            tagContent=(DynamicTagContent)ent2.getValue();
            index=(String)ent2.getKey(); %>
        <div class="assistant_step7_form_item ">
        <div style="margin-top:150px; float: left; width:130px;">
            <bean:message key="mailing.Content"/>:
            <% String editorId = "content[" + index + "].dynContent"; %>
            <logic:equal value="true" name="mailingWizardForm" property="showHTMLEditorForDynTag">
                &nbsp;<img src="${emmLayoutBase.imagesURL}/edit.gif" class="ckeditor-toggle-button"
                               border="0" onclick="toggleEditor('<%= editorId %>', 650, 400, 0); return false;" alt="<bean:message key='htmled.title'/>">
            </logic:equal>
            </div>
            <html:hidden property='<%= \"content[\"+index+\"].dynOrder\" %>'/>
            <div id="Textarea<%= index %>" class="mailing_wizard_form_item">
                <html:textarea styleId="<%= editorId %>" property='<%= editorId %>' rows="20" cols="85"/>&nbsp;
            </div>

            <div style="margin-top:150px;">
                <% if (len > 1 && i != 1) { %>
                <input type="image"
                       src="${emmLayoutBase.imagesURL}/button_up.gif" border="0"
                       name="order"
                       onclick="<%= "document.mailingWizardForm.contentID.value=" + tagContent.getDynOrder() + "; document.mailingWizardForm.action.value='textmodule_move_up'" %>">
                <br>
                <% } %>
                <% if (len > 1 && i != len) { %>
                <input type="image"
                       src="${emmLayoutBase.imagesURL}/button_down.gif"
                       border="0" name="order"
                       onclick="<%= "document.mailingWizardForm.contentID.value=" + tagContent.getDynOrder() + "; document.mailingWizardForm.action.value='textmodule_move_down'" %>">
                <% }
                    i++; %>
            </div>
        </div>

        <div class="assistant_step7_form_item">
            <label><bean:message key="target.Target"/>:&nbsp;</label>
            <html:select property='<%= \"content[\"+index+\"].targetID\" %>' size="1">
                <html:option value="0"><bean:message key="statistic.All_Subscribers"/></html:option>
                <logic:notEmpty name="targets" scope="request">
                    <c:forEach var="dbTarget" items="${targets}">
                        <html:option value="${dbTarget.id}">${dbTarget.targetName}</html:option>
                    </c:forEach>
                </logic:notEmpty>
            </html:select>
        </div>

        <div class="button_container">
            <div class="action_button mailingwizard_add_button">
                <a href="#"
                   onclick="saveAllHtmlEditors();document.mailingWizardForm.action.value='${ACTION_TEXTMODULE_SAVE}'; document.mailingWizardForm.submit(); return false;"><span><bean:message
                        key="button.Save"/></span></a>
            </div>
        </div>
    </logic:iterate>

    <script type="text/javascript">
        function saveAllHtmlEditors() {
            removeAllEditors();
        }
    </script>

    <div class="assistant_step7_form_item ">
        <span class="head3"><bean:message key="mailing.New_Content"/></span>
    </div>

    <div class="assistant_step7_form_item ">
        <div class="tooltiphelp mailing_wizard_form_item" style="margin-top: 150px; width:130px;" id="content">
            <bean:message key="mailing.Content"/>:
            <logic:equal value="true" name="mailingWizardForm" property="showHTMLEditorForDynTag">
                &nbsp;<img src="${emmLayoutBase.imagesURL}/edit.gif" border="0" onclick="toggleEditor('newContent', 650, 400, 0); return false;"
                    alt="<bean:message key='htmled.title'/>" class="ckeditor-toggle-button">
            </logic:equal>
        </div>
        <script type="text/javascript">
            var hb1 = new HelpBalloon({
                dataURL: 'help_${helplanguage}/mailingwizard/step_08/Content.xml'
            });
            $('content').appendChild(hb1.icon);
        </script>

        <div id="Textarea" class="mailing_wizard_form_item">
            <html:textarea property="newContent" styleId="newContent" rows="20" cols="85"/>&nbsp;
        </div>
    </div>

    <div class="assistant_step7_form_item">
        <div class="tooltiphelp mailing_wizard_form_item" style="width: 130px;" id="target"><bean:message
                key="target.Target"/>:
        </div>
        <script type="text/javascript">
            var hb2 = new HelpBalloon({
                dataURL: 'help_${helplanguage}/mailingwizard/step_08/Target.xml'
            });
            $('target').appendChild(hb2.icon);
        </script>
        <html:select property="targetID" size="1" styleId="targetID">
            <html:option value="0"><bean:message key="statistic.All_Subscribers"/></html:option>
            <logic:notEmpty name="targets" scope="request">
                <c:forEach var="dbTarget" items="${targets}">
                    <html:option value="${dbTarget.id}">${dbTarget.targetName}</html:option>
                </c:forEach>
            </logic:notEmpty>
        </html:select>
    </div>

    <div class="button_container">
        <div class="action_button mailingwizard_add_button">
            <a href="#"
               onclick="saveAllHtmlEditors(); document.mailingWizardForm.action.value='${ACTION_TEXTMODULE_ADD}'; document.mailingWizardForm.submit(); return false;"><span><bean:message
                    key="button.Add"/></span></a>
        </div>
    </div>

    <div class="assistant_step7_button_container">
        <div class="action_button">
            <a href="#" onclick="document.mailingWizardForm.action.value='${ACTION_FINISH}'; document.mailingWizardForm.submit(); return false;">
                <span><bean:message key="button.Finish"/></span>
            </a>
        </div>
        <div class="action_button">
            <a href="#" onclick="document.mailingWizardForm.action.value='skip'; document.mailingWizardForm.submit(); return false;">
                <span><bean:message key="button.Skip"/></span>
            </a>
        </div>
        <div class="action_button">
            <a href="#" onclick="document.mailingWizardForm.action.value='${ACTION_TEXTMODULE}'; document.mailingWizardForm.submit(); return false;">
                <span><bean:message key="button.Proceed"/></span>
            </a>
        </div>
        <div class="action_button">
            <a href="#" onclick="document.mailingWizardForm.action.value='previous'; document.mailingWizardForm.submit(); return false;">
                <span><bean:message key="button.Back"/></span>
            </a>
        </div>
    </div>

</html:form>
</div>