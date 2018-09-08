<%-- checked --%>
<%@ page language="java"
         import="org.agnitas.util.AgnUtils, org.agnitas.web.AdminAction, org.agnitas.beans.Admin, java.util.Locale, java.util.TimeZone, org.agnitas.emm.core.logintracking.*, java.text.DateFormat"
         contentType="text/html; charset=utf-8" errorPage="/error.jsp" %>
<%@ page import="org.agnitas.beans.AdminPreferences" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<c:set var="ACTION_VIEW" value="<%= AdminAction.ACTION_VIEW %>"/>

<c:set var="LOGIN_STATUS_SUCCESS" value="<%= LoginStatus.SUCCESS.getStatusCode() %>" />
<c:set var="LOGIN_STATUS_FAIL" value="<%= LoginStatus.FAIL.getStatusCode() %>" />
<c:set var="LOGIN_STATUS_SUCCESS_BUT_BLOCKED" value="<%= LoginStatus.SUCCESS_BUT_BLOCKED.getStatusCode() %>" />

<c:set var="MAILING_CONTENT_HTML_EDITOR" value="<%= AdminPreferences.MAILING_CONTENT_HTML_EDITOR %>"/>
<c:set var="MAILING_CONTENT_HTML_CODE" value="<%= AdminPreferences.MAILING_CONTENT_HTML_CODE %>"/>

<c:set var="MAILING_SETTINGS_EXPANDED" value="<%= AdminPreferences.MAILING_SETTINGS_EXPANDED %>"/>
<c:set var="MAILING_SETTINGS_COLLAPSED" value="<%= AdminPreferences.MAILING_SETTINGS_COLLAPSED %>"/>

<script src="settings/admin/PasswordRanking.js" type="text/javascript"></script>
<script type="text/javascript">
    var rank = new PasswordRanking();
</script>

<%
    Admin admin = AgnUtils.getAdmin(request);
%>

<script type="text/javascript">
    function checkPasswordMatch() {
        var matching = rank.checkMatch('password', 'repeat');

        if (matching) {
            document.getElementById('different_passwords').style.display = '';
        } else {
            document.getElementById('different_passwords').style.display = 'none';
        }

        veDiv = document.getElementById('validation_errors');
        if (veDiv != null) {
            veDiv.style.display = 'none';
        }

        rank.enableButton('save_btn', 'button.Save', matching);
    }

    Event.observe(window, 'load', function() {
	    var passwordValue = document.getElementById('password').value;
	    
		if (!passwordValue) {
		    rank.enableButton('save_btn', 'button.Save', false);
		} else {
		    rank.enableButton('save_btn', 'button.Save', true);
		}
    });

</script>
<div class="statusbox_containter top_10" id="different_passwords" style="display: none;">
    <div class="statusbox_top"></div>
    <div class="statusbox_content">
        <div class="status_error"><bean:message key="error.admin.different_passwords"/></div>
    </div>
    <div class="statusbox_bottom"/>
</div>
</div>

<html:form action="selfservice.do?action=save" focus="username" onsubmit="return !rank.checkMatch('password', 'repeat');">

    <html:hidden property="action"/>
    <html:hidden property="adminID"/>
    <html:hidden property="previousAction" value="${ACTION_VIEW}"/>


    <div class="grey_box_container">
        <div class="grey_box_top"></div>
        <div class="grey_box_content">
            <div class="grey_box_left_column">
                <label for="mailing_name"><bean:message key="default.Name"/>:&nbsp;</label>
                <html:text styleId="mailing_name" property="fullname" maxlength="99" size="32"/>
            </div>
            <div class="grey_box_center_column"></div>
            <div class="grey_box_right_column"></div>
        </div>
        <div class="grey_box_bottom"></div>
    </div>


    <div class="blue_box_container">
        <div class="blue_box_top"></div>
        <div class="blue_box_content">
        	<div class="blue_box_content_left">

                <div class="admin_filed_detail_topic_item">
                    <bean:message key="settings.UserSettings"/>
                </div>

	            <div class="admin_filed_detail_form_item">
	                <label for="username"><bean:message key="logon.username"/>:&nbsp;</label>
	                ${adminForm.username}
	            </div>

	            <div class="admin_filed_detail_form_item">
	                <label for="password"><bean:message key="logon.password"/>:&nbsp;</label>
	
	                <html:password property="password" styleId="password"
	                               onkeyup="rank.securityCheck('bar', this.id); checkPasswordMatch();" size="52"
	                               maxlength="99"/>
	            </div>

	            <div class="admin_filed_detail_form_item">
	                <label for="repeat"><bean:message key="settings.admin.Confirm"/>:&nbsp;</label>
	
	                <html:password property="passwordConfirm" styleId="repeat" onkeyup="checkPasswordMatch();" size="52"
	                               maxlength="99"/>
	            </div>

                <div class="admin_filed_detail_form_item">
                    <script type="text/javascript">rank.showBar("bar", "<bean:message key='settings.secure'/>", "<bean:message key='settings.insecure'/>");</script>
                </div>

                <agn:ShowByPermission token="admin.setgroup">
                    <div class="admin_filed_detail_form_item">
                        <label for="groupID"><bean:message key="settings.Usergroup"/>:&nbsp;</label>

                        <html:select property="groupID" size="1" styleId="groupID">
                            <html:option value="0"><bean:message key="settings.Usergroup.none"/></html:option>
                            <c:forEach var="adminGroup" items="${adminGroups}">
                                <html:option value="${adminGroup.groupID}">
                                    ${adminGroup.shortname}
                                </html:option>
                            </c:forEach>
                        </html:select>
                    </div>
                </agn:ShowByPermission>

                <% if (!AgnUtils.allowed("admin.setgroup", request)) { %>
                <html:hidden property="groupID"/>

                <div class="admin_filed_detail_form_item">
                    <label for="groupIDShow"><bean:message key="settings.Usergroup"/>:&nbsp;</label>
                    <% String groupName= admin.getGroup().getShortname();
                        if (groupName.equals("Dummy")) groupName ="None";%>
                    <%= groupName%>
                </div>
                <% } %>

                <div class="admin_filed_detail_form_item">
	                <label for="language"><bean:message key="settings.Language"/>:&nbsp;</label>
	
	                <html:select property="language" size="1" styleId="language">
	                    <html:option value="<%= Locale.US.toString() %>"><bean:message key="settings.English"/></html:option>
	                    <html:option value="NL_nl"><bean:message key="settings.Dutch"/></html:option>
	                    <html:option value="<%= Locale.FRANCE.toString() %>"><bean:message key="settings.French"/></html:option>
	                    <html:option value="<%= Locale.GERMANY.toString() %>"><bean:message key="settings.German"/></html:option>
	                    <html:option value="<%= Locale.ITALY.toString() %>"><bean:message key="settings.Italian" /></html:option>
	                    <html:option value="PT_pt"><bean:message key="settings.Portuguese"/></html:option>
	                    <html:option value="ES_es"><bean:message key="settings.Spanish"/></html:option>
	                    <html:option value="<%= Locale.CHINA.toString() %>"><bean:message key="settings.Chinese"/></html:option>
	                </html:select>
	
	            </div>
	
	            <div class="admin_filed_detail_form_item">
	                <label for="adminTimezone"><bean:message key="settings.Timezone"/>:&nbsp;</label>
	
	                <html:select property="adminTimezone" size="1" styleId="adminTimezone">
	                    <% String allZones[] = TimeZone.getAvailableIDs();
	                        int len = allZones.length;
	                        TimeZone tmpZone = TimeZone.getDefault();
	                        Locale aLoc = (Locale) session.getAttribute("messages_lang");
	                        for (int i = 0; i < len; i++) {
	                            tmpZone.setID(allZones[i]);
	                    %>
	                    <html:option value="<%= allZones[i] %>"><%= /* tmpZone.getDisplayName(aLoc) */ allZones[i] %>
	                    </html:option>
	                    <% } %>
	                </html:select>
	
	            </div>

                <div class="admin_filed_detail_topic_item">
                    <bean:message key="settings.AccountSettings"/>
                </div>

                <div class="admin_filed_detail_form_item">
	                <label for="numberofRows"><bean:message key="settings.Admin.numberofrows"/>:&nbsp;</label>
	
	                <html:select property="numberofRows" styleId="numberofRows">
	                    <%
	                        String[] sizes = {"20", "50", "100"};
	                        for (int i = 0; i < sizes.length; i++) {
	                    %>
	                    <html:option value="<%= sizes[i] %>"><%= sizes[i] %>
	                    </html:option>
	                    <%
	                        }
	                    %>
	                </html:select>
	            </div>

                    <%-- Content Blocks --%>
                <div class="admin_filed_detail_form_item">
                    <label for="mailingContentView"><bean:message key="mailing.content.contentblocks"/>:&nbsp;</label>
                    <html:select property="mailingContentView" size="1" styleId="mailingContentView">
                        <html:option value="${MAILING_CONTENT_HTML_EDITOR}"><bean:message key="mailingContentHTMLEditor"/></html:option>
                        <html:option value="${MAILING_CONTENT_HTML_CODE}"><bean:message key="mailingContentHTMLCode"/></html:option>
                    </html:select>
                </div>

                    <%--Mailing settings--%>
                <agn:HideByPermission token="mailing.settings.hide"> <%--change to "ShowByPermission" when permission will be found--%>
                    <div class="admin_filed_detail_form_item">
                        <label for="mailingSettingsView"><bean:message key="mailing.settings"/>:&nbsp;</label>
                        <html:select property="mailingSettingsView" size="1" styleId="mailingSettingsView">
                            <html:option value="${MAILING_SETTINGS_EXPANDED}"><bean:message key="mailing.settings.expanded"/></html:option>
                            <html:option value="${MAILING_SETTINGS_COLLAPSED}"><bean:message key="mailing.settings.collapsed"/></html:option>
                        </html:select>
                    </div>
                </agn:HideByPermission>

                <html:hidden property="companyID" value="1"/>
	         </div><%-- End of "blue_box_content_left" --%>
	    
	    	<div class="blue_box_content_right">
	    		<div class="scrollable_content">
	    			<agn:ShowByPermission token="admin.login.fails.showWarning">
	    				<% 
	    					Locale userLocale = AgnUtils.getAdmin(request).getLocale();
	    					DateFormat dateFormat = DateFormat.getDateTimeInstance( DateFormat.MEDIUM, DateFormat.LONG, userLocale);
	    				%>
						<display:table class="list_table selfservice" id="loginData" name="login_tracking_list">
							<display:column headerClass="head_action" class="action" titleKey="warning.failed_login.date">
								<%= dateFormat.format(((LoginData) loginData).getLoginTime()) %>
<%--								${loginData.loginTime} --%>
							</display:column>
							<display:column headerClass="head_action" class="action" titleKey="warning.failed_login.ip">
								${loginData.loginIP}
							</display:column>
							<display:column headerClass="head_action" class="action" titleKey="warning.failed_login.status">
								<c:choose>
									<c:when test="${loginData.loginStatus.statusCode == LOGIN_STATUS_SUCCESS}">
										<bean:message key="warning.failed_login.status.success" />
									</c:when>
									<c:when test="${loginData.loginStatus.statusCode == LOGIN_STATUS_FAIL}">
										<bean:message key="warning.failed_login.status.failed" />
									</c:when>
									<c:when test="${loginData.loginStatus.statusCode == LOGIN_STATUS_SUCCESS_BUT_BLOCKED}">
										<bean:message key="warning.failed_login.status.blocked" />
									</c:when>
								</c:choose>
							</display:column>
						</display:table>
					</agn:ShowByPermission>						
				</div>
	    	</div>     
        </div><%-- End of "blue_box_content" --%>
        <div class="blue_box_bottom"></div>
    </div>
    
    <div class="button_container" style="padding-top:5px;">
        <input type="hidden" name="save" value=""/>
        <agn:ShowByPermission token="admin.change">
            <div class="action_button" id='save_btn'>
            	<a href="#" onclick=" document.adminForm.save.value='save'; document.adminForm.submit();return false;">
            		<span><bean:message key="button.Save"/></span>
                </a>
            </div>
        </agn:ShowByPermission>
    </div>

</html:form>