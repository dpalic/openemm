<%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*, java.util.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="mailing.components.show"/>

<% int tmpMailingID=0;
   String tmpShortname=new String("");
   MailingComponentsForm aForm=null;
   if(request.getAttribute("mailingComponentsForm")!=null) {
      aForm=(MailingComponentsForm)request.getAttribute("mailingComponentsForm");
      tmpMailingID=aForm.getMailingID();
      tmpShortname=aForm.getShortname();
   }
%>

<logic:equal name="mailingComponentsForm" property="isTemplate" value="true">
<% // template navigation:
  pageContext.setAttribute("sidemenu_active", new String("Templates")); 
  pageContext.setAttribute("sidemenu_sub_active", new String("none"));
  pageContext.setAttribute("agnNavigationKey", new String("templateView"));
  pageContext.setAttribute("agnHighlightKey", new String("Graphics_Components"));
  pageContext.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
  pageContext.setAttribute("agnTitleKey", new String("Template")); 
  pageContext.setAttribute("agnSubtitleKey", new String("Template"));
  pageContext.setAttribute("agnSubtitleValue", tmpShortname);
%>
</logic:equal>

<logic:equal name="mailingComponentsForm" property="isTemplate" value="false">
<%
// mailing navigation:
    pageContext.setAttribute("sidemenu_active", new String("Mailings")); 
    pageContext.setAttribute("sidemenu_sub_active", new String("none"));
    pageContext.setAttribute("agnNavigationKey", new String("mailingView"));
    pageContext.setAttribute("agnHighlightKey", new String("Graphics_Components"));
    pageContext.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
    pageContext.setAttribute("agnTitleKey", new String("Mailing")); 
    pageContext.setAttribute("agnSubtitleKey", new String("Mailing"));
    pageContext.setAttribute("agnSubtitleValue", tmpShortname); 
%>
</logic:equal>

<%@include file="/header.jsp"%>

<html:errors/>

<html:form action="/mcomponents" enctype="multipart/form-data">
<html:hidden property="mailingID"/>
<html:hidden property="action"/>
            <table border="0" cellspacing="0" cellpadding="0">
              <agn:ShowByPermission token="mailing.graphics_upload">
              <tr><td colspan="3"><bean:message key="New_Component"/>:&nbsp;<html:file property="newFile"/>&nbsp;<html:image src="button?msg=Add" property="save" value="save"/></td></tr>
              <tr><td colspan="3"><hr></td></tr>
              </agn:ShowByPermission>
              <% MailingComponent comp=null; %>
              <agn:HibernateQuery id="component" query="<%= new String("from MailingComponent where companyID="+AgnUtils.getCompanyID(request)+" and mailingID="+tmpMailingID+" ORDER BY componentName") %>">
                <% comp=(MailingComponent)pageContext.getAttribute("component"); %> 
                    <% if(comp.getType()==MailingComponent.TYPE_IMAGE) { %>
                    <tr>
                    <td><b><bean:message key="Graphics_Component.external"/>:</b><br><%= comp.getComponentName() %>&nbsp;<br><br>
                        <html:image src="button?msg=Update" property="<%= "update"+comp.getId() %>" value="update"/>
                        <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="5" height="5" border="0">
                    </td>
                    <td background="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="1" height="1" border="0"></td>
                    <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="5" height="5" border="0"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="10" border="0"><html:img src="<%= "sc?compID=" + comp.getId() %>" border="1"/></td>
                    </tr>
                    <tr><td colspan="3"><hr></td></tr>
                    <% } 
                    if(comp.getType()==MailingComponent.TYPE_HOSTED_IMAGE) { %>
                    <td><b><bean:message key="Graphics_Component"/>:</b>&nbsp;&nbsp;<%= comp.getComponentName() %>&nbsp;<br>
                        <b><bean:message key="Mime_Type"/>:</b>&nbsp;&nbsp;<%= comp.getMimeType() %>&nbsp;<br><br>
                        <html:image src="button?msg=Delete" property="<%= "delete"+comp.getId() %>" value="delete"/>
                        <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="5" height="5" border="0">
                    </td>
                    <td background="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="1" height="1" border="0"></td>
                    <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="5" height="5" border="0"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="10" height="10" border="0"><html:img src="<%= "sc?compID=" + comp.getId() %>" border="1"/></td>
                    </tr>
                    <tr><td colspan="3"><hr></td></tr>
                    <% } %>
              </agn:HibernateQuery>
            </table>
</html:form>
<%@include file="/footer.jsp"%>
