<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.web.BlacklistAction, java.net.URLEncoder" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<div class="new_mailing_start_description"><bean:message
        key="Recipient"/>:&nbsp;<%= request.getParameter("delete") %> <br>
    <bean:message key="recipient.blacklist.delete"/>
</div>
<div class="remove_element_button_container">
    <div class="greybox_small_top"></div>
    <div class="greybox_small_content">
        <div class="new_mailing_step1_left_column">
            <div class="big_button"><a href="<html:rewrite page='<%= new String(\"/blacklist.do?action=\"+BlacklistAction.ACTION_DELETE+\"&delete=\" + URLEncoder.encode((String)request.getParameter(\"delete\"), \"UTF-8\")) %>'/>"><span><bean:message
                    key="button.Delete"/></span></a></div>
        </div>
        <div class="new_mailing_step1_right_column">
            <div class="big_button"><a
                    href="<html:rewrite page='<%= new String(\"/blacklist.do?action=\"+BlacklistAction.ACTION_LIST) %>'/>"><span><bean:message
                    key="button.Cancel"/></span></a></div>
        </div>
    </div>
    <div class="greybox_small_bottom"></div>
</div>