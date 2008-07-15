<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.actions.*, java.util.*, org.springframework.web.context.support.*" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<agn:CheckLogon/>

<agn:Permission token="actions.change"/>	 

<% 
int tmpActionID=0;
String tmpShortname=new String("");

if(session.getAttribute("emmActionForm")!=null) {
    tmpActionID=((EmmActionForm)session.getAttribute("emmActionForm")).getActionID();
    tmpShortname=((EmmActionForm)session.getAttribute("emmActionForm")).getShortname();
}
%>

<% pageContext.setAttribute("sidemenu_active", new String("Actions")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("New_Action")); %>
<% pageContext.setAttribute("agnTitleKey", new String("New_Action")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("New_Action")); %>
<% pageContext.setAttribute("agnSubtitleValue", SafeString.getHTMLSafeString(tmpShortname)); %>
<% pageContext.setAttribute("agnNavigationKey", new String("Action")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("New_Action")); %>
<% pageContext.setAttribute("agnNavHrefAppend", new String("")); %>
<%@include file="/header.jsp"%>


            <html:form action="/action" method="post">
              <html:hidden property="action"/>
              <html:hidden property="actionID"/>
              <table border="0" cellspacing="0" cellpadding="0">
                <tr> 
                  <td><bean:message key="Name"/>:&nbsp;</td>
                  <td> 
                    <html:text property="shortname" maxlength="99" size="42"/>
                  </td>
                </tr>
                <tr> 
                  <td><bean:message key="Description"/>:&nbsp;</td>
                  <td> 
		    <html:textarea property="description" rows="5" cols="35"/>
                  </td>
                </tr>
                <agn:ShowByPermission token="actions.set_usage">
                <tr> 
                  <td><bean:message key="Usage"/>:&nbsp;</td>
                  <td> 
                    <html:select property="type" size="1">
                        <html:option value="0"><bean:message key="actionType.link"/></html:option>
                        <html:option value="1"><bean:message key="actionType.form"/></html:option>
                        <html:option value="9"><bean:message key="actionType.all"/></html:option>
                    </html:select>
                  </td>
                </tr>
                </agn:ShowByPermission>
                
                <logic:present name="emmActionForm" property="actions">
                
                <tr>
                    <td colspan="2">
                        <hr>
                        <span class="head3"><bean:message key="Steps"/>:</span><br><br>
                    </td>
                </tr>
                <% int index=0;
                   String[] classNames=null;
                   String className=null;
                    org.springframework.context.ApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(application); %>
                <logic:iterate id="op" name="emmActionForm" property="actions">
                    <tr><td colspan="2">
                        <html:errors property="<%= Integer.toString(index) %>"/>
                        <% 
                            request.setAttribute("op", pageContext.getAttribute("op"));
                            request.setAttribute("opIndex", new Integer(index));
System.err.println("Trying to Get: "+pageContext.getAttribute("op").getClass());
                            classNames=wac.getBeanNamesForType(pageContext.getAttribute("op").getClass());
                            className=classNames[0];
                            index++;
                        %>       
                        <jsp:include page="<%= new String("ops/"+className+".jsp") %>"/>
                    </td></tr>
                </logic:iterate>

                </logic:present>
                
                <tr>
                <td colspan="2">
                    <hr>
                    <span class="head3"><bean:message key="Add_Step"/>:</span><br><br>
                </td>
                </tr>
                <tr> 
                  <td colspan="2">
                    <bean:message key="Type"/>:&nbsp;
                    <html:select property="newModule" size="1">
                        <logic:iterate id="aop" name="oplist" scope="request">
                            <html:option value="${aop.value}"><bean:write name="aop" property="key"/></html:option>
                        </logic:iterate>
                    </html:select>
                    &nbsp;<html:image src="button?msg=Add" property="add" border="0"/>
                  </td>
                </tr>
              </table>
              <p>
                <html:image src="button?msg=Save" border="0"/>&nbsp;&nbsp;
                <logic:notEqual name="emmActionForm" property="actionID" value="0">
                   <html:link page="<%= new String("/action.do?action=" + EmmActionAction.ACTION_CONFIRM_DELETE + "&actionID=" + tmpActionID) %>"><html:img src="button?msg=Delete" border="0"/></html:link>
                </logic:notEqual>
              </p>
              </html:form>
<%@include file="/footer.jsp"%>
