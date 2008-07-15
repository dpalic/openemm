<%@ page language="java" import="org.agnitas.util.*, org.agnitas.dao.*, org.springframework.context.*, org.springframework.web.context.support.WebApplicationContextUtils, org.agnitas.target.*, org.agnitas.web.*, java.util.*" contentType="text/html; charset=utf-8" %>

<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="targets.show"/>

<% int tmpTargetID=0;
   String tmpShortname=new String("");

    if(request.getParameter("targetID")!=null) {
    	ApplicationContext aContext=WebApplicationContextUtils.getWebApplicationContext(application);
        TargetDao dao = (TargetDao) aContext.getBean("TargetDao");
        Target aTarget= dao.getTarget(Integer.parseInt(request.getParameter("targetID")), AgnUtils.getCompanyID(request));
        if(aTarget != null) {
            tmpShortname = aTarget.getTargetName();
   		 }
    }

    if(request.getAttribute("targetForm")!=null) {
		tmpTargetID=((TargetForm)request.getAttribute("targetForm")).getTargetID();
	    tmpShortname=((TargetForm)request.getAttribute("targetForm")).getShortname();
	}
%>


<% pageContext.setAttribute("sidemenu_active", new String("Targets")); %>
<% if(tmpTargetID!=0) {
     pageContext.setAttribute("sidemenu_sub_active", new String("none"));
   } else {
     pageContext.setAttribute("sidemenu_sub_active", new String("NewTarget"));
   }
%>
<% pageContext.setAttribute("agnTitleKey", new String("Target")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Target")); %>
<% pageContext.setAttribute("agnSubtitleValue", tmpShortname); %>
<% pageContext.setAttribute("agnNavigationKey", new String("targetView")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Target")); %>
<% pageContext.setAttribute("agnNavHrefAppend", new String("&targetID="+tmpTargetID)); %>
<%@include file="/header.jsp"%>
<html:errors/>



    <b><bean:message key="MailingListFromTargetQuestion"/></b>

<table border="0" cellspacing="0" cellpadding="0">

  <html:form action="/mailinglist" method="post">
    <input type="hidden" name="action" value="2">
    <input type="hidden" name="targetID" value="<%= tmpTargetID %>">




    <input type=hidden name ="MTP0" value="on">

    <tr>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td><html:image src="button?msg=OK" border="0"/>
      <html:img src="button?msg=Cancel" border="0"/>
      </td>
    </tr>

  </html:form>
</table>
<%@include file="/footer.jsp"%>