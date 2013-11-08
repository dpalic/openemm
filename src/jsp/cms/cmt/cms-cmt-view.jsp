<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<script type="text/javascript" src="<%= request.getContextPath() %>/js/cms/cmPreviewResize.js"></script>
<script type="text/javascript">
    function toggleContainer(container){
        $(container).toggleClassName('toggle_open');
        $(container).toggleClassName('toggle_closed');
        $(container).next().toggle();
    }

    Event.observe(window, 'load', function() {
        toggleContainer(document.getElementById("cm_preview_toggle"));
    });
</script>

<html:form action="/cms_cmt" focus="name">
    <html:hidden property="cmtId"/>
    <html:hidden property="action"/>

    <div class="mailing_name_box_container">
        <div class="mailing_name_box_top"></div>
        <div class="mailing_name_box_content">
            <div class="mailing_name_box_left_column">
                <label for="mailing_name"><bean:message key="default.Name"/>:</label>
                <html:text styleId="mailing_name" property="name" maxlength="99" size="42"/>
            </div>
            <div class="mailing_name_box_center_column">
                <label for="mailing_name"><bean:message key="default.description"/>:</label>
                <html:textarea styleId="mailing_description" property="description" rows="5" cols="32"/>
            </div>
            <div class="mailing_name_box_right_column"></div>
        </div>
        <div class="mailing_name_box_bottom"></div>
    </div>

    <logic:notEqual name="contentModuleTypeForm" property="cmtId" value="0">
        <div class="export_wizard_content">
            <div id="advanced_search_top"></div>
            <div id="advanced_search_content">
                <div class="advanced_search_toggle toggle_open" id="cm_preview_toggle" onclick="toggleContainer(this);">
                    <a href="#"><bean:message key="default.Preview"/></a>
                </div>
                <div>
                    <iframe width="650" scrolling="auto" height="300" id="cm_preview"
                            src="<html:rewrite page="/cms_cmt.do?action=${ACTION_PREVIEW}&cmtId=${contentModuleTypeForm.cmtId}"/>"
                            style="background-color : #FFFFFF;">
                        "Your Browser does not support IFRAMEs, please
                        update!
                    </iframe>
                </div>
            </div>
            <div id="advanced_search_bottom" class="cm_preview_panel"></div>
        </div>
    </logic:notEqual>

    <div class="emailbox_container">
        <div class="emailbox_top"></div>
        <div class="emailbox_content">
            <div class="cmt_content">
                <bean:message key="mailing.Content"/>:
            </div>
            <div>
                <html:textarea property="content" rows="13" cols="90" readonly="${readOnly}"/>
            </div>
        </div>
        <div class="emailbox_bottom"></div>
    </div>

    <div class="target_button_container">

        <input type="hidden" id="save" name="save" value=""/>

        <logic:notEqual name="contentModuleTypeForm" property="cmtId" value="0">
            <logic:equal name="contentModuleTypeForm" property="readOnly" value="false">
                <div class="maildetail_button">
                    <html:link page="/cms_cmt.do?action=${ACTION_CONFIRM_DELETE}&cmtId=${contentModuleTypeForm.cmtId}&fromListPage=false">
                        <span><bean:message key="button.Delete"/></span>
                    </html:link>
                </div>
            </logic:equal>

            <div class="maildetail_button">
                <html:link page="/cms_cmt.do?action=${ACTION_COPY}&cmtId=${contentModuleTypeForm.cmtId}">
                    <span><bean:message key="button.Copy"/></span>
                </html:link>
            </div>
        </logic:notEqual>

        <logic:equal name="contentModuleTypeForm" property="readOnly" value="false">
            <div class="maildetail_button">
                <a href="#" onclick="document.getElementById('save').value='true'; document.contentModuleTypeForm.submit(); return false;"><span><bean:message key="button.Save"/></span></a>
            </div>
        </logic:equal>

        <div class="maildetail_button"><bean:message key="cms.ContentModuleType"/>:</div>
    </div>

</html:form>