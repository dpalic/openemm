<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.web.TrackableLinkForm"  errorPage="/error.jsp" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

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
        request.setAttribute("sidemenu_active", new String("Mailings"));
        request.setAttribute("sidemenu_sub_active", new String("none"));
        request.setAttribute("agnNavigationKey", new String("templateView"));
        request.setAttribute("agnHighlightKey", new String("mailing.Trackable_Links"));
        request.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
        request.setAttribute("agnTitleKey", new String("mailing.Trackable_Link"));
        request.setAttribute("agnSubtitleKey", new String("mailing.Trackable_Link"));
        request.setAttribute("agnSubtitleValue", tmpShortname);
    %>
</logic:equal>

<logic:equal name="trackableLinkForm" property="isTemplate" value="false">
     <% // mailing navigation:
         request.setAttribute("sidemenu_active", new String("Mailings"));
         request.setAttribute("sidemenu_sub_active", new String("none"));
         request.setAttribute("agnNavigationKey", new String("mailingView"));
         request.setAttribute("agnHighlightKey", new String("mailing.Trackable_Links"));
         request.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
         request.setAttribute("agnTitleKey", new String("mailing.Trackable_Link"));
         request.setAttribute("agnSubtitleKey", new String("mailing.Trackable_Link"));
         request.setAttribute("agnSubtitleValue", tmpShortname);
     %>
</logic:equal>
<c:set var="agnHelpKey" value="trackableLinkView" scope="request" />
