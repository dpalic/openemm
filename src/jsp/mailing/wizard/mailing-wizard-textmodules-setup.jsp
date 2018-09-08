<%@ page language="java" import="org.agnitas.beans.Mailing, org.agnitas.web.MailingWizardForm" contentType="text/html; charset=utf-8"  errorPage="/error.jsp" %>
<%@ page import="org.agnitas.web.MailingWizardAction" %>
<%@ page import="org.agnitas.web.MailingContentAction" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>
<% MailingWizardForm aForm=(MailingWizardForm)session.getAttribute("mailingWizardForm");
    Mailing mailing=aForm.getMailing();

    aForm.setDynName("");
%>

<agn:Permission token="mailing.show"/>


<%
// mailing navigation:
    request.setAttribute("sidemenu_active", new String("Mailings"));
    request.setAttribute("sidemenu_sub_active", new String("mailing.New_Mailing"));
    request.setAttribute("agnNavigationKey", new String("MailingNew"));
    request.setAttribute("agnHighlightKey", new String("mailing.New_Mailing"));
    request.setAttribute("agnTitleKey", new String("Mailing"));
    request.setAttribute("agnSubtitleKey", new String("Mailing"));
    request.setAttribute("agnSubtitleValue", mailing.getShortname());
    request.setAttribute( "ACTION_FINISH", MailingWizardAction.ACTION_FINISH);
    request.setAttribute( "ACTION_TEXTMODULE", MailingWizardAction.ACTION_TEXTMODULE);
    request.setAttribute( "ACTION_DELETE_TEXTBLOCK", MailingContentAction.ACTION_DELETE_TEXTBLOCK);
    request.setAttribute( "ACTION_TEXTMODULES_PREVIOUS", MailingWizardAction.ACTION_TEXTMODULES_PREVIOUS);
%>
<c:set var="agnHelpKey" value="newMailingWizard" scope="request" />
