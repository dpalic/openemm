<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="settings.show"/>

<%
int tmpSalutationID=0;
String tmpShortname=new String("");

if(request.getAttribute("salutationForm")!=null) {
    tmpSalutationID=((SalutationForm)request.getAttribute("salutationForm")).getSalutationID();
    tmpShortname=((SalutationForm)request.getAttribute("salutationForm")).getShortname();
}

pageContext.setAttribute("sidemenu_active", new String("Settings"));
pageContext.setAttribute("sidemenu_sub_active", new String("FormsOfAddress"));
pageContext.setAttribute("agnNavigationKey", new String("Salutation"));
pageContext.setAttribute("agnHighlightKey", new String("NewFormOfAddress"));
if(tmpSalutationID!=0) {
  pageContext.setAttribute("agnSubtitleKey", new String("FormOfAddress"));
} else {
  pageContext.setAttribute("agnSubtitleKey", new String("NewFormOfAddress"));
}
pageContext.setAttribute("agnTitleKey", new String("FormsOfAddress"));
pageContext.setAttribute("agnSubtitleValue", tmpShortname);
pageContext.setAttribute("agnNavHrefAppend", new String("&salutationID="+tmpSalutationID));
%>

<%@include file="/header.jsp"%>

<html:errors/>
<table border="0" cellspacing="0" cellpadding="0" width="100%">
<html:form action="/salutation">
<html:hidden property="salutationID"/>
<html:hidden property="action"/>
<% if(tmpSalutationID!=0) { %>
    <tr>
        <td><b>ID:</b></td>
        <td><b><%= tmpSalutationID %></b></td>
        <td colspan=2></td>
    </tr>
<% } %>
    <tr><td colspan=4><br></td></tr>
    <tr>
        <td><b><bean:message key="Description"/>:&nbsp;</b></td>
        <td colspan=2>
            <html:text property="shortname" size="32"/>
        </td>
        <td></td>
    </tr>
    <tr><td colspan=4 align=center><hr size="1" noshade></td></tr>
    <tr>
        <td><b><bean:message key="Salutation"/>:&nbsp;</b></td>
        <td colspan=2>
            <table>
                <tr>
                    <td>GENDER=0 (<bean:message key="Male"/>):<br><html:text property="salMale" size="32"/></td>
                </tr>  
                <tr>
                    <td>GENDER=1 (<bean:message key="Female"/>):<br><html:text property="salFemale" size="32"/></td>
                </tr>
                <tr>
                    <td>GENDER=2 (<bean:message key="Unknown"/>):<br><html:text property="salUnknown" size="32"/></td>
                </tr>
            </table>
        </td>
        <td></td>
    </tr>
    <tr><td colspan=4 align=center><hr noshade></td></tr>
    <tr>
        <td colspan="4">
            <html:image src="button?msg=Save" border="0" property="save" value="save"/>&nbsp;&nbsp;<html:link page="<%= new String("/salutation.do?action=" + SalutationAction.ACTION_LIST) %>"><html:img src="button?msg=Cancel" border="0"/></html:link>
        </td>
    </tr>
</html:form>  
</table>

<%@include file="/footer.jsp"%>
