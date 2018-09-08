<%@ page language="java" contentType="text/html; charset=utf-8" import="java.util.List"  errorPage="/error.jsp" %>
<%@ page import="org.agnitas.cms.utils.TagUtils" %>
<%@ page import="org.agnitas.cms.web.CmsImageTag" %>
<%@ page import="org.agnitas.cms.web.ContentModuleAction" %>
<%@ page import="org.agnitas.cms.web.forms.ContentModuleForm" %>
<%@ page import="org.agnitas.cms.webservices.generated.CmsTag" %>
<%@ page import="org.agnitas.util.AgnUtils" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ include file="/WEB-INF/taglibs.jsp" %>

<% ContentModuleForm aForm = (ContentModuleForm) session.getAttribute("contentModuleForm"); %>

<% String previewUrl = "/cms_contentmodule.do?action=" + ContentModuleAction.ACTION_PURE_PREVIEW +
        "&contentModuleId=" + aForm.getContentModuleId() + "&cmtId=" + aForm.getCmtId() + "&sourceCMId=" + aForm.getSourceCMId(); %>

<% pageContext.setAttribute("CKEDITOR_PATH", AgnUtils.getEMMProperty("ckpath")); %>
<jsp:include page="/${CKEDITOR_PATH}/ckeditor-emm-helper.jsp"/>

<script type="text/javascript" src="<%= request.getContextPath() %>/js/lib/cms/cmPreviewResize.js"></script>

<script type="text/javascript">
    function toggleContainer(container){
        $(container).toggleClassName('toggle_open');
        $(container).toggleClassName('toggle_closed');
        $(container).next().toggle();
    }

    Event.observe(window, 'load', function() {
        <agn:ShowByPermission token="settings.open">
                var closed = document.getElementsByClassName('toggle_closed');
                if(closed)
                for(var i=0;i<closed.length;i++){
                    closed[i].addClassName('toggle_open');
                    closed[i].next().show();
                    closed[i].removeClassName('toggle_closed');
                }
        </agn:ShowByPermission>
        <agn:HideByPermission token="settings.open">
            toggleContainer(document.getElementById("cm_preview_toggle"));
        </agn:HideByPermission>
    });
</script>

<html:form action="/cms_contentmodule" focus="name"
           enctype="multipart/form-data">
<html:hidden property="contentModuleId"/>
<html:hidden property="cmtId"/>
<html:hidden property="action"/>
<input type="hidden" name="save.x" value="0">


    <div class="grey_box_container">
        <div class="grey_box_top"></div>
        <div class="grey_box_content">
            <div class="grey_box_left_column">
                <div class="action_name_box">
                    <label class="action_name_label"><bean:message key="default.Name"/>:</label>
                    <html:text styleId="mailing_name" property="name" size="42" maxlength="99"/>
                </div>
                <div class="action_name_box">
                    <label class="action_name_label"><bean:message key="cms.Category"/>:</label>
                    <html:select property="category" size="1" styleClass="namebox_select">
                        <html:option value="0">
                                &lt;<bean:message key="none"/>&gt;
                        </html:option>
                        <c:forEach var="cmCategory" items="${contentModuleForm.allCategories}">
                            <html:option value="${cmCategory.id}">
                                ${cmCategory.name}
                            </html:option>
                        </c:forEach>
                    </html:select>
                </div>
            </div>
            <div class="grey_box_center_column">
                <label for="mailing_name"><bean:message key="default.description"/>:</label>
                <html:textarea property="description" styleId="mailing_description"  cols="32" rows="5"/>
            </div>
            <div class="grey_box_right_column"></div>
        </div>
        <div class="grey_box_bottom"></div>
    </div>

    <div class="export_wizard_content">
        <div id="advanced_search_top"></div>
        <div id="advanced_search_content">
            <div class="advanced_search_toggle toggle_open" id="cm_preview_toggle" onclick="toggleContainer(this);">
                <a href="#"><bean:message key="mailing.Preview"/></a>
            </div>
            <div>
                <iframe width="650" scrolling="auto" height="300" id="cm_preview"
                        src="<html:rewrite page="<%= previewUrl %>"/>"
                        style="background-color : #FFFFFF;">
                    "Your Browser does not support IFRAMEs, please
                    update!
                </iframe>
            </div>
        </div>
        <div id="advanced_search_bottom" class="cm_preview_panel"></div>
    </div>

    <div class="blue_box_container">
        <div class="blue_box_top"></div>
        <div class="blue_box_content">
            <h2 class="blue_box_header"><bean:message key="mailing.Content"/></h2>

            <table cellspacing="0" cellpadding="4" width="100%">
                <% List<CmsTag> tags = aForm.getTags(); %>
                <% for (CmsTag tag : tags) {%>
                <tr>
                    <td colspan="5" style="height:2px; line-height:2px;">
                        <div class="dotted_line" style="margin-top:2px;"></div>
                    </td>
                </tr>
                <tr>
                    <td width="100%" colspan="5" style="height:26px;">
                        <table cellpadding="0" cellspacing="0">
                            <tr>
                                <td>
                                    <b>
                                        <%= tag.getName() %>
                                    </b>
                                </td>
                                <td>
                                    <% String editorId = "cm." + tag.getType() + "." + tag.getName(); %>
                                    <%if (tag.getType() == TagUtils.TAG_TEXT) {%>
                                    &nbsp;<img style="cursor:pointer;" src="${emmLayoutBase.imagesURL}/edit.gif" border="0" class="ckeditor-toggle-button"
                                               onclick="toggleEditor('<%= editorId %>', 450, 250, 0); return false;" alt="<bean:message key="htmled.title"/>">
                                    <%}%>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <% if (tag.getType() == TagUtils.TAG_TEXT) {%>
                <tr>
                    <input type="hidden" name='<%= "editor." + editorId %>'
                           id="<%= "editor." + editorId %>" value="text"/>
                    <td width="40px"></td>
                    <td width="100%">
                        <div id='Textarea.<%= editorId %>'>
                            <textarea styleId="newContent" id='<%= editorId %>' rows="4" cols="75" name="<%= editorId %>"
                                      style="width:350px;"><%=tag.getValue()%></textarea>
                        </div>

                    </td>
                </tr>

                <% } else if (tag.getType() == TagUtils.TAG_LABEL || tag.getType() == TagUtils.TAG_LINK) {%>

                <tr>
                    <td width="40px"></td>
                    <td width="100%">
                        <% if (tag.getType() == TagUtils.TAG_LINK) { %>
                           <html:errors property='<%="url_link_"+tag.getName()%>'/>
                        <%}%>
                        <input type="text"
                               name='<%= "cm." + tag.getType() + "." + tag.getName() %>'
                               value="<%=tag.getValue() %>"
                               style="width:350px;"/>
                    </td>
                </tr>

                <% } else if (tag.getType() == TagUtils.TAG_IMAGE) {%>
                <% if (aForm.isValidState()) {%>
                <tr>
                    <td colspan="5">
                        <table width="100%">
                            <tr>
                                <td colspan="2">
                                    <iframe src="<%= tag.getValue() %>"
                                            width="150"
                                            height="150">"Your Browser does
                                        not support IFRAMEs, please
                                        update!
                                    </iframe>
                                </td>
                                <td width="100%" valign="top">
                                    <table cellpadding="1" width="100%">
                                        <tr>
                                            <td width="100%" colspan="2">
                                                <input type="radio"
                                                       name='<%= "cm." + tag.getType() + "." + tag.getName() + ".select"%>'
                                                       value="upload"
                                                       checked="1">
                                                Upload image
                                            </td>
                                        </tr>
                                        <tr>
                                            <td>
                                                &nbsp;&nbsp;&nbsp;&nbsp;</td>
                                            <td width="100%">
                                                <input type="file"
                                                       name='<%= "cm." + tag.getType() + "." + tag.getName() + ".file" %>'>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td width="100%" colspan="2">
                                                <input type="radio"
                                                       name='<%= "cm." + tag.getType() + "." + tag.getName() + ".select"%>'
                                                       value="external">
                                                Use external image
                                            </td>
                                        </tr>
                                        <tr>
                                            <td>
                                                &nbsp;&nbsp;&nbsp;&nbsp;</td>
                                            <td width="100%">
                                                <input type="text"
                                                       name='<%= "cm." + tag.getType() + "." + tag.getName() + ".url" %>'
                                                       style="width:250px;">
                                            </td>
                                        </tr>
                                    </table>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <% } else {%>
                <% if (tag instanceof CmsImageTag) {
                    final CmsImageTag cmsImageTag = (CmsImageTag) tag;%>
                <tr>
                    <td colspan="5">
                        <table width="100%">
                            <tr>
                                <td colspan="2">
                                    <iframe src="<%= cmsImageTag.getValue() %>"
                                            width="150"
                                            height="150">"Your Browser does
                                        not support IFRAMEs, please
                                        update!
                                    </iframe>
                                </td>
                                <td width="100%" valign="top">
                                    <table cellpadding="1" width="100%">
                                        <tr>
                                            <td width="100%" colspan="2">
                                                <input type="radio"
                                                       name='<%= "cm." + tag.getType() + "." + tag.getName() + ".select"%>'
                                                       value="upload"
                                                        <% if (cmsImageTag.isUpload()){%>
                                                       checked="1"
                                                        <%}%>
                                                        >
                                                Upload image
                                            </td>
                                        </tr>
                                        <tr>
                                            <td>
                                                &nbsp;&nbsp;&nbsp;&nbsp;</td>
                                            <td width="100%">
                                                <input type="file"
                                                       name='<%= "cm." + tag.getType() + "." + tag.getName() + ".file" %>'
                                                       value="<%=cmsImageTag.getNewValue()%>">
                                            </td>
                                        </tr>
                                        <tr>
                                            <td width="100%" colspan="2">
                                                <input type="radio"
                                                       name='<%= "cm." + tag.getType() + "." + tag.getName() + ".select"%>'
                                                       value="external"
                                                        <% if (!cmsImageTag.isUpload()){%>
                                                       checked="1"
                                                        <%}%>
                                                        >
                                                Use external image
                                            </td>
                                        </tr>
                                        <tr>
                                            <td>
                                                &nbsp;&nbsp;&nbsp;&nbsp;</td>
                                            <td width="100%">
                                                <html:errors property='<%="url_img_"+tag.getName()%>'/>
                                                <input type="text"
                                                       name='<%= "cm." + tag.getType() + "." + tag.getName() + ".url" %>'
                                                        <% if (!cmsImageTag.isUpload()){ %>
                                                       value="<%=cmsImageTag.getNewValue()%>"
                                                        <% } %>
                                                       style="width:250px;">
                                            </td>
                                        </tr>
                                    </table>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <% } %>
                <% } %>
                <% } %>
                <% } %>
            </table>

        </div>
        <div class="blue_box_bottom"></div>
    </div>

    <div class="button_container">

        <logic:notEqual name="contentModuleForm" property="contentModuleId" value="0">
            <input type="hidden" id="delete" name="delete" value=""/>
            <div class="action_button">
                <html:link page='<%= "/cms_contentmodule.do?action=" + ContentModuleAction.ACTION_CONFIRM_DELETE + "&contentModuleId=" + aForm.getContentModuleId() + "&fromListPage=false" %>'>
                    <span><bean:message key="button.Delete"/></span>
                </html:link>
            </div>

            <input type="hidden" id="copy" name="copy" value=""/>
            <div class="action_button">
                <html:link page='<%= "/cms_contentmodule.do?action=" + ContentModuleAction.ACTION_COPY + "&contentModuleId=" + aForm.getContentModuleId() %>'>
                    <span><bean:message key="button.Copy"/></span>
                </html:link>
            </div>

            <div class="action_button">
                <html:link page='<%= "/cms_contentmodule.do?action=" + ContentModuleAction.ACTION_ASSIGN_LIST + "&contentModuleId=" + aForm.getContentModuleId() %>'><span><bean:message key="cms.AssignToMailing"/></span></html:link>
            </div>
        </logic:notEqual>

        <input type="hidden" id="save" name="save" value=""/>
        <div class="action_button">
            <a href="#" onclick="removeAllEditors(); document.getElementById('save').value='true'; document.contentModuleForm.submit(); return false;"><span><bean:message key="button.Save"/></span></a>
        </div>

        <div class="action_button"><bean:message key="cms.ContentModule"/>:</div>
    </div>

</html:form>