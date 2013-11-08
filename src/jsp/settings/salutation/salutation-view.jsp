<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.web.SalutationAction"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%
    int tmpSalutationID=(Integer)request.getAttribute("tmpSalutationID");
%>

<html:form action="/salutation">
    <html:hidden property="salutationID"/>
    <html:hidden property="action"/>

    <div class="mailing_name_box_container">
        <div class="mailing_name_box_top"></div>
        <div class="mailing_name_box_content">
            <div class="mailing_name_box_center_column">
                <c:if test="${tmpSalutationID!=0}">

                    <div class="emailbox_form_item">
                        <label for="mailing_name">ID:</label>

                        <label>${tmpSalutationID}</label>

                    </div>
                </c:if>

                <div class="emailbox_form_item">
                    <label for="mailing_name"><bean:message key="default.description"/>:</label>
                    <html:text styleId="mailing_name" property="shortname" size="32"/>
                </div>
            </div>
            <div class="mailing_name_box_right_column"></div>
        </div>
        <div class="mailing_name_box_bottom"></div>
    </div>

    <div class="emailbox_container">
        <div class="emailbox_top"></div>
        <div class="emailbox_content">

            <div class="emailbox_left_column">
                <div class="emailbox_form_item_salutation">
                    <label class="emailbox_form_item_salutation_label" for="salMale">GENDER=0 (<bean:message
                            key="recipient.Male"/>):</label>
                    <html:text styleId="salMale" property="salMale" maxlength="199"/>

                </div>
                <div class="emailbox_form_item_salutation">
                    <label class="emailbox_form_item_salutation_label" for="salFemale">GENDER=1 (<bean:message
                            key="recipient.Female"/>):</label>
                    <html:text styleId="salFemale" property="salFemale" maxlength="99"/>
                </div>
                <div class="emailbox_form_item_salutation">
                    <label class="emailbox_form_item_salutation_label" for="salUnknown">GENDER=2 (<bean:message
                            key="recipient.Unknown"/>):</label>
                    <html:text styleId="salUnknown" property="salUnknown" maxlength="99"/>
                </div>

            </div>
        </div>
        <div class="emailbox_bottom"></div>
    </div>

    <p>

    <div class="target_button_container">

        <input type="hidden" id="save" name="save" value=""/>

        <div class="maildetail_button">
            <a href="#"
               onclick="document.getElementById('save').value='save'; document.salutationForm.submit(); return false;"><span><bean:message
                    key="button.Save"/></span></a>
        </div>


        <div class="maildetail_button">
            <html:link
                    page='<%= new String("/salutation.do?action=" + SalutationAction.ACTION_LIST) %>'>
                <span><bean:message key="button.Cancel"/></span>
            </html:link>
        </div>
    </div>
</html:form>