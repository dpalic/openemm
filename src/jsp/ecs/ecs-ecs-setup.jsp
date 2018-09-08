<%--checked--%>
<%@ page language="java" contentType="text/html; charset=utf-8"  errorPage="/error.jsp" %>
<%@ page import="org.agnitas.ecs.web.forms.EcsMailingStatForm" %>
<%@ page import="org.agnitas.ecs.EcsGlobals" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>

<agn:Permission token="stats.rdir"/>

<% int tmpMailingID=0;
   String tmpShortname=new String("");
    EcsMailingStatForm aForm = (EcsMailingStatForm) session.getAttribute("ecsForm");

   if(aForm != null) {
	  tmpMailingID = aForm.getMailingID();
      tmpShortname = aForm.getShortname();
   }
%>

<% request.setAttribute("sidemenu_active", "Mailings"); %>
<% request.setAttribute("sidemenu_sub_active", "none"); %>
<% request.setAttribute("agnTitleKey", "Mailing"); %>
<% request.setAttribute("agnSubtitleKey", "Mailing"); %>
<% request.setAttribute("agnSubtitleValue", tmpShortname); %>
<% request.setAttribute("agnNavigationKey", "mailingView"); %>
<% request.setAttribute("agnHighlightKey", "Statistics"); %>
<% request.setAttribute("agnNavHrefAppend", "&mailingID=" + tmpMailingID); %>

<% request.setAttribute("GROSS_CLICKS", EcsGlobals.MODE_GROSS_CLICKS); %>
<% request.setAttribute("NET_CLICKS", EcsGlobals.MODE_NET_CLICKS); %>
<% request.setAttribute("PURE_MAILING", EcsGlobals.MODE_PURE_MAILING); %>
<c:set var="agnHelpKey" value="heatmap" scope="request" />