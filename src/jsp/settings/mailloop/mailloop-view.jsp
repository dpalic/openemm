<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.util.AgnUtils, org.agnitas.web.MailloopAction, java.util.Locale"  errorPage="/error.jsp" %>
<%@ page import="org.agnitas.web.MailloopForm" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<% int tmpLoopID = 0;

    if (request.getAttribute("mailloopForm") != null) {
        tmpLoopID = ((MailloopForm) request.getAttribute("mailloopForm")).getMailloopID();
    }
    pageContext.setAttribute("CKEDITOR_PATH", AgnUtils.getEMMProperty("ckpath"));
%>

<jsp:include page="/${CKEDITOR_PATH}/ckeditor-emm-helper.jsp"/>

<html:form action="/mailloop">
<html:hidden property="action"/>
<html:hidden property="mailloopID"/>
<div class="grey_box_container">
    <div class="grey_box_top"></div>
    <div class="grey_box_content">
        <div class="grey_box_left_column">
            <label for="mailing_name"><bean:message key="default.Name"/>:&nbsp;</label>
            <html:text property="shortname" maxlength="99" size="42" styleId="mailing_name"/>
        </div>

        <div class="grey_box_center_column">
            <label for="mailing_name"><bean:message key="default.description"/>:&nbsp;</label>

            <html:textarea property="description" rows="5" cols="32" styleId="mailing_description"/>

        </div>
        <div class="grey_box_right_column"></div>
    </div>
    <div class="grey_box_bottom"></div>
</div>

<div class="blue_box_container">
    <div class="blue_box_top"></div>
    <div class="blue_box_content">
        <div class="admin_filed_detail_form_item">
            <label><bean:message key="settings.mailloop.forward"/></label>

            <div class="mailloop_checkbox">
                <html:checkbox property="doForward"/>
            </div>
        </div>

        <div class="admin_filed_detail_form_item">
            <label><bean:message key="settings.mailloop.forward_adr"/>:&nbsp;</label>

            <html:text property="forwardEmail" maxlength="99" size="42"/>

        </div>
    </div>
    <div class="blue_box_bottom"></div>
</div>
<div class="blue_box_container">
    <div class="blue_box_top"></div>
    <div class="blue_box_content">
        <div class="admin_filed_detail_form_item">
            <label><bean:message key="settings.mailloop.subscribe"/></label>

            <div class="mailloop_checkbox">
                <html:checkbox property="doSubscribe"/>
            </div>
        </div>
        <div class="admin_filed_detail_form_item">
            <label><bean:message key="Mailinglist"/>:&nbsp;</label>
            <html:select property="mailinglistID" size="1">
                <c:forEach var="mailinglist" items="${mailloopForm.mailinglists}">
                    <html:option value="${mailinglist.id}">
                        ${mailinglist.shortname}
                    </html:option>
                </c:forEach>
            </html:select>

        </div>
        <div class="admin_filed_detail_form_item">
            <label><bean:message key="settings.mailloop.userform"/>:&nbsp;</label>
            <html:select property="userformID" size="1">
                <c:forEach var="userform" items="${mailloopForm.userforms}">
                    <html:option value="${userform.id}">
                        ${userform.formName}
                    </html:option>
                </c:forEach>
            </html:select>

        </div>
    </div>
    <div class="blue_box_bottom"></div>
</div>

<div class="blue_box_container">
    <div class="blue_box_top"></div>
    <div class="blue_box_content">
        <div class="admin_filed_detail_form_item">
            <label><bean:message key="settings.mailloop.autoresponder"/></label>

            <div class="mailloop_checkbox">
                <html:checkbox property="doAutoresponder"/>
            </div>
        </div>

        <div class="admin_filed_detail_form_item">
            <label><bean:message key="settings.mailloop.ar_sender"/>:&nbsp;</label>

            <html:text property="arSender" maxlength="99" size="42"/>

        </div>

        <div class="admin_filed_detail_form_item">
            <label><bean:message key="settings.mailloop.ar_subject"/>:&nbsp;</label>

            <html:text property="arSubject" maxlength="99" size="42"/>

        </div>

        <div class="admin_filed_detail_form_item">
            <label><bean:message key="mailing.Text_Version"/>:&nbsp;</label>

            <html:textarea property="arText" rows="14" cols="75"/>

        </div>

        <div class="admin_filed_detail_form_item">
            <label><bean:message key="mailing.HTML_Version"/>:&nbsp;
                <img src="${emmLayoutBase.imagesURL}/edit.gif" border="0" class="ckeditor-toggle-button"
                    onclick="toggleEditor('newContent', 650, 400, 0);" alt="<bean:message key="htmled.title"/>">
            </label>
            <div id="Textarea" style="margin-left:150px;">
                <html:textarea property="arHtml" styleId="newContent" rows="14" cols="75"/>&nbsp;
            </div>
        </div>

    </div>
    <div class="blue_box_bottom"></div>

</div>

<div class="button_container" style="padding-top:5px;">
    <agn:ShowByPermission token="mailloop.change">
    	<input type="hidden" name="saveMailloop" value="" id="saveMailloop"/>
	    <div class="action_button" id="save_button"><a href="#"
              onclick="removeAllEditors(); document.getElementById('saveMailloop').value='save'; document.mailloopForm.submit(); return false;"><span><bean:message
            key="button.Save"/></span></a></div>
    </agn:ShowByPermission>
    <agn:ShowByPermission token="mailloop.delete">
    	<logic:notEqual name="mailloopForm" property="mailloopID" value="0">
        	<div class="action_button"><html:link
            	    page='<%= new String("/mailloop.do?action=" + MailloopAction.ACTION_CONFIRM_DELETE) + "&mailloopID=" + tmpLoopID + "&fromListPage=false"%>'><span><bean:message
                	key="button.Delete"/></span></html:link>
      		</div>
  		</logic:notEqual>
  	</agn:ShowByPermission>

</div>
</html:form>