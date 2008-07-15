<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.ImportWizardForm" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<agn:CheckLogon/>

<agn:Permission token="wizard.import"/>

<% pageContext.setAttribute("sidemenu_active", new String("Recipient")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("csv_upload")); %>
<% pageContext.setAttribute("agnTitleKey", new String("UploadSubscribers")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("UploadSubscribers")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("subscriber_import")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("ImportWizard")); %>

<%@include file="/header.jsp"%>

<b><font color=#73A2D0><bean:message key="ImportWizStep_1_of_7"/></font></b>
<br>                
<html:errors/>
<table border="0" cellspacing="0" cellpadding="0" width="100%">
    <html:form action="/importwizard" enctype="multipart/form-data">
        <html:hidden property="action"/>
        <tr>
            <td colspan="2"><b><bean:message key="FileName"/>:</b><br>
                <html:file property="csvFile"/>
            </td>

            <td>&nbsp;&nbsp;</td>
        </tr>

        <tr>
            <td>
                <br><b><bean:message key="Separator"/>:</b><br>
                <html:select property="status.separator" size="1">
                    <html:option value=";"><bean:message key="separator.semicolon"/></html:option>
                    <html:option value=","><bean:message key="separator.comma"/></html:option>
                    <html:option value="|"><bean:message key="separator.pipe"/></html:option>
                    <html:option value="	"><bean:message key="separator.tab"/></html:option>
                </html:select>
            </td>
        </tr>

        <tr>
            <td>
                <br><b><bean:message key="Delimiter"/>:</b><br>
                <html:select property="status.delimiter" size="1">
                    <html:option value=""><bean:message key="delimiter.none"/></html:option>
                    <html:option value="&#34;"><bean:message key="delimiter.doublequote"/></html:option>
                    <html:option value="'"><bean:message key="delimiter.singlequote"/></html:option>
                </html:select>
            </td>
        </tr>

        <tr>
            <td>
                <agn:ShowByPermission token="mailing.show.charsets">
                    <br><b><bean:message key="Charset"/>:</b><br>
                    <html:select property="status.charset" size="1">
                        <agn:ShowNavigation navigation="charsets" highlightKey="">
                            <agn:ShowByPermission token="<%= _navigation_token %>">
Token: <%= _navigation_token %><br>
                                <html:option value="<%= _navigation_href %>"><bean:message key="<%= _navigation_navMsg %>"/></html:option>
                            </agn:ShowByPermission>          
                        </agn:ShowNavigation>
                    </html:select>
                </agn:ShowByPermission>
            </td>
        </tr>
        
        <tr>
            <td>
                    <br><b><bean:message key="dateFormat"/>:</b><br>
                    <html:select property="dateFormat" size="1">
                         <html:option value="dd.MM.yyyy HH:mm">dd.MM.yyyy HH:mm</html:option>
                         <html:option value="dd.MM.yyyy">dd.MM.yyyy</html:option>
                         <html:option value="yyyyMMdd">yyyyMMdd</html:option>
                         <html:option value="yyyyMMdd HH:mm">yyyyMMdd HH:mm</html:option>
                    </html:select>
            </td>
        </tr>
        

        <tr><td colspan="3">&nbsp;&nbsp;</td></tr>
        <tr>
            <td><html:image src="button?msg=Proceed" border="0"/>&nbsp;&nbsp;</td>
            <td colspan="2">&nbsp;&nbsp;</td>
        </tr>

    </html:form>

</table>

<%@include file="/footer.jsp"%>
