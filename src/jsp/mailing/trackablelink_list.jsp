<%@ page language="java" contentType="text/html; charset=utf-8" import="java.net.*, org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*, org.agnitas.actions.*" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="mailing.content.show"/>

<% int tmpMailingID=0;
    String tmpShortname=new String("");
    TrackableLinkForm aForm=null;
    if(request.getAttribute("trackableLinkForm")!=null) {
        aForm=(TrackableLinkForm)request.getAttribute("trackableLinkForm");
        tmpMailingID=aForm.getMailingID();
        tmpShortname=aForm.getShortname();
    }
%>


<logic:equal name="trackableLinkForm" property="isTemplate" value="true">
    <% // template navigation:
        pageContext.setAttribute("sidemenu_active", new String("Templates"));
        pageContext.setAttribute("sidemenu_sub_active", new String("none"));
        pageContext.setAttribute("agnNavigationKey", new String("templateView"));
        pageContext.setAttribute("agnHighlightKey", new String("Trackable_Links"));
        pageContext.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
        pageContext.setAttribute("agnTitleKey", new String("Template"));
        pageContext.setAttribute("agnSubtitleKey", new String("Template"));
        pageContext.setAttribute("agnSubtitleValue", tmpShortname); %>
    </logic:equal>

<logic:equal name="trackableLinkForm" property="isTemplate" value="false">
    <% // mailing navigation:
        pageContext.setAttribute("sidemenu_active", new String("Mailings"));
        pageContext.setAttribute("sidemenu_sub_active", new String("none"));
        pageContext.setAttribute("agnNavigationKey", new String("mailingView"));
        pageContext.setAttribute("agnHighlightKey", new String("Trackable_Links"));
        pageContext.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
        pageContext.setAttribute("agnTitleKey", new String("Mailing"));
        pageContext.setAttribute("agnSubtitleKey", new String("Mailing"));
        pageContext.setAttribute("agnSubtitleValue", tmpShortname);
    %>
</logic:equal>

<%@include file="/header.jsp"%>

<html:errors/>

<table border="0" cellspacing="0" cellpadding="0" width="100%">
    <tr> 
        <td><span class="head3"><bean:message key="URL"/>&nbsp;</span></td>
        <td><span class="head3"><bean:message key="Description"/>&nbsp;</span></td>
        <td><span class="head3"><bean:message key="Trackable"/>&nbsp;</span></td>
        <td><span class="head3"><bean:message key="Action"/></span></td>
        <td><span class="head3">&nbsp;</span></td>
    </tr>
    <tr><td colspan="5"><hr></td></tr>
    <% TrackableLink aLink=null; %>
    <logic:iterate id="link" name="trackableLinkForm" property="links">
        <% aLink=(TrackableLink)pageContext.getAttribute("link"); %>
        <tr><td><a href="<%= "dereferer?to=" + URLEncoder.encode(aLink.getFullUrl(), "utf-8") %>" target="_blank"><img border="0" alt="<%= aLink.getFullUrl() %>" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>extlink.gif"></a>&nbsp;<html:link page="<%= "/tracklink.do?action=" + TrackableLinkAction.ACTION_VIEW + "&linkID=" + aLink.getId() + "&mailingID=" + tmpMailingID %>" title="<%= aLink.getFullUrl() %>"><%= SafeString.getHTMLSafeString(aLink.getFullUrl(), 30) %></html:link>&nbsp;&nbsp;</td>
            <td><html:link page="<%= new String("/tracklink.do?action=" + TrackableLinkAction.ACTION_VIEW + "&linkID=" + pageContext.getAttribute("_agntbl1_url_id") + "&mailingID=" + tmpMailingID) %>"><%= SafeString.getHTMLSafeString(aLink.getShortname(), 30) %></html:link>&nbsp;&nbsp;</td>
            <td><% if(aLink.getUsage()==0) { %><bean:message key="Not_Trackable"/><% } %>
                <% if(aLink.getUsage()==1) { %><bean:message key="Only_Text_Version"/><% } %>
                <% if(aLink.getUsage()==2) { %><bean:message key="Only_HTML_Version"/><% } %>
                <% if(aLink.getUsage()==3) { %><bean:message key="Text_and_HTML_Version"/><% } %>
                &nbsp;
            </td>
            <td>
                <% if(aLink.getActionID()==0) { %>
                <bean:message key="No_Action"/>
                <% } else { %>
                <agn:HibernateQuery id="action" query="<%= "from EmmAction where companyID="+AgnUtils.getCompanyID(request)+" and id="+aLink.getActionID() %>">                
                    ${action.getShortname()}
                </agn:HibernateQuery>
                <% } %>
                &nbsp;
            </td>
            <td><html:link page="<%= "/tracklink.do?action=" + TrackableLinkAction.ACTION_VIEW + "&linkID=" + aLink.getId() + "&mailingID=" + tmpMailingID %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>bearbeiten.gif" alt="<bean:message key="Edit"/>" border="0"></html:link></td>
        </tr>
    </logic:iterate>



                    
    <agn:ShowByPermission token="mailing.default_action">
        <tr> 
            <td colspan="5"<hr></td>
        </tr>
                
        <tr> 
            <html:form action="/tracklink">
            <html:hidden property="mailingID"/>
            <html:hidden property="action" value="<%= ""+TrackableLinkAction.ACTION_SET_STANDARD_ACTION %>"/>
            <td colspan="5"><span class="head3"><bean:message key="DefaultAction"/>:&nbsp;</span>
            <html:select property="linkAction" size="1">
                <html:option value="0"><bean:message key="No_Action"/></html:option>
                <agn:HibernateQuery id="action" query="<%= "from EmmAction where companyID="+AgnUtils.getCompanyID(request)+" and type="+EmmAction.TYPE_LINK %>">                
                    <html:option value="${action.getId()}">${action.getShortname()}</html:option>
                </agn:HibernateQuery>
            </html:select>&nbsp;<html:image src="button?msg=Save"/></td></html:form>
        </tr>
    </agn:ShowByPermission>
 
</table>
<%@include file="/footer.jsp"%>
