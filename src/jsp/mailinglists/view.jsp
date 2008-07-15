<%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, java.util.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="mailinglist.show"/>

<% int tmpMailinglistID=0;
   String tmpShortname=new String("");
   if(request.getAttribute("mailinglistForm")!=null) {
      tmpMailinglistID=((MailinglistForm)request.getAttribute("mailinglistForm")).getMailinglistID();
      System.out.println("tmpMailinglistID: " + tmpMailinglistID);
      tmpShortname=((MailinglistForm)request.getAttribute("mailinglistForm")).getShortname();
   }
%>

<% pageContext.setAttribute("sidemenu_active", new String("Mailinglists")); %>
<% if(tmpMailinglistID!=0) {
     pageContext.setAttribute("sidemenu_sub_active", new String("none"));
     pageContext.setAttribute("agnNavigationKey", new String("show_mailinglist"));
     pageContext.setAttribute("agnHighlightKey", new String("Mailinglist"));
     pageContext.setAttribute("agnTitleKey", new String("Mailinglist"));
     pageContext.setAttribute("agnSubtitleKey", new String("Mailinglist"));
     pageContext.setAttribute("agnSubtitleValue", tmpShortname);
   } else {
     pageContext.setAttribute("sidemenu_sub_active", new String("NewMailinglist"));
     pageContext.setAttribute("agnNavigationKey", new String("MailinglistNew"));
     pageContext.setAttribute("agnHighlightKey", new String("NewMailinglist"));
     pageContext.setAttribute("agnTitleKey", new String("NewMailinglist"));
     pageContext.setAttribute("agnSubtitleKey", new String("NewMailinglist"));
   }
%>

<% pageContext.setAttribute("agnNavHrefAppend", new String("&mailinglistID="+tmpMailinglistID)); %>
<%@include file="/header.jsp"%>

<html:errors/>



<html:form action="/mailinglist" focus="shortname">
                <html:hidden property="mailinglistID"/>
                <html:hidden property="action"/>

              <table border="0" cellspacing="0" cellpadding="0">
                <tr> 
                  <td><bean:message key="Name"/>:&nbsp;</td>
                  <td> 
                    <html:text property="shortname" size="52" maxlength="99"/>
                  </td>
                </tr>
		<tr> 
                  <td><bean:message key="Description"/>:&nbsp;</td>
                  <td> 
		    <html:textarea property="description" cols="40" rows="5"/>
                  </td>
                </tr>




                </table>
			  <p>
                <agn:ShowByPermission token="mailinglist.change">
                <html:image src="button?msg=Save" border="0" property="save" value="save"/>
                </agn:ShowByPermission>
                <agn:ShowByPermission token="mailinglist.delete">
                <% if(tmpMailinglistID!=0) { %>
                <html:link page="<%= "/mailinglist.do?action=" + MailinglistAction.ACTION_CONFIRM_DELETE + "&mailinglistID=" + tmpMailinglistID %>"><html:img src="button?msg=Delete" border="0"/></html:link>
                <% } %>
                </agn:ShowByPermission>
              </p>
              </html:form>

<%@include file="/footer.jsp"%>
