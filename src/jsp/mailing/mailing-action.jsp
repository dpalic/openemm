<%--checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.web.EmmActionAction"
         errorPage="/error.jsp" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="ACTION_VIEW" value="<%= EmmActionAction.ACTION_VIEW %>" scope="page" />

<html:form action="/mailingbase" styleClass="top_10">
    <html:hidden property="mailingID"/>
    <html:hidden property="action"/>

    <table border="0" cellspacing="0" cellpadding="0" class="list_table" id="actions">
        <thead>
            <tr>
                <th><span class="mailing_action_head_action"><bean:message key="action.Action"/></span></th>
                <th><span class="mailing_action_head_url"><bean:message key="mailing.URL"/>&nbsp;</span></th>
            </tr>
        </thead>

        <c:if test="${!empty mailingBaseForm.actions}">
            <c:set var="index" value="0" scope="request"/>
            <logic:iterate id="action" name="mailingBaseForm" property="actions" indexId="index">
                <c:set var="trStyle" value="even" scope="request"/>
                <c:if test="${(index mod 2) == 0}">
                    <c:set var="trStyle" value="odd" scope="request"/>
                </c:if>
                <tr class="${trStyle}">
                    <td>
                        <span class="ie7hack">
                            <html:link page="/action.do?action=${ACTION_VIEW}&actionID=${action[\"action_id\"]}" title="${action[\"url\"]}">
                                ${action["action_name"]}
                            </html:link>
                        </span>
                    </td>
                    <td>
                        <span class="ie7hack">
                            ${action["url"]}
                        </span>
                    </td>
                </tr>
            </logic:iterate>
        </c:if>
        <c:if test="${empty mailingBaseForm.actions}">
            <tr>
                <td colspan="2">
                    <span class="ie7hack">
                        <bean:message key="mailing.noActionsLinked"/>
                    </span>
                </td>
            </tr>
        </c:if>
    </table>

    <div class="action_button mailing_action_back">
        <html:link page="/mailingbase.do?action=${mailingBaseForm.previousAction}" >
            <span><bean:message key="button.Back"/></span>
        </html:link>
    </div>

    <script type="text/javascript">
        $$('#actions tbody tr').each(function(item) {
            item.observe('mouseover', function() {
                item.addClassName('list_highlight');
            });
            item.observe('mouseout', function() {
                item.removeClassName('list_highlight');
            });
        });
    </script>

</html:form>

