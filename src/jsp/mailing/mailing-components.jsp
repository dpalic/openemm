<%--checked --%>
<%@ page language="java" import="org.agnitas.beans.MailingComponent, org.agnitas.beans.TrackableLink, org.agnitas.util.AgnUtils" contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.web.forms.MailingComponentsForm" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<script type="text/javascript" src="<%=request.getContextPath()%>/js/lightbox/lightbox.js"></script>

<script type="text/javascript">
	var component_counter = 1;

	function moreComponents() {
		component_counter++;

		table = document.getElementById( "component_table");
		table.appendChild( createComponentFileRow( component_counter));
		table.appendChild( createComponentLinkRow( component_counter));
		table.appendChild( createComponentSpacerRow( component_counter));
	}

	function createComponentFileRow( rowId) {
		tr = document.createElement( "tr");
		tr.id = "component_file_" + rowId;

		td = document.createElement( "td");
		td.appendChild( document.createTextNode( '<bean:message key="mailing.New_Component"/>: '));

		tr.appendChild( td);
		tr.appendChild( createFileUploadCells( "newFile[" + rowId + "]"));

		return tr;
	}

	function createComponentLinkRow( rowId) {
		tr = document.createElement( "tr");
		tr.id = "component_link_" + rowId;

		td = document.createElement( "td");
		td.appendChild( document.createTextNode( '<bean:message key="mailing.ComponentLink"/>: '));
		tr.appendChild( td);
		tr.appendChild( createComponentLinkCells( "link[" + rowId + "]"));

		return tr;
	}

	function createComponentSpacerRow( rowId) {
		tr = document.createElement( "tr");
		tr.id = "component_spacer_" + rowId;

		td = document.createElement( "td");
		td.colSpan = 3;
		td.appendChild( document.createElement( "br"));
		tr.appendChild( td);

		return tr;
	}

	function createFileUploadCells( name) {
		td = document.createElement( "td");
		td.colSpan = 2;

		elem = document.createElement( "input");
		elem.type = "file";
		elem.name = name;

		td.appendChild( elem);

		return td;
	}


	function createComponentLinkCells( name) {
		td = document.createElement( "td");
		td.colSpan = 2;

		elem = document.createElement( "input");
		elem.type = "text";
		elem.name = name;

		td.appendChild( elem);

		return td;
	}

    function imageLoaded(imgElement, dimensionLabel) {
        dimensionLabel.value = imgElement.offsetWidth + ' x ' + imgElement.offsetHeight;
        var maxWidth = 366;
        var maxHeight = 136;
        if (imgElement.offsetWidth > maxWidth || imgElement.offsetHeight > maxHeight) {
            var scaleX = maxWidth / imgElement.offsetWidth;
            var scaleY = maxHeight / imgElement.offsetHeight;
            var scale = scaleY;
            if (scaleX < scaleY) {
                scale = scaleX;
            }
            var newWidth = scale * imgElement.offsetWidth;
            var newHeight = scale * imgElement.offsetHeight;
            imgElement.width = newWidth;
            imgElement.height = newHeight;
        }
    }
</script>

<%
    MailingComponentsForm aForm = null;
    if (request.getAttribute("mailingComponentsForm") != null) {
        aForm = (MailingComponentsForm) request.getAttribute("mailingComponentsForm");
    }
%>

<html:form action="/mcomponents" enctype="multipart/form-data">
<html:hidden property="mailingID"/>
<html:hidden property="action"/>
   <div id="filterbox_container" style="margin-left:28px;">
        <div class="filterbox_form_container" >
            <div id="filterbox_top"></div>
            <div id="suchbox_content" class="filterbox_form_container">
                <label style="margin-left:10px;">${mailingComponentsForm.shortname}&nbsp;&nbsp;<%if (aForm != null && aForm.getDescription() != null && !aForm.getDescription().isEmpty()) {%>|&nbsp;&nbsp;${mailingComponentsForm.description}<% } %></label>
            </div>
            <div id="filterbox_bottom"></div>
        </div>
    </div>
    <agn:ShowByPermission token="mailing.graphics_upload">
        <div class="mailing_name_box_container">
            <div class="mailing_name_box_top"></div>
            <div class="mailing_name_box_content">
                <div class="graphic_component_upload_form_item">
                    <label for="new_component_upload"><bean:message key="mailing.New_Component"/>:</label>
                    <html:file property="newFile"/>
                </div>
                <div class="graphic_component_upload_form_item">
                    <label for="new_component_link_target"><bean:message key="mailing.ComponentLink"/>:</label>
                    <html:text property="link"/>

                </div>

                <div class="graphic_component_upload_form_item graphic_component_upload_button_container">
                        <%--<label for="new_component_description"><bean:message key="default.description"/>:</label>
                                    <html:text property="description"/>
                        --%>
                    <div class="maildetail_button"><a href="#"
                                                      onclick="document.mailingComponentsForm.submit();"><span><bean:message
                            key="button.Upload"/></span></a></div>
                </div>
            </div>
            <div class="mailing_name_box_bottom"></div>
        </div>
    </agn:ShowByPermission>

  <agn:HibernateQuery id="component" query='<%= new String(\"from MailingComponent where companyID=\"+AgnUtils.getCompanyID(request)+\" and mailingID=\"+request.getAttribute("tmpMailingID")+\" ORDER BY componentName\") %>'>

                 <% MailingComponent comp=(MailingComponent)pageContext.getAttribute("component"); %>
            <% if(comp.getType()==MailingComponent.TYPE_IMAGE||comp.getType()==MailingComponent.TYPE_HOSTED_IMAGE)  { %>
            <div class="graphic_component_container">
                	 <div class="graphic_component_info_container">
                     	<div class="graphic_component_info_form_container">

                            <div class="graphic_component_form_item">
                    			<label for="graphic_component_name_1"><bean:message key="mailing.Graphics_Component"/>:</label>
                        		<input id="graphic_component_name_1" type="text" readonly="true"  value="${component.componentName}"/>
                            </div>

                            <%if(comp.getType()==MailingComponent.TYPE_HOSTED_IMAGE) { %>
                            <%--<div class="graphic_component_form_item">
                                <label for="graphic_component_description_1"><bean:message key="default.description"/>:</label>
                                <input id="graphic_componentt_description_1" type="text" readonly="true" value="${component.description}" />
                            </div>--%>
                                <c:if test="${component.urlID == 0}">
                                    <div class="graphic_component_form_item no_component_form_item">&nbsp;</div>
                                </c:if>
                                <% TrackableLink link = null; %>

               	        	<agn:HibernateQuery id="url" query='<%= new String(\"from TrackableLink where companyID=\" + AgnUtils.getCompanyID(request) + \" and mailingID=\" + request.getAttribute("tmpMailingID") + \" and url_id=\" + comp.getUrlID()) %>'>
                                <% link = (TrackableLink) pageContext.getAttribute("url");
                                if(link != null) {
                                    String full = link.getFullUrl();
                                    System.err.println("link" + full);
                                    if(!full.equals("")) { %>
                                    <div class="graphic_component_form_item">
                                        <label for="graphic_component_link_target_1"><bean:message key="htmled.link"/>:</label>
                                        <input id="graphic_component_link_target_1" type="text" readonly="true" value="<%= full %>" />
                                    </div>

                                <% }
                                } %>
                                </agn:HibernateQuery>

                            <%
                                }
                            %>
                            <% if(comp.getType()==MailingComponent.TYPE_IMAGE) { %>
                                <div class="graphic_component_form_item no_component_form_item">&nbsp;</div>
                                <div class="graphic_component_form_item no_component_form_item">&nbsp;</div>
                            <%
                                }
                            %>

                            <div class="graphic_component_file_info">
                            	<div class="graphic_component_file_info_item">
                                	<label for="graphic_component_file_format_1"><bean:message key="mailing.Graphics_Component.FileFormat"/></label>
                                    <input id="graphic_component_file_format_1" type="text" disabled="disabled" value="${component.mimeType}" />
                                    <label for="graphic_component_file_measures_${component.id}"><bean:message key="mailing.Graphics_Component.Dimensions"/></label>
                                    <input id="graphic_component_file_measures_${component.id}" type="text" disabled="disabled" value=""/>
                                    <%--<div class="dotted_line"></div>
                                    <label for="graphic_component_file_size_1"><bean:message key="mailing.Graphics_Component.FileSize"/></label>
                                    <input id="graphic_component_file_size_1" type="text" disabled="disabled" value="${mailingComponentsForm.fileSizes[component.id]} kB" />
                                    <label for="graphic_component_file_added_1"><bean:message key="mailing.Graphics_Component.AddDate"/></label>
                                    <input id="graphic_component_file_added_1" type="text" disabled="disabled" value="${mailingComponentsForm.timestamps[component.id]}" />--%>
                                </div>
                            </div>

                        </div>
                     </div>
                     <div class="graphic_component_image_container"><table><tr><td>
                         <a href="<html:rewrite page="/sc?compID=${component.id}"/>" rel="lightbox">
                            <img src="<html:rewrite page="/sc?compID=${component.id}" />" alt="" border="1"
                                 onload="imageLoaded(this, document.getElementById('graphic_component_file_measures_${component.id}'));"/>
                         </a>
                     </td></tr></table></div>
                     <div class="graphic_component_icons_container">
                        <a href="<html:rewrite page="/sc?compID=${component.id}"/>" rel="lightbox" class="mailing_preview"></a>

                        <c:if test="${component.type == MAILING_COMPONENT_TYPE_HOSTED_IMAGE}">
                            <input type="hidden" name="delete${component.id}" value=""/>
                            <a href="#" onclick="document.mailingComponentsForm.delete${component.id}.value='delete'; document.mailingComponentsForm.submit();" title="<bean:message key="mailing.Graphics_Component.Delete"/>" class="mailing_delete"></a>
                        </c:if>

                        <c:if test="${component.type == MAILING_COMPONENT_TYPE_IMAGE}">
                            <input type="hidden" name="update${component.id}" value=""/>
                            <a href="#" onclick="document.mailingComponentsForm.update${component.id}.value='update'; document.mailingComponentsForm.submit();" title="<bean:message key="mailing.Graphics_Component.Update"/>" class="picture_refresh"></a>
                        </c:if>

                     </div>

                </div>
            <%
                }
            %>


		</agn:HibernateQuery>

</html:form>

<script language="javascript">

    if ($('targetlink')) {
        var helpBalloonTargetlink = new HelpBalloon({
            dataURL: 'help_${helplanguage}/mailing/picture_components/LinkTargetForPicture.xml'
        });
        $('targetlink').insertBefore(helpBalloonTargetlink.icon, $('targetlink').childNodes[0]);
    }

</script>