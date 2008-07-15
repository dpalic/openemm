<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>
<agn:Permission token="settings.show"/>

<% int tmpSalutationID=0;
   String tmpShortname=new String("");
   if(request.getAttribute("salutationForm")!=null) {
      tmpSalutationID=((SalutationForm)request.getAttribute("salutationForm")).getSalutationID();
      tmpShortname=((SalutationForm)request.getAttribute("salutationForm")).getShortname();
   }
%>

<% pageContext.setAttribute("sidemenu_active", new String("Settings")); %>
<% if(tmpSalutationID!=0) {
     pageContext.setAttribute("sidemenu_sub_active", new String("FormsOfAddress"));
     pageContext.setAttribute("agnNavigationKey", new String("Salutation"));
     pageContext.setAttribute("agnHighlightKey", new String("FormOfAddress"));
     pageContext.setAttribute("agnSubtitleKey", new String("FormOfAddress"));
   } else {
     pageContext.setAttribute("sidemenu_sub_active", new String("FormsOfAddress"));
     pageContext.setAttribute("agnNavigationKey", new String("Salutation"));
     pageContext.setAttribute("agnHighlightKey", new String("FormOfAddress"));
     pageContext.setAttribute("agnSubtitleKey", new String("NewFormOfAddress"));
   }
%>
<% pageContext.setAttribute("agnTitleKey", new String("FormsOfAddress")); %>
<% pageContext.setAttribute("agnSubtitleValue", tmpShortname); %>
<% pageContext.setAttribute("agnNavHrefAppend", new String("&salutationID="+tmpSalutationID)); %>

<%@include file="/header.jsp"%>

<html:errors/>

             <span class="head3"><bean:message key="DeleteSalutationQuestion"/></span><br>
              <p>
                <html:form action="/salutation">
                <html:hidden property="salutationID"/>
                <html:hidden property="action"/>
                <html:image src="button?msg=Delete" property="kill" value="kill"/>
                <html:link page="<%= new String("/salutation.do?action=" + SalutationAction.ACTION_VIEW + "&salutationID=" + tmpSalutationID) %>"><html:img src="button?msg=Cancel" border="0"/></html:link>
                </html:form>
              </p>

<%@include file="/footer.jsp"%>
