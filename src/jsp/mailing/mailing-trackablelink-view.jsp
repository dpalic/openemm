<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.actions.EmmAction, org.agnitas.util.AgnUtils, org.agnitas.web.TrackableLinkAction" %>
<%@ page import="org.agnitas.web.TrackableLinkForm" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<agn:CheckLogon/>

<agn:Permission token="mailing.content.show"/>

<% int tmpMailingID=0;
    TrackableLinkForm aForm=null;
    if(request.getAttribute("trackableLinkForm")!=null) {
        aForm=(TrackableLinkForm)request.getAttribute("trackableLinkForm");
        tmpMailingID=aForm.getMailingID();
    }
%>
<div class="emailbox_container">
<html:form action="/tracklink">
    <html:hidden property="mailingID"/>
    <html:hidden property="linkID"/>
    <html:hidden property="action"/>
    <div class="assistant_trackablelinks_form_item">
            <label><bean:message key="mailing.URL"/>:&nbsp;</label>
            <bean:write name="trackableLinkForm" property="linkUrl"/>
        </div>

        <div class="assistant_trackablelinks_form_item">
            <label><bean:message key="default.description"/>:&nbsp;</label>
            <html:text property="linkName" size="52" maxlength="99"/>
        </div>

        <div class="assistant_trackablelinks_form_item">
            <label><bean:message key="mailing.Trackable"/>:&nbsp;</label>
            <html:select property="trackable">
                <html:option value="0"><bean:message key="mailing.Not_Trackable"/></html:option>
                <html:option value="1"><bean:message key="mailing.Only_Text_Version"/></html:option>
                <html:option value="2"><bean:message key="mailing.Only_HTML_Version"/></html:option>
                <html:option value="3"><bean:message key="mailing.Text_and_HTML_Version"/></html:option>
            </html:select>
        </div>

        <div class="assistant_trackablelinks_form_item">
            <label><bean:message key="action.Action"/>:&nbsp;</label>
            <html:select property="linkAction" size="1">
                <html:option value="0"><bean:message key="settings.No_Action"/></html:option>
                <agn:HibernateQuery id="action" query='<%= \"from EmmAction where companyID=\"+AgnUtils.getCompanyID(request)+\" and type<>\"+EmmAction.TYPE_FORM %>'>
                    <html:option value="${action.id}">${action.shortname}</html:option>
                </agn:HibernateQuery>
            </html:select>
            
        </div>

    <div class="maildetail_button mailingwizard_add_button">
        <a href="#"
           onclick="document.trackableLinkForm.submit(); return false;"><span><bean:message
                key="button.Save"/></span></a>
    </div>
    <div class="maildetail_button mailingwizard_add_button">
        <html:link
                page='<%=new String("/tracklink.do?action=" + TrackableLinkAction.ACTION_LIST + "&mailingID=" + tmpMailingID)%>'>
            <span><bean:message key="button.Cancel"/></span></html:link>
    </div>
</html:form>
</div>
